package crawler_nlp_and_validation.objects;

import java.util.LinkedList;

public class FindingDisease {

	private String name;
	private String cui;
	private LinkedList<String> semanticTypes;
	
	public FindingDisease(String n, String c, LinkedList<String> sts) {
		this.name = n;
		this.cui = c;
		this.semanticTypes = sts;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCui() {
		return cui;
	}

	public void setCui(String cui) {
		this.cui = cui;
	}

	public LinkedList<String> getSemanticTypes() {
		return semanticTypes;
	}

	public void setSemanticTypes(LinkedList<String> semanticTypes) {
		this.semanticTypes = semanticTypes;
	}
	
	
}
