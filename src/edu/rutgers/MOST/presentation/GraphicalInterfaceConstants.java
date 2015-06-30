package edu.rutgers.MOST.presentation;

import java.awt.Color;
import java.util.Arrays;

public class GraphicalInterfaceConstants
{

	public static final String TITLE = "MOST";

	public static final String DEFAULT_MODEL_NAME = "untitled";

	public static final int BLANK_METABOLITE_ROW_COUNT = 2000;
	public static final int BLANK_REACTION_ROW_COUNT = 2000;

	public static final String REACTION_ID_COLUMN_NAME = "ID";
	public static final String KNOCKOUT_COLUMN_NAME = "KO";
	public static final String FLUX_VALUE_COLUMN_NAME = "Flux Value";
	public static final String MINIMUM_FLUX_COLUMN_NAME = "Min. Flux";
	public static final String MAXIMUM_FLUX_COLUMN_NAME = "Max. Flux";
	public static final String REACTION_ABBREVIATION_COLUMN_NAME = "Reaction Abbreviation";
	public static final String REACTION_NAME_COLUMN_NAME = "Reaction Name";
	public static final String REACTION_EQUATION_ABBR_COLUMN_NAME = "Reaction Equation (Metabolite Abbreviation)";
	public static final String REACTION_EQUATION_NAMES_COLUMN_NAME = "Reaction Equation (Metabolite Name)";
	public static final String REVERSIBLE_COLUMN_NAME = "Reversible";
	public static final String LOWER_BOUND_COLUMN_NAME = "Lower Bound";
	public static final String UPPER_BOUND_COLUMN_NAME = "Upper Bound";
	public static final String BIOLOGICAL_OBJECTIVE_COLUMN_NAME = "Biological Objective";
	public static final String SYNTHETIC_OBJECTIVE_COLUMN_NAME = "Synthetic Objective";
	public static final String GENE_ASSOCIATION_COLUMN_NAME = "Gene Association";
	public static final String PROTEIN_ASSOCIATION_COLUMN_NAME = "Protein Association";
	public static final String SUBSYSTEM_COLUMN_NAME = "Subsystem";
	public static final String PROTEIN_CLASS_COLUMN_NAME = "Protein Class";

	public static final String[] REACTIONS_COLUMN_NAMES = {
		REACTION_ID_COLUMN_NAME, KNOCKOUT_COLUMN_NAME, FLUX_VALUE_COLUMN_NAME,
		MINIMUM_FLUX_COLUMN_NAME, MAXIMUM_FLUX_COLUMN_NAME,
		REACTION_ABBREVIATION_COLUMN_NAME, REACTION_NAME_COLUMN_NAME,
		REACTION_EQUATION_ABBR_COLUMN_NAME,
		REACTION_EQUATION_NAMES_COLUMN_NAME, REVERSIBLE_COLUMN_NAME,
		LOWER_BOUND_COLUMN_NAME, UPPER_BOUND_COLUMN_NAME,
		BIOLOGICAL_OBJECTIVE_COLUMN_NAME, SYNTHETIC_OBJECTIVE_COLUMN_NAME,
		GENE_ASSOCIATION_COLUMN_NAME, PROTEIN_ASSOCIATION_COLUMN_NAME,
		SUBSYSTEM_COLUMN_NAME, PROTEIN_CLASS_COLUMN_NAME };

	private static java.util.List< String > reactionsList = Arrays
		.asList( REACTIONS_COLUMN_NAMES );

	public static final String KNOCKOUT_TOOLTIP = "Knockout";
	public static final String MIN_FLUX_TOOLTIP = "Minimum Flux Value";
	public static final String MAX_FLUX_TOOLTIP = "Maximum Flux Value";

	public static final String METABOLITE_ID_COLUMN_NAME = "ID";
	public static final String METABOLITE_ABBREVIATION_COLUMN_NAME = "Metabolite Abbreviation";
	public static final String METABOLITE_NAME_COLUMN_NAME = "Metabolite Name";
	public static final String CHARGE_COLUMN_NAME = "Charge";
	public static final String COMPARTMENT_COLUMN_NAME = "Compartment";
	public static final String BOUNDARY_CONDITION_COLUMN_NAME = "Boundary Condition";

