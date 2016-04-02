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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

import com.mslavik.speedygrader.io.Input;
import com.mslavik.speedygrader.io.Output;
import com.mslavik.speedygrader.source.CppFile;
import com.mslavik.speedygrader.source.JavaFile;
import com.mslavik.speedygrader.source.SourceFile;
import com.mslavik.speedygrader.source.SourceRunner;
import com.mslavik.speedygrader.source.SourceType;
import com.mslavik.speedygrader.source.group.CppGroupFile;
import com.mslavik.speedygrader.source.group.JavaGroupFile;
import com.mslavik.speedygrader.utils.FolderSorter;
import com.mslavik.speedygrader.utils.SpeedyGraderFileFilter;

@SuppressWarnings("serial")
public class SpeedyGrader extends JFrame implements ActionListener, ListSelectionListener {

	private JMenuBar menuBar;
	private JMenuItem openItem, inputItem, saveItem;
	private JCheckBoxMenuItem timeoutPrograms;
	private JList<SourceFile> filesList;
	private DefaultListModel<SourceFile> filesListModel;
	private JSplitPane splitMainPane;
	private JSplitPane splitEditorPane;
	private JTabbedPane tabbedPane;
	private ArrayList<EditorPanel> editorPanels;
	private JTextArea consoleTextArea;

	private File filesLoc;
	private Input input;
	private Output output;

	private Font textFont;

	private ExecutorService exe;
	private ArrayList<Future<?>> futures = new ArrayList<Future<?>>();

	public SpeedyGrader() {
		super("SpeedyGrader");

		this.setSize(1000, 500);
		
		BufferedImage icon = null;
		try {
			icon = ImageIO.read(new File( "lib"+File.separator+"speedygrader-icon.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.setIconImage(icon);

		input = new Input();

		exe = Executors.newCachedThreadPool();

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
				filesLoc = chooser.getSelectedFile();
				newFolderSelected();
			}
		} else if (ae.getSource().equals(inputItem)) {
			new InputCreator(this);
		} else if (ae.getSource().equals(saveItem)) {
			for(EditorPanel ep : editorPanels){
				ep.save();
			}
			startComplieAndRun();
		}
	}

	public void newFolderSelected() {
		SourceFile.setFolders(filesLoc);
		tabbedPane.removeAll();
		editorPanels.clear();
		consoleTextArea.setText("");
		filesListModel.clear();
		
		FolderSorter.sort(filesLoc);
		
		for (File f : filesLoc.listFiles(new SpeedyGraderFileFilter())) {
			if(f.isFile()){
				SourceType st = SourceType.getSourceType(f);
	
				if (st != null && SourceFile.hasMain(st, f)) {
					SourceFile sf = null;
	
					switch (st) {
					case CPP:
						sf = new CppFile(f);
						break;
					case JAVA:
						sf = new JavaFile(f);
						break;
					}
					
					if (sf != null) {
						filesListModel.addElement(sf);
					}
				}
			}else{
				for(File f2 : f.listFiles(new SpeedyGraderFileFilter())){
					SourceType st = SourceType.getSourceType(f2);
					
					if (st != null && SourceFile.hasMain(st, f2)) {
						SourceFile sf = null;
						
						switch (st) {
						case CPP:
							sf = new CppGroupFile(f2);
							break;
						case JAVA:
							sf = new JavaGroupFile(f2);
							break;
						}
						
						if (sf != null) {
							filesListModel.addElement(sf);
						}
						
						break;
					}
				}
			}
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
			
			for(Entry<String, ArrayList<File>> ents : filesList.getSelectedValue().getFileList().entrySet()){
				EditorPanel ep = null;
				if(ents.getValue().size() > 1){
					ep = new EditorPanel(ents.getValue().get(0), ents.getValue().get(1), textFont);
				}else{
					ep = new EditorPanel(ents.getValue().get(0), textFont);
				}
				tabbedPane.addTab(ents.getKey(), ep);
				editorPanels.add(ep);
			}
			
			startComplieAndRun();
		}
	}

	public void startComplieAndRun() {
		//New file to compile and run, cancel the old one.
		if(output != null){
			output.cancel();
		}
		for(Future<?> future : futures){
			if(!future.isDone()){
				future.cancel(true);
			}
		}
		futures.clear();
		
		//Start the new compile
		SourceFile sf = filesList.getSelectedValue();
		consoleTextArea.setText("");
		
		if(sf != null){
			String compileErrors = sf.compile();
	
			if (compileErrors.length() != 0) {
				consoleTextArea.append("Compile Errors:\n" + compileErrors);
			} else {
				
				//Run the inputs if we complied successfully
				output = new Output(consoleTextArea, input.size());
				for (int i = 0; i < input.size(); i++) {
					futures.add(exe.submit(new SourceRunner(this, output, i, sf)));
				}
			}
		}
	}
	
	public Input getInput() {
		return input;
	}
	
	public Font getTextFont(){
		return textFont;
	}
	
	public boolean timeoutPrograms(){
		return timeoutPrograms.isSelected();
	}

}
