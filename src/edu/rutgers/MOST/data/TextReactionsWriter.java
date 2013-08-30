package edu.rutgers.MOST.data;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.presentation.GraphicalInterface;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;

import au.com.bytecode.opencsv.CSVWriter;

//TODO: account for hidden columns
public class TextReactionsWriter {

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
				for (int i = 1; i < GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length; i++) {
					headerNames += GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[i] + "\t";
				}

				ReactionsMetaColumnManager reactionsMetaColumnManager = new ReactionsMetaColumnManager();

				int metaColumnCount = reactionsMetaColumnManager.getMetaColumnCount(LocalConfig.getInstance().getDatabaseName());
				for (int j = 1; j < metaColumnCount + 1; j++) {
					headerNames += reactionsMetaColumnManager.getColumnName(LocalConfig.getInstance().getDatabaseName(), j) + "\t";
				}

				String [] header = (headerNames.substring(0, headerNames.length() - 1)).split("\t");

				writer.writeNext(header);
				int numReactions = GraphicalInterface.reactionsTable.getModel().getRowCount();
				for (int n = 0; n < numReactions; n++) {
					int viewRow = GraphicalInterface.reactionsTable.convertRowIndexToModel(n);
					
					String knockout = "false";
					String fluxValue = "0.0";
					String reactionAbbreviation = " ";
					String reactionName = " ";
					String reactionEqunAbbr = " ";
					String reactionEqunNames = " ";
					String reversible = " ";
					String lowerBound = "-999999";
					String upperBound = "999999";
					String biologicalObjective = "0.0";
					String syntheticObjective = "0.0";
					String geneAssociations = " ";
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
					if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.KO_COLUMN)!= null) {
						if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.KO_COLUMN).toString().length() > 0) {							
							knockout = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.KO_COLUMN);
						} else {
							knockout = "false";
						}
					}
					if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.FLUX_VALUE_COLUMN)!= null) {
						if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.FLUX_VALUE_COLUMN).toString().length() > 0) {							
							fluxValue = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.FLUX_VALUE_COLUMN);
						} else {
							fluxValue = "0.0";
						}
					}				
					if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_ABBREVIATION_COLUMN)!= null) {
						if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_ABBREVIATION_COLUMN).toString().length() > 0) {							
							reactionAbbreviation = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_ABBREVIATION_COLUMN);
						} else {
							reactionAbbreviation = " ";
						}
					}	
					if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_NAME_COLUMN)!= null) {
						if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_NAME_COLUMN).toString().length() > 0) {	
							reactionName = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_NAME_COLUMN);
						} else {
							reactionName = " ";
						}
					}
					if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN)!= null) {
						if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN).toString().length() > 0) {	
							reactionEqunAbbr = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN);
						} else {
							reactionEqunAbbr = " ";
						}
					}
					if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_EQUN_NAMES_COLUMN)!= null) {
						if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_EQUN_NAMES_COLUMN).toString().length() > 0) {	
							reactionEqunNames = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_EQUN_NAMES_COLUMN);
						} else {
							reactionEqunNames = " ";
						}
					}
					if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REVERSIBLE_COLUMN)!= null) {
						if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REVERSIBLE_COLUMN).toString().length() > 0) {	
							reversible = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REVERSIBLE_COLUMN);
						} else {
							reversible = " ";
						}
					}
					if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.LOWER_BOUND_COLUMN)!= null) {
						if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.LOWER_BOUND_COLUMN).toString().length() > 0) {	
							lowerBound = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.LOWER_BOUND_COLUMN);
						} else {
							lowerBound = "-999999";
						}
					}
					if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.UPPER_BOUND_COLUMN)!= null) {
						if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.UPPER_BOUND_COLUMN).toString().length() > 0) {	
							upperBound = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.UPPER_BOUND_COLUMN);
						} else {
							upperBound = "999999";
						}
					}
					if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN)!= null) {
						if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN).toString().length() > 0) {	
							biologicalObjective = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN);
						} else {
							biologicalObjective = "0.0";
						}
					}
					if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_COLUMN)!= null) {
						if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_COLUMN).toString().length() > 0) {	
							syntheticObjective = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_COLUMN);
						} else {
							syntheticObjective = "0.0";
						}
					}
					if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.GENE_ASSOCIATIONS_COLUMN)!= null) {
						if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.GENE_ASSOCIATIONS_COLUMN).toString().length() > 0) {	
							geneAssociations = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.GENE_ASSOCIATIONS_COLUMN);
						} else {
							geneAssociations = " ";
						}
					}
					
					String metaString = "";
					if (metaColumnCount > 0) {
						//check if null before toString() to avoid null pointer error
						if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META1_COLUMN)!= null) {
							if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META1_COLUMN).toString().length() > 0) {
								meta1 = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META1_COLUMN);
							} 				
						}
						metaString += meta1 + "\t";
					}
					if (metaColumnCount > 1) {
						if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META2_COLUMN)!= null) {
							if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META2_COLUMN).toString().length() > 0) {
								meta2 = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META2_COLUMN);
							} 						
						} 
						metaString += meta2 + "\t";
					}
					if (metaColumnCount > 2) {
						if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META3_COLUMN)!= null) {
							if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META3_COLUMN).toString().length() > 0) {
								meta3 = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META3_COLUMN);
							}					
						} 
						metaString += meta3 + "\t";
					}
					if (metaColumnCount > 3) {
						if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META4_COLUMN)!= null) {
							if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META4_COLUMN).toString().length() > 0) {
								meta4 = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META4_COLUMN);
							} 						
						} 
						metaString += meta4 + "\t";
					}
					if (metaColumnCount > 4) {
						if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META5_COLUMN)!= null) {
							if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META5_COLUMN).toString().length() > 0) {
								meta5 = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META5_COLUMN);
							} 				
						} 
						metaString += meta5 + "\t";
					}
					if (metaColumnCount > 5) {
						if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META6_COLUMN)!= null) {
							if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META6_COLUMN).toString().length() > 0) {
								meta6 = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META6_COLUMN);
							} 						
						} 
						metaString += meta6 + "\t";
					}
					if (metaColumnCount > 6) {
						if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META7_COLUMN)!= null) {
							if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META7_COLUMN).toString().length() > 0) {
								meta7 = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META7_COLUMN);
							} 
						}
						metaString += meta7 + "\t";
					}
					if (metaColumnCount > 7) {
						if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META8_COLUMN)!= null) {
							if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META8_COLUMN).toString().length() > 0) {
								meta8 = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META8_COLUMN);
							} 			
						} 
						metaString += meta8 + "\t";
					}
					if (metaColumnCount > 8) {
						if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META9_COLUMN)!= null) {
							if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META9_COLUMN).toString().length() > 0) {
								meta9 = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META9_COLUMN);
							} 						
						} 
						metaString += meta9 + "\t";
					}
					if (metaColumnCount > 9) {
						if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META10_COLUMN)!= null) {
							if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META10_COLUMN).toString().length() > 0) {
								meta10 = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META10_COLUMN);
							} 						
						} 
						metaString += meta10 + "\t";
					}
					if (metaColumnCount > 10) {
						if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META11_COLUMN)!= null) {
							if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META11_COLUMN).toString().length() > 0) {
								meta11 = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META11_COLUMN);
							} 
						} 
						metaString += meta11 + "\t";
					}
					if (metaColumnCount > 11) {
						if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META12_COLUMN)!= null) {
							if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META12_COLUMN).toString().length() > 0) {
								meta12 = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META12_COLUMN);
							} 						
						} 
						metaString += meta12 + "\t";
					}
					if (metaColumnCount > 12) {
						if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META13_COLUMN)!= null) {
							if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META13_COLUMN).toString().length() > 0) {
								meta13 = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META13_COLUMN);
							}				
						} 
						metaString += meta13 + "\t";
					}
					if (metaColumnCount > 13) {
						if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META14_COLUMN)!= null) {
							if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META14_COLUMN).toString().length() > 0) {
								meta14 = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META14_COLUMN);
							} 					
						} 
						metaString += meta14 + "\t";
					}
					if (metaColumnCount > 14) {
						if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META15_COLUMN)!= null) {
							if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META15_COLUMN).toString().length() > 0) {
								meta15 = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTION_META15_COLUMN);
							} 						
						} 
						metaString += meta15 + "\t";
					}			
					
					if (metaString.length() > 0) {
						String [] entries = (knockout + "\t" + fluxValue + "\t" + reactionAbbreviation + "\t" + reactionName + "\t" + reactionEqunAbbr + "\t" + reactionEqunNames + "\t" + reversible + "\t" + lowerBound + "\t" + upperBound + "\t" + biologicalObjective + "\t" + syntheticObjective + "\t" + geneAssociations + "\t" + metaString.substring(0, metaString.length() - 1)).split("\t");
						writer.writeNext(entries);
					} else {
						String [] entries = (knockout + "\t" + fluxValue + "\t" + reactionAbbreviation + "\t" + reactionName + "\t" + reactionEqunAbbr + "\t" + reactionEqunNames + "\t" + reversible + "\t" + lowerBound + "\t" + upperBound + "\t" + biologicalObjective + "\t" + syntheticObjective + "\t" + geneAssociations).split("\t");
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

}


