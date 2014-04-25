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

public class SuspiciousMetabolitesDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//http://stackoverflow.com/questions/14737810/jlabel-show-longer-text-as-multiple-lines
	// html can be used to make multi line label.
	public static JLabel messageLabel = new JLabel("<html>"+ GraphicalInterfaceConstants.SUSPICIOUS_METABOLITES_MESSAGE + "</html>");
	public static JButton messageButton = new JButton();	
	public static JButton okButton = new JButton("OK");	
	
	public SuspiciousMetabolitesDialog() {
		// need to set up box layout
		setTitle(GraphicalInterfaceConstants.SUSPICIOUS_METABOLITES_TITLE);
		
		//box layout
		Box vb = Box.createVerticalBox();
		   	    
		Box hbMessageLabel = Box.createHorizontalBox();
		Box hbLink = Box.createHorizontalBox();
		Box hbButton = Box.createHorizontalBox();
		
		messageLabel.setSize(new Dimension(150, 10));
		//top, left, bottom. right
		messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));
		messageLabel.setAlignmentX(CENTER_ALIGNMENT);

		JPanel messageLabelPanel = new JPanel();
		messageLabelPanel.setLayout(new BoxLayout(messageLabelPanel, BoxLayout.X_AXIS));
		messageLabelPanel.add(messageLabel);
		messageLabelPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

		hbMessageLabel.add(messageLabelPanel);
		
//		class OpenUrlAction implements ActionListener {
//			@Override public void actionPerformed(ActionEvent e) {
//				try{ 
//					String url = GraphicalInterfaceConstants.ABOUT_LICENSE_URL;  
//					java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));  
//				}  
//				catch (java.io.IOException e1) {  
//					JOptionPane.showMessageDialog(null,                
//							GraphicalInterfaceConstants.HELP_URL_NOT_FOUND_MESSAGE,                
//							GraphicalInterfaceConstants.HELP_URL_NOT_FOUND_TITLE,                                
//							JOptionPane.ERROR_MESSAGE);   
//				}
//			}
//		}
		
		// based on http://stackoverflow.com/questions/527719/how-to-add-hyperlink-in-jlabel
		// making a button into a link
		//JButton messageButton = new JButton();
		messageButton.setText(GraphicalInterfaceConstants.SUSPICIOUS_METABOLITES_LINK);
		messageButton.setHorizontalAlignment(SwingConstants.CENTER);
		messageButton.setBorderPainted(false);
		messageButton.setOpaque(false);
		messageButton.setBackground(Color.WHITE);
		//messageButton.addActionListener(new OpenUrlAction());
	    add(messageButton);	
	    
	    JPanel linkPanel = new JPanel();
	    linkPanel.setLayout(new BoxLayout(linkPanel, BoxLayout.X_AXIS));
	    linkPanel.add(messageButton);
	    linkPanel.setBorder(BorderFactory.createEmptyBorder(10,20,20,20));
	    
	    hbLink.add(linkPanel);
	    
	    JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(okButton);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0,20,20,20));

		hbButton.add(buttonPanel);
	    
		vb.add(hbMessageLabel);
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
		SuspiciousMetabolitesDialog d = new SuspiciousMetabolitesDialog();
		d.setSize(360, 180);
		d.setVisible(true);
	}
	
}

