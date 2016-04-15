package com.mslavik.speedygrader.io;

import java.util.ArrayList;

public class Input {
	
	private ArrayList<String> input;
	
	public Input(){
		input = new ArrayList<String>();
	}
	
	public int size(){
		if(input.isEmpty()){
			return 1;
		}
		return input.size();
	}
	
	public String get(int i){
		if(input.isEmpty()){
			return "";
		}
		return input.get(i);
	}
	
	public void clear(){
		input.clear();
	}
	
	public void add(String s){
		input.add(s);
	}

}
