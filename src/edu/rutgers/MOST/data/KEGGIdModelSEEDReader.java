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
		// original files from http://sb.nhri.org.tw/GEMSiRV/en/Manual and 
		// http://dx.doi.org/10.1186/gb-2009-10-6-r69
		File file = new File("etc/visualization/KEEGtoSEED.txt");
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
				if (splitted.length > 1 && splitted[1] != null && splitted[1].length() > 0 &&
					splitted[0] != null && splitted[0].length() > 0) {
//					if (modelSEEDKeggIdMap.containsKey(splitted[1].trim())) {
//						System.out.println("c " + splitted[1].trim());
//					} 
					modelSEEDKeggIdMap.put(splitted[1].trim(), splitted[0].trim());
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

