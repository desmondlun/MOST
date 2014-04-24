package edu.rutgers.MOST.presentation;

import java.util.Arrays;

public class ModelCollectionConstants {
	
	public static final String TITLE = GraphicalInterfaceConstants.TITLE + " - " + "Model Collection";

	public static final String[] VISIBLE_COLUMN_NAMES = 
		{
			"Model Version", 	
			"Organism Name",	
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
	
	private static java.util.List<String> invisibleColumnsList = Arrays.asList(INVISIBLE_COLUMN_NAMES);
	
	public static final String MODEL_COLLECTION_FILE_NAME = "ModelCollection.csv";
	
	public static final int DEFAULT_WIDTH = 70; //columns with no assigned width use default
	public static final int MODEL_VERSION_COLUMN = visibleColumnsList.indexOf("Model Version");
	public static final int MODEL_VERSION_WIDTH = 100; 
	public static final int ORGANISM_NAME_COLUMN = visibleColumnsList.indexOf("Organism Name");
	public static final int ORGANISM_NAME_WIDTH = 200; 
	public static final int GENES_COLUMN = visibleColumnsList.indexOf("Genes");
	public static final int REACTIONS_COLUMN = visibleColumnsList.indexOf("Reactions");
	public static final int METABOLITES_COLUMN = visibleColumnsList.indexOf("Metabolites");
	public static final int REFERENCE_COLUMN = visibleColumnsList.indexOf("Reference");
	public static final int REFERENCE_WIDTH = 200; 
	public static final int PATH_COLUMN = visibleColumnsList.size() + invisibleColumnsList.indexOf("Path");
	public static final int TYPE_COLUMN = visibleColumnsList.size() + invisibleColumnsList.indexOf("Type");
	public static final int URL_COLUMN = visibleColumnsList.size() + invisibleColumnsList.indexOf("URL");
		
	public static final int WIDTH = 750;
	// each model added will increase height by 20 (cell height). Maximum height should
	// be ~500 for the best appearance. Additional models will add scroll bar.
	// height = 266 is for 9 models, w/ no scroll bar and no cut off of any cells - exact fit
	public static final int HEIGHT = 266;
	
}
