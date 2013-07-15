package edu.rutgers.MOST.data;

public class UndoConstants {

	// Descriptions of undo actions
	// undo uses sql statement
	public static final String TYPING = "Typing ";
	public static final String REPLACE = "Replace";
	public static final String RENAME_METABOLITE = "Rename";
	public static final String ADD_ROW = "Add Row";
	public static final String EDIT_REACTION = "Edit Reaction";
	
	// undo modifies hidden column list
	public static final String ADD_COLUMN = "Add Column";
	public static final String ADD_COLUMN_PREFIX = "Add '";
	public static final String ADD_COLUMN_SUFFIX = "' Column";
	public static final String DELETE_COLUMN = "Delete Column";
	public static final String DELETE_COLUMN_PREFIX = "Delete '";
	public static final String DELETE_COLUMN_SUFFIX = "' Column";
	
	// undo copies database
	public static final String PASTE = "Paste";
	public static final String CLEAR_CONTENTS = "Clear Contents";
	public static final String DELETE_ROW = "Delete Row(s)";
	public static final String DELETE_UNUSED = "Delete All Unused Metabolites";
	public static final String REPLACE_ALL = "Replace All";
	
	// sort
	public static final String SORT = "Sort";
	
	public static final String REACTION_UNDO_ITEM_TYPE = "reactions";
	public static final String METABOLITE_UNDO_ITEM_TYPE = "metabolites";
		
	public static final String REACTION_UNDO_SUFFIX = " in Reactions Table";
	public static final String METABOLITE_UNDO_SUFFIX = " in Metabolites Table";
	
}
