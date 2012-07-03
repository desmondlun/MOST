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
import edu.rutgers.MOST.data.Excel97Reader;
import edu.rutgers.MOST.data.MetabolitesMetaColumnManager;
import edu.rutgers.MOST.data.ReactionsMetaColumnManager;
import edu.rutgers.MOST.data.TextMetabolitesModelReader;
import edu.rutgers.MOST.data.TextReactionsModelReader;

public class ExcelColumnNameInterface extends JDialog {
	
	JButton okButton = new JButton("     OK     ");
    JButton cancelButton = new JButton("  Cancel  ");  
    
    final JComboBox<String> cbMetabColumnDisplayName1 = new JComboBox();
    final JComboBox<String> cbMetabColumnNameFromFile1 = new JComboBox();
    final JComboBox<String> cbMetabColumnDisplayName2 = new JComboBox();
    final JComboBox<String> cbMetabColumnNameFromFile2 = new JComboBox();
    final JComboBox<String> cbMetabColumnDisplayName3 = new JComboBox();
    final JComboBox<String> cbMetabColumnNameFromFile3 = new JComboBox();
    final JComboBox<String> cbMetabColumnDisplayName4 = new JComboBox();
    final JComboBox<String> cbMetabColumnNameFromFile4 = new JComboBox();   
    final JComboBox<String> cbMetabColumnDisplayName5 = new JComboBox();
    final JComboBox<String> cbMetabColumnNameFromFile5 = new JComboBox();
    
    final JComboBox<String> cbReacColumnDisplayName1 = new JComboBox();
    final JComboBox<String> cbReacColumnNameFromFile1 = new JComboBox();
    final JComboBox<String> cbReacColumnDisplayName2 = new JComboBox();
    final JComboBox<String> cbReacColumnNameFromFile2 = new JComboBox();
    final JComboBox<String> cbReacColumnDisplayName3 = new JComboBox();
    final JComboBox<String> cbReacColumnNameFromFile3 = new JComboBox();
    final JComboBox<String> cbReacColumnDisplayName4 = new JComboBox();
    final JComboBox<String> cbReacColumnNameFromFile4 = new JComboBox();
    final JComboBox<String> cbReacColumnDisplayName5 = new JComboBox();
    final JComboBox<String> cbReacColumnNameFromFile5 = new JComboBox();
    final JComboBox<String> cbReacColumnDisplayName6 = new JComboBox();
    final JComboBox<String> cbReacColumnNameFromFile6 = new JComboBox();
    final JComboBox<String> cbReacColumnDisplayName7 = new JComboBox();
    final JComboBox<String> cbReacColumnNameFromFile7 = new JComboBox();
    final JComboBox<String> cbReacColumnDisplayName8 = new JComboBox();
    final JComboBox<String> cbReacColumnNameFromFile8 = new JComboBox();
    final JComboBox<String> cbReacColumnDisplayName9 = new JComboBox();
    final JComboBox<String> cbReacColumnNameFromFile9 = new JComboBox();
    
    public static ArrayList<String> metabColumnNamesFromFile;
    
    public static ArrayList<String> getMetabColumnNamesFromFile() {
		return metabColumnNamesFromFile;
	}
	
	public void setMetabColumnNamesFromFile(ArrayList<String> metabColumnNamesFromFile) {
		this.metabColumnNamesFromFile = metabColumnNamesFromFile;
	}
	
    public static ArrayList<String> reacColumnNamesFromFile;
    
    public static ArrayList<String> getReacColumnNamesFromFile() {
		return reacColumnNamesFromFile;
	}
	
	public void setReacColumnNamesFromFile(ArrayList<String> reacColumnNamesFromFile) {
		this.reacColumnNamesFromFile = reacColumnNamesFromFile;
	}
	