	public static final String[] METABOLITES_COLUMN_NAMES = {
		METABOLITE_ID_COLUMN_NAME, METABOLITE_ABBREVIATION_COLUMN_NAME,
		METABOLITE_NAME_COLUMN_NAME, CHARGE_COLUMN_NAME,
		COMPARTMENT_COLUMN_NAME, BOUNDARY_CONDITION_COLUMN_NAME };

	private static java.util.List< String > metabolitesList = Arrays
		.asList( METABOLITES_COLUMN_NAMES );

	// reactions table column numbers and associated widths
	public static final int DEFAULT_WIDTH = 90; // columns with no assigned
												// width use default
	public static final int REACTIONS_ID_COLUMN = reactionsList
		.indexOf( REACTION_ID_COLUMN_NAME );
	public static final int REACTIONS_ID_WIDTH = 50;
	public static final int KO_COLUMN = reactionsList
		.indexOf( KNOCKOUT_COLUMN_NAME );
	public static final int KO_WIDTH = 60;
	public static final int FLUX_VALUE_COLUMN = reactionsList
		.indexOf( FLUX_VALUE_COLUMN_NAME );
	public static final int MIN_FLUX_COLUMN = reactionsList
		.indexOf( MINIMUM_FLUX_COLUMN_NAME );
	public static final int MAX_FLUX_COLUMN = reactionsList
		.indexOf( MAXIMUM_FLUX_COLUMN_NAME );
	public static final int REACTION_ABBREVIATION_WIDTH = 150;
	public static final int REACTION_ABBREVIATION_COLUMN = reactionsList
		.indexOf( REACTION_ABBREVIATION_COLUMN_NAME );
	public static final int REACTION_NAME_WIDTH = 200;
	public static final int REACTION_NAME_COLUMN = reactionsList
		.indexOf( REACTION_NAME_COLUMN_NAME );
	public static final int REACTION_EQUN_ABBR_WIDTH = 300;
	public static final int REACTION_EQUN_ABBR_COLUMN = reactionsList
		.indexOf( REACTION_EQUATION_ABBR_COLUMN_NAME );
	public static final int REACTION_EQUN_NAMES_WIDTH = 300;
	public static final int REACTION_EQUN_NAMES_COLUMN = reactionsList
		.indexOf( REACTION_EQUATION_NAMES_COLUMN_NAME );
	public static final int REVERSIBLE_WIDTH = 60;
	public static final int REVERSIBLE_COLUMN = reactionsList
		.indexOf( REVERSIBLE_COLUMN_NAME );
	public static final int LOWER_BOUND_COLUMN = reactionsList
		.indexOf( LOWER_BOUND_COLUMN_NAME );
	public static final int UPPER_BOUND_COLUMN = reactionsList
		.indexOf( UPPER_BOUND_COLUMN_NAME );
	public static final int BIOLOGICAL_OBJECTIVE_COLUMN = reactionsList
		.indexOf( BIOLOGICAL_OBJECTIVE_COLUMN_NAME );
	public static final int SYNTHETIC_OBJECTIVE_COLUMN = reactionsList
		.indexOf( SYNTHETIC_OBJECTIVE_COLUMN_NAME );
	public static final int GENE_ASSOCIATION_COLUMN = reactionsList
		.indexOf( GENE_ASSOCIATION_COLUMN_NAME );
	public static final int PROTEIN_ASSOCIATION_COLUMN = reactionsList
		.indexOf( PROTEIN_ASSOCIATION_COLUMN_NAME );
	public static final int SUBSYSTEM_COLUMN = reactionsList
		.indexOf( SUBSYSTEM_COLUMN_NAME );
	public static final int PROTEIN_CLASS_COLUMN = reactionsList
		.indexOf( PROTEIN_CLASS_COLUMN_NAME );
	public static final int REACTION_META_DEFAULT_WIDTH = 150;

