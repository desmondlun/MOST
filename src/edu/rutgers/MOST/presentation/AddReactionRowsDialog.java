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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

// This may appear redundant since AddMetaboliteRowsDialog and AddReactionRows Dialog are exactly 
// the same but when having one AddRowsDialog and extending for these two classes, clicking OK
// button added rows to both tables. The other option of using conditionals such as
// if tabbed pane index = 0 or 1 seems messier than this approach
public class AddReactionRowsDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static JButton okButton = new JButton("    OK    ");

	public static JButton getOkButton() {
		return okButton;
	}

	public static void setOkButton(JButton okButton) {
		AddReactionRowsDialog.okButton = okButton;
	}

	private static JButton cancelButton = new JButton("  Cancel  ");

	public static JButton getCancelButton() {
		return cancelButton;
	}

	public static void setCancelButton(JButton cancelButton) {
		AddReactionRowsDialog.cancelButton = cancelButton;
	}

	public static final JTextField textField = new JTextField();

	public AddReactionRowsDialog() {

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		getRootPane().setDefaultButton(okButton);

		//box layout
		Box vb = Box.createVerticalBox();

		Box hbLabel = Box.createHorizontalBox();
		Box hbText = Box.createHorizontalBox();
		Box hbButton = Box.createHorizontalBox();

		JLabel topLabel = new JLabel();
		topLabel.setText(GraphicalInterfaceConstants.ADD_ROWS_DIALOG_LABEL);
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
		textField.setText(GraphicalInterfaceConstants.DEFAULT_NUM_ADD_ROWS);
	    textField.setHorizontalAlignment(JTextField.RIGHT);
				
		textField.setEditable(true);
		
		textField.addFocusListener(new FocusListener() {

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

		okButton.setMnemonic(KeyEvent.VK_O);
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
				//setVisible(false);
				//dispose();				
			}
		};

		cancelButton.addActionListener(cancelButtonActionListener);
	} 	 
	
	public static void main(String[] args) throws Exception {
		//based on code from http://stackoverflow.com/questions/6403821/how-to-add-an-image-to-a-jframe-title-bar
		final ArrayList<Image> icons = new ArrayList<Image>(); 
		icons.add(new ImageIcon("images/most16.jpg").getImage()); 
		icons.add(new ImageIcon("images/most32.jpg").getImage());

		AddReactionRowsDialog frame = new AddReactionRowsDialog();

		frame.setIconImages(icons);
		frame.setSize(220, 150);
		frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}








