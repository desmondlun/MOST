package edu.rutgers.MOST.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;
import edu.rutgers.MOST.presentation.Utilities;

// based on http://www.mkyong.com/java/java-properties-file-examples/
public class ConfigProperties {
	private static String mixedIntegerLinearSolverName;
	private static String quadraticSolverName;
	private static String nonlinearSolverName;

	public static String getMixedIntegerLinearSolverName() {
		return mixedIntegerLinearSolverName;
	}

	public static void setMixedIntegerLinearSolverName(
			String mixedIntegerLinearSolverName) {
		ConfigProperties.mixedIntegerLinearSolverName = mixedIntegerLinearSolverName;
	}

	public static String getQuadraticSolverName() {
		return quadraticSolverName;
	}

	public static void setQuadraticSolverName(String quadraticSolverName) {
		ConfigProperties.quadraticSolverName = quadraticSolverName;
	}

	public static String getNonlinearSolverName() {
		return nonlinearSolverName;
	}

	public static void setNonlinearSolverName(String nonlinearSolverName) {
		ConfigProperties.nonlinearSolverName = nonlinearSolverName;
	}

	/**
	 * 
	 * @param linearSolverName
	 * @param quadraticSolverName
	 * @param nonLinearSolverName
	 */
	public static void writeToFile(String linearSolverName, String quadraticSolverName,
			String nonLinearSolverName) {

		Properties prop = new Properties();
		OutputStream output = null;

		try {
			System.out.println(propertiesPath("config.properties"));
			output = new FileOutputStream(propertiesPath("config.properties"));

			// set the properties value
			prop.setProperty("mixedIntegerLinear", linearSolverName);
			prop.setProperty("quadratic", quadraticSolverName);
			prop.setProperty("nonlinear", nonLinearSolverName);

			// save properties
			prop.store(output, null);

		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					//e.printStackTrace();
				}
			}

		}
	}

	public static void readFile() {
		Properties prop = new Properties();
		InputStream input = null;

		try {
			input = new FileInputStream(propertiesPath("config.properties"));
			prop.load(input);
			setMixedIntegerLinearSolverName(prop.getProperty("mixedIntegerLinear"));
			setQuadraticSolverName(prop.getProperty("quadratic"));
			setNonlinearSolverName(prop.getProperty("nonlinear"));
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					//e.printStackTrace();
				}
			}
		}
	}

	public boolean fileExists() {
		File f = new File(propertiesPath("config.properties"));
		return f.exists();
	}

	public static void main(String[] args) {
		writeToFile(GraphicalInterfaceConstants.DEFAULT_MIXED_INTEGER_SOLVER_NAME,
				GraphicalInterfaceConstants.DEFAULT_QUADRATIC_SOLVER_NAME,
				GraphicalInterfaceConstants.DEFAULT_NONLINEAR_SOLVER_NAME);
		readFile();
	}
	
	/**
	 * Create path for config.properties file
	 * @param name
	 * @return
	 */
	private static String propertiesPath(String name) {
		Utilities u = new Utilities();
		String fileName = u.createLogFileName(name);
		return fileName;
	}
}