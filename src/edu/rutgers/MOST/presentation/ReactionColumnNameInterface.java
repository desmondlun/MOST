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
import edu.rutgers.MOST.data.ReactionsMetaColumnManager;
import edu.rutgers.MOST.data.TextMetabolitesModelReader;
import edu.rutgers.MOST.data.TextReactionsModelReader;

public class ReactionColumnNameInterface extends JDialog {
	
	JButton okButton = new JButton("     OK     ");
    JButton cancelButton = new JButton("  Cancel  ");  
    
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
    final JComboBox<String> cbColumnDisplayName6 = new JComboBox();
    final JComboBox<String> cbColumnNameFromFile6 = new JComboBox();
    final JComboBox<String> cbColumnDisplayName7 = new JComboBox();
    final JComboBox<String> cbColumnNameFromFile7 = new JComboBox();
    final JComboBox<String> cbColumnDisplayName8 = new JComboBox();
    final JComboBox<String> cbColumnNameFromFile8 = new JComboBox();
    final JComboBox<String> cbColumnDisplayName9 = new JComboBox();
    final JComboBox<String> cbColumnNameFromFile9 = new JComboBox();
    
    public static ArrayList<String> columnNamesFromFile;
    
    public static ArrayList<String> getColumnNamesFromFile() {
		return columnNamesFromFile;
	}
	
	public void setColumnNamesFromFile(ArrayList<String> columnNamesFromFile) {
		this.columnNamesFromFile = columnNamesFromFile;
	}
	
	public ReactionColumnNameInterface(final Connection con, ArrayList<String> columnNamesFromFile)
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
		cbColumnDisplayName6.setEditable(true);		
		cbColumnNameFromFile6.setEditable(true);
		cbColumnDisplayName7.setEditable(true);		
		cbColumnNameFromFile7.setEditable(true);
		cbColumnDisplayName8.setEditable(true);		
		cbColumnNameFromFile8.setEditable(true);
		cbColumnDisplayName9.setEditable(true);		
		cbColumnNameFromFile9.setEditable(true);
	    
	    for (int g = 0; g < GraphicalInterfaceConstants.REACTION_ID_COLUMN_NAMES.length; g++) {
	    	cbColumnDisplayName1.addItem(GraphicalInterfaceConstants.REACTION_ID_COLUMN_NAMES[g].toString());
	    }
	    	
	    for (int g = 0; g < GraphicalInterfaceConstants.REACTION_NAME_COLUMN_NAMES.length; g++) {
	    	cbColumnDisplayName2.addItem(GraphicalInterfaceConstants.REACTION_NAME_COLUMN_NAMES[g].toString());
	    }
	    
	    for (int g = 0; g < GraphicalInterfaceConstants.REACTION_EQUATION_COLUMN_NAMES.length; g++) {
	    	cbColumnDisplayName3.addItem(GraphicalInterfaceConstants.REACTION_EQUATION_COLUMN_NAMES[g].toString());
	    }
	    
	    for (int g = 0; g < GraphicalInterfaceConstants.KNOCKOUT_COLUMN_NAMES.length; g++) {
	    	cbColumnDisplayName4.addItem(GraphicalInterfaceConstants.KNOCKOUT_COLUMN_NAMES[g].toString());
	    }
	    
	    for (int g = 0; g < GraphicalInterfaceConstants.FLUX_VALUE_COLUMN_NAMES.length; g++) {
	    	cbColumnDisplayName5.addItem(GraphicalInterfaceConstants.FLUX_VALUE_COLUMN_NAMES[g].toString());
	    }
	    
	    for (int g = 0; g < GraphicalInterfaceConstants.REVERSIBLE_COLUMN_NAMES.length; g++) {
	    	cbColumnDisplayName6.addItem(GraphicalInterfaceConstants.REVERSIBLE_COLUMN_NAMES[g].toString());
	    }
	    	
	    for (int g = 0; g < GraphicalInterfaceConstants.LOWER_BOUND_COLUMN_NAMES.length; g++) {
	    	cbColumnDisplayName7.addItem(GraphicalInterfaceConstants.LOWER_BOUND_COLUMN_NAMES[g].toString());
	    }
	    
