package edu.rutgers.MOST.data;

import java.io.*;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.presentation.GraphicalInterface;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;
import au.com.bytecode.opencsv.CSVWriter;

// TODO: account for hidden columns
public class TextMetabolitesWriter {

	public void write(String file) {
		//String extension = ".csv";
		CSVWriter writer;
		try {
			writer = new CSVWriter(new FileWriter(file), ',');

			String headerNames = "";
			//start with 1 to avoid reading database id
			for (int i = 1; i < GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length; i++) {
				headerNames += GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES[i] + "\t";
			}

			int metaColumnCount = LocalConfig.getInstance().getMetabolitesMetaColumnNames().size();
			for (int j = 0; j < metaColumnCount; j++) {
				headerNames += LocalConfig.getInstance().getMetabolitesMetaColumnNames().get(j) + "\t";
			}

			String [] header = (headerNames.substring(0, headerNames.length() - 1)).split("\t");

			writer.writeNext(header);				

			int numMetabolites = GraphicalInterface.metabolitesTable.getModel().getRowCount();
			for (int n = 0; n < numMetabolites; n++) {
				int viewRow = GraphicalInterface.metabolitesTable.convertRowIndexToModel(n);

				String metaboliteAbbreviation = " ";
				String metaboliteName = " ";
				String charge = " ";
				String compartment = " ";
				String boundary = "false";

				//check if null before toString() to avoid null pointer error
				if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN)!= null) {
					if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN).toString().length() > 0) {							
						metaboliteAbbreviation = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN);
					} else {
						metaboliteAbbreviation = " ";
					}
				}	
				if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN)!= null) {
					if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN).toString().length() > 0) {	
						metaboliteName = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN);
					} else {
						metaboliteName = " ";
					}
				}
				if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.CHARGE_COLUMN)!= null) {
					if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.CHARGE_COLUMN).toString().length() > 0) {	
						charge = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.CHARGE_COLUMN);
					} else {
						charge = " ";
					}
				}
				if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.COMPARTMENT_COLUMN)!= null) {
					if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.COMPARTMENT_COLUMN).toString().length() > 0) {
						compartment = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.COMPARTMENT_COLUMN);
					} else {
						compartment = " ";
					}
				}
				if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.BOUNDARY_COLUMN)!= null) {
					if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.BOUNDARY_COLUMN).toString().length() > 0) {
						boundary = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.BOUNDARY_COLUMN);
					} else {
						boundary = "false";
					}
				}					

				String metaString = "";
				String value = " ";
				for (int i = 0; i < LocalConfig.getInstance().getMetabolitesMetaColumnNames().size(); i++) {
					if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length + i)!= null) {
						if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length + i).toString().length() > 0) {
							value = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length + i);
						} else {
							value = " ";
						}
					}
					metaString += value + "\t";
				}

				if (metaString.length() > 0) {
					String [] entries = (metaboliteAbbreviation + "\t" + metaboliteName + "\t" + charge + "\t" + compartment + "\t" + boundary + "\t" + metaString.substring(0, metaString.length() - 1)).split("\t");
					writer.writeNext(entries);
				} else {
					String [] entries = (metaboliteAbbreviation + "\t" + metaboliteName + "\t" + charge + "\t" + compartment + "\t" + boundary).split("\t");
					writer.writeNext(entries);
				}							
			}	
			writer.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,                
					"File Not Found Error.",                
					"Error",                                
					JOptionPane.ERROR_MESSAGE);
			//e.printStackTrace();
		}
	}

	public static void main(String[] arg) throws Exception { 

	}
}

