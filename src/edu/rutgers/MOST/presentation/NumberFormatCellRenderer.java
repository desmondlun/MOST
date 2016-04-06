package edu.rutgers.MOST.presentation;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class NumberFormatCellRenderer extends DefaultTableCellRenderer{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Component getTableCellRendererComponent (JTable table, 
			Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Utilities u = new Utilities();
		value = u.formattedNumber((String) value);
		
		return super.getTableCellRendererComponent(
				table, value, isSelected, hasFocus, row, column);
	}
} 

