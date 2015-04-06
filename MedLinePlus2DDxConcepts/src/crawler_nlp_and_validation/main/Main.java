package crawler_nlp_and_validation.main;

import java.util.LinkedList;

import mpddx.common.Constants;
import mpddx.common.StaticUtils;
import crawler_nlp_and_validation.crawler.MedlinePlusCrawler;
import crawler_nlp_and_validation.logic.DiseasesLoader;
import crawler_nlp_and_validation.logic.Validation;
import crawler_nlp_and_validation.nlp.MetaMapNLP;
import crawler_nlp_and_validation.objects.Disease;

public class Main {

	private MedlinePlusCrawler mpc;
	private Validation validation;
	private LinkedList<Disease> diseases;



	private final String NLP_OPTIONS[] = { "metamap", "ctakes" };
	private final String LOAD_DATA_OPTIONS[] = { "-from-urls",
			"-from-text-files" };

	public Main(String args[]) throws Exception {

		if (args.length < 1) {
			System.err
					.println("Error! Use: Main -all <source data> <nlp method> | -validation");
			System.exit(-1);
		} else {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("-validation")) {
					performValidation(true);
				} else {
					System.err
							.println("Error: if only one parameter is given should be '-validation'");
					System.exit(-1);
				}
			}
			if (args.length == 3) {
				if (args[0].equalsIgnoreCase("-all")) {
					performAll(args);
				} else {
					System.err
							.println("Error: if three parameters are received is expecting that the first one should be '-all'");
					System.exit(-1);
				}
			} else {
				System.err
						.println("Error! Use: Main -all <source data> <nlp method> | -validation");
				System.exit(-1);
			}
		}
	}

	private void performAll(String args[]) throws Exception {
		mpc = new MedlinePlusCrawler();
		loadDiseaseData(args[1]);
		performNLP(args[2]);
		performValidation(false);
	}

	private void performValidation(boolean loadFromTextFiles) throws Exception {

		if (loadFromTextFiles) {
			validation = new Validation(Constants.VALIDATED_FOLDER, Constants.NO_VALIDATED_FOLDER,
					Constants.VALIDATION_FINDINGS_FILE, Constants.DISEASES_FINDINGS_FOLDER);
		} else {
			validation = new Validation(Constants.VALIDATED_FOLDER, Constants.NO_VALIDATED_FOLDER,
					Constants.VALIDATION_FINDINGS_FILE, diseases);
		}
		validation.doValidation();

	}

	private void performNLP(String nlpMethod) throws Exception {
		switch (StaticUtils.getIntPositionOf(nlpMethod, NLP_OPTIONS)) {
		case 0:
			performMetaMap();
			break;
		case 1:
			performCTakes();
			break;
		default:
			System.err.println("Error, invalid NLP option!");
			System.exit(-2);
		}
	}

	private void performCTakes() {
		System.err.println("Error! CTakes not implemented yet.. Exiting");
		System.exit(-4);
	}

	private void loadDiseaseData(String param) throws Exception {
		switch (StaticUtils.getIntPositionOf(param, LOAD_DATA_OPTIONS)) {
		case 0:
			loadDiseaseDataFromURLs();
			break;
		case 1:
			loadDiseaseDataFromTextFiles();
			break;
		default:
			System.err.println("Error, invalid data source option!");
			System.exit(-3);
		}

	}


	private void loadDiseaseDataFromTextFiles() throws Exception {
		diseases = DiseasesLoader
				.loadDiseasesDataFromTextFiles(Constants.DISEASES_TEXTS_FOLDER);
	}


	private void loadDiseaseDataFromURLs() throws Exception {
		diseases = DiseasesLoader.loadDiseasesURLs(Constants.DISEASES_URLS_FILE);
		for (int i = 0; i < diseases.size(); i++) {
			Disease d = diseases.get(i);
			d = mpc.loadTextFromURL(d.getUrlEnglish(), d);
			d.storeAsText(Constants.DISEASES_TEXTS_FOLDER);
		}
	}

	private void performMetaMap() throws Exception {
		MetaMapNLP mmn = new MetaMapNLP();
		for (int i = 0; i < diseases.size(); i++) {
			Disease d = diseases.get(i);
			mmn.performNLP(d);
			d.saveMedicalConcepts(Constants.DISEASES_FINDINGS_FOLDER);
		}
	}

	public static void main(String args[]) {
		try {
			new Main(args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
