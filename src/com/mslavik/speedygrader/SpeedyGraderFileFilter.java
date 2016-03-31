package com.mslavik.speedygrader;

import java.io.File;
import java.io.FileFilter;

import com.mslavik.speedygrader.source.SourceType;

public class SpeedyGraderFileFilter implements FileFilter {

	@Override
	public boolean accept(File f) {
		if (f.isFile()){
			return SourceType.getSourceType(f) != null;
		}else if(f.isDirectory()){
			String name = f.getName();
			return !name.equalsIgnoreCase("bin") || !name.equalsIgnoreCase("src");
		}
		return false;
	}

}
