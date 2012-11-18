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
import edu.rutgers.MOST.logic.ReactionParser1;
import edu.rutgers.MOST.presentation.GraphicalInterface;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;

public class TextReactionsModelReader {
	
	boolean addMetaboliteOption = true;
	
	public ArrayList<String> columnNamesFromFile(File file, int row) {
		ArrayList<String> columnNamesFromFile = new ArrayList();
		
		String[] dataArray = null;

		//use fileReader to read first line to get headers
		BufferedReader CSVFile;
		try {
			CSVFile = new BufferedReader(new FileReader(file));
			String dataRow = CSVFile.readLine();

			if ((GraphicalInterface.getSplitCharacter().compareTo(',')) == 0) {
				dataArray = dataRow.split(",");				
			} 

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

					if ((GraphicalInterface.getSplitCharacter().compareTo(',')) == 0) {
						dataArray = dataRow.split(",");				
					} 
					
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
			reader = new CSVReader(new FileReader(file), GraphicalInterface.getSplitCharacter());
			String [] dataArray;
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
		//if first row of file in not column names, starts reading after row that contains names
		int correction = LocalConfig.getInstance().getReactionsNextRowCorrection();
		int row = 1;
		int maxMetabId = LocalConfig.getInstance().getMaxMetaboliteId();

		LocalConfig.getInstance().getMetaboliteUsedMap().clear();
		
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

			stat.executeUpdate("drop table if exists reactions;");
			stat.executeUpdate("create table reactions (id INTEGER PRIMARY KEY, " 
					+ " knockout varchar(6), flux_value double, reaction_abbreviation varchar(40), reaction_name varchar(500), "
					+ " reaction_string varchar(500), reversible varchar(6), lower_bound double, " 
					+ " upper_bound double, biological_objective double, meta_1 varchar(500), " 
					+ " meta_2 varchar(500), meta_3 varchar(500), meta_4 varchar(500), meta_5 varchar(500), "
					+ " meta_6 varchar(500), meta_7 varchar(500), meta_8 varchar(500), meta_9 varchar(500), "
					+ " meta_10 varchar(500), meta_11 varchar(500), meta_12 varchar(500), "
					+ " meta_13 varchar(500), meta_14 varchar(500), meta_15 varchar(500));");
			
			stat.executeUpdate("drop table if exists reaction_reactants;");
			stat.executeUpdate("CREATE TABLE reaction_reactants (reaction_id INTEGER, " 
					+ " metabolite_id INTEGER, stoic FLOAT);");

			stat.executeUpdate("drop table if exists reaction_products;");
			stat.executeUpdate("CREATE TABLE reaction_products (reaction_id INTEGER, " 
					+ " metabolite_id INTEGER, stoic FLOAT);");
			
			CSVReader reader;
			try {
				reader = new CSVReader(new FileReader(file), GraphicalInterface.getSplitCharacter());
				String [] dataArray;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				reader = new CSVReader(new FileReader(file), GraphicalInterface.getSplitCharacter());
				
				int numLines = numberOfLines(file);
				
				stat.executeUpdate("BEGIN TRANSACTION");	
				for (int i = 0; i < numLines; i++) {
					LocalConfig.getInstance().setProgress(i*100/numLines);
					
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
						String reactionString = "";
						String reversible = "";
						Double lowerBound = GraphicalInterfaceConstants.LOWER_BOUND_DEFAULT;
						Double upperBound =	GraphicalInterfaceConstants.UPPER_BOUND_DEFAULT;
						Double objective = GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_DEFAULT;
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
							if (dataArray[LocalConfig.getInstance().getFluxValueColumnIndex()].compareTo("") != 0) {
								fluxValue = Double.valueOf(dataArray[LocalConfig.getInstance().getFluxValueColumnIndex()]);
							} 
						} 
						
						//if strings contain ' (single quote), it will not execute insert statement
						//this code escapes ' as '' - sqlite syntax for escaping '
						if (dataArray[LocalConfig.getInstance().getReactionAbbreviationColumnIndex()].contains("'")) {
							reactionAbbreviation = dataArray[LocalConfig.getInstance().getReactionAbbreviationColumnIndex()].replaceAll("'", "''");
						} else {
							reactionAbbreviation = dataArray[LocalConfig.getInstance().getReactionAbbreviationColumnIndex()];
						}
												
						if (dataArray[LocalConfig.getInstance().getReactionNameColumnIndex()].contains("'")) {
							reactionName = dataArray[LocalConfig.getInstance().getReactionNameColumnIndex()].replaceAll("'", "''");
						} else {
							reactionName = dataArray[LocalConfig.getInstance().getReactionNameColumnIndex()];
						}	
						
						if (dataArray[LocalConfig.getInstance().getReactionEquationColumnIndex()].contains("'")) {
							reactionString = dataArray[LocalConfig.getInstance().getReactionEquationColumnIndex()].replaceAll("'", "''");
						} else {
							reactionString = dataArray[LocalConfig.getInstance().getReactionEquationColumnIndex()];
						}
						reactionString = reactionString.trim();
						
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
									if (reactionString != null) {
										if (reactionString.contains("<") || (reactionString.contains("=") && !reactionString.contains(">"))) {
											reversible = "true";
										} else if (reactionString.contains("-->") || reactionString.contains("->") || reactionString.contains("=>")) {
											reversible = "false";		    		
										}				
									} 
								}
								
							}
						}
						
