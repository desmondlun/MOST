package edu.rutgers.MOST.presentation;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class NodeInformationDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;
	//http://stackoverflow.com/questions/14737810/jlabel-show-longer-text-as-multiple-lines
	// html can be used to make multi line label.
	public static JLabel messageLabel = new JLabel();	
	public static JButton okButton = new JButton("OK");	
	public JButton copyNodeInfoButton = new JButton("Copy Node Information to Clipboard");
	
	public NodeInformationDialog(String info) {
		// need to set up box layout
		setTitle(PathwaysFrameConstants.NODE_INFORMATION_TITLE);
		
		//box layout
		Box vb = Box.createVerticalBox();
		   	    
		Box hbMessageLabel = Box.createHorizontalBox();
		Box hbButton = Box.createHorizontalBox();
		
		messageLabel.setText(info);
		//messageLabel.setSize(new Dimension(150, 10));
		//top, left, bottom. right
		messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		messageLabel.setAlignmentX(CENTER_ALIGNMENT);

		JPanel messageLabelPanel = new JPanel();
		messageLabelPanel.setLayout(new BoxLayout(messageLabelPanel, BoxLayout.X_AXIS));
		messageLabelPanel.add(messageLabel);
		messageLabelPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

		hbMessageLabel.add(messageLabelPanel);
	    
	    JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(okButton);
		JLabel label = new JLabel("  ");
		buttonPanel.add(label);
		buttonPanel.add(copyNodeInfoButton);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0,20,20,20));

		hbButton.add(buttonPanel);
	    
		vb.add(hbMessageLabel);
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
		//based on code from http:stackoverflow.com/questions/6403821/how-to-add-an-image-to-a-jframe-title-bar
		final ArrayList<Image> icons = new ArrayList<Image>(); 
		icons.add(new ImageIcon("etc/most16.jpg").getImage()); 
		icons.add(new ImageIcon("etc/most32.jpg").getImage());

		NodeInformationDialog d = new NodeInformationDialog("test");

		d.setIconImages(icons);
		//d.setSize(360, 180);
		d.pack();
		d.setVisible(true);
	}

}
