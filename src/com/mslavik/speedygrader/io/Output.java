package com.mslavik.speedygrader.io;

import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JTextArea;

public class Output {
	
	private CopyOnWriteArrayList<String> output;
	private JTextArea area;
	
	public Output(JTextArea area, int runs){
		this.area = area;
		output = new CopyOnWriteArrayList<String>();
		for(int i = 0; i < runs; i++){
			output.add("Running...");
		}
		updateText();
	}
	
	public void setOutput(int i, String out){
		output.set(i, out);
		updateText();
	}
	
	public void updateText(){
		String text = "";
		for(int i = 0; i < output.size(); i++){
			if(output.size() > 1){
				text+="Run "+(i+1)+":\n";
			}
			text += output.get(i) + "\n";
		}
		area.setText(text);
	}

}
