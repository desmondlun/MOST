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
import edu.rutgers.MOST.data.MetaboliteFactory;
import edu.rutgers.MOST.data.ReactionFactory;

public class IdentifierColumnNameDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public JComboBox<String> cbColumnName = new JComboBox<String>();
	
	public JButton okButton = new JButton("     OK     ");
	public JButton cancelButton = new JButton("  Cancel  ");

	public IdentifierColumnNameDialog(final String type, String title, final String columnType, final int selectedIndex) {

		getRootPane().setDefaultButton(okButton);

		setTitle(title);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		//setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		cbColumnName.setEditable(false);	

		cbColumnName.setPreferredSize(new Dimension(250, 25));
		cbColumnName.setMaximumSize(new Dimension(250, 25));
		cbColumnName.setMinimumSize(new Dimension(250, 25));

		populateComboBoxes(type, selectedIndex);
		
		//box layout
		Box vb = Box.createVerticalBox();

		Box hbLabels = Box.createHorizontalBox();
		Box hbTop = Box.createHorizontalBox();	    	    
		Box hbColumnLabel = Box.createHorizontalBox();	    
		Box hbColumn = Box.createHorizontalBox();
		
		Box vbLabels = Box.createVerticalBox();
		Box vbCombos = Box.createVerticalBox();

		Box hbLabeledCombos = Box.createHorizontalBox();
		Box hbButton = Box.createHorizontalBox();

		//top label
		JLabel topLabel = new JLabel();
		topLabel.setText("");
		topLabel.setSize(new Dimension(300, 30));
		topLabel.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
		
		hbTop.add(topLabel);	
		hbTop.setAlignmentX(LEFT_ALIGNMENT);

		hbLabels.add(hbTop);

		//column Label and combo
		JLabel columnLabel = new JLabel();
		columnLabel.setText(columnType + " Column");
		columnLabel.setPreferredSize(new Dimension(150, 25));
		columnLabel.setMaximumSize(new Dimension(150, 25));
		columnLabel.setMinimumSize(new Dimension(150, 25));
		columnLabel.setBorder(BorderFactory.createEmptyBorder(10,10,0,10));
		columnLabel.setAlignmentX(LEFT_ALIGNMENT);
		//columnLabel.setAlignmentY(TOP_ALIGNMENT);	    	    

		JPanel panelColumnLabel = new JPanel();
		panelColumnLabel.setLayout(new BoxLayout(panelColumnLabel, BoxLayout.X_AXIS));
		panelColumnLabel.add(columnLabel);
		panelColumnLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbColumnLabel.add(panelColumnLabel);
		hbColumnLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelColumnName = new JPanel();
		panelColumnName.setLayout(new BoxLayout(panelColumnName, BoxLayout.X_AXIS));
		panelColumnName.add(cbColumnName);
		panelColumnName.setBorder(BorderFactory.createEmptyBorder(0,0,10,10));
		panelColumnName.setAlignmentX(RIGHT_ALIGNMENT);

		hbColumn.add(panelColumnName);
		hbColumn.setAlignmentX(RIGHT_ALIGNMENT);

		vbLabels.add(hbColumnLabel);
		JLabel blankLabel1 = new JLabel("");
		vbLabels.add(blankLabel1);
		vbCombos.add(hbColumn);

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
				int index = -1;
				if (cbColumnName.getSelectedIndex() > -1) {
					if (type.equals(GraphicalInterfaceConstants.METABOLITES_COLUMNS_IDENTIFIER)) {
						// meta columns populated first, so if selected item is < meta column list size, it is a
						// meta column item, index is fixed columns length + index
						if (cbColumnName.getSelectedIndex() < LocalConfig.getInstance().getMetabolitesMetaColumnNames().size()) {
							index = cbColumnName.getSelectedIndex() + GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length;
						} else {
							// not a meta column, index is index of item in fixed column
							String name = GraphicalInterfaceConstants.metabolitesIdentifiersList.get(cbColumnName.getSelectedIndex() -
									LocalConfig.getInstance().getMetabolitesMetaColumnNames().size());
							//System.out.println(name);
							if (GraphicalInterfaceConstants.metabolitesList.contains(name)) {
								index = GraphicalInterfaceConstants.metabolitesList.indexOf(name);
							} 
						}
						if (columnType.equals(GraphicalInterfaceConstants.METABOLITE_KEGG_ID_COLUMN_NAME)) {
							LocalConfig.getInstance().setKeggMetaboliteIdColumn(index);
						} else if (columnType.equals(GraphicalInterfaceConstants.CHEBI_ID_COLUMN_NAME)) {
							LocalConfig.getInstance().setChebiIdColumn(index);
						}
					} else if (type.equals(GraphicalInterfaceConstants.REACTIONS_COLUMNS_IDENTIFIER)) {
						if (cbColumnName.getSelectedIndex() < LocalConfig.getInstance().getReactionsMetaColumnNames().size()) {
							index = cbColumnName.getSelectedIndex() + GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length;
						} else {
							String name = GraphicalInterfaceConstants.reactionsIdentifiersList.get(cbColumnName.getSelectedIndex() -
									LocalConfig.getInstance().getReactionsMetaColumnNames().size());
							//System.out.println(name);
							if (GraphicalInterfaceConstants.reactionsList.contains(name)) {
								index = GraphicalInterfaceConstants.reactionsList.indexOf(name);
							} 
						}
						//System.out.println(index);
						if (columnType.equals(GraphicalInterfaceConstants.EC_NUMBER_COLUMN_NAME)) {
							LocalConfig.getInstance().setEcNumberColumn(index);
						} else if (columnType.equals(GraphicalInterfaceConstants.REACTION_KEGG_ID_COLUMN_NAME)) {
							LocalConfig.getInstance().setKeggReactionIdColumn(index);
						}
					}
				}
				setVisible(false);
				dispose();	
			}
		};

		okButton.addActionListener(okButtonActionListener);

		ActionListener cancelButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				setVisible(false);
				dispose();
			}
		};

		cancelButton.addActionListener(cancelButtonActionListener);

	} 
	
	public void populateComboBoxes(String type, int selectedIndex) {
		cbColumnName.removeAllItems();
		//populate combo boxes
		if (type.equals(GraphicalInterfaceConstants.REACTIONS_COLUMNS_IDENTIFIER)) {
			for (int i = 0; i < LocalConfig.getInstance().getReactionsMetaColumnNames().size(); i++) {
				cbColumnName.addItem(LocalConfig.getInstance().getReactionsMetaColumnNames().get(i));
			}
			for (int j = 0; j < GraphicalInterfaceConstants.reactionsIdentifiersList.size(); j++) {
				cbColumnName.addItem(GraphicalInterfaceConstants.reactionsIdentifiersList.get(j));
			}
		} else if (type.equals(GraphicalInterfaceConstants.METABOLITES_COLUMNS_IDENTIFIER)) {
			for (int i = 0; i < LocalConfig.getInstance().getMetabolitesMetaColumnNames().size(); i++) {
				cbColumnName.addItem(LocalConfig.getInstance().getMetabolitesMetaColumnNames().get(i));
			}
			for (int j = 0; j < GraphicalInterfaceConstants.metabolitesIdentifiersList.size(); j++) {
				cbColumnName.addItem(GraphicalInterfaceConstants.metabolitesIdentifiersList.get(j));
			}
		}
		cbColumnName.setSelectedIndex(selectedIndex);
	}
	
	public static void main(String[] args) throws Exception {
		//based on code from http://stackoverflow.com/questions/6403821/how-to-add-an-image-to-a-jframe-title-bar
		final ArrayList<Image> icons = new ArrayList<Image>(); 
		icons.add(new ImageIcon("images/most16.jpg").getImage()); 
		icons.add(new ImageIcon("images/most32.jpg").getImage());

		IdentifierColumnNameDialog frame = new IdentifierColumnNameDialog("", "Column", "Type", -1);

		frame.setIconImages(icons);
		//frame.setSize(550, 270);
		frame.pack();
		frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}







