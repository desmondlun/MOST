package edu.rutgers.MOST.data;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.*;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;
import edu.rutgers.MOST.presentation.ProgressConstants;

public class SBMLModelReader {
	private String databaseName;
	private boolean readNotes;

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	private SBMLDocument doc;

	public SBMLModelReader(SBMLDocument doc) {
		this.doc = doc;
	}

	private static ArrayList<String> reactionsMetaColumnNames = new ArrayList<String>();

	public ArrayList<String> getReactionsMetaColumnNames() {
		return reactionsMetaColumnNames;
	}

	public void setReactionsMetaColumnNames(ArrayList<String> reactionsMetaColumnNames) {
		this.reactionsMetaColumnNames = reactionsMetaColumnNames;
	}

	private static ArrayList<String> metabolitesMetaColumnNames = new ArrayList<String>();

	public ArrayList<String> getMetabolitesMetaColumnNames() {
		return metabolitesMetaColumnNames;
	}

	public void setMetabolitesMetaColumnNames(ArrayList<String> metabolitesMetaColumnNames) {
		this.metabolitesMetaColumnNames = metabolitesMetaColumnNames;
	}

	public static Map<String, Object> metaboliteIdNameMap = new HashMap<String, Object>();
	
	public static Map<String, Object> getMetaboliteIdNameMap() {
		return metaboliteIdNameMap;
	}

	public static void setMetaboliteIdNameMap(Map<String, Object> metaboliteIdNameMap) {
		SBMLModelReader.metaboliteIdNameMap = metaboliteIdNameMap;
	}

