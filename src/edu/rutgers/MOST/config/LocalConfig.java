package edu.rutgers.MOST.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;

import edu.rutgers.MOST.data.ModelReactionEquation;

public class LocalConfig {	

	
	//Singleton pattern:
	private static final LocalConfig instance = new LocalConfig();

	// Private constructor prevents instantiation from other classes
	private LocalConfig() { }

	public static LocalConfig getInstance() {
		return instance;
	}
	
    private String modelName;
    
    public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	
	/*****************************************************************************/
	// table model maps
	/*****************************************************************************/
	
	private Map<String, DefaultTableModel> metabolitesTableModelMap;

	public Map<String, DefaultTableModel> getMetabolitesTableModelMap() {
		return metabolitesTableModelMap;
	}

	public void setMetabolitesTableModelMap(
			Map<String, DefaultTableModel> metabolitesTableModelMap) {
		this.metabolitesTableModelMap = metabolitesTableModelMap;
	}
	
	private Map<String, DefaultTableModel> reactionsTableModelMap;

	public Map<String, DefaultTableModel> getReactionsTableModelMap() {
		return reactionsTableModelMap;
	}

	public void setReactionsTableModelMap(
			Map<String, DefaultTableModel> reactionsTableModelMap) {
		this.reactionsTableModelMap = reactionsTableModelMap;
	}

	/*****************************************************************************/
	// end table model maps
	/*****************************************************************************/
	
	private Integer progress;
	
	public void setProgress(Integer progress) {
		this.progress = progress;
	}
	
	public Integer getProgress() {
		return progress;
	}
	
    private ArrayList<String> invalidReactions = new ArrayList<String>();
	
	public ArrayList<String> getInvalidReactions() {
		return invalidReactions;
	}
	
	public void setInvalidReactions(ArrayList<String> invalidReactions) {
		this.invalidReactions = invalidReactions;
	}
	
	private File metabolitesCSVFile;

	public void setMetabolitesCSVFile(File metabolitesCSVFile) {
		this.metabolitesCSVFile = metabolitesCSVFile;
	}

	public File getMetabolitesCSVFile() {
		return metabolitesCSVFile;
	}

	private File reactionsCSVFile;

	public void setReactionsCSVFile(File reactionsCSVFile) {
		this.reactionsCSVFile = reactionsCSVFile;
	}

	public File getReactionsCSVFile() {
		return reactionsCSVFile;
	}
	
	private ArrayList<Integer> participatingReactions;

	public void setParticipatingReactions(ArrayList<Integer> participatingReactions) {
		this.participatingReactions = participatingReactions;
	}

	public ArrayList<Integer> getParticipatingReactions() {
		return participatingReactions;
	}  
	
	// list used when exiting program. If items remain in list at exit, user prompted
	// to save these files
	private ArrayList<String> optimizationFilesList;

	public void setOptimizationFilesList(ArrayList<String> optimizationFilesList) {
		this.optimizationFilesList = optimizationFilesList;
	}

	public ArrayList<String> getOptimizationFilesList() {
		return optimizationFilesList;
	}  
	
	//Map used to hold number of reactions a metabolite is used in. if a metabolites
	//is not present in map, it is unused. Also used when adding, deleting or changing
	//reactions to determine whether the used status of a metabolite must be changed.
	private Map<String, Object> metaboliteUsedMap = new HashMap<String, Object>();
	
	public Map<String, Object> getMetaboliteUsedMap() {
		return metaboliteUsedMap;
	}

	public void setMetaboliteUsedMap(Map<String, Object> metaboliteUsedMap) {
		this.metaboliteUsedMap = metaboliteUsedMap;
	}
	
    private ArrayList<Integer> unusedList = new ArrayList<Integer>();
	
	public ArrayList<Integer> getUnusedList() {
		return unusedList;
	}

	public void setUnusedList(ArrayList<Integer> unusedList) {
		this.unusedList = unusedList;
	}
    
