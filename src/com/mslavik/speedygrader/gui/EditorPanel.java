package com.mslavik.speedygrader.gui;

import java.awt.Font;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;

import javax.swing.JPanel;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.mslavik.speedygrader.source.SourceType;

@SuppressWarnings("serial")
public class EditorPanel extends JPanel {
	
	private File editedFile;
	private RSyntaxTextArea textArea;
	private String savedText;
	
	public EditorPanel(File editedFile, Font editorFont) {
		this.editedFile = editedFile;
		
		textArea = new RSyntaxTextArea();
		textArea.setEditable(true);
		textArea.setCodeFoldingEnabled(true);
		textArea.setFont(editorFont);
		textArea.setTabSize(4);
		
		SourceType st = SourceType.getSourceType(editedFile);
		if(st != null){
			switch(st){
			case CPP:
				textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);
				break;
			case JAVA:
				textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
				break;
			}
		}
		
		String text = "";
		try {
			BufferedReader in = new BufferedReader(new FileReader(editedFile));
			String line = in.readLine();
			while (line != null) {
				text+=line + "\n";
				line = in.readLine();
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		savedText = text;
		
		this.setLayout(new GridLayout(1, 1));
		this.add(new RTextScrollPane(textArea));
		
		textArea.setText(text);
		textArea.setCaretPosition(0);
	}
	
	public void save(){
		if(needsSave()){
			try {
				PrintWriter pw = new PrintWriter(editedFile);
				pw.append(textArea.getText());
				pw.flush();
				pw.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			savedText = textArea.getText();
		}
	}
	
	public boolean needsSave(){
		return !textArea.getText().equals(savedText);
	}

}
