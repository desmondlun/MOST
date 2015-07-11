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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import layout.TableLayout;

import org.jdesktop.swingx.JXTable;

import edu.rutgers.MOST.data.SBMLConstants;

public class SaveAsSBMLRenamedItemsFrame extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public JXTable reactionsRenamedTable = new JXTable();
	public JXTable metabolitesRenamedTable = new JXTable();
	
	public DefaultTableModel reactionsRenamedModel = new DefaultTableModel();
	public DefaultTableModel metabolitesRenamedModel = new DefaultTableModel();
	
	public SaveAsSBMLRenamedItemsFrame() {
		setTitle("MOST");

		/**************************************************************************/
		//MenuBar
		/**************************************************************************/
		JMenuBar menuBar = new JMenuBar();

		setJMenuBar(menuBar);
		
		// from http://www.java2s.com/Tutorial/Java/0240__Swing/thelastcolumnismovedtothefirstposition.htm
		// columns cannot be rearranged by dragging
		reactionsRenamedTable.getTableHeader().setReorderingAllowed(false); 
		metabolitesRenamedTable.getTableHeader().setReorderingAllowed(false); 
		
		/************************************************************************/
		//set frame layout 
		/************************************************************************/

		JLabel label = new JLabel("<html>The following Reactions and Metabolites have been renamed"
			+ " to fulfill the SBML Standards.</html>");

		//set tabs south (bottom) = 3
		JTabbedPane tabbedPane = new JTabbedPane(3); 
		JScrollPane scrollPaneReac = new JScrollPane(reactionsRenamedTable);
		tabbedPane.addTab("Reactions", scrollPaneReac);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_R);

		JScrollPane scrollPaneMetab = new JScrollPane(metabolitesRenamedTable);
		tabbedPane.addTab("Metabolites", scrollPaneMetab);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_B);

		double border = 10;
		double size[][] =
		{{border, TableLayout.FILL, border},  // Columns
			{border, 0.10, 20, TableLayout.FILL, border}}; // Rows  

		setLayout (new TableLayout(size)); 

		add (label, "1, 1, 1, 1"); // Top
		add (tabbedPane, "1, 3, 1, 1"); // Right

		setBackground(Color.lightGray);
			
		// set empty table models in case only metabolites or reactions renamed to
		// prevent loading a table with a null model. model set in GraphicalInterface
		// if any renaming occurs.
		reactionsRenamedModel = createTableModel(SBMLConstants.REACTIONS_RENAMED_COLUMN_NAMES);
		metabolitesRenamedModel = createTableModel(SBMLConstants.METABOLITES_RENAMED_COLUMN_NAMES);

		reactionsRenamedTable.setModel(reactionsRenamedModel);
		metabolitesRenamedTable.setModel(metabolitesRenamedModel);
		
		setTableLayout(reactionsRenamedTable);
		setTableLayout(metabolitesRenamedTable);
	}

	public static DefaultTableModel createTableModel(String[] columnNamesArray) {
		DefaultTableModel model = new DefaultTableModel();
		Vector<String> columnNames = new Vector<String>();
		for (int c = 0; c < columnNamesArray.length; c++) {
			columnNames.add(columnNamesArray[c]);
		}
		
		for (int i = 0; i < columnNames.size(); i++) {
			model.addColumn(columnNames.get(i));
		}		
		
		return model; 
		
	}
	
	public void setTableLayout(JXTable table) {
		GenericTableCellRenderer renderer = new GenericTableCellRenderer();
		int r = table.getModel().getColumnCount();
		for (int i = 0; i < r; i++) {
			TableColumn column = table.getColumnModel().getColumn(i);
			column.setCellRenderer(renderer);
			if (i==0) {
				column.setPreferredWidth(50);
				column.setMaxWidth(50);
			} else {
				column.setPreferredWidth(GraphicalInterfaceConstants.REACTION_ABBREVIATION_WIDTH);
			}
		}
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
