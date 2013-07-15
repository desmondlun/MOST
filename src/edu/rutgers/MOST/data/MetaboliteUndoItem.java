package edu.rutgers.MOST.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.SortOrder;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.logic.ReactionParser;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;

public class MetaboliteUndoItem implements UndoItem {

	private String databaseName;	
	private Integer id;
	private Integer row;
	private Integer column;
	private String oldValue;
	private String newValue;
	private String undoType;
	private String undoItemType;
	public int oldSortColumnIndex;
	public int newSortColumnIndex;
	public SortOrder oldSortOrder;
	public SortOrder newSortOrder;
	public int addedColumnIndex;
	public int deletedColumnIndex;
	public ArrayList<Integer> oldBlankMetabIds;
	public ArrayList<Integer> newBlankMetabIds;
	public ArrayList<Integer> oldDuplicateIds;
	public ArrayList<Integer> newDuplicateIds;
	public Map<String, Object> oldMetaboliteIdNameMap;
	public Map<String, Object> newMetaboliteIdNameMap;
	public Map<String, Object> oldMetaboliteUsedMap;
	public Map<String, Object> newMetaboliteUsedMap;
	public ArrayList<Integer> oldSuspiciousMetabolites;		
	public ArrayList<Integer> newSuspiciousMetabolites;
	public ArrayList<Integer> oldUnusedList;
	public ArrayList<Integer> newUnusedList;
	public ArrayList<Integer> reactionIdList;
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
	
