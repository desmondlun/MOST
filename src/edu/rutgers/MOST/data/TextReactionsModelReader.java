package edu.rutgers.MOST.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;

import au.com.bytecode.opencsv.CSVReader;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.logic.ReactionParser;
import edu.rutgers.MOST.presentation.GraphicalInterface;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;

public class TextReactionsModelReader {
	
	public boolean noReactants;     // type ==> p
	public boolean noProducts;      // type r ==>
	
	public ArrayList<String> columnNamesFromFile(File file, int row) {
		ArrayList<String> columnNamesFromFile = new ArrayList<String>();
		
		String[] dataArray = null;

		//use fileReader to read first line to get headers
		BufferedReader CSVFile;
		try {
			CSVFile = new BufferedReader(new FileReader(file));
			String dataRow = CSVFile.readLine();
			dataArray = dataRow.split(",");				

			//add all column names to list			
			for (int h = 0; h < dataArray.length; h++) { 

				//remove quotes if exist
				if (dataArray[h].startsWith("\"")) {
					//removes " " and null strings
					if (dataArray[h].compareTo("\" \"") != 0 && dataArray[h].trim().length() > 0) {
						columnNamesFromFile.add(dataArray[h].substring(1, dataArray[h].length() - 1));
					}					
				} else {
					if (dataArray[h].trim().length() > 0) {
						columnNamesFromFile.add(dataArray[h]);
					}					
				}			
			}

			if (row > 0) {
				for (int i = 0; i < row; i++) {
					dataRow = CSVFile.readLine();
					dataArray = dataRow.split(",");								
					columnNamesFromFile.clear();
					//add all column names to list			
					for (int h = 0; h < dataArray.length; h++) { 
						//remove quotes if exist
						if (dataArray[h].startsWith("\"")) {
							//removes " " and null strings
							if (dataArray[h].compareTo("\" \"") != 0 && dataArray[h].trim().length() > 0) {
								columnNamesFromFile.add(dataArray[h].substring(1, dataArray[h].length() - 1));
							}					
						} else {
							if (dataArray[h].trim().length() > 0) {
								columnNamesFromFile.add(dataArray[h]);
							}					
						}			
					} 
				}				
			}

			CSVFile.close();

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();							
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 

		return columnNamesFromFile;
	}	

	public Integer numberOfLines(File file) {
		int count = 0;
		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(file), ',');
			String[] dataArray;
			try {
				while ((dataArray = reader.readNext()) != null) {
					count++; 	
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
		return count;		
	}

	public void load(File file, String databaseName){	
		ReactionParser parser = new ReactionParser();		
		//if first row of file in not column names, starts reading after row that contains names
		int correction = LocalConfig.getInstance().getReactionsNextRowCorrection();
		int row = 1;
		int maxMetabId = LocalConfig.getInstance().getMaxMetaboliteId();
		
		LocalConfig.getInstance().getMetaboliteUsedMap().clear();
		
		DatabaseCreator databaseCreator = new DatabaseCreator();
		databaseCreator.createReactionsTable(databaseName, "reactions");
		databaseCreator.createReactionReactantsTable(databaseName, "reaction_reactants");
		databaseCreator.createReactionProductsTable(databaseName, "reaction_products");
		
		LocalConfig.getInstance().addMetaboliteOption = true;
		
		String metaString = "";
		for (int i = 0; i < 15; i++) {
			String meta = ", meta_" + (i + 1)+ " varchar(500)";
			metaString += meta;
		}
		
		String queryString = "jdbc:sqlite:" + databaseName + ".db";

		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			Connection conn =
				DriverManager.getConnection(queryString);

			Statement stat = conn.createStatement();
			
			if (!LocalConfig.getInstance().hasMetabolitesFile) {
						
				LocalConfig.getInstance().getMetaboliteUsedMap().clear();
				LocalConfig.getInstance().getDuplicateIds().clear();
				LocalConfig.getInstance().getSuspiciousMetabolites().clear();
				LocalConfig.getInstance().getMetaboliteIdNameMap().clear();
				LocalConfig.getInstance().setMaxMetaboliteId(0);
				maxMetabId = 0;
				
				databaseCreator.createMetabolitesTable(databaseName, "metabolites");
				
				stat.executeUpdate("drop table if exists metabolites_meta_info;");		    
				stat.executeUpdate("CREATE TABLE metabolites_meta_info (id INTEGER PRIMARY KEY, meta_column_name varchar(100));");
				
			}
			
			CSVReader reader;
			try {
				reader = new CSVReader(new FileReader(file), ',');
				String [] dataArray;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				reader = new CSVReader(new FileReader(file), ',');
				
				int numLines = numberOfLines(file);
				
				stat.executeUpdate("BEGIN TRANSACTION");
				PreparedStatement reacInsertPrep = conn.prepareStatement("INSERT INTO reactions(knockout, flux_value, reaction_abbreviation, " 
						+ " reaction_name, reaction_equn_abbr, reaction_equn_names, reversible, lower_bound, upper_bound, biological_objective," 
						+ " synthetic_objective, gene_associations, meta_1, meta_2, meta_3, meta_4, meta_5, meta_6, meta_7, meta_8, meta_9, meta_10, meta_11, "
						+ " meta_12, meta_13, meta_14, meta_15) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"); 
				PreparedStatement addMetabPrep = conn.prepareStatement("insert into metabolites (metabolite_abbreviation, boundary) values(?, 'false')");											
				PreparedStatement rrInsertPrep = conn.prepareStatement("INSERT INTO reaction_reactants(reaction_id, stoic, metabolite_id) values (?, ?, ?)");
				PreparedStatement rpInsertPrep = conn.prepareStatement("INSERT INTO reaction_products(reaction_id, stoic, metabolite_id) values (?, ?, ?)");
				PreparedStatement deleteReacPrep = conn.prepareStatement("delete from reaction_reactants where reaction_id=?;");
				PreparedStatement deleteProdPrep = conn.prepareStatement("delete from reaction_products where reaction_id=?;");
				PreparedStatement reacNamePrep = conn.prepareStatement("SELECT metabolite_name from metabolites where id=?;");
				for (int i = 0; i < numLines; i++) {
					LocalConfig.getInstance().setProgress(i*100/numLines);
					
					StringBuffer reacNamesBfr = new StringBuffer();
					StringBuffer prodNamesBfr = new StringBuffer();
					StringBuffer rxnNamesBfr = new StringBuffer();
					
					String [] dataArray = reader.readNext();
					for (int s = 0; s < dataArray.length; s++) {
						if (dataArray[s].length() > 0 && dataArray[s].substring(0,1).matches("\"")) {
							dataArray[s] = dataArray[s].substring(1, (dataArray[s].length() - 1));			
						}
					}
					
					if (i >= (row + correction)) {
						String knockout = GraphicalInterfaceConstants.KO_DEFAULT;
						Double fluxValue = GraphicalInterfaceConstants.FLUX_VALUE_DEFAULT;
						String reactionAbbreviation = "";
						String reactionName = "";
						String reactionEqunAbbr = "";
						String reactionEqunNames = "";
						String reversible = "";
						Double lowerBound = GraphicalInterfaceConstants.LOWER_BOUND_DEFAULT;
						Double upperBound =	GraphicalInterfaceConstants.UPPER_BOUND_DEFAULT;
						Double biologicalObjective = GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_DEFAULT;
						Double syntheticObjective = GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_DEFAULT;
						String geneAssociations = "";
						String meta1 = "";
						String meta2 = "";
						String meta3 = "";
						String meta4 = "";
						String meta5 = "";
						String meta6 = "";
						String meta7 = "";
						String meta8 = "";
						String meta9 = "";
						String meta10 = "";
						String meta11 = "";
						String meta12 = "";
						String meta13 = "";
						String meta14 = "";
						String meta15 = "";
                        
						if (LocalConfig.getInstance().getKnockoutColumnIndex() > -1) {
							if (dataArray[LocalConfig.getInstance().getKnockoutColumnIndex()].compareTo("false") == 0 || dataArray[LocalConfig.getInstance().getKnockoutColumnIndex()].compareTo("FALSE") == 0 || dataArray[LocalConfig.getInstance().getKnockoutColumnIndex()].compareTo("0") == 0 || dataArray[LocalConfig.getInstance().getKnockoutColumnIndex()].compareTo("0.0") == 0) {
								knockout = "false";
							} else if (dataArray[LocalConfig.getInstance().getKnockoutColumnIndex()].compareTo("true") == 0 || dataArray[LocalConfig.getInstance().getKnockoutColumnIndex()].compareTo("TRUE") == 0 || dataArray[LocalConfig.getInstance().getKnockoutColumnIndex()].compareTo("1") == 0 || dataArray[LocalConfig.getInstance().getKnockoutColumnIndex()].compareTo("1.0") == 0) {
								knockout = "true";													
							} 
						} 
						if (LocalConfig.getInstance().getFluxValueColumnIndex() > -1) {
							if (isNumber(dataArray[LocalConfig.getInstance().getFluxValueColumnIndex()])) {
								fluxValue = Double.valueOf(dataArray[LocalConfig.getInstance().getFluxValueColumnIndex()]);
							} 
						} 
						
						reactionAbbreviation = dataArray[LocalConfig.getInstance().getReactionAbbreviationColumnIndex()];
						
						if (LocalConfig.getInstance().getReactionNameColumnIndex() > -1) {
							reactionName = dataArray[LocalConfig.getInstance().getReactionNameColumnIndex()];
						}
								
						reactionEqunAbbr = dataArray[LocalConfig.getInstance().getReactionEquationColumnIndex()];
						reactionEqunAbbr = reactionEqunAbbr.trim();
						
						if (LocalConfig.getInstance().getReversibleColumnIndex() > -1) {
							if (dataArray[LocalConfig.getInstance().getReversibleColumnIndex()].compareTo("false") == 0 || dataArray[LocalConfig.getInstance().getReversibleColumnIndex()].compareTo("FALSE") == 0 || dataArray[LocalConfig.getInstance().getReversibleColumnIndex()].compareTo("0") == 0 || dataArray[LocalConfig.getInstance().getReversibleColumnIndex()].compareTo("0.0") == 0) {
								reversible = "false";
							} else if (dataArray[LocalConfig.getInstance().getReversibleColumnIndex()].compareTo("true") == 0 || dataArray[LocalConfig.getInstance().getReversibleColumnIndex()].compareTo("TRUE") == 0 || dataArray[LocalConfig.getInstance().getReversibleColumnIndex()].compareTo("1") == 0 || dataArray[LocalConfig.getInstance().getReversibleColumnIndex()].compareTo("1.0") == 0) {
								reversible = "true";
							} else {
								//if reversible field contains a value it is used, otherwise determined by arrow in reaction 
								if (dataArray[LocalConfig.getInstance().getReversibleColumnIndex()].length() > 0) {
									reversible = dataArray[LocalConfig.getInstance().getReversibleColumnIndex()];
								} else {
									if (reactionEqunAbbr != null) {
										if (reactionEqunAbbr.contains("<") || (reactionEqunAbbr.contains("=") && !reactionEqunAbbr.contains(">"))) {
											reversible = "true";
										} else if (reactionEqunAbbr.contains("-->") || reactionEqunAbbr.contains("->") || reactionEqunAbbr.contains("=>")) {
											reversible = "false";		    		
										}				
									} 
								}
							}
						}
						
						try {
							//ReactionParser parser = new ReactionParser();
							boolean valid = true;
							
							ArrayList<ArrayList<ArrayList<String>>> reactionList = parser.reactionList(reactionEqunAbbr.trim());
							if (parser.isValid(reactionEqunAbbr)) {
								noReactants = false;
								noProducts = false;
								ArrayList<ArrayList<String>> reactants = parser.reactionList(reactionEqunAbbr.trim()).get(0);
								// if user hits "No", reactant will not be included in equation
								ArrayList<String> removeReacList = new ArrayList<String>();
								ArrayList<String> removeProdList = new ArrayList<String>();
								//reactions of the type ==> b will be size 1, assigned the value [0] in parser			
								if (reactants.get(0).size() == 1) {
									noReactants = true;
								} else {
									for (int r = 0; r < reactants.size(); r++) {
										if (reactants.get(r).size() == 2) {											
											String stoicStr = (String) reactants.get(r).get(0);
											String reactant = (String) reactants.get(r).get(1);
											boolean newMetabolite = false;
											if (!(LocalConfig.getInstance().getMetaboliteIdNameMap().containsKey(reactant.trim()))) {
												newMetabolite = true;
												if (GraphicalInterface.showPrompt && reactant.length() > 0) {
													Object[] options = {"Yes",
															"Yes to All",
													"No"};

													
													int choice = JOptionPane.showOptionDialog(null, 
															"The metabolite " + reactant + " does not exist. Do you wish to add it?", 
															"Add Metabolite?", 
															JOptionPane.YES_NO_CANCEL_OPTION, 
															JOptionPane.QUESTION_MESSAGE, 
															null, options, options[0]);
													//options[0] sets "Yes" as default button

													// interpret the user's choice	  
													if (choice == JOptionPane.YES_OPTION)
													{
														LocalConfig.getInstance().addMetaboliteOption = true;
														addMetabPrep.setString(1, reactant);
														addMetabPrep.executeUpdate();
														maxMetabId += 1;
														LocalConfig.getInstance().getMetaboliteIdNameMap().put(reactant, new Integer(maxMetabId));
													}
													//No option actually corresponds to "Yes to All" button
													if (choice == JOptionPane.NO_OPTION)
													{
														LocalConfig.getInstance().addMetaboliteOption = true;
														GraphicalInterface.showPrompt = false;
														addMetabPrep.setString(1, reactant);
														addMetabPrep.executeUpdate();
														maxMetabId += 1;
														LocalConfig.getInstance().getMetaboliteIdNameMap().put(reactant, new Integer(maxMetabId));
													}
													//Cancel option actually corresponds to "No" button
													if (choice == JOptionPane.CANCEL_OPTION) {
														LocalConfig.getInstance().addMetaboliteOption = false;
														LocalConfig.getInstance().noButtonClicked = true;
														removeReacList.add(reactant);
													}	  
												} else {
													addMetabPrep.setString(1, reactant);
													addMetabPrep.executeUpdate();
													maxMetabId += 1;
													LocalConfig.getInstance().getMetaboliteIdNameMap().put(reactant, new Integer(maxMetabId));
												}											
											}										
											
											Integer id = (Integer) LocalConfig.getInstance().getMetaboliteIdNameMap().get(reactant);				
											String metabName = "";
											reacNamePrep.setInt(1, id);
											ResultSet rs = reacNamePrep.executeQuery();
											while (rs.next()) {
												metabName = rs.getString("metabolite_name");
											}
											rs.close();
											if (r == 0) {
												if (stoicStr.length() == 0 || Double.valueOf(stoicStr) == 1.0) {
													if (metabName != null && metabName.trim().length() > 0) {
														reacNamesBfr.append(metabName);
													} else {
														reacNamesBfr.append(reactant);
													}									
												} else {
													if (metabName != null && metabName.trim().length() > 0) {
														reacNamesBfr.append(stoicStr + " " + metabName);
													} else {
														reacNamesBfr.append(stoicStr + " " + reactant);
													}									
												}

											} else {
												if (stoicStr.length() == 0 || Double.valueOf(stoicStr) == 1.0) {
													if (metabName != null && metabName.trim().length() > 0) {
														reacNamesBfr.append(" + " + metabName);
													} else {
														reacNamesBfr.append(" + " + reactant);
													}
													
												} else {
													if (metabName != null && metabName.trim().length() > 0) {
														reacNamesBfr.append(" + " + stoicStr + " " + metabName);
													} else {
														reacNamesBfr.append(" + " + stoicStr + " " + reactant);
													}
												}				
											}			
											if (!newMetabolite || LocalConfig.getInstance().addMetaboliteOption) {
												rrInsertPrep.setInt(1, i - correction);
												rrInsertPrep.setDouble(2, Double.valueOf(stoicStr));
												rrInsertPrep.setInt(3, id);
												rrInsertPrep.executeUpdate();
												if (parser.isSuspicious(reactant)) {
													if (!LocalConfig.getInstance().getSuspiciousMetabolites().contains(id)) {
														LocalConfig.getInstance().getSuspiciousMetabolites().add(id);
													}							
												}
												if (LocalConfig.getInstance().getMetaboliteUsedMap().containsKey(reactant)) {
													int usedCount = (Integer) LocalConfig.getInstance().getMetaboliteUsedMap().get(reactant);
													LocalConfig.getInstance().getMetaboliteUsedMap().put(reactant, new Integer(usedCount + 1));									
												} else {
													LocalConfig.getInstance().getMetaboliteUsedMap().put(reactant, new Integer(1));
												}	
											}
										} else {
											//Invalid reaction
											valid = false;
											break;
										}								
									}
								}
										
								//reactions of the type a ==> will be size 1, assigned the value [0] in parser
								ArrayList<ArrayList<String>> products = parser.reactionList(reactionEqunAbbr.trim()).get(1);
								if (products.get(0).size() == 1) {
									noProducts = true;
								} else {
									for (int p = 0; p < products.size(); p++) {
										if (products.get(p).size() == 2) {
											String stoicStr = (String) products.get(p).get(0);
											String product = (String) products.get(p).get(1);	
											boolean newMetabolite = false;
											if (!(LocalConfig.getInstance().getMetaboliteIdNameMap().containsKey(product))) {
												newMetabolite = true;
												if (GraphicalInterface.showPrompt && product.length() > 0) {
													Object[] options = {"Yes",
															"Yes to All",
													"No"};

													int choice = JOptionPane.showOptionDialog(null, 
															"The metabolite " + product + " does not exist. Do you wish to add it?", 
															"Add Metabolite?", 
															JOptionPane.YES_NO_CANCEL_OPTION, 
															JOptionPane.QUESTION_MESSAGE, 
															null, options, options[0]);
													//options[0] sets "Yes" as default button

													// interpret the user's choice	  
													if (choice == JOptionPane.YES_OPTION)
													{
														LocalConfig.getInstance().addMetaboliteOption = true;
														addMetabPrep.setString(1, product);
														addMetabPrep.executeUpdate();
														maxMetabId += 1;
														LocalConfig.getInstance().getMetaboliteIdNameMap().put(product, new Integer(maxMetabId));
													}
													//No option actually corresponds to "Yes to All" button
													if (choice == JOptionPane.NO_OPTION)
													{
														LocalConfig.getInstance().addMetaboliteOption = true;
														GraphicalInterface.showPrompt = false;
														addMetabPrep.setString(1, product);
														addMetabPrep.executeUpdate();
														maxMetabId += 1;
														LocalConfig.getInstance().getMetaboliteIdNameMap().put(product, new Integer(maxMetabId));
													}
													//Cancel option actually corresponds to "No" button
													if (choice == JOptionPane.CANCEL_OPTION) {
														LocalConfig.getInstance().addMetaboliteOption = false;
														LocalConfig.getInstance().noButtonClicked = true;
														removeProdList.add(product);
													}	  
												} else {
													addMetabPrep.setString(1, product);
													addMetabPrep.executeUpdate();
													maxMetabId += 1;
													LocalConfig.getInstance().getMetaboliteIdNameMap().put(product, new Integer(maxMetabId));
												}		
											}
											
											Integer id = (Integer) LocalConfig.getInstance().getMetaboliteIdNameMap().get(product);											
											String metabName = "";
											reacNamePrep.setInt(1, id);
											ResultSet rs = reacNamePrep.executeQuery();
											while (rs.next()) {
												metabName = rs.getString("metabolite_name");
											}
											rs.close();
											if (p == 0) {
												if (stoicStr.length() == 0 || Double.valueOf(stoicStr) == 1.0) {
													if (metabName != null && metabName.trim().length() > 0) {
														prodNamesBfr.append(metabName);
													} else {
														prodNamesBfr.append(product);
													}									
												} else {
													if (metabName != null && metabName.trim().length() > 0) {
														prodNamesBfr.append(stoicStr + " " + metabName);
													} else {
														prodNamesBfr.append(stoicStr + " " + product);
													}									
												}

											} else {
												if (stoicStr.length() == 0 || Double.valueOf(stoicStr) == 1.0) {
													if (metabName != null && metabName.trim().length() > 0) {
														prodNamesBfr.append(" + " + metabName);
													} else {
														prodNamesBfr.append(" + " + product);
													}
													
												} else {
													if (metabName != null && metabName.trim().length() > 0) {
														prodNamesBfr.append(" + " + stoicStr + " " + metabName);
													} else {
														prodNamesBfr.append(" + " + stoicStr + " " + product);
													}
												}				
											}			
											if (!newMetabolite || LocalConfig.getInstance().addMetaboliteOption) {
												rpInsertPrep.setInt(1, i - correction);
												rpInsertPrep.setDouble(2, Double.valueOf(stoicStr));
												rpInsertPrep.setInt(3, id);
												rpInsertPrep.executeUpdate();	
												if (parser.isSuspicious(product)) {
													if (!LocalConfig.getInstance().getSuspiciousMetabolites().contains(id)) {
														LocalConfig.getInstance().getSuspiciousMetabolites().add(id);
													}							
												}
												if (LocalConfig.getInstance().getMetaboliteUsedMap().containsKey(product)) {
													int usedCount = (Integer) LocalConfig.getInstance().getMetaboliteUsedMap().get(product);
													LocalConfig.getInstance().getMetaboliteUsedMap().put(product, new Integer(usedCount + 1));									
												} else {
													LocalConfig.getInstance().getMetaboliteUsedMap().put(product, new Integer(1));
												}
											}										
											
										} else {
											//Invalid reaction
											valid = false;
											break;											
										}
									}							
								}
								// revise reaction equation if "No" button clicked
								if (valid && LocalConfig.getInstance().noButtonClicked) {
									String revisedReactants = "";
									String revisedProducts = "";
									String revisedReaction = "";
									if (!noReactants) {
										revisedReactants = revisedReactants(reactants, removeReacList);
									}									
									String splitString = parser.splitString(reactionEqunAbbr);
									if (!noProducts) {
										revisedProducts = revisedProducts(products, removeProdList);
									}									
									revisedReaction = revisedReactants + " " + splitString + revisedProducts;
									// prevents reaction equation from appearing as only an arrow such as ==>
									if (revisedReaction.trim().compareTo(splitString.trim()) != 0) {
										reactionEqunAbbr = revisedReaction.trim();
									} else {
										reactionEqunAbbr = "";
									}									
								}								
							} else {
								//Invalid reaction
								valid = false;
							}
														
							if (!valid) {
								deleteReacPrep.setInt(1, i - correction);
								deleteReacPrep.executeUpdate();
								deleteProdPrep.setInt(1, i - correction);
								deleteProdPrep.executeUpdate();
								if (reactionEqunAbbr != null && reactionEqunAbbr.length() > 0) {
									LocalConfig.getInstance().getInvalidReactions().add(reactionEqunAbbr);
								}
							}										
						} catch (Throwable t) {
							
						}
						
						if (reversible == "false") {
							rxnNamesBfr.append(reacNamesBfr).append(" --> ").append(prodNamesBfr);
						} else {
							rxnNamesBfr.append(reacNamesBfr).append(" <==> ").append(prodNamesBfr);
						}

						reactionEqunNames = rxnNamesBfr.toString().trim();
						
						if (LocalConfig.getInstance().getLowerBoundColumnIndex() > -1) {
							if (isNumber(dataArray[LocalConfig.getInstance().getLowerBoundColumnIndex()])) {
								lowerBound = Double.valueOf(dataArray[LocalConfig.getInstance().getLowerBoundColumnIndex()]);							
							} 
						} 
						if (LocalConfig.getInstance().getUpperBoundColumnIndex() > -1) {
							if (isNumber(dataArray[LocalConfig.getInstance().getUpperBoundColumnIndex()])) {
								upperBound = Double.valueOf(dataArray[LocalConfig.getInstance().getUpperBoundColumnIndex()]);							
							}
						} 
						if (LocalConfig.getInstance().getBiologicalObjectiveColumnIndex() > -1) {
							if (isNumber(dataArray[LocalConfig.getInstance().getBiologicalObjectiveColumnIndex()])) {
								biologicalObjective = Double.valueOf(dataArray[LocalConfig.getInstance().getBiologicalObjectiveColumnIndex()]);							
							} 							
						} 
						if (LocalConfig.getInstance().getSyntheticObjectiveColumnIndex() > -1) {
							if (isNumber(dataArray[LocalConfig.getInstance().getSyntheticObjectiveColumnIndex()])) {
								syntheticObjective = Double.valueOf(dataArray[LocalConfig.getInstance().getSyntheticObjectiveColumnIndex()]);							
							} 							
						} 
						if (LocalConfig.getInstance().getGeneAssociationColumnIndex() > -1) {
							geneAssociations = dataArray[LocalConfig.getInstance().getGeneAssociationColumnIndex()];						 							
						} 
						
						if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 0) {
							meta1 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(0)];						
						}
						if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 1) {
							meta2 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(1)];
						}
						if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 2) {
							meta3 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(2)];
						}
						if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 3) {
							meta4 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(3)];
						}
						if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 4) {
							meta5 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(4)];
						}
						if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 5) {
							meta6 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(5)];
						}
						if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 6) {
							meta7 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(6)];
						}
						if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 7) {
							meta8 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(7)];
						}
						if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 8) {
							meta9 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(8)];
						}
						if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 9) {
							meta10 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(9)];
						}
						if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 10) {
							meta11 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(10)];
						}
						if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 11) {
							meta12 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(11)];
						}
						if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 12) {
							meta13 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(12)];
						}
						if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 13) {
							meta14 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(13)];
						}
						if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 14) {
							meta15 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(14)];
						}

						// TODO : add error message here?
						if (lowerBound < 0.0 && reversible.equals("false")) {
							lowerBound = 0.0;
						}
						
						reacInsertPrep.setString(1, knockout);
						reacInsertPrep.setDouble(2, fluxValue);
						reacInsertPrep.setString(3, reactionAbbreviation);
						reacInsertPrep.setString(4, reactionName);
						reacInsertPrep.setString(5, reactionEqunAbbr);
						reacInsertPrep.setString(6, reactionEqunNames);
						reacInsertPrep.setString(7, reversible);
						reacInsertPrep.setDouble(8, lowerBound);
						reacInsertPrep.setDouble(9, upperBound);
						reacInsertPrep.setDouble(10, biologicalObjective);
						reacInsertPrep.setDouble(11, syntheticObjective);
						reacInsertPrep.setString(12, geneAssociations);
						reacInsertPrep.setString(13, meta1);
						reacInsertPrep.setString(14, meta2);
						reacInsertPrep.setString(15, meta3);
						reacInsertPrep.setString(16, meta4);
						reacInsertPrep.setString(17, meta5);
						reacInsertPrep.setString(18, meta6);
						reacInsertPrep.setString(19, meta7);
						reacInsertPrep.setString(20, meta8);
						reacInsertPrep.setString(21, meta9);
						reacInsertPrep.setString(22, meta10);
						reacInsertPrep.setString(23, meta11);
						reacInsertPrep.setString(24, meta12);
						reacInsertPrep.setString(25, meta13);
						reacInsertPrep.setString(26, meta14);
						reacInsertPrep.setString(27, meta15);
						
						reacInsertPrep.executeUpdate();
					}
					LocalConfig.getInstance().noButtonClicked = false;
				}
				stat.executeUpdate("COMMIT");
			} catch (Exception e) {
				stat.executeUpdate("ROLLBACK"); // throw away all updates since BEGIN TRANSACTION
			}

			conn.close();
			LocalConfig.getInstance().setProgress(100);	
			
		}catch(SQLException e){

			e.printStackTrace();

		}
		GraphicalInterface.showPrompt = true;
		LocalConfig.getInstance().hasMetabolitesFile = false;
	}
	
	// methods used if "No" button is pressed in order to reconstruct reaction equation with species omitted
	public String revisedReactants(ArrayList<ArrayList<String>> reactants, ArrayList<String> removeReacList) {
		String revisedReactants = "";
		StringBuffer reacBfr = new StringBuffer();
		for (int r = 0; r < reactants.size(); r++) {
			String stoicStr = (String) reactants.get(r).get(0);
			String reactant = (String) reactants.get(r).get(1);
			if (!removeReacList.contains(reactant)) {
				if (r == 0) {
					if (Double.valueOf(stoicStr) == 1) {
						reacBfr.append(reactant);
					} else {
						reacBfr.append(stoicStr + " " + reactant);
					}		
				} else {
					if (Double.valueOf(stoicStr) == 1) {
						reacBfr.append(" + " + reactant);
					} else {
						reacBfr.append(" + " + stoicStr + " " + reactant);
					}				
				}
			}								
		}
		revisedReactants = reacBfr.toString();
		if (revisedReactants.startsWith(" + ")) {
			revisedReactants = revisedReactants.substring(3);
		}
		
		return revisedReactants;
	}
	
	public String revisedProducts(ArrayList<ArrayList<String>> products, ArrayList<String> removeProdList) {
		String revisedProducts = "";
		StringBuffer prodBfr = new StringBuffer();
		for (int r = 0; r < products.size(); r++) {
			String stoicStr = (String) products.get(r).get(0);
			String product = (String) products.get(r).get(1);
			if (!removeProdList.contains(product)) {
				if (r == 0) {
					if (Double.valueOf(stoicStr) == 1) {
						prodBfr.append(product);
					} else {
						prodBfr.append(stoicStr + " " + product);
					}		
				} else {
					if (Double.valueOf(stoicStr) == 1) {
						prodBfr.append(" + " + product);
					} else {
						prodBfr.append(" + " + stoicStr + " " + product);
					}				
				}
			}						
		}
		revisedProducts = prodBfr.toString();
		if (revisedProducts.startsWith(" + ")) {
			revisedProducts = revisedProducts.substring(3);
		}
				
		return revisedProducts;
	}
	
	public boolean isNumber(String s) {
		try {
			Double.parseDouble(s);
		}
		catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
	
}



