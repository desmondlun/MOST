package edu.rutgers.MOST.presentation;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import edu.rutgers.MOST.config.LocalConfig;

import java.io.*;

//based on code from http://leepoint.net/notes-java/examples/components/editor/nutpad.html
public class OutputPopout extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static JTextArea    textArea;
	private JFileChooser fileChooser = new JFileChooser(new java.io.File("."));
	private final JMenuItem outputCopyItem = new JMenuItem("Copy");
	private final JMenuItem outputSelectAllItem = new JMenuItem("Select All");
	private String pathName;

	public OutputPopout() {		
		//... Create scrollable text area.
		textArea = new JTextArea(30, 60);
		textArea.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		//textArea.setFont(new Font("monospaced", Font.PLAIN, 14));
		textArea.setEditable(false);
		JScrollPane scrollingText = new JScrollPane(textArea);

		fileChooser.setFileFilter(new TextFileFilter());
		
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
		//setTitle(GraphicalInterfaceConstants.TITLE + " - " + LocalConfig.getInstance().getModelName());
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
		final JPopupMenu outputPopupMenu = new JPopupMenu();
		outputCopyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		outputSelectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		outputPopupMenu.add(outputCopyItem);
		outputCopyItem.setEnabled(false);
		outputCopyItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) { 	
				setClipboardContents(textArea.getSelectedText());							
			}
		});
		outputPopupMenu.add(outputSelectAllItem);
		outputSelectAllItem.setEnabled(false);
		outputSelectAllItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) { 
				textArea.selectAll();							
			}
		});
		
		textArea.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e)  {check(e);}
			public void mouseReleased(MouseEvent e) {check(e);}

			public void check(MouseEvent e) {
				if (e.isPopupTrigger()) { //if the event shows the menu
					outputPopupMenu.show(textArea, e.getX(), e.getY()); 
				}
			}
		});	

		textArea.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				fieldChangeAction();
			}
			public void removeUpdate(DocumentEvent e) {
				fieldChangeAction();
			}
			public void insertUpdate(DocumentEvent e) {
				fieldChangeAction();
			}
			public void fieldChangeAction() {
				if (textArea.getText().length() > 0) {
					outputCopyItem.setEnabled(true);
					outputSelectAllItem.setEnabled(true);
				} else {
					outputCopyItem.setEnabled(false);
					outputSelectAllItem.setEnabled(false);
				}
			}
		});
	}

	class OpenAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String lastPopout_path = GraphicalInterface.curSettings.get("LastPopoutOpen");
			Utilities u = new Utilities();
			// if path is null or does not exist, default used, else last path used
			fileChooser.setCurrentDirectory(new File(u.lastPath(lastPopout_path, fileChooser)));	
			int retval = fileChooser.showOpenDialog(OutputPopout.this);
			if (retval == JFileChooser.APPROVE_OPTION) {
				String path = fileChooser.getSelectedFile().getPath();
				GraphicalInterface.curSettings.add("LastPopoutOpen", path);
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
				String lastPopout_path = GraphicalInterface.curSettings.get("LastPopoutSave");
				Utilities u = new Utilities();
				// if path is null or does not exist, default used, else last path used
				fileChooser.setCurrentDirectory(new File(u.lastPath(lastPopout_path, fileChooser)));
				String titlePrefix = GraphicalInterfaceConstants.TITLE + " - ";
				File file = new File(getTitle().substring(titlePrefix.length()));
				fileChooser.setSelectedFile(file);
//				if (getPathName() != null) {
//					File file = new File(getPathName().substring(0, getPathName().length() - 4) + ".txt");
//					//if (file.exists()) {
//						fileChooser.setSelectedFile(file);
//					//} 
//				}				
				int retval = fileChooser.showSaveDialog(OutputPopout.this);
				if (retval == JFileChooser.CANCEL_OPTION) {
					done = true;
				}
				if (retval == JFileChooser.APPROVE_OPTION) {					
					String path = fileChooser.getSelectedFile().getPath();
					GraphicalInterface.curSettings.add("LastPopoutSave", path.substring(0, path.lastIndexOf("\\")));
					if (!path.endsWith(".txt")) {
						path = path + ".txt";
					}
					File f = new File(path);

					if (f.exists()) {
						int confirmDialog = JOptionPane.showConfirmDialog(fileChooser, "Replace existing file?");
						if (confirmDialog == JOptionPane.YES_OPTION) {
							done = true;
							writeFile(f);
							setTitle(GraphicalInterfaceConstants.TITLE + " - " + f.getName());
						} else if (confirmDialog == JOptionPane.NO_OPTION) {        		    	  
							done = false;
						} else {
							done = true;
						}       		    	  
					} else {
						done = true;
						writeFile(f);
						setTitle(GraphicalInterfaceConstants.TITLE + " - " + f.getName());
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
	public void load(String path, String title) {
		File file;
		FileReader in = null;

		try {
			file = new File(path); 
			setPathName(path);
			in = new FileReader(file); 
			char[] buffer = new char[4096]; // Read 4K characters at a time
			int len; 
			textArea.setText("");  	     
			while ((len = in.read(buffer)) != -1) { // Read a batch of chars
				String s = new String(buffer, 0, len); 
				textArea.append(s); 
			}
			textArea.setCaretPosition(0); 
			setTitle(title);
			//setTitle(GraphicalInterfaceConstants.TITLE + " - " + path);
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

	public String getPathName() {
		return pathName;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}

	public void clear() {
		textArea.setText(""); 
		setTitle(LocalConfig.getInstance().getModelName());
	}

	class ExitAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
		}
	}

	private static void setClipboardContents(String s) {
	      StringSelection selection = new StringSelection(s);
	      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
	            selection, selection);
	}
	
	class TextFileFilter extends javax.swing.filechooser.FileFilter {
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().toLowerCase().endsWith(".txt");
		}

		public String getDescription() {
			return ".txt files";
		}
	}
	
	public void setOutputText(String text) {
		textArea.setText(text);
	}
	
	public static void main(String[] args) {
		new OutputPopout();
		//loadOutputPane("C://CMakeCache.txt");
	}
}


