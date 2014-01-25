package edu.rutgers.MOST.data;

public class SBMLConstants {
		
	public static final String[] REACTIONS_IGNORE_LIST =
    {"Abbreviation", "Equation", "GENE ASSOCIATION", "GENE_ASSOCIATION",
		"PROTEIN ASSOCIATION", "PROTEIN_ASSOCIATION",
		"SUBSYSTEM", "PROTEIN CLASS]", "PROTEIN_CLASS"};
	
	public static final String[] METABOLITES_IGNORE_LIST =
    {"CHARGE"
    };
	
	// accounts for sbml files where there are more than one LOCUS entry in
	// the "genes" node of notes
	public static final String LOCUS_COLUMN_DISPLAY_NAME = "Genes";
	
	public static final String DUPLICATE_METAB_COLUMN_ERROR_TITLE = "Metabolites Duplicate Column Names Error";
	public static final String DUPLICATE_REAC_COLUMN_ERROR_TITLE = "Reactions Duplicate Column Names Error";
	public static final String DUPLICATE_COLUMN_ERROR_MESSAGE = "Do you wish to keep both columns? If \"No\" only the first will be used.";
	public static final String RENAME_COLUMN_MESSAGE = "Do you wish to rename a column? If \"No\", column names will be ";
	public static final String DUPLICATE_COLUMN_SUFFIX = "_1";
	
	public static final String METABOLITE_ABBREVIATION_PREFIX = "M_";
	public static final String REACTION_ABBREVIATION_PREFIX = "R_";
	public static final String[] METABOLITE_ABBREVIATION_PREFIXES =
		{"M_", "m_", "S_", "s_"
		};
	public static final String[] REACTION_ABBREVIATION_PREFIXES =
		{"R_", "r_"
		};
	
	// Illegal characters
//	public static final String ASTERIC_REPLACEMENT = "_star_";
//	public static final String PARENTHESIS_REPLACEMENT = "_par_";
//	public static final String COLON_CHARACTER_REPLACEMENT = "_colon_";
//	public static final String APOSTROPHE_REPLACEMENT = "_apos_";
//	public static final String QUOTATION_MARK_REPLACEMENT = "_quot_";
//	public static final String NUMBER_SIGN_REPLACEMENT = "_num_";
//	public static final String AMPERSAND_REPLACEMENT = "_and_";
//	public static final String PLUS_SIGN_REPLACEMENT = "_plus_";
	
	public static final String ASTERIC_REPLACEMENT = "_";
	public static final String PARENTHESIS_REPLACEMENT = "_";
	public static final String COLON_CHARACTER_REPLACEMENT = "_";
	public static final String APOSTROPHE_REPLACEMENT = "_";
	public static final String QUOTATION_MARK_REPLACEMENT = "_";
	public static final String NUMBER_SIGN_REPLACEMENT = "_";
	public static final String AMPERSAND_REPLACEMENT = "_";
	public static final String PLUS_SIGN_REPLACEMENT = "_";
	
}
