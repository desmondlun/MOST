package edu.rutgers.MOST.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import edu.rutgers.MOST.presentation.Utilities;

// based on http://www.mkyong.com/java/java-properties-file-examples/
public class VisualizationConfigProperties {	
	private static String fileName = "visualization_config.properties";
	private static String graphMissingMetabolitesSelected;
	private static String graphCalvinCycleSelected;
	private static String scaleEdgeThicknessSelected;
	private static String ignoreWaterSelected;
	private static String showVisualizationReportSelected;

	public static String getGraphMissingMetabolitesSelected()
	{
		return graphMissingMetabolitesSelected;
	}

	public static void setGraphMissingMetabolitesSelected(
		String graphMissingMetabolitesSelected )
	{
		VisualizationConfigProperties.graphMissingMetabolitesSelected = graphMissingMetabolitesSelected;
	}

	public static String getGraphCalvinCycleSelected()
	{
		return graphCalvinCycleSelected;
	}

	public static void setGraphCalvinCycleSelected( String graphCalvinCycleSelected )
	{
		VisualizationConfigProperties.graphCalvinCycleSelected = graphCalvinCycleSelected;
	}

	public static String getScaleEdgeThicknessSelected()
	{
		return scaleEdgeThicknessSelected;
	}

	public static void setScaleEdgeThicknessSelected(
		String scaleEdgeThicknessSelected )
	{
		VisualizationConfigProperties.scaleEdgeThicknessSelected = scaleEdgeThicknessSelected;
	}

	public static String getIgnoreWaterSelected()
	{
		return ignoreWaterSelected;
	}

	public static void setIgnoreWaterSelected( String ignoreWaterSelected )
	{
		VisualizationConfigProperties.ignoreWaterSelected = ignoreWaterSelected;
	}

	public static String getShowVisualizationReportSelected()
	{
		return showVisualizationReportSelected;
	}

	public static void setShowVisualizationReportSelected(
		String showVisualizationReportSelected )
	{
		VisualizationConfigProperties.showVisualizationReportSelected = showVisualizationReportSelected;
	}

	/**
	 * Write the configuration properties to the MOST directory
	 * @param linearSolverName The string containing the linear solver name
	 * @param quadraticSolverName The string containing the quadratic solver name
	 * @param nonLinearSolverName The string containing the nonlinear solver name
	 */
	public static void writeToFile(String graphMissingMetabolitesSelected, String graphCalvinCycleSelected,
			String scaleEdgeThicknessSelected, String ignoreWaterSelected, String showVisualizationReportSelected) {

		Properties prop = new Properties();
		OutputStream output = null;

		try {
			output = new FileOutputStream(propertiesPath(fileName));

			// set the properties value
			prop.setProperty("graphMissingMetabolitesSelected", graphMissingMetabolitesSelected);
			prop.setProperty("graphCalvinCycleSelected", graphCalvinCycleSelected);
			prop.setProperty("scaleEdgeThicknessSelected", scaleEdgeThicknessSelected);
			prop.setProperty("ignoreWaterSelected", ignoreWaterSelected);
			prop.setProperty("showVisualizationReportSelected", showVisualizationReportSelected);

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
			input = new FileInputStream(propertiesPath(fileName));
			prop.load(input);
			if (prop.getProperty("graphMissingMetabolitesSelected") != null) {
				setGraphMissingMetabolitesSelected(prop.getProperty("graphMissingMetabolitesSelected"));
			}
			if (prop.getProperty("graphCalvinCycleSelected") != null) {
				setGraphCalvinCycleSelected(prop.getProperty("graphCalvinCycleSelected"));
			}
			if (prop.getProperty("scaleEdgeThicknessSelected") != null) {
				setScaleEdgeThicknessSelected(prop.getProperty("scaleEdgeThicknessSelected"));
			}
			if (prop.getProperty("ignoreWaterSelected") != null) {
				setIgnoreWaterSelected(prop.getProperty("ignoreWaterSelected"));
			}
			if (prop.getProperty("showVisualizationReportSelected") != null) {
				setShowVisualizationReportSelected(prop.getProperty("showVisualizationReportSelected"));
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
		File f = new File(propertiesPath(fileName));
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
