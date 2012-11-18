package edu.rutgers.MOST.presentation;

public class GraphicalInterfaceConstants {
	
	public static final String TITLE = "MOST";
	
	public static final String DEFAULT_DATABASE_NAME = "untitled";
	
	public static final int BLANK_DB_METABOLITE_ROW_COUNT = 100;
	public static final int BLANK_DB_REACTION_ROW_COUNT = 100;

	public static final String[] REACTIONS_DB_COLUMN_NAMES = 
	{ 
	"id",                    
	"ko",  
	"flux_value", 
	"reaction_id",           
	"reaction_name",         
	"reaction_string",       
	"reversible",            	
	"lower_bound",           
	"upper_bound",           	
	"biological_objective"
	};
	
	public static final String[] REACTIONS_COLUMN_NAMES = 
	{ 
		"ID",
		"KO",
		"Flux Value",
		"Reaction ID",
		"Reaction Name",
		"Reaction Equation",
		"Reversible",		
		"Lower Bound",
		"Upper Bound",	
		"Biological Objective"		
	};
	
	public static final String KNOCKOUT_TOOLTIP = "Knockout";
	
	public static final String[] METABOLITES_DB_COLUMN_NAMES = 
	{ 
	"id", 
	"metabolite_id",
	"metabolite_name",
	"compartment",
	"charge",
	"boundary"
	};
	
	public static final String[] METABOLITES_COLUMN_NAMES = 
	{ 
		"ID",
		"Metabolite ID",
		"Metabolite Name",		
		"Charge",
		"Compartment",
		"Boundary Condition"
	};
	
	//reactions table column numbers and associated widths
	public static final int DEFAULT_WIDTH = 90; //columns with no assigned width use default	
    public static final int DB_REACTIONS_ID_WIDTH = 50;
    public static final int DB_REACTIONS_ID_COLUMN = 0; 
    public static final int KO_WIDTH = 60;
    public static final int KO_COLUMN = 1;
    public static final int FLUX_VALUE_COLUMN = 2;
    public static final int REACTION_ABBREVIATION_WIDTH = 100;
    public static final int REACTION_ABBREVIATION_COLUMN = 3;
    public static final int REACTION_NAME_WIDTH = 200; 
    public static final int REACTION_NAME_COLUMN = 4;
    public static final int REACTION_STRING_WIDTH = 300;
    public static final int REACTION_STRING_COLUMN = 5;
    public static final int REVERSIBLE_WIDTH = 60;
    public static final int REVERSIBLE_COLUMN = 6;         
    public static final int LOWER_BOUND_COLUMN = 7;
    public static final int UPPER_BOUND_COLUMN = 8;
    public static final int BIOLOGICAL_OBJECTIVE_COLUMN = 9;    
    public static final int REACTION_META1_COLUMN = 10;
    public static final int REACTION_META2_COLUMN = 11;
    public static final int REACTION_META3_COLUMN = 12;
    public static final int REACTION_META4_COLUMN = 13;
    public static final int REACTION_META5_COLUMN = 14;
    public static final int REACTION_META6_COLUMN = 15;
    public static final int REACTION_META7_COLUMN = 16;
    public static final int REACTION_META8_COLUMN = 17;
    public static final int REACTION_META9_COLUMN = 18;
    public static final int REACTION_META10_COLUMN = 19;
    public static final int REACTION_META11_COLUMN = 20;
    public static final int REACTION_META12_COLUMN = 21;
    public static final int REACTION_META13_COLUMN = 22;
    public static final int REACTION_META14_COLUMN = 23;
    public static final int REACTION_META15_COLUMN = 24;
    public static final int REACTION_META_DEFAULT_WIDTH = 150;
    
