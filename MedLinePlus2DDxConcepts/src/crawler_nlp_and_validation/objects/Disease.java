package crawler_nlp_and_validation.objects;

import gov.nih.nlm.nls.metamap.Ev;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class Disease {

	private String name;
	private String urlSpanish;
	private String urlEnglish;
	private LinkedList<String> texts;
	private LinkedList<MedicalConcept> medicalConcepts;
	private LinkedList<FindingDisease> NLPFindings;

	public Disease(String n) {
		this.name = n;
	}

	public Disease(String n, String us, String ue) {
		this.name = n;
		this.urlEnglish = ue;
		this.urlSpanish = us;
		this.texts = new LinkedList<String>();
		this.medicalConcepts = new LinkedList<MedicalConcept>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrlSpanish() {
		return urlSpanish;
	}

	public void setUrlSpanish(String urlSpanish) {
		this.urlSpanish = urlSpanish;
	}

	public String getUrlEnglish() {
		return urlEnglish;
	}

	public void setUrlEnglish(String urlEnglish) {
		this.urlEnglish = urlEnglish;
	}

	public LinkedList<String> getTexts() {
		return this.texts;
	}

	public void addText(String t) {
		this.texts.add(t);

	}

	public void storeAsText(String folder) throws Exception {
		Properties prop = new Properties();
		for (int i = 0; i < texts.size(); i++) {
			prop.setProperty("TEXT_" + i, texts.get(i));
		}
		prop.setProperty("NUMBER_TEXTS", Integer.toString(texts.size()));
		prop.setProperty("NAME", this.getName());
		prop.setProperty("URL_SPANISH", this.getUrlSpanish());
		prop.setProperty("URL_ENGLISH", this.getUrlEnglish());
		prop.store(
				new FileOutputStream(folder + name + ".dis", false),
				"");
	}

	public void addExtractedConcept(Ev mapEv) throws Exception {
		String n = mapEv.getConceptName();
		String id = mapEv.getConceptId();
		List<String> st = mapEv.getSemanticTypes();
		MedicalConcept mc = new MedicalConcept(n, id, st);
		if (!this.medicalConcepts.contains(mc)) {
			this.medicalConcepts.add(mc);
		}
	}

	public void saveMedicalConcepts(String folder) throws Exception {
		System.out.print("Saving results of disease '" + this.getName()
				+ "' .. ");
		BufferedWriter bW = new BufferedWriter(new FileWriter(folder + ""
				+ this.getName() + ".dis"));
		for (int i = 0; i < this.medicalConcepts.size(); i++) {
			MedicalConcept mc = this.medicalConcepts.get(i);
			bW.write(mc.toWrite());
			bW.newLine();
		}
		bW.close();
		System.out.println("Done!");
	}

	public void setNLPFindings(LinkedList<FindingDisease> fd) {
		this.NLPFindings = fd;
	}

	public LinkedList<FindingDisease> getNLPFindings() {
		return NLPFindings;
	}

}