    //map used to hold metabolite abbreviation/id pairs, in order to construct reaction_reactant
	//and reaction_product (lookup) tables
    public Map<String, Object> metaboliteAbbreviationIdMap = new HashMap<String, Object>();

    public Map<String, Object> getMetaboliteAbbreviationIdMap() {
		return metaboliteAbbreviationIdMap;
	}

	public void setMetaboliteAbbreviationIdMap(
			Map<String, Object> metaboliteAbbreviationIdMap) {
		this.metaboliteAbbreviationIdMap = metaboliteAbbreviationIdMap;
	}
	
	private Map<Object, String> metaboliteIdNameMap;
	
    public Map<Object, String> getMetaboliteIdNameMap() {
		return metaboliteIdNameMap;
	}

	public void setMetaboliteIdNameMap(
			Map<Object, String> metaboliteIdNameMap) {
		this.metaboliteIdNameMap = metaboliteIdNameMap;
	}
	
	//used for determining id when adding a metabolite when a reaction is
	//read and metabolite is not present
	private Integer maxMetabolite;
	
	public Integer getMaxMetabolite() {
		return maxMetabolite;
	}

	public void setMaxMetabolite(Integer maxMetabolite) {
		this.maxMetabolite = maxMetabolite;
	}

	// used for adding rows
	private Integer maxMetaboliteId;
	
	public void setMaxMetaboliteId(Integer maxMetaboliteId) {
		this.maxMetaboliteId = maxMetaboliteId;
	}
	
	public Integer getMaxMetaboliteId() {
		return maxMetaboliteId;
	}
	
	private Integer maxReactionId;

	public Integer getMaxReactionId() {
		return maxReactionId;
	}

	public void setMaxReactionId(Integer maxReactionId) {
		this.maxReactionId = maxReactionId;
	}

	private Map<Object, ModelReactionEquation> reactionEquationMap;

	public Map<Object, ModelReactionEquation> getReactionEquationMap() {
		return reactionEquationMap;
	}

	public void setReactionEquationMap(
			Map<Object, ModelReactionEquation> reactionEquationMap) {
		this.reactionEquationMap = reactionEquationMap;
	}

	private Map<String, Object> reactionsIdRowMap;

	public Map<String, Object> getReactionsIdRowMap() {
		return reactionsIdRowMap;
	}

	public void setReactionsIdRowMap(Map<String, Object> reactionsIdRowMap) {
		this.reactionsIdRowMap = reactionsIdRowMap;
	}

	private ArrayList<String> metabolitesMetaColumnNames;

	public ArrayList<String> getMetabolitesMetaColumnNames() {
		return metabolitesMetaColumnNames;
	}

	public void setMetabolitesMetaColumnNames(
			ArrayList<String> metabolitesMetaColumnNames) {
		this.metabolitesMetaColumnNames = metabolitesMetaColumnNames;
	}
	
	private ArrayList<String> reactionsMetaColumnNames;

	public ArrayList<String> getReactionsMetaColumnNames() {
		return reactionsMetaColumnNames;
	}

	public void setReactionsMetaColumnNames(
			ArrayList<String> reactionsMetaColumnNames) {
		this.reactionsMetaColumnNames = reactionsMetaColumnNames;
	}
	
	private ArrayList<Integer> suspiciousMetabolites = new ArrayList<Integer>();
	
	public ArrayList<Integer> getSuspiciousMetabolites() {
		return suspiciousMetabolites;
	}
	
	public void setSuspiciousMetabolites(ArrayList<Integer> suspiciousMetabolites) {
		this.suspiciousMetabolites = suspiciousMetabolites;
	}
	
	public boolean hasMetabolitesFile;
	public boolean hasReactionsFile;
	
	/**********************************************************************************/
	//parameters for metabolites in columnNameInterfaces
	/**********************************************************************************/
    	
	//column indices
    private Integer metaboliteAbbreviationColumnIndex;
	
	public void setMetaboliteAbbreviationColumnIndex(Integer metaboliteAbbreviationColumnIndex) {
		this.metaboliteAbbreviationColumnIndex = metaboliteAbbreviationColumnIndex;
	}

