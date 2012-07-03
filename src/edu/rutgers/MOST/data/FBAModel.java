package edu.rutgers.MOST.data;

import java.util.Vector;

public class FBAModel {

	private Vector<ModelReaction> reactions;
	private Vector<ModelReaction> exchangeReactions;
	private Vector<ModelMetabolite> metabolites;
	private Vector<Integer> objFunction;
	public FBAModel(){
		reactions = new Vector<ModelReaction>();
		exchangeReactions = new Vector<ModelReaction>();
		metabolites = new Vector<ModelMetabolite> ();
		objFunction = new Vector<Integer>();
	}
	public void addMetabolite(ModelMetabolite metabolite){
		metabolites.add(metabolite);
	}
	
	public void addReaction(ModelReaction reaction){
		reactions.add(reaction);
	}
	
	public Vector<ModelReaction> getReactions(){
		return this.reactions;
	}
	
	public void addExchangeReaction(ModelReaction reaction){
		exchangeReactions.add(reaction);
	}
	
	public Vector<ModelReaction> getExchangeReactions(){
		return this.exchangeReactions;
	}
	public void setBiologicalObjective(Vector<Integer> reactionIds){
		this.objFunction = reactionIds;
	}
	
	public Vector<Integer> getBiologicalObjective(){
		return this.objFunction;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String toString() {
		return "FBAModel [reactions=" + reactions + ", metabolites="
				+ metabolites + ", objFunction=" + objFunction + "]";
	}

}