	// metabolites table column numbers and associated widths
	public static final int METABOLITE_ID_WIDTH = 50;
	// "id_m" to distinguish between numerical id in reactions table and
	// metabolites table
	public static final int METABOLITE_ID_COLUMN = metabolitesList
		.indexOf( METABOLITE_ID_COLUMN_NAME );
	public static final int METABOLITE_ABBREVIATION_WIDTH = 200;
	public static final int METABOLITE_ABBREVIATION_COLUMN = metabolitesList
		.indexOf( METABOLITE_ABBREVIATION_COLUMN_NAME );
	public static final int METABOLITE_NAME_WIDTH = 300;
	public static final int METABOLITE_NAME_COLUMN = metabolitesList
		.indexOf( METABOLITE_NAME_COLUMN_NAME );
	public static final int CHARGE_WIDTH = 80;
	public static final int CHARGE_COLUMN = metabolitesList
		.indexOf( CHARGE_COLUMN_NAME );
	public static final int COMPARTMENT_WIDTH = 150;
	public static final int COMPARTMENT_COLUMN = metabolitesList
		.indexOf( COMPARTMENT_COLUMN_NAME );
	public static final int BOUNDARY_WIDTH = 60;
	public static final int BOUNDARY_COLUMN = metabolitesList
		.indexOf( BOUNDARY_CONDITION_COLUMN_NAME );
	public static final int METABOLITE_META_DEFAULT_WIDTH = 150;

	public static final double FLUX_VALUE_DEFAULT = 0;
	public static final String FLUX_VALUE_DEFAULT_STRING = "0";
	public static final double MIN_FLUX_DEFAULT = 0;
	public static final String MIN_FLUX_DEFAULT_STRING = "0";
	public static final double MAX_FLUX_DEFAULT = 0;
	public static final String MAX_FLUX_DEFAULT_STRING = "0";
	public static final double LOWER_BOUND_DEFAULT = 0.0;
	public static final double LOWER_BOUND_REVERSIBLE_CSV_DEFAULT = Double.valueOf("-" + GraphicalInterfaceConstants.VALID_INFINITY_ENTRY);
	public static final double LOWER_BOUND_REVERSIBLE_DEFAULT = -999999.0;
	public static final String LOWER_BOUND_DEFAULT_IRREVERBIBLE_STRING = "0.0";
	public static final String LOWER_BOUND_DEFAULT_REVERSIBLE_STRING = "-999999.0";
	public static final double UPPER_BOUND_DEFAULT = 999999.0;
	public static final double UPPER_BOUND_CSV_DEFAULT = Double.valueOf(GraphicalInterfaceConstants.VALID_INFINITY_ENTRY);
	public static final String UPPER_BOUND_DEFAULT_STRING = "999999.0";
	public static final double BIOLOGICAL_OBJECTIVE_DEFAULT = 0.0;
	public static final String BIOLOGICAL_OBJECTIVE_DEFAULT_STRING = "0.0";
	public static final double SYNTHETIC_OBJECTIVE_DEFAULT = 0.0;
	public static final String SYNTHETIC_OBJECTIVE_DEFAULT_STRING = "0.0";
	public static final String KO_DEFAULT = "false";
	// if lower bound default < 0 then reversible must be true
	public static final String REVERSIBLE_DEFAULT = "true";
	public static final String BOUNDARY_DEFAULT = "false";

	public static final double MIN_DECIMAL_FORMAT = 0.0001;
	public static final double MAX_DECIMAL_FORMAT = 10000;

	// prefixes and suffixes
	public static final String DB_COPIER_SUFFIX = "_orig";

	public static final String OPTIMIZATION_PREFIX = "OPT_";

	public static final String MIP_SUFFIX = "_MIP";

	/*****************************************************************************/
	// metabolite column filtering names
	/*****************************************************************************/
	public static final String[] METAB_ABBREVIATION_FILTER = { "abbreviation",
		"id" };

	public static final String[] METAB_ABBREVIATION_NOT_FILTER = { "required",
		"recommended", "optional" };

	public static final String[] METAB_NAME_FILTER = { "name" };

	public static final String[] COMPARTMENT_FILTER = { "compartment" };

	public static final String[] CHARGE_FILTER = { "charge" };

	public static final String[] CHARGE_NOT_FILTER = { "charged" };

	public static final String[] BOUNDARY_FILTER = { "boundary" };

	/*****************************************************************************/
	// reaction column filtering names
	/*****************************************************************************/
	public static final String[] ABBREVIATION_COLUMN_FILTER = { "abbreviation",
		"reaction id" };

	public static final String[] ABBREVIATION_COLUMN_NOT_FILTER = { "metabolite" };

	public static final String[] NAME_COLUMN_FILTER = { "name" };

	public static final String[] NAME_COLUMN_NOT_FILTER = { "metabolite" };

