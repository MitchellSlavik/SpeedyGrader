package com.mslavik.speedygrader.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import com.mslavik.speedygrader.source.SourceType;

public class FolderSorter {

	public static void sort(File dir) {
		HashMap<File, ArrayList<File>> files = new HashMap<File, ArrayList<File>>();

		ArrayList<File> filesNoMain = new ArrayList<File>();

		for (File f : dir.listFiles(new SpeedyGraderFileFilter())) {
			if (f.isDirectory()) {
				files.put(f, getSourceFiles(f));
			} else if (f.isFile()) {
				SourceType st = SourceType.getSourceType(f);

				if (st != null) {
					if (Utilities.hasMain(st, f)) {
						files.put(f, new ArrayList<File>());
					} else {
						filesNoMain.add(f);
					}
				}
			}
		}

		ArrayList<File> addedToMain = new ArrayList<File>();
		for (File f : filesNoMain) {
			SourceType st = SourceType.getSourceType(f);
			if (st != null) {
				boolean added = false;
				for (File f2 : files.keySet()) {
					if (f2.isFile()) {
						if (!f.equals(f2) && st == SourceType.getSourceType(f2)) {
							String fname = f.getName().substring(0, f.getName().lastIndexOf('.'));
							String f2name = f2.getName().substring(0, f2.getName().lastIndexOf('.'));

							double comparison = Utilities.diceCoefficient(fname, f2name);

							if (comparison > .65) {
								files.get(f2).add(f);
								added = true;
								break;
							}
						}
					}
				}
				if (added) {
					addedToMain.add(f);
				}
			}
		}

		filesNoMain.removeAll(addedToMain);

		if (!filesNoMain.isEmpty()) {
			ArrayList<String> choices = new ArrayList<String>();

			choices.add("None");

			for (File f : files.keySet()) {
				if (f.isFile()) {
					choices.add(f.getName().substring(0, f.getName().lastIndexOf('.')));
				} else if (f.isDirectory()) {
					choices.add(f.getName());
				}
			}

			String[] choiceArr = choices.toArray(new String[] {});

			for (File f : filesNoMain) {

				String input = (String) JOptionPane.showInputDialog(null,
						"We were unable to associate this file with a main file.  Please select the main file for this source file.\n"
								+ f.getName(),
						"File association", JOptionPane.QUESTION_MESSAGE, null, choiceArr, choiceArr[0]);

				if (input != null && !input.equals("None")) {
					for (File f2 : files.keySet()) {
						if (f2.getName().substring(0, f.getName().lastIndexOf('.')).equals(input)) {
							files.get(f2).add(f);
							break;
						}
					}
				}
			}
		}

		for (File f : files.keySet()) {
			if (!files.get(f).isEmpty()) {
				ArrayList<File> myFiles = files.get(f);
				if (f.isDirectory()) {
					for (File f2 : myFiles) {
						if (!f.equals(f2.getParentFile())) {
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
				} else if (f.isFile()) {
					File dir2 = new File(dir, f.getName().substring(0, f.getName().lastIndexOf('.')));

					File newf = new File(dir2, f.getName());

					newf.mkdirs();
					try {
						Files.copy(f.toPath(), newf.toPath(), StandardCopyOption.REPLACE_EXISTING);
					} catch (IOException e) {
						e.printStackTrace();
					}
					f.delete();

					for (File f2 : myFiles) {
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
		
		// Now all files are in the folders they need to be in.
		// Rename as we can.
		for(File d : dir.listFiles(new SpeedyGraderFileFilter())){
			if(d.isDirectory()){
				for(File f : d.listFiles(new SpeedyGraderFileFilter())){
					String[] s = f.getName().split("_assignsubmission_file_");
					if(s.length > 1){
						File f2 = new File(d, s[s.length-1]);
						f.renameTo(f2);
					}
				}
			}
		}
		
		for(File f : dir.listFiles(new SpeedyGraderFileFilter(SourceType.JAVA))){
			if(f.isDirectory()){
				for(File f2 : f.listFiles(new SpeedyGraderFileFilter(SourceType.JAVA))){
					File f3 = new File(f, Utilities.getJavaName(f2) + ".java");
					if(!f3.getName().equals(".java") && !f2.getName().equals(f3.getName())){
						f2.renameTo(f3);
					}
				}
			}else if(f.isFile()){
				File f3 = new File(f, Utilities.getJavaName(f) + ".java");
				if(!f3.getName().equals(".java") && !f.getName().equals(f3.getName())){
					f.renameTo(f3);
				}
			}
		}
	}

	private static ArrayList<File> getSourceFiles(File dir) {
		ArrayList<File> files = new ArrayList<File>();

		for (File f : dir.listFiles(new SpeedyGraderFileFilter())) {
			if (f.isFile()) {
				if (SourceType.getSourceType(f) != null) {
					files.add(f);
				}
			}
		}

		return files;
	}

}
