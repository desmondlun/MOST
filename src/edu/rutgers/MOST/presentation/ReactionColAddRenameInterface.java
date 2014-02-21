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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import edu.rutgers.MOST.config.LocalConfig;

public class ReactionColAddRenameInterface  extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static JButton okButton = new JButton("    OK    ");
	public static JButton cancelButton = new JButton("  Cancel  ");
	public static final JTextField textField = new JTextField();

	public ReactionColAddRenameInterface() {

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		getRootPane().setDefaultButton(okButton);

		//box layout
		Box vb = Box.createVerticalBox();

		Box hbLabel = Box.createHorizontalBox();
		Box hbText = Box.createHorizontalBox();
		Box hbButton = Box.createHorizontalBox();

		JLabel topLabel = new JLabel();
		topLabel.setText(GraphicalInterfaceConstants.COLUMN_ADD_RENAME_LABEL);
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
		textField.setEditable(true);

		okButton.setMnemonic(KeyEvent.VK_O);
		okButton.setEnabled(false);
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

	public boolean isColumnDuplicate() {
		String columnName = textField.getText();
		int columnIndex = -1;
		boolean duplicate = false;
		for (int i = 0; i < GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length; i++) {
			if (GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[i].equals(columnName)) {
				duplicate = true;
				columnIndex = i;
			}
		}
		for (int j = 0; j < LocalConfig.getInstance().getReactionsMetaColumnNames().size(); j++) {
			if (LocalConfig.getInstance().getReactionsMetaColumnNames().get(j).equals(columnName)) {
				duplicate = true;
				columnIndex = GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length + j;
			}
		}	
		return duplicate;
	}
	
	public void addColumn(){
		String columnName = textField.getText();
		//LocalConfig.getInstance().getReactionsMetaColumnNames().add(columnName);
		DefaultTableModel model = (DefaultTableModel) GraphicalInterface.reactionsTable.getModel();
		model.addColumn(columnName);
		LocalConfig.getInstance().getReactionsTableModelMap().put(LocalConfig.getInstance().getModelName(), model);
	}

	public static void main(String[] args) throws Exception {
		//based on code from http://stackoverflow.com/questions/6403821/how-to-add-an-image-to-a-jframe-title-bar
		final ArrayList<Image> icons = new ArrayList<Image>(); 
		icons.add(new ImageIcon("images/most16.jpg").getImage()); 
		icons.add(new ImageIcon("images/most32.jpg").getImage());

		ReactionColAddRenameInterface frame = new ReactionColAddRenameInterface();

		frame.setIconImages(icons);
		frame.setSize(350, 170);
		frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}