	public static final String[] EQUATION_COLUMN_FILTER = { "equation",
		"reaction" };

	public static final String[] EQUATION_COLUMN_NOT_FILTER = { "metabolite name" };

	public static final String[] REVERSIBLE_COLUMN_FILTER = { "reversible" };

	public static final String[] LOWER_BOUND_FILTER = { "lb", "lower" };

	public static final String[] UPPER_BOUND_FILTER = { "ub", "upper" };

	public static final String[] BIOLOGICAL_OBJECTIVE_FILTER = { "obj" };

	public static final String[] BIOLOGICAL_OBJECTIVE_NOT_FILTER = { "syn" };

	public static final String[] SYNTHETIC_OBJECTIVE_FILTER = { "obj" };

	public static final String[] SYNTHETIC_OBJECTIVE_NOT_FILTER = { "bio" };

	public static final String[] KNOCKOUT_COLUMN_FILTER = { "knockout", "ko" };

	public static final String[] FLUX_VALUE_COLUMN_FILTER = { "flux" };

	public static final String[] FLUX_VALUE_NOT_FILTER = { "min", "max" };

	public static final String[] GENE_ASSOCIATION_COLUMN_FILTER = { "gene",
		"assoc" };

	public static final String[] PROTEIN_ASSOCIATION_COLUMN_FILTER = {
		"protein", "assoc" };

	public static final String[] SUBSYSTEM_COLUMN_FILTER = { "subsystem" };

	public static final String[] PROTEIN_CLASS_COLUMN_FILTER = { "protein",
		"class" };

	public static final String[] REACTIONS_COLUMN_IGNORE_LIST = { "Reaction Equation (Metabolite Name)" };

	// messages box titles and messages
	// general
	public static final String NUMERIC_VALUE_ERROR_TITLE = "Invalid numeric entry.";
	public static final String NUMERIC_VALUE_ERROR_MESSAGE = "Number Format Error.";
	public static final String INTEGER_VALUE_ERROR_TITLE = "Value not an Integer.";
	public static final String INTEGER_VALUE_ERROR_MESSAGE = "Number Format Error.";

	public static final String[] BOOLEAN_VALUES = { "false", "true" };
	public static final String[] VALID_FALSE_VALUES = { "f" };
	public static final String[] VALID_TRUE_VALUES = { "t" };
	public static final String BOOLEAN_VALUE_ERROR_TITLE = "Boolean Value Error";
	public static final String BOOLEAN_VALUE_ERROR_MESSAGE = "Invalid Boolean Value";
	// public static final String BOOLEAN_VALUE_ERROR_MESSAGE =
	// "Invalid entry. Enter \"t\" for \"true\", \"f\" for \"false\"";

	public static final String[] VALID_INFINITY_VALUES = { "inf", "infinity" };
	public static final String VALID_INFINITY_ENTRY = "Infinity";

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
	public static final String PARTICIPATING_METAB_RENAME_MESSAGE_PREFIX = "Renaming '";
	public static final String PARTICIPATING_METAB_RENAME_MESSAGE_SUFFIX = "' will result in changing one or more reactions. Are you sure you want to do this?";

	public static final String METABOLITE_RENAME_ERROR_TITLE = "Rename Error";
	public static final String METABOLITE_RENAME_ERROR_MESSAGE = "Metabolite Abbreviation Cannot Be Blank if it Participates in Reactions";

	// duplicate metabolite
	public static final String DUPLICATE_METABOLITE_TITLE = "Duplicate Metabolite";
	public static final String DUPLICATE_METABOLITE_MESSAGE = "Duplicate Metabolite. Rename as ";
	public static final String DUPLICATE_METABOLITE_PASTE_MESSAGE = "Duplicate Metabolites. Names Will Be Appended With '[1]', '[2]', etc.";

	public static final String DUPLICATE_SUFFIX = "[1]";

	// duplicate metabolite
	public static final String DUPLICATE_REACTION_TITLE = "Duplicate Reaction";
	public static final String DUPLICATE_REACTION_MESSAGE = "Duplicate Reaction. Rename as ";
	public static final String DUPLICATE_REACTION_PASTE_MESSAGE = "Duplicate Reactions. Names Will Be Appended With '[1]', '[2]', etc.";

