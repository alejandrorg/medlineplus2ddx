package crawler_nlp_and_validation.crawler;

import java.util.Iterator;

import mpddx.common.StaticUtils;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import crawler_nlp_and_validation.objects.Disease;

public abstract class GenericCrawler {

	public abstract Disease loadTextFromURL(String url, Disease disease) throws Exception;

	public Elements getElementsBetweenHElementAndNextHElement(
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
}
