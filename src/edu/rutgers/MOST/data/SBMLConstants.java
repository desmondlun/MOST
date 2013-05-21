package edu.rutgers.MOST.data;

public class SBMLConstants {
	
	public static final String[] REACTIONS_IGNORE_LIST =
    {"Abbreviation", "Equation", "GENE ASSOCIATION", "GENE_ASSOCIATION"
    };
	
	public static final String[] METABOLITES_IGNORE_LIST =
    {"CHARGE"
    };
	
	public static final String DUPLICATE_METAB_COLUMN_ERROR_TITLE = "Metabolites Duplicate Column Names Error";
	public static final String DUPLICATE_REAC_COLUMN_ERROR_TITLE = "Reactions Duplicate Column Names Error";
	public static final String DUPLICATE_COLUMN_ERROR_MESSAGE = "Do you wish to keep both columns? If \"No\" only the first will be used.";
	public static final String RENAME_COLUMN_MESSAGE = "Do you wish to rename a column? If \"No\", column names will be ";
	public static final String DUPLICATE_COLUMN_SUFFIX = "_1";
	
}
