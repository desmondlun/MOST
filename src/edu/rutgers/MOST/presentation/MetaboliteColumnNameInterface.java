package edu.rutgers.MOST.presentation;

import javax.swing.*;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import javax.swing.JFrame;

import edu.rutgers.MOST.config.ConfigConstants;
import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.data.DatabaseCreator;
import edu.rutgers.MOST.data.MetabolitesMetaColumnManager;
import edu.rutgers.MOST.data.SBMLProduct;
import edu.rutgers.MOST.data.SBMLReactant;
import edu.rutgers.MOST.data.TextMetabolitesModelReader;

public class MetaboliteColumnNameInterface extends JDialog {
	
	public JButton okButton = new JButton("     OK     ");
    public JButton cancelButton = new JButton("  Cancel  ");  
    
    final JComboBox<String> cbColumnDisplayName1 = new JComboBox();
    final JComboBox<String> cbColumnNameFromFile1 = new JComboBox();
    final JComboBox<String> cbColumnDisplayName2 = new JComboBox();
    final JComboBox<String> cbColumnNameFromFile2 = new JComboBox();
    final JComboBox<String> cbColumnDisplayName3 = new JComboBox();
    final JComboBox<String> cbColumnNameFromFile3 = new JComboBox();
    final JComboBox<String> cbColumnDisplayName4 = new JComboBox();
    final JComboBox<String> cbColumnNameFromFile4 = new JComboBox();
    final JComboBox<String> cbColumnDisplayName5 = new JComboBox();
    final JComboBox<String> cbColumnNameFromFile5 = new JComboBox();
    
    public static ArrayList<String> columnNamesFromFile;
    
    public static ArrayList<String> getColumnNamesFromFile() {
		return columnNamesFromFile;
	}
	
	public void setColumnNamesFromFile(ArrayList<String> columnNamesFromFile) {
		this.columnNamesFromFile = columnNamesFromFile;
	}
	
