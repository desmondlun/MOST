package edu.rutgers.MOST.presentation;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRootPane;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

//based on code from http://www.java2s.com/Code/Java/Swing-JFC/CreateaProgressBar.htm
public class ProgressBar extends JFrame {

	//public JButton cancelButton = new JButton("  Cancel  ");
	
	public JProgressBar progress = new JProgressBar(0, 100);
	public ProgressBar() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel pane = new JPanel();
		progress.setValue(0);
		progress.setStringPainted(true);
		pane.add(progress);
		/*
		JPanel pane2 = new JPanel();
		pane2.add(cancelButton);
		pane.add(pane2);
		*/
		setContentPane(pane);
		setUndecorated(true);
		//pane.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.LIGHT_GRAY, Color.GRAY));
		pane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2, false));
	}
}