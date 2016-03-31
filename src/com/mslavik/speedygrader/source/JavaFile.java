package com.mslavik.speedygrader.source;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import com.mslavik.speedygrader.Utilities;

public class JavaFile extends SourceFile {

	public JavaFile(File originalFileLoc) {
		super(SourceType.JAVA, originalFileLoc);
		
		className = Utilities.getJavaClassName(originalFileLoc);
		
		if(className.equals("")){
			System.out.println("Could not find the class name for: "+originalFileLoc.getAbsolutePath());
		}
		
		String name = originalFileLoc.getName().substring(0, originalFileLoc.getName().length()-SourceType.JAVA.getExtention().length());
		
		if (!className.equalsIgnoreCase(name)) {
			newFileLoc = new File(originalFileLoc.getParent()+File.separator+"src"+File.separator, className + ".java");
			try {
				newFileLoc.createNewFile();
				Files.copy(fileLoc.toPath(), newFileLoc.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected ProcessBuilder getCompileProcessBuilder() {
		File file = null;
		if(newFileLoc != null && newFileLoc.exists()){
			file = newFileLoc;
		}else{
			file = fileLoc;
		}
		return new ProcessBuilder("javac", "-d", "\"" + binFolder.getAbsolutePath() + "\"","\"" + file.getAbsolutePath() + "\"");
	};
		
		
	
	

}
