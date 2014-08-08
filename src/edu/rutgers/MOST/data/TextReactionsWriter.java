package edu.rutgers.MOST.data;

import java.io.*;

import javax.swing.JOptionPane;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.presentation.GraphicalInterface;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;
import au.com.bytecode.opencsv.CSVWriter;

//TODO: account for hidden columns
public class TextReactionsWriter {

	public void write(String file) {
		//String extension = ".csv";
		CSVWriter writer;
		try {
			writer = new CSVWriter(new FileWriter(file), ',');

			String headerNames = "";
			//start with 1 to avoid reading database id
			for (int i = 1; i < GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length; i++) {
				if (LocalConfig.getInstance().fvaColumnsVisible) {
					if (i != GraphicalInterfaceConstants.REACTION_EQUN_NAMES_COLUMN) {
						headerNames += GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[i] + "\t";
					}	
				} else {
					if (i == GraphicalInterfaceConstants.REACTION_EQUN_NAMES_COLUMN ||
							i == GraphicalInterfaceConstants.MIN_FLUX_COLUMN ||
							i == GraphicalInterfaceConstants.MAX_FLUX_COLUMN) {
					} else {
						headerNames += GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[i] + "\t";
					}
				}
			}
			
			int metaColumnCount = LocalConfig.getInstance().getReactionsMetaColumnNames().size();
			for (int j = 0; j < metaColumnCount; j++) {
				headerNames += LocalConfig.getInstance().getReactionsMetaColumnNames().get(j) + "\t";
			}

			String [] header = (headerNames.substring(0, headerNames.length() - 1)).split("\t");

			writer.writeNext(header);
			int numReactions = GraphicalInterface.reactionsTable.getModel().getRowCount();
			for (int n = 0; n < numReactions; n++) {
				int viewRow = GraphicalInterface.reactionsTable.convertRowIndexToModel(n);
				
				String knockout = "false";
				String fluxValue = "0.0";
				String minFlux = "0.0";
				String maxFlux = "0.0";
				String reactionAbbreviation = " ";
				String reactionName = " ";
				String reactionEqunAbbr = " ";
				String reversible = " ";
				String lowerBound = "-999999";
				String upperBound = "999999";
				String biologicalObjective = "0.0";
				String syntheticObjective = "0.0";
				String geneAssociation = " ";
				String proteinAssociation = " ";
				String subsystem = " ";
				String proteinClass = " ";
				
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
						fluxValue = ((String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.FLUX_VALUE_COLUMN));
					} else {
						fluxValue = "0.0";
					}
				}
				if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.MIN_FLUX_COLUMN)!= null) {
					if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.MIN_FLUX_COLUMN).toString().length() > 0) {							
						minFlux = ((String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.MIN_FLUX_COLUMN));
					} else {
						minFlux = "0.0";
					}
				}
				if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.MAX_FLUX_COLUMN)!= null) {
					if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.MAX_FLUX_COLUMN).toString().length() > 0) {							
						maxFlux = ((String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.MAX_FLUX_COLUMN));
					} else {
						maxFlux = "0.0";
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
				if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.GENE_ASSOCIATION_COLUMN)!= null) {
					if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.GENE_ASSOCIATION_COLUMN).toString().length() > 0) {	
						geneAssociation = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.GENE_ASSOCIATION_COLUMN);
					} else {
						geneAssociation = " ";
					}
				}
				if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.PROTEIN_ASSOCIATION_COLUMN)!= null) {
					if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.PROTEIN_ASSOCIATION_COLUMN).toString().length() > 0) {	
						proteinAssociation = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.PROTEIN_ASSOCIATION_COLUMN);
					} else {
						proteinAssociation = " ";
					}
				}
				if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.SUBSYSTEM_COLUMN)!= null) {
					if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.SUBSYSTEM_COLUMN).toString().length() > 0) {	
						subsystem = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.SUBSYSTEM_COLUMN);
					} else {
						subsystem = " ";
					}
				}
				if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.PROTEIN_CLASS_COLUMN)!= null) {
					if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.PROTEIN_CLASS_COLUMN).toString().length() > 0) {	
						proteinClass = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.PROTEIN_CLASS_COLUMN);
					} else {
						proteinClass = " ";
					}
				}
				
				String metaString = "";
				String value = " ";
				
				if (LocalConfig.getInstance().getReactionsMetaColumnNames().size() > 0) {
					for (int i = 0; i < LocalConfig.getInstance().getReactionsMetaColumnNames().size(); i++) {
						if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length + i)!= null) {
							if (GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length + i).toString().length() > 0) {
								value = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(viewRow, GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length + i);
							} else {
								value = " ";
							}
						}
						metaString += value + "\t";
					}
				}
				
				if (LocalConfig.getInstance().fvaColumnsVisible) {
					if (metaString.length() > 0) {
						String [] entries = (knockout + "\t" + fluxValue + "\t" + minFlux + "\t" + maxFlux + "\t" + reactionAbbreviation + "\t" + reactionName + "\t" + reactionEqunAbbr + "\t" + reversible + "\t" + lowerBound + "\t" + upperBound + "\t" + biologicalObjective + "\t" + syntheticObjective + "\t" + geneAssociation + "\t" + proteinAssociation + "\t" + subsystem + "\t" + proteinClass + "\t" + metaString.substring(0, metaString.length() - 1)).split("\t");
						writer.writeNext(entries);
					} else {
						String [] entries = (knockout + "\t" + fluxValue + "\t" + minFlux + "\t" + maxFlux + "\t" + reactionAbbreviation + "\t" + reactionName + "\t" + reactionEqunAbbr + "\t" + reversible + "\t" + lowerBound + "\t" + upperBound + "\t" + biologicalObjective + "\t" + syntheticObjective + "\t" + geneAssociation + "\t" + proteinAssociation + "\t" + subsystem + "\t" + proteinClass).split("\t");
						writer.writeNext(entries);
					}	
				} else {
					if (metaString.length() > 0) {
						String [] entries = (knockout + "\t" + fluxValue + "\t" + reactionAbbreviation + "\t" + reactionName + "\t" + reactionEqunAbbr + "\t" + reversible + "\t" + lowerBound + "\t" + upperBound + "\t" + biologicalObjective + "\t" + syntheticObjective + "\t" + geneAssociation + "\t" + proteinAssociation + "\t" + subsystem + "\t" + proteinClass + "\t" + metaString.substring(0, metaString.length() - 1)).split("\t");
						writer.writeNext(entries);
					} else {
						String [] entries = (knockout + "\t" + fluxValue + "\t" + reactionAbbreviation + "\t" + reactionName + "\t" + reactionEqunAbbr + "\t" + reversible + "\t" + lowerBound + "\t" + upperBound + "\t" + biologicalObjective + "\t" + syntheticObjective + "\t" + geneAssociation + "\t" + proteinAssociation + "\t" + subsystem + "\t" + proteinClass).split("\t");
						writer.writeNext(entries);
					}
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

}


