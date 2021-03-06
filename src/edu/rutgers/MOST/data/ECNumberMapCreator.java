package edu.rutgers.MOST.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import edu.rutgers.MOST.config.LocalConfig;

public class ECNumberMapCreator {

	/**
	 * EC Number map created to be used for getting information from loaded
	 * model by EC Number. 
	 * If reaction is identified by EC Number, the only reason it should be rejected is
	 * if main species in reaction are different from main species in visualization diagram.
	 * This often occurs with sugars for example.
	 */
	//public void createEcNumberReactionMap(Vector<SBMLReaction> rxns) {
	public Map<String, ArrayList<SBMLReaction>> createEcNumberReactionMap(Vector<SBMLReaction> rxns) {	
		Map<String, ArrayList<SBMLReaction>> ecNumberReactionMap = new HashMap<String, ArrayList<SBMLReaction>>();
		//ReactionFactory rf = new ReactionFactory("SBML");
		//Vector<SBMLReaction> rxns = rf.getReactionsByCompartment(LocalConfig.getInstance().getCytosolName());
		//Vector<SBMLReaction> rxns = rf.getAllReactions();
		for (int r = 0; r < rxns.size(); r++) {
			if (rxns.get(r) != null) {
				SBMLReaction reaction = (SBMLReaction) rxns.get(r);
				String ecString = reaction.getEcNumber();
				if (ecString != null && ecString.length() > 0) {
					// model may contain more than one EC number, separated by white space
					// AraGEM model has this condition
					java.util.List<String> ecNumbers = Arrays.asList(ecString.split("\\s"));
					for (int i = 0; i < ecNumbers.size(); i++) {
						String ecNum = "";
						// removes comma when EC numbers in a list - eg 1.1.1.1, 1.1.1.2
						if (ecNumbers.get(i).endsWith(",")) {
							ecNum = ecNumbers.get(i).substring(0, ecNumbers.get(i).length() - 2);
						} else {
							ecNum = ecNumbers.get(i);
						}
						if (ecNum != null && ecNum.length() > 0) {
							// KEGG reaction ids are sometimes present in EC Number column
							if (ecNum.startsWith("R") || ecNum.startsWith("r")) {
								//System.out.println(ecNum);
								if (LocalConfig.getInstance().getKeggReactionIdECNumberMap().containsKey(ecNum.toUpperCase())) {
									if (LocalConfig.getInstance().getKeggReactionIdECNumberMap().get(ecNum.toUpperCase()) != null) {
										try {
											//System.out.println(LocalConfig.getInstance().getKeggReactionIdECNumberMap().get(ecNum.toUpperCase()));
											for (int j = 0; j < LocalConfig.getInstance().getKeggReactionIdECNumberMap().get(ecNum.toUpperCase()).size(); j++) {
												ecNum = LocalConfig.getInstance().getKeggReactionIdECNumberMap().get(ecNum.toUpperCase()).get(j);
												if (ecNumberReactionMap.containsKey(ecNum)) {
													ArrayList<SBMLReaction> rxnsList = ecNumberReactionMap.get(ecNum);
													rxnsList.add(reaction);
													updateECNumberReactionMap(ecNumberReactionMap, ecNum, rxnsList);
													//ecNumberReactionMap.put(ecNum, rxnsList);
												} else {
													ArrayList<SBMLReaction> rxnsList = new ArrayList<SBMLReaction>();
													rxnsList.add(reaction);
													updateECNumberReactionMap(ecNumberReactionMap, ecNum, rxnsList);
													//ecNumberReactionMap.put(ecNum, rxnsList);
												}
											}
										} catch (Exception e) {
											
										}
									}
								}
							}
							if (ecNumberReactionMap.containsKey(ecNum)) {
								ArrayList<SBMLReaction> rxnsList = ecNumberReactionMap.get(ecNum);
								rxnsList.add(reaction);
								updateECNumberReactionMap(ecNumberReactionMap, ecNum, rxnsList);
								//ecNumberReactionMap.put(ecNum, rxnsList);
							} else {
								ArrayList<SBMLReaction> rxnsList = new ArrayList<SBMLReaction>();
								rxnsList.add(reaction);
								updateECNumberReactionMap(ecNumberReactionMap, ecNum, rxnsList);
								//ecNumberReactionMap.put(ecNum, rxnsList);
							}
						}
					}
					if (!LocalConfig.getInstance().getIdentifierIds().contains(reaction.getId())) {
						LocalConfig.getInstance().getIdentifierIds().add(reaction.getId());
					}
				} 
			}
		}

		//LocalConfig.getInstance().setEcNumberReactionMap(ecNumberReactionMap);
		//System.out.println("ec " + ecNumberReactionMap);
		return ecNumberReactionMap;
	}
	
	public void updateECNumberReactionMap(Map<String, ArrayList<SBMLReaction>> ecNumberReactionMap, 
		String ecNum, ArrayList<SBMLReaction> rxnsList) {
		if (ecNum != null && ecNum.trim().length() > 0) {
			ecNumberReactionMap.put(ecNum, rxnsList);
		}
	}
	
}
