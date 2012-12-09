package edu.rutgers.MOST.presentation;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Insets;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import edu.rutgers.MOST.config.LocalConfig;

public class ReactionsTableCellRenderer extends DefaultTableCellRenderer{
	public Component getTableCellRendererComponent (JTable table, 
			Object obj, boolean isSelected, boolean hasFocus, int row, int column) {
		Component cell = super.getTableCellRendererComponent(
				table, obj, isSelected, hasFocus, row, column);
		int viewRow = table.convertRowIndexToModel(row);
		if (isSelected) {
			//cell.setBackground(new Color(180, 216, 231));
		}

		//based on code from http://tech.chitgoks.com/2009/11/05/display-tooltip-in-jtable-cell-if-text-is-truncated/
		int availableWidth = table.getColumnModel().getColumn(column).getWidth();
		availableWidth -= table.getIntercellSpacing().getWidth();
		Insets borderInsets = getBorder().getBorderInsets(cell);
		availableWidth -= (borderInsets.left + borderInsets.right);
		FontMetrics fm = getFontMetrics( getFont() );
		
		if (table.getModel().getValueAt(viewRow, column) != null) {
			String cellText = table.getModel().getValueAt(viewRow, column).toString();

			if (fm.stringWidth(cellText) > availableWidth) {
				((javax.swing.JLabel) cell).setToolTipText(table.getModel().getValueAt(viewRow, column).toString()); 
				if (LocalConfig.getInstance().getInvalidReactions().contains(table.getModel().getValueAt(viewRow, column).toString())) {
					((javax.swing.JLabel) cell).setToolTipText("Error: invalid syntax : " + table.getModel().getValueAt(viewRow, column).toString()); 
				}
			} else if (LocalConfig.getInstance().getInvalidReactions().contains(table.getModel().getValueAt(viewRow, column).toString())) {
				((javax.swing.JLabel) cell).setToolTipText("Error: invalid syntax");
			} else {
				((javax.swing.JLabel) cell).setToolTipText(null);
			}
		}

		return cell;
	}
} 
