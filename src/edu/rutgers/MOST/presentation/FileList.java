package edu.rutgers.MOST.presentation;

import javax.swing.*;

import java.awt.event.*;

//based on code from http://www.roseindia.net/java/example/java/swing/TooltipTextOfList.shtml
class FileList extends JList {
	public FileList() {
		super();

		// Attach a mouse motion adapter to let us know the mouse is over an item and to show the tip.
		addMouseMotionListener( new MouseMotionAdapter() {
			public void mouseMoved( MouseEvent e) {
				FileList fileList = (FileList) e.getSource();
				ListModel model = fileList.getModel();
				int index = fileList.locationToIndex(e.getPoint());
				if (index > -1) {
					fileList.setToolTipText(null);
					String text = (String) 
					model.getElementAt(index);
					fileList.setToolTipText(text);
				}
			}
		});
	}

	// Expose the getToolTipText event of our JList
	public String getToolTipText(MouseEvent e){
		return super.getToolTipText();
	}
}