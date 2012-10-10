package edu.rutgers.MOST.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.rutgers.MOST.data.SBMLModelReader;

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
	
	/**********************************************************************************/
	//parameters set for metabolites in columnNameInterfaces
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
	//parameters set for reactions in columnNameInterfaces
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
	
	//sheet names list for ExcelSheetInterface and Excel97Reader
    private static ArrayList<String> sheetNamesList;
	
	public ArrayList<String> getSheetNamesList() {
		return sheetNamesList;
	}
	
	public void setSheetNamesList(ArrayList<String> sheetNamesList) {
		this.sheetNamesList = sheetNamesList;
	}
	
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
	
	private static Integer maxMetaboliteId;
	
	public void setMaxMetaboliteId(Integer maxMetaboliteId) {
		this.maxMetaboliteId = maxMetaboliteId;
	}
	
	public Integer getMaxMetaboliteId() {
		return maxMetaboliteId;
	}
	
}