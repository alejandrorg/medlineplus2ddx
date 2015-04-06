package crawler_nlp_and_validation.nlp;

import crawler_nlp_and_validation.objects.Disease;

public interface NLPInterface {

	public void performNLP(Disease disease) throws Exception;
	
}