	public ArrayList<Integer> getOldBlankMetabIds() {
		return oldBlankMetabIds;
	}
	public void setOldBlankMetabIds(ArrayList<Integer> oldBlankMetabIds) {
		this.oldBlankMetabIds = oldBlankMetabIds;
	}
	public ArrayList<Integer> getNewBlankMetabIds() {
		return newBlankMetabIds;
	}
	public void setNewBlankMetabIds(ArrayList<Integer> newBlankMetabIds) {
		this.newBlankMetabIds = newBlankMetabIds;
	}
	public ArrayList<Integer> getOldDuplicateIds() {
		return oldDuplicateIds;
	}
	public void setOldDuplicateIds(ArrayList<Integer> oldDuplicateIds) {
		this.oldDuplicateIds = oldDuplicateIds;
	}
	public ArrayList<Integer> getNewDuplicateIds() {
		return newDuplicateIds;
	}
	public void setNewDuplicateIds(ArrayList<Integer> newDuplicateIds) {
		this.newDuplicateIds = newDuplicateIds;
	}
	public Map<String, Object> getOldMetaboliteIdNameMap() {
		return oldMetaboliteIdNameMap;
	}
	public void setOldMetaboliteIdNameMap(Map<String, Object> oldMetaboliteIdNameMap) {
		this.oldMetaboliteIdNameMap = oldMetaboliteIdNameMap;
	}
	public Map<String, Object> getNewMetaboliteIdNameMap() {
		return newMetaboliteIdNameMap;
	}
	public void setNewMetaboliteIdNameMap(Map<String, Object> newMetaboliteIdNameMap) {
		this.newMetaboliteIdNameMap = newMetaboliteIdNameMap;
	}
	public Map<String, Object> getOldMetaboliteUsedMap() {
		return oldMetaboliteUsedMap;
	}
	public void setOldMetaboliteUsedMap(Map<String, Object> oldMetaboliteUsedMap) {
		this.oldMetaboliteUsedMap = oldMetaboliteUsedMap;
	}
	public Map<String, Object> getNewMetaboliteUsedMap() {
		return newMetaboliteUsedMap;
	}
	public void setNewMetaboliteUsedMap(Map<String, Object> newMetaboliteUsedMap) {
		this.newMetaboliteUsedMap = newMetaboliteUsedMap;
	}
	public ArrayList<Integer> getOldSuspiciousMetabolites() {
		return oldSuspiciousMetabolites;
	}
	public void setOldSuspiciousMetabolites(
			ArrayList<Integer> oldSuspiciousMetabolites) {
		this.oldSuspiciousMetabolites = oldSuspiciousMetabolites;
	}
	public ArrayList<Integer> getNewSuspiciousMetabolites() {
		return newSuspiciousMetabolites;
	}
	public void setNewSuspiciousMetabolites(
			ArrayList<Integer> newSuspiciousMetabolites) {
		this.newSuspiciousMetabolites = newSuspiciousMetabolites;
	}
	public ArrayList<Integer> getOldUnusedList() {
		return oldUnusedList;
	}
	public void setOldUnusedList(ArrayList<Integer> oldUnusedList) {
		this.oldUnusedList = oldUnusedList;
	}
	public ArrayList<Integer> getNewUnusedList() {
		return newUnusedList;
	}
	public void setNewUnusedList(ArrayList<Integer> newUnusedList) {
		this.newUnusedList = newUnusedList;
	}	
	public ArrayList<Integer> getReactionIdList() {
		return reactionIdList;
	}
	public void setReactionIdList(ArrayList<Integer> reactionIdList) {
		this.reactionIdList = reactionIdList;
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
			+ this.newValue + "' in '" + displayMetabolitesColumnNameFromIndex(this.column) + "' row " + (this.row + 1);
		} else if (this.undoType.equals(UndoConstants.REPLACE)) {
			undoDescription = UndoConstants.REPLACE;
		} else if (this.undoType.equals(UndoConstants.REPLACE_ALL)) {
			undoDescription = UndoConstants.REPLACE_ALL;	
		} else if (this.undoType.equals(UndoConstants.ADD_ROW)) {
			undoDescription = UndoConstants.ADD_ROW;
		} else if (this.undoType.equals(UndoConstants.ADD_COLUMN)) {
			undoDescription = UndoConstants.ADD_COLUMN_PREFIX + displayMetabolitesColumnNameFromIndex(this.addedColumnIndex) + UndoConstants.ADD_COLUMN_SUFFIX;	
		} else if (this.undoType.equals(UndoConstants.DELETE_ROW)) {
			undoDescription = UndoConstants.DELETE_ROW;	
		} else if (this.undoType.equals(UndoConstants.DELETE_COLUMN)) {
			undoDescription = UndoConstants.DELETE_COLUMN_PREFIX + displayMetabolitesColumnNameFromIndex(this.deletedColumnIndex) + UndoConstants.DELETE_COLUMN_SUFFIX;				
		} else if (this.undoType.equals(UndoConstants.PASTE)) {
			undoDescription = UndoConstants.PASTE;	
		} else if (this.undoType.equals(UndoConstants.CLEAR_CONTENTS)) {
			undoDescription = UndoConstants.CLEAR_CONTENTS;	
		} else if (this.undoType.equals(UndoConstants.SORT)) {
			undoDescription = UndoConstants.SORT;
		} else if (this.undoType.equals(UndoConstants.DELETE_UNUSED)) {
			undoDescription = UndoConstants.DELETE_UNUSED;	
		} else if (this.undoType.equals(UndoConstants.RENAME_METABOLITE)) {
			undoDescription = UndoConstants.RENAME_METABOLITE;
		}
		
