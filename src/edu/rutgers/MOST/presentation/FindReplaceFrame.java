package edu.rutgers.MOST.presentation;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import edu.rutgers.MOST.config.LocalConfig;

import static javax.swing.GroupLayout.Alignment.*;
 
public class FindReplaceFrame extends JFrame {
	
	public static JButton findButton = new JButton("Find Next");
	public static JButton findAllButton = new JButton("Find All");
	public static JButton replaceButton = new JButton("Replace"); 
	public static JButton replaceAllButton = new JButton("Replace All");
	public static JButton doneButton = new JButton("Done");
	public static final JTextField findField = new JTextField();
	public static final JTextField replaceField = new JTextField();
	public static final JCheckBox caseCheckBox = new JCheckBox("Match Case");//1
	
	private String findText;

	public void setFindText(String findText) {
		this.findText = findText;
	}

	public String getFindText() {
		return findText;
	}
	
	private String replaceText;

	public void setReplaceText(String replaceText) {
		this.replaceText = replaceText;
	}

	public String getReplaceText() {
		return replaceText;
	}
	
    public FindReplaceFrame() {
    	
    	setMaximumSize(new Dimension(200, 150));
    	setResizable(false);
    
        JLabel findLabel = new JLabel("Find What:");
        findLabel.setFont(findLabel.getFont().deriveFont(Font.PLAIN));
        JLabel replaceLabel = new JLabel("Replace:");
        replaceLabel.setFont(replaceLabel.getFont().deriveFont(Font.PLAIN));
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(searchLabel.getFont().deriveFont(Font.PLAIN));
        JLabel placeholder1 = new JLabel("");
        JLabel placeholder2 = new JLabel("");
        JLabel placeholder3 = new JLabel("");
        final JTextField findField = new JTextField();
        final JTextField replaceField = new JTextField();
        caseCheckBox.setFont(caseCheckBox.getFont().deriveFont(Font.PLAIN));
        JCheckBox wholeCheckBox = new JCheckBox("Whole Words");//2
        wholeCheckBox.setFont(wholeCheckBox.getFont().deriveFont(Font.PLAIN));
        JCheckBox wrapCheckBox = new JCheckBox("Wrap Around");//4  
        wrapCheckBox.setFont(wrapCheckBox.getFont().deriveFont(Font.PLAIN));
        JCheckBox backCheckBox = new JCheckBox("Backwards");//5
        backCheckBox.setFont(backCheckBox.getFont().deriveFont(Font.PLAIN));
        JCheckBox selectCheckBox = new JCheckBox("Selected Area  ");//5
        selectCheckBox.setFont(selectCheckBox.getFont().deriveFont(Font.PLAIN));
        
        // buttons
        findButton.setFont(findButton.getFont().deriveFont(Font.PLAIN)); 
        findButton.setEnabled(false);
        findAllButton.setFont(findButton.getFont().deriveFont(Font.PLAIN)); 
        findAllButton.setEnabled(false);
        replaceButton.setFont(replaceButton.getFont().deriveFont(Font.PLAIN)); 
        replaceButton.setEnabled(false); 
        replaceAllButton.setFont(replaceAllButton.getFont().deriveFont(Font.PLAIN));
        replaceAllButton.setEnabled(false);
        doneButton.setFont(doneButton.getFont().deriveFont(Font.PLAIN));
        SizedComboBox tableColumns = new SizedComboBox();
        //JComboBox<String> tableColumns = new JComboBox<String>();
        tableColumns.setFont(tableColumns.getFont().deriveFont(Font.PLAIN));
        tableColumns.addItem("All");
        tableColumns.addItem("Reaction Name");
 
        // remove redundant default border of check boxes - they would hinder
        // correct spacing and aligning (maybe not needed on some look and feels)
        caseCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        wrapCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        wholeCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        backCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
 
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
 
        layout.setHorizontalGroup(layout.createSequentialGroup()
        		.addGroup(layout.createParallelGroup(LEADING)
        				.addComponent(findLabel)
        				.addComponent(replaceLabel)
        				.addComponent(caseCheckBox)
        				.addComponent(wholeCheckBox)
        				.addComponent(findButton))
        		.addGroup(layout.createParallelGroup(LEADING)
                    .addComponent(findField)
                    .addComponent(replaceField)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(LEADING)
                        		.addComponent(wrapCheckBox)
                        		.addComponent(placeholder3)
                                .addComponent(findAllButton))
                        .addGroup(layout.createParallelGroup(LEADING)
                        		.addComponent(searchLabel) 
                        		.addComponent(selectCheckBox)
                        		.addComponent(replaceButton))
                         .addGroup(layout.createParallelGroup(LEADING)
                        		 .addComponent(tableColumns, 80, 80, 80)
                        		.addComponent(backCheckBox)
                        		.addComponent(replaceAllButton)) 
                         .addGroup(layout.createParallelGroup(LEADING)
                        		 .addComponent(placeholder1)
                        		 .addComponent(placeholder2)
                        		.addComponent(doneButton))))
                        		
            );
            
            layout.linkSize(SwingConstants.HORIZONTAL, findButton, findAllButton, replaceButton, replaceAllButton, doneButton);
            
            layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(BASELINE)
                    .addComponent(findLabel)
                    .addComponent(findField, 25, 25, 25))
                .addGroup(layout.createParallelGroup(BASELINE)
                    .addComponent(replaceLabel)
                    .addComponent(replaceField, 25, 25, 25))
                .addGap(10)
                .addGroup(layout.createParallelGroup(LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(BASELINE)
                        		.addComponent(caseCheckBox)
                        		.addComponent(wrapCheckBox)
                        		.addComponent(searchLabel)
                                .addComponent(tableColumns)
                                .addComponent(placeholder1))
                         .addGroup(layout.createParallelGroup(BASELINE)
                        		 .addComponent(wholeCheckBox)
                        		 .addComponent(placeholder3)
                        		 .addComponent(selectCheckBox)
                        		 .addComponent(backCheckBox)
                        		 .addComponent(placeholder2))  
                        .addGap(10)		 
                        .addGroup(layout.createParallelGroup(BASELINE)
                        		.addComponent(findButton)
                        		.addComponent(findAllButton)
                        		.addComponent(replaceButton)
                        		.addComponent(replaceAllButton)
                        		.addComponent(doneButton))))
                  
            );
            
        setTitle("Find/Replace");
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        caseCheckBox.setSelected(false);
        
        findField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				enableFindButtons();
			}
			public void removeUpdate(DocumentEvent e) {
				enableFindButtons();
			}
			public void insertUpdate(DocumentEvent e) {
				enableFindButtons();
			}

			public void enableFindButtons() {
				if (findField.getText() != null && findField.getText().trim().length() > 0) {
					LocalConfig.getInstance().setReactionsLocationsListCount(0);
					LocalConfig.getInstance().findFieldChanged = true;
					findButton.setEnabled(true);
					findAllButton.setEnabled(true);
				} else {
					findButton.setEnabled(false);
					findAllButton.setEnabled(false);
					replaceButton.setEnabled(false);
					replaceAllButton.setEnabled(false);
				}
			}
		});
		    
        replaceField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				enableReplaceButtons();
			}
			public void removeUpdate(DocumentEvent e) {
				enableReplaceButtons();
			}
			public void insertUpdate(DocumentEvent e) {
				enableReplaceButtons();
			}

			public void enableReplaceButtons() {
				if (replaceField.getText() != null && replaceField.getText().trim().length() > 0) {
					LocalConfig.getInstance().replaceFieldChanged = true;
				}
			}
		});
        
        ActionListener findButtonActionListener = new ActionListener() {
    		public void actionPerformed(ActionEvent ae) {
    			setFindText(findField.getText());	
    			replaceButton.setEnabled(true);
    		}
    	};

    	findButton.addActionListener(findButtonActionListener);
        
        ActionListener findAllButtonActionListener = new ActionListener() {
    		public void actionPerformed(ActionEvent ae) {
    			setFindText(findField.getText());
    			replaceButton.setEnabled(false);
    			replaceAllButton.setEnabled(true);
    		}
    	};

    	findAllButton.addActionListener(findAllButtonActionListener);
        
    	ActionListener replaceButtonActionListener = new ActionListener() {
    		public void actionPerformed(ActionEvent ae) {
    			setReplaceText(replaceField.getText());	
    		}
    	};

    	replaceButton.addActionListener(replaceButtonActionListener);
        
        ActionListener replaceAllButtonActionListener = new ActionListener() {
    		public void actionPerformed(ActionEvent ae) {
    			setReplaceText(replaceField.getText());		
    		}
    	};

    	replaceAllButton.addActionListener(replaceAllButtonActionListener);
    	
        ActionListener doneButtonActionListener = new ActionListener() {
    		public void actionPerformed(ActionEvent ae) {
    			setVisible(false);
    			dispose();				
    		}
    	};

    	doneButton.addActionListener(doneButtonActionListener);
        
    }
    
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                
                new FindReplaceFrame().setVisible(true);
            }
        });
    }
}

