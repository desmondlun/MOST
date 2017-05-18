package edu.rutgers.MOST.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;

/**
 * Data from pathway_reactions.csv file. Used to construct PathwayReactionNodes.
 */
public class PathwayReactionData {
	// data from reactions.csv
	private String pathwayId;
	private String reactionId;
	private String keggReactionId;
	private ArrayList<String> reactantIds;
	private ArrayList<String> productIds;
	private ArrayList<String> keggReactantIds;
	private ArrayList<String> keggProductIds;
	private ArrayList<String> keggIds;
	private Map<String, PathwayMetaboliteData> keggReactantIdsDataMap;
	private Map<String, PathwayMetaboliteData> keggProductIdsDataMap;
	private String reversible;
	private ArrayList<String> ecNumbers;
	private double level;
	private double levelPosition;
	// equation made up of metabolite node names
	private String equation;
	
	// generic name used if reaction not found in model = name + ec numbers
	private String name;
	private ArrayList<String> names;
	private String displayName;
	private int occurences;
	
	private String direction;
	private boolean directionsMatch = true;
	
	// data from pathway_reaction_positions, currently using same class for storing
	// data from reactions.csv and pathway_reaction_positions. may consider using
	// two different classes in future
	private ArrayList<String> keggReactionIds;
	
	public String getPathwayId() {
		return pathwayId;
	}

	public void setPathwayId(String pathwayId) {
		this.pathwayId = pathwayId;
	}

	public String getReactionId() {
		return reactionId;
	}

	public void setReactionId(String reactionId) {
		this.reactionId = reactionId;
	}

	public String getKeggReactionId() {
		return keggReactionId;
	}

	public void setKeggReactionId(String keggReactionId) {
		this.keggReactionId = keggReactionId;
	}

	public ArrayList<String> getKeggReactionIds() {
		return keggReactionIds;
	}

	public void setKeggReactionIds(ArrayList<String> keggReactionIds) {
		this.keggReactionIds = keggReactionIds;
	}

	public ArrayList<String> getReactantIds() {
		return reactantIds;
	}

	public void setReactantIds(ArrayList<String> reactantIds) {
		this.reactantIds = reactantIds;
	}

	public ArrayList<String> getProductIds() {
		return productIds;
	}

	public void setProductIds(ArrayList<String> productIds) {
		this.productIds = productIds;
	}

	public ArrayList<String> getKeggReactantIds() {
		return keggReactantIds;
	}

	public void setKeggReactantIds(ArrayList<String> keggReactantIds) {
		this.keggReactantIds = keggReactantIds;
	}

	public ArrayList<String> getKeggProductIds() {
		return keggProductIds;
	}

	public void setKeggProductIds(ArrayList<String> keggProductIds) {
		this.keggProductIds = keggProductIds;
	}

	public ArrayList<String> getKeggIds() {
		return keggIds;
	}

	public void setKeggIds(ArrayList<String> keggIds) {
		this.keggIds = keggIds;
	}

	public Map<String, PathwayMetaboliteData> getKeggReactantIdsDataMap() {
		return keggReactantIdsDataMap;
	}

	public void setKeggReactantIdsDataMap(
			Map<String, PathwayMetaboliteData> keggReactantIdsDataMap) {
		this.keggReactantIdsDataMap = keggReactantIdsDataMap;
	}

	public Map<String, PathwayMetaboliteData> getKeggProductIdsDataMap() {
		return keggProductIdsDataMap;
	}

	public void setKeggProductIdsDataMap(
			Map<String, PathwayMetaboliteData> keggProductIdsDataMap) {
		this.keggProductIdsDataMap = keggProductIdsDataMap;
	}

	public String getReversible() {
		return reversible;
	}

	public void setReversible(String reversible) {
		this.reversible = reversible;
	}

	public ArrayList<String> getEcNumbers() {
		return ecNumbers;
	}

	public void setEcNumbers(ArrayList<String> ecNumbers) {
		this.ecNumbers = ecNumbers;
	}

	public double getLevel() {
		return level;
	}

	public void setLevel(double level) {
		this.level = level;
	}

	public double getLevelPosition() {
		return levelPosition;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<String> getNames() {
		return names;
	}

	public void setNames(ArrayList<String> names) {
		this.names = names;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public int getOccurences() {
		return occurences;
	}

	public void setOccurences(int occurences) {
		this.occurences = occurences;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public boolean isDirectionsMatch()
	{
		return directionsMatch;
	}

	public void setDirectionsMatch( boolean directionsMatch )
	{
		this.directionsMatch = directionsMatch;
	}

	public void setLevelPosition(double levelPosition) {
		this.levelPosition = levelPosition;
	}

	public String getEquation() {
		return equation;
	}

	public void setEquation(String equation) {
		this.equation = equation;
	}

	public void writeReactionEquation() {
		StringBuffer reacBfr = new StringBuffer();
		StringBuffer prodBfr = new StringBuffer();
		StringBuffer rxnBfr = new StringBuffer();
		
		for (int r = 0; r < reactantIds.size(); r++) {
			if (LocalConfig.getInstance().getMetabolicPathways().get(this.pathwayId).getMetabolitesData().containsKey(reactantIds.get(r))) {
				String name = LocalConfig.getInstance().getMetabolicPathways().get(this.pathwayId).getMetabolitesData().get(reactantIds.get(r)).getNames().get(0);
				if (r == 0) {
					reacBfr.append(name);							
				} else {
					reacBfr.append(" + " + name);			
				}	
			}		
		}
		
		for (int r = 0; r < productIds.size(); r++) {
			if (LocalConfig.getInstance().getMetabolicPathways().get(this.pathwayId).getMetabolitesData().containsKey(productIds.get(r))) {
				String name = LocalConfig.getInstance().getMetabolicPathways().get(this.pathwayId).getMetabolitesData().get(productIds.get(r)).getNames().get(0);
				if (r == 0) {
					prodBfr.append(name);							
				} else {
					prodBfr.append(" + " + name);			
				}	
			}	
		}

		if (reversible.equals("0")) {
			rxnBfr.append(reacBfr).append(" " + GraphicalInterfaceConstants.HTML_NOT_REVERSIBLE_ARROW).append(prodBfr);
		} else if (reversible.equals("1")) {
			rxnBfr.append(reacBfr).append(" " + GraphicalInterfaceConstants.HTML_REVERSIBLE_ARROW).append(prodBfr);
		}
			
		this.equation = rxnBfr.toString();
		//System.out.println(this.equation);
	}
	
	@Override
	public String toString() {
		return "Pathway Reaction Data [pathwayId=" + pathwayId
		+ ", reactionId=" + reactionId
		+ ", keggReactionId=" + keggReactionId
		+ ", keggReactionIds=" + keggReactionIds
		+ ", reactantIds=" + reactantIds
		+ ", productIds=" + productIds
		+ ", keggReactantIds=" + keggReactantIds
		+ ", keggProductIds=" + keggProductIds
		+ ", keggIds=" + keggIds
		+ ", reversible=" + reversible
		+ ", ecNumbers=" + ecNumbers
		+ ", level=" + level
		+ ", levelPosition=" + levelPosition
		+ ", name=" + name
		+ ", names=" + names
		+ ", occurences=" + occurences
		+ ", equation=" + equation + "]";
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}

