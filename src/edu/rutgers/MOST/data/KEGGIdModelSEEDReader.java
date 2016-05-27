package edu.rutgers.MOST.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import edu.rutgers.MOST.config.LocalConfig;

//based on code from http://javarevisited.blogspot.com/2012/07/read-file-line-by-line-java-example-scanner.html
public class KEGGIdModelSEEDReader {  

	public void readFile() {
		// file from http://sb.nhri.org.tw/GEMSiRV/en/Manual
		File file = new File("etc/visualization/met_KEEGtoSEED-08022456.TXT");
		//reading file line by line in Java using BufferedReader      
		FileInputStream fis = null;
		BufferedReader reader = null;
		
		Map<String, String> modelSEEDKeggIdMap = new HashMap<String, String>();

		try {
			fis = new FileInputStream(file);
			reader = new BufferedReader(new InputStreamReader(fis));

			String line = reader.readLine();
			while(line != null){
				//System.out.println(line);
				line = reader.readLine();
				line = line.trim();
				String[] splitted = line.split("\\s+");
//				for (int i = 0; i < splitted.length; i++) {
//					System.out.println(splitted[i]);
//				}
				if (splitted.length > 1 && splitted[1] != null && splitted[1].length() > 0 &&
					splitted[0] != null && splitted[0].length() > 0) {
					// only one duplicate ModelSEED id in file - cpd00027
					// corresponds to C00031 D-Glucose, C00267 alpha-D-Glucose
//					if (modelSEEDKeggIdMap.containsKey(splitted[1])) {
//						System.out.println("c " + splitted[1]);
//					} 
					modelSEEDKeggIdMap.put(splitted[1], splitted[0]);
				}
			}          

		} catch (FileNotFoundException ex) {

		} catch (IOException ex) {

		} catch (Exception ex) {

		} finally {
			try {
				reader.close();
				fis.close();
				//writer.close();
				modelSEEDKeggIdMap.put("cpd00027", "C00031");
				LocalConfig.getInstance().setModelSEEDKeggIdMap(modelSEEDKeggIdMap);
				//System.out.println(LocalConfig.getInstance().getModelSEEDKeggIdMap());
			} catch (IOException ex) {

			}
		}
	}

	public static void main(String args[]) {
		KEGGIdModelSEEDReader reader = new KEGGIdModelSEEDReader();
		reader.readFile();
	}
}

