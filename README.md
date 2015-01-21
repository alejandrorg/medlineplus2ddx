# medline2ddx
MedLine2DDx is a research project to extract diagnosis terms from MedLine webpages.

diseasesList.txt: Contains the list of diseases used (with its MedLine URL).
Results.xlsx: Contains the raw results of the evaluation.

medline2ddx has been divided in two different projects:

MED2DDX_Findings_Extractor: Is the project in charge of generate the list of "validation terms".
MED2DDX_WebParser_MetaMap_And_Validation: Is the project in charge of:

1) Web parser/scrapper: Obtain the text from the MedLine webpages.
2) MetaMap: It executes MetaMap NLP process. It create the list of terms (NLP terms) extracted.
3) Validation: Once we have "validation terms" and "NLP terms" it performs the validation of NLP terms against validation terms.