	public MetaboliteColumnNameInterface(final Connection con, ArrayList<String> columnNamesFromFile)
	    throws SQLException {

		setColumnNamesFromFile(columnNamesFromFile);
		
		setTitle("Column Selector");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	    	    
		cbColumnDisplayName1.setEditable(true);		
		cbColumnNameFromFile1.setEditable(true);
		cbColumnDisplayName2.setEditable(true);		
		cbColumnNameFromFile2.setEditable(true);
		cbColumnDisplayName3.setEditable(true);		
		cbColumnNameFromFile3.setEditable(true);
		cbColumnDisplayName4.setEditable(true);		
		cbColumnNameFromFile4.setEditable(true);
		cbColumnDisplayName5.setEditable(true);		
		cbColumnNameFromFile5.setEditable(true);
	    
		//populate comboboxes		
	    for (int g = 0; g < GraphicalInterfaceConstants.METABOLITE_ID_COLUMN_NAMES.length; g++) {
	    	cbColumnDisplayName1.addItem(GraphicalInterfaceConstants.METABOLITE_ID_COLUMN_NAMES[g].toString());
	    }
	    	
	    for (int g = 0; g < GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN_NAMES.length; g++) {
	    	cbColumnDisplayName2.addItem(GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN_NAMES[g].toString());
	    }
	    
	    for (int g = 0; g < GraphicalInterfaceConstants.METABOLITE_CHARGE_COLUMN_NAMES.length; g++) {
	    	cbColumnDisplayName3.addItem(GraphicalInterfaceConstants.METABOLITE_CHARGE_COLUMN_NAMES[g].toString());
	    }
	    
	    for (int g = 0; g < GraphicalInterfaceConstants.METABOLITE_COMPARTMENT_COLUMN_NAMES.length; g++) {
	    	cbColumnDisplayName4.addItem(GraphicalInterfaceConstants.METABOLITE_COMPARTMENT_COLUMN_NAMES[g].toString());
	    }
	    
	    populateNamesFromFileBoxes(columnNamesFromFile);
	    
	    //set size
	    cbColumnDisplayName1.setPreferredSize(new Dimension(150, 10));
	    cbColumnDisplayName1.setSelectedIndex(0);
	    
	    cbColumnDisplayName2.setPreferredSize(new Dimension(150, 10));
	    cbColumnDisplayName2.setSelectedIndex(0);
	    
	    cbColumnDisplayName3.setPreferredSize(new Dimension(150, 10));
	    cbColumnDisplayName3.setSelectedIndex(0);
	    
	    cbColumnDisplayName4.setPreferredSize(new Dimension(150, 10));
	    cbColumnDisplayName4.setSelectedIndex(0);
	    
	    cbColumnDisplayName5.setPreferredSize(new Dimension(150, 10));
	    //cbColumnDisplayName5.setSelectedIndex(0);
	    
	    cbColumnNameFromFile1.setPreferredSize(new Dimension(150, 10));
	    cbColumnNameFromFile2.setPreferredSize(new Dimension(150, 10));
	    cbColumnNameFromFile3.setPreferredSize(new Dimension(150, 10));
	    cbColumnNameFromFile4.setPreferredSize(new Dimension(150, 10));
	    cbColumnNameFromFile5.setPreferredSize(new Dimension(150, 10));
	    
	    //add combokey handler so typing loads suggestions
	    JTextField fieldDisplayName1 = (JTextField)cbColumnDisplayName1.getEditor().getEditorComponent();
	    fieldDisplayName1.addKeyListener(new ComboKeyHandler(cbColumnDisplayName1));
	    
	    JTextField fieldDisplayName2 = (JTextField)cbColumnDisplayName2.getEditor().getEditorComponent();
	    fieldDisplayName2.addKeyListener(new ComboKeyHandler(cbColumnDisplayName2));
	    
	    JTextField fieldDisplayName3 = (JTextField)cbColumnDisplayName3.getEditor().getEditorComponent();
	    fieldDisplayName3.addKeyListener(new ComboKeyHandler(cbColumnDisplayName3));
	    
	    JTextField fieldDisplayName4 = (JTextField)cbColumnDisplayName4.getEditor().getEditorComponent();
	    fieldDisplayName4.addKeyListener(new ComboKeyHandler(cbColumnDisplayName4));
	    
	    JTextField fieldDisplayName5 = (JTextField)cbColumnDisplayName5.getEditor().getEditorComponent();
	    fieldDisplayName4.addKeyListener(new ComboKeyHandler(cbColumnDisplayName5));
	    
	    JTextField fieldFromFile1 = (JTextField)cbColumnNameFromFile1.getEditor().getEditorComponent();
	    fieldFromFile1.addKeyListener(new ComboKeyHandler(cbColumnNameFromFile1));
	    
	    JTextField fieldFromFile2 = (JTextField)cbColumnNameFromFile2.getEditor().getEditorComponent();
	    fieldFromFile2.addKeyListener(new ComboKeyHandler(cbColumnNameFromFile2));
	    
	    JTextField fieldFromFile3 = (JTextField)cbColumnNameFromFile3.getEditor().getEditorComponent();
	    fieldFromFile3.addKeyListener(new ComboKeyHandler(cbColumnNameFromFile3));
	    
	    JTextField fieldFromFile4 = (JTextField)cbColumnNameFromFile4.getEditor().getEditorComponent();
	    fieldFromFile4.addKeyListener(new ComboKeyHandler(cbColumnNameFromFile4));
	    
	    JTextField fieldFromFile5 = (JTextField)cbColumnNameFromFile5.getEditor().getEditorComponent();
	    fieldFromFile5.addKeyListener(new ComboKeyHandler(cbColumnNameFromFile5));

	    //box layout
	    Box vb = Box.createVerticalBox();
	    
	    Box hbLabels = Box.createHorizontalBox();
	    Box hb1 = Box.createHorizontalBox();
	    Box hb2 = Box.createHorizontalBox();
	    Box hb3 = Box.createHorizontalBox();
	    Box hb4 = Box.createHorizontalBox();
	    Box hb5 = Box.createHorizontalBox();
	    
	    Box hbDisplayLabel = Box.createHorizontalBox();	    
	    Box hbFromFileLabel = Box.createHorizontalBox();	    
	    Box hbDisplay1 = Box.createHorizontalBox();	    
	    Box hbFromFile1 = Box.createHorizontalBox();
	    Box hbDisplay2 = Box.createHorizontalBox();	    
	    Box hbFromFile2 = Box.createHorizontalBox();
	    Box hbDisplay3 = Box.createHorizontalBox();	    
	    Box hbFromFile3 = Box.createHorizontalBox();
	    Box hbDisplay4 = Box.createHorizontalBox();	    
	    Box hbFromFile4 = Box.createHorizontalBox();
	    Box hbDisplay5 = Box.createHorizontalBox();	    
	    Box hbFromFile5 = Box.createHorizontalBox();
	    	    
	    Box hbButton = Box.createHorizontalBox();
	    
	    //alignment not working
	    JLabel displayLabel = new JLabel();
	    displayLabel.setText(GraphicalInterfaceConstants.METABOLITES_DISPLAY_LABEL);
	    displayLabel.setSize(new Dimension(150, 20));
	    displayLabel.setBorder(BorderFactory.createEmptyBorder(10,20,0,100));
	    displayLabel.setAlignmentX(LEFT_ALIGNMENT);
	    
	    hbDisplayLabel.add(displayLabel);	
	    hbDisplayLabel.setAlignmentX(LEFT_ALIGNMENT);
	    
	    JLabel fromFileLabel = new JLabel();
	    fromFileLabel.setText(GraphicalInterfaceConstants.METABOLITES_FROM_FILE_LABEL);
	    fromFileLabel.setSize(new Dimension(150, 20));
	    fromFileLabel.setBorder(BorderFactory.createEmptyBorder(10,50,0,20));
	    fromFileLabel.setAlignmentX(RIGHT_ALIGNMENT);
	    
	    hbFromFileLabel.add(fromFileLabel);
	    hbFromFileLabel.setAlignmentX(RIGHT_ALIGNMENT);
	    
	    hbLabels.add(hbDisplayLabel);
	    hbLabels.add(hbFromFileLabel);
	    
	    JPanel panelDisplay1 = new JPanel();
	    panelDisplay1.setLayout(new BoxLayout(panelDisplay1, BoxLayout.X_AXIS));
	    panelDisplay1.add(cbColumnDisplayName1);
	    panelDisplay1.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbDisplay1.add(panelDisplay1);
	    
	    JPanel panelFromFile1 = new JPanel();
	    panelFromFile1.setLayout(new BoxLayout(panelFromFile1, BoxLayout.X_AXIS));
	    panelFromFile1.add(cbColumnNameFromFile1);
	    panelFromFile1.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbFromFile1.add(panelFromFile1);
	    
	    hb1.add(hbDisplay1);
	    hb1.add(hbFromFile1);
	    
	    JPanel panelDisplay2 = new JPanel();
	    panelDisplay2.setLayout(new BoxLayout(panelDisplay2, BoxLayout.X_AXIS));
	    panelDisplay2.add(cbColumnDisplayName2);
	    panelDisplay2.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbDisplay2.add(panelDisplay2);
	    
	    JPanel panelFromFile2 = new JPanel();
	    panelFromFile2.setLayout(new BoxLayout(panelFromFile2, BoxLayout.X_AXIS));
	    panelFromFile2.add(cbColumnNameFromFile2);
	    panelFromFile2.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbFromFile2.add(panelFromFile2);
	    
	    hb2.add(hbDisplay2);
	    hb2.add(hbFromFile2);
	    
	    JPanel panelDisplay3 = new JPanel();
	    panelDisplay3.setLayout(new BoxLayout(panelDisplay3, BoxLayout.X_AXIS));
	    panelDisplay3.add(cbColumnDisplayName3);
	    panelDisplay3.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbDisplay3.add(panelDisplay3);
	    
	    JPanel panelFromFile3 = new JPanel();
	    panelFromFile3.setLayout(new BoxLayout(panelFromFile3, BoxLayout.X_AXIS));
	    panelFromFile3.add(cbColumnNameFromFile3);
	    panelFromFile3.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbFromFile3.add(panelFromFile3);
	    
	    hb3.add(hbDisplay3);
	    hb3.add(hbFromFile3);
	    
	    JPanel panelDisplay4 = new JPanel();
	    panelDisplay4.setLayout(new BoxLayout(panelDisplay4, BoxLayout.X_AXIS));
	    panelDisplay4.add(cbColumnDisplayName4);
	    panelDisplay4.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbDisplay4.add(panelDisplay4);
	    
	    JPanel panelFromFile4 = new JPanel();
	    panelFromFile4.setLayout(new BoxLayout(panelFromFile4, BoxLayout.X_AXIS));
	    panelFromFile4.add(cbColumnNameFromFile4);
	    panelFromFile4.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbFromFile4.add(panelFromFile4);
	    
	    hb4.add(hbDisplay4);
	    hb4.add(hbFromFile4);
	    
	    JPanel panelDisplay5 = new JPanel();
	    panelDisplay5.setLayout(new BoxLayout(panelDisplay5, BoxLayout.X_AXIS));
	    panelDisplay5.add(cbColumnDisplayName5);
	    panelDisplay5.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbDisplay5.add(panelDisplay5);
	    
	    JPanel panelFromFile5 = new JPanel();
	    panelFromFile5.setLayout(new BoxLayout(panelFromFile5, BoxLayout.X_AXIS));
	    panelFromFile5.add(cbColumnNameFromFile5);
	    panelFromFile5.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbFromFile5.add(panelFromFile5);
	    
	    hb5.add(hbDisplay5);
	    hb5.add(hbFromFile5);
	    	    
	    okButton.setMnemonic(KeyEvent.VK_O);
	    JLabel blank = new JLabel("    "); 
	    cancelButton.setMnemonic(KeyEvent.VK_C);

	    JPanel buttonPanel = new JPanel();
	    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
	    buttonPanel.add(okButton);
	    buttonPanel.add(blank);
	    buttonPanel.add(cancelButton);
	    buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
	    
	    hbButton.add(buttonPanel);
	    
	    vb.add(hbLabels);
	    vb.add(hb1);
	    vb.add(hb2);
	    vb.add(hb3);
	    vb.add(hb4);
	    vb.add(hb5);
	    vb.add(hbButton);
	    
	    add(vb);
	    
	    ActionListener actionListener = new ActionListener() {
		      public void actionPerformed(ActionEvent actionEvent) { 
		    	String displayNameSelection1 = (String) cbColumnDisplayName1.getSelectedItem(); 		    	
		    	//String nameFromFileSelection1 = (String) cbColumnNameFromFile1.getSelectedItem(); 
		    	String displayNameSelection2 = (String) cbColumnDisplayName2.getSelectedItem(); 		    	
		    	//String nameFromFileSelection2 = (String) cbColumnNameFromFile2.getSelectedItem();
		    	String displayNameSelection3 = (String) cbColumnDisplayName3.getSelectedItem(); 		    	
		    	// nameFromFileSelection3 = (String) cbColumnNameFromFile3.getSelectedItem();
		    	String displayNameSelection4 = (String) cbColumnDisplayName4.getSelectedItem(); 		    	
		    	//String nameFromFileSelection4 = (String) cbColumnNameFromFile4.getSelectedItem(); 	
		    	String displayNameSelection5 = (String) cbColumnDisplayName5.getSelectedItem(); 		    	
		    	//String nameFromFileSelection5 = (String) cbColumnNameFromFile5.getSelectedItem(); 	
		    	LocalConfig.getInstance().setMetaboliteAbbreviationColumnName(displayNameSelection1);
		    	LocalConfig.getInstance().setMetaboliteNameColumnName(displayNameSelection2);
		    	LocalConfig.getInstance().setChargeColumnName(displayNameSelection3);
		    	LocalConfig.getInstance().setCompartmentColumnName(displayNameSelection4);
		    	//LocalConfig.getInstance().setBoundaryColumnName(displayNameSelection4);
		    	if (cbColumnNameFromFile1.getSelectedItem() != null) {
		    		LocalConfig.getInstance().setMetaboliteAbbreviationColumnIndex(cbColumnDisplayName1.getSelectedIndex());
		    	} else {
		    		LocalConfig.getInstance().setMetaboliteAbbreviationColumnIndex(-1);
		    	}
		    	if (cbColumnNameFromFile2.getSelectedItem() != null) {
		    		LocalConfig.getInstance().setMetaboliteNameColumnIndex(cbColumnDisplayName2.getSelectedIndex());
		    	} else {
		    		LocalConfig.getInstance().setMetaboliteNameColumnIndex(-1);
		    	}
		    	if (cbColumnNameFromFile3.getSelectedItem() != null) {
		    		LocalConfig.getInstance().setChargeColumnIndex(cbColumnDisplayName3.getSelectedIndex());
		    	} else {
		    		LocalConfig.getInstance().setChargeColumnIndex(-1);
		    	}
		    	if (cbColumnNameFromFile4.getSelectedItem() != null) {
		    		LocalConfig.getInstance().setCompartmentColumnIndex(cbColumnDisplayName4.getSelectedIndex());
		    	} else {
		    		LocalConfig.getInstance().setCompartmentColumnIndex(-1);
		    	}
		    	if (cbColumnNameFromFile5.getSelectedItem() != null) {
		    		LocalConfig.getInstance().setBoundaryColumnIndex(cbColumnDisplayName5.getSelectedIndex());
		    	} else {
		    		LocalConfig.getInstance().setBoundaryColumnIndex(-1);
		    	}
		      }
		};
		   
		cbColumnDisplayName1.addActionListener(actionListener);
		cbColumnNameFromFile1.addActionListener(actionListener);
		cbColumnDisplayName2.addActionListener(actionListener);
		cbColumnNameFromFile2.addActionListener(actionListener);
		cbColumnDisplayName3.addActionListener(actionListener);
		cbColumnNameFromFile3.addActionListener(actionListener);
		cbColumnDisplayName4.addActionListener(actionListener);
		cbColumnNameFromFile4.addActionListener(actionListener);
		cbColumnDisplayName5.addActionListener(actionListener);
		cbColumnNameFromFile5.addActionListener(actionListener);
		
		ActionListener okButtonActionListener = new ActionListener() {
	        public void actionPerformed(ActionEvent ae) {
	        	LocalConfig.getInstance().setMetaboliteAbbreviationColumnName((String) cbColumnDisplayName1.getSelectedItem());
	        	LocalConfig.getInstance().setMetaboliteNameColumnName((String) cbColumnDisplayName2.getSelectedItem());
	        	LocalConfig.getInstance().setChargeColumnName((String) cbColumnDisplayName3.getSelectedItem());
	        	LocalConfig.getInstance().setCompartmentColumnName((String) cbColumnDisplayName4.getSelectedItem());
	        	//LocalConfig.getInstance().setBoundaryColumnName((String) cbColumnDisplayName5.getSelectedItem());
	        	
	        	//add metacolumn names to db
	        	MetabolitesMetaColumnManager metabolitesMetaColumnManager = new MetabolitesMetaColumnManager();
	        	ArrayList<String> metaColumnNames = new ArrayList();
	        	ArrayList<Integer> usedIndices = new ArrayList();
	        	ArrayList<Integer> metaColumnIndexList = new ArrayList();
	        	
	    		if (getColumnNamesFromFile().contains(cbColumnNameFromFile1.getSelectedItem())) {
	    			LocalConfig.getInstance().setMetaboliteAbbreviationColumnIndex(getColumnNamesFromFile().indexOf(cbColumnNameFromFile1.getSelectedItem()));
	    			usedIndices.add(getColumnNamesFromFile().indexOf(cbColumnNameFromFile1.getSelectedItem()));
	        	}
	    		if (getColumnNamesFromFile().contains(cbColumnNameFromFile2.getSelectedItem())) {
	    			LocalConfig.getInstance().setMetaboliteNameColumnIndex(getColumnNamesFromFile().indexOf(cbColumnNameFromFile2.getSelectedItem()));
	    			usedIndices.add(getColumnNamesFromFile().indexOf(cbColumnNameFromFile2.getSelectedItem()));
	        	}
	    		if (getColumnNamesFromFile().contains(cbColumnNameFromFile3.getSelectedItem())) {
	    			LocalConfig.getInstance().setChargeColumnIndex(getColumnNamesFromFile().indexOf(cbColumnNameFromFile3.getSelectedItem()));
	    			usedIndices.add(getColumnNamesFromFile().indexOf(cbColumnNameFromFile3.getSelectedItem()));
	        	}
	    		if (getColumnNamesFromFile().contains(cbColumnNameFromFile4.getSelectedItem())) {
	    			LocalConfig.getInstance().setCompartmentColumnIndex(getColumnNamesFromFile().indexOf(cbColumnNameFromFile4.getSelectedItem()));
	    			usedIndices.add(getColumnNamesFromFile().indexOf(cbColumnNameFromFile4.getSelectedItem()));
	        	}
	    		if (getColumnNamesFromFile().contains(cbColumnNameFromFile5.getSelectedItem())) {
	    			LocalConfig.getInstance().setBoundaryColumnIndex(getColumnNamesFromFile().indexOf(cbColumnNameFromFile5.getSelectedItem()));
	    			usedIndices.add(getColumnNamesFromFile().indexOf(cbColumnNameFromFile5.getSelectedItem()));
	        	}
	        	for (int i = 0; i < getColumnNamesFromFile().size(); i++) {
	        		if (!usedIndices.contains(i)) {
	        			metaColumnNames.add(getColumnNamesFromFile().get(i));
	        			metaColumnIndexList.add(getColumnNamesFromFile().indexOf(getColumnNamesFromFile().get(i)));
	        		} 
	        	}
	        	DatabaseCreator creator = new DatabaseCreator();
	        	creator.createDatabase(LocalConfig.getInstance().getDatabaseName());
	        	metabolitesMetaColumnManager.addColumnNames(LocalConfig.getInstance().getDatabaseName(), metaColumnNames);
	        	LocalConfig.getInstance().setMetabolitesMetaColumnIndexList(metaColumnIndexList);
	        	
	        	setVisible(false);
				dispose();
	        	
				final ArrayList<Image> icons = new ArrayList<Image>(); 
			    icons.add(new ImageIcon("etc/most16.jpg").getImage()); 
			    icons.add(new ImageIcon("etc/most32.jpg").getImage());
				
				ProgressBar progress = new ProgressBar();
		  	    progress.setIconImages(icons);
				progress.setSize(200, 75);
			    progress.setTitle("Loading...");
				progress.setVisible(true);
				
	        	TextMetabolitesModelReader reader = new TextMetabolitesModelReader();
	        	reader.load(GraphicalInterface.getMetabolitesCSVFile(), LocalConfig.getInstance().getDatabaseName());
	        	
	        	progress.setVisible(false);
	        	progress.dispose();
	        }
		};
		
		okButton.addActionListener(okButtonActionListener); 
		
		ActionListener cancelButtonActionListener = new ActionListener() {
	        public void actionPerformed(ActionEvent prodActionEvent) {
				setVisible(false);
				dispose();
				//this is a hack, same as clear method in gui
				try {
		    		  Class.forName("org.sqlite.JDBC");       
		    		  DatabaseCreator databaseCreator = new DatabaseCreator();
		    		  LocalConfig.getInstance().setDatabaseName(ConfigConstants.DEFAULT_DATABASE_NAME);
		    		  Connection con = DriverManager.getConnection("jdbc:sqlite:" + ConfigConstants.DEFAULT_DATABASE_NAME + ".db");
		    		  databaseCreator.createDatabase(LocalConfig.getInstance().getDatabaseName());
		    		  databaseCreator.addRows(LocalConfig.getInstance().getDatabaseName(), GraphicalInterfaceConstants.BLANK_DB_NUMBER_OF_ROWS, GraphicalInterfaceConstants.BLANK_DB_NUMBER_OF_ROWS);
				  } catch (ClassNotFoundException e) {
					  // TODO Auto-generated catch block
					  e.printStackTrace();
				  } catch (SQLException e) {
				      // TODO Auto-generated catch block
				      e.printStackTrace();
			      }
			}
		};
					
		cancelButton.addActionListener(cancelButtonActionListener);    		    
	}
	
