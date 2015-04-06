package crawler_nlp_and_validation.nlp;

import gov.nih.nlm.nls.metamap.Ev;
import gov.nih.nlm.nls.metamap.Mapping;
import gov.nih.nlm.nls.metamap.MetaMapApi;
import gov.nih.nlm.nls.metamap.MetaMapApiImpl;
import gov.nih.nlm.nls.metamap.PCM;
import gov.nih.nlm.nls.metamap.Result;
import gov.nih.nlm.nls.metamap.Utterance;

import java.util.List;

import mpddx.common.StaticUtils;
import crawler_nlp_and_validation.objects.Disease;

public class MetaMapNLP implements NLPInterface {

	private final String SEMANTIC_TYPES[] = { "sosy", "diap", "dsyn", "fndg",
			"lbpr", "lbtr" };
	private MetaMapApi mmapi;
	
	public MetaMapNLP() {
		this.mmapi = new MetaMapApiImpl();
		this.mmapi.setOptions("-R SNOMEDCT_US");
	}
	
	/**
	 * Method to process the UMLS terms loaded in a disease.
	 * 
	 * @param disease
	 *            Receives the disease.
	 * @throws Exception
	 *             It can throws an exception.
	 */
	public void performNLP(Disease disease) throws Exception {
		System.out.print("Processing disease: " + disease.getName() + " ... ");
		for (int i = 0; i < disease.getTexts().size(); i++) {
			String readedHTML = disease.getTexts().get(i).trim();
			readedHTML = readedHTML.replace("\u00A0", "");
			if (!StaticUtils.isEmpty(readedHTML)) {
				List<Result> results = this.mmapi
						.processCitationsFromString(readedHTML);
				for (int j = 0; j < results.size(); j++) {
					Result result = results.get(j);
					for (Utterance utterance : result.getUtteranceList()) {
						for (PCM pcm : utterance.getPCMList()) {
							for (Mapping map : pcm.getMappingList()) {
								for (Ev mapEv : map.getEvList()) {
									if (isAValidSemanticType(mapEv
											.getSemanticTypes())) {
										disease.addExtractedConcept(mapEv);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Method to check if contains a valid semantic type.
	 * 
	 * @param semanticTypes
	 *            Receive the list of semantic types of the term.
	 * @return Return true or false.
	 */
	private boolean isAValidSemanticType(List<String> semanticTypes) {
		for (int i = 0; i < SEMANTIC_TYPES.length; i++) {
			String validSemanticType = SEMANTIC_TYPES[i];
			if (semanticTypes.contains(validSemanticType)) {
				return true;
			}
		}
		return false;
	}
}
