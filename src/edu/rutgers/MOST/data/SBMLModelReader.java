package edu.rutgers.MOST.data;

import java.sql.*;
import java.util.ArrayList;

import org.sbml.jsbml.*;
import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.presentation.ProgressConstants;


public class SBMLModelReader {
	String databaseName;
	Integer numberOfMetabolites;
	Integer numberOfReactions;
	boolean readNotes;

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



	public void load(){
		readNotes = false;
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
			Connection conn =
				DriverManager.getConnection(queryString);

			new SBMLModelReader(doc);
			String id = doc.getModel().getId(); 

			//add boundary condition column name
			MetabolitesMetaColumnManager metabolitesMetaColumnManager = new MetabolitesMetaColumnManager();
			ArrayList<String> metabMetaColumnNames = new ArrayList();

			ListOf<Species> metabolites = doc.getModel().getListOfSpecies();
			for (int i = 0; i < metabolites.size(); i++) {
				if (i%10 == 0) {
					LocalConfig.getInstance().setProgress((i*ProgressConstants.METABOLITE_LOAD_PERCENT)/metabolites.size());		
				}		
				SBMLMetabolite metab[] = new SBMLMetabolite[metabolites.size()];
				metab[i] = new SBMLMetabolite();

				metab[i].setMetaboliteAbbreviation(metabolites.get(i).getId());

				metab[i].setCompartment(metabolites.get(i).getCompartment());

				int charge = metabolites.get(i).getCharge();
				metab[i].setCharge(String.valueOf(charge));

				metab[i].setMetaboliteName(metabolites.get(i).getName());

				if (!metabolites.get(i).getBoundaryCondition()) {
					String boundary = "false"; 
					metab[i].setBoundary(boundary);
				} else {
					String boundary = "true";
					metab[i].setBoundary(boundary);
				}		

				if (metabolites.get(i).isSetNotes()) {
					ArrayList<String> metabNoteItemList = new ArrayList();

					for (int u = 0; u < metabolites.get(i).getNotes().getNumChildren(); u++) {
						String noteString = metabolites.get(i).getNotes().getChild(u).toXMLString();
						String noteItem = "";
						//removes xmlns (xml namespace tags)
						if (noteString.contains("xmlns")) {
							noteString = noteString.substring(noteString.indexOf(">") + 1, noteString.lastIndexOf("<"));
							String endtag = noteString.substring(noteString.lastIndexOf("<"));
							String[] nameSpaces = noteString.split(endtag);
							for (int n = 0; n < nameSpaces.length; n++) {
								noteItem = nameSpaces[n].substring(nameSpaces[n].indexOf(">") + 1); 
								metabNoteItemList.add(noteItem);
							}
						} else {
							//for "<>", "</>" types of nodes, tags are removed
							noteItem = noteString.substring(noteString.indexOf(">") + 1, noteString.lastIndexOf("<"));
							metabNoteItemList.add(noteItem);
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
										//System.out.println("column " + columnName);													
										metabMetaColumnNames.add(columnName);																				 
									}	
								}
							}
							metabolitesMetaColumnManager.addColumnNames(LocalConfig.getInstance().getDatabaseName(), metabMetaColumnNames);

						}
						for (int n = 0; n < metabNoteItemList.size(); n++) {
							if (metabNoteItemList.get(n).contains(":")) {
								//accounts for condition of multiple ":"
								String columnName = metabNoteItemList.get(n).substring(0, metabNoteItemList.get(n).indexOf(":"));
								String value = metabNoteItemList.get(n).substring(metabNoteItemList.get(n).indexOf(":") + 1);
								int columnCount = metabolitesMetaColumnManager.getMetaColumnCount(getDatabaseName());
								if (columnName.trim().compareTo("CHARGE") != 0) {
									if (columnCount > 0 && columnName.compareTo(metabolitesMetaColumnManager.getColumnName(getDatabaseName(), 1)) == 0) {
										metab[i].setMeta1(value);
									}
									if (columnCount > 1 && columnName.compareTo(metabolitesMetaColumnManager.getColumnName(getDatabaseName(), 2)) == 0) {
										metab[i].setMeta2(value);
									}
									if (columnCount > 2 && columnName.compareTo(metabolitesMetaColumnManager.getColumnName(getDatabaseName(), 3)) == 0) {
										metab[i].setMeta3(value);
									}
									if (columnCount > 3 && columnName.compareTo(metabolitesMetaColumnManager.getColumnName(getDatabaseName(), 4)) == 0) {
										metab[i].setMeta4(value);
									}
									if (columnCount > 4 && columnName.compareTo(metabolitesMetaColumnManager.getColumnName(getDatabaseName(), 5)) == 0) {
										metab[i].setMeta5(value);
									}
									if (columnCount > 5 && columnName.compareTo(metabolitesMetaColumnManager.getColumnName(getDatabaseName(), 6)) == 0) {
										metab[i].setMeta6(value);
									}
									if (columnCount > 6 && columnName.compareTo(metabolitesMetaColumnManager.getColumnName(getDatabaseName(), 7)) == 0) {
										metab[i].setMeta7(value);
									}
									if (columnCount > 7 && columnName.compareTo(metabolitesMetaColumnManager.getColumnName(getDatabaseName(), 8)) == 0) {
										metab[i].setMeta8(value);
									}
									if (columnCount > 8 && columnName.compareTo(metabolitesMetaColumnManager.getColumnName(getDatabaseName(), 9)) == 0) {
										metab[i].setMeta9(value);
									}
									if (columnCount > 9 && columnName.compareTo(metabolitesMetaColumnManager.getColumnName(getDatabaseName(), 10)) == 0) {
										metab[i].setMeta10(value);
									}
								} else {
									metab[i].setCharge(value);
								}


							}
						}
					}
				}

				PreparedStatement prep = conn.prepareStatement(
						"insert into metabolites (id, metabolite_abbreviation, metabolite_name, charge, compartment," 
						+ " boundary, meta_1, meta_2, meta_3, meta_4, meta_5, meta_6, meta_7, meta_8, meta_9, meta_10, used) " 
						+ " values (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'false');");
				prep.setString(1, metab[i].getMetaboliteAbbreviation());
				prep.setString(2, metab[i].getMetaboliteName());		
				prep.setString(3, metab[i].getCharge());
				prep.setString(4, metab[i].getCompartment());
				prep.setString(5, metab[i].getBoundary());
				prep.setString(6, metab[i].getMeta1());
				prep.setString(7, metab[i].getMeta2());
				prep.setString(8, metab[i].getMeta3());
				prep.setString(9, metab[i].getMeta4());
				prep.setString(10, metab[i].getMeta5());
				prep.setString(11, metab[i].getMeta6());
				prep.setString(12, metab[i].getMeta7());
				prep.setString(13, metab[i].getMeta8());
				prep.setString(14, metab[i].getMeta9());
				prep.setString(15, metab[i].getMeta10());
				prep.addBatch();

				conn.setAutoCommit(false);
				prep.executeBatch();
				conn.setAutoCommit(true);

			}

			ListOf<Reaction> reactions = doc.getModel().getListOfReactions();
			for (int j = 0; j < reactions.size(); j++) {
				if (j%10 == 0) {
					LocalConfig.getInstance().setProgress((j*ProgressConstants.REACTION_LOAD_PERCENT)/reactions.size() + ProgressConstants.METABOLITE_LOAD_PERCENT);		
				}
				SBMLReaction reac[] = new SBMLReaction[reactions.size()];
				reac[j] = new SBMLReaction();

				reac[j].setReactionAbbreviation(reactions.get(j).getId());
				reac[j].setReactionName(reactions.get(j).getName());

				String reacString = "";
				String prodString = "";
				String reactionString = "";

				ListOf<SpeciesReference> reactants = reactions.get(j).getListOfReactants();
				for (int r = 0; r < reactants.size(); r++) {
					PreparedStatement prep1 = conn.prepareStatement(
							"insert into reaction_reactants values (?, (select id from metabolites where metabolite_abbreviation = ?), ?);");
					prep1.setDouble(1, j + 1);
					prep1.setString(2, reactants.get(r).getSpecies());
					prep1.setDouble(3, reactants.get(r).getStoichiometry());

					String stoicStr = "";
					if (reactants.get(r).getStoichiometry() == 1) {
						stoicStr = "";
					} else {
						stoicStr = Double.toString(reactants.get(r).getStoichiometry());
					}

					if (r == 0) {
						if (stoicStr.length() == 0) {
							reacString += reactants.get(r).getSpecies();
						} else {
							reacString += stoicStr + " " + reactants.get(r).getSpecies();
						}	    		
					} else {
						if (stoicStr.length() == 0) {
							reacString += " + " + reactants.get(r).getSpecies();
						} else {
							reacString += " + " + stoicStr + " " + reactants.get(r).getSpecies();
						}    	    
					}

					prep1.addBatch();

					conn.setAutoCommit(false);
					prep1.executeBatch();
					conn.setAutoCommit(true);
				}

				ListOf<SpeciesReference> products = reactions.get(j).getListOfProducts();
				for (int p = 0; p < products.size(); p++) {
					PreparedStatement prep1 = conn.prepareStatement(
							"insert into reaction_products values (?, (select id from metabolites where metabolite_abbreviation = ?), ?);");
					prep1.setDouble(1, j + 1);
					prep1.setString(2, products.get(p).getSpecies());
					prep1.setDouble(3, products.get(p).getStoichiometry());

					String stoicStr = "";
					if (products.get(p).getStoichiometry() == 1) {
						stoicStr = "";
					} else {
						stoicStr = Double.toString(products.get(p).getStoichiometry());
					}

					if (p == 0) {
						if (stoicStr.length() == 0) {
							prodString += products.get(p).getSpecies();
						} else {
							prodString += stoicStr + " " + products.get(p).getSpecies();
						}	    		
					} else {
						if (stoicStr.length() == 0) {
							prodString += " + " + products.get(p).getSpecies();
						} else {
							prodString += " + " + stoicStr + " " + products.get(p).getSpecies();
						}    	    
					}

					prep1.addBatch();

					conn.setAutoCommit(false);
					prep1.executeBatch();
					conn.setAutoCommit(true);
				}

				if (!reactions.get(j).getReversible()) {
					String reversible = "false";
					reac[j].setReversible(reversible);
				} else {
					String reversible = "true";
					reac[j].setReversible(reversible);
				}

				if (reac[j].getReversible() == "false") {
					reactionString = reacString + " -> " + prodString;
				} else {
					reactionString = reacString + " <==> " + prodString; 
				}					

				if (reactions.get(j).isSetKineticLaw()) {
					for(int k = 0; k < reactions.get(j).getKineticLaw().getListOfLocalParameters().size(); k++) {		    	
						if (reactions.get(j).getKineticLaw().getListOfLocalParameters().get(k).getId().matches("LOWER_BOUND")) {
							reac[j].setLowerBound(reactions.get(j).getKineticLaw().getLocalParameter("LOWER_BOUND").getValue());
						} 
						if (reactions.get(j).getKineticLaw().getListOfLocalParameters().get(k).getId().matches("UPPER_BOUND")) {
							reac[j].setUpperBound(reactions.get(j).getKineticLaw().getLocalParameter("UPPER_BOUND").getValue());			
						} 
						if (reactions.get(j).getKineticLaw().getListOfLocalParameters().get(k).getId().matches("OBJECTIVE_COEFFICIENT")) {
							reac[j].setBiologicalObjective(reactions.get(j).getKineticLaw().getLocalParameter("OBJECTIVE_COEFFICIENT").getValue());			
						} 
						if (reactions.get(j).getKineticLaw().getListOfLocalParameters().get(k).getId().matches("FLUX_VALUE")) {
							reac[j].setFluxValue(reactions.get(j).getKineticLaw().getLocalParameter("FLUX_VALUE").getValue());			
						}
					}	    	
				} else {
					reac[j].setLowerBound(-999999.0);
					reac[j].setUpperBound(999999.0);
					reac[j].setBiologicalObjective(0.0);
					reac[j].setFluxValue(0.0);
				}

				if (reactions.get(j).isSetNotes() && readNotes == true) {
					ArrayList<String> noteItemList = new ArrayList();	
					ReactionsMetaColumnManager reactionsMetaColumnManager = new ReactionsMetaColumnManager();

					for (int u = 0; u < reactions.get(j).getNotes().getNumChildren(); u++) {
						String noteString = reactions.get(j).getNotes().getChild(u).toXMLString();
						String noteItem = "";
						//removes xmlns (xml namespace tags)
						if (noteString.contains("xmlns")) {
							noteString = noteString.substring(noteString.indexOf(">") + 1, noteString.lastIndexOf("<"));
							String endtag = noteString.substring(noteString.lastIndexOf("<"));
							String[] nameSpaces = noteString.split(endtag);
							for (int n = 0; n < nameSpaces.length; n++) {
								noteItem = nameSpaces[n].substring(nameSpaces[n].indexOf(">") + 1); 
								noteItemList.add(noteItem);
							}
						} else {
							//for "<>", "</>" types of nodes, tags are removed
							noteItem = noteString.substring(noteString.indexOf(">") + 1, noteString.lastIndexOf("<"));
							noteItemList.add(noteItem);
						}				
					}

					if (j == 0) {
						//set list of notes names to meta columns
						ArrayList<String> reactionsMetaColumnNames = new ArrayList();				
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
									//System.out.println("column " + columnName);													
									reactionsMetaColumnNames.add(columnName);																				 
								}	
							}
						}
						reactionsMetaColumnManager.addColumnNames(LocalConfig.getInstance().getDatabaseName(), reactionsMetaColumnNames);
					}

					for (int n = 0; n < noteItemList.size(); n++) {
						if (noteItemList.get(n).contains(":")) {
							//accounts for condition of multiple ":"
							String columnName = noteItemList.get(n).substring(0, noteItemList.get(n).indexOf(":"));
							String value = noteItemList.get(n).substring(noteItemList.get(n).indexOf(":") + 1);
							int columnCount = reactionsMetaColumnManager.getMetaColumnCount(getDatabaseName());
							if (columnCount > 0 && columnName.compareTo(reactionsMetaColumnManager.getColumnName(getDatabaseName(), 1)) == 0) {
								reac[j].setMeta1(value);
							}
							if (columnCount > 1 && columnName.compareTo(reactionsMetaColumnManager.getColumnName(getDatabaseName(), 2)) == 0) {
								reac[j].setMeta2(value);
							}
							if (columnCount > 2 && columnName.compareTo(reactionsMetaColumnManager.getColumnName(getDatabaseName(), 3)) == 0) {
								reac[j].setMeta3(value);
							}
							if (columnCount > 3 && columnName.compareTo(reactionsMetaColumnManager.getColumnName(getDatabaseName(), 4)) == 0) {
								reac[j].setMeta4(value);
							}
							if (columnCount > 4 && columnName.compareTo(reactionsMetaColumnManager.getColumnName(getDatabaseName(), 5)) == 0) {
								reac[j].setMeta5(value);
							}
							if (columnCount > 5 && columnName.compareTo(reactionsMetaColumnManager.getColumnName(getDatabaseName(), 6)) == 0) {
								reac[j].setMeta6(value);
							}
							if (columnCount > 6 && columnName.compareTo(reactionsMetaColumnManager.getColumnName(getDatabaseName(), 7)) == 0) {
								reac[j].setMeta7(value);
							}
							if (columnCount > 7 && columnName.compareTo(reactionsMetaColumnManager.getColumnName(getDatabaseName(), 8)) == 0) {
								reac[j].setMeta8(value);
							}
							if (columnCount > 8 && columnName.compareTo(reactionsMetaColumnManager.getColumnName(getDatabaseName(), 9)) == 0) {
								reac[j].setMeta9(value);
							}
							if (columnCount > 9 && columnName.compareTo(reactionsMetaColumnManager.getColumnName(getDatabaseName(), 10)) == 0) {
								reac[j].setMeta10(value);
							}
							if (columnCount > 10 && columnName.compareTo(reactionsMetaColumnManager.getColumnName(getDatabaseName(), 11)) == 0) {
								reac[j].setMeta11(value);
							}
							if (columnCount > 11 && columnName.compareTo(reactionsMetaColumnManager.getColumnName(getDatabaseName(), 12)) == 0) {
								reac[j].setMeta12(value);
							}
							if (columnCount > 12 && columnName.compareTo(reactionsMetaColumnManager.getColumnName(getDatabaseName(), 13)) == 0) {
								reac[j].setMeta13(value);
							}
							if (columnCount > 13 && columnName.compareTo(reactionsMetaColumnManager.getColumnName(getDatabaseName(), 14)) == 0) {
								reac[j].setMeta14(value);
							}
							if (columnCount > 14 && columnName.compareTo(reactionsMetaColumnManager.getColumnName(getDatabaseName(), 15)) == 0) {
								reac[j].setMeta15(value);
							}					
						}
					}				
				}

				PreparedStatement prep = conn.prepareStatement(
						"insert into reactions (id, knockout, flux_value, reaction_abbreviation, " 
						+ " reaction_name, reaction_string, reversible, " 
						+ " lower_bound, upper_bound, biological_objective, " 
						+ " meta_1, meta_2, meta_3, meta_4, meta_5, meta_6, meta_7, meta_8, "
						+ " meta_9, meta_10, meta_11, meta_12, meta_13, meta_14, meta_15) "
						+ " values (NULL, 'false', ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
				prep.setDouble(1, reac[j].getFluxValue());
				prep.setString(2, reac[j].getReactionAbbreviation());
				prep.setString(3, reac[j].getReactionName());
				prep.setString(4, reactionString);
				prep.setString(5, reac[j].getReversible());		
				prep.setDouble(6, reac[j].getLowerBound());
				prep.setDouble(7, reac[j].getUpperBound());
				prep.setDouble(8, reac[j].getBiologicalObjective());		
				prep.setString(9, reac[j].getMeta1());
				prep.setString(10, reac[j].getMeta2());
				prep.setString(11, reac[j].getMeta3());
				prep.setString(12, reac[j].getMeta4());
				prep.setString(13, reac[j].getMeta5());
				prep.setString(14, reac[j].getMeta6());
				prep.setString(15, reac[j].getMeta7());
				prep.setString(16, reac[j].getMeta8());
				prep.setString(17, reac[j].getMeta9());
				prep.setString(18, reac[j].getMeta10());
				prep.setString(19, reac[j].getMeta11());
				prep.setString(20, reac[j].getMeta12());
				prep.setString(21, reac[j].getMeta13());
				prep.setString(22, reac[j].getMeta14());
				prep.setString(23, reac[j].getMeta15());

				prep.addBatch();

				conn.setAutoCommit(false);
				prep.executeBatch();
				conn.setAutoCommit(true);
			}
			conn.close();
			//System.out.println("Done");
			LocalConfig.getInstance().setProgress(65);
		}catch(SQLException e){

			e.printStackTrace();

		}
		ReactionFactory aFactory = new ReactionFactory();
		aFactory.setMetabolitesUsedStatus(LocalConfig.getInstance().getDatabaseName());
	}	
}