	public Integer getMetaboliteAbbreviationColumnIndex() {
		return metaboliteAbbreviationColumnIndex;
	}
	
    private Integer metaboliteNameColumnIndex;
	
	public void setMetaboliteNameColumnIndex(Integer metaboliteNameColumnIndex) {
		this.metaboliteNameColumnIndex = metaboliteNameColumnIndex;
	}

	public Integer getMetaboliteNameColumnIndex() {
		return metaboliteNameColumnIndex;
	}
	
    private Integer chargeColumnIndex;
	
	public void setChargeColumnIndex(Integer chargeColumnIndex) {
		this.chargeColumnIndex = chargeColumnIndex;
	}

	public Integer getChargeColumnIndex() {
		return chargeColumnIndex;
	}
	
	private Integer compartmentColumnIndex;
	
	public void setCompartmentColumnIndex(Integer compartmentColumnIndex) {
		this.compartmentColumnIndex = compartmentColumnIndex;
	}

	public Integer getCompartmentColumnIndex() {
		return compartmentColumnIndex;
	}
	
    private Integer boundaryColumnIndex;
	
	public void setBoundaryColumnIndex(Integer boundaryColumnIndex) {
		this.boundaryColumnIndex = boundaryColumnIndex;
	}

	public Integer getBoundaryColumnIndex() {
		return boundaryColumnIndex;
	}
	
	private ArrayList<Integer> metabolitesMetaColumnIndexList;
	
	public ArrayList<Integer> getMetabolitesMetaColumnIndexList() {
		return metabolitesMetaColumnIndexList;
	}
	
	public void setMetabolitesMetaColumnIndexList(ArrayList<Integer> metabolitesMetaColumnIndexList) {
		this.metabolitesMetaColumnIndexList = metabolitesMetaColumnIndexList;
	}    
	
    private Integer metabolitesNextRowCorrection;
	
	public void setMetabolitesNextRowCorrection(Integer metabolitesNextRowCorrection) {
		this.metabolitesNextRowCorrection = metabolitesNextRowCorrection;
	}

	public Integer getMetabolitesNextRowCorrection() {
		return metabolitesNextRowCorrection;
	}
	
	/**********************************************************************************/
	//parameters for reactions in columnNameInterfaces
	/**********************************************************************************/
	//reaction column indices
	
	private Integer knockoutColumnIndex;

	public Integer getKnockoutColumnIndex() {
		return knockoutColumnIndex;
	}

	public void setKnockoutColumnIndex(Integer knockoutColumnIndex) {
		this.knockoutColumnIndex = knockoutColumnIndex;
	}
	
	private Integer fluxValueColumnIndex;

	public Integer getFluxValueColumnIndex() {
		return fluxValueColumnIndex;
	}

	public void setFluxValueColumnIndex(Integer fluxValueColumnIndex) {
		this.fluxValueColumnIndex = fluxValueColumnIndex;
	}
	
    private Integer reactionAbbreviationColumnIndex;
	
	public void setReactionAbbreviationColumnIndex(Integer reactionAbbreviationColumnIndex) {
		this.reactionAbbreviationColumnIndex = reactionAbbreviationColumnIndex;
	}

	public Integer getReactionAbbreviationColumnIndex() {
		return reactionAbbreviationColumnIndex;
	}
	
    private Integer reactionNameColumnIndex;
	
	public void setReactionNameColumnIndex(Integer reactionNameColumnIndex) {
		this.reactionNameColumnIndex = reactionNameColumnIndex;
	}

	public Integer getReactionNameColumnIndex() {
		return reactionNameColumnIndex;
	}
	
	private Integer reactionEquationColumnIndex;

	public Integer getReactionEquationColumnIndex() {
		return reactionEquationColumnIndex;
	}

	public void setReactionEquationColumnIndex(Integer reactionEquationColumnIndex) {
		this.reactionEquationColumnIndex = reactionEquationColumnIndex;
	}
	
	private Integer reactionEquationNamesColumnIndex;

