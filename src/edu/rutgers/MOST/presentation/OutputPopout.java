package edu.rutgers.MOST.presentation;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import edu.rutgers.MOST.config.LocalConfig;

import java.io.*;

//based on code from http://leepoint.net/notes-java/examples/components/editor/nutpad.html
public class OutputPopout extends JFrame {

	private static JTextArea    textArea;
	private JFileChooser fileChooser = new JFileChooser(new java.io.File("."));

	public OutputPopout() {
		//... Create scrollable text area.
		textArea = new JTextArea(30, 60);
		textArea.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		//textArea.setFont(new Font("monospaced", Font.PLAIN, 14));
		textArea.setEditable(false);
		JScrollPane scrollingText = new JScrollPane(textArea);

		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());
		content.add(scrollingText, BorderLayout.CENTER);

		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = menuBar.add(new JMenu("File"));
		fileMenu.setMnemonic('F');
		JMenuItem openItem = new JMenuItem("Open");
		fileMenu.add(openItem);
		openItem.setMnemonic('O');
		openItem.addActionListener(new OpenAction()); 
		JMenuItem saveAsItem = new JMenuItem("Save As...");
		fileMenu.add(saveAsItem);
		saveAsItem.setMnemonic('A');
		saveAsItem.addActionListener(new SaveAction());
		fileMenu.addSeparator();
		JMenuItem exitItem = new JMenuItem("Exit");
		fileMenu.add(exitItem);
		exitItem.setMnemonic('X');
		exitItem.addActionListener(new ExitAction());

		setContentPane(content);
		setJMenuBar(menuBar);

		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setTitle(GraphicalInterfaceConstants.TITLE + " - " + LocalConfig.getInstance().getLoadedDatabase());
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	class OpenAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int retval = fileChooser.showOpenDialog(OutputPopout.this);
			if (retval == JFileChooser.APPROVE_OPTION) {
				File f = fileChooser.getSelectedFile();
				String filename = fileChooser.getSelectedFile().getName();
				setTitle(GraphicalInterfaceConstants.TITLE + " - " + filename);
				try {
					FileReader reader = new FileReader(f);
					textArea.read(reader, "");  // Use TextComponent read
				} catch (IOException ie) {
					System.exit(1);
				}
			}
		}
	}

	class SaveAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {			
			boolean done = false;
			while (!done) {
				int retval = fileChooser.showSaveDialog(OutputPopout.this);
				if (retval == JFileChooser.CANCEL_OPTION) {
					done = true;
				}
				if (retval == JFileChooser.APPROVE_OPTION) {					
					String path = fileChooser.getSelectedFile().getPath();
					if (!path.endsWith(".txt")) {
						path = path + ".txt";
					}
					File f = new File(path);

					if (path == null) {
						done = true;
					} else {        	    	  
						if (f.exists()) {
							int confirmDialog = JOptionPane.showConfirmDialog(fileChooser, "Replace existing file?");
							if (confirmDialog == JOptionPane.YES_OPTION) {
								done = true;
								writeFile(f);
							} else if (confirmDialog == JOptionPane.NO_OPTION) {        		    	  
								done = false;
							} else {
								done = true;
							}       		    	  
						} else {
							done = true;
							writeFile(f);
						}
					}	
				}
			}
		}
	}
	
	public void writeFile(File f) {
		try {
			FileWriter writer = new FileWriter(f);
			textArea.write(writer);  // Use TextComponent write
		} catch (IOException ie) {
			JOptionPane.showMessageDialog(OutputPopout.this, ie);
			System.exit(1);
		}
	}
	
	//based on http://www.java2s.com/Code/Java/File-Input-Output/Textfileviewer.htm
	public void load(String path) {
		File file;
		FileReader in = null;

		try {
			file = new File(path); 
			in = new FileReader(file); 
			char[] buffer = new char[4096]; // Read 4K characters at a time
			int len; 
			textArea.setText("");  	     
			while ((len = in.read(buffer)) != -1) { // Read a batch of chars
				String s = new String(buffer, 0, len); 
				textArea.append(s); 
			}
			textArea.setCaretPosition(0); 
			setTitle(GraphicalInterfaceConstants.TITLE + " - " + path);
		}

		catch (IOException e) {

		}

		finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
			}
		}
	}

	public void clear() {
		textArea.setText(""); 
		setTitle(LocalConfig.getInstance().getDatabaseName());
	}

	class ExitAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
		}
	}

	public static void main(String[] args) {
		new OutputPopout();
		//loadOutputPane("C://CMakeCache.txt");
	}
}


