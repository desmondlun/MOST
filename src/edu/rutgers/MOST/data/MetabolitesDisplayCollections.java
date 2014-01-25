package edu.rutgers.MOST.data;

import java.util.ArrayList;
import java.util.Map;

public class MetabolitesDisplayCollections {

	private ArrayList<Integer> blankMetabIds;
	private Map<String, Object> metaboliteNameIdMap;
	private Map<String, Object> metaboliteUsedMap;
	private ArrayList<Integer> suspiciousMetabolites;		
	private ArrayList<Integer> unusedList;
	public ArrayList<Integer> getBlankMetabIds() {
		return blankMetabIds;
	}
	public void setBlankMetabIds(ArrayList<Integer> blankMetabIds) {
		this.blankMetabIds = blankMetabIds;
	}
	public Map<String, Object> getMetaboliteNameIdMap() {
		return metaboliteNameIdMap;
	}
	public void setMetaboliteNameIdMap(Map<String, Object> metaboliteNameIdMap) {
		this.metaboliteNameIdMap = metaboliteNameIdMap;
	}
	public Map<String, Object> getMetaboliteUsedMap() {
		return metaboliteUsedMap;
	}
	public void setMetaboliteUsedMap(Map<String, Object> metaboliteUsedMap) {
		this.metaboliteUsedMap = metaboliteUsedMap;
	}
	public ArrayList<Integer> getSuspiciousMetabolites() {
		return suspiciousMetabolites;
	}
	public void setSuspiciousMetabolites(ArrayList<Integer> suspiciousMetabolites) {
		this.suspiciousMetabolites = suspiciousMetabolites;
	}
	public ArrayList<Integer> getUnusedList() {
		return unusedList;
	}
	public void setUnusedList(ArrayList<Integer> unusedList) {
		this.unusedList = unusedList;
	}
	
}
