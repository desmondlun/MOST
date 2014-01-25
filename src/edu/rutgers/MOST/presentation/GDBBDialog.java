package edu.rutgers.MOST.presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.text.MaskFormatter;

import edu.rutgers.MOST.data.Solution;
import edu.rutgers.MOST.optimization.solvers.GurobiSolver;
import edu.rutgers.MOST.presentation.GraphicalInterface.GDBBTask;

public class GDBBDialog extends JDialog
                                          implements ActionListener,
                                                     FocusListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JFormattedTextField numKnockouts, totalTime;
	// JFormattedTextField zipField;
	// JSpinner stateSpinner;
	// boolean addressSet = false;
	Font regularFont, italicFont;
	JLabel addressDisplay;
	private GraphicalInterface gi;
	final static int GAP = 10;
	private JButton startButton;
	private JButton stopButton;
	private Timer timer;
	private int count;
	private JLabel counter;
	private JComboBox<Integer> threadNum;
	private JComboBox<String> columnList;
	private double timeLimit;
	private Map<String, String> reactionNameDBColumnMapping;
	private static String finiteTimeString = "Finite optimizer time limit";
	private static String infiniteTimeString = "Indefinite optimizer time";
	private JRadioButton finiteTimeButton;
	private JRadioButton indefiniteTimeButton;
	private InputVerifier integerVerifier;
	private JLabel exception;

	private class ParseIntegers extends InputVerifier {

		@Override
		public boolean verify(JComponent input) {
			// TODO Auto-generated method stub
			boolean isInteger = true;
			JTextField tf = (JTextField) input;
			try {
				Integer.parseInt(tf.getText());
				//System.out.println("It is an integer");
				//exception.setText("");
				startButton.setEnabled(true);
			}
			catch(NumberFormatException nfe) {
				//System.out.println("It is not an integer");
				isInteger = false;
				//exception.setText("Not an integer!");
				setAlwaysOnTop(false);
				JOptionPane.showMessageDialog(null,                
						GraphicalInterfaceConstants.INTEGER_VALUE_ERROR_TITLE,                
						GraphicalInterfaceConstants.INTEGER_VALUE_ERROR_MESSAGE,                               
						JOptionPane.ERROR_MESSAGE);
				// if non-integer is entered in these fields after numerical error
				// prompt, set to default value
				try {
					Integer.parseInt(numKnockouts.getText());
				}
				catch(NumberFormatException nfe2) {
					numKnockouts.setText("1");
				}
				try {
					Integer.parseInt(totalTime.getText());
				}
				catch(NumberFormatException nfe2) {
					totalTime.setText("300");
				}
				
				setAlwaysOnTop(true);
				//startButton.setEnabled(false);
			}
			return isInteger;
		}

	}

	public GDBBDialog(GraphicalInterface parent) {
		// setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		gi = parent;
		integerVerifier = new ParseIntegers();
		exception = new JLabel("");
		exception.setBackground(Color.RED);

		JPanel leftHalf = new JPanel() {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			//Don't allow us to stretch vertically.
			public Dimension getMaximumSize() {
				Dimension pref = getPreferredSize();
				return new Dimension(Integer.MAX_VALUE,
						pref.height);
			}
		};

		leftHalf.setLayout(new BoxLayout(leftHalf,
				BoxLayout.PAGE_AXIS));
		leftHalf.add(createEntryFields());
		leftHalf.add(createRadioButtonFields());
		// leftHalf.add(createComboBox());
		leftHalf.add(createButtons());
		leftHalf.add(createTimer());
		leftHalf.add(createExceptionSection());
		add(leftHalf);
		// add(createAddressDisplay());

		//Set up timer to drive animation events.
		timer = new Timer(1000, this);
		count = 0;
		reactionNameDBColumnMapping = new HashMap<String, String>();
		reactionNameDBColumnMapping.put(GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_COLUMN], GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_COLUMN]);
		reactionNameDBColumnMapping.put(GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN], GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN]);		
	}

	private Component createExceptionSection() {
		// TODO Auto-generated method stub
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panel.add(exception);
		return panel;
	}

	private Component createRadioButtonFields() {
		// TODO Auto-generated method stub
		JPanel panel = new JPanel(new GridLayout(2, 1));

		JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		labelPanel.add(new JLabel("Optimizer time limit"));

		indefiniteTimeButton = new JRadioButton(infiniteTimeString);
		indefiniteTimeButton.setMnemonic(KeyEvent.VK_I);
		indefiniteTimeButton.setActionCommand(infiniteTimeString);
		indefiniteTimeButton.setSelected(true);

		finiteTimeButton = new JRadioButton(finiteTimeString);
		finiteTimeButton.setMnemonic(KeyEvent.VK_F);
		finiteTimeButton.setActionCommand(finiteTimeString);

		//Group the radio buttons.
		ButtonGroup group = new ButtonGroup();
		group.add(indefiniteTimeButton);
		group.add(finiteTimeButton);

		//Register a listener for the radio buttons.
		indefiniteTimeButton.addActionListener(this);
		finiteTimeButton.addActionListener(this);

		JPanel radioPanel = new JPanel(new SpringLayout());
		radioPanel.add(indefiniteTimeButton);
		radioPanel.add(new Label(""));
		radioPanel.add(finiteTimeButton);

		totalTime = new JFormattedTextField();
		totalTime.setColumns(6);
		totalTime.setText("300");
		totalTime.setEditable(false);
		totalTime.setInputVerifier(integerVerifier);
		radioPanel.add(totalTime);

		SpringUtilities.makeCompactGrid(radioPanel,
				2, 2,
				GAP, GAP, //init x,y
				GAP, GAP/2);//xpad, ypad

//		panel.add(labelPanel);
		panel.add(radioPanel);

		return panel;
	}

	public Map<String, String> getReactionNameDBColumnMapping() {
		return reactionNameDBColumnMapping;
	}

