package com.mslavik.speedygrader.source;

import java.io.File;

public class CppFile extends SourceFile {

	public CppFile(File originalFileLoc) {
		super(SourceType.CPP, originalFileLoc);
		
		className = originalFileLoc.getName().substring(0, originalFileLoc.getName().length()-SourceType.CPP.getExtention().length());
	}

	@Override
	public ProcessBuilder getCompileProcessBuilder() {
		return new ProcessBuilder("g++", "\""+fileLoc.getAbsolutePath()+"\"", "-o", "\"" + binFolder.getAbsolutePath() + File.separator + className + ".exe\"");
	}

}
