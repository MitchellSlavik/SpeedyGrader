package com.mslavik.speedygrader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import com.mslavik.speedygrader.source.SourceFile;
import com.mslavik.speedygrader.source.SourceType;

public class FolderSorter {
	
	public static void sort(File dir){
		HashMap<File, ArrayList<File>> files = new HashMap<File, ArrayList<File>>();
		
		ArrayList<File> filesNoMain = new ArrayList<File>();
		
		for(File f : dir.listFiles(new SpeedyGraderFileFilter())){
			if(f.isDirectory()){
				files.put(f, getSourceFiles(f));
			}else if(f.isFile()){
				SourceType st = SourceType.getSourceType(f);
				
				if(st != null ){
					if( SourceFile.hasMain(st, f)){
						files.put(f, new ArrayList<File>());
					}else{
						filesNoMain.add(f);
					}
				}
			}
		}
		
		for(File f : filesNoMain){
			SourceType st = SourceType.getSourceType(f);
			if(st != null){
				for(File f2 : files.keySet()){
					if(f2.isFile()){
						if(!f.equals(f2) && st == SourceType.getSourceType(f2)){
							// Match files together
						}
					}
				}
			}
		}
		
		if(!filesNoMain.isEmpty()){
			ArrayList<String> choices = new ArrayList<String>();
			
			choices.add("None");
			
			for(File f : files.keySet()){
				choices.add(f.getName().substring(0, f.getName().lastIndexOf('.')));
			}
			
			String[] choiceArr = choices.toArray(new String[]{});
			
			for(File f : filesNoMain){
				
				String input = (String) JOptionPane.showInputDialog(null, 
						"We were unable to associate this file with a main file.  Please select the main file to this file.", 
						"File association", 
						JOptionPane.QUESTION_MESSAGE, 
						null, 
						choiceArr, 
						choiceArr[0]);
				
				if(!input.equals("None")){
					for(File f2 : files.keySet()){
						if(f2.getName().substring(0, f.getName().lastIndexOf('.')).equals(input)){
							files.get(f2).add(f);
							break;
						}
					}
				}
			}
		}
		
		for(File f : files.keySet()){
			if(!files.get(f).isEmpty()){
				ArrayList<File> myFiles = files.get(f);
				if(f.isDirectory()){
					for(File f2 : myFiles){
						if(!f.equals(f2.getParent())){
							File newf = new File(f, f2.getName());
							newf.mkdirs();
							try {
								Files.copy(f2.toPath(), newf.toPath(), StandardCopyOption.REPLACE_EXISTING);
							} catch (IOException e) {
								e.printStackTrace();
							}
							f2.delete();
						}
					}
				}else if(f.isFile()){
					File dir2 = new File(dir, f.getName().substring(0, f.getName().lastIndexOf('.')));
					
					File newf = new File(dir2, f.getName());
					
					newf.mkdirs();
					try {
						Files.copy(f.toPath(), newf.toPath(), StandardCopyOption.REPLACE_EXISTING);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					for(File f2 : myFiles){
						newf = new File(dir2, f2.getName());
						newf.mkdirs();
						try {
							Files.copy(f2.toPath(), newf.toPath(), StandardCopyOption.REPLACE_EXISTING);
						} catch (IOException e) {
							e.printStackTrace();
						}
						f2.delete();
					}
				}
			}
		}
	}
	
	private static ArrayList<File> getSourceFiles(File dir){
		ArrayList<File> files = new ArrayList<File>();
		
		for(File f : dir.listFiles(new SpeedyGraderFileFilter())){
			if(f.isFile()){
				files.add(f);
			}
		}
		
		return files;
	}

}
