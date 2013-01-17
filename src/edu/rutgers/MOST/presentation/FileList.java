package edu.rutgers.MOST.presentation;

import javax.swing.*;

import edu.rutgers.MOST.config.LocalConfig;

import java.awt.event.*;

//based on code from http://www.roseindia.net/java/example/java/swing/TooltipTextOfList.shtml
class FileList extends JList {

	public static int row;

	public static int getRow() {
		return row;
	}

	public static void setRow(int row) {
		FileList.row = row;
	}

	public JPopupMenu jPopupMenu = new JPopupMenu();
	public JMenuItem saveItem = new JMenuItem("Save");         
	public JMenuItem saveAsCSVItem = new JMenuItem("Save As CSV");    
	public JMenuItem saveAsSBMLItem = new JMenuItem("Save As SBML");
	public JMenuItem saveAllItem = new JMenuItem("Save All Optimizations");
	public JMenuItem deleteItem = new JMenuItem("Delete");
	public JMenuItem clearItem = new JMenuItem("Clear Optimizations");

	public FileList() {
		super();

		// Attach a mouse motion adapter to let us know the mouse is over an item and to show the tip.
		addMouseMotionListener( new MouseMotionAdapter() {
			public void mouseMoved( MouseEvent e) {
				FileList fileList = (FileList) e.getSource();
				ListModel model = fileList.getModel();
				int index = fileList.locationToIndex(e.getPoint());
				if (index > -1 && index <= model.getSize()) {
					fileList.setToolTipText(null);
					String text = (String) 
					model.getElementAt(index);
					if (text.length() > 26) {
						fileList.setToolTipText(text);
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
								saveItem.setEnabled(true);
								saveAsCSVItem.setEnabled(true);
								//saveAsSBMLItem.setEnabled(true); // will enable when SBML save works
								saveAllItem.setEnabled(true);
								deleteItem.setEnabled(true);
								clearItem.setEnabled(true);								
							}
						}
					} 	          
				}
			}
		});	
		
		jPopupMenu.add(saveItem);
		saveItem.setEnabled(false);
		saveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) { 
				if (getRow() > 0) {
					String item = GraphicalInterface.listModel.getElementAt(getRow());
					LocalConfig.getInstance().getOptimizationFilesList().remove(item);	
					System.out.println(LocalConfig.getInstance().getOptimizationFilesList());
				}				
			}
		});	
		
		jPopupMenu.add(saveAsCSVItem);
		saveAsCSVItem.setEnabled(false);
		saveAsCSVItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) { 
				if (getRow() > 0) {
					String item = GraphicalInterface.listModel.getElementAt(getRow());
					LocalConfig.getInstance().getOptimizationFilesList().remove(item);	
					System.out.println(LocalConfig.getInstance().getOptimizationFilesList());				
				}				
			}
		});
		
		jPopupMenu.add(saveAsSBMLItem);
		saveAsSBMLItem.setEnabled(false);
		saveAsSBMLItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) { 
				if (getRow() > 0) {
					String item = GraphicalInterface.listModel.getElementAt(getRow());
					LocalConfig.getInstance().getOptimizationFilesList().remove(item);	
					System.out.println(LocalConfig.getInstance().getOptimizationFilesList());				
				}				
			}
		});	
		
		jPopupMenu.add(saveAllItem);
		saveAllItem.setEnabled(false);
		saveAllItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) { 
				if (getRow() > 0) {
					LocalConfig.getInstance().getOptimizationFilesList().clear();
					System.out.println(LocalConfig.getInstance().getOptimizationFilesList());
				}				
			}
		});
		
		jPopupMenu.addSeparator();
		
		jPopupMenu.add(deleteItem);
		deleteItem.setEnabled(false);
		deleteItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) { 
				if (getRow() > 0) {
					LocalConfig.getInstance().setLoadedDatabase(LocalConfig.getInstance().getDatabaseName());
					String item = GraphicalInterface.listModel.getElementAt(getRow());
					LocalConfig.getInstance().getOptimizationFilesList().remove(item);
					GraphicalInterface.listModel.remove(getRow());
					GraphicalInterface.fileList.setModel(GraphicalInterface.listModel);
					GraphicalInterface.fileList.setSelectedIndex(0);
					GraphicalInterface.fileListPane.repaint();							
					System.out.println(LocalConfig.getInstance().getOptimizationFilesList());				
				}				
			}
		});

		jPopupMenu.addSeparator();
		
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
				LocalConfig.getInstance().getOptimizationFilesList().clear();
				System.out.println(LocalConfig.getInstance().getOptimizationFilesList());
				setSelectedIndex(0);
			}
		});

		add(jPopupMenu);
	}

	// Expose the getToolTipText event of our JList
	public String getToolTipText(MouseEvent e){
		return super.getToolTipText();
	}
}


