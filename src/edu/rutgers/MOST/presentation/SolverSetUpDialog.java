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

public class SolverSetUpDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static JLabel solverSelectionLabel = new JLabel(GraphicalInterfaceConstants.SOLVER_SELECTION_LABEL);
	public static JComboBox<String> cbLinear = new JComboBox<String>(); 
	public static JComboBox<String> cbNonlinear = new JComboBox<String>(); 
	public static JComboBox<String> cbQuadratic = new JComboBox<String>(); 
	public static JButton okButton = new JButton("    OK    ");
	public static JButton cancelButton = new JButton("  Cancel  ");
	public static JLabel gurobiLabel = new JLabel();
	
	public SolverSetUpDialog() {

		setTitle(GraphicalInterfaceConstants.SOLVER_DIALOG_TITLE);		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		getRootPane().setDefaultButton(okButton);
		
		cbLinear.setSelectedIndex(-1);
		
		cbLinear.setEditable(false);
		cbNonlinear.setEditable(false);
		cbQuadratic.setEditable(false);

		cbLinear.setPreferredSize(new Dimension(150, 25));
		cbLinear.setMaximumSize(new Dimension(150, 25));
		cbLinear.setMinimumSize(new Dimension(150, 25));
		
		cbNonlinear.setPreferredSize(new Dimension(150, 25));
		cbNonlinear.setMaximumSize(new Dimension(150, 25));
		cbNonlinear.setMinimumSize(new Dimension(150, 25));
		
		cbQuadratic.setPreferredSize(new Dimension(150, 25));
		cbQuadratic.setMaximumSize(new Dimension(150, 25));
		cbQuadratic.setMinimumSize(new Dimension(150, 25));
		
		cbLinear.removeAll();
		cbQuadratic.removeAll();
		cbNonlinear.removeAll();
		for (int i = 0; i < GraphicalInterfaceConstants.MIXED_INTEGER_LINEAR_OPTIONS.length; i++) {
			cbLinear.addItem(GraphicalInterfaceConstants.MIXED_INTEGER_LINEAR_OPTIONS[i]);
		}
		for (int i = 0; i < GraphicalInterfaceConstants.QUADRATIC_OPTIONS.length; i++) {
			cbQuadratic.addItem(GraphicalInterfaceConstants.QUADRATIC_OPTIONS[i]);
		}
		for (int i = 0; i < GraphicalInterfaceConstants.NONLINEAR_OPTIONS.length; i++) {
			cbNonlinear.addItem(GraphicalInterfaceConstants.NONLINEAR_OPTIONS[i]);
		}
		
		//box layout
		Box vb = Box.createVerticalBox();
		Box hbGurobiLabel = Box.createHorizontalBox();
		Box hbLinearLabel = Box.createHorizontalBox();
		Box hbLinear = Box.createHorizontalBox();
		Box hbQuadraticLabel = Box.createHorizontalBox();
		Box hbQuadratic = Box.createHorizontalBox();
		Box hbNonlinearLabel = Box.createHorizontalBox();
		Box hbNonlinear = Box.createHorizontalBox();
		Box hbButton = Box.createHorizontalBox();
		
		Box vbLabels = Box.createVerticalBox();
		Box vbCombos = Box.createVerticalBox();
		
		Box hbLabeledCombos = Box.createHorizontalBox();
		
		solverSelectionLabel.setSize(new Dimension(150, 10));
		//top, left, bottom. right
		solverSelectionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		//solverSelectionLabel.setAlignmentX(CENTER_ALIGNMENT);

		JPanel solverSelectionPanel = new JPanel();
		solverSelectionPanel.setLayout(new BoxLayout(solverSelectionPanel, BoxLayout.X_AXIS));
		solverSelectionPanel.add(solverSelectionLabel);
		solverSelectionPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

		solverSelectionLabel.setMinimumSize(new Dimension(200, 15));

		gurobiLabel.setSize(new Dimension(150, 10));
		//top, left, bottom. right
		gurobiLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		gurobiLabel.setAlignmentX(CENTER_ALIGNMENT);

		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
		labelPanel.add(gurobiLabel);
		labelPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));

		hbGurobiLabel.add(labelPanel);
		
		gurobiLabel.setMinimumSize(new Dimension(200, 15));
