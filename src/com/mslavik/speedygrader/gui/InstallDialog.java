package com.mslavik.speedygrader.gui;

import javax.swing.JDialog;

import com.mslavik.speedygrader.SpeedyGrader;

@SuppressWarnings("serial")
public class InstallDialog extends JDialog {
	
	public InstallDialog() {
		super(SpeedyGrader.getInstance().getGUI(), "About");
		
	}

}
