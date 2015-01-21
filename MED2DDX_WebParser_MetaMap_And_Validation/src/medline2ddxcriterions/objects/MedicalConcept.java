package medline2ddxcriterions.objects;

import java.util.List;

public class MedicalConcept {
	
	private final String CHAR_SEPARATOR = "!";
	private String name;
	private String id;
	private List<String> semanticTypes;
	
	public MedicalConcept(String n, String i, List<String> st) {
		this.name = n;
		this.id = i;
		this.semanticTypes = st;
	}

	public boolean equals(Object o) {
		if (o instanceof MedicalConcept) {
			MedicalConcept mc = (MedicalConcept)o;
			return mc.getName().equalsIgnoreCase(name);
		}
		return false;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getSemanticTypes() {
		return semanticTypes;
	}

	public void setSemanticTypes(List<String> semanticTypes) {
		this.semanticTypes = semanticTypes;
	}
	
	public String toString() {
		String ret = "";
		ret += "Name: " + this.getName() + "\n";
		ret += "ID: " + this.getId() + "\n";
		String sts = "";
		for (int i = 0; i < this.semanticTypes.size(); i++) {
			sts += this.semanticTypes.get(i) + ", ";
		}
		sts = sts.trim();
		sts = sts.substring(0, sts.length() - 1);
		ret += "Semantic types: " + sts + "\n\n";
		return ret;
	}

	public String toWrite() {
		String ret = "";
		String sts = getSemanticTypesPlainTextSeparatedArrobas();
		ret += name + CHAR_SEPARATOR + id + CHAR_SEPARATOR + sts;
		return ret;
	}
	
	private String getSemanticTypesPlainTextSeparatedArrobas() {
		String sts = "";
		for (int i = 0; i < this.semanticTypes.size(); i++) {
			sts += this.semanticTypes.get(i) + "@";
		}
		if (sts.length() > 0) {
			sts = sts.substring(0, sts.length() - 1);
		}
		if (sts.length() == 0) {
			sts = "n/a";
		}
		return sts;
	}
}
