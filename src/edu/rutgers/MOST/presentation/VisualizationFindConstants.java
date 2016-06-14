package edu.rutgers.MOST.presentation;

import java.util.Arrays;

public class VisualizationFindConstants {
	
	public static final String KEGG_METABOLITE_ID_ITEM = "KEGG Metabolite ID";
	public static final String EC_NUMBER_ITEM = "EC Number";
	public static final String KEGG_REACTION_ID_ITEM = "KEGG Reaction ID";
	
	public static final String[] FIND_BY_COLUMN_LIST = 
		{ 
		GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN_NAME, 
		KEGG_METABOLITE_ID_ITEM, EC_NUMBER_ITEM, KEGG_REACTION_ID_ITEM, 
		GraphicalInterfaceConstants.REACTION_ABBREVIATION_COLUMN_NAME
		};

	public static java.util.List<String> findByColumnList = Arrays.asList(FIND_BY_COLUMN_LIST);

	public static boolean MATCH_CASE_DEFAULT = false;
	public static boolean SEARCH_BACKWARDS_DEFAULT = false;
	public static boolean WRAP_AROUND_DEFAULT = true;
	public static boolean EXACT_MATCH_DEFAULT = false;

}
