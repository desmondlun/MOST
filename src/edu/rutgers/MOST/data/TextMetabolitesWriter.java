package edu.rutgers.MOST.data;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.presentation.GraphicalInterface;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;

import au.com.bytecode.opencsv.CSVWriter;

// TODO: account for hidden columns
public class TextMetabolitesWriter {

	public void write(String file, String databaseName) {
		String queryString = "jdbc:sqlite:" + databaseName + ".db";
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Connection conn =
				DriverManager.getConnection(queryString);
				
			//String extension = ".csv";
			CSVWriter writer;
			try {
				writer = new CSVWriter(new FileWriter(file), ',');

				String headerNames = "";
				//start with 1 to avoid reading database id
				for (int i = 1; i < GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length; i++) {
					headerNames += GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES[i] + "#";
				}
                
				MetabolitesMetaColumnManager metabolitesMetaColumnManager = new MetabolitesMetaColumnManager();

				int metaColumnCount = metabolitesMetaColumnManager.getMetaColumnCount(LocalConfig.getInstance().getDatabaseName());
				for (int j = 1; j < metaColumnCount + 1; j++) {
					headerNames += metabolitesMetaColumnManager.getColumnName(LocalConfig.getInstance().getDatabaseName(), j) + "#";
				}
                
				String [] header = (headerNames.substring(0, headerNames.length() - 1)).split("#");

				writer.writeNext(header);				
				
				int numMetabolites = GraphicalInterface.metabolitesTable.getModel().getRowCount();
				for (int n = 0; n < numMetabolites; n++) {
					int viewRow = GraphicalInterface.metabolitesTable.convertRowIndexToModel(n);
					
					String metaboliteAbbreviation = " ";
					String metaboliteName = " ";
					String charge = " ";
					String compartment = " ";
					String boundary = "false";
					String meta1 = " ";
					String meta2 = " ";
					String meta3 = " ";
					String meta4 = " ";
					String meta5 = " ";
					String meta6 = " ";
					String meta7 = " ";
					String meta8 = " ";
					String meta9 = " ";
					String meta10 = " ";
					String meta11 = " ";
					String meta12 = " ";
					String meta13 = " ";
					String meta14 = " ";
					String meta15 = " ";
					
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
					if (metaColumnCount > 0) {
						//check if null before toString() to avoid null pointer error
						if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META1_COLUMN)!= null) {
							if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META1_COLUMN).toString().length() > 0) {
								meta1 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META1_COLUMN);
							} 				
						}
						metaString += meta1 + "#";
					}
					if (metaColumnCount > 1) {
						if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META2_COLUMN)!= null) {
							if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META2_COLUMN).toString().length() > 0) {
								meta2 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META2_COLUMN);
							} 						
						} 
						metaString += meta2 + "#";
					}
					if (metaColumnCount > 2) {
						if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META3_COLUMN)!= null) {
							if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META3_COLUMN).toString().length() > 0) {
								meta3 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META3_COLUMN);
							}					
						} 
						metaString += meta3 + "#";
					}
					if (metaColumnCount > 3) {
						if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META4_COLUMN)!= null) {
							if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META4_COLUMN).toString().length() > 0) {
								meta4 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META4_COLUMN);
							} 						
						} 
						metaString += meta4 + "#";
					}
					if (metaColumnCount > 4) {
						if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META5_COLUMN)!= null) {
							if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META5_COLUMN).toString().length() > 0) {
								meta5 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META5_COLUMN);
							} 				
						} 
						metaString += meta5 + "#";
					}
					if (metaColumnCount > 5) {
						if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META6_COLUMN)!= null) {
							if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META6_COLUMN).toString().length() > 0) {
								meta6 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META6_COLUMN);
							} 						
						} 
						metaString += meta6 + "#";
					}
					if (metaColumnCount > 6) {
						if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META7_COLUMN)!= null) {
							if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META7_COLUMN).toString().length() > 0) {
								meta7 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META7_COLUMN);
							} 
						}
						metaString += meta7 + "#";
					}
					if (metaColumnCount > 7) {
						if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META8_COLUMN)!= null) {
							if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META8_COLUMN).toString().length() > 0) {
								meta8 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META8_COLUMN);
							} 			
						} 
						metaString += meta8 + "#";
					}
					if (metaColumnCount > 8) {
						if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META9_COLUMN)!= null) {
							if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META9_COLUMN).toString().length() > 0) {
								meta9 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META9_COLUMN);
							} 						
						} 
						metaString += meta9 + "#";
					}
					if (metaColumnCount > 9) {
						if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META10_COLUMN)!= null) {
							if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META10_COLUMN).toString().length() > 0) {
								meta10 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META10_COLUMN);
							} 						
						} 
						metaString += meta10 + "#";
					}
					if (metaColumnCount > 10) {
						if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META11_COLUMN)!= null) {
							if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META11_COLUMN).toString().length() > 0) {
								meta11 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META11_COLUMN);
							} 
						} 
						metaString += meta11 + "#";
					}
					if (metaColumnCount > 11) {
						if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META12_COLUMN)!= null) {
							if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META12_COLUMN).toString().length() > 0) {
								meta12 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META12_COLUMN);
							} 						
						} 
						metaString += meta12 + "#";
					}
					if (metaColumnCount > 12) {
						if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META13_COLUMN)!= null) {
							if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META13_COLUMN).toString().length() > 0) {
								meta13 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META13_COLUMN);
							}				
						} 
						metaString += meta13 + "#";
					}
					if (metaColumnCount > 13) {
						if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META14_COLUMN)!= null) {
							if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META14_COLUMN).toString().length() > 0) {
								meta14 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META14_COLUMN);
							} 					
						} 
						metaString += meta14 + "#";
					}
					if (metaColumnCount > 14) {
						if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META15_COLUMN)!= null) {
							if (GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META15_COLUMN).toString().length() > 0) {
								meta15 = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.METABOLITE_META15_COLUMN);
							} 						
						} 
						metaString += meta15 + "#";
					}
					
					if (metaString.length() > 0) {
						String [] entries = (metaboliteAbbreviation + "#" + metaboliteName + "#" + charge + "#" + compartment + "#" + boundary + "#" + metaString.substring(0, metaString.length() - 1)).split("#");
						writer.writeNext(entries);
					} else {
						String [] entries = (metaboliteAbbreviation + "#" + metaboliteName + "#" + charge + "#" + compartment + "#" + boundary).split("#");
						writer.writeNext(entries);
					}							
				}	
				writer.close();
				conn.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] arg) throws Exception { 

	}
}

