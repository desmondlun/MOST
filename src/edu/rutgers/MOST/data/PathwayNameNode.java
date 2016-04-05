package edu.rutgers.MOST.data;

import java.util.ArrayList;

public class PathwayNameNode {
	private String name;
	// links node to id field
	private String dataId;
	// links to model kegg id
	private ArrayList<String> keggIds;
	private double xPosition;
	private double yPosition;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDataId() {
		return dataId;
	}
	public void setDataId(String dataId) {
		this.dataId = dataId;
	}
	public ArrayList<String> getKeggIds() {
		return keggIds;
	}
	public void setKeggIds(ArrayList<String> keggIds) {
		this.keggIds = keggIds;
	}
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
	
	@Override
	public String toString() {
		return "PathwayName Node [name=" + name
		+ ", dataId=" + dataId
		+ ", keggIds=" + keggIds
		+ ", xPosition=" + xPosition
		+ ", yPosition=" + yPosition + "]";
	}

}
