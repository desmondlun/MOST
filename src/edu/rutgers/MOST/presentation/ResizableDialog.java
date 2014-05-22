package edu.rutgers.MOST.presentation;

import javax.swing.*;  
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.*;  
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
   
// based on code from http://www.coderanch.com/t/344723/GUI/java/JDialog-resize
public class ResizableDialog extends JDialog  
{  
    /**
	 * 
	 */
	 
	private JPanel LabelPanel;
    private JPanel ButtonPanel;  
    public JButton OKButton;  
    private JButton DetailsButton;
    private JPanel MessagePanel;  
    private JLabel Label;
    public boolean messageShown;
    
    private String errorTitle;

	public String getErrorTitle() {
		return errorTitle;
	}

	public void setErrorTitle(String errorTitle) {
		this.errorTitle = errorTitle;
	}

	private String errorDescription;
	
	public String getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}

	private String errorMessage;

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

    
    private final JMenuItem copyItem = new JMenuItem("Copy");
	private final JMenuItem selectAllItem = new JMenuItem("Select All");

	private static final long serialVersionUID = 1L;
	public ResizableDialog(String errorTitle, String errorDescription, String errorMessage) 
    {		
        initComponents(errorTitle, errorDescription, errorMessage); 
    }  
                           
    private void initComponents(String errorTitle, String errorDescription, String errorMessage)  
    {   	
    	setErrorTitle(errorTitle);
    	setErrorDescription(errorDescription);
    	setErrorMessage(errorMessage);
    	messageShown = false;
    	LabelPanel = new JPanel(); 
    	Label = new JLabel(); 
        ButtonPanel = new JPanel();  
        OKButton = new JButton();  
        DetailsButton = new JButton();  
        MessagePanel = new JPanel();   
              
        getRootPane().setDefaultButton(OKButton);
        
        setTitle(errorTitle);
        Label.setText(errorDescription);
   
        //setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);  
        
        LabelPanel.add(Label);
        getContentPane().add(LabelPanel, java.awt.BorderLayout.NORTH); 
        getContentPane().add(MessagePanel, java.awt.BorderLayout.SOUTH);  
   
        ButtonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));  
   
        OKButton.setText("  OK  ");  
        OKButton.addActionListener(new ActionListener()  
        {  
            public void actionPerformed(ActionEvent evt)  
            {  
            	setVisible(false); 
            }  
        });  
   
        ButtonPanel.add(OKButton);
        
        DetailsButton.setText("Details >>"); 
        DetailsButton.setMnemonic(KeyEvent.VK_D);
        DetailsButton.addActionListener(new ActionListener()  
        {  
            public void actionPerformed(ActionEvent evt)  
            {  
            	setTitle(getErrorTitle());
            	Label.setText(getErrorDescription());
            	// Add or remove some stuff!  
                if(!messageShown) {
                	JTextArea textArea = createTextArea();
                	//JTextArea textArea = new JTextArea();                	                	
                	DetailsButton.setText("<< Details");
                	textArea.setText(getErrorMessage());
                	textArea.setCaretPosition(0);
                	//add reaction field to scroll pane		
            		JScrollPane scrollPane = new JScrollPane(textArea);
            		scrollPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,20));
            		scrollPane.setPreferredSize(new Dimension(500, 200));
                	MessagePanel.add(scrollPane);
                	messageShown = true;
                } else {
                	DetailsButton.setText("Details >>");
                	Component[] old = MessagePanel.getComponents();  
                    for( Component c : old )  
                    {   
                        if( c instanceof JTextArea )  
                        {  
                        	MessagePanel.remove(c);  
                        } 
                        if( c instanceof JScrollPane )  
                        {  
                        	MessagePanel.remove(c);  
                        }
                    } 
                    messageShown = false;
                }
                              
                // resize with the new components  
                pack();   
            }  
        });  
   
        ButtonPanel.add(DetailsButton);
        
        getContentPane().add(ButtonPanel, java.awt.BorderLayout.CENTER);  
   
        pack();  
    }                                                                                                  
    
    public JTextArea createTextArea() {
    	final JTextArea textArea = new JTextArea();

    	textArea.setSize(200, 200);
    	textArea.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
    	//textArea.setFont(new Font("monospaced", Font.PLAIN, 14));
    	textArea.setEditable(false);

    	final JPopupMenu popupMenu = new JPopupMenu(); 
    	copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		selectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		popupMenu.add(copyItem);
		copyItem.setEnabled(true);
		copyItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) { 	
				setClipboardContents(textArea.getSelectedText());							
			}
		});
		popupMenu.add(selectAllItem);
		selectAllItem.setEnabled(true);
		selectAllItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) { 
				textArea.selectAll();							
			}
		});
		
		textArea.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e)  {check(e);}
			public void mouseReleased(MouseEvent e) {check(e);}

			public void check(MouseEvent e) {
				if (e.isPopupTrigger()) { //if the event shows the menu
					popupMenu.show(textArea, e.getX(), e.getY()); 
				}
			}
		});	
		
		textArea.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				fieldChangeAction();
			}
			public void removeUpdate(DocumentEvent e) {
				fieldChangeAction();
			}
			public void insertUpdate(DocumentEvent e) {
				fieldChangeAction();
			}
			public void fieldChangeAction() {
				if (textArea.getText().length() > 0) {
					copyItem.setEnabled(true);
					selectAllItem.setEnabled(true);
				} else {
					copyItem.setEnabled(false);
					selectAllItem.setEnabled(false);
				}
			}
		});
		
		return textArea;
    	
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

  	private static boolean isClipboardContainingText(Object requestor) {
  		Transferable t = Toolkit.getDefaultToolkit()
  		.getSystemClipboard().getContents(requestor);
  		return t != null
  		&& (t.isDataFlavorSupported(DataFlavor.stringFlavor) || t
  				.isDataFlavorSupported(DataFlavor.plainTextFlavor));
  	}
  	
  	private static void setClipboardContents(String s) {
  	      StringSelection selection = new StringSelection(s);
  	      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
  	            selection, selection);
  	}
  	

    public static void main(String[] args) {
    	final ArrayList<Image> icons = new ArrayList<Image>(); 
		icons.add(new ImageIcon("etc/most16.jpg").getImage()); 
		icons.add(new ImageIcon("etc/most32.jpg").getImage());
    	
    	ResizableDialog r = new ResizableDialog("Error", "Error message", "Testing\nTesting\nTesting\nTesting");
    	
    	r.setIconImages(icons);
    	r.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    	r.setLocationRelativeTo(null);
    	r.setVisible(true);
    }
}  
