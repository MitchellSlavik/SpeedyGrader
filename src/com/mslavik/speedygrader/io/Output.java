package com.mslavik.speedygrader.io;

import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class Output {
	
	private CopyOnWriteArrayList<String> output;
	private JTextArea area;
	private boolean cancelled;
	
	public Output(JTextArea area, int runs){
		this.area = area;
		this.cancelled = false;
		output = new CopyOnWriteArrayList<String>();
		area.setText("Running...");
		for(int i = 0; i < runs; i++){
			output.add("~!~!~!~");
		}
	}
	
	public void cancel(){
		cancelled = true;
	}
	
	public void setOutput(int i, String out){
		output.set(i, out);
		if(!output.contains("~!~!~!~")){
			updateText();
		}
	}
	
	public void updateText(){
		if(cancelled){
			return;
		}	
		
		String text = "";
		for(int i = 0; i < output.size(); i++){
			if(output.size() > 1){
				text+="Run "+(i+1)+":\n";
			}
			text += output.get(i) + "\n";
		}
		
		// Just make sure we arn't cancelled since the cancel will come from another thread
		if(!cancelled){
			area.setText(text);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					area.repaint();
				}
			});
		}
	}

}
