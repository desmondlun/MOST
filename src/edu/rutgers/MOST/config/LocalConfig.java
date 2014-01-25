package edu.rutgers.MOST.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;

import edu.rutgers.MOST.data.ModelReactionEquation;
import edu.rutgers.MOST.presentation.GraphicalInterface;

public class LocalConfig {	

	
	//Singleton pattern:
	private static final LocalConfig instance = new LocalConfig();

	// Private constructor prevents instantiation from other classes
	private LocalConfig() { }

	public static LocalConfig getInstance() {
		return instance;
	}
	
    private static String modelName;
    
    public static String getModelName() {
		return modelName;
	}

	public static void setModelName(String modelName) {
		LocalConfig.modelName = modelName;
	}
	
	/*****************************************************************************/
	// table model maps
	/*****************************************************************************/
	
	private static Map<String, DefaultTableModel> metabolitesTableModelMap;

	public static Map<String, DefaultTableModel> getMetabolitesTableModelMap() {
		return metabolitesTableModelMap;
	}

	public static void setMetabolitesTableModelMap(
			Map<String, DefaultTableModel> metabolitesTableModelMap) {
		LocalConfig.metabolitesTableModelMap = metabolitesTableModelMap;
	}
	
	private static Map<String, DefaultTableModel> reactionsTableModelMap;

	public static Map<String, DefaultTableModel> getReactionsTableModelMap() {
		return reactionsTableModelMap;
	}

	public static void setReactionsTableModelMap(
			Map<String, DefaultTableModel> reactionsTableModelMap) {
		LocalConfig.reactionsTableModelMap = reactionsTableModelMap;
	}

	/*****************************************************************************/
	// end table model maps
	/*****************************************************************************/
	
	private static Integer progress;
	
	public void setProgress(Integer progress) {
		this.progress = progress;
	}
	
	public Integer getProgress() {
		return progress;
	}
	
	private static File metabolitesCSVFile;

	public void setMetabolitesCSVFile(File metabolitesCSVFile) {
		LocalConfig.metabolitesCSVFile = metabolitesCSVFile;
	}

	public static File getMetabolitesCSVFile() {
		return metabolitesCSVFile;
	}

	private static File reactionsCSVFile;

	public void setReactionsCSVFile(File reactionsCSVFile) {
		LocalConfig.reactionsCSVFile = reactionsCSVFile;
	}

	public static File getReactionsCSVFile() {
		return reactionsCSVFile;
	}
	
	private static ArrayList<Integer> participatingReactions;

	public void setParticipatingReactions(ArrayList<Integer> participatingReactions) {
		LocalConfig.participatingReactions = participatingReactions;
	}

	public ArrayList<Integer> getParticipatingReactions() {
		return participatingReactions;
	}  
	
	// list used when exiting program. If items remain in list at exit, user prompted
	// to save these files
	private static ArrayList<String> optimizationFilesList;

	public void setOptimizationFilesList(ArrayList<String> optimizationFilesList) {
		LocalConfig.optimizationFilesList = optimizationFilesList;
	}

	public ArrayList<String> getOptimizationFilesList() {
		return optimizationFilesList;
	}  
	
	//Map used to hold number of reactions a metabolite is used in. if a metabolites
	//is not present in map, it is unused. Also used when adding, deleting or changing
	//reactions to determine whether the used status of a metabolite must be changed.
	private static Map<String, Object> metaboliteUsedMap = new HashMap<String, Object>();
	
	public static Map<String, Object> getMetaboliteUsedMap() {
		return metaboliteUsedMap;
	}

	public void setMetaboliteUsedMap(Map<String, Object> metaboliteUsedMap) {
		LocalConfig.metaboliteUsedMap = metaboliteUsedMap;
	}
	
    private static ArrayList<Integer> unusedList = new ArrayList<Integer>();
	
	public static ArrayList<Integer> getUnusedList() {
		return unusedList;
	}

	public static void setUnusedList(ArrayList<Integer> unusedList) {
		LocalConfig.unusedList = unusedList;
	}
    
    //map used to hold metabolite abbreviation/id pairs, in order to construct reaction_reactant
	//and reaction_product (lookup) tables
    public static Map<String, Object> metaboliteAbbreviationIdMap = new HashMap<String, Object>();

