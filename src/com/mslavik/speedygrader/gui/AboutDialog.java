package com.mslavik.speedygrader.gui;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;

import com.mslavik.speedygrader.SpeedyGrader;

@SuppressWarnings("serial")
public class AboutDialog extends JDialog {
	
	private static String[] licenses = {
		"MigLayout",
		"RSyntaxTextArea",
		"HttpComponents"
	};
	
	private JPanel mainPanel;
	private JPanel topPanel;
	private JTextPane licensePanel;
	
	public AboutDialog() {
		super(SpeedyGrader.getInstance().getGUI(), "About SpeedyGrader");
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		
		licensePanel = new JTextPane();
		licensePanel.setEditable(false);
		licensePanel.setFont(SpeedyGrader.getInstance().getGUI().getTextFont());
		
		String licensesStr = "";
		ArrayList<InputStream> licenseStreams = new ArrayList<InputStream>();
		
		for(String s : licenses){
			try{
				licenseStreams.add(AboutDialog.class.getResourceAsStream("/"+s+".License.txt"));
			}catch(Exception e){
				try {
					licenseStreams.add(new FileInputStream("licenses"+File.separator+s+".License.txt"));
				} catch (FileNotFoundException e1) {
					System.out.println("Could not find licenses!");
				}
			}
		}
		
		int i = 0;
		for(InputStream is : licenseStreams){
			licensesStr += licenses[i++]+" License:\n";
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line;
			try {
				while((line = br.readLine()) != null){
					licensesStr+=line+"\n";
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(!licenseStreams.get(licenseStreams.size()-1).equals(is)){
				licensesStr += "\n\n\n";
			}
		}
		
		licensePanel.setText(licensesStr);
		licensePanel.setCaretPosition(0);
		
		mainPanel.add(new JScrollPane(licensePanel), BorderLayout.CENTER);
		
		topPanel = new JPanel();
		
		JLabel label = new JLabel("<html><div style='text-align: center;'>Created by: Mitchell Slavik<br/>Email: Mitchell.A.Slavik@gmail.com</html>", JLabel.CENTER);
		label.setFont(SpeedyGrader.getInstance().getGUI().getTextFont());
		topPanel.add(label, BorderLayout.CENTER);
		
		mainPanel.add(topPanel, BorderLayout.NORTH);
		
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setContentPane(mainPanel);
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setSize(850, 500);
		this.setResizable(false);
		this.setLocationRelativeTo(SpeedyGrader.getInstance().getGUI());
		this.setVisible(true);
	}

}
