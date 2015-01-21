package medline2ddxcriterions.logic;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.CosineSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.EuclideanDistance;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;
import uk.ac.shef.wit.simmetrics.similaritymetrics.MongeElkan;
import medline2ddxcriterions.objects.Disease;

public class Main {

	private M2D m2d;

	public Main() {

		m2d = new M2D();
		try {
			// test();
			// loadDiseasesAndSaveToText();
			//loadDiseasesFromTextFiles();
			//performMetaMap();
			m2d.validateMetaMapFindingsVsExternalFindings();

//			String str1 = "Eye infection";
//			String str2 = "infection,of the eye";
//			String r = StaticUtils.removeStopWords(str2);
//			r = StaticUtils.orderWords(r);
//			System.out.println(r);
//			 AbstractStringMetric metric1 = new CosineSimilarity();
//			 AbstractStringMetric metric2 = new EuclideanDistance();
//			 AbstractStringMetric metric3 = new MongeElkan();
//			 AbstractStringMetric metric4 = new Levenshtein();
//			 float result = metric1.getSimilarity(str1, str2);
//			 System.out.println("Cosine: " + result);
//			 result = metric2.getSimilarity(str1, str2);
//			 System.out.println("Euclidean: " + result);
//			 result = metric3.getSimilarity(str1, str2);
//			 System.out.println("Monge Elkan: " + result);
//			 result = metric4.getSimilarity(str1, str2);
//			 System.out.println("Levenshtein: " + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void performMetaMap() throws Exception {
		for (int i = 0; i < m2d.getDiseases().size(); i++) {
			Disease d = m2d.getDiseases().get(i);
			d = m2d.processUMLSTerms(d);
			d.saveMedicalConcepts();
		}
	}

	/**
	 * Método para cargar las enfermedades desde los ficheros de texto.
	 */
	private void loadDiseasesFromTextFiles() {
		try {
			m2d.loadDiseasesFromTextFiles();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Método para cargar los textos desde las URL y guardarlos como texto para
	 * no tener que hacer conexiones a las URL.
	 * 
	 * @throws Exception
	 *             Puede lanzar excepción.
	 */
	private void loadDiseasesAndSaveToText() throws Exception {
		try {
			m2d.loadDiseases();
		} catch (Exception e) {
			throw new Exception(e);
		}
		for (int i = 0; i < m2d.getDiseases().size(); i++) {
			Disease d = m2d.getDiseases().get(i);
			try {
				System.out.println("Processing disease: " + d.getName());
				d = m2d.loadTextFromURLExactCategories(d.getUrlEnglish(), d);
				//d = m2d.loadTextFromURLImproved(d.getUrlEnglish(), d);
				// d = m2d.loadTextFromURL(d.getUrlEnglish(), d);
				System.out.print("Storing as text.. ");
				d.storeAsText();
				System.out.println("Done!");
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Error processing disease '" + d.getName()
						+ "': " + e.getMessage());
			}
		}

	}

	private void test() throws Exception {
		String url = "http://www.nlm.nih.gov/medlineplus/ency/article/000860.htm";
		Document doc = Jsoup.connect(url).get();
		Elements els = doc.select("#encymain");
		for (int i = 0; i < els.size(); i++) {
			Element e = els.get(i);
			System.out.println(e.html());
		}
		System.out.println("----- exit -------");
		System.exit(0);
	}

	public static void main(String args[]) {
		new Main();
	}
}
