package edu.rutgers.MOST.presentation;

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
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class GDBBDialog  extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField numKnockoutsField = new JTextField();
	public JComboBox<Integer> cbNumThreads = new JComboBox<Integer>();
	public SizedComboBox cbSynObj = new SizedComboBox();
	public JButton startButton = new JButton("Start");
	public JButton stopButton = new JButton("Stop");
	private JRadioButton indefiniteTimeButton = new JRadioButton(GDBBConstants.INDEFINITE_TIME_LABEL);
	private JRadioButton finiteTimeButton = new JRadioButton(GDBBConstants.FINITE_TIME_LABEL);
	private JLabel blankLabel = new JLabel("");
	private JTextField finiteTimeField = new JTextField();
	private JLabel counterLabel = new JLabel(GDBBConstants.COUNTER_LABEL_PREFIX + "0" + GDBBConstants.COUNTER_LABEL_SUFFIX);
	
	private String numKnockouts;
	private String numThreads;
	private String finiteTimeString;
	
	public boolean finiteTimeSelected = false;
	
	public String getNumKnockouts() {
		return numKnockouts;
	}

	public void setNumKnockouts(String numKnockouts) {
		this.numKnockouts = numKnockouts;
	}

	public String getNumThreads() {
		return numThreads;
	}

	public void setNumThreads(String numThreads) {
		this.numThreads = numThreads;
	}
	
	private int count;
	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	private int dotCount;
	
	public int getDotCount() {
		return dotCount;
	}

	public void setDotCount(int dotCount) {
		this.dotCount = dotCount;
	}

	public JLabel getCounterLabel() {
		return counterLabel;
	}

	public void setCounterLabel(JLabel counterLabel) {
		this.counterLabel = counterLabel;
	}
	
	private String processingString;
	
	public String getProcessingString() {
		return processingString;
	}

	public void setProcessingString(String processingString) {
		this.processingString = processingString;
	}
	
	public String getFiniteTimeString() {
		return finiteTimeString;
	}

	public void setFiniteTimeString(String finiteTimeString) {
		this.finiteTimeString = finiteTimeString;
	}

	@SuppressWarnings("unchecked")
	public GDBBDialog() {
		
		final ArrayList<Image> icons = new ArrayList<Image>(); 
		icons.add(new ImageIcon("etc/most16.jpg").getImage()); 
		icons.add(new ImageIcon("etc/most32.jpg").getImage());
		
		getRootPane().setDefaultButton(startButton);

		setTitle(GDBBConstants.GDBB_DIALOG_TITLE);
		
		cbNumThreads.setEditable(false);
		cbSynObj.setEditable(false);
		
		for (int i = 1; i <= GDBBConstants.MAX_NUM_THREADS; i++) {
			cbNumThreads.addItem(i);
		}
		
		cbSynObj.addItem(GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_COLUMN]);
		//cbSynObj.addItem(GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN]);		

		numKnockoutsField.setPreferredSize(new Dimension(GDBBConstants.COMPONENT_WIDTH, GDBBConstants.COMPONENT_HEIGHT));
		numKnockoutsField.setMaximumSize(new Dimension(GDBBConstants.COMPONENT_WIDTH, GDBBConstants.COMPONENT_HEIGHT));
		numKnockoutsField.setMinimumSize(new Dimension(GDBBConstants.COMPONENT_WIDTH, GDBBConstants.COMPONENT_HEIGHT));
		
		setKnockoutDefaultValue();
		
		numKnockoutsField.addFocusListener(new FocusListener() {

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
		
		numKnockoutsField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				setNumKnockouts(numKnockoutsField.getText());
			}
			public void removeUpdate(DocumentEvent e) {
				setNumKnockouts(numKnockoutsField.getText());
			}
			public void insertUpdate(DocumentEvent e) {
				setNumKnockouts(numKnockoutsField.getText());
			}
		});

		cbNumThreads.setPreferredSize(new Dimension(GDBBConstants.COMPONENT_WIDTH, GDBBConstants.COMPONENT_HEIGHT));
		cbNumThreads.setMaximumSize(new Dimension(GDBBConstants.COMPONENT_WIDTH, GDBBConstants.COMPONENT_HEIGHT));
		cbNumThreads.setMinimumSize(new Dimension(GDBBConstants.COMPONENT_WIDTH, GDBBConstants.COMPONENT_HEIGHT));

		cbSynObj.setPreferredSize(new Dimension(GDBBConstants.COMPONENT_WIDTH, GDBBConstants.COMPONENT_HEIGHT));
		cbSynObj.setMaximumSize(new Dimension(GDBBConstants.COMPONENT_WIDTH, GDBBConstants.COMPONENT_HEIGHT));
		cbSynObj.setMinimumSize(new Dimension(GDBBConstants.COMPONENT_WIDTH, GDBBConstants.COMPONENT_HEIGHT));
		
		blankLabel.setPreferredSize(new Dimension(GDBBConstants.COMPONENT_WIDTH, GDBBConstants.COMPONENT_HEIGHT));
		blankLabel.setMaximumSize(new Dimension(GDBBConstants.COMPONENT_WIDTH, GDBBConstants.COMPONENT_HEIGHT));
		blankLabel.setMinimumSize(new Dimension(GDBBConstants.COMPONENT_WIDTH, GDBBConstants.COMPONENT_HEIGHT));
		
		indefiniteTimeButton.setPreferredSize(new Dimension(GDBBConstants.LABELED_BUTTON_WIDTH, GDBBConstants.LABELED_BUTTON_HEIGHT));
		indefiniteTimeButton.setMaximumSize(new Dimension(GDBBConstants.LABELED_BUTTON_WIDTH, GDBBConstants.LABELED_BUTTON_HEIGHT));
		indefiniteTimeButton.setMinimumSize(new Dimension(GDBBConstants.LABELED_BUTTON_WIDTH, GDBBConstants.LABELED_BUTTON_HEIGHT));
		indefiniteTimeButton.setMnemonic(KeyEvent.VK_I);
		
		finiteTimeButton.setPreferredSize(new Dimension(GDBBConstants.LABELED_BUTTON_WIDTH, GDBBConstants.LABELED_BUTTON_HEIGHT));
		finiteTimeButton.setMaximumSize(new Dimension(GDBBConstants.LABELED_BUTTON_WIDTH, GDBBConstants.LABELED_BUTTON_HEIGHT));
		finiteTimeButton.setMinimumSize(new Dimension(GDBBConstants.LABELED_BUTTON_WIDTH, GDBBConstants.LABELED_BUTTON_HEIGHT));
		finiteTimeButton.setMnemonic(KeyEvent.VK_F);
		
		//Group the radio buttons.
		ButtonGroup group = new ButtonGroup();
		group.add(indefiniteTimeButton);
		group.add(finiteTimeButton);
		indefiniteTimeButton.setSelected(true);
		
		finiteTimeField.setPreferredSize(new Dimension(GDBBConstants.COMPONENT_WIDTH, GDBBConstants.COMPONENT_HEIGHT));
		finiteTimeField.setMaximumSize(new Dimension(GDBBConstants.COMPONENT_WIDTH, GDBBConstants.COMPONENT_HEIGHT));
		finiteTimeField.setMinimumSize(new Dimension(GDBBConstants.COMPONENT_WIDTH, GDBBConstants.COMPONENT_HEIGHT));
		
		setFiniteTimeDefaultValue();
		
		// since indefinite is the default button, this field is disabled
		finiteTimeField.setEditable(false);
		
		finiteTimeField.addFocusListener(new FocusListener() {

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
		
		finiteTimeField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				setFiniteTimeString(finiteTimeField.getText());
			}
			public void removeUpdate(DocumentEvent e) {
				setFiniteTimeString(finiteTimeField.getText());
			}
			public void insertUpdate(DocumentEvent e) {
				setFiniteTimeString(finiteTimeField.getText());
			}
		});
		
		//box layout
		Box vb = Box.createVerticalBox();
   	    
		Box hbTopLabel = Box.createHorizontalBox();
		Box hbTop = Box.createHorizontalBox();
		Box hbNumKnockoutsLabel = Box.createHorizontalBox();	    
		Box hbNumKnockouts = Box.createHorizontalBox();
		Box hbNumThreadsLabel = Box.createHorizontalBox();	    
		Box hbMetabolite = Box.createHorizontalBox();
		Box hbSynObjLabel = Box.createHorizontalBox();	    
		Box hbSynObj = Box.createHorizontalBox();   
		Box hbIndefiniteTime = Box.createHorizontalBox();	    
		Box hbBlankLabel = Box.createHorizontalBox();
		Box hbFiniteTime = Box.createHorizontalBox();	    
		Box hbFiniteTimeField = Box.createHorizontalBox();
		
		Box vbLabels = Box.createVerticalBox();
		Box vbCombos = Box.createVerticalBox();

		Box hbLabeledComponents = Box.createHorizontalBox();
		Box hbButton = Box.createHorizontalBox();
		Box hbCounterLabel = Box.createHorizontalBox();

		//top label
		JLabel topLabel = new JLabel("");
		topLabel.setSize(new Dimension(300, 10));
		//top, left, bottom. right
		topLabel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		topLabel.setAlignmentX(LEFT_ALIGNMENT);

		hbTop.add(topLabel);	
		hbTop.setAlignmentX(LEFT_ALIGNMENT);

		hbTopLabel.add(hbTop);
		
		//Number of Knockouts Label and combo
		JLabel numKnockoutsLabel = new JLabel();
		numKnockoutsLabel.setText(GDBBConstants.NUM_KNOCKOUTS_LABEL);
		numKnockoutsLabel.setPreferredSize(new Dimension(GDBBConstants.LABEL_WIDTH, GDBBConstants.LABEL_HEIGHT));
		numKnockoutsLabel.setMaximumSize(new Dimension(GDBBConstants.LABEL_WIDTH, GDBBConstants.LABEL_HEIGHT));
		numKnockoutsLabel.setMinimumSize(new Dimension(GDBBConstants.LABEL_WIDTH, GDBBConstants.LABEL_HEIGHT));
		numKnockoutsLabel.setBorder(BorderFactory.createEmptyBorder(10,0,ColumnInterfaceConstants.LABEL_BOTTOM_BORDER_SIZE,10));
		numKnockoutsLabel.setAlignmentX(LEFT_ALIGNMENT);
		//numKnockoutsLabel.setAlignmentY(TOP_ALIGNMENT);
		numKnockoutsLabel.setDisplayedMnemonic('K');
		numKnockoutsLabel.setLabelFor(numKnockoutsField);

		JPanel panelNumKnockoutsLabel = new JPanel();
		panelNumKnockoutsLabel.setLayout(new BoxLayout(panelNumKnockoutsLabel, BoxLayout.X_AXIS));
		panelNumKnockoutsLabel.add(numKnockoutsLabel);
		panelNumKnockoutsLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbNumKnockoutsLabel.add(panelNumKnockoutsLabel);
		hbNumKnockoutsLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelNumKnockouts = new JPanel();
		panelNumKnockouts.setLayout(new BoxLayout(panelNumKnockouts, BoxLayout.X_AXIS));
		panelNumKnockouts.add(numKnockoutsField);
		panelNumKnockouts.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		panelNumKnockouts.setAlignmentX(RIGHT_ALIGNMENT);

		hbNumKnockouts.add(panelNumKnockouts);
		hbNumKnockouts.setAlignmentX(RIGHT_ALIGNMENT);

		vbLabels.add(hbNumKnockoutsLabel);
		JLabel blankLabel1 = new JLabel("");
		vbLabels.add(blankLabel1);
		vbCombos.add(hbNumKnockouts);

		//Number of Threads Label and combo
		JLabel numThreadsLabel = new JLabel();
		numThreadsLabel.setText(GDBBConstants.NUM_THREADS_LABEL);
		numThreadsLabel.setPreferredSize(new Dimension(GDBBConstants.LABEL_WIDTH, GDBBConstants.LABEL_HEIGHT));
		numThreadsLabel.setMaximumSize(new Dimension(GDBBConstants.LABEL_WIDTH, GDBBConstants.LABEL_HEIGHT));
		numThreadsLabel.setMinimumSize(new Dimension(GDBBConstants.LABEL_WIDTH, GDBBConstants.LABEL_HEIGHT));
		numThreadsLabel.setBorder(BorderFactory.createEmptyBorder(ColumnInterfaceConstants.LABEL_TOP_BORDER_SIZE,0,ColumnInterfaceConstants.LABEL_BOTTOM_BORDER_SIZE,10));
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

		hbMetabolite.add(panelNumThreads);
		hbMetabolite.setAlignmentX(RIGHT_ALIGNMENT);

		vbLabels.add(hbNumThreadsLabel);
		JLabel blankLabel2 = new JLabel("");
		vbLabels.add(blankLabel2);
		vbCombos.add(hbMetabolite);

		//synObj label and combo
		JLabel synObjLabel = new JLabel();
		synObjLabel.setText(GDBBConstants.SYN_OBJ_COLUMN_LABEL);
		synObjLabel.setPreferredSize(new Dimension(GDBBConstants.LABEL_WIDTH, GDBBConstants.LABEL_HEIGHT));
		synObjLabel.setMaximumSize(new Dimension(GDBBConstants.LABEL_WIDTH, GDBBConstants.LABEL_HEIGHT));
		synObjLabel.setMinimumSize(new Dimension(GDBBConstants.LABEL_WIDTH, GDBBConstants.LABEL_HEIGHT));
		synObjLabel.setBorder(BorderFactory.createEmptyBorder(ColumnInterfaceConstants.LABEL_TOP_BORDER_SIZE,0,ColumnInterfaceConstants.LABEL_BOTTOM_BORDER_SIZE,10));
		synObjLabel.setAlignmentX(LEFT_ALIGNMENT);
		synObjLabel.setDisplayedMnemonic('O');
		synObjLabel.setLabelFor(cbSynObj);

		JPanel panelSynObjLabel = new JPanel();
		panelSynObjLabel.setLayout(new BoxLayout(panelSynObjLabel, BoxLayout.X_AXIS));
		panelSynObjLabel.add(synObjLabel);
		panelSynObjLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbSynObjLabel.add(panelSynObjLabel);
		hbSynObjLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelSynObj = new JPanel();
		panelSynObj.setLayout(new BoxLayout(panelSynObj, BoxLayout.X_AXIS));
		panelSynObj.add(cbSynObj);
		panelSynObj.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		panelSynObj.setAlignmentX(RIGHT_ALIGNMENT);

		hbSynObj.add(panelSynObj);
		hbSynObj.setAlignmentX(RIGHT_ALIGNMENT);

		vbLabels.add(hbSynObjLabel);
		JLabel blankLabel3 = new JLabel("");
		vbLabels.add(blankLabel3);
		vbCombos.add(hbSynObj);
		
		// indefinite time button and blank label
		JPanel panelIndefiniteTime = new JPanel();
		panelIndefiniteTime.setLayout(new BoxLayout(panelIndefiniteTime, BoxLayout.X_AXIS));
		panelIndefiniteTime.add(indefiniteTimeButton);
		panelIndefiniteTime.setBorder(BorderFactory.createEmptyBorder(GDBBConstants.LABELED_BUTTON_TOP_GAP,0,10,0));

		hbIndefiniteTime.add(panelIndefiniteTime);
		hbIndefiniteTime.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelBlankLabel = new JPanel();
		panelBlankLabel.setLayout(new BoxLayout(panelBlankLabel, BoxLayout.X_AXIS));
		panelBlankLabel.add(blankLabel);
		panelBlankLabel.setBorder(BorderFactory.createEmptyBorder(GDBBConstants.LABELED_BUTTON_TOP_GAP,0,10,0));
		panelBlankLabel.setAlignmentX(RIGHT_ALIGNMENT);

		hbBlankLabel.add(panelBlankLabel);
		hbBlankLabel.setAlignmentX(RIGHT_ALIGNMENT);

		vbLabels.add(hbIndefiniteTime);
		vbCombos.add(hbBlankLabel);
		
		// finite time button and text field
		JPanel panelFiniteTime = new JPanel();
		panelFiniteTime.setLayout(new BoxLayout(panelFiniteTime, BoxLayout.X_AXIS));
		panelFiniteTime.add(finiteTimeButton);
		//top, left, bottom. right
		panelFiniteTime.setBorder(BorderFactory.createEmptyBorder(0,0,GDBBConstants.LABELED_BUTTON_BOTTOM_GAP,0));

		hbFiniteTime.add(panelFiniteTime);
		hbFiniteTime.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelFiniteTimeField = new JPanel();
		panelFiniteTimeField.setLayout(new BoxLayout(panelFiniteTimeField, BoxLayout.X_AXIS));
		panelFiniteTimeField.add(finiteTimeField);
		panelFiniteTimeField.setBorder(BorderFactory.createEmptyBorder(0,0,GDBBConstants.LABELED_BUTTON_BOTTOM_GAP,0));
		panelFiniteTimeField.setAlignmentX(RIGHT_ALIGNMENT);

		hbFiniteTimeField.add(panelFiniteTimeField);
		hbFiniteTimeField.setAlignmentX(RIGHT_ALIGNMENT);

		vbLabels.add(hbFiniteTime);
		vbCombos.add(hbFiniteTimeField);
		
		startButton.setMnemonic(KeyEvent.VK_S);
		JLabel blank = new JLabel("    "); 
		stopButton.setMnemonic(KeyEvent.VK_P);
		stopButton.setEnabled(false);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(startButton);
		buttonPanel.add(blank);
		buttonPanel.add(stopButton);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,20,20,20));

		hbButton.add(buttonPanel);
		
		hbCounterLabel.add(counterLabel);

		vb.add(hbTopLabel);
		hbLabeledComponents.add(vbLabels);
		hbLabeledComponents.add(vbCombos);
		vb.add(hbLabeledComponents);		
		vb.add(hbButton);
		vb.add(hbCounterLabel);

		add(vb);
		
		count = 0;
		dotCount = 0;
		
		enableComponents();
		
		ActionListener startButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				//System.out.println("Start");
			}
		};

		startButton.addActionListener(startButtonActionListener);

		ActionListener stopButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				
			}
		};

		stopButton.addActionListener(stopButtonActionListener);
		
		indefiniteTimeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				finiteTimeField.setEditable(false);
				finiteTimeSelected = false;
			}
		});
		
		finiteTimeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				finiteTimeField.setEnabled(true);
				finiteTimeField.setEditable(true);
				finiteTimeField.requestFocus();
				finiteTimeSelected = true;
			}
		});
	
	}
	
	// disables changing any components while GDBB is running
	public void disableComponents() {
		startButton.setEnabled(false);
		numKnockoutsField.setEditable(false);
		cbNumThreads.setEnabled(false);
		cbSynObj.setEnabled(false);
		indefiniteTimeButton.setEnabled(false);
		finiteTimeButton.setEnabled(false);
		finiteTimeField.setEnabled(false);
		finiteTimeField.setEditable(false);
	}
	
	public void enableComponents() {
		numKnockoutsField.setEditable(true);
		cbNumThreads.setEnabled(true);
		cbSynObj.setEnabled(true);
		indefiniteTimeButton.setEnabled(true);
		finiteTimeButton.setEnabled(true);
	}
	
	public void enableStart() {
		startButton.setEnabled(true);
		stopButton.setEnabled(false);
	}
	
	public void selectIndefiniteTimeButton() {
		indefiniteTimeButton.setSelected(true);
		finiteTimeField.setEditable(false);
		finiteTimeField.setEnabled(false);
	}
	
	public void setKnockoutDefaultValue() {
		numKnockoutsField.setText(GDBBConstants.NUM_KNOCKOUTS_DEFAULT);
		setNumKnockouts(GDBBConstants.NUM_KNOCKOUTS_DEFAULT);
	}
	
	public void setFiniteTimeDefaultValue() {
		finiteTimeField.setText(GDBBConstants.FINITE_TIME_DEFAULT);	
		setFiniteTimeString(GDBBConstants.FINITE_TIME_DEFAULT);
	}	

	public static void main(String[] args) throws Exception {
//		//based on code from http:stackoverflow.com/questions/6403821/how-to-add-an-image-to-a-jframe-title-bar
//		final ArrayList<Image> icons = new ArrayList<Image>(); 
//		icons.add(new ImageIcon("etc/most16.jpg").getImage()); 
//		icons.add(new ImageIcon("etc/most32.jpg").getImage());
//		
//		GDBBDialog d = new GDBBDialog();
//		
//		d.setIconImages(icons);
//    	d.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
//    	d.setSize(400, 350);
//    	d.setLocationRelativeTo(null);
//    	d.setVisible(true);

	}
	
}