    public static Map<String, Object> getMetaboliteAbbreviationIdMap() {
		return metaboliteAbbreviationIdMap;
	}

	public static void setMetaboliteAbbreviationIdMap(
			Map<String, Object> metaboliteAbbreviationIdMap) {
		LocalConfig.metaboliteAbbreviationIdMap = metaboliteAbbreviationIdMap;
	}
	
	private static Map<Object, String> metaboliteIdNameMap;
	
    public static Map<Object, String> getMetaboliteIdNameMap() {
		return metaboliteIdNameMap;
	}

	public static void setMetaboliteIdNameMap(
			Map<Object, String> metaboliteIdNameMap) {
		LocalConfig.metaboliteIdNameMap = metaboliteIdNameMap;
	}
	
	//used for determining id when adding a metabolite when a reaction is
	//read and metabolite is not present
	private static Integer maxMetabolite;
	
	public static Integer getMaxMetabolite() {
		return maxMetabolite;
	}

	public static void setMaxMetabolite(Integer maxMetabolite) {
		LocalConfig.maxMetabolite = maxMetabolite;
	}

	// used for adding rows
	private static Integer maxMetaboliteId;
	
	public void setMaxMetaboliteId(Integer maxMetaboliteId) {
		LocalConfig.maxMetaboliteId = maxMetaboliteId;
	}
	
	public Integer getMaxMetaboliteId() {
		return maxMetaboliteId;
	}
	
	private static Integer maxReactionId;

	public static Integer getMaxReactionId() {
		return maxReactionId;
	}

	public static void setMaxReactionId(Integer maxReactionId) {
		LocalConfig.maxReactionId = maxReactionId;
	}

	private static Map<Object, ModelReactionEquation> reactionEquationMap;

	public static Map<Object, ModelReactionEquation> getReactionEquationMap() {
		return reactionEquationMap;
	}

	public static void setReactionEquationMap(
			Map<Object, ModelReactionEquation> reactionEquationMap) {
		LocalConfig.reactionEquationMap = reactionEquationMap;
	}

	private static Map<String, Object> reactionsIdRowMap;

	public static Map<String, Object> getReactionsIdRowMap() {
		return reactionsIdRowMap;
	}

	public static void setReactionsIdRowMap(Map<String, Object> reactionsIdRowMap) {
		LocalConfig.reactionsIdRowMap = reactionsIdRowMap;
	}

	private static ArrayList<String> metabolitesMetaColumnNames;

	public ArrayList<String> getMetabolitesMetaColumnNames() {
		return metabolitesMetaColumnNames;
	}

	public void setMetabolitesMetaColumnNames(
			ArrayList<String> metabolitesMetaColumnNames) {
		LocalConfig.metabolitesMetaColumnNames = metabolitesMetaColumnNames;
	}
	
	private static ArrayList<String> reactionsMetaColumnNames;

	public ArrayList<String> getReactionsMetaColumnNames() {
		return reactionsMetaColumnNames;
	}

	public void setReactionsMetaColumnNames(
			ArrayList<String> reactionsMetaColumnNames) {
		LocalConfig.reactionsMetaColumnNames = reactionsMetaColumnNames;
	}
	
	private static ArrayList<Integer> suspiciousMetabolites = new ArrayList<Integer>();
	
	public ArrayList<Integer> getSuspiciousMetabolites() {
		return suspiciousMetabolites;
	}
	
	public void setSuspiciousMetabolites(ArrayList<Integer> suspiciousMetabolites) {
		LocalConfig.suspiciousMetabolites = suspiciousMetabolites;
	}
	
	public boolean hasMetabolitesFile;
	public boolean hasReactionsFile;
	
	/**********************************************************************************/
	//parameters for metabolites in columnNameInterfaces
	/**********************************************************************************/
    	
	//column indices
    private static Integer metaboliteAbbreviationColumnIndex;
	
	public void setMetaboliteAbbreviationColumnIndex(Integer metaboliteAbbreviationColumnIndex) {
		LocalConfig.metaboliteAbbreviationColumnIndex = metaboliteAbbreviationColumnIndex;
	}

	public static Integer getMetaboliteAbbreviationColumnIndex() {
		return metaboliteAbbreviationColumnIndex;
	}
	