	public Integer getReactionEquationNamesColumnIndex() {
		return reactionEquationNamesColumnIndex;
	}

	public void setReactionEquationNamesColumnIndex(Integer reactionEquationNamesColumnIndex) {
		this.reactionEquationNamesColumnIndex = reactionEquationNamesColumnIndex;
	}
	
	private Integer reversibleColumnIndex;

	public Integer getReversibleColumnIndex() {
		return reversibleColumnIndex;
	}

	public void setReversibleColumnIndex(Integer reversibleColumnIndex) {
		this.reversibleColumnIndex = reversibleColumnIndex;
	}
	
	private Integer lowerBoundColumnIndex;

	public Integer getLowerBoundColumnIndex() {
		return lowerBoundColumnIndex;
	}

	public void setLowerBoundColumnIndex(Integer lowerBoundColumnIndex) {
		this.lowerBoundColumnIndex = lowerBoundColumnIndex;
	}
	
	private Integer upperBoundColumnIndex;

	public Integer getUpperBoundColumnIndex() {
		return upperBoundColumnIndex;
	}

	public void setUpperBoundColumnIndex(Integer upperBoundColumnIndex) {
		this.upperBoundColumnIndex = upperBoundColumnIndex;
	}
	
	private Integer biologicalObjectiveColumnIndex;

	public Integer getBiologicalObjectiveColumnIndex() {
		return biologicalObjectiveColumnIndex;
	}

	public void setBiologicalObjectiveColumnIndex(Integer biologicalObjectiveColumnIndex) {
		this.biologicalObjectiveColumnIndex = biologicalObjectiveColumnIndex;
	}
	
	private Integer syntheticObjectiveColumnIndex;

	public Integer getSyntheticObjectiveColumnIndex() {
		return syntheticObjectiveColumnIndex;
	}

	public void setSyntheticObjectiveColumnIndex(Integer syntheticObjectiveColumnIndex) {
		this.syntheticObjectiveColumnIndex = syntheticObjectiveColumnIndex;
	}
	
	private Integer geneAssociationColumnIndex;

	public Integer getGeneAssociationColumnIndex() {
		return geneAssociationColumnIndex;
	}

	public void setGeneAssociationColumnIndex(Integer geneAssociationColumnIndex) {
		this.geneAssociationColumnIndex = geneAssociationColumnIndex;
	}
	
	private Integer proteinAssociationColumnIndex;
	
    public Integer getProteinAssociationColumnIndex() {
		return proteinAssociationColumnIndex;
	}

	public void setProteinAssociationColumnIndex(
			Integer proteinAssociationColumnIndex) {
		this.proteinAssociationColumnIndex = proteinAssociationColumnIndex;
	}

	private Integer subsystemColumnIndex;
	
	public Integer getSubsystemColumnIndex() {
		return subsystemColumnIndex;
	}

	public void setSubsystemColumnIndex(Integer subsystemColumnIndex) {
		this.subsystemColumnIndex = subsystemColumnIndex;
	}

	private Integer proteinClassColumnIndex;
	
	public Integer getProteinClassColumnIndex() {
		return proteinClassColumnIndex;
	}

	public void setProteinClassColumnIndex(Integer proteinClassColumnIndex) {
		this.proteinClassColumnIndex = proteinClassColumnIndex;
	}

	private ArrayList<Integer> reactionsMetaColumnIndexList;
	
	public ArrayList<Integer> getReactionsMetaColumnIndexList() {
		return reactionsMetaColumnIndexList;
	}
	
	public void setReactionsMetaColumnIndexList(ArrayList<Integer> reactionsMetaColumnIndexList) {
		this.reactionsMetaColumnIndexList = reactionsMetaColumnIndexList;
	}
	
    private Integer reactionsNextRowCorrection;
	
	public void setReactionsNextRowCorrection(Integer reactionsNextRowCorrection) {
		this.reactionsNextRowCorrection = reactionsNextRowCorrection;
	}

