package com.mslavik.speedygrader.source.group;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.mslavik.speedygrader.source.SourceFile;
import com.mslavik.speedygrader.source.SourceType;
import com.mslavik.speedygrader.utils.SpeedyGraderFileFilter;

public abstract class SourceGroup extends SourceFile{

	protected SourceGroup(SourceType type, File originalFileLoc) {
		super(type, originalFileLoc);
	}
	
	@Override
	public HashMap<String, ArrayList<File>> getFileList() {
		HashMap<String, ArrayList<File>> files = super.getFileList();
		
		for(File f : fileLoc.getParentFile().listFiles(new SpeedyGraderFileFilter(type))){
			ArrayList<File> a = new ArrayList<File>();
			a.add(f);
			files.put(f.getName(), a);
		}
		
		return files;
	}

}
