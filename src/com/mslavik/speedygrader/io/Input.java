package com.mslavik.speedygrader.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Input {
	
	private ArrayList<String> input;
	
	public Input(){
		input = new ArrayList<String>();
		input.add("");
	}
	
	public void parseFile(File f){
		input.clear();
		input.add("");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			String line;
			while ((line = br.readLine()) != null) {
				if(line.toLowerCase().matches("run [0-9]+:")){
					if(!(input.size() == 1 && input.get(0).equals(""))){
						input.add("");
					}
				}else{
					input.set(input.size()-1, input.get(input.size()-1) + line + "\n" ) ;
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
	}
	
	public int size(){
		return input.size();
	}
	
	public String get(int i){
		return input.get(i);
	}
	
	public void clear(){
		input.clear();
		input.add("");
	}
	
	public void add(String s){
		if(input.get(0).equals("")){
			input.set(0, s);
		}else{
			input.add(s);
		}
	}

}
