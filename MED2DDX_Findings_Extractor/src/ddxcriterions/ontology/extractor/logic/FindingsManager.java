package ddxcriterions.ontology.extractor.logic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;

import ddxcriterions.ontology.extractor.objects.Finding;

public class FindingsManager {

	/**
	 * Método para escribir los findings en un fichero de texto.
	 * @param findings Recibe los findings.
	 * @param f Recibe el fichero.
	 * @throws Exception Puede lanzar excepción.
	 */
	public static void write(ArrayList<Finding> findings, String f)
			throws Exception {
		BufferedWriter bW = new BufferedWriter(new FileWriter(f));
		for (int i = 0; i < findings.size(); i++) {
			bW.write(findings.get(i).toWrite());
			bW.newLine();
		}
		bW.close();
	}

	/**
	 * Método para juntar todos los ficheros en uno solo.
	 * @param folder Recibe el directorio donde están todos los ficheros.
	 * @param file Recibe el fichero donde guardar los datos.
	 * @throws Exception Puede lanzar excepción.
	 */
	public static void mergeAll(String folder, String file) throws Exception {
		System.out.println("Merging all findings-files in a single one..");
		System.out.println("Source folder: " + folder);
		System.out.println("Destiny file: " + file);
		ArrayList<Finding> fds = readAllFindingsFiles(folder);
		BufferedWriter bW = new BufferedWriter(new FileWriter(file));
		for (int i = 0; i < fds.size(); i++) {
			bW.write(fds.get(i).toWrite());
			bW.newLine();
		}
		bW.close();
		System.out.println("Process finished! Total findings: " + fds.size());
	}

	/**
	 * Método para cargar todos los findings dado un directorio.
	 * @param folder Recibe el directorio.
	 * @return Devuelve la lista de findings.
	 * @throws Exception Puede lanzar excepción.
	 */
	private static ArrayList<Finding> readAllFindingsFiles(String folder)
			throws Exception {
		ArrayList<Finding> findings = new ArrayList<Finding>();
		File fold = new File(folder);
		for (int i = 0; i < fold.listFiles().length; i++) {
			File f = fold.listFiles()[i];
			if (f.isFile()) {
				int readed = 0;
				System.out.print("\tReading file '" + f.getAbsoluteFile().getName() + "' .. ");
				BufferedReader bL = new BufferedReader(new FileReader(f));
				while (bL.ready()) {
					String rd = bL.readLine();
					String parts[] = rd.split("!");
					if (parts.length == 7) {
						String name = parts[0];
						String mainCode = parts[1];
						String cuis[] = parts[2].split("@");
						String uri = parts[3];
						String synonyms[] = parts[4].split("@");
						String codes[] = parts[5].split("@");
						String source = parts[6];
						Finding fd = new Finding(name);
						fd.setCode(mainCode);
						fd.addCuis(getListFromArray(cuis));
						fd.setURI(uri);
						fd.setSynonyms(getListFromArray(synonyms));
						fd.setCodes(getListFromArray(codes));
						fd.setSource(source);
						readed++;
						findings.add(fd);
					} else {
						System.err.println("Error in line (file: "
								+ f.getAbsoluteFile().getName() + "): " + rd
								+ " [Parts: " + parts.length + "]");
					}
				}
				bL.close();
				System.out.println("Done! Readed " + readed + " findings.");
			}
		}
		return findings;
	}

	/**
	 * Método para meter un array en una lista.
	 * @param array Recibe un array.
	 * @return Devuelve la lista.
	 */
	private static LinkedList<String> getListFromArray(String[] array) {
		LinkedList<String> list = new LinkedList<String>();
		for (int i = 0; i < array.length; i++) {
			list.add(array[i]);
		}
		return list;
	}

	/**
	 * Método para cargar todos los findings de un fichero único.
	 * @param f Recibe el nombre del fichero.
	 * @return Puede lanzar una excepción.
	 * @throws Exception Puede lanzar excepción.
	 */
	public static ArrayList<Finding> loadAllFindings(String f) throws Exception {
		ArrayList<Finding> findings = new ArrayList<Finding>();
		int readed = 0;
		System.out.print("Loading all the findings from file '" + new File(f).getAbsoluteFile().getName() + "' .. ");
		BufferedReader bL = new BufferedReader(new FileReader(f));
		while (bL.ready()) {
			String rd = bL.readLine();
			String parts[] = rd.split("!");
			if (parts.length == 7) {
				String name = parts[0];
				String mainCode = parts[1];
				String cuis[] = parts[2].split("@");
				String uri = parts[3];
				String synonyms[] = parts[4].split("@");
				String codes[] = parts[5].split("@");
				String source = parts[6];
				Finding fd = new Finding(name);
				fd.setCode(mainCode);
				fd.addCuis(getListFromArray(cuis));
				fd.setURI(uri);
				fd.setSynonyms(getListFromArray(synonyms));
				fd.setCodes(getListFromArray(codes));
				fd.setSource(source);
				readed++;
				findings.add(fd);
			} else {
				System.err.println("Error in line (file: "
						+ new File(f).getAbsoluteFile().getName() + "): " + rd
						+ " [Parts: " + parts.length + "]");
			}
		}
		bL.close();
		System.out.println("Done! Readed " + readed + " findings.");
		return findings;
	}
}
