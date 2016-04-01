package edu.rutgers.MOST.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import au.com.bytecode.opencsv.CSVReader;
import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;
import edu.rutgers.MOST.presentation.Utilities;

public class TextMetabolitesModelReader {

	private static DefaultTableModel metabolitesTableModel;
	
	public static DefaultTableModel getMetabolitesTableModel() {
		return metabolitesTableModel;
	}

	public static void setMetabolitesTableModel(
			DefaultTableModel metabolitesTableModel) {
		TextMetabolitesModelReader.metabolitesTableModel = metabolitesTableModel;
	}

	public static Map<Object, String> metaboliteIdNameMap = new HashMap<Object, String>();
	public static Map<Object, String> metaboliteIdCompartmentMap = new HashMap<Object, String>();
	private ArrayList<String> compartments = new ArrayList<String>();
	
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
			JOptionPane.showMessageDialog(null,                
					"File Not Found Error.",                
					"Error",                                
					JOptionPane.ERROR_MESSAGE);
			//e1.printStackTrace();							
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null,                
					"File Not Found Error.",                
					"Error",                                
					JOptionPane.ERROR_MESSAGE);
			//e1.printStackTrace();
		} 		
		return columnNamesFromFile;
	}

	public Integer numberOfLines(File file) {
		int count = 0;
		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(file), ',');
			try {
				while ((reader.readNext()) != null) {
					count++; 	
				}
				reader.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,                
						"File Not Found Error.",                
						"Error",                                
						JOptionPane.ERROR_MESSAGE);
				//e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null,                
					"File Not Found Error.",                
					"Error",                                
					JOptionPane.ERROR_MESSAGE);
			//e.printStackTrace();
		}

		return count;		
	}

	public void load(File file){
		Utilities u = new Utilities();
		DefaultTableModel metabTableModel = new DefaultTableModel();
		for (int m = 0; m < GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length; m++) {
			metabTableModel.addColumn(GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES[m]);
		}
		for (int n = 0; n < LocalConfig.getInstance().getMetabolitesMetaColumnNames().size(); n++) {
			metabTableModel.addColumn(LocalConfig.getInstance().getMetabolitesMetaColumnNames().get(n));
		}
		LocalConfig.getInstance().getMetaboliteUsedMap().clear();
		LocalConfig.getInstance().getSuspiciousMetabolites().clear();
		LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().clear();

		//if first row of file is not column names, starts reading after row that contains names
		int correction = LocalConfig.getInstance().getMetabolitesNextRowCorrection();
		int row = 1;
		
		CSVReader reader;
	
		try {
			reader = new CSVReader(new FileReader(file), ',');
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null,                
					"File Not Found Error.",                
					"Error",                                
					JOptionPane.ERROR_MESSAGE);
			//e.printStackTrace();
		}
		
		try {
			reader = new CSVReader(new FileReader(file), ',');
			int numLines = numberOfLines(file);
			
			//sets maximum metabolite id for use in adding metabolites to metaboliteIdNameMap
			//when reactions contain metabolites not present in file being read
			LocalConfig.getInstance().setMaxMetabolite(numLines - 1 - correction);
			LocalConfig.getInstance().setMaxMetaboliteId(numLines - 1 - correction);
			
			int id = 0;
			for (int i = 0; i < numLines; i++) {
				String [] dataArray = reader.readNext();
				for (int s = 0; s < dataArray.length; s++) {
					if (dataArray[s].length() > 0 && dataArray[s].substring(0,1).matches("\"")) {
						dataArray[s] = dataArray[s].substring(1, (dataArray[s].length() - 1));
					}
				}
				if (i >= (row + correction)) {
					Vector <String> metabRow = new Vector<String>();
					metabRow.add(Integer.toString(id));
					String compartment = "";
					String boundary = "";
					
	                String metaboliteAbbreviation = dataArray[LocalConfig.getInstance().getMetaboliteAbbreviationColumnIndex()];
	                
	                // appends suffix on duplicate abbreviations
					if (metaboliteAbbreviation == null || metaboliteAbbreviation.trim().length() == 0) {
							
					} else {
						if (LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().containsKey(metaboliteAbbreviation)) {
							metaboliteAbbreviation = metaboliteAbbreviation + u.duplicateSuffix(metaboliteAbbreviation, LocalConfig.getInstance().getMetaboliteAbbreviationIdMap());
						}
						LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().put(metaboliteAbbreviation, id);
					}
					metabRow.add(metaboliteAbbreviation);
					
					metabRow.add(dataArray[LocalConfig.getInstance().getMetaboliteNameColumnIndex()]);
					metaboliteIdNameMap.put(new Integer(id), dataArray[LocalConfig.getInstance().getMetaboliteNameColumnIndex()]);
					
					// This code is necessary so that if charge column is not in model
					// no error is thrown, just a column of all blank values
					if (LocalConfig.getInstance().getChargeColumnIndex() > -1) {
						metabRow.add(dataArray[LocalConfig.getInstance().getChargeColumnIndex()]);
					} else {
						metabRow.add("");
					}	
					
					if (LocalConfig.getInstance().getCompartmentColumnIndex() > -1) {
						compartment = dataArray[LocalConfig.getInstance().getCompartmentColumnIndex()];	
						metaboliteIdCompartmentMap.put(new Integer(id), compartment);
						if (!compartments.contains(compartment)) {
							compartments.add(compartment);
						}
					}	
					metabRow.add(compartment);
					if (LocalConfig.getInstance().getBoundaryColumnIndex() > -1) {									
						if (dataArray[LocalConfig.getInstance().getBoundaryColumnIndex()].compareTo("false") == 0 || dataArray[LocalConfig.getInstance().getBoundaryColumnIndex()].compareTo("FALSE") == 0 || dataArray[LocalConfig.getInstance().getBoundaryColumnIndex()].compareTo("0") == 0 || dataArray[LocalConfig.getInstance().getBoundaryColumnIndex()].compareTo("0.0") == 0) {
							boundary = GraphicalInterfaceConstants.BOOLEAN_VALUES[0]; 
						} else if (dataArray[LocalConfig.getInstance().getBoundaryColumnIndex()].compareTo("true") == 0 || dataArray[LocalConfig.getInstance().getBoundaryColumnIndex()].compareTo("TRUE") == 0 || dataArray[LocalConfig.getInstance().getBoundaryColumnIndex()].compareTo("1") == 0 || dataArray[LocalConfig.getInstance().getBoundaryColumnIndex()].compareTo("1.0") == 0) {
							boundary = GraphicalInterfaceConstants.BOOLEAN_VALUES[1];
						}
					} else {
						boundary = GraphicalInterfaceConstants.BOOLEAN_VALUES[0];
					}					
					metabRow.add(boundary);
					
					if (LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().size() > 0) {
						for (int m = 0; m < LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().size(); m++) {
							metabRow.add(dataArray[LocalConfig.getInstance().getMetabolitesMetaColumnIndexList().get(m)]);
						}
					}
					metabTableModel.addRow(metabRow);
					id += 1;
				}				
			}
			ArrayList<SBMLCompartment> listOfCompartments = new ArrayList<SBMLCompartment>();
			for (int c = 0; c < compartments.size(); c++) {
				SBMLCompartment comp = new SBMLCompartment();
				comp.setId(compartments.get(c));
				//comp.setName(compartments.get(c));
				listOfCompartments.add(comp);
			}
			LocalConfig.getInstance().setListOfCompartments(listOfCompartments);
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null,                
					"File Not Found Error.",                
					"Error",                                
					JOptionPane.ERROR_MESSAGE);
			//e.printStackTrace();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,                
					"File Not Found Error.",                
					"Error",                                
					JOptionPane.ERROR_MESSAGE);
			//e.printStackTrace();
		}
		
		LocalConfig.getInstance().setMetaboliteIdNameMap(metaboliteIdNameMap);
		//System.out.println(LocalConfig.getInstance().getMetaboliteIdNameMap());
		LocalConfig.getInstance().setMetaboliteIdCompartmentMap(metaboliteIdCompartmentMap);
		//System.out.println(LocalConfig.getInstance().getMetaboliteIdCompartmentMap());
		LocalConfig.getInstance().hasMetabolitesFile = true;
		setMetabolitesTableModel(metabTableModel);
		//System.out.println("Done");		
	}
	
}


