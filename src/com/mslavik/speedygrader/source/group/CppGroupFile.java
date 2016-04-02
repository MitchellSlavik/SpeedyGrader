package com.mslavik.speedygrader.source.group;

import java.io.File;
import java.util.ArrayList;

import com.mslavik.speedygrader.source.SourceType;
import com.mslavik.speedygrader.utils.SpeedyGraderFileFilter;

public class CppGroupFile extends SourceGroup {

	public CppGroupFile(File mainFileLoc) {
		super(SourceType.CPP, mainFileLoc);
		
		className = mainFileLoc.getName().substring(0, mainFileLoc.getName().length()-SourceType.CPP.getExtention().length());
	}

	@Override
	protected ProcessBuilder getCompileProcessBuilder() {
		ArrayList<String> args = new ArrayList<String>();
		
		args.add("g++");
		args.add("-std=c++11");
		for(File f : fileLoc.getParentFile().listFiles(new SpeedyGraderFileFilter())){
			if(f.getName().endsWith(SourceType.CPP.getExtention())){
				args.add("\""+f.getAbsolutePath()+"\"");
			}
		}
		args.add("-o");
		args.add("\"" + binFolder.getAbsolutePath() + File.separator + className + ".exe\"");
		return new ProcessBuilder(args);
	}

}
