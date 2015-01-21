package medline2ddxcriterions.objects;

import java.util.LinkedList;

public class MatchNLP {

	private FindingDisease nlpFinding;
	private Disease disease;
	private LinkedList<Match> matches;
	private boolean hasMatches;

	public MatchNLP(FindingDisease nlpFinding, Disease dis) {
		this.nlpFinding = nlpFinding;
		this.disease = dis;
		this.matches = new LinkedList<Match>();
	}

	public FindingDisease getNlpFinding() {
		return nlpFinding;
	}

	public void setNlpFinding(FindingDisease nlpFinding) {
		this.nlpFinding = nlpFinding;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public void setMatches(LinkedList<Match> matches) {
		this.matches = matches;
		this.hasMatches = matches.size() > 0;
	}

	public LinkedList<Match> getMatches() {
		return matches;
	}

	public void setHasMatches(boolean b) {
		this.hasMatches = b;
	}

	public boolean hasMatches() {
		return hasMatches;
	}

	public String toWriteValidated() {
		String ret = "";
		ret += this.nlpFinding.getName() + ":\n";
		for (int i = 0; i < matches.size(); i++) {
			Match m = matches.get(i);
			ret += "\t" + matches.get(i).toWriteValidated() + "\n";
		}
		return ret;
	}

	public String toWriteNotValidated() {
		return this.nlpFinding.getName();
	}

}
