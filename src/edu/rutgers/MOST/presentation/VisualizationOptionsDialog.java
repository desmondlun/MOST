package edu.rutgers.MOST.presentation;

import java.awt.Component;
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

import edu.rutgers.MOST.config.LocalConfig;

public class VisualizationOptionsDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JCheckBox graphMissingMetabolitesCheckBox = new JCheckBox(VisualizationOptionsConstants.GRAPH_ALL_REACTIONS_METABOLITES_IN_DATABASE_NAME);
	
	private JCheckBox scaleEdgeThicknessCheckBox = new JCheckBox(VisualizationOptionsConstants.SCALE_EDGE_THICKNESS_USING_FLUX_VALUES_NAME);
	
	// Proton cannot be ignored unless reactions and metabolites positions databases are updated
	// to include protons. Protons currently omitted in most reactions.
//	private JCheckBox ignoreProtonCheckBox = new JCheckBox(VisualizationOptionsConstants.IGNORE_PROTON_FOR_REACTION_MATCHING_NAME);
	private JCheckBox ignoreWaterCheckBox = new JCheckBox(VisualizationOptionsConstants.IGNORE_WATER_FOR_REACTION_MATCHING_NAME);
	
	private JCheckBox showVisualizationRerortCheckBox = new JCheckBox(VisualizationOptionsConstants.SHOW_VISUALIZATION_REPORT);
	
	public JButton okButton = new JButton("    OK    ");
	public JButton cancelButton = new JButton("  Cancel  ");

	public VisualizationOptionsDialog() {
		
		getRootPane().setDefaultButton(okButton);
		
		setTitle(VisualizationOptionsConstants.VISUALIZATION_OPTIONS_DIALOG_TITLE);
		
		// set check box defaults
		graphMissingMetabolitesCheckBox.setSelected(LocalConfig.getInstance().isGraphMissingMetabolitesSelected());
		scaleEdgeThicknessCheckBox.setSelected(LocalConfig.getInstance().isScaleEdgeThicknessSelected());
//		ignoreProtonCheckBox.setSelected(LocalConfig.getInstance().isIgnoreProtonSelected());
		ignoreWaterCheckBox.setSelected(LocalConfig.getInstance().isIgnoreWaterSelected());
		showVisualizationRerortCheckBox.setSelected(LocalConfig.getInstance().isShowVisualizationReportSelected());
		
		graphMissingMetabolitesCheckBox.setMnemonic(KeyEvent.VK_G);
		scaleEdgeThicknessCheckBox.setMnemonic(KeyEvent.VK_S);
//		ignoreProtonCheckBox.setMnemonic(KeyEvent.VK_I);
		ignoreWaterCheckBox.setMnemonic(KeyEvent.VK_W);
		showVisualizationRerortCheckBox.setMnemonic(KeyEvent.VK_R);
		
		//box layout
		Box vb = Box.createVerticalBox();

		Box hbGraphMissingMetabolites = Box.createHorizontalBox();
		Box hbScaleEdgeThickness = Box.createHorizontalBox();
//		Box hbIgnoreProton = Box.createHorizontalBox();
		Box hbIgnoreWater = Box.createHorizontalBox();
		Box hbShowReport = Box.createHorizontalBox();
		Box hbButton = Box.createHorizontalBox();
		
		JPanel hbGraphMissingMetabolitesPanel = new JPanel();
		hbGraphMissingMetabolitesPanel.setLayout(new BoxLayout(hbGraphMissingMetabolitesPanel, BoxLayout.X_AXIS));
		hbGraphMissingMetabolitesPanel.add(graphMissingMetabolitesCheckBox);
		hbGraphMissingMetabolitesPanel.setBorder(BorderFactory.createEmptyBorder(
				VisualizationOptionsConstants.TOP_BORDER, 
				VisualizationOptionsConstants.LEFT_BORDER, 
				VisualizationOptionsConstants.SUB_TOPIC_GAP, 
				VisualizationOptionsConstants.RIGHT_BORDER));

		hbGraphMissingMetabolites.add(leftJustify(hbGraphMissingMetabolitesPanel));
		
		JPanel hbScaleEdgeThicknessPanel = new JPanel();
		hbScaleEdgeThicknessPanel.setLayout(new BoxLayout(hbScaleEdgeThicknessPanel, BoxLayout.X_AXIS));
		hbScaleEdgeThicknessPanel.add(scaleEdgeThicknessCheckBox);
		hbScaleEdgeThicknessPanel.setBorder(BorderFactory.createEmptyBorder(0, 
				VisualizationOptionsConstants.LEFT_BORDER, 
				VisualizationOptionsConstants.MAIN_TOPIC_GAP, 
				VisualizationOptionsConstants.RIGHT_BORDER));

		hbScaleEdgeThickness.add(leftJustify(hbScaleEdgeThicknessPanel));
		
//		JPanel hbIgnoreProtonPanel = new JPanel();
//		hbIgnoreProtonPanel.setLayout(new BoxLayout(hbIgnoreProtonPanel, BoxLayout.X_AXIS));
//		hbIgnoreProtonPanel.add(ignoreProtonCheckBox);
//		hbIgnoreProtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 
//				VisualizationOptionsConstants.LEFT_BORDER, 
//				VisualizationOptionsConstants.MAIN_TOPIC_GAP, 
//				VisualizationOptionsConstants.RIGHT_BORDER));
//
//		hbIgnoreProton.add(leftJustify(hbIgnoreProtonPanel));
		
		JPanel hbIgnoreWaterPanel = new JPanel();
		hbIgnoreWaterPanel.setLayout(new BoxLayout(hbIgnoreWaterPanel, BoxLayout.X_AXIS));
		hbIgnoreWaterPanel.add(ignoreWaterCheckBox);
		hbIgnoreWaterPanel.setBorder(BorderFactory.createEmptyBorder(0, 
				VisualizationOptionsConstants.LEFT_BORDER, 
				VisualizationOptionsConstants.MAIN_TOPIC_GAP, 
				VisualizationOptionsConstants.RIGHT_BORDER));

		hbIgnoreWater.add(leftJustify(hbIgnoreWaterPanel));
		
		JPanel hbShowReportPanel = new JPanel();
		hbShowReportPanel.setLayout(new BoxLayout(hbShowReportPanel, BoxLayout.X_AXIS));
		hbShowReportPanel.add(showVisualizationRerortCheckBox);
		hbShowReportPanel.setBorder(BorderFactory.createEmptyBorder(0, 
				VisualizationOptionsConstants.LEFT_BORDER, 
				VisualizationOptionsConstants.MAIN_TOPIC_GAP, 
				VisualizationOptionsConstants.RIGHT_BORDER));

		hbShowReport.add(leftJustify(hbShowReportPanel));
		
		okButton.setMnemonic(KeyEvent.VK_O);
		JLabel blank = new JLabel("    "); 
		cancelButton.setMnemonic(KeyEvent.VK_C);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(okButton);
		buttonPanel.add(blank);
		buttonPanel.add(cancelButton);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,20,VisualizationOptionsConstants.BOTTOM_BORDER,20));

		hbButton.add(buttonPanel);

		vb.add(hbGraphMissingMetabolites);
		vb.add(hbScaleEdgeThickness);
