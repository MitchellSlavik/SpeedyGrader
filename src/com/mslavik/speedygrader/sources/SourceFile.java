package com.mslavik.speedygrader.sources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;

public abstract class SourceFile {
	
	protected static File srcFolder;
	protected static File binFolder;
	
	public static void setFolders(File folder){
		srcFolder = new File(folder, "src");
		srcFolder.mkdirs();
		binFolder = new File(folder, "bin");
		binFolder.mkdirs();
	}
	
	public static File getBinFolder(){
		return binFolder;
	}
	
	public static File getSrcFolder(){
		return srcFolder;
	}
	
	protected String className;
	protected File fileLoc;
	protected File newFileLoc;
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
	
	public String getFileText(){
		String text = "";
		try {
			BufferedReader in = new BufferedReader(new FileReader(fileLoc));
			String line = in.readLine();
			while (line != null) {
				text+=line + "\n";
				line = in.readLine();
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return text;
	}
	
	public abstract String compile();
	
	public void save(String toWrite){
		try {
			PrintWriter pw = new PrintWriter(fileLoc);
			pw.append(toWrite);
			pw.flush();
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		if(newFileLoc != null && newFileLoc.exists()){
			try {
				PrintWriter pw = new PrintWriter(newFileLoc);
				pw.append(toWrite);
				pw.flush();
				pw.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

}
