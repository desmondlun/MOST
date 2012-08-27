package edu.rutgers.MOST.presentation;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.data.DatabaseCreator;
import edu.rutgers.MOST.data.Excel97Reader;

public class ExcelSheetInterface  extends JDialog {

	public JComboBox<String> cbMetaboliteSheetName = new JComboBox();
	public JComboBox<String> cbReactionSheetName = new JComboBox();

	JButton okButton = new JButton("     OK     ");
	JButton cancelButton = new JButton("  Cancel  ");

	public ExcelSheetInterface(final Connection con) {

		setTitle("Excel Sheet Name Selector");

		cbMetaboliteSheetName.setEditable(true);	
		cbReactionSheetName.setEditable(true);

		cbMetaboliteSheetName.setPreferredSize(new Dimension(250, 30));
		cbMetaboliteSheetName.setMaximumSize(new Dimension(250, 30));

		cbReactionSheetName.setPreferredSize(new Dimension(250, 30));
		cbReactionSheetName.setMaximumSize(new Dimension(250, 30));

		JTextField fieldMetaboliteSheetName = (JTextField)cbMetaboliteSheetName.getEditor().getEditorComponent();
		fieldMetaboliteSheetName.addKeyListener(new ComboKeyHandler(cbMetaboliteSheetName));

		JTextField fieldReactionSheetName = (JTextField)cbReactionSheetName.getEditor().getEditorComponent();
		fieldReactionSheetName.addKeyListener(new ComboKeyHandler(cbReactionSheetName));

		//box layout
		Box vb = Box.createVerticalBox();

		Box hbLabels = Box.createHorizontalBox();
		Box hb1 = Box.createHorizontalBox();
		Box hb2 = Box.createHorizontalBox();

		Box hbSheetType = Box.createHorizontalBox();	    	    
		Box hbMetabolitesLabel = Box.createHorizontalBox();	    
		Box hbFromFile1 = Box.createHorizontalBox();
		Box hbReactionsLabel = Box.createHorizontalBox();	    
		Box hbFromFile2 = Box.createHorizontalBox();

		Box hbButton = Box.createHorizontalBox();

		JLabel sheetType = new JLabel();
		sheetType.setText(GraphicalInterfaceConstants.EXCEL_SHEET_LABEL);
		sheetType.setSize(new Dimension(300, 30));
		//top, left, bottom. right
		sheetType.setBorder(BorderFactory.createEmptyBorder(15,50,20,200));
		sheetType.setAlignmentX(LEFT_ALIGNMENT);

		hbSheetType.add(sheetType);	
		hbSheetType.setAlignmentX(LEFT_ALIGNMENT);

		hbLabels.add(hbSheetType);

		//left side of interface		
		JLabel metabSheetLabel = new JLabel();
		metabSheetLabel.setText(GraphicalInterfaceConstants.DEFAULT_METABOLITE_TABLE_TAB_NAME);
		metabSheetLabel.setSize(new Dimension(50, 20));
		metabSheetLabel.setBorder(BorderFactory.createEmptyBorder(0,20,10,80));
		metabSheetLabel.setAlignmentX(LEFT_ALIGNMENT);
		//metabSheetLabel.setAlignmentY(TOP_ALIGNMENT);	    	    

		JPanel panelMetabolitesLabel = new JPanel();
		panelMetabolitesLabel.setLayout(new BoxLayout(panelMetabolitesLabel, BoxLayout.X_AXIS));
		panelMetabolitesLabel.add(metabSheetLabel);
		panelMetabolitesLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbMetabolitesLabel.add(panelMetabolitesLabel);
		hbMetabolitesLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelFromFile1 = new JPanel();
		panelFromFile1.setLayout(new BoxLayout(panelFromFile1, BoxLayout.X_AXIS));
		panelFromFile1.add(cbMetaboliteSheetName);
		panelFromFile1.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbFromFile1.add(panelFromFile1);

		hb1.add(hbMetabolitesLabel);
		hb1.add(hbFromFile1);

		JLabel reacSheetLabel = new JLabel();
		reacSheetLabel.setText(GraphicalInterfaceConstants.DEFAULT_REACTION_TABLE_TAB_NAME);
		reacSheetLabel.setSize(new Dimension(50, 20));	    
		reacSheetLabel.setBorder(BorderFactory.createEmptyBorder(0,20,0,80));
		reacSheetLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelReactionsLabel = new JPanel();
		panelReactionsLabel.setLayout(new BoxLayout(panelReactionsLabel, BoxLayout.X_AXIS));
		panelReactionsLabel.add(reacSheetLabel);
		panelReactionsLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbReactionsLabel.add(panelReactionsLabel);
		hbReactionsLabel.setAlignmentX(LEFT_ALIGNMENT);
		JPanel panelFromFile2 = new JPanel();
		panelFromFile2.setLayout(new BoxLayout(panelFromFile2, BoxLayout.X_AXIS));
		panelFromFile2.add(cbReactionSheetName);
		panelFromFile2.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbFromFile2.add(panelFromFile2);

		hb2.add(hbReactionsLabel);
		hb2.add(hbFromFile2);

		okButton.setMnemonic(KeyEvent.VK_O);
		JLabel blank = new JLabel("    "); 
		cancelButton.setMnemonic(KeyEvent.VK_C);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(okButton);
		buttonPanel.add(blank);
		buttonPanel.add(cancelButton);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,20,20,20));

		hbButton.add(buttonPanel);

		vb.add(hbLabels);
		vb.add(hb1);
		vb.add(hb2);
		vb.add(hbButton);

		add(vb);

		ActionListener metaboliteActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {	  	  	      	      
				String metaboliteSelection = (String) cbMetaboliteSheetName.getSelectedItem();
				if (metaboliteSelection != null) {
					System.out.println(metaboliteSelection);
				}      	    	      

			}
		};
		cbMetaboliteSheetName.addActionListener(metaboliteActionListener);

		ActionListener reactionActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {	  	  	      	      
				String reactionSelection = (String) cbReactionSheetName.getSelectedItem();
				if (reactionSelection != null) {
					System.out.println(reactionSelection);
				}      	    	      

			}
		};
		cbReactionSheetName.addActionListener(reactionActionListener);

		ActionListener okButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				ArrayList<String> sheetNames = new ArrayList();
				sheetNames.add((String) cbMetaboliteSheetName.getSelectedItem());
				sheetNames.add((String) cbReactionSheetName.getSelectedItem());
				LocalConfig.getInstance().setSheetNamesList(sheetNames);
				Excel97Reader reader = new Excel97Reader();
				ArrayList<String> metaboliteColumnNames = reader.metaboliteColumnNamesFromFile(GraphicalInterface.getExcelPath(), sheetNames);
				ArrayList<String> reactionColumnNames = reader.reactionColumnNamesFromFile(GraphicalInterface.getExcelPath(), sheetNames);
				//reader.load(GraphicalInterface.getExcelPath(), LocalConfig.getInstance().getDatabaseName(), sheetNames);
				setVisible(false);

				final ArrayList<Image> icons = new ArrayList<Image>(); 
				icons.add(new ImageIcon("images/most16.jpg").getImage()); 
				icons.add(new ImageIcon("images/most32.jpg").getImage());

				ExcelColumnNameInterface columnNameInterface;
				try {
					columnNameInterface = new ExcelColumnNameInterface(con, metaboliteColumnNames, reactionColumnNames);
					columnNameInterface.setModal(true);
					columnNameInterface.setIconImages(icons);

					columnNameInterface.setSize(600, 700);
					columnNameInterface.setResizable(false);
					columnNameInterface.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
					columnNameInterface.setLocationRelativeTo(null);
					columnNameInterface.setVisible(true);
					columnNameInterface.setAlwaysOnTop(true);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


				dispose();
			}
		};

		okButton.addActionListener(okButtonActionListener);

		ActionListener cancelButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				setVisible(false);
				dispose();
			}
		};

		cancelButton.addActionListener(cancelButtonActionListener);

	} 	 

	public void populateBoxes(ArrayList<String> sheetNames) {
		for (int i = 0; i < sheetNames.size(); i++) {
			cbMetaboliteSheetName.addItem(sheetNames.get(i));
			cbReactionSheetName.addItem(sheetNames.get(i));
		}
	}

	public static void main(String[] args) throws Exception {
		Class.forName("org.sqlite.JDBC");       
		DatabaseCreator databaseCreator = new DatabaseCreator();
		Connection con = DriverManager.getConnection("jdbc:sqlite:" + "untitled" + ".db");

		//based on code from http://stackoverflow.com/questions/6403821/how-to-add-an-image-to-a-jframe-title-bar
		final ArrayList<Image> icons = new ArrayList<Image>(); 
		icons.add(new ImageIcon("images/most16.jpg").getImage()); 
		icons.add(new ImageIcon("images/most32.jpg").getImage());

		ExcelSheetInterface frame = new ExcelSheetInterface(con);

		frame.setIconImages(icons);
		frame.setSize(500, 220);
		frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}


