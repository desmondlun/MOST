package edu.rutgers.MOST.presentation;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.data.TextFieldUndoItem;
import static javax.swing.GroupLayout.Alignment.*;
 
public class VisualizationFindDialog extends JDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static JButton findButton = new JButton("Find Next");
	public static JButton doneButton = new JButton("Done");
	public static JLabel findLabel = new JLabel("Find What:");
	public static final SizedComboBox findBox = new SizedComboBox();
	public static final SizedComboBox matchByBox = new SizedComboBox();
	public static final JCheckBox caseCheckBox = new JCheckBox("Match Case");
	public static final JCheckBox exactMatchCheckBox = new JCheckBox("Exact Match by:");
	public static final JCheckBox wrapCheckBox = new JCheckBox("Wrap Around");
//	public static final JCheckBox selectedAreaCheckBox = new JCheckBox("Selected Area  ");
	public static JLabel placeholderLabel = new JLabel(" ");
	public static final JCheckBox backwardsCheckBox = new JCheckBox("Backwards");
	public static final JMenuItem findUndoMenuItem = new JMenuItem("Undo");
	public static final JMenuItem findCutItem = new JMenuItem("Cut");
	public static final JMenuItem findCopyItem = new JMenuItem("Copy");
	public static final JMenuItem findPasteItem = new JMenuItem("Paste");
	public static final JMenuItem findDeleteItem = new JMenuItem("Delete");
	public static final JMenuItem findSelectAllItem = new JMenuItem("Select All");
	
	// undo changes in find text field
	public static final TextFieldUndoItem findUndoItem = new TextFieldUndoItem();
		
	private String findText;

	public void setFindText(String findText) {
		this.findText = findText;
	}

	public String getFindText() {
		return findText;
	}

	private String oldFindValue;
	
	public String getOldFindValue() {
		return oldFindValue;
	}

	public void setOldFindValue(String oldFindValue) {
		this.oldFindValue = oldFindValue;
	}

	private WindowFocusListener windowFocusListener;
	
	public VisualizationFindDialog() {
    	
    	setMaximumSize(new Dimension(250, 300));
    	setResizable(false);
    	
    	getRootPane().setDefaultButton(findButton);
    	
        JLabel searchLabel = new JLabel("Search:"); 
        JLabel placeholder = new JLabel(""); 
        
        populateComboBox(findBox, LocalConfig.getInstance().getFindEntryList());
        matchByBox.removeAllItems();
        for (int i = 0; i < VisualizationFindConstants.FIND_BY_COLUMN_LIST.length; i++) {
			matchByBox.addItem(VisualizationFindConstants.FIND_BY_COLUMN_LIST[i]);
		}
        
        findBox.setSelectedIndex(-1);
        findBox.setEditable(true);
        
        final JTextField findField = (JTextField)findBox.getEditor().getEditorComponent();
        final JPopupMenu findPopupMenu = new JPopupMenu();
        findUndoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
        findCutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        findCopyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        findPasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        findSelectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        findPopupMenu.add(findUndoMenuItem);
        findPopupMenu.addSeparator();
        findPopupMenu.add(findCutItem);
        findPopupMenu.add(findCopyItem);
        findPopupMenu.add(findPasteItem);
        findPopupMenu.add(findDeleteItem);
        findPopupMenu.addSeparator();
        findPopupMenu.add(findSelectAllItem);
        findBox.add(findPopupMenu);
        
        setOldFindValue("");
        
        findField.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e)  {check(e);}
			public void mouseReleased(MouseEvent e) {check(e);}

			public void check(MouseEvent e) {
				if (e.isPopupTrigger()) { //if the event shows the menu
					findPopupMenu.show(findField, e.getX(), e.getY()); 
				}
			}
		});  
        
        findUndoMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
	            findField.setText(findUndoItem.getOldValue());	
	            String temp = findUndoItem.getOldValue();
	            findUndoItem.setOldValue(findUndoItem.getNewValue());
	            findUndoItem.setNewValue(temp);
			}
		});
        findCutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) { 
				setClipboardContents(findField.getSelectedText());
				String selection = findField.getSelectedText();	             
	            if(selection==null){
	                return;
	            }
	            findField.replaceSelection("");				
			}
		});
		findCopyItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) { 
				setClipboardContents("");
				setClipboardContents(findField.getSelectedText());
			}
		});
		findPasteItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) { 
				findField.setText("");
	            try{
	                String clip_string = getClipboardContents(VisualizationFindDialog.this);
	                findField.replaceSelection(clip_string.trim());
	                 
	            }catch(Exception excpt){
	                 
	            }
			}
		});
		findDeleteItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) { 	
				findField.setText("");
			}
		});
		findSelectAllItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) { 	
				findField.selectAll();
			}
		});
        
		findField.addFocusListener(new FocusListener() {

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
		
        caseCheckBox.setSelected(VisualizationFindConstants.MATCH_CASE_DEFAULT);
    	wrapCheckBox.setSelected(VisualizationFindConstants.WRAP_AROUND_DEFAULT);
        backwardsCheckBox.setSelected(VisualizationFindConstants.SEARCH_BACKWARDS_DEFAULT);
        exactMatchCheckBox.setSelected(VisualizationFindConstants.EXACT_MATCH_DEFAULT);
        
        findLabel.setDisplayedMnemonic('F');
        findLabel.setLabelFor(findBox);
        findButton.setMnemonic(KeyEvent.VK_N);
        doneButton.setMnemonic(KeyEvent.VK_D);
        
        caseCheckBox.setMnemonic(KeyEvent.VK_M);
        wrapCheckBox.setMnemonic(KeyEvent.VK_W);
        backwardsCheckBox.setMnemonic(KeyEvent.VK_B);
        
        findButton.setEnabled(false);
        backwardsCheckBox.setEnabled(false);
        
        // remove redundant default border of check boxes - they would hinder
        // correct spacing and aligning (maybe not needed on some look and feels)
        caseCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        exactMatchCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        wrapCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        backwardsCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        placeholderLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
 
        findCutItem.setEnabled(false);
		findCopyItem.setEnabled(false);
		findDeleteItem.setEnabled(false);
		findSelectAllItem.setEnabled(false);
        
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
 
        layout.setHorizontalGroup(layout.createSequentialGroup()
        		.addGap(20)
        		.addGroup(layout.createParallelGroup(LEADING)
        				.addComponent(findLabel)
        				.addComponent(caseCheckBox) 
        				.addComponent(exactMatchCheckBox)
        				.addComponent(searchLabel)
        				.addComponent(backwardsCheckBox)
        				.addComponent(findButton, getButtonWidth(), getButtonWidth(), getButtonWidth()))
        		.addGroup(layout.createParallelGroup(LEADING)
                    .addComponent(findBox, getTextAreaWidth(), getTextAreaWidth(), getTextAreaWidth())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(LEADING)
                        		.addComponent(wrapCheckBox) 
                        		.addComponent(matchByBox, getTextAreaWidth(), getTextAreaWidth(), getTextAreaWidth())
                                .addComponent(placeholder)
                                .addComponent(placeholderLabel)
                                .addComponent(doneButton, getButtonWidth(), getButtonWidth(), getButtonWidth()))))
                          
            );
     
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(BASELINE)
                    .addComponent(findLabel)
                    .addComponent(findBox, getTextAreaHeight(), getTextAreaHeight(), getTextAreaHeight()))
                .addGap(10)
                .addGroup(layout.createParallelGroup(LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(BASELINE)
                        		.addComponent(caseCheckBox)
                        		.addComponent(wrapCheckBox))
                        .addGap(20)
                        .addGroup(layout.createParallelGroup(BASELINE)
                        		.addComponent(exactMatchCheckBox)
                        		.addComponent(matchByBox, getTextAreaHeight(), getTextAreaHeight(), getTextAreaHeight()))
                        .addGap(20)
                        .addGroup(layout.createParallelGroup(BASELINE)
                        		 .addComponent(searchLabel)
                        		 .addComponent(placeholder))
                        .addGap(10)		 
                        .addGroup(layout.createParallelGroup(BASELINE)
                        		 .addComponent(backwardsCheckBox)
                        		 .addComponent(placeholderLabel))                        		 
                        .addGap(20)	
                        .addGroup(layout.createParallelGroup(BASELINE)
                        		.addComponent(findButton)
                        		.addComponent(doneButton))))
                        .addGap(25)
            );
            
        setTitle("Visualization Find");
        pack();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        findField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				enableFindButtons();
				fieldChangeAction();
			}
			public void removeUpdate(DocumentEvent e) {
				enableFindButtons();
				fieldChangeAction();
			}
			public void insertUpdate(DocumentEvent e) {
				enableFindButtons();
				fieldChangeAction();
			}

			public void enableFindButtons() {
				if (findField.getText() != null && findField.getText().trim().length() > 0) {
					findButton.setEnabled(true);
					backwardsCheckBox.setEnabled(true);
				} else {
					findButton.setEnabled(false);
					backwardsCheckBox.setEnabled(false);
				}
			}
			public void fieldChangeAction() {
				if (findField.getText().length() > 0) {
					findCutItem.setEnabled(true);
					findCopyItem.setEnabled(true);
					findDeleteItem.setEnabled(true);
					findSelectAllItem.setEnabled(true);
				} else {
					findCutItem.setEnabled(false);
					findCopyItem.setEnabled(false);
					findDeleteItem.setEnabled(false);
					findSelectAllItem.setEnabled(false);
				}
			}
		});
        
        findField.addKeyListener(new KeyAdapter() {
        	public void keyPressed(KeyEvent e) {
        		int key = e.getKeyCode();
        		if (key == KeyEvent.VK_ENTER) { 
        			String value = findField.getText(); 
        			findUndoItem.setNewValue(value);
        			if (!getOldFindValue().equals(value)) {
        				findUndoItem.setOldValue(getOldFindValue());
        				setOldFindValue(value);
        			}
        			if (value.trim().length() > 0) {
        				updateComboBox(findBox, LocalConfig.getInstance().getFindEntryList(), findField.getText()); 
        				findField.setText(value);  
        			} 
//        			System.out.println("undo old = " + findUndoItem.getOldValue());
//        			System.out.println("undo new = " + findUndoItem.getNewValue());
        		}
        	}
        }
        		);  
        
        ActionListener findButtonActionListener = new ActionListener() {
    		public void actionPerformed(ActionEvent ae) {
    			setFindText(findField.getText());
    			findUndoItem.setNewValue(findField.getText());
    			if (!getOldFindValue().equals(findField.getText())) {
    				findUndoItem.setOldValue(getOldFindValue());
    				setOldFindValue(findField.getText());
    			}
    			updateComboBox(findBox, LocalConfig.getInstance().getFindEntryList(), findField.getText()); 
//    			System.out.println("undo old = " + findUndoItem.getOldValue());
//    			System.out.println("undo new = " + findUndoItem.getNewValue());
    		}
    	};

    	findButton.addActionListener(findButtonActionListener);
    	
        ActionListener doneButtonActionListener = new ActionListener() {
    		public void actionPerformed(ActionEvent ae) {
//    			setVisible(false);
//    			dispose();				
    		}
    	};

    	doneButton.addActionListener(doneButtonActionListener);
    	
    	ActionListener searchBackwardsActionListener = new ActionListener() {
    		public void actionPerformed(ActionEvent actionEvent) {
    			//setFindText(findField.getText());								
    		}
    	};
    	
    	backwardsCheckBox.addActionListener(searchBackwardsActionListener);
    	
    	windowFocusListener = new WindowFocusListener()
        {
            public void windowGainedFocus(WindowEvent we)
            {
            	LocalConfig.getInstance().visualizationFindFocusLost = false;
            	LocalConfig.getInstance().visualizationFindFocusGained = true;
            }

            public void windowLostFocus(WindowEvent we)
            {               
            	LocalConfig.getInstance().visualizationFindFocusLost = true;
            	LocalConfig.getInstance().visualizationFindFocusGained = false;
            }
        };

        addWindowFocusListener(windowFocusListener);
        
    }
     
    private int getButtonWidth() 
    { 
        return 160; 
    } 
    
    private int getTextAreaWidth() 
    {  
        return 160;
    }
    
    private int getTextAreaHeight() 
    { 
        return 25; 
    }
    
    public void populateComboBox(SizedComboBox cb, ArrayList<String> list) {
    	cb.removeAllItems();
        for (int i = 0; i < list.size(); i++) {
            cb.addItem(list.get(i));
        }
    }

    public void updateComboBox(SizedComboBox cb, ArrayList<String> list, String entry) {    	
    	if (!list.contains(entry)) {
    		cb.addItem(entry);
    		list.add(entry);
    	}        
	}
    
    //from http://www.javakb.com/Uwe/Forum.aspx/java-programmer/21291/popupmenu-for-a-cell-in-a-JXTable
	private static String getClipboardContents(Object requestor) {
		Transferable t = Toolkit.getDefaultToolkit()
		.getSystemClipboard().getContents(requestor);
		if (t != null) {
			DataFlavor df = DataFlavor.stringFlavor;
			if (df != null) {
				try {
					Reader r = df.getReaderForText(t);
					char[] charBuf = new char[512];
					StringBuffer buf = new StringBuffer();
					int n;
					while ((n = r.read(charBuf, 0, charBuf.length)) > 0) {
						buf.append(charBuf, 0, n);
					}
					r.close();
					return (buf.toString());
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(null,                
							"Clipboard Error.",                
							"Error",                                
							JOptionPane.ERROR_MESSAGE);
					//ex.printStackTrace();
				} catch (UnsupportedFlavorException ex) {
					JOptionPane.showMessageDialog(null,                
							"Clipboard Error. Unsupported Flavor",                
							"Error",                                
							JOptionPane.ERROR_MESSAGE);
					//ex.printStackTrace();
				}
			}
		}
		return null;
	}

	private static void setClipboardContents(String s) {
	      StringSelection selection = new StringSelection(s);
	      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
	            selection, selection);
	}
    
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            	
                new VisualizationFindDialog().setVisible(true);
            }
        });
    }
}


