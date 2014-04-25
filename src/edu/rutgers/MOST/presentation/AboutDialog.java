package edu.rutgers.MOST.presentation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class AboutDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static JLabel mostLabel = new JLabel(GraphicalInterfaceConstants.ABOUT_BOX_TEXT);
	public static JLabel versionLabel = new JLabel(GraphicalInterfaceConstants.ABOUT_BOX_VERSION_TEXT);
	public static JButton licenseButton = new JButton();	
	public static JButton okButton = new JButton("OK");	
	
	public AboutDialog() {
		// need to set up box layout
		setTitle(GraphicalInterfaceConstants.ABOUT_BOX_TITLE);
		
		//box layout
		Box vb = Box.createVerticalBox();
		   	    
		Box hbMostLabel = Box.createHorizontalBox();
		Box hbVersionLabel = Box.createHorizontalBox();
		Box hbLink = Box.createHorizontalBox();
		Box hbButton = Box.createHorizontalBox();
				
		mostLabel.setSize(new Dimension(150, 10));
		//top, left, bottom. right
		mostLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		mostLabel.setAlignmentX(CENTER_ALIGNMENT);

		JPanel mostLabelPanel = new JPanel();
		mostLabelPanel.setLayout(new BoxLayout(mostLabelPanel, BoxLayout.X_AXIS));
		mostLabelPanel.add(mostLabel);
		mostLabelPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

		hbMostLabel.add(mostLabelPanel);
		
		versionLabel.setSize(new Dimension(150, 10));
		//top, left, bottom. right
		versionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		versionLabel.setAlignmentX(CENTER_ALIGNMENT);

		JPanel versionLabelPanel = new JPanel();
		versionLabelPanel.setLayout(new BoxLayout(versionLabelPanel, BoxLayout.X_AXIS));
		versionLabelPanel.add(versionLabel);
		versionLabelPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

		hbVersionLabel.add(versionLabelPanel);
		
		// based on http://stackoverflow.com/questions/527719/how-to-add-hyperlink-in-jlabel
		// making a button into a link
		//JButton licenseButton = new JButton();
		licenseButton.setText("<HTML><FONT color=\"#000099\"><U>Licensing Information</U></FONT>"
	        + "</HTML>");
		licenseButton.setHorizontalAlignment(SwingConstants.CENTER);
		licenseButton.setBorderPainted(false);
		licenseButton.setOpaque(false);
		licenseButton.setBackground(Color.WHITE);
		//licenseButton.addActionListener(new OpenUrlAction());
	    add(licenseButton);	
	    
	    JPanel linkPanel = new JPanel();
	    linkPanel.setLayout(new BoxLayout(linkPanel, BoxLayout.X_AXIS));
	    linkPanel.add(licenseButton);
	    linkPanel.setBorder(BorderFactory.createEmptyBorder(10,20,20,20));
	    
	    hbLink.add(linkPanel);
	    
	    JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(okButton);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0,20,20,20));

		hbButton.add(buttonPanel);
	    
		vb.add(hbMostLabel);
		vb.add(hbVersionLabel);
		vb.add(hbLink);
		vb.add(hbButton);

		add(vb);
		
		ActionListener okButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				setVisible(false);
				dispose();
			}
		};
		
		okButton.addActionListener(okButtonActionListener);
		
	}
	
	public static void main(String[] args) {
		AboutDialog d = new AboutDialog();
		d.setSize(400, 180);
		d.setVisible(true);
	}
	
}
