package com.mslavik.speedygrader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Utilities {
	
	public static String getJavaClassName(File f) {
		String ret = "";
		try {
			BufferedReader in = new BufferedReader(new FileReader(f));
			String line = in.readLine();
			while (line != null) {

				if (line.contains("class") && !line.contains("import")) {
					int classIndex = line.indexOf("class");
					ret = line.substring(classIndex + 6, line.indexOf(" ", classIndex + 6));
					break;
				}

				line = in.readLine();
			}
			in.close();
		} catch (Exception e) {
		}

		return ret;
	}

}
