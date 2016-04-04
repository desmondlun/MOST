package edu.rutgers.MOST.presentation;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class FluxLevelsDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public JTextField maxFluxField = new JTextField();
	public JTextField secondaryMaxFluxField = new JTextField();
	
	public JButton okButton = new JButton("     OK     ");
	public JButton cancelButton = new JButton("  Cancel  ");

	public FluxLevelsDialog() {

		getRootPane().setDefaultButton(okButton);

		setTitle(FluxLevelsConstants.TITLE);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		//setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		maxFluxField.setPreferredSize(new Dimension(120, 25));
		maxFluxField.setMaximumSize(new Dimension(120, 25));
		maxFluxField.setMinimumSize(new Dimension(120, 25));
		
		secondaryMaxFluxField.setPreferredSize(new Dimension(120, 25));
		secondaryMaxFluxField.setMaximumSize(new Dimension(120, 25));
		secondaryMaxFluxField.setMinimumSize(new Dimension(120, 25));
		
		//box layout
		Box vb = Box.createVerticalBox();

		Box hbLabels = Box.createHorizontalBox();
		Box hbTop = Box.createHorizontalBox();	    	    
		Box hbMaxFluxLabel = Box.createHorizontalBox();	    
		Box hbMaxFlux = Box.createHorizontalBox();
		Box hbSecondaryMaxFluxLabel = Box.createHorizontalBox();	    
		Box hbSecondaryMaxFlux = Box.createHorizontalBox();
		
		Box vbLabels = Box.createVerticalBox();
		Box vbFields = Box.createVerticalBox();

		Box hbLabeledFields = Box.createHorizontalBox();
		Box hbButton = Box.createHorizontalBox();

		//top label
		JLabel topLabel = new JLabel();
		topLabel.setText(FluxLevelsConstants.TOP_LABEL);
		topLabel.setSize(new Dimension(300, 30));
		topLabel.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
		
		hbTop.add(topLabel);	
		hbTop.setAlignmentX(LEFT_ALIGNMENT);

		hbLabels.add(hbTop);

		//max flux label and combo
		JLabel maxFluxLabel = new JLabel();
		maxFluxLabel.setText(FluxLevelsConstants.MAXIMUM_FLUX_LABEL);
		maxFluxLabel.setPreferredSize(new Dimension(150, 25));
		maxFluxLabel.setMaximumSize(new Dimension(150, 25));
		maxFluxLabel.setMinimumSize(new Dimension(150, 25));
		maxFluxLabel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		maxFluxLabel.setAlignmentX(LEFT_ALIGNMENT);
		maxFluxLabel.setAlignmentY(TOP_ALIGNMENT);		    	    

		JPanel maxFluxLabelPanel = new JPanel();
		maxFluxLabelPanel.setLayout(new BoxLayout(maxFluxLabelPanel, BoxLayout.X_AXIS));
		maxFluxLabelPanel.add(maxFluxLabel);
		maxFluxLabelPanel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbMaxFluxLabel.add(maxFluxLabelPanel);
		hbMaxFluxLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelMaxFlux = new JPanel();
		panelMaxFlux.setLayout(new BoxLayout(panelMaxFlux, BoxLayout.X_AXIS));
		panelMaxFlux.add(maxFluxField);
		panelMaxFlux.setBorder(BorderFactory.createEmptyBorder(0,0,10,10));
		panelMaxFlux.setAlignmentX(RIGHT_ALIGNMENT);

		hbMaxFlux.add(panelMaxFlux);
		hbMaxFlux.setAlignmentX(RIGHT_ALIGNMENT);

		vbLabels.add(hbMaxFluxLabel);
		JLabel blankLabel1 = new JLabel("");
		vbLabels.add(blankLabel1);
		vbFields.add(hbMaxFlux);

		//secondary max flux label and combo
		JLabel secondaryMFluxLabel = new JLabel();
		secondaryMFluxLabel.setText(FluxLevelsConstants.SECONDARY_MAXIMUM_FLUX_LABEL);
		secondaryMFluxLabel.setPreferredSize(new Dimension(150, 25));
		secondaryMFluxLabel.setMaximumSize(new Dimension(150, 25));
		secondaryMFluxLabel.setMinimumSize(new Dimension(150, 25));
		secondaryMFluxLabel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		secondaryMFluxLabel.setAlignmentX(LEFT_ALIGNMENT);
		secondaryMFluxLabel.setAlignmentY(TOP_ALIGNMENT);	

		JPanel secondaryMFluxLabelPanel = new JPanel();
		secondaryMFluxLabelPanel.setLayout(new BoxLayout(secondaryMFluxLabelPanel, BoxLayout.X_AXIS));
		secondaryMFluxLabelPanel.add(secondaryMFluxLabel);
		secondaryMFluxLabelPanel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbSecondaryMaxFluxLabel.add(secondaryMFluxLabelPanel);
		hbSecondaryMaxFluxLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelSecondaryMaxFlux = new JPanel();
		panelSecondaryMaxFlux.setLayout(new BoxLayout(panelSecondaryMaxFlux, BoxLayout.X_AXIS));
		panelSecondaryMaxFlux.add(secondaryMaxFluxField);
		panelSecondaryMaxFlux.setBorder(BorderFactory.createEmptyBorder(0,0,10,10));
		panelSecondaryMaxFlux.setAlignmentX(RIGHT_ALIGNMENT);

		hbSecondaryMaxFlux.add(panelSecondaryMaxFlux);
		hbSecondaryMaxFlux.setAlignmentX(RIGHT_ALIGNMENT);

		vbLabels.add(hbSecondaryMaxFluxLabel);
		JLabel blankLabel2 = new JLabel("");
		vbLabels.add(blankLabel2);
		vbFields.add(hbSecondaryMaxFlux);

		okButton.setMnemonic(KeyEvent.VK_O);
		JLabel blank = new JLabel("    "); 
		cancelButton.setMnemonic(KeyEvent.VK_C);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(okButton);
		buttonPanel.add(blank);
		buttonPanel.add(cancelButton);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,20,20,20));

		hbButton.add(buttonPanel);

		vb.add(hbLabels);
		hbLabeledFields.add(vbLabels);
		hbLabeledFields.add(vbFields);
		vb.add(hbLabeledFields);
		vb.add(hbButton);

		add(vb);
		
		ActionListener okButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
					
			}
		};

		okButton.addActionListener(okButtonActionListener);

		ActionListener cancelButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
//				setVisible(false);
//				dispose();
			}
		};

		cancelButton.addActionListener(cancelButtonActionListener);

	} 
	
	public static void main(String[] args) throws Exception {
		//based on code from http://stackoverflow.com/questions/6403821/how-to-add-an-image-to-a-jframe-title-bar
		final ArrayList<Image> icons = new ArrayList<Image>(); 
		icons.add(new ImageIcon("etc/most16.jpg").getImage()); 
		icons.add(new ImageIcon("etc/most32.jpg").getImage());

		FluxLevelsDialog frame = new FluxLevelsDialog();

		frame.setIconImages(icons);
		//frame.setSize(550, 270);
		frame.pack();
		frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}






