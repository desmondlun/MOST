package edu.rutgers.MOST.presentation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import edu.rutgers.MOST.data.SettingsFactory;

public class SolverSetUpDialog  extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static JLabel solverSelectionLabel = new JLabel(GraphicalInterfaceConstants.SOLVER_SELECTION_LABEL);
	public JRadioButton glpkRadioButton = new JRadioButton(GraphicalInterfaceConstants.GLPK_SOLVER_BUTTON_LABEL);
	public JRadioButton gurobiRadioButton = new JRadioButton(GraphicalInterfaceConstants.GUROBI_SOLVER_BUTTON_LABEL);
	public static JButton okButton = new JButton("    OK    ");
	public static JButton cancelButton = new JButton("  Cancel  ");
	public static JLabel gurobiLabel = new JLabel();
	
	public SolverSetUpDialog() {

		setTitle(GraphicalInterfaceConstants.SOLVER_DIALOG_TITLE);		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		getRootPane().setDefaultButton(okButton);
		
		//box layout
		Box vb = Box.createVerticalBox();
		Box hbSolverSelection = Box.createHorizontalBox();
		Box hbGurobiLabel = Box.createHorizontalBox();
		Box hbMetab = Box.createHorizontalBox();
		Box hbButton = Box.createHorizontalBox();
		
		solverSelectionLabel.setSize(new Dimension(150, 10));
		//top, left, bottom. right
		solverSelectionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		//solverSelectionLabel.setAlignmentX(CENTER_ALIGNMENT);

		JPanel solverSelectionPanel = new JPanel();
		solverSelectionPanel.setLayout(new BoxLayout(solverSelectionPanel, BoxLayout.X_AXIS));
		solverSelectionPanel.add(solverSelectionLabel);
		solverSelectionPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

		solverSelectionLabel.setMinimumSize(new Dimension(200, 15));
		
		JPanel glpkButtonPanel = new JPanel();
		glpkButtonPanel.setLayout(new BoxLayout(glpkButtonPanel, BoxLayout.X_AXIS));
		glpkButtonPanel.add(glpkRadioButton);
		glpkButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
		
		JPanel gurobiButtonPanel = new JPanel();
		gurobiButtonPanel.setLayout(new BoxLayout(gurobiButtonPanel, BoxLayout.X_AXIS));
		gurobiButtonPanel.add(gurobiRadioButton);
		gurobiButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
		
		glpkRadioButton.setMnemonic(KeyEvent.VK_L);
		gurobiRadioButton.setMnemonic(KeyEvent.VK_U);
		
		//Group the radio buttons.
		ButtonGroup group = new ButtonGroup();
		group.add(glpkRadioButton);
		group.add(gurobiRadioButton);
		glpkRadioButton.setSelected(true);
		
		hbSolverSelection.add(solverSelectionPanel);		
		hbSolverSelection.add(glpkButtonPanel);
		hbSolverSelection.add(gurobiButtonPanel);

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
		//gurobiLabel.setText(getGurobiLabelText());
		//gurobiLabel.setText("<HTML>" + GraphicalInterfaceConstants.GUROBI_NOT_INSTALLED_PREFIX + GraphicalInterfaceConstants.GUROBI_MINIMUM_VERSION + GraphicalInterfaceConstants.GUROBI_NOT_INSTALLED_SUFFIX + "</HTML>");
		//gurobiLabel.setText("<HTML>" + GraphicalInterfaceConstants.GUROBI_INSTALLED_MESSAGE + "</HTML>");
		
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

		vb.add(hbSolverSelection);
		vb.add(hbGurobiLabel);
		vb.add(hbMetab);
		vb.add(hbButton);
		add(vb);	
		
		glpkRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		
		gurobiRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		
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
	
	public void enableGurobiComponents() {
		gurobiRadioButton.setEnabled(true);
		gurobiLabel.setText("");
	}
	
	public void disableGurobiComponents() {
		gurobiRadioButton.setEnabled(false);
		gurobiLabel.setText("");
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









