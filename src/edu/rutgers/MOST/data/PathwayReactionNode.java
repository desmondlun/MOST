package edu.rutgers.MOST.data;

import java.util.ArrayList;

/**
 * Data for each reaction node in graph
 */
public class PathwayReactionNode {
	private String compartment;
	private String pathwayId;
	// key, used for tooltip in graph
	private String reactionName;
	// used for node text
	private String modelReactionName;
	private String dataId;
	private String equation;
	private ArrayList<SBMLReaction> reactions;
	private double fluxValue;
	private String reversible;
	private double xPosition;
	private double yPosition;	
	private ArrayList<String> compartmentList;
	private ArrayList<String> compartmentReactantsList;
	private ArrayList<String> compartmentProductsList;
	private String subsystem;	
	private String displayName;
	private String name;
	private String direction;

	public String getCompartment() {
		return compartment;
	}

	public void setCompartment(String compartment) {
		this.compartment = compartment;
	}

	public String getPathwayId() {
		return pathwayId;
	}

	public void setPathwayId(String pathwayId) {
		this.pathwayId = pathwayId;
	}

	public String getReactionName() {
		return reactionName;
	}

	public void setReactionName(String reactionName) {
		this.reactionName = reactionName;
	}

//	public ArrayList<String> getModelReactionNames() {
//		return modelReactionNames;
//	}
//
//	public void setModelReactionNames(ArrayList<String> modelReactionNames) {
//		this.modelReactionNames = modelReactionNames;
//	}

	public String getModelReactionName() {
		return modelReactionName;
	}

	public void setModelReactionName(String modelReactionName) {
		this.modelReactionName = modelReactionName;
	}

	public String getDataId() {
		return dataId;
	}

	public void setDataId(String dataId) {
		this.dataId = dataId;
	}

	public String getEquation() {
		return equation;
	}

	public void setEquation(String equation) {
		this.equation = equation;
	}

//	public String getModelEquation() {
//		return modelEquation;
//	}
//
//	public void setModelEquation(String modelEquation) {
//		this.modelEquation = modelEquation;
//	}

//	public ArrayList<String> getModelEquations() {
//		return modelEquations;
//	}
//
//	public void setModelEquations(ArrayList<String> modelEquations) {
//		this.modelEquations = modelEquations;
//	}
//
//	public ArrayList<Double> getFluxes() {
//		return fluxes;
//	}
//
//	public void setFluxes(ArrayList<Double> fluxes) {
//		this.fluxes = fluxes;
//	}

	public ArrayList<SBMLReaction> getReactions() {
		return reactions;
	}

	public void setReactions(ArrayList<SBMLReaction> reactions) {
		this.reactions = reactions;
	}

	public double getFluxValue() {
		return fluxValue;
	}

	public void setFluxValue(double fluxValue) {
		this.fluxValue = fluxValue;
	}

//	public ArrayList<String> getEnzymeDataEquations() {
//		return enzymeDataEquations;
//	}
//
//	public void setEnzymeDataEquations(ArrayList<String> enzymeDataEquations) {
//		this.enzymeDataEquations = enzymeDataEquations;
//	}
//
//	public ArrayList<String> getMainReactants() {
//		return mainReactants;
//	}
//
//	public void setMainReactants(ArrayList<String> mainReactants) {
//		this.mainReactants = mainReactants;
//	}
//
//	public ArrayList<String> getMainProducts() {
//		return mainProducts;
//	}
//
//	public void setMainProducts(ArrayList<String> mainProducts) {
//		this.mainProducts = mainProducts;
//	}
//
//	public ArrayList<String> getSideReactants() {
//		return sideReactants;
//	}
//
//	public void setSideReactants(ArrayList<String> sideReactants) {
//		this.sideReactants = sideReactants;
//	}
//
//	public ArrayList<String> getSideProducts() {
//		return sideProducts;
//	}
//
//	public void setSideProducts(ArrayList<String> sideProducts) {
//		this.sideProducts = sideProducts;
//	}

//	public ArrayList<PathwayMetaboliteNode> getMainPathwayReactants() {
//		return mainPathwayReactants;
//	}
//
//	public void setMainPathwayReactants(
//			ArrayList<PathwayMetaboliteNode> mainPathwayReactants) {
//		this.mainPathwayReactants = mainPathwayReactants;
//	}
//
//	public ArrayList<PathwayMetaboliteNode> getMainPathwayProducts() {
//		return mainPathwayProducts;
//	}
//
//	public void setMainPathwayProducts(
//			ArrayList<PathwayMetaboliteNode> mainPathwayProducts) {
//		this.mainPathwayProducts = mainPathwayProducts;
//	}

	public String getReversible() {
		return reversible;
	}

	public void setReversible(String reversible) {
		this.reversible = reversible;
	}

//	public ArrayList<String> getEcNumbers() {
//		return ecNumbers;
//	}
//
//	public void setEcNumbers(ArrayList<String> ecNumbers) {
//		this.ecNumbers = ecNumbers;
//	}

	public double getxPosition() {
		return xPosition;
	}

	public void setxPosition(double xPosition) {
		this.xPosition = xPosition;
	}

	public double getyPosition() {
		return yPosition;
	}

	public void setyPosition(double yPosition) {
		this.yPosition = yPosition;
	}

	public ArrayList<String> getCompartmentList() {
		return compartmentList;
	}

	public void setCompartmentList(ArrayList<String> compartmentList) {
		this.compartmentList = compartmentList;
	}

	public ArrayList<String> getCompartmentReactantsList() {
		return compartmentReactantsList;
	}

	public void setCompartmentReactantsList(
			ArrayList<String> compartmentReactantsList) {
		this.compartmentReactantsList = compartmentReactantsList;
	}

	public ArrayList<String> getCompartmentProductsList() {
		return compartmentProductsList;
	}

	public void setCompartmentProductsList(ArrayList<String> compartmentProductsList) {
		this.compartmentProductsList = compartmentProductsList;
	}

	public String getSubsystem() {
		return subsystem;
	}

	public void setSubsystem(String subsystem) {
		this.subsystem = subsystem;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	@Override
	public String toString() {
		return "Pathway Reaction Node [compartment=" + compartment
		+ ", pathwayId=" + pathwayId
		+ ", reactionName=" + reactionName
		+ ", modelReactionName=" + modelReactionName
		+ ", dataId=" + dataId
		+ ", equation=" + equation
		//+ ", modelEquation=" + modelEquation
		//+ ", modelEquations=" + modelEquations
		//+ ", fluxes=" + fluxes
		//+ ", enzymeDataEquations=" + enzymeDataEquations
		//+ ", mainReactants=" + mainReactants		
		//+ ", mainProducts=" + mainProducts
		//+ ", sideReactants=" + sideReactants		
		//+ ", sideProducts=" + sideProducts
		//+ ", mainPathwayReactants=" + mainPathwayReactants		
		//+ ", mainPathwayProducts=" + mainPathwayProducts
		+ ", reversible=" + reversible
		//+ ", ecNumbers=" + ecNumbers
		+ ", xPosition=" + xPosition
		+ ", yPosition=" + yPosition + "]";
	}
}
