package crawler_nlp_and_validation.logic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedList;

import mpddx.common.StaticUtils;
import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;
import crawler_nlp_and_validation.objects.BooleanAndString;
import crawler_nlp_and_validation.objects.Disease;
import crawler_nlp_and_validation.objects.FindingDisease;
import crawler_nlp_and_validation.objects.Match;
import crawler_nlp_and_validation.objects.MatchNLP;
import crawler_nlp_and_validation.objects.ValidationFinding;

public class Validation {

	private LinkedList<ValidationFinding> validationFindings;
	private final float SIMILARITY_MIN_VALUE = (float) 0.85;
	private String validatedFolder;
	private String noValidatedFolder;
	private String validationFindingsFile;
	private String diseasesFolder;
	private LinkedList<Disease> diseases;
	
	
	/**
	 * Constructor which receives the diseases objects directly.
	 * @param vFolder Folder for "validated".
	 * @param nvFolder Folder for "not validated".
	 * @param vfFile File which contains the external terms for the validation.
	 * @param diseases List of diseases.
	 */
	public Validation(String vFolder, String nvFolder, String vfFile, LinkedList<Disease> diseases) {
		this.validationFindings = new LinkedList<ValidationFinding>();
		this.validatedFolder = vFolder;
		this.noValidatedFolder = nvFolder;
		this.validationFindingsFile = vfFile;
		this.diseases = diseases;
	}
	
	/**
	 * Constructor which receives the folder where the diseases are stored.
	 * @param vFolder Folder for "validated".
	 * @param nvFolder Folder for "not validated".
	 * @param vfFile File which contains the external terms for the validation.
	 * @param df Folder with the diseases.
	 */
	public Validation(String vFolder, String nvFolder, String vfFile, String df) {
		this.validationFindings = new LinkedList<ValidationFinding>();
		this.validatedFolder = vFolder;
		this.noValidatedFolder = nvFolder;
		this.validationFindingsFile = vfFile;
		this.diseasesFolder = df;
	}
	/**
	 * Metodo para realizar la validación. 1º) Borra los ficheros de validación
	 * existentes. 2º) Carga los términos encontrados por NLP de los ficheros de
	 * cada enfermedad. 3º) Carga los findings de "contraste/validación" del
	 * fichero. 4º) Por cada enfermedad, coge un elemento de NLP extraido
	 * (nlpFinding) 5º) Se recorren todos los posibles elementos de validación y
	 * se intenta validar ese "nlpFinding" con ellos. Los tres métodos de
	 * validación son: CUI, equals o similarity. Se añade el "match" (positivo o
	 * no) a la lista. 6º) Se guardan los datos.
	 * 
	 * @throws Exception
	 *             Puede lanzar una excepción.
	 */
	public void doValidation() throws Exception {
		StaticUtils.deleteFiles(this.validatedFolder);
		StaticUtils.deleteFiles(this.noValidatedFolder);
		if (this.diseases == null) {
			this.loadNLPFoundFindingsPerDiseaseFromFiles();
		}
		this.validationFindings = FindingsManager.loadAllFindings(this.validationFindingsFile);
		System.out.println("Starting validation proces..");
		for (int i = 0; i < diseases.size(); i++) {
			// cogemos cada enfermedad
			Disease dis = diseases.get(i);
			System.out.println("Disease: " + dis.getName());
			LinkedList<MatchNLP> matches = new LinkedList<MatchNLP>();
			for (int j = 0; j < dis.getNLPFindings().size(); j++) {
				// cogemos cada finding
				FindingDisease nlpFinding = dis.getNLPFindings().get(j);
				System.out.println("\tNLP Finding: " + nlpFinding.getName());
				MatchNLP matchesNLPf = validate(dis, nlpFinding);
				matches.add(matchesNLPf);
			}
			saveMatches(matches, dis);
		}
	}
	
