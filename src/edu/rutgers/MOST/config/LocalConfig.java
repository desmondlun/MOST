package edu.rutgers.MOST.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LocalConfig {	

	
	//Singleton pattern:
    private static final LocalConfig instance = new LocalConfig();

    // Private constructor prevents instantiation from other classes
    private LocalConfig() { }

    public static LocalConfig getInstance() {
            return instance;
    }
    
    
    private String databaseName;

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	
	//Database currently loaded in table. This is needed when optimizations are loaded
	//update methods change optimized database. 
	private String loadedDatabase;

	public String getLoadedDatabase() {
		return loadedDatabase;
	}

	public void setLoadedDatabase(String loadedDatabase) {
		this.loadedDatabase = loadedDatabase;
	}
	
	public boolean hasMetabolitesFile;
	public boolean hasReactionsFile;
	
	/**********************************************************************************/
	//parameters for metabolites in columnNameInterfaces
	/**********************************************************************************/
    	
	//column indices
    private static Integer metaboliteAbbreviationColumnIndex;
	
	public void setMetaboliteAbbreviationColumnIndex(Integer metaboliteAbbreviationColumnIndex) {
		this.metaboliteAbbreviationColumnIndex = metaboliteAbbreviationColumnIndex;
	}

	public static Integer getMetaboliteAbbreviationColumnIndex() {
		return metaboliteAbbreviationColumnIndex;
	}
	
    private static Integer metaboliteNameColumnIndex;
	
	public void setMetaboliteNameColumnIndex(Integer metaboliteNameColumnIndex) {
		this.metaboliteNameColumnIndex = metaboliteNameColumnIndex;
	}

	public static Integer getMetaboliteNameColumnIndex() {
		return metaboliteNameColumnIndex;
	}
	
    private static Integer chargeColumnIndex;
	
	public void setChargeColumnIndex(Integer chargeColumnIndex) {
		this.chargeColumnIndex = chargeColumnIndex;
	}

	public static Integer getChargeColumnIndex() {
		return chargeColumnIndex;
	}
	
	private static Integer compartmentColumnIndex;
	
	public void setCompartmentColumnIndex(Integer compartmentColumnIndex) {
		this.compartmentColumnIndex = compartmentColumnIndex;
	}

	public static Integer getCompartmentColumnIndex() {
		return compartmentColumnIndex;
	}
	
    private static Integer boundaryColumnIndex;
	
	public void setBoundaryColumnIndex(Integer boundaryColumnIndex) {
		this.boundaryColumnIndex = boundaryColumnIndex;
	}

	public static Integer getBoundaryColumnIndex() {
		return boundaryColumnIndex;
	}
	
	private static ArrayList<Integer> metabolitesMetaColumnIndexList;
	
	public ArrayList<Integer> getMetabolitesMetaColumnIndexList() {
		return metabolitesMetaColumnIndexList;
	}
	
	public void setMetabolitesMetaColumnIndexList(ArrayList<Integer> metabolitesMetaColumnIndexList) {
		this.metabolitesMetaColumnIndexList = metabolitesMetaColumnIndexList;
	}    
	
    private static Integer metabolitesNextRowCorrection;
	
	public void setMetabolitesNextRowCorrection(Integer metabolitesNextRowCorrection) {
		this.metabolitesNextRowCorrection = metabolitesNextRowCorrection;
	}

	public static Integer getMetabolitesNextRowCorrection() {
		return metabolitesNextRowCorrection;
	}
	
	/**********************************************************************************/
	//parameters for reactions in columnNameInterfaces
	/**********************************************************************************/
	//reaction column indices
    private static Integer reactionAbbreviationColumnIndex;
	
	public void setReactionAbbreviationColumnIndex(Integer reactionAbbreviationColumnIndex) {
		this.reactionAbbreviationColumnIndex = reactionAbbreviationColumnIndex;
	}

	public static Integer getReactionAbbreviationColumnIndex() {
		return reactionAbbreviationColumnIndex;
	}
	
    private static Integer reactionNameColumnIndex;
	
	public void setReactionNameColumnIndex(Integer reactionNameColumnIndex) {
		this.reactionNameColumnIndex = reactionNameColumnIndex;
	}

	public static Integer getReactionNameColumnIndex() {
		return reactionNameColumnIndex;
	}
	
	private static Integer reactionEquationColumnIndex;

	public Integer getReactionEquationColumnIndex() {
		return reactionEquationColumnIndex;
	}

	public void setReactionEquationColumnIndex(Integer reactionEquationColumnIndex) {
		this.reactionEquationColumnIndex = reactionEquationColumnIndex;
	}
	
	private static Integer knockoutColumnIndex;

	public Integer getKnockoutColumnIndex() {
		return knockoutColumnIndex;
	}

	public void setKnockoutColumnIndex(Integer knockoutColumnIndex) {
		this.knockoutColumnIndex = knockoutColumnIndex;
	}
	
	private static Integer fluxValueColumnIndex;

	public Integer getFluxValueColumnIndex() {
		return fluxValueColumnIndex;
	}

	public void setFluxValueColumnIndex(Integer fluxValueColumnIndex) {
		this.fluxValueColumnIndex = fluxValueColumnIndex;
	}
	
	private static Integer reversibleColumnIndex;

	public Integer getReversibleColumnIndex() {
		return reversibleColumnIndex;
	}

	public void setReversibleColumnIndex(Integer reversibleColumnIndex) {
		this.reversibleColumnIndex = reversibleColumnIndex;
	}
	
	private static Integer lowerBoundColumnIndex;

	public Integer getLowerBoundColumnIndex() {
		return lowerBoundColumnIndex;
	}

	public void setLowerBoundColumnIndex(Integer lowerBoundColumnIndex) {
		this.lowerBoundColumnIndex = lowerBoundColumnIndex;
	}
	
	private static Integer upperBoundColumnIndex;

	public Integer getUpperBoundColumnIndex() {
		return upperBoundColumnIndex;
	}

	public void setUpperBoundColumnIndex(Integer upperBoundColumnIndex) {
		this.upperBoundColumnIndex = upperBoundColumnIndex;
	}
	
	private static Integer biologicalObjectiveColumnIndex;

	public Integer getBiologicalObjectiveColumnIndex() {
		return biologicalObjectiveColumnIndex;
	}

	public void setBiologicalObjectiveColumnIndex(Integer biologicalObjectiveColumnIndex) {
		this.biologicalObjectiveColumnIndex = biologicalObjectiveColumnIndex;
	}
	
    private static ArrayList<Integer> reactionsMetaColumnIndexList;
	
	public ArrayList<Integer> getReactionsMetaColumnIndexList() {
		return reactionsMetaColumnIndexList;
	}
	
	public void setReactionsMetaColumnIndexList(ArrayList<Integer> reactionsMetaColumnIndexList) {
		this.reactionsMetaColumnIndexList = reactionsMetaColumnIndexList;
	}
	
    private static Integer reactionsNextRowCorrection;
	
	public void setReactionsNextRowCorrection(Integer reactionsNextRowCorrection) {
		this.reactionsNextRowCorrection = reactionsNextRowCorrection;
	}

	public static Integer getReactionsNextRowCorrection() {
		return reactionsNextRowCorrection;
	}
	
	/**********************************************************************************/
	//end parameters for columnNameInterfaces
	/**********************************************************************************/
	
	private static Integer progress;
	
	public void setProgress(Integer progress) {
		this.progress = progress;
	}
	
	public Integer getProgress() {
		return progress;
	}
	
	private static ArrayList<String> invalidReactions = new ArrayList();
	
	public ArrayList<String> getInvalidReactions() {
		return invalidReactions;
	}
	
	public void setInvalidReactions(ArrayList<String> invalidReactions) {
		this.invalidReactions = invalidReactions;
	}
	
    private static Integer numberCopiedRows;
	
	public void setNumberCopiedColumns(Integer numberCopiedColumns) {
		this.numberCopiedColumns = numberCopiedColumns;
	}
	
	public Integer getNumberCopiedColumns() {
		return numberCopiedColumns;
	}
	
	private static Integer numberCopiedColumns;
	
	public void setNumberCopiedRows(Integer numberCopiedRows) {
		this.numberCopiedRows = numberCopiedRows;
	}
	
	public Integer getNumberCopiedRows() {
		return numberCopiedRows;
	}
	
    private static Integer headerColumnIndex;
	
	public void setHeaderColumnIndex(Integer headerColumnIndex) {
		this.headerColumnIndex = headerColumnIndex;
	}
	
	public Integer getHeaderColumnIndex() {
		return headerColumnIndex;
	}
	
	//map used to hold metabolite name/id pairs, in order to construct reaction_reactant
	//and reaction_product (lookup) tables
    public static Map<String, Object> metaboliteIdNameMap = new HashMap<String, Object>();
	
	public static Map<String, Object> getMetaboliteIdNameMap() {
		return metaboliteIdNameMap;
	}

	public void setMetaboliteIdNameMap(Map<String, Object> metaboliteIdNameMap) {
		this.metaboliteIdNameMap = metaboliteIdNameMap;
	}
	
    private static ArrayList<Integer> blankMetabIds = new ArrayList<Integer>();
	
	public ArrayList<Integer> getBlankMetabIds() {
		return blankMetabIds;
	}
	
	public void setBlankMetabIds(ArrayList<Integer> blankMetabIds) {
		this.blankMetabIds = blankMetabIds;
	}
	
	private static ArrayList<Integer> duplicateIds = new ArrayList<Integer>();
	
	public ArrayList<Integer> getDuplicateIds() {
		return duplicateIds;
	}
	
	public void setDuplicateIds(ArrayList<Integer> duplicateIds) {
		this.duplicateIds = duplicateIds;
	}
	
	//used for determining id when adding a metabolite when a reaction is
	//read and metabolite is not present
	private static Integer maxMetaboliteId;
	
	public void setMaxMetaboliteId(Integer maxMetaboliteId) {
		this.maxMetaboliteId = maxMetaboliteId;
	}
	
	public Integer getMaxMetaboliteId() {
		return maxMetaboliteId;
	}

	//Map used to hold number of reactions a metabolite is used in. if a metabolites
	//is not present in map, it is unused. Also used when adding, deleting or changing
	//reactions to determine whether the used status of a metabolite must be changed.
	private static Map<String, Object> metaboliteUsedMap = new HashMap<String, Object>();
	
	public static Map<String, Object> getMetaboliteUsedMap() {
		return metaboliteUsedMap;
	}

	public void setMetaboliteUsedMap(Map<String, Object> metaboliteUsedMap) {
		this.metaboliteUsedMap = metaboliteUsedMap;
	}
	
	private static File metabolitesCSVFile;

	public void setMetabolitesCSVFile(File metabolitesCSVFile) {
		this.metabolitesCSVFile = metabolitesCSVFile;
	}

	public static File getMetabolitesCSVFile() {
		return metabolitesCSVFile;
	}

	private static File reactionsCSVFile;

	public void setReactionsCSVFile(File reactionsCSVFile) {
		this.reactionsCSVFile = reactionsCSVFile;
	}

	public static File getReactionsCSVFile() {
		return reactionsCSVFile;
	}
	
	private static ArrayList<Integer> participatingReactions;

	public void setParticipatingReactions(ArrayList<Integer> participatingReactions) {
		this.participatingReactions = participatingReactions;
	}

	public ArrayList<Integer> getParticipatingReactions() {
		return participatingReactions;
	}  
	
	// list used when exiting program. If items remain in list at exit, user prompted
	// to save these files
	private static ArrayList<String> optimizationFilesList;

	public void setOptimizationFilesList(ArrayList<String> optimizationFilesList) {
		this.optimizationFilesList = optimizationFilesList;
	}

	public ArrayList<String> getOptimizationFilesList() {
		return optimizationFilesList;
	}  
	
	// if "No" button pressed in Add Metabolite Prompt, is set to false
	public boolean addMetaboliteOption;	
	public boolean noButtonClicked;
	public boolean yesToAllButtonClicked;
	public boolean pastedReaction;
	public boolean includesReactions;
	
	public boolean editMode;
	
	public boolean reactionsTableChanged;
	public boolean metabolitesTableChanged;
	
	public boolean findMode;
	public boolean findFieldChanged;
	public boolean replaceFieldChanged;
	public boolean findReplaceFocusLost;
	public boolean findReplaceFocusGained;
	
	public boolean addReactantPromptShown;
	
	public static Integer reactionsLocationsListCount;
	

	public void setReactionsLocationsListCount(Integer reactionsLocationsListCount) {
		this.reactionsLocationsListCount = reactionsLocationsListCount;
	}
	
	public Integer getReactionsLocationsListCount() {
		return reactionsLocationsListCount;
	}
	
	public static Integer metabolitesLocationsListCount;

	public static Integer getMetabolitesLocationsListCount() {
		return metabolitesLocationsListCount;
	}

	public static void setMetabolitesLocationsListCount(
			Integer metabolitesLocationsListCount) {
		LocalConfig.metabolitesLocationsListCount = metabolitesLocationsListCount;
	}
	
	private static ArrayList<Integer> suspiciousMetabolites = new ArrayList<Integer>();
	
	public ArrayList<Integer> getSuspiciousMetabolites() {
		return suspiciousMetabolites;
	}
	
	public void setSuspiciousMetabolites(ArrayList<Integer> suspiciousMetabolites) {
		this.suspiciousMetabolites = suspiciousMetabolites;
	}
	
}