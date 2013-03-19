package edu.rutgers.MOST.presentation;

import javax.swing.*;

import org.rutgers.MOST.tree.DynamicTree;

import edu.rutgers.MOST.config.LocalConfig;

import java.awt.event.*;

//based on code from http://www.roseindia.net/java/example/java/swing/TooltipTextOfList.shtml
class GDBBList extends JList<DynamicTree> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static int row;

	public static int getRow() {
		return row;
	}

	public static void setRow(int row) {
		GDBBList.row = row;
	}

	public JPopupMenu jPopupMenu = new JPopupMenu();
	public JMenuItem deleteItem = new JMenuItem("Delete");
	public JMenuItem clearItem = new JMenuItem("Clear Optimizations");

	public GDBBList() {
		super();

		// Attach a mouse motion adapter to let us know the mouse is over an item and to show the tip.
		addMouseMotionListener( new MouseMotionAdapter() {
			public void mouseMoved( MouseEvent e) {
				GDBBList gdbbList = (GDBBList) e.getSource();
				ListModel model = gdbbList.getModel();
				int index = gdbbList.locationToIndex(e.getPoint());
				if (index > -1 && index <= model.getSize()) {
					gdbbList.setToolTipText(null);
					String text = (String) 
					model.getElementAt(index);
					if (text.length() > 26) {
						gdbbList.setToolTipText(text);
					}					
				}
			}
		});

		addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e)  {check(e);}
			public void mouseReleased(MouseEvent e) {check(e);}

			public void check(MouseEvent e) {
				if (e.isPopupTrigger()) { //if the event shows the menu
					FileList fileList = (FileList) e.getSource();
					ListModel model = fileList.getModel();
					setSelectedIndex(-1);       
					setSelectedIndex(locationToIndex(e.getPoint())); //select the item    	    	  
					fileList.setSelectedIndex(-1);       
					fileList.setSelectedIndex(fileList.locationToIndex(e.getPoint())); //select the item    	    	      			  
					int index = fileList.locationToIndex(e.getPoint());
					if (index > -1 && index <= model.getSize()) {
						String text = (String) 
						model.getElementAt(index);
						setRow(index);
						if (text.length() > 0) {
							jPopupMenu.show(fileList, e.getX(), e.getY()); //and show the menu
							if (index > 0 && index <= model.getSize()) {
								deleteItem.setEnabled(true);
								clearItem.setEnabled(true);
							}
						}

					} 	          
				}
			}
		});	


		jPopupMenu.add(deleteItem);
		deleteItem.setEnabled(false);
		deleteItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) { 
				if (getRow() > 0) {
					LocalConfig.getInstance().setLoadedDatabase(LocalConfig.getInstance().getDatabaseName());
					GraphicalInterface.listModel.remove(getRow());
					GraphicalInterface.fileList.setModel(GraphicalInterface.listModel);
					GraphicalInterface.fileListPane.repaint();					
				}				
			}
		});

		jPopupMenu.add(clearItem);
		clearItem.setEnabled(false);
		clearItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				LocalConfig.getInstance().setLoadedDatabase(LocalConfig.getInstance().getDatabaseName());
				GraphicalInterface.listModel.clear();
				GraphicalInterface.listModel.addElement(GraphicalInterface.getDatabaseName());
				GraphicalInterface.fileList.setModel(GraphicalInterface.listModel);
				GraphicalInterface.fileListPane.repaint();
				GraphicalInterface.outputTextArea.setText("");
			}
		});

		add(jPopupMenu);
	}

	// Expose the getToolTipText event of our JList
	public String getToolTipText(MouseEvent e){
		return super.getToolTipText();
	}
}