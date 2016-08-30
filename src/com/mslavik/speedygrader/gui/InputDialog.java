package com.mslavik.speedygrader.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import com.mslavik.speedygrader.SpeedyGrader;
import com.mslavik.speedygrader.io.Input;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class InputDialog extends JDialog implements ActionListener, WindowListener {

	private ArrayList<RunArea> inputPanels = new ArrayList<RunArea>();

	private JMenuBar menuBar;
	private JMenuItem exportRuns, importRuns;
	private JPanel mainPanel, contentPanel, buttonPanel;
	private JButton addButton, removeButton, saveButton;

	private Input in;

	public InputDialog() {
		super(SpeedyGrader.getInstance().getGUI(), "Input Editor");

		SpeedyGrader sg = SpeedyGrader.getInstance();
		in = sg.getInput();

		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		JLabel l = new JLabel("Input Editor", SwingConstants.CENTER);
		l.setFont(sg.getGUI().getTextFont());
		mainPanel.add(l, BorderLayout.NORTH);

		contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

		for (int i = 0; i < in.size(); i++) {
			addRunArea(in.get(i));
		}

		mainPanel.add(new JScrollPane(contentPanel), BorderLayout.CENTER);

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new MigLayout("", "[grow][][grow]"));
		addButton = new JButton("Add new run");
		addButton.addActionListener(this);
		addButton.setFont(sg.getGUI().getTextFont());
		removeButton = new JButton("Remove last run");
		removeButton.addActionListener(this);
		removeButton.setFont(sg.getGUI().getTextFont());
		saveButton = new JButton("Save and close");
		saveButton.addActionListener(this);
		saveButton.setFont(sg.getGUI().getTextFont());
		buttonPanel.add(addButton, "align center");
		buttonPanel.add(removeButton, "align center");
		buttonPanel.add(saveButton, "align center");

		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu("  File  ");
		fileMenu.setMnemonic(KeyEvent.VK_F);

		exportRuns = new JMenuItem("Export runs");
		exportRuns.setToolTipText("Save the runs to a file.");
		exportRuns.addActionListener(this);
		exportRuns.setFont(sg.getGUI().getTextFont());
		exportRuns.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));

		importRuns = new JMenuItem("Import runs");
		importRuns.setToolTipText("Load runs from a file.");
		importRuns.addActionListener(this);
		importRuns.setFont(sg.getGUI().getTextFont());
		importRuns.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));

		fileMenu.add(exportRuns);
		fileMenu.add(importRuns);

		menuBar.add(fileMenu);

		this.setJMenuBar(menuBar);

		this.addWindowListener(this);
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.setContentPane(mainPanel);
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setSize(500, 350);
		this.setResizable(false);
		this.setLocationRelativeTo(sg.getGUI());
		this.setVisible(true);
	}

	private void addRunArea(String text) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weighty = 1;
		gbc.gridx = 0;
		gbc.gridy = inputPanels.size();
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;

		RunArea ra = new RunArea(text);
		inputPanels.add(ra);
		contentPanel.add(ra, gbc);
		this.validate();
		this.repaint();
	}

	private void removeRunArea() {
		contentPanel.remove(inputPanels.remove(inputPanels.size() - 1));
		this.validate();
		this.repaint();
	}

	private void save() {
		in.clear();
		for (int i = 0; i < inputPanels.size(); i++) {
			in.add(inputPanels.get(i).getText());
		}
		// We changed the input so lets re-run the current file
		SpeedyGrader.getInstance().startComplieAndRun();
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource().equals(addButton)) {
			if (inputPanels.size() < 20) {
				addRunArea("");
			}
		} else if (ae.getSource().equals(removeButton)) {
			if (inputPanels.size() > 0) {
				removeRunArea();
			}
		} else if (ae.getSource().equals(saveButton)) {
			save();
			this.setVisible(false);
			this.dispose();
		} else if (ae.getSource().equals(exportRuns)) {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.removeChoosableFileFilter(chooser.getChoosableFileFilters()[0]);
			chooser.addChoosableFileFilter(new FileNameExtensionFilter("Speedy Grader Run File", "sgrun"));
			int ret = chooser.showSaveDialog(this);
			if (ret == JFileChooser.APPROVE_OPTION) {
				File f = chooser.getSelectedFile();
				if(!f.getName().endsWith(".sgrun")){
					f = new File(f.getParentFile(), f.getName()+".sgrun");
				}
				writeExport(f);
			}
		} else if (ae.getSource().equals(importRuns)) {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.removeChoosableFileFilter(chooser.getChoosableFileFilters()[0]);
			chooser.addChoosableFileFilter(new FileNameExtensionFilter("Speedy Grader Run File", "sgrun"));
			int ret = chooser.showOpenDialog(this);
			if (ret == JFileChooser.APPROVE_OPTION) {
				File f = chooser.getSelectedFile();
				if(f.exists()){
					int i = JOptionPane.showConfirmDialog(this, "Importing runs will clear all current runs. Continue?", "Continue?", JOptionPane.OK_CANCEL_OPTION);
					if(i == JOptionPane.OK_OPTION){
						readInput(f);
					}
				}
			}
		}
	}
	
	public void writeExport(File f){
		if(f.exists()){
			f.delete();
		}
		f.getParentFile().mkdirs();
		try {
			f.createNewFile();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Unable to save file.", "Error", JOptionPane.ERROR_MESSAGE);
		}
		
		try {
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(f));
		
			for(int i = 0; i < inputPanels.size(); i++){
				String text = inputPanels.get(i).getText();
				ZipEntry e = new ZipEntry(i+".run");
				out.putNextEntry(e);
		
				byte[] data = text.getBytes();
				out.write(data, 0, data.length);
				out.closeEntry();
			}
	
			out.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Unable to save file.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void readInput(File f){
		HashMap<Integer, String> runs = new HashMap<Integer, String>();
		
		try {
			ZipInputStream in = new ZipInputStream(new FileInputStream(f));
			
			ZipEntry ent = null;
			while((ent = in.getNextEntry()) != null){
				Integer num = Integer.parseInt(ent.getName().split("\\.")[0]);
				StringBuilder sb = new StringBuilder();
				for (int c = in.read(); c != -1; c = in.read()) {
				    sb.append((char)c);
				}
	            runs.put(num, sb.toString());
			}
			
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Unable to read file.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		ArrayList<Entry<Integer, String>> runsSorted = new ArrayList<Entry<Integer, String>>(runs.entrySet());
		Collections.sort(runsSorted, new Comparator<Entry<Integer, String>>() {
			@Override
			public int compare(Entry<Integer, String> o1, Entry<Integer, String> o2) {
				return o1.getKey().compareTo(o2.getKey());
			}
		});
		
		while(!inputPanels.isEmpty()){
			contentPanel.remove(inputPanels.remove(inputPanels.size() - 1));
		}
		this.validate();
		this.repaint();
		
		for(Entry<Integer, String> ent : runsSorted){
			addRunArea(ent.getValue());
		}
	}

	@Override
	public void windowClosing(WindowEvent e) {
		boolean changed = false;

		if (inputPanels.size() == in.size()) {
			for (int i = 0; i < in.size(); i++) {
				if (!inputPanels.get(i).getText().equals(in.get(i))) {
					changed = true;
					break;
				}
			}
		} else {
			changed = true;
		}

		if (changed) {
			int i = JOptionPane.showConfirmDialog(this, "Would you like to save your changes to the inputs?", "Save?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			switch (i) {
			case JOptionPane.YES_OPTION:
				save();
			case JOptionPane.NO_OPTION:
				this.dispose();
				break;
			case JOptionPane.CANCEL_OPTION:
			case JOptionPane.CLOSED_OPTION:
				break;
			}
		} else {
			this.dispose();
		}
	}

	// Extra window listener methods
	public void windowOpened(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}
}

@SuppressWarnings("serial")
class RunArea extends JPanel {

	JLabel label;
	JTextArea area;

	public RunArea(String text) {
		this.setLayout(new MigLayout());

		label = new JLabel("Run:");
		area = new JTextArea(5, 38);
		
		area.setText(text);

		this.add(label, "cell 0 0");
		this.add(new JScrollPane(area), "cell 1 0 4 2");
	}

	public String getText() {
		return area.getText();
	}

	public void setText(String s) {
		area.setText(s);
	}

}