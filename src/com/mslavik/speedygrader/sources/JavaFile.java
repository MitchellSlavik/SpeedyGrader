package com.mslavik.speedygrader.sources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class JavaFile extends SourceFile {

	public JavaFile(File originalFileLoc) {
		super(SourceType.JAVA, originalFileLoc);
		
		className = getClassName(originalFileLoc);
		
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
	public String compile() {
		File file = null;
		if(newFileLoc != null && newFileLoc.exists()){
			file = newFileLoc;
		}else{
			file = fileLoc;
		}
		String compileErrors = "";
		ProcessBuilder pb = new ProcessBuilder("javac", "-d", "\"" + binFolder.getAbsolutePath() + "\"","\"" + file.getAbsolutePath() + "\"");
		try {
			Process p = pb.start();
			p.waitFor();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(new SequenceInputStream(p.getInputStream(), p.getErrorStream())));
			String line = null;
			
			while ((line = in.readLine()) != null) {
				compileErrors += line + "\n";
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return compileErrors;
	}
	
	private String getClassName(File f) {
		String ret = "";
		try {
			BufferedReader in = new BufferedReader(new FileReader(f));
			String line = in.readLine();
			while (line != null) {

				if (line.contains("class") && !line.contains("import")) {
					int classIndex = line.indexOf("class");
					ret = line.substring(classIndex + 6, line.indexOf(" ", classIndex + 6));
					break;
				}

				line = in.readLine();
			}
			in.close();
		} catch (Exception e) {
		}

		return ret;
	}

}
