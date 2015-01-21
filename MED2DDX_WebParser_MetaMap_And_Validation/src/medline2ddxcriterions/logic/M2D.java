package medline2ddxcriterions.logic;

import gov.nih.nlm.nls.metamap.Ev;
import gov.nih.nlm.nls.metamap.Mapping;
import gov.nih.nlm.nls.metamap.MetaMapApi;
import gov.nih.nlm.nls.metamap.MetaMapApiImpl;
import gov.nih.nlm.nls.metamap.PCM;
import gov.nih.nlm.nls.metamap.Result;
import gov.nih.nlm.nls.metamap.Utterance;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;
import medline2ddxcriterions.objects.BooleanAndString;
import medline2ddxcriterions.objects.Disease;
import medline2ddxcriterions.objects.FindingDisease;
import medline2ddxcriterions.objects.Match;
import medline2ddxcriterions.objects.MatchNLP;
import medline2ddxcriterions.objects.ValidationFinding;

public class M2D {

	private LinkedList<Disease> diseases;
	private final String SEMANTIC_TYPES[] = { "sosy", "diap", "dsyn", "fndg",
			"lbpr", "lbtr" };

	private MetaMapApi mmapi;
	private LinkedList<ValidationFinding> validationFindings;
	private final float SIMILARITY_MIN_VALUE = (float) 0.85;
	private final String SYMPTOMS_ELEMENT = "Symptoms";
	private final String DIAGNOSTIC_TESTS_ELEMENTS = "Exams and Tests";

	/**
	 * Constructor.
	 */
	public M2D() {
		this.mmapi = new MetaMapApiImpl();
		this.mmapi.setOptions("-R SNOMEDCT_US");
		this.diseases = new LinkedList<Disease>();
		this.validationFindings = new LinkedList<ValidationFinding>();
	}

	/**
	 * Method to process the UMLS terms loaded in a disease.
	 * 
	 * @param disease
	 *            Receives the disease.
	 * @throws Exception
	 *             It can throws an exception.
	 */
	public Disease processUMLSTerms(Disease disease) throws Exception {
		System.out.print("Processing disease: " + disease.getName() + " ... ");
		for (int i = 0; i < disease.getTexts().size(); i++) {
			String readedHTML = disease.getTexts().get(i).trim();
			readedHTML = readedHTML.replace("\u00A0", "");
			if (!StaticUtils.isEmpty(readedHTML)) {
				// System.out.println("Readed HTML: " + readedHTML);
				List<Result> results = this.mmapi
						.processCitationsFromString(readedHTML);
				for (int j = 0; j < results.size(); j++) {
					Result result = results.get(j);
					for (Utterance utterance : result.getUtteranceList()) {
						for (PCM pcm : utterance.getPCMList()) {
							for (Mapping map : pcm.getMappingList()) {
								// System.out.println(" Map Score: " +
								// map.getScore());
								for (Ev mapEv : map.getEvList()) {
									if (isAValidSemanticType(mapEv
											.getSemanticTypes())) {
										disease.addExtractedConcept(mapEv);
									}
								}
							}
						}
					}
				}
			}
		}
		System.out.println("Done!");
		return disease;
	}

