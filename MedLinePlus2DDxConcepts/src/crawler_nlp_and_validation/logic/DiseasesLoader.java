package crawler_nlp_and_validation.logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.Properties;

import crawler_nlp_and_validation.objects.Disease;

public class DiseasesLoader {

	/**
	 * Method to load the diseases data from text files.
	 * 
	 * @throws Exception
	 *             It can throws an exception.
	 */
	public static LinkedList<Disease> loadDiseasesDataFromTextFiles(String folder) throws Exception {
		LinkedList<Disease> diseases = new LinkedList<Disease>();
		File diseasesFolder = new File(folder);
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
				diseases.add(dis);
			}
		}
		return diseases;
	}
	
	/**
	 * Method to load the diseases from the text file.
	 * 
	 * @throws Exception
	 *             It can throw an exception.
	 */
	public static LinkedList<Disease> loadDiseasesURLs(String f) throws Exception {
		LinkedList<Disease> diseases = new LinkedList<Disease>();
		BufferedReader bL = new BufferedReader(new FileReader(f));
		while (bL.ready()) {
			String read = bL.readLine();
			String parts[] = read.split("\t");
			if (parts.length == 3) {
				Disease d = new Disease(parts[0], parts[1], parts[2]);
				diseases.add(d);
			} else {
				System.err.println("Error processing line: " + read);
			}
		}
		bL.close();
		return diseases;
	}
}
