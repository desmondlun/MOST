package edu.rutgers.MOST.presentation;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXTable;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.data.SBMLCompartment;

public class CompartmentsTableUpdater {
	
	private Vector<String> columnNames;

	public Vector<String> getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(Vector<String> columnNames) {
		this.columnNames = columnNames;
	}
	
	public void createColumns(JXTable table) {
		DefaultTableModel model = new DefaultTableModel();
		Vector<String> columnNames = new Vector<String>();
		
		for (int i = 0; i < CompartmentsConstants.visibleColumnsList.size(); i++) {						
				columnNames.add(CompartmentsConstants.visibleColumnsList.get(i));
		}
		for (int j = 0; j < columnNames.size(); j++) {
			model.addColumn(columnNames.get(j));
		}		
		setColumnNames(columnNames);
			
		table.setModel(model);
	}
	
	/**
	 * Creates Compartments table model from List of SBML Compartments
	 * @param table
	 * @return
	 */
	public DefaultTableModel createCompartmentsTableModel(JXTable table) {
		createColumns(table);
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		for (int k = 0; k < LocalConfig.getInstance().getListOfCompartments().size(); k++) {
			Vector <String> row = new Vector<String>();
			row.add(LocalConfig.getInstance().getListOfCompartments().get(k).getId());
			row.add(LocalConfig.getInstance().getListOfCompartments().get(k).getName());
			row.add(LocalConfig.getInstance().getListOfCompartments().get(k).getOutside());
			model.addRow(row);
		}
		
		return model;
	}
	
	public void updateTableByRow(JXTable table, int row) {
		for (int i = 0; i < LocalConfig.getInstance().getListOfCompartments().size(); i++) {
			if (LocalConfig.getInstance().getListOfCompartments().get(i).getId().equals(table.getModel().getValueAt(row, CompartmentsConstants.ABBREVIATION_COLUMN))) {
				updateCompartment(table, LocalConfig.getInstance().getListOfCompartments().get(i), row);
			}
		}
	}
	
	public void updateCompartment(JXTable table, SBMLCompartment comp, int row) {
		comp.setName((String) table.getModel().getValueAt(row, CompartmentsConstants.NAME_COLUMN));
		comp.setOutside((String) table.getModel().getValueAt(row, CompartmentsConstants.OUTSIDE_COLUMN));
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
	
	public void tablePaste(JXTable table) {
		String pasteString = ""; 
		try { 
			pasteString = (String)(Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this).getTransferData(DataFlavor.stringFlavor));
			if (table.getSelectedRow() > -1 && table.getSelectedColumn() > -1 &&
					table.getSelectedColumn() != CompartmentsConstants.ABBREVIATION_COLUMN) {
				table.setValueAt(pasteString, table.getSelectedRow(), table.getSelectedColumn());
				updateTableByRow(table, table.getSelectedRow());
			}
		} catch (Exception e1) { 
			JOptionPane.showMessageDialog(null, "Invalid Paste Type", "Invalid Paste Type", JOptionPane.ERROR_MESSAGE);
			return; 
		} 
	}
	
	private String escape(Object cell) { 
		return cell.toString().replace("\n", " ").replace("\t", " "); 
	} 

}
