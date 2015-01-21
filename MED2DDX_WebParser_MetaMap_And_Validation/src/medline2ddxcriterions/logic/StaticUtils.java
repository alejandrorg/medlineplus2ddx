package medline2ddxcriterions.logic;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Properties;

/**
 * Static Utils
 * 
 * @author Alejandro Rodríguez González - Centre for Biotechnology and Plant
 *         Genomics
 * 
 */
public class StaticUtils {

	private static String[] STOP_WORDS = { "I", "a", "above", "after",
			"against", "all", "alone", "always", "am", "amount", "an", "and",
			"any", "are", "around", "as", "at", "back", "be", "before",
			"behind", "below", "between", "bill", "both", "bottom", "by",
			"call", "can", "co", "con", "de", "detail", "do", "done", "down",
			"due", "during", "each", "eg", "eight", "eleven", "empty", "ever",
			"every", "few", "fill", "find", "fire", "first", "five", "for",
			"former", "four", "from", "front", "full", "further", "get",
			"give", "go", "had", "has", "hasnt", "he", "her", "hers", "him",
			"his", "i", "ie", "if", "in", "into", "is", "it", "last", "less",
			"ltd", "many", "may", "me", "mill", "mine", "more", "most",
			"mostly", "must", "my", "name", "next", "nine", "no", "none",
			"nor", "not", "nothing", "now", "of", "off", "often", "on", "once",
			"one", "only", "or", "other", "others", "out", "over", "part",
			"per", "put", "re", "same", "see", "serious", "several", "she",
			"show", "side", "since", "six", "so", "some", "sometimes", "still",
			"take", "ten", "the", "then", "third", "this", "thick", "thin",
			"three", "through", "to", "together", "top", "toward", "towards",
			"twelve", "two", "un", "under", "until", "up", "upon", "us",
			"very", "via", "was", "we", "well", "when", "while", "who",
			"whole", "will", "with", "within", "without", "you", "yourself",
			"yourselves" };

	public static String removeStopWords(String input) {
		String output = "";

		input = input.replace(",", " ");
		input = input.replace(".", " ");
		input = input.replace(";", " ");
		input = input.replace(":", " ");
		input = input.replace("\"", " ");
		input = input.replace("(", " ");
		input = input.replace(")", " ");
		input = input.replace("/", " ");
		input = input.replace("-", " ");
		input = input.trim();

		String[] input_text = input.split("\\s+");

		boolean isSW = false;

		for (int i = 0; i < input_text.length; i++) {
			for (int j = 0; j < STOP_WORDS.length; j++) {
				if (input_text[i].compareToIgnoreCase(STOP_WORDS[j]) == 0) {
					isSW = true;
				}
			}
			if (!isSW) {
				output = output + input_text[i] + " ";
			}
			isSW = false;
		}
		if (output.length() > 0) {
			output = output.substring(0, output.length() - 1);
			output = output.toUpperCase();
		}

		return output;
	}

	public static String orderWords(String s) {
		String parts[] = s.split(" ");
		ArrayList<String> partsAL = new ArrayList<String>();
		for (int i = 0; i < parts.length; i++) {
			partsAL.add(parts[i]);
		}
		Collections.sort(partsAL);
		String ret = "";
		for (int i = 0; i < partsAL.size(); i++) {
			ret += partsAL.get(i) + " ";
		}
		ret = ret.trim();
		return ret;
	}

	/**
	 * Method to convert seconds to hours, minutes, seconds.
	 * 
	 * @param biggy
	 *            Receives the seconds.
	 * @return The value.
	 */
	public static String convertSecondsToTime(BigDecimal biggy) {
		long longVal = biggy.longValue();
		int hours = (int) longVal / 3600;
		int remainder = (int) longVal - hours * 3600;
		int mins = remainder / 60;
		remainder = remainder - mins * 60;
		int secs = remainder;
		return hours + " hours, " + mins + " mins, " + secs + " secs.";
	}

	/**
	 * Método para borrar una extensión en una cadena que contenga un fichero y
	 * su extensión.
	 * 
	 * @param name
	 *            Recibe la cadena.
	 * @return Devuelve otra cadena.
	 */
	public static String removeExtension(String name) {
		String ret = "";
		for (int i = 0; i < name.lastIndexOf('.'); i++) {
			ret += name.charAt(i);
		}
		return ret;
	}

	/**
	 * Method to check if the string is empty ("" or null)
	 * 
	 * @param str
	 *            Receives the string.
	 * @return Returns a boolean.
	 */
	public static boolean isEmpty(String str) {
		return ((str == null) || (str.trim().equals("")));
	}

	/**
	 * Method to, given a concrete string, get all the data starting from the
	 * end until we found the first slash. For example. Given string:
	 * testing/whatever we want to obtain "whatever".
	 * 
	 * @param s
	 *            Receives the string.
	 * @return Return the final string.
	 */
	public static String getUntilFirstSlashBackwards(String s) {
		String tmpString = "";
		for (int i = s.length() - 1; i >= 0; i--) {
			if (s.charAt(i) != '/') {
				tmpString += s.charAt(i);
			} else {
				break;
			}
		}
		String finalString = "";
		for (int i = tmpString.length() - 1; i >= 0; i--) {
			finalString += tmpString.charAt(i);
		}
		return finalString;
	}

	/**
	 * Method to know if all the characteres of a given String are numbers.
	 * 
	 * @param s
	 *            Receives the string.
	 * @return Return true or false.
	 */
	public static boolean isAllNumbers(String s) {
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			try {
				Integer.parseInt(Character.toString(c));
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Method to delete files in a given folder.
	 * 
	 * @throws Exception
	 *             It can throws an exception.
	 */
	public static void deleteFiles(String f) throws Exception {
		File folder = new File(f);
		File files[] = folder.listFiles();
		for (int i = 0; i < files.length; i++) {
			files[i].delete();
		}
	}

	/**
	 * Method to check if an arbitrary number of parameters are not null (then,
	 * all of them are valid).
	 * 
	 * In the case that any of the objects is a String, we check if it is not
	 * empty.
	 * 
	 * @param objects
	 *            Receive the objects.
	 * @return A boolean.
	 */
	public static boolean areValid(Object... objects) {
		for (Object obj : objects) {
			if (obj instanceof String) {
				if (isEmpty((String) obj)) {
					return false;
				}
			} else {
				if (obj == null) {
					return false;
				}
			}
		}
		return true;
	}

	public static boolean isNumber(char c) {
		return (c >= '0' && c <= '9');
	}
}