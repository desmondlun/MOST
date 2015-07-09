package edu.rutgers.MOST.presentation;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

import layout.TableLayout;

import org.jdesktop.swingx.JXTable;

public class SaveAsSBMLRenamedItemsFrame extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static JXTable reactionsTable = new JXTable();
	public static JXTable metabolitesTable = new JXTable();
	
	private DefaultTableModel reactionsRenamedModel = new DefaultTableModel();
	private DefaultTableModel metabolitesRenamedModel = new DefaultTableModel();
	
	public SaveAsSBMLRenamedItemsFrame() {
		setTitle("MOST");

		/**************************************************************************/
		//MenuBar
		/**************************************************************************/
		JMenuBar menuBar = new JMenuBar();

		setJMenuBar(menuBar);
		
		int id = 0;
		for (int i = 0; i < 100; i++) {
			Vector<String> row = new Vector<String>();
			row.addElement(Integer.toString(id));
			row.addElement("");
			row.addElement("");
			reactionsRenamedModel.addRow(row);
			id += 1;
		}
		reactionsTable.setModel(reactionsRenamedModel);
		
		/************************************************************************/
		//set frame layout 
		/************************************************************************/
		//set tabs south (bottom) = 3
//		JTabbedPane tabbedPane = new JTabbedPane(3); 
//		JScrollPane scrollPaneReac = new JScrollPane(reactionsTable);
//		tabbedPane.addTab("Reactions", scrollPaneReac);
//		tabbedPane.setMnemonicAt(0, KeyEvent.VK_R);
//
//		JScrollPane scrollPaneMetab = new JScrollPane(metabolitesTable);
//		tabbedPane.addTab("Metabolites", scrollPaneMetab);
//		tabbedPane.setMnemonicAt(1, KeyEvent.VK_B);
		
		JLabel label = new JLabel("<html>The following Reactions and Metabolites have been renamed"
			+ " to fulfill the SBML Standards.</html>");

		//set tabs south (bottom) = 3
		JTabbedPane tabbedPane = new JTabbedPane(3); 
		JScrollPane scrollPaneReac = new JScrollPane(reactionsTable);
		tabbedPane.addTab("Reactions", scrollPaneReac);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_R);

		JScrollPane scrollPaneMetab = new JScrollPane(metabolitesTable);
		tabbedPane.addTab("Metabolites", scrollPaneMetab);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_B);

		double border = 10;
		double size[][] =
		{{border, TableLayout.FILL, border},  // Columns
			{border, 0.20, 20, TableLayout.FILL, border}}; // Rows  

		setLayout (new TableLayout(size)); 

		add (label, "1, 1, 1, 1"); // Top
		add (tabbedPane, "1, 3, 1, 1"); // Right

		setBackground(Color.lightGray);

	}
	
	public static void main(String[] args) {
    	final ArrayList<Image> icons = new ArrayList<Image>(); 
		icons.add(new ImageIcon("etc/most16.jpg").getImage()); 
		icons.add(new ImageIcon("etc/most32.jpg").getImage());
    	
		SaveAsSBMLRenamedItemsFrame frame = new SaveAsSBMLRenamedItemsFrame();
    	
		frame.setIconImages(icons);
		frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		frame.setSize(500, 300);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
    }
}
