package com.mslavik.speedygrader.gui;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.SplashScreen;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;

public class SpeedySplash extends JWindow {
	
	private SplashScreen sc;
	private Graphics2D g;
	private ImageIcon img;
	
	public SpeedySplash() {
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
