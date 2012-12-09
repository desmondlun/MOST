package edu.rutgers.MOST.data;

import java.util.ArrayList;

public class ReactantFactory {
	private String sourceType;
	private String databaseName;
	
	public ReactantFactory(String sourceType, String databaseName) {
		this.sourceType = sourceType;
		this.databaseName = databaseName;
	}
		
	public ModelReactant getReactantByReactionId(Integer reactionId) {
		if("SBML".equals(sourceType)){
			SBMLReactant reactant = new SBMLReactant();
			reactant.setDatabaseName(databaseName);
			reactant.loadByReactionId(reactionId);
			return reactant;
		}
		return new SBMLReactant(); //Default behavior.
	}
	
	public ArrayList<ModelReactant> getReactantsByReactionId(Integer reactionId) {
		SBMLReactantCollection aReactantCollection = new SBMLReactantCollection();
		if("SBML".equals(sourceType)){			
			aReactantCollection.setDatabaseName(databaseName);
			aReactantCollection.loadByReactionId(reactionId);					
		}
		
		return aReactantCollection.getReactantList();		
	}
	
	public ArrayList<ModelReactant> getAllReactants() {
		SBMLReactantCollection aReactantCollection = new SBMLReactantCollection();
		if("SBML".equals(sourceType)){			
			aReactantCollection.setDatabaseName(databaseName);
			aReactantCollection.loadAll();					
		}
		
		return aReactantCollection.getReactantList();		
	}
		
}