						try {
							ReactionParser1 parser = new ReactionParser1();
							boolean valid = true;
							
							if (parser.isValid(reactionString)) {
								ArrayList<ArrayList> reactants = parser.reactionList(reactionString.trim()).get(0);
								//reactions of the type ==> b will be size 1, assigned the value [0] in parser
								if (reactants.get(0).size() == 1) {
								} else {
									for (int r = 0; r < reactants.size(); r++) {
										if (reactants.get(r).size() == 2) {
											String stoicStr = (String) reactants.get(r).get(0);
											String reactant = (String) reactants.get(r).get(1);
											String addMetab = "insert into metabolites (metabolite_abbreviation, boundary) values('"  + reactant + "', 'false');";	
											
											if (!(LocalConfig.getInstance().getMetaboliteIdNameMap().containsKey(reactant.trim()))) {
												if (GraphicalInterface.showPrompt) {
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
														stat.executeUpdate(addMetab);
														maxMetabId += 1;
														LocalConfig.getInstance().getMetaboliteIdNameMap().put(reactant, new Integer(maxMetabId));
													}
													//No option actually corresponds to "Yes to All" button
													if (choice == JOptionPane.NO_OPTION)
													{
														GraphicalInterface.showPrompt = false;
														stat.executeUpdate(addMetab);
														maxMetabId += 1;
														LocalConfig.getInstance().getMetaboliteIdNameMap().put(reactant, new Integer(maxMetabId));
													}
													//Cancel option actually corresponds to "No" button
													if (choice == JOptionPane.CANCEL_OPTION) {
														addMetaboliteOption = false;
														reactionString = "";
														valid = false;
													}	  
												} else {
													stat.executeUpdate(addMetab);
													maxMetabId += 1;
													LocalConfig.getInstance().getMetaboliteIdNameMap().put(reactant, new Integer(maxMetabId));
												}											
											}										
											
											Integer id = (Integer) LocalConfig.getInstance().getMetaboliteIdNameMap().get(reactant);
											
											String insert = "INSERT INTO reaction_reactants(reaction_id, stoic, metabolite_id) values (" + (i - correction) + ", " + stoicStr + ", " + id + ");";
											stat.executeUpdate(insert);
											if (LocalConfig.getInstance().getMetaboliteUsedMap().containsKey(reactant)) {
												int usedCount = (Integer) LocalConfig.getInstance().getMetaboliteUsedMap().get(reactant);
												LocalConfig.getInstance().getMetaboliteUsedMap().put(reactant, new Integer(usedCount + 1));									
											} else {
												LocalConfig.getInstance().getMetaboliteUsedMap().put(reactant, new Integer(1));
											}	
											
										} else {
											//Invalid reaction
											valid = false;
											break;
										}								
									}
								}
								//reactions of the type a ==> will be size 1, assigned the value [0] in parser
								ArrayList<ArrayList> products = parser.reactionList(reactionString.trim()).get(1);
								if (products.get(0).size() == 1) {
								} else {
									for (int p = 0; p < products.size(); p++) {
										if (products.get(p).size() == 2) {
											String stoicStr = (String) products.get(p).get(0);
											String product = (String) products.get(p).get(1);
											String addMetab = "insert into metabolites (metabolite_abbreviation, boundary) values('"  + product + "', 'false');";
											
											if (!(LocalConfig.getInstance().getMetaboliteIdNameMap().containsKey(product))) {
												if (GraphicalInterface.showPrompt) {
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
														stat.executeUpdate(addMetab);
														maxMetabId += 1;
														LocalConfig.getInstance().getMetaboliteIdNameMap().put(product, new Integer(maxMetabId));
													}
													//No option actually corresponds to "Yes to All" button
													if (choice == JOptionPane.NO_OPTION)
													{
														GraphicalInterface.showPrompt = false;
														stat.executeUpdate(addMetab);
														maxMetabId += 1;
														LocalConfig.getInstance().getMetaboliteIdNameMap().put(product, new Integer(maxMetabId));
													}
													//Cancel option actually corresponds to "No" button
													if (choice == JOptionPane.CANCEL_OPTION) {
														addMetaboliteOption = false;
														reactionString = "";
														valid = false;
													}	  
												} else {
													stat.executeUpdate(addMetab);
													maxMetabId += 1;
													LocalConfig.getInstance().getMetaboliteIdNameMap().put(product, new Integer(maxMetabId));
												}		
											}
											
											Integer id = (Integer) LocalConfig.getInstance().getMetaboliteIdNameMap().get(product);
											
											String insert = "INSERT INTO reaction_products(reaction_id, stoic, metabolite_id) values (" + (i - correction) + ", " + stoicStr + ", " + id + ");";
											stat.executeUpdate(insert);	
											if (LocalConfig.getInstance().getMetaboliteUsedMap().containsKey(product)) {
												int usedCount = (Integer) LocalConfig.getInstance().getMetaboliteUsedMap().get(product);
												LocalConfig.getInstance().getMetaboliteUsedMap().put(product, new Integer(usedCount + 1));									
											} else {
												LocalConfig.getInstance().getMetaboliteUsedMap().put(product, new Integer(1));
											}
											
										} else {
											//Invalid reaction
											valid = false;
											break;
										}
									}							
								}
							} else {
								//Invalid reaction
								valid = false;
							}
							
							
							if (!valid) {
								String deleteReac = "delete from reaction_reactants where reaction_id=" + (i - correction) + ";";
								stat.executeUpdate(deleteReac);
								String deleteProd = "delete from reaction_products where reaction_id=" + (i - correction) + ";";
								stat.executeUpdate(deleteProd);
								if (reactionString != null || reactionString.length() > 0) {
									LocalConfig.getInstance().getInvalidReactions().add(reactionString);
								}	
							}										
						} catch (Throwable t) {
							
						}
						
						if (LocalConfig.getInstance().getLowerBoundColumnIndex() > -1) {
							if (dataArray[LocalConfig.getInstance().getLowerBoundColumnIndex()].compareTo("") != 0) {
								lowerBound = Double.valueOf(dataArray[LocalConfig.getInstance().getLowerBoundColumnIndex()]);							
							} 
						} 
						if (LocalConfig.getInstance().getUpperBoundColumnIndex() > -1) {
							if (dataArray[LocalConfig.getInstance().getUpperBoundColumnIndex()].compareTo("") != 0) {
								upperBound = Double.valueOf(dataArray[LocalConfig.getInstance().getUpperBoundColumnIndex()]);							
							}
						} 
						if (LocalConfig.getInstance().getBiologicalObjectiveColumnIndex() > -1) {
							if (dataArray[LocalConfig.getInstance().getBiologicalObjectiveColumnIndex()].compareTo("") != 0) {
								objective = Double.valueOf(dataArray[LocalConfig.getInstance().getBiologicalObjectiveColumnIndex()]);							
							} 							
						} 
						
						if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 0) {
							if (dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(0)].contains("'")) {
								meta1 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(0)].replaceAll("'", "''");
							} else {
								meta1 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(0)];
							}							
						}
						if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 1) {
							if (dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(0)].contains("'")) {
								meta2 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(1)].replaceAll("'", "''");
							} else {
								meta2 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(1)];
							}
						}
						if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 2) {
							if (dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(0)].contains("'")) {
								meta3 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(2)].replaceAll("'", "''");
							} else {
								meta3 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(2)];
							}
						}
						if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 3) {
							if (dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(0)].contains("'")) {
								meta4 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(3)].replaceAll("'", "''");
							} else {
								meta4 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(3)];
							}
						}
						if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 4) {
							if (dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(0)].contains("'")) {
								meta5 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(4)].replaceAll("'", "''");
							} else {
								meta5 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(4)];
							}
						}
						if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 5) {
							if (dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(0)].contains("'")) {
								meta6 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(5)].replaceAll("'", "''");
							} else {
								meta6 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(5)];
							}
						}
						if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 6) {
							if (dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(0)].contains("'")) {
								meta7 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(6)].replaceAll("'", "''");
							} else {
								meta7 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(6)];
							}
						}
						if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 7) {
							if (dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(0)].contains("'")) {
								meta8 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(7)].replaceAll("'", "''");
							} else {
								meta8 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(7)];
							}
						}
						if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 8) {
							if (dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(0)].contains("'")) {
								meta9 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(8)].replaceAll("'", "''");
							} else {
								meta9 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(8)];
							}
						}
						if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 9) {
							if (dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(0)].contains("'")) {
								meta10 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(9)].replaceAll("'", "''");
							} else {
								meta10 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(9)];
							}
						}
						if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 10) {
							if (dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(0)].contains("'")) {
								meta11 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(10)].replaceAll("'", "''");
							} else {
								meta11 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(10)];
							}
						}
						if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 11) {
							if (dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(0)].contains("'")) {
								meta12 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(11)].replaceAll("'", "''");
							} else {
								meta12 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(11)];
							}
						}
						if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 12) {
							if (dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(0)].contains("'")) {
								meta13 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(12)].replaceAll("'", "''");
							} else {
								meta13 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(12)];
							}
						}
						if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 13) {
							if (dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(0)].contains("'")) {
								meta14 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(13)].replaceAll("'", "''");
							} else {
								meta14 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(13)];
							}
						}
						if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 14) {
							if (dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(0)].contains("'")) {
								meta15 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(14)].replaceAll("'", "''");
							} else {
								meta15 = dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(14)];
							}
						}
						
						String insert = "INSERT INTO reactions(knockout, flux_value, reaction_abbreviation, " 
							+ " reaction_name, reaction_string, reversible, lower_bound, upper_bound, biological_objective," 
							+ " meta_1, meta_2, meta_3, meta_4, meta_5, meta_6, meta_7, meta_8, "
							+ " meta_9, meta_10, meta_11, meta_12, meta_13, meta_14, meta_15) values " 
							+ " (" + "'" + knockout + "', '" + fluxValue + "', '" + reactionAbbreviation + "', '" + reactionName + "', '" + reactionString + "', '" + reversible + "', '" + lowerBound + "', '" + upperBound + "', '" + objective + "', '" + meta1 + "', '" + meta2 + "', '" + meta3 + "', '" + meta4 + "', '" + meta5 + "', '" + meta6 + "', '" + meta7 + "', '" + meta8 + "', '" + meta9 + "', '" + meta10 + "', '" + meta11 + "', '" + meta12 + "', '" + meta13 + "', '" + meta14 + "', '" + meta15 + "');";
						stat.executeUpdate(insert);
					}					
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

		//System.out.println("Done");
	}
}



