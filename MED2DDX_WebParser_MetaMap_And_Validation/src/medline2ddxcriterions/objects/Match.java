package medline2ddxcriterions.objects;

public class Match {

	private ValidationFinding validationFinding;
	private String validationFindingString;
	private boolean isMatch;
	private String validationMethod;
	
	public Match(ValidationFinding vf, String vfs, String valMethod) {
		this.validationFinding = vf;
		this.validationFindingString = vfs;
		this.isMatch = true;
		this.validationMethod = valMethod;
	}

	
	public String getValidationMethod() {
		return validationMethod;
	}


	public void setValidationMethod(String validationMethod) {
		this.validationMethod = validationMethod;
	}


	public Match() {
		this.isMatch = false;
	}

	

	public boolean isMatch() {
		return isMatch;
	}


	public void setMatch(boolean isMatch) {
		this.isMatch = isMatch;
	}


	public ValidationFinding getValidationFinding() {
		return validationFinding;
	}


	public void setValidationFinding(ValidationFinding validationFinding) {
		this.validationFinding = validationFinding;
	}


	public String getValidationFindingString() {
		return validationFindingString;
	}


	public void setValidationFindingString(String validationFindingString) {
		this.validationFindingString = validationFindingString;
	}


	public String toWriteValidated() {
		String ret = "";
		ret += validationFindingString + "!" + validationFinding.getSource() + "!" + validationMethod;
		return ret;
	}

}
