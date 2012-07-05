package edu.rutgers.MOST.presentation;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ColorTableCellRenderer extends DefaultTableCellRenderer{
	public Component getTableCellRendererComponent (JTable table, 
			Object obj, boolean isSelected, boolean hasFocus, int row, int column) {
		Component cell = super.getTableCellRendererComponent(
				table, obj, isSelected, hasFocus, row, column);
		if (isSelected) {
			//cell.setBackground(Color.green);
		} 

		cell.setBackground(new Color(240, 240, 240));	   
		return cell;
	}
}

