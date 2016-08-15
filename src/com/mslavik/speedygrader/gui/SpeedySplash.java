package com.mslavik.speedygrader.gui;

import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class SpeedySplash extends JWindow {
	
	public SpeedySplash() {
		ImageIcon img;
		URL imgURL = getClass().getResource("/speedygrader-splash.png");
	    if (imgURL != null) {
	        img = new ImageIcon(imgURL, "");
	    } else {
	        System.err.println("Couldn't find file: " + "/speedygrader-splash.png");
	        img = null;
	    }
		
		this.setSize(600, 450);
		this.getContentPane().add(new JLabel("", img, SwingConstants.CENTER));
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

}
