package edu.rutgers.MOST.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map;
import java.util.Vector;

public class FBAModel {

	protected Vector<ModelReaction> reactions;
	protected Vector<ModelMetabolite> metabolites;
	protected Vector<Double> objective;
	protected ArrayList<Map<Integer, Double>> sMatrix;
	
	public FBAModel(String databaseName) {
		ReactionFactory rFactory = new ReactionFactory("SBML", databaseName);
		this.reactions = rFactory.getAllReactions(); 
		this.objective = rFactory.getObjective();
		
		MetaboliteFactory mFactory = new MetaboliteFactory("SBML", databaseName);
		this.metabolites = mFactory.getAllInternalMetabolites();

		ReactantFactory reactantFactory = new ReactantFactory("SBML", databaseName);
		ArrayList<ModelReactant> reactantList = reactantFactory.getAllReactants();		
		ProductFactory productFactory = new ProductFactory("SBML", databaseName);
		ArrayList<ModelProduct> productList = productFactory.getAllProducts();
		
		this.sMatrix = new ArrayList<Map<Integer, Double>>(metabolites.size());
		for (int i = 0; i < metabolites.size(); i++) {
			Map<Integer, Double> sRow = new HashMap<Integer, Double>();
			sMatrix.add(sRow);
		}
		
		for (int i = 0; i < reactantList.size(); i++) {
			SBMLReactant reactant = (SBMLReactant) reactantList.get(i);
			if (reactant.getMetaboliteId() <= metabolites.size()) {
				sMatrix.get(reactant.getMetaboliteId() - 1).put(reactant.getReactionId() - 1, -reactant.getStoic());
			}
		}
		
		for (int i = 0; i < productList.size(); i++) {
			SBMLProduct product = (SBMLProduct) productList.get(i);
			if (product.getMetaboliteId() <= metabolites.size()) {
				sMatrix.get(product.getMetaboliteId() - 1).put(product.getReactionId() - 1, product.getStoic());
			}
		}
		
//		for (int i = 0; i < metabolites.size(); i++) {
//			Iterator<Integer> iterator = sMatrix.get(i).keySet().iterator();
//			
//			while (iterator.hasNext()) {
//				Integer j = iterator.next();
//				Double s = sMatrix.get(i).get(j);
//				
//				System.out.println((i + 1) + "\t" + (j + 1) + "\t" + s);
//			}
//		}
	}
	
	public Vector<ModelReaction> getReactions() {
		return this.reactions;
	}
	
	public int getNumMetabolites() {
		return this.metabolites.size();
	}
	
	public int getNumReactions() {
		return this.reactions.size();
	}
	
	public Vector<Double> getObjective() {
	    return this.objective;
	}
	
	public ArrayList<Map<Integer, Double>> getSMatrix() {
		return this.sMatrix;
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
				+ metabolites + ", objective=" + objective + "]";
	}

}
