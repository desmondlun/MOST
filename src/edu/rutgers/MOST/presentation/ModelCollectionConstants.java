package edu.rutgers.MOST.presentation;

import java.util.Arrays;

public class ModelCollectionConstants {
	
	public static final String TITLE = GraphicalInterfaceConstants.TITLE + " - " + "Model Collection";

	public static final String[] VISIBLE_COLUMN_NAMES = 
		{
			"Model Version", 	
			"Organism Name",	
			"Year",	
			"Genes",	
			"Reactions",	
			"Metabolites",	
			"Reference"
		};
	
	private static java.util.List<String> visibleColumnsList = Arrays.asList(VISIBLE_COLUMN_NAMES);
	
	public static final String[] INVISIBLE_COLUMN_NAMES = 
		{
			"Path", 	
			"Type",
			"URL"
		};
	
	public static final int DEFAULT_WIDTH = 70; //columns with no assigned width use default
	public static final int MODEL_VERSION_COLUMN = visibleColumnsList.indexOf("Model Version");
	public static final int MODEL_VERSION_WIDTH = 100; 
	public static final int ORGANISM_NAME_COLUMN = visibleColumnsList.indexOf("Organism Name");
	public static final int ORGANISM_NAME_WIDTH = 200; 
	public static final int YEAR_COLUMN = visibleColumnsList.indexOf("Year");
	public static final int GENES_COLUMN = visibleColumnsList.indexOf("Genes");
	public static final int REACTIONS_COLUMN = visibleColumnsList.indexOf("Reactions");
	public static final int METABOLITES_COLUMN = visibleColumnsList.indexOf("Metabolites");
	public static final int REFERENCE_COLUMN = visibleColumnsList.indexOf("Reference");
	public static final int REFERENCE_WIDTH = 200; 
			
}
