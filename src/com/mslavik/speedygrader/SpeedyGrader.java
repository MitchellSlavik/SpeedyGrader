package com.mslavik.speedygrader;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.SequenceInputStream;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

@SuppressWarnings("serial")
public class SpeedyGrader extends JFrame implements ActionListener, ListSelectionListener {

	private JMenuBar menuBar;
	private JButton openButton;
	private JButton inputButton;
	private JList<String> filesList;
	private DefaultListModel<String> filesListModel;
	private JSplitPane splitMainPane;
	private JSplitPane splitEditorPane;
	private RSyntaxTextArea editorTextArea;
	private JTextArea consoleTextArea;

	private File javaFilesLoc;
	private String inputText;
	
	private Font textFont;

	public SpeedyGrader() {
		super("SpeedyGrader");

		this.setSize(1000, 500);
		
		textFont = new Font("Consolas", 0, 16);

		menuBar = new JMenuBar();

		openButton = new JButton("Open Folder");
		openButton.setToolTipText("The folder that contains the .java files.");
		openButton.addActionListener(this);
		openButton.setFont(textFont);

		inputButton = new JButton("Select Input File");
		inputButton.addActionListener(this);
		inputButton.setFont(textFont);

		menuBar.add(openButton);
		menuBar.add(inputButton);

		this.setJMenuBar(menuBar);

		filesList = new JList<String>();

		filesList.setSelectionMode(JList.VERTICAL);
		filesList.setLayoutOrientation(JList.VERTICAL);
		filesList.setVisibleRowCount(-1);
		filesList.addListSelectionListener(this);
		filesList.setFont(textFont);

		filesListModel = new DefaultListModel<String>();

		filesListModel.addElement("");

		filesList.setModel(filesListModel);

		editorTextArea = new RSyntaxTextArea();
		editorTextArea.setEditable(false);
		editorTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
		editorTextArea.setCodeFoldingEnabled(true);
		editorTextArea.setFont(textFont);
		editorTextArea.setTabSize(4);
		consoleTextArea = new JTextArea();
		consoleTextArea.setEditable(false);
		consoleTextArea.setFont(textFont);

		splitEditorPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitEditorPane.add(new RTextScrollPane(editorTextArea));
		splitEditorPane.add(new JScrollPane(consoleTextArea));
		splitEditorPane.setDividerLocation(.8);

		splitMainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitMainPane.add(new JScrollPane(filesList));
		splitMainPane.add(splitEditorPane);
		splitMainPane.setDividerLocation(.2);

		this.setLocationRelativeTo(null);
		this.add(splitMainPane);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource().equals(openButton)) {
			JFileChooser chooser = new JFileChooser(new File("."));
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int ret = chooser.showOpenDialog(this);
			if (ret == JFileChooser.APPROVE_OPTION) {
				javaFilesLoc = chooser.getSelectedFile();
				newFolderSelected();
			}
		} else if (ae.getSource().equals(inputButton)) {
			JFileChooser chooser = new JFileChooser(new File("."));
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int ret = chooser.showOpenDialog(this);
			if (ret == JFileChooser.APPROVE_OPTION) {
				inputText = "";
				File inputFile = chooser.getSelectedFile();
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));
					String line;
					while ((line = br.readLine()) != null) {
						inputText += line + "\n";
					}
					br.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {

				}
			}
		}
	}

	public void newFolderSelected() {
		filesListModel.clear();
		for (File f : javaFilesLoc.listFiles(new FileFilter() {
			@Override
			public boolean accept(File f) {
				if (f.isFile() && f.getName().endsWith(".java")) {
					return true;
				}
				return false;
			}
		})) {
			filesListModel.addElement(f.getName().substring(0, f.getName().length() - 5));
		}
		splitMainPane.setDividerLocation(.2);
		this.revalidate();
		this.repaint();
	}

	@Override
	public void valueChanged(ListSelectionEvent lse) {
		if (!lse.getValueIsAdjusting()) {
			String name = filesList.getSelectedValue();
			editorTextArea.setText("");
			try {
				BufferedReader in = new BufferedReader(new FileReader(new File(javaFilesLoc, name + ".java")));
				String line = in.readLine();
				while (line != null) {
					editorTextArea.append(line + "\n");
					line = in.readLine();
				}
				in.close();
			} catch (Exception e) {
			}
			startComplieAndRun();
		}

	}

	public void startComplieAndRun() {
		String name = filesList.getSelectedValue();
		consoleTextArea.setText("");

		File f = new File(javaFilesLoc, "bin\\");
		f.mkdirs();
		try {
			Process pro1 = Runtime.getRuntime().exec("javac -d \"" + f.getAbsolutePath() + "\" \"" + javaFilesLoc.getAbsolutePath() + "\\" + name + ".java\"");
			pro1.waitFor();

			BufferedReader in = new BufferedReader(new InputStreamReader(new SequenceInputStream(pro1.getInputStream(), pro1.getErrorStream())));
			String line = null;
			while ((line = in.readLine()) != null) {
				consoleTextArea.append(line + "\n");
			}

			Process pro2 = Runtime.getRuntime().exec("java -cp \"" + f.getAbsolutePath() + "\" " + name);
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(pro2.getOutputStream()));
			out.write(inputText);
			out.close();
			in = new BufferedReader(new InputStreamReader(new SequenceInputStream(pro2.getInputStream(), pro2.getErrorStream())));
			while ((line = in.readLine()) != null) {
				consoleTextArea.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
