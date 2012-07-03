package edu.rutgers.MOST.config;

import java.util.ArrayList;

import edu.rutgers.MOST.data.SBMLProduct;
import edu.rutgers.MOST.data.SBMLReactant;

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
    
    //parameters set from metabolites in columnNameInterface
	//column names
	private String metaboliteAbbreviationColumnName;

	public String getMetaboliteAbbreviationColumnName() {
		return metaboliteAbbreviationColumnName;
	}

	public void setMetaboliteAbbreviationColumnName(String metaboliteAbbreviationColumnName) {
		this.metaboliteAbbreviationColumnName = metaboliteAbbreviationColumnName;
	}
	
	private String metaboliteNameColumnName;

	public String getMetaboliteNameColumnName() {
		return metaboliteNameColumnName;
	}

	public void setMetaboliteNameColumnName(String metaboliteNameColumnName) {
		this.metaboliteNameColumnName = metaboliteNameColumnName;
	}
	
	private String chargeColumnName;

	public String getChargeColumnName() {
		return chargeColumnName;
	}

	public void setChargeColumnName(String chargeColumnName) {
		this.chargeColumnName = chargeColumnName;
	}
	
	private String compartmentColumnName;

	public String getCompartmentColumnName() {
		return compartmentColumnName;
	}

	public void setCompartmentColumnName(String compartmentColumnName) {
		this.compartmentColumnName = compartmentColumnName;
	}
	
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
    
	//parameters set from reactions in columnNameInterface
	//column names
	private String reactionAbbreviationColumnName;

	public String getReactionAbbreviationColumnName() {
		return reactionAbbreviationColumnName;
	}

	public void setReactionAbbreviationColumnName(String reactionAbbreviationColumnName) {
		this.reactionAbbreviationColumnName = reactionAbbreviationColumnName;
	}
	
	private String reactionNameColumnName;

	public String getReactionNameColumnName() {
		return reactionNameColumnName;
	}

	public void setReactionNameColumnName(String reactionNameColumnName) {
		this.reactionNameColumnName = reactionNameColumnName;
	}
	
	private String reactionEquationColumnName;

	public String getReactionEquationColumnName() {
		return reactionEquationColumnName;
	}

	public void setReactionEquationColumnName(String reactionEquationColumnName) {
		this.reactionEquationColumnName = reactionEquationColumnName;
	}
	
	private String knockoutColumnName;

	public String getKnockoutColumnName() {
		return knockoutColumnName;
	}

	public void setKnockoutColumnName(String knockoutColumnName) {
		this.knockoutColumnName = knockoutColumnName;
	}
	
	private String fluxValueColumnName;

	public String getFluxValueColumnName() {
		return fluxValueColumnName;
	}

	public void setFluxValueColumnName(String fluxValueColumnName) {
		this.fluxValueColumnName = fluxValueColumnName;
	}
	
	private String reversibleColumnName;

	public String getReversibleColumnName() {
		return reversibleColumnName;
	}

	public void setReversibleColumnName(String reversibleColumnName) {
		this.reversibleColumnName = reversibleColumnName;
	}
	
	private String lowerBoundColumnName;

	public String getLowerBoundColumnName() {
		return lowerBoundColumnName;
	}

	public void setLowerBoundColumnName(String lowerBoundColumnName) {
		this.lowerBoundColumnName = lowerBoundColumnName;
	}
	
	private String upperBoundColumnName;

	public String getUpperBoundColumnName() {
		return upperBoundColumnName;
	}

	public void setUpperBoundColumnName(String upperBoundColumnName) {
		this.upperBoundColumnName = upperBoundColumnName;
	}
	
	private String biologicalObjectiveColumnName;

	public String getBiologicalObjectiveColumnName() {
		return biologicalObjectiveColumnName;
	}

	public void setBiologicalObjectiveColumnName(String biologicalObjectiveColumnName) {
		this.biologicalObjectiveColumnName = biologicalObjectiveColumnName;
	}
	
	//column indices
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
	
	//sheet names list for ExcelSheetInterface and Excel97Reader
    private static ArrayList<String> sheetNamesList;
	
	public ArrayList<String> getSheetNamesList() {
		return sheetNamesList;
	}
	
	public void setSheetNamesList(ArrayList<String> sheetNamesList) {
		this.sheetNamesList = sheetNamesList;
	}
}