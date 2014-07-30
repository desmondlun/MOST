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
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class FBADialog  extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public JCheckBox normBox = new JCheckBox("Minimize Euclidean Norm ");
	public JCheckBox fvaBox = new JCheckBox("Include FVA Results           ");
	public JButton okButton = new JButton("    OK    ");
	public JButton cancelButton = new JButton("  Cancel  ");
	public JLabel topLabel = new JLabel();
	
	public FBADialog() {

		setTitle("FBA Dialog");		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		getRootPane().setDefaultButton(okButton);
		
		//box layout
		Box vb = Box.createVerticalBox();

		Box hbLabel = Box.createHorizontalBox();
		Box hbButton = Box.createHorizontalBox();

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

		okButton.setMnemonic(KeyEvent.VK_O);
		JLabel blank = new JLabel("    "); 
		cancelButton.setMnemonic(KeyEvent.VK_C);
		
		JPanel normPanel = new JPanel();
		normPanel.setLayout(new BoxLayout(normPanel, BoxLayout.X_AXIS));
		normPanel.add(normBox);
		normPanel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		
		JPanel fvaPanel = new JPanel();
		fvaPanel.setLayout(new BoxLayout(fvaPanel, BoxLayout.X_AXIS));
		fvaPanel.add(fvaBox);
		fvaPanel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(okButton);
		buttonPanel.add(blank);
		buttonPanel.add(cancelButton);
		
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(5,20,15,20));

		hbButton.add(buttonPanel);

		vb.add(hbLabel);
		vb.add(normPanel);
		vb.add(fvaPanel);
		vb.add(hbButton);
		add(vb);	
		
		normBox.setSelected(FBAConstants.EUCLIDEAN_NORM_DEFAULT);
		fvaBox.setSelected(FBAConstants.FVA_DEFAULT);
		
//		ActionListener okButtonActionListener = new ActionListener() {
//			public void actionPerformed(ActionEvent prodActionEvent) {
//				if (normBox.isSelected()) {
//					minEuclideanNorm = true;
//				} else {
//					minEuclideanNorm = false;
//				}
//				if (fvaBox.isSelected()) {
//					runFVA = true;
//				} else {
//					runFVA = false;
//				}
//			}
//		};
		
		ActionListener cancelButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				setVisible(false);
				dispose();
			}
		}; 
		
//		okButton.addActionListener(okButtonActionListener);
		cancelButton.addActionListener(cancelButtonActionListener);
	} 	
	
	public static void main(String[] args) throws Exception {
		
		//based on code from http://stackoverflow.com/questions/6403821/how-to-add-an-image-to-a-jframe-title-bar
		final ArrayList<Image> icons = new ArrayList<Image>(); 
		icons.add(new ImageIcon("etc/most16.jpg").getImage()); 
		icons.add(new ImageIcon("etc/most32.jpg").getImage());

		FBADialog frame = new FBADialog();
		frame.setIconImages(icons);
		frame.setSize(300, 160);
		frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}










