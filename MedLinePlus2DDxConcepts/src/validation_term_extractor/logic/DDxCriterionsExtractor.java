package validation_term_extractor.logic;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.LinkedList;

import mpddx.common.Constants;
import validation_term_extractor.objects.Finding;
import validation_term_extractor.objects.Substitution;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;


public class DDxCriterionsExtractor {

	private ArrayList<Finding> findings;
	private DatatypeProperty label;
	private DatatypeProperty altLabel;
	private DatatypeProperty seeAlso;
	private OntModel ontModel;

	/**
	 * Método para obtener los findings a través de consulta a bioportal a
	 * diferencias ontologías.
	 * 
	 * @param source
	 *            Recibe la fuente (un String que la indica).
	 * @param uriSource
	 *            Recibe el URI en el que empezar a consultar.
	 * @param typeOfFindings
	 *            Recibe un string (informativo) con el tipo de findings a
	 *            obtener.
	 * @param file
	 *            Recibe el fichero donde se va a guardar la información.
	 * @param queryFile
	 *            Recibe el fichero donde está la query.
	 * @param variables
	 *            Recibe las variables que se tienen que consultar.
	 * @param classesRetrievalMethod
	 *            Recibe el método de obtención de clases (todas o solo aquellas
	 *            sin hijos)
	 * @throws Exception
	 *             Puede lanzar una excepción.
	 */
	public void getFindingsFromBioportal(String source, String uriSource,
			String typeOfFindings, String file, String queryFile,
			String variables[], int classesRetrievalMethod) throws Exception {
		System.out.println("Starting proces..");
		System.out.println("\tSource: " + source);
		System.out.println("\tURI source: " + uriSource);
		System.out.println("\tType of findings: " + typeOfFindings);
		System.out.println("\tFile to save: " + file);
		System.out
				.println("\tRetrieval method: "
						+ (classesRetrievalMethod == Constants.CLASSES_RETRIEVAL_METHOD_ALL_CLASSES ? "All classes"
								: "Only classes without childs"));
		SPARQLQueryEngine sqe = new SPARQLQueryEngine();
		ArrayList<Substitution> subs = new ArrayList<Substitution>();
		if (uriSource != null) {
			subs.add(new Substitution(Constants.CLASS_SUBSTITUTION, uriSource));
		}
		String fq = sqe.loadQueryFromFileWithSubstitutions(queryFile, subs);
		this.findings = sqe.executeQueryBioportalClasses(fq,
				Constants.BIOPORTAL_ENDPOINT, source, variables,
				classesRetrievalMethod);
		System.out.println("Findings (" + typeOfFindings + ") found in "
				+ source + ": " + findings.size());
		for (int i = 0; i < findings.size(); i++) {
			System.out.println(findings.get(i).toString());
		}
		FindingsManager.write(findings, file);
		System.out.println("Finished!");
	}

	/**
	 * Método para obtener datos de una ontología.
	 * @param fOnt Fichero de la ontología.
	 * @param typeOfFindings Mensaje sobre el tipo de findings.
	 * @param fSave Fichero a guardar.
	 * @param source Fuente.
	 * @throws Exception Puede lanzar excepción.
	 */
	public void getFindingsFromOntology(String fOnt, String typeOfFindings, String fSave, String source)
			throws Exception {
		System.out.println("Starting proces..");
		System.out.println("\tSource: " + source);
		System.out.println("\tType of findings: " + typeOfFindings);
		System.out.println("\tFile to save: " + fSave);
		this.findings = new ArrayList<Finding>();
		ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		this.label = ontModel
				.createDatatypeProperty("http://www.w3.org/2000/01/rdf-schema#label");
		this.seeAlso = ontModel
				.createDatatypeProperty("http://www.w3.org/2000/01/rdf-schema#seeAlso");
		this.altLabel = ontModel
				.createDatatypeProperty("http://www.w3.org/2004/02/skos/core#altLabel");
		ontModel.read(new FileInputStream(fOnt), "");
		ExtendedIterator<OntClass> clss = ontModel.listClasses();
		while (clss.hasNext()) {
			OntClass cls = clss.next();
			RDFNode nd = cls.getPropertyValue(this.label);
			if (!cls.hasSubClass()) {
				String name = nd.asLiteral().getString();
				String code = cls.getLocalName();
				String uri = cls.toString();
				LinkedList<String> codes = getCodes(cls);
				LinkedList<String> synoms = getSynonyms(cls);
				String cui = getCui(codes);
				Finding fd = new Finding(name, code, uri, cui, source);
				fd.addSynonyms(synoms);
				fd.addCodes(codes);
				findings.add(fd);
			}
		}
		System.out.println("Findings (" + typeOfFindings + ") found in "
				+ source + ": " + findings.size());
		for (int i = 0; i < findings.size(); i++) {
			System.out.println(findings.get(i).toString());
		}
		FindingsManager.write(findings, fSave);
		this.ontModel.close();
		System.out.println("Finished!");
	}

	private String getCui(LinkedList<String> codes) {
		for (int i = 0; i < codes.size(); i++) {
			if (codes.get(i).contains("UMLS")) {
				String parts[] = codes.get(i).split(":");
				if (parts.length == 2) {
					return parts[1];
				}
			}
		}
		return "n/a";
	}

	private LinkedList<String> getSynonyms(OntClass cls) {
		LinkedList<String> res = new LinkedList<String>();
		NodeIterator ndi = cls.listPropertyValues(this.altLabel);
		while (ndi.hasNext()) {
			RDFNode nd = ndi.next();
			if (nd != null) {
				String syn = nd.asLiteral().getString();
				res.add(syn);
			}
		}
		return res;
	}

	private LinkedList<String> getCodes(OntClass cls) {
		LinkedList<String> res = new LinkedList<String>();
		NodeIterator ndi = cls.listPropertyValues(this.seeAlso);
		while (ndi.hasNext()) {
			RDFNode nd = ndi.next();
			if (nd != null) {
				String code = nd.asLiteral().getString();
				res.add(code);
			}
		}
		return res;
	}

	public void getFindingsFromOLD(String source, String uri,
			String typeOfFindings, String queryFile, String fileSave, String[] variables) throws Exception {
		System.out.println("Starting proces..");
		System.out.println("\tSource: " + source);
		System.out.println("\tURI source: " + uri);
		System.out.println("\tType of findings: " + typeOfFindings);
		System.out.println("\tFile to save: " + fileSave);
		SPARQLQueryEngine sqe = new SPARQLQueryEngine();
		ArrayList<Substitution> subs = new ArrayList<Substitution>();
		if (uri != null) {
			subs.add(new Substitution(Constants.CLASS_SUBSTITUTION, uri));
		}
		String fq = sqe.loadQueryFromFileWithSubstitutions(queryFile, subs);
		this.findings = sqe.executeQueryBioportalClasses(fq,
				Constants.OLD_MESH_ENDPOINT, source, variables, Constants.CLASSES_RETRIEVAL_METHOD_ALL_CLASSES);
		System.out.println("Findings (" + typeOfFindings + ") found in "
				+ source + ": " + findings.size());
		for (int i = 0; i < findings.size(); i++) {
			System.out.println(findings.get(i).toString());
		}
		FindingsManager.write(findings, fileSave);
		System.out.println("Finished!");
	}

}