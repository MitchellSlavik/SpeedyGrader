package com.mslavik.speedygrader.source;

import java.io.File;
import com.mslavik.speedygrader.utils.Utilities;

public class JavaFile extends SourceFile {

	public JavaFile(File originalFileLoc) {
		super(SourceType.JAVA, originalFileLoc);
		
		className = Utilities.getJavaName(originalFileLoc);
	}

	@Override
	protected ProcessBuilder getCompileProcessBuilder() {
		return new ProcessBuilder("javac", "-d", "\"" + Utilities.getBinFolder().getAbsolutePath() + "\"","\"" + fileLoc.getAbsolutePath() + "\"");
	}
	
}