	// invalid reactions
	public static final String INVALID_REACTIONS_ENTRY_ERROR_TITLE = "Invalid Reaction";
	public static final String INVALID_REACTIONS_ENTRY_ERROR_MESSAGE = "Invalid Reaction Format. This is probably a result of incorrect spacing.\nFor example, a -> b is valid, a->b is not valid.";
	public static final String EQUATION_NAMES_ERROR_TITLE = "Equation Not Editable";
	public static final String EQUATION_NAMES_ERROR_MESSAGE = "Reaction equation with names is not editable";

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
	public static final String PASTE_OUT_OF_RANGE_ERROR = "Paste area out of range";

	// clear error
	public static final String CLEAR_ERROR_MESSAGE = "One or more cells selected require a value and cannot be cleared.";

	// interface titles and labels
	public static final String COLUMN_ADD_INTERFACE_TITLE = "Add Column";
	public static final String COLUMN_RENAME_INTERFACE_TITLE = "Rename Column";
	public static final String COLUMN_ADD_RENAME_LABEL = "Enter Column Name:";

	public static final String ADD_ROWS_DIALOG_TITLE = "Add Rows";
	public static final String ADD_ROWS_DIALOG_LABEL = "Enter Number of Rows to Add:";
	public static final String DEFAULT_NUM_ADD_ROWS = "10";

	// to prevent entering an outrageous number like 1000000;
	public static final int MAX_NUM_ADD_ROWS = 20000;
	public static final String ADD_ROWS_OUT_OF_RANGE_TITLE = "Out of Range Error";
	public static final String ADD_ROWS_OUT_OF_RANGE_MESSAGE = "Value out of range.";

	public static final String RENAME_METABOLITE_INTERFACE_TITLE = "Rename Metabolite";
	public static final String RENAME_METABOLITE_LABEL = "Enter New Metabolite Name";

	public static final String CSV_FILE_LOAD_INTERFACE_TITLE = "CSV File Load";
	public static final String CSV_FILE_LOAD_METAB_BUTTON = "Load Metabolites File";
	public static final String CSV_FILE_LOAD_REAC_BUTTON = " Load Reactions File  ";

	public static final String CSV_FILE_SAVE_INTERFACE_TITLE = "Save As CSV";
	public static final String CSV_FILE_SAVE_METAB_BUTTON = "Save Metabolites File";
	public static final String CSV_FILE_SAVE_REAC_BUTTON = " Save Reactions File  ";

	// delete optimize database and log files
	public static final String DELETE_ASSOCIATED_FILES_TITLE = "Delete Associated Files?";
	public static final String DELETE_ASSOCIATED_FILES = "Delete Associated log Files?";

	// tab names
	public static final String DEFAULT_METABOLITE_TABLE_TAB_NAME = "Metabolites";

	public static final String DEFAULT_REACTION_TABLE_TAB_NAME = " Reactions ";

	// help
	public static final String HELP_TOPICS_URL = "http://most.ccib.rutgers.edu/help.html";

	// about
	public static final String ABOUT_BOX_TITLE = "About MOST";
	public static final String ABOUT_BOX_TEXT = "MOST - Metabolic Optimization and Simulation Tool.";
	public static final String ABOUT_BOX_VERSION_TEXT = "Version: alpha-14";

	public static final String ABOUT_LICENSE_URL = "http://most.ccib.rutgers.edu/help.html#about";

	public static final String SUSPICIOUS_METABOLITES_TITLE = "Suspicious Metabolites Warning";
	public static final String SUSPICIOUS_METABOLITES_MESSAGE = "Model contains suspicious metabolites. "
		+ "This is usually due to invalid spacing in reaction equations. Click URL below for more information.";
	public static final String SUSPICIOUS_METABOLITES_LINK = "<HTML><FONT color=\"#000099\"><U>More Information</U></FONT></HTML>";
	public static final String SUSPICIOUS_METABOLITES_URL = "http://most.ccib.rutgers.edu/Edit_Menu.html#suspicious";
	public static final int SUSPICIOUS_METABOLITES_DIALOG_WIDTH = 360;
	public static final int SUSPICIOUS_METABOLITES_DIALOG_HEIGHT = 180;

	public static final String URL_NOT_FOUND_TITLE = "URL Not Found";
	public static final String URL_NOT_FOUND_MESSAGE = "URL may not exist. Check internet connection.";

