package edu.rutgers.MOST.presentation;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.data.Solution;

// based on code from http://www.java2s.com/Tutorial/Java/0240__Swing/WorkingwithTreeTooltipsusingaTooltipCellRenderer.htm
public class ToolTipTreeCellRenderer implements TreeCellRenderer {
	DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();

	public ToolTipTreeCellRenderer() {

	}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
			boolean expanded, boolean leaf, int row, boolean hasFocus) {
		renderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		if (value != null) {
			Object tipKey;
			if (value instanceof DefaultMutableTreeNode) {
				tipKey = ((DefaultMutableTreeNode) value).getUserObject();
			} else {
				tipKey = tree.convertValueToText(value, selected, expanded, leaf, row, hasFocus);
			}
			renderer.setToolTipText(LocalConfig.getInstance().getModelName());
			if (tipKey != null) {
				renderer.setToolTipText(((Solution) tipKey).getSolutionName());
			}			
		}
		return renderer;
	}
	
	public void setIcon(ImageIcon leafIcon) {
		renderer.setLeafIcon(leafIcon);
	}
}