	@SuppressWarnings("unchecked")
	public void load(){
		readNotes = true;
		
		LocalConfig.getInstance().getMetaboliteUsedMap().clear();
		
		DatabaseCreator databaseCreator = new DatabaseCreator();
		databaseCreator.createDatabase(getDatabaseName());

		String queryString = "jdbc:sqlite:" + getDatabaseName() + ".db";

		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			Connection conn = DriverManager.getConnection(queryString);
			Statement stat = conn.createStatement();
			PreparedStatement metabInsertPrep = conn.prepareStatement("INSERT INTO metabolites(metabolite_abbreviation, metabolite_name, "
					+ " charge, compartment, boundary, meta_1, meta_2, meta_3, meta_4, meta_5, meta_6, meta_7, meta_8, meta_9, meta_10, "
					+ " meta_11, meta_12, meta_13, meta_14, meta_15) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"); 

			new SBMLModelReader(doc);

			ArrayList<String> metabMetaColumnNames = new ArrayList<String>();

			try {
//				long startTime = System.currentTimeMillis();
				stat.executeUpdate("BEGIN TRANSACTION");
				
				ListOf<Species> metabolites = doc.getModel().getListOfSpecies();
				for (int i = 0; i < metabolites.size(); i++) {
					if (i%10 == 0) {
						LocalConfig.getInstance().setProgress((i * ProgressConstants.METABOLITE_LOAD_PERCENT) / metabolites.size()
								+ ProgressConstants.SBML_LOAD_PERCENT);		
					}
					
					//if strings contain ' (single quote), it will not execute insert statement
					//this code escapes ' as '' - sqlite syntax for escaping '
					String metaboliteAbbreviation = "";
					if (metabolites.get(i).getId().contains("'")) {
						metaboliteAbbreviation = metabolites.get(i).getId().replaceAll("'", "''");
					} else {
						metaboliteAbbreviation = metabolites.get(i).getId();
					}
					
					metaboliteIdNameMap.put(metaboliteAbbreviation, new Integer(i + 1));
					
					String metaboliteName = "";
					if (metabolites.get(i).getName().contains("'")) {
						metaboliteName = metabolites.get(i).getName().replaceAll("'", "''");
					} else {
						metaboliteName = metabolites.get(i).getName();
					}					
					int charge = metabolites.get(i).getCharge();
					String chargeString = String.valueOf(charge);
					String compartment = metabolites.get(i).getCompartment();
					String boundary = "";
					if (!metabolites.get(i).getBoundaryCondition()) {
						boundary = "false"; 
					} else {
						boundary = "true";
					}

					String metabMeta1 = " ";
					String metabMeta2 = " ";
					String metabMeta3 = " ";
					String metabMeta4 = " ";
					String metabMeta5 = " ";
					String metabMeta6 = " ";
					String metabMeta7 = " ";
					String metabMeta8 = " ";
					String metabMeta9 = " ";
					String metabMeta10 = " ";
					String metabMeta11 = " ";
					String metabMeta12 = " ";
					String metabMeta13 = " ";
					String metabMeta14 = " ";
					String metabMeta15 = " ";

					if (metabolites.get(i).isSetNotes()) {
						ArrayList<String> metabNoteItemList = new ArrayList<String>();

						for (int u = 0; u < metabolites.get(i).getNotes().getChildCount(); u++) {
							if (!metabolites.get(i).getNotes().getChildAt(u).getName().isEmpty()) {
								String noteString = metabolites.get(i).getNotes().getChildAt(u).toXMLString();
								String noteItem = "";
								//removes xmlns (xml namespace tags)
								if (noteString.contains("xmlns")) {
									noteString = noteString.substring(noteString.indexOf('>') + 1, noteString.lastIndexOf('<'));
									String endtag = noteString.substring(noteString.lastIndexOf('<'));
									String[] nameSpaces = noteString.split(endtag);
									for (int n = 0; n < nameSpaces.length; n++) {
										noteItem = nameSpaces[n].substring(nameSpaces[n].indexOf('>') + 1); 
										metabNoteItemList.add(noteItem);
									}
								} else {
									//for "<>", "</>" types of nodes, tags are removed
									noteItem = noteString.substring(noteString.indexOf('>') + 1, noteString.lastIndexOf('<'));
									metabNoteItemList.add(noteItem);
								}
							}
                            
							if (i == 0) {
								//set list of notes names to meta columns			
								for (int n = 0; n < metabNoteItemList.size(); n++) {
									if (metabNoteItemList.get(n).contains(":")) {
										//accounts for condition of multiple ":"
										String columnName = metabNoteItemList.get(n).substring(0, metabNoteItemList.get(n).indexOf(":"));
										boolean contains = false;

										for (int s = 0; s < SBMLConstants.METABOLITES_IGNORE_LIST.length; s++) {
											if ((SBMLConstants.METABOLITES_IGNORE_LIST[s].compareTo(columnName.trim()) == 0)) {
												contains = true;
												break;						
											}
										}

										if (!contains) {
											metabMetaColumnNames.add(columnName);
											String insert = "insert into metabolites_meta_info (meta_column_name) values ('" + columnName + "');";
											stat.executeUpdate(insert);	
										}	
									}
								}
								setMetabolitesMetaColumnNames(metabMetaColumnNames);

							}
							for (int n = 0; n < metabNoteItemList.size(); n++) {
								if (metabNoteItemList.get(n).contains(":")) {
									//accounts for condition of multiple ":"
									String columnName = metabNoteItemList.get(n).substring(0, metabNoteItemList.get(n).indexOf(":"));
									String value = "";
									if (metabNoteItemList.get(n).substring(metabNoteItemList.get(n).indexOf(":") + 1).contains("'")) {
										value = metabNoteItemList.get(n).substring(metabNoteItemList.get(n).indexOf(":") + 1).replaceAll("'", "''");
									} else {
										value = metabNoteItemList.get(n).substring(metabNoteItemList.get(n).indexOf(":") + 1);
									}
									if (!(columnName.trim().equals("CHARGE"))) {
										if (getMetabolitesMetaColumnNames().size() > 0 && columnName.compareTo(getMetabolitesMetaColumnNames().get(0)) == 0) {
											metabMeta1 = value.trim();											
										}
										if (getMetabolitesMetaColumnNames().size() > 1 && columnName.compareTo(getMetabolitesMetaColumnNames().get(1)) == 0) {
											metabMeta2 = value.trim();
										}
										if (getMetabolitesMetaColumnNames().size() > 2 && columnName.compareTo(getMetabolitesMetaColumnNames().get(2)) == 0) {
											metabMeta3 = value.trim();
										}
										if (getMetabolitesMetaColumnNames().size() > 3 && columnName.compareTo(getMetabolitesMetaColumnNames().get(3)) == 0) {
											metabMeta4 = value.trim();
										}
										if (getMetabolitesMetaColumnNames().size() > 4 && columnName.compareTo(getMetabolitesMetaColumnNames().get(4)) == 0) {
											metabMeta5 = value.trim();
										}
										if (getMetabolitesMetaColumnNames().size() > 5 && columnName.compareTo(getMetabolitesMetaColumnNames().get(5)) == 0) {
											metabMeta6 = value.trim();
										}
										if (getMetabolitesMetaColumnNames().size() > 6 && columnName.compareTo(getMetabolitesMetaColumnNames().get(6)) == 0) {
											metabMeta7 = value.trim();
										}
										if (getMetabolitesMetaColumnNames().size() > 7 && columnName.compareTo(getMetabolitesMetaColumnNames().get(7)) == 0) {
											metabMeta8 = value.trim();
										}
										if (getMetabolitesMetaColumnNames().size() > 8 && columnName.compareTo(getMetabolitesMetaColumnNames().get(8)) == 0) {
											metabMeta9 = value.trim();
										}
										if (getMetabolitesMetaColumnNames().size() > 9 && columnName.compareTo(getMetabolitesMetaColumnNames().get(9)) == 0) {
											metabMeta10 = value.trim();
										}
										if (getMetabolitesMetaColumnNames().size() > 10 && columnName.compareTo(getMetabolitesMetaColumnNames().get(10)) == 0) {
											metabMeta11 = value.trim();
										}
										if (getMetabolitesMetaColumnNames().size() > 11 && columnName.compareTo(getMetabolitesMetaColumnNames().get(11)) == 0) {
											metabMeta12 = value.trim();
										}
										if (getMetabolitesMetaColumnNames().size() > 12 && columnName.compareTo(getMetabolitesMetaColumnNames().get(12)) == 0) {
											metabMeta13 = value.trim();
										}
										if (getMetabolitesMetaColumnNames().size() > 13 && columnName.compareTo(getMetabolitesMetaColumnNames().get(13)) == 0) {
											metabMeta14 = value.trim();
										}
										if (getMetabolitesMetaColumnNames().size() > 14 && columnName.compareTo(getMetabolitesMetaColumnNames().get(14)) == 0) {
											metabMeta15 = value.trim();
										}
									} else {
										chargeString = value;
									}


								}
							}
						}
					}

					metabInsertPrep.setString(1, metaboliteAbbreviation);
					metabInsertPrep.setString(2, metaboliteName);
					metabInsertPrep.setString(3, chargeString);
					metabInsertPrep.setString(4, compartment);
					metabInsertPrep.setString(5, boundary);
					metabInsertPrep.setString(6, metabMeta1);
					metabInsertPrep.setString(7, metabMeta2);
					metabInsertPrep.setString(8, metabMeta3);
					metabInsertPrep.setString(9, metabMeta4);
					metabInsertPrep.setString(10, metabMeta5);
					metabInsertPrep.setString(11, metabMeta6);
					metabInsertPrep.setString(12, metabMeta7);
					metabInsertPrep.setString(13, metabMeta8);
					metabInsertPrep.setString(14, metabMeta9);
					metabInsertPrep.setString(15, metabMeta10);
					metabInsertPrep.setString(16, metabMeta11);
					metabInsertPrep.setString(17, metabMeta12);
					metabInsertPrep.setString(18, metabMeta13);
					metabInsertPrep.setString(19, metabMeta14);
					metabInsertPrep.setString(20, metabMeta15);
					
					metabInsertPrep.executeUpdate();	
				}
				LocalConfig.getInstance().setMaxMetaboliteId(metabolites.size());
				LocalConfig.getInstance().setMetaboliteIdNameMap(metaboliteIdNameMap);
				
//				long endTime = System.currentTimeMillis();
//				System.out.println("Metabolite read time: " + (endTime - startTime));
//								
//				startTime = System.currentTimeMillis();
				
				PreparedStatement reacInsertPrep = conn.prepareStatement("INSERT INTO reactions(knockout, flux_value, reaction_abbreviation, " 
						+ " reaction_name, reaction_string, reversible, lower_bound, upper_bound, biological_objective," 
						+ " meta_1, meta_2, meta_3, meta_4, meta_5, meta_6, meta_7, meta_8, meta_9, meta_10, meta_11, "
						+ "meta_12, meta_13, meta_14, meta_15) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"); 
				PreparedStatement rrInsertPrep = conn.prepareStatement("INSERT INTO reaction_reactants(reaction_id, stoic, metabolite_id) values (?, ?, ?)");
				PreparedStatement rpInsertPrep = conn.prepareStatement("INSERT INTO reaction_products(reaction_id, stoic, metabolite_id) values (?, ?, ?)");
								
				ListOf<Reaction> reactions = doc.getModel().getListOfReactions();
				for (int j = 0; j < reactions.size(); j++) {
					if (j%10 == 0) {
						LocalConfig.getInstance().setProgress((j * ProgressConstants.REACTION_LOAD_PERCENT) / reactions.size() 
								+ ProgressConstants.METABOLITE_LOAD_PERCENT + ProgressConstants.SBML_LOAD_PERCENT);		
					}
					
					StringBuffer reacBfr = new StringBuffer();
					StringBuffer prodBfr = new StringBuffer();
					StringBuffer rxnBfr = new StringBuffer();
					String reversible = "";
					
					if (!reactions.get(j).getReversible()) {
						reversible = "false";
					} else {
						reversible = "true";
					}
					
					if (reactions.get(j).isSetListOfReactants()) {
						ListOf<SpeciesReference> reactants = reactions.get(j).getListOfReactants();
						for (int r = 0; r < reactants.size(); r++) {
							if (reactants.get(r).isSetSpecies()) {
								Integer id = (Integer) metaboliteIdNameMap.get(reactants.get(r).getSpecies());
								rrInsertPrep.setInt(1, j + 1);
								rrInsertPrep.setDouble(2, reactants.get(r).getStoichiometry());
								rrInsertPrep.setInt(3, id);
								rrInsertPrep.executeUpdate();
								if (LocalConfig.getInstance().getMetaboliteUsedMap().containsKey(reactants.get(r).getSpecies())) {
									int usedCount = (Integer) LocalConfig.getInstance().getMetaboliteUsedMap().get(reactants.get(r).getSpecies());
									LocalConfig.getInstance().getMetaboliteUsedMap().put(reactants.get(r).getSpecies(), new Integer(usedCount + 1));
								} else {
									LocalConfig.getInstance().getMetaboliteUsedMap().put(reactants.get(r).getSpecies(), new Integer(1));
								}					
							}
							
							String stoicStr = "";
							if (reactants.get(r).getStoichiometry() == 1) {
								stoicStr = "";
							} else {
								stoicStr = Double.toString(reactants.get(r).getStoichiometry());
								if (stoicStr.endsWith(".0")) {
									stoicStr = stoicStr.substring(0, stoicStr.length() - 2);
								}
							}
							if (r == 0) {
								if (stoicStr.length() == 0) {
									reacBfr.append(reactants.get(r).getSpecies());
								} else {
									reacBfr.append(stoicStr + " " + reactants.get(r).getSpecies());
								}
								
							} else {
								if (stoicStr.length() == 0) {
									reacBfr.append(" + " + reactants.get(r).getSpecies());
								} else {
									reacBfr.append(" + " + stoicStr + " " + reactants.get(r).getSpecies());
								}				
							}				    	
						}			  

					}
					
					if (reactions.get(j).isSetListOfProducts()) {
						ListOf<SpeciesReference> products = reactions.get(j).getListOfProducts();
						for (int p = 0; p < products.size(); p++) {	
							if (products.get(p).isSetSpecies()) {
								Integer id = (Integer) metaboliteIdNameMap.get(products.get(p).getSpecies());
								rpInsertPrep.setInt(1, j + 1);
								rpInsertPrep.setDouble(2, products.get(p).getStoichiometry());
								rpInsertPrep.setInt(3, id);
								rpInsertPrep.executeUpdate();
								if (LocalConfig.getInstance().getMetaboliteUsedMap().containsKey(products.get(p).getSpecies())) {
									int usedCount = (Integer) LocalConfig.getInstance().getMetaboliteUsedMap().get(products.get(p).getSpecies());
									LocalConfig.getInstance().getMetaboliteUsedMap().put(products.get(p).getSpecies(), new Integer(usedCount + 1));
								} else {
									LocalConfig.getInstance().getMetaboliteUsedMap().put(products.get(p).getSpecies(), new Integer(1));
								}		
							}
													
							String stoicStr = "";
							if (products.get(p).getStoichiometry() == 1) {
								stoicStr = "";
							} else {
								stoicStr = Double.toString(products.get(p).getStoichiometry());
								if (stoicStr.endsWith(".0")) {
									stoicStr = stoicStr.substring(0, stoicStr.length() - 2);
								}
							}
							if (p == 0) {
								if (stoicStr.length() == 0) {
									prodBfr.append(products.get(p).getSpecies());
								} else {
									prodBfr.append(stoicStr + " " + products.get(p).getSpecies());
								}				
							} else {
								if (stoicStr.length() == 0) {
									prodBfr.append(" + " + products.get(p).getSpecies());
								} else {
									prodBfr.append(" + " + stoicStr + " " + products.get(p).getSpecies());
								}	
							}
						}		
					}	
						    

					if (reversible == "false") {
						rxnBfr.append(reacBfr).append(" --> ").append(prodBfr);
					} else {
						rxnBfr.append(reacBfr).append(" <==> ").append(prodBfr);
					}

					String reactionString = rxnBfr.toString().trim();
					
					String knockout = GraphicalInterfaceConstants.KO_DEFAULT;	
					Double lowerBound = GraphicalInterfaceConstants.LOWER_BOUND_DEFAULT;
					Double upperBound =	GraphicalInterfaceConstants.UPPER_BOUND_DEFAULT;
					Double objective = GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_DEFAULT;
					Double fluxValue = GraphicalInterfaceConstants.FLUX_VALUE_DEFAULT;
					
					//if strings contain ' (single quote), it will not execute insert statement
					//this code escapes ' as '' - sqlite syntax for escaping '
					String reactionAbbreviation = "";
					if (reactions.get(j).getId().contains("'")) {
						reactionAbbreviation = reactions.get(j).getId().replaceAll("'", "''");
					} else {
						reactionAbbreviation = reactions.get(j).getId();
					}
					String reactionName = "";
					if (reactions.get(j).getName().contains("'")) {
						reactionName = reactions.get(j).getName().replaceAll("'", "''");
					} else {
						reactionName = reactions.get(j).getName();
					}

					if (reactions.get(j).isSetKineticLaw()) {
						for(int k = 0; k < reactions.get(j).getKineticLaw().getListOfLocalParameters().size(); k++) {		    	
							if (reactions.get(j).getKineticLaw().getListOfLocalParameters().get(k).getId().matches("LOWER_BOUND")) {
								lowerBound = reactions.get(j).getKineticLaw().getLocalParameter("LOWER_BOUND").getValue();
							} 
							if (reactions.get(j).getKineticLaw().getListOfLocalParameters().get(k).getId().matches("UPPER_BOUND")) {
								upperBound = reactions.get(j).getKineticLaw().getLocalParameter("UPPER_BOUND").getValue();			
							} 
							if (reactions.get(j).getKineticLaw().getListOfLocalParameters().get(k).getId().matches("OBJECTIVE_COEFFICIENT")) {
								objective = reactions.get(j).getKineticLaw().getLocalParameter("OBJECTIVE_COEFFICIENT").getValue();			
							} 
							if (reactions.get(j).getKineticLaw().getListOfLocalParameters().get(k).getId().matches("FLUX_VALUE")) {
								fluxValue = reactions.get(j).getKineticLaw().getLocalParameter("FLUX_VALUE").getValue();			
							}
						}	    	
					} 

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

					if (reactions.get(j).isSetNotes() && readNotes == true) {
						ArrayList<String> noteItemList = new ArrayList<String>();	
						
						for (int u = 0; u < reactions.get(j).getNotes().getChildCount(); u++) {
							if (!reactions.get(j).getNotes().getChildAt(u).getName().isEmpty()) {
								String noteString = reactions.get(j).getNotes().getChildAt(u).toXMLString();
								String noteItem = "";
								//removes xmlns (xml namespace tags)
								if (noteString.contains("xmlns")) {
									if (!noteString.endsWith("/>")) {
										noteString = noteString.substring(noteString.indexOf(">") + 1, noteString.lastIndexOf("<"));
										String endtag = noteString.substring(noteString.lastIndexOf("<"));
										String[] nameSpaces = noteString.split(endtag);
										for (int n = 0; n < nameSpaces.length; n++) {
											noteItem = nameSpaces[n].substring(nameSpaces[n].indexOf(">") + 1); 
											noteItemList.add(noteItem);
										}
									}
								} else {
									//for "<>", "</>" types of nodes, tags are removed
									noteItem = noteString.substring(noteString.indexOf(">") + 1, noteString.lastIndexOf("<"));
									noteItemList.add(noteItem);
								}	
							}
						}

						if (j == 0) {
							//set list of notes names to meta columns
							ArrayList<String> reactionsMetaColumnNames = new ArrayList<String>();				
							for (int n = 0; n < noteItemList.size(); n++) {
								if (noteItemList.get(n).contains(":")) {
									//accounts for condition of multiple ":"
									String columnName = noteItemList.get(n).substring(0, noteItemList.get(n).indexOf(":"));
									boolean contains = false;

									for (int s = 0; s < SBMLConstants.REACTIONS_IGNORE_LIST.length; s++) {
										if ((SBMLConstants.REACTIONS_IGNORE_LIST[s].compareTo(columnName.trim()) == 0)) {
											contains = true;
											break;						
										}
									}
									if (!contains) {													
										reactionsMetaColumnNames.add(columnName);
										String rmInsert = "insert into reactions_meta_info (meta_column_name) values ('" + columnName + "');";
										stat.executeUpdate(rmInsert);	
									}
								}
							}
							setReactionsMetaColumnNames(reactionsMetaColumnNames);
						}

						for (int n = 0; n < noteItemList.size(); n++) {
							if (noteItemList.get(n).contains(":")) {
								//accounts for condition of multiple ":"
								String columnName = noteItemList.get(n).substring(0, noteItemList.get(n).indexOf(":"));
								String value = "";
								if (noteItemList.get(n).substring(noteItemList.get(n).indexOf(":") + 1).contains("'")) {
									value = noteItemList.get(n).substring(noteItemList.get(n).indexOf(":") + 1).replaceAll("'", "''");
								} else {
									value = noteItemList.get(n).substring(noteItemList.get(n).indexOf(":") + 1);
								}
								if (getReactionsMetaColumnNames().size() > 0 && columnName.compareTo(getReactionsMetaColumnNames().get(0)) == 0) {
									meta1 = value.trim();
								}
								if (getReactionsMetaColumnNames().size() > 1 && columnName.compareTo(getReactionsMetaColumnNames().get(1)) == 0) {
									meta2 = value.trim();
								}
								if (getReactionsMetaColumnNames().size() > 2 && columnName.compareTo(getReactionsMetaColumnNames().get(2)) == 0) {
									meta3 = value.trim();
								}
								if (getReactionsMetaColumnNames().size() > 3 && columnName.compareTo(getReactionsMetaColumnNames().get(3)) == 0) {
									meta4 = value.trim();
								}
								if (getReactionsMetaColumnNames().size() > 4 && columnName.compareTo(getReactionsMetaColumnNames().get(4)) == 0) {
									meta5 = value.trim();
								}
								if (getReactionsMetaColumnNames().size() > 5 && columnName.compareTo(getReactionsMetaColumnNames().get(5)) == 0) {
									meta6 = value.trim();
								}
								if (getReactionsMetaColumnNames().size() > 6 && columnName.compareTo(getReactionsMetaColumnNames().get(6)) == 0) {
									meta7 = value.trim();
								}
								if (getReactionsMetaColumnNames().size() > 7 && columnName.compareTo(getReactionsMetaColumnNames().get(7)) == 0) {
									meta8 = value.trim();
								}
								if (getReactionsMetaColumnNames().size() > 8 && columnName.compareTo(getReactionsMetaColumnNames().get(8)) == 0) {
									meta9 = value.trim();
								}
								if (getReactionsMetaColumnNames().size() > 9 && columnName.compareTo(getReactionsMetaColumnNames().get(9)) == 0) {
									meta10 = value.trim();
								}
								if (getReactionsMetaColumnNames().size() > 10 && columnName.compareTo(getReactionsMetaColumnNames().get(10)) == 0) {
									meta11 = value.trim();
								}
								if (getReactionsMetaColumnNames().size() > 11 && columnName.compareTo(getReactionsMetaColumnNames().get(11)) == 0) {
									meta12 = value.trim();
								}
								if (getReactionsMetaColumnNames().size() > 12 && columnName.compareTo(getReactionsMetaColumnNames().get(12)) == 0) {
									meta13 = value.trim();
								}
								if (getReactionsMetaColumnNames().size() > 13 && columnName.compareTo(getReactionsMetaColumnNames().get(13)) == 0) {
									meta14 = value.trim();
								}
								if (getReactionsMetaColumnNames().size() > 14 && columnName.compareTo(getReactionsMetaColumnNames().get(14)) == 0) {
									meta15 = value.trim();
								}					
							}
						}	
					}
                 	
					reacInsertPrep.setString(1, knockout);
					reacInsertPrep.setDouble(2, fluxValue);
					reacInsertPrep.setString(3, reactionAbbreviation);
					reacInsertPrep.setString(4, reactionName);
					reacInsertPrep.setString(5, reactionString);
					reacInsertPrep.setString(6, reversible);
					reacInsertPrep.setDouble(7, lowerBound);
					reacInsertPrep.setDouble(8, upperBound);
					reacInsertPrep.setDouble(9, objective);
					reacInsertPrep.setString(10, meta1);
					reacInsertPrep.setString(11, meta2);
					reacInsertPrep.setString(12, meta3);
					reacInsertPrep.setString(13, meta4);
					reacInsertPrep.setString(14, meta5);
					reacInsertPrep.setString(15, meta6);
					reacInsertPrep.setString(16, meta7);
					reacInsertPrep.setString(17, meta8);
					reacInsertPrep.setString(18, meta9);
					reacInsertPrep.setString(19, meta10);
					reacInsertPrep.setString(20, meta11);
					reacInsertPrep.setString(21, meta12);
					reacInsertPrep.setString(22, meta13);
					reacInsertPrep.setString(23, meta14);
					reacInsertPrep.setString(24, meta15);
					
					reacInsertPrep.executeUpdate();
				}
				stat.executeUpdate("COMMIT");
				
//				endTime = System.currentTimeMillis();
//				System.out.println("Reaction read time: " + (endTime - startTime));				
			} catch (Exception e) {
				e.printStackTrace();
				stat.executeUpdate("ROLLBACK"); // throw away all updates since BEGIN TRANSACTION
			}

			conn.close();
			LocalConfig.getInstance().setProgress(100);	
			
		}catch(SQLException e){

			e.printStackTrace();

		}

		//System.out.println("Done");
	}
	
	public static void main(String[] args) {
		
	}
}
