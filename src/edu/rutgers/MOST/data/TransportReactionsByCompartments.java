package edu.rutgers.MOST.data;

import java.util.ArrayList;

public class TransportReactionsByCompartments {
	
	private ArrayList<String> compartmentIdsList;
	private ArrayList<SBMLReactionEquation> diffusionReactions;
	private ArrayList<SBMLReactionEquation> symportProtonReactions;
	private ArrayList<SBMLReactionEquation> symportPhosphateReactions;
	private ArrayList<SBMLReactionEquation> symportSodiumReactions;
	private ArrayList<SBMLReactionEquation> symportReactions;
	private ArrayList<SBMLReactionEquation> antiportProtonReactions;
	private ArrayList<SBMLReactionEquation> antiportPhosphateReactions;
	private ArrayList<SBMLReactionEquation> antiportSodiumReactions;
	private ArrayList<SBMLReactionEquation> antiportReactions;
	private ArrayList<SBMLReactionEquation> ptsReactions;
	private ArrayList<SBMLReactionEquation> abcReactions;
	private ArrayList<SBMLReactionEquation> otherTransportReactions;
	
	public ArrayList<String> getCompartmentIdsList() {
		return compartmentIdsList;
	}
	public void setCompartmentIdsList(ArrayList<String> compartmentIdsList) {
		this.compartmentIdsList = compartmentIdsList;
	}
	public ArrayList<SBMLReactionEquation> getDiffusionReactions() {
		return diffusionReactions;
	}
	public void setDiffusionReactions(
			ArrayList<SBMLReactionEquation> diffusionReactions) {
		this.diffusionReactions = diffusionReactions;
	}
	public ArrayList<SBMLReactionEquation> getSymportProtonReactions() {
		return symportProtonReactions;
	}
	public void setSymportProtonReactions(
			ArrayList<SBMLReactionEquation> symportProtonReactions) {
		this.symportProtonReactions = symportProtonReactions;
	}
	public ArrayList<SBMLReactionEquation> getSymportPhosphateReactions() {
		return symportPhosphateReactions;
	}
	public void setSymportPhosphateReactions(
			ArrayList<SBMLReactionEquation> symportPhosphateReactions) {
		this.symportPhosphateReactions = symportPhosphateReactions;
	}
	public ArrayList<SBMLReactionEquation> getSymportSodiumReactions() {
		return symportSodiumReactions;
	}
	public void setSymportSodiumReactions(
			ArrayList<SBMLReactionEquation> symportSodiumReactions) {
		this.symportSodiumReactions = symportSodiumReactions;
	}
	public ArrayList<SBMLReactionEquation> getSymportReactions() {
		return symportReactions;
	}
	public void setSymportReactions(ArrayList<SBMLReactionEquation> symportReactions) {
		this.symportReactions = symportReactions;
	}
	public ArrayList<SBMLReactionEquation> getAntiportProtonReactions() {
		return antiportProtonReactions;
	}
	public void setAntiportProtonReactions(
			ArrayList<SBMLReactionEquation> antiportProtonReactions) {
		this.antiportProtonReactions = antiportProtonReactions;
	}
	public ArrayList<SBMLReactionEquation> getAntiportPhosphateReactions() {
		return antiportPhosphateReactions;
	}
	public void setAntiportPhosphateReactions(
			ArrayList<SBMLReactionEquation> antiportPhosphateReactions) {
		this.antiportPhosphateReactions = antiportPhosphateReactions;
	}
	public ArrayList<SBMLReactionEquation> getAntiportSodiumReactions() {
		return antiportSodiumReactions;
	}
	public void setAntiportSodiumReactions(
			ArrayList<SBMLReactionEquation> antiportSodiumReactions) {
		this.antiportSodiumReactions = antiportSodiumReactions;
	}
	public ArrayList<SBMLReactionEquation> getAntiportReactions() {
		return antiportReactions;
	}
	public void setAntiportReactions(
			ArrayList<SBMLReactionEquation> antiportReactions) {
		this.antiportReactions = antiportReactions;
	}
	public ArrayList<SBMLReactionEquation> getPtsReactions() {
		return ptsReactions;
	}
	public void setPtsReactions(ArrayList<SBMLReactionEquation> ptsReactions) {
		this.ptsReactions = ptsReactions;
	}
	public ArrayList<SBMLReactionEquation> getAbcReactions() {
		return abcReactions;
	}
	public void setAbcReactions(ArrayList<SBMLReactionEquation> abcReactions) {
		this.abcReactions = abcReactions;
	}
	public ArrayList<SBMLReactionEquation> getOtherTransportReactions() {
		return otherTransportReactions;
	}
	public void setOtherTransportReactions(
			ArrayList<SBMLReactionEquation> otherTransportReactions) {
		this.otherTransportReactions = otherTransportReactions;
	}
	
}
