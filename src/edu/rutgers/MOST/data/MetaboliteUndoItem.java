package edu.rutgers.MOST.data;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.presentation.GraphicalInterface;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;

public class MetaboliteUndoItem implements UndoItem {

	private Integer id;
	private Integer row;
	private Integer column;
	private String oldValue;
	private String newValue;
	private String undoType;
	private String undoItemType;
	private int oldSortColumnIndex;
	private int newSortColumnIndex;
	private SortOrder oldSortOrder;
	private SortOrder newSortOrder;
	private int addedColumnIndex;
	private int deletedColumnIndex;
	private Map<String, Object> oldMetaboliteAbbreviationIdMap;
	private Map<String, Object> newMetaboliteAbbreviationIdMap;
	private Map<String, Object> oldMetaboliteUsedMap;
	private Map<String, Object> newMetaboliteUsedMap;
	private ArrayList<Integer> oldSuspiciousMetabolites;		
	private ArrayList<Integer> newSuspiciousMetabolites;
	private ArrayList<Integer> oldUnusedList;
	private ArrayList<Integer> newUnusedList;
	private ArrayList<Integer> reactionIdList;
	private int tableCopyIndex;
	private ArrayList<String> oldMetaColumnNames;
	private ArrayList<String> newMetaColumnNames;
	private String columnName;
	
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
	public Map<String, Object> getOldMetaboliteAbbreviationIdMap() {
		return oldMetaboliteAbbreviationIdMap;
	}
	public void setOldMetaboliteAbbreviationIdMap(Map<String, Object> oldMetaboliteAbbreviationIdMap) {
		this.oldMetaboliteAbbreviationIdMap = oldMetaboliteAbbreviationIdMap;
	}
	public Map<String, Object> getNewMetaboliteAbbreviationIdMap() {
		return newMetaboliteAbbreviationIdMap;
	}
	public void setNewMetaboliteAbbreviationIdMap(Map<String, Object> newMetaboliteAbbreviationIdMap) {
		this.newMetaboliteAbbreviationIdMap = newMetaboliteAbbreviationIdMap;
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
	
	public ArrayList<String> getOldMetaColumnNames() {
		return oldMetaColumnNames;
	}
	public void setOldMetaColumnNames(ArrayList<String> oldMetaColumnNames) {
		this.oldMetaColumnNames = oldMetaColumnNames;
	}
	public ArrayList<String> getNewMetaColumnNames() {
		return newMetaColumnNames;
	}
	public void setNewMetaColumnNames(ArrayList<String> newMetaColumnNames) {
		this.newMetaColumnNames = newMetaColumnNames;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	
	public String createUndoDescription() {
		String undoDescription = "";
		if (this.undoType.equals(UndoConstants.TYPING)) {
			undoDescription = UndoConstants.TYPING + "'"
			+ this.newValue + "' in '" + this.columnName + "' row " + (this.row + 1);				
		} else if (this.undoType.equals(UndoConstants.REPLACE)) {
			undoDescription = UndoConstants.REPLACE;
		} else if (this.undoType.equals(UndoConstants.REPLACE_ALL)) {
			undoDescription = UndoConstants.REPLACE_ALL;	
		} else if (this.undoType.equals(UndoConstants.ADD_ROW)) {
			undoDescription = UndoConstants.ADD_ROW;
		} else if (this.undoType.equals(UndoConstants.ADD_ROWS)) {
			undoDescription = UndoConstants.ADD_ROWS;	
		} else if (this.undoType.equals(UndoConstants.ADD_COLUMN)) {
			undoDescription = UndoConstants.ADD_COLUMN_PREFIX + displayMetabolitesColumnNameFromIndex(this.addedColumnIndex, this.newMetaColumnNames) + UndoConstants.ADD_COLUMN_SUFFIX;	
		} else if (this.undoType.equals(UndoConstants.DELETE_ROW)) {
			undoDescription = UndoConstants.DELETE_ROW;	
		} else if (this.undoType.equals(UndoConstants.DELETE_COLUMN)) {
			undoDescription = UndoConstants.DELETE_COLUMN_PREFIX + displayMetabolitesColumnNameFromIndex(this.deletedColumnIndex + 1, this.oldMetaColumnNames) + UndoConstants.DELETE_COLUMN_SUFFIX;	
		} else if (this.undoType.equals(UndoConstants.PASTE)) {
			undoDescription = UndoConstants.PASTE;	
		} else if (this.undoType.equals(UndoConstants.CLEAR_CONTENTS)) {
			undoDescription = UndoConstants.CLEAR_CONTENTS;	
		} else if (this.undoType.equals(UndoConstants.SORT)) {
			undoDescription = UndoConstants.SORT;
		} else if (this.undoType.equals(UndoConstants.UNSORT)) {
			undoDescription = UndoConstants.UNSORT;	
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
		} else if (this.undoType.equals(UndoConstants.ADD_COLUMN)) {
			undoAddColumn();
			copyTableUndoAction();	
		} else if (this.undoType.equals(UndoConstants.RENAME_METABOLITE)) {
			rewriteReactions("undo");
			undoRename();
			//rewriteReactions("undo");
		} else if (this.undoType.equals(UndoConstants.DELETE_COLUMN)) {
			undoDeleteColumn();
			copyTableUndoAction();
		} else if (this.undoType.equals(UndoConstants.PASTE) || this.undoType.equals(UndoConstants.CLEAR_CONTENTS) ||
				this.undoType.equals(UndoConstants.DELETE_ROW) || this.undoType.equals(UndoConstants.REPLACE_ALL) ||
				this.undoType.equals(UndoConstants.ADD_ROWS)) {	
			copyTableUndoAction();
		} else if (this.undoType.equals(UndoConstants.DELETE_UNUSED)) {			
			copyTableUndoAction();
		}
		restoreOldCollections();
	}
	
	public void copyTableUndoAction() {
		int numCopied = LocalConfig.getInstance().getNumMetabolitesTableCopied();
		numCopied -= 2;
		LocalConfig.getInstance().setNumMetabolitesTableCopied(numCopied);
		GraphicalInterface.metabolitesTable.setModel(LocalConfig.getInstance().getMetabolitesUndoTableModelMap().get(Integer.toString(numCopied + 1)));
	}
	
	public void redo() {
		if (this.undoType.equals(UndoConstants.TYPING) || this.undoType.equals(UndoConstants.REPLACE)) {
			redoEntry();
		} else if (this.undoType.equals(UndoConstants.ADD_ROW)) {
			redoAddRow();
		} else if (this.undoType.equals(UndoConstants.ADD_COLUMN)) {
			redoAddColumn();
			copyTableRedoAction();	
		} else if (this.undoType.equals(UndoConstants.RENAME_METABOLITE)) {
			rewriteReactions("redo");
			redoRename();
			//rewriteReactions("redo");
		} else if (this.undoType.equals(UndoConstants.DELETE_COLUMN)) {
			redoDeleteColumn();
			copyTableRedoAction();
		} else if (this.undoType.equals(UndoConstants.PASTE) || this.undoType.equals(UndoConstants.CLEAR_CONTENTS) ||
				this.undoType.equals(UndoConstants.DELETE_ROW) || this.undoType.equals(UndoConstants.REPLACE_ALL) ||
				this.undoType.equals(UndoConstants.ADD_ROWS)) {
			copyTableRedoAction();
		} else if (this.undoType.equals(UndoConstants.DELETE_UNUSED)) {
			copyTableRedoAction();
		}
		restoreNewCollections();
	}
	
	public void copyTableRedoAction() {
		int numCopied = LocalConfig.getInstance().getNumMetabolitesTableCopied();
		numCopied += 2;
		LocalConfig.getInstance().setNumMetabolitesTableCopied(numCopied);
		GraphicalInterface.metabolitesTable.setModel(LocalConfig.getInstance().getMetabolitesUndoTableModelMap().get(Integer.toString(numCopied)));
	}
	
	public boolean undoEntry() {

		if (this.column == GraphicalInterfaceConstants.BOUNDARY_COLUMN) {
			if (this.oldValue.toLowerCase().startsWith(GraphicalInterfaceConstants.VALID_TRUE_VALUES[0])) {
				this.oldValue = GraphicalInterfaceConstants.BOOLEAN_VALUES[1];
			} else if (this.oldValue.toLowerCase().startsWith(GraphicalInterfaceConstants.VALID_FALSE_VALUES[0])) {
				this.oldValue = GraphicalInterfaceConstants.BOOLEAN_VALUES[0];
			}				
		}	
		updateCellById(this.oldValue, this.id, this.column);
		//GraphicalInterface.metabolitesTable.getModel().setValueAt(oldValue, this.row, this.column);
		
		return true;
	} 
	
	public boolean undoAddRow() {
		DefaultTableModel model = (DefaultTableModel) GraphicalInterface.metabolitesTable.getModel();
		int maxId = LocalConfig.getInstance().getMaxMetaboliteId();
		Map<String, Object> reactionsIdRowMap = new HashMap<String, Object>();
		for (int i = 0; i < GraphicalInterface.metabolitesTable.getRowCount(); i++) {
			reactionsIdRowMap.put((String) GraphicalInterface.metabolitesTable.getModel().getValueAt(i, GraphicalInterfaceConstants.METABOLITE_ID_COLUMN), i);
		}
		String row = (reactionsIdRowMap.get(Integer.toString(id))).toString();
		int rowNum = Integer.valueOf(row);
		model.removeRow(rowNum);
		LocalConfig.getInstance().setMaxMetaboliteId(maxId - 1);
		
		return true;
		
	}
	
	public void undoAddColumn() {
		LocalConfig.getInstance().setMetabolitesMetaColumnNames(this.oldMetaColumnNames);
	}
	
	public void undoRename() {
		undoEntry();
	}
	
	public void undoDeleteColumn() {
		
	}
	
	public boolean redoEntry() {
		
		if (this.column == GraphicalInterfaceConstants.BOUNDARY_COLUMN) {
			if (this.newValue.toLowerCase().startsWith(GraphicalInterfaceConstants.VALID_TRUE_VALUES[0])) {
				this.newValue = GraphicalInterfaceConstants.BOOLEAN_VALUES[1];
			} else if (this.newValue.toLowerCase().startsWith(GraphicalInterfaceConstants.VALID_FALSE_VALUES[0])) {
				this.newValue = GraphicalInterfaceConstants.BOOLEAN_VALUES[0];
			}				
		}
		updateCellById(this.newValue, this.id, this.column);
		//GraphicalInterface.metabolitesTable.getModel().setValueAt(newValue, this.row, this.column);
		
		return true;
	} 
	
	public void redoRename() {
		redoEntry();		
	}
	
	public void redoAddRow() {
		
	}
	
	public void redoAddColumn() {
		LocalConfig.getInstance().setMetabolitesMetaColumnNames(this.newMetaColumnNames);
	}
	
	public void redoDeleteColumn() {
		
	}
	
	public static String tableCopySuffix(int count) {
    	return new DecimalFormat("000").format(count);
    }
	
	public String displayMetabolitesColumnNameFromIndex(int columnIndex, ArrayList<String> metaColumnNames) {
		String columnName = "";
		if (columnIndex < GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length) {
			columnName = GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES[columnIndex];
		} else {
			columnName = metaColumnNames.get(columnIndex - GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length);
		}
		return columnName;
	}
	
	public void restoreOldCollections() {
		LocalConfig.getInstance().setMetaboliteAbbreviationIdMap(this.oldMetaboliteAbbreviationIdMap);
		LocalConfig.getInstance().setMetaboliteUsedMap(this.oldMetaboliteUsedMap);
		LocalConfig.getInstance().setSuspiciousMetabolites(this.oldSuspiciousMetabolites);
		LocalConfig.getInstance().setUnusedList(this.oldUnusedList);
	}
	
	public void restoreNewCollections() {
		LocalConfig.getInstance().setMetaboliteAbbreviationIdMap(this.newMetaboliteAbbreviationIdMap);
		LocalConfig.getInstance().setMetaboliteUsedMap(this.newMetaboliteUsedMap);
		LocalConfig.getInstance().setSuspiciousMetabolites(this.newSuspiciousMetabolites);
		LocalConfig.getInstance().setUnusedList(this.newUnusedList);
	}
	
	public String toString() {
		String undoString = "";
		if (this.undoType.startsWith(UndoConstants.TYPING) || this.undoType.startsWith(UndoConstants.REPLACE)) {
			
		} else if (this.undoType.equals(UndoConstants.RENAME_METABOLITE)) {
						
		} else if (this.undoType.equals(UndoConstants.ADD_ROW)) {
			
		} else if (this.undoType.equals(UndoConstants.ADD_COLUMN)) {
			undoString += this.oldMetaColumnNames + " ";
			undoString += this.newMetaColumnNames;	
		} else if (this.undoType.equals(UndoConstants.DELETE_COLUMN)) {
			
		} else if (this.undoType.equals(UndoConstants.SORT)) {
			
		}
		return undoString;
		
	}
	
	public String toRedoString() {
		String redoString = "";
		if (this.undoType.startsWith(UndoConstants.TYPING) || this.undoType.startsWith(UndoConstants.REPLACE)) {
			
		} else if (this.undoType.equals(UndoConstants.RENAME_METABOLITE)) {
						
		} else if (this.undoType.equals(UndoConstants.ADD_ROW)) {
				
		} else if (this.undoType.equals(UndoConstants.ADD_COLUMN)) {
			
		} else if (this.undoType.equals(UndoConstants.DELETE_COLUMN)) {
			
		} else if (this.undoType.equals(UndoConstants.SORT)) {
			
		}
		return redoString;
		
	}
	
	// method used when renaming metabolites
	public void rewriteReactions(String type) {	
		String oldReactant = "";
		String newReactant = "";
		if (type.equals("undo")) {
			newReactant = this.oldValue;
			oldReactant = this.newValue; 
		} else if (type.equals("redo")) {
			newReactant = this.newValue;
			oldReactant = this.oldValue; 
		}
		MetaboliteFactory aFactory = new MetaboliteFactory("SBML");
		ArrayList<Integer> participatingReactions = aFactory.participatingReactions(oldReactant);
		
		for (int i = 0; i < participatingReactions.size(); i++) {
			SBMLReactionEquation equn = (SBMLReactionEquation) LocalConfig.getInstance().getReactionEquationMap().get(participatingReactions.get(i));
			if (column == GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN) {
				for (int j = 0; j < equn.getReactants().size(); j++) {
					if (equn.getReactants().get(j).getMetaboliteAbbreviation().equals(oldReactant)) {
						equn.getReactants().get(j).setMetaboliteAbbreviation(newReactant);
					}
				}
				for (int j = 0; j < equn.getProducts().size(); j++) {
					if (equn.getProducts().get(j).getMetaboliteAbbreviation().equals(oldReactant)) {
						equn.getProducts().get(j).setMetaboliteAbbreviation(newReactant);
					}
				}
				equn.writeReactionEquation();
				GraphicalInterface.updateReactionsCellById(equn.equationAbbreviations, participatingReactions.get(i), GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN);
			} else if (column == GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN) {
				for (int j = 0; j < equn.getReactants().size(); j++) {
					if (equn.getReactants().get(j).getMetaboliteName().equals(oldReactant)) {
						equn.getReactants().get(j).setMetaboliteName(newReactant);
					}
				}
				for (int j = 0; j < equn.getProducts().size(); j++) {
					if (equn.getProducts().get(j).getMetaboliteName().equals(oldReactant)) {
						equn.getProducts().get(j).setMetaboliteName(newReactant);
					}
				}
				equn.writeReactionEquation();
				GraphicalInterface.updateReactionsCellById(equn.equationNames, participatingReactions.get(i), GraphicalInterfaceConstants.REACTION_EQUN_NAMES_COLUMN);
			}								
		}
		
//		System.out.println("bef" + LocalConfig.getInstance().getMetaboliteAbbreviationIdMap());
//		System.out.println("bef" + LocalConfig.getInstance().getMetaboliteUsedMap());		
		if (column == GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN) {
			if (LocalConfig.getInstance().getMetaboliteUsedMap().containsKey(oldReactant)) {
				int usedCount = (Integer) LocalConfig.getInstance().getMetaboliteUsedMap().get(oldReactant);
				LocalConfig.getInstance().getMetaboliteUsedMap().remove(oldReactant);
				LocalConfig.getInstance().getMetaboliteUsedMap().put(newReactant, new Integer(usedCount));									
			}
			LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().remove(oldReactant);
			LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().put(newReactant, this.id);
		}
//		System.out.println("aft" + LocalConfig.getInstance().getMetaboliteAbbreviationIdMap());
//		System.out.println("aft" + LocalConfig.getInstance().getMetaboliteUsedMap());
	}
	
	@SuppressWarnings( "rawtypes" )
	public static Object getKeyFromValue(Map hm, Object value) {
		for (Object o : hm.keySet()) {
			if (hm.get(o).equals(value)) {
				return o;
			}
		}
		return null;
	}
	
	public static void updateCellById(String value, int id, int col) {
		Map<String, Object> reactionsIdRowMap = new HashMap<String, Object>();
		for (int i = 0; i < GraphicalInterface.metabolitesTable.getRowCount(); i++) {
			reactionsIdRowMap.put((String) GraphicalInterface.metabolitesTable.getModel().getValueAt(i, GraphicalInterfaceConstants.METABOLITE_ID_COLUMN), i);
		}
		String row = (reactionsIdRowMap.get(Integer.toString(id))).toString();
		int rowNum = Integer.valueOf(row);
		GraphicalInterface.metabolitesTable.getModel().setValueAt(value, rowNum, col);
	}
	
	public static void main(String[] args) {
		
	}
	
}