		return undoDescription + UndoConstants.METABOLITE_UNDO_SUFFIX;
	}
	
	public void undo() {
		if (this.undoType.equals(UndoConstants.TYPING) || this.undoType.equals(UndoConstants.REPLACE)) {
			undoEntry();
		} else if (this.undoType.equals(UndoConstants.ADD_ROW)) {
			undoAddRow();
		} else if (this.undoType.equals(UndoConstants.RENAME_METABOLITE)) {
			undoRename();
		} else if (this.undoType.equals(UndoConstants.ADD_COLUMN)) {
			undoAddColumn();
		} else if (this.undoType.equals(UndoConstants.DELETE_COLUMN)) {
			undoDeleteColumn();
		} else if (this.undoType.equals(UndoConstants.PASTE) || this.undoType.equals(UndoConstants.CLEAR_CONTENTS) ||
				this.undoType.equals(UndoConstants.DELETE_ROW) || this.undoType.equals(UndoConstants.REPLACE_ALL)) {	
			loadCopiedTable(this.tableCopyIndex);
			int numCopied = LocalConfig.getInstance().getNumMetabolitesTableCopied();
			numCopied -= 2;
			LocalConfig.getInstance().setNumMetabolitesTableCopied(numCopied);
		} else if (this.undoType.equals(UndoConstants.DELETE_UNUSED)) {			
			loadCopiedTable(this.tableCopyIndex);
			int numCopied = LocalConfig.getInstance().getNumMetabolitesTableCopied();
			numCopied -= 2;
			LocalConfig.getInstance().setNumMetabolitesTableCopied(numCopied);
		}
		restoreOldCollections();
	}
	
	public void redo() {
		if (this.undoType.equals(UndoConstants.TYPING) || this.undoType.equals(UndoConstants.REPLACE)) {
			redoEntry();
		} else if (this.undoType.equals(UndoConstants.ADD_ROW)) {
			redoAddRow();
		} else if (this.undoType.equals(UndoConstants.RENAME_METABOLITE)) {
			redoRename();
		} else if (this.undoType.equals(UndoConstants.ADD_COLUMN)) {
			redoAddColumn();
		} else if (this.undoType.equals(UndoConstants.DELETE_COLUMN)) {
			redoDeleteColumn();
		} else if (this.undoType.equals(UndoConstants.PASTE) || this.undoType.equals(UndoConstants.CLEAR_CONTENTS) ||
				this.undoType.equals(UndoConstants.DELETE_ROW) || this.undoType.equals(UndoConstants.REPLACE_ALL)) {	
			loadCopiedTable(this.tableCopyIndex + 1);
			int numCopied = LocalConfig.getInstance().getNumMetabolitesTableCopied();
			numCopied += 2;
			LocalConfig.getInstance().setNumMetabolitesTableCopied(numCopied);
		} else if (this.undoType.equals(UndoConstants.DELETE_UNUSED)) {
			loadCopiedTable(this.tableCopyIndex + 1);
			int numCopied = LocalConfig.getInstance().getNumMetabolitesTableCopied();
			numCopied += 2;
			LocalConfig.getInstance().setNumMetabolitesTableCopied(numCopied);
		}
		restoreNewCollections();
	}
	
	
	public boolean undoEntry() {

		if (this.column == GraphicalInterfaceConstants.BOUNDARY_COLUMN) {
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
			conn = DriverManager.getConnection(createConnectionStatement(getDatabaseName())); // TODO:

			PreparedStatement prep = conn
			.prepareStatement("update metabolites set " + dbMetabolitesColumnNameFromIndex(this.column) + "=? where id=?;");
			prep.setString(1, this.oldValue);
			prep.setInt(2, this.id);
			conn.setAutoCommit(true);
			prep.executeUpdate();

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
			"delete from metabolites where id = ?;");

			prep.setInt(1, this.id);

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
	
	public void undoRename() {
		undoEntry();
		if (this.reactionIdList != null) {
			rewriteReactions("undo");
		}
	}
	
	public void undoAddColumn() {
		LocalConfig.getInstance().getHiddenMetabolitesColumns().add(this.addedColumnIndex);
	}
	
	public void undoDeleteColumn() {
		LocalConfig.getInstance().getHiddenMetabolitesColumns().remove(LocalConfig.getInstance().getHiddenMetabolitesColumns().indexOf(this.deletedColumnIndex));
	}
	
	public boolean redoEntry() {

		if (this.column == GraphicalInterfaceConstants.BOUNDARY_COLUMN) {
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
			conn = DriverManager.getConnection(createConnectionStatement(getDatabaseName())); // TODO:

			PreparedStatement prep = conn
			.prepareStatement("update metabolites set " + dbMetabolitesColumnNameFromIndex(this.column) + "=? where id=?;");
			prep.setString(1, this.newValue);
			prep.setInt(2, this.id);
			conn.setAutoCommit(true);
			prep.executeUpdate();

			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	} 
	
	public void redoRename() {
		redoEntry();
		if (this.reactionIdList != null) {
			rewriteReactions("redo");
		}		
	}
	
	public void redoAddRow() {
		DatabaseCreator creator = new DatabaseCreator();
		creator.addMetaboliteRow(this.databaseName);
	}
	
	public void redoAddColumn() {
		LocalConfig.getInstance().getHiddenMetabolitesColumns().remove(LocalConfig.getInstance().getHiddenMetabolitesColumns().indexOf(this.addedColumnIndex));
	}
	
	public void redoDeleteColumn() {
		LocalConfig.getInstance().getHiddenMetabolitesColumns().add(this.deletedColumnIndex);
	}
	
	public void loadCopiedTable(int index) {	
		DatabaseCreator creator = new DatabaseCreator();
		creator.createMetabolitesTable(databaseName, "metabolites");		
		creator.copyTable(databaseName, "metabolites" + tableCopySuffix(index), "metabolites");	
	}
	
	public static String tableCopySuffix(int count) {
    	return new DecimalFormat("000").format(count);
    }
	
	public String createConnectionStatement(String databaseName) {
		return "jdbc:sqlite:" + getDatabaseName() + ".db";
	}
	
	public String displayMetabolitesColumnNameFromIndex(int columnIndex) {
		String columnName = "";
		if (columnIndex > GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length - 1) {
			MetabolitesMetaColumnManager metabolitesMetaColumnManager = new MetabolitesMetaColumnManager();
			columnName = metabolitesMetaColumnManager.getColumnName(this.databaseName, columnIndex - GraphicalInterfaceConstants.METABOLITES_DB_COLUMN_NAMES.length + 1);
		} else {
			columnName = GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES[columnIndex];
		}
		return columnName;
	}
	
	public String dbMetabolitesColumnNameFromIndex(int columnIndex) {
		String dbColumnName = "";
		if (columnIndex > GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length - 1) {
			dbColumnName = "meta_" + (columnIndex - GraphicalInterfaceConstants.METABOLITES_DB_COLUMN_NAMES.length + 1);
		} else {
			dbColumnName = GraphicalInterfaceConstants.METABOLITES_DB_COLUMN_NAMES[columnIndex];
		}
		return dbColumnName;
	}
	
	public void restoreOldCollections() {
		LocalConfig.getInstance().setBlankMetabIds(this.oldBlankMetabIds);
		LocalConfig.getInstance().setDuplicateIds(this.oldDuplicateIds);
		LocalConfig.getInstance().setMetaboliteIdNameMap(this.oldMetaboliteIdNameMap);
		LocalConfig.getInstance().setMetaboliteUsedMap(this.oldMetaboliteUsedMap);
		LocalConfig.getInstance().setSuspiciousMetabolites(this.oldSuspiciousMetabolites);
		LocalConfig.getInstance().setUnusedList(this.oldUnusedList);
		/*
		System.out.println("old" + LocalConfig.getInstance().getMetaboliteIdNameMap());
		System.out.println("old" + LocalConfig.getInstance().getUnusedList());
		System.out.println("old" + LocalConfig.getInstance().getDuplicateIds());
		System.out.println("new" + this.newMetaboliteIdNameMap);
		System.out.println("new" + this.newUnusedList);
		System.out.println("new" + this.newDuplicateIds);
		*/
	}
	
	public void restoreNewCollections() {
		LocalConfig.getInstance().setBlankMetabIds(this.newBlankMetabIds);
		LocalConfig.getInstance().setDuplicateIds(this.newDuplicateIds);
		LocalConfig.getInstance().setMetaboliteIdNameMap(this.newMetaboliteIdNameMap);
		LocalConfig.getInstance().setMetaboliteUsedMap(this.newMetaboliteUsedMap);
		LocalConfig.getInstance().setSuspiciousMetabolites(this.newSuspiciousMetabolites);
		LocalConfig.getInstance().setUnusedList(this.newUnusedList);
		/*
		System.out.println("new" + LocalConfig.getInstance().getMetaboliteIdNameMap());
		System.out.println("new" + LocalConfig.getInstance().getUnusedList());
		System.out.println("new" + LocalConfig.getInstance().getDuplicateIds());
		System.out.println("old" + this.oldMetaboliteIdNameMap);
		System.out.println("old" + this.oldUnusedList);
		System.out.println("old" + this.oldDuplicateIds);
		*/
	}
	
	public String toString() {
		String undoString = "";
		if (this.undoType.startsWith(UndoConstants.TYPING) || this.undoType.startsWith(UndoConstants.REPLACE)) {
			undoString = "update metabolites set " + dbMetabolitesColumnNameFromIndex(this.column) + "='" + this.oldValue + "' where id=" + this.id + ";";		
		} else if (this.undoType.equals(UndoConstants.RENAME_METABOLITE)) {
			undoString = "update metabolites set " + dbMetabolitesColumnNameFromIndex(this.column) + "='" + this.oldValue + "' where id=" + this.id + ";" +
			"updated reactions list:";			
		} else if (this.undoType.equals(UndoConstants.ADD_ROW)) {
			undoString = "delete from metabolites where id = " + this.id + ";";	
		} else if (this.undoType.equals(UndoConstants.ADD_COLUMN)) {
			undoString = "add " + this.addedColumnIndex + " to hiddenMetabolitesColumns";	
		} else if (this.undoType.equals(UndoConstants.DELETE_COLUMN)) {
			undoString = "delete " + this.deletedColumnIndex + " from hiddenMetabolitesColumns";			
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
			redoString = "redo: update metabolites set " + dbMetabolitesColumnNameFromIndex(this.column) + "='" + this.newValue + "' where id=" + this.id + ";";		
		} else if (this.undoType.equals(UndoConstants.RENAME_METABOLITE)) {
			redoString = "redo: update metabolites set " + dbMetabolitesColumnNameFromIndex(this.column) + "='" + this.newValue + "' where id=" + this.id + ";" +
			"updated metabolites list:";			
		} else if (this.undoType.equals(UndoConstants.ADD_ROW)) {
			redoString = "id = " + (creator.maxReactionId(this.databaseName) + 1);	
		} else if (this.undoType.equals(UndoConstants.ADD_COLUMN)) {
			redoString = "redo: remove " + this.addedColumnIndex + " from hiddenMetabolitesColumns";	
		} else if (this.undoType.equals(UndoConstants.DELETE_COLUMN)) {
			redoString = "redo: add " + this.deletedColumnIndex + " to hiddenMetabolitesColumns";			
		} else if (this.undoType.equals(UndoConstants.SORT)) {
			redoString = "old sort index = " + this.oldSortColumnIndex + ", old sort order = " + this.oldSortOrder +
			", new sort index = " + this.newSortColumnIndex + ", new sort order = " + this.newSortOrder;
		}
		return redoString;
		
	}
	
	// method used when renaming metabolites
	public void rewriteReactions(String type) {		
		String queryString = "jdbc:sqlite:" + this.databaseName + ".db";
		
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			Connection conn =
				DriverManager.getConnection(queryString);
			Statement stat = conn.createStatement();
			ResultSet rsReac = null;
			ResultSet rsEqun = null;
			ResultSet rsProd = null;
			
			ReactionParser parser = new ReactionParser();
			
			try {
				stat.executeUpdate("BEGIN TRANSACTION");

				for (int i = 0; i < this.reactionIdList.size(); i++) {
					StringBuffer reacBfr = new StringBuffer();
					StringBuffer prodBfr = new StringBuffer();
					StringBuffer rxnBfr = new StringBuffer();
					PreparedStatement rPrep = conn.prepareStatement("select metabolite_id, stoic from reaction_reactants where reaction_id=?");
					rPrep.setInt(1, reactionIdList.get(i));
					conn.setAutoCommit(true);
					rsReac = rPrep.executeQuery();
					int r = 0;
					while(rsReac.next()) {						
						int stoicStr = rsReac.getInt("stoic");
						int reactantId = rsReac.getInt("metabolite_id");
						String reactant = "";
						if (reactantId == this.id) {
							if (type.equals("undo")) {
								reactant = this.oldValue;
							} else if (type.equals("redo")) {
								reactant = this.newValue;
							}
							LocalConfig.getInstance().getMetaboliteUsedMap().put(reactant, this.id);
							LocalConfig.getInstance().getMetaboliteIdNameMap().put(reactant, this.id);
						} else {
							Object key = getKeyFromValue(LocalConfig.getInstance().getMetaboliteIdNameMap(), reactantId);
							reactant = key.toString();
						}						
						if (r == 0) {
							if (Double.valueOf(stoicStr) == 1) {
								reacBfr.append(reactant);
							} else {
								reacBfr.append(stoicStr + " " + reactant);
							}		
						} else {
							if (Double.valueOf(stoicStr) == 1) {
								reacBfr.append(" + " + reactant);
							} else {
								reacBfr.append(" + " + stoicStr + " " + reactant);
							}				
						}
						r += 1;
					}
					
					PreparedStatement eqPrep = conn.prepareStatement("select reaction_equn_abbr from reactions where id=?");
					eqPrep.setInt(1, reactionIdList.get(i));
					conn.setAutoCommit(true);
					rsEqun = eqPrep.executeQuery();
					String equation = rsEqun.getString("reaction_equn_abbr");
					String splitString = parser.splitString(equation);
					
					PreparedStatement pPrep = conn.prepareStatement("select metabolite_id, stoic from reaction_products where reaction_id=?");
					pPrep.setInt(1, reactionIdList.get(i));
					conn.setAutoCommit(true);
					rsProd = pPrep.executeQuery();
					r = 0;
					while(rsProd.next()) {
						int stoicStr = rsProd.getInt("stoic");
						int productId = rsProd.getInt("metabolite_id");
						String product = "";
						if (productId == this.id) {
							if (type.equals("undo")) {
								product = this.oldValue;
							} else if (type.equals("redo")) {
								product = this.newValue;
							}
							LocalConfig.getInstance().getMetaboliteUsedMap().put(product, this.id);
							LocalConfig.getInstance().getMetaboliteIdNameMap().put(product, this.id);
						} else {
							Object key = getKeyFromValue(LocalConfig.getInstance().getMetaboliteIdNameMap(), productId);
							product = key.toString();
						}
						if (r == 0) {
							if (Double.valueOf(stoicStr) == 1) {
								prodBfr.append(product);
							} else {
								prodBfr.append(stoicStr + " " + product);
							}		
						} else {
							if (Double.valueOf(stoicStr) == 1) {
								prodBfr.append(" + " + product);
							} else {
								prodBfr.append(" + " + stoicStr + " " + product);
							}				
						}
						r += 1;
					}
					rxnBfr.append(reacBfr).append(" " + splitString).append(prodBfr);
					PreparedStatement eqUpdatePrep = conn.prepareStatement("update reactions set reaction_equn_abbr=? where id=?");
					eqUpdatePrep.setString(1, rxnBfr.toString());
					eqUpdatePrep.setInt(2, reactionIdList.get(i));
					conn.setAutoCommit(true);
					eqUpdatePrep.executeUpdate();
				}
				
				stat.executeUpdate("COMMIT");
			} catch (Exception e) {
				e.printStackTrace();
				stat.executeUpdate("ROLLBACK"); // throw away all updates since BEGIN TRANSACTION
			}
			
			conn.close();	

		}catch(SQLException e){

			e.printStackTrace();

		}
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
		MetaboliteUndoItem m = new MetaboliteUndoItem();
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
	}
	
}
