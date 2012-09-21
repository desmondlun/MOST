package edu.rutgers.MOST.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.presentation.GraphicalInterface;

public class TextMetabolitesModelReader {

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
		DatabaseCreator creator = new DatabaseCreator();		
		creator.createBlankReactionsTable(databaseName);

		//if first row of file in not column names, starts reading after row that contains names
		int correction = LocalConfig.getInstance().getMetabolitesNextRowCorrection();
		int row = 1;

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
			
			CSVReader reader;
			
			Map<String, Object> metaboliteIdNameMap = new HashMap<String, Object>();
			
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
				
				//sets maximum metabolite id for use in adding metabolites to metaboliteIdNameMap
				//when reactions contain metabolites not present in file being read
				LocalConfig.getInstance().setMaxMetaboliteId(numLines - 1 - correction);
				
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
						String metaboliteAbbreviation = "";
						String metaboliteName = "";
						String chargeString = "";
						String compartment = "";
						String boundary = "";
						String used = "false"; 
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
						
						//if strings contain ' (single quote), it will not execute insert statement
						//this code escapes ' as '' - sqlite syntax for escaping '
						if (dataArray[LocalConfig.getInstance().getMetaboliteAbbreviationColumnIndex()].contains("'")) {
							metaboliteAbbreviation = dataArray[LocalConfig.getInstance().getMetaboliteAbbreviationColumnIndex()].replaceAll("'", "''");
						} else {
							metaboliteAbbreviation = dataArray[LocalConfig.getInstance().getMetaboliteAbbreviationColumnIndex()];
						}
						
						metaboliteIdNameMap.put(metaboliteAbbreviation, new Integer(i - correction));
						
						if (dataArray[LocalConfig.getInstance().getMetaboliteNameColumnIndex()].contains("'")) {
							metaboliteName = dataArray[LocalConfig.getInstance().getMetaboliteNameColumnIndex()].replaceAll("'", "''");
						} else {
							metaboliteName = dataArray[LocalConfig.getInstance().getMetaboliteNameColumnIndex()];
						}
						if (LocalConfig.getInstance().getChargeColumnIndex() > -1) {
							chargeString = dataArray[LocalConfig.getInstance().getChargeColumnIndex()];	
						}
						if (LocalConfig.getInstance().getCompartmentColumnIndex() > -1) {
							if (dataArray[LocalConfig.getInstance().getCompartmentColumnIndex()].contains("'")) {
								compartment = dataArray[LocalConfig.getInstance().getCompartmentColumnIndex()].replaceAll("'", "''");	
							} else {
								compartment = dataArray[LocalConfig.getInstance().getCompartmentColumnIndex()];	
							}	
						}								
						if (LocalConfig.getInstance().getBoundaryColumnIndex() > -1) {									
							if (dataArray[LocalConfig.getInstance().getBoundaryColumnIndex()].compareTo("false") == 0 || dataArray[LocalConfig.getInstance().getBoundaryColumnIndex()].compareTo("FALSE") == 0 || dataArray[LocalConfig.getInstance().getBoundaryColumnIndex()].compareTo("0") == 0 || dataArray[LocalConfig.getInstance().getBoundaryColumnIndex()].compareTo("0.0") == 0) {
								boundary = "false"; 
							} else if (dataArray[LocalConfig.getInstance().getBoundaryColumnIndex()].compareTo("true") == 0 || dataArray[LocalConfig.getInstance().getBoundaryColumnIndex()].compareTo("TRUE") == 0 || dataArray[LocalConfig.getInstance().getBoundaryColumnIndex()].compareTo("1") == 0 || dataArray[LocalConfig.getInstance().getBoundaryColumnIndex()].compareTo("1.0") == 0) {
								boundary = "true";
							}
						} else {
							boundary = "false";
						}					
						
