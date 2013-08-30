package edu.rutgers.MOST.data;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;
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

	public static Map<Object, String> duplicateReacColumnMap = new HashMap<Object, String>();

	public static Map<Object, String> getDuplicateReacColumnMap() {
		return duplicateReacColumnMap;
	}

	public static void setDuplicateReacColumnMap(Map<Object, String> duplicateReacColumnMap) {
		SBMLModelReader.duplicateReacColumnMap = duplicateReacColumnMap;
	}

	public static Map<Object, String> duplicateMetabColumnMap = new HashMap<Object, String>();

	public static Map<Object, String> getDuplicateMetabColumnMap() {
		return duplicateMetabColumnMap;
	}

	public static void setDuplicateMetabColumnMap(Map<Object, String> duplicateMetabColumnMap) {
		SBMLModelReader.duplicateMetabColumnMap = duplicateMetabColumnMap;
	}

	public static String columnNewName; 

	public static String getColumnNewName() {
		return columnNewName;
	}

	public static void setColumnNewName(String columnNewName) {
		SBMLModelReader.columnNewName = columnNewName;
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
						}

						if (i == 0) {
							int metaColCount = 0;
							//set list of notes names to meta columns			
							for (int n = 0; n < metabNoteItemList.size(); n++) {
								if (metabNoteItemList.get(n).contains(":")) {
									//accounts for condition of multiple ":"
									String columnName = metabNoteItemList.get(n).substring(0, metabNoteItemList.get(n).indexOf(":"));
									boolean contains = false;

									if (metabMetaColumnNames.contains(columnName)) {
										Object[] options = {"    Yes    ", "    No    ",};
										int choice = JOptionPane.showOptionDialog(null, "Duplicate " + columnName + " columns. " +
												SBMLConstants.DUPLICATE_COLUMN_ERROR_MESSAGE,
												SBMLConstants.DUPLICATE_METAB_COLUMN_ERROR_TITLE, 
												JOptionPane.YES_NO_OPTION, 
												JOptionPane.QUESTION_MESSAGE, 
												null, options, options[0]);
										// set lower bound to 0 and set new equation
										if (choice == JOptionPane.YES_OPTION) {
											String displayName = columnName;
											Object[] options2 = {"    Yes    ", "    No    ",};
											int choice2 = JOptionPane.showOptionDialog(null, 
													SBMLConstants.RENAME_COLUMN_MESSAGE + displayName + " and " 
													+ displayName + SBMLConstants.DUPLICATE_COLUMN_SUFFIX + ".", 
													SBMLConstants.DUPLICATE_METAB_COLUMN_ERROR_TITLE, 
													JOptionPane.YES_NO_OPTION, 
													JOptionPane.QUESTION_MESSAGE, 
													null, options2, options2[0]);
											// set lower bound to 0 and set new equation
											if (choice2 == JOptionPane.YES_OPTION) {
												displayName = JOptionPane.showInputDialog(null, "Enter Column Name", GraphicalInterfaceConstants.COLUMN_RENAME_INTERFACE_TITLE, JOptionPane.WARNING_MESSAGE);
												if (displayName != null && displayName.length() > 0) {
													duplicateMetabColumnMap.put(new Integer(metaColCount + 1), displayName);
												} else {
													// If user hits cancel or doesn't enter anything default name assigned
													displayName = columnName + SBMLConstants.DUPLICATE_COLUMN_SUFFIX;
													duplicateMetabColumnMap.put(new Integer(metaColCount + 1), displayName);
												}		
											}
											if (choice2 == JOptionPane.NO_OPTION) {													
												displayName = columnName + SBMLConstants.DUPLICATE_COLUMN_SUFFIX;
												duplicateMetabColumnMap.put(new Integer(metaColCount + 1), displayName);

											}
										}
										if (choice == JOptionPane.NO_OPTION) {
											LocalConfig.getInstance().getHiddenMetabolitesColumns().add(GraphicalInterfaceConstants.METABOLITES_DB_COLUMN_NAMES.length + metaColCount);
										}
									}

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
										metaColCount += 1;
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
				//System.out.println("id name map " + LocalConfig.getInstance().getMetaboliteIdNameMap());

				//				long endTime = System.currentTimeMillis();
				//				System.out.println("Metabolite read time: " + (endTime - startTime));
				//								
				//				startTime = System.currentTimeMillis();

				PreparedStatement reacInsertPrep = conn.prepareStatement("INSERT INTO reactions(knockout, flux_value, reaction_abbreviation, " 
						+ " reaction_name, reaction_equn_abbr, reaction_equn_names, reversible, lower_bound, upper_bound, biological_objective," 
						+ " synthetic_objective, gene_associations, meta_1, meta_2, meta_3, meta_4, meta_5, meta_6, meta_7, meta_8, meta_9, meta_10, meta_11, "
						+ " meta_12, meta_13, meta_14, meta_15) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"); 
				PreparedStatement rrInsertPrep = conn.prepareStatement("INSERT INTO reaction_reactants(reaction_id, stoic, metabolite_id) values (?, ?, ?)");
				PreparedStatement rpInsertPrep = conn.prepareStatement("INSERT INTO reaction_products(reaction_id, stoic, metabolite_id) values (?, ?, ?)");
				PreparedStatement reacNamePrep = conn.prepareStatement("SELECT metabolite_name from metabolites where id=?;");

				ListOf<Reaction> reactions = doc.getModel().getListOfReactions();
				boolean locusColumnName = false;
				for (int j = 0; j < reactions.size(); j++) {
					if (j%10 == 0) {
						LocalConfig.getInstance().setProgress((j * ProgressConstants.REACTION_LOAD_PERCENT) / reactions.size() 
								+ ProgressConstants.METABOLITE_LOAD_PERCENT + ProgressConstants.SBML_LOAD_PERCENT);		
					}

					StringBuffer reacBfr = new StringBuffer();
					StringBuffer reacNamesBfr = new StringBuffer();
					StringBuffer prodBfr = new StringBuffer();
					StringBuffer prodNamesBfr = new StringBuffer();
					StringBuffer rxnBfr = new StringBuffer();
					StringBuffer rxnNamesBfr = new StringBuffer();
					String reversible = "";

					if (!reactions.get(j).getReversible()) {
						reversible = "false";
					} else {
						reversible = "true";
					}

					if (reactions.get(j).isSetListOfReactants()) {
						ListOf<SpeciesReference> reactants = reactions.get(j).getListOfReactants();
						int id = 0;
						for (int r = 0; r < reactants.size(); r++) {							
							if (reactants.get(r).isSetSpecies()) {
								// if a metabolite is missing in the model, but used in an equation, 
								// null error caught here
								if (metaboliteIdNameMap.get(reactants.get(r).getSpecies()) == null) {
									// add missing metabolite to maps and database
									id = LocalConfig.getInstance().getMaxMetaboliteId() + 1;
									LocalConfig.getInstance().setMaxMetaboliteId(id);
									metaboliteIdNameMap.put(reactants.get(r).getSpecies(), id);
									LocalConfig.getInstance().setMetaboliteIdNameMap(metaboliteIdNameMap);
									
									PreparedStatement metabInsertPrep2 = conn.prepareStatement("insert into metabolites (metabolite_abbreviation, metabolite_name, boundary) values(?, '', 'false')");
									metabInsertPrep2.setString(1, reactants.get(r).getSpecies());
									metabInsertPrep2.executeUpdate();									
								} else {
									id = (Integer) metaboliteIdNameMap.get(reactants.get(r).getSpecies());									
								}
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
							String metabName = "";
							reacNamePrep.setInt(1, id);
							ResultSet rs = reacNamePrep.executeQuery();
							while (rs.next()) {
								metabName = rs.getString("metabolite_name");
							}
							rs.close();
							if (r == 0) {
								if (stoicStr.length() == 0) {
									reacBfr.append(reactants.get(r).getSpecies());
									if (metabName != null && metabName.trim().length() > 0) {
										reacNamesBfr.append(metabName);
									} else {
										reacNamesBfr.append(reactants.get(r).getSpecies());
									}									
								} else {
									reacBfr.append(stoicStr + " " + reactants.get(r).getSpecies());
									if (metabName != null && metabName.trim().length() > 0) {
										reacNamesBfr.append(stoicStr + " " + metabName);
									} else {
										reacNamesBfr.append(stoicStr + " " + reactants.get(r).getSpecies());
									}									
								}

							} else {
								if (stoicStr.length() == 0) {
									reacBfr.append(" + " + reactants.get(r).getSpecies());
									if (metabName != null && metabName.trim().length() > 0) {
										reacNamesBfr.append(" + " + metabName);
									} else {
										reacNamesBfr.append(" + " + reactants.get(r).getSpecies());
									}
									
								} else {
									reacBfr.append(" + " + stoicStr + " " + reactants.get(r).getSpecies());
									if (metabName != null && metabName.trim().length() > 0) {
										reacNamesBfr.append(" + " + stoicStr + " " + metabName);
									} else {
										reacNamesBfr.append(" + " + stoicStr + " " + reactants.get(r).getSpecies());
									}								
								}				
							}				    	
						}			  

					}

					if (reactions.get(j).isSetListOfProducts()) {
						ListOf<SpeciesReference> products = reactions.get(j).getListOfProducts();
						int id = 0;
						for (int p = 0; p < products.size(); p++) {	
							if (products.get(p).isSetSpecies()) {
								// if a metabolite is missing in the model, but used in an equation, 
								// null error caught here
								if (metaboliteIdNameMap.get(products.get(p).getSpecies()) == null) {
									// add missing metabolite to maps and database
									id = LocalConfig.getInstance().getMaxMetaboliteId() + 1;
									LocalConfig.getInstance().setMaxMetaboliteId(id);
									metaboliteIdNameMap.put(products.get(p).getSpecies(), id);
									LocalConfig.getInstance().setMetaboliteIdNameMap(metaboliteIdNameMap);
									
									PreparedStatement metabInsertPrep2 = conn.prepareStatement("insert into metabolites (metabolite_abbreviation, metabolite_name, boundary) values(?, '', 'false')");
									metabInsertPrep2.setString(1, products.get(p).getSpecies());
									metabInsertPrep2.executeUpdate();									
								} else {
								    id = (Integer) metaboliteIdNameMap.get(products.get(p).getSpecies());
								}
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
							String metabName = "";
							reacNamePrep.setInt(1, id);
							ResultSet rs = reacNamePrep.executeQuery();
							while (rs.next()) {
								metabName = rs.getString("metabolite_name");
							}
							rs.close();
							if (p == 0) {
								if (stoicStr.length() == 0) {
									prodBfr.append(products.get(p).getSpecies());
									if (metabName != null && metabName.trim().length() > 0) {
										prodNamesBfr.append(metabName);
									} else {
										prodNamesBfr.append(products.get(p).getSpecies());
									}				
								} else {
									prodBfr.append(stoicStr + " " + products.get(p).getSpecies());
									if (metabName != null && metabName.trim().length() > 0) {
										prodNamesBfr.append(stoicStr + " " + metabName);
									} else {
										prodNamesBfr.append(stoicStr + " " + products.get(p).getSpecies());
									}									
								}				
							} else {
								if (stoicStr.length() == 0) {
									prodBfr.append(" + " + products.get(p).getSpecies());
									if (metabName != null && metabName.trim().length() > 0) {
										prodNamesBfr.append(" + " + metabName);
									} else {
										prodNamesBfr.append(" + " + products.get(p).getSpecies());
									}									
								} else {
									prodBfr.append(" + " + stoicStr + " " + products.get(p).getSpecies());
									if (metabName != null && metabName.trim().length() > 0) {
										prodNamesBfr.append(" + " + stoicStr + " " + metabName);
									} else {
										prodNamesBfr.append(" + " + stoicStr + " " + products.get(p).getSpecies());
									}									
								}	
							}
						}		
					}	

					if (reversible == "false") {
						rxnBfr.append(reacBfr).append(" --> ").append(prodBfr);
						rxnNamesBfr.append(reacNamesBfr).append(" --> ").append(prodNamesBfr);
					} else {
						rxnBfr.append(reacBfr).append(" <==> ").append(prodBfr);
						rxnNamesBfr.append(reacNamesBfr).append(" <==> ").append(prodNamesBfr);
					}

					String reactionEqunAbbr = rxnBfr.toString().trim();
					String reactionEqunNames = rxnNamesBfr.toString().trim();

					String knockout = GraphicalInterfaceConstants.KO_DEFAULT;	
					Double lowerBound = GraphicalInterfaceConstants.LOWER_BOUND_DEFAULT;
					Double upperBound =	GraphicalInterfaceConstants.UPPER_BOUND_DEFAULT;
					Double biologicalObjective = GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_DEFAULT;
					Double syntheticObjective = GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_DEFAULT;
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
								biologicalObjective = reactions.get(j).getKineticLaw().getLocalParameter("OBJECTIVE_COEFFICIENT").getValue();			
							} 
							/*
							 * need to see if any sbml files contain a synthetic coefficient and if so, what it is called. this is just a 
							 * guess. do not uncomment this code unless certain it is correct
							if (reactions.get(j).getKineticLaw().getListOfLocalParameters().get(k).getId().matches("SYNTHETIC_COEFFICIENT")) {
								syntheticObjective = reactions.get(j).getKineticLaw().getLocalParameter("SYNTHETIC_COEFFICIENT").getValue();			
							}
							*/
							if (reactions.get(j).getKineticLaw().getListOfLocalParameters().get(k).getId().matches("FLUX_VALUE")) {
								fluxValue = reactions.get(j).getKineticLaw().getLocalParameter("FLUX_VALUE").getValue();			
							}
						}	    	
					} 

					String geneAssociations = "";
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
								//System.out.println(noteString);
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
									if ((noteString.indexOf(">") + 1) < noteString.lastIndexOf("<")) {
										//for "<>", "</>" types of nodes, tags are removed
										noteItem = noteString.substring(noteString.indexOf(">") + 1, noteString.lastIndexOf("<"));
										noteItemList.add(noteItem);	
									} else if (noteString.contains("listOfGenes")) {
										noteItemList.add("listOfGenes");
									}
								}	
							}
						}

						ArrayList<String> reactionsMetaColumnNames = new ArrayList<String>();
						StringBuffer locusBfr = new StringBuffer();
						if (j == 0) {
							//set list of notes names to meta columns							
							boolean genes = false;
							int metaColCount = 0;
							for (int n = 0; n < noteItemList.size(); n++) {
								if (noteItemList.get(n).contains(":")) {
									//accounts for condition of multiple ":"
									String columnName = noteItemList.get(n).substring(0, noteItemList.get(n).indexOf(":"));
							
									boolean contains = false;

									if (columnName.compareTo("genes") == 0) {
										genes = true;
									}
									if (genes) {
										if (columnName.compareTo("LOCUS") == 0) {
											locusColumnName = true;
										}
									}
									if (reactionsMetaColumnNames.contains(columnName)) {
										String displayName = columnName;
										if (locusColumnName) {
											displayName = "Genes";
											LocalConfig.getInstance().getHiddenReactionsColumns().add(GraphicalInterfaceConstants.REACTIONS_DB_COLUMN_NAMES.length + metaColCount);
										} else {
											Object[] options = {"    Yes    ", "    No    ",};
											int choice = JOptionPane.showOptionDialog(null, "Duplicate " + displayName + " columns. " +
													SBMLConstants.DUPLICATE_COLUMN_ERROR_MESSAGE,
													SBMLConstants.DUPLICATE_REAC_COLUMN_ERROR_TITLE, 
													JOptionPane.YES_NO_OPTION, 
													JOptionPane.QUESTION_MESSAGE, 
													null, options, options[0]);
											// set lower bound to 0 and set new equation
											if (choice == JOptionPane.YES_OPTION) {					
												Object[] options2 = {"    Yes    ", "    No    ",};
												int choice2 = JOptionPane.showOptionDialog(null, 
														SBMLConstants.RENAME_COLUMN_MESSAGE + displayName + " and " 
														+ displayName + SBMLConstants.DUPLICATE_COLUMN_SUFFIX + ".", 
														SBMLConstants.DUPLICATE_REAC_COLUMN_ERROR_TITLE, 
														JOptionPane.YES_NO_OPTION, 
														JOptionPane.QUESTION_MESSAGE, 
														null, options2, options2[0]);
												// set lower bound to 0 and set new equation
												if (choice2 == JOptionPane.YES_OPTION) {
													displayName = JOptionPane.showInputDialog(null, "Enter Column Name", GraphicalInterfaceConstants.COLUMN_RENAME_INTERFACE_TITLE, JOptionPane.WARNING_MESSAGE);
													if (displayName != null && displayName.length() > 0) {
														duplicateReacColumnMap.put(new Integer(metaColCount + 1), displayName);
													} else {
														// If user hits cancel or doesn't enter anything default name assigned
														displayName = columnName + SBMLConstants.DUPLICATE_COLUMN_SUFFIX;
														duplicateReacColumnMap.put(new Integer(metaColCount + 1), displayName);
													}
												}
												if (choice2 == JOptionPane.NO_OPTION) {								
													displayName = columnName + SBMLConstants.DUPLICATE_COLUMN_SUFFIX;
													duplicateReacColumnMap.put(new Integer(metaColCount + 1), displayName);											
												}
											}
											if (choice == JOptionPane.NO_OPTION) {
												LocalConfig.getInstance().getHiddenReactionsColumns().add(GraphicalInterfaceConstants.REACTIONS_DB_COLUMN_NAMES.length + metaColCount);
											}
										}										
									}

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
										metaColCount += 1;
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
								if (locusColumnName && getReactionsMetaColumnNames().size() > 0 && columnName.compareTo("LOCUS") == 0) {
									value = noteItemList.get(n).substring(noteItemList.get(n).indexOf(":") + 1);
									locusBfr.append("LOCUS:" + value + ", ");
								}
							}
						}
						String locusBfrStr = locusBfr.toString();
						if (locusBfrStr.length() > 1) {
							locusBfrStr = locusBfrStr.substring(0, locusBfrStr.length() - 2);
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
								if (columnName.compareTo("GENE ASSOCIATION") == 0 || columnName.compareTo("GENE_ASSOCIATION") == 0) {
									geneAssociations = value.trim();
								} else {
									if (getReactionsMetaColumnNames().size() > 0 && columnName.compareTo(getReactionsMetaColumnNames().get(0)) == 0) {
										if (columnName.compareTo("LOCUS") == 0) {
											meta1 = locusBfrStr;
										} else {
											meta1 = value.trim();
										}					
									}
									if (getReactionsMetaColumnNames().size() > 1 && columnName.compareTo(getReactionsMetaColumnNames().get(1)) == 0) {
										if (columnName.compareTo("LOCUS") == 0) {
											meta2 = locusBfrStr;
										} else {
											meta2 = value.trim();
										}									
									}
									if (getReactionsMetaColumnNames().size() > 2 && columnName.compareTo(getReactionsMetaColumnNames().get(2)) == 0) {
										if (columnName.compareTo("LOCUS") == 0) {
											meta3 = locusBfrStr;
										} else {
											meta3 = value.trim();
										}
									}
									if (getReactionsMetaColumnNames().size() > 3 && columnName.compareTo(getReactionsMetaColumnNames().get(3)) == 0) {
										if (columnName.compareTo("LOCUS") == 0) {
											meta4 = locusBfrStr;
										} else {
											meta4 = value.trim();
										}
									}
									if (getReactionsMetaColumnNames().size() > 4 && columnName.compareTo(getReactionsMetaColumnNames().get(4)) == 0) {
										if (columnName.compareTo("LOCUS") == 0) {
											meta5 = locusBfrStr;
										} else {
											meta5 = value.trim();
										}
									}
									if (getReactionsMetaColumnNames().size() > 5 && columnName.compareTo(getReactionsMetaColumnNames().get(5)) == 0) {
										if (columnName.compareTo("LOCUS") == 0) {
											meta6 = locusBfrStr;
										} else {
											meta6 = value.trim();
										}
									}
									if (getReactionsMetaColumnNames().size() > 6 && columnName.compareTo(getReactionsMetaColumnNames().get(6)) == 0) {
										if (columnName.compareTo("LOCUS") == 0) {
											meta7 = locusBfrStr;
										} else {
											meta7 = value.trim();
										}
									}
									if (getReactionsMetaColumnNames().size() > 7 && columnName.compareTo(getReactionsMetaColumnNames().get(7)) == 0) {
										if (columnName.compareTo("LOCUS") == 0) {
											meta8 = locusBfrStr;
										} else {
											meta8 = value.trim();
										}
									}
									if (getReactionsMetaColumnNames().size() > 8 && columnName.compareTo(getReactionsMetaColumnNames().get(8)) == 0) {
										if (columnName.compareTo("LOCUS") == 0) {
											meta9 = locusBfrStr;
										} else {
											meta9 = value.trim();
										}
									}
									if (getReactionsMetaColumnNames().size() > 9 && columnName.compareTo(getReactionsMetaColumnNames().get(9)) == 0) {
										if (columnName.compareTo("LOCUS") == 0) {
											meta10 = locusBfrStr;
										} else {
											meta10 = value.trim();
										}
									}
									if (getReactionsMetaColumnNames().size() > 10 && columnName.compareTo(getReactionsMetaColumnNames().get(10)) == 0) {
										if (columnName.compareTo("LOCUS") == 0) {
											meta11 = locusBfrStr;
										} else {
											meta11 = value.trim();
										}
									}
									if (getReactionsMetaColumnNames().size() > 11 && columnName.compareTo(getReactionsMetaColumnNames().get(11)) == 0) {
										if (columnName.compareTo("LOCUS") == 0) {
											meta12 = locusBfrStr;
										} else {
											meta12 = value.trim();
										}
									}
									if (getReactionsMetaColumnNames().size() > 12 && columnName.compareTo(getReactionsMetaColumnNames().get(12)) == 0) {
										if (columnName.compareTo("LOCUS") == 0) {
											meta13 = locusBfrStr;
										} else {
											meta13 = value.trim();
										}
									}
									if (getReactionsMetaColumnNames().size() > 13 && columnName.compareTo(getReactionsMetaColumnNames().get(13)) == 0) {
										if (columnName.compareTo("LOCUS") == 0) {
											meta14 = locusBfrStr;
										} else {
											meta14 = value.trim();
										}
									}
									if (getReactionsMetaColumnNames().size() > 14 && columnName.compareTo(getReactionsMetaColumnNames().get(14)) == 0) {
										if (columnName.compareTo("LOCUS") == 0) {
											meta15 = locusBfrStr;
										} else {
											meta15 = value.trim();
										}
									}	
								}												
							}
						}
					}

					if (locusColumnName) {
						for (int z = 0; z < getReactionsMetaColumnNames().size(); z++) {
							if (getReactionsMetaColumnNames().get(z).compareTo("LOCUS") == 0) {
								String rmUpdate = "update reactions_meta_info set meta_column_name='Genes' where id=" + (z + 1) + ";";
								stat.executeUpdate(rmUpdate);	
							}
						}
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

					for (Object key : duplicateMetabColumnMap.keySet()) {
						String dupMUpdate = "update metabolites_meta_info set meta_column_name='" + duplicateMetabColumnMap.get(key) + "' where id=" + key + ";"; 
						stat.executeUpdate(dupMUpdate);
					}
					for (Object key : duplicateReacColumnMap.keySet()) {
						String dupRUpdate = "update reactions_meta_info set meta_column_name='" + duplicateReacColumnMap.get(key) + "' where id=" + key + ";"; 
						stat.executeUpdate(dupRUpdate);
					}
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
			//System.out.println("used map " + LocalConfig.getInstance().getMetaboliteUsedMap());

		}catch(SQLException e){

			e.printStackTrace();

		}

		//System.out.println("Done");
	}

	public static void main(String[] args) {
		SBMLReader reader = new SBMLReader();
		SBMLDocument doc;
		try {
			//doc = reader.readSBML("C:\\Users\\dslun\\GitHub\\desmondlun_MOST\\etc\\sbml\\Ec_core_flux1.xml");
			doc = reader.readSBML("C:\\Users\\dslun\\GitHub\\desmondlun_MOST\\etc\\sbml\\Ec_iAF1260_anaerobic_glc10_acetate.xml");
			SBMLModelReader modelReader = new SBMLModelReader(doc);
			modelReader.setDatabaseName("Ec_core_flux1.db");
			modelReader.load();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