	// other
	public static final String[] REVERSIBLE_ARROWS = { "<==> ", "<=> ", "= " };

	public static final String[] NOT_REVERSIBLE_ARROWS = { "=> ", "--> ", "-> " };

	public static final int PROGRESS_BAR_WIDTH = 175;
	public static final int PROGRESS_BAR_HEIGHT = 38;

	public static final int UNDO_MAX_VISIBLE_ROWS = 10;
	public static final double UNDO_VISIBILITY_FRACTION = 0.1;
	public static final int UNDO_BORDER_HEIGHT = 30;

	// public static final String UNDO_ICON_IMAGE_PATH =
	// "etc/toolbarIcons/Sideways_Arrow_Icon16b.png";
	public static final String UNDO_ICON_IMAGE_PATH = "etc/toolbarIcons/1374634434_back_undo.png";
	public static final String REDO_ICON_IMAGE_PATH = "etc/toolbarIcons/1374634411_redo_forward.png";
	// public static final String REDO_ICON_IMAGE_PATH =
	// "etc/toolbarIcons/Sideways_Arrow_Icon16a.png";
	// public static final String UNDO_GRAYED_ICON_IMAGE_PATH =
	// "etc/toolbarIcons/Sideways_Arrow_Icon16d.png";
	public static final String UNDO_GRAYED_ICON_IMAGE_PATH = "etc/toolbarIcons/1374634434_back_undo_grey.png";
	// public static final String REDO_GRAYED_ICON_IMAGE_PATH =
	// "etc/toolbarIcons/Sideways_Arrow_Icon16c.png";
	public static final String REDO_GRAYED_ICON_IMAGE_PATH = "etc/toolbarIcons/1374634411_redo_forward_grey.png";

	// icons from http://www.veryicon.com/icons/application/toolbar-icons/
	/*
	 * Icon Author: Ruby Software
	 * 
	 * HomePage:
	 * http://www.rubysoftware.nl/free-toolbar-icon-collection/old.php?lang=en
	 * License: Commercial usage: Allowed
	 */
	public static final String OPEN_ICON_IMAGE_PATH = "etc/toolbarIcons/Open.png";
	public static final String SAVE_ICON_IMAGE_PATH = "etc/toolbarIcons/Save.png";
	// From a 48x48 image, resized in Photoshop and color and contrast changed
	public static final String COPY_ICON_IMAGE_PATH = "etc/toolbarIcons/copy16.png";
	public static final String PASTE_ICON_IMAGE_PATH = "etc/toolbarIcons/Paste16.png";

	// icons from
	// http://www.softpedia.com/get/Desktop-Enhancements/Icons-Related/24x24-Free-Application-Icons.shtml
	// also free license from Aha-soft
	// resized to 16x16 in Photoshop
	public static final String FIND_ICON_IMAGE_PATH = "etc/toolbarIcons/Search1.png";

	// this shade of gray matches color when labeled components are grayed out
	public static final Color GRAYED_LABEL_COLOR = new Color( 150, 150, 150 );
	
	// colors used for both tables
	public static final Color FIND_ALL_COLOR = new Color( 140, 160, 200 ); // bluish
	// public static final Color FIND_ALL_COLOR = new Color(190,205,225);
	public static final Color SELECTED_AREA_COLOR = new Color( 190, 205, 225 ); // bluish
	// Visual clue to indicate table cell is not editable
	public static final Color NONEDITABLE_COLOR = Color.GRAY;
	// Gray was not obvious enough visual clue to indicate formula bar is not
	// editable
	public static final Color FORMULA_BAR_NONEDITABLE_COLOR = Color.LIGHT_GRAY;
	
	// reaction table highlight colors
	// lower bound < 0 reversible = false
	public static final Color LOWER_BOUND_REVERSIBLE_WARNING_COLOR = Color.ORANGE;
	// reversible column value does not match equation arrow
	public static final Color REVERSIBLE_EQUATION_WARNING_COLOR = Color.YELLOW;
	public static final Color PARTICIPATING_REACTION_COLOR = Color.GREEN;
	// reaction not valid for ReactionParser - example - type "a" only
	public static final Color INVALID_REACTION_COLOR = Color.RED;
	
