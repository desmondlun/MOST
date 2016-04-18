package edu.rutgers.MOST.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;

import edu.rutgers.MOST.data.MetabolicPathway;
import edu.rutgers.MOST.data.ModelReactionEquation;
import edu.rutgers.MOST.data.PathwayMetaboliteData;
import edu.rutgers.MOST.data.PathwayNameData;
import edu.rutgers.MOST.data.PathwayReactionData;
import edu.rutgers.MOST.data.SBMLCompartment;
import edu.rutgers.MOST.data.SBMLMetabolite;
import edu.rutgers.MOST.data.SBMLReaction;
import edu.rutgers.MOST.data.TransportReactionsByCompartments;
import edu.rutgers.MOST.data.VisualizationData;

public class LocalConfig {	

	
	//Singleton pattern:
	private static final LocalConfig instance = new LocalConfig();

	// Private constructor prevents instantiation from other classes
	private LocalConfig() { }

	public static synchronized LocalConfig getInstance() {
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

	public synchronized void setProgress(Integer progress) {
		this.progress = progress;
	}

	public synchronized Integer getProgress() {
		return progress;
	}

	private Integer visualizationsProgress;

	public Integer getVisualizationsProgress() {
		return visualizationsProgress;
	}

	public void setVisualizationsProgress(Integer visualizationsProgress) {
		this.visualizationsProgress = visualizationsProgress;
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

	public Map<String, Object> reactionAbbreviationIdMap = new HashMap<String, Object>();

	public Map<String, Object> getReactionAbbreviationIdMap() {
		return reactionAbbreviationIdMap;
	}

	public void setReactionAbbreviationIdMap(
		Map<String, Object> reactionAbbreviationIdMap) {
		this.reactionAbbreviationIdMap = reactionAbbreviationIdMap;
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

	private Map<Object, String> metaboliteIdCompartmentMap;

	public Map<Object, String> getMetaboliteIdCompartmentMap() {
		return metaboliteIdCompartmentMap;
	}

	public void setMetaboliteIdCompartmentMap(
		Map<Object, String> metaboliteIdCompartmentMap) {
		this.metaboliteIdCompartmentMap = metaboliteIdCompartmentMap;
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

	private Integer maxCompartmentId;

	public Integer getMaxCompartmentId() {
		return maxCompartmentId;
	}

	public void setMaxCompartmentId(Integer maxCompartmentId) {
		this.maxCompartmentId = maxCompartmentId;
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

	// ids of reactions where reversible = false and lower bound < 0
	private ArrayList<Integer> invalidLowerBoundReversibleCombinations = new ArrayList<Integer>();

	public ArrayList< Integer > getInvalidLowerBoundReversibleCombinations() {
		return invalidLowerBoundReversibleCombinations;
	}

	public void setInvalidLowerBoundReversibleCombinations(
		ArrayList<Integer> invalidLowerBoundReversibleCombinations) {
		this.invalidLowerBoundReversibleCombinations = invalidLowerBoundReversibleCombinations;
	}

	// ids of reactions where value in Reversible column does not match arrow in equation
	private ArrayList<Integer> invalidEquationReversibleCombinations = new ArrayList<Integer>();

	public ArrayList<Integer> getInvalidEquationReversibleCombinations()
	{
		return invalidEquationReversibleCombinations;
	}

	public void setInvalidEquationReversibleCombinations(
		ArrayList<Integer> invalidEquationReversibleCombinations) {
		this.invalidEquationReversibleCombinations = invalidEquationReversibleCombinations;
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

	public boolean fvaDone;
	public boolean fvaColumnsVisible;

	public boolean noBiolObjWarningShown;
	public boolean noSynObjWarningShown;

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

	private ArrayList<String> showFVAColumnsList;

	public ArrayList<String> getShowFVAColumnsList() {
		return showFVAColumnsList;
	}

	public void setShowFVAColumnsList(ArrayList<String> showFVAColumnsList) {
		this.showFVAColumnsList = showFVAColumnsList;
	}

	// sbml file read - list from beginning of sbml file
	private ArrayList<SBMLCompartment> listOfCompartments;

	public ArrayList<SBMLCompartment> getListOfCompartments() {
		return listOfCompartments;
	}

	public void setListOfCompartments(ArrayList<SBMLCompartment> listOfCompartments) {
		this.listOfCompartments = listOfCompartments;
	}

	private ArrayList<ArrayList<String>> listOfCompartmentLists;

	public ArrayList<ArrayList<String>> getListOfCompartmentLists() {
		return listOfCompartmentLists;
	}

	public void setListOfCompartmentLists(
		ArrayList<ArrayList<String>> listOfCompartmentLists) {
		this.listOfCompartmentLists = listOfCompartmentLists;
	}
	
	// compartment names from model
	private String selectedCompartmentName;

	public String getSelectedCompartmentName() {
		return selectedCompartmentName;
	}

	public void setSelectedCompartmentName(String selectedCompartmentName) {
		this.selectedCompartmentName = selectedCompartmentName;
	}
	
	private ArrayList<TransportReactionsByCompartments> transportReactionsByCompartmentsList;

	public ArrayList<TransportReactionsByCompartments> getTransportReactionsByCompartmentsList() {
		return transportReactionsByCompartmentsList;
	}

	public void setTransportReactionsByCompartmentsList(
			ArrayList<TransportReactionsByCompartments> transportReactionsByCompartmentsList) {
		this.transportReactionsByCompartmentsList = transportReactionsByCompartmentsList;
	}

	private String keggReactionIdColumnName;

	public String getKeggReactionIdColumnName() {
		return keggReactionIdColumnName;
	}

	public void setKeggReactionIdColumnName(String keggReactionIdColumnName) {
		this.keggReactionIdColumnName = keggReactionIdColumnName;
	}

	// identifier column indices
	private int keggMetaboliteIdColumn;

	public int getKeggMetaboliteIdColumn() {
		return keggMetaboliteIdColumn;
	}

	public void setKeggMetaboliteIdColumn(int keggMetaboliteIdColumn) {
		this.keggMetaboliteIdColumn = keggMetaboliteIdColumn;
	}

	private int chebiIdColumn;

	public int getChebiIdColumn() {
		return chebiIdColumn;
	}

	public void setChebiIdColumn(int chebiIdColumn) {
		this.chebiIdColumn = chebiIdColumn;
	}

	private int ecNumberColumn;

	public int getEcNumberColumn() {
		return ecNumberColumn;
	}

	public void setEcNumberColumn(int ecNumberColumn) {
		this.ecNumberColumn = ecNumberColumn;
	}

	private int keggReactionIdColumn;

	public int getKeggReactionIdColumn() {
		return keggReactionIdColumn;
	}

	public void setKeggReactionIdColumn(int keggReactionIdColumn) {
		this.keggReactionIdColumn = keggReactionIdColumn;
	}

	// Visualization options
	private boolean graphMissingMetabolitesSelected;
	private boolean scaleEdgeThicknessSelected;
	private boolean ignoreProtonSelected;
	private boolean ignoreWaterSelected;
	private boolean showVisualizationReportSelected;

	public boolean isGraphMissingMetabolitesSelected() {
		return graphMissingMetabolitesSelected;
	}

	public void setGraphMissingMetabolitesSelected(
		boolean graphMissingMetabolitesSelected) {
		this.graphMissingMetabolitesSelected = graphMissingMetabolitesSelected;
	}

	public boolean isScaleEdgeThicknessSelected() {
		return scaleEdgeThicknessSelected;
	}

	public void setScaleEdgeThicknessSelected(boolean scaleEdgeThicknessSelected) {
		this.scaleEdgeThicknessSelected = scaleEdgeThicknessSelected;
	}

	public boolean isIgnoreProtonSelected() {
		return ignoreProtonSelected;
	}

	public void setIgnoreProtonSelected(boolean ignoreProtonSelected) {
		this.ignoreProtonSelected = ignoreProtonSelected;
	}

	public boolean isIgnoreWaterSelected() {
		return ignoreWaterSelected;
	}

	public boolean isShowVisualizationReportSelected() {
		return showVisualizationReportSelected;
	}

	public void setShowVisualizationReportSelected(
		boolean showVisualizationReportSelected) {
		this.showVisualizationReportSelected = showVisualizationReportSelected;
	}

	public void setIgnoreWaterSelected(boolean ignoreWaterSelected) {
		this.ignoreWaterSelected = ignoreWaterSelected;
	}

	// used to add kegg ids to metabolites table from supplementary data
	private Map<String, String> metaboliteAbbrKeggIdMap;

	public Map<String, String> getMetaboliteAbbrKeggIdMap() {
		return metaboliteAbbrKeggIdMap;
	}

	public void setMetaboliteAbbrKeggIdMap(
		Map<String, String> metaboliteAbbrKeggIdMap) {
		this.metaboliteAbbrKeggIdMap = metaboliteAbbrKeggIdMap;
	}
	
	// used to add ec numbers from supplementary data 
	private Map<String, String> reactionAbbrECNumberMap;

	public Map<String, String> getReactionAbbrECNumberMap() {
		return reactionAbbrECNumberMap;
	}

	public void setReactionAbbrECNumberMap(
		Map<String, String> reactionAbbrECNumberMap) {
		this.reactionAbbrECNumberMap = reactionAbbrECNumberMap;
	}

	// visualization flux values for edge thicknesses
	private ArrayList<Double> fluxes = new ArrayList<Double>();

	public ArrayList<Double> getFluxes() {
		return fluxes;
	}

	public void setFluxes(ArrayList<Double> fluxes) {
		this.fluxes = fluxes;
	}

	private double maxFlux;

	public double getMaxFlux() {
		return maxFlux;
	}

	public void setMaxFlux(double maxFlux) {
		this.maxFlux = maxFlux;
	}

	private double secondaryMaxFlux;

	public double getSecondaryMaxFlux() {
		return secondaryMaxFlux;
	}

	public void setSecondaryMaxFlux(double secondaryMaxFlux) {
		this.secondaryMaxFlux = secondaryMaxFlux;
	}

	private boolean isFluxLevelsSet;

	public boolean isFluxLevelsSet() {
		return isFluxLevelsSet;
	}

	public void setFluxLevelsSet(boolean isFluxLevelsSet) {
		this.isFluxLevelsSet = isFluxLevelsSet;
	}

	// visualizations find
	public boolean visualizationFindFieldChanged;
	public boolean visualizationFindFocusLost;
	public boolean visualizationFindFocusGained;

	// data from etc/visualizations files
	private Map<String, ArrayList<String>> alternateMetabolitesMap;

	public Map<String, ArrayList<String>> getAlternateMetabolitesMap() {
		return alternateMetabolitesMap;
	}

	public void setAlternateMetabolitesMap(
		Map<String, ArrayList<String>> alternateMetabolitesMap) {
		this.alternateMetabolitesMap = alternateMetabolitesMap;
	}

	private Map<String, ArrayList<String>> metaboliteSubstitutionsMap;

	public Map<String, ArrayList<String>> getMetaboliteSubstitutionsMap() {
		return metaboliteSubstitutionsMap;
	}

	public void setMetaboliteSubstitutionsMap(
		Map<String, ArrayList<String>> metaboliteSubstitutionsMap) {
		this.metaboliteSubstitutionsMap = metaboliteSubstitutionsMap;
	}

	private Map<String, ArrayList<String>> ecNumberKeggReactionIdMap;

	public Map<String, ArrayList<String>> getEcNumberKeggReactionIdMap() {
		return ecNumberKeggReactionIdMap;
	}

	public void setEcNumberKeggReactionIdMap(
		Map<String, ArrayList<String>> ecNumberKeggReactionIdMap) {
		this.ecNumberKeggReactionIdMap = ecNumberKeggReactionIdMap;
	}

	private Map<String, ArrayList<String>> keggReactionIdECNumberMap;

	public Map<String, ArrayList<String>> getKeggReactionIdECNumberMap() {
		return keggReactionIdECNumberMap;
	}

	public void setKeggReactionIdECNumberMap(
		Map<String, ArrayList<String>> keggReactionIdECNumberMap) {
		this.keggReactionIdECNumberMap = keggReactionIdECNumberMap;
	}

	private Map<String, MetabolicPathway> metabolicPathways;

	public Map<String, MetabolicPathway> getMetabolicPathways() {
		return metabolicPathways;
	}

	public void setMetabolicPathways(Map<String, MetabolicPathway> metabolicPathways) {
		this.metabolicPathways = metabolicPathways;
	}

	private Map<String, PathwayNameData> pathwayNameMap;

	public Map<String, PathwayNameData> getPathwayNameMap() {
		return pathwayNameMap;
	}

	public void setPathwayNameMap(Map<String, PathwayNameData> pathwayNameMap) {
		this.pathwayNameMap = pathwayNameMap;
	}

	private Map<String, PathwayMetaboliteData> metaboliteDataKeggIdMap;

	public Map<String, PathwayMetaboliteData> getMetaboliteDataKeggIdMap() {
		return metaboliteDataKeggIdMap;
	}

	public void setMetaboliteDataKeggIdMap(
		Map<String, PathwayMetaboliteData> metaboliteDataKeggIdMap) {
		this.metaboliteDataKeggIdMap = metaboliteDataKeggIdMap;
	}

	private Map<String, String> metaboliteNameAbbrMap;

	public Map<String, String> getMetaboliteNameAbbrMap() {
		return metaboliteNameAbbrMap;
	}

	public void setMetaboliteNameAbbrMap(Map<String, String> metaboliteNameAbbrMap) {
		this.metaboliteNameAbbrMap = metaboliteNameAbbrMap;
	}

	Map<String, PathwayMetaboliteData> metaboliteNameDataMap;

	public Map<String, PathwayMetaboliteData> getMetaboliteNameDataMap() {
		return metaboliteNameDataMap;
	}

	public void setMetaboliteNameDataMap(
		Map<String, PathwayMetaboliteData> metaboliteNameDataMap) {
		this.metaboliteNameDataMap = metaboliteNameDataMap;
	}

	private Map<String, PathwayReactionData> reactionDataKeggIdMap;

	public Map<String, PathwayReactionData> getReactionDataKeggIdMap() {
		return reactionDataKeggIdMap;
	}

	public void setReactionDataKeggIdMap(
		Map<String, PathwayReactionData> reactionDataKeggIdMap) {
		this.reactionDataKeggIdMap = reactionDataKeggIdMap;
	}

	private ArrayList<String> sideSpeciesList;

	public ArrayList<String> getSideSpeciesList() {
		return sideSpeciesList;
	}

	public void setSideSpeciesList(ArrayList<String> sideSpeciesList) {
		this.sideSpeciesList = sideSpeciesList;
	}

	// rxns with EC number or KEGG reaction id
	private ArrayList<Integer> identifierIds;

	public ArrayList<Integer> getIdentifierIds() {
		return identifierIds;
	}

	public void setIdentifierIds(ArrayList<Integer> identifierIds) {
		this.identifierIds = identifierIds;
	}

	// rxns with no EC number or KEGG reaction id
	private ArrayList<Integer> noIdentifierIds;

	public ArrayList<Integer> getNoIdentifierIds() {
		return noIdentifierIds;
	}

	public void setNoIdentifierIds(ArrayList<Integer> noIdentifierIds) {
		this.noIdentifierIds = noIdentifierIds;
	}

	// created each time visualize run from model
	private Map<String, ArrayList<String>> keggIdCompartmentMap;

	public Map<String, ArrayList<String>> getKeggIdCompartmentMap() {
		return keggIdCompartmentMap;
	}

	public void setKeggIdCompartmentMap(
		Map<String, ArrayList<String>> keggIdCompartmentMap) {
		this.keggIdCompartmentMap = keggIdCompartmentMap;
	}

	private Map<String, ArrayList<SBMLMetabolite>> keggIdMetaboliteMap;

	public Map<String, ArrayList<SBMLMetabolite>> getKeggIdMetaboliteMap() {
		return keggIdMetaboliteMap;
	}

	public void setKeggIdMetaboliteMap(
		Map<String, ArrayList<SBMLMetabolite>> keggIdMetaboliteMap) {
		this.keggIdMetaboliteMap = keggIdMetaboliteMap;
	}

	private Map<String, String> metaboliteIdKeggIdMap;

	public Map<String, String> getMetaboliteIdKeggIdMap() {
		return metaboliteIdKeggIdMap;
	}

	public void setMetaboliteIdKeggIdMap(Map<String, String> metaboliteIdKeggIdMap) {
		this.metaboliteIdKeggIdMap = metaboliteIdKeggIdMap;
	}
	
	private Map<String, ArrayList<SBMLReaction>> ecNumberReactionMap;

	public Map<String, ArrayList<SBMLReaction>> getEcNumberReactionMap() {
		return ecNumberReactionMap;
	}

	public void setEcNumberReactionMap(
			Map<String, ArrayList<SBMLReaction>> ecNumberReactionMap) {
		this.ecNumberReactionMap = ecNumberReactionMap;
	}

	private Map<String, ArrayList<SBMLReaction>> keggIdReactionMap;

	public Map<String, ArrayList<SBMLReaction>> getKeggIdReactionMap() {
		return keggIdReactionMap;
	}

	public void setKeggIdReactionMap(
			Map<String, ArrayList<SBMLReaction>> keggIdReactionMap) {
		this.keggIdReactionMap = keggIdReactionMap;
	}

	private Map<String, String> chebiIdKeggIdMap;

	public Map<String, String> getChebiIdKeggIdMap() {
		return chebiIdKeggIdMap;
	}

	public void setChebiIdKeggIdMap(Map<String, String> chebiIdKeggIdMap) {
		this.chebiIdKeggIdMap = chebiIdKeggIdMap;
	}

	private Map<String, PathwayReactionData> modelKeggEquationMap;

	public Map<String, PathwayReactionData> getModelKeggEquationMap() {
		return modelKeggEquationMap;
	}

	public void setModelKeggEquationMap(
		Map<String, PathwayReactionData> modelKeggEquationMap) {
		this.modelKeggEquationMap = modelKeggEquationMap;
	}
	
	private ArrayList<String> keggIdsInGraph;

	public ArrayList<String> getKeggIdsInGraph() {
		return keggIdsInGraph;
	}

	public void setKeggIdsInGraph(ArrayList<String> keggIdsInGraph) {
		this.keggIdsInGraph = keggIdsInGraph;
	}

	private ArrayList<Integer> reactionsMissingKeggId;

	public ArrayList<Integer> getReactionsMissingKeggId() {
		return reactionsMissingKeggId;
	}

	public void setReactionsMissingKeggId(ArrayList<Integer> reactionsMissingKeggId) {
		this.reactionsMissingKeggId = reactionsMissingKeggId;
	}

	private ArrayList<Integer> reactionsContainingKeggIdsNotInGraph;

	public ArrayList<Integer> getReactionsContainingKeggIdsNotInGraph() {
		return reactionsContainingKeggIdsNotInGraph;
	}

	public void setReactionsContainingKeggIdsNotInGraph(
		ArrayList<Integer> reactionsContainingKeggIdsNotInGraph) {
		this.reactionsContainingKeggIdsNotInGraph = reactionsContainingKeggIdsNotInGraph;
	}
	
	private ArrayList<Integer> externalReactionIds;

	public ArrayList<Integer> getExternalReactionIds() {
		return externalReactionIds;
	}

	public void setExternalReactionIds(ArrayList<Integer> externalReactionIds) {
		this.externalReactionIds = externalReactionIds;
	}

	private ArrayList<Integer> unplottedReactionIds;

	public ArrayList<Integer> getUnplottedReactionIds() {
		return unplottedReactionIds;
	}

	public void setUnplottedReactionIds(ArrayList<Integer> unplottedReactionIds) {
		this.unplottedReactionIds = unplottedReactionIds;
	}
	
	private ArrayList<Integer> noTransportReactionIds;
	
	public ArrayList<Integer> getNoTransportReactionIds() {
		return noTransportReactionIds;
	}

	public void setNoTransportReactionIds(ArrayList<Integer> noTransportReactionIds) {
		this.noTransportReactionIds = noTransportReactionIds;
	}
	
	// transport reactions
	private Map<String, String> sideSpeciesTransportMetaboliteKeggIdMap;

	public Map<String, String> getSideSpeciesTransportMetaboliteKeggIdMap() {
		return sideSpeciesTransportMetaboliteKeggIdMap;
	}

	public void setSideSpeciesTransportMetaboliteKeggIdMap(
		Map<String, String> sideSpeciesTransportMetaboliteKeggIdMap) {
		this.sideSpeciesTransportMetaboliteKeggIdMap = sideSpeciesTransportMetaboliteKeggIdMap;
	}

	private ArrayList<String> transportMetaboliteIds;

	public ArrayList<String> getTransportMetaboliteIds() {
		return transportMetaboliteIds;
	}

	public void setTransportMetaboliteIds(ArrayList<String> transportMetaboliteIds) {
		this.transportMetaboliteIds = transportMetaboliteIds;
	}

	private VisualizationData visualizationData;

	public VisualizationData getVisualizationData() {
		return visualizationData;
	}

	public void setVisualizationData(VisualizationData visualizationData) {
		this.visualizationData = visualizationData;
	}

}