	/**
	 * Method to check if contains a valid semantic type.
	 * 
	 * @param semanticTypes
	 *            Receive the list of semantic types of the term.
	 * @return Return true or false.
	 */
	private boolean isAValidSemanticType(List<String> semanticTypes) {
		for (int i = 0; i < SEMANTIC_TYPES.length; i++) {
			String validSemanticType = SEMANTIC_TYPES[i];
			if (semanticTypes.contains(validSemanticType)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Method to load text of a disease from an URL.
	 * 
	 * @param url
	 *            Receives the url.
	 * @param disease
	 *            Receives the disease object.
	 * @return Return the modified disease object.
	 * @throws Exception
	 *             It can throws an exception.
	 */
	public Disease loadTextFromURL(String url, Disease disease)
			throws Exception {
		Document doc = Jsoup.connect(url).get();
		for (int j = 0; j < doc.getAllElements().size(); j++) {
			Element el = doc.getAllElements().get(j);
			String txt = el.ownText();
			if (!StaticUtils.isEmpty(txt)) {
				disease.addText(txt);
			}
		}
		return disease;
	}

	/**
	 * Method to load text of a disease from an URL.
	 * 
	 * Improved version where only a part of the HTML is processed based on the
	 * analysis performed over the HTML.
	 * 
	 * @param url
	 *            Receives the url.
	 * @param disease
	 *            Receives the disease object.
	 * @return Return the modified disease object.
	 * @throws Exception
	 *             It can throws an exception.
	 */
	public Disease loadTextFromURLImproved(String url, Disease disease)
			throws Exception {
		Document doc = Jsoup.connect(url).get();
		Elements els = doc.select("#encymain");
		Document docData = Jsoup.parse(els.html());
		for (int i = 0; i < docData.getAllElements().size(); i++) {
			Element el = docData.getAllElements().get(i);
			String txt = el.ownText();
			if (!StaticUtils.isEmpty(txt)) {
				disease.addText(txt);
				// System.out.println("Found: " + txt);
			}
		}
		return disease;
	}

	/**
	 * Method to load symptoms and diagnostic tests of a disease from an URL.
	 * 
	 * Improved version where only a part of the HTML (concretely: symptoms and
	 * test sections) is processed based on the analysis performed over the
	 * HTML.
	 * 
	 * @param url
	 *            Receives the url.
	 * @param disease
	 *            Receives the disease object.
	 * @return Return the modified disease object.
	 * @throws Exception
	 *             It can throws an exception.
	 */

	public Disease loadTextFromURLExactCategories(String url, Disease disease)
			throws Exception {
		Document doc = Jsoup.connect(url).get();
		Elements els = doc.select("h2.subheading");
		for (int i = 0; i < els.size(); i++) {
			Element el = els.get(i);
			if (el.text().equalsIgnoreCase(SYMPTOMS_ELEMENT)
					|| el.text().equalsIgnoreCase(DIAGNOSTIC_TESTS_ELEMENTS)) {
				Elements sympEls = getElementsBetweenHElementAndNextHElement(
						el, doc);
				for (int j = 0; j < sympEls.size(); j++) {
					Element elSD = sympEls.get(j);
					String elSDtxt = elSD.ownText();
					System.out.println(elSDtxt);
					if (!StaticUtils.isEmpty(elSDtxt)) {
						disease.addText(elSDtxt);
						// System.out.println(elSD.text());
					}
				}
			}
		}
		return disease;
	}

	public static Elements getElementsBetweenHElementAndNextHElement(
			Element el, Document d) {
		Elements elsRet = new Elements();

		Elements els = d.getAllElements();
		boolean startGettingElements = false;
		Iterator<Element> itEls = els.iterator();
		while (itEls.hasNext()) {
			Element elem = itEls.next();
			// System.out.println("ELEM: " + elem.text());
			if (startGettingElements) {
				/*
				 * Si esto está true, es que ya encontramos el primero.
				 */
				/*
				 * Cogemos el tag del elemento actual.
				 */
				String tagName = elem.tagName();
				/*
				 * Miramos si es de tipo h.
				 */
				if (tagName.length() == 2 && tagName.charAt(0) == 'h'
						&& (StaticUtils.isNumber(tagName.charAt(1)))) {
					/*
					 * Si lo es, devolvemos lo encontrado.
					 */
					// System.out.println("!!!");
					// System.out.println(elem.toString());
					return elsRet;

				} else {
					/* Si no es el último.. seguimos insertando. */
					if (!elem.tagName().equalsIgnoreCase("ul")) {
						if (!elsRet.contains(elem)) {
//							System.out.println("\tAdding element["
//									+ elem.tagName() + "]: " + elem.text());
							elsRet.add(elem);
						}
					}
				}

			}
			if (elem.equals(el)) {
				// System.out.println("Starting getting elements: "
				// + elem.toString());
				startGettingElements = true;
			}
		}
		// System.out.println("XXX");
		return elsRet;
	}

	/**
	 * Method to load the diseases from the text file.
	 * 
	 * @throws Exception
	 *             It can throw an exception.
	 */
	public void loadDiseases() throws Exception {
		BufferedReader bL = new BufferedReader(new FileReader(
				"data/diseases.txt"));
		while (bL.ready()) {
			String read = bL.readLine();
			String parts[] = read.split("\t");
			if (parts.length == 3) {
				Disease d = new Disease(parts[0], parts[1], parts[2]);
				this.diseases.add(d);
			} else {
				System.err.println("Error processing line: " + read);
			}
		}
		bL.close();
	}

	/**
	 * Method to obtain the list of diseases.
	 * 
	 * @return Return the list.
	 */
	public LinkedList<Disease> getDiseases() {
		return this.diseases;
	}

	/**
	 * Method to load the diseases from text files.
	 * 
	 * @throws Exception
	 *             It can throws an exception.
	 */
	public void loadDiseasesFromTextFiles() throws Exception {
		File diseasesFolder = new File("data/diseases");
		for (int i = 0; i < diseasesFolder.listFiles().length; i++) {
			File fDis = diseasesFolder.listFiles()[i];
			if (fDis.isFile()) {
				Properties prop = new Properties();
				prop.load(new FileInputStream(fDis));
				String name = prop.getProperty("NAME");
				String urlS = prop.getProperty("URL_SPANISH");
				String urlE = prop.getProperty("URL_ENGLISH");
				int numTexts = Integer.parseInt(prop
						.getProperty("NUMBER_TEXTS"));
				Disease dis = new Disease(name, urlS, urlE);
				for (int j = 0; j < numTexts; j++) {
					String txt = prop.getProperty("TEXT_" + j);
					dis.addText(txt);
				}
				this.diseases.add(dis);
			}
		}
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
	public void validateMetaMapFindingsVsExternalFindings() throws Exception {
		StaticUtils.deleteFiles("validation/validated/");
		StaticUtils.deleteFiles("validation/not_validated/");
		this.loadNLPFoundFindingsPerDiseaseFromFiles();
		this.validationFindings = FindingsManager
				.loadAllFindings("fds/allFindings.fd");
		System.out.println("Starting validation proces..");
		for (int i = 0; i < this.diseases.size(); i++) {
			// cogemos cada enfermedad
			Disease dis = this.diseases.get(i);
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
				"validation/validated/" + dis.getName() + ".val", true));
		BufferedWriter bWNotVal = new BufferedWriter(new FileWriter(
				"validation/not_validated/" + dis.getName() + ".val", true));
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
	 * Método para meter un array en una lista.
	 * 
	 * @param array
	 *            Recibe un array.
	 * @return Devuelve la lista.
	 */
	private static LinkedList<String> getListFromArray(String[] array) {
		LinkedList<String> list = new LinkedList<String>();
		for (int i = 0; i < array.length; i++) {
			list.add(array[i]);
		}
		return list;
	}

	/**
	 * Método para cargar los términos de NLP encontrados por MetaMap de cada
	 * fichero de enfermedad.
	 * 
	 * @throws Exception
	 *             Puede lanzar una excepción.
	 */
	private void loadNLPFoundFindingsPerDiseaseFromFiles() throws Exception {
		System.out.println("Loading NLP terms (findings) from disease files..");
		File diseasesFolder = new File("diseases");
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
						LinkedList<String> semTypes = getListFromArray(sts);
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
