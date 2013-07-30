package edu.rutgers.MOST.presentation;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import au.com.bytecode.opencsv.CSVReader;

// loosely based on http://www.cs.cf.ac.uk/Dave/HCI/HCI_Handout_CALLER/node167.html
// based on http://www.coderanch.com/t/345311/GUI/java/Adding-rows-Jtable
class ModelCollectionTable
		extends 	JFrame
 {
	// Instance attributes used in this example
	private	JPanel		topPanel;
	private	JScrollPane scrollPane;
	private JTable table = new JTable(){  
		public boolean isCellEditable(int row, int column){
			return false;
			
		}	
	};
	private DefaultTableModel model = new DefaultTableModel();
	private	JPanel		bottomLeftPanel;
	private	JPanel		bottomRightPanel;
	private	JPanel		bottomPanel;
	public static JButton okButton = new JButton("  OK  ");
	public static JButton cancelButton = new JButton("Cancel");
	
	public String fileName;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String path;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	// Constructor of main frame
	public ModelCollectionTable(File file)
	{
		// Set the frame characteristics
		setTitle( GraphicalInterfaceConstants.TITLE + " - " + "Model Collection" );
		setSize( 700, 500 );
		setBackground( Color.gray );
		
		okButton.setEnabled(false);
		
		getRootPane().setDefaultButton(okButton);
		
		table.setRowHeight(20);
		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(true); 
		table.getSelectionModel().addListSelectionListener(new RowListener());
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);  

		// Create a panel to hold all other components
		topPanel = new JPanel();
		topPanel.setLayout( new BorderLayout() );
		getContentPane().add( topPanel );
		
		/*
		// Create columns names
		String columnNames[] = { "Column 1", "Column 2", "Column 3",  "Column 4", "Column 5", 
				"Column 6", "Column 7", "Column 8", "Column 9",};		
		
		for (int i = 0; i < columnNames.length; i++) {
			model.addColumn(columnNames[i]);
		}		
		
		table.setModel(model);
		*/
		int count = 0;
		Vector<String> columnNames = new Vector<String>();
		
		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(file), ',');
			String [] dataArray;
			try {
				while ((dataArray = reader.readNext()) != null) {
					if (count == 0) {
						for (int s = 0; s < dataArray.length; s++) {						
							columnNames.add(dataArray[s]);
						}
						for (int i = 0; i < columnNames.size(); i++) {
							model.addColumn(columnNames.get(i));
						}		
						
						table.setModel(model);
					} else {
						Vector <String> row = new Vector<String>();
						for (int s = 0; s < dataArray.length; s++) {						
							row.add(dataArray[s]);
						}
						model.addRow(row);
					}
					count += 1;
				}
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	

		// Add the table to a scrolling pane
		scrollPane = new JScrollPane( table );
		topPanel.add( scrollPane, BorderLayout.CENTER );
		bottomLeftPanel = new JPanel();
		bottomLeftPanel.setLayout( new BorderLayout() );
		bottomRightPanel = new JPanel();
		bottomPanel = new JPanel();
		bottomRightPanel.setLayout( new BorderLayout() );
		bottomLeftPanel.add( okButton, BorderLayout.WEST );
		bottomRightPanel.add( cancelButton, BorderLayout.EAST );
		bottomPanel.add(bottomLeftPanel, BorderLayout.WEST);	
		bottomPanel.add(bottomRightPanel, BorderLayout.EAST);
		topPanel.add( bottomPanel, BorderLayout.SOUTH );
		
		int r = table.getModel().getColumnCount();	
		for (int i = 0; i < r; i++) {
			//set background of id column to grey
			ModelCollectionCellRenderer renderer = new ModelCollectionCellRenderer();			
			TableColumn column = table.getColumnModel().getColumn(i);
            column.setCellRenderer(renderer);
            // Column widths can be changed here
            if (i == 1) {
            	column.setPreferredWidth(200);
            }
            if (i == 6) {
            	column.setPreferredWidth(150);
            }
            if (i == 7 || i == 8) {
            	//sets column not visible
    			column.setMaxWidth(0);
    			column.setMinWidth(0); 
    			column.setWidth(0); 
    			column.setPreferredWidth(0);
            }            
		}	
		
		ActionListener okButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				//setVisible(false);
				//dispose();				
			}
		};

		okButton.addActionListener(okButtonActionListener);
		
		ActionListener cancelButtonActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				setVisible(false);
				dispose();				
			}
		};

		cancelButton.addActionListener(cancelButtonActionListener);
		
	}
	
	private class RowListener implements ListSelectionListener {
    	public void valueChanged(ListSelectionEvent event) {
    		if (table.getSelectedRow() > -1) {
    			// location of folder in the user's computer
				String userPath = "etc";
				// model collection folder location
				String path = (table.getModel().getValueAt(table.getSelectedRow(), 7)).toString();
				//String path = (table.getModel().getValueAt(table.getSelectedRow(), 7)).toString().trim();
				String filename = (table.getModel().getValueAt(table.getSelectedRow(), 0)).toString();
				//String filename = (table.getModel().getValueAt(table.getSelectedRow(), 0)).toString().trim();
				String ending = "";
				String type = (table.getModel().getValueAt(table.getSelectedRow(), 8)).toString();
				if (type.equals("sbml")) {
					ending = ".xml";
				} else if (type.equals("csv")) {
					ending = "._mtb.csv";
				}
				setFileName(filename);
				setPath(userPath + "\\" + path + "\\" + filename + ending);
    			okButton.setEnabled(true);
			}
    	}
    }
	
	public static void main( String args[] )
	{
		File f = new File("ModelCollection2.csv");
		ModelCollectionTable mainFrame	= new ModelCollectionTable(f);
		mainFrame.setVisible( true );
	}
}