	    for (int g = 0; g < GraphicalInterfaceConstants.UPPER_BOUND_COLUMN_NAMES.length; g++) {
	    	cbColumnDisplayName8.addItem(GraphicalInterfaceConstants.UPPER_BOUND_COLUMN_NAMES[g].toString());
	    }
	    
	    for (int g = 0; g < GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN_NAMES.length; g++) {
	    	cbColumnDisplayName9.addItem(GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN_NAMES[g].toString());
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
	    cbColumnDisplayName5.setSelectedIndex(0);
	    
	    cbColumnDisplayName6.setPreferredSize(new Dimension(150, 10));
	    cbColumnDisplayName6.setSelectedIndex(0);
	    
	    cbColumnDisplayName7.setPreferredSize(new Dimension(150, 10));
	    cbColumnDisplayName7.setSelectedIndex(0);
	    
	    cbColumnDisplayName8.setPreferredSize(new Dimension(150, 10));
	    cbColumnDisplayName8.setSelectedIndex(0);
	    
	    cbColumnDisplayName9.setPreferredSize(new Dimension(150, 10));
	    cbColumnDisplayName9.setSelectedIndex(0);
	    
	    
	    
	    JTextField fieldDisplayName1 = (JTextField)cbColumnDisplayName1.getEditor().getEditorComponent();
	    fieldDisplayName1.addKeyListener(new ComboKeyHandler(cbColumnDisplayName1));
	    
	    JTextField fieldDisplayName2 = (JTextField)cbColumnDisplayName2.getEditor().getEditorComponent();
	    fieldDisplayName2.addKeyListener(new ComboKeyHandler(cbColumnDisplayName2));
	    
	    JTextField fieldDisplayName3 = (JTextField)cbColumnDisplayName3.getEditor().getEditorComponent();
	    fieldDisplayName3.addKeyListener(new ComboKeyHandler(cbColumnDisplayName3));
	    
	    JTextField fieldDisplayName4 = (JTextField)cbColumnDisplayName4.getEditor().getEditorComponent();
	    fieldDisplayName4.addKeyListener(new ComboKeyHandler(cbColumnDisplayName4));
	    
	    JTextField fieldDisplayName5 = (JTextField)cbColumnDisplayName5.getEditor().getEditorComponent();
	    fieldDisplayName5.addKeyListener(new ComboKeyHandler(cbColumnDisplayName5));
	    
	    JTextField fieldDisplayName6 = (JTextField)cbColumnDisplayName6.getEditor().getEditorComponent();
	    fieldDisplayName6.addKeyListener(new ComboKeyHandler(cbColumnDisplayName6));
	    
	    JTextField fieldDisplayName7 = (JTextField)cbColumnDisplayName7.getEditor().getEditorComponent();
	    fieldDisplayName7.addKeyListener(new ComboKeyHandler(cbColumnDisplayName7));
	    
	    JTextField fieldDisplayName8 = (JTextField)cbColumnDisplayName8.getEditor().getEditorComponent();
	    fieldDisplayName8.addKeyListener(new ComboKeyHandler(cbColumnDisplayName8));
	    
	    JTextField fieldDisplayName9 = (JTextField)cbColumnDisplayName9.getEditor().getEditorComponent();
	    fieldDisplayName9.addKeyListener(new ComboKeyHandler(cbColumnDisplayName9));
	    
	    
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
	    
	    JTextField fieldFromFile6 = (JTextField)cbColumnNameFromFile6.getEditor().getEditorComponent();
	    fieldFromFile6.addKeyListener(new ComboKeyHandler(cbColumnNameFromFile6));
	    
	    JTextField fieldFromFile7 = (JTextField)cbColumnNameFromFile7.getEditor().getEditorComponent();
	    fieldFromFile7.addKeyListener(new ComboKeyHandler(cbColumnNameFromFile7));
	    
	    JTextField fieldFromFile8 = (JTextField)cbColumnNameFromFile8.getEditor().getEditorComponent();
	    fieldFromFile8.addKeyListener(new ComboKeyHandler(cbColumnNameFromFile8));
	    
	    JTextField fieldFromFile9 = (JTextField)cbColumnNameFromFile9.getEditor().getEditorComponent();
	    fieldFromFile9.addKeyListener(new ComboKeyHandler(cbColumnNameFromFile9));
	    
	    //box layout
	    Box vb = Box.createVerticalBox();
	    
	    Box hbLabels = Box.createHorizontalBox();
	    Box hb1 = Box.createHorizontalBox();
	    Box hb2 = Box.createHorizontalBox();
	    Box hb3 = Box.createHorizontalBox();
	    Box hb4 = Box.createHorizontalBox();
	    Box hb5 = Box.createHorizontalBox();
	    Box hb6 = Box.createHorizontalBox();
	    Box hb7 = Box.createHorizontalBox();
	    Box hb8 = Box.createHorizontalBox();
	    Box hb9 = Box.createHorizontalBox();
	    
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
	    Box hbDisplay6 = Box.createHorizontalBox();	    
	    Box hbFromFile6 = Box.createHorizontalBox();
	    Box hbDisplay7 = Box.createHorizontalBox();	    
	    Box hbFromFile7 = Box.createHorizontalBox();
	    Box hbDisplay8 = Box.createHorizontalBox();	    
	    Box hbFromFile8 = Box.createHorizontalBox();
	    Box hbDisplay9 = Box.createHorizontalBox();	    
	    Box hbFromFile9 = Box.createHorizontalBox();
	    	    
	    Box hbButton = Box.createHorizontalBox();
	    
	    //alignment not working
	    JLabel displayLabel = new JLabel();
	    displayLabel.setText(GraphicalInterfaceConstants.REACTIONS_DISPLAY_LABEL);
	    displayLabel.setSize(new Dimension(150, 20));
	    displayLabel.setBorder(BorderFactory.createEmptyBorder(10,20,0,100));
	    displayLabel.setAlignmentX(LEFT_ALIGNMENT);
	    
	    hbDisplayLabel.add(displayLabel);	
	    hbDisplayLabel.setAlignmentX(LEFT_ALIGNMENT);
	    
	    JLabel fromFileLabel = new JLabel();
	    fromFileLabel.setText(GraphicalInterfaceConstants.REACTIONS_FROM_FILE_LABEL);
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
	    
	    JPanel panelDisplay6 = new JPanel();
	    panelDisplay6.setLayout(new BoxLayout(panelDisplay6, BoxLayout.X_AXIS));
	    panelDisplay6.add(cbColumnDisplayName6);
	    panelDisplay6.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbDisplay6.add(panelDisplay6);
	    
	    JPanel panelFromFile6 = new JPanel();
	    panelFromFile6.setLayout(new BoxLayout(panelFromFile6, BoxLayout.X_AXIS));
	    panelFromFile6.add(cbColumnNameFromFile6);
	    panelFromFile6.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbFromFile6.add(panelFromFile6);
	    
	    hb6.add(hbDisplay6);
	    hb6.add(hbFromFile6);
	    
	    JPanel panelDisplay7 = new JPanel();
	    panelDisplay7.setLayout(new BoxLayout(panelDisplay7, BoxLayout.X_AXIS));
	    panelDisplay7.add(cbColumnDisplayName7);
	    panelDisplay7.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbDisplay7.add(panelDisplay7);
	    
	    JPanel panelFromFile7 = new JPanel();
	    panelFromFile7.setLayout(new BoxLayout(panelFromFile7, BoxLayout.X_AXIS));
	    panelFromFile7.add(cbColumnNameFromFile7);
	    panelFromFile7.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbFromFile7.add(panelFromFile7);
	    
	    hb7.add(hbDisplay7);
	    hb7.add(hbFromFile7);
	    
	    JPanel panelDisplay8 = new JPanel();
	    panelDisplay8.setLayout(new BoxLayout(panelDisplay8, BoxLayout.X_AXIS));
	    panelDisplay8.add(cbColumnDisplayName8);
	    panelDisplay8.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbDisplay8.add(panelDisplay8);
	    
	    JPanel panelFromFile8 = new JPanel();
	    panelFromFile8.setLayout(new BoxLayout(panelFromFile8, BoxLayout.X_AXIS));
	    panelFromFile8.add(cbColumnNameFromFile8);
	    panelFromFile8.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbFromFile8.add(panelFromFile8);
	    
	    hb8.add(hbDisplay8);
	    hb8.add(hbFromFile8);
	    
	    JPanel panelDisplay9 = new JPanel();
	    panelDisplay9.setLayout(new BoxLayout(panelDisplay9, BoxLayout.X_AXIS));
	    panelDisplay9.add(cbColumnDisplayName9);
	    panelDisplay9.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbDisplay9.add(panelDisplay9);
	    
	    JPanel panelFromFile9 = new JPanel();
	    panelFromFile9.setLayout(new BoxLayout(panelFromFile9, BoxLayout.X_AXIS));
	    panelFromFile9.add(cbColumnNameFromFile9);
	    panelFromFile9.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbFromFile9.add(panelFromFile9);
	    
	    hb9.add(hbDisplay9);
	    hb9.add(hbFromFile9);
	    
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
	    vb.add(hb6);
	    vb.add(hb7);
	    vb.add(hb8);
	    vb.add(hb9);
	    vb.add(hbButton);
	    
	    add(vb);
	    
	    ActionListener actionListener = new ActionListener() {
		      public void actionPerformed(ActionEvent actionEvent) { 
		    	String displayNameSelection1 = (String) cbColumnDisplayName1.getSelectedItem(); 		    	
		    	//String nameFromFileSelection1 = (String) cbColumnNameFromFile1.getSelectedItem(); 
		    	String displayNameSelection2 = (String) cbColumnDisplayName2.getSelectedItem(); 		    	
		    	//String nameFromFileSelection2 = (String) cbColumnNameFromFile2.getSelectedItem();
		    	String displayNameSelection3 = (String) cbColumnDisplayName3.getSelectedItem(); 		    	
		    	//String nameFromFileSelection3 = (String) cbColumnNameFromFile3.getSelectedItem();
		    	String displayNameSelection4 = (String) cbColumnDisplayName4.getSelectedItem(); 		    	
		    	//String nameFromFileSelection4 = (String) cbColumnNameFromFile4.getSelectedItem(); 
		    	String displayNameSelection5 = (String) cbColumnDisplayName5.getSelectedItem(); 		    	
		    	//String nameFromFileSelection5 = (String) cbColumnNameFromFile5.getSelectedItem(); 
		    	String displayNameSelection6 = (String) cbColumnDisplayName6.getSelectedItem(); 		    	
		    	//String nameFromFileSelection6 = (String) cbColumnNameFromFile6.getSelectedItem();
		    	String displayNameSelection7 = (String) cbColumnDisplayName7.getSelectedItem(); 		    	
		    	//String nameFromFileSelection7 = (String) cbColumnNameFromFile7.getSelectedItem();
		    	String displayNameSelection8 = (String) cbColumnDisplayName8.getSelectedItem(); 		    	
		    	//String nameFromFileSelection8 = (String) cbColumnNameFromFile8.getSelectedItem(); 
		    	String displayNameSelection9 = (String) cbColumnDisplayName9.getSelectedItem(); 		    	
		    	//String nameFromFileSelection9 = (String) cbColumnNameFromFile9.getSelectedItem(); 
		    	LocalConfig.getInstance().setReactionAbbreviationColumnName(displayNameSelection1);
		    	LocalConfig.getInstance().setReactionNameColumnName(displayNameSelection2);
		    	LocalConfig.getInstance().setReactionEquationColumnName(displayNameSelection3);
		    	LocalConfig.getInstance().setKnockoutColumnName(displayNameSelection4);
		    	LocalConfig.getInstance().setFluxValueColumnName(displayNameSelection5);
		    	LocalConfig.getInstance().setReversibleColumnName(displayNameSelection6);
		    	LocalConfig.getInstance().setLowerBoundColumnName(displayNameSelection7);
		    	LocalConfig.getInstance().setUpperBoundColumnName(displayNameSelection8);
		    	LocalConfig.getInstance().setBiologicalObjectiveColumnName(displayNameSelection9);
		    	if (cbColumnNameFromFile1.getSelectedItem() != null) {
		    		LocalConfig.getInstance().setReactionAbbreviationColumnIndex(cbColumnDisplayName1.getSelectedIndex());
		    	} else {
		    		LocalConfig.getInstance().setReactionAbbreviationColumnIndex(-1);
		    	}
		    	if (cbColumnNameFromFile2.getSelectedItem() != null) {
		    		LocalConfig.getInstance().setReactionNameColumnIndex(cbColumnDisplayName2.getSelectedIndex());
		    	} else {
		    		LocalConfig.getInstance().setReactionNameColumnIndex(-1);
		    	}
		    	if (cbColumnNameFromFile3.getSelectedItem() != null) {
		    		LocalConfig.getInstance().setReactionEquationColumnIndex(cbColumnDisplayName3.getSelectedIndex());
		    	} else {
		    		LocalConfig.getInstance().setReactionEquationColumnIndex(-1);
		    	}
		    	if (cbColumnNameFromFile4.getSelectedItem() != null) {
		    		LocalConfig.getInstance().setKnockoutColumnIndex(cbColumnDisplayName4.getSelectedIndex());
		    	} else {
		    		LocalConfig.getInstance().setKnockoutColumnIndex(-1);
		    	}
		    	if (cbColumnNameFromFile5.getSelectedItem() != null) {
		    		LocalConfig.getInstance().setFluxValueColumnIndex(cbColumnDisplayName5.getSelectedIndex());
		    	} else {
		    		LocalConfig.getInstance().setFluxValueColumnIndex(-1);
		    	}
		    	if (cbColumnNameFromFile6.getSelectedItem() != null) {
		    		LocalConfig.getInstance().setReversibleColumnIndex(cbColumnDisplayName6.getSelectedIndex());
		    	} else {
		    		LocalConfig.getInstance().setReversibleColumnIndex(-1);
		    	}
		    	if (cbColumnNameFromFile7.getSelectedItem() != null) {
		    		LocalConfig.getInstance().setLowerBoundColumnIndex(cbColumnDisplayName7.getSelectedIndex());
		    	} else {
		    		LocalConfig.getInstance().setLowerBoundColumnIndex(-1);
		    	}
		    	if (cbColumnNameFromFile8.getSelectedItem() != null) {
		    		LocalConfig.getInstance().setUpperBoundColumnIndex(cbColumnDisplayName8.getSelectedIndex());
		    	} else {
		    		LocalConfig.getInstance().setUpperBoundColumnIndex(-1);
		    	}
		    	if (cbColumnNameFromFile9.getSelectedItem() != null) {
		    		LocalConfig.getInstance().setBiologicalObjectiveColumnIndex(cbColumnDisplayName9.getSelectedIndex());
		    	} else {
		    		LocalConfig.getInstance().setBiologicalObjectiveColumnIndex(-1);
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
		cbColumnDisplayName6.addActionListener(actionListener);
		cbColumnNameFromFile6.addActionListener(actionListener);
		cbColumnDisplayName7.addActionListener(actionListener);
		cbColumnNameFromFile7.addActionListener(actionListener);
		cbColumnDisplayName8.addActionListener(actionListener);
		cbColumnNameFromFile8.addActionListener(actionListener);
		cbColumnDisplayName9.addActionListener(actionListener);
		cbColumnNameFromFile9.addActionListener(actionListener);
		
		ActionListener okButtonActionListener = new ActionListener() {
	        public void actionPerformed(ActionEvent ae) {
	        	LocalConfig.getInstance().setReactionAbbreviationColumnName((String) cbColumnDisplayName1.getSelectedItem());
	        	LocalConfig.getInstance().setReactionNameColumnName((String) cbColumnDisplayName2.getSelectedItem());
	        	LocalConfig.getInstance().setReactionEquationColumnName((String) cbColumnDisplayName3.getSelectedItem());
	        	LocalConfig.getInstance().setKnockoutColumnName((String) cbColumnDisplayName4.getSelectedItem());
	        	LocalConfig.getInstance().setFluxValueColumnName((String) cbColumnDisplayName5.getSelectedItem());
	        	LocalConfig.getInstance().setReversibleColumnName((String) cbColumnDisplayName6.getSelectedItem());
	        	LocalConfig.getInstance().setLowerBoundColumnName((String) cbColumnDisplayName7.getSelectedItem());
	        	LocalConfig.getInstance().setUpperBoundColumnName((String) cbColumnDisplayName8.getSelectedItem());
	        	LocalConfig.getInstance().setBiologicalObjectiveColumnName((String) cbColumnDisplayName9.getSelectedItem());
	        	
	        	//add metacolumn names to db
	        	ReactionsMetaColumnManager reactionsMetaColumnManager = new ReactionsMetaColumnManager();
	        	ArrayList<String> metaColumnNames = new ArrayList();
	        	ArrayList<Integer> usedIndices = new ArrayList();
	        	ArrayList<Integer> metaColumnIndexList = new ArrayList();
	        	
	        	if (getColumnNamesFromFile().contains(cbColumnNameFromFile1.getSelectedItem())) {
	    			LocalConfig.getInstance().setReactionAbbreviationColumnIndex(getColumnNamesFromFile().indexOf(cbColumnNameFromFile1.getSelectedItem()));
	    			usedIndices.add(getColumnNamesFromFile().indexOf(cbColumnNameFromFile1.getSelectedItem()));
	        	}
	    		if (getColumnNamesFromFile().contains(cbColumnNameFromFile2.getSelectedItem())) {
	    			LocalConfig.getInstance().setReactionNameColumnIndex(getColumnNamesFromFile().indexOf(cbColumnNameFromFile2.getSelectedItem()));
	    			usedIndices.add(getColumnNamesFromFile().indexOf(cbColumnNameFromFile2.getSelectedItem()));
	        	}
	    		if (getColumnNamesFromFile().contains(cbColumnNameFromFile3.getSelectedItem())) {
	    			LocalConfig.getInstance().setReactionEquationColumnIndex(getColumnNamesFromFile().indexOf(cbColumnNameFromFile3.getSelectedItem()));
	    			usedIndices.add(getColumnNamesFromFile().indexOf(cbColumnNameFromFile3.getSelectedItem()));
	        	}
	    		if (getColumnNamesFromFile().contains(cbColumnNameFromFile4.getSelectedItem())) {
	    			LocalConfig.getInstance().setKnockoutColumnIndex(getColumnNamesFromFile().indexOf(cbColumnNameFromFile4.getSelectedItem()));
	    			usedIndices.add(getColumnNamesFromFile().indexOf(cbColumnNameFromFile4.getSelectedItem()));
	        	}
	    		if (getColumnNamesFromFile().contains(cbColumnNameFromFile5.getSelectedItem())) {
	    			LocalConfig.getInstance().setFluxValueColumnIndex(getColumnNamesFromFile().indexOf(cbColumnNameFromFile5.getSelectedItem()));
	    			usedIndices.add(getColumnNamesFromFile().indexOf(cbColumnNameFromFile5.getSelectedItem()));
	        	}
	    		if (getColumnNamesFromFile().contains(cbColumnNameFromFile6.getSelectedItem())) {
	    			LocalConfig.getInstance().setReversibleColumnIndex(getColumnNamesFromFile().indexOf(cbColumnNameFromFile6.getSelectedItem()));
	    			usedIndices.add(getColumnNamesFromFile().indexOf(cbColumnNameFromFile6.getSelectedItem()));
	        	}
	    		if (getColumnNamesFromFile().contains(cbColumnNameFromFile7.getSelectedItem())) {
	    			LocalConfig.getInstance().setLowerBoundColumnIndex(getColumnNamesFromFile().indexOf(cbColumnNameFromFile7.getSelectedItem()));
	    			usedIndices.add(getColumnNamesFromFile().indexOf(cbColumnNameFromFile7.getSelectedItem()));
	        	}
	    		if (getColumnNamesFromFile().contains(cbColumnNameFromFile8.getSelectedItem())) {
	    			LocalConfig.getInstance().setUpperBoundColumnIndex(getColumnNamesFromFile().indexOf(cbColumnNameFromFile8.getSelectedItem()));
	    			usedIndices.add(getColumnNamesFromFile().indexOf(cbColumnNameFromFile8.getSelectedItem()));
	        	}
	    		if (getColumnNamesFromFile().contains(cbColumnNameFromFile9.getSelectedItem())) {
	    			LocalConfig.getInstance().setBiologicalObjectiveColumnIndex(getColumnNamesFromFile().indexOf(cbColumnNameFromFile9.getSelectedItem()));
	    			usedIndices.add(getColumnNamesFromFile().indexOf(cbColumnNameFromFile9.getSelectedItem()));
	        	}
	    		for (int i = 0; i < getColumnNamesFromFile().size(); i++) {
	        		if (!usedIndices.contains(i)) {
	        			metaColumnNames.add(getColumnNamesFromFile().get(i));
	        			metaColumnIndexList.add(getColumnNamesFromFile().indexOf(getColumnNamesFromFile().get(i)));
	        		} 
	        	}
	        	
	        	reactionsMetaColumnManager.addColumnNames(LocalConfig.getInstance().getDatabaseName(), metaColumnNames);
	        	LocalConfig.getInstance().setReactionsMetaColumnIndexList(metaColumnIndexList);
	        	
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
				
	        	TextReactionsModelReader reader = new TextReactionsModelReader();
	        	reader.load(GraphicalInterface.getReactionsCSVFile(), LocalConfig.getInstance().getDatabaseName());
	    		
	        	progress.setVisible(false);
	        	progress.dispose();
	        }
		};
		
					
		okButton.addActionListener(okButtonActionListener); 

		ActionListener cancelButtonActionListener = new ActionListener() {
	        public void actionPerformed(ActionEvent prodActionEvent) {
				setVisible(false);
				dispose();
				//this is a hack, creates balnk reaction table
				try {
		    		  Class.forName("org.sqlite.JDBC");       
		    		  DatabaseCreator databaseCreator = new DatabaseCreator();
		    		  databaseCreator.createBlankReactionsTable(LocalConfig.getInstance().getDatabaseName());
				  } catch (ClassNotFoundException e) {
					  // TODO Auto-generated catch block
					  e.printStackTrace();
				  }
			}
		};
					
		cancelButton.addActionListener(cancelButtonActionListener);    
		    
	}
	
    public void populateNamesFromFileBoxes(ArrayList<String> columnNamesFromFile) {
		
		LocalConfig.getInstance().setKnockoutColumnIndex(-1);
		LocalConfig.getInstance().setFluxValueColumnIndex(-1);
		LocalConfig.getInstance().setReversibleColumnIndex(-1);
		LocalConfig.getInstance().setLowerBoundColumnIndex(-1);
		LocalConfig.getInstance().setUpperBoundColumnIndex(-1);
		LocalConfig.getInstance().setBiologicalObjectiveColumnIndex(-1);
				
		//add all column names to from file comboboxes
		for (int c = 0; c < columnNamesFromFile.size(); c++) { 
			cbColumnNameFromFile1.addItem(columnNamesFromFile.get(c));
			cbColumnNameFromFile2.addItem(columnNamesFromFile.get(c));
			cbColumnNameFromFile3.addItem(columnNamesFromFile.get(c));
			cbColumnNameFromFile4.addItem(columnNamesFromFile.get(c));
			cbColumnNameFromFile5.addItem(columnNamesFromFile.get(c));
			cbColumnNameFromFile6.addItem(columnNamesFromFile.get(c));
			cbColumnNameFromFile7.addItem(columnNamesFromFile.get(c));
			cbColumnNameFromFile8.addItem(columnNamesFromFile.get(c));
			cbColumnNameFromFile9.addItem(columnNamesFromFile.get(c));
		}		
		cbColumnNameFromFile4.setSelectedIndex(-1);	
		cbColumnNameFromFile5.setSelectedIndex(-1);
		cbColumnNameFromFile6.setSelectedIndex(-1);	
		cbColumnNameFromFile7.setSelectedIndex(-1);
		cbColumnNameFromFile8.setSelectedIndex(-1);
		cbColumnNameFromFile9.setSelectedIndex(-1);
		for (int c = 0; c < columnNamesFromFile.size(); c++) { 
			//filters to match column names from file to required column names in table			
			if((columnNamesFromFile.get(c).toLowerCase()).compareTo(GraphicalInterfaceConstants.KNOCKOUT_COLUMN_FILTER[0]) == 0 || (columnNamesFromFile.get(c).toLowerCase()).compareTo(GraphicalInterfaceConstants.KNOCKOUT_COLUMN_FILTER[1]) == 0) {
				cbColumnNameFromFile4.setSelectedIndex(c);
				LocalConfig.getInstance().setKnockoutColumnIndex(c);	
			} else if((columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.FLUX_VALUE_COLUMN_FILTER[0])) {
				cbColumnNameFromFile5.setSelectedIndex(c);
				LocalConfig.getInstance().setFluxValueColumnIndex(c);
			} else if((columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.REVERSIBLE_COLUMN_FILTER[0])) {
				cbColumnNameFromFile6.setSelectedIndex(c);
				LocalConfig.getInstance().setReversibleColumnIndex(c); 
			} else if((columnNamesFromFile.get(c).toLowerCase()).compareTo(GraphicalInterfaceConstants.LOWER_BOUND_FILTER[0]) == 0 || (columnNamesFromFile.get(c).toLowerCase()).compareTo(GraphicalInterfaceConstants.LOWER_BOUND_FILTER[1]) == 0) {
				cbColumnNameFromFile7.setSelectedIndex(c);
				LocalConfig.getInstance().setLowerBoundColumnIndex(c); 
			} else if((columnNamesFromFile.get(c).toLowerCase()).compareTo(GraphicalInterfaceConstants.UPPER_BOUND_FILTER[0]) == 0 || (columnNamesFromFile.get(c).toLowerCase()).compareTo(GraphicalInterfaceConstants.UPPER_BOUND_FILTER[1]) == 0) {
				cbColumnNameFromFile8.setSelectedIndex(c);
				LocalConfig.getInstance().setUpperBoundColumnIndex(c); 
			} else if((columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_FILTER[0])) {
				cbColumnNameFromFile9.setSelectedIndex(c);
				LocalConfig.getInstance().setBiologicalObjectiveColumnIndex(c); 	
			}
		}
		 
		//csv files written by TextReactionWriter will have KO and fluxValue as col 1 and 2
		if((columnNamesFromFile.get(0).toLowerCase()).compareTo(GraphicalInterfaceConstants.KNOCKOUT_COLUMN_FILTER[0]) == 0 || (columnNamesFromFile.get(0).toLowerCase()).compareTo(GraphicalInterfaceConstants.KNOCKOUT_COLUMN_FILTER[1]) == 0) {
			cbColumnNameFromFile1.setSelectedIndex(2);
			LocalConfig.getInstance().setReactionAbbreviationColumnIndex(2);
			cbColumnNameFromFile2.setSelectedIndex(3);	
			LocalConfig.getInstance().setReactionNameColumnIndex(3);
			cbColumnNameFromFile3.setSelectedIndex(4);	
			LocalConfig.getInstance().setReactionEquationColumnIndex(4);
		} else {
			//first two columns are recommended to be column 1 and 2: abbreviation (id), and name
			cbColumnNameFromFile1.setSelectedIndex(0);
			LocalConfig.getInstance().setReactionAbbreviationColumnIndex(0);
			cbColumnNameFromFile2.setSelectedIndex(1);	
			LocalConfig.getInstance().setReactionNameColumnIndex(1);
			cbColumnNameFromFile3.setSelectedIndex(2);	
			LocalConfig.getInstance().setReactionEquationColumnIndex(2);
		}		
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
		ReactionColumnNameInterface frame = new ReactionColumnNameInterface(con, list);
		frame.setIconImages(icons);
		frame.setSize(600, 420);
	    frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	    frame.setLocationRelativeTo(null);
	    frame.setVisible(true);
	    
	  }

	}


