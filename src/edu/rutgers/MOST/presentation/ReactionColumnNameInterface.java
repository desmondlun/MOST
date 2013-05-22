package edu.rutgers.MOST.presentation;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import edu.rutgers.MOST.config.ConfigConstants;
import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.data.DatabaseCreator;
import edu.rutgers.MOST.data.ReactionsMetaColumnManager;
import edu.rutgers.MOST.data.TextMetabolitesModelReader;
import edu.rutgers.MOST.data.TextReactionsModelReader;
import edu.rutgers.MOST.presentation.MetaboliteColumnNameInterface.Task;
import edu.rutgers.MOST.presentation.MetaboliteColumnNameInterface.TimeListener;

public class ReactionColumnNameInterface  extends JDialog {

	public JComboBox<String> cbKnockout = new JComboBox();
	public JComboBox<String> cbFluxValue = new JComboBox();
	public JComboBox<String> cbReactionAbbreviation = new JComboBox();
	public JComboBox<String> cbReactionName = new JComboBox();
	public JComboBox<String> cbReactionEquation = new JComboBox();
	public JComboBox<String> cbReversible = new JComboBox();
	public JComboBox<String> cbLowerBound = new JComboBox();
	public JComboBox<String> cbUpperBound = new JComboBox();
	public JComboBox<String> cbObjective = new JComboBox();
	public JComboBox<String> cbGeneAssociation = new JComboBox();

	public JButton okButton = new JButton("     OK     ");
	public JButton cancelButton = new JButton("  Cancel  ");
	public JButton prevRowButton = new JButton("Previous Row");
	public JButton nextRowButton = new JButton(" Next Row ");
	public JLabel rowLabel = new JLabel();

	public static ArrayList<String> columnNamesFromFile;

	public static ArrayList<String> getColumnNamesFromFile() {
		return columnNamesFromFile;
	}

	public void setColumnNamesFromFile(ArrayList<String> columnNamesFromFile) {
		this.columnNamesFromFile = columnNamesFromFile;
	}

	private Task task;

	public final ProgressBar progressBar = new ProgressBar();

	javax.swing.Timer timer = new javax.swing.Timer(1000, new TimeListener());

