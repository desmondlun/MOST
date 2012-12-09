package edu.rutgers.MOST.presentation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.presentation.GraphicalInterface.Task;

public class CSVLoadInterface  extends JDialog {

	public JTextField columnNameField = new JTextField();
	public static JButton metabFileButton = new JButton(GraphicalInterfaceConstants.CSV_FILE_LOAD_METAB_BUTTON);
	public static JButton reacFileButton = new JButton(GraphicalInterfaceConstants.CSV_FILE_LOAD_REAC_BUTTON);	
	public static JButton okButton = new JButton("    OK    ");
	public static JButton cancelButton = new JButton("  Cancel  ");
	public static final JTextField textMetabField = new JTextField();
	public static final JTextField textReacField = new JTextField();

	public CSVLoadInterface() {

		setTitle(GraphicalInterfaceConstants.CSV_FILE_LOAD_INTERFACE_TITLE);		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		textMetabField.setText("");
		textReacField.setText("");
		
		//box layout
		Box vb = Box.createVerticalBox();

		Box hbLabel = Box.createHorizontalBox();
		Box hbMetab = Box.createHorizontalBox();
		Box hbReac = Box.createHorizontalBox();
		Box hbButton = Box.createHorizontalBox();

		JLabel topLabel = new JLabel();
		topLabel.setText("File Name");
		topLabel.setSize(new Dimension(150, 10));
		//top, left, bottom. right
		topLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		topLabel.setAlignmentX(CENTER_ALIGNMENT);

		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
		labelPanel.add(topLabel);
		labelPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

		hbLabel.add(labelPanel);
		
		topLabel.setMinimumSize(new Dimension(200, 15));
		textMetabField.setEditable(false);
		textReacField.setEditable(false);
		textMetabField.setBackground(Color.white);
		textReacField.setBackground(Color.white);

		okButton.setMnemonic(KeyEvent.VK_O);
		okButton.setEnabled(false);
		JLabel blank = new JLabel("    "); 
		cancelButton.setMnemonic(KeyEvent.VK_C);

		textMetabField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				enableOKButton();
			}
			public void removeUpdate(DocumentEvent e) {
				enableOKButton();
			}
			public void insertUpdate(DocumentEvent e) {
				enableOKButton();
			}

			public void enableOKButton() {
				if (textMetabField.getText() != null) {
					okButton.setEnabled(true);
				}
			}
		});
		

		JPanel textMetabPanel = new JPanel();
		textMetabPanel.setLayout(new BoxLayout(textMetabPanel, BoxLayout.X_AXIS));
		textMetabPanel.add(textMetabField);
		textMetabPanel.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));

		JLabel blank2 = new JLabel("      ");
		
		hbMetab.add(blank2);
		hbMetab.add(metabFileButton);
		hbMetab.add(textMetabPanel);
		
		JPanel textReacPanel = new JPanel();
		textReacPanel.setLayout(new BoxLayout(textReacPanel, BoxLayout.X_AXIS));
		textReacPanel.add(textReacField);
		textReacPanel.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));

		JLabel blank3 = new JLabel("      ");
		
		hbReac.add(blank3);
		hbReac.add(reacFileButton);
		hbReac.add(textReacPanel);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(okButton);
		buttonPanel.add(blank);
		buttonPanel.add(cancelButton);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(5,20,15,20));

		hbButton.add(buttonPanel);

		vb.add(hbLabel);
		vb.add(hbMetab);
		vb.add(hbReac);
		vb.add(hbButton);
		add(vb);	
		
		ActionListener metabFileButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				JTextArea output = null;
				JFileChooser fileChooser = new JFileChooser(); 
				//... Open a file dialog.
				int retval = fileChooser.showOpenDialog(output);
				if (retval == JFileChooser.APPROVE_OPTION) {
					//... The user selected a file, get it, use it.          	
					File file = fileChooser.getSelectedFile();
					String rawFilename = fileChooser.getSelectedFile().getName();
					String filename = rawFilename.substring(0, rawFilename.length() - 4);      	
					String path = fileChooser.getSelectedFile().getPath();
					if (!path.endsWith(".csv")) {
						JOptionPane.showMessageDialog(null,                
								"Not a Valid CSV File.",                
								"Invalid CSV File",                                
								JOptionPane.ERROR_MESSAGE);
					} else {
						textMetabField.setText(path);
						LocalConfig.getInstance().setMetabolitesCSVFile(file);   	
						LocalConfig.getInstance().setDatabaseName(filename);
						LocalConfig.getInstance().setLoadedDatabase(filename);
					}
				}				
			}
		};
		
		ActionListener reacFileButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				JTextArea output = null;
				JFileChooser fileChooser = new JFileChooser(); 
				//... Open a file dialog.
				int retval = fileChooser.showOpenDialog(output);
				if (retval == JFileChooser.APPROVE_OPTION) {
					//... The user selected a file, get it, use it.          	
					File file = fileChooser.getSelectedFile();
					String path = fileChooser.getSelectedFile().getPath();
					if (!path.endsWith(".csv")) {
						JOptionPane.showMessageDialog(null,                
								"Not a Valid CSV File.",                
								"Invalid CSV File",                                
								JOptionPane.ERROR_MESSAGE);
					} else {
						textReacField.setText(path);
						LocalConfig.getInstance().setReactionsCSVFile(file);
					}

				}				
			}
		};
		
		ActionListener cancelButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				setVisible(false);
				dispose();
			}
		}; 
		
		metabFileButton.addActionListener(metabFileButtonActionListener);
		reacFileButton.addActionListener(reacFileButtonActionListener);
		cancelButton.addActionListener(cancelButtonActionListener);
	} 	 
	
	public static void main(String[] args) throws Exception {

		//based on code from http://stackoverflow.com/questions/6403821/how-to-add-an-image-to-a-jframe-title-bar
		final ArrayList<Image> icons = new ArrayList<Image>(); 
		icons.add(new ImageIcon("images/most16.jpg").getImage()); 
		icons.add(new ImageIcon("images/most32.jpg").getImage());

		CSVLoadInterface frame = new CSVLoadInterface();

		frame.setIconImages(icons);
		frame.setSize(600, 200);
		frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}








