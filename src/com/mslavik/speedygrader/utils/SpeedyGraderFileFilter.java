package com.mslavik.speedygrader.utils;

import java.io.File;
import java.io.FileFilter;

import com.mslavik.speedygrader.source.SourceType;

public class SpeedyGraderFileFilter implements FileFilter {
	
	private SourceType st;
	
	public SpeedyGraderFileFilter() {
	}
	
	public SpeedyGraderFileFilter(SourceType st){
		this.st = st;
	}

	@Override
	public boolean accept(File f) {
		if (f.isFile()){
			if(f.getName().equalsIgnoreCase("stdafx.h"))
				return false;
			
			if(st == null){
				return SourceType.getSourceType(f) != null;
			}else{
				return SourceType.getSourceType(f) == st;
			}
		}else if(f.isDirectory()){
			String name = f.getName();
			return !name.equalsIgnoreCase(".bin") && !name.equalsIgnoreCase(".src");
		}
		return false;
	}

}
