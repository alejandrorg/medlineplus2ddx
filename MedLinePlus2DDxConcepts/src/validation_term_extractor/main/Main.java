package validation_term_extractor.main;


import java.util.ArrayList;

import mpddx.common.Constants;
import validation_term_extractor.logic.DDxCriterionsExtractor;
import validation_term_extractor.logic.FindingsManager;
import validation_term_extractor.objects.Finding;


public class Main {

	@SuppressWarnings("unused")
	public Main() {
		DDxCriterionsExtractor ddce = new DDxCriterionsExtractor();
		try {
			/*
			 * Fidedignas:
			 * 
			 * ICD9, ICD10: Automática (bioportal)
			 * Mesh: Automática (OpenLifeData)
			 * Manual: MedLine
			 */
			ddce.getFindingsFromBioportal(Constants.SOURCE_ICD9CM, Constants.ICD9_URI_SYMPTOMS, "Symptoms", Constants.VALIDATION_FINDINGS_TEMP_FOLDER + "icd9cm_symptoms.fds", Constants.BIOPORTAL_QUERY_SUBCLASSES_FILE, new String[] { "?subc", "?cui", "?name", "?id"}, Constants.CLASSES_RETRIEVAL_METHOD_ONLY_CLASSES_WITHOUT_SUBCLASSES);
			ddce.getFindingsFromBioportal(Constants.SOURCE_ICD10CM, Constants.ICD10_URI_SYMPTOMS_AND_DIAGNOSTIC_TESTS, "Symptoms and diagnostic tests", Constants.VALIDATION_FINDINGS_TEMP_FOLDER + "icd10cm_symptoms_and_diagnostic_tests.fds", Constants.BIOPORTAL_QUERY_SUBCLASSES_FILE, new String[] { "?subc", "?cui", "?name", "?id"}, Constants.CLASSES_RETRIEVAL_METHOD_ONLY_CLASSES_WITHOUT_SUBCLASSES);
			ddce.getFindingsFromOLD(Constants.SOURCE_MESH, Constants.MESH_URI_SYMPTOMS, "Symptoms and signs", Constants.OLD_QUERY_FILE, Constants.VALIDATION_FINDINGS_TEMP_FOLDER + "mesh_symptoms.fds", new String[] { "?subc", "?name", "?id"});
			/*
			 * Investigación:
			 * 
			 * SYMPTOMS Ontology, TM-SIGNS-AND-SYMPTOMS: Automática (bioportal)
			 * CSSO: Automática (Ontología)
			 */
			ddce.getFindingsFromBioportal(Constants.SOURCE_SYMPTOMS_ONTOLOGY, Constants.SYMPTOMS_ONTOLOGY_URI, "Symptoms", Constants.VALIDATION_FINDINGS_TEMP_FOLDER + "symptoms_ontology_signs_and_symptoms.fds", Constants.BIOPORTAL_QUERY_SYMPTOMS_ONTOLOGY_FILE, new String[] { "?subc", "?id", "?name"}, Constants.CLASSES_RETRIEVAL_METHOD_ALL_CLASSES);
			ddce.getFindingsFromBioportal(Constants.SOURCE_TM_SIGNS_AND_SYMPTOMS, null, "Signs and Symptoms", Constants.VALIDATION_FINDINGS_TEMP_FOLDER + "tm-signs-and-symptoms_ontology_signs_and_symptoms.fds", Constants.BIOPORTAL_QUERY_TM_SIGNS_AND_SYMPTOMS_ONTOLOGY_FILE, new String[] { "?subc", "?id", "?name"}, Constants.CLASSES_RETRIEVAL_METHOD_ALL_CLASSES);
			ddce.getFindingsFromOntology("vte_data/onts/CSSO.owl", "Signs and Symptoms", Constants.VALIDATION_FINDINGS_TEMP_FOLDER + "ccso_signs_and_symptoms.fds", Constants.SOURCE_CSSO_ONTOLOGY);
			
			/*
			 * Colaborativa:
			 * 
			 * Freebase: Automática (MLQ, Marcos)
			 * Wikipedia: Manual
			 */
		
			FindingsManager.mergeAll(Constants.VALIDATION_FINDINGS_TEMP_FOLDER, Constants.VALIDATION_FINDINGS_FILE);
			ArrayList<Finding> findings = FindingsManager.loadAllFindings(Constants.VALIDATION_FINDINGS_FILE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Main();
	}

}