	/**
	 * Método para validar un término encontrado por NLP.
	 * 
	 * @param dis
	 *            Recibe la enfermedad.
	 * @param nlpFinding
	 *            Recibe el término.
	 * @return Devuelve la lista de matches encontrados.
	 * @throws Exception
	 *             Puede lanzar una excepción.
	 */
	private MatchNLP validate(Disease dis, FindingDisease nlpFinding)
			throws Exception {
		// tenemos el finding obtenido por NLP
		// recorremos la lista de findings con los que validar
		//
		boolean validated = false;
		MatchNLP mnlp = new MatchNLP(nlpFinding, dis);
		LinkedList<Match> matches = new LinkedList<Match>();
		for (int i = 0; i < this.validationFindings.size(); i++) {
			ValidationFinding vf = this.validationFindings.get(i);
			if (vf.getCuis().contains(nlpFinding.getCui())) {
				// Si se valida por CUI.. salimos.
				String cuiV = "NLPF[" + nlpFinding.getName() + "]\tVF["
						+ vf.getName() + "][" + vf.getSource() + "]";
				System.out.println("\t\tValidated: " + cuiV + " - CUI METHOD");
				Match match = new Match(vf, vf.getName(), "CUI");
				matches.add(match);
				validated = true;
			} else {
				// si no se valida por CUI, intentamos validar por exactitud en
				// el nombre del nlpFinding
				// con el nombre del vf o sus sinonimos
				BooleanAndString validateVFAndSynonyms = equalsVFNameOrSynonymsWithNLPFinding(
						vf, nlpFinding);
				if (validateVFAndSynonyms.getBool()) {
					String eqV = "NLPF[" + nlpFinding.getName()
							+ "]\tValidation via equals\tVF["
							+ validateVFAndSynonyms.getString() + "][" + vf.getSource() + "]";
					System.out.println("\t\tValidated: " + eqV + " - EQUALS METHOD");
					Match match = new Match(vf,
							validateVFAndSynonyms.getString(), "Equals");
					matches.add(match);
					validated = true;
				} else {
					// si no hay validación por coincidencia exacta, usamos
					// algoritmos de similitud
					BooleanAndString validateVFAndSynonymsWithSimilarity = hasEnoughSimilarityNameOrSynonymsWithNLPFinding(
							vf, nlpFinding, new Levenshtein());
					if (validateVFAndSynonymsWithSimilarity.getBool()) {
						String eqV = "NLPF["
								+ nlpFinding.getName()
								+ "]\tValidation via similarity\tVF["
								+ validateVFAndSynonymsWithSimilarity
										.getString() + "][" + vf.getSource() + "]";
						System.out.println("\t\tValidated: " + eqV + " - SIMILARITY METHOD");
						Match match = new Match(
								vf,
								validateVFAndSynonymsWithSimilarity.getString(), "Similarity");
						matches.add(match);
						validated = true;
					}
				}
			}
		}
		mnlp.setMatches(matches);
		if (!validated) {
			System.out.println("\t\tNot validated!");
		}
		return mnlp;
	}
	
	/**
	 * Método para saber si un término de validación (o sus sinónimos) es
	 * equivalente (equals) al término de NLP.
	 * 
	 * @param vf
	 *            Recibe el término de validación.
	 * @param nlpFinding
	 *            Recibe el término NLP.
	 * @return Devuelve un objeto.
	 */
	private BooleanAndString equalsVFNameOrSynonymsWithNLPFinding(
			ValidationFinding vf, FindingDisease nlpFinding) {
		String vfString = StaticUtils.removeStopWords(vf.getName());
		vfString = StaticUtils.orderWords(vfString);
		String nlpFindingString = StaticUtils.removeStopWords(nlpFinding.getName());
		nlpFindingString = StaticUtils.orderWords(nlpFindingString);
		if (vfString.equalsIgnoreCase(nlpFindingString)) {
			return new BooleanAndString(true, vf.getName() + " {" + vfString + "}");
		} else {
			for (int i = 0; i < vf.getSynonyms().size(); i++) {
				String vfSynom = StaticUtils.removeStopWords(vf.getSynonyms().get(i));
				vfSynom = StaticUtils.orderWords(vfSynom);
				if (vfSynom.equalsIgnoreCase(nlpFindingString)) {
					return new BooleanAndString(true, vf.getSynonyms().get(i) + " {" + vfSynom + "}");
				}
			}
		}
		return new BooleanAndString(false, "");
	}
	