//		vb.add(hbIgnoreProton);
		vb.add(hbIgnoreWater);
		vb.add(hbShowReport);
		vb.add(hbButton);
		add(vb);
    	
    	ActionListener okButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				LocalConfig.getInstance().setGraphMissingMetabolitesSelected(graphMissingMetabolitesCheckBox.isSelected());
				LocalConfig.getInstance().setScaleEdgeThicknessSelected(scaleEdgeThicknessCheckBox.isSelected());
//				LocalConfig.getInstance().setIgnoreProtonSelected(ignoreProtonCheckBox.isSelected());
				LocalConfig.getInstance().setIgnoreWaterSelected(ignoreWaterCheckBox.isSelected());
				LocalConfig.getInstance().setShowVisualizationReportSelected(showVisualizationRerortCheckBox.isSelected());
				setVisible(false);
				dispose();
//				System.out.println(LocalConfig.getInstance().isGraphMissingReactionsSelected());
//				System.out.println(LocalConfig.getInstance().isHighlightMissingReactionsSelected());
//				System.out.println(LocalConfig.getInstance().isScaleEdgeThicknessSelected());
//				System.out.println(LocalConfig.getInstance().isIgnoreWaterSelected());
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
	
	/**
	 * Left Justifies component in a panel
	 * @param panel
	 * @return
	 */
	// from http://stackoverflow.com/questions/9212155/java-boxlayout-panels-alignment
	private Component leftJustify(JPanel panel)  {
	    Box  b = Box.createHorizontalBox();
	    b.add( panel );
	    b.add( Box.createHorizontalGlue() );
	    // (Note that you could throw a lot more components
	    // and struts and glue in here.)
	    return b;
	}
	
	public static void main(String[] args) throws Exception {
		//based on code from http:stackoverflow.com/questions/6403821/how-to-add-an-image-to-a-jframe-title-bar
		final ArrayList<Image> icons = new ArrayList<Image>(); 
		icons.add(new ImageIcon("etc/most16.jpg").getImage()); 
		icons.add(new ImageIcon("etc/most32.jpg").getImage());
		
		VisualizationOptionsDialog d = new VisualizationOptionsDialog();
		
		d.setIconImages(icons);
    	d.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    	//d.setSize(400, 300);
    	d.pack();
    	d.setLocationRelativeTo(null);
    	d.setVisible(true);

	}
	
}








