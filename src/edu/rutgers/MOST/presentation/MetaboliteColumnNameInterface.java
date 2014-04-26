package edu.rutgers.MOST.presentation;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.data.TextMetabolitesModelReader;

public class MetaboliteColumnNameInterface  extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public JComboBox<String> cbMetaboliteAbbreviation = new JComboBox<String>();
	public JComboBox<String> cbMetaboliteName = new JComboBox<String>();
	public JComboBox<String> cbCharge = new JComboBox<String>();
	public JComboBox<String> cbCompartment = new JComboBox<String>();
	public JComboBox<String> cbBoundary = new JComboBox<String>();

	public JButton okButton = new JButton("     OK     ");
	public JButton cancelButton = new JButton("  Cancel  ");
	public JButton prevRowButton = new JButton("Previous Row");
	public JButton nextRowButton = new JButton(" Next Row ");
	public JLabel rowLabel = new JLabel();
	
	public boolean validColumns;
	
	private static ArrayList<String> columnNamesFromFile;

	public static ArrayList<String> getColumnNamesFromFile() {
		return columnNamesFromFile;
	}

	public void setColumnNamesFromFile(ArrayList<String> columnNamesFromFile) {
		MetaboliteColumnNameInterface.columnNamesFromFile = columnNamesFromFile;
	}
	
	public MetaboliteColumnNameInterface(ArrayList<String> columnNamesFromFile) {

		prevRowButton.setEnabled(false);
		LocalConfig.getInstance().setMetabolitesNextRowCorrection(0);
		rowLabel.setText("   row " + (LocalConfig.getInstance().getMetabolitesNextRowCorrection() + 1));
		
		final ArrayList<Image> icons = new ArrayList<Image>(); 
		icons.add(new ImageIcon("etc/most16.jpg").getImage()); 
		icons.add(new ImageIcon("etc/most32.jpg").getImage());
		
		getRootPane().setDefaultButton(okButton);

		setColumnNamesFromFile(columnNamesFromFile);

		setTitle(ColumnInterfaceConstants.METABOLITES_COLUMN_NAME_INTERFACE_TITLE);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		//setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		LocalConfig.getInstance().setMetabolitesNextRowCorrection(0);

		validColumns = true;
		
		cbMetaboliteAbbreviation.setEditable(true);	
		cbMetaboliteName.setEditable(true);
		cbCharge.setEditable(true);	
		cbCompartment.setEditable(true);
		cbBoundary.setEditable(true);

		cbMetaboliteAbbreviation.setPreferredSize(new Dimension(250, 25));
		cbMetaboliteAbbreviation.setMaximumSize(new Dimension(250, 25));
		cbMetaboliteAbbreviation.setMinimumSize(new Dimension(250, 25));

		cbMetaboliteName.setPreferredSize(new Dimension(250, 25));
		cbMetaboliteName.setMaximumSize(new Dimension(250, 25));
		cbMetaboliteName.setMinimumSize(new Dimension(250, 25));

		cbCharge.setPreferredSize(new Dimension(250, 25));
		cbCharge.setMaximumSize(new Dimension(250, 25));
		cbCharge.setMinimumSize(new Dimension(250, 25));

		cbCompartment.setPreferredSize(new Dimension(250, 25));
		cbCompartment.setMaximumSize(new Dimension(250, 25));
		cbCompartment.setMinimumSize(new Dimension(250, 25));

		cbBoundary.setPreferredSize(new Dimension(250, 25));
		cbBoundary.setMaximumSize(new Dimension(250, 25));
		cbBoundary.setMinimumSize(new Dimension(250, 25));

		populateNamesFromFileBoxes(columnNamesFromFile);
		
		//box layout
		Box vb = Box.createVerticalBox();

		Box hbLabels = Box.createHorizontalBox();
		Box hbTop = Box.createHorizontalBox();	    	    
		Box hbMetaboliteAbbreviationLabel = Box.createHorizontalBox();	    
		Box hbMetaboliteAbbreviation = Box.createHorizontalBox();
		Box hbMetaboliteNameLabel = Box.createHorizontalBox();	    
		Box hbMetabolite = Box.createHorizontalBox();
		Box hbChargeLabel = Box.createHorizontalBox();	    
		Box hbCharge = Box.createHorizontalBox();
		Box hbCompartmentLabel = Box.createHorizontalBox();	    
		Box hbCompartment = Box.createHorizontalBox();
		Box hbBoundaryLabel = Box.createHorizontalBox();	    
		Box hbBoundary = Box.createHorizontalBox();
		
		Box vbLabels = Box.createVerticalBox();
		Box vbCombos = Box.createVerticalBox();

		Box hbLabeledCombos = Box.createHorizontalBox();
		Box hbRequiredLabel = Box.createHorizontalBox();
		Box hbButton = Box.createHorizontalBox();

		//top label
		JLabel topLabel = new JLabel();
		topLabel.setText(ColumnInterfaceConstants.METABOLITES_TOP_LABEL);
		topLabel.setSize(new Dimension(300, 30));
		//top, left, bottom. right
		topLabel.setBorder(BorderFactory.createEmptyBorder(20,30,20,200));
		topLabel.setAlignmentX(LEFT_ALIGNMENT);

		hbTop.add(topLabel);	
		hbTop.setAlignmentX(LEFT_ALIGNMENT);

		hbLabels.add(hbTop);

		//metabolite Abbreviation Label and combo
		JLabel metaboliteAbbreviationLabel = new JLabel();
		metaboliteAbbreviationLabel.setText(ColumnInterfaceConstants.METABOLITE_ABBREVIATION_LABEL);
		metaboliteAbbreviationLabel.setPreferredSize(new Dimension(250, 25));
		metaboliteAbbreviationLabel.setMaximumSize(new Dimension(250, 25));
		metaboliteAbbreviationLabel.setMinimumSize(new Dimension(250, 25));
		metaboliteAbbreviationLabel.setBorder(BorderFactory.createEmptyBorder(10,0,ColumnInterfaceConstants.LABEL_BOTTOM_BORDER_SIZE,10));
		metaboliteAbbreviationLabel.setAlignmentX(LEFT_ALIGNMENT);
		//metaboliteAbbreviationLabel.setAlignmentY(TOP_ALIGNMENT);	    	    

		JPanel panelMetaboliteAbbreviationLabel = new JPanel();
		panelMetaboliteAbbreviationLabel.setLayout(new BoxLayout(panelMetaboliteAbbreviationLabel, BoxLayout.X_AXIS));
		panelMetaboliteAbbreviationLabel.add(metaboliteAbbreviationLabel);
		panelMetaboliteAbbreviationLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbMetaboliteAbbreviationLabel.add(panelMetaboliteAbbreviationLabel);
		hbMetaboliteAbbreviationLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelMetaboliteAbbreviation = new JPanel();
		panelMetaboliteAbbreviation.setLayout(new BoxLayout(panelMetaboliteAbbreviation, BoxLayout.X_AXIS));
		panelMetaboliteAbbreviation.add(cbMetaboliteAbbreviation);
		panelMetaboliteAbbreviation.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		panelMetaboliteAbbreviation.setAlignmentX(RIGHT_ALIGNMENT);

		hbMetaboliteAbbreviation.add(panelMetaboliteAbbreviation);
		hbMetaboliteAbbreviation.setAlignmentX(RIGHT_ALIGNMENT);

		vbLabels.add(hbMetaboliteAbbreviationLabel);
		JLabel blankLabel1 = new JLabel("");
		vbLabels.add(blankLabel1);
		vbCombos.add(hbMetaboliteAbbreviation);

		//metabolite Name Label and combo
		JLabel metaboliteNameLabel = new JLabel();
		metaboliteNameLabel.setText(ColumnInterfaceConstants.METABOLITE_NAME_LABEL);
		metaboliteNameLabel.setPreferredSize(new Dimension(250, 25));
		metaboliteNameLabel.setMaximumSize(new Dimension(250, 25));
		metaboliteNameLabel.setMinimumSize(new Dimension(250, 25));
		metaboliteNameLabel.setBorder(BorderFactory.createEmptyBorder(ColumnInterfaceConstants.LABEL_TOP_BORDER_SIZE,0,ColumnInterfaceConstants.LABEL_BOTTOM_BORDER_SIZE,10));
		metaboliteNameLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelMetaboliteNameLabel = new JPanel();
		panelMetaboliteNameLabel.setLayout(new BoxLayout(panelMetaboliteNameLabel, BoxLayout.X_AXIS));
		panelMetaboliteNameLabel.add(metaboliteNameLabel);
		panelMetaboliteNameLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbMetaboliteNameLabel.add(panelMetaboliteNameLabel);
		hbMetaboliteNameLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelMetaboliteName = new JPanel();
		panelMetaboliteName.setLayout(new BoxLayout(panelMetaboliteName, BoxLayout.X_AXIS));
		panelMetaboliteName.add(cbMetaboliteName);
		panelMetaboliteName.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		panelMetaboliteName.setAlignmentX(RIGHT_ALIGNMENT);

		hbMetabolite.add(panelMetaboliteName);
		hbMetabolite.setAlignmentX(RIGHT_ALIGNMENT);

		vbLabels.add(hbMetaboliteNameLabel);
		JLabel blankLabel2 = new JLabel("");
		vbLabels.add(blankLabel2);
		vbCombos.add(hbMetabolite);

		//charge label and combo
		JLabel chargeLabel = new JLabel();
		chargeLabel.setText(ColumnInterfaceConstants.CHARGE_LABEL);
		chargeLabel.setPreferredSize(new Dimension(250, 25));
		chargeLabel.setMaximumSize(new Dimension(250, 25));
		chargeLabel.setMinimumSize(new Dimension(250, 25));
		chargeLabel.setBorder(BorderFactory.createEmptyBorder(ColumnInterfaceConstants.LABEL_TOP_BORDER_SIZE,0,ColumnInterfaceConstants.LABEL_BOTTOM_BORDER_SIZE,10));
		chargeLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelChargeLabel = new JPanel();
		panelChargeLabel.setLayout(new BoxLayout(panelChargeLabel, BoxLayout.X_AXIS));
		panelChargeLabel.add(chargeLabel);
		panelChargeLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbChargeLabel.add(panelChargeLabel);
		hbChargeLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelCharge = new JPanel();
		panelCharge.setLayout(new BoxLayout(panelCharge, BoxLayout.X_AXIS));
		panelCharge.add(cbCharge);
		panelCharge.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		panelCharge.setAlignmentX(RIGHT_ALIGNMENT);

		hbCharge.add(panelCharge);
		hbCharge.setAlignmentX(RIGHT_ALIGNMENT);

		vbLabels.add(hbChargeLabel);
		JLabel blankLabel3 = new JLabel("");
		vbLabels.add(blankLabel3);
		vbCombos.add(hbCharge);

		//compartment label and combo
		JLabel compartmentLabel = new JLabel();
		compartmentLabel.setText(ColumnInterfaceConstants.COMPARTMENT_LABEL);
		compartmentLabel.setPreferredSize(new Dimension(250, 25));
		compartmentLabel.setMaximumSize(new Dimension(250, 25));
		compartmentLabel.setMinimumSize(new Dimension(250, 25));
		compartmentLabel.setBorder(BorderFactory.createEmptyBorder(ColumnInterfaceConstants.LABEL_TOP_BORDER_SIZE,0,ColumnInterfaceConstants.LABEL_BOTTOM_BORDER_SIZE,10));
		compartmentLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelCompartmentLabel = new JPanel();
		panelCompartmentLabel.setLayout(new BoxLayout(panelCompartmentLabel, BoxLayout.X_AXIS));
		panelCompartmentLabel.add(compartmentLabel);
		panelCompartmentLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbCompartmentLabel.add(panelCompartmentLabel);
		hbCompartmentLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelCompartment = new JPanel();
		panelCompartment.setLayout(new BoxLayout(panelCompartment, BoxLayout.X_AXIS));
		panelCompartment.add(cbCompartment);
		panelCompartment.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		panelCompartment.setAlignmentX(RIGHT_ALIGNMENT);

		hbCompartment.add(panelCompartment);
		hbCompartment.setAlignmentX(RIGHT_ALIGNMENT);

		vbLabels.add(hbCompartmentLabel);
		JLabel blankLabel4 = new JLabel("");
		vbLabels.add(blankLabel4);
		vbCombos.add(hbCompartment);

		//boundary label and combo
		JLabel boundaryLabel = new JLabel();
		boundaryLabel.setText(ColumnInterfaceConstants.BOUNDARY_LABEL);
		boundaryLabel.setPreferredSize(new Dimension(250, 25));
		boundaryLabel.setMaximumSize(new Dimension(250, 25));
		boundaryLabel.setMinimumSize(new Dimension(250, 25));
		boundaryLabel.setBorder(BorderFactory.createEmptyBorder(0,0,0,10));
		boundaryLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelBoundaryLabel = new JPanel();
		panelBoundaryLabel.setLayout(new BoxLayout(panelBoundaryLabel, BoxLayout.X_AXIS));
		panelBoundaryLabel.add(boundaryLabel);
		panelBoundaryLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbBoundaryLabel.add(panelBoundaryLabel);
		hbBoundaryLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelBoundary = new JPanel();
		panelBoundary.setLayout(new BoxLayout(panelBoundary, BoxLayout.X_AXIS));
		panelBoundary.add(cbBoundary);
		panelBoundary.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		panelBoundary.setAlignmentX(RIGHT_ALIGNMENT);

		hbBoundary.add(panelBoundary);
		hbBoundary.setAlignmentX(RIGHT_ALIGNMENT);

		vbLabels.add(hbBoundaryLabel);
		vbCombos.add(hbBoundary);

		JLabel required = new JLabel(ColumnInterfaceConstants.REQUIRED_LABEL);
		hbRequiredLabel.add(required);
		
		okButton.setMnemonic(KeyEvent.VK_O);
		JLabel blank = new JLabel("    "); 
		cancelButton.setMnemonic(KeyEvent.VK_C);
		JLabel blank1 = new JLabel("    ");
		prevRowButton.setMnemonic(KeyEvent.VK_P);
		JLabel blank2 = new JLabel("    ");
		nextRowButton.setMnemonic(KeyEvent.VK_N);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(okButton);
		buttonPanel.add(blank);
		buttonPanel.add(cancelButton);
		buttonPanel.add(blank1);
		buttonPanel.add(prevRowButton);
		buttonPanel.add(blank2);
		buttonPanel.add(nextRowButton);
		buttonPanel.add(rowLabel);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,20,20,20));

		hbButton.add(buttonPanel);

		vb.add(hbLabels);
		hbLabeledCombos.add(vbLabels);
		hbLabeledCombos.add(vbCombos);
		vb.add(hbLabeledCombos);
		vb.add(hbRequiredLabel);
		vb.add(hbButton);

		add(vb);
		
		ActionListener okButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				
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

		ActionListener prevRowButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {				
				TextMetabolitesModelReader reader = new TextMetabolitesModelReader();
				int correction = LocalConfig.getInstance().getMetabolitesNextRowCorrection();
				if (correction > 0) {
					LocalConfig.getInstance().setMetabolitesNextRowCorrection(correction - 1);	
					rowLabel.setText("   row " + (LocalConfig.getInstance().getMetabolitesNextRowCorrection() + 1));
					ArrayList<String> newColumnNamesFromFile = reader.columnNamesFromFile(LocalConfig.getInstance().getMetabolitesCSVFile(), LocalConfig.getInstance().getMetabolitesNextRowCorrection());
					setColumnNamesFromFile(newColumnNamesFromFile);
					populateNamesFromFileBoxes(newColumnNamesFromFile);
				} else {
					prevRowButton.setEnabled(false);
				}
				nextRowButton.setEnabled(true);
			}
		};
		
		prevRowButton.addActionListener(prevRowButtonActionListener);
		
		ActionListener nextRowButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {				
				TextMetabolitesModelReader reader = new TextMetabolitesModelReader();
				int correction = LocalConfig.getInstance().getMetabolitesNextRowCorrection();
				if ((correction + 1) < reader.numberOfLines(LocalConfig.getInstance().getMetabolitesCSVFile())) {
					LocalConfig.getInstance().setMetabolitesNextRowCorrection(correction + 1);	
					rowLabel.setText("   row " + (LocalConfig.getInstance().getMetabolitesNextRowCorrection() + 1));
					ArrayList<String> newColumnNamesFromFile = reader.columnNamesFromFile(LocalConfig.getInstance().getMetabolitesCSVFile(), LocalConfig.getInstance().getMetabolitesNextRowCorrection());
					setColumnNamesFromFile(newColumnNamesFromFile);
					populateNamesFromFileBoxes(newColumnNamesFromFile);
				} else {
					nextRowButton.setEnabled(false);
				}
				prevRowButton.setEnabled(true);
			}
		};

		nextRowButton.addActionListener(nextRowButtonActionListener);

	} 	 

	public void populateNamesFromFileBoxes(ArrayList<String> columnNamesFromFile) {

		LocalConfig.getInstance().setMetaboliteAbbreviationColumnIndex(-1);
		LocalConfig.getInstance().setMetaboliteNameColumnIndex(-1);
		LocalConfig.getInstance().setCompartmentColumnIndex(-1);
		LocalConfig.getInstance().setChargeColumnIndex(-1);
		LocalConfig.getInstance().setBoundaryColumnIndex(-1);
		cbMetaboliteAbbreviation.removeAllItems();
		cbMetaboliteName.removeAllItems();
		cbCharge.removeAllItems();
		cbCompartment.removeAllItems();
		cbBoundary.removeAllItems();
		//add all column names to from file comboboxes
		for (int c = 0; c < columnNamesFromFile.size(); c++) { 
			cbMetaboliteAbbreviation.addItem(columnNamesFromFile.get(c));
			cbMetaboliteName.addItem(columnNamesFromFile.get(c));
			cbCharge.addItem(columnNamesFromFile.get(c));
			cbCompartment.addItem(columnNamesFromFile.get(c));
			cbBoundary.addItem(columnNamesFromFile.get(c));
		}
		cbCompartment.setSelectedIndex(-1);
		cbCharge.setSelectedIndex(-1);
		cbBoundary.setSelectedIndex(-1);
		for (int c = 0; c < columnNamesFromFile.size(); c++) { 
			//filters to match column names from file to required column names in table		
			if((columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.COMPARTMENT_FILTER[0])) {
				cbCompartment.setSelectedIndex(c);
				LocalConfig.getInstance().setCompartmentColumnIndex(c);	
			} else if((columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.CHARGE_FILTER[0]) && !(columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.CHARGE_NOT_FILTER[0])) {
				cbCharge.setSelectedIndex(c);
				LocalConfig.getInstance().setChargeColumnIndex(c);
			} else if((columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.BOUNDARY_FILTER[0])) {
				cbBoundary.setSelectedIndex(c);
				LocalConfig.getInstance().setBoundaryColumnIndex(c);
			}  
		}

		//first two columns are recommended to be column 1 and 2: abbreviation (id), and name
		cbMetaboliteAbbreviation.setSelectedIndex(0);
		LocalConfig.getInstance().setMetaboliteAbbreviationColumnIndex(0);
		cbMetaboliteName.setSelectedIndex(1);	
		LocalConfig.getInstance().setMetaboliteNameColumnIndex(1);
	}

	public void getColumnIndices() {
		if (cbMetaboliteAbbreviation.getSelectedIndex() == -1) {
			JOptionPane.showMessageDialog(null,                
					ColumnInterfaceConstants.BLANK_METABOLITE_FIELDS_ERROR_MESSAGE,
					ColumnInterfaceConstants.BLANK_METABOLITE_FIELDS_ERROR_TITLE,                                
					JOptionPane.ERROR_MESSAGE);
			validColumns = false;
		} else if (cbMetaboliteAbbreviation.getSelectedItem().toString().toLowerCase().equals(GraphicalInterfaceConstants.METAB_ABBREVIATION_NOT_FILTER[0]) ||
				cbMetaboliteAbbreviation.getSelectedItem().toString().toLowerCase().equals(GraphicalInterfaceConstants.METAB_ABBREVIATION_NOT_FILTER[1]) ||
				cbMetaboliteAbbreviation.getSelectedItem().toString().toLowerCase().equals(GraphicalInterfaceConstants.METAB_ABBREVIATION_NOT_FILTER[2])) {
			JOptionPane.showMessageDialog(null,                
					"Invalid name for Metabolite Abbreviation column.",
					"Column Name Error",                                
					JOptionPane.ERROR_MESSAGE);
			validColumns = false;
		} else {
			validColumns = true;
			ArrayList<String> metaColumnNames = new ArrayList<String>();
			ArrayList<Integer> usedIndices = new ArrayList<Integer>();
			ArrayList<Integer> metaColumnIndexList = new ArrayList<Integer>();

			if (getColumnNamesFromFile().contains(cbMetaboliteAbbreviation.getSelectedItem())) {
				LocalConfig.getInstance().setMetaboliteAbbreviationColumnIndex(getColumnNamesFromFile().indexOf(cbMetaboliteAbbreviation.getSelectedItem()));
				usedIndices.add(getColumnNamesFromFile().indexOf(cbMetaboliteAbbreviation.getSelectedItem()));
			}
			if (getColumnNamesFromFile().contains(cbMetaboliteName.getSelectedItem())) {
				LocalConfig.getInstance().setMetaboliteNameColumnIndex(getColumnNamesFromFile().indexOf(cbMetaboliteName.getSelectedItem()));
				usedIndices.add(getColumnNamesFromFile().indexOf(cbMetaboliteName.getSelectedItem()));
			}
			if (getColumnNamesFromFile().contains(cbCharge.getSelectedItem())) {
				LocalConfig.getInstance().setChargeColumnIndex(getColumnNamesFromFile().indexOf(cbCharge.getSelectedItem()));
				usedIndices.add(getColumnNamesFromFile().indexOf(cbCharge.getSelectedItem()));
			}
			if (getColumnNamesFromFile().contains(cbCompartment.getSelectedItem())) {
				LocalConfig.getInstance().setCompartmentColumnIndex(getColumnNamesFromFile().indexOf(cbCompartment.getSelectedItem()));
				usedIndices.add(getColumnNamesFromFile().indexOf(cbCompartment.getSelectedItem()));
			}
			if (getColumnNamesFromFile().contains(cbBoundary.getSelectedItem())) {
				LocalConfig.getInstance().setBoundaryColumnIndex(getColumnNamesFromFile().indexOf(cbBoundary.getSelectedItem()));
				usedIndices.add(getColumnNamesFromFile().indexOf(cbBoundary.getSelectedItem()));
			}
			for (int i = 0; i < getColumnNamesFromFile().size(); i++) {
				if (!usedIndices.contains(i)) {
					metaColumnNames.add(getColumnNamesFromFile().get(i));
					metaColumnIndexList.add(getColumnNamesFromFile().indexOf(getColumnNamesFromFile().get(i)));
				} 
			}
			LocalConfig.getInstance().setMetabolitesMetaColumnNames(metaColumnNames);
			LocalConfig.getInstance().setMetabolitesMetaColumnIndexList(metaColumnIndexList);
		}				
	}
	
	public static void main(String[] args) throws Exception {
		//based on code from http:stackoverflow.com/questions/6403821/how-to-add-an-image-to-a-jframe-title-bar
		final ArrayList<Image> icons = new ArrayList<Image>(); 
		icons.add(new ImageIcon("etc/most16.jpg").getImage()); 
		icons.add(new ImageIcon("etc/most32.jpg").getImage());

		ArrayList<String> list = new ArrayList<String>();
		list.add("test1");
		list.add("test2");
		list.add("test3");

		MetaboliteColumnNameInterface frame = new MetaboliteColumnNameInterface(list);

		frame.setIconImages(icons);
		frame.setSize(600, 330);
		frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}



