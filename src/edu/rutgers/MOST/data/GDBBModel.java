package edu.rutgers.MOST.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class GDBBModel extends FBAModel {

	protected Vector<Double> syntheticObjective;
	protected Vector<String> geneAssociations;
	protected Vector<String> distinctGeneAssociations;
	protected ArrayList<Map<Integer, Double>> gprMatrix;
	private double D;
	private double C;
	private double timeLimit;
	private int threadNum;
	
	public double getD() {
		return D;
	}

	public void setD(double d) {
		D = d;
	}

	public double getC() {
		return C;
	}

	public void setC(double c) {
		C = c;
	}

	public GDBBModel(String databaseName) {
		super(databaseName);
		
		ReactionFactory rFactory = new ReactionFactory("SBML", databaseName);
		
		this.geneAssociations = rFactory.getGeneAssociations();
		
		this.distinctGeneAssociations = rFactory.getUniqueGeneAssociations();
		
		//	GPR Matrix
		gprMatrix = new ArrayList<Map<Integer, Double>>();
		for (int i = 0; i < distinctGeneAssociations.size(); i++) {
			Map<Integer, Double> sRow = new HashMap<Integer, Double>();
			gprMatrix.add(sRow);
		}
		
		//	Populating Values
		for (int i = 0; i < distinctGeneAssociations.size(); i++) {
			for(int j = 0; j < geneAssociations.size(); j++) {
				if(distinctGeneAssociations.elementAt(i).equals(geneAssociations.elementAt(j))) {
					gprMatrix.get(i).put(j, 1.0);
				}
			}
		}
		
		D = 100.0;
		C = 1;
		
		//	TODO Need to retrieve actual synthetic objective function
		syntheticObjective = new Vector<Double>();
		
		for (int i = 0; i < reactions.size(); i++) {
			SBMLReaction reac = (SBMLReaction) (reactions.elementAt(i));
			if (reac.getId() != 729) {
				syntheticObjective.add(0.0);
			}
			else {
//				System.out.println("Id = " + reac.getId() + ", Name = " + reac.getReactionName());
				syntheticObjective.add(1.0);
				break;
			}
		}
	}

	public Vector<Double> getSyntheticObjective() {
		return syntheticObjective;
	}

	public ArrayList<Map<Integer, Double>> getGprMatrix() {
		return gprMatrix;
	}

	public int getNumGeneAssociations() {
		return distinctGeneAssociations.size();
	}

	public void setTimeLimit(double timeLimit) {
		this.timeLimit = timeLimit;
	}

	public double getTimeLimit() {
		return timeLimit;
	}

	public int getThreadNum() {
		return threadNum;
	}

	public void setThreadNum(int threadNum) {
		this.threadNum = threadNum;
	}
}
