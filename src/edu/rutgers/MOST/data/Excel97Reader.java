package edu.rutgers.MOST.data;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFRow;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.logic.ReactionParser;
import edu.rutgers.MOST.presentation.ProgressConstants;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class Excel97Reader {

	public boolean hasStandardSheetNames(String file) {

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			HSSFWorkbook workbook = new HSSFWorkbook(fis);	
			if ((workbook.getSheetName(0).compareTo("metabolites") == 0 && workbook.getSheetName(1).compareTo("reactions") == 0) || (workbook.getSheetName(1).compareTo("metabolites") == 0 && workbook.getSheetName(0).compareTo("reactions") == 0)) {
				return true;
			}                             

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return false;		    					
	}

	public ArrayList<String> sheetNames(String file) {
		ArrayList<String> sheetNames = new ArrayList();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			HSSFWorkbook workbook = new HSSFWorkbook(fis);	
			String sheet0 = workbook.getSheetName(0);
			String sheet1 = workbook.getSheetName(1);
			sheetNames.add(sheet0);
			sheetNames.add(sheet1);	                           
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return sheetNames;		
	}

	public ArrayList<String> metaboliteColumnNamesFromFile(String file, ArrayList<String> sheetNames) {
		ArrayList<String> metaboliteColumnNamesFromFile = new ArrayList();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			HSSFWorkbook workbook = new HSSFWorkbook(fis);	
			HSSFSheet metabolitesSheet = workbook.getSheet(sheetNames.get(0));	            
			HSSFRow metabolitesHeadersRow = metabolitesSheet.getRow(0);

			for (int h = 0; h < metabolitesHeadersRow.getLastCellNum(); h++) {
				//trim gets rid of blank fields
				if (metabolitesHeadersRow.getCell(h).toString().trim().length() > 0) {
					metaboliteColumnNamesFromFile.add(metabolitesHeadersRow.getCell(h).toString());
				}	            	
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return metaboliteColumnNamesFromFile;
	}

	public ArrayList<String> reactionColumnNamesFromFile(String file, ArrayList<String> sheetNames) {
		ArrayList<String> reactionColumnNamesFromFile = new ArrayList();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			HSSFWorkbook workbook = new HSSFWorkbook(fis);	
			HSSFSheet reactionsSheet = workbook.getSheet(sheetNames.get(1));	            
			HSSFRow reactionsHeadersRow = reactionsSheet.getRow(0);

			for (int h = 0; h < reactionsHeadersRow.getLastCellNum(); h++) {
				//trim gets rid of blank fields
				if (reactionsHeadersRow.getCell(h).toString().trim().length() > 0) {
					reactionColumnNamesFromFile.add(reactionsHeadersRow.getCell(h).toString());
				}	            	
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return reactionColumnNamesFromFile;
	}

	public void load(String file, String databaseName, ArrayList<String> sheetNames) {
		//DatabaseCreator databaseCreator = new DatabaseCreator();
		//databaseCreator.createDatabase(databaseName);

		String queryString = "jdbc:sqlite:" + databaseName + ".db";
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Connection conn;
		try {
			conn = DriverManager.getConnection(queryString);

			//based on code from http://www.kodejava.org/examples/461.html
			//
			// Create an ArrayList to store the data read from excel sheet.
			//
			List sheetData = new ArrayList();

			FileInputStream fis = null;
			try {
				//
				// Create a FileInputStream that will be use to read the 
				// excel file.
				//
				fis = new FileInputStream(file);

				//
				// Create an excel workbook from the file system.
				//
				HSSFWorkbook workbook = new HSSFWorkbook(fis);

				//metabolites sheet
				//HSSFSheet metabolitesSheet = workbook.getSheetAt(0);
				HSSFSheet metabolitesSheet = workbook.getSheet(sheetNames.get(0));
				List<String> metabolitesData = new ArrayList();
				
				//start with row 1 to avoid reading headers
				for (int r = 1; r <= metabolitesSheet.getLastRowNum(); r++) {
					LocalConfig.getInstance().setProgress((r*ProgressConstants.METABOLITE_LOAD_PERCENT)/metabolitesSheet.getLastRowNum());	
					//prevents index out of range errors
					int columnExistsMetabCorrection = 0;

					HSSFRow metabolitesRow = metabolitesSheet.getRow(r);
					for (int c = 0; c < metabolitesRow.getLastCellNum(); c++) {
						if (metabolitesRow.getCell(c) == null) {		                	 
							metabolitesData.add("");
						} else {
							metabolitesData.add(metabolitesRow.getCell(c).toString());
						}
					}
					MetaboliteFactory aFactory = new MetaboliteFactory();

					if (aFactory.metaboliteCount(metabolitesData.get(0)) == 0) {
						SBMLMetabolite aMetabolite = (SBMLMetabolite)aFactory.getMetaboliteById(r, "SBML", "untitled");//getDatabaseName()); 

						aMetabolite.setMetaboliteAbbreviation(metabolitesData.get(LocalConfig.getInstance().getMetaboliteAbbreviationColumnIndex()));	
						aMetabolite.setMetaboliteName(metabolitesData.get(LocalConfig.getInstance().getMetaboliteNameColumnIndex()));					    
						if (metabolitesData.size() > 2 && LocalConfig.getInstance().getChargeColumnIndex() > -1) {
							aMetabolite.setCharge(metabolitesData.get(LocalConfig.getInstance().getChargeColumnIndex()));
							columnExistsMetabCorrection += 1;
						}
						if (metabolitesData.size() > (columnExistsMetabCorrection + 2) && LocalConfig.getInstance().getCompartmentColumnIndex() > -1) {
							aMetabolite.setCompartment(metabolitesData.get(LocalConfig.getInstance().getCompartmentColumnIndex()));
							columnExistsMetabCorrection += 1;
						} 
						if (metabolitesData.size() > (columnExistsMetabCorrection + 2) && LocalConfig.getInstance().getBoundaryColumnIndex() > -1) {
							if (metabolitesData.get(LocalConfig.getInstance().getBoundaryColumnIndex()).compareTo("false") == 0 || metabolitesData.get(LocalConfig.getInstance().getBoundaryColumnIndex()).compareTo("FALSE") == 0 || metabolitesData.get(LocalConfig.getInstance().getBoundaryColumnIndex()).compareTo("0") == 0 || metabolitesData.get(LocalConfig.getInstance().getBoundaryColumnIndex()).compareTo("0.0") == 0) {
								aMetabolite.setBoundary("false");
							} else if (metabolitesData.get(LocalConfig.getInstance().getBoundaryColumnIndex()).compareTo("true") == 0 || metabolitesData.get(LocalConfig.getInstance().getBoundaryColumnIndex()).compareTo("TRUE") == 0 || metabolitesData.get(LocalConfig.getInstance().getBoundaryColumnIndex()).compareTo("1") == 0 || metabolitesData.get(LocalConfig.getInstance().getBoundaryColumnIndex()).compareTo("1.0") == 0) {
								aMetabolite.setBoundary("true");													
							} else {
								aMetabolite.setBoundary("false");
							}
						} else {
							aMetabolite.setBoundary("false");
						}
												
						if (metabolitesData.size() > (columnExistsMetabCorrection + 2) && LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().size() > 0) {
							aMetabolite.setMeta1(metabolitesData.get(LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(0)));
						}
						if (metabolitesData.size() > (columnExistsMetabCorrection + 3) && LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().size() > 1) {
							aMetabolite.setMeta2(metabolitesData.get(LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(1)));
						}
						if (metabolitesData.size() > (columnExistsMetabCorrection + 4) && LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().size() > 2) {
							aMetabolite.setMeta3(metabolitesData.get(LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(2)));
						}
						if (metabolitesData.size() > (columnExistsMetabCorrection + 5) && LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().size() > 3) {
							aMetabolite.setMeta4(metabolitesData.get(LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(3)));
						}
						if (metabolitesData.size() > (columnExistsMetabCorrection + 6) && LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().size() > 4) {
							aMetabolite.setMeta5(metabolitesData.get(LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(4)));
						}
						if (metabolitesData.size() > (columnExistsMetabCorrection + 7) && LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().size() > 5) {
							aMetabolite.setMeta6(metabolitesData.get(LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(5)));
						}
						if (metabolitesData.size() > (columnExistsMetabCorrection + 8) && LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().size() > 6) {
							aMetabolite.setMeta7(metabolitesData.get(LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(6)));
						}
						if (metabolitesData.size() > (columnExistsMetabCorrection + 9) && LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().size() > 7) {
							aMetabolite.setMeta8(metabolitesData.get(LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(7)));
						}
						if (metabolitesData.size() > (columnExistsMetabCorrection + 10) && LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().size() > 8) {
							aMetabolite.setMeta9(metabolitesData.get(LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(8)));
						}
						if (metabolitesData.size() > (columnExistsMetabCorrection + 11) && LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().size() > 9) {
							aMetabolite.setMeta10(metabolitesData.get(LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(9)));
						}


						PreparedStatement prep = conn.prepareStatement(
								"insert into metabolites (id, metabolite_abbreviation, metabolite_name, charge, compartment," 
										+ " boundary, meta_1, meta_2, meta_3, meta_4, meta_5, meta_6, meta_7, meta_8, meta_9, meta_10, used) " 
										+ " values (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'false');");
						prep.setString(1, aMetabolite.getMetaboliteAbbreviation());
						prep.setString(2, aMetabolite.getMetaboliteName());
						prep.setString(3, aMetabolite.getCharge());
						prep.setString(4, aMetabolite.getCompartment());
						prep.setString(5, aMetabolite.getBoundary());
						prep.setString(6, aMetabolite.getMeta1());
						prep.setString(7, aMetabolite.getMeta2());
						prep.setString(8, aMetabolite.getMeta3());
						prep.setString(9, aMetabolite.getMeta4());
						prep.setString(10, aMetabolite.getMeta5());
						prep.setString(11, aMetabolite.getMeta6());
						prep.setString(12, aMetabolite.getMeta7());
						prep.setString(13, aMetabolite.getMeta8());
						prep.setString(14, aMetabolite.getMeta9());
						prep.setString(15, aMetabolite.getMeta10());

						prep.addBatch();

						conn.setAutoCommit(false);
						prep.executeBatch();
						conn.setAutoCommit(true);


					} else {
						//System.out.println("duplicate metabolite");
					}
					//clear lists for data from next row
					metabolitesData.clear();

				}

				//reactions sheet
				//HSSFSheet reactionsSheet = workbook.getSheetAt(1);
				HSSFSheet reactionsSheet = workbook.getSheet(sheetNames.get(1));
				List<String> reactionsData = new ArrayList();	            

				//start with row 1 to avoid reading headers
				for (int r = 1; r <= reactionsSheet.getLastRowNum(); r++) {
					LocalConfig.getInstance().setProgress((r*ProgressConstants.REACTION_LOAD_PERCENT)/reactionsSheet.getLastRowNum() + ProgressConstants.METABOLITE_LOAD_PERCENT);		
					//prevents index out of range errors
					int columnExistsCorrection = 0;

					HSSFRow reactionsRow = reactionsSheet.getRow(r);
					for (int c = 0; c < reactionsRow.getLastCellNum(); c++) {
						if (reactionsRow.getCell(c) == null) {		                	 
							reactionsData.add("");
						} else {
							reactionsData.add(reactionsRow.getCell(c).toString());
						}
					}

					ReactionFactory aFactory = new ReactionFactory();

					SBMLReaction aReaction = (SBMLReaction)aFactory.getReactionById(r, "SBML", "untitled"); 
					
					if (reactionsData.size() > 0 && LocalConfig.getInstance().getKnockoutColumnIndex() > -1) {						
						if (reactionsData.get(LocalConfig.getInstance().getKnockoutColumnIndex()).compareTo("false") == 0 || reactionsData.get(LocalConfig.getInstance().getKnockoutColumnIndex()).compareTo("FALSE") == 0 || reactionsData.get(LocalConfig.getInstance().getKnockoutColumnIndex()).compareTo("0") == 0 || reactionsData.get(LocalConfig.getInstance().getKnockoutColumnIndex()).compareTo("0.0") == 0) {
							aReaction.setKnockout("false");
						} else if (reactionsData.get(LocalConfig.getInstance().getKnockoutColumnIndex()).compareTo("true") == 0 || reactionsData.get(LocalConfig.getInstance().getKnockoutColumnIndex()).compareTo("TRUE") == 0 || reactionsData.get(LocalConfig.getInstance().getKnockoutColumnIndex()).compareTo("1") == 0 || reactionsData.get(LocalConfig.getInstance().getKnockoutColumnIndex()).compareTo("1.0") == 0) {
							aReaction.setKnockout("true");													
						} else {
							aReaction.setKnockout("false");
						}
						columnExistsCorrection += 1;
					} else {
						aReaction.setKnockout("false");
					}
					
					if (LocalConfig.getInstance().getFluxValueColumnIndex() > -1) {
						aReaction.setFluxValue(Double.valueOf(reactionsData.get(LocalConfig.getInstance().getFluxValueColumnIndex())));
						columnExistsCorrection += 1;
					} else {
						aReaction.setFluxValue(0.0);
					}

					aReaction.setReactionAbbreviation(reactionsData.get(LocalConfig.getInstance().getReactionAbbreviationColumnIndex()));
					aReaction.setReactionName(reactionsData.get(LocalConfig.getInstance().getReactionNameColumnIndex()));
					String equation;
					if (reactionsData.get(LocalConfig.getInstance().getReactionEquationColumnIndex()).startsWith("[")) {
						equation = reactionsData.get(LocalConfig.getInstance().getReactionEquationColumnIndex()).substring(5);
					} else {
						equation = reactionsData.get(LocalConfig.getInstance().getReactionEquationColumnIndex());
					}
					aReaction.setReactionString(equation.trim());

					//there are three columns that must be present, hence columnExistsCorrection + 3
					if (reactionsData.size() > (columnExistsCorrection + 3) && LocalConfig.getInstance().getReversibleColumnIndex() > -1) {
						if (reactionsData.get(LocalConfig.getInstance().getReversibleColumnIndex()).compareTo("false") == 0 || reactionsData.get(LocalConfig.getInstance().getReversibleColumnIndex()).compareTo("FALSE") == 0 || reactionsData.get(LocalConfig.getInstance().getReversibleColumnIndex()).compareTo("0") == 0 || reactionsData.get(LocalConfig.getInstance().getReversibleColumnIndex()).compareTo("0.0") == 0) {
							aReaction.setReversible("false");
						} else if (reactionsData.get(LocalConfig.getInstance().getReversibleColumnIndex()).compareTo("true") == 0 || reactionsData.get(LocalConfig.getInstance().getReversibleColumnIndex()).compareTo("TRUE") == 0 || reactionsData.get(LocalConfig.getInstance().getReversibleColumnIndex()).compareTo("1") == 0 || reactionsData.get(LocalConfig.getInstance().getReversibleColumnIndex()).compareTo("1.0") == 0) {
							aReaction.setReversible("true");													
						} else {
							aReaction.setReversible(reactionsData.get(LocalConfig.getInstance().getReversibleColumnIndex()));
						}
						columnExistsCorrection += 1;
					} else {
						if (aReaction.getReactionString().contains("<") || aReaction.getReactionString().contains("=")) {
							aReaction.setReversible("true");
						} else if (aReaction.getReactionString().contains("-->") || aReaction.getReactionString().contains("->")) {
							aReaction.setReversible("false");		    		
						}
					}	    	

					if (reactionsData.size() > (columnExistsCorrection + 3) && LocalConfig.getInstance().getLowerBoundColumnIndex() > -1) {
						aReaction.setLowerBound(Float.valueOf(reactionsData.get(LocalConfig.getInstance().getLowerBoundColumnIndex())));
						columnExistsCorrection += 1;
					} else {
						aReaction.setLowerBound(-99999);
					} 
					if (LocalConfig.getInstance().getUpperBoundColumnIndex() > -1) {
						aReaction.setUpperBound(Float.valueOf(reactionsData.get(LocalConfig.getInstance().getUpperBoundColumnIndex())));
						columnExistsCorrection += 1;
					} else {
						aReaction.setUpperBound(99999);
					} 			  
					if (reactionsData.size() > (columnExistsCorrection + 3) && LocalConfig.getInstance().getBiologicalObjectiveColumnIndex() > -1) {
						aReaction.setBiologicalObjective(Float.valueOf(reactionsData.get(LocalConfig.getInstance().getBiologicalObjectiveColumnIndex())));
						columnExistsCorrection += 1;
					} else {
						aReaction.setBiologicalObjective(0);
					} 

					//there are three columns that must be present, hence columnExistsCorrection + 3 for first meta
					if (reactionsData.size() > (columnExistsCorrection + 3) && LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 0) {			    		
						aReaction.setMeta1(reactionsData.get(LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(0)));	    	
					}
					if (reactionsData.size() > (columnExistsCorrection + 4) && LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 1) {			    		
						aReaction.setMeta2(reactionsData.get(LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(1)));	    	
					}
					if (reactionsData.size() > (columnExistsCorrection + 5) && LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 2) {			    		
						aReaction.setMeta3(reactionsData.get(LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(2)));	    	
					}
					if (reactionsData.size() > (columnExistsCorrection + 6) && LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 3) {			    		
						aReaction.setMeta4(reactionsData.get(LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(3)));	    	
					}
					if (reactionsData.size() > (columnExistsCorrection + 7) && LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 4) {			    		
						aReaction.setMeta5(reactionsData.get(LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(4)));	    	
					}
					if (reactionsData.size() > (columnExistsCorrection + 8) && LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 5) {			    		
						aReaction.setMeta6(reactionsData.get(LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(5)));	    	
					}
					if (reactionsData.size() > (columnExistsCorrection + 9) && LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 6) {			    		
						aReaction.setMeta7(reactionsData.get(LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(6)));	    	
					}
					if (reactionsData.size() > (columnExistsCorrection + 10) && LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 7) {			    		
						aReaction.setMeta8(reactionsData.get(LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(7)));	    	
					}
					if (reactionsData.size() > (columnExistsCorrection + 11) && LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 8) {			    		
						aReaction.setMeta9(reactionsData.get(LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(8)));	    	
					}
					if (reactionsData.size() > (columnExistsCorrection + 12) && LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 9) {			    		
						aReaction.setMeta10(reactionsData.get(LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(9)));	    	
					}
					if (reactionsData.size() > (columnExistsCorrection + 13) && LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 10) {			    		
						aReaction.setMeta11(reactionsData.get(LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(10)));	    	
					}
					if (reactionsData.size() > (columnExistsCorrection + 14) && LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 11) {			    		
						aReaction.setMeta12(reactionsData.get(LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(11)));	    	
					}
					if (reactionsData.size() > (columnExistsCorrection + 15) && LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 12) {			    		
						aReaction.setMeta13(reactionsData.get(LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(12)));	    	
					}
					if (reactionsData.size() > (columnExistsCorrection + 16) && LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 13) {			    		
						aReaction.setMeta14(reactionsData.get(LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(13)));	    	
					}
					if (reactionsData.size() > (columnExistsCorrection + 17) && LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 14) {			    		
						aReaction.setMeta15(reactionsData.get(LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(14)));	    	
					}

					PreparedStatement prep = conn.prepareStatement(
							"insert into reactions (id, knockout, flux_value, reaction_abbreviation, "
									+ " reaction_name, reaction_string, reversible, " 
									+ " lower_bound, upper_bound, biological_objective, "
									+ " meta_1, meta_2, meta_3, meta_4, meta_5, meta_6, meta_7, meta_8, "
									+ " meta_9, meta_10, meta_11, meta_12, meta_13, meta_14, meta_15) "
									+ " values (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
					prep.setString(1, aReaction.getKnockout());
					prep.setDouble(2, aReaction.getFluxValue());			
					prep.setString(3, aReaction.getReactionAbbreviation());
					prep.setString(4, aReaction.getReactionName());
					prep.setString(5, aReaction.getReactionString());
					prep.setString(6, aReaction.getReversible());
					prep.setDouble(7, aReaction.getLowerBound());
					prep.setDouble(8, aReaction.getUpperBound());
					prep.setDouble(9, aReaction.getBiologicalObjective());   
					prep.setString(10, aReaction.getMeta1());
					prep.setString(11, aReaction.getMeta2());
					prep.setString(12, aReaction.getMeta3());
					prep.setString(13, aReaction.getMeta4());
					prep.setString(14, aReaction.getMeta5());
					prep.setString(15, aReaction.getMeta6());
					prep.setString(16, aReaction.getMeta7());
					prep.setString(17, aReaction.getMeta8());
					prep.setString(18, aReaction.getMeta9());
					prep.setString(19, aReaction.getMeta10());
					prep.setString(20, aReaction.getMeta11());
					prep.setString(21, aReaction.getMeta12());
					prep.setString(22, aReaction.getMeta13());
					prep.setString(23, aReaction.getMeta14());
					prep.setString(24, aReaction.getMeta15());

					prep.addBatch();

					conn.setAutoCommit(false);
					prep.executeBatch();
					conn.setAutoCommit(true);

					ReactionParser parser = new ReactionParser();
					if (parser.isValid(aReaction.getReactionString())) {
						ArrayList reactantsAndProducts = parser.parseReaction(aReaction.getReactionString(), r);
						aReaction.setReactantsList((ArrayList) reactantsAndProducts.get(0));
						aReaction.updateReactants();
						if (reactantsAndProducts.size() > 1) {
							aReaction.setProductsList((ArrayList) reactantsAndProducts.get(1));
							aReaction.updateProducts();
						}
					}			    	

					//clear lists for data from next row
					reactionsData.clear();	            	
				}               
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			ReactionFactory aFactory = new ReactionFactory();
			aFactory.setMetabolitesUsedStatus(LocalConfig.getInstance().getDatabaseName());
			// Close the file once all data has been read.   
			conn.close(); 

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
	}
}


