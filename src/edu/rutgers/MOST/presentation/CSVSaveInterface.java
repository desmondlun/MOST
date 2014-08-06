package edu.rutgers.MOST.presentation;

import java.awt.Color;
import java.awt.Dimension;
//import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
//import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
//import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import edu.rutgers.MOST.config.LocalConfig;

public class CSVSaveInterface  extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public JButton metabFileButton = new JButton(GraphicalInterfaceConstants.CSV_FILE_SAVE_METAB_BUTTON);
	public JButton reacFileButton = new JButton(GraphicalInterfaceConstants.CSV_FILE_SAVE_REAC_BUTTON);	
	public JButton okButton = new JButton("    OK    ");
	public JButton cancelButton = new JButton("  Cancel  ");
	public JButton clearMetabButton = new JButton("Clear");
	public JButton clearReacButton = new JButton("Clear");
	private final JTextField textMetabField = new JTextField();
	private final JTextField textReacField = new JTextField();

	public CSVSaveInterface() {

		setTitle(GraphicalInterfaceConstants.CSV_FILE_SAVE_INTERFACE_TITLE);		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		getRootPane().setDefaultButton(okButton);
		
		//box layout
		Box vb = Box.createVerticalBox();

		Box hbLabel = Box.createHorizontalBox();
		Box hbMetab = Box.createHorizontalBox();
		Box hbReac = Box.createHorizontalBox();
		Box hbButton = Box.createHorizontalBox();

		metabFileButton.setMnemonic(KeyEvent.VK_M);
		reacFileButton.setMnemonic(KeyEvent.VK_R);
		clearMetabButton.setMnemonic(KeyEvent.VK_E);
		clearReacButton.setMnemonic(KeyEvent.VK_A);
		
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
				if (textMetabField.getText() != null && textMetabField.getText().length() > 0) {
					okButton.setEnabled(true);
					LocalConfig.getInstance().hasMetabolitesFile = true;
				} else {
					LocalConfig.getInstance().hasMetabolitesFile = false;
				}
			}
		});
		
		textReacField.getDocument().addDocumentListener(new DocumentListener() {
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
				if (textReacField.getText() != null && textReacField.getText().length() > 0) {
					okButton.setEnabled(true);
				} 
			}
		});
		
		JPanel textMetabPanel = new JPanel();
		textMetabPanel.setLayout(new BoxLayout(textMetabPanel, BoxLayout.X_AXIS));
		textMetabPanel.add(textMetabField);
		textMetabPanel.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));

		JLabel blank2 = new JLabel("      ");
		JLabel blank3 = new JLabel("      ");
		
		hbMetab.add(blank2);
		hbMetab.add(metabFileButton);
		hbMetab.add(textMetabPanel);
		hbMetab.add(clearMetabButton);
		hbMetab.add(blank3);
		
		JPanel textReacPanel = new JPanel();
		textReacPanel.setLayout(new BoxLayout(textReacPanel, BoxLayout.X_AXIS));
		textReacPanel.add(textReacField);
		textReacPanel.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));

		JLabel blank4 = new JLabel("      ");
		JLabel blank5 = new JLabel("      ");
		
		hbReac.add(blank4);
		hbReac.add(reacFileButton);
		hbReac.add(textReacPanel);
		hbReac.add(clearReacButton);
		hbReac.add(blank5);
		
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
				fileChooser.setDialogTitle("Save CSV Metabolite File");
				fileChooser.setFileFilter(new CSVFileFilter1());
				fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				String lastCSV_path = GraphicalInterface.curSettings.get("LastCSV");
				Utilities u = new Utilities();
				// if path is null or does not exist, default used, else last path used		
				fileChooser.setCurrentDirectory(new File(u.lastPath(lastCSV_path, fileChooser)));	
				fileChooser.setApproveButtonText("Save");
				File f = new File(metabolitesPath());
				fileChooser.setSelectedFile(f);
				
				//... Open a file dialog.
				int retval = fileChooser.showOpenDialog(output);
				if (retval == JFileChooser.APPROVE_OPTION) {
					//... The user selected a file, get it, use it.          	
					File file = fileChooser.getSelectedFile();
					String rawPathName = file.getAbsolutePath();
					if (!rawPathName.endsWith(".csv")) {
						rawPathName = rawPathName + ".csv";
					}
					textMetabField.setText(rawPathName);
				}				
			}
		};
		
		ActionListener reacFileButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				JTextArea output = null;
				JFileChooser fileChooser = new JFileChooser(); 
				fileChooser.setDialogTitle("Save CSV Reaction File");
				fileChooser.setFileFilter(new CSVFileFilter1());
				fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);				
				String lastCSV_path = GraphicalInterface.curSettings.get("LastCSV");
				Utilities u = new Utilities();
				// if path is null or does not exist, default used, else last path used		
				fileChooser.setCurrentDirectory(new File(u.lastPath(lastCSV_path, fileChooser)));	
				fileChooser.setApproveButtonText("Save");
				File f = new File(reactionsPath());
				fileChooser.setSelectedFile(f);
				
				//... Open a file dialog.
				int retval = fileChooser.showOpenDialog(output);
				if (retval == JFileChooser.APPROVE_OPTION) {
					//... The user selected a file, get it, use it.          	
					File file = fileChooser.getSelectedFile();
					String rawPathName = file.getAbsolutePath();
					if (!rawPathName.endsWith(".csv")) {
						rawPathName = rawPathName + ".csv";
					}
					textReacField.setText(rawPathName);
				}				
			}
		};
		
		ActionListener cancelButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				//setVisible(false);
				//dispose();
			}
		}; 
		
		ActionListener clearMetabButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				textMetabField.setText("");	
				if (textReacField.getText().trim().length() == 0) {
					okButton.setEnabled(false);
				}
			}
		}; 
		
		ActionListener clearReacButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				textReacField.setText("");
				if (textMetabField.getText().trim().length() == 0) {
					okButton.setEnabled(false);
				}
			}
		};
		
		metabFileButton.addActionListener(metabFileButtonActionListener);
		reacFileButton.addActionListener(reacFileButtonActionListener);
		cancelButton.addActionListener(cancelButtonActionListener);
		clearMetabButton.addActionListener(clearMetabButtonActionListener);
		clearReacButton.addActionListener(clearReacButtonActionListener);
		
	} 
	
	public void updateMetabolitesPath(String path) {
		textMetabField.setText(path);
	}
	
	public void updateReactionsPath(String path) {
		textReacField.setText(path);
	}
	
	public String metabolitesPath() {
		return textMetabField.getText();
	}
	
	public String reactionsPath() {
		return textReacField.getText();
	}
	
	public static void main(String[] args) throws Exception {

		//Main will not work unless GraphicalInterface.curSetting lines commented out
		//Uncomment imports to run main
		
		//based on code from http://stackoverflow.com/questions/6403821/how-to-add-an-image-to-a-jframe-title-bar
//		final ArrayList<Image> icons = new ArrayList<Image>(); 
//		icons.add(new ImageIcon("images/most16.jpg").getImage()); 
//		icons.add(new ImageIcon("images/most32.jpg").getImage());
//
//		CSVSaveInterface frame = new CSVSaveInterface();
//
//		frame.setIconImages(icons);
//		frame.setSize(600, 200);
//		frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
//		frame.setLocationRelativeTo(null);
//		frame.setVisible(true);
	}
}

class CSVFileFilter1 extends javax.swing.filechooser.FileFilter {
    public boolean accept(File f) {
        return f.isDirectory() || f.getName().toLowerCase().endsWith(".csv");
    }
    
    public String getDescription() {
        return ".csv files";
    }
}

