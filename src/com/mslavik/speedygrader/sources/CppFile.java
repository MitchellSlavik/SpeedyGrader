package com.mslavik.speedygrader.sources;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;

public class CppFile extends SourceFile {

	public CppFile(File originalFileLoc) {
		super(SourceType.CPP, originalFileLoc);
		
		className = originalFileLoc.getName().substring(0, originalFileLoc.getName().length()-SourceType.CPP.getExtention().length());
	}

	@Override
	public String compile() {
		String compileErrors = "";
		ProcessBuilder pb = new ProcessBuilder("g++", "\""+fileLoc.getAbsolutePath()+"\"", "-o", "\"" + binFolder.getAbsolutePath() + File.separator + className + ".exe\"");
		try {
			Process p = pb.start();
			p.waitFor();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(new SequenceInputStream(p.getInputStream(), p.getErrorStream())));
			String line = null;
			
			while ((line = in.readLine()) != null) {
				compileErrors += line + "\n";
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return compileErrors;
	}

}
