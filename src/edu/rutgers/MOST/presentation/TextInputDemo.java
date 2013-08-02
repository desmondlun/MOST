/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package edu.rutgers.MOST.presentation;

import java.awt.*;
import java.awt.event.*;
import java.sql.Array;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.tree.DefaultMutableTreeNode;

import org.rutgers.MOST.tree.DynamicTreeDemo;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.data.ReactionFactory;
import edu.rutgers.MOST.data.ReactionsMetaColumnManager;
import edu.rutgers.MOST.data.Solution;
import edu.rutgers.MOST.optimization.solvers.Callback;

/**
 * TextInputDemo.java uses these additional files:
 *   SpringUtilities.java
 *   ...
 */
public class TextInputDemo extends JDialog
                                          implements ActionListener,
                                                     FocusListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JFormattedTextField numKnockouts, totalTime;
//    JFormattedTextField zipField;
//    JSpinner stateSpinner;
//    boolean addressSet = false;
    Font regularFont, italicFont;
    JLabel addressDisplay;
	private GraphicalInterface gi;
    final static int GAP = 10;
	private JButton startButton;
	private JButton stopButton;
	private Timer timer;
	private int count;
	private JLabel counter;
	private JFormattedTextField threadNum;
	//private JComboBox<String> columnList;

    public TextInputDemo(GraphicalInterface parent) {
//        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        gi = parent;
        
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
        //leftHalf.add(createComboBox());
        leftHalf.add(createButtons());
        leftHalf.add(createTimer());

        add(leftHalf);
//        add(createAddressDisplay());
        
        //Set up timer to drive animation events.
        timer = new Timer(1000, this);
        count = 0;
//        timer.setInitialDelay(1900);
//        timer.start();
    }

    /*
    private Component createComboBox() {
		// TODO Auto-generated method stub
    	JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    	columnList = new JComboBox<String>();
    	columnList.addItem("NULL");
    	panel.add(columnList);
		return panel;
	}
	*/
    
    private JComponent createTimer() {
    	JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    	counter = new JLabel("" + count);
    	panel.add(new JLabel("Time Elapsed: "));
    	panel.add(counter);
    	panel.add(new JLabel(" s"));
//    	panel.setPreferredSize(new Dimension(200, 150));
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
        	Callback.setAbort(true);
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            timer.stop();
            //We can't just setText on the formatted text
            //field, since its value will remain set.
        } else if ("Start".equals(e.getActionCommand())) {
        	count = 0;
        	timer.restart();
        	startButton.setEnabled(false);
            stopButton.setEnabled(true);
            
            // database
        	//System.out.println("comboBox, LocalConfig.getInstance().getLoadedDatabase() = " + LocalConfig.getInstance().getLoadedDatabase());
        	//System.out.println("comboBox, LocalConfig.getInstance().getOptimizationFilesList() = " + LocalConfig.getInstance().getOptimizationFilesList().get(LocalConfig.getInstance().getOptimizationFilesList().size() - 1));
        	
        	String solutionName = GraphicalInterface.listModel.get(GraphicalInterface.listModel.getSize() - 1);
			DynamicTreeDemo.treePanel.addObject(new Solution(solutionName, solutionName));
			
			gi.gdbbTask = gi.new GDBBTask();
			Callback.setAbort(false);
			
        	gi.gdbbTask.getModel().setC((new Double(numKnockouts.getText())).doubleValue());
        	if ((new Double(totalTime.getText())).doubleValue() != -1.0) {
        		gi.gdbbTask.getModel().setTimeLimit((new Double(totalTime.getText())).doubleValue());
        	}
        	else {
        		gi.gdbbTask.getModel().setTimeLimit(Double.POSITIVE_INFINITY);
        	}
        	
        	gi.gdbbTask.getModel().setThreadNum((new Integer(threadNum.getText())).intValue());
        	
        	// select column
        	/*
        	System.out.println("columnList.getSelectedIndex() = "
					+ columnList.getSelectedIndex());
        	*/
        	//ReactionFactory.setColumnName("meta_" + (columnList.getSelectedIndex() + 1));
        	/*
        	System.out.println("ReactionFactory.getColumnName = "
					+ ReactionFactory.getColumnName());
					*/
        	gi.gdbbTask.execute();
        	this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
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
                                0,     //left
                                GAP/2, //bottom
                                0));   //right
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

    protected JComponent createEntryFields() {
        JPanel panel = new JPanel(new SpringLayout());

        String[] labelStrings = {
            "Number of Knockouts ",
            "Optimizer time limit ",
            "Number of Threads "
        };

        JLabel[] labels = new JLabel[labelStrings.length];
        JComponent[] fields = new JComponent[labelStrings.length];
        int fieldNum = 0;

        //Create the text field and set it up.
        numKnockouts  = new JFormattedTextField();
        numKnockouts.setColumns(6);
        numKnockouts.setText("1");
        fields[fieldNum++] = numKnockouts;

        totalTime = new JFormattedTextField();
        totalTime.setColumns(6);
        totalTime.setText("300");
        fields[fieldNum++] = totalTime;

        threadNum = new JFormattedTextField();
        threadNum.setColumns(6);
        threadNum.setText("1");
        fields[fieldNum++] = threadNum;
        
        //Associate label/field pairs, add everything,
        //and lay it out.
        for (int i = 0; i < labelStrings.length; i++) {
            labels[i] = new JLabel(labelStrings[i],
                                   JLabel.TRAILING);
            labels[i].setLabelFor(fields[i]);
            panel.add(labels[i]);
            panel.add(fields[i]);

            //Add listeners to each field.
            JTextField tf = null;
            if (fields[i] instanceof JSpinner) {
                tf = getTextField((JSpinner)fields[i]);
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
     * Create the GUI and show it.  For thread safety,
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
//        frame.add(new TextInputDemo());

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

	public void setObjectiveColumnNames(String loadedDatabase) {
		// TODO Auto-generated method stub
		ReactionsMetaColumnManager manager = new ReactionsMetaColumnManager();
    	ArrayList<String> columnNames = manager.getColumnNames(loadedDatabase);
    	//System.out.println("columnNames = " + columnNames);
    	//columnList.removeAllItems();
    	/*
    	for(int i = 0; i < columnNames.size(); i++) {
    		columnList.addItem(columnNames.get(i));
    	}
    	*/
	}
}
