package edu.rutgers.MOST.presentation;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.hyperlink.AbstractHyperlinkAction;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.HyperlinkProvider;

import au.com.bytecode.opencsv.CSVReader;

// loosely based on http://www.cs.cf.ac.uk/Dave/HCI/HCI_Handout_CALLER/node167.html
// based on http://www.coderanch.com/t/345311/GUI/java/Adding-rows-Jtable
class ModelCollectionTable
		extends 	JFrame
 {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Instance attributes used in this example
	private	JPanel		topPanel;
	private	JScrollPane scrollPane;
	private JXTable table = new JXTable(){  
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public boolean isCellEditable(int row, int column){
			return false;
			
		}	
	};
	private DefaultTableModel model = new DefaultTableModel();
	private	JPanel		bottomLeftPanel;
	private	JPanel		bottomRightPanel;
	private	JPanel		bottomPanel;
	public static JButton okButton = new JButton("  OK  ");
	public static JButton cancelButton = new JButton("Cancel");
	
	private String fileName;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	private String path;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	private String fileType;
	
	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	private Vector<String> columnNames;
	
	public static ArrayList<Integer> getVisibleColumns() {
		return visibleColumns;
	}

	public static void setVisibleColumns(ArrayList<Integer> visibleColumns) {
		ModelCollectionTable.visibleColumns = visibleColumns;
	}

	public Vector<String> getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(Vector<String> columnNames) {
		this.columnNames = columnNames;
	}

	private static ArrayList<Integer> visibleColumns;
	
	// Constructor of main frame
	public ModelCollectionTable(File file)
	{
		// Set the frame characteristics
		setTitle(ModelCollectionConstants.TITLE);
		//setSize( 700, 500 );
		setSize(ModelCollectionConstants.WIDTH, ModelCollectionConstants.HEIGHT);
		
		setBackground( Color.gray );
		
		okButton.setMnemonic(KeyEvent.VK_O);
		okButton.setEnabled(false);
		cancelButton.setMnemonic(KeyEvent.VK_C);
		
		//getRootPane().setDefaultButton(okButton);
		
		table.setRowHeight(20);
		table.setColumnSelectionAllowed(false);
		// If desired to have single cell selection, change setRowSelectionAllowed
		// to false, and uncomment the line table.setCellSelectionEnabled(true);
		// Since selection determines which file to load, it may be confusing
		// to only have a single cell selected
		table.setRowSelectionAllowed(true); 
		//table.setCellSelectionEnabled(true);
		table.getSelectionModel().addListSelectionListener(new RowListener());
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
		// The code below allows multiple cells to be selectable, but also allows for
		// multiple row selection. Since the primary purpose of this table is to load files,
		// only 1 row can be selectable at a time. Providing copy is only a convenience
		// and should not take precedence over functionality. As a result of this,
		// only 1 cell can be copied at a time
		//table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		
		PopupListener popupListener = new PopupListener();
		table.addMouseListener(popupListener);

		// from http://www.java.net/node/651087
		// need tab to skip hidden columns		
		table.getInputMap().put(KeyStroke.getKeyStroke("TAB"), "actionString");
		table.getActionMap().put("actionString", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -5869679654069664250L;

			public void actionPerformed(ActionEvent ae) {
				// This overrides tab key and performs an action
				tabToNextVisibleCell(table, getVisibleColumns());
			}
		});
		
		ActionListener copyActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				if (table.getSelectedRow() > -1 && table.getSelectedColumn() > -1) {
					tableCopy();
				}			
			}
		};
		
		KeyStroke copyKey = KeyStroke.getKeyStroke(KeyEvent.VK_C,ActionEvent.CTRL_MASK,false);
		
		table.registerKeyboardAction(copyActionListener, copyKey, JComponent.WHEN_FOCUSED); 

		// Create a panel to hold all other components
		topPanel = new JPanel();
		topPanel.setLayout( new BorderLayout() );
		getContentPane().add( topPanel );
		
		int count = 0;
		Vector<String> columnNames = new Vector<String>();
		
		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(file), ',');
			String [] dataArray;
			try {
				while ((dataArray = reader.readNext()) != null) {
					if (count == 0) {
						for (int s = 0; s < dataArray.length; s++) {						
							columnNames.add(dataArray[s]);
						}
						for (int i = 0; i < columnNames.size(); i++) {
							model.addColumn(columnNames.get(i));
						}		
						setColumnNames(columnNames);
						
						table.setModel(model);
					} else {
						Vector <String> row = new Vector<String>();
						for (int s = 0; s < dataArray.length; s++) {						
							row.add(dataArray[s]);
						}
						model.addRow(row);
					}
					count += 1;
				}
				reader.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,                
						"File Not Found Error.",                
						"Error",                                
						JOptionPane.ERROR_MESSAGE);
				//e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null,                
					"File Not Found Error.",                
					"Error",                                
					JOptionPane.ERROR_MESSAGE);
			//e.printStackTrace();
		}	
		
		ArrayList<Integer> visibleColumns = visibleColumnList();
		setVisibleColumns(visibleColumns);

		// Add the table to a scrolling pane
		scrollPane = new JScrollPane( table );
		topPanel.add( scrollPane, BorderLayout.CENTER );
		bottomLeftPanel = new JPanel();
		bottomLeftPanel.setLayout( new BorderLayout() );
		bottomRightPanel = new JPanel();
		bottomPanel = new JPanel();
		bottomRightPanel.setLayout( new BorderLayout() );
		bottomLeftPanel.add( okButton, BorderLayout.WEST );
		bottomRightPanel.add( cancelButton, BorderLayout.EAST );
		bottomPanel.add(bottomLeftPanel, BorderLayout.WEST);	
		bottomPanel.add(bottomRightPanel, BorderLayout.EAST);
		topPanel.add( bottomPanel, BorderLayout.SOUTH );
		
		//from http://www.java2s.com/Tutorial/Java/0240__Swing/thelastcolumnismovedtothefirstposition.htm
		// columns cannot be rearranged by dragging
		table.getTableHeader().setReorderingAllowed(false); 
		
		int r = table.getModel().getColumnCount();	
		for (int i = 0; i < r; i++) {
			//set background of id column to grey
			ModelCollectionCellRenderer renderer = new ModelCollectionCellRenderer();
			TableCellRenderer hyperlinkRenderer = new DefaultTableRenderer(new HyperlinkProvider(simpleAction));
			TableColumn column = table.getColumnModel().getColumn(i);
			if (i == ModelCollectionConstants.REFERENCE_COLUMN) {
				column.setCellRenderer(hyperlinkRenderer);
			} else {
				column.setCellRenderer(renderer);
			}            
            // Column widths can be changed here           
            if (i == ModelCollectionConstants.MODEL_VERSION_COLUMN) {
            	column.setPreferredWidth(ModelCollectionConstants.MODEL_VERSION_WIDTH);
            }
            if (i == ModelCollectionConstants.ORGANISM_NAME_COLUMN) {
            	column.setPreferredWidth(ModelCollectionConstants.ORGANISM_NAME_WIDTH);
            }
            if (i == ModelCollectionConstants.REFERENCE_COLUMN) {
            	column.setPreferredWidth(ModelCollectionConstants.REFERENCE_WIDTH);
            }
            if (i == ModelCollectionConstants.GENES_COLUMN ||
            		i == ModelCollectionConstants.REACTIONS_COLUMN || 
            		i == ModelCollectionConstants.METABOLITES_COLUMN ||
            		i == ModelCollectionConstants.IDENTIFIERS_COLUMN) {
            	column.setPreferredWidth(ModelCollectionConstants.DEFAULT_WIDTH);
            	renderer.setHorizontalAlignment(JLabel.RIGHT);
            }
            if (i == ModelCollectionConstants.IDENTIFIERS_COLUMN) {
            	column.setPreferredWidth(ModelCollectionConstants.DEFAULT_WIDTH);
            	renderer.setHorizontalAlignment(JLabel.LEFT);
            }
            if (i >= ModelCollectionConstants.VISIBLE_COLUMN_NAMES.length) {
            	//sets column not visible
    			column.setMaxWidth(0);
    			column.setMinWidth(0); 
    			column.setWidth(0); 
    			column.setPreferredWidth(0);
            }            
		}	
		
		ActionListener okButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				setVisible(false);
				//dispose();				
			}
		};

		okButton.addActionListener(okButtonActionListener);
		
		ActionListener cancelButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				setVisible(false);
				//dispose();				
			}
		};

		cancelButton.addActionListener(cancelButtonActionListener);
		
	}
	
	private class RowListener implements ListSelectionListener {
    	public void valueChanged(ListSelectionEvent event) {
    		if (table.getSelectedRow() > -1) {
    			// location of folder in the user's computer
				String userPath = "etc";
				// model collection folder location
				String path = (table.getModel().getValueAt(table.getSelectedRow(), ModelCollectionConstants.PATH_COLUMN)).toString();
				String filename = (table.getModel().getValueAt(table.getSelectedRow(), ModelCollectionConstants.MODEL_VERSION_COLUMN)).toString();
				String ending = "";
				String type = (table.getModel().getValueAt(table.getSelectedRow(), ModelCollectionConstants.TYPE_COLUMN)).toString();
				if (type.equals("sbml")) {
					setFileType(GraphicalInterfaceConstants.SBML_FILE_TYPE);
					ending = ".xml";
				} else if (type.equals("csv")) {
					setFileType(GraphicalInterfaceConstants.CSV_FILE_TYPE);
					ending = "._mtb.csv";
				}
				setFileName(filename);
				setPath(userPath + "/" + path + "/" + filename + ending);
    			okButton.setEnabled(true);
			}
    	}
    }
	
	// from http://docs.oracle.com/javase/tutorial/uiswing/components/menu.html
	public class PopupListener extends MouseAdapter {

		public void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger() && table.isEnabled()) {
				Point p = new Point(e.getX(), e.getY());
				int col = table.columnAtPoint(p);
				int row = table.rowAtPoint(p);
				// translate table index to model index
				//int mcol = reactionsTable.getColumn(reactionsTable.getColumnName(col)).getModelIndex();

				if (row >= 0 && row < table.getRowCount()) {
					cancelCellEditing();            
					JPopupMenu contextMenu = createContextMenu(row, col);
					if (contextMenu != null
							&& contextMenu.getComponentCount() > 0) {
						contextMenu.show(table, p.x, p.y);
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
		CellEditor ce = table.getCellEditor();
		if (ce != null) {
			ce.cancelCellEditing();
		}
	}
	
	private JPopupMenu createContextMenu(final int rowIndex,
			final int columnIndex) {
		JPopupMenu contextMenu = new JPopupMenu();

		JMenuItem copyMenu = new JMenuItem("Copy");
		copyMenu.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		copyMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tableCopy();
			}
		});
		contextMenu.add(copyMenu);

		return contextMenu;
	}

	public void tableCopy() {
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
	
	/***********************************************************************************/
	//end clipboard
	/***********************************************************************************/
	
	/***********************************************************************************/
	//tab override methods
	/***********************************************************************************/
	
	// these methods prevent tab from visiting hidden columns
	
	public void tabToNextVisibleCell(JTable table, ArrayList<Integer> visibleColumns) {
		// This overrides tab key and performs an action	
		// from http://www.coderanch.com/t/344392/GUI/java/Tabbing-cells-JTable
		int row = table.getSelectedRow();  
        int col = table.getSelectedColumn();  
        // Make sure we start with legal values.  
        while(col < 0) col++;  
        while(row < 0) row++;  
        // Find the next editable cell.  
        col++;
        while(!(visibleColumns.contains(col)))  
        {  
            col++;  
            if(col > table.getColumnCount()-1)  
            {  
                col = 0;  
                row = (row == table.getRowCount()-1) ? 0 : row+1;
            }  
        }
        TableCellEditor editor = table.getCellEditor();
        if (editor != null) {
          editor.stopCellEditing();
        }
        scrollToLocation(table, row, col);        
	}
	
	public void setTableCellFocused(int row, int col, JTable table) {
		table.changeSelection(row, col, false, false);
		table.requestFocus();
	}
	
	public void scrollToLocation(JTable table, int row, int column) {
    	try {
    		table.scrollRectToVisible(table.getCellRect(row, column, false));
    		setTableCellFocused(row, column, table);
    	} catch (Throwable t) {
    		
    	}    	
    }
	
	public boolean isColumnVisible(int col) {
		if (col < ModelCollectionConstants.VISIBLE_COLUMN_NAMES.length) {
			return true;
		}
		return false;
		
	}
	
	ArrayList<Integer> visibleColumnList() {
		ArrayList<Integer> visibleColumnList = new ArrayList<Integer>();
		for (int i = 0; i < table.getColumnCount(); i++) {
			if (isColumnVisible(i)) {
				visibleColumnList.add(i);
			}
		}
		return visibleColumnList;
		
	}
	
	/***********************************************************************************/
	//end tab override methods
	/***********************************************************************************/
	
	AbstractHyperlinkAction<Object> simpleAction = new AbstractHyperlinkAction<Object>(null) {

	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
	    	if (table.getModel().getValueAt(table.getSelectedRow(), ModelCollectionConstants.URL_COLUMN) != null &&
	    			((String) table.getModel().getValueAt(table.getSelectedRow(), ModelCollectionConstants.URL_COLUMN)).trim().length() > 0) {
	    		try{ 
					String url = ((String) table.getModel().getValueAt(table.getSelectedRow(), ModelCollectionConstants.URL_COLUMN)).trim();  
					java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));  
				}  
				catch (java.io.IOException e1) {  
					setAlwaysOnTop(false);
					JOptionPane.showMessageDialog(null,                
							GraphicalInterfaceConstants.URL_NOT_FOUND_MESSAGE,                
							GraphicalInterfaceConstants.URL_NOT_FOUND_TITLE,                                
							JOptionPane.ERROR_MESSAGE);
					setAlwaysOnTop(true);
				}
	    	} else {
	    		setAlwaysOnTop(false);
				JOptionPane.showMessageDialog(null,                
						GraphicalInterfaceConstants.URL_NOT_FOUND_MESSAGE,                
						GraphicalInterfaceConstants.URL_NOT_FOUND_TITLE,                                
						JOptionPane.ERROR_MESSAGE);
				setAlwaysOnTop(true);
	    	}
	    }

	};
	
	public static void main( String args[] )
	{
		File f = new File(ModelCollectionConstants.MODEL_COLLECTION_FILE_NAME);
		ModelCollectionTable mainFrame	= new ModelCollectionTable(f);
		mainFrame.setVisible( true );
	}
}
