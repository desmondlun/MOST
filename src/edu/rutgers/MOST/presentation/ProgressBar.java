package edu.rutgers.MOST.presentation;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

//based on code from http://www.java2s.com/Code/Java/Swing-JFC/CreateaProgressBar.htm
public class ProgressBar extends JFrame {

	public JProgressBar progress = new JProgressBar(0, 100);
	int num = 0;
	public ProgressBar() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel pane = new JPanel();
		progress.setValue(0);
		progress.setStringPainted(true);
		pane.add(progress);
		setContentPane(pane);
	}
}