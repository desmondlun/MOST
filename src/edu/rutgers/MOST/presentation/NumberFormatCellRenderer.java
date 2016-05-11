package edu.rutgers.MOST.presentation;

import java.awt.Component;
import java.text.DecimalFormat;

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
		DecimalFormat formatter = GraphicalInterfaceConstants.FLUX_FORMATTER;
		DecimalFormat sciFormatter = GraphicalInterfaceConstants.SCIENTIFIC_FLUX_FORMATTER;
		value = u.formattedNumber((String) value, formatter, sciFormatter, 
			GraphicalInterfaceConstants.MIN_DECIMAL_FORMAT, GraphicalInterfaceConstants.MAX_DECIMAL_FORMAT);
		
		return super.getTableCellRendererComponent(
				table, value, isSelected, hasFocus, row, column);
	}
} 

