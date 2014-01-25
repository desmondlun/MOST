package edu.rutgers.MOST.data;

import java.util.ArrayList;

import edu.rutgers.MOST.config.LocalConfig;

public class SBMLReactantCollection implements ModelReactantCollection {
    
	private Integer reactionId;
	private ArrayList<SBMLReactant> reactantList;
	

	public ArrayList<SBMLReactant> getReactantList() {
		return reactantList;
	}

	public void setReactantList(ArrayList<SBMLReactant> reactantList) {
		this.reactantList = reactantList;
	}
	
	public void setReactionId(Integer reactionId) {
		this.reactionId = reactionId;
	}

	public Integer getReactionId() {
		return reactionId;
	}

	public void loadByReactionId(Integer reactionId) {

	}

	public void loadAll() {
		reactantList = new ArrayList<SBMLReactant>();
		ReactionFactory rFactory = new ReactionFactory("SBML");
		ArrayList<Integer> reactionIdList = rFactory.reactionIdList();
		for (int i = 0; i < reactionIdList.size(); i++) {
			int index = reactionIdList.get(i);
			try {
				for (int j = 0; j < ((SBMLReactionEquation)LocalConfig.getInstance().getReactionEquationMap().get(index)).reactants.size(); j++) {
					SBMLReactant reactant = ((SBMLReactionEquation)LocalConfig.getInstance().getReactionEquationMap().get(index)).reactants.get(j);
					this.reactantList.add(reactant);
					//System.out.println(i);
					//System.out.println(reactant.getMetaboliteAbbreviation());
				}
			} catch (Throwable t) {
				
			}
		}
		//System.out.println("reactant list " + this.reactantList);
		//"select reaction_id, metabolite_id, stoic, metabolite_abbreviation from reaction_reactants, metabolites where reaction_reactants.metabolite_id = metabolites.id;");
        
        
        /*        
        aReactant.setReactionId(rs.getInt("reaction_id"));
        aReactant.setMetaboliteId(rs.getInt("metabolite_id"));
        aReactant.setStoic(rs.getDouble("stoic"));
        aReactant.setMetaboliteAbbreviation(rs.getString("metabolite_abbreviation"));
        */
        
	}	
		
	public static void main(String[] args) {
		/*
		ReactantFactory aReactantFactory = new ReactantFactory("SBML", "test_03182012");
		ArrayList<ModelReactant> reactants = aReactantFactory.getReactantsByReactionId(1);
		Iterator<ModelReactant> iterator = reactants.iterator();
		 
		while(iterator.hasNext()){
			SBMLReactant aReactant = (SBMLReactant)iterator.next();
			//System.out.print("\nabbr" + aReactant.toString());
		}
		*/
	}

}


