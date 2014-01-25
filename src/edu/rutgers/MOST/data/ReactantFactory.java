package edu.rutgers.MOST.data;

import java.util.ArrayList;

public class ReactantFactory {
	private String sourceType;
	
	public ReactantFactory(String sourceType) {
		this.sourceType = sourceType;
	}
		
	public ModelReactant getReactantByReactionId(Integer reactionId) {
		if("SBML".equals(sourceType)){
			SBMLReactant reactant = new SBMLReactant();
			reactant.loadByReactionId(reactionId);
			return reactant;
		}
		return new SBMLReactant(); //Default behavior.
	}
	
	public ArrayList<SBMLReactant> getReactantsByReactionId(Integer reactionId) {
		SBMLReactantCollection aReactantCollection = new SBMLReactantCollection();
		if("SBML".equals(sourceType)){			
			aReactantCollection.loadByReactionId(reactionId);					
		}
		
		return aReactantCollection.getReactantList();		
	}
	
	public ArrayList<SBMLReactant> getAllReactants() {
		SBMLReactantCollection aReactantCollection = new SBMLReactantCollection();
		if("SBML".equals(sourceType)){			
			aReactantCollection.loadAll();					
		}
		
		return aReactantCollection.getReactantList();		
	}
		
}