	/**
	 * Método para saber si un criterio de validación (el primario o sus
	 * sinonimos) tiene suficiente similitud con el término NLP.
	 * 
	 * @param vf
	 *            Recibe el término de validación.
	 * @param nlpFinding
	 *            Recibe el término NLP.
	 * @param metric
	 *            Recibe la métrica a usar para la similitud.
	 * @return Devuelve un objeto.
	 */
	private BooleanAndString hasEnoughSimilarityNameOrSynonymsWithNLPFinding(
			ValidationFinding vf, FindingDisease nlpFinding,
			AbstractStringMetric metric) {
		
		String vfString = StaticUtils.removeStopWords(vf.getName());
		vfString = StaticUtils.orderWords(vfString);
		String nlpFindingString = StaticUtils.removeStopWords(nlpFinding.getName());
		nlpFindingString = StaticUtils.orderWords(nlpFindingString);
		
		float result = metric.getSimilarity(vfString, nlpFindingString);
		if (result > SIMILARITY_MIN_VALUE) {
			return new BooleanAndString(true, vf.getName() + " {" + vfString + "}");
		} else {
			for (int i = 0; i < vf.getSynonyms().size(); i++) {
				String vfSynom = StaticUtils.removeStopWords(vf.getSynonyms().get(i));
				vfSynom = StaticUtils.orderWords(vfSynom);
				result = metric.getSimilarity(vfSynom, nlpFindingString);
				if (result > SIMILARITY_MIN_VALUE) {
					return new BooleanAndString(true, vf.getSynonyms().get(i) + " {" + vfSynom + "}");
				}
			}
		}
		return new BooleanAndString(false, "");
	}
	
	/**
	 * Método para guardar los matches.
	 * 
	 * @param matches
	 *            Recibe la lista.
	 * @param dis
	 *            Recibe la enfermedad
	 * @throws Exception
	 *             Puede lanzar una excepción.
	 */
	private void saveMatches(LinkedList<MatchNLP> matches, Disease dis)
			throws Exception {
		BufferedWriter bWVal = new BufferedWriter(new FileWriter(
				this.validatedFolder + dis.getName() + ".val", true));
		BufferedWriter bWNotVal = new BufferedWriter(new FileWriter(
				this.noValidatedFolder + dis.getName() + ".val", true));
		for (int i = 0; i < matches.size(); i++) {
			MatchNLP matchNlp = matches.get(i);
			if (matchNlp.hasMatches()) {
				bWVal.write(matchNlp.toWriteValidated());
				bWVal.newLine();
			} else {
				bWNotVal.write(matchNlp.toWriteNotValidated());
				bWNotVal.newLine();
			}
		}
		bWVal.close();
		bWNotVal.close();
	}
	
	/**
	 * Método para cargar los términos de NLP encontrados por el proceso NLP de cada
	 * fichero de enfermedad.
	 * 
	 * @throws Exception
	 *             Puede lanzar una excepción.
	 */
	private void loadNLPFoundFindingsPerDiseaseFromFiles() throws Exception {
		this.diseases = new LinkedList<Disease>();
		System.out.println("Loading NLP terms (findings) from disease files..");
		File diseasesFolder = new File(this.diseasesFolder);
		for (int i = 0; i < diseasesFolder.listFiles().length; i++) {
			File fDis = diseasesFolder.listFiles()[i];
			if (fDis.isFile()) {
				BufferedReader bL = new BufferedReader(new FileReader(fDis));
				String fileName = fDis.getAbsoluteFile().getName();
				System.out.print("Processing " + fileName + " ..");
				Disease dis = new Disease(fileName.substring(0,
						fileName.indexOf(".")));
				LinkedList<FindingDisease> findingsDisease = new LinkedList<FindingDisease>();
				while (bL.ready()) {
					String readed = bL.readLine();
					String parts[] = readed.split("!");
					if (parts.length == 3) {
						String name = parts[0];
						String cui = parts[1];
						String sts[] = parts[2].split("@");
						LinkedList<String> semTypes = StaticUtils.getListFromArray(sts);
						FindingDisease fd = new FindingDisease(name, cui,
								semTypes);
						findingsDisease.add(fd);
					} else {
						System.err.println("Error with NLP finding ("
								+ fileName + "): " + readed);
					}
				}
				bL.close();
				if (findingsDisease.size() > 0) {
					dis.setNLPFindings(findingsDisease);
					this.diseases.add(dis);
					System.out.println(" Done! Number of terms retrieved: "
							+ findingsDisease.size());
				} else {
					System.out.println(" Ommited! Number of terms retrieved: "
							+ findingsDisease.size());
				}
			}
		}
	}
}
