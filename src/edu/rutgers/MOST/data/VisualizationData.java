package edu.rutgers.MOST.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

public class VisualizationData {
	
	// map with node names and positions
	private Map<String, String[]> nodeNamePositionMap;
	// keyset of node names
	private ArrayList<String> nodeNameList; 
	// key = name of rxn, value = reactant, product, reversible
	private Map<String, String[]> reactionMap; 
	// keyset of reactions
	private ArrayList<String> reactionList;
	// lists used to distinguish node types
	private ArrayList<String> borderList;   // compartment border
	private ArrayList<String> noBorderList;   // metabolite node border
	private ArrayList<String> pathwayNames;
	private ArrayList<String> mainMetabolites;
	private ArrayList<String> smallMainMetabolites;
	private ArrayList<String> sideMetabolites;
	private ArrayList<String> cofactors;
	private ArrayList<String> reactions;
	private Map<String, Double> fluxMap; 
	private Map<String, Double> colorMap;
	private ArrayList<String> koReactions;
	private ArrayList<String> foundMetabolitesList;
	private ArrayList<String> foundReactionsList;
	private ArrayList<String> foundPathwayNamesList;
	private Map<String, Icon> iconMap; 
	private ArrayList<Integer> plottedIds;
	private Map<String, String> oldNameNewNameMap; 
	// maps for find - exact match
	private HashMap<String, ArrayList<String[]>> metaboliteAbbrPositionsMap;
	private HashMap<String, ArrayList<String[]>> keggMetaboliteIdPositionsMap;
	private HashMap<String, ArrayList<String[]>> ecNumberPositionsMap;
	private HashMap<String, ArrayList<String[]>> keggReactionIdPositionsMap;
	private HashMap<String, ArrayList<String[]>> reactionAbbrPositionsMap;
	private String report;
	
