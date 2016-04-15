package com.mslavik.speedygrader;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.mslavik.speedygrader.gui.SpeedyGraderInterface;
import com.mslavik.speedygrader.io.Input;
import com.mslavik.speedygrader.io.Output;
import com.mslavik.speedygrader.source.CppFile;
import com.mslavik.speedygrader.source.JavaFile;
import com.mslavik.speedygrader.source.SourceFile;
import com.mslavik.speedygrader.source.SourceRunner;
import com.mslavik.speedygrader.source.SourceType;
import com.mslavik.speedygrader.source.group.CppGroupFile;
import com.mslavik.speedygrader.source.group.JavaGroupFile;
import com.mslavik.speedygrader.utils.FolderSorter;
import com.mslavik.speedygrader.utils.SpeedyGraderFileFilter;
import com.mslavik.speedygrader.utils.Utilities;

public class SpeedyGrader {
	
	private static SpeedyGrader sg;
	
	public static SpeedyGrader getInstance(){
		if(sg == null){
			sg = new SpeedyGrader();
		}
		return sg;
	}
	
	private SpeedyGraderInterface gui;
	
	private ExecutorService exe;
	private ArrayList<Future<?>> futures;
	
	private File filesLoc;
	private Input input;
	private Output output;
	
	private SpeedyGrader() {
		exe = Executors.newCachedThreadPool();
		futures = new ArrayList<Future<?>>();
		
		gui = new SpeedyGraderInterface();
		
		input = new Input();
	}
	
	public void startComplieAndRun() {
		//New file to compile and run, cancel the old one.
		if(output != null){
			output.cancel();
		}
		for(Future<?> future : futures){
			if(!future.isDone()){
				future.cancel(true);
			}
		}
		futures.clear();
		
		//Start the new compile
		SourceFile sf = gui.getSelectedSourceFile();
		gui.setOutputTextArea("");
		
		if(sf != null){
			String compileErrors = sf.compile();
	
			if (compileErrors.length() != 0) {
				gui.setOutputTextArea("Compile Errors:\n" + compileErrors);
			} else {
				
				//Run the inputs if we complied successfully
				output = new Output(gui.getOutputTextArea(), input.size());
				for (int i = 0; i < input.size(); i++) {
					futures.add(exe.submit(new SourceRunner(output, i, sf)));
				}
			}
		}
	}
	
	public ArrayList<SourceFile> getSourceFiles(File dir){
		if(dir == null){
			// Refresh
			dir = filesLoc;
		}else{
			// Save location
			filesLoc = dir;
		}
		
		if(dir == null){
			return new ArrayList<SourceFile>();
		}
		
		Utilities.createBinFolder(dir);
		FolderSorter.sort(dir);
		
		ArrayList<SourceFile> files = new ArrayList<SourceFile>();
		
		for (File f : dir.listFiles(new SpeedyGraderFileFilter())) {
			if(f.isFile()){
				SourceType st = SourceType.getSourceType(f);
	
				if (st != null && Utilities.hasMain(st, f)) {
					SourceFile sf = null;
	
					switch (st) {
					case CPP:
						sf = new CppFile(f);
						break;
					case JAVA:
						sf = new JavaFile(f);
						break;
					}
					
					if (sf != null) {
						files.add(sf);
					}
				}
			}else if(f.isDirectory()){
				for(File f2 : f.listFiles(new SpeedyGraderFileFilter())){
					SourceType st = SourceType.getSourceType(f2);
					
					if (st != null && Utilities.hasMain(st, f2)) {
						SourceFile sf = null;
						
						switch (st) {
						case CPP:
							sf = new CppGroupFile(f2);
							break;
						case JAVA:
							sf = new JavaGroupFile(f2);
							break;
						}
						
						if (sf != null) {
							files.add(sf);
						}
						
						// We found a main in this folder, don't look for another one
						break;
					}
				}
			}
		}
		
		return files;
	}
	
	public SpeedyGraderInterface getGUI() {
		return gui;
	}
	
	public Input getInput() {
		return input;
	}
	
	public boolean timeoutPrograms(){
		return gui.timeoutPrograms();
	}

}
