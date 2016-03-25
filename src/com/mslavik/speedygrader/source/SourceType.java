package com.mslavik.speedygrader.source;

import java.io.File;

public enum SourceType {
	JAVA(".java"),
	CPP(".cpp");
	
	private String ext;
	
	private SourceType(String extention){
		ext = extention;
	}
	
	public String getExtention() {
		return ext;
	}
	
	public static SourceType getSourceType(File f){
		for(SourceType st : values()){
			if(f.getName().endsWith(st.getExtention())){
				return st;
			}
		}
		return null;
	}
}
