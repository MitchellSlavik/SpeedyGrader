package com.mslavik.speedygrader.source;

import java.io.File;
import java.util.ArrayList;

public enum SourceType {
	JAVA(".java"),
	CPP(".cpp", ".h", ".hpp");
	
	private String ext;
	private ArrayList<String> moreExts;
	
	private SourceType(String extention){
		ext = extention;
		moreExts = new ArrayList<String>();
	}
	
	private SourceType(String extention, String... moreExtentions){
		this(extention);
		for(String s : moreExtentions){
			moreExts.add(s);
		}
	}
	
	public String getExtention() {
		return ext;
	}
	
	public static SourceType getSourceType(File f){
		for(SourceType st : values()){
			if(f.getName().endsWith(st.getExtention())){
				return st;
			}
			for(String ext : st.moreExts){
				if(f.getName().endsWith(ext)){
					return st;
				}
			}
		}
		return null;
	}
}
