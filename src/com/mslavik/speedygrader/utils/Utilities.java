package com.mslavik.speedygrader.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mslavik.speedygrader.source.SourceType;

public class Utilities {
	
	protected static File binFolder;
	
	public static void createBinFolder(File folder){
		binFolder = new File(folder, ".bin");
		binFolder.mkdirs();
	}
	
	public static File getBinFolder(){
		return binFolder;
	}
	
	public static boolean hasMain(SourceType st, File f) {
		boolean haveMain = false;
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(f));
			String line = in.readLine();
			
			String match = "";
			
			switch(st){
			case CPP:
				match = ".*(int|void) main.*";
				break;
			case JAVA:
				match = ".*void main.*";
				break;
			}
			
			while (line != null) {

				if(line.matches(match)){
					haveMain = true;
					break;
				}

				line = in.readLine();
			}
			in.close();
		} catch (Exception e) {
		}
		return haveMain;
	}

	private static Pattern javaPattern = Pattern.compile("public(\\s|\\sabstract\\s)(class|interface|enum)\\s([A-z]|[0-9])+");

	public static String getJavaName(File f) {
		if (SourceType.getSourceType(f) == SourceType.JAVA) {

			String ret = "";
			try {
				BufferedReader in = new BufferedReader(new FileReader(f));
				String text = "";
				String line = in.readLine();
				while (line != null) {
					text += line + "\n";

					line = in.readLine();
				}
				in.close();

				Matcher m = javaPattern.matcher(text);
				if (m.find()) {
					String match = m.group();
					ret = match.substring(match.lastIndexOf(' ')+1, match.length());
				} else {
					throw new Exception();
				}
			} catch (Exception e) {
				new Exception("Could not find java type name for: " + f.getAbsolutePath()).printStackTrace();
			}

			return ret;
		}else{
			new Exception("getJavaName called on non-java file!").printStackTrace();
			return "";
		}
	}

	/**
	 * Here's an optimized version of the dice coefficient calculation. It takes
	 * advantage of the fact that a bigram of 2 chars can be stored in 1 int,
	 * and applies a matching algorithm of O(n*log(n)) instead of O(n*n).
	 * 
	 * @param s
	 *            The first string
	 * @param t
	 *            The second String
	 * @return The dice coefficient between the two input strings. Returns 0 if
	 *         one or both of the strings are {@code null}. Also returns 0 if
	 *         one or both of the strings contain less than 2 characters and are
	 *         not equal.
	 * @author Jelle Fresen
	 */
	public static double diceCoefficient(String s, String t) {
		// Verifying the input:
		if (s == null || t == null)
			return 0;
		// Quick check to catch identical objects:
		if (s == t)
			return 1;
		// avoid exception for single character searches
		if (s.length() < 2 || t.length() < 2)
			return 0;

		// Create the bigrams for string s:
		final int n = s.length() - 1;
		final int[] sPairs = new int[n];
		for (int i = 0; i <= n; i++)
			if (i == 0)
				sPairs[i] = s.charAt(i) << 16;
			else if (i == n)
				sPairs[i - 1] |= s.charAt(i);
			else
				sPairs[i] = (sPairs[i - 1] |= s.charAt(i)) << 16;

		// Create the bigrams for string t:
		final int m = t.length() - 1;
		final int[] tPairs = new int[m];
		for (int i = 0; i <= m; i++)
			if (i == 0)
				tPairs[i] = t.charAt(i) << 16;
			else if (i == m)
				tPairs[i - 1] |= t.charAt(i);
			else
				tPairs[i] = (tPairs[i - 1] |= t.charAt(i)) << 16;

		// Sort the bigram lists:
		Arrays.sort(sPairs);
		Arrays.sort(tPairs);

		// Count the matches:
		int matches = 0, i = 0, j = 0;
		while (i < n && j < m) {
			if (sPairs[i] == tPairs[j]) {
				matches += 2;
				i++;
				j++;
			} else if (sPairs[i] < tPairs[j])
				i++;
			else
				j++;
		}
		return (double) matches / (n + m);
	}
	
	public static int versionCompare(String str1, String str2) {
	    String[] vals1 = str1.split("\\.");
	    String[] vals2 = str2.split("\\.");
	    int i = 0;
	    // set index to first non-equal ordinal or length of shortest version string
	    while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
	      i++;
	    }
	    // compare first non-equal ordinal number
	    if (i < vals1.length && i < vals2.length) {
	        int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
	        return Integer.signum(diff);
	    }
	    // the strings are equal or one string is a substring of the other
	    // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
	    return Integer.signum(vals1.length - vals2.length);
	}

}