	public Integer getReactionsNextRowCorrection() {
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
	
	public boolean hasValidGurobiKey;
	
	private Integer reactionsLocationsListCount;
	
	public void setReactionsLocationsListCount(Integer reactionsLocationsListCount) {
		this.reactionsLocationsListCount = reactionsLocationsListCount;
	}
	
	public Integer getReactionsLocationsListCount() {
		return reactionsLocationsListCount;
	}
	
	private Integer metabolitesLocationsListCount;

	public Integer getMetabolitesLocationsListCount() {
		return metabolitesLocationsListCount;
	}

	public void setMetabolitesLocationsListCount(
			Integer metabolitesLocationsListCount) {
		this.metabolitesLocationsListCount = metabolitesLocationsListCount;
	}
	
	private  ArrayList<String> findEntryList = new ArrayList<String>();
	
	public ArrayList<String> getFindEntryList() {
		return findEntryList;
	}

	public void setFindEntryList(ArrayList<String> findEntryList) {
		this.findEntryList = findEntryList;
	}

	private  ArrayList<String> replaceEntryList = new ArrayList<String>();
	
	public ArrayList<String> getReplaceEntryList() {
		return replaceEntryList;
	}

	public void setReplaceEntryList(ArrayList<String> replaceEntryList) {
		this.replaceEntryList = replaceEntryList;
	}
	
	private Map<String, Object> metabDisplayCollectionMap = new HashMap<String, Object>();
	
	public Map<String, Object> getMetabDisplayCollectionMap() {
		return metabDisplayCollectionMap;
	}

	public void setMetabDisplayCollectionMap(
			Map<String, Object> metabDisplayCollectionMap) {
		this.metabDisplayCollectionMap = metabDisplayCollectionMap;
	}

	/********************************************************************************/
	// undo/redo
	/********************************************************************************/
	
	private Map<Object, Object> undoItemMap = new HashMap<Object, Object>();	

	public Map<Object, Object> getUndoItemMap() {
		return undoItemMap;
	}

	public void setUndoItemMap(Map<Object, Object> undoItemMap) {
		this.undoItemMap = undoItemMap;
	}
	
	private Map<Object, Object> redoItemMap = new HashMap<Object, Object>();	

	public Map<Object, Object> getRedoItemMap() {
		return redoItemMap;
	}

	public void setRedoItemMap(Map<Object, Object> redoItemMap) {
		this.redoItemMap = redoItemMap;
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
	
	private Map<String, DefaultTableModel> metabolitesUndoTableModelMap;
	
	public Map<String, DefaultTableModel> getMetabolitesUndoTableModelMap() {
		return metabolitesUndoTableModelMap;
	}

	public void setMetabolitesUndoTableModelMap(
			Map<String, DefaultTableModel> metabolitesUndoTableModelMap) {
		this.metabolitesUndoTableModelMap = metabolitesUndoTableModelMap;
	}

	private Map<String, DefaultTableModel> reactionsUndoTableModelMap;

	public Map<String, DefaultTableModel> getReactionsUndoTableModelMap() {
		return reactionsUndoTableModelMap;
	}

	public void setReactionsUndoTableModelMap(
			Map<String, DefaultTableModel> reactionsUndoTableModelMap) {
		this.reactionsUndoTableModelMap = reactionsUndoTableModelMap;
	}
	
	private ArrayList<Integer> gdbbKnockoutsList;
	
    public ArrayList<Integer> getGdbbKnockoutsList() {
		return gdbbKnockoutsList;
	}

	public void setGdbbKnockoutsList(ArrayList<Integer> gdbbKnockoutsList) {
		this.gdbbKnockoutsList = gdbbKnockoutsList;
	}

	private Map<String, ArrayList<Integer>> gdbbKnockoutsMap;

	public Map<String, ArrayList<Integer>> getGdbbKnockoutsMap() {
		return gdbbKnockoutsMap;
	}

	public void setGdbbKnockoutsMap(Map<String, ArrayList<Integer>> gdbbKnockoutsMap) {
		this.gdbbKnockoutsMap = gdbbKnockoutsMap;
	}

}