package validation_term_extractor.objects;

public class Substitution {

	private String originalString;
	private String replacedString;
	
	public Substitution(String os, String rs) {
		this.originalString = os;
		this.replacedString = rs;
	}
	
	public String getOriginalString() {
		return this.originalString;
	}
	public String getReplacedString() {
		return this.replacedString;
	}
}