//	private Component createComboBox() {
//		// TODO Auto-generated method stub
//		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
//		setColumnList(new JComboBox<String>());
//		panel.add(new JLabel("Synthetic Objective Vector "));
//		panel.add(getColumnList());
//		return panel;
//	}

	private JComponent createTimer() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		counter = new JLabel("" + count);
		panel.add(new JLabel("Time Elapsed: "));
		panel.add(counter);
		panel.add(new JLabel(" s"));
//      panel.setPreferredSize(new Dimension(200, 150));
		return panel;
	}

	protected JComponent createButtons() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		startButton = new JButton("Start");
		startButton.addActionListener(this);
		panel.add(startButton);

		stopButton = new JButton("Stop");
		stopButton.addActionListener(this);
		stopButton.setActionCommand("clear");
		stopButton.setEnabled(false);
		panel.add(stopButton);

		//Match the SpringLayout's gap, subtracting 5 to make
		//up for the default gap FlowLayout provides.
		panel.setBorder(BorderFactory.createEmptyBorder(0, 0,
				GAP-5, GAP-5));
		return panel;
	}

	/**
	 * Called when the user clicks the button or presses
	 * Enter in a text field.
	 */
	public void actionPerformed(ActionEvent e) {
		if ("clear".equals(e.getActionCommand())) {
			//Callback.setAbort(true);
			startButton.setEnabled(true);
			stopButton.setEnabled(false);
			this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			timer.stop();
			//We can't just setText on the formatted text
			//field, since its value will remain set.
		} else if ("Start".equals(e.getActionCommand())) {
			System.out.println("start");
			count = 0;
			timer.restart();
			startButton.setEnabled(false);
			stopButton.setEnabled(true);

			String solutionName = GraphicalInterface.listModel.get(GraphicalInterface.listModel.getSize() - 1);
			DynamicTreePanel.treePanel.addObject(new Solution(solutionName, solutionName));

			gi.gdbbTask = gi.new GDBBTask();
			//Callback.setAbort(false);

			gi.gdbbTask.getModel().setC((new Double(numKnockouts.getText())).doubleValue());
			gi.gdbbTask.getModel().setTimeLimit(timeLimit);

			if (indefiniteTimeButton.isSelected()) {
				gi.gdbbTask.getModel().setTimeLimit(Double.POSITIVE_INFINITY);
			}
			else {
				gi.gdbbTask.getModel().setTimeLimit((new Double(totalTime.getText())).doubleValue());
			}

			gi.gdbbTask.getModel().setThreadNum((Integer)threadNum.getSelectedItem());
			gi.gdbbTask.execute();

			this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		} else if (infiniteTimeString.equals(e.getActionCommand())) {
			totalTime.setEditable(false);
		} else if (finiteTimeString.equals(e.getActionCommand())) {
			totalTime.setEditable(true);
		}
		counter.setText("" + count++);
		updateDisplays();
	}

	protected void updateDisplays() {

	}

	protected JComponent createAddressDisplay() {
		JPanel panel = new JPanel(new BorderLayout());
		addressDisplay = new JLabel();
		addressDisplay.setHorizontalAlignment(JLabel.CENTER);
		regularFont = addressDisplay.getFont().deriveFont(Font.PLAIN,
				16.0f);
		italicFont = regularFont.deriveFont(Font.ITALIC);
		updateDisplays();

		//Lay out the panel.
		panel.setBorder(BorderFactory.createEmptyBorder(
				GAP/2, //top
				0, //left
				GAP/2, //bottom
				0)); //right
		panel.add(new JSeparator(JSeparator.VERTICAL),
				BorderLayout.LINE_START);
		panel.setPreferredSize(new Dimension(200, 150));

		return panel;
	}

	//A convenience method for creating a MaskFormatter.
	protected MaskFormatter createFormatter(String s) {
		MaskFormatter formatter = null;
		try {
			formatter = new MaskFormatter(s);
		} catch (java.text.ParseException exc) {
			System.err.println("formatter is bad: " + exc.getMessage());
			System.exit(-1);
		}
		return formatter;
	}

	/**
	 * Called when one of the fields gets the focus so that
	 * we can select the focused field.
	 */
	public void focusGained(FocusEvent e) {
		Component c = e.getComponent();
		if (c instanceof JFormattedTextField) {
			selectItLater(c);
		} else if (c instanceof JTextField) {
			((JTextField)c).selectAll();
		}
	}

	//Workaround for formatted text field focus side effects.
	protected void selectItLater(Component c) {
		if (c instanceof JFormattedTextField) {
			final JFormattedTextField ftf = (JFormattedTextField)c;
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					ftf.selectAll();
				}
			});
		}
	}

	//Needed for FocusListener interface.
	public void focusLost(FocusEvent e) { } //ignore

	@SuppressWarnings("unchecked")
	protected JComponent createEntryFields() {
		JPanel panel = new JPanel(new SpringLayout());

		String[] labelStrings = {
				"Number of Knockouts ",
				// "Optimizer time limit ",
				"Number of Threads ",
				"Synthetic Objective Vector "
		};

		JLabel[] labels = new JLabel[labelStrings.length];
		JComponent[] fields = new JComponent[labelStrings.length];
		int fieldNum = 0;

		//Create the text field and set it up.
		numKnockouts = new JFormattedTextField();
		numKnockouts.setColumns(6);
		numKnockouts.setText("1");
		numKnockouts.setInputVerifier(integerVerifier);
		fields[fieldNum++] = numKnockouts;

		// totalTime = new JFormattedTextField();
		// totalTime.setColumns(6);
		// totalTime.setText("300");
		// fields[fieldNum++] = totalTime;

		threadNum = new JComboBox<Integer>();
		threadNum.addItem(1);
		threadNum.addItem(2);
		threadNum.addItem(3);
		threadNum.addItem(4);
		fields[fieldNum++] = threadNum;

		columnList = new JComboBox<String>();
		columnList.addItem(GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_COLUMN]);
		columnList.addItem(GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN]);		
		fields[fieldNum++] = columnList;

		//Associate label/field pairs, add everything,
		//and lay it out.
		for (int i = 0; i < labelStrings.length; i++) {
			labels[i] = new JLabel(labelStrings[i],
					JLabel.LEADING);
			labels[i].setLabelFor(fields[i]);
			panel.add(labels[i]);
			panel.add(fields[i]);

			//Add listeners to each field.
			JTextField tf = null;
			if (fields[i] instanceof JSpinner) {
				tf = getTextField((JSpinner)fields[i]);
			} else if (fields[i] instanceof JComboBox){
				tf = new JTextField(((JComboBox<Integer>)fields[i]).getToolTipText());
			} else {
				tf = (JTextField)fields[i];
			}
			tf.addActionListener(this);
			tf.addFocusListener(this);
		}

		SpringUtilities.makeCompactGrid(panel,
				labelStrings.length, 2,
				GAP, GAP, //init x,y
				GAP, GAP/2);//xpad, ypad
				return panel;
	}

	public JFormattedTextField getTextField(JSpinner spinner) {
		JComponent editor = spinner.getEditor();
		if (editor instanceof JSpinner.DefaultEditor) {
			return ((JSpinner.DefaultEditor)editor).getTextField();
		} else {
			System.err.println("Unexpected editor type: "
					+ spinner.getEditor().getClass()
					+ " isn't a descendant of DefaultEditor");
			return null;
		}
	}

	/**
	 * Create the GUI and show it. For thread safety,
	 * this method should be invoked from the
	 * event dispatch thread.
	 */
	private static void createAndShowGUI() {
		//Create and set up the window.
		JFrame frame = new JFrame("TextInputDemo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//based on code from http://stackoverflow.com/questions/6403821/how-to-add-an-image-to-a-jframe-title-bar
		final ArrayList<Image> icons = new ArrayList<Image>();
		icons.add(new ImageIcon("etc/most16.jpg").getImage());
		icons.add(new ImageIcon("etc/most32.jpg").getImage());

		frame.setIconImages(icons);
		//Add contents to the window.
		// frame.add(new TextInputDemo());

		frame.setLocationRelativeTo(null);

		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		//Schedule a job for the event dispatch thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//Turn off metal's use of bold fonts
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				createAndShowGUI();
			}
		});
	}

	public void enableStart() {
		startButton.setEnabled(true);
		stopButton.setEnabled(false);
		timer.stop();
	}

	/*
	public void setObjectiveColumnNames() {
		// TODO Auto-generated method stub
		ArrayList<String> columnNames = new ArrayList<String>();
		for (int i = 0; i < columnNames.size(); i++) {
			columnList.addItem(columnNames.get(i));
			reactionNameDBColumnMapping.put(columnNames.get(i), "meta_" + (columnList.getItemCount() - 4));
		}
	}
	*/

	/*
	public void addObjectiveColumnName(String columnName) {
		// TODO Auto-generated method stub
		columnList.addItem(columnName);
		reactionNameDBColumnMapping.put(columnName, "meta_" + (columnList.getItemCount() - 4));
		System.out.println("reactionNameDBColumnMapping.get(columnName) = "
				+ reactionNameDBColumnMapping.get(columnName));
	}
	*/

	public JComboBox<String> getColumnList() {
		return columnList;
	}

	public void setColumnList(JComboBox<String> columnList) {
		this.columnList = columnList;
	}
}