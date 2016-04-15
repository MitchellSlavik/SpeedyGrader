package com.mslavik.speedygrader.gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.mslavik.speedygrader.SpeedyGrader;
import com.mslavik.speedygrader.source.SourceFile;

@SuppressWarnings("serial")
public class SpeedyGraderInterface extends JFrame implements ActionListener, ListSelectionListener {

	private JMenuBar menuBar;
	private JMenuItem openItem, inputItem, saveItem, refreshItem;
	private JCheckBoxMenuItem timeoutPrograms;
	private JList<SourceFile> filesList;
	private DefaultListModel<SourceFile> filesListModel;
	private JSplitPane splitMainPane;
	private JSplitPane splitEditorPane;
	private JTabbedPane tabbedPane;
	private ArrayList<EditorPanel> editorPanels;
	private JTextArea consoleTextArea;

	private Font textFont;

	public SpeedyGraderInterface() {
		super("SpeedyGrader");

		this.setSize(1000, 500);
		
		BufferedImage icon = null;
		try {
			icon = ImageIO.read(new File( "img"+File.separator+"speedygrader-icon.png"));
		} catch (IOException e) {
			try{
				icon = ImageIO.read(SpeedyGraderInterface.class.getResourceAsStream("/speedygrader-icon.png"));
			}catch(IOException e2){
				e2.printStackTrace();
			}
		}
		
		this.setIconImage(icon);

		textFont = new Font("Consolas", 0, 16);

		menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("  File  ");
		fileMenu.setMnemonic(KeyEvent.VK_F);

		openItem = new JMenuItem("Select Folder");
		openItem.setToolTipText("The folder that contains the source files.");
		openItem.addActionListener(this);
		openItem.setFont(textFont);
		openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));

		inputItem = new JMenuItem("Set Input");
		inputItem.addActionListener(this);
		inputItem.setFont(textFont);
		inputItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));

		saveItem = new JMenuItem("Save All and Run");
		saveItem.addActionListener(this);
		saveItem.setFont(textFont);
		saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));

		fileMenu.add(openItem);
		fileMenu.add(inputItem);
		fileMenu.add(saveItem);
		
		JMenu optionsMenu = new JMenu(" Options ");
		optionsMenu.setMnemonic(KeyEvent.VK_O);
		
		refreshItem = new JMenuItem("Refresh Folder");
		refreshItem.setFont(textFont);
		refreshItem.addActionListener(this);
		refreshItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		optionsMenu.add(refreshItem);
		
		timeoutPrograms = new JCheckBoxMenuItem("Timeout Programs");
		timeoutPrograms.setSelected(true);
		timeoutPrograms.setFont(textFont);
		optionsMenu.add(timeoutPrograms);
		
		menuBar.add(fileMenu);
		
		menuBar.add(optionsMenu);

		this.setJMenuBar(menuBar);

		filesList = new JList<SourceFile>();

		filesList.setSelectionMode(JList.VERTICAL);
		filesList.setLayoutOrientation(JList.VERTICAL);
		filesList.setVisibleRowCount(-1);
		filesList.addListSelectionListener(this);
		filesList.setFont(textFont);

		filesListModel = new DefaultListModel<SourceFile>();

		filesList.setModel(filesListModel);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		
		editorPanels = new ArrayList<EditorPanel>();
		
		consoleTextArea = new JTextArea();
		consoleTextArea.setEditable(false);
		consoleTextArea.setFont(textFont);

		splitEditorPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitEditorPane.add(tabbedPane);
		splitEditorPane.add(new JScrollPane(consoleTextArea));
		splitEditorPane.setDividerLocation(.8);

		splitMainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitMainPane.add(new JScrollPane(filesList));
		splitMainPane.add(splitEditorPane);
		splitMainPane.setDividerLocation(.2);

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.add(splitMainPane);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource().equals(openItem)) {
			JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.home")));
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int ret = chooser.showOpenDialog(this);
			if (ret == JFileChooser.APPROVE_OPTION) {
				newFolderSelected(chooser.getSelectedFile());
			}
		} else if (ae.getSource().equals(inputItem)) {
			new InputCreator();
		} else if (ae.getSource().equals(saveItem)) {
			for(EditorPanel ep : editorPanels){
				ep.save();
			}
			SpeedyGrader.getInstance().startComplieAndRun();
		}else if (ae.getSource().equals(refreshItem)){
			newFolderSelected(null);
		}
	}

	public void newFolderSelected(File dir) {
		tabbedPane.removeAll();
		editorPanels.clear();
		consoleTextArea.setText("");
		filesListModel.clear();
		
		for(SourceFile sf : SpeedyGrader.getInstance().getSourceFiles(dir)){
			filesListModel.addElement(sf);
		}
		
		splitMainPane.setDividerLocation(.2);
		this.revalidate();
		this.repaint();
	}

	@Override
	public void valueChanged(ListSelectionEvent lse) {
		if (!lse.getValueIsAdjusting()) {
			boolean needsSave = false;
			for(EditorPanel ep : editorPanels){
				if(ep.needsSave()){
					needsSave = true;
				}
			}
			if(needsSave){
				int i = JOptionPane.showConfirmDialog(this, "Would you like to save before switching files?", "Save?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				switch(i){
				case JOptionPane.YES_OPTION:
					for(EditorPanel ep : editorPanels){
						ep.save();
					}
					break;
				case JOptionPane.NO_OPTION:
					break;
				case JOptionPane.CANCEL_OPTION:
				case JOptionPane.CLOSED_OPTION:
					return;
				}
			}
			tabbedPane.removeAll();
			editorPanels.clear();
			
			if(filesList.getSelectedValue() != null){
				for(Entry<String, File> ents : filesList.getSelectedValue().getFileList().entrySet()){
					EditorPanel ep = new EditorPanel(ents.getValue(), textFont);
					tabbedPane.addTab(ents.getKey(), ep);
					editorPanels.add(ep);
				}
				
				SpeedyGrader.getInstance().startComplieAndRun();
			}
		}
	}

	public Font getTextFont(){
		return textFont;
	}
	
	public boolean timeoutPrograms(){
		return timeoutPrograms.isSelected();
	}
	
	public SourceFile getSelectedSourceFile(){
		return filesList.getSelectedValue();
	}
	
	public void setOutputTextArea(String s){
		consoleTextArea.setText(s);
	}
	
	public JTextArea getOutputTextArea(){
		return consoleTextArea;
	}

}
