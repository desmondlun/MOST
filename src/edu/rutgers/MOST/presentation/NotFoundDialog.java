package edu.rutgers.MOST.presentation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class NotFoundDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public JButton okButton = new JButton("OK");	
	
	public NotFoundDialog() {
		// need to set up box layout
		setTitle("Item Not Found");
		
		getRootPane().setDefaultButton(okButton);
		
		//box layout
		Box vb = Box.createVerticalBox();

		Box hbLabel = Box.createHorizontalBox();
		Box hbButton = Box.createHorizontalBox();
		
		JLabel label = new JLabel("<html><p>MOST has not found the item you are searching for.");
		
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
		labelPanel.add(label);
		labelPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 10));
		
		hbLabel.add(labelPanel);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(okButton);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,20,20,20));

		hbButton.add(buttonPanel);
		
		vb.add(hbLabel);
		vb.add(hbButton);

		add(vb);
		
//		ActionListener okButtonActionListener = new ActionListener() {
//			public void actionPerformed(ActionEvent prodActionEvent) {
//				setVisible(false);
//				//dispose();
//			}
//		};
//		
//		okButton.addActionListener(okButtonActionListener);
		
	}
	
	public static void main(String[] args) {
		NotFoundDialog d = new NotFoundDialog();
		d.pack();
		d.setVisible(true);
	}
	
}

