package com.mslavik.speedygrader.source;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class SourceFile {
	
	
	
	protected String className;
	protected File fileLoc;
	protected SourceType type;
	
	protected SourceFile(SourceType type, File originalFileLoc){
		this.type = type;
		fileLoc = originalFileLoc;
	}
	
	public SourceType getSourceType(){
		return type;
	}
	
	public String toString(){
		return className;
	}
	
	public String compile(){
		String compileErrors = "";
		ProcessBuilder pb = getCompileProcessBuilder();
		try {
			Process p = pb.start();
			p.waitFor();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(new SequenceInputStream(p.getInputStream(), p.getErrorStream())));
			String line = null;
			
			while ((line = in.readLine()) != null) {
				compileErrors += line + "\n";
			}
			
			if(type == SourceType.CPP){
				if(compileErrors.toLowerCase().contains("#include \"stdafx.h\"")){
					File fakeH = new File(fileLoc.getParentFile(), "stdafx.h");
					if(!fakeH.exists()){
						fakeH.createNewFile();
						return compile();
					}
				}
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return compileErrors;
	}
	
	protected abstract ProcessBuilder getCompileProcessBuilder();
	
	public HashMap<String, File> getFileList(){
		HashMap<String, File> files = new HashMap<String, File>();
		files.put(className, fileLoc);
		return files;
	}

}
