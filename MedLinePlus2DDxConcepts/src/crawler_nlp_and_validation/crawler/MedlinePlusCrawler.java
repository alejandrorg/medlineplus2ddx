package crawler_nlp_and_validation.crawler;

import mpddx.common.StaticUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import crawler_nlp_and_validation.objects.Disease;

public class MedlinePlusCrawler extends GenericCrawler {

	private final String SYMPTOMS_ELEMENT = "Symptoms";
	private final String DIAGNOSTIC_TESTS_ELEMENTS = "Exams and Tests";
	
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

	public Disease loadTextFromURL(String url, Disease disease) throws Exception {
		System.out.println("Processing disease [" + disease.getName() + "]: " + url);
		Document doc = Jsoup.connect(url).get();
		Elements els = doc.select("h2");
		for (int i = 0; i < els.size(); i++) {
			Element el = els.get(i);
			if (el.text().equalsIgnoreCase(SYMPTOMS_ELEMENT)
					|| el.text().equalsIgnoreCase(DIAGNOSTIC_TESTS_ELEMENTS)) {
				Elements sympEls = getElementsBetweenHElementAndNextHElement(
						el, doc);
				for (int j = 0; j < sympEls.size(); j++) {
					Element elSD = sympEls.get(j);
					String elSDtxt = elSD.ownText();
					if (!StaticUtils.isEmpty(elSDtxt)) {
						disease.addText(elSDtxt);
					}
				}
			}
		}
		return disease;
	}
}
