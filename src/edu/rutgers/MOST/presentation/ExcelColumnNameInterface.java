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
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import edu.rutgers.MOST.config.ConfigConstants;
import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.data.DatabaseCreator;
import edu.rutgers.MOST.data.Excel97Reader;
import edu.rutgers.MOST.data.MetabolitesMetaColumnManager;
import edu.rutgers.MOST.data.ReactionsMetaColumnManager;

public class ExcelColumnNameInterface  extends JDialog {

	//metabolite combos
	public JComboBox<String> cbMetaboliteAbbreviation = new JComboBox();
	public JComboBox<String> cbMetaboliteName = new JComboBox();
	public JComboBox<String> cbCharge = new JComboBox();
	public JComboBox<String> cbCompartment = new JComboBox();
	public JComboBox<String> cbBoundary = new JComboBox();
	
	//reaction combos
	public JComboBox<String> cbKnockout = new JComboBox();
	public JComboBox<String> cbFluxValue = new JComboBox();
	public JComboBox<String> cbReactionAbbreviation = new JComboBox();
	public JComboBox<String> cbReactionName = new JComboBox();
	public JComboBox<String> cbReactionEquation = new JComboBox();
	public JComboBox<String> cbReversible = new JComboBox();
	public JComboBox<String> cbLowerBound = new JComboBox();
	public JComboBox<String> cbUpperBound = new JComboBox();
	public JComboBox<String> cbObjective = new JComboBox();
	
	JButton okButton = new JButton("     OK     ");
	JButton cancelButton = new JButton("  Cancel  ");
	
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
	
    private Task task;
	
	public final ProgressBar progressBar = new ProgressBar();
	
	javax.swing.Timer t = new javax.swing.Timer(1000, new TimeListener());

