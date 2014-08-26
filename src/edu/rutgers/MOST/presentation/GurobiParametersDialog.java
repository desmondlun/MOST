package edu.rutgers.MOST.presentation;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
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
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class GurobiParametersDialog  extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JLabel feasibilityLabel = new JLabel();
	public JTextField feasibilityField = new JTextField();
	private JLabel intFeasibilityLabel = new JLabel();
	public JTextField intFeasibilityField = new JTextField();
	private JLabel optimalityLabel = new JLabel();
	public JTextField optimalityField = new JTextField();
	private JLabel heuristicsLabel = new JLabel();
	public JTextField heuristicsField = new JTextField();
	private JLabel mipFocusLabel = new JLabel();
	private JComboBox<Integer> cbMIPFocus = new JComboBox<Integer>();
	private JLabel numThreadsLabel = new JLabel();
	public JComboBox<Integer> cbNumThreads = new JComboBox<Integer>();
	public JButton okButton = new JButton("   OK   ");
	public JButton cancelButton = new JButton("Cancel");
	public JButton resetButton = new JButton("Reset to Defaults");
	
	private boolean threadsPopulated;
	
	private double feasibility;
	private double intFeasibility;
	private double optimality;
	private double heuristics;
	
	private String errorMessage;

	public double getFeasibility() {
		return feasibility;
	}

	public void setFeasibility(double feasibility) {
		this.feasibility = feasibility;
	}

	public double getIntFeasibility() {
		return intFeasibility;
	}

	public void setIntFeasibility(double intFeasibility) {
		this.intFeasibility = intFeasibility;
	}

	public double getOptimality() {
		return optimality;
	}

	public void setOptimality(double optimality) {
		this.optimality = optimality;
	}

	public double getHeuristics() {
		return heuristics;
	}

	public void setHeuristics(double heuristics) {
		this.heuristics = heuristics;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public GurobiParametersDialog() {
		
		final ArrayList<Image> icons = new ArrayList<Image>(); 
		icons.add(new ImageIcon("etc/most16.jpg").getImage()); 
		icons.add(new ImageIcon("etc/most32.jpg").getImage());
		
		getRootPane().setDefaultButton(okButton);

		setTitle(GurobiParameters.TITLE);
		
		feasibilityField.setToolTipText("Valid range: " + GurobiParameters.FEASIBILITYTOL_MINIMUM_VALUE +
				" to " + GurobiParameters.FEASIBILITYTOL_MAXIMUM_VALUE);
		intFeasibilityField.setToolTipText("Valid range: " + GurobiParameters.INTFEASIBILITYTOL_MINIMUM_VALUE +
				" to " + GurobiParameters.INTFEASIBILITYTOL_MAXIMUM_VALUE);
		optimalityField.setToolTipText("Valid range: " + GurobiParameters.OPTIMALITYTOL_MINIMUM_VALUE +
				" to " + GurobiParameters.OPTIMALITYTOL_MAXIMUM_VALUE);
		heuristicsField.setToolTipText("Valid range: " + GurobiParameters.HEURISTICS_MINIMUM_VALUE +
				" to " + GurobiParameters.HEURISTICS_MAXIMUM_VALUE);
		cbMIPFocus.setEditable(false);
		cbNumThreads.setEditable(false);
		
		threadsPopulated = false;
		
		for (int i = 0; i <= GurobiParameters.MIPFOCUS_MAXIMUM_VALUE; i++) {
			cbMIPFocus.addItem(i);
		}
		cbMIPFocus.setSelectedItem(GurobiParameters.MIPFOCUS_DEFAULT_VALUE);
		
		for (int i = 1; i <= GurobiParameters.MAX_NUM_THREADS; i++) {
			cbNumThreads.addItem(i);
		}
		threadsPopulated = true;
		cbNumThreads.setSelectedItem(GurobiParameters.MAX_NUM_THREADS);
		
		feasibilityField.setPreferredSize(new Dimension(GurobiParameters.COMPONENT_WIDTH, GurobiParameters.COMPONENT_HEIGHT));
		feasibilityField.setMaximumSize(new Dimension(GurobiParameters.COMPONENT_WIDTH, GurobiParameters.COMPONENT_HEIGHT));
		feasibilityField.setMinimumSize(new Dimension(GurobiParameters.COMPONENT_WIDTH, GurobiParameters.COMPONENT_HEIGHT));
		
		setFeasibilityDefaultValue();
		
		feasibilityField.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {	
				Component c = e.getComponent();
				if (c instanceof JTextField) {
		            ((JTextField)c).selectAll();
		        }				
			}

			@Override
			public void focusLost(FocusEvent arg0) {				
			}
		});
		
		feasibilityField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				feasibilityField.setForeground(Color.BLACK);
			}
			public void removeUpdate(DocumentEvent e) {
				feasibilityField.setForeground(Color.BLACK);
			}
			public void insertUpdate(DocumentEvent e) {
				feasibilityField.setForeground(Color.BLACK);
			}
		});
		
		intFeasibilityField.setPreferredSize(new Dimension(GurobiParameters.COMPONENT_WIDTH, GurobiParameters.COMPONENT_HEIGHT));
		intFeasibilityField.setMaximumSize(new Dimension(GurobiParameters.COMPONENT_WIDTH, GurobiParameters.COMPONENT_HEIGHT));
		intFeasibilityField.setMinimumSize(new Dimension(GurobiParameters.COMPONENT_WIDTH, GurobiParameters.COMPONENT_HEIGHT));
		
		setIntFeasibilityDefaultValue();
		
		intFeasibilityField.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {	
				Component c = e.getComponent();
				if (c instanceof JTextField) {
		            ((JTextField)c).selectAll();
		        }				
			}

			@Override
			public void focusLost(FocusEvent arg0) {				
			}
		});
		
		intFeasibilityField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				intFeasibilityField.setForeground(Color.BLACK);
			}
			public void removeUpdate(DocumentEvent e) {
				intFeasibilityField.setForeground(Color.BLACK);
			}
			public void insertUpdate(DocumentEvent e) {
				intFeasibilityField.setForeground(Color.BLACK);
			}
		});
		
		optimalityField.setPreferredSize(new Dimension(GurobiParameters.COMPONENT_WIDTH, GurobiParameters.COMPONENT_HEIGHT));
		optimalityField.setMaximumSize(new Dimension(GurobiParameters.COMPONENT_WIDTH, GurobiParameters.COMPONENT_HEIGHT));
		optimalityField.setMinimumSize(new Dimension(GurobiParameters.COMPONENT_WIDTH, GurobiParameters.COMPONENT_HEIGHT));
		
		setOptimalityDefaultValue();
		
		optimalityField.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {	
				Component c = e.getComponent();
				if (c instanceof JTextField) {
		            ((JTextField)c).selectAll();
		        }				
			}

			@Override
			public void focusLost(FocusEvent arg0) {				
			}
		});
		
		optimalityField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				optimalityField.setForeground(Color.BLACK);
			}
			public void removeUpdate(DocumentEvent e) {
				optimalityField.setForeground(Color.BLACK);
			}
			public void insertUpdate(DocumentEvent e) {
				optimalityField.setForeground(Color.BLACK);
			}
		});
		
		heuristicsField.setPreferredSize(new Dimension(GurobiParameters.COMPONENT_WIDTH, GurobiParameters.COMPONENT_HEIGHT));
		heuristicsField.setMaximumSize(new Dimension(GurobiParameters.COMPONENT_WIDTH, GurobiParameters.COMPONENT_HEIGHT));
		heuristicsField.setMinimumSize(new Dimension(GurobiParameters.COMPONENT_WIDTH, GurobiParameters.COMPONENT_HEIGHT));
		
		setHeuristicsDefaultValue();
		
		heuristicsField.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {	
				Component c = e.getComponent();
				if (c instanceof JTextField) {
		            ((JTextField)c).selectAll();
		        }				
			}

			@Override
			public void focusLost(FocusEvent arg0) {				
			}
		});
		
		heuristicsField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				heuristicsField.setForeground(Color.BLACK);
			}
			public void removeUpdate(DocumentEvent e) {
				heuristicsField.setForeground(Color.BLACK);
			}
			public void insertUpdate(DocumentEvent e) {
				heuristicsField.setForeground(Color.BLACK);
			}
		});
		
		cbMIPFocus.setPreferredSize(new Dimension(GurobiParameters.COMPONENT_WIDTH, GurobiParameters.COMPONENT_HEIGHT));
		cbMIPFocus.setMaximumSize(new Dimension(GurobiParameters.COMPONENT_WIDTH, GurobiParameters.COMPONENT_HEIGHT));
		cbMIPFocus.setMinimumSize(new Dimension(GurobiParameters.COMPONENT_WIDTH, GurobiParameters.COMPONENT_HEIGHT));

		cbNumThreads.setPreferredSize(new Dimension(GurobiParameters.COMPONENT_WIDTH, GurobiParameters.COMPONENT_HEIGHT));
		cbNumThreads.setMaximumSize(new Dimension(GurobiParameters.COMPONENT_WIDTH, GurobiParameters.COMPONENT_HEIGHT));
		cbNumThreads.setMinimumSize(new Dimension(GurobiParameters.COMPONENT_WIDTH, GurobiParameters.COMPONENT_HEIGHT));
		
		//box layout
		Box vb = Box.createVerticalBox();
   	    
		Box hbTopLabel = Box.createHorizontalBox();
		Box hbTop = Box.createHorizontalBox();
		Box hbFeasibilityLabel = Box.createHorizontalBox();	    
		Box hbFeasibility = Box.createHorizontalBox();
		Box hbIntFeasibilityLabel = Box.createHorizontalBox();	    
		Box hbIntFeasibility = Box.createHorizontalBox();
		Box hbOptimalityLabel = Box.createHorizontalBox();	    
		Box hbOptimality = Box.createHorizontalBox();
		Box hbHeuristicsLabel = Box.createHorizontalBox();	    
		Box hbHeuristics = Box.createHorizontalBox();
		Box hbMIPFocusLabel = Box.createHorizontalBox();	
		Box hbMIPFocus = Box.createHorizontalBox();
		Box hbNumThreadsLabel = Box.createHorizontalBox();	
		Box hbNumThreads = Box.createHorizontalBox();	
		
		Box vbLabels = Box.createVerticalBox();
		Box vbCombos = Box.createVerticalBox();

		Box hbLabeledComponents = Box.createHorizontalBox();
		Box hbButton = Box.createHorizontalBox();

		//top label
		JLabel topLabel = new JLabel("");
		topLabel.setSize(new Dimension(300, 10));
		//top, left, bottom. right
		topLabel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		topLabel.setAlignmentX(LEFT_ALIGNMENT);

		hbTop.add(topLabel);	
		hbTop.setAlignmentX(LEFT_ALIGNMENT);

		hbTopLabel.add(hbTop);
		
		//Feasibility Label and combo
		feasibilityLabel.setText(GurobiParameters.FEASIBILITYTOL_NAME);
		feasibilityLabel.setPreferredSize(new Dimension(GurobiParameters.LABEL_WIDTH, GurobiParameters.LABEL_HEIGHT));
		feasibilityLabel.setMaximumSize(new Dimension(GurobiParameters.LABEL_WIDTH, GurobiParameters.LABEL_HEIGHT));
		feasibilityLabel.setMinimumSize(new Dimension(GurobiParameters.LABEL_WIDTH, GurobiParameters.LABEL_HEIGHT));
		feasibilityLabel.setBorder(BorderFactory.createEmptyBorder(10,0,GurobiParameters.LABEL_BOTTOM_BORDER_SIZE,10));
		feasibilityLabel.setAlignmentX(LEFT_ALIGNMENT);
		//feasibilityLabel.setAlignmentY(TOP_ALIGNMENT);
		feasibilityLabel.setDisplayedMnemonic('F');
		feasibilityLabel.setLabelFor(feasibilityField);

		JPanel panelFeasibilityLabel = new JPanel();
		panelFeasibilityLabel.setLayout(new BoxLayout(panelFeasibilityLabel, BoxLayout.X_AXIS));
		panelFeasibilityLabel.add(feasibilityLabel);
		panelFeasibilityLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbFeasibilityLabel.add(panelFeasibilityLabel);
		hbFeasibilityLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelFeasibility = new JPanel();
		panelFeasibility.setLayout(new BoxLayout(panelFeasibility, BoxLayout.X_AXIS));
		panelFeasibility.add(feasibilityField);
		panelFeasibility.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		panelFeasibility.setAlignmentX(RIGHT_ALIGNMENT);

		hbFeasibility.add(panelFeasibility);
		hbFeasibility.setAlignmentX(RIGHT_ALIGNMENT);

		vbLabels.add(hbFeasibilityLabel);
		JLabel blankLabel1 = new JLabel("");
		vbLabels.add(blankLabel1);
		vbCombos.add(hbFeasibility);
		
		//Int Feasibility Label and combo
		intFeasibilityLabel.setText(GurobiParameters.INTFEASIBILITYTOL_NAME);
		intFeasibilityLabel.setPreferredSize(new Dimension(GurobiParameters.LABEL_WIDTH, GurobiParameters.LABEL_HEIGHT));
		intFeasibilityLabel.setMaximumSize(new Dimension(GurobiParameters.LABEL_WIDTH, GurobiParameters.LABEL_HEIGHT));
		intFeasibilityLabel.setMinimumSize(new Dimension(GurobiParameters.LABEL_WIDTH, GurobiParameters.LABEL_HEIGHT));
		intFeasibilityLabel.setBorder(BorderFactory.createEmptyBorder(10,0,GurobiParameters.LABEL_BOTTOM_BORDER_SIZE,10));
		intFeasibilityLabel.setAlignmentX(LEFT_ALIGNMENT);
		//intFeasibilityLabel.setAlignmentY(TOP_ALIGNMENT);
		intFeasibilityLabel.setDisplayedMnemonic('I');
		intFeasibilityLabel.setLabelFor(intFeasibilityField);

		JPanel panelIntFeasibilityLabel = new JPanel();
		panelIntFeasibilityLabel.setLayout(new BoxLayout(panelIntFeasibilityLabel, BoxLayout.X_AXIS));
		panelIntFeasibilityLabel.add(intFeasibilityLabel);
		panelIntFeasibilityLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbIntFeasibilityLabel.add(panelIntFeasibilityLabel);
		hbIntFeasibilityLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelIntFeasibility = new JPanel();
		panelIntFeasibility.setLayout(new BoxLayout(panelIntFeasibility, BoxLayout.X_AXIS));
		panelIntFeasibility.add(intFeasibilityField);
		panelIntFeasibility.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		panelIntFeasibility.setAlignmentX(RIGHT_ALIGNMENT);

		hbIntFeasibility.add(panelIntFeasibility);
		hbIntFeasibility.setAlignmentX(RIGHT_ALIGNMENT);

		vbLabels.add(hbIntFeasibilityLabel);
		JLabel blankLabel2 = new JLabel("");
		vbLabels.add(blankLabel2);
		vbCombos.add(hbIntFeasibility);
		
		//optimality Label and combo
		optimalityLabel.setText(GurobiParameters.OPTIMALITYTOL_NAME);
		optimalityLabel.setPreferredSize(new Dimension(GurobiParameters.LABEL_WIDTH, GurobiParameters.LABEL_HEIGHT));
		optimalityLabel.setMaximumSize(new Dimension(GurobiParameters.LABEL_WIDTH, GurobiParameters.LABEL_HEIGHT));
		optimalityLabel.setMinimumSize(new Dimension(GurobiParameters.LABEL_WIDTH, GurobiParameters.LABEL_HEIGHT));
		optimalityLabel.setBorder(BorderFactory.createEmptyBorder(10,0,GurobiParameters.LABEL_BOTTOM_BORDER_SIZE,10));
		optimalityLabel.setAlignmentX(LEFT_ALIGNMENT);
		//optimalityLabel.setAlignmentY(TOP_ALIGNMENT);
		optimalityLabel.setDisplayedMnemonic('D');
		optimalityLabel.setLabelFor(optimalityField);

		JPanel panelOptimalityLabel = new JPanel();
		panelOptimalityLabel.setLayout(new BoxLayout(panelOptimalityLabel, BoxLayout.X_AXIS));
		panelOptimalityLabel.add(optimalityLabel);
		panelOptimalityLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbOptimalityLabel.add(panelOptimalityLabel);
		hbOptimalityLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelOptimality = new JPanel();
		panelOptimality.setLayout(new BoxLayout(panelOptimality, BoxLayout.X_AXIS));
		panelOptimality.add(optimalityField);
		panelOptimality.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		panelOptimality.setAlignmentX(RIGHT_ALIGNMENT);

		hbOptimality.add(panelOptimality);
		hbOptimality.setAlignmentX(RIGHT_ALIGNMENT);

		vbLabels.add(hbOptimalityLabel);
		JLabel blankLabel3 = new JLabel("");
		vbLabels.add(blankLabel3);
		vbCombos.add(hbOptimality);
		
		//heuristics Label and combo
		heuristicsLabel.setText(GurobiParameters.HEURISTICS_NAME);
		heuristicsLabel.setPreferredSize(new Dimension(GurobiParameters.LABEL_WIDTH, GurobiParameters.LABEL_HEIGHT));
		heuristicsLabel.setMaximumSize(new Dimension(GurobiParameters.LABEL_WIDTH, GurobiParameters.LABEL_HEIGHT));
		heuristicsLabel.setMinimumSize(new Dimension(GurobiParameters.LABEL_WIDTH, GurobiParameters.LABEL_HEIGHT));
		heuristicsLabel.setBorder(BorderFactory.createEmptyBorder(10,0,GurobiParameters.LABEL_BOTTOM_BORDER_SIZE,10));
		heuristicsLabel.setAlignmentX(LEFT_ALIGNMENT);
		//heuristicsLabel.setAlignmentY(TOP_ALIGNMENT);
		heuristicsLabel.setDisplayedMnemonic('H');
		heuristicsLabel.setLabelFor(heuristicsField);

		JPanel panelHeuristicsLabel = new JPanel();
		panelHeuristicsLabel.setLayout(new BoxLayout(panelHeuristicsLabel, BoxLayout.X_AXIS));
		panelHeuristicsLabel.add(heuristicsLabel);
		panelHeuristicsLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbHeuristicsLabel.add(panelHeuristicsLabel);
		hbHeuristicsLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelHeuristics = new JPanel();
		panelHeuristics.setLayout(new BoxLayout(panelHeuristics, BoxLayout.X_AXIS));
		panelHeuristics.add(heuristicsField);
		panelHeuristics.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		panelHeuristics.setAlignmentX(RIGHT_ALIGNMENT);

		hbHeuristics.add(panelHeuristics);
		hbHeuristics.setAlignmentX(RIGHT_ALIGNMENT);

		vbLabels.add(hbHeuristicsLabel);
		JLabel blankLabel4 = new JLabel("");
		vbLabels.add(blankLabel4);
		vbCombos.add(hbHeuristics);
		
		//MIPFocus Label and combo
		mipFocusLabel.setText(GurobiParameters.MIPFOCUS_NAME);
		mipFocusLabel.setPreferredSize(new Dimension(GurobiParameters.LABEL_WIDTH, GurobiParameters.LABEL_HEIGHT));
		mipFocusLabel.setMaximumSize(new Dimension(GurobiParameters.LABEL_WIDTH, GurobiParameters.LABEL_HEIGHT));
		mipFocusLabel.setMinimumSize(new Dimension(GurobiParameters.LABEL_WIDTH, GurobiParameters.LABEL_HEIGHT));
		mipFocusLabel.setBorder(BorderFactory.createEmptyBorder(GurobiParameters.LABEL_TOP_BORDER_SIZE,0,GurobiParameters.LABEL_BOTTOM_BORDER_SIZE,10));
		mipFocusLabel.setAlignmentX(LEFT_ALIGNMENT);
		mipFocusLabel.setDisplayedMnemonic('M');
		mipFocusLabel.setLabelFor(cbMIPFocus);

		JPanel panelMIPFocusLabel = new JPanel();
		panelMIPFocusLabel.setLayout(new BoxLayout(panelMIPFocusLabel, BoxLayout.X_AXIS));
		panelMIPFocusLabel.add(mipFocusLabel);
		panelMIPFocusLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbMIPFocusLabel.add(panelMIPFocusLabel);
		hbMIPFocusLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelMIPFocus = new JPanel();
		panelMIPFocus.setLayout(new BoxLayout(panelMIPFocus, BoxLayout.X_AXIS));
		panelMIPFocus.add(cbMIPFocus);
		panelMIPFocus.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		panelMIPFocus.setAlignmentX(RIGHT_ALIGNMENT);

		hbMIPFocus.add(panelMIPFocus);
		hbMIPFocus.setAlignmentX(RIGHT_ALIGNMENT);

		vbLabels.add(hbMIPFocusLabel);
		JLabel blankLabel5 = new JLabel("");
		vbLabels.add(blankLabel5);
		vbCombos.add(hbMIPFocus);

		//Number of Threads Label and combo
		numThreadsLabel.setText(GurobiParameters.NUM_THREADS_NAME);
		numThreadsLabel.setPreferredSize(new Dimension(GurobiParameters.LABEL_WIDTH, GurobiParameters.LABEL_HEIGHT));
		numThreadsLabel.setMaximumSize(new Dimension(GurobiParameters.LABEL_WIDTH, GurobiParameters.LABEL_HEIGHT));
		numThreadsLabel.setMinimumSize(new Dimension(GurobiParameters.LABEL_WIDTH, GurobiParameters.LABEL_HEIGHT));
		numThreadsLabel.setBorder(BorderFactory.createEmptyBorder(GurobiParameters.LABEL_TOP_BORDER_SIZE,0,GurobiParameters.LABEL_BOTTOM_BORDER_SIZE,10));
		numThreadsLabel.setAlignmentX(LEFT_ALIGNMENT);
		numThreadsLabel.setDisplayedMnemonic('T');
		numThreadsLabel.setLabelFor(cbNumThreads);

		JPanel panelNumThreadsLabel = new JPanel();
		panelNumThreadsLabel.setLayout(new BoxLayout(panelNumThreadsLabel, BoxLayout.X_AXIS));
		panelNumThreadsLabel.add(numThreadsLabel);
		panelNumThreadsLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbNumThreadsLabel.add(panelNumThreadsLabel);
		hbNumThreadsLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelNumThreads = new JPanel();
		panelNumThreads.setLayout(new BoxLayout(panelNumThreads, BoxLayout.X_AXIS));
		panelNumThreads.add(cbNumThreads);
		panelNumThreads.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		panelNumThreads.setAlignmentX(RIGHT_ALIGNMENT);

		hbNumThreads.add(panelNumThreads);
		hbNumThreads.setAlignmentX(RIGHT_ALIGNMENT);

		vbLabels.add(hbNumThreadsLabel);
		JLabel blankLabel6 = new JLabel("");
		vbLabels.add(blankLabel6);
		vbCombos.add(hbNumThreads);

		okButton.setMnemonic(KeyEvent.VK_O);
		JLabel blank = new JLabel("    "); 
		cancelButton.setMnemonic(KeyEvent.VK_C);
		cancelButton.setEnabled(false);
		JLabel blank2 = new JLabel("    "); 
		resetButton.setMnemonic(KeyEvent.VK_R);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(okButton);
		buttonPanel.add(blank);
		buttonPanel.add(cancelButton);
		buttonPanel.add(blank2);
		buttonPanel.add(resetButton);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,20,20,20));

		hbButton.add(buttonPanel);

		vb.add(hbTopLabel);
		hbLabeledComponents.add(vbLabels);
		hbLabeledComponents.add(vbCombos);
		vb.add(hbLabeledComponents);		
		vb.add(hbButton);

		add(vb);
		
		ActionListener cancelButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				setVisible(false);
				dispose();
			}
		};

		cancelButton.addActionListener(cancelButtonActionListener);
		
		ActionListener resetButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				setFeasibilityDefaultValue();
				setIntFeasibilityDefaultValue();
				setOptimalityDefaultValue();
				setHeuristicsDefaultValue();
				feasibilityField.setForeground(Color.BLACK);
				intFeasibilityField.setForeground(Color.BLACK);
				optimalityField.setForeground(Color.BLACK);
				heuristicsField.setForeground(Color.BLACK);
				cbMIPFocus.setSelectedItem(GurobiParameters.MIPFOCUS_DEFAULT_VALUE);
				
				// Gurobi Parameters Dialog sends information to only Gurobi
				if (!threadsPopulated) {
					for (int i = 1; i <= GurobiParameters.MAX_NUM_THREADS; i++) {
						cbNumThreads.addItem(i);
					}
				}
				cbNumThreads.setSelectedItem(GurobiParameters.MAX_NUM_THREADS);
			}
		};

		resetButton.addActionListener(resetButtonActionListener);
	
	}
	
	public void setFeasibilityDefaultValue() {
		feasibilityField.setText(Double.toString(GurobiParameters.FEASIBILITYTOL_DEFAULT_VALUE));
		setFeasibility(GurobiParameters.FEASIBILITYTOL_DEFAULT_VALUE);
	}
	
	public void setIntFeasibilityDefaultValue() {
		intFeasibilityField.setText(Double.toString(GurobiParameters.INTFEASIBILITYTOL_DEFAULT_VALUE));
		setIntFeasibility(GurobiParameters.INTFEASIBILITYTOL_DEFAULT_VALUE);
	}
	
	public void setOptimalityDefaultValue() {
		optimalityField.setText(Double.toString(GurobiParameters.OPTIMALITYTOL_DEFAULT_VALUE));
		setOptimality(GurobiParameters.OPTIMALITYTOL_DEFAULT_VALUE);
	}
	
	public void setHeuristicsDefaultValue() {
		heuristicsField.setText(Double.toString(GurobiParameters.HEURISTICS_DEFAULT_VALUE));
		setHeuristics(GurobiParameters.HEURISTICS_DEFAULT_VALUE);
	}
	
	public Integer selectedMIPFocus() {
		return (Integer) cbMIPFocus.getSelectedItem();
	}
	
	public Integer selectedNumberOfThreads() {
		return (Integer) cbNumThreads.getSelectedItem();
	}
	
	public void selectMIPFocus(String item) {
		cbMIPFocus.setSelectedItem(Integer.parseInt(item));
	}
	
	public void selectNumberOfThreads(String item) {
		cbNumThreads.setSelectedItem(Integer.parseInt(item));
	}
	
	public boolean validEntries() {
		EntryValidator validator = new EntryValidator(); 
		boolean numberError = false;
		boolean outOfRangeError = false;
		if (!validator.isNumber(feasibilityField.getText())) {
			feasibilityField.setForeground(Color.RED);
			numberError = true;
		} else {
			if (Double.parseDouble(feasibilityField.getText()) < GurobiParameters.FEASIBILITYTOL_MINIMUM_VALUE ||
					Double.parseDouble(feasibilityField.getText()) > GurobiParameters.FEASIBILITYTOL_MAXIMUM_VALUE) {
				feasibilityField.setForeground(Color.RED);
				outOfRangeError = true;
			} else {
				feasibilityField.setForeground(Color.BLACK);
			}
		}
		if (!validator.isNumber(intFeasibilityField.getText())) {
			intFeasibilityField.setForeground(Color.RED);
			numberError = true;
		} else {
			if (Double.parseDouble(intFeasibilityField.getText()) < GurobiParameters.INTFEASIBILITYTOL_MINIMUM_VALUE ||
					Double.parseDouble(intFeasibilityField.getText()) > GurobiParameters.INTFEASIBILITYTOL_MAXIMUM_VALUE) {
				intFeasibilityField.setForeground(Color.RED);
				outOfRangeError = true;
			} else {
				intFeasibilityField.setForeground(Color.BLACK);
			}
		}
		if (!validator.isNumber(optimalityField.getText())) {
			optimalityField.setForeground(Color.RED);
			numberError = true;
		} else {
			if (Double.parseDouble(optimalityField.getText()) < GurobiParameters.OPTIMALITYTOL_MINIMUM_VALUE ||
					Double.parseDouble(optimalityField.getText()) > GurobiParameters.OPTIMALITYTOL_MAXIMUM_VALUE) {
				optimalityField.setForeground(Color.RED);
				outOfRangeError = true;
			} else {
				optimalityField.setForeground(Color.BLACK);
			}
		}
		if (!validator.isNumber(heuristicsField.getText())) {
			heuristicsField.setForeground(Color.RED);
			numberError = true;
		} else {
			if (Double.parseDouble(heuristicsField.getText()) < GurobiParameters.HEURISTICS_MINIMUM_VALUE ||
					Double.parseDouble(heuristicsField.getText()) > GurobiParameters.HEURISTICS_MAXIMUM_VALUE) {
				heuristicsField.setForeground(Color.RED);
				outOfRangeError = true;
			} else {
				heuristicsField.setForeground(Color.BLACK);
			}
		}
		if (numberError || outOfRangeError) {
			String error = "";
			if (numberError) {
				error = GraphicalInterfaceConstants.NUMERIC_VALUE_ERROR_MESSAGE;
			} 
			if (outOfRangeError) {
				error = "Value(s) out of range.";
			}
			if (numberError && outOfRangeError) {
				error = GraphicalInterfaceConstants.NUMERIC_VALUE_ERROR_MESSAGE + " "
						+ "Value(s) out of range.";
			}
			setErrorMessage(error);
			return false;
		}
		return true;
		
	}

	public static void main(String[] args) throws Exception {
		//based on code from http:stackoverflow.com/questions/6403821/how-to-add-an-image-to-a-jframe-title-bar
		final ArrayList<Image> icons = new ArrayList<Image>(); 
		icons.add(new ImageIcon("etc/most16.jpg").getImage()); 
		icons.add(new ImageIcon("etc/most32.jpg").getImage());
		
		GurobiParametersDialog d = new GurobiParametersDialog();
		
		d.setIconImages(icons);
    	d.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    	d.setSize(GurobiParameters.DIALOG_WIDTH, GurobiParameters.DIALOG_HEIGHT);
    	d.setLocationRelativeTo(null);
    	d.setVisible(true);

	}
	
}








