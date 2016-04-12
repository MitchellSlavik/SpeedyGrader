package com.mslavik.speedygrader.source;

import java.io.File;

import com.mslavik.speedygrader.utils.Utilities;

public class CppFile extends SourceFile {

	public CppFile(File originalFileLoc) {
		super(SourceType.CPP, originalFileLoc);
		
		className = originalFileLoc.getName().substring(0, originalFileLoc.getName().length()-SourceType.CPP.getExtention().length());
	}

	@Override
	public ProcessBuilder getCompileProcessBuilder() {
		return new ProcessBuilder("g++", "\""+fileLoc.getAbsolutePath()+"\"", "-o", "\"" + Utilities.getBinFolder().getAbsolutePath() + File.separator + className + ".exe\"");
	}

}