	public ReactionColumnNameInterface(final Connection con, ArrayList<String> columnNamesFromFile)
	throws SQLException {

		prevRowButton.setEnabled(false);
		LocalConfig.getInstance().setReactionsNextRowCorrection(0);
		rowLabel.setText("   row " + (LocalConfig.getInstance().getReactionsNextRowCorrection() + 1));
		
		final ArrayList<Image> icons = new ArrayList<Image>(); 
		icons.add(new ImageIcon("etc/most16.jpg").getImage()); 
		icons.add(new ImageIcon("etc/most32.jpg").getImage());
		
		getRootPane().setDefaultButton(okButton);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		int x = (screenSize.width - progressBar.getSize().width)/2;
		int y = (screenSize.height - progressBar.getSize().height)/2;
		
		LocalConfig.getInstance().setProgress(0);
		progressBar.pack();
		progressBar.setIconImages(icons);
		progressBar.setSize(200, 70);
		progressBar.setTitle("Loading...");
		progressBar.setLocation(x  - progressBar.getSize().width/2, y - 180);
		progressBar.setVisible(false);

		setColumnNamesFromFile(columnNamesFromFile);

		setTitle(ColumnInterfaceConstants.REACTIONS_COLUMN_NAME_INTERFACE_TITLE);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		//setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		cbReactionAbbreviation.setEditable(true);
		cbKnockout.setEditable(true);	
		cbFluxValue.setEditable(true);			
		cbReactionName.setEditable(true);
		cbReactionEquation.setEditable(true);
		cbReversible.setEditable(true);
		cbLowerBound.setEditable(true);
		cbUpperBound.setEditable(true);
		cbObjective.setEditable(true);
		cbGeneAssociation.setEditable(true);

		cbReactionAbbreviation.setPreferredSize(new Dimension(250, 30));
		cbReactionAbbreviation.setMaximumSize(new Dimension(250, 30));
		cbReactionAbbreviation.setMinimumSize(new Dimension(250, 30));
		
		cbKnockout.setPreferredSize(new Dimension(250, 30));
		cbKnockout.setMaximumSize(new Dimension(250, 30));
		cbKnockout.setMinimumSize(new Dimension(250, 30));

		cbFluxValue.setPreferredSize(new Dimension(250, 30));
		cbFluxValue.setMaximumSize(new Dimension(250, 30));
		cbFluxValue.setMinimumSize(new Dimension(250, 30));

		cbReactionName.setPreferredSize(new Dimension(250, 30));
		cbReactionName.setMaximumSize(new Dimension(250, 30));
		cbReactionName.setMinimumSize(new Dimension(250, 30));

		cbReactionEquation.setPreferredSize(new Dimension(250, 30));
		cbReactionEquation.setMaximumSize(new Dimension(250, 30));
		cbReactionEquation.setMinimumSize(new Dimension(250, 30));

		cbReversible.setPreferredSize(new Dimension(250, 30));
		cbReversible.setMaximumSize(new Dimension(250, 30));
		cbReversible.setMinimumSize(new Dimension(250, 30));

		cbLowerBound.setPreferredSize(new Dimension(250, 30));
		cbLowerBound.setMaximumSize(new Dimension(250, 30));
		cbLowerBound.setMinimumSize(new Dimension(250, 30));

		cbUpperBound.setPreferredSize(new Dimension(250, 30));
		cbUpperBound.setMaximumSize(new Dimension(250, 30));
		cbUpperBound.setMinimumSize(new Dimension(250, 30));

		cbObjective.setPreferredSize(new Dimension(250, 30));
		cbObjective.setMaximumSize(new Dimension(250, 30));
		cbObjective.setMinimumSize(new Dimension(250, 30));

		cbGeneAssociation.setPreferredSize(new Dimension(250, 30));
		cbGeneAssociation.setMaximumSize(new Dimension(250, 30));
		cbGeneAssociation.setMinimumSize(new Dimension(250, 30));
		
		JTextField fieldReactionAbbreviation = (JTextField)cbReactionAbbreviation.getEditor().getEditorComponent();
		fieldReactionAbbreviation.addKeyListener(new ComboKeyHandler(cbReactionAbbreviation));
		
		JTextField fieldKnockout = (JTextField)cbKnockout.getEditor().getEditorComponent();
		fieldKnockout.addKeyListener(new ComboKeyHandler(cbKnockout));

		JTextField fieldFluxValue = (JTextField)cbFluxValue.getEditor().getEditorComponent();
		fieldFluxValue.addKeyListener(new ComboKeyHandler(cbFluxValue));	

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

		JTextField fieldGeneAssociation = (JTextField)cbGeneAssociation.getEditor().getEditorComponent();
		fieldGeneAssociation.addKeyListener(new ComboKeyHandler(cbGeneAssociation));
		
		populateNamesFromFileBoxes(columnNamesFromFile);

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
		Box hb10 = Box.createHorizontalBox();
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
		Box hbObjectiveLabel = Box.createHorizontalBox();	    
		Box hbObjective = Box.createHorizontalBox();
		Box hbGeneAssociationLabel = Box.createHorizontalBox();
		Box hbGeneAssociation = Box.createHorizontalBox();

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

		//reaction abbreviation label and combo
		JLabel reactionAbbreviationLabel = new JLabel();
		reactionAbbreviationLabel.setText(ColumnInterfaceConstants.REACTION_ABBREVIATION_LABEL);
		reactionAbbreviationLabel.setSize(new Dimension(300, 20));	    
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

		hb1.add(hbReactionAbbreviationLabel);
		hb1.add(hbReactionAbbreviation);
		
		//knockout Label and combo
		JLabel knockoutLabel = new JLabel();
		knockoutLabel.setText(ColumnInterfaceConstants.KO_LABEL);
		knockoutLabel.setSize(new Dimension(300, 20));
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

		hb2.add(hbKnockoutLabel);
		hb2.add(hbKnockout);

		//flux value label and combo
		JLabel fluxValueLabel = new JLabel();
		fluxValueLabel.setText(ColumnInterfaceConstants.FLUX_VALUE_LABEL);
		fluxValueLabel.setSize(new Dimension(300, 20));	    
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

		hb3.add(hbFluxValueLabel);
		hb3.add(hbFluxValue);

		//reaction name label and combo
		JLabel reactionNameLabel = new JLabel();
		reactionNameLabel.setText(ColumnInterfaceConstants.REACTION_NAME_LABEL);
		reactionNameLabel.setSize(new Dimension(300, 20));	    
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

		hb4.add(hbReactionNameLabel);
		hb4.add(hbReactionName);

		//reaction equation label and combo
		JLabel reactionEquationLabel = new JLabel();
		reactionEquationLabel.setText(ColumnInterfaceConstants.REACTION_EQUATION_LABEL);
		reactionEquationLabel.setSize(new Dimension(300, 20));	    
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

		hb5.add(hbReactionEquationLabel);
		hb5.add(hbReactionEquation);

		//reversible label and combo
		JLabel reversibleLabel = new JLabel();
		reversibleLabel.setText(ColumnInterfaceConstants.REVERSIBLE_LABEL);
		reversibleLabel.setSize(new Dimension(300, 20));	    
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

		hb6.add(hbReversibleLabel);
		hb6.add(hbReversible);

		//lower bound label and combo
		JLabel lowerBoundLabel = new JLabel();
		lowerBoundLabel.setText(ColumnInterfaceConstants.LOWER_BOUND_LABEL);
		lowerBoundLabel.setSize(new Dimension(300, 20));	    
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

		hb7.add(hbLowerBoundLabel);
		hb7.add(hbLowerBound);

		//upper bound label and combo
		JLabel upperBoundLabel = new JLabel();
		upperBoundLabel.setText(ColumnInterfaceConstants.UPPER_BOUND_LABEL);
		upperBoundLabel.setSize(new Dimension(300, 20));	    
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

		hb8.add(hbUpperBoundLabel);
		hb8.add(hbUpperBound);

		//objective label and combo
		JLabel objectiveLabel = new JLabel();
		objectiveLabel.setText(ColumnInterfaceConstants.BIOLOGICAL_OBJECTIVE_LABEL);
		objectiveLabel.setSize(new Dimension(300, 20));	    
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

		hb9.add(hbObjectiveLabel);
		hb9.add(hbObjective);

		//gene association label and combo
		JLabel geneAssociationLabel = new JLabel();
		geneAssociationLabel.setText(ColumnInterfaceConstants.GENE_ASSOCIATION_LABEL);
		geneAssociationLabel.setSize(new Dimension(300, 20));	    
		geneAssociationLabel.setBorder(BorderFactory.createEmptyBorder(0,0,0,80));
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

		hb10.add(hbGeneAssociationLabel);
		hb10.add(hbGeneAssociation);
		
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
		vb.add(hb1);
		vb.add(hb2);
		vb.add(hb3);
		vb.add(hb4);
		vb.add(hb5);
		vb.add(hb6);
		vb.add(hb7);
		vb.add(hb8);
		vb.add(hb9);
		vb.add(hb10);
		vb.add(hbRequiredLabel);
		vb.add(hbButton);

		add(vb);
		
		ActionListener okButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				if (cbReactionAbbreviation.getSelectedIndex() == -1 || cbReactionEquation.getSelectedIndex() == -1) {
					JOptionPane.showMessageDialog(null,                
							ColumnInterfaceConstants.BLANK_REACTION_FIELDS_ERROR_MESSAGE,
							ColumnInterfaceConstants.BLANK_REACTION_FIELDS_ERROR_TITLE,                                
							JOptionPane.ERROR_MESSAGE);
				} else {
					//add metacolumn names to db
					ReactionsMetaColumnManager reactionsMetaColumnManager = new ReactionsMetaColumnManager();
					ArrayList<String> metaColumnNames = new ArrayList();
					ArrayList<Integer> usedIndices = new ArrayList();
					ArrayList<Integer> metaColumnIndexList = new ArrayList();

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
					if (getColumnNamesFromFile().contains(cbObjective.getSelectedItem())) {
						LocalConfig.getInstance().setBiologicalObjectiveColumnIndex(getColumnNamesFromFile().indexOf(cbObjective.getSelectedItem()));
						usedIndices.add(getColumnNamesFromFile().indexOf(cbObjective.getSelectedItem()));
					}
					if (getColumnNamesFromFile().contains(cbGeneAssociation.getSelectedItem())) {
						LocalConfig.getInstance().setGeneAssociationColumnIndex(getColumnNamesFromFile().indexOf(cbGeneAssociation.getSelectedItem()));
						usedIndices.add(getColumnNamesFromFile().indexOf(cbGeneAssociation.getSelectedItem()));
					}
					for (int i = 0; i < getColumnNamesFromFile().size(); i++) {
						if (!usedIndices.contains(i)) {
							metaColumnNames.add(getColumnNamesFromFile().get(i));
							metaColumnIndexList.add(getColumnNamesFromFile().indexOf(getColumnNamesFromFile().get(i)));
						} 
					}

					if (LocalConfig.getInstance().hasMetabolitesFile == false) {
						DatabaseCreator creator = new DatabaseCreator();
						creator.createDatabase(LocalConfig.getInstance().getDatabaseName());
					}
					reactionsMetaColumnManager.addColumnNames(LocalConfig.getInstance().getDatabaseName(), metaColumnNames);
					LocalConfig.getInstance().setReactionsMetaColumnIndexList(metaColumnIndexList);
					
					setVisible(false);
					dispose();
					
					progressBar.setVisible(true);

					timer.start();

					task = new Task();
					task.execute();
				}				
			}
		};
				
		okButton.addActionListener(okButtonActionListener);

		ActionListener cancelButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				setVisible(false);
				dispose();
				if (LocalConfig.getInstance().getCurrentConnection() != null) {
		        	try {
						LocalConfig.getInstance().getCurrentConnection().close();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		        }
				//this is a hack, same as clear method in gui
				try {
					Class.forName("org.sqlite.JDBC");       
					DatabaseCreator databaseCreator = new DatabaseCreator();
					LocalConfig.getInstance().setDatabaseName(ConfigConstants.DEFAULT_DATABASE_NAME);
					Connection con = DriverManager.getConnection("jdbc:sqlite:" + ConfigConstants.DEFAULT_DATABASE_NAME + ".db");
					LocalConfig.getInstance().setCurrentConnection(con);
					databaseCreator.createDatabase(LocalConfig.getInstance().getDatabaseName());
					databaseCreator.addRows(LocalConfig.getInstance().getDatabaseName(), GraphicalInterfaceConstants.BLANK_DB_METABOLITE_ROW_COUNT, GraphicalInterfaceConstants.BLANK_DB_REACTION_ROW_COUNT);
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
		LocalConfig.getInstance().setReversibleColumnIndex(-1);
		LocalConfig.getInstance().setLowerBoundColumnIndex(-1);
		LocalConfig.getInstance().setUpperBoundColumnIndex(-1);
		LocalConfig.getInstance().setBiologicalObjectiveColumnIndex(-1);
		LocalConfig.getInstance().setGeneAssociationColumnIndex(-1);
		
		cbReactionAbbreviation.removeAllItems();
		cbKnockout.removeAllItems();
		cbFluxValue.removeAllItems();		
		cbReactionName.removeAllItems();
		cbReactionEquation.removeAllItems();
		cbReversible.removeAllItems();
		cbLowerBound.removeAllItems();
		cbUpperBound.removeAllItems();
		cbObjective.removeAllItems();
		cbGeneAssociation.removeAllItems();
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
			cbObjective.addItem(columnNamesFromFile.get(c));
			cbGeneAssociation.addItem(columnNamesFromFile.get(c));
		}	
		cbReactionAbbreviation.setSelectedIndex(-1);
		cbKnockout.setSelectedIndex(-1);
		cbFluxValue.setSelectedIndex(-1);		
		cbReactionName.setSelectedIndex(-1);
		cbReactionEquation.setSelectedIndex(-1);
		cbReversible.setSelectedIndex(-1);	
		cbLowerBound.setSelectedIndex(-1);
		cbUpperBound.setSelectedIndex(-1);
		cbObjective.setSelectedIndex(-1);
		cbGeneAssociation.setSelectedIndex(-1);
		
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
			} else if((columnNamesFromFile.get(c).toLowerCase()).compareTo(GraphicalInterfaceConstants.LOWER_BOUND_FILTER[0]) == 0 || (columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.LOWER_BOUND_FILTER[1])) {
				cbLowerBound.setSelectedIndex(c);
				LocalConfig.getInstance().setLowerBoundColumnIndex(c); 
			} else if((columnNamesFromFile.get(c).toLowerCase()).compareTo(GraphicalInterfaceConstants.UPPER_BOUND_FILTER[0]) == 0 || (columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.UPPER_BOUND_FILTER[1])) {
				cbUpperBound.setSelectedIndex(c);
				LocalConfig.getInstance().setUpperBoundColumnIndex(c); 
			} else if((columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_FILTER[0]) && !(columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_NOT_FILTER[0])) {
				cbObjective.setSelectedIndex(c);
				LocalConfig.getInstance().setBiologicalObjectiveColumnIndex(c); 	
			} else if((columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.GENE_ASSOCIATION_COLUMN_FILTER[0]) && (columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.GENE_ASSOCIATION_COLUMN_FILTER[1])) {
				cbGeneAssociation.setSelectedIndex(c);
				LocalConfig.getInstance().setGeneAssociationColumnIndex(c);
			} else if((columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.ABBREVIATION_COLUMN_FILTER[0]) || (columnNamesFromFile.get(c).toLowerCase()).compareTo(GraphicalInterfaceConstants.ABBREVIATION_COLUMN_FILTER[1]) == 0) {
				cbReactionAbbreviation.setSelectedIndex(c);
				LocalConfig.getInstance().setReactionAbbreviationColumnIndex(c); 
			} else if((columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.NAME_COLUMN_FILTER[0])) {
				cbReactionName.setSelectedIndex(c);
				LocalConfig.getInstance().setReactionNameColumnIndex(c); 
			} else if((columnNamesFromFile.get(c).toLowerCase()).contains(GraphicalInterfaceConstants.EQUATION_COLUMN_FILTER[0]) || (columnNamesFromFile.get(c).toLowerCase()).equals(GraphicalInterfaceConstants.EQUATION_COLUMN_FILTER[1])) {
				cbReactionEquation.setSelectedIndex(c);
				LocalConfig.getInstance().setReactionEquationColumnIndex(c); 
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


	class Task extends SwingWorker<Void, Void> {

		@Override
		public void done() {

		}

		@Override
		protected Void doInBackground() throws Exception {
			int progress = 0;
			TextReactionsModelReader reader = new TextReactionsModelReader();
			reader.load(LocalConfig.getInstance().getReactionsCSVFile(), LocalConfig.getInstance().getDatabaseName());	
			while (progress < 100) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ignore) {
				}			
			}
			timer.stop();
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

		//based on code from http:stackoverflow.com/questions/6403821/how-to-add-an-image-to-a-jframe-title-bar
		final ArrayList<Image> icons = new ArrayList<Image>(); 
		icons.add(new ImageIcon("images/most16.jpg").getImage()); 
		icons.add(new ImageIcon("images/most32.jpg").getImage());

		ArrayList<String> list = new ArrayList();
		list.add("test");
		list.add("test");
		list.add("test");

		ReactionColumnNameInterface frame = new ReactionColumnNameInterface(con, list);

		frame.setIconImages(icons);
		//frame.setSize(600, 510);
		frame.setSize(600, 550);
		frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}




