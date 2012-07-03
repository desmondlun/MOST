package edu.rutgers.MOST.data;

import java.awt.Image;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.WindowConstants;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.logic.ReactionParser;
import edu.rutgers.MOST.presentation.GraphicalInterface;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;
import edu.rutgers.MOST.presentation.MetaboliteColumnNameInterface;
import edu.rutgers.MOST.presentation.ReactionColumnNameInterface;

import au.com.bytecode.opencsv.CSVReader;

//http://beginwithjava.blogspot.com/2011/05/java-csv-file-reader.html
public class TextReactionsModelReader{
		
	public ArrayList<String> columnNamesFromFile(File file) {
		ArrayList<String> columnNamesFromFile = new ArrayList();
		
        String[] dataArray = null;
		
		//use fileReader to read first line to get headers
	    BufferedReader CSVFile;
		try {
			CSVFile = new BufferedReader(new FileReader(file));
			String dataRow = CSVFile.readLine();
			
			if ((GraphicalInterface.getSplitCharacter().compareTo(',')) == 0) {
				dataArray = dataRow.split(",");				
			} else {
				dataArray = dataRow.split("\t");
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
		
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();							
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		
		return columnNamesFromFile;
	}	
	
	public void load(File file, String databaseName) {
		int row = 1;
		//BufferedReader CSVFile;
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
			
		    CSVReader reader;
			try {
				reader = new CSVReader(new FileReader(file), GraphicalInterface.getSplitCharacter());
				String [] dataArray;
				try {
					while ((dataArray = reader.readNext()) != null) {
						for (int s = 0; s < dataArray.length; s++) {
					    	if (dataArray[s].length() > 0 && dataArray[s].substring(0,1).matches("\"")) {
					    	    dataArray[s] = dataArray[s].substring(1, (dataArray[s].length() - 1));
					    	}
					    }
						
						//start with row 1 to avoid reading headers
						if (row > 1) {
							ReactionFactory aFactory = new ReactionFactory();

							SBMLReaction aReaction = (SBMLReaction)aFactory.getReactionById(row, "SBML", "untitled"); 
							
							if (LocalConfig.getInstance().getKnockoutColumnIndex() > -1) {
								aReaction.setKnockout(dataArray[LocalConfig.getInstance().getKnockoutColumnIndex()]);
							} else {
								aReaction.setKnockout("false");
							}	
							if (LocalConfig.getInstance().getFluxValueColumnIndex() > -1) {
								aReaction.setFluxValue(Double.valueOf(dataArray[LocalConfig.getInstance().getFluxValueColumnIndex()]));
							} else {
								aReaction.setFluxValue(0.0);
							}				
							//column numbers are corrected for change of position due to insertion of "ko" column (constants - 1)
							aReaction.setReactionAbbreviation(dataArray[LocalConfig.getInstance().getReactionAbbreviationColumnIndex()]);
							aReaction.setReactionName(dataArray[LocalConfig.getInstance().getReactionNameColumnIndex()]);
							String equation;
							if (dataArray[LocalConfig.getInstance().getReactionEquationColumnIndex()].startsWith("[")) {
								equation = dataArray[LocalConfig.getInstance().getReactionEquationColumnIndex()].substring(5);
							} else {
								equation = dataArray[LocalConfig.getInstance().getReactionEquationColumnIndex()];
							}
							aReaction.setReactionString(equation.trim());
							
							if (LocalConfig.getInstance().getReversibleColumnIndex() > -1) {
								if (dataArray[LocalConfig.getInstance().getReversibleColumnIndex()].compareTo("false") == 0 || dataArray[LocalConfig.getInstance().getReversibleColumnIndex()].compareTo("FALSE") == 0 || dataArray[LocalConfig.getInstance().getReversibleColumnIndex()].compareTo("0") == 0 || dataArray[LocalConfig.getInstance().getReversibleColumnIndex()].compareTo("0.0") == 0) {
									aReaction.setReversible("false");
								} else if (dataArray[LocalConfig.getInstance().getReversibleColumnIndex()].compareTo("true") == 0 || dataArray[LocalConfig.getInstance().getReversibleColumnIndex()].compareTo("TRUE") == 0 || dataArray[LocalConfig.getInstance().getReversibleColumnIndex()].compareTo("1") == 0 || dataArray[LocalConfig.getInstance().getReversibleColumnIndex()].compareTo("1.0") == 0) {
									aReaction.setReversible("true");													
						    	} else {
						    		aReaction.setReversible(dataArray[LocalConfig.getInstance().getReversibleColumnIndex()]);
						    	}
							}
							
							//string cannot be cast to double but valueOf works, from http://www.java-examples.com/convert-java-string-double-example							
							aReaction.setLowerBound(Float.valueOf(dataArray[LocalConfig.getInstance().getLowerBoundColumnIndex()]));
							aReaction.setUpperBound(Float.valueOf(dataArray[LocalConfig.getInstance().getUpperBoundColumnIndex()]));							
							if (LocalConfig.getInstance().getBiologicalObjectiveColumnIndex() > -1) {
								aReaction.setBiologicalObjective(Float.valueOf(dataArray[LocalConfig.getInstance().getBiologicalObjectiveColumnIndex()]));							
							} else {
								aReaction.setBiologicalObjective(0);
							}
							

							if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 0) {
						    	aReaction.setMeta1(dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(0)]);
						    }
						    if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 1) {
						    	aReaction.setMeta2(dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(1)]);
						    }
						    if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 2) {
						    	aReaction.setMeta3(dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(2)]);
						    }
						    if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 3) {
						    	aReaction.setMeta4(dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(3)]);
						    }
						    if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 4) {
						    	aReaction.setMeta5(dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(4)]);
						    }
						    if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 5) {
						    	aReaction.setMeta6(dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(5)]);
						    }
						    if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 6) {
						    	aReaction.setMeta7(dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(6)]);
						    }
						    if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 7) {
						    	aReaction.setMeta8(dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(7)]);
						    }
						    if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 8) {
						    	aReaction.setMeta9(dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(8)]);
						    }
						    if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 9) {
						    	aReaction.setMeta10(dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(9)]);
						    }						
						    if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 10) {
						    	aReaction.setMeta11(dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(10)]);
						    }
						    if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 11) {
						    	aReaction.setMeta12(dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(11)]);
						    }
						    if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 12) {
						    	aReaction.setMeta13(dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(12)]);
						    }
						    if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 13) {
						    	aReaction.setMeta14(dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(13)]);
						    }
						    if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 14) {
						    	aReaction.setMeta15(dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(15)]);
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
						    	ArrayList reactantsAndProducts = parser.parseReaction(aReaction.getReactionString(), (row - 1));
						    	aReaction.setReactantsList((ArrayList) reactantsAndProducts.get(0));
						    	aReaction.updateReactants();
						    	if (reactantsAndProducts.size() > 1) {
						    		aReaction.setProductsList((ArrayList) reactantsAndProducts.get(1));
							    	aReaction.updateProducts();
						    	}
						    }
						} 
						row += 1;					    
					}
					ReactionFactory aFactory = new ReactionFactory();
					aFactory.listUsedMetabolites(LocalConfig.getInstance().getDatabaseName());
				conn.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
	}
}