	public ExcelColumnNameInterface(final Connection con, ArrayList<String> metabColumnNamesFromFile, ArrayList<String> reacColumnNamesFromFile)
        throws SQLException {

		final ArrayList<Image> icons = new ArrayList<Image>(); 
	    icons.add(new ImageIcon("etc/most16.jpg").getImage()); 
	    icons.add(new ImageIcon("etc/most32.jpg").getImage());
		
		LocalConfig.getInstance().setProgress(0);
		progressBar.pack();
		progressBar.setIconImages(icons);
		progressBar.setSize(200, 75);
	    progressBar.setTitle("Loading...");
		progressBar.setVisible(false);

		setMetabColumnNamesFromFile(metabColumnNamesFromFile);
		setReacColumnNamesFromFile(reacColumnNamesFromFile);
		
		setTitle(ColumnInterfaceConstants.EXCEL_COLUMN_NAME_INTERFACE_TITLE);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		//metabolites
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

		JTextField fieldMetaboliteAbbreviation = (JTextField)cbMetaboliteAbbreviation.getEditor().getEditorComponent();
		fieldMetaboliteAbbreviation.addKeyListener(new ComboKeyHandler(cbMetaboliteAbbreviation));

		JTextField fieldMetaboliteName = (JTextField)cbMetaboliteName.getEditor().getEditorComponent();
		fieldMetaboliteName.addKeyListener(new ComboKeyHandler(cbMetaboliteName));

		JTextField fieldCharge = (JTextField)cbCharge.getEditor().getEditorComponent();
		fieldCharge.addKeyListener(new ComboKeyHandler(cbCharge));

		JTextField fieldCompartment = (JTextField)cbCompartment.getEditor().getEditorComponent();
		fieldCompartment.addKeyListener(new ComboKeyHandler(cbCompartment));
		
		JTextField fieldBoundary = (JTextField)cbBoundary.getEditor().getEditorComponent();
		fieldBoundary.addKeyListener(new ComboKeyHandler(cbBoundary));
		
		//reactions
		cbKnockout.setEditable(true);	
		cbFluxValue.setEditable(true);
		cbReactionAbbreviation.setEditable(true);	
		cbReactionName.setEditable(true);
		cbReactionEquation.setEditable(true);
		cbReversible.setEditable(true);
		cbLowerBound.setEditable(true);
		cbUpperBound.setEditable(true);
		cbObjective.setEditable(true);
		
		cbKnockout.setPreferredSize(new Dimension(250, 25));
		cbKnockout.setMaximumSize(new Dimension(250, 25));
		cbKnockout.setMinimumSize(new Dimension(250, 25));

		cbFluxValue.setPreferredSize(new Dimension(250, 25));
		cbFluxValue.setMaximumSize(new Dimension(250, 25));
		cbFluxValue.setMinimumSize(new Dimension(250, 25));
		
		cbReactionAbbreviation.setPreferredSize(new Dimension(250, 25));
		cbReactionAbbreviation.setMaximumSize(new Dimension(250, 25));
		cbReactionAbbreviation.setMinimumSize(new Dimension(250, 25));

		cbReactionName.setPreferredSize(new Dimension(250, 25));
		cbReactionName.setMaximumSize(new Dimension(250, 25));
		cbReactionName.setMinimumSize(new Dimension(250, 25));
		
		cbReactionEquation.setPreferredSize(new Dimension(250, 25));
		cbReactionEquation.setMaximumSize(new Dimension(250, 25));
		cbReactionEquation.setMinimumSize(new Dimension(250, 25));
		
		cbReversible.setPreferredSize(new Dimension(250, 25));
		cbReversible.setMaximumSize(new Dimension(250, 25));
		cbReversible.setMinimumSize(new Dimension(250, 25));
		
		cbLowerBound.setPreferredSize(new Dimension(250, 25));
		cbLowerBound.setMaximumSize(new Dimension(250, 25));
		cbLowerBound.setMinimumSize(new Dimension(250, 25));
		
		cbUpperBound.setPreferredSize(new Dimension(250, 25));
		cbUpperBound.setMaximumSize(new Dimension(250, 25));
		cbUpperBound.setMinimumSize(new Dimension(250, 25));

		cbObjective.setPreferredSize(new Dimension(250, 25));
		cbObjective.setMaximumSize(new Dimension(250, 25));
		cbObjective.setMinimumSize(new Dimension(250, 25));
		
		JTextField fieldKnockout = (JTextField)cbKnockout.getEditor().getEditorComponent();
		fieldKnockout.addKeyListener(new ComboKeyHandler(cbKnockout));

		JTextField fieldFluxValue = (JTextField)cbFluxValue.getEditor().getEditorComponent();
		fieldFluxValue.addKeyListener(new ComboKeyHandler(cbFluxValue));

		JTextField fieldReactionAbbreviation = (JTextField)cbReactionAbbreviation.getEditor().getEditorComponent();
		fieldReactionAbbreviation.addKeyListener(new ComboKeyHandler(cbReactionAbbreviation));

		JTextField fieldReactionName = (JTextField)cbReactionName.getEditor().getEditorComponent();
		fieldReactionName.addKeyListener(new ComboKeyHandler(cbReactionName));
		
		JTextField fieldReactionEquation = (JTextField)cbReactionEquation.getEditor().getEditorComponent();
		fieldReactionEquation.addKeyListener(new ComboKeyHandler(cbReactionEquation));
		
		JTextField fieldReversible = (JTextField)cbReversible.getEditor().getEditorComponent();
		fieldReversible.addKeyListener(new ComboKeyHandler(cbReversible));
		
		JTextField fieldLowerBound = (JTextField)cbLowerBound.getEditor().getEditorComponent();
		fieldLowerBound.addKeyListener(new ComboKeyHandler(cbLowerBound));
		
		JTextField fieldUpperBound = (JTextField)cbUpperBound.getEditor().getEditorComponent();
		fieldUpperBound.addKeyListener(new ComboKeyHandler(cbUpperBound));
		
		JTextField fieldObjective = (JTextField)cbObjective.getEditor().getEditorComponent();
		fieldObjective.addKeyListener(new ComboKeyHandler(cbObjective));
		
		populateMetabNamesFromFileBoxes(metabColumnNamesFromFile);
	    populateReacNamesFromFileBoxes(reacColumnNamesFromFile);
		
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

		Box hbMetabTopLabels = Box.createHorizontalBox();	    	    
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
		Box hbReacTopLabels = Box.createHorizontalBox();
		Box hbKnockoutLabel = Box.createHorizontalBox();	    
		Box hbKnockout = Box.createHorizontalBox();
		Box hbFluxValueLabel = Box.createHorizontalBox();	    
		Box hbFluxValue = Box.createHorizontalBox();
		Box hbReactionAbbreviationLabel = Box.createHorizontalBox();	    
		Box hbReactionAbbreviation = Box.createHorizontalBox();
		Box hbReactionNameLabel = Box.createHorizontalBox();	    
		Box hbReactionName = Box.createHorizontalBox();
		Box hbReactionEquationLabel = Box.createHorizontalBox();	    
		Box hbReactionEquation = Box.createHorizontalBox();
		Box hbReversibleLabel = Box.createHorizontalBox();	    
		Box hbReversible = Box.createHorizontalBox();
		Box hbLowerBoundLabel = Box.createHorizontalBox();	    
		Box hbLowerBound = Box.createHorizontalBox();
		Box hbUpperBoundLabel = Box.createHorizontalBox();	    
		Box hbUpperBound = Box.createHorizontalBox();
		Box hbObjectiveLabel = Box.createHorizontalBox();	    
		Box hbObjective = Box.createHorizontalBox();

		Box hbButton = Box.createHorizontalBox();

		//metabolite top label
		JLabel metaboliteTopLabel = new JLabel();
		metaboliteTopLabel.setText(ColumnInterfaceConstants.EXCEL_METABOLITES_TOP_LABEL);
		metaboliteTopLabel.setSize(new Dimension(300, 20));
		//top, left, bottom. right
		metaboliteTopLabel.setBorder(BorderFactory.createEmptyBorder(10,30,20,200));
		metaboliteTopLabel.setAlignmentX(LEFT_ALIGNMENT);

		hbMetabTopLabels.add(metaboliteTopLabel);	
		hbMetabTopLabels.setAlignmentX(LEFT_ALIGNMENT);

		hbMetabLabels.add(hbMetabTopLabels);
		
		//metabolite Abbreviation Label and combo
		JLabel metaboliteAbbreviationLabel = new JLabel();
		metaboliteAbbreviationLabel.setText(ColumnInterfaceConstants.METABOLITE_ABBREVIATION_LABEL);
		metaboliteAbbreviationLabel.setSize(new Dimension(250, 25));
		metaboliteAbbreviationLabel.setMinimumSize(new Dimension(250, 25));
		metaboliteAbbreviationLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,80));
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

		hbMetab1.add(hbMetaboliteAbbreviationLabel);
		hbMetab1.add(hbMetaboliteAbbreviation);

		//metabolite Name Label and combo
		JLabel metaboliteNameLabel = new JLabel();
		metaboliteNameLabel.setText(ColumnInterfaceConstants.METABOLITE_NAME_LABEL);
		metaboliteNameLabel.setSize(new Dimension(250, 25));
		metaboliteNameLabel.setMinimumSize(new Dimension(250, 25));
		metaboliteNameLabel.setBorder(BorderFactory.createEmptyBorder(0,0,0,80));
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

		hbMetab2.add(hbMetaboliteNameLabel);
		hbMetab2.add(hbMetabolite);

		//charge label and combo
		JLabel chargeLabel = new JLabel();
		chargeLabel.setText(ColumnInterfaceConstants.CHARGE_LABEL);
		chargeLabel.setSize(new Dimension(250, 25));	
		chargeLabel.setMinimumSize(new Dimension(250, 25));
		chargeLabel.setBorder(BorderFactory.createEmptyBorder(0,0,0,80));
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

		hbMetab3.add(hbChargeLabel);
		hbMetab3.add(hbCharge);
		
		//compartment label and combo
		JLabel compartmentLabel = new JLabel();
		compartmentLabel.setText(ColumnInterfaceConstants.COMPARTMENT_LABEL);
		compartmentLabel.setSize(new Dimension(250, 25));
		compartmentLabel.setMinimumSize(new Dimension(250, 25));
		compartmentLabel.setBorder(BorderFactory.createEmptyBorder(0,0,0,80));
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

		hbMetab4.add(hbCompartmentLabel);
		hbMetab4.add(hbCompartment);
		
		//boundary label and combo
		JLabel boundaryLabel = new JLabel();
		boundaryLabel.setText(ColumnInterfaceConstants.BOUNDARY_LABEL);
		boundaryLabel.setSize(new Dimension(250, 25));
		boundaryLabel.setMinimumSize(new Dimension(250, 25));
		boundaryLabel.setBorder(BorderFactory.createEmptyBorder(0,0,0,80));
		boundaryLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelBoundaryLabel = new JPanel();
		panelBoundaryLabel.setLayout(new BoxLayout(panelBoundaryLabel, BoxLayout.X_AXIS));
		panelBoundaryLabel.add(boundaryLabel);
		panelBoundaryLabel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));

		hbBoundaryLabel.add(panelBoundaryLabel);
		hbBoundaryLabel.setAlignmentX(LEFT_ALIGNMENT);
		
		JPanel panelBoundary = new JPanel();
		panelBoundary.setLayout(new BoxLayout(panelBoundary, BoxLayout.X_AXIS));
		panelBoundary.add(cbBoundary);
		panelBoundary.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		panelBoundary.setAlignmentX(RIGHT_ALIGNMENT);

		hbBoundary.add(panelBoundary);
		hbBoundary.setAlignmentX(RIGHT_ALIGNMENT);

		hbMetab5.add(hbBoundaryLabel);
		hbMetab5.add(hbBoundary);
		
		JPanel separatorPane = new JPanel();
	    separatorPane.setLayout(new BoxLayout(separatorPane,
                BoxLayout.LINE_AXIS));
	    separatorPane.add(new JSeparator(JSeparator.HORIZONTAL));
	    separatorPane.setBorder(BorderFactory.createEmptyBorder(20,0,0,0));
	    hbSeparator.add(separatorPane);
		
	    //reaction top label
		JLabel reactionTopLabel = new JLabel();
		reactionTopLabel.setText(ColumnInterfaceConstants.EXCEL_REACTIONS_TOP_LABEL);
		reactionTopLabel.setSize(new Dimension(300, 25));
		//top, left, bottom. right
		reactionTopLabel.setBorder(BorderFactory.createEmptyBorder(20,30,20,200));
		reactionTopLabel.setAlignmentX(LEFT_ALIGNMENT);

		hbReacTopLabels.add(reactionTopLabel);	
		hbReacTopLabels.setAlignmentX(LEFT_ALIGNMENT);

		hbReacLabels.add(hbReacTopLabels);
	    
		//knockout Label and combo
		JLabel knockoutLabel = new JLabel();
		knockoutLabel.setText(ColumnInterfaceConstants.KO_LABEL);
		knockoutLabel.setSize(new Dimension(250, 25));
		knockoutLabel.setMinimumSize(new Dimension(250, 25));
		knockoutLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,80));
		knockoutLabel.setAlignmentX(LEFT_ALIGNMENT);
		//knockoutLabel.setAlignmentY(TOP_ALIGNMENT);	    	    

		JPanel panelKnockoutLabel = new JPanel();
		panelKnockoutLabel.setLayout(new BoxLayout(panelKnockoutLabel, BoxLayout.X_AXIS));
		panelKnockoutLabel.add(knockoutLabel);
		panelKnockoutLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbKnockoutLabel.add(panelKnockoutLabel);
		hbKnockoutLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelKnockout = new JPanel();
		panelKnockout.setLayout(new BoxLayout(panelKnockout, BoxLayout.X_AXIS));
		panelKnockout.add(cbKnockout);
		panelKnockout.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		panelKnockout.setAlignmentX(RIGHT_ALIGNMENT);

		hbKnockout.add(panelKnockout);
		hbKnockout.setAlignmentX(RIGHT_ALIGNMENT);

		hbReac1.add(hbKnockoutLabel);
		hbReac1.add(hbKnockout);

		//flux value label and combo
		JLabel fluxValueLabel = new JLabel();
		fluxValueLabel.setText(ColumnInterfaceConstants.FLUX_VALUE_LABEL);
		fluxValueLabel.setSize(new Dimension(250, 25));	 
		fluxValueLabel.setMinimumSize(new Dimension(250, 25));
		fluxValueLabel.setBorder(BorderFactory.createEmptyBorder(0,0,0,80));
		fluxValueLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelFluxValueLabel = new JPanel();
		panelFluxValueLabel.setLayout(new BoxLayout(panelFluxValueLabel, BoxLayout.X_AXIS));
		panelFluxValueLabel.add(fluxValueLabel);
		panelFluxValueLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbFluxValueLabel.add(panelFluxValueLabel);
		hbFluxValueLabel.setAlignmentX(LEFT_ALIGNMENT);
		
		JPanel panelFluxValue = new JPanel();
		panelFluxValue.setLayout(new BoxLayout(panelFluxValue, BoxLayout.X_AXIS));
		panelFluxValue.add(cbFluxValue);
		panelFluxValue.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		panelFluxValue.setAlignmentX(RIGHT_ALIGNMENT);

		hbFluxValue.add(panelFluxValue);
		hbFluxValue.setAlignmentX(RIGHT_ALIGNMENT);

		hbReac2.add(hbFluxValueLabel);
		hbReac2.add(hbFluxValue);

		//reaction abbreviation label and combo
		JLabel reactionAbbreviationLabel = new JLabel();
		reactionAbbreviationLabel.setText(ColumnInterfaceConstants.REACTION_ABBREVIATION_LABEL);
		reactionAbbreviationLabel.setSize(new Dimension(250, 25));	 
		reactionAbbreviationLabel.setMinimumSize(new Dimension(250, 25));
		reactionAbbreviationLabel.setBorder(BorderFactory.createEmptyBorder(0,0,0,80));
		reactionAbbreviationLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelReactionAbbreviationLabel = new JPanel();
		panelReactionAbbreviationLabel.setLayout(new BoxLayout(panelReactionAbbreviationLabel, BoxLayout.X_AXIS));
		panelReactionAbbreviationLabel.add(reactionAbbreviationLabel);
		panelReactionAbbreviationLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbReactionAbbreviationLabel.add(panelReactionAbbreviationLabel);
		hbReactionAbbreviationLabel.setAlignmentX(LEFT_ALIGNMENT);
		
		JPanel panelReactionAbbreviation = new JPanel();
		panelReactionAbbreviation.setLayout(new BoxLayout(panelReactionAbbreviation, BoxLayout.X_AXIS));
		panelReactionAbbreviation.add(cbReactionAbbreviation);
		panelReactionAbbreviation.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		panelReactionAbbreviation.setAlignmentX(RIGHT_ALIGNMENT);

		hbReactionAbbreviation.add(panelReactionAbbreviation);
		hbReactionAbbreviation.setAlignmentX(RIGHT_ALIGNMENT);
		
		hbReac3.add(hbReactionAbbreviationLabel);
		hbReac3.add(hbReactionAbbreviation);
		
		//reaction name label and combo
		JLabel reactionNameLabel = new JLabel();
		reactionNameLabel.setText(ColumnInterfaceConstants.REACTION_NAME_LABEL);
		reactionNameLabel.setSize(new Dimension(250, 25));	
		reactionNameLabel.setMinimumSize(new Dimension(250, 25));
		reactionNameLabel.setBorder(BorderFactory.createEmptyBorder(0,0,0,80));
		reactionNameLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelReactionNameLabel = new JPanel();
		panelReactionNameLabel.setLayout(new BoxLayout(panelReactionNameLabel, BoxLayout.X_AXIS));
		panelReactionNameLabel.add(reactionNameLabel);
		panelReactionNameLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbReactionNameLabel.add(panelReactionNameLabel);
		hbReactionNameLabel.setAlignmentX(LEFT_ALIGNMENT);
		
		JPanel panelReactionName = new JPanel();
		panelReactionName.setLayout(new BoxLayout(panelReactionName, BoxLayout.X_AXIS));
		panelReactionName.add(cbReactionName);
		panelReactionName.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		panelReactionName.setAlignmentX(RIGHT_ALIGNMENT);

		hbReactionName.add(panelReactionName);
		hbReactionName.setAlignmentX(RIGHT_ALIGNMENT);

		hbReac4.add(hbReactionNameLabel);
		hbReac4.add(hbReactionName);
		
		//reaction equation label and combo
		JLabel reactionEquationLabel = new JLabel();
		reactionEquationLabel.setText(ColumnInterfaceConstants.REACTION_EQUATION_LABEL);
		reactionEquationLabel.setSize(new Dimension(250, 25));	
		reactionEquationLabel.setMinimumSize(new Dimension(250, 25));
		reactionEquationLabel.setBorder(BorderFactory.createEmptyBorder(0,0,0,80));
		reactionEquationLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelReactionEquationLabel = new JPanel();
		panelReactionEquationLabel.setLayout(new BoxLayout(panelReactionEquationLabel, BoxLayout.X_AXIS));
		panelReactionEquationLabel.add(reactionEquationLabel);
		panelReactionEquationLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbReactionEquationLabel.add(panelReactionEquationLabel);
		hbReactionEquationLabel.setAlignmentX(LEFT_ALIGNMENT);
		
		JPanel panelReactionEquation = new JPanel();
		panelReactionEquation.setLayout(new BoxLayout(panelReactionEquation, BoxLayout.X_AXIS));
		panelReactionEquation.add(cbReactionEquation);
		panelReactionEquation.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		panelReactionEquation.setAlignmentX(RIGHT_ALIGNMENT);

		hbReactionEquation.add(panelReactionEquation);
		hbReactionEquation.setAlignmentX(RIGHT_ALIGNMENT);

		hbReac5.add(hbReactionEquationLabel);
		hbReac5.add(hbReactionEquation);
		
		//reversible label and combo
		JLabel reversibleLabel = new JLabel();
		reversibleLabel.setText(ColumnInterfaceConstants.REVERSIBLE_LABEL);
		reversibleLabel.setSize(new Dimension(250, 25));
		reversibleLabel.setMinimumSize(new Dimension(250, 25));
		reversibleLabel.setBorder(BorderFactory.createEmptyBorder(0,0,0,80));
		reversibleLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelReversibleLabel = new JPanel();
		panelReversibleLabel.setLayout(new BoxLayout(panelReversibleLabel, BoxLayout.X_AXIS));
		panelReversibleLabel.add(reversibleLabel);
		panelReversibleLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbReversibleLabel.add(panelReversibleLabel);
		hbReversibleLabel.setAlignmentX(LEFT_ALIGNMENT);
		
		JPanel panelReversible = new JPanel();
		panelReversible.setLayout(new BoxLayout(panelReversible, BoxLayout.X_AXIS));
		panelReversible.add(cbReversible);
		panelReversible.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		panelReversible.setAlignmentX(RIGHT_ALIGNMENT);

		hbReversible.add(panelReversible);
		hbReversible.setAlignmentX(RIGHT_ALIGNMENT);

		hbReac6.add(hbReversibleLabel);
		hbReac6.add(hbReversible);
		
		//lower bound label and combo
		JLabel lowerBoundLabel = new JLabel();
		lowerBoundLabel.setText(ColumnInterfaceConstants.LOWER_BOUND_LABEL);
		lowerBoundLabel.setSize(new Dimension(250, 25));	
		lowerBoundLabel.setMinimumSize(new Dimension(250, 25));
		lowerBoundLabel.setBorder(BorderFactory.createEmptyBorder(0,0,0,80));
		lowerBoundLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelLowerBoundLabel = new JPanel();
		panelLowerBoundLabel.setLayout(new BoxLayout(panelLowerBoundLabel, BoxLayout.X_AXIS));
		panelLowerBoundLabel.add(lowerBoundLabel);
		panelLowerBoundLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbLowerBoundLabel.add(panelLowerBoundLabel);
		hbLowerBoundLabel.setAlignmentX(LEFT_ALIGNMENT);
		
		JPanel panelLowerBound = new JPanel();
		panelLowerBound.setLayout(new BoxLayout(panelLowerBound, BoxLayout.X_AXIS));
		panelLowerBound.add(cbLowerBound);
		panelLowerBound.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		panelLowerBound.setAlignmentX(RIGHT_ALIGNMENT);

		hbLowerBound.add(panelLowerBound);
		hbLowerBound.setAlignmentX(RIGHT_ALIGNMENT);

		hbReac7.add(hbLowerBoundLabel);
		hbReac7.add(hbLowerBound);
		
		//upper bound label and combo
		JLabel upperBoundLabel = new JLabel();
		upperBoundLabel.setText(ColumnInterfaceConstants.UPPER_BOUND_LABEL);
		upperBoundLabel.setSize(new Dimension(250, 25));	
		upperBoundLabel.setMinimumSize(new Dimension(250, 25));
		upperBoundLabel.setBorder(BorderFactory.createEmptyBorder(0,0,0,80));
		upperBoundLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelUpperBoundLabel = new JPanel();
		panelUpperBoundLabel.setLayout(new BoxLayout(panelUpperBoundLabel, BoxLayout.X_AXIS));
		panelUpperBoundLabel.add(upperBoundLabel);
		panelUpperBoundLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbUpperBoundLabel.add(panelUpperBoundLabel);
		hbUpperBoundLabel.setAlignmentX(LEFT_ALIGNMENT);
		
		JPanel panelUpperBound = new JPanel();
		panelUpperBound.setLayout(new BoxLayout(panelUpperBound, BoxLayout.X_AXIS));
		panelUpperBound.add(cbUpperBound);
		panelUpperBound.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		panelUpperBound.setAlignmentX(RIGHT_ALIGNMENT);

		hbUpperBound.add(panelUpperBound);
		hbUpperBound.setAlignmentX(RIGHT_ALIGNMENT);

		hbReac8.add(hbUpperBoundLabel);
		hbReac8.add(hbUpperBound);
		
		//objective label and combo
		JLabel objectiveLabel = new JLabel();
		objectiveLabel.setText(ColumnInterfaceConstants.BIOLOGICAL_OBJECTIVE_LABEL);
		objectiveLabel.setSize(new Dimension(250, 25));	
		objectiveLabel.setMinimumSize(new Dimension(250, 25));
		objectiveLabel.setBorder(BorderFactory.createEmptyBorder(0,0,0,80));
		objectiveLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelObjectiveLabel = new JPanel();
		panelObjectiveLabel.setLayout(new BoxLayout(panelObjectiveLabel, BoxLayout.X_AXIS));
		panelObjectiveLabel.add(objectiveLabel);
		panelObjectiveLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbObjectiveLabel.add(panelObjectiveLabel);
		hbObjectiveLabel.setAlignmentX(LEFT_ALIGNMENT);
		
		JPanel panelObjective = new JPanel();
		panelObjective.setLayout(new BoxLayout(panelObjective, BoxLayout.X_AXIS));
		panelObjective.add(cbObjective);
		panelObjective.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		panelObjective.setAlignmentX(RIGHT_ALIGNMENT);

		hbObjective.add(panelObjective);
		hbObjective.setAlignmentX(RIGHT_ALIGNMENT);

		hbReac9.add(hbObjectiveLabel);
		hbReac9.add(hbObjective);
		
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

		ActionListener okButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				//add metacolumn names to db
	        	MetabolitesMetaColumnManager metabolitesMetaColumnManager = new MetabolitesMetaColumnManager();
	        	ArrayList<String> metaColumnNames = new ArrayList();
	        	ArrayList<Integer> usedIndices = new ArrayList();
	        	ArrayList<Integer> metaColumnIndexList = new ArrayList();
	        	
	    		if (getMetabColumnNamesFromFile().contains(cbMetaboliteAbbreviation.getSelectedItem())) {
	    			LocalConfig.getInstance().setMetaboliteAbbreviationColumnIndex(getMetabColumnNamesFromFile().indexOf(cbMetaboliteAbbreviation.getSelectedItem()));
	    			usedIndices.add(getMetabColumnNamesFromFile().indexOf(cbMetaboliteAbbreviation.getSelectedItem()));
	        	}
	    		if (getMetabColumnNamesFromFile().contains(cbMetaboliteName.getSelectedItem())) {
	    			LocalConfig.getInstance().setMetaboliteNameColumnIndex(getMetabColumnNamesFromFile().indexOf(cbMetaboliteName.getSelectedItem()));
	    			usedIndices.add(getMetabColumnNamesFromFile().indexOf(cbMetaboliteName.getSelectedItem()));
	        	}
	    		if (getMetabColumnNamesFromFile().contains(cbCharge.getSelectedItem())) {
	    			LocalConfig.getInstance().setChargeColumnIndex(getMetabColumnNamesFromFile().indexOf(cbCharge.getSelectedItem()));
	    			usedIndices.add(getMetabColumnNamesFromFile().indexOf(cbCharge.getSelectedItem()));
	        	}
	    		if (getMetabColumnNamesFromFile().contains(cbCompartment.getSelectedItem())) {
	    			LocalConfig.getInstance().setCompartmentColumnIndex(getMetabColumnNamesFromFile().indexOf(cbCompartment.getSelectedItem()));
	    			usedIndices.add(getMetabColumnNamesFromFile().indexOf(cbCompartment.getSelectedItem()));
	        	}
	    		if (getMetabColumnNamesFromFile().contains(cbBoundary.getSelectedItem())) {
	    			LocalConfig.getInstance().setBoundaryColumnIndex(getMetabColumnNamesFromFile().indexOf(cbBoundary.getSelectedItem()));
	    			usedIndices.add(getMetabColumnNamesFromFile().indexOf(cbBoundary.getSelectedItem()));
	        	}
	        	for (int i = 0; i < getMetabColumnNamesFromFile().size(); i++) {
	        		if (!usedIndices.contains(i)) {
	        			metaColumnNames.add(getMetabColumnNamesFromFile().get(i));
	        			metaColumnIndexList.add(getMetabColumnNamesFromFile().indexOf(getMetabColumnNamesFromFile().get(i)));
	        		} 
	        	}
	        	DatabaseCreator creator = new DatabaseCreator();
	        	creator.createDatabase(LocalConfig.getInstance().getDatabaseName());
	        	metabolitesMetaColumnManager.addColumnNames(LocalConfig.getInstance().getDatabaseName(), metaColumnNames);
	        	LocalConfig.getInstance().setMetabolitesMetaColumnIndexList(metaColumnIndexList);
	        	
	        	ReactionsMetaColumnManager reactionsMetaColumnManager = new ReactionsMetaColumnManager();
	        	ArrayList<String> reacMetaColumnNames = new ArrayList();
	        	ArrayList<Integer> reacUsedIndices = new ArrayList();
	        	ArrayList<Integer> reacMetaColumnIndexList = new ArrayList();
	        	
	        	if (getReacColumnNamesFromFile().contains(cbKnockout.getSelectedItem())) {
	    			LocalConfig.getInstance().setKnockoutColumnIndex(getReacColumnNamesFromFile().indexOf(cbKnockout.getSelectedItem()));
	    			reacUsedIndices.add(getReacColumnNamesFromFile().indexOf(cbKnockout.getSelectedItem()));
	        	}
	    		if (getReacColumnNamesFromFile().contains(cbFluxValue.getSelectedItem())) {
	    			LocalConfig.getInstance().setFluxValueColumnIndex(getReacColumnNamesFromFile().indexOf(cbFluxValue.getSelectedItem()));
	    			reacUsedIndices.add(getReacColumnNamesFromFile().indexOf(cbFluxValue.getSelectedItem()));
	        	}
	    		if (getReacColumnNamesFromFile().contains(cbReactionAbbreviation.getSelectedItem())) {
	    			LocalConfig.getInstance().setReactionAbbreviationColumnIndex(getReacColumnNamesFromFile().indexOf(cbReactionAbbreviation.getSelectedItem()));
	    			reacUsedIndices.add(getReacColumnNamesFromFile().indexOf(cbReactionAbbreviation.getSelectedItem()));
	        	}
	    		if (getReacColumnNamesFromFile().contains(cbReactionName.getSelectedItem())) {
	    			LocalConfig.getInstance().setReactionNameColumnIndex(getReacColumnNamesFromFile().indexOf(cbReactionName.getSelectedItem()));
	    			reacUsedIndices.add(getReacColumnNamesFromFile().indexOf(cbReactionName.getSelectedItem()));
	        	}
	    		if (getReacColumnNamesFromFile().contains(cbReactionEquation.getSelectedItem())) {
	    			LocalConfig.getInstance().setReactionEquationColumnIndex(getReacColumnNamesFromFile().indexOf(cbReactionEquation.getSelectedItem()));
	    			reacUsedIndices.add(getReacColumnNamesFromFile().indexOf(cbReactionEquation.getSelectedItem()));
	        	}
	    		if (getReacColumnNamesFromFile().contains(cbReversible.getSelectedItem())) {
	    			LocalConfig.getInstance().setReversibleColumnIndex(getReacColumnNamesFromFile().indexOf(cbReversible.getSelectedItem()));
	    			reacUsedIndices.add(getReacColumnNamesFromFile().indexOf(cbReversible.getSelectedItem()));
	        	}
	    		if (getReacColumnNamesFromFile().contains(cbLowerBound.getSelectedItem())) {
	    			LocalConfig.getInstance().setLowerBoundColumnIndex(getReacColumnNamesFromFile().indexOf(cbLowerBound.getSelectedItem()));
	    			reacUsedIndices.add(getReacColumnNamesFromFile().indexOf(cbLowerBound.getSelectedItem()));
	        	}
	    		if (getReacColumnNamesFromFile().contains(cbUpperBound.getSelectedItem())) {
	    			LocalConfig.getInstance().setUpperBoundColumnIndex(getReacColumnNamesFromFile().indexOf(cbUpperBound.getSelectedItem()));
	    			reacUsedIndices.add(getReacColumnNamesFromFile().indexOf(cbUpperBound.getSelectedItem()));
	        	}
	    		if (getReacColumnNamesFromFile().contains(cbObjective.getSelectedItem())) {
	    			LocalConfig.getInstance().setBiologicalObjectiveColumnIndex(getReacColumnNamesFromFile().indexOf(cbObjective.getSelectedItem()));
	    			reacUsedIndices.add(getReacColumnNamesFromFile().indexOf(cbObjective.getSelectedItem()));
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
				
                progressBar.setVisible(true);
			    
			    t.start();

				task = new Task();
				task.execute();
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

    public void populateMetabNamesFromFileBoxes(ArrayList<String> columnNamesFromFile) {
		
		LocalConfig.getInstance().setCompartmentColumnIndex(-1);
		LocalConfig.getInstance().setChargeColumnIndex(-1);
		LocalConfig.getInstance().setBoundaryColumnIndex(-1);
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
	
    public void populateReacNamesFromFileBoxes(ArrayList<String> columnNamesFromFile) {
		
		LocalConfig.getInstance().setKnockoutColumnIndex(-1);
		LocalConfig.getInstance().setFluxValueColumnIndex(-1);
		LocalConfig.getInstance().setReversibleColumnIndex(-1);
		LocalConfig.getInstance().setLowerBoundColumnIndex(-1);
		LocalConfig.getInstance().setUpperBoundColumnIndex(-1);
		LocalConfig.getInstance().setBiologicalObjectiveColumnIndex(-1);
				
		//add all column names to from file comboboxes
		for (int c = 0; c < columnNamesFromFile.size(); c++) { 
			cbKnockout.addItem(columnNamesFromFile.get(c));
			cbFluxValue.addItem(columnNamesFromFile.get(c));
			cbReactionAbbreviation.addItem(columnNamesFromFile.get(c));
			cbReactionName.addItem(columnNamesFromFile.get(c));
			cbReactionEquation.addItem(columnNamesFromFile.get(c));
			cbReversible.addItem(columnNamesFromFile.get(c));
			cbLowerBound.addItem(columnNamesFromFile.get(c));
			cbUpperBound.addItem(columnNamesFromFile.get(c));
			cbObjective.addItem(columnNamesFromFile.get(c));
		}		
		cbKnockout.setSelectedIndex(-1);	
		cbFluxValue.setSelectedIndex(-1);
		cbReversible.setSelectedIndex(-1);	
		cbLowerBound.setSelectedIndex(-1);
		cbUpperBound.setSelectedIndex(-1);
		cbObjective.setSelectedIndex(-1);
		for (int c = 0; c < columnNamesFromFile.size(); c++) { 
			//filters to match column names from file to required column names in table			
			if((columnNamesFromFile.get(c).toLowerCase()).compareTo(GraphicalInterfaceConstants.KNOCKOUT_COLUMN_FILTER[0]) == 0 || (columnNamesFromFile.get(c).toLowerCase()).compareTo(GraphicalInterfaceConstants.KNOCKOUT_COLUMN_FILTER[1]) == 0) {
				cbKnockout.setSelectedIndex(c);
				LocalConfig.getInstance().setKnockoutColumnIndex(c);	
			} else if((columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.FLUX_VALUE_COLUMN_FILTER[0])) {
				cbFluxValue.setSelectedIndex(c);
				LocalConfig.getInstance().setFluxValueColumnIndex(c);
			} else if((columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.REVERSIBLE_COLUMN_FILTER[0])) {
				cbReversible.setSelectedIndex(c);
				LocalConfig.getInstance().setReversibleColumnIndex(c); 
			} else if((columnNamesFromFile.get(c).toLowerCase()).compareTo(GraphicalInterfaceConstants.LOWER_BOUND_FILTER[0]) == 0 || (columnNamesFromFile.get(c).toLowerCase()).compareTo(GraphicalInterfaceConstants.LOWER_BOUND_FILTER[1]) == 0) {
				cbLowerBound.setSelectedIndex(c);
				LocalConfig.getInstance().setLowerBoundColumnIndex(c); 
			} else if((columnNamesFromFile.get(c).toLowerCase()).compareTo(GraphicalInterfaceConstants.UPPER_BOUND_FILTER[0]) == 0 || (columnNamesFromFile.get(c).toLowerCase()).compareTo(GraphicalInterfaceConstants.UPPER_BOUND_FILTER[1]) == 0) {
				cbUpperBound.setSelectedIndex(c);
				LocalConfig.getInstance().setUpperBoundColumnIndex(c); 
			} else if((columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_FILTER[0])) {
				cbObjective.setSelectedIndex(c);
				LocalConfig.getInstance().setBiologicalObjectiveColumnIndex(c); 	
			}
		}
		 
		//csv files written by TextReactionWriter will have KO and fluxValue as col 1 and 2
		if((columnNamesFromFile.get(0).toLowerCase()).compareTo(GraphicalInterfaceConstants.KNOCKOUT_COLUMN_FILTER[0]) == 0 || (columnNamesFromFile.get(0).toLowerCase()).compareTo(GraphicalInterfaceConstants.KNOCKOUT_COLUMN_FILTER[1]) == 0) {
			cbReactionAbbreviation.setSelectedIndex(2);
			LocalConfig.getInstance().setReactionAbbreviationColumnIndex(2);
			cbReactionName.setSelectedIndex(3);	
			LocalConfig.getInstance().setReactionNameColumnIndex(3);
			cbReactionEquation.setSelectedIndex(4);	
			LocalConfig.getInstance().setReactionEquationColumnIndex(4);
		} else {
			//first two columns are recommended to be column 1 and 2: abbreviation (id), and name
			cbReactionAbbreviation.setSelectedIndex(0);
			LocalConfig.getInstance().setReactionAbbreviationColumnIndex(0);
			cbReactionName.setSelectedIndex(1);	
			LocalConfig.getInstance().setReactionNameColumnIndex(1);
			cbReactionEquation.setSelectedIndex(2);	
			LocalConfig.getInstance().setReactionEquationColumnIndex(2);
		}		
	}

	class Task extends SwingWorker<Void, Void> {

		@Override
		public void done() {
			 
		}

		@Override
		protected Void doInBackground() throws Exception {
			int progress = 0;
			Excel97Reader reader = new Excel97Reader();			
			reader.load(GraphicalInterface.getExcelPath(), LocalConfig.getInstance().getDatabaseName(), LocalConfig.getInstance().getSheetNamesList());
			while (progress < 100) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ignore) {
				}			
			}
			t.stop();
			return null;
		}
	}
	
	class TimeListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			progressBar.progress.setValue(LocalConfig.getInstance().getProgress());
			progressBar.progress.repaint();
			if (LocalConfig.getInstance().getProgress() == 100) {
				progressBar.setVisible(false);
				progressBar.dispose();
			}
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
	    list.add("test1");
	    list.add("test2");
	    ArrayList<String> list2 = new ArrayList();
	    list2.add("test1");
	    list2.add("test2");
	    list2.add("test3");
		ExcelColumnNameInterface frame = new ExcelColumnNameInterface(con, list, list2);
		frame.setIconImages(icons);
		frame.setSize(600, 700);
	    frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	    frame.setLocationRelativeTo(null);
	    frame.setVisible(true);
	}
}




