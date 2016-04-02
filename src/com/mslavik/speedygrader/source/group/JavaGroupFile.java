package com.mslavik.speedygrader.source.group;

import java.io.File;

import com.mslavik.speedygrader.source.SourceType;

public class JavaGroupFile extends SourceGroup{

	public JavaGroupFile(File mainFileLoc) {
		super(SourceType.JAVA, mainFileLoc);
		
		//TODO
	}

	@Override
	protected ProcessBuilder getCompileProcessBuilder() {
		return null;
	}

}