    private static Integer metaboliteNameColumnIndex;
	
	public void setMetaboliteNameColumnIndex(Integer metaboliteNameColumnIndex) {
		LocalConfig.metaboliteNameColumnIndex = metaboliteNameColumnIndex;
	}

	public static Integer getMetaboliteNameColumnIndex() {
		return metaboliteNameColumnIndex;
	}
	
    private static Integer chargeColumnIndex;
	
	public void setChargeColumnIndex(Integer chargeColumnIndex) {
		LocalConfig.chargeColumnIndex = chargeColumnIndex;
	}

	public static Integer getChargeColumnIndex() {
		return chargeColumnIndex;
	}
	
	private static Integer compartmentColumnIndex;
	
	public void setCompartmentColumnIndex(Integer compartmentColumnIndex) {
		LocalConfig.compartmentColumnIndex = compartmentColumnIndex;
	}

	public static Integer getCompartmentColumnIndex() {
		return compartmentColumnIndex;
	}
	
    private static Integer boundaryColumnIndex;
	
	public void setBoundaryColumnIndex(Integer boundaryColumnIndex) {
		LocalConfig.boundaryColumnIndex = boundaryColumnIndex;
	}

	public static Integer getBoundaryColumnIndex() {
		return boundaryColumnIndex;
	}
	
	private static ArrayList<Integer> metabolitesMetaColumnIndexList;
	
	public ArrayList<Integer> getMetabolitesMetaColumnIndexList() {
		return metabolitesMetaColumnIndexList;
	}
	
	public void setMetabolitesMetaColumnIndexList(ArrayList<Integer> metabolitesMetaColumnIndexList) {
		LocalConfig.metabolitesMetaColumnIndexList = metabolitesMetaColumnIndexList;
	}    
	
    private static Integer metabolitesNextRowCorrection;
	
	public void setMetabolitesNextRowCorrection(Integer metabolitesNextRowCorrection) {
		LocalConfig.metabolitesNextRowCorrection = metabolitesNextRowCorrection;
	}

	public static Integer getMetabolitesNextRowCorrection() {
		return metabolitesNextRowCorrection;
	}
	
	/**********************************************************************************/
	//parameters for reactions in columnNameInterfaces
	/**********************************************************************************/
	//reaction column indices
	
	private static Integer knockoutColumnIndex;

	public Integer getKnockoutColumnIndex() {
		return knockoutColumnIndex;
	}

	public void setKnockoutColumnIndex(Integer knockoutColumnIndex) {
		LocalConfig.knockoutColumnIndex = knockoutColumnIndex;
	}
	
	private static Integer fluxValueColumnIndex;

	public Integer getFluxValueColumnIndex() {
		return fluxValueColumnIndex;
	}

	public void setFluxValueColumnIndex(Integer fluxValueColumnIndex) {
		LocalConfig.fluxValueColumnIndex = fluxValueColumnIndex;
	}
	
    private static Integer reactionAbbreviationColumnIndex;
	
	public void setReactionAbbreviationColumnIndex(Integer reactionAbbreviationColumnIndex) {
		LocalConfig.reactionAbbreviationColumnIndex = reactionAbbreviationColumnIndex;
	}

	public static Integer getReactionAbbreviationColumnIndex() {
		return reactionAbbreviationColumnIndex;
	}
	
    private static Integer reactionNameColumnIndex;
	
	public void setReactionNameColumnIndex(Integer reactionNameColumnIndex) {
		LocalConfig.reactionNameColumnIndex = reactionNameColumnIndex;
	}

	public static Integer getReactionNameColumnIndex() {
		return reactionNameColumnIndex;
	}
	
	private static Integer reactionEquationColumnIndex;

	public Integer getReactionEquationColumnIndex() {
		return reactionEquationColumnIndex;
	}

	public void setReactionEquationColumnIndex(Integer reactionEquationColumnIndex) {
		LocalConfig.reactionEquationColumnIndex = reactionEquationColumnIndex;
	}
	
	private static Integer reactionEquationNamesColumnIndex;

	public Integer getReactionEquationNamesColumnIndex() {
		return reactionEquationNamesColumnIndex;
	}

	public void setReactionEquationNamesColumnIndex(Integer reactionEquationNamesColumnIndex) {
		LocalConfig.reactionEquationNamesColumnIndex = reactionEquationNamesColumnIndex;
	}
	
