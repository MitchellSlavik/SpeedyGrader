package com.mslavik.speedygrader;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import com.mslavik.speedygrader.io.Input;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class InputCreator extends JDialog implements ActionListener, WindowListener {

	private SpeedyGrader speedyGrader;

	private ArrayList<RunArea> inputPanels = new ArrayList<RunArea>();

	private JPanel mainPanel, contentPanel, buttonPanel;
	private JButton addButton, removeButton, saveButton;

	private Input in;

	public InputCreator(SpeedyGrader sg) {
		super(sg, "Input Editor");

		speedyGrader = sg;
		in = speedyGrader.getInput();

		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		JLabel l = new JLabel("Input Editor", SwingConstants.CENTER);
		mainPanel.add(l, BorderLayout.NORTH);

		contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

		for (int i = 0; i < in.size(); i++) {
			addRunArea();
			inputPanels.get(i).setText(in.get(i));
			;
		}

		mainPanel.add(new JScrollPane(contentPanel), BorderLayout.CENTER);

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new MigLayout("", "[grow][][grow]"));
		addButton = new JButton("Add new run");
		addButton.addActionListener(this);
		removeButton = new JButton("Remove last run");
		removeButton.addActionListener(this);
		saveButton = new JButton("Save and close");
		saveButton.addActionListener(this);
		buttonPanel.add(addButton, "align center");
		buttonPanel.add(removeButton, "align center");
		buttonPanel.add(saveButton, "align center");

		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		this.addWindowListener(this);
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.setContentPane(mainPanel);
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setSize(500, 350);
		this.setResizable(false);
		this.setLocationRelativeTo(sg);
		this.setVisible(true);
	}

	private void addRunArea() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weighty = 1;
		gbc.gridx = 0;
		gbc.gridy = inputPanels.size();
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;

		RunArea ra = new RunArea();
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
		//We changed the input so lets re-run the current file
		speedyGrader.startComplieAndRun();
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource().equals(addButton)) {
			if (inputPanels.size() < 20) {
				addRunArea();
			}
		} else if (ae.getSource().equals(removeButton)) {
			if (inputPanels.size() > 0) {
				removeRunArea();
			}
		} else if (ae.getSource().equals(saveButton)) {
			save();
			this.setVisible(false);
			this.dispose();
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
		}else{
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

	public RunArea() {
		this.setLayout(new MigLayout());

		label = new JLabel("Run:");
		area = new JTextArea(5, 38);

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