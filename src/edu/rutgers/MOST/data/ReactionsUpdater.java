package edu.rutgers.MOST.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JOptionPane;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.logic.ReactionParser;
import edu.rutgers.MOST.presentation.GraphicalInterface;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;

public class ReactionsUpdater {

	public boolean noReactants;     // type ==> p
	public boolean noProducts;      // type r ==>
	// used if no button is hit, for building new equation with species not included
	public String reactionEqunAbbr = "";
	public String reactionEqunNames = "";
	
	public void updateReactionRows(ArrayList<Integer> rowList, ArrayList<Integer> reacIdList, ArrayList<String> oldReactionsList, String databaseName) {
			
		if (LocalConfig.getInstance().includesReactions) {
			//update MetabolitesUsedMap by decrementing count or removing metabolite
			//based on oldReactions that are being replaced
			for (int i = 0; i < oldReactionsList.size(); i++) {
				ReactionParser parser = new ReactionParser();
				if (parser.isValid(oldReactionsList.get(i))) {
				//if (parser.isValid(oldReactionsList.get(i)) && !LocalConfig.getInstance().getInvalidReactions().contains(oldReactionsList.get(i))) {
					ArrayList<ArrayList<ArrayList<String>>> oldReactionList = parser.reactionList(oldReactionsList.get(i));

					//remove old species from used map
					for (int x = 0; x < oldReactionList.size(); x++) {
						for (int y = 0; y < oldReactionList.get(x).size(); y++) {
							if (((ArrayList) oldReactionList.get(x).get(y)).size() > 1) {
								if (LocalConfig.getInstance().getMetaboliteUsedMap().get((String) ((ArrayList) oldReactionList.get(x).get(y)).get(1)) != null) {
									int usedCount = (Integer) LocalConfig.getInstance().getMetaboliteUsedMap().get((String) ((ArrayList) oldReactionList.get(x).get(y)).get(1));
									if (usedCount > 1) {
										LocalConfig.getInstance().getMetaboliteUsedMap().put((String) ((ArrayList) oldReactionList.get(x).get(y)).get(1), new Integer(usedCount - 1));
									} else {
										LocalConfig.getInstance().getMetaboliteUsedMap().remove((String) ((ArrayList) oldReactionList.get(x).get(y)).get(1));
									}
								}			
							}					
						}
					}
				}
			}
			//System.out.println("ru upd rxn old " + LocalConfig.getInstance().getMetaboliteUsedMap());
		}		
		
		String queryString = "jdbc:sqlite:" + databaseName + ".db";
		
		try{
			Connection conn =
				DriverManager.getConnection(queryString);
			Statement stat = conn.createStatement();
			PreparedStatement reacInsertPrep = conn.prepareStatement("update reactions set knockout=?, flux_value=?, " 
					+ " reaction_abbreviation=?, reaction_name=?, reaction_equn_abbr=?, reaction_equn_names=?, reversible=?, lower_bound=?, " 
					+ " upper_bound=?, biological_objective=?, synthetic_objective=?, gene_associations=?, meta_1=?, meta_2=?, meta_3=?, meta_4=?, meta_5=?, meta_6=?, "
					+ " meta_7=?, meta_8=?, meta_9=?, meta_10=?, meta_11=?, meta_12=?, meta_13=?, meta_14=?, meta_15=? where id=?"); 

			try {
				stat.executeUpdate("BEGIN TRANSACTION");

				for (int i = 0; i < rowList.size(); i++) {
					
					if (LocalConfig.getInstance().includesReactions) {
						//remove old reactions from db
						String rrUpdate = "delete from reaction_reactants where reaction_id=" + reacIdList.get(i) + ";";				
						stat.executeUpdate(rrUpdate);
						String rpUpdate = "delete from reaction_products where reaction_id=" + reacIdList.get(i) + ";";				
						stat.executeUpdate(rpUpdate);	
					}					
					
					String knockout = "false";
					if (GraphicalInterface.reactionsTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.KO_COLUMN) != null) {
						knockout = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.KO_COLUMN);
						if (knockout.length() == 0) {
							knockout = GraphicalInterfaceConstants.KO_DEFAULT;
						}
					}
					Double fluxValue = GraphicalInterfaceConstants.FLUX_VALUE_DEFAULT;
					if (GraphicalInterface.reactionsTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.FLUX_VALUE_COLUMN) != null) {
						fluxValue = Double.valueOf((String) GraphicalInterface.reactionsTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.FLUX_VALUE_COLUMN));	
					} 
					String reactionAbbreviation = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.REACTION_ABBREVIATION_COLUMN);
					if (reactionAbbreviation == null) {
						reactionAbbreviation = " ";
					}
					String reactionName = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.REACTION_NAME_COLUMN);
					if (reactionName == null) {
						reactionName = " ";
					}
					String reactionEqunAbbr = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN);
					reactionEqunNames = "test";
					//String reactionEqunNames = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.REACTION_EQUN_NAMES_COLUMN);
					String reversible = GraphicalInterfaceConstants.REVERSIBLE_DEFAULT;
					
					if (LocalConfig.getInstance().includesReactions) {
						ReactionParser parser = new ReactionParser();
						//if (reactionEqunAbbr != null) {
						if (reactionEqunAbbr != null && reactionEqunAbbr.length() > 0) {
							if (parser.isValid(reactionEqunAbbr)) {
								ArrayList<ArrayList<ArrayList<String>>> newReactionList = parser.reactionList(reactionEqunAbbr);
								
								//add new species to used map
								for (int x = 0; x < newReactionList.size(); x++) {
									for (int y = 0; y < newReactionList.get(x).size(); y++) {
										if (((ArrayList) newReactionList.get(x).get(y)).size() > 1) {

											if (LocalConfig.getInstance().getMetaboliteUsedMap().containsKey((String) ((ArrayList) newReactionList.get(x).get(y)).get(1))) {
												if (LocalConfig.getInstance().getMetaboliteUsedMap().get((String) ((ArrayList) newReactionList.get(x).get(y)).get(1)) != null) {
													int usedCount = (Integer) LocalConfig.getInstance().getMetaboliteUsedMap().get((String) ((ArrayList) newReactionList.get(x).get(y)).get(1));
													LocalConfig.getInstance().getMetaboliteUsedMap().put((String) ((ArrayList) newReactionList.get(x).get(y)).get(1), new Integer(usedCount + 1));
												}									
											} else {
												LocalConfig.getInstance().getMetaboliteUsedMap().put((String) ((ArrayList) newReactionList.get(x).get(y)).get(1), new Integer(1));
											}
											Integer metabId = (Integer) LocalConfig.getInstance().getMetaboliteIdNameMap().get((String) ((ArrayList) newReactionList.get(x).get(y)).get(1));
											String stoic = ((String) ((ArrayList) newReactionList.get(x).get(y)).get(0));
											if (x == 0) {//reactants
												String rrUpdate2 = "insert into reaction_reactants (reaction_id, metabolite_id, stoic) values (" + reacIdList.get(i) + ", " + metabId + ", " + stoic + ");";				
												stat.executeUpdate(rrUpdate2);				
											}
											if (x == 1) {//products
												String rpUpdate2 = "insert into reaction_products (reaction_id, metabolite_id, stoic) values (" + reacIdList.get(i) + ", " + metabId + ", " + stoic + ");";	
												stat.executeUpdate(rpUpdate2);
											}
										}							
									}
								}
								
							}
							//System.out.println("ru upd rxn new " + LocalConfig.getInstance().getMetaboliteUsedMap());													
						} else {
							reactionEqunAbbr = " ";
						}
						
						if (reactionEqunAbbr != null) {
							if (reactionEqunAbbr.contains("<") || (reactionEqunAbbr.contains("=") && !reactionEqunAbbr.contains(">"))) {
								reversible = "true";
							} else if (reactionEqunAbbr.contains("-->") || reactionEqunAbbr.contains("->") || reactionEqunAbbr.contains("=>")) {
								reversible = "false";		    		
							}
						}			
					}							
					
					Double lowerBound = GraphicalInterfaceConstants.LOWER_BOUND_DEFAULT;
					if (GraphicalInterface.reactionsTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.LOWER_BOUND_COLUMN) != null) {
						lowerBound = Double.valueOf((String) GraphicalInterface.reactionsTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.LOWER_BOUND_COLUMN));				
					} 
					Double upperBound = GraphicalInterfaceConstants.UPPER_BOUND_DEFAULT;
					if (GraphicalInterface.reactionsTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.UPPER_BOUND_COLUMN) != null) {
						upperBound = Double.valueOf((String) GraphicalInterface.reactionsTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.UPPER_BOUND_COLUMN));
				
					} 
					Double biologicalObjective = GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_DEFAULT;
					if (GraphicalInterface.reactionsTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN) != null) {
						biologicalObjective = Double.valueOf((String) GraphicalInterface.reactionsTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN));			
					} 
					Double syntheticObjective = GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_DEFAULT;
					if (GraphicalInterface.reactionsTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_COLUMN) != null) {
						syntheticObjective = Double.valueOf((String) GraphicalInterface.reactionsTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_COLUMN));			
					} 
					
					String geneAssociations = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.GENE_ASSOCIATIONS_COLUMN);
					
					String meta1 = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.REACTION_META1_COLUMN);
					if (meta1 == null) {
						meta1 = " ";
					}
					String meta2 = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.REACTION_META2_COLUMN);
					if (meta2 == null) {
						meta2 = " ";
					}
					String meta3 = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.REACTION_META3_COLUMN);
					if (meta3 == null) {
						meta3 = " ";
					}
					String meta4 = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.REACTION_META4_COLUMN);
					if (meta4 == null) {
						meta4 = " ";
					}
					String meta5 = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.REACTION_META5_COLUMN);
					if (meta5 == null) {
						meta5 = " ";
					}
					String meta6 = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.REACTION_META6_COLUMN);
					if (meta6 == null) {
						meta6 = " ";
					}
					String meta7 = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.REACTION_META7_COLUMN);
					if (meta7 == null) {
						meta7 = " ";
					}
					String meta8 = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.REACTION_META8_COLUMN);
					if (meta8 == null) {
						meta8 = " ";
					}
					String meta9 = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.REACTION_META9_COLUMN);
					if (meta9 == null) {
						meta9 = " ";
					}
					String meta10 = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.REACTION_META10_COLUMN);
					if (meta10 == null) {
						meta10 = " ";
					}
					String meta11 = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.REACTION_META11_COLUMN);
					if (meta11 == null) {
						meta11 = " ";
					}
					String meta12 = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.REACTION_META12_COLUMN);
					if (meta12 == null) {
						meta12 = " ";
					}
					String meta13 = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.REACTION_META13_COLUMN);
					if (meta13 == null) {
						meta13 = " ";
					}
					String meta14 = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.REACTION_META14_COLUMN);
					if (meta14 == null) {
						meta14 = " ";
					}
					String meta15 = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(rowList.get(i), GraphicalInterfaceConstants.REACTION_META15_COLUMN);
					if (meta15 == null) {
						meta15 = " ";
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
					reacInsertPrep.setInt(28, reacIdList.get(i));
					
					reacInsertPrep.executeUpdate();
				}
				
				stat.executeUpdate("COMMIT");
			} catch (Exception e) {
				e.printStackTrace();
				stat.executeUpdate("ROLLBACK"); // throw away all updates since BEGIN TRANSACTION
			}

			conn.close();
			
		}catch(SQLException e){

			e.printStackTrace();

		}
		GraphicalInterface.showPrompt = true;
	}
	
	//used for updating when a single row is edited, and when pasting reaction equations
	public void updateReactionEquations(int id, String oldEquation, String newEquation, String databaseName) {
		
		LocalConfig.getInstance().addMetaboliteOption = true;
		
		LocalConfig.getInstance().getAddedMetabolites().clear();
		
		ReactionParser parser = new ReactionParser();
		DatabaseCreator creator = new DatabaseCreator();

		String queryString = "jdbc:sqlite:" + databaseName + ".db";
		
		StringBuffer reacNamesBfr = new StringBuffer();
		StringBuffer prodNamesBfr = new StringBuffer();
		StringBuffer rxnNamesBfr = new StringBuffer();

		try{
			Connection conn =
				DriverManager.getConnection(queryString);
			Statement stat = conn.createStatement();		

			//update for old reaction
			if (oldEquation != null && parser.isValid(oldEquation)) {
				ArrayList<ArrayList<ArrayList<String>>> oldReactionList = parser.reactionList(oldEquation);
				//System.out.println(oldEquation);
				//System.out.println("old " + oldReactionList);
				
				//remove old species from used map
				for (int x = 0; x < oldReactionList.size(); x++) {
					for (int y = 0; y < oldReactionList.get(x).size(); y++) {
						if (((ArrayList) oldReactionList.get(x).get(y)).size() > 1) {
							if (LocalConfig.getInstance().getMetaboliteUsedMap().get((String) ((ArrayList) oldReactionList.get(x).get(y)).get(1)) != null) {
								int usedCount = (Integer) LocalConfig.getInstance().getMetaboliteUsedMap().get((String) ((ArrayList) oldReactionList.get(x).get(y)).get(1));
								if (usedCount > 1) {
									LocalConfig.getInstance().getMetaboliteUsedMap().put((String) ((ArrayList) oldReactionList.get(x).get(y)).get(1), new Integer(usedCount - 1));
								} else {
									LocalConfig.getInstance().getMetaboliteUsedMap().remove((String) ((ArrayList) oldReactionList.get(x).get(y)).get(1));
								}
							}			
						}					
					}
				}
				//System.out.println("ru ure old used " + LocalConfig.getInstance().getMetaboliteUsedMap());
			}

			try {
				stat.executeUpdate("BEGIN TRANSACTION");

				String rrUpdate = "delete from reaction_reactants where reaction_id=" + id + ";";				
				stat.executeUpdate(rrUpdate);
				String rpUpdate = "delete from reaction_products where reaction_id=" + id + ";";				
				stat.executeUpdate(rpUpdate);				
				stat.executeUpdate("COMMIT");
			} catch (Exception e) {
				e.printStackTrace();
				stat.executeUpdate("ROLLBACK"); // throw away all updates since BEGIN TRANSACTION
			}
			
			// get maximum metabolite id in case any metabolites need to be added
			// when parsing reactions
			Map<String, Object> idMap = LocalConfig.getInstance().getMetaboliteIdNameMap();
			ArrayList<Integer> idList = new ArrayList(idMap.values());
			int maxMetabId = 0;
			for (int i = 0; i < idList.size(); i++) {
				if (idList.get(i) > maxMetabId) {
					maxMetabId = idList.get(i);
				}
			}
			PreparedStatement reacNamePrep = conn.prepareStatement("SELECT metabolite_name from metabolites where id=?;");
			PreparedStatement reversiblePrep = conn.prepareStatement("SELECT reversible from reactions where id=?;");
			//update for new reaction
			try {
				boolean valid = true;
				
				if (parser.isValid(newEquation)) {
					ArrayList<ArrayList<ArrayList<String>>> reactionList = parser.reactionList(newEquation.trim());					
					// if reaction contains a prefix such as [c]: and a compartment suffix
					// such as a[c], it is invalid
					if (parser.invalidSyntax) {
					//if (parser.invalidSyntax || parser.invalidSpacing) {
						valid = false;
					} else {
						noReactants = false;
						noProducts = false;
						ArrayList<ArrayList<String>> reactants = reactionList.get(0);
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
									String update = "update metabolites set metabolite_abbreviation='" + reactant + "', boundary='false' where id=" + (maxMetabId + 1) + ";";	
									boolean newMetabolite = false;
									if (!(LocalConfig.getInstance().getMetaboliteIdNameMap().containsKey(reactant.trim()))) {
										newMetabolite = true;
										if (GraphicalInterface.showPrompt && !(GraphicalInterface.replaceAllMode && LocalConfig.getInstance().yesToAllButtonClicked)) {
											Object[] options = {"Yes",
													"Yes to All",
											"No"};
											LocalConfig.getInstance().addReactantPromptShown = true;
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
												creator.addMetaboliteRow(databaseName);
												stat.executeUpdate(update);
												LocalConfig.getInstance().getMetaboliteIdNameMap().put(reactant, (maxMetabId + 1));
												LocalConfig.getInstance().getAddedMetabolites().add((maxMetabId + 1));
												maxMetabId += 1;
											}
											//No option actually corresponds to "Yes to All" button
											if (choice == JOptionPane.NO_OPTION)
											{
												LocalConfig.getInstance().addMetaboliteOption = true;
												GraphicalInterface.showPrompt = false;
												creator.addMetaboliteRow(databaseName);
												stat.executeUpdate(update);
												LocalConfig.getInstance().getMetaboliteIdNameMap().put(reactant, (maxMetabId + 1));
												LocalConfig.getInstance().getAddedMetabolites().add((maxMetabId + 1));
												maxMetabId += 1;
												LocalConfig.getInstance().yesToAllButtonClicked = true;
											}
											//Cancel option actually corresponds to "No" button
											if (choice == JOptionPane.CANCEL_OPTION) {
												LocalConfig.getInstance().addMetaboliteOption = false;
												LocalConfig.getInstance().noButtonClicked = true;
												removeReacList.add(reactant);						
											}	  
										} else {
											creator.addMetaboliteRow(databaseName);
											stat.executeUpdate(update);
											LocalConfig.getInstance().getMetaboliteIdNameMap().put(reactant, (maxMetabId + 1));
											LocalConfig.getInstance().getAddedMetabolites().add((maxMetabId + 1));
											maxMetabId += 1;
										}
									}										
									
									Integer metabId = (Integer) LocalConfig.getInstance().getMetaboliteIdNameMap().get(reactant);									
									String metabName = "";
									reacNamePrep.setInt(1, metabId);
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
										String insert = "INSERT INTO reaction_reactants(reaction_id, stoic, metabolite_id) values (" + id + ", " + stoicStr + ", " + metabId + ");";
										stat.executeUpdate(insert);
										if (parser.isSuspicious(reactant)) {	
											if (!LocalConfig.getInstance().getSuspiciousMetabolites().contains(metabId)) {
												LocalConfig.getInstance().getSuspiciousMetabolites().add(metabId);
											}
										}
										if (LocalConfig.getInstance().pastedReaction == false) {
											if (LocalConfig.getInstance().getMetaboliteUsedMap().containsKey(reactant)) {
												int usedCount = (Integer) LocalConfig.getInstance().getMetaboliteUsedMap().get(reactant);
												LocalConfig.getInstance().getMetaboliteUsedMap().put(reactant, new Integer(usedCount + 1));									
											} else {
												LocalConfig.getInstance().getMetaboliteUsedMap().put(reactant, new Integer(1));
											}	
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
						ArrayList<ArrayList<String>> products = reactionList.get(1);
						if (products.get(0).size() == 1) {
							noProducts = true;
						} else {
							for (int p = 0; p < products.size(); p++) {
								if (products.get(p).size() == 2) {
									String stoicStr = (String) products.get(p).get(0);
									String product = (String) products.get(p).get(1);	
									//String update = "update metabolites set metabolite_abbreviation='" + product + "', boundary='false' where id=" + (LocalConfig.getInstance().getMetaboliteIdNameMap().size() + 1) + ";";	
									String update = "update metabolites set metabolite_abbreviation='" + product + "', boundary='false' where id=" + (maxMetabId + 1) + ";";
									boolean newMetabolite = false;
									if (!(LocalConfig.getInstance().getMetaboliteIdNameMap().containsKey(product))) {
										newMetabolite = true;
										// prompt only shown once in replace all mode if yes to all clicked
										// TODO should also only shown once per reaction in find mode
										if (GraphicalInterface.showPrompt && !(GraphicalInterface.replaceAllMode && LocalConfig.getInstance().yesToAllButtonClicked)) {
											Object[] options = {"Yes",
													"Yes to All",
											"No"};
											LocalConfig.getInstance().addReactantPromptShown = true;
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
												creator.addMetaboliteRow(databaseName);
												stat.executeUpdate(update);
											    LocalConfig.getInstance().getMetaboliteIdNameMap().put(product, (maxMetabId + 1));
											    LocalConfig.getInstance().getAddedMetabolites().add((maxMetabId + 1));
												maxMetabId += 1;
											}
											//No option actually corresponds to "Yes to All" button
											if (choice == JOptionPane.NO_OPTION)
											{
												LocalConfig.getInstance().addMetaboliteOption = true;
												GraphicalInterface.showPrompt = false;
												creator.addMetaboliteRow(databaseName);
												stat.executeUpdate(update);
												LocalConfig.getInstance().getMetaboliteIdNameMap().put(product, (maxMetabId + 1));
												LocalConfig.getInstance().getAddedMetabolites().add((maxMetabId + 1));
												maxMetabId += 1;
												LocalConfig.getInstance().yesToAllButtonClicked = true;
											}
											//Cancel option actually corresponds to "No" button
											if (choice == JOptionPane.CANCEL_OPTION) {
												LocalConfig.getInstance().addMetaboliteOption = false;
												LocalConfig.getInstance().noButtonClicked = true;						
												removeProdList.add(product);
											}	  
										} else {
											creator.addMetaboliteRow(databaseName);
											stat.executeUpdate(update);
											LocalConfig.getInstance().getMetaboliteIdNameMap().put(product, (maxMetabId + 1));
											LocalConfig.getInstance().getAddedMetabolites().add((maxMetabId + 1));
											maxMetabId += 1;
										}		
									}
									
									Integer metabId = (Integer) LocalConfig.getInstance().getMetaboliteIdNameMap().get(product);									
									String metabName = "";
									reacNamePrep.setInt(1, metabId);
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
										String insert = "INSERT INTO reaction_products(reaction_id, stoic, metabolite_id) values (" + id + ", " + stoicStr + ", " + metabId + ");";
										stat.executeUpdate(insert);	
										if (parser.isSuspicious(product)) {
											if (!LocalConfig.getInstance().getSuspiciousMetabolites().contains(metabId)) {
												LocalConfig.getInstance().getSuspiciousMetabolites().add(metabId);
											}							
										}
										if (LocalConfig.getInstance().pastedReaction == false) {
											if (LocalConfig.getInstance().getMetaboliteUsedMap().containsKey(product)) {
												int usedCount = (Integer) LocalConfig.getInstance().getMetaboliteUsedMap().get(product);
												LocalConfig.getInstance().getMetaboliteUsedMap().put(product, new Integer(usedCount + 1));									
											} else {
												LocalConfig.getInstance().getMetaboliteUsedMap().put(product, new Integer(1));
											}
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
							String splitString = parser.splitString(newEquation);
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
					}					
				} else {
					//Invalid reaction
					valid = false;
				}
				//System.out.println("ru ure new used " + LocalConfig.getInstance().getMetaboliteUsedMap());
				
				if (!valid) {
					String deleteReac = "delete from reaction_reactants where reaction_id=" + id + ";";
					stat.executeUpdate(deleteReac);
					String deleteProd = "delete from reaction_products where reaction_id=" + id + ";";
					stat.executeUpdate(deleteProd);
					if (newEquation != null && newEquation.trim().length() > 0) {
						LocalConfig.getInstance().getInvalidReactions().add(newEquation);
						//System.out.println("invalid " + LocalConfig.getInstance().getInvalidReactions());
					}	
				}
				parser.invalidSyntax = false;
			} catch (Throwable t) {
				
			}
			
			String reversible = "";
			reversiblePrep.setInt(1, id);
			ResultSet rs = reversiblePrep.executeQuery();
			while (rs.next()) {
				reversible = rs.getString("reversible");
			}
			rs.close();
			if (reversible == "false") {
				rxnNamesBfr.append(reacNamesBfr).append(" --> ").append(prodNamesBfr);
			} else {
				rxnNamesBfr.append(reacNamesBfr).append(" <==> ").append(prodNamesBfr);
			}

			reactionEqunNames = rxnNamesBfr.toString().trim();
			
			conn.close();
			
		}catch(SQLException e){

			e.printStackTrace();

		}
		GraphicalInterface.showPrompt = true;
	}
	
	public void deleteRows(ArrayList<Integer> idList, ArrayList<String> deletedReactions, String databaseName) {

		String queryString = "jdbc:sqlite:" + databaseName + ".db";
		
		try{
			Connection conn =
				DriverManager.getConnection(queryString);
			Statement stat = conn.createStatement();

			try {
				stat.executeUpdate("BEGIN TRANSACTION");

				for (int i = 0; i < idList.size(); i++) {
					String delete = "delete from reactions where id = " + idList.get(i) + ";";
					stat.executeUpdate(delete);
					String rrDelete = "delete from reaction_reactants where reaction_id = " + idList.get(i) + ";";
					stat.executeUpdate(rrDelete);
					String rpDelete = "delete from reaction_products where reaction_id = " + idList.get(i) + ";";
					stat.executeUpdate(rpDelete);
				}
				
				stat.executeUpdate("COMMIT");
			} catch (Exception e) {
				e.printStackTrace();
				stat.executeUpdate("ROLLBACK"); // throw away all updates since BEGIN TRANSACTION
			}

			conn.close();
			
		}catch(SQLException e){

			e.printStackTrace();

		}
		
		ReactionParser parser = new ReactionParser();
		
		for (int r = 0; r < deletedReactions.size(); r++) {
			if (parser.isValid(deletedReactions.get(r))) {
				ArrayList<ArrayList<ArrayList<String>>> oldReactionList = parser.reactionList(deletedReactions.get(r));
				//remove old species from used map
				for (int x = 0; x < oldReactionList.size(); x++) {
					for (int y = 0; y < oldReactionList.get(x).size(); y++) {
						if (((ArrayList) oldReactionList.get(x).get(y)).size() > 1) {
							if (LocalConfig.getInstance().getMetaboliteUsedMap().get((String) ((ArrayList) oldReactionList.get(x).get(y)).get(1)) != null) {
								int usedCount = (Integer) LocalConfig.getInstance().getMetaboliteUsedMap().get((String) ((ArrayList) oldReactionList.get(x).get(y)).get(1));
								if (usedCount > 1) {
									LocalConfig.getInstance().getMetaboliteUsedMap().put((String) ((ArrayList) oldReactionList.get(x).get(y)).get(1), new Integer(usedCount - 1));
								} else {
									LocalConfig.getInstance().getMetaboliteUsedMap().remove((String) ((ArrayList) oldReactionList.get(x).get(y)).get(1));
								}
							}			
						}		
					}
				}
			}			
		}
		//System.out.println("del used map" + LocalConfig.getInstance().getMetaboliteUsedMap());		
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
	
	// method used when renaming metabolites
	public void rewriteReactions(ArrayList<Integer> reactionIdList, String databaseName) {		
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
			ResultSet rsReac = null;
			ResultSet rsEqun = null;
			ResultSet rsProd = null;
			
			ReactionParser parser = new ReactionParser();
				
			try {
				stat.executeUpdate("BEGIN TRANSACTION");

				for (int i = 0; i < reactionIdList.size(); i++) {
					StringBuffer reacBfr = new StringBuffer();
					StringBuffer prodBfr = new StringBuffer();
					StringBuffer rxnBfr = new StringBuffer();
					PreparedStatement rPrep = conn.prepareStatement("select metabolite_id, stoic from reaction_reactants where reaction_id=?");
					rPrep.setInt(1, reactionIdList.get(i));
					conn.setAutoCommit(true);
					rsReac = rPrep.executeQuery();
					int r = 0;
					while(rsReac.next()) {						
						int stoicStr = rsReac.getInt("stoic");
						int reactantId = rsReac.getInt("metabolite_id");
						Object key = getKeyFromValue(LocalConfig.getInstance().getMetaboliteIdNameMap(), reactantId);
						String reactant = key.toString();
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
						r += 1;
					}
					
					PreparedStatement eqPrep = conn.prepareStatement("select reaction_equn_abbr from reactions where id=?");
					eqPrep.setInt(1, reactionIdList.get(i));
					conn.setAutoCommit(true);
					rsEqun = eqPrep.executeQuery();
					String equation = rsEqun.getString("reaction_equn_abbr");
					String splitString = parser.splitString(equation);
					
					PreparedStatement pPrep = conn.prepareStatement("select metabolite_id, stoic from reaction_products where reaction_id=?");
					pPrep.setInt(1, reactionIdList.get(i));
					conn.setAutoCommit(true);
					rsProd = pPrep.executeQuery();
					r = 0;
					while(rsProd.next()) {
						int stoicStr = rsProd.getInt("stoic");
						int productId = rsProd.getInt("metabolite_id");
						Object key = getKeyFromValue(LocalConfig.getInstance().getMetaboliteIdNameMap(), productId);
						String product = key.toString();
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
						r += 1;
					}
					rxnBfr.append(reacBfr).append(" " + splitString).append(prodBfr);
					PreparedStatement eqUpdatePrep = conn.prepareStatement("update reactions set reaction_equn_abbr=? where id=?");
					eqUpdatePrep.setString(1, rxnBfr.toString());
					eqUpdatePrep.setInt(2, reactionIdList.get(i));
					conn.setAutoCommit(true);
					eqUpdatePrep.executeUpdate();
				}
				
				stat.executeUpdate("COMMIT");
			} catch (Exception e) {
				e.printStackTrace();
				stat.executeUpdate("ROLLBACK"); // throw away all updates since BEGIN TRANSACTION
			}

			
			conn.close();	

		}catch(SQLException e){

			e.printStackTrace();

		}
	}
	
	public static Object getKeyFromValue(Map hm, Object value) {
		for (Object o : hm.keySet()) {
			if (hm.get(o).equals(value)) {
				return o;
			}
		}
		return null;
	}
}
