package com.mslavik.speedygrader.source.group;

import java.io.File;
import java.util.HashMap;

import com.mslavik.speedygrader.source.SourceFile;
import com.mslavik.speedygrader.source.SourceType;
import com.mslavik.speedygrader.utils.SpeedyGraderFileFilter;

public abstract class SourceGroup extends SourceFile{

	protected SourceGroup(SourceType type, File fileLoc) {
		super(type, fileLoc);
		
		className = fileLoc.getName().substring(0, fileLoc.getName().length()-type.getExtention().length());
	}
	
	@Override
	public HashMap<String, File> getFileList() {
		HashMap<String, File> files = super.getFileList();
		
		for(File f : fileLoc.getParentFile().listFiles(new SpeedyGraderFileFilter(type))){
			files.put(f.getName(), f);
		}
		
		return files;
	}

}