	// metabolites table highlight colors
	public static final Color SUSPICIOUS_METABOLITE_WARNING_COLOR = Color.ORANGE;
	public static final Color UNUSED_METABOLITE_WARNING_COLOR = Color.YELLOW;

	// directories used for writing log files
	// The suffix must be used as there are bugs in getting the application data
	// folder
	// in Windows XP -
	// http://stackoverflow.com/questions/1198911/how-to-get-local-application-data-folder-in-java
	public static final String SETTINGS_PATH_SUFFIX_WINDOWS_7 = "\\AppData\\Local";

	public static final String SETTINGS_PATH_SUFFIX_WINDOWS_XP = "\\Local Settings\\Application Data";

	public static final String FOLDER_NAME = "\\MOST\\";

	public static final String EDIT_OPT_TABLE_ERROR = "Results Tables Are Not Editable";
	public static final String EDIT_OPT_TABLE_ERROR_TITLE = "Read-Only Table";

	// Solver and Gurobi set up
	public static final int SOLVER_DIALOG_WIDTH = 360;
	public static final int SOLVER_DIALOG_HEIGHT = 240;
	public static final String SOLVER_DIALOG_TITLE = "Select Solvers";
	public static final String SOLVER_SELECTION_LABEL = "";
	public static final String GLPK_SOLVER_BUTTON_LABEL = "GLPK";
	public static final String GUROBI_SOLVER_BUTTON_LABEL = "Gurobi";
	public static final String JIPOPT_SOLVER_BUTTON_LABEL = "Ipopt";
	public static final String GUROBI_NOT_INSTALLED_PREFIX = "No Gurobi installation detected. Please install Gurobi ";
	public static final String GUROBI_MINIMUM_VERSION = "5.6.2 ";
	public static final String GUROBI_NOT_INSTALLED_SUFFIX = " bit version if you would like to use Gurobi.";
	public static final String GUROBI_INSTALLED_MESSAGE = "Gurobi has been installed on this computer and is available for selection as a solver.";

	public static final String GLPK_SOLVER_NAME = "GLPK";
	public static final String GUROBI_SOLVER_NAME = "Gurobi";
	public static final String IPOPT_SOLVER_NAME = "Ipopt";
	// use GLPK or Ipopt as default solvers since they are free and always
	// available
	public static final String DEFAULT_MIXED_INTEGER_SOLVER_NAME = GLPK_SOLVER_NAME;
	public static final String DEFAULT_QUADRATIC_SOLVER_NAME = IPOPT_SOLVER_NAME;
	public static final String DEFAULT_NONLINEAR_SOLVER_NAME = IPOPT_SOLVER_NAME;

	public static final String MIXED_INTEGER_LINEAR_LABEL = "Mixed Integer Linear";
	public static final String QUADRATIC_LABEL = "Quadratic";
	public static final String NONLINEAR_LABEL = "Nonlinear";

	public static final String[] MIXED_INTEGER_LINEAR_OPTIONS = { GLPK_SOLVER_NAME };

	public static final String[] QUADRATIC_OPTIONS = { IPOPT_SOLVER_NAME };

	public static final String[] NONLINEAR_OPTIONS = { IPOPT_SOLVER_NAME };

	public static final String GLPK_NO_MULTIPLE_THREADS_TOOLTIP = "GLPK does not support multiple threads.";

	// Gurobi
	public static final String GUROBI_KEY_ERROR_TITLE = "Gurobi Key Error";
	public static final String GUROBI_KEY_ERROR = "Gurobi Key Error";

	public static final String GUROBI_ERROR_CODE_URL = "http://www.gurobi.com/documentation/5.6/refman/error_codes.html";

	public static final String LOAD_FROM_MODEL_COLLECTION_TABLE_TITLE = "Load from Model Collection Database";

	// spaces for alignment
	public static final String ROW_HEADER_TITLE = "   Row";

	// since csv accepts anything, and sbml is much more stringent,
	// the default if the save button is used when editing a blank
	// or "Untitled" model is csv
	public static final String SBML_FILE_TYPE = "sbml";
	public static final String CSV_FILE_TYPE = "csv";
	public static final String DEFAULT_FILE_TYPE = "csv";

	public static final String GDBB_PREFIX = "GDBB_";
	
	// output solver text
	public static final boolean DEBUG_MODE = true;
	public static final boolean SOLVER_DEBUG_OUTPUT = DEBUG_MODE;

}
