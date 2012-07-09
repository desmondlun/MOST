package edu.rutgers.MOST.data;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.presentation.GraphicalInterface;
import au.com.bytecode.opencsv.CSVReader;

//http://beginwithjava.blogspot.com/2011/05/java-csv-file-reader.html
public class TextMetabolitesModelReader{

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
	
	public void load(File file, String databaseName) {
		int numOfLines = numberOfLines(file);
		DatabaseCreator creator = new DatabaseCreator();		
		creator.createBlankReactionsTable(databaseName);
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

						if (row > 1) {
							MetaboliteFactory aFactory = new MetaboliteFactory();
							//check that metabolite abbreviation does not exist to 
							//avoid duplicate entries of abbreviations
							if (aFactory.metaboliteCount(dataArray[0]) == 0) {
								SBMLMetabolite aMetabolite = (SBMLMetabolite)aFactory.getMetaboliteById(row, "SBML", "untitled");//getDatabaseName()); 

								aMetabolite.setMetaboliteAbbreviation(dataArray[LocalConfig.getInstance().getMetaboliteAbbreviationColumnIndex()]);
								aMetabolite.setMetaboliteName(dataArray[LocalConfig.getInstance().getMetaboliteNameColumnIndex()]);
								if (LocalConfig.getInstance().getChargeColumnIndex() > -1) {
									aMetabolite.setCharge(dataArray[LocalConfig.getInstance().getChargeColumnIndex()]);	
								}
								if (LocalConfig.getInstance().getCompartmentColumnIndex() > -1) {
									aMetabolite.setCompartment(dataArray[LocalConfig.getInstance().getCompartmentColumnIndex()]);	
								} 
								if (LocalConfig.getInstance().getBoundaryColumnIndex() > -1) {
									if (dataArray[LocalConfig.getInstance().getBoundaryColumnIndex()].compareTo("false") == 0 || dataArray[LocalConfig.getInstance().getBoundaryColumnIndex()].compareTo("FALSE") == 0 || dataArray[LocalConfig.getInstance().getBoundaryColumnIndex()].compareTo("0") == 0 || dataArray[LocalConfig.getInstance().getBoundaryColumnIndex()].compareTo("0.0") == 0) {
										aMetabolite.setBoundary("false");
									} else if (dataArray[LocalConfig.getInstance().getBoundaryColumnIndex()].compareTo("true") == 0 || dataArray[LocalConfig.getInstance().getBoundaryColumnIndex()].compareTo("TRUE") == 0 || dataArray[LocalConfig.getInstance().getBoundaryColumnIndex()].compareTo("1") == 0 || dataArray[LocalConfig.getInstance().getBoundaryColumnIndex()].compareTo("1.0") == 0) {
										aMetabolite.setBoundary("true");													
									} else {
										aMetabolite.setBoundary("false");
									}
								} else {
									aMetabolite.setBoundary("false");
								}	
								if (LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().size() > 0) {
									aMetabolite.setMeta1(dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(0)]);
								}
								if (LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().size() > 1) {
									aMetabolite.setMeta2(dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(1)]);
								}
								if (LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().size() > 2) {
									aMetabolite.setMeta3(dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(2)]);
								}
								if (LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().size() > 3) {
									aMetabolite.setMeta4(dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(3)]);
								}
								if (LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().size() > 4) {
									aMetabolite.setMeta5(dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(4)]);
								}
								if (LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().size() > 5) {
									aMetabolite.setMeta6(dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(5)]);
								}
								if (LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().size() > 6) {
									aMetabolite.setMeta7(dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(6)]);
								}
								if (LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().size() > 7) {
									aMetabolite.setMeta8(dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(7)]);
								}
								if (LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().size() > 8) {
									aMetabolite.setMeta9(dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(8)]);
								}
								if (LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().size() > 9) {
									aMetabolite.setMeta10(dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(9)]);
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
						} 
						row += 1;	
						LocalConfig.getInstance().setProgress(row*100/numOfLines);
					}
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
		//ReactionFactory aFactory = new ReactionFactory();	
		//aFactory.setMetabolitesUsedStatus(LocalConfig.getInstance().getDatabaseName());
		LocalConfig.getInstance().setProgress(100);
	}	    	
}