	private static Integer reversibleColumnIndex;

	public Integer getReversibleColumnIndex() {
		return reversibleColumnIndex;
	}

	public void setReversibleColumnIndex(Integer reversibleColumnIndex) {
		LocalConfig.reversibleColumnIndex = reversibleColumnIndex;
	}
	
	private static Integer lowerBoundColumnIndex;

	public Integer getLowerBoundColumnIndex() {
		return lowerBoundColumnIndex;
	}

	public void setLowerBoundColumnIndex(Integer lowerBoundColumnIndex) {
		LocalConfig.lowerBoundColumnIndex = lowerBoundColumnIndex;
	}
	
	private static Integer upperBoundColumnIndex;

	public Integer getUpperBoundColumnIndex() {
		return upperBoundColumnIndex;
	}

	public void setUpperBoundColumnIndex(Integer upperBoundColumnIndex) {
		LocalConfig.upperBoundColumnIndex = upperBoundColumnIndex;
	}
	
	private static Integer biologicalObjectiveColumnIndex;

	public Integer getBiologicalObjectiveColumnIndex() {
		return biologicalObjectiveColumnIndex;
	}

	public void setBiologicalObjectiveColumnIndex(Integer biologicalObjectiveColumnIndex) {
		LocalConfig.biologicalObjectiveColumnIndex = biologicalObjectiveColumnIndex;
	}
	
	private static Integer syntheticObjectiveColumnIndex;

	public Integer getSyntheticObjectiveColumnIndex() {
		return syntheticObjectiveColumnIndex;
	}

	public void setSyntheticObjectiveColumnIndex(Integer syntheticObjectiveColumnIndex) {
		LocalConfig.syntheticObjectiveColumnIndex = syntheticObjectiveColumnIndex;
	}
	
	private static Integer geneAssociationColumnIndex;

	public Integer getGeneAssociationColumnIndex() {
		return geneAssociationColumnIndex;
	}

	public void setGeneAssociationColumnIndex(Integer geneAssociationColumnIndex) {
		LocalConfig.geneAssociationColumnIndex = geneAssociationColumnIndex;
	}
	
	private static Integer proteinAssociationColumnIndex;
	
    public static Integer getProteinAssociationColumnIndex() {
		return proteinAssociationColumnIndex;
	}

	public static void setProteinAssociationColumnIndex(
			Integer proteinAssociationColumnIndex) {
		LocalConfig.proteinAssociationColumnIndex = proteinAssociationColumnIndex;
	}

	private static Integer subsystemColumnIndex;
	
	public static Integer getSubsystemColumnIndex() {
		return subsystemColumnIndex;
	}

	public static void setSubsystemColumnIndex(Integer subsystemColumnIndex) {
		LocalConfig.subsystemColumnIndex = subsystemColumnIndex;
	}

	private static Integer proteinClassColumnIndex;
	
	public static Integer getProteinClassColumnIndex() {
		return proteinClassColumnIndex;
	}

	public static void setProteinClassColumnIndex(Integer proteinClassColumnIndex) {
		LocalConfig.proteinClassColumnIndex = proteinClassColumnIndex;
	}

	private static ArrayList<Integer> reactionsMetaColumnIndexList;
	
	public ArrayList<Integer> getReactionsMetaColumnIndexList() {
		return reactionsMetaColumnIndexList;
	}
	
	public void setReactionsMetaColumnIndexList(ArrayList<Integer> reactionsMetaColumnIndexList) {
		LocalConfig.reactionsMetaColumnIndexList = reactionsMetaColumnIndexList;
	}
	
    private static Integer reactionsNextRowCorrection;
	
	public void setReactionsNextRowCorrection(Integer reactionsNextRowCorrection) {
		LocalConfig.reactionsNextRowCorrection = reactionsNextRowCorrection;
	}

	public static Integer getReactionsNextRowCorrection() {
		return reactionsNextRowCorrection;
	}
	
	/**********************************************************************************/
	//end parameters for columnNameInterfaces
	/**********************************************************************************/
	
	// if "No" button pressed in Add Metabolite Prompt, is set to false
	public boolean addMetaboliteOption;	
	public boolean noButtonClicked;
	public boolean yesToAllButtonClicked;
	public boolean pastedReaction;
	public boolean includesReactions;
	
