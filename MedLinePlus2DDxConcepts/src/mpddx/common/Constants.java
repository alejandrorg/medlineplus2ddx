package mpddx.common;

public class Constants {

	public final static String BIOPORTAL_QUERY_SUBCLASSES_FILE = "vte_data/sparql/query_bioportal_subclasses.sparql";
	public final static String BIOPORTAL_QUERY_SUBCLASSES_COUNT_FILE = "vte_data/sparql/query_bioportal_count_subclasses.sparql";
	public final static String BIOPORTAL_QUERY_SYMPTOMS_ONTOLOGY_FILE = "vte_data/sparql/query_bioportal_symptoms_ontology.sparql";
	public final static String BIOPORTAL_QUERY_TM_SIGNS_AND_SYMPTOMS_ONTOLOGY_FILE = "vte_data/sparql/query_bioportal_tm-signs-and-symptoms_ontology.sparql";
	public final static String OLD_QUERY_FILE = "vte_data/sparql/query_OLD.sparql";
			
	public final static String ICD9_URI_SYMPTOMS = "http://purl.bioontology.org/ontology/ICD9CM/780-789.99";
	public final static String ICD10_URI_SYMPTOMS_AND_DIAGNOSTIC_TESTS = "http://purl.bioontology.org/ontology/ICD10CM/R00-R99";
	public final static String MEDLINE_PLUS_URI_SYMPTOMS = "http://purl.bioontology.org/ontology/MEDLINEPLUS/C1457887";
	public final static String MEDLINE_PLUS_URI_DIAGNOSTIC_TESTS = "http://purl.bioontology.org/ontology/MEDLINEPLUS/C0086143";
	public final static String MESH_URI_SYMPTOMS = "http://bio2rdf.org/mesh:C23.888";
	
	public final static String CCSO_URI = "http://bioportal.bioontology.org/ontologies/CSSO";
	public final static String SYMPTOMS_ONTOLOGY_URI = "http://purl.obolibrary.org/obo/SYMP_0000462";
	public final static String TM_SIGNS_AND_SYMPTOMS_URI = "http://bioportal.bioontology.org/ontologies/TM-SIGNS-AND-SYMPTS";

	public final static String BIOPORTAL_ENDPOINT = "http://sparql.bioontology.org/sparql";
	public final static String OLD_MESH_ENDPOINT = "http://openlifedata.org/mesh/sparql/";
	
	public final static String CLASS_SUBSTITUTION = "@CLASS";

	public final static String CUI_VARIABLE = "?cui";
	public final static String SUBCLASS_VARIABLE = "?subc";
	public final static String NAME_VARIABLE = "?name";
	public final static String COUNT_VARIABLE = "?count";
	public final static String ID_VARIABLE = "?id";

	public final static int CLASSES_RETRIEVAL_METHOD_ONLY_CLASSES_WITHOUT_SUBCLASSES = 0;
	public final static int CLASSES_RETRIEVAL_METHOD_ALL_CLASSES = 1;

	public final static String SOURCE_TM_SIGNS_AND_SYMPTOMS = "TM Signs and Symptoms Ontology";
	public final static String SOURCE_ICD9CM = "ICD9CM";
	public final static String SOURCE_ICD10CM = "ICD10CM";
	public final static String SOURCE_SYMPTOMS_ONTOLOGY = "Symptoms Ontology";
	public final static String SOURCE_CSSO_ONTOLOGY = "CSSO Ontology";
	public final static String SOURCE_MESH = "MeSH";
	
	public final static String CHAR_SEPARATOR = "!";
	
	public final static String VALIDATED_FOLDER = "cnv_data/validation/validated/";
	public final static String NO_VALIDATED_FOLDER = "cnv_data/validation/not_validated/";
	public final static String VALIDATION_FINDINGS_FILE = "vte_data/results/allFindings.fd";
	public final static String DISEASES_FINDINGS_FOLDER = "cnv_data/diseases_findings/";
	public final static String DISEASES_URLS_FILE = "cnv_data/diseases.lst";
	public final static String DISEASES_TEXTS_FOLDER = "cnv_data/diseasesData/";
	public final static String VALIDATION_FINDINGS_TEMP_FOLDER = "vte_data/temp_findings/";

}