    //metabolites table column numbers and associated widths
    public static final int DB_METABOLITE_ID_WIDTH = 0;  
    //"id_m" to distinguish between numerical id in reactions table and metabolites table
    public static final int DB_METABOLITE_ID_COLUMN = 0;
    public static final int METABOLITE_ABBREVIATION_WIDTH = 200;
    public static final int METABOLITE_ABBREVIATION_COLUMN = 1;
    public static final int METABOLITE_NAME_WIDTH = 300;
    public static final int METABOLITE_NAME_COLUMN = 2;
    public static final int CHARGE_WIDTH = 80;
    public static final int CHARGE_COLUMN = 3;
    public static final int COMPARTMENT_WIDTH = 150;
    public static final int COMPARTMENT_COLUMN = 4;
    public static final int BOUNDARY_WIDTH = 60;
    public static final int BOUNDARY_COLUMN = 5;
    public static final int METABOLITE_META1_COLUMN = 6;
    public static final int METABOLITE_META2_COLUMN = 7;
    public static final int METABOLITE_META3_COLUMN = 8;
    public static final int METABOLITE_META4_COLUMN = 9;
    public static final int METABOLITE_META5_COLUMN = 10;
    public static final int METABOLITE_META6_COLUMN = 11;
    public static final int METABOLITE_META7_COLUMN = 12;
    public static final int METABOLITE_META8_COLUMN = 13;
    public static final int METABOLITE_META9_COLUMN = 14;
    public static final int METABOLITE_META10_COLUMN = 15;
    public static final int METABOLITE_META11_COLUMN = 16;
    public static final int METABOLITE_META12_COLUMN = 17;
    public static final int METABOLITE_META13_COLUMN = 18;
    public static final int METABOLITE_META14_COLUMN = 19;
    public static final int METABOLITE_META15_COLUMN = 20;
    public static final int USED_COLUMN = 21;
    public static final int METABOLITE_META_DEFAULT_WIDTH = 150;
    
    public static final double FLUX_VALUE_DEFAULT = 0.0;
    public static final double LOWER_BOUND_DEFAULT = 0.0;
    public static final double UPPER_BOUND_DEFAULT = 999999.0;
    public static final double BIOLOGICAL_OBJECTIVE_DEFAULT = 0.0;
    public static final String KO_DEFAULT = "false";
    public static final String REVERSIBLE_DEFAULT = "false";
    public static final String BOUNDARY_DEFAULT = "false";
        
    public static final String LOWER_BOUND_ERROR_TITLE = "Lower Bound Error.";
    public static final String LOWER_BOUND_ERROR_MESSAGE = "Reaction is irreversible and lower bound is negative. Do you wish to set it to 0?";
    
    public static final String NUMERIC_VALUE_ERROR_TITLE = "Invalid numeric entry.";
    public static final String NUMERIC_VALUE_ERROR_MESSAGE = "Number Format Error";
    
    public static final String[] BOOLEAN_VALUES = {"false", "true"};
    public static final String[] VALID_FALSE_VALUES = {"f"};
    public static final String[] VALID_TRUE_VALUES = {"t"};
    public static final String BOOLEAN_VALUE_ERROR_TITLE = "Invalid entry. Enter \"t\" for \"true\", \"f\" for \"false\"";
    public static final String BOOLEAN_VALUE_ERROR_MESSAGE = "Boolean Value Error";
	
    public static final String HELP_TOPICS_URL = "http://most.codeplex.com/wikipage?title=MOST%20Help";
    
    public static final String DB_COPIER_SUFFIX = "_orig";
    
    public static final String OPTIMIZATION_PREFIX = "OPT_";
    
    public static final String MIP_SUFFIX = "_MIP";
    
    //metabolite column names
    public static final String[] METABOLITE_ID_COLUMN_NAMES =
    {"Metabolite Id", "Metabolite Abbreviation", "Species Id", "Species Abbreviation",
    	"Id", "Compound Id", "Compound Abbreviation"
    };

    public static final String[] METABOLITE_NAME_COLUMN_NAMES =
    {"Metabolite Name", "Metabolite description", "Name", "Species Name", 
    	"Species description", "Compound Name", "Compound description"
    };
    
    public static final String[] METABOLITE_CHARGE_COLUMN_NAMES =
    {"Charge", "Metabolite charge", "Species charge", "Compound charge"
    };
    
    public static final String[] METABOLITE_COMPARTMENT_COLUMN_NAMES =
    {"Compartment"
    };    
    
