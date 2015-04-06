package validation_term_extractor.logic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import mpddx.common.Constants;
import validation_term_extractor.objects.Finding;
import validation_term_extractor.objects.Substitution;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;



/**
 * Class to perform the SPARQL query.
 * 
 * @author Alejandro Rodríguez González - Centre for Biotechnology and Plant
 *         Genomics
 * 
 */
public class SPARQLQueryEngine {

	private final String API_KEY = "a865fdd8-dbe0-4516-a666-0b05dd5a372c";

	/**
	 * Método para ejecutar query sobre bioportal.
	 * @param finalQuery Recibe la query final.
	 * @param endpoint Recibe el endpoint.
	 * @param source Recibe el nombre del source.
	 * @param variables Recibe las variables a obtener.
	 * @param classesRetrievalMethod Recibe el método de obtención de clases.
	 * @return Devuelve los findings.
	 * @throws Exception Puede lanzar una excepción.
	 */
	@SuppressWarnings("resource")
	public ArrayList<Finding> executeQueryBioportalClasses(String finalQuery,
			String endpoint, String source, String variables[],
			int classesRetrievalMethod) throws Exception {
		ArrayList<Finding> resultsToReturn = new ArrayList<Finding>();
		Query query = null;
		QueryEngineHTTP qexec = null;
		try {
			query = QueryFactory.create(finalQuery);
			qexec = new QueryEngineHTTP(endpoint, query);
			// qexec.setSelectContentType("text/csv");
			qexec.addParam("apikey", API_KEY);
			ResultSet results = qexec.execSelect();
			while (results.hasNext()) {
				QuerySolution qs = results.next();
				Finding fd = new Finding();
				fd.setSource(source);
				Resource uriClass = qs.getResource(Constants.SUBCLASS_VARIABLE);
				fd.setURI(uriClass.toString());
				if (containsVariable(variables, Constants.CUI_VARIABLE)) {
					Literal cui = qs.getLiteral(Constants.CUI_VARIABLE);
					if (cui != null) {
						fd.addCui(cui.getString());
					}
				}
				if (containsVariable(variables, Constants.NAME_VARIABLE)) {
					Literal name = qs.getLiteral(Constants.NAME_VARIABLE);
					String nameStr = name.getString();
					if (source
							.equalsIgnoreCase(Constants.SOURCE_TM_SIGNS_AND_SYMPTOMS)) {
						/*
						 * Esta fuente necesita que se obtenga el nombre el
						 * inglés y se descarte el chino.
						 */
						String parts[] = nameStr.split("/");
						if (parts.length > 1) {
							nameStr = parts[0].trim();
						}
					}
					fd.setName(nameStr);
				}
				if (containsVariable(variables, Constants.ID_VARIABLE)) {
					Literal id = qs.getLiteral(Constants.ID_VARIABLE);
					fd.setCode(id.getString());
				}
				if (getNumberSubclasses(uriClass, endpoint) > 0) {
					System.out.println("Class with subclasses found ('"
							+ fd.getName() + "'). Searching childs..");
					resultsToReturn
							.addAll(executeQueryClassesWithNoChilds(fd,
									endpoint, source, variables,
									classesRetrievalMethod));
					if (classesRetrievalMethod == Constants.CLASSES_RETRIEVAL_METHOD_ALL_CLASSES) {
						resultsToReturn.add(fd);
					}
				} else {
					System.out.println("Adding element: " + fd.getName());
					resultsToReturn.add(fd);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultsToReturn;
	}

	/**
	 * Método para comprobar si el array de variables contiene una variable dada.
	 * @param variables Recibe el array.
	 * @param name Recibe la variable a comprobar.
	 * @return Devuelve un booleano.
	 */
	private boolean containsVariable(String variables[], String name) {
		for (int i = 0; i < variables.length; i++) {
			if (variables[i].equalsIgnoreCase(name))
				return true;
		}
		return false;
	}

	/**
	 * Método para obtener el número de subclases de una clase determinada.
	 * 
	 * @param uriClass
	 *            Recibe su URI.
	 * @param endpoint
	 *            Recibe el endpoint.
	 * @return Devuelve el número de clases.
	 * @throws Exception
	 *             Puede lanzar una excepción.
	 */
	@SuppressWarnings("resource")
	private int getNumberSubclasses(Resource uriClass, String endpoint)
			throws Exception {
		ArrayList<Substitution> subs = new ArrayList<Substitution>();
		subs.add(new Substitution(Constants.CLASS_SUBSTITUTION, uriClass
				.toString()));
		String finalQuery = loadQueryFromFileWithSubstitutions(
				Constants.BIOPORTAL_QUERY_SUBCLASSES_COUNT_FILE, subs);
		Query query = null;
		QueryEngineHTTP qexec = null;
		query = QueryFactory.create(finalQuery);
		qexec = new QueryEngineHTTP(endpoint, query);
		qexec.addParam("apikey", API_KEY);
		ResultSet results = qexec.execSelect();
		QuerySolution qs = results.next();
		Literal cui = qs.getLiteral(Constants.COUNT_VARIABLE);
		return cui.getInt();
	}

	/**
	 * Método para ejecutar el query de obtener clases sin hijos.
	 * @param fd Recibe el finding.
	 * @param endpoint Recibe el endpoint.
	 * @param source Recibe la fuente.
	 * @param variables Recibe las variables.
	 * @param classesRetrievalMethod Recibe el método de obtención.
	 * @return Devuelve los findings.
	 * @throws Exception Puede lanzar excepción.
	 */
	private ArrayList<Finding> executeQueryClassesWithNoChilds(Finding fd,
			String endpoint, String source, String variables[],
			int classesRetrievalMethod) throws Exception {
		ArrayList<Substitution> subs = new ArrayList<Substitution>();
		subs.add(new Substitution(Constants.CLASS_SUBSTITUTION, fd.getURI()));
		String fq = this.loadQueryFromFileWithSubstitutions(
				Constants.BIOPORTAL_QUERY_SUBCLASSES_FILE, subs);
		ArrayList<Finding> findings = executeQueryBioportalClasses(fq,
				endpoint, source, variables, classesRetrievalMethod);
		return findings;
	}

	/**
	 * Method to load a query from an external file doing some replacements on
	 * the data.
	 * 
	 * @param f
	 *            Receives the file.
	 * @param subs
	 *            List of replacements.
	 * @return Return the final query.
	 * @throws Exception
	 *             It can throw an exception.
	 */
	public String loadQueryFromFileWithSubstitutions(String f,
			ArrayList<Substitution> subs) throws Exception {
		String query = "";
		BufferedReader bL = new BufferedReader(new FileReader(f));
		while (bL.ready()) {
			String read = bL.readLine();
			Substitution sub = getSubstituion(read, subs);
			if (sub != null) {
				read = read.replace(sub.getOriginalString(),
						sub.getReplacedString());
			}
			query += read + "\r\n";
		}
		bL.close();
		return query;
	}

	/**
	 * Method to, given a concrete string readed in the file, knows if contains
	 * the pattern that needs to be replaced.
	 * 
	 * @param read
	 *            Original string readed.
	 * @param subs
	 *            List of possible replacements.
	 * @return Return the object with the replacement (if found)
	 */
	private Substitution getSubstituion(String read,
			ArrayList<Substitution> subs) {
		for (int i = 0; i < subs.size(); i++) {
			String origStr = subs.get(i).getOriginalString();
			if (read.trim().contains(origStr.trim())) {
				return subs.get(i);
			}
		}
		return null;
	}

}