	public void populateNamesFromFileBoxes(ArrayList<String> columnNamesFromFile) {
		
		LocalConfig.getInstance().setCompartmentColumnIndex(-1);
		LocalConfig.getInstance().setChargeColumnIndex(-1);
		LocalConfig.getInstance().setBoundaryColumnIndex(-1);
		//add all column names to from file comboboxes
		for (int c = 0; c < columnNamesFromFile.size(); c++) { 
			cbColumnNameFromFile1.addItem(columnNamesFromFile.get(c));
			cbColumnNameFromFile2.addItem(columnNamesFromFile.get(c));
			cbColumnNameFromFile3.addItem(columnNamesFromFile.get(c));
			cbColumnNameFromFile4.addItem(columnNamesFromFile.get(c));
			cbColumnNameFromFile5.addItem(columnNamesFromFile.get(c));
		}
		cbColumnNameFromFile4.setSelectedIndex(-1);
		cbColumnNameFromFile3.setSelectedIndex(-1);
		cbColumnNameFromFile5.setSelectedIndex(-1);
		for (int c = 0; c < columnNamesFromFile.size(); c++) { 
			//filters to match column names from file to required column names in table
			if((columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.COMPARTMENT_FILTER[0])) {
				cbColumnNameFromFile4.setSelectedIndex(c);
				LocalConfig.getInstance().setCompartmentColumnIndex(c);	
			} else if((columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.CHARGE_FILTER[0]) && !(columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.CHARGE_NOT_FILTER[0])) {
				cbColumnNameFromFile3.setSelectedIndex(c);
				LocalConfig.getInstance().setChargeColumnIndex(c);
			} else if((columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.BOUNDARY_FILTER[0])) {
				cbColumnNameFromFile5.setSelectedIndex(c);
				LocalConfig.getInstance().setBoundaryColumnIndex(c);
			}  
		}
		 
		//first two columns are recommended to be column 1 and 2: abbreviation (id), and name
		cbColumnNameFromFile1.setSelectedIndex(0);
		LocalConfig.getInstance().setMetaboliteAbbreviationColumnIndex(0);
		cbColumnNameFromFile2.setSelectedIndex(1);	
		LocalConfig.getInstance().setMetaboliteNameColumnIndex(1);
	}
	
	
	public static void main(String[] args) throws Exception {
	    
		Class.forName("org.sqlite.JDBC");       
		DatabaseCreator databaseCreator = new DatabaseCreator();
		Connection con = DriverManager.getConnection("jdbc:sqlite:" + "untitled" + ".db");
		//Connection con = DriverManager.getConnection("jdbc:sqlite:" + LocalConfig.getInstance().getDatabaseName() + ".db");
	    
		//based on code from http://stackoverflow.com/questions/6403821/how-to-add-an-image-to-a-jframe-title-bar
		final ArrayList<Image> icons = new ArrayList<Image>(); 
	    icons.add(new ImageIcon("etc/most16.jpg").getImage()); 
	    icons.add(new ImageIcon("etc/most32.jpg").getImage());

	    ArrayList<String> list = new ArrayList();
	    list.add("test");
	    list.add("test");
	    list.add("test");
		MetaboliteColumnNameInterface frame = new MetaboliteColumnNameInterface(con, list);
		frame.setIconImages(icons);
		frame.setSize(600, 300);
	    frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	    frame.setLocationRelativeTo(null);
	    frame.setVisible(true);
	    
	  }

	}

	