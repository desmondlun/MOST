package edu.rutgers.MOST.presentation;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Insets;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import edu.rutgers.MOST.config.LocalConfig;

public class ReactionsTableCellRenderer extends DefaultTableCellRenderer{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Component getTableCellRendererComponent (JTable table, 
			Object obj, boolean isSelected, boolean hasFocus, int row, int column) {
		Component cell = super.getTableCellRendererComponent(
				table, obj, isSelected, hasFocus, row, column);
		String tooltip = "";
		int viewRow = table.convertRowIndexToModel(row);
		int id = Integer.valueOf(table.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_ID_COLUMN).toString());	
		if (LocalConfig.getInstance().getInvalidLowerBoundReversibleCombinations().contains(id)) {
			tooltip += GraphicalInterfaceConstants.INVALIID_LOWER_BOUND_REVERSIBLE_COMBINATION_TOOLTIP;
		} 
		if (LocalConfig.getInstance().getInvalidEquationReversibleCombinations().contains(id)) {
			tooltip += GraphicalInterfaceConstants.INVALIID_EQUATION_REVERSIBLE_COMBINATION_TOOLTIP;
		} 
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
					((javax.swing.JLabel) cell).setToolTipText("Error: invalid syntax : " + tooltip + table.getModel().getValueAt(viewRow, column).toString()); 
				}
			} else if (LocalConfig.getInstance().getInvalidReactions().contains(table.getModel().getValueAt(viewRow, column).toString())
					&&  column == GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN) {
				((javax.swing.JLabel) cell).setToolTipText("Error: invalid syntax" + tooltip);
			} else {
				((javax.swing.JLabel) cell).setToolTipText(tooltip);
			}
		}

		return cell;
	}
} 
