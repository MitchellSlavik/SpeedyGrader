package com.mslavik.speedygrader.source.group;

import java.io.File;
import java.util.ArrayList;

import com.mslavik.speedygrader.source.SourceType;
import com.mslavik.speedygrader.utils.SpeedyGraderFileFilter;
import com.mslavik.speedygrader.utils.Utilities;

public class JavaGroupFile extends SourceGroup{

	public JavaGroupFile(File mainFileLoc) {
		super(SourceType.JAVA, mainFileLoc);
	}

	@Override
	protected ProcessBuilder getCompileProcessBuilder() {
		ArrayList<String> args = new ArrayList<String>();
		
		args.add("javac");
		args.add("-d");
		args.add("\""+Utilities.getBinFolder().getAbsolutePath()+"\"");
		for(File f : fileLoc.getParentFile().listFiles(new SpeedyGraderFileFilter())){
			if(f.getName().endsWith(type.getExtention())){
				args.add("\""+f.getAbsolutePath()+"\"");
			}
		}
		return new ProcessBuilder(args);
	}

}
