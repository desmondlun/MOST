package edu.rutgers.MOST.presentation;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Insets;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ModelCollectionCellRenderer extends DefaultTableCellRenderer{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Component getTableCellRendererComponent (JTable table, 
			Object obj, boolean isSelected, boolean hasFocus, int row, int column) {
		Component cell = super.getTableCellRendererComponent(
				table, obj, isSelected, hasFocus, row, column);
		if (isSelected) {
			//cell.setBackground(new Color(180, 216, 231));
		}

		//based on code from http://tech.chitgoks.com/2009/11/05/display-tooltip-in-jtable-cell-if-text-is-truncated/
		int availableWidth = table.getColumnModel().getColumn(column).getWidth();
		availableWidth -= table.getIntercellSpacing().getWidth();
		Insets borderInsets = getBorder().getBorderInsets(cell);
		availableWidth -= (borderInsets.left + borderInsets.right);
		FontMetrics fm = getFontMetrics( getFont() );
		
		if (table.getModel().getValueAt(row, column) != null) {
			String cellText = table.getModel().getValueAt(row, column).toString();

			if (fm.stringWidth(cellText) > availableWidth) {
				((javax.swing.JLabel) cell).setToolTipText(table.getModel().getValueAt(row, column).toString()); 
			} else {
				((javax.swing.JLabel) cell).setToolTipText(null);
			}
		}

		return cell;
	}
} 
