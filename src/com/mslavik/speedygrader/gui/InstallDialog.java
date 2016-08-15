package com.mslavik.speedygrader.gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.Desktop.Action;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.mslavik.speedygrader.SpeedyGrader;

@SuppressWarnings("serial")
public class InstallDialog extends JDialog implements ActionListener {
	
	private static String JDK_URL = "http://www.oracle.com/technetwork/java/javase/downloads/index.html";
	private static String MINGW_URL = "https://sourceforge.net/projects/mingw/files/latest/download";
	
	private JButton jdkButton;
	private JButton mingwButton;
	
	public InstallDialog() {
		super(SpeedyGrader.getInstance().getGUI(), "Install Instructions");
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		
		InputStream is = null;
		
		try{
			is = AboutDialog.class.getResourceAsStream("/Install.html");
		}catch(Exception e){
			try {
				is = new FileInputStream("res"+File.separator+"Install.html");
			} catch (FileNotFoundException e1) {
				System.out.println("Could not find licenses!");
			}
		}
		String text = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line;
		try {
			while((line = br.readLine()) != null){
				text+=line+"\n";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		JLabel label = new JLabel(text);
		label.setFont(SpeedyGrader.getInstance().getGUI().getTextFont());
		
		mainPanel.add(label, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel();
		
		jdkButton = new JButton("JDK Download");
		mingwButton = new JButton("MinGW Download");
		
		jdkButton.addActionListener(this);
		mingwButton.addActionListener(this);
		
		buttonPanel.add(jdkButton);
		buttonPanel.add(mingwButton);
		
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		this.setContentPane(new JScrollPane(mainPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		this.setSize(700, 500);
		this.setResizable(false);
		this.setLocationRelativeTo(SpeedyGrader.getInstance().getGUI());
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String url = "";
		
		if(e.getSource().equals(jdkButton)){
			url = JDK_URL;
		}else if(e.getSource().equals(mingwButton)){
			url = MINGW_URL;
		}
		
		if(!url.isEmpty()){
			Desktop d = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
			if(d != null && d.isSupported(Action.BROWSE)){
				try {
					d.browse(URI.create(url));
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}else{
				JOptionPane.showMessageDialog(this, "We were unable to open a web browser. The url has been copied to your clipboard.",
						"Unable to preform operation", JOptionPane.ERROR_MESSAGE);
				StringSelection selection = new StringSelection(url);
			    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			    clipboard.setContents(selection, selection);
			}
		}
	}
	
}
