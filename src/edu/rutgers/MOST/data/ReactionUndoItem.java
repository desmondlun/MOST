package edu.rutgers.MOST.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.SortOrder;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;

public class ReactionUndoItem implements UndoItem {

	private String databaseName;	
	private Integer id;
	private Integer row;
	private Integer column;
	private String oldValue;
	private String newValue;
	private String undoType;
	private String undoItemType;
	private String equationNames;
	public int oldSortColumnIndex;
	public int newSortColumnIndex;
	public SortOrder oldSortOrder;
	public SortOrder newSortOrder;
	public int addedColumnIndex;
	public int deletedColumnIndex;
	public ArrayList<Integer> addedMetabolites;
	public int tableCopyIndex;
	
	public String getDatabaseName() {
		return databaseName;
	}
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getRow() {
		return row;
	}
	public void setRow(Integer row) {
		this.row = row;
	}
	public Integer getColumn() {
		return column;
	}
	public void setColumn(Integer column) {
		this.column = column;
	}
	public String getOldValue() {
		return oldValue;
	}
	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}
	public String getNewValue() {
		return newValue;
	}
	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}	
	public String getUndoType() {
		return undoType;
	}
	public void setUndoType(String undoType) {
		this.undoType = undoType;
	}	
	public String getUndoItemType() {
		return undoItemType;
	}
	public void setUndoItemType(String undoItemType) {
		this.undoItemType = undoItemType;
	}
	public String getEquationNames() {
		return equationNames;
	}
	public void setEquationNames(String equationNames) {
		this.equationNames = equationNames;
	}
	public int getOldSortColumnIndex() {
		return oldSortColumnIndex;
	}
	public void setOldSortColumnIndex(int oldSortColumnIndex) {
		this.oldSortColumnIndex = oldSortColumnIndex;
	}
	public int getNewSortColumnIndex() {
		return newSortColumnIndex;
	}
	public void setNewSortColumnIndex(int newSortColumnIndex) {
		this.newSortColumnIndex = newSortColumnIndex;
	}
	public SortOrder getOldSortOrder() {
		return oldSortOrder;
	}
	public void setOldSortOrder(SortOrder oldSortOrder) {
		this.oldSortOrder = oldSortOrder;
	}
	public SortOrder getNewSortOrder() {
		return newSortOrder;
	}
	public void setNewSortOrder(SortOrder newSortOrder) {
		this.newSortOrder = newSortOrder;
	}	
	public int getAddedColumnIndex() {
		return addedColumnIndex;
	}
	public void setAddedColumnIndex(int addedColumnIndex) {
		this.addedColumnIndex = addedColumnIndex;
	}
	public int getDeletedColumnIndex() {
		return deletedColumnIndex;
	}
	public void setDeletedColumnIndex(int deletedColumnIndex) {
		this.deletedColumnIndex = deletedColumnIndex;
	}	
	public ArrayList<Integer> getAddedMetabolites() {
		return addedMetabolites;
	}
	public void setAddedMetabolites(ArrayList<Integer> addedMetabolites) {
		this.addedMetabolites = addedMetabolites;
	}	
	public int getTableCopyIndex() {
		return tableCopyIndex;
	}
	public void setTableCopyIndex(int tableCopyIndex) {
		this.tableCopyIndex = tableCopyIndex;
	}
	
	public String createUndoDescription() {
		String undoDescription = "";
		if (this.undoType.equals(UndoConstants.TYPING)) {
			undoDescription = UndoConstants.TYPING + "'"
			+ this.newValue + "' in '" + displayReactionsColumnNameFromIndex(this.column) + "' row " + (this.row + 1);
		} else if (this.undoType.equals(UndoConstants.REPLACE)) {
			undoDescription = UndoConstants.REPLACE;
		} else if (this.undoType.equals(UndoConstants.REPLACE_ALL)) {
			undoDescription = UndoConstants.REPLACE_ALL;	
		} else if (this.undoType.equals(UndoConstants.ADD_ROW)) {
			undoDescription = UndoConstants.ADD_ROW;
		} else if (this.undoType.equals(UndoConstants.ADD_COLUMN)) {
			undoDescription = UndoConstants.ADD_COLUMN_PREFIX + displayReactionsColumnNameFromIndex(this.addedColumnIndex) + UndoConstants.ADD_COLUMN_SUFFIX;	
		} else if (this.undoType.equals(UndoConstants.DELETE_ROW)) {
			undoDescription = UndoConstants.DELETE_ROW;	
		} else if (this.undoType.equals(UndoConstants.DELETE_COLUMN)) {
			undoDescription = UndoConstants.DELETE_COLUMN_PREFIX + displayReactionsColumnNameFromIndex(this.deletedColumnIndex) + UndoConstants.DELETE_COLUMN_SUFFIX;								
		} else if (this.undoType.equals(UndoConstants.PASTE)) {
			undoDescription = UndoConstants.PASTE;	
		} else if (this.undoType.equals(UndoConstants.CLEAR_CONTENTS)) {
			undoDescription = UndoConstants.CLEAR_CONTENTS;	
		} else if (this.undoType.equals(UndoConstants.SORT)) {
			undoDescription = UndoConstants.SORT;
		} else if (this.undoType.equals(UndoConstants.EDIT_REACTION)) {
			undoDescription = UndoConstants.EDIT_REACTION;
		}
		return undoDescription + UndoConstants.REACTION_UNDO_SUFFIX;
	}
	
	public void undo() {
		if (this.undoType.equals(UndoConstants.TYPING) || this.undoType.equals(UndoConstants.REPLACE) || this.undoType.equals(UndoConstants.EDIT_REACTION)) {
			undoEntry();
		} else if (this.undoType.equals(UndoConstants.ADD_ROW)) {
			undoAddRow();
		} else if (this.undoType.equals(UndoConstants.ADD_COLUMN)) {
			undoAddColumn();
		} else if (this.undoType.equals(UndoConstants.DELETE_COLUMN)) {
			undoDeleteColumn();
		} else if (this.undoType.equals(UndoConstants.PASTE) || this.undoType.equals(UndoConstants.CLEAR_CONTENTS) ||
				this.undoType.equals(UndoConstants.DELETE_ROW) || this.undoType.equals(UndoConstants.REPLACE_ALL)) {	
			loadCopiedTables(this.tableCopyIndex);
			int numCopied = LocalConfig.getInstance().getNumReactionTablesCopied();
			numCopied -= 2;
			LocalConfig.getInstance().setNumReactionTablesCopied(numCopied);
		}
	}
	
	public void redo() {
		DatabaseCreator creator = new DatabaseCreator();
		if (this.undoType.equals(UndoConstants.TYPING) || this.undoType.equals(UndoConstants.REPLACE)) {
			redoEntry();
		} else if (this.undoType.equals(UndoConstants.ADD_ROW)) {
			redoAddRow();
		} else if (this.undoType.equals(UndoConstants.ADD_COLUMN)) {
			redoAddColumn();
		} else if (this.undoType.equals(UndoConstants.DELETE_COLUMN)) {
			redoDeleteColumn();
		} else if (this.undoType.equals(UndoConstants.PASTE) || this.undoType.equals(UndoConstants.CLEAR_CONTENTS) ||
				this.undoType.equals(UndoConstants.DELETE_ROW) || this.undoType.equals(UndoConstants.REPLACE_ALL)) {	
			loadCopiedTables(this.tableCopyIndex + 1);
			int numCopied = LocalConfig.getInstance().getNumReactionTablesCopied();
			numCopied += 2;
			LocalConfig.getInstance().setNumReactionTablesCopied(numCopied);
		}
	}
	
	public boolean undoEntry() {
		
		if (this.column == GraphicalInterfaceConstants.KO_COLUMN) {
			if (this.oldValue.toLowerCase().startsWith(GraphicalInterfaceConstants.VALID_TRUE_VALUES[0])) {
				this.oldValue = GraphicalInterfaceConstants.BOOLEAN_VALUES[1];
			} else if (this.oldValue.toLowerCase().startsWith(GraphicalInterfaceConstants.VALID_FALSE_VALUES[0])) {
				this.oldValue = GraphicalInterfaceConstants.BOOLEAN_VALUES[0];
			}				
		}
		
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		Connection conn;
		try {
			conn = DriverManager.getConnection(createConnectionStatement(getDatabaseName())); 

			PreparedStatement prep = conn
			.prepareStatement("update reactions set " + dbReactionsColumnNameFromIndex(this.getColumn()) + "=? where id=?;");
			prep.setString(1, this.oldValue);
			prep.setInt(2, this.getId());
			conn.setAutoCommit(true);			
			prep.executeUpdate();
			
			if (this.column.equals(GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN)) {
				//System.out.println("undo added" + this.addedMetabolites);
				for (int i = 0; i < this.addedMetabolites.size(); i++) {
					String abbrev = (String) getKeyFromValue(LocalConfig.getInstance().getMetaboliteIdNameMap(), this.addedMetabolites.get(i)); 
					LocalConfig.getInstance().getMetaboliteIdNameMap().remove(abbrev);
					LocalConfig.getInstance().getMetaboliteUsedMap().remove(abbrev);
					int maxId = LocalConfig.getInstance().getMaxMetaboliteId();
					maxId -= 1;
					LocalConfig.getInstance().setMaxMetaboliteId(maxId);
					PreparedStatement prep2 = conn
					.prepareStatement("update metabolites set metabolite_abbreviation='' where id=?;");
					prep2.setInt(1, this.addedMetabolites.get(i));
					conn.setAutoCommit(true);
					prep2.executeUpdate();
				}
				ReactionsUpdater updater = new ReactionsUpdater();
				updater.updateReactionEquations(this.id, this.newValue, this.oldValue, this.databaseName);				
			}
			
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
	} 
	
	public boolean undoAddRow() {
		
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			Connection conn =
				DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db");

			PreparedStatement prep = conn.prepareStatement(
			"delete from reactions where id = ?;");

			prep.setInt(1, this.getId());

			prep.addBatch();

			conn.setAutoCommit(false);
			prep.executeBatch();
			conn.setAutoCommit(true);

			conn.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
		
	}
		
	public void undoAddColumn() {
		LocalConfig.getInstance().getHiddenReactionsColumns().add(this.addedColumnIndex);
	}
	
	public void undoDeleteColumn() {
		LocalConfig.getInstance().getHiddenReactionsColumns().remove(LocalConfig.getInstance().getHiddenReactionsColumns().indexOf(this.deletedColumnIndex));
	}
	
	public boolean redoEntry() {

		if (this.column == GraphicalInterfaceConstants.KO_COLUMN) {
			if (this.newValue.toLowerCase().startsWith(GraphicalInterfaceConstants.VALID_TRUE_VALUES[0])) {
				this.newValue = GraphicalInterfaceConstants.BOOLEAN_VALUES[1];
			} else if (this.newValue.toLowerCase().startsWith(GraphicalInterfaceConstants.VALID_FALSE_VALUES[0])) {
				this.newValue = GraphicalInterfaceConstants.BOOLEAN_VALUES[0];
			}				
		}
		
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		Connection conn;
		try {
			conn = DriverManager.getConnection(createConnectionStatement(getDatabaseName())); 

			PreparedStatement prep = conn
			.prepareStatement("update reactions set " + dbReactionsColumnNameFromIndex(this.getColumn()) + "=? where id=?;");
			prep.setString(1, this.newValue);
			prep.setInt(2, this.getId());
			conn.setAutoCommit(true);
			prep.executeUpdate();

			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		if (this.column.equals(GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN)) {
			ReactionsUpdater updater = new ReactionsUpdater();
			updater.updateReactionEquations(this.id, this.oldValue, this.newValue, this.databaseName);
		}
		
		return true;
	} 
	
	public void redoAddRow() {
		DatabaseCreator creator = new DatabaseCreator();
		creator.addReactionRow(this.databaseName);
	}
	
	public void redoAddColumn() {
		LocalConfig.getInstance().getHiddenReactionsColumns().remove(LocalConfig.getInstance().getHiddenReactionsColumns().indexOf(this.addedColumnIndex));
	}
	
	public void redoDeleteColumn() {
		LocalConfig.getInstance().getHiddenReactionsColumns().add(this.deletedColumnIndex);
	}
	
	public void loadCopiedTables(int index) {		
		DatabaseCreator creator = new DatabaseCreator();
		creator.createReactionsTable(databaseName, "reactions");		
		creator.createReactionReactantsTable(databaseName, "reaction_reactants");
		creator.createReactionProductsTable(databaseName, "reaction_products");
		creator.copyTable(databaseName, "reactions" + tableCopySuffix(index), "reactions");
		creator.copyTable(databaseName, "reaction_reactants" + tableCopySuffix(index),  "reaction_reactants");
		creator.copyTable(databaseName, "reaction_products" + tableCopySuffix(index),  "reaction_products");	
	}
	
	public static String tableCopySuffix(int count) {
    	return new DecimalFormat("000").format(count);
    }
	
	public String createConnectionStatement(String databaseName) {
		return "jdbc:sqlite:" + getDatabaseName() + ".db";
	}
	
	public String displayReactionsColumnNameFromIndex(int columnIndex) {
		String columnName = "";
		if (columnIndex > GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length - 1) {
			ReactionsMetaColumnManager reactionsMetaColumnManager = new ReactionsMetaColumnManager();
			columnName = reactionsMetaColumnManager.getColumnName(this.databaseName, columnIndex - GraphicalInterfaceConstants.REACTIONS_DB_COLUMN_NAMES.length + 1);
		} else {
			columnName = GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[columnIndex];
		}
		return columnName;
	}
	
	public String dbReactionsColumnNameFromIndex(int columnIndex) {
		String dbColumnName = "";
		if (columnIndex > GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length - 1) {
			dbColumnName = "meta_" + (columnIndex - GraphicalInterfaceConstants.REACTIONS_DB_COLUMN_NAMES.length + 1);
		} else {
			dbColumnName = GraphicalInterfaceConstants.REACTIONS_DB_COLUMN_NAMES[columnIndex];
		}
		return dbColumnName;
	}
	
	public String toString() {
		String undoString = "";
		if (this.undoType.startsWith(UndoConstants.TYPING) || this.undoType.startsWith(UndoConstants.REPLACE)) {
			undoString = "update reactions set " + dbReactionsColumnNameFromIndex(this.column) + "='" + this.oldValue + "' where id=" + this.id + ";";		
		} else if (this.undoType.equals(UndoConstants.ADD_ROW)) {
			undoString = "delete from reactions where id = " + this.id + ";";
		} else if (this.undoType.equals(UndoConstants.ADD_COLUMN)) {
			undoString = "add " + this.addedColumnIndex + " to hiddenReactionsColumns";
		} else if (this.undoType.equals(UndoConstants.DELETE_COLUMN)) {
			undoString = "delete " + this.deletedColumnIndex + " from hiddenReactionsColumns";			
		} else if (this.undoType.equals(UndoConstants.SORT)) {
			undoString = "old sort index = " + this.oldSortColumnIndex + ", old sort order = " + this.oldSortOrder +
			", new sort index = " + this.newSortColumnIndex + ", new sort order = " + this.newSortOrder;
		}
		return undoString;
		
	}
	
	public String toRedoString() {
		DatabaseCreator creator = new DatabaseCreator();
		String redoString = "";
		if (this.undoType.startsWith(UndoConstants.TYPING) || this.undoType.startsWith(UndoConstants.REPLACE)) {
			redoString = "redo: update reactions set " + dbReactionsColumnNameFromIndex(this.column) + "='" + this.newValue + "' where id=" + this.id + ";";		
		} else if (this.undoType.equals(UndoConstants.ADD_ROW)) {
			redoString = "id = " + (creator.maxReactionId(this.databaseName) + 1);
		} else if (this.undoType.equals(UndoConstants.ADD_COLUMN)) {
			redoString = "redo: remove " + this.addedColumnIndex + " from hiddenReactionsColumns";
		} else if (this.undoType.equals(UndoConstants.DELETE_COLUMN)) {
			redoString = "redo: add " + this.deletedColumnIndex + " to hiddenReactionsColumns";			
		} else if (this.undoType.equals(UndoConstants.SORT)) {
			redoString = "old sort index = " + this.oldSortColumnIndex + ", old sort order = " + this.oldSortOrder +
			", new sort index = " + this.newSortColumnIndex + ", new sort order = " + this.newSortOrder;
		}
		return redoString;
		
	}
	
	public static Object getKeyFromValue(Map hm, Object value) {
		for (Object o : hm.keySet()) {
			if (hm.get(o).equals(value)) {
				return o;
			}
		}
		return null;
	}
	
	public static void main(String[] args) {
		/*
		ReactionUndoItem m = new ReactionUndoItem();
		m.setDatabaseName("untitled");
		m.setColumn(1);
		m.setOldValue("test");
		m.setNewValue("1");
		m.setRow(10);
		m.setId(1);
		m.setUndoType(UndoConstants.TYPING);
		System.out.println(m.createUndoDescription());
		System.out.println(m.toString());
		m.undo();
		System.out.println(m.undoType);
		m.setId(9);
		m.setUndoType(UndoConstants.ADD_ROW);
		System.out.println(m.createUndoDescription());
		System.out.println(m.toString());
		m.undo();
		*/
	}
	
}