						if (LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().size() > 0) {
							if (dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(0)].contains("'")) {
								meta1 = dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(0)].replaceAll("'", "''");
							} else {
								meta1 = dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(0)];
							}							
						}
						if (LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().size() > 1) {
							if (dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(0)].contains("'")) {
								meta2 = dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(1)].replaceAll("'", "''");
							} else {
								meta2 = dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(1)];
							}
						}
						if (LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().size() > 2) {
							if (dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(0)].contains("'")) {
								meta3 = dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(2)].replaceAll("'", "''");
							} else {
								meta3 = dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(2)];
							}
						}
						if (LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().size() > 3) {
							if (dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(0)].contains("'")) {
								meta4 = dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(3)].replaceAll("'", "''");
							} else {
								meta4 = dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(3)];
							}
						}
						if (LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().size() > 4) {
							if (dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(0)].contains("'")) {
								meta5 = dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(4)].replaceAll("'", "''");
							} else {
								meta5 = dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(4)];
							}
						}
						if (LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().size() > 5) {
							if (dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(0)].contains("'")) {
								meta6 = dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(5)].replaceAll("'", "''");
							} else {
								meta6 = dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(5)];
							}
						}
						if (LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().size() > 6) {
							if (dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(0)].contains("'")) {
								meta7 = dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(6)].replaceAll("'", "''");
							} else {
								meta7 = dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(6)];
							}
						}
						if (LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().size() > 7) {
							if (dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(0)].contains("'")) {
								meta8 = dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(7)].replaceAll("'", "''");
							} else {
								meta8 = dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(7)];
							}
						}
						if (LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().size() > 8) {
							if (dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(0)].contains("'")) {
								meta9 = dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(8)].replaceAll("'", "''");
							} else {
								meta9 = dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(8)];
							}
						}
						if (LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().size() > 9) {
							if (dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(0)].contains("'")) {
								meta10 = dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(9)].replaceAll("'", "''");
							} else {
								meta10 = dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(9)];
							}
						}
						if (LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().size() > 10) {
							if (dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(0)].contains("'")) {
								meta11 = dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(10)].replaceAll("'", "''");
							} else {
								meta11 = dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(10)];
							}
						}
						if (LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().size() > 11) {
							if (dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(0)].contains("'")) {
								meta12 = dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(11)].replaceAll("'", "''");
							} else {
								meta12 = dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(11)];
							}
						}
						if (LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().size() > 12) {
							if (dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(0)].contains("'")) {
								meta13 = dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(12)].replaceAll("'", "''");
							} else {
								meta13 = dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(12)];
							}
						}
						if (LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().size() > 13) {
							if (dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(0)].contains("'")) {
								meta14 = dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(13)].replaceAll("'", "''");
							} else {
								meta14 = dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(13)];
							}
						}
						if (LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().size() > 14) {
							if (dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(0)].contains("'")) {
								meta15 = dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(14)].replaceAll("'", "''");
							} else {
								meta15 = dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(14)];
							}
						}
						
						String insert = "INSERT INTO metabolites(metabolite_abbreviation, metabolite_name, charge, compartment, boundary, meta_1, meta_2, "
							+ " meta_3, meta_4, meta_5, meta_6, meta_7, meta_8, meta_9, meta_10, "
							+ " meta_11, meta_12, meta_13, meta_14, meta_15, used) values" 
							+ " (" + "'" + metaboliteAbbreviation + "', '" + metaboliteName + "', '" + chargeString + "', '" + compartment + "', '" + boundary + "', '" + meta1 + "', '" + meta2 + "', '" + meta3 + "', '" + meta4 + "', '" + meta5 + "', '" + meta6 + "', '" + meta7 + "', '" + meta8 + "', '" + meta9 + "', '" + meta10 + "', '" + meta11 + "', '" + meta12 + "', '" + meta13 + "', '" + meta14 + "', '" + meta15 + "', '" + used + "');";
						stat.executeUpdate(insert);	
					}					
				}
				LocalConfig.getInstance().setMetaboliteIdNameMap(metaboliteIdNameMap);
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