	public boolean reactionsTableChanged;
	public boolean metabolitesTableChanged;
	
	public boolean findFieldChanged;
	public boolean replaceFieldChanged;
	public boolean findReplaceFocusLost;
	public boolean findReplaceFocusGained;
	
	public boolean addReactantPromptShown;
	public boolean reactionEditorVisible;
	public boolean loadExistingVisible;
	public boolean addColumnInterfaceVisible;
	
	public boolean hasValidGurobiKey;
	
	private static Integer reactionsLocationsListCount;
	
	public void setReactionsLocationsListCount(Integer reactionsLocationsListCount) {
		LocalConfig.reactionsLocationsListCount = reactionsLocationsListCount;
	}
	
	public Integer getReactionsLocationsListCount() {
		return reactionsLocationsListCount;
	}
	
	private static Integer metabolitesLocationsListCount;

	public static Integer getMetabolitesLocationsListCount() {
		return metabolitesLocationsListCount;
	}

	public static void setMetabolitesLocationsListCount(
			Integer metabolitesLocationsListCount) {
		LocalConfig.metabolitesLocationsListCount = metabolitesLocationsListCount;
	}
	
	private static  ArrayList<String> findEntryList = new ArrayList<String>();
	
	public static ArrayList<String> getFindEntryList() {
		return findEntryList;
	}

	public static void setFindEntryList(ArrayList<String> findEntryList) {
		LocalConfig.findEntryList = findEntryList;
	}

	private static  ArrayList<String> replaceEntryList = new ArrayList<String>();
	
	public static ArrayList<String> getReplaceEntryList() {
		return replaceEntryList;
	}

	public static void setReplaceEntryList(ArrayList<String> replaceEntryList) {
		LocalConfig.replaceEntryList = replaceEntryList;
	}
	
	private static Map<String, Object> metabDisplayCollectionMap = new HashMap<String, Object>();
	
	public static Map<String, Object> getMetabDisplayCollectionMap() {
		return metabDisplayCollectionMap;
	}

	public static void setMetabDisplayCollectionMap(
			Map<String, Object> metabDisplayCollectionMap) {
		LocalConfig.metabDisplayCollectionMap = metabDisplayCollectionMap;
	}

	/********************************************************************************/
	// undo/redo
	/********************************************************************************/
	
	private static Map<Object, Object> undoItemMap = new HashMap<Object, Object>();	

	public static Map<Object, Object> getUndoItemMap() {
		return undoItemMap;
	}

	public static void setUndoItemMap(Map<Object, Object> undoItemMap) {
		LocalConfig.undoItemMap = undoItemMap;
	}
	
	private static Map<Object, Object> redoItemMap = new HashMap<Object, Object>();	

	public static Map<Object, Object> getRedoItemMap() {
		return redoItemMap;
	}

	public static void setRedoItemMap(Map<Object, Object> redoItemMap) {
		LocalConfig.redoItemMap = redoItemMap;
	}
	
	private Integer undoMenuIndex;

	public Integer getUndoMenuIndex() {
		return undoMenuIndex;
	}

	public void setUndoMenuIndex(Integer undoMenuIndex) {
		this.undoMenuIndex = undoMenuIndex;
	}	
	
	// used for remembering sort events for undo
	private ArrayList<Integer> reactionsSortColumns = new ArrayList<Integer>();	
	private ArrayList<SortOrder> reactionsSortOrderList = new ArrayList<SortOrder>();	
	private ArrayList<Integer> metabolitesSortColumns = new ArrayList<Integer>();	
	private ArrayList<SortOrder> metabolitesSortOrderList = new ArrayList<SortOrder>();
	private ArrayList<Integer> reactionsRedoSortColumns = new ArrayList<Integer>();	
	private ArrayList<SortOrder> reactionsRedoSortOrderList = new ArrayList<SortOrder>();	
	private ArrayList<Integer> metabolitesRedoSortColumns = new ArrayList<Integer>();	
	private ArrayList<SortOrder> metabolitesRedoSortOrderList = new ArrayList<SortOrder>();
	
	public ArrayList<Integer> getReactionsSortColumns() {
		return reactionsSortColumns;
	}

