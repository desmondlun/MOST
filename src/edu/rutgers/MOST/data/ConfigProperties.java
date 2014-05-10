package edu.rutgers.MOST.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import edu.rutgers.MOST.presentation.GraphicalInterface;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;

// based on http://www.mkyong.com/java/java-properties-file-examples/
public class ConfigProperties {
	private String solverName;
	
	public String getSolverName() {
		return solverName;
	}

	public void setSolverName(String solverName) {
		this.solverName = solverName;
	}

	private static String propertiesPath() {
		String fileName = "";
		if (System.getProperty("os.name").equals("Windows 7") || System.getProperty("os.name").equals("Windows 8") || System.getProperty("os.name").equals("Windows Vista")) {
			File destDir = new File(SettingsConstants.SETTINGS_PATH_PREFIX_WINDOWS_7 + System.getProperty("user.name") + SettingsConstants.SETTINGS_PATH_SUFFIX_WINDOWS_7 + SettingsConstants.FOLDER_NAME);
			if (!destDir.exists()) {
				destDir.mkdir();				
			}
			fileName = SettingsConstants.SETTINGS_PATH_PREFIX_WINDOWS_7 + System.getProperty("user.name") + SettingsConstants.SETTINGS_PATH_SUFFIX_WINDOWS_7 + SettingsConstants.FOLDER_NAME + "config.properties";
		} else if (System.getProperty("os.name").equals("Windows XP")) {
			File destDir = new File(SettingsConstants.SETTINGS_PATH_PREFIX_WINDOWS_XP + System.getProperty("user.name") + SettingsConstants.SETTINGS_PATH_SUFFIX_WINDOWS_XP + SettingsConstants.FOLDER_NAME);
			if (!destDir.exists()) {
				destDir.mkdir();				
			}
			fileName = SettingsConstants.SETTINGS_PATH_PREFIX_WINDOWS_XP + System.getProperty("user.name") + SettingsConstants.SETTINGS_PATH_SUFFIX_WINDOWS_XP + SettingsConstants.FOLDER_NAME + "config.properties";
		} else {
			fileName = "config.properties";
		}
		
		return fileName;
	}
	
	public static void writeToFile(String solverName) {
		
		Properties prop = new Properties();
		OutputStream output = null;

		try {
			output = new FileOutputStream(propertiesPath());

			// set the properties value
			prop.setProperty("solver", solverName);
			
			// save properties
			prop.store(output, null);

		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}
	
	public static void readFile() {
		Properties prop = new Properties();
		InputStream input = null;
	 
		try {
	 
			input = new FileInputStream(propertiesPath());
	 
			// load a properties file
			prop.load(input);
	 
			// get the property value and print it out
			System.out.println(prop.getProperty("solver"));
	 
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public boolean fileExists() {
		File f = new File(propertiesPath());
		return f.exists();
	}

	public static void main(String[] args) {
        writeToFile(GraphicalInterfaceConstants.DEFAULT_SOLVER_NAME);
        readFile();
	}
}