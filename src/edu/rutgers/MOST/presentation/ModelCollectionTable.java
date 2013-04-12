package edu.rutgers.MOST.presentation;

import java.awt.*;
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

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

	// Constructor of main frame
	public ModelCollectionTable()
	{
		// Set the frame characteristics
		setTitle( GraphicalInterfaceConstants.TITLE + " - " + "Model Collection" );
		setSize( 700, 500 );
		setBackground( Color.gray );

		// Create a panel to hold all other components
		topPanel = new JPanel();
		topPanel.setLayout( new BorderLayout() );
		getContentPane().add( topPanel );

		// Create columns names
		String columnNames[] = { "Column 1", "Column 2", "Column 3",  "Column 4", "Column 5", 
				"Column 6", "Column 7", "Column 8", "Column 9",};

		DefaultTableModel model = new DefaultTableModel();
		
		for (int i = 0; i < columnNames.length; i++) {
			model.addColumn(columnNames[i]);
		}
		
		int numRows = 70;		
		
		table.setModel(model);
		for (int j = 0; j < numRows; j++) {	
			Vector <String> row = new Vector<String>();
			// add data here
			row.add("test");
			row.add("1");
			row.add("cat");
			row.add("dog");
			row.add("-99999");
			model.addRow(row);
		}		

		// Add the table to a scrolling pane
		scrollPane = new JScrollPane( table );
		topPanel.add( scrollPane, BorderLayout.CENTER );
	
	}

	// Main entry point for this example
	public static void main( String args[] )
	{
		// Create an instance of the test application
		ModelCollectionTable mainFrame	= new ModelCollectionTable();
		mainFrame.setVisible( true );
	}
}
