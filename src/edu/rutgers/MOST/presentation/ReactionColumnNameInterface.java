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
import edu.rutgers.MOST.data.TextReactionsModelReader;

public class ReactionColumnNameInterface  extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public JComboBox<String> cbKnockout = new JComboBox<String>();
	public JComboBox<String> cbFluxValue = new JComboBox<String>();
	public JComboBox<String> cbReactionAbbreviation = new JComboBox<String>();
	public JComboBox<String> cbReactionName = new JComboBox<String>();
	public JComboBox<String> cbReactionEquation = new JComboBox<String>();
	public JComboBox<String> cbReversible = new JComboBox<String>();
	public JComboBox<String> cbLowerBound = new JComboBox<String>();
	public JComboBox<String> cbUpperBound = new JComboBox<String>();
	public JComboBox<String> cbBiologicalObjective = new JComboBox<String>();
	public JComboBox<String> cbSyntheticObjective = new JComboBox<String>();
	public JComboBox<String> cbGeneAssociation = new JComboBox<String>();
	public JComboBox<String> cbProteinAssociation = new JComboBox<String>();
	public JComboBox<String> cbSubsystem = new JComboBox<String>();
	public JComboBox<String> cbProteinClass = new JComboBox<String>();

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
		ReactionColumnNameInterface.columnNamesFromFile = columnNamesFromFile;
	}

	public ReactionColumnNameInterface(ArrayList<String> columnNamesFromFile) {

		prevRowButton.setEnabled(false);
		LocalConfig.getInstance().setReactionsNextRowCorrection(0);
		rowLabel.setText("   row " + (LocalConfig.getInstance().getReactionsNextRowCorrection() + 1));
		
		validColumns = true;
		
		final ArrayList<Image> icons = new ArrayList<Image>(); 
		icons.add(new ImageIcon("etc/most16.jpg").getImage()); 
		icons.add(new ImageIcon("etc/most32.jpg").getImage());
		
		getRootPane().setDefaultButton(okButton);

		setColumnNamesFromFile(columnNamesFromFile);

		setTitle(ColumnInterfaceConstants.REACTIONS_COLUMN_NAME_INTERFACE_TITLE);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		//setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		cbReactionAbbreviation.setEditable(false);
		cbKnockout.setEditable(false);	
		cbFluxValue.setEditable(false);			
		cbReactionName.setEditable(false);
		cbReactionEquation.setEditable(false);
		cbReversible.setEditable(false);
		cbLowerBound.setEditable(false);
		cbUpperBound.setEditable(false);
		cbBiologicalObjective.setEditable(false);
		cbSyntheticObjective.setEditable(false);
		cbGeneAssociation.setEditable(false);
		cbProteinAssociation.setEditable(false);
		cbSubsystem.setEditable(false);
		cbProteinClass.setEditable(false);

		cbReactionAbbreviation.setPreferredSize(new Dimension(250, 25));
		cbReactionAbbreviation.setMaximumSize(new Dimension(250, 25));
		cbReactionAbbreviation.setMinimumSize(new Dimension(250, 25));
		
		cbKnockout.setPreferredSize(new Dimension(250, 25));
		cbKnockout.setMaximumSize(new Dimension(250, 25));
		cbKnockout.setMinimumSize(new Dimension(250, 25));

		cbFluxValue.setPreferredSize(new Dimension(250, 25));
		cbFluxValue.setMaximumSize(new Dimension(250, 25));
		cbFluxValue.setMinimumSize(new Dimension(250, 25));

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

		cbBiologicalObjective.setPreferredSize(new Dimension(250, 25));
		cbBiologicalObjective.setMaximumSize(new Dimension(250, 25));
		cbBiologicalObjective.setMinimumSize(new Dimension(250, 25));
		
		cbSyntheticObjective.setPreferredSize(new Dimension(250, 25));
		cbSyntheticObjective.setMaximumSize(new Dimension(250, 25));
		cbSyntheticObjective.setMinimumSize(new Dimension(250, 25));

		cbGeneAssociation.setPreferredSize(new Dimension(250, 25));
		cbGeneAssociation.setMaximumSize(new Dimension(250, 25));
		cbGeneAssociation.setMinimumSize(new Dimension(250, 25));
		
		cbProteinAssociation.setPreferredSize(new Dimension(250, 25));
		cbProteinAssociation.setMaximumSize(new Dimension(250, 25));
		cbProteinAssociation.setMinimumSize(new Dimension(250, 25));
		
		cbSubsystem.setPreferredSize(new Dimension(250, 25));
		cbSubsystem.setMaximumSize(new Dimension(250, 25));
		cbSubsystem.setMinimumSize(new Dimension(250, 25));
		
		cbProteinClass.setPreferredSize(new Dimension(250, 25));
		cbProteinClass.setMaximumSize(new Dimension(250, 25));
		cbProteinClass.setMinimumSize(new Dimension(250, 25));
		
		populateNamesFromFileBoxes(columnNamesFromFile);
		
		//box layout
		Box vb = Box.createVerticalBox();

		Box hbLabels = Box.createHorizontalBox();
		Box hbTop = Box.createHorizontalBox();	    	    
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
		Box hbBiologicalObjectiveLabel = Box.createHorizontalBox();	    
		Box hbBiologicalObjective = Box.createHorizontalBox();
		Box hbSyntheticObjectiveLabel = Box.createHorizontalBox();	    
		Box hbSyntheticObjective = Box.createHorizontalBox();
		Box hbGeneAssociationLabel = Box.createHorizontalBox();
		Box hbGeneAssociation = Box.createHorizontalBox();
		Box hbProteinAssociationLabel = Box.createHorizontalBox();
		Box hbProteinAssociation = Box.createHorizontalBox();
		Box hbSubsystemLabel = Box.createHorizontalBox();
		Box hbSubsystem = Box.createHorizontalBox();
		Box hbProteinClassLabel = Box.createHorizontalBox();
		Box hbProteinClass = Box.createHorizontalBox();

		Box vbLabels = Box.createVerticalBox();
		Box vbCombos = Box.createVerticalBox();
		
		Box hbLabeledCombos = Box.createHorizontalBox();
		Box hbRequiredLabel = Box.createHorizontalBox();
		Box hbButton = Box.createHorizontalBox();

		//top label
		JLabel topLabel = new JLabel();
		topLabel.setText(ColumnInterfaceConstants.REACTIONS_TOP_LABEL);
		topLabel.setSize(new Dimension(300, 30));
		//top, left, bottom. right
		topLabel.setBorder(BorderFactory.createEmptyBorder(20,30,20,200));
		topLabel.setAlignmentX(LEFT_ALIGNMENT);

		hbTop.add(topLabel);	
		hbTop.setAlignmentX(LEFT_ALIGNMENT);

		hbLabels.add(hbTop);
		
		//knockout Label and combo
		JLabel knockoutLabel = new JLabel();
		knockoutLabel.setText(ColumnInterfaceConstants.KO_LABEL);
		knockoutLabel.setPreferredSize(new Dimension(250, 25));
		knockoutLabel.setMaximumSize(new Dimension(250, 25));
		knockoutLabel.setMinimumSize(new Dimension(250, 25));
		knockoutLabel.setBorder(BorderFactory.createEmptyBorder(10,0,ColumnInterfaceConstants.LABEL_BOTTOM_BORDER_SIZE,10));
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

		vbLabels.add(hbKnockoutLabel);
		JLabel blankLabel2 = new JLabel("");
		vbLabels.add(blankLabel2);
		vbCombos.add(hbKnockout);

		//flux value label and combo
		JLabel fluxValueLabel = new JLabel();
		fluxValueLabel.setText(ColumnInterfaceConstants.FLUX_VALUE_LABEL);
		fluxValueLabel.setPreferredSize(new Dimension(250, 25));
		fluxValueLabel.setMaximumSize(new Dimension(250, 25));
		fluxValueLabel.setMinimumSize(new Dimension(250, 25));
		fluxValueLabel.setBorder(BorderFactory.createEmptyBorder(ColumnInterfaceConstants.LABEL_TOP_BORDER_SIZE,0,ColumnInterfaceConstants.LABEL_BOTTOM_BORDER_SIZE,10));
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

		vbLabels.add(hbFluxValueLabel);
		JLabel blankLabel3 = new JLabel("");
		vbLabels.add(blankLabel3);
		vbCombos.add(hbFluxValue);

		//reaction abbreviation label and combo
		JLabel reactionAbbreviationLabel = new JLabel();
		reactionAbbreviationLabel.setText(ColumnInterfaceConstants.REACTION_ABBREVIATION_LABEL);
		reactionAbbreviationLabel.setPreferredSize(new Dimension(250, 25));
		reactionAbbreviationLabel.setMaximumSize(new Dimension(250, 25));
		reactionAbbreviationLabel.setMinimumSize(new Dimension(250, 25));
		reactionAbbreviationLabel.setBorder(BorderFactory.createEmptyBorder(ColumnInterfaceConstants.LABEL_TOP_BORDER_SIZE,0,ColumnInterfaceConstants.LABEL_BOTTOM_BORDER_SIZE,10));
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

		vbLabels.add(hbReactionAbbreviationLabel);
		JLabel blankLabel1 = new JLabel("");
		vbLabels.add(blankLabel1);
		vbCombos.add(hbReactionAbbreviation);
		
		//reaction name label and combo
		JLabel reactionNameLabel = new JLabel();
		reactionNameLabel.setText(ColumnInterfaceConstants.REACTION_NAME_LABEL);
		reactionNameLabel.setPreferredSize(new Dimension(250, 25));
		reactionNameLabel.setMaximumSize(new Dimension(250, 25));
		reactionNameLabel.setMinimumSize(new Dimension(250, 25));
		reactionNameLabel.setBorder(BorderFactory.createEmptyBorder(ColumnInterfaceConstants.LABEL_TOP_BORDER_SIZE,0,ColumnInterfaceConstants.LABEL_BOTTOM_BORDER_SIZE,10));
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

		vbLabels.add(hbReactionNameLabel);
		JLabel blankLabel4 = new JLabel("");
		vbLabels.add(blankLabel4);
		vbCombos.add(hbReactionName);

		//reaction equation label and combo
		JLabel reactionEquationLabel = new JLabel();
		reactionEquationLabel.setText(ColumnInterfaceConstants.REACTION_EQUATION_LABEL);
		reactionEquationLabel.setPreferredSize(new Dimension(250, 25));
		reactionEquationLabel.setMaximumSize(new Dimension(250, 25));
		reactionEquationLabel.setMinimumSize(new Dimension(250, 25));
		reactionEquationLabel.setBorder(BorderFactory.createEmptyBorder(ColumnInterfaceConstants.LABEL_TOP_BORDER_SIZE,0,ColumnInterfaceConstants.LABEL_BOTTOM_BORDER_SIZE,10));
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

		vbLabels.add(hbReactionEquationLabel);
		JLabel blankLabel5 = new JLabel("");
		vbLabels.add(blankLabel5);
		vbCombos.add(hbReactionEquation);

		//reversible label and combo
		JLabel reversibleLabel = new JLabel();
		reversibleLabel.setText(ColumnInterfaceConstants.REVERSIBLE_LABEL);
		reversibleLabel.setPreferredSize(new Dimension(250, 25));
		reversibleLabel.setMaximumSize(new Dimension(250, 25));
		reversibleLabel.setMinimumSize(new Dimension(250, 25));
		reversibleLabel.setBorder(BorderFactory.createEmptyBorder(ColumnInterfaceConstants.LABEL_TOP_BORDER_SIZE,0,ColumnInterfaceConstants.LABEL_BOTTOM_BORDER_SIZE,10));
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

		vbLabels.add(hbReversibleLabel);
		JLabel blankLabel6 = new JLabel("");
		vbLabels.add(blankLabel6);
		vbCombos.add(hbReversible);

		//lower bound label and combo
		JLabel lowerBoundLabel = new JLabel();
		lowerBoundLabel.setText(ColumnInterfaceConstants.LOWER_BOUND_LABEL);
		lowerBoundLabel.setPreferredSize(new Dimension(250, 25));
		lowerBoundLabel.setMaximumSize(new Dimension(250, 25));
		lowerBoundLabel.setMinimumSize(new Dimension(250, 25));
		lowerBoundLabel.setBorder(BorderFactory.createEmptyBorder(ColumnInterfaceConstants.LABEL_TOP_BORDER_SIZE,0,ColumnInterfaceConstants.LABEL_BOTTOM_BORDER_SIZE,10));
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

		vbLabels.add(hbLowerBoundLabel);
		JLabel blankLabel7 = new JLabel("");
		vbLabels.add(blankLabel7);
		vbCombos.add(hbLowerBound);

		//upper bound label and combo
		JLabel upperBoundLabel = new JLabel();
		upperBoundLabel.setText(ColumnInterfaceConstants.UPPER_BOUND_LABEL);
		upperBoundLabel.setPreferredSize(new Dimension(250, 25));
		upperBoundLabel.setMaximumSize(new Dimension(250, 25));
		upperBoundLabel.setMinimumSize(new Dimension(250, 25));
		upperBoundLabel.setBorder(BorderFactory.createEmptyBorder(ColumnInterfaceConstants.LABEL_TOP_BORDER_SIZE,0,ColumnInterfaceConstants.LABEL_BOTTOM_BORDER_SIZE,10));
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

		vbLabels.add(hbUpperBoundLabel);
		JLabel blankLabel8 = new JLabel("");
		vbLabels.add(blankLabel8);
		vbCombos.add(hbUpperBound);

		//biological objective label and combo
		JLabel biologicalObjectiveLabel = new JLabel();
		biologicalObjectiveLabel.setText(ColumnInterfaceConstants.BIOLOGICAL_OBJECTIVE_LABEL);
		biologicalObjectiveLabel.setPreferredSize(new Dimension(250, 25));
		biologicalObjectiveLabel.setMaximumSize(new Dimension(250, 25));
		biologicalObjectiveLabel.setMinimumSize(new Dimension(250, 25));
		biologicalObjectiveLabel.setBorder(BorderFactory.createEmptyBorder(ColumnInterfaceConstants.LABEL_TOP_BORDER_SIZE,0,ColumnInterfaceConstants.LABEL_BOTTOM_BORDER_SIZE,10));
		biologicalObjectiveLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelBiologicalObjectiveLabel = new JPanel();
		panelBiologicalObjectiveLabel.setLayout(new BoxLayout(panelBiologicalObjectiveLabel, BoxLayout.X_AXIS));
		panelBiologicalObjectiveLabel.add(biologicalObjectiveLabel);
		panelBiologicalObjectiveLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbBiologicalObjectiveLabel.add(panelBiologicalObjectiveLabel);
		hbBiologicalObjectiveLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelBiologicalObjective = new JPanel();
		panelBiologicalObjective.setLayout(new BoxLayout(panelBiologicalObjective, BoxLayout.X_AXIS));
		panelBiologicalObjective.add(cbBiologicalObjective);
		panelBiologicalObjective.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		panelBiologicalObjective.setAlignmentX(RIGHT_ALIGNMENT);

		hbBiologicalObjective.add(panelBiologicalObjective);
		hbBiologicalObjective.setAlignmentX(RIGHT_ALIGNMENT);

		vbLabels.add(hbBiologicalObjectiveLabel);
		JLabel blankLabel9 = new JLabel("");
		vbLabels.add(blankLabel9);
		vbCombos.add(hbBiologicalObjective);

		//synthetic objective label and combo
		JLabel syntheticObjectiveLabel = new JLabel();
		syntheticObjectiveLabel.setText(ColumnInterfaceConstants.SYNTHETIC_OBJECTIVE_LABEL);
		syntheticObjectiveLabel.setPreferredSize(new Dimension(250, 25));
		syntheticObjectiveLabel.setMaximumSize(new Dimension(250, 25));
		syntheticObjectiveLabel.setMinimumSize(new Dimension(250, 25));
		syntheticObjectiveLabel.setBorder(BorderFactory.createEmptyBorder(ColumnInterfaceConstants.LABEL_TOP_BORDER_SIZE,0,ColumnInterfaceConstants.LABEL_BOTTOM_BORDER_SIZE,10));
		syntheticObjectiveLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelSyntheticObjectiveLabel = new JPanel();
		panelSyntheticObjectiveLabel.setLayout(new BoxLayout(panelSyntheticObjectiveLabel, BoxLayout.X_AXIS));
		panelSyntheticObjectiveLabel.add(syntheticObjectiveLabel);
		panelSyntheticObjectiveLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbSyntheticObjectiveLabel.add(panelSyntheticObjectiveLabel);
		hbSyntheticObjectiveLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelSyntheticObjective = new JPanel();
		panelSyntheticObjective.setLayout(new BoxLayout(panelSyntheticObjective, BoxLayout.X_AXIS));
		panelSyntheticObjective.add(cbSyntheticObjective);
		panelSyntheticObjective.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		panelSyntheticObjective.setAlignmentX(RIGHT_ALIGNMENT);

		hbSyntheticObjective.add(panelSyntheticObjective);
		hbSyntheticObjective.setAlignmentX(RIGHT_ALIGNMENT);

		vbLabels.add(hbSyntheticObjectiveLabel);
		JLabel blankLabel10 = new JLabel("");
		vbLabels.add(blankLabel10);
		vbCombos.add(hbSyntheticObjective);
		
		//gene association label and combo
		JLabel geneAssociationLabel = new JLabel();
		geneAssociationLabel.setText(ColumnInterfaceConstants.GENE_ASSOCIATION_LABEL);
		geneAssociationLabel.setPreferredSize(new Dimension(250, 25));
		geneAssociationLabel.setMaximumSize(new Dimension(250, 25));
		geneAssociationLabel.setMinimumSize(new Dimension(250, 25));
		geneAssociationLabel.setBorder(BorderFactory.createEmptyBorder(ColumnInterfaceConstants.LABEL_TOP_BORDER_SIZE,0,ColumnInterfaceConstants.LABEL_BOTTOM_BORDER_SIZE,10));
		geneAssociationLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelGeneAssociationLabel = new JPanel();
		panelGeneAssociationLabel.setLayout(new BoxLayout(panelGeneAssociationLabel, BoxLayout.X_AXIS));
		panelGeneAssociationLabel.add(geneAssociationLabel);
		panelGeneAssociationLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbGeneAssociationLabel.add(panelGeneAssociationLabel);
		hbGeneAssociationLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelGeneAssociation = new JPanel();
		panelGeneAssociation.setLayout(new BoxLayout(panelGeneAssociation, BoxLayout.X_AXIS));
		panelGeneAssociation.add(cbGeneAssociation);
		panelGeneAssociation.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		panelGeneAssociation.setAlignmentX(RIGHT_ALIGNMENT);

		hbGeneAssociation.add(panelGeneAssociation);
		hbGeneAssociation.setAlignmentX(RIGHT_ALIGNMENT);

		vbLabels.add(hbGeneAssociationLabel);
		JLabel blankLabel11 = new JLabel("");
		vbLabels.add(blankLabel11);
		vbCombos.add(hbGeneAssociation);
		
		//protein association label and combo
		JLabel proteinAssociationLabel = new JLabel();
		proteinAssociationLabel.setText(ColumnInterfaceConstants.PROTEIN_ASSOCIATION_LABEL);
		proteinAssociationLabel.setPreferredSize(new Dimension(250, 25));
		proteinAssociationLabel.setMaximumSize(new Dimension(250, 25));
		proteinAssociationLabel.setMinimumSize(new Dimension(250, 25));
		proteinAssociationLabel.setBorder(BorderFactory.createEmptyBorder(ColumnInterfaceConstants.LABEL_TOP_BORDER_SIZE,0,ColumnInterfaceConstants.LABEL_BOTTOM_BORDER_SIZE,10));
		proteinAssociationLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelProteinAssociationLabel = new JPanel();
		panelProteinAssociationLabel.setLayout(new BoxLayout(panelProteinAssociationLabel, BoxLayout.X_AXIS));
		panelProteinAssociationLabel.add(proteinAssociationLabel);
		panelProteinAssociationLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbProteinAssociationLabel.add(panelProteinAssociationLabel);
		hbProteinAssociationLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelProteinAssociation = new JPanel();
		panelProteinAssociation.setLayout(new BoxLayout(panelProteinAssociation, BoxLayout.X_AXIS));
		panelProteinAssociation.add(cbProteinAssociation);
		panelProteinAssociation.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		panelProteinAssociation.setAlignmentX(RIGHT_ALIGNMENT);

		hbProteinAssociation.add(panelProteinAssociation);
		hbProteinAssociation.setAlignmentX(RIGHT_ALIGNMENT);

		vbLabels.add(hbProteinAssociationLabel);
		JLabel blankLabel12 = new JLabel("");
		vbLabels.add(blankLabel12);
		vbCombos.add(hbProteinAssociation);
		
		//subsystem label and combo
		JLabel subsystemLabel = new JLabel();
		subsystemLabel.setText(ColumnInterfaceConstants.SUBSYSTEM_LABEL);
		subsystemLabel.setPreferredSize(new Dimension(250, 25));
		subsystemLabel.setMaximumSize(new Dimension(250, 25));
		subsystemLabel.setMinimumSize(new Dimension(250, 25));
		subsystemLabel.setBorder(BorderFactory.createEmptyBorder(ColumnInterfaceConstants.LABEL_TOP_BORDER_SIZE,0,ColumnInterfaceConstants.LABEL_BOTTOM_BORDER_SIZE,10));
		subsystemLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelSubsystemLabel = new JPanel();
		panelSubsystemLabel.setLayout(new BoxLayout(panelSubsystemLabel, BoxLayout.X_AXIS));
		panelSubsystemLabel.add(subsystemLabel);
		panelSubsystemLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbSubsystemLabel.add(panelSubsystemLabel);
		hbSubsystemLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelSubsystem = new JPanel();
		panelSubsystem.setLayout(new BoxLayout(panelSubsystem, BoxLayout.X_AXIS));
		panelSubsystem.add(cbSubsystem);
		panelSubsystem.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		panelSubsystem.setAlignmentX(RIGHT_ALIGNMENT);

		hbSubsystem.add(panelSubsystem);
		hbSubsystem.setAlignmentX(RIGHT_ALIGNMENT);

		vbLabels.add(hbSubsystemLabel);
		JLabel blankLabel13 = new JLabel("");
		vbLabels.add(blankLabel13);
		vbCombos.add(hbSubsystem);
		
		//protein class label and combo
		JLabel proteinClassLabel = new JLabel();
		proteinClassLabel.setText(ColumnInterfaceConstants.PROTEIN_CLASS_LABEL);
		proteinClassLabel.setPreferredSize(new Dimension(250, 25));
		proteinClassLabel.setMaximumSize(new Dimension(250, 25));
		proteinClassLabel.setMinimumSize(new Dimension(250, 25));
		proteinClassLabel.setBorder(BorderFactory.createEmptyBorder(ColumnInterfaceConstants.LABEL_TOP_BORDER_SIZE,0,ColumnInterfaceConstants.LABEL_BOTTOM_BORDER_SIZE,10));
		proteinClassLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelProteinClassLabel = new JPanel();
		panelProteinClassLabel.setLayout(new BoxLayout(panelProteinClassLabel, BoxLayout.X_AXIS));
		panelProteinClassLabel.add(proteinClassLabel);
		panelProteinClassLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbProteinClassLabel.add(panelProteinClassLabel);
		hbProteinClassLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelProteinClass = new JPanel();
		panelProteinClass.setLayout(new BoxLayout(panelProteinClass, BoxLayout.X_AXIS));
		panelProteinClass.add(cbProteinClass);
		panelProteinClass.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		panelProteinClass.setAlignmentX(RIGHT_ALIGNMENT);

		hbProteinClass.add(panelProteinClass);
		hbProteinClass.setAlignmentX(RIGHT_ALIGNMENT);

		vbLabels.add(hbProteinClassLabel);
		JLabel blankLabel14 = new JLabel("");
		vbLabels.add(blankLabel14);
		vbCombos.add(hbProteinClass);
		
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
				/*
				if (cbReactionAbbreviation.getSelectedIndex() == -1 || cbReactionEquation.getSelectedIndex() == -1) {
					JOptionPane.showMessageDialog(null,                
							ColumnInterfaceConstants.BLANK_REACTION_FIELDS_ERROR_MESSAGE,
							ColumnInterfaceConstants.BLANK_REACTION_FIELDS_ERROR_TITLE,                                
							JOptionPane.ERROR_MESSAGE);
				} else if (cbReactionAbbreviation.getSelectedItem().toString().toLowerCase().equals(GraphicalInterfaceConstants.METAB_ABBREVIATION_NOT_FILTER[0]) ||
						cbReactionAbbreviation.getSelectedItem().toString().toLowerCase().equals(GraphicalInterfaceConstants.METAB_ABBREVIATION_NOT_FILTER[1]) ||
						cbReactionAbbreviation.getSelectedItem().toString().toLowerCase().equals(GraphicalInterfaceConstants.METAB_ABBREVIATION_NOT_FILTER[2])) {
					JOptionPane.showMessageDialog(null,                
							"Invalid name for Reaction Abbreviation column.",
							"Column Name Error",                                
							JOptionPane.ERROR_MESSAGE);
				} else {
					ArrayList<String> metaColumnNames = new ArrayList<String>();
					ArrayList<Integer> usedIndices = new ArrayList<Integer>();
					ArrayList<Integer> metaColumnIndexList = new ArrayList<Integer>();

					if (getColumnNamesFromFile().contains(cbReactionAbbreviation.getSelectedItem())) {
						LocalConfig.getInstance().setReactionAbbreviationColumnIndex(getColumnNamesFromFile().indexOf(cbReactionAbbreviation.getSelectedItem()));
						usedIndices.add(getColumnNamesFromFile().indexOf(cbReactionAbbreviation.getSelectedItem()));
					}
					if (getColumnNamesFromFile().contains(cbKnockout.getSelectedItem())) {
						LocalConfig.getInstance().setKnockoutColumnIndex(getColumnNamesFromFile().indexOf(cbKnockout.getSelectedItem()));
						usedIndices.add(getColumnNamesFromFile().indexOf(cbKnockout.getSelectedItem()));
					}
					if (getColumnNamesFromFile().contains(cbFluxValue.getSelectedItem())) {
						LocalConfig.getInstance().setFluxValueColumnIndex(getColumnNamesFromFile().indexOf(cbFluxValue.getSelectedItem()));
						usedIndices.add(getColumnNamesFromFile().indexOf(cbFluxValue.getSelectedItem()));
					}	
					if (cbReactionName.getSelectedIndex() == -1) {
						LocalConfig.getInstance().getHiddenReactionsColumns().add(GraphicalInterfaceConstants.REACTION_NAME_COLUMN);
					} else if (getColumnNamesFromFile().contains(cbReactionName.getSelectedItem())) {
						LocalConfig.getInstance().setReactionNameColumnIndex(getColumnNamesFromFile().indexOf(cbReactionName.getSelectedItem()));
						usedIndices.add(getColumnNamesFromFile().indexOf(cbReactionName.getSelectedItem()));
					}
					if (getColumnNamesFromFile().contains(cbReactionEquation.getSelectedItem())) {
						LocalConfig.getInstance().setReactionEquationColumnIndex(getColumnNamesFromFile().indexOf(cbReactionEquation.getSelectedItem()));
						usedIndices.add(getColumnNamesFromFile().indexOf(cbReactionEquation.getSelectedItem()));
					}
					if (cbReversible.getSelectedIndex() == -1) {
						LocalConfig.getInstance().getHiddenReactionsColumns().add(GraphicalInterfaceConstants.REVERSIBLE_COLUMN);
					} else if (getColumnNamesFromFile().contains(cbReversible.getSelectedItem())) {
						LocalConfig.getInstance().setReversibleColumnIndex(getColumnNamesFromFile().indexOf(cbReversible.getSelectedItem()));
						usedIndices.add(getColumnNamesFromFile().indexOf(cbReversible.getSelectedItem()));
					}
					if (getColumnNamesFromFile().contains(cbLowerBound.getSelectedItem())) {
						LocalConfig.getInstance().setLowerBoundColumnIndex(getColumnNamesFromFile().indexOf(cbLowerBound.getSelectedItem()));
						usedIndices.add(getColumnNamesFromFile().indexOf(cbLowerBound.getSelectedItem()));
					}
					if (getColumnNamesFromFile().contains(cbUpperBound.getSelectedItem())) {
						LocalConfig.getInstance().setUpperBoundColumnIndex(getColumnNamesFromFile().indexOf(cbUpperBound.getSelectedItem()));
						usedIndices.add(getColumnNamesFromFile().indexOf(cbUpperBound.getSelectedItem()));
					}
					if (getColumnNamesFromFile().contains(cbBiologicalObjective.getSelectedItem())) {
						LocalConfig.getInstance().setBiologicalObjectiveColumnIndex(getColumnNamesFromFile().indexOf(cbBiologicalObjective.getSelectedItem()));
						usedIndices.add(getColumnNamesFromFile().indexOf(cbBiologicalObjective.getSelectedItem()));
					}
					if (getColumnNamesFromFile().contains(cbSyntheticObjective.getSelectedItem())) {
						LocalConfig.getInstance().setSyntheticObjectiveColumnIndex(getColumnNamesFromFile().indexOf(cbSyntheticObjective.getSelectedItem()));
						usedIndices.add(getColumnNamesFromFile().indexOf(cbSyntheticObjective.getSelectedItem()));
					}
					if (getColumnNamesFromFile().contains(cbGeneAssociation.getSelectedItem())) {
						LocalConfig.getInstance().setGeneAssociationColumnIndex(getColumnNamesFromFile().indexOf(cbGeneAssociation.getSelectedItem()));
						usedIndices.add(getColumnNamesFromFile().indexOf(cbGeneAssociation.getSelectedItem()));
					}
					if (getColumnNamesFromFile().contains(cbProteinAssociation.getSelectedItem())) {
						LocalConfig.getInstance().setProteinAssociationColumnIndex(getColumnNamesFromFile().indexOf(cbProteinAssociation.getSelectedItem()));
						usedIndices.add(getColumnNamesFromFile().indexOf(cbProteinAssociation.getSelectedItem()));
					}
					if (getColumnNamesFromFile().contains(cbSubsystem.getSelectedItem())) {
						LocalConfig.getInstance().setSubsystemColumnIndex(getColumnNamesFromFile().indexOf(cbSubsystem.getSelectedItem()));
						usedIndices.add(getColumnNamesFromFile().indexOf(cbSubsystem.getSelectedItem()));
					}
					if (getColumnNamesFromFile().contains(cbProteinClass.getSelectedItem())) {
						LocalConfig.getInstance().setProteinClassColumnIndex(getColumnNamesFromFile().indexOf(cbProteinClass.getSelectedItem()));
						usedIndices.add(getColumnNamesFromFile().indexOf(cbProteinClass.getSelectedItem()));
					}
					for (int i = 0; i < getColumnNamesFromFile().size(); i++) {
						if (!usedIndices.contains(i)) {
							metaColumnNames.add(getColumnNamesFromFile().get(i));
							metaColumnIndexList.add(getColumnNamesFromFile().indexOf(getColumnNamesFromFile().get(i)));
						} 
					}
					
					LocalConfig.getInstance().setReactionsMetaColumnIndexList(metaColumnIndexList);
					
					setVisible(false);
					dispose();
				}
				*/				
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
				TextReactionsModelReader reader = new TextReactionsModelReader();
				int correction = LocalConfig.getInstance().getReactionsNextRowCorrection();
				if (correction > 0) {
					LocalConfig.getInstance().setReactionsNextRowCorrection(correction - 1);	
					rowLabel.setText("   row " + (LocalConfig.getInstance().getReactionsNextRowCorrection() + 1));
					ArrayList<String> newColumnNamesFromFile = reader.columnNamesFromFile(LocalConfig.getInstance().getReactionsCSVFile(), LocalConfig.getInstance().getReactionsNextRowCorrection());
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
				TextReactionsModelReader reader = new TextReactionsModelReader();
				int correction = LocalConfig.getInstance().getReactionsNextRowCorrection();
				if ((correction + 1) < reader.numberOfLines(LocalConfig.getInstance().getReactionsCSVFile())) {
					LocalConfig.getInstance().setReactionsNextRowCorrection(correction + 1);	
					rowLabel.setText("   row " + (LocalConfig.getInstance().getReactionsNextRowCorrection() + 1));
					ArrayList<String> newColumnNamesFromFile = reader.columnNamesFromFile(LocalConfig.getInstance().getReactionsCSVFile(), LocalConfig.getInstance().getReactionsNextRowCorrection());
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

		LocalConfig.getInstance().setReactionAbbreviationColumnIndex(-1);
		LocalConfig.getInstance().setKnockoutColumnIndex(-1);
		LocalConfig.getInstance().setFluxValueColumnIndex(-1);		
		LocalConfig.getInstance().setReactionNameColumnIndex(-1);
		LocalConfig.getInstance().setReactionEquationColumnIndex(-1);
		LocalConfig.getInstance().setReactionEquationNamesColumnIndex(-1);
		LocalConfig.getInstance().setReversibleColumnIndex(-1);
		LocalConfig.getInstance().setLowerBoundColumnIndex(-1);
		LocalConfig.getInstance().setUpperBoundColumnIndex(-1);
		LocalConfig.getInstance().setBiologicalObjectiveColumnIndex(-1);
		LocalConfig.getInstance().setSyntheticObjectiveColumnIndex(-1);
		LocalConfig.getInstance().setGeneAssociationColumnIndex(-1);
		LocalConfig.getInstance().setProteinAssociationColumnIndex(-1);
		LocalConfig.getInstance().setSubsystemColumnIndex(-1);
		LocalConfig.getInstance().setProteinClassColumnIndex(-1);
		
		cbReactionAbbreviation.removeAllItems();
		cbKnockout.removeAllItems();
		cbFluxValue.removeAllItems();		
		cbReactionName.removeAllItems();
		cbReactionEquation.removeAllItems();
		cbReversible.removeAllItems();
		cbLowerBound.removeAllItems();
		cbUpperBound.removeAllItems();
		cbBiologicalObjective.removeAllItems();
		cbSyntheticObjective.removeAllItems();
		cbGeneAssociation.removeAllItems();
		cbProteinAssociation.removeAllItems();
		cbSubsystem.removeAllItems();
		cbProteinClass.removeAllItems();
		//add all column names to from file comboboxes
		for (int c = 0; c < columnNamesFromFile.size(); c++) {
			cbReactionAbbreviation.addItem(columnNamesFromFile.get(c));
			cbKnockout.addItem(columnNamesFromFile.get(c));
			cbFluxValue.addItem(columnNamesFromFile.get(c));			
			cbReactionName.addItem(columnNamesFromFile.get(c));
			cbReactionEquation.addItem(columnNamesFromFile.get(c));
			cbReversible.addItem(columnNamesFromFile.get(c));
			cbLowerBound.addItem(columnNamesFromFile.get(c));
			cbUpperBound.addItem(columnNamesFromFile.get(c));
			cbBiologicalObjective.addItem(columnNamesFromFile.get(c));
			cbSyntheticObjective.addItem(columnNamesFromFile.get(c));
			cbGeneAssociation.addItem(columnNamesFromFile.get(c));
			cbProteinAssociation.addItem(columnNamesFromFile.get(c));
			cbSubsystem.addItem(columnNamesFromFile.get(c));
			cbProteinClass.addItem(columnNamesFromFile.get(c));
		}	
		cbReactionAbbreviation.setSelectedIndex(-1);
		cbKnockout.setSelectedIndex(-1);
		cbFluxValue.setSelectedIndex(-1);		
		cbReactionName.setSelectedIndex(-1);
		cbReactionEquation.setSelectedIndex(-1);
		cbReversible.setSelectedIndex(-1);	
		cbLowerBound.setSelectedIndex(-1);
		cbUpperBound.setSelectedIndex(-1);
		cbBiologicalObjective.setSelectedIndex(-1);
		cbSyntheticObjective.setSelectedIndex(-1);
		cbGeneAssociation.setSelectedIndex(-1);
		cbProteinAssociation.setSelectedIndex(-1);
		cbSubsystem.setSelectedIndex(-1);
		cbProteinClass.setSelectedIndex(-1);
		
		for (int c = 0; c < columnNamesFromFile.size(); c++) { 
			//filters to match column names from file to required column names in table			
			if((columnNamesFromFile.get(c).toLowerCase()).compareTo(GraphicalInterfaceConstants.KNOCKOUT_COLUMN_FILTER[0]) == 0 || (columnNamesFromFile.get(c).toLowerCase()).compareTo(GraphicalInterfaceConstants.KNOCKOUT_COLUMN_FILTER[1]) == 0) {
				cbKnockout.setSelectedIndex(c);
				LocalConfig.getInstance().setKnockoutColumnIndex(c);
			} else if((columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.FLUX_VALUE_COLUMN_FILTER[0]) &&
					!(columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.FLUX_VALUE_NOT_FILTER[0]) &&
					!(columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.FLUX_VALUE_NOT_FILTER[1])) {
				cbFluxValue.setSelectedIndex(c);
				LocalConfig.getInstance().setFluxValueColumnIndex(c);
			} else if(((columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.ABBREVIATION_COLUMN_FILTER[0]) || (columnNamesFromFile.get(c).toLowerCase()).compareTo(GraphicalInterfaceConstants.ABBREVIATION_COLUMN_FILTER[1]) == 0) && !(columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.ABBREVIATION_COLUMN_NOT_FILTER[0])) {
				cbReactionAbbreviation.setSelectedIndex(c);
				LocalConfig.getInstance().setReactionAbbreviationColumnIndex(c); 
			} else if((columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.NAME_COLUMN_FILTER[0]) && !(columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.NAME_COLUMN_NOT_FILTER[0])) {
				cbReactionName.setSelectedIndex(c);
				LocalConfig.getInstance().setReactionNameColumnIndex(c); 
			} else if(((columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.EQUATION_COLUMN_FILTER[0]) || (columnNamesFromFile.get(c).toLowerCase()).equals(GraphicalInterfaceConstants.EQUATION_COLUMN_FILTER[1])) && !(columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.EQUATION_COLUMN_NOT_FILTER[0])) {
				cbReactionEquation.setSelectedIndex(c);
				LocalConfig.getInstance().setReactionEquationColumnIndex(c); 
			} else if((columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.REVERSIBLE_COLUMN_FILTER[0])) {
				cbReversible.setSelectedIndex(c);
				LocalConfig.getInstance().setReversibleColumnIndex(c); 
			} else if((columnNamesFromFile.get(c).toLowerCase()).compareTo(GraphicalInterfaceConstants.LOWER_BOUND_FILTER[0]) == 0 || (columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.LOWER_BOUND_FILTER[1])) {
				cbLowerBound.setSelectedIndex(c);
				LocalConfig.getInstance().setLowerBoundColumnIndex(c); 
			} else if((columnNamesFromFile.get(c).toLowerCase()).compareTo(GraphicalInterfaceConstants.UPPER_BOUND_FILTER[0]) == 0 || (columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.UPPER_BOUND_FILTER[1])) {
				cbUpperBound.setSelectedIndex(c);
				LocalConfig.getInstance().setUpperBoundColumnIndex(c); 
			} else if((columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_FILTER[0]) && !(columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_NOT_FILTER[0])) {
				cbBiologicalObjective.setSelectedIndex(c);
				LocalConfig.getInstance().setBiologicalObjectiveColumnIndex(c); 
			} else if((columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_FILTER[0]) && !(columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_NOT_FILTER[0])) {
				cbSyntheticObjective.setSelectedIndex(c);
				LocalConfig.getInstance().setSyntheticObjectiveColumnIndex(c); 
			} else if((columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.GENE_ASSOCIATION_COLUMN_FILTER[0]) && (columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.GENE_ASSOCIATION_COLUMN_FILTER[1])) {
				cbGeneAssociation.setSelectedIndex(c);
				LocalConfig.getInstance().setGeneAssociationColumnIndex(c);
			} else if((columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.PROTEIN_ASSOCIATION_COLUMN_FILTER[0]) && (columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.PROTEIN_ASSOCIATION_COLUMN_FILTER[1])) {
				cbProteinAssociation.setSelectedIndex(c);
				LocalConfig.getInstance().setProteinAssociationColumnIndex(c);
			} else if(((columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.SUBSYSTEM_COLUMN_FILTER[0]))) {
				cbSubsystem.setSelectedIndex(c);
				LocalConfig.getInstance().setSubsystemColumnIndex(c); 
			} else if((columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.PROTEIN_CLASS_COLUMN_FILTER[0]) && (columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.PROTEIN_CLASS_COLUMN_FILTER[1])) {
				cbProteinClass.setSelectedIndex(c);
				LocalConfig.getInstance().setProteinClassColumnIndex(c);
			}
		}

		/*
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
		*/	
	}

	public void getColumnIndices() {
		if (cbReactionAbbreviation.getSelectedIndex() == -1 || cbReactionEquation.getSelectedIndex() == -1) {
			JOptionPane.showMessageDialog(null,                
					ColumnInterfaceConstants.BLANK_REACTION_FIELDS_ERROR_MESSAGE,
					ColumnInterfaceConstants.BLANK_REACTION_FIELDS_ERROR_TITLE,                                
					JOptionPane.ERROR_MESSAGE);
			validColumns = false;
		} else if (cbReactionAbbreviation.getSelectedItem().toString().toLowerCase().equals(GraphicalInterfaceConstants.METAB_ABBREVIATION_NOT_FILTER[0]) ||
				cbReactionAbbreviation.getSelectedItem().toString().toLowerCase().equals(GraphicalInterfaceConstants.METAB_ABBREVIATION_NOT_FILTER[1]) ||
				cbReactionAbbreviation.getSelectedItem().toString().toLowerCase().equals(GraphicalInterfaceConstants.METAB_ABBREVIATION_NOT_FILTER[2])) {
			JOptionPane.showMessageDialog(null,                
					"Invalid name for Reaction Abbreviation column.",
					"Column Name Error",                                
					JOptionPane.ERROR_MESSAGE);
			validColumns = false;
		} else {
			validColumns = true;
			ArrayList<String> metaColumnNames = new ArrayList<String>();
			ArrayList<Integer> usedIndices = new ArrayList<Integer>();
			ArrayList<Integer> metaColumnIndexList = new ArrayList<Integer>();

			if (getColumnNamesFromFile().contains(cbReactionAbbreviation.getSelectedItem())) {
				LocalConfig.getInstance().setReactionAbbreviationColumnIndex(getColumnNamesFromFile().indexOf(cbReactionAbbreviation.getSelectedItem()));
				usedIndices.add(getColumnNamesFromFile().indexOf(cbReactionAbbreviation.getSelectedItem()));
			}
			if (getColumnNamesFromFile().contains(cbKnockout.getSelectedItem())) {
				LocalConfig.getInstance().setKnockoutColumnIndex(getColumnNamesFromFile().indexOf(cbKnockout.getSelectedItem()));
				usedIndices.add(getColumnNamesFromFile().indexOf(cbKnockout.getSelectedItem()));
			}
			if (getColumnNamesFromFile().contains(cbFluxValue.getSelectedItem())) {
				LocalConfig.getInstance().setFluxValueColumnIndex(getColumnNamesFromFile().indexOf(cbFluxValue.getSelectedItem()));
				usedIndices.add(getColumnNamesFromFile().indexOf(cbFluxValue.getSelectedItem()));
			}	
			if (getColumnNamesFromFile().contains(cbReactionName.getSelectedItem())) {
				LocalConfig.getInstance().setReactionNameColumnIndex(getColumnNamesFromFile().indexOf(cbReactionName.getSelectedItem()));
				usedIndices.add(getColumnNamesFromFile().indexOf(cbReactionName.getSelectedItem()));
			}
			if (getColumnNamesFromFile().contains(cbReactionEquation.getSelectedItem())) {
				LocalConfig.getInstance().setReactionEquationColumnIndex(getColumnNamesFromFile().indexOf(cbReactionEquation.getSelectedItem()));
				usedIndices.add(getColumnNamesFromFile().indexOf(cbReactionEquation.getSelectedItem()));
			}
			if (getColumnNamesFromFile().contains(cbReversible.getSelectedItem())) {
				LocalConfig.getInstance().setReversibleColumnIndex(getColumnNamesFromFile().indexOf(cbReversible.getSelectedItem()));
				usedIndices.add(getColumnNamesFromFile().indexOf(cbReversible.getSelectedItem()));
			}
			if (getColumnNamesFromFile().contains(cbLowerBound.getSelectedItem())) {
				LocalConfig.getInstance().setLowerBoundColumnIndex(getColumnNamesFromFile().indexOf(cbLowerBound.getSelectedItem()));
				usedIndices.add(getColumnNamesFromFile().indexOf(cbLowerBound.getSelectedItem()));
			}
			if (getColumnNamesFromFile().contains(cbUpperBound.getSelectedItem())) {
				LocalConfig.getInstance().setUpperBoundColumnIndex(getColumnNamesFromFile().indexOf(cbUpperBound.getSelectedItem()));
				usedIndices.add(getColumnNamesFromFile().indexOf(cbUpperBound.getSelectedItem()));
			}
			if (getColumnNamesFromFile().contains(cbBiologicalObjective.getSelectedItem())) {
				LocalConfig.getInstance().setBiologicalObjectiveColumnIndex(getColumnNamesFromFile().indexOf(cbBiologicalObjective.getSelectedItem()));
				usedIndices.add(getColumnNamesFromFile().indexOf(cbBiologicalObjective.getSelectedItem()));
			}
			if (getColumnNamesFromFile().contains(cbSyntheticObjective.getSelectedItem())) {
				LocalConfig.getInstance().setSyntheticObjectiveColumnIndex(getColumnNamesFromFile().indexOf(cbSyntheticObjective.getSelectedItem()));
				usedIndices.add(getColumnNamesFromFile().indexOf(cbSyntheticObjective.getSelectedItem()));
			}
			if (getColumnNamesFromFile().contains(cbGeneAssociation.getSelectedItem())) {
				LocalConfig.getInstance().setGeneAssociationColumnIndex(getColumnNamesFromFile().indexOf(cbGeneAssociation.getSelectedItem()));
				usedIndices.add(getColumnNamesFromFile().indexOf(cbGeneAssociation.getSelectedItem()));
			}
			if (getColumnNamesFromFile().contains(cbProteinAssociation.getSelectedItem())) {
				LocalConfig.getInstance().setProteinAssociationColumnIndex(getColumnNamesFromFile().indexOf(cbProteinAssociation.getSelectedItem()));
				usedIndices.add(getColumnNamesFromFile().indexOf(cbProteinAssociation.getSelectedItem()));
			}
			if (getColumnNamesFromFile().contains(cbSubsystem.getSelectedItem())) {
				LocalConfig.getInstance().setSubsystemColumnIndex(getColumnNamesFromFile().indexOf(cbSubsystem.getSelectedItem()));
				usedIndices.add(getColumnNamesFromFile().indexOf(cbSubsystem.getSelectedItem()));
			}
			if (getColumnNamesFromFile().contains(cbProteinClass.getSelectedItem())) {
				LocalConfig.getInstance().setProteinClassColumnIndex(getColumnNamesFromFile().indexOf(cbProteinClass.getSelectedItem()));
				usedIndices.add(getColumnNamesFromFile().indexOf(cbProteinClass.getSelectedItem()));
			}
			for (int i = 0; i < getColumnNamesFromFile().size(); i++) {
				if (!usedIndices.contains(i) && !getColumnNamesFromFile().get(i).equals(GraphicalInterfaceConstants.REACTIONS_COLUMN_IGNORE_LIST[0])) {
					metaColumnNames.add(getColumnNamesFromFile().get(i));
					metaColumnIndexList.add(getColumnNamesFromFile().indexOf(getColumnNamesFromFile().get(i)));
				} 
			}
			LocalConfig.getInstance().setReactionsMetaColumnNames(metaColumnNames);
			LocalConfig.getInstance().setReactionsMetaColumnIndexList(metaColumnIndexList);
		}		
	}
	
	public static void main(String[] args) throws Exception {
		//based on code from http:stackoverflow.com/questions/6403821/how-to-add-an-image-to-a-jframe-title-bar
		final ArrayList<Image> icons = new ArrayList<Image>(); 
		icons.add(new ImageIcon("etc/most16.jpg").getImage()); 
		icons.add(new ImageIcon("etc/most32.jpg").getImage());

		ArrayList<String> list = new ArrayList<String>();
		list.add("test");
		list.add("test");
		list.add("test");

		ReactionColumnNameInterface frame = new ReactionColumnNameInterface(list);

		frame.setIconImages(icons);
		//frame.setSize(600, 510);
		frame.setSize(600, 650);
		frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}




