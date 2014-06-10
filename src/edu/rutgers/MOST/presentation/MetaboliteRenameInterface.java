package edu.rutgers.MOST.presentation;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class MetaboliteRenameInterface  extends JDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static JButton okButton = new JButton("    OK    ");
	public static JButton cancelButton = new JButton("  Cancel  ");
	public static final JTextField textField = new JTextField();
	
	private String newName;

	public void setNewName(String newName) {
		this.newName = newName;
	}

	public String getNewName() {
		return newName;
	}
	
	public boolean duplicate;
	
	public MetaboliteRenameInterface()
			{
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		getRootPane().setDefaultButton(okButton);
		
		//box layout
		Box vb = Box.createVerticalBox();

		Box hbLabel = Box.createHorizontalBox();
		Box hbText = Box.createHorizontalBox();
		Box hbButton = Box.createHorizontalBox();

		JLabel topLabel = new JLabel();
		topLabel.setText(GraphicalInterfaceConstants.RENAME_METABOLITE_LABEL);
		topLabel.setSize(new Dimension(150, 10));
		//top, left, bottom. right
		topLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		topLabel.setAlignmentX(CENTER_ALIGNMENT);

		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
		labelPanel.add(topLabel);
		labelPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

		hbLabel.add(labelPanel);
		
		topLabel.setMinimumSize(new Dimension(200, 15));
		textField.setEditable(true);

		okButton.setMnemonic(KeyEvent.VK_O);
		okButton.setEnabled(false);
		JLabel blank = new JLabel("    "); 
		cancelButton.setMnemonic(KeyEvent.VK_C);

		textField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				enableSubmitButton();
			}
			public void removeUpdate(DocumentEvent e) {
				enableSubmitButton();
			}
			public void insertUpdate(DocumentEvent e) {
				enableSubmitButton();
			}

			public void enableSubmitButton() {
				if (textField.getText() != null) {
					okButton.setEnabled(true);
				}
			}
		});


		JPanel textPanel = new JPanel();
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.X_AXIS));
		textPanel.add(textField);
		textPanel.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));

		hbText.add(textPanel);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(okButton);
		buttonPanel.add(blank);
		buttonPanel.add(cancelButton);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(5,20,15,20));

		hbButton.add(buttonPanel);

		vb.add(hbLabel);
		vb.add(hbText);
		vb.add(hbButton);
		add(vb);	

		ActionListener cancelButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				textField.setText("");
				setVisible(false);
				dispose();				
			}
		};

		cancelButton.addActionListener(cancelButtonActionListener);
	} 	 
	
	/*
	public static void main(String[] args) throws Exception {
		//based on code from http://stackoverflow.com/questions/6403821/how-to-add-an-image-to-a-jframe-title-bar
		final ArrayList<Image> icons = new ArrayList<Image>(); 
		icons.add(new ImageIcon("images/most16.jpg").getImage()); 
		icons.add(new ImageIcon("images/most32.jpg").getImage());

		MetaboliteRenameInterface frame = new MetaboliteRenameInterface();

		frame.setIconImages(icons);
		frame.setSize(350, 170);
		frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	*/
}






