package com.mslavik.speedygrader.source.group;

import java.io.File;

import com.mslavik.speedygrader.source.SourceType;

public class CppGroupFile extends SourceGroup {

	public CppGroupFile(File mainFileLoc) {
		super(SourceType.CPP, mainFileLoc);
		
		className = mainFileLoc.getName().substring(0, mainFileLoc.getName().length()-SourceType.CPP.getExtention().length());
	}

	@Override
	protected ProcessBuilder getCompileProcessBuilder() {
		return null;
	}

}
