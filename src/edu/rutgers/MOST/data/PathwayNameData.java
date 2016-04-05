package edu.rutgers.MOST.data;

import java.util.ArrayList;

public class PathwayNameData {

	private String id;
	private ArrayList<String> keggIds;
	private String name;
	private double level;
	private double levelPosition;
	private ArrayList<String> metabolites;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ArrayList<String> getKeggIds() {
		return keggIds;
	}

	public void setKeggIds(ArrayList<String> keggIds) {
		this.keggIds = keggIds;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public void setLevelPosition(double levelPosition) {
		this.levelPosition = levelPosition;
	}

	public ArrayList<String> getMetabolites() {
		return metabolites;
	}

	public void setMetabolites(ArrayList<String> metabolites) {
		this.metabolites = metabolites;
	}

	@Override
	public String toString() {
		return "PathwayName Data [id=" + id
		+ ", keggIds=" + keggIds
		+ ", name=" + name
		+ ", level=" + level
		+ ", levelPosition=" + levelPosition
		+ ", metabolites=" + metabolites + "]";
	}
	
}

