package edu.rutgers.MOST.data;

import javax.swing.tree.DefaultMutableTreeNode;

public class Solution extends DefaultMutableTreeNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double objectiveValue;
	private double [] knockoutVector;
	private String databaseName;
	private String solutionName;
	private int index = -1;

	public Solution(double objectiveValue, double[] knockoutVector) {
		super(new Double( objectiveValue ).toString());
		this.objectiveValue = objectiveValue;
		this.knockoutVector = knockoutVector;
		this.solutionName = new Double( objectiveValue ).toString();
	}
	
	public Solution(String solutionName, String databaseName) {
		super(solutionName);
		this.setSolutionName(solutionName);
		this.databaseName = databaseName;
	}

	public Solution(String solutionName) {
		super(solutionName);
		this.setSolutionName(solutionName);
	}

	public double getObjectiveValue() {
		return objectiveValue;
	}

	public void setObjectiveValue(double objectiveValue) {
		this.objectiveValue = objectiveValue;
	}
	
	public double[] getKnockoutVector() {
		return knockoutVector;
	}

	public void setKnockoutVector(double[] knockoutVector) {
		this.knockoutVector = knockoutVector;
	}

	public String getDatabaseName() {
		return databaseName;
	}
	
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public String getSolutionName() {
		return solutionName;
	}

	public void setSolutionName(String solutionName) {
		this.solutionName = solutionName;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}