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
	private static String gurobiFeasibility;
	private static String gurobiIntFeasibility;
	private static String gurobiOptimality;
	private static String gurobiHeuristics;
	private static String gurobiMipFocus;
	private static String gurobiNumThreads;

	/**
	 * Get the mixed-integer-linear problem solver name
	 * @return String containing the MIL solver name
	 */
	public static String getMixedIntegerLinearSolverName() {
		return mixedIntegerLinearSolverName;
	}

	/**
	 * Set the mixed-integer linear problem solver name
	 * @param mixedIntegerLinearSolverName The String containing the solver name
	 */
	public static void setMixedIntegerLinearSolverName(
			String mixedIntegerLinearSolverName) {
		ConfigProperties.mixedIntegerLinearSolverName = mixedIntegerLinearSolverName;
	}

	/**
	 * Get the quadratic problem solver name
	 * @return A string containing the quadratic solver name
	 */
	public static String getQuadraticSolverName() {
		return quadraticSolverName;
	}

	/**
	 * Set the quadratic solver name
	 * @param quadraticSolverName The String containing the solver name
	 */
	public static void setQuadraticSolverName(String quadraticSolverName) {
		ConfigProperties.quadraticSolverName = quadraticSolverName;
	}

	/**
	 * Get the Nonlinear solver name
	 * @return A string containing the nonlinear solver name
	 */
	public static String getNonlinearSolverName() {
		return nonlinearSolverName;
	}

	/**
	 * Set the nonlinear solver name
	 * @param nonlinearSolverName The string containing the nonlinear solver name
	 */
	public static void setNonlinearSolverName(String nonlinearSolverName) {
		ConfigProperties.nonlinearSolverName = nonlinearSolverName;
	}

	public static String getGurobiFeasibility() {
		return gurobiFeasibility;
	}

	public static void setGurobiFeasibility(String gurobiFeasibility) {
		ConfigProperties.gurobiFeasibility = gurobiFeasibility;
	}

	public static String getGurobiIntFeasibility() {
		return gurobiIntFeasibility;
	}

	public static void setGurobiIntFeasibility(String gurobiIntFeasibility) {
		ConfigProperties.gurobiIntFeasibility = gurobiIntFeasibility;
	}

	public static String getGurobiOptimality() {
		return gurobiOptimality;
	}

	public static void setGurobiOptimality(String gurobiOptimality) {
		ConfigProperties.gurobiOptimality = gurobiOptimality;
	}

	public static String getGurobiHeuristics() {
		return gurobiHeuristics;
	}

	public static void setGurobiHeuristics(String gurobiHeuristics) {
		ConfigProperties.gurobiHeuristics = gurobiHeuristics;
	}

	public static String getGurobiMipFocus() {
		return gurobiMipFocus;
	}

	public static void setGurobiMipFocus(String gurobiMipFocus) {
		ConfigProperties.gurobiMipFocus = gurobiMipFocus;
	}

	public static String getGurobiNumThreads() {
		return gurobiNumThreads;
	}

	public static void setGurobiNumThreads(String gurobiNumThreads) {
		ConfigProperties.gurobiNumThreads = gurobiNumThreads;
	}

	/**
	 * Write the configuration properties to the MOST directory
	 * @param linearSolverName The string containing the linear solver name
	 * @param quadraticSolverName The string containing the quadratic solver name
	 * @param nonLinearSolverName The string containing the nonlinear solver name
	 */
	public static void writeToFile(String linearSolverName, String quadraticSolverName,
			String nonLinearSolverName, String feasibility, String intFeasibility,
			String optimality, String heuristics, String mipFocus, String numThreads) {

		Properties prop = new Properties();
		OutputStream output = null;

		try {
			output = new FileOutputStream(propertiesPath("config.properties"));

			// set the properties value
			prop.setProperty("mixedIntegerLinear", linearSolverName);
			prop.setProperty("quadratic", quadraticSolverName);
			prop.setProperty("nonlinear", nonLinearSolverName);
			prop.setProperty("gurobiFeasibility", feasibility);
			prop.setProperty("gurobiIntFeasibility", intFeasibility);
			prop.setProperty("gurobiOptimality", optimality);
			prop.setProperty("gurobiHeuristics", heuristics);
			prop.setProperty("gurobiMIPFocus", mipFocus);
			prop.setProperty("gurobiNumThreads", numThreads);

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
	
	/**
	 * Read from the file and set the configurations for each of the solvers
	 * @see setNonlinearSolverName
	 * @see setQuadraticSolverName
	 * @see setMixedIntegerLinearSolverName
	 */
	public static void readFile() {
		Properties prop = new Properties();
		InputStream input = null;

		try {
			input = new FileInputStream(propertiesPath("config.properties"));
			prop.load(input);
			if (prop.getProperty("mixedIntegerLinear") != null) {
				setMixedIntegerLinearSolverName(prop.getProperty("mixedIntegerLinear"));
			}
			if (prop.getProperty("quadratic") != null) {
				setQuadraticSolverName(prop.getProperty("quadratic"));
			}
			if (prop.getProperty("nonlinear") != null) {
				setNonlinearSolverName(prop.getProperty("nonlinear"));
			}
			
			if (prop.getProperty("gurobiFeasibility") != null) {
				setGurobiFeasibility(prop.getProperty("gurobiFeasibility"));
			}
			if (prop.getProperty("gurobiIntFeasibility") != null) {
				setGurobiIntFeasibility(prop.getProperty("gurobiIntFeasibility"));
			}
			if (prop.getProperty("gurobiOptimality") != null) {
				setGurobiOptimality(prop.getProperty("gurobiOptimality"));
			}
			if (prop.getProperty("gurobiHeuristics") != null) {
				setGurobiHeuristics(prop.getProperty("gurobiHeuristics"));
			}
			if (prop.getProperty("gurobiMIPFocus") != null) {
				setGurobiMipFocus(prop.getProperty("gurobiMIPFocus"));
			}
			if (prop.getProperty("gurobiNumThreads") != null) {
				setGurobiNumThreads(prop.getProperty("gurobiNumThreads"));
			}
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
		readFile();
	}
	
	/**
	 * Create path for config.properties file
	 * @param name The string containing the file name
	 * @return the name passed to propertiesPath
	 */
	private static String propertiesPath(String name) {
		Utilities u = new Utilities();
		String fileName = u.createLogFileName(name);
		return fileName;
	}
}