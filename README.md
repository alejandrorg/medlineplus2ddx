# medlineplus2ddx

MedLinePlus2DDx is a research project to extract diagnosis terms from MedLine webpages.

Current version contains a merge of two modules:

<b>VTE (Validation Terms Extractor):</b>

This module is in charge of obtain the terms that are going to be used to validate the concepts extracted by CNV module.

It retrieves terms from several sources (reliable, research and collaborative).

<b>CNV (Crawler, NLP and Validation):</b>

This module extracts the text from MedlinePlus diseases webpages (crawler), performs NLP (currently with MetaMap)
and validate the terms extracted by the NLP process using the terms extracted by VTE module.

Interesting files and folders:
<ul>
<li>Results.xlsx: Contains the raw results of the evaluation. Detailed results are in PACBB paper.</br></li>
</ul>
</br>
<p>CNV module files and folders</br></p>
<ul>
<li>cnv_data/diseases.lst: Contains the list of diseases to retrieve/retrieved. Current version contains a set of 30 infectious diseases.</li>
<li>cnv_data/diseases_findings/*: Contains a .dis file for each disease. It contains the terms returned by the NLP process from the text associated to the disease.</li>
<li>cnv_data/diseasesData/*: Contains a .dis file for each disease. It contains the raw text (in a Properties file) extracted by the crawler.</li>
<li>cnv_data/validation/validated/*: Contains a .val file for each disease. The content of each file are the terms extracted by NLP process and validated with the VTE terms.</li>
<li>cnv_data/validation/not_validated/*: Contains a .val file for each disease. The content of each file are the terms extracted by NLP process but NOT validated with the VTE terms.</li>
</ul>

</br>
<p>VTE module files and folders</br></p>
<ul>
<li>vte_data/onts/*: Contains the ontology files of sources which contains validation terms in an ontology.</li>
<li>vte_data/temp_findings/*: Each .fds file contains the terms to use in the validation of a given source.</li>
<li>vte_data/sparql/*: Contains the SPARQL files with the queries to be performed in some sources.</li>
<li>vte_data/results/allFindings.fd: Contains all the findings extracted from the different sources.</li>
</ul>