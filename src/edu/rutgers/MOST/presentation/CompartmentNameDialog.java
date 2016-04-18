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
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.data.SBMLCompartment;

public class CompartmentNameDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public JComboBox<String> cbCompartmentName = new JComboBox<String>();

	public JButton okButton = new JButton("     OK     ");
	public JButton cancelButton = new JButton("  Cancel  ");

	public CompartmentNameDialog() {

		getRootPane().setDefaultButton(okButton);

		setTitle(CompartmentNameAbbreviationConstants.DIALOG_TITLE);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		//setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		cbCompartmentName.setEditable(false);		

		cbCompartmentName.setPreferredSize(new Dimension(250, 25));
		cbCompartmentName.setMaximumSize(new Dimension(250, 25));
		cbCompartmentName.setMinimumSize(new Dimension(250, 25));

		populateComboBoxes();
		
		//box layout
		Box vb = Box.createVerticalBox();

		Box hbLabels = Box.createHorizontalBox();
		Box hbTop = Box.createHorizontalBox();	    	    
		Box hbCompartmentLabel = Box.createHorizontalBox();	    
		Box hbCompartment = Box.createHorizontalBox();
		
		Box vbLabels = Box.createVerticalBox();
		Box vbCombos = Box.createVerticalBox();

		Box hbLabeledCombos = Box.createHorizontalBox();
		Box hbButton = Box.createHorizontalBox();

		//top label
		JLabel topLabel = new JLabel();
		topLabel.setText(CompartmentNameAbbreviationConstants.TOP_LABEL);
		topLabel.setSize(new Dimension(300, 30));
		topLabel.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
		
		hbTop.add(topLabel);	
		hbTop.setAlignmentX(LEFT_ALIGNMENT);

		hbLabels.add(hbTop);

		//compartment Label and combo
		JLabel compartmentLabel = new JLabel();
		compartmentLabel.setText(CompartmentsConstants.COMPARTMENT_NAME_LABEL);
		compartmentLabel.setPreferredSize(new Dimension(150, 25));
		compartmentLabel.setMaximumSize(new Dimension(150, 25));
		compartmentLabel.setMinimumSize(new Dimension(150, 25));
		compartmentLabel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		compartmentLabel.setAlignmentX(LEFT_ALIGNMENT);
		compartmentLabel.setAlignmentY(TOP_ALIGNMENT);	    	    

		JPanel panelCompartmentLabel = new JPanel();
		panelCompartmentLabel.setLayout(new BoxLayout(panelCompartmentLabel, BoxLayout.X_AXIS));
		panelCompartmentLabel.add(compartmentLabel);
		panelCompartmentLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbCompartmentLabel.add(panelCompartmentLabel);
		hbCompartmentLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelCytosol = new JPanel();
		panelCytosol.setLayout(new BoxLayout(panelCytosol, BoxLayout.X_AXIS));
		panelCytosol.add(cbCompartmentName);
		panelCytosol.setBorder(BorderFactory.createEmptyBorder(0,0,10,10));
		panelCytosol.setAlignmentX(RIGHT_ALIGNMENT);

		hbCompartment.add(panelCytosol);
		hbCompartment.setAlignmentX(RIGHT_ALIGNMENT);

		vbLabels.add(hbCompartmentLabel);
		JLabel blankLabel1 = new JLabel("");
		vbLabels.add(blankLabel1);
		vbCombos.add(hbCompartment);
		
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
		hbLabeledCombos.add(vbLabels);
		hbLabeledCombos.add(vbCombos);
		vb.add(hbLabeledCombos);
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
	
    public void populateComboBoxes() {
    	if (LocalConfig.getInstance().getListOfCompartments() != null && 
    			LocalConfig.getInstance().getListOfCompartments().size() > 0) {
    		cbCompartmentName.removeAllItems();
    		//populate combo boxes
    		for (int c = 0; c < LocalConfig.getInstance().getListOfCompartments().size(); c++) { 
    			String item = LocalConfig.getInstance().getListOfCompartments().get(c).getId();
    			if (LocalConfig.getInstance().getListOfCompartments().get(c).getName() != null &&
    					LocalConfig.getInstance().getListOfCompartments().get(c).getName().length() > 0) {
    				item += " (" + LocalConfig.getInstance().getListOfCompartments().get(c).getName() + ")";
    			}
    			cbCompartmentName.addItem(item);
    		}
    		// cannot allow blank compartment to be selected. do not uncoomment.
    		//cbCompartmentName.setSelectedIndex(-1);
    		for (int c = 0; c < LocalConfig.getInstance().getListOfCompartments().size(); c++) {
    			//filters to match compartment names from list of compartments	
    			if (LocalConfig.getInstance().getListOfCompartments().get(c).getName() != null) {
    				if((LocalConfig.getInstance().getListOfCompartments().get(c).getName().toLowerCase()).contains(CompartmentsConstants.CYTOSOL_FILTER[0])) {
        				cbCompartmentName.setSelectedIndex(c);
        			} 
    			}
    		}
    	}
    }
    
    public void setSelectedItemByFilter(JComboBox<String> cb, ArrayList<SBMLCompartment> compList, 
    		String[] filter, int index) {
    	if (compList.get(index).getName().contains(filter[0])) {
			cb.setSelectedIndex(index);
		} else {
			cb.setSelectedIndex(-1);
		}
    }
	
	public static void main(String[] args) throws Exception {
		//based on code from http://stackoverflow.com/questions/6403821/how-to-add-an-image-to-a-jframe-title-bar
		final ArrayList<Image> icons = new ArrayList<Image>(); 
		icons.add(new ImageIcon("etc/most16.jpg").getImage()); 
		icons.add(new ImageIcon("etc/most32.jpg").getImage());

		CompartmentNameDialog frame = new CompartmentNameDialog();

		frame.setIconImages(icons);
		//frame.setSize(550, 270);
		frame.pack();
		frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}