	public Map<String, String[]> getNodeNamePositionMap() {
		return nodeNamePositionMap;
	}
	public void setNodeNamePositionMap(Map<String, String[]> nodeNamePositionMap) {
		this.nodeNamePositionMap = nodeNamePositionMap;
	}
	public ArrayList<String> getNodeNameList() {
		return nodeNameList;
	}
	public void setNodeNameList(ArrayList<String> nodeNameList) {
		this.nodeNameList = nodeNameList;
	}
	public Map<String, String[]> getReactionMap() {
		return reactionMap;
	}
	public void setReactionMap(Map<String, String[]> reactionMap) {
		this.reactionMap = reactionMap;
	}
	public ArrayList<String> getReactionList() {
		return reactionList;
	}
	public void setReactionList(ArrayList<String> reactionList) {
		this.reactionList = reactionList;
	}
	public ArrayList<String> getBorderList() {
		return borderList;
	}
	public void setBorderList(ArrayList<String> borderList) {
		this.borderList = borderList;
	}
	public ArrayList<String> getNoBorderList() {
		return noBorderList;
	}
	public void setNoBorderList(ArrayList<String> noBorderList) {
		this.noBorderList = noBorderList;
	}
	public ArrayList<String> getPathwayNames() {
		return pathwayNames;
	}
	public void setPathwayNames(ArrayList<String> pathwayNames) {
		this.pathwayNames = pathwayNames;
	}
	public ArrayList<String> getMainMetabolites() {
		return mainMetabolites;
	}
	public void setMainMetabolites(ArrayList<String> mainMetabolites) {
		this.mainMetabolites = mainMetabolites;
	}
	public ArrayList<String> getSmallMainMetabolites() {
		return smallMainMetabolites;
	}
	public void setSmallMainMetabolites(ArrayList<String> smallMainMetabolites) {
		this.smallMainMetabolites = smallMainMetabolites;
	}
	public ArrayList<String> getSideMetabolites() {
		return sideMetabolites;
	}
	public void setSideMetabolites(ArrayList<String> sideMetabolites) {
		this.sideMetabolites = sideMetabolites;
	}
	public ArrayList<String> getCofactors() {
		return cofactors;
	}
	public void setCofactors(ArrayList<String> cofactors) {
		this.cofactors = cofactors;
	}
	public ArrayList<String> getReactions() {
		return reactions;
	}
	public void setReactions(ArrayList<String> reactions) {
		this.reactions = reactions;
	}
	public Map<String, Double> getFluxMap() {
		return fluxMap;
	}
	public void setFluxMap(Map<String, Double> fluxMap) {
		this.fluxMap = fluxMap;
	}
	public Map<String, Double> getColorMap() {
		return colorMap;
	}
	public void setColorMap(Map<String, Double> colorMap) {
		this.colorMap = colorMap;
	}
	public ArrayList<String> getKoReactions() {
		return koReactions;
	}
	public void setKoReactions(ArrayList<String> koReactions) {
		this.koReactions = koReactions;
	}
	public ArrayList<String> getFoundMetabolitesList() {
		return foundMetabolitesList;
	}
	public void setFoundMetabolitesList(ArrayList<String> foundMetabolitesList) {
		this.foundMetabolitesList = foundMetabolitesList;
	}
	public ArrayList<String> getFoundReactionsList() {
		return foundReactionsList;
	}
	public void setFoundReactionsList(ArrayList<String> foundReactionsList) {
		this.foundReactionsList = foundReactionsList;
	}
	public ArrayList<String> getFoundPathwayNamesList() {
		return foundPathwayNamesList;
	}
	public void setFoundPathwayNamesList(ArrayList<String> foundPathwayNamesList) {
		this.foundPathwayNamesList = foundPathwayNamesList;
	}
	public Map<String, Icon> getIconMap() {
		return iconMap;
	}
	public void setIconMap(Map<String, Icon> iconMap) {
		this.iconMap = iconMap;
	}
	public ArrayList<Integer> getPlottedIds() {
		return plottedIds;
	}
	public void setPlottedIds(ArrayList<Integer> plottedIds) {
		this.plottedIds = plottedIds;
	}
	public Map<String, String> getOldNameNewNameMap() {
		return oldNameNewNameMap;
	}
	public void setOldNameNewNameMap(Map<String, String> oldNameNewNameMap) {
		this.oldNameNewNameMap = oldNameNewNameMap;
	}
	public HashMap<String, ArrayList<String[]>> getMetaboliteAbbrPositionsMap() {
		return metaboliteAbbrPositionsMap;
	}
	public void setMetaboliteAbbrPositionsMap(
			HashMap<String, ArrayList<String[]>> metaboliteAbbrPositionsMap) {
		this.metaboliteAbbrPositionsMap = metaboliteAbbrPositionsMap;
	}
	public HashMap<String, ArrayList<String[]>> getKeggMetaboliteIdPositionsMap() {
		return keggMetaboliteIdPositionsMap;
	}
	public void setKeggMetaboliteIdPositionsMap(
			HashMap<String, ArrayList<String[]>> keggMetaboliteIdPositionsMap) {
		this.keggMetaboliteIdPositionsMap = keggMetaboliteIdPositionsMap;
	}
	public HashMap<String, ArrayList<String[]>> getEcNumberPositionsMap() {
		return ecNumberPositionsMap;
	}
	public void setEcNumberPositionsMap(
			HashMap<String, ArrayList<String[]>> ecNumberPositionsMap) {
		this.ecNumberPositionsMap = ecNumberPositionsMap;
	}
	public HashMap<String, ArrayList<String[]>> getKeggReactionIdPositionsMap() {
		return keggReactionIdPositionsMap;
	}
	public void setKeggReactionIdPositionsMap(
			HashMap<String, ArrayList<String[]>> keggReactionIdPositionsMap) {
		this.keggReactionIdPositionsMap = keggReactionIdPositionsMap;
	}
	public HashMap<String, ArrayList<String[]>> getReactionAbbrPositionsMap() {
		return reactionAbbrPositionsMap;
	}
	public void setReactionAbbrPositionsMap(
			HashMap<String, ArrayList<String[]>> reactionAbbrPositionsMap) {
		this.reactionAbbrPositionsMap = reactionAbbrPositionsMap;
	}
	public String getReport() {
		return report;
	}
	public void setReport(String report) {
		this.report = report;
	}
	

}