	public ExcelColumnNameInterface(final Connection con, ArrayList<String> metabColumnNamesFromFile, ArrayList<String> reacColumnNamesFromFile)
	    throws SQLException {

		setMetabColumnNamesFromFile(metabColumnNamesFromFile);
		setReacColumnNamesFromFile(reacColumnNamesFromFile);
		
		setTitle("Column Selector");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	    	
		cbMetabColumnDisplayName1.setEditable(true);		
		cbMetabColumnNameFromFile1.setEditable(true);
		cbMetabColumnDisplayName2.setEditable(true);		
		cbMetabColumnNameFromFile2.setEditable(true);
		cbMetabColumnDisplayName3.setEditable(true);		
		cbMetabColumnNameFromFile3.setEditable(true);
		cbMetabColumnDisplayName4.setEditable(true);		
		cbMetabColumnNameFromFile4.setEditable(true);
		cbMetabColumnDisplayName5.setEditable(true);		
		cbMetabColumnNameFromFile5.setEditable(true);
		
		cbReacColumnDisplayName1.setEditable(true);		
		cbReacColumnNameFromFile1.setEditable(true);
		cbReacColumnDisplayName2.setEditable(true);		
		cbReacColumnNameFromFile2.setEditable(true);
		cbReacColumnDisplayName3.setEditable(true);		
		cbReacColumnNameFromFile3.setEditable(true);
		cbReacColumnDisplayName4.setEditable(true);		
		cbReacColumnNameFromFile4.setEditable(true);
		cbReacColumnDisplayName5.setEditable(true);		
		cbReacColumnNameFromFile5.setEditable(true);
		cbReacColumnDisplayName6.setEditable(true);		
		cbReacColumnNameFromFile6.setEditable(true);
		cbReacColumnDisplayName7.setEditable(true);		
		cbReacColumnNameFromFile7.setEditable(true);
		cbReacColumnDisplayName8.setEditable(true);		
		cbReacColumnNameFromFile8.setEditable(true);
		cbReacColumnDisplayName9.setEditable(true);		
		cbReacColumnNameFromFile9.setEditable(true);
	    
		for (int g = 0; g < GraphicalInterfaceConstants.METABOLITE_ID_COLUMN_NAMES.length; g++) {
	    	cbMetabColumnDisplayName1.addItem(GraphicalInterfaceConstants.METABOLITE_ID_COLUMN_NAMES[g].toString());
		}
	    
		for (int g = 0; g < GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN_NAMES.length; g++) {
	    	cbMetabColumnDisplayName2.addItem(GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN_NAMES[g].toString());
	    }
	    
	    for (int g = 0; g < GraphicalInterfaceConstants.METABOLITE_CHARGE_COLUMN_NAMES.length; g++) {
	    	cbMetabColumnDisplayName3.addItem(GraphicalInterfaceConstants.METABOLITE_CHARGE_COLUMN_NAMES[g].toString());
	    }
	    
	    for (int g = 0; g < GraphicalInterfaceConstants.METABOLITE_COMPARTMENT_COLUMN_NAMES.length; g++) {
	    	cbMetabColumnDisplayName4.addItem(GraphicalInterfaceConstants.METABOLITE_COMPARTMENT_COLUMN_NAMES[g].toString());
	    }
			    
		
	    for (int g = 0; g < GraphicalInterfaceConstants.REACTION_ID_COLUMN_NAMES.length; g++) {
	    	cbReacColumnDisplayName1.addItem(GraphicalInterfaceConstants.REACTION_ID_COLUMN_NAMES[g].toString());
	    }
	    	
	    for (int g = 0; g < GraphicalInterfaceConstants.REACTION_NAME_COLUMN_NAMES.length; g++) {
	    	cbReacColumnDisplayName2.addItem(GraphicalInterfaceConstants.REACTION_NAME_COLUMN_NAMES[g].toString());
	    }
	    
	    for (int g = 0; g < GraphicalInterfaceConstants.REACTION_EQUATION_COLUMN_NAMES.length; g++) {
	    	cbReacColumnDisplayName3.addItem(GraphicalInterfaceConstants.REACTION_EQUATION_COLUMN_NAMES[g].toString());
	    }
	    
	    for (int g = 0; g < GraphicalInterfaceConstants.KNOCKOUT_COLUMN_NAMES.length; g++) {
	    	cbReacColumnDisplayName4.addItem(GraphicalInterfaceConstants.KNOCKOUT_COLUMN_NAMES[g].toString());
	    }
	    
	    for (int g = 0; g < GraphicalInterfaceConstants.FLUX_VALUE_COLUMN_NAMES.length; g++) {
	    	cbReacColumnDisplayName5.addItem(GraphicalInterfaceConstants.FLUX_VALUE_COLUMN_NAMES[g].toString());
	    }
	    
	    for (int g = 0; g < GraphicalInterfaceConstants.REVERSIBLE_COLUMN_NAMES.length; g++) {
	    	cbReacColumnDisplayName6.addItem(GraphicalInterfaceConstants.REVERSIBLE_COLUMN_NAMES[g].toString());
	    }
	    	
	    for (int g = 0; g < GraphicalInterfaceConstants.LOWER_BOUND_COLUMN_NAMES.length; g++) {
	    	cbReacColumnDisplayName7.addItem(GraphicalInterfaceConstants.LOWER_BOUND_COLUMN_NAMES[g].toString());
	    }
	    
	    for (int g = 0; g < GraphicalInterfaceConstants.UPPER_BOUND_COLUMN_NAMES.length; g++) {
	    	cbReacColumnDisplayName8.addItem(GraphicalInterfaceConstants.UPPER_BOUND_COLUMN_NAMES[g].toString());
	    }
	    
	    for (int g = 0; g < GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN_NAMES.length; g++) {
	    	cbReacColumnDisplayName9.addItem(GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN_NAMES[g].toString());
	    }
	    
	    populateMetabNamesFromFileBoxes(metabColumnNamesFromFile);
	    populateReacNamesFromFileBoxes(reacColumnNamesFromFile);
	    
	    cbMetabColumnDisplayName1.setPreferredSize(new Dimension(150, 10));
	    cbMetabColumnDisplayName1.setSelectedIndex(0);
	    cbMetabColumnDisplayName2.setPreferredSize(new Dimension(150, 10));
	    cbMetabColumnDisplayName2.setSelectedIndex(0);
	    cbMetabColumnDisplayName3.setPreferredSize(new Dimension(150, 10));
	    cbMetabColumnDisplayName3.setSelectedIndex(0);
	    cbMetabColumnDisplayName4.setPreferredSize(new Dimension(150, 10));
	    cbMetabColumnDisplayName4.setSelectedIndex(0);
	    cbMetabColumnDisplayName5.setPreferredSize(new Dimension(150, 10));
	    //cbMetabColumnDisplayName5.setSelectedIndex(0);
	    
	    cbMetabColumnNameFromFile1.setPreferredSize(new Dimension(150, 10)); 
	    cbMetabColumnNameFromFile2.setPreferredSize(new Dimension(150, 10)); 
	    cbMetabColumnNameFromFile3.setPreferredSize(new Dimension(150, 10)); 
	    cbMetabColumnNameFromFile4.setPreferredSize(new Dimension(150, 10)); 
	    cbMetabColumnNameFromFile5.setPreferredSize(new Dimension(150, 10));
	    
	    //set size
	    cbReacColumnDisplayName1.setPreferredSize(new Dimension(150, 10));
	    cbReacColumnDisplayName1.setSelectedIndex(0);
	    
	    cbReacColumnDisplayName2.setPreferredSize(new Dimension(150, 10));
	    cbReacColumnDisplayName2.setSelectedIndex(0);
	    
	    cbReacColumnDisplayName3.setPreferredSize(new Dimension(150, 10));
	    cbReacColumnDisplayName3.setSelectedIndex(0);
	    
	    cbReacColumnDisplayName4.setPreferredSize(new Dimension(150, 10));
	    cbReacColumnDisplayName4.setSelectedIndex(0);
	    
	    cbReacColumnDisplayName5.setPreferredSize(new Dimension(150, 10));
	    cbReacColumnDisplayName5.setSelectedIndex(0);
	    
	    cbReacColumnDisplayName6.setPreferredSize(new Dimension(150, 10));
	    cbReacColumnDisplayName6.setSelectedIndex(0);
	    
	    cbReacColumnDisplayName7.setPreferredSize(new Dimension(150, 10));
	    cbReacColumnDisplayName7.setSelectedIndex(0);
	    
	    cbReacColumnDisplayName8.setPreferredSize(new Dimension(150, 10));
	    cbReacColumnDisplayName8.setSelectedIndex(0);
	    
	    cbReacColumnDisplayName9.setPreferredSize(new Dimension(150, 10));
	    cbReacColumnDisplayName9.setSelectedIndex(0);
	    
	    
	    cbReacColumnNameFromFile1.setPreferredSize(new Dimension(150, 10));
	    cbReacColumnNameFromFile2.setPreferredSize(new Dimension(150, 10));
	    cbReacColumnNameFromFile3.setPreferredSize(new Dimension(150, 10));
	    cbReacColumnNameFromFile4.setPreferredSize(new Dimension(150, 10));
	    cbReacColumnNameFromFile5.setPreferredSize(new Dimension(150, 10));
	    cbReacColumnNameFromFile6.setPreferredSize(new Dimension(150, 10));
	    cbReacColumnNameFromFile7.setPreferredSize(new Dimension(150, 10));
	    cbReacColumnNameFromFile8.setPreferredSize(new Dimension(150, 10));
	    cbReacColumnNameFromFile9.setPreferredSize(new Dimension(150, 10));

	    
	    JTextField fieldMetabDisplayName1 = (JTextField)cbMetabColumnDisplayName1.getEditor().getEditorComponent();
	    fieldMetabDisplayName1.addKeyListener(new ComboKeyHandler(cbMetabColumnDisplayName1));
	    
	    JTextField fieldMetabDisplayName2 = (JTextField)cbMetabColumnDisplayName2.getEditor().getEditorComponent();
	    fieldMetabDisplayName2.addKeyListener(new ComboKeyHandler(cbMetabColumnDisplayName2));
	    
	    JTextField fieldMetabDisplayName3 = (JTextField)cbMetabColumnDisplayName3.getEditor().getEditorComponent();
	    fieldMetabDisplayName3.addKeyListener(new ComboKeyHandler(cbMetabColumnDisplayName3));
	    
	    JTextField fieldMetabDisplayName4 = (JTextField)cbMetabColumnDisplayName4.getEditor().getEditorComponent();
	    fieldMetabDisplayName4.addKeyListener(new ComboKeyHandler(cbMetabColumnDisplayName4));
	    
	    JTextField fieldMetabDisplayName5 = (JTextField)cbMetabColumnDisplayName5.getEditor().getEditorComponent();
	    fieldMetabDisplayName5.addKeyListener(new ComboKeyHandler(cbMetabColumnDisplayName5));
	    
	    
	    JTextField fieldReacDisplayName1 = (JTextField)cbReacColumnDisplayName1.getEditor().getEditorComponent();
	    fieldReacDisplayName1.addKeyListener(new ComboKeyHandler(cbReacColumnDisplayName1));
	    
	    JTextField fieldReacDisplayName2 = (JTextField)cbReacColumnDisplayName2.getEditor().getEditorComponent();
	    fieldReacDisplayName2.addKeyListener(new ComboKeyHandler(cbReacColumnDisplayName2));
	    
	    JTextField fieldReacDisplayName3 = (JTextField)cbReacColumnDisplayName3.getEditor().getEditorComponent();
	    fieldReacDisplayName3.addKeyListener(new ComboKeyHandler(cbReacColumnDisplayName3));
	    
	    JTextField fieldReacDisplayName4 = (JTextField)cbReacColumnDisplayName4.getEditor().getEditorComponent();
	    fieldReacDisplayName4.addKeyListener(new ComboKeyHandler(cbReacColumnDisplayName4));
	    
	    JTextField fieldReacDisplayName5 = (JTextField)cbReacColumnDisplayName5.getEditor().getEditorComponent();
	    fieldReacDisplayName5.addKeyListener(new ComboKeyHandler(cbReacColumnDisplayName5));
	    
	    JTextField fieldReacDisplayName6 = (JTextField)cbReacColumnDisplayName6.getEditor().getEditorComponent();
	    fieldReacDisplayName6.addKeyListener(new ComboKeyHandler(cbReacColumnDisplayName6));
	    
	    JTextField fieldReacDisplayName7 = (JTextField)cbReacColumnDisplayName7.getEditor().getEditorComponent();
	    fieldReacDisplayName7.addKeyListener(new ComboKeyHandler(cbReacColumnDisplayName7));
	    
	    JTextField fieldReacDisplayName8 = (JTextField)cbReacColumnDisplayName8.getEditor().getEditorComponent();
	    fieldReacDisplayName8.addKeyListener(new ComboKeyHandler(cbReacColumnDisplayName8));
	    
	    JTextField fieldReacDisplayName9 = (JTextField)cbReacColumnDisplayName9.getEditor().getEditorComponent();
	    fieldReacDisplayName9.addKeyListener(new ComboKeyHandler(cbReacColumnDisplayName9));
	    
	    
	    JTextField fieldMetabFromFile1 = (JTextField)cbMetabColumnNameFromFile1.getEditor().getEditorComponent();
	    fieldMetabFromFile1.addKeyListener(new ComboKeyHandler(cbMetabColumnNameFromFile1));
	    
	    JTextField fieldMetabFromFile2 = (JTextField)cbMetabColumnNameFromFile2.getEditor().getEditorComponent();
	    fieldMetabFromFile2.addKeyListener(new ComboKeyHandler(cbMetabColumnNameFromFile2));
	    
	    JTextField fieldMetabFromFile3 = (JTextField)cbMetabColumnNameFromFile3.getEditor().getEditorComponent();
	    fieldMetabFromFile3.addKeyListener(new ComboKeyHandler(cbMetabColumnNameFromFile3));
	    
	    JTextField fieldMetabFromFile4 = (JTextField)cbMetabColumnNameFromFile4.getEditor().getEditorComponent();
	    fieldMetabFromFile4.addKeyListener(new ComboKeyHandler(cbMetabColumnNameFromFile4));
	    
	    JTextField fieldMetabFromFile5 = (JTextField)cbMetabColumnNameFromFile5.getEditor().getEditorComponent();
	    fieldMetabFromFile5.addKeyListener(new ComboKeyHandler(cbMetabColumnNameFromFile5));
	    
	    
	    JTextField fieldReacFromFile1 = (JTextField)cbReacColumnNameFromFile1.getEditor().getEditorComponent();
	    fieldReacFromFile1.addKeyListener(new ComboKeyHandler(cbReacColumnNameFromFile1));
	    
	    JTextField fieldReacFromFile2 = (JTextField)cbReacColumnNameFromFile2.getEditor().getEditorComponent();
	    fieldReacFromFile2.addKeyListener(new ComboKeyHandler(cbReacColumnNameFromFile2));
	    
	    JTextField fieldReacFromFile3 = (JTextField)cbReacColumnNameFromFile3.getEditor().getEditorComponent();
	    fieldReacFromFile3.addKeyListener(new ComboKeyHandler(cbReacColumnNameFromFile3));
	    
	    JTextField fieldReacFromFile4 = (JTextField)cbReacColumnNameFromFile4.getEditor().getEditorComponent();
	    fieldReacFromFile4.addKeyListener(new ComboKeyHandler(cbReacColumnNameFromFile4));
	   	  
	    JTextField fieldReacFromFile5 = (JTextField)cbReacColumnNameFromFile5.getEditor().getEditorComponent();
	    fieldReacFromFile5.addKeyListener(new ComboKeyHandler(cbReacColumnNameFromFile5));
	    
	    JTextField fieldReacFromFile6 = (JTextField)cbReacColumnNameFromFile6.getEditor().getEditorComponent();
	    fieldReacFromFile6.addKeyListener(new ComboKeyHandler(cbReacColumnNameFromFile6));
	    
	    JTextField fieldReacFromFile7 = (JTextField)cbReacColumnNameFromFile7.getEditor().getEditorComponent();
	    fieldReacFromFile7.addKeyListener(new ComboKeyHandler(cbReacColumnNameFromFile7));
	    
	    JTextField fieldReacFromFile8 = (JTextField)cbReacColumnNameFromFile8.getEditor().getEditorComponent();
	    fieldReacFromFile8.addKeyListener(new ComboKeyHandler(cbReacColumnNameFromFile8));
	    
	    JTextField fieldReacFromFile9 = (JTextField)cbReacColumnNameFromFile9.getEditor().getEditorComponent();
	    fieldReacFromFile9.addKeyListener(new ComboKeyHandler(cbReacColumnNameFromFile9));
	    
	    //box layout
	    Box vb = Box.createVerticalBox();
	    
	    Box hbMetabLabels = Box.createHorizontalBox();
	    Box hbMetab1 = Box.createHorizontalBox();
	    Box hbMetab2 = Box.createHorizontalBox();
	    Box hbMetab3 = Box.createHorizontalBox();
	    Box hbMetab4 = Box.createHorizontalBox();
	    Box hbMetab5 = Box.createHorizontalBox();
	    Box hbSeparator = Box.createHorizontalBox();
	    Box hbReacLabels = Box.createHorizontalBox();
	    Box hbReac1 = Box.createHorizontalBox();
	    Box hbReac2 = Box.createHorizontalBox();
	    Box hbReac3 = Box.createHorizontalBox();
	    Box hbReac4 = Box.createHorizontalBox();
	    Box hbReac5 = Box.createHorizontalBox();
	    Box hbReac6 = Box.createHorizontalBox();
	    Box hbReac7 = Box.createHorizontalBox();
	    Box hbReac8 = Box.createHorizontalBox();
	    Box hbReac9 = Box.createHorizontalBox();
	    
	    Box hbMetabDisplayLabel = Box.createHorizontalBox();	    
	    Box hbMetabFromFileLabel = Box.createHorizontalBox();
	    
	    Box hbMetabDisplay1 = Box.createHorizontalBox();
	    Box hbMetabFromFile1 = Box.createHorizontalBox();
	    Box hbMetabDisplay2 = Box.createHorizontalBox();
	    Box hbMetabFromFile2 = Box.createHorizontalBox();
	    Box hbMetabDisplay3 = Box.createHorizontalBox();
	    Box hbMetabFromFile3 = Box.createHorizontalBox();
	    Box hbMetabDisplay4 = Box.createHorizontalBox();
	    Box hbMetabFromFile4 = Box.createHorizontalBox();
	    Box hbMetabDisplay5 = Box.createHorizontalBox();
	    Box hbMetabFromFile5 = Box.createHorizontalBox();
	       
	    Box hbReacDisplayLabel = Box.createHorizontalBox();	    
	    Box hbReacFromFileLabel = Box.createHorizontalBox();
	    
	    Box hbReacDisplay1 = Box.createHorizontalBox();	    
	    Box hbReacFromFile1 = Box.createHorizontalBox();
	    Box hbReacDisplay2 = Box.createHorizontalBox();	    
	    Box hbReacFromFile2 = Box.createHorizontalBox();
	    Box hbReacDisplay3 = Box.createHorizontalBox();	    
	    Box hbReacFromFile3 = Box.createHorizontalBox();
	    Box hbReacDisplay4 = Box.createHorizontalBox();	    
	    Box hbReacFromFile4 = Box.createHorizontalBox();
	    Box hbReacDisplay5 = Box.createHorizontalBox();	    
	    Box hbReacFromFile5 = Box.createHorizontalBox();
	    Box hbReacDisplay6 = Box.createHorizontalBox();	    
	    Box hbReacFromFile6 = Box.createHorizontalBox();
	    Box hbReacDisplay7 = Box.createHorizontalBox();	    
	    Box hbReacFromFile7 = Box.createHorizontalBox();
	    Box hbReacDisplay8 = Box.createHorizontalBox();	    
	    Box hbReacFromFile8 = Box.createHorizontalBox();
	    Box hbReacDisplay9 = Box.createHorizontalBox();	    
	    Box hbReacFromFile9 = Box.createHorizontalBox();
	    	    
	    Box hbButton = Box.createHorizontalBox();
	    
	    //alignment not working
	    JLabel metabDisplayLabel = new JLabel();
	    metabDisplayLabel.setText(GraphicalInterfaceConstants.METABOLITES_DISPLAY_LABEL);
	    metabDisplayLabel.setSize(new Dimension(150, 20));
	    metabDisplayLabel.setBorder(BorderFactory.createEmptyBorder(10,20,0,100));
	    metabDisplayLabel.setAlignmentX(LEFT_ALIGNMENT);
	    
	    hbMetabDisplayLabel.add(metabDisplayLabel);	
	    hbMetabDisplayLabel.setAlignmentX(LEFT_ALIGNMENT);
	    
	    JLabel metabFromFileLabel = new JLabel();
	    metabFromFileLabel.setText(GraphicalInterfaceConstants.METABOLITES_FROM_FILE_LABEL);
	    metabFromFileLabel.setSize(new Dimension(150, 20));
	    metabFromFileLabel.setBorder(BorderFactory.createEmptyBorder(10,50,0,20));
	    metabFromFileLabel.setAlignmentX(RIGHT_ALIGNMENT);
	    
	    hbMetabFromFileLabel.add(metabFromFileLabel);
	    hbMetabFromFileLabel.setAlignmentX(RIGHT_ALIGNMENT);
	    
	    hbMetabLabels.add(hbMetabDisplayLabel);
	    hbMetabLabels.add(hbMetabFromFileLabel);
	    
	    
	    JPanel panelMetabDisplay1 = new JPanel();
	    panelMetabDisplay1.setLayout(new BoxLayout(panelMetabDisplay1, BoxLayout.X_AXIS));
	    panelMetabDisplay1.add(cbMetabColumnDisplayName1);
	    panelMetabDisplay1.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbMetabDisplay1.add(panelMetabDisplay1);
	    
	    JPanel panelMetabFromFile1 = new JPanel();
	    panelMetabFromFile1.setLayout(new BoxLayout(panelMetabFromFile1, BoxLayout.X_AXIS));
	    panelMetabFromFile1.add(cbMetabColumnNameFromFile1);
	    panelMetabFromFile1.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbMetabFromFile1.add(panelMetabFromFile1);
	    
	    hbMetab1.add(hbMetabDisplay1);
	    hbMetab1.add(hbMetabFromFile1);
	    
	    JPanel panelMetabDisplay2 = new JPanel();
	    panelMetabDisplay2.setLayout(new BoxLayout(panelMetabDisplay2, BoxLayout.X_AXIS));
	    panelMetabDisplay2.add(cbMetabColumnDisplayName2);
	    panelMetabDisplay2.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbMetabDisplay2.add(panelMetabDisplay2);
	    
	    JPanel panelMetabFromFile2 = new JPanel();
	    panelMetabFromFile2.setLayout(new BoxLayout(panelMetabFromFile2, BoxLayout.X_AXIS));
	    panelMetabFromFile2.add(cbMetabColumnNameFromFile2);
	    panelMetabFromFile2.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbMetabFromFile2.add(panelMetabFromFile2);
	    
	    hbMetab2.add(hbMetabDisplay2);
	    hbMetab2.add(hbMetabFromFile2);
	    
	    JPanel panelMetabDisplay3 = new JPanel();
	    panelMetabDisplay3.setLayout(new BoxLayout(panelMetabDisplay3, BoxLayout.X_AXIS));
	    panelMetabDisplay3.add(cbMetabColumnDisplayName3);
	    panelMetabDisplay3.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbMetabDisplay3.add(panelMetabDisplay3);
	    
	    JPanel panelMetabFromFile3 = new JPanel();
	    panelMetabFromFile3.setLayout(new BoxLayout(panelMetabFromFile3, BoxLayout.X_AXIS));
	    panelMetabFromFile3.add(cbMetabColumnNameFromFile3);
	    panelMetabFromFile3.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbMetabFromFile3.add(panelMetabFromFile3);
	    
	    hbMetab3.add(hbMetabDisplay3);
	    hbMetab3.add(hbMetabFromFile3);
	    
	    JPanel panelMetabDisplay4 = new JPanel();
	    panelMetabDisplay4.setLayout(new BoxLayout(panelMetabDisplay4, BoxLayout.X_AXIS));
	    panelMetabDisplay4.add(cbMetabColumnDisplayName4);
	    panelMetabDisplay4.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbMetabDisplay4.add(panelMetabDisplay4);
	    
	    JPanel panelMetabFromFile4 = new JPanel();
	    panelMetabFromFile4.setLayout(new BoxLayout(panelMetabFromFile4, BoxLayout.X_AXIS));
	    panelMetabFromFile4.add(cbMetabColumnNameFromFile4);
	    panelMetabFromFile4.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbMetabFromFile4.add(panelMetabFromFile4);
	    
	    hbMetab4.add(hbMetabDisplay4);
	    hbMetab4.add(hbMetabFromFile4);
	   	    
	    JPanel panelMetabDisplay5 = new JPanel();
	    panelMetabDisplay5.setLayout(new BoxLayout(panelMetabDisplay5, BoxLayout.X_AXIS));
	    panelMetabDisplay5.add(cbMetabColumnDisplayName5);
	    panelMetabDisplay5.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbMetabDisplay5.add(panelMetabDisplay5);
	    
	    JPanel panelMetabFromFile5 = new JPanel();
	    panelMetabFromFile5.setLayout(new BoxLayout(panelMetabFromFile5, BoxLayout.X_AXIS));
	    panelMetabFromFile5.add(cbMetabColumnNameFromFile5);
	    panelMetabFromFile5.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbMetabFromFile5.add(panelMetabFromFile5);
	    
	    hbMetab5.add(hbMetabDisplay5);
	    hbMetab5.add(hbMetabFromFile5);
	    
	    
	    JPanel separatorPane = new JPanel();
	    separatorPane.setLayout(new BoxLayout(separatorPane,
                BoxLayout.LINE_AXIS));
	    separatorPane.add(new JSeparator(JSeparator.HORIZONTAL));
	    separatorPane.setBorder(BorderFactory.createEmptyBorder(20,0,0,0));
	    hbSeparator.add(separatorPane);
	    
	    
	    //alignment not working
	    JLabel reacDisplayLabel = new JLabel();
	    reacDisplayLabel.setText(GraphicalInterfaceConstants.REACTIONS_DISPLAY_LABEL);
	    reacDisplayLabel.setSize(new Dimension(150, 20));
	    reacDisplayLabel.setBorder(BorderFactory.createEmptyBorder(0,20,0,100));
	    reacDisplayLabel.setAlignmentX(LEFT_ALIGNMENT);
	    
	    hbReacDisplayLabel.add(reacDisplayLabel);	
	    hbReacDisplayLabel.setAlignmentX(LEFT_ALIGNMENT);
	    
	    JLabel reacFromFileLabel = new JLabel();
	    reacFromFileLabel.setText(GraphicalInterfaceConstants.REACTIONS_FROM_FILE_LABEL);
	    reacFromFileLabel.setSize(new Dimension(150, 20));
	    reacFromFileLabel.setBorder(BorderFactory.createEmptyBorder(0,50,0,20));
	    reacFromFileLabel.setAlignmentX(RIGHT_ALIGNMENT);
	    
	    hbReacFromFileLabel.add(reacFromFileLabel);
	    hbReacFromFileLabel.setAlignmentX(RIGHT_ALIGNMENT);
	    
	    hbReacLabels.add(hbReacDisplayLabel);
	    hbReacLabels.add(hbReacFromFileLabel);
	    
	    
	    JPanel panelReacDisplay1 = new JPanel();
	    panelReacDisplay1.setLayout(new BoxLayout(panelReacDisplay1, BoxLayout.X_AXIS));
	    panelReacDisplay1.add(cbReacColumnDisplayName1);
	    panelReacDisplay1.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbReacDisplay1.add(panelReacDisplay1);
	    
	    JPanel panelReacFromFile1 = new JPanel();
	    panelReacFromFile1.setLayout(new BoxLayout(panelReacFromFile1, BoxLayout.X_AXIS));
	    panelReacFromFile1.add(cbReacColumnNameFromFile1);
	    panelReacFromFile1.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbReacFromFile1.add(panelReacFromFile1);
	    
	    hbReac1.add(hbReacDisplay1);
	    hbReac1.add(hbReacFromFile1);
	    
	    JPanel panelReacDisplay2 = new JPanel();
	    panelReacDisplay2.setLayout(new BoxLayout(panelReacDisplay2, BoxLayout.X_AXIS));
	    panelReacDisplay2.add(cbReacColumnDisplayName2);
	    panelReacDisplay2.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbReacDisplay2.add(panelReacDisplay2);
	    
	    JPanel panelReacFromFile2 = new JPanel();
	    panelReacFromFile2.setLayout(new BoxLayout(panelReacFromFile2, BoxLayout.X_AXIS));
	    panelReacFromFile2.add(cbReacColumnNameFromFile2);
	    panelReacFromFile2.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbReacFromFile2.add(panelReacFromFile2);
	    
	    hbReac2.add(hbReacDisplay2);
	    hbReac2.add(hbReacFromFile2);
	    
	    JPanel panelReacDisplay3 = new JPanel();
	    panelReacDisplay3.setLayout(new BoxLayout(panelReacDisplay3, BoxLayout.X_AXIS));
	    panelReacDisplay3.add(cbReacColumnDisplayName3);
	    panelReacDisplay3.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbReacDisplay3.add(panelReacDisplay3);
	    
	    JPanel panelReacFromFile3 = new JPanel();
	    panelReacFromFile3.setLayout(new BoxLayout(panelReacFromFile3, BoxLayout.X_AXIS));
	    panelReacFromFile3.add(cbReacColumnNameFromFile3);
	    panelReacFromFile3.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbReacFromFile3.add(panelReacFromFile3);
	    
	    hbReac3.add(hbReacDisplay3);
	    hbReac3.add(hbReacFromFile3);
	    
	    JPanel panelReacDisplay4 = new JPanel();
	    panelReacDisplay4.setLayout(new BoxLayout(panelReacDisplay4, BoxLayout.X_AXIS));
	    panelReacDisplay4.add(cbReacColumnDisplayName4);
	    panelReacDisplay4.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbReacDisplay4.add(panelReacDisplay4);
	    
	    JPanel panelReacFromFile4 = new JPanel();
	    panelReacFromFile4.setLayout(new BoxLayout(panelReacFromFile4, BoxLayout.X_AXIS));
	    panelReacFromFile4.add(cbReacColumnNameFromFile4);
	    panelReacFromFile4.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbReacFromFile4.add(panelReacFromFile4);
	    
	    hbReac4.add(hbReacDisplay4);
	    hbReac4.add(hbReacFromFile4);
	    
	    JPanel panelReacDisplay5 = new JPanel();
	    panelReacDisplay5.setLayout(new BoxLayout(panelReacDisplay5, BoxLayout.X_AXIS));
	    panelReacDisplay5.add(cbReacColumnDisplayName5);
	    panelReacDisplay5.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbReacDisplay5.add(panelReacDisplay5);
	    
	    JPanel panelReacFromFile5 = new JPanel();
	    panelReacFromFile5.setLayout(new BoxLayout(panelReacFromFile5, BoxLayout.X_AXIS));
	    panelReacFromFile5.add(cbReacColumnNameFromFile5);
	    panelReacFromFile5.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbReacFromFile5.add(panelReacFromFile5);
	    
	    hbReac5.add(hbReacDisplay5);
	    hbReac5.add(hbReacFromFile5);
	    
	    JPanel panelReacDisplay6 = new JPanel();
	    panelReacDisplay6.setLayout(new BoxLayout(panelReacDisplay6, BoxLayout.X_AXIS));
	    panelReacDisplay6.add(cbReacColumnDisplayName6);
	    panelReacDisplay6.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbReacDisplay6.add(panelReacDisplay6);
	    
	    JPanel panelReacFromFile6 = new JPanel();
	    panelReacFromFile6.setLayout(new BoxLayout(panelReacFromFile6, BoxLayout.X_AXIS));
	    panelReacFromFile6.add(cbReacColumnNameFromFile6);
	    panelReacFromFile6.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbReacFromFile6.add(panelReacFromFile6);
	    
	    hbReac6.add(hbReacDisplay6);
	    hbReac6.add(hbReacFromFile6);
	    
	    JPanel panelReacDisplay7 = new JPanel();
	    panelReacDisplay7.setLayout(new BoxLayout(panelReacDisplay7, BoxLayout.X_AXIS));
	    panelReacDisplay7.add(cbReacColumnDisplayName7);
	    panelReacDisplay7.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbReacDisplay7.add(panelReacDisplay7);
	    
	    JPanel panelReacFromFile7 = new JPanel();
	    panelReacFromFile7.setLayout(new BoxLayout(panelReacFromFile7, BoxLayout.X_AXIS));
	    panelReacFromFile7.add(cbReacColumnNameFromFile7);
	    panelReacFromFile7.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbReacFromFile7.add(panelReacFromFile7);
	    
	    hbReac7.add(hbReacDisplay7);
	    hbReac7.add(hbReacFromFile7);
	    
	    JPanel panelReacDisplay8 = new JPanel();
	    panelReacDisplay8.setLayout(new BoxLayout(panelReacDisplay8, BoxLayout.X_AXIS));
	    panelReacDisplay8.add(cbReacColumnDisplayName8);
	    panelReacDisplay8.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbReacDisplay8.add(panelReacDisplay8);
	    
	    JPanel panelReacFromFile8 = new JPanel();
	    panelReacFromFile8.setLayout(new BoxLayout(panelReacFromFile8, BoxLayout.X_AXIS));
	    panelReacFromFile8.add(cbReacColumnNameFromFile8);
	    panelReacFromFile8.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbReacFromFile8.add(panelReacFromFile8);
	    
	    hbReac8.add(hbReacDisplay8);
	    hbReac8.add(hbReacFromFile8);
	    
	    JPanel panelReacDisplay9 = new JPanel();
	    panelReacDisplay9.setLayout(new BoxLayout(panelReacDisplay9, BoxLayout.X_AXIS));
	    panelReacDisplay9.add(cbReacColumnDisplayName9);
	    panelReacDisplay9.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbReacDisplay9.add(panelReacDisplay9);
	    
	    JPanel panelReacFromFile9 = new JPanel();
	    panelReacFromFile9.setLayout(new BoxLayout(panelReacFromFile9, BoxLayout.X_AXIS));
	    panelReacFromFile9.add(cbReacColumnNameFromFile9);
	    panelReacFromFile9.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
	    
	    hbReacFromFile9.add(panelReacFromFile9);
	    
	    hbReac9.add(hbReacDisplay9);
	    hbReac9.add(hbReacFromFile9);
	    
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
	    
	    vb.add(hbMetabLabels);
	    vb.add(hbMetab1);
	    vb.add(hbMetab2);
	    vb.add(hbMetab3);
	    vb.add(hbMetab4);
	    vb.add(hbMetab5);
	    vb.add(hbSeparator);
	    vb.add(hbReacLabels);
	    vb.add(hbReac1);
	    vb.add(hbReac2);
	    vb.add(hbReac3);
	    vb.add(hbReac4);
	    vb.add(hbReac5);
	    vb.add(hbReac6);
	    vb.add(hbReac7);
	    vb.add(hbReac8);
	    vb.add(hbReac9);
	    vb.add(hbButton);
	    
	    add(vb);
	    
	    ActionListener actionListener = new ActionListener() {
		      public void actionPerformed(ActionEvent actionEvent) { 
		    	String metabDisplayNameSelection1 = (String) cbMetabColumnDisplayName1.getSelectedItem(); 		    	
			    //String metabNameFromFileSelection1 = (String) cbMetabColumnNameFromFile1.getSelectedItem(); 
			    String metabDisplayNameSelection2 = (String) cbMetabColumnDisplayName2.getSelectedItem(); 		    	
			    //String metabNameFromFileSelection2 = (String) cbMetabColumnNameFromFile2.getSelectedItem();
			    String metabDisplayNameSelection3 = (String) cbMetabColumnDisplayName3.getSelectedItem(); 		    	
			    //String metabNameFromFileSelection3 = (String) cbMetabColumnNameFromFile3.getSelectedItem();
			    String metabDisplayNameSelection4 = (String) cbMetabColumnDisplayName4.getSelectedItem(); 		    	
			    //String metabNameFromFileSelection4 = (String) cbMetabColumnNameFromFile4.getSelectedItem();  
			    String metabDisplayNameSelection5 = (String) cbMetabColumnDisplayName5.getSelectedItem(); 		    	
			    //String metabNameFromFileSelection5 = (String) cbMetabColumnNameFromFile5.getSelectedItem();    
			    
			    
		    	String reacDisplayNameSelection1 = (String) cbReacColumnDisplayName1.getSelectedItem(); 		    	
		    	//String reaNacmeFromFileSelection1 = (String) cbReacColumnNameFromFile1.getSelectedItem(); 
		    	String reacDisplayNameSelection2 = (String) cbReacColumnDisplayName2.getSelectedItem(); 		    	
		    	//String reaNacmeFromFileSelection2 = (String) cbReacColumnNameFromFile2.getSelectedItem();
		    	String reacDisplayNameSelection3 = (String) cbReacColumnDisplayName3.getSelectedItem(); 		    	
		    	//String reaNacmeFromFileSelection3 = (String) cbReacColumnNameFromFile3.getSelectedItem();
		    	String reacDisplayNameSelection4 = (String) cbReacColumnDisplayName4.getSelectedItem(); 		    	
		    	//String reaNacmeFromFileSelection4 = (String) cbReacColumnNameFromFile4.getSelectedItem(); 
		    	String reacDisplayNameSelection5 = (String) cbReacColumnDisplayName5.getSelectedItem(); 		    	
		    	//String reaNacmeFromFileSelection5 = (String) cbReacColumnNameFromFile5.getSelectedItem(); 
		    	String reacDisplayNameSelection6 = (String) cbReacColumnDisplayName6.getSelectedItem(); 		    	
		    	//String reaNacmeFromFileSelection6 = (String) cbReacColumnNameFromFile6.getSelectedItem();
		    	String reacDisplayNameSelection7 = (String) cbReacColumnDisplayName7.getSelectedItem(); 		    	
		    	//String reaNacmeFromFileSelection7 = (String) cbReacColumnNameFromFile7.getSelectedItem();
		    	String reacDisplayNameSelection8 = (String) cbReacColumnDisplayName8.getSelectedItem(); 		    	
		    	//String reaNacmeFromFileSelection8 = (String) cbReacColumnNameFromFile8.getSelectedItem(); 
		    	String reacDisplayNameSelection9 = (String) cbReacColumnDisplayName9.getSelectedItem(); 		    	
		    	//String reaNacmeFromFileSelection9 = (String) cbReacColumnNameFromFile9.getSelectedItem(); 
		    	
		    	LocalConfig.getInstance().setMetaboliteAbbreviationColumnName(metabDisplayNameSelection1);
		    	LocalConfig.getInstance().setMetaboliteNameColumnName(metabDisplayNameSelection2);
		    	LocalConfig.getInstance().setChargeColumnName(metabDisplayNameSelection3);
		    	LocalConfig.getInstance().setCompartmentColumnName(metabDisplayNameSelection4);
		    	//LocalConfig.getInstance().setBoundaryColumnName(metabDisplayNameSelection5);
		    	
		    	LocalConfig.getInstance().setReactionAbbreviationColumnName(reacDisplayNameSelection1);
		    	LocalConfig.getInstance().setReactionNameColumnName(reacDisplayNameSelection2);
		    	LocalConfig.getInstance().setReactionEquationColumnName(reacDisplayNameSelection3);
		    	LocalConfig.getInstance().setKnockoutColumnName(reacDisplayNameSelection4);
		    	LocalConfig.getInstance().setFluxValueColumnName(reacDisplayNameSelection5);
		    	LocalConfig.getInstance().setReversibleColumnName(reacDisplayNameSelection6);
		    	LocalConfig.getInstance().setLowerBoundColumnName(reacDisplayNameSelection7);
		    	LocalConfig.getInstance().setUpperBoundColumnName(reacDisplayNameSelection8);
		    	LocalConfig.getInstance().setBiologicalObjectiveColumnName(reacDisplayNameSelection9);
		    	
		    	if (cbMetabColumnNameFromFile1.getSelectedItem() != null) {
		    		LocalConfig.getInstance().setMetaboliteAbbreviationColumnIndex(cbMetabColumnDisplayName1.getSelectedIndex());
		    	} else {
		    		LocalConfig.getInstance().setMetaboliteAbbreviationColumnIndex(-1);
		    	}
		    	if (cbMetabColumnNameFromFile2.getSelectedItem() != null) {
		    		LocalConfig.getInstance().setMetaboliteNameColumnIndex(cbMetabColumnDisplayName2.getSelectedIndex());
		    	} else {
		    		LocalConfig.getInstance().setMetaboliteNameColumnIndex(-1);
		    	}
		    	if (cbMetabColumnNameFromFile3.getSelectedItem() != null) {
		    		LocalConfig.getInstance().setChargeColumnIndex(cbMetabColumnDisplayName3.getSelectedIndex());
		    	} else {
		    		LocalConfig.getInstance().setChargeColumnIndex(-1);
		    	}
		    	if (cbMetabColumnNameFromFile4.getSelectedItem() != null) {
		    		LocalConfig.getInstance().setCompartmentColumnIndex(cbMetabColumnDisplayName4.getSelectedIndex());
		    	} else {
		    		LocalConfig.getInstance().setCompartmentColumnIndex(-1);
		    	}
		    	if (cbMetabColumnNameFromFile5.getSelectedItem() != null) {
		    		LocalConfig.getInstance().setBoundaryColumnIndex(cbMetabColumnDisplayName5.getSelectedIndex());
		    	} else {
		    		LocalConfig.getInstance().setBoundaryColumnIndex(-1);
		    	}
		    	
		    	
		    	if (cbReacColumnNameFromFile1.getSelectedItem() != null) {
		    		LocalConfig.getInstance().setReactionAbbreviationColumnIndex(cbReacColumnDisplayName1.getSelectedIndex());
		    	} else {
		    		LocalConfig.getInstance().setReactionAbbreviationColumnIndex(-1);
		    	}
		    	if (cbReacColumnNameFromFile2.getSelectedItem() != null) {
		    		LocalConfig.getInstance().setReactionNameColumnIndex(cbReacColumnDisplayName2.getSelectedIndex());
		    	} else {
		    		LocalConfig.getInstance().setReactionNameColumnIndex(-1);
		    	}
		    	if (cbReacColumnNameFromFile3.getSelectedItem() != null) {
		    		LocalConfig.getInstance().setReactionEquationColumnIndex(cbReacColumnDisplayName3.getSelectedIndex());
		    	} else {
		    		LocalConfig.getInstance().setReactionEquationColumnIndex(-1);
		    	}
		    	if (cbReacColumnNameFromFile4.getSelectedItem() != null) {
		    		LocalConfig.getInstance().setKnockoutColumnIndex(cbReacColumnDisplayName4.getSelectedIndex());
		    	} else {
		    		LocalConfig.getInstance().setKnockoutColumnIndex(-1);
		    	}
		    	if (cbReacColumnNameFromFile5.getSelectedItem() != null) {
		    		LocalConfig.getInstance().setFluxValueColumnIndex(cbReacColumnDisplayName5.getSelectedIndex());
		    	} else {
		    		LocalConfig.getInstance().setFluxValueColumnIndex(-1);
		    	}
		    	if (cbReacColumnNameFromFile6.getSelectedItem() != null) {
		    		LocalConfig.getInstance().setReversibleColumnIndex(cbReacColumnDisplayName6.getSelectedIndex());
		    	} else {
		    		LocalConfig.getInstance().setReversibleColumnIndex(-1);
		    	}
		    	if (cbReacColumnNameFromFile7.getSelectedItem() != null) {
		    		LocalConfig.getInstance().setLowerBoundColumnIndex(cbReacColumnDisplayName7.getSelectedIndex());
		    	} else {
		    		LocalConfig.getInstance().setLowerBoundColumnIndex(-1);
		    	}
		    	if (cbReacColumnNameFromFile8.getSelectedItem() != null) {
		    		LocalConfig.getInstance().setUpperBoundColumnIndex(cbReacColumnDisplayName8.getSelectedIndex());
		    	} else {
		    		LocalConfig.getInstance().setUpperBoundColumnIndex(-1);
		    	}
		    	if (cbReacColumnNameFromFile9.getSelectedItem() != null) {
		    		LocalConfig.getInstance().setBiologicalObjectiveColumnIndex(cbReacColumnDisplayName9.getSelectedIndex());
		    	} else {
		    		LocalConfig.getInstance().setBiologicalObjectiveColumnIndex(-1);
		    	}
		      }
		};
		   
		cbMetabColumnDisplayName1.addActionListener(actionListener);		
		cbMetabColumnNameFromFile1.addActionListener(actionListener);
		cbMetabColumnDisplayName2.addActionListener(actionListener);		
		cbMetabColumnNameFromFile2.addActionListener(actionListener);
		cbMetabColumnDisplayName3.addActionListener(actionListener);		
		cbMetabColumnNameFromFile3.addActionListener(actionListener);
		cbMetabColumnDisplayName4.addActionListener(actionListener);		
		cbMetabColumnNameFromFile4.addActionListener(actionListener);
		cbMetabColumnDisplayName5.addActionListener(actionListener);		
		cbMetabColumnNameFromFile5.addActionListener(actionListener);
		
		cbReacColumnDisplayName1.addActionListener(actionListener);
		cbReacColumnNameFromFile1.addActionListener(actionListener);
		cbReacColumnDisplayName2.addActionListener(actionListener);
		cbReacColumnNameFromFile2.addActionListener(actionListener);
		cbReacColumnDisplayName3.addActionListener(actionListener);
		cbReacColumnNameFromFile3.addActionListener(actionListener);
		cbReacColumnDisplayName4.addActionListener(actionListener);
		cbReacColumnNameFromFile4.addActionListener(actionListener);
		cbReacColumnDisplayName5.addActionListener(actionListener);
		cbReacColumnNameFromFile5.addActionListener(actionListener);
		cbReacColumnDisplayName6.addActionListener(actionListener);
		cbReacColumnNameFromFile6.addActionListener(actionListener);
		cbReacColumnDisplayName7.addActionListener(actionListener);
		cbReacColumnNameFromFile7.addActionListener(actionListener);
		cbReacColumnDisplayName8.addActionListener(actionListener);
		cbReacColumnNameFromFile8.addActionListener(actionListener);
		cbReacColumnDisplayName9.addActionListener(actionListener);
		cbReacColumnNameFromFile9.addActionListener(actionListener);
		
		ActionListener okButtonActionListener = new ActionListener() {
	        public void actionPerformed(ActionEvent ae) {
	        	LocalConfig.getInstance().setMetaboliteAbbreviationColumnName((String) cbMetabColumnDisplayName1.getSelectedItem());
	        	LocalConfig.getInstance().setMetaboliteNameColumnName((String) cbMetabColumnDisplayName2.getSelectedItem());
	        	LocalConfig.getInstance().setChargeColumnName((String) cbMetabColumnDisplayName3.getSelectedItem());
	        	LocalConfig.getInstance().setCompartmentColumnName((String) cbMetabColumnDisplayName4.getSelectedItem());
	        	//LocalConfig.getInstance().setBoundaryColumnName((String) cbMetabColumnDisplayName5.getSelectedItem());
	        	
	        	LocalConfig.getInstance().setReactionAbbreviationColumnName((String) cbReacColumnDisplayName1.getSelectedItem());
	        	LocalConfig.getInstance().setReactionNameColumnName((String) cbReacColumnDisplayName2.getSelectedItem());
	        	LocalConfig.getInstance().setReactionEquationColumnName((String) cbReacColumnDisplayName3.getSelectedItem());
	        	LocalConfig.getInstance().setKnockoutColumnName((String) cbReacColumnDisplayName4.getSelectedItem());
	        	LocalConfig.getInstance().setFluxValueColumnName((String) cbReacColumnDisplayName5.getSelectedItem());
	        	LocalConfig.getInstance().setReversibleColumnName((String) cbReacColumnDisplayName6.getSelectedItem());
	        	LocalConfig.getInstance().setLowerBoundColumnName((String) cbReacColumnDisplayName7.getSelectedItem());
	        	LocalConfig.getInstance().setUpperBoundColumnName((String) cbReacColumnDisplayName8.getSelectedItem());
	        	LocalConfig.getInstance().setBiologicalObjectiveColumnName((String) cbReacColumnDisplayName9.getSelectedItem());
	        	
	        	//add metacolumn names to db
	        	MetabolitesMetaColumnManager metabolitesMetaColumnManager = new MetabolitesMetaColumnManager();
	        	ArrayList<String> metabMetaColumnNames = new ArrayList();
	        	ArrayList<Integer> metabUsedIndices = new ArrayList();
	        	ArrayList<Integer> metabMetaColumnIndexList = new ArrayList();
	        	
	        	if (getMetabColumnNamesFromFile().contains(cbMetabColumnNameFromFile1.getSelectedItem())) {
	    			LocalConfig.getInstance().setMetaboliteAbbreviationColumnIndex(getMetabColumnNamesFromFile().indexOf(cbMetabColumnNameFromFile1.getSelectedItem()));
	    			metabUsedIndices.add(getMetabColumnNamesFromFile().indexOf(cbMetabColumnNameFromFile1.getSelectedItem()));
	        	}
	    		if (getMetabColumnNamesFromFile().contains(cbMetabColumnNameFromFile2.getSelectedItem())) {
	    			LocalConfig.getInstance().setMetaboliteNameColumnIndex(getMetabColumnNamesFromFile().indexOf(cbMetabColumnNameFromFile2.getSelectedItem()));
	    			metabUsedIndices.add(getMetabColumnNamesFromFile().indexOf(cbMetabColumnNameFromFile2.getSelectedItem()));
	        	}
	    		if (getMetabColumnNamesFromFile().contains(cbMetabColumnNameFromFile3.getSelectedItem())) {
	    			LocalConfig.getInstance().setChargeColumnIndex(getMetabColumnNamesFromFile().indexOf(cbMetabColumnNameFromFile3.getSelectedItem()));
	    			metabUsedIndices.add(getMetabColumnNamesFromFile().indexOf(cbMetabColumnNameFromFile3.getSelectedItem()));
	        	}
	    		if (getMetabColumnNamesFromFile().contains(cbMetabColumnNameFromFile4.getSelectedItem())) {
	    			LocalConfig.getInstance().setCompartmentColumnIndex(getMetabColumnNamesFromFile().indexOf(cbMetabColumnNameFromFile4.getSelectedItem()));
	    			metabUsedIndices.add(getMetabColumnNamesFromFile().indexOf(cbMetabColumnNameFromFile4.getSelectedItem()));
	        	}
	    		if (getMetabColumnNamesFromFile().contains(cbMetabColumnNameFromFile5.getSelectedItem())) {
	    			LocalConfig.getInstance().setBoundaryColumnIndex(getMetabColumnNamesFromFile().indexOf(cbMetabColumnNameFromFile5.getSelectedItem()));
	    			metabUsedIndices.add(getMetabColumnNamesFromFile().indexOf(cbMetabColumnNameFromFile5.getSelectedItem()));
	        	}
	    		for (int i = 0; i < getMetabColumnNamesFromFile().size(); i++) {
	        		if (!metabUsedIndices.contains(i)) {
	        			metabMetaColumnNames.add(getMetabColumnNamesFromFile().get(i));
	        			metabMetaColumnIndexList.add(getMetabColumnNamesFromFile().indexOf(getMetabColumnNamesFromFile().get(i)));
	        		} 
	        	}
	    		
	    		DatabaseCreator creator = new DatabaseCreator();
	        	creator.createDatabase(LocalConfig.getInstance().getDatabaseName());
	        	metabolitesMetaColumnManager.addColumnNames(LocalConfig.getInstance().getDatabaseName(), metabMetaColumnNames);
	        	LocalConfig.getInstance().setMetabolitesMetaColumnIndexList(metabMetaColumnIndexList);
	    			        	
	        	ReactionsMetaColumnManager reactionsMetaColumnManager = new ReactionsMetaColumnManager();
	        	ArrayList<String> reacMetaColumnNames = new ArrayList();
	        	ArrayList<Integer> reacUsedIndices = new ArrayList();
	        	ArrayList<Integer> reacMetaColumnIndexList = new ArrayList();
	        	
	        	if (getReacColumnNamesFromFile().contains(cbReacColumnNameFromFile1.getSelectedItem())) {
	    			LocalConfig.getInstance().setReactionAbbreviationColumnIndex(getReacColumnNamesFromFile().indexOf(cbReacColumnNameFromFile1.getSelectedItem()));
	    			reacUsedIndices.add(getReacColumnNamesFromFile().indexOf(cbReacColumnNameFromFile1.getSelectedItem()));
	        	}
	    		if (getReacColumnNamesFromFile().contains(cbReacColumnNameFromFile2.getSelectedItem())) {
	    			LocalConfig.getInstance().setReactionNameColumnIndex(getReacColumnNamesFromFile().indexOf(cbReacColumnNameFromFile2.getSelectedItem()));
	    			reacUsedIndices.add(getReacColumnNamesFromFile().indexOf(cbReacColumnNameFromFile2.getSelectedItem()));
	        	}
	    		if (getReacColumnNamesFromFile().contains(cbReacColumnNameFromFile3.getSelectedItem())) {
	    			LocalConfig.getInstance().setReactionEquationColumnIndex(getReacColumnNamesFromFile().indexOf(cbReacColumnNameFromFile3.getSelectedItem()));
	    			reacUsedIndices.add(getReacColumnNamesFromFile().indexOf(cbReacColumnNameFromFile3.getSelectedItem()));
	        	}
	    		if (getReacColumnNamesFromFile().contains(cbReacColumnNameFromFile4.getSelectedItem())) {
	    			LocalConfig.getInstance().setKnockoutColumnIndex(getReacColumnNamesFromFile().indexOf(cbReacColumnNameFromFile4.getSelectedItem()));
	    			reacUsedIndices.add(getReacColumnNamesFromFile().indexOf(cbReacColumnNameFromFile4.getSelectedItem()));
	        	}
	    		if (getReacColumnNamesFromFile().contains(cbReacColumnNameFromFile5.getSelectedItem())) {
	    			LocalConfig.getInstance().setFluxValueColumnIndex(getReacColumnNamesFromFile().indexOf(cbReacColumnNameFromFile5.getSelectedItem()));
	    			reacUsedIndices.add(getReacColumnNamesFromFile().indexOf(cbReacColumnNameFromFile5.getSelectedItem()));
	        	}
	    		if (getReacColumnNamesFromFile().contains(cbReacColumnNameFromFile6.getSelectedItem())) {
	    			LocalConfig.getInstance().setReversibleColumnIndex(getReacColumnNamesFromFile().indexOf(cbReacColumnNameFromFile6.getSelectedItem()));
	    			reacUsedIndices.add(getReacColumnNamesFromFile().indexOf(cbReacColumnNameFromFile6.getSelectedItem()));
	        	}
	    		if (getReacColumnNamesFromFile().contains(cbReacColumnNameFromFile7.getSelectedItem())) {
	    			LocalConfig.getInstance().setLowerBoundColumnIndex(getReacColumnNamesFromFile().indexOf(cbReacColumnNameFromFile7.getSelectedItem()));
	    			reacUsedIndices.add(getReacColumnNamesFromFile().indexOf(cbReacColumnNameFromFile7.getSelectedItem()));
	        	}
	    		if (getReacColumnNamesFromFile().contains(cbReacColumnNameFromFile8.getSelectedItem())) {
	    			LocalConfig.getInstance().setUpperBoundColumnIndex(getReacColumnNamesFromFile().indexOf(cbReacColumnNameFromFile8.getSelectedItem()));
	    			reacUsedIndices.add(getReacColumnNamesFromFile().indexOf(cbReacColumnNameFromFile8.getSelectedItem()));
	        	}
	    		if (getReacColumnNamesFromFile().contains(cbReacColumnNameFromFile9.getSelectedItem())) {
	    			LocalConfig.getInstance().setBiologicalObjectiveColumnIndex(getReacColumnNamesFromFile().indexOf(cbReacColumnNameFromFile9.getSelectedItem()));
	    			reacUsedIndices.add(getReacColumnNamesFromFile().indexOf(cbReacColumnNameFromFile9.getSelectedItem()));
	        	}
	    		for (int i = 0; i < getReacColumnNamesFromFile().size(); i++) {
	        		if (!reacUsedIndices.contains(i)) {
	        			reacMetaColumnNames.add(getReacColumnNamesFromFile().get(i));
	        			reacMetaColumnIndexList.add(getReacColumnNamesFromFile().indexOf(getReacColumnNamesFromFile().get(i)));
	        		} 
	        	}	        	
	        	reactionsMetaColumnManager.addColumnNames(LocalConfig.getInstance().getDatabaseName(), reacMetaColumnNames);
	        	LocalConfig.getInstance().setReactionsMetaColumnIndexList(reacMetaColumnIndexList);
	        	
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
				
				Excel97Reader reader = new Excel97Reader();
				reader.load(GraphicalInterface.getExcelPath(), LocalConfig.getInstance().getDatabaseName(), LocalConfig.getInstance().getSheetNamesList());
	    		
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
		/*
		ActionListener cancelButtonActionListener = new ActionListener() {
	        public void actionPerformed(ActionEvent prodActionEvent) {
				setVisible(false);
				dispose();
				//this is a hack, creates blank reaction table
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
		 */   
	}
	
    public void populateMetabNamesFromFileBoxes(ArrayList<String> metabColumnNamesFromFile) {
		
		LocalConfig.getInstance().setCompartmentColumnIndex(-1);
		LocalConfig.getInstance().setChargeColumnIndex(-1);
		LocalConfig.getInstance().setBoundaryColumnIndex(-1);
		//add all column names to from file comboboxes
		for (int c = 0; c < metabColumnNamesFromFile.size(); c++) { 
			cbMetabColumnNameFromFile1.addItem(metabColumnNamesFromFile.get(c));
			cbMetabColumnNameFromFile2.addItem(metabColumnNamesFromFile.get(c));
			cbMetabColumnNameFromFile3.addItem(metabColumnNamesFromFile.get(c));
			cbMetabColumnNameFromFile4.addItem(metabColumnNamesFromFile.get(c));
			cbMetabColumnNameFromFile5.addItem(metabColumnNamesFromFile.get(c));
		}
		cbMetabColumnNameFromFile4.setSelectedIndex(-1);
		cbMetabColumnNameFromFile3.setSelectedIndex(-1);
		cbMetabColumnNameFromFile5.setSelectedIndex(-1);
		for (int c = 0; c < metabColumnNamesFromFile.size(); c++) { 
			//filters to match column names from file to required column names in table
			if((metabColumnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.COMPARTMENT_FILTER[0])) {
				cbMetabColumnNameFromFile4.setSelectedIndex(c);
				LocalConfig.getInstance().setCompartmentColumnIndex(c);	
			} else if((metabColumnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.CHARGE_FILTER[0]) && !(metabColumnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.CHARGE_NOT_FILTER[0])) {
				cbMetabColumnNameFromFile3.setSelectedIndex(c);
				LocalConfig.getInstance().setChargeColumnIndex(c);				
			} else if((metabColumnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.BOUNDARY_FILTER[0])) {
				cbMetabColumnNameFromFile5.setSelectedIndex(c);
				LocalConfig.getInstance().setBoundaryColumnIndex(c);				
			}  
		}
		//first two columns are recommended to be column 1 and 2: abbreviation (id), and name
		cbMetabColumnNameFromFile1.setSelectedIndex(0);
		LocalConfig.getInstance().setMetaboliteAbbreviationColumnIndex(0);
		cbMetabColumnNameFromFile2.setSelectedIndex(1);	
		LocalConfig.getInstance().setMetaboliteNameColumnIndex(1);
    }
	
    public void populateReacNamesFromFileBoxes(ArrayList<String> reacColumnNamesFromFile) {
		
		LocalConfig.getInstance().setKnockoutColumnIndex(-1);
		LocalConfig.getInstance().setFluxValueColumnIndex(-1);
		LocalConfig.getInstance().setReversibleColumnIndex(-1);
		LocalConfig.getInstance().setLowerBoundColumnIndex(-1);
		LocalConfig.getInstance().setUpperBoundColumnIndex(-1);
		LocalConfig.getInstance().setBiologicalObjectiveColumnIndex(-1);
		
		//add all column names to from file comboboxes
		for (int c = 0; c < reacColumnNamesFromFile.size(); c++) { 
			cbReacColumnNameFromFile1.addItem(reacColumnNamesFromFile.get(c));
			cbReacColumnNameFromFile2.addItem(reacColumnNamesFromFile.get(c));
			cbReacColumnNameFromFile3.addItem(reacColumnNamesFromFile.get(c));
			cbReacColumnNameFromFile4.addItem(reacColumnNamesFromFile.get(c));
			cbReacColumnNameFromFile5.addItem(reacColumnNamesFromFile.get(c));
			cbReacColumnNameFromFile6.addItem(reacColumnNamesFromFile.get(c));
			cbReacColumnNameFromFile7.addItem(reacColumnNamesFromFile.get(c));
			cbReacColumnNameFromFile8.addItem(reacColumnNamesFromFile.get(c));
			cbReacColumnNameFromFile9.addItem(reacColumnNamesFromFile.get(c));
		}		
		cbReacColumnNameFromFile4.setSelectedIndex(-1);	
		cbReacColumnNameFromFile5.setSelectedIndex(-1);
		cbReacColumnNameFromFile6.setSelectedIndex(-1);	
		cbReacColumnNameFromFile7.setSelectedIndex(-1);
		cbReacColumnNameFromFile8.setSelectedIndex(-1);
		cbReacColumnNameFromFile9.setSelectedIndex(-1);
		for (int c = 0; c < reacColumnNamesFromFile.size(); c++) { 
			//filters to match column names from file to required column names in table			
			if((reacColumnNamesFromFile.get(c).toLowerCase()).compareTo(GraphicalInterfaceConstants.KNOCKOUT_COLUMN_FILTER[0]) == 0 || (reacColumnNamesFromFile.get(c).toLowerCase()).compareTo(GraphicalInterfaceConstants.KNOCKOUT_COLUMN_FILTER[1]) == 0) {
				cbReacColumnNameFromFile4.setSelectedIndex(c);
				LocalConfig.getInstance().setKnockoutColumnIndex(c);	
			} else if((reacColumnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.FLUX_VALUE_COLUMN_FILTER[0])) {
				cbReacColumnNameFromFile5.setSelectedIndex(c);
				LocalConfig.getInstance().setFluxValueColumnIndex(c);
			} else if((reacColumnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.REVERSIBLE_COLUMN_FILTER[0])) {
				cbReacColumnNameFromFile6.setSelectedIndex(c);
				LocalConfig.getInstance().setReversibleColumnIndex(c); 
			} else if((reacColumnNamesFromFile.get(c).toLowerCase()).compareTo(GraphicalInterfaceConstants.LOWER_BOUND_FILTER[0]) == 0 || (reacColumnNamesFromFile.get(c).toLowerCase()).compareTo(GraphicalInterfaceConstants.LOWER_BOUND_FILTER[1]) == 0) {
				cbReacColumnNameFromFile7.setSelectedIndex(c);
				LocalConfig.getInstance().setLowerBoundColumnIndex(c); 
			} else if((reacColumnNamesFromFile.get(c).toLowerCase()).compareTo(GraphicalInterfaceConstants.UPPER_BOUND_FILTER[0]) == 0 || (reacColumnNamesFromFile.get(c).toLowerCase()).compareTo(GraphicalInterfaceConstants.UPPER_BOUND_FILTER[1]) == 0) {
				cbReacColumnNameFromFile8.setSelectedIndex(c);
				LocalConfig.getInstance().setUpperBoundColumnIndex(c); 
			} else if((reacColumnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_FILTER[0])) {
				cbReacColumnNameFromFile9.setSelectedIndex(c);
				LocalConfig.getInstance().setBiologicalObjectiveColumnIndex(c); 	
			}
		}
		 
		//first two columns are recommended to be column 1 and 2: abbreviation (id), and name
		cbReacColumnNameFromFile1.setSelectedIndex(0);
		LocalConfig.getInstance().setReactionAbbreviationColumnIndex(0);
		cbReacColumnNameFromFile2.setSelectedIndex(1);	
		LocalConfig.getInstance().setReactionNameColumnIndex(1);
		cbReacColumnNameFromFile3.setSelectedIndex(2);	
		LocalConfig.getInstance().setReactionEquationColumnIndex(2);
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
	    list.add("test1");
	    list.add("test2");
	    ArrayList<String> list2 = new ArrayList();
	    list2.add("test1");
	    list2.add("test2");
	    list2.add("test3");
		ExcelColumnNameInterface frame = new ExcelColumnNameInterface(con, list, list2);
		frame.setIconImages(icons);
		frame.setSize(600, 650);
	    frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	    frame.setLocationRelativeTo(null);
	    frame.setVisible(true);
	    
	  }

	}



