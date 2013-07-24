package edu.rutgers.MOST.presentation;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import edu.rutgers.MOST.config.LocalConfig;

import static javax.swing.GroupLayout.Alignment.*;
 
public class FindReplaceDialog extends JDialog {
	
	public static JButton findButton = new JButton("Find Next");
	public static JButton findAllButton = new JButton("Find All");
	public static JButton replaceButton = new JButton("Replace"); 
	public static JButton replaceAllButton = new JButton("Replace All");
	public static JButton replaceFindButton = new JButton("Replace/Find");
	public static JButton doneButton = new JButton("Done");
	public static final JTextField findField = new JTextField();
	public static final JTextField replaceField = new JTextField();
	public static final JCheckBox caseCheckBox = new JCheckBox("Match Case");//1
	public static final JCheckBox wrapCheckBox = new JCheckBox("Wrap Around");
	public static final JCheckBox selectedAreaCheckBox = new JCheckBox("Selected Area  ");
	public static final JCheckBox backwardsCheckBox = new JCheckBox("Backwards");
		
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
	
	private WindowFocusListener windowFocusListener;
	
    public FindReplaceDialog() {
    	
    	setMaximumSize(new Dimension(250, 300));
    	setResizable(false);
    	
        JLabel findLabel = new JLabel("Find What:");
        JLabel replaceLabel = new JLabel("Replace:");
        JLabel searchLabel = new JLabel("Search:"); 
        JLabel placeholder = new JLabel(""); 
         
        findField.setText("");
        replaceField.setText("");
        
        caseCheckBox.setSelected(false);
    	wrapCheckBox.setSelected(false);
    	selectedAreaCheckBox.setSelected(false);
        backwardsCheckBox.setSelected(false);
        
        findButton.setMnemonic(KeyEvent.VK_N);
        findAllButton.setMnemonic(KeyEvent.VK_L);
        replaceButton.setMnemonic(KeyEvent.VK_R);
        replaceAllButton.setMnemonic(KeyEvent.VK_P);
        replaceFindButton.setMnemonic(KeyEvent.VK_I);
        doneButton.setMnemonic(KeyEvent.VK_D);
        
        caseCheckBox.setMnemonic(KeyEvent.VK_M);
        wrapCheckBox.setMnemonic(KeyEvent.VK_W);
        selectedAreaCheckBox.setMnemonic(KeyEvent.VK_S);
        backwardsCheckBox.setMnemonic(KeyEvent.VK_B);
        
        findButton.setEnabled(false);
        findAllButton.setEnabled(false);
        replaceButton.setEnabled(false); 
        replaceAllButton.setEnabled(false);
        replaceFindButton.setEnabled(false);
        backwardsCheckBox.setEnabled(false);
        
        // remove redundant default border of check boxes - they would hinder
        // correct spacing and aligning (maybe not needed on some look and feels)
        caseCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        wrapCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        backwardsCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        selectedAreaCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
 
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
 
        layout.setHorizontalGroup(layout.createSequentialGroup()
        		.addGap(20)
        		.addGroup(layout.createParallelGroup(LEADING)
        				.addComponent(findLabel)
        				.addComponent(replaceLabel)
        				.addComponent(caseCheckBox) 
        				.addComponent(searchLabel)
        				.addComponent(backwardsCheckBox)
        				.addComponent(findButton, getButtonWidth(), getButtonWidth(), getButtonWidth())
        				.addComponent(findAllButton, getButtonWidth(), getButtonWidth(), getButtonWidth())
        				.addComponent(replaceFindButton, getButtonWidth(), getButtonWidth(), getButtonWidth()))
        		.addGroup(layout.createParallelGroup(LEADING)
                    .addComponent(findField, getTextAreaWidth(), getTextAreaWidth(), getTextAreaWidth())
                    .addComponent(replaceField, getTextAreaWidth(), getTextAreaWidth(), getTextAreaWidth())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(LEADING)
                        		.addComponent(wrapCheckBox) 
                                .addComponent(placeholder, getTextAreaWidth(), getTextAreaWidth(), getTextAreaWidth())
                                .addComponent(selectedAreaCheckBox)
                                .addComponent(replaceButton, getButtonWidth(), getButtonWidth(), getButtonWidth()) 
                                .addComponent(replaceAllButton, getButtonWidth(), getButtonWidth(), getButtonWidth())
                                .addComponent(doneButton, getButtonWidth(), getButtonWidth(), getButtonWidth()))))
                          
            );
     
            layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(BASELINE)
                    .addComponent(findLabel)
                    .addComponent(findField, getTextAreaHeight(), getTextAreaHeight(), getTextAreaHeight()))
                .addGroup(layout.createParallelGroup(BASELINE)
                    .addComponent(replaceLabel)
                    .addComponent(replaceField, getTextAreaHeight(), getTextAreaHeight(), getTextAreaHeight()))
                .addGap(10)
                .addGroup(layout.createParallelGroup(LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(BASELINE)
                        		.addComponent(caseCheckBox)
                        		.addComponent(wrapCheckBox))
                        .addGap(20)
                        .addGroup(layout.createParallelGroup(BASELINE)
                        		 .addComponent(searchLabel)
                        		 .addComponent(placeholder))
                        .addGap(10)		 
                        .addGroup(layout.createParallelGroup(BASELINE)
                        		 .addComponent(backwardsCheckBox)
                        		 .addComponent(selectedAreaCheckBox))                        		 
                        .addGap(20)	
                        .addGroup(layout.createParallelGroup(BASELINE)
                        		.addComponent(findButton)
                        		.addComponent(replaceButton))
                        .addGroup(layout.createParallelGroup(BASELINE)
                        		.addComponent(findAllButton)
                        		.addComponent(replaceAllButton))
                        .addGroup(layout.createParallelGroup(BASELINE)
                        		.addComponent(replaceFindButton)
                        		.addComponent(doneButton))))
                        .addGap(15)
            );
            
        setTitle("Find/Replace");
        pack();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
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
					backwardsCheckBox.setEnabled(true);
				} else {
					findButton.setEnabled(false);
					findAllButton.setEnabled(false);
					replaceButton.setEnabled(false);
					replaceAllButton.setEnabled(false);
					replaceFindButton.setEnabled(false);
					backwardsCheckBox.setEnabled(false);
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
    			if (GraphicalInterface.isRoot) {
    				replaceButton.setEnabled(true);
        			replaceFindButton.setEnabled(true);
    			} 
    			replaceAllButton.setEnabled(false);
    		}
    	};

    	findButton.addActionListener(findButtonActionListener);
        
        ActionListener findAllButtonActionListener = new ActionListener() {
    		public void actionPerformed(ActionEvent ae) {
    			setFindText(findField.getText());
    			replaceButton.setEnabled(false);
    			if (GraphicalInterface.isRoot) {
    				replaceAllButton.setEnabled(true);
    			}    			
    			replaceFindButton.setEnabled(false);
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
    	
    	ActionListener replaceFindButtonActionListener = new ActionListener() {
    		public void actionPerformed(ActionEvent ae) {
    			setFindText(findField.getText());
    			setReplaceText(replaceField.getText());		
    		}
    	};

    	replaceFindButton.addActionListener(replaceFindButtonActionListener);
    	
        ActionListener doneButtonActionListener = new ActionListener() {
    		public void actionPerformed(ActionEvent ae) {
    			setVisible(false);
    			dispose();				
    		}
    	};

    	doneButton.addActionListener(doneButtonActionListener);
    	
    	ActionListener searchBackwardsActionListener = new ActionListener() {
    		public void actionPerformed(ActionEvent actionEvent) {
    			setFindText(findField.getText());								
    		}
    	};
    	
    	backwardsCheckBox.addActionListener(searchBackwardsActionListener);
    	
    	windowFocusListener = new WindowFocusListener()
        {
            public void windowGainedFocus(WindowEvent we)
            {
            	LocalConfig.getInstance().findReplaceFocusLost = false;
            	LocalConfig.getInstance().findReplaceFocusGained = true;
            	LocalConfig.getInstance().addReactantPromptShown = false;
            }

            public void windowLostFocus(WindowEvent we)
            {               
            	LocalConfig.getInstance().findReplaceFocusLost = true;
            	LocalConfig.getInstance().findReplaceFocusGained = false;
            }
        };

        addWindowFocusListener(windowFocusListener);
        
    }
     
    private int getButtonWidth() 
    { 
        return 110; 
    } 
    
    private int getTextAreaWidth() 
    { 
        return 120; 
    }
    
    private int getTextAreaHeight() 
    { 
        return 25; 
    }
    
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                
                new FindReplaceDialog().setVisible(true);
            }
        });
    }
}

