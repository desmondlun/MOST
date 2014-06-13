package edu.rutgers.MOST.presentation;

import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

//based on http://examples.javacodegeeks.com/core-java/text/format-number-with-custom-numberformat
// based on http://helpdesk.objects.com.au/java/how-to-control-decimal-places-displayed-in-jtable-column
// may need to add in tool tips
public class NumberFormatCellRenderer extends DefaultTableCellRenderer{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final DecimalFormat formatter = new DecimalFormat("#.########");
	private static final DecimalFormat sciFormatter = new DecimalFormat("#.######E0");

	public Component getTableCellRendererComponent (JTable table, 
			Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		try {
			if (Math.abs(Double.valueOf((String) value)) != 0.0 && (Math.abs(Double.valueOf((String) value)) < GraphicalInterfaceConstants.MIN_DECIMAL_FORMAT || Math.abs(Double.valueOf((String) value)) > GraphicalInterfaceConstants.MAX_DECIMAL_FORMAT)) {
				try {
					value = sciFormatter.format((Number)Double.valueOf((String) value));
				} catch (NumberFormatException nfe) {
					
				}
			} else {
				try {
					value = formatter.format((Number)Double.valueOf((String) value));
				} catch (NumberFormatException nfe) {
					
				}
			}
		} catch (NumberFormatException nfe) {
			
		}
		
		return super.getTableCellRendererComponent(
				table, value, isSelected, hasFocus, row, column);
	}
} 

