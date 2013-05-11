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
		"Reaction Abbreviation",
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
		"Metabolite Abbreviation",
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
    public static final int REACTION_ABBREVIATION_WIDTH = 150;
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
    public static final double LOWER_BOUND_DEFAULT = -999999.0;
    public static final double UPPER_BOUND_DEFAULT = 999999.0;
    public static final String UPPER_BOUND_DEFAULT_STRING = "999999.0";
    public static final double BIOLOGICAL_OBJECTIVE_DEFAULT = 0.0;
    public static final String KO_DEFAULT = "false";
    // if lower bound default < 0 then reversible must be true
    public static final String REVERSIBLE_DEFAULT = "true";
    public static final String BOUNDARY_DEFAULT = "false";
        
    // prefixes and suffixes
    public static final String DB_COPIER_SUFFIX = "_orig";
    
    public static final String OPTIMIZATION_PREFIX = "OPT_";
    
    public static final String MIP_SUFFIX = "_MIP"; 
    
    /*****************************************************************************/
    //metabolite column filtering names
    /*****************************************************************************/
    public static final String[] METAB_ABBREVIATION_FILTER =
    {"abbreviation", "id"
    };
    
    public static final String[] METAB_NAME_FILTER =
    {"name"
    };
    
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
      
    /*****************************************************************************/
    //reaction column filtering names  
    /*****************************************************************************/
    public static final String[] ABBREVIATION_COLUMN_FILTER =
    {"abbreviation", "reaction id"
    };
    
    public static final String[] NAME_COLUMN_FILTER =
    {"name"
    };
    
    public static final String[] EQUATION_COLUMN_FILTER =
    {"equation", "reaction"
    };
    
    public static final String[] REVERSIBLE_COLUMN_FILTER =
    {"reversible"
    };
    
    public static final String[] LOWER_BOUND_FILTER =
    {"lb", "lower"
    };
    
    public static final String[] UPPER_BOUND_FILTER =
    {"ub", "upper"
    };
    
    public static final String[] BIOLOGICAL_OBJECTIVE_FILTER =
    {"objective"
    };
    
    public static final String[] BIOLOGICAL_OBJECTIVE_NOT_FILTER =
    {"synthetic"
    };
    
    public static final String[] KNOCKOUT_COLUMN_FILTER =
    {"knockout", "ko"
    };
    
    public static final String[] FLUX_VALUE_COLUMN_FILTER =
    {"flux"
    };
        
    // messages box titles and messages
    // general 
    public static final String NUMERIC_VALUE_ERROR_TITLE = "Invalid numeric entry.";
    public static final String NUMERIC_VALUE_ERROR_MESSAGE = "Number Format Error";
    
    public static final String[] BOOLEAN_VALUES = {"false", "true"};
    public static final String[] VALID_FALSE_VALUES = {"f"};
    public static final String[] VALID_TRUE_VALUES = {"t"};
    public static final String BOOLEAN_VALUE_ERROR_TITLE = "Boolean Value Error";
    public static final String BOOLEAN_VALUE_ERROR_MESSAGE = "Invalid entry. Enter \"t\" for \"true\", \"f\" for \"false\"";
	
    // lower/upper bound/reversible
    public static final String LOWER_BOUND_ERROR_TITLE = "Lower Bound Error.";
    public static final String LOWER_BOUND_ERROR_MESSAGE = "Reaction is irreversible and lower bound is negative. Do you wish to set it to 0?";
    public static final String LOWER_BOUND_ERROR_MESSAGE2 = "Lower bound must be less than or equal to upper bound. Do you wish to set it to 0?";
    public static final String UPPER_BOUND_ERROR_TITLE = "Upper Bound Error.";
    public static final String UPPER_BOUND_ERROR_MESSAGE = "Upper bound must be greater than or equal to lower bound. Do you wish to set it to default value?";
    
    public static final String IRREVERSIBLE_REACTION_ERROR_TITLE = "Irreversible Reaction Error";
    public static final String IRREVERSIBLE_REACTION_ERROR_MESSAGE = "Irreversible reaction requires lower bound to be >= 0";
       
    public static final String REVERSIBLE_ERROR_TITLE = "Non-Editable Column";
    public static final String REVERSIBLE_ERROR_MESSAGE = "Reversible Column cannot only edited or changed by editing reaction equation.";
    
    // participating
    public static final String PARTICIPATING_METAB_ERROR_TITLE = "Participating Metabolite Deletion Error";
    public static final String PARTICIPATING_METAB_ERROR_MESSAGE = "One or more selected metabolites participate in reactions and cannot be deleted.";
     
    public static final String PARTICIPATING_METAB_PASTE_ERROR_TITLE = "Participating Metabolite Paste Error";
    public static final String PARTICIPATING_METAB_PASTE_ERROR_MESSAGE = "Invalid Paste. One or more selected metabolites participate in reactions.";
    
    public static final String PARTICIPATING_METAB_RENAME_TITLE = "Rename Participating Metabolite?";
    public static final String PARTICIPATING_METAB_RENAME_MESSAGE_PREFIX = "Renaming ";
    public static final String PARTICIPATING_METAB_RENAME_MESSAGE_SUFFIX = " will result in changing one or more reactions. Are you sure you want to do this?";
        
    // invalid reactions
    public static final String INVALID_REACTIONS_ERROR_TITLE = "Invalid Reactions Warning";
    public static final String INVALID_REACTIONS_ERROR_MESSAGE = "Model contains invalid reactions. Are you sure you wish to save?";    
    
    // replace errors
    public static final String REPLACE_ALL_ERROR_TITLE = "Replace All Error";
    public static final String REPLACE_ALL_BOOLEAN_VALUE_ERROR = "Replace value is not a valid boolean value";
    public static final String REPLACE_ALL_PARTICIPATING_ERROR_MESSAGE = "Invalid Replace. One or more selected metabolites participate in reactions and cannot be changed.";
    public static final String LOWER_BOUND_REPLACE_ALL_ERROR = "Lower bound must be less than or equal to upper bound.";
    public static final String UPPER_BOUND_REPLACE_ALL_ERROR = "Upper bound must be greater than or equal to lower bound.";
    public static final String INVALID_REPLACE_ALL_BOOLEAN_VALUE = "Boolean Value Error. Must be \"True\" or \"False\"";
    
    // paste errors
    public static final String PASTE_AREA_ERROR = "Copy Area and Paste Area are different sizes.";
    public static final String LOWER_BOUND_PASTE_ERROR = "Lower bound must be less than or equal to upper bound.";
    public static final String UPPER_BOUND_PASTE_ERROR = "Upper bound must be greater than or equal to lower bound.";
    public static final String INVALID_PASTE_BOOLEAN_VALUE = "Boolean Value Error";
    
    // clear error
    public static final String CLEAR_ERROR_MESSAGE = "One or more cells selected require a value and cannot be cleared.";
    
    // interface titles and labels
    public static final String COLUMN_ADD_INTERFACE_TITLE = "Add Column";   
    public static final String COLUMN_RENAME_INTERFACE_TITLE = "Rename Column";    
    public static final String COLUMN_ADD_RENAME_LABEL = "Enter Column Name";
      
    public static final String RENAME_METABOLITE_INTERFACE_TITLE = "Rename Metabolite";
    public static final String RENAME_METABOLITE_LABEL = "Enter New Metabolite Name";
    
    public static final String CSV_FILE_LOAD_INTERFACE_TITLE = "CSV File Load";    
    public static final String CSV_FILE_LOAD_METAB_BUTTON = "Load Metabolites File";
    public static final String CSV_FILE_LOAD_REAC_BUTTON = " Load Reactions File "; 
    
    // delete optimize database and log files
    public static final String DELETE_ASSOCIATED_FILES_TITLE = "Delete Associated Files?";
    public static final String DELETE_ASSOCIATED_FILES = "Delete Associated database and log Files?";
    
    // tab names
    public static final String DEFAULT_METABOLITE_TABLE_TAB_NAME = "Metabolites";
    
    public static final String DEFAULT_REACTION_TABLE_TAB_NAME = " Reactions ";
    
    // help
    public static final String HELP_TOPICS_URL = "https://github.com/dennisegen/MOST/wiki";
    public static final String HELP_URL_NOT_FOUND_TITLE = "URL Not Found";
    public static final String HELP_URL_NOT_FOUND_MESSAGE = "This URL may not exist. Check internet connection.";
    
    // about
    public static final String ABOUT_BOX_TITLE = "About MOST";
    public static final String ABOUT_BOX_TEXT = "MOST - Metabolic Optimization and Simulation Tool." +
	" Version 1.0.0";
    
    // other
    public static final String[] REVERSIBLE_ARROWS =
    {"<==> ", "<=>", "="
    };
    
    public static final String[] NOT_REVERSIBLE_ARROWS =
    {"=>", "-->", "->"
    };
    
    public static final String GUROBI_KEY_ERROR_TITLE = "Gurobi Key Error";
    public static final String GUROBI_KEY_ERROR = "Gurobi Key Error";
        
    public static final String LOAD_FROM_MODEL_COLLECTION_TABLE_TITLE = "Load from Model Collection Database";
    
    // spaces for alignment
    public static final String ROW_HEADER_TITLE = "   Row";
}