    //metabolite column filtering names
    public static final String[] COMPARTMENT_FILTER =
    {"compartment"
    }; 
    
    public static final String[] CHARGE_FILTER =
    {"charge"
    };
    
    public static final String[] CHARGE_NOT_FILTER =
    {"charged"
    };
    
    public static final String[] BOUNDARY_FILTER =
    {"boundary"
    };
    
    //labels for MetaboliteColumnNameInterface
    public static final String METABOLITES_DISPLAY_LABEL = "Metabolite Column Display Name";    
    public static final String METABOLITES_FROM_FILE_LABEL = "Metabolite Column Name From File";

    //labels for ReactionColumnNameInterface
    public static final String REACTIONS_DISPLAY_LABEL = "Reactions Column Display Name";    
    public static final String REACTIONS_FROM_FILE_LABEL = "Reactions Column Name From File";
    
    //labels for ExcelSheetInterface
    public static final String TAB_NAME_LABEL = "Tab Name:";    
    public static final String SHEET_NAME_FROM_FILE_LABEL = "Sheet Name From File:";    
    
    //reaction column filtering names
    public static final String[] REVERSIBLE_COLUMN_FILTER =
    {"reversible"
    };
    
    public static final String[] LOWER_BOUND_FILTER =
    {"lower bound", "lb", "lower_bound"
    };
    
    public static final String[] UPPER_BOUND_FILTER =
    {"upper bound", "ub", "upper_bound"
    };
    
    public static final String[] BIOLOGICAL_OBJECTIVE_FILTER =
    {"objective"
    };
    
    public static final String[] KNOCKOUT_COLUMN_FILTER =
    {"knockout", "ko"
    };
    
    public static final String[] FLUX_VALUE_COLUMN_FILTER =
    {"flux"
    };
    
    //reaction column names
    public static final String[] REACTION_ID_COLUMN_NAMES =
    {"Reaction Id", "Reaction Abbreviation"
    };
    
    public static final String[] REACTION_NAME_COLUMN_NAMES =
    {"Reaction Name", "Reaction description", "Name"
    };
    
    public static final String[] REACTION_EQUATION_COLUMN_NAMES =
    {"Reaction Equation", "Equation", "Reaction"
    };
    
    public static final String[] KNOCKOUT_COLUMN_NAMES =
    {"KO", "Knockout"
    };
    
    public static final String[] FLUX_VALUE_COLUMN_NAMES =
    {"Flux Value"
    };
    
    public static final String[] REVERSIBLE_COLUMN_NAMES =
    {"Reversible"
    };
    
    public static final String[] LOWER_BOUND_COLUMN_NAMES =
    {"Lower Bound", "LB"
    };
    
    public static final String[] UPPER_BOUND_COLUMN_NAMES =
    {"Upper Bound", "UB"
    };
    
    public static final String[] BIOLOGICAL_OBJECTIVE_COLUMN_NAMES =
    {"Biological Objective"
    };
    
    public static final String[] STANDARD_SHEET_NAMES =
    {"metabolites", "reactions"
    };
    
    public static final String[] METABOLITE_TAB_NAMES =
    {"Metabolites", "Species", "Compounds"
    };
    
    public static final String[] REACTION_TAB_NAMES =
    {"Reactions"
    };
    
    public static final String EXCEL_SHEET_LABEL = " Sheet Type                                                         Sheet Name";
    
    public static final String SHEET_NAMES_LABEL = "Sheet Name";
    
    public static final String DEFAULT_METABOLITE_TABLE_TAB_NAME = "Metabolites";
    
    public static final String DEFAULT_REACTION_TABLE_TAB_NAME = " Reactions ";
    
    public static final String[] REVERSIBLE_ARROWS =
    {"<==> ", "<=>", "="
    };
    
    public static final String[] NOT_REVERSIBLE_ARROWS =
    {"=>", "-->", "->"
    };
    
    public static final String COLUMN_ADD_INTERFACE_TITLE = "Add Column";
    
    public static final String COLUMN_RENAME_INTERFACE_TITLE = "Rename Column";
    
    public static final String COLUMN_ADD_RENAME_LABEL = "Enter Column Name";
      
}

