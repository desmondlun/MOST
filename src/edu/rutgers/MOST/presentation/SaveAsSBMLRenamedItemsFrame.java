package edu.rutgers.MOST.presentation;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.CellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
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
	
	public JLabel label = new JLabel();
	
	private static final long serialVersionUID = 1L;
	public JXTable reactionsRenamedTable = new JXTable();
	public JXTable metabolitesRenamedTable = new JXTable();
	
	public JTabbedPane tabbedPane = new JTabbedPane(3);
	
	public DefaultTableModel reactionsRenamedModel = new DefaultTableModel();
	public DefaultTableModel metabolitesRenamedModel = new DefaultTableModel();
	
	public JButton okButton = new JButton("OK");
	
	private String message;
	
	public String getMessage()
	{
		return message;
	}

	public void setMessage( String message )
	{
		this.message = message;
	}

	public SaveAsSBMLRenamedItemsFrame() {
		setTitle(GraphicalInterfaceConstants.TITLE + " - " + SBMLConstants.RENAMED_ABBREVIATIONS_WARNING_TITLE);

		getRootPane().setDefaultButton(okButton);
		
		/**************************************************************************/
		//MenuBar
		/**************************************************************************/
		JMenuBar menuBar = new JMenuBar();

		setJMenuBar(menuBar);
		
		// from http://www.java2s.com/Tutorial/Java/0240__Swing/thelastcolumnismovedtothefirstposition.htm
		// columns cannot be rearranged by dragging
		reactionsRenamedTable.getTableHeader().setReorderingAllowed(false); 
		metabolitesRenamedTable.getTableHeader().setReorderingAllowed(false); 
		
		reactionsRenamedTable.setColumnSelectionAllowed(false);
		reactionsRenamedTable.setRowSelectionAllowed(false); 
		reactionsRenamedTable.setCellSelectionEnabled(true);
		
		ReactionsPopupListener reactionsPopupListener = new ReactionsPopupListener();
		reactionsRenamedTable.addMouseListener(reactionsPopupListener);
		
		metabolitesRenamedTable.setColumnSelectionAllowed(false);
		metabolitesRenamedTable.setRowSelectionAllowed(false); 
		metabolitesRenamedTable.setCellSelectionEnabled(true);
		
		MetabolitesPopupListener metabolitesPopupListener = new MetabolitesPopupListener();
		metabolitesRenamedTable.addMouseListener(metabolitesPopupListener);
		
		/************************************************************************/
		//set frame layout 
		/************************************************************************/

		//set tabs south (bottom) = 3
		//JTabbedPane tabbedPane = new JTabbedPane(3); 
		JScrollPane scrollPaneReac = new JScrollPane(reactionsRenamedTable);
		tabbedPane.addTab("Reactions", scrollPaneReac);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_R);

		JScrollPane scrollPaneMetab = new JScrollPane(metabolitesRenamedTable);
		tabbedPane.addTab("Metabolites", scrollPaneMetab);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_B);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(okButton);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0,0,20,0));

		double border = 10;
		double size[][] =
		{{border, TableLayout.FILL, border},  // Columns
			{border, 0.10, 20, TableLayout.FILL, 10, 0.15, border}}; // Rows  

		setLayout (new TableLayout(size)); 

		add (label, "1, 1, 1, 1"); // Top
		add (tabbedPane, "1, 3, 1, 1"); // Right
		add (buttonPanel, "1, 5, 1, 1");

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
		
		ActionListener okButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent prodActionEvent) {
				setVisible(false);
				dispose();
			}
		};
		
		okButton.addActionListener(okButtonActionListener);
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
	
	// from http://docs.oracle.com/javase/tutorial/uiswing/components/menu.html
	public class ReactionsPopupListener extends MouseAdapter {

		public void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger() && reactionsRenamedTable.isEnabled()) {
				Point p = new Point(e.getX(), e.getY());
				int col = reactionsRenamedTable.columnAtPoint(p);
				int row = reactionsRenamedTable.rowAtPoint(p);
				// translate table index to model index
				//int mcol = reactionsTable.getColumn(reactionsTable.getColumnName(col)).getModelIndex();

				if (row >= 0 && row < reactionsRenamedTable.getRowCount()) {
					cancelCellEditing();            
					JPopupMenu contextMenu = createContextMenu(reactionsRenamedTable, row, col);
					if (contextMenu != null
						&& contextMenu.getComponentCount() > 0) {
						contextMenu.show(reactionsRenamedTable, p.x, p.y);
					}
				}
			}
		}

		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}
	}
	
	public class MetabolitesPopupListener extends MouseAdapter {

		public void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger() && metabolitesRenamedTable.isEnabled()) {
				Point p = new Point(e.getX(), e.getY());
				int col = metabolitesRenamedTable.columnAtPoint(p);
				int row = metabolitesRenamedTable.rowAtPoint(p);
				// translate table index to model index
				//int mcol = reactionsTable.getColumn(reactionsTable.getColumnName(col)).getModelIndex();

				if (row >= 0 && row < metabolitesRenamedTable.getRowCount()) {
					cancelCellEditing();            
					JPopupMenu contextMenu = createContextMenu(metabolitesRenamedTable, row, col);
					if (contextMenu != null
						&& contextMenu.getComponentCount() > 0) {
						contextMenu.show(metabolitesRenamedTable, p.x, p.y);
					}
				}
			}
		}

		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}
	}

	private void cancelCellEditing() {
		if (tabbedPane.getSelectedIndex() == 0) {
			CellEditor ce = reactionsRenamedTable.getCellEditor();
			if (ce != null) {
				ce.cancelCellEditing();
			}
		} else if (tabbedPane.getSelectedIndex() == 1) {
			CellEditor ce = metabolitesRenamedTable.getCellEditor();
			if (ce != null) {
				ce.cancelCellEditing();
			}
		}
	}

	private JPopupMenu createContextMenu(final JXTable table, final int rowIndex,
		final int columnIndex) {
		JPopupMenu contextMenu = new JPopupMenu();

		JMenuItem copyMenu = new JMenuItem("Copy");
		copyMenu.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		copyMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tableCopy(table);
			}
		});
		contextMenu.add(copyMenu);

		return contextMenu;
	}

	public void tableCopy(JXTable table) {
		int numCols=table.getSelectedColumnCount(); 
		int numRows=table.getSelectedRowCount(); 
		int[] rowsSelected=table.getSelectedRows(); 
		int[] colsSelected=table.getSelectedColumns(); 
		try {
			if (numRows!=rowsSelected[rowsSelected.length-1]-rowsSelected[0]+1 || numRows!=rowsSelected.length || 
				numCols!=colsSelected[colsSelected.length-1]-colsSelected[0]+1 || numCols!=colsSelected.length) {

				JOptionPane.showMessageDialog(null, "Invalid Copy Selection", "Invalid Copy Selection", JOptionPane.ERROR_MESSAGE);
				return; 
			} 
		} catch (Throwable t) {

		}		
		StringBuffer excelStr=new StringBuffer(); 
		for (int i=0; i<numRows; i++) { 
			for (int j=0; j<numCols; j++) { 
				try {
					excelStr.append(escape(table.getValueAt(rowsSelected[i], colsSelected[j]))); 
				} catch (Throwable t) {

				}						
				if (j<numCols-1) {
					//System.out.println("t");
					excelStr.append("\t"); 
				} 
			} 
			//System.out.println("n");
			excelStr.append("\n"); 
		} 

		StringSelection sel  = new StringSelection(excelStr.toString()); 
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, sel);
	}

	private String escape(Object cell) { 
		return cell.toString().replace("\n", " ").replace("\t", " "); 
	} 
	
	public static void main(String[] args) {
    	final ArrayList<Image> icons = new ArrayList<Image>(); 
		icons.add(new ImageIcon("etc/most16.jpg").getImage()); 
		icons.add(new ImageIcon("etc/most32.jpg").getImage());
    	
		SaveAsSBMLRenamedItemsFrame frame = new SaveAsSBMLRenamedItemsFrame();
    	
		frame.setIconImages(icons);
		frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		frame.setSize(500, 340);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
    }
}
