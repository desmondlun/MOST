package edu.rutgers.MOST.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import au.com.bytecode.opencsv.CSVReader;
import edu.rutgers.MOST.config.LocalConfig;

public class ReactionSupplementaryMaterialReader {
	
	public ArrayList<String> columnNamesFromFile(File file, int row) {
		TextModelReader textReader = new TextModelReader();
		ArrayList<String> columnNamesFromFile = textReader.columnNamesFromFile(file, row);	
		return columnNamesFromFile;
	}

	/**
	 * 
	 * @param file
	 * @param abbreviationColumnName
	 * @param ecNumberColumnName
	 * @param trimStartIndex
	 * @param trimEndIndex
	 * Trim start index and end index used to match format of reaction abbreviations in model
	 * with abbreviations in supplementary material
	 */
	public void readFile(File file, int abbreviationColumnIndex, int ecNumberColumnIndex,
			int filetrimStartIndex, int filetrimEndIndex) {
		CSVReader reader;

		int count = 0;
		String reacAbbr = "";
		String ecNumber = "";
		
		Map<String, String> reactionAbbrECNumberMap = new HashMap<String, String>();

		try {
			reader = new CSVReader(new FileReader(file), ',');
			String [] dataArray;
			try {
				while ((dataArray = reader.readNext()) != null) {
					if (count > 0) {
						for (int s = 0; s < dataArray.length; s++) {	
							if (s == abbreviationColumnIndex) {
								//System.out.println(dataArray[s]);
								int totalTrimLength = filetrimStartIndex + filetrimEndIndex;
								if (dataArray[s] != null && dataArray[s].length() > totalTrimLength) {
									reacAbbr = dataArray[s].substring(filetrimStartIndex, dataArray[s].length() - filetrimEndIndex);
									if (reacAbbr.contains("-")) {
										reacAbbr = reacAbbr.replace("-", "_");
									}
								}
							}
							if (s == ecNumberColumnIndex) {
								ecNumber = dataArray[s];
								if (ecNumber.startsWith("EC-")) {
									ecNumber = ecNumber.substring(3);
								}
								//System.out.println(dataArray[s]);
							}
						}
						if (reacAbbr != null && reacAbbr.length() > 0 && ecNumber != null && ecNumber.length() > 0) {
							reactionAbbrECNumberMap.put(reacAbbr, ecNumber);
						}
					}
					count += 1;
				}
				reader.close();
				LocalConfig.getInstance().setReactionAbbrECNumberMap(reactionAbbrECNumberMap);
				System.out.println(reactionAbbrECNumberMap);
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
	}
	
	public static void main( String args[] )
	{
		ReactionSupplementaryMaterialReader reader = new ReactionSupplementaryMaterialReader();
		File iJR904 = new File("etc/sbml/E. Coli/iJR904/gb-2003-4-9-r54-s1.csv");
		reader.readFile(iJR904, 0, 4, 0, 0);
	}
	
}

