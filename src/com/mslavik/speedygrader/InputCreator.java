package com.mslavik.speedygrader;

import java.awt.GridBagLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.mslavik.speedygrader.io.Input;

@SuppressWarnings("serial")
public class InputCreator extends JDialog{
	
	private SpeedyGrader speedyGrader;
	
	private ArrayList<JPanel> inputPanels = new ArrayList<JPanel>();
	
	private JPanel mainPanel;
	
	public InputCreator(SpeedyGrader sg){
		super(sg, "Input Editor");
		
		speedyGrader = sg;
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		
		Input in = speedyGrader.getInput();
		
		
		
		this.setContentPane(new JScrollPane(mainPanel));
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setLocationRelativeTo(sg);
		this.setSize(500, 350);
		this.setVisible(true);
	}

}

class RunArea extends JPanel{
	
	JLabel label;
	JButton addButton;
	JButton removeButton;
	JTextArea area;
	
}