	public void setReactionsSortColumns(ArrayList<Integer> reactionsSortColumns) {
		this.reactionsSortColumns = reactionsSortColumns;
	}

	public ArrayList<SortOrder> getReactionsSortOrderList() {
		return reactionsSortOrderList;
	}

	public void setReactionsSortOrderList(ArrayList<SortOrder> reactionsSortOrderList) {
		this.reactionsSortOrderList = reactionsSortOrderList;
	}

	public ArrayList<Integer> getMetabolitesSortColumns() {
		return metabolitesSortColumns;
	}

	public void setMetabolitesSortColumns(ArrayList<Integer> metabolitesSortColumns) {
		this.metabolitesSortColumns = metabolitesSortColumns;
	}

	public ArrayList<SortOrder> getMetabolitesSortOrderList() {
		return metabolitesSortOrderList;
	}

	public void setMetabolitesSortOrderList(ArrayList<SortOrder> metabolitesSortOrderList) {
		this.metabolitesSortOrderList = metabolitesSortOrderList;
	}
	
	public ArrayList<Integer> getReactionsRedoSortColumns() {
		return reactionsRedoSortColumns;
	}

	public void setReactionsRedoSortColumns(
			ArrayList<Integer> reactionsRedoSortColumns) {
		this.reactionsRedoSortColumns = reactionsRedoSortColumns;
	}

	public ArrayList<SortOrder> getReactionsRedoSortOrderList() {
		return reactionsRedoSortOrderList;
	}

	public void setReactionsRedoSortOrderList(
			ArrayList<SortOrder> reactionsRedoSortOrderList) {
		this.reactionsRedoSortOrderList = reactionsRedoSortOrderList;
	}

	public ArrayList<Integer> getMetabolitesRedoSortColumns() {
		return metabolitesRedoSortColumns;
	}

	public void setMetabolitesRedoSortColumns(
			ArrayList<Integer> metabolitesRedoSortColumns) {
		this.metabolitesRedoSortColumns = metabolitesRedoSortColumns;
	}

	public ArrayList<SortOrder> getMetabolitesRedoSortOrderList() {
		return metabolitesRedoSortOrderList;
	}

	public void setMetabolitesRedoSortOrderList(
			ArrayList<SortOrder> metabolitesRedoSortOrderList) {
		this.metabolitesRedoSortOrderList = metabolitesRedoSortOrderList;
	}

	private int numReactionTablesCopied;
	
	private int numMetabolitesTableCopied;

	public int getNumReactionTablesCopied() {
		return numReactionTablesCopied;
	}

	public void setNumReactionTablesCopied(int numReactionTablesCopied) {
		this.numReactionTablesCopied = numReactionTablesCopied;
	}

	public int getNumMetabolitesTableCopied() {
		return numMetabolitesTableCopied;
	}

	public void setNumMetabolitesTableCopied(int numMetabolitesTableCopied) {
		this.numMetabolitesTableCopied = numMetabolitesTableCopied;
	}
	
	// used in undo for removing metabolites added when editing reactions
	private ArrayList<Integer> addedMetabolites = new ArrayList<Integer>();

	public ArrayList<Integer> getAddedMetabolites() {
		return addedMetabolites;
	}

	public void setAddedMetabolites(ArrayList<Integer> addedMetabolites) {
		this.addedMetabolites = addedMetabolites;
	}
	
	private static Map<String, DefaultTableModel> metabolitesUndoTableModelMap;
	
	public static Map<String, DefaultTableModel> getMetabolitesUndoTableModelMap() {
		return metabolitesUndoTableModelMap;
	}

	public static void setMetabolitesUndoTableModelMap(
			Map<String, DefaultTableModel> metabolitesUndoTableModelMap) {
		LocalConfig.metabolitesUndoTableModelMap = metabolitesUndoTableModelMap;
	}

	private static Map<String, DefaultTableModel> reactionsUndoTableModelMap;

	public static Map<String, DefaultTableModel> getReactionsUndoTableModelMap() {
		return reactionsUndoTableModelMap;
	}

	public static void setReactionsUndoTableModelMap(
			Map<String, DefaultTableModel> reactionsUndoTableModelMap) {
		LocalConfig.reactionsUndoTableModelMap = reactionsUndoTableModelMap;
	}

}