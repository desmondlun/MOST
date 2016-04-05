package edu.rutgers.MOST.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MetabolicPathway {
	private String id;
	private String name;
	private String keggId;
	private Map<String, PathwayMetaboliteData> metabolitesData = new HashMap<String, PathwayMetaboliteData>();
	private Map<String, PathwayMetaboliteNode> metabolitesNodes = new HashMap<String, PathwayMetaboliteNode>();
	private Map<String, PathwayReactionData> reactionsData = new HashMap<String, PathwayReactionData>();
	private Map<String, PathwayReactionNode> reactionsNodes = new HashMap<String, PathwayReactionNode>();
	private ArrayList<PathwayEdge> edges;
	private ArrayList<ArrayList<String>> ecNumbers = new ArrayList<ArrayList<String>>();
	// horizontal or vertical
	private int component;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getKeggId() {
		return keggId;
	}
	public void setKeggId(String keggId) {
		this.keggId = keggId;
	}
	public Map<String, PathwayMetaboliteData> getMetabolitesData() {
		return metabolitesData;
	}
	public void setMetabolitesData(
			Map<String, PathwayMetaboliteData> metabolitesData) {
		this.metabolitesData = metabolitesData;
	}
	public Map<String, PathwayMetaboliteNode> getMetabolitesNodes() {
		return metabolitesNodes;
	}
	public void setMetabolitesNodes(
			Map<String, PathwayMetaboliteNode> metabolitesNodes) {
		this.metabolitesNodes = metabolitesNodes;
	}
	public Map<String, PathwayReactionData> getReactionsData() {
		return reactionsData;
	}
	public void setReactionsData(Map<String, PathwayReactionData> reactionsData) {
		this.reactionsData = reactionsData;
	}
	public Map<String, PathwayReactionNode> getReactionsNodes() {
		return reactionsNodes;
	}
	public void setReactionsNodes(Map<String, PathwayReactionNode> reactionsNodes) {
		this.reactionsNodes = reactionsNodes;
	}
	public ArrayList<ArrayList<String>> getEcNumbers() {
		return ecNumbers;
	}
	public void setEcNumbers(ArrayList<ArrayList<String>> ecNumbers) {
		this.ecNumbers = ecNumbers;
	}
	public ArrayList<PathwayEdge> getEdges() {
		return edges;
	}
	public void setEdges(ArrayList<PathwayEdge> edges) {
		this.edges = edges;
	}
	
	public int getComponent() {
		return component;
	}
	public void setComponent(int component) {
		this.component = component;
	}
	@Override
	public String toString() {
		return "Metabolic Pathway [id=" + id
		+ ", name=" + name	
		+ ", keggId=" + keggId
		+ ", metabolitesData=" + metabolitesData
		+ ", metabolitesNodes=" + metabolitesNodes
		+ ", reactionsData=" + reactionsData
		+ ", reactionsNodes=" + reactionsNodes
		+ ", edges=" + edges
		+ ", ecNumbers=" + ecNumbers
		+ ", component=" + component + "]\n";
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}