//		gurobiLabel.setText(getGurobiLabelText());
//		gurobiLabel.setText("<HTML>" + GraphicalInterfaceConstants.GUROBI_NOT_INSTALLED_PREFIX + GraphicalInterfaceConstants.GUROBI_MINIMUM_VERSION + GraphicalInterfaceConstants.GUROBI_NOT_INSTALLED_SUFFIX + "</HTML>");
		gurobiLabel.setText("<HTML>" + GraphicalInterfaceConstants.GUROBI_INSTALLED_MESSAGE + "</HTML>");
		
		//Linear Label and combo
		JLabel linearLabel = new JLabel();
		linearLabel.setText(GraphicalInterfaceConstants.MIXED_INTEGER_LINEAR_LABEL);
		linearLabel.setPreferredSize(new Dimension(150, 25));
		linearLabel.setMaximumSize(new Dimension(150, 25));
		linearLabel.setMinimumSize(new Dimension(150, 25));
		linearLabel.setBorder(BorderFactory.createEmptyBorder(10,0,ColumnInterfaceConstants.LABEL_BOTTOM_BORDER_SIZE,10));
		linearLabel.setAlignmentX(LEFT_ALIGNMENT);
		//linearLabel.setAlignmentY(TOP_ALIGNMENT);	    	    

		JPanel panelLinearLabel = new JPanel();
		panelLinearLabel.setLayout(new BoxLayout(panelLinearLabel, BoxLayout.X_AXIS));
		panelLinearLabel.add(linearLabel);
		panelLinearLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbLinearLabel.add(panelLinearLabel);
		hbLinearLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelLinear = new JPanel();
		panelLinear.setLayout(new BoxLayout(panelLinear, BoxLayout.X_AXIS));
		panelLinear.add(cbLinear);
		panelLinear.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		panelLinear.setAlignmentX(RIGHT_ALIGNMENT);

		hbLinear.add(panelLinear);
		hbLinear.setAlignmentX(RIGHT_ALIGNMENT);

		vbLabels.add(hbLinearLabel);
		JLabel blankLabel3 = new JLabel("");
		vbLabels.add(blankLabel3);
		vbCombos.add(hbLinear);
		
		//Quadratic Label and combo
		JLabel quadraticLabel = new JLabel();
		quadraticLabel.setText(GraphicalInterfaceConstants.QUADRATIC_LABEL);
		quadraticLabel.setPreferredSize(new Dimension(150, 25));
		quadraticLabel.setMaximumSize(new Dimension(150, 25));
		quadraticLabel.setMinimumSize(new Dimension(150, 25));
		quadraticLabel.setBorder(BorderFactory.createEmptyBorder(10,0,ColumnInterfaceConstants.LABEL_BOTTOM_BORDER_SIZE,10));
		quadraticLabel.setAlignmentX(LEFT_ALIGNMENT);
		//quadraticLabel.setAlignmentY(TOP_ALIGNMENT);	    	    

		JPanel panelQuadraticLabel = new JPanel();
		panelQuadraticLabel.setLayout(new BoxLayout(panelQuadraticLabel, BoxLayout.X_AXIS));
		panelQuadraticLabel.add(quadraticLabel);
		panelQuadraticLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbQuadraticLabel.add(panelQuadraticLabel);
		hbQuadraticLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelQuadratic = new JPanel();
		panelQuadratic.setLayout(new BoxLayout(panelQuadratic, BoxLayout.X_AXIS));
		panelQuadratic.add(cbQuadratic);
		panelQuadratic.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		panelQuadratic.setAlignmentX(RIGHT_ALIGNMENT);

		hbQuadratic.add(panelQuadratic);
		hbQuadratic.setAlignmentX(RIGHT_ALIGNMENT);

		vbLabels.add(hbQuadraticLabel);
		JLabel blankLabel1 = new JLabel("");
		vbLabels.add(blankLabel1);
		vbCombos.add(hbQuadratic);
		
		//Nonlinear Label and combo
		JLabel nonlinearLabel = new JLabel();
		nonlinearLabel.setText(GraphicalInterfaceConstants.NONLINEAR_LABEL);
		nonlinearLabel.setPreferredSize(new Dimension(150, 25));
		nonlinearLabel.setMaximumSize(new Dimension(150, 25));
		nonlinearLabel.setMinimumSize(new Dimension(150, 25));
		nonlinearLabel.setBorder(BorderFactory.createEmptyBorder(10,0,ColumnInterfaceConstants.LABEL_BOTTOM_BORDER_SIZE,10));
		nonlinearLabel.setAlignmentX(LEFT_ALIGNMENT);
		//nonlinearLabel.setAlignmentY(TOP_ALIGNMENT);	    	    

		JPanel panelNonlinearLabel = new JPanel();
		panelNonlinearLabel.setLayout(new BoxLayout(panelNonlinearLabel, BoxLayout.X_AXIS));
		panelNonlinearLabel.add(nonlinearLabel);
		panelNonlinearLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

		hbNonlinearLabel.add(panelNonlinearLabel);
		hbNonlinearLabel.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelNonlinear = new JPanel();
		panelNonlinear.setLayout(new BoxLayout(panelNonlinear, BoxLayout.X_AXIS));
		panelNonlinear.add(cbNonlinear);
		panelNonlinear.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		panelNonlinear.setAlignmentX(RIGHT_ALIGNMENT);

		hbNonlinear.add(panelNonlinear);
		hbNonlinear.setAlignmentX(RIGHT_ALIGNMENT);

		vbLabels.add(hbNonlinearLabel);
		JLabel blankLabel2 = new JLabel("");
		vbLabels.add(blankLabel2);
		vbCombos.add(hbNonlinear);
		
		okButton.setMnemonic(KeyEvent.VK_O);
		okButton.setEnabled(true);
		JLabel blank = new JLabel("    "); 
		cancelButton.setMnemonic(KeyEvent.VK_C);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(okButton);
		buttonPanel.add(blank);
		buttonPanel.add(cancelButton);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(5,20,15,20));

		hbButton.add(buttonPanel);

		vb.add(hbGurobiLabel);
		hbLabeledCombos.add(vbLabels);
		hbLabeledCombos.add(vbCombos);
		vb.add(hbLabeledCombos);
		vb.add(hbButton);
		add(vb);	
		
		ActionListener okButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				
			}
		};
		
		ActionListener cancelButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				setVisible(false);
			}
		}; 
		
		okButton.addActionListener(okButtonActionListener);
		cancelButton.addActionListener(cancelButtonActionListener);
		
	} 	
	
	public static void main(String[] args) throws Exception {
		
		//based on code from http://stackoverflow.com/questions/6403821/how-to-add-an-image-to-a-jframe-title-bar
		final ArrayList<Image> icons = new ArrayList<Image>(); 
		icons.add(new ImageIcon("etc/most16.jpg").getImage()); 
		icons.add(new ImageIcon("etc/most32.jpg").getImage());

		SolverSetUpDialog frame = new SolverSetUpDialog();
		frame.setIconImages(icons);
		frame.setSize(GraphicalInterfaceConstants.SOLVER_DIALOG_WIDTH, GraphicalInterfaceConstants.SOLVER_DIALOG_HEIGHT);
		frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}










