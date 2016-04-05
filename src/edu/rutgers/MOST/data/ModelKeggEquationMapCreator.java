package edu.rutgers.MOST.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.rutgers.MOST.config.LocalConfig;

public class ModelKeggEquationMapCreator {
	
	public void createKeggEquationMap() {
		Map<String, PathwayReactionData> modelKeggEquationMap = new HashMap<String, PathwayReactionData>();
		ArrayList<Integer> reactionsMissingKeggId = new ArrayList<Integer>();
		ArrayList<Integer> reactionsContainingKeggIdsNotInGraph = new ArrayList<Integer>();
		for (int j = 0; j < LocalConfig.getInstance().getReactionEquationMap().size(); j++) {
			boolean found = true;
			ArrayList<String> keggReactantIds = new ArrayList<String>();
			ArrayList<String> keggProductIds = new ArrayList<String>();
			PathwayReactionData prd = new PathwayReactionData();
			if (LocalConfig.getInstance().getReactionEquationMap().containsKey(j)) {
				int reactionId = -1;
				ArrayList<SBMLReactant> reac = ((SBMLReactionEquation) LocalConfig.getInstance().getReactionEquationMap().get(j)).getReactants();
				ArrayList<SBMLProduct> prod = ((SBMLReactionEquation) LocalConfig.getInstance().getReactionEquationMap().get(j)).getProducts();
				// accounts for reactions of the "--> product" type
				if (reac.size() > 0) {
					reactionId = reac.get(0).getReactionId();
				} else if (prod.size() > 0) {
					reactionId = prod.get(0).getReactionId();
				}
				if (reactionId > -1) {
					prd.setReactionId(Integer.toString(reactionId));
					for (int r = 0; r < ((SBMLReactionEquation) LocalConfig.getInstance().getReactionEquationMap().get(j)).getReactants().size(); r++) {
						//reactionId = ((SBMLReactionEquation) LocalConfig.getInstance().getReactionEquationMap().get(j)).getReactants().get(r).getReactionId();
						int metabId = ((SBMLReactionEquation) LocalConfig.getInstance().getReactionEquationMap().get(j)).getReactants().get(r).getMetaboliteId();
						if (LocalConfig.getInstance().getMetaboliteIdKeggIdMap().get(Integer.toString(metabId)) != null) {
							//System.out.println("k r " + LocalConfig.getInstance().getMetaboliteIdKeggIdMap().get(Integer.toString(metabId)));
							keggReactantIds.add(LocalConfig.getInstance().getMetaboliteIdKeggIdMap().get(Integer.toString(metabId)));
	// getting reactions with kegg ids not in graph is not working correctly here
//							if (!LocalConfig.getInstance().getKeggIdsInGraph().contains(LocalConfig.getInstance().getMetaboliteIdKeggIdMap().get(Integer.toString(metabId)))) {
//								if (!reactionsContainingKeggIdsNotInGraph.contains(Integer.valueOf(prd.getReactionId()))) {
//									reactionsContainingKeggIdsNotInGraph.add(Integer.valueOf(prd.getReactionId()));
//								}
//							}
						} else {
							found = false;
							keggReactantIds.clear();
							break;
						}
					}
					for (int p = 0; p < ((SBMLReactionEquation) LocalConfig.getInstance().getReactionEquationMap().get(j)).getProducts().size(); p++) {
						int metabId = ((SBMLReactionEquation) LocalConfig.getInstance().getReactionEquationMap().get(j)).getProducts().get(p).getMetaboliteId();
						if (LocalConfig.getInstance().getMetaboliteIdKeggIdMap().get(Integer.toString(metabId)) != null) {
							//System.out.println("k p " + LocalConfig.getInstance().getMetaboliteIdKeggIdMap().get(Integer.toString(metabId)));
							keggProductIds.add(LocalConfig.getInstance().getMetaboliteIdKeggIdMap().get(Integer.toString(metabId)));
//							if (!LocalConfig.getInstance().getKeggIdsInGraph().contains(LocalConfig.getInstance().getMetaboliteIdKeggIdMap().get(Integer.toString(metabId)))) {
//								if (!reactionsContainingKeggIdsNotInGraph.contains(Integer.valueOf(prd.getReactionId()))) {
//									reactionsContainingKeggIdsNotInGraph.add(Integer.valueOf(prd.getReactionId()));
//								}
//							}
						}  else {
							found = false;
							keggProductIds.clear();
						}
					}
					if (found) {
						prd.setKeggReactantIds(keggReactantIds);
						prd.setKeggProductIds(keggProductIds);
						modelKeggEquationMap.put(Integer.toString(reactionId), prd);
					} else {
						reactionsMissingKeggId.add(Integer.valueOf(prd.getReactionId()));
					}
				}
				//System.out.println(reactionId);
			} 
//			for (int p = 0; p < ((SBMLReactionEquation) LocalConfig.getInstance().getReactionEquationMap().get(j)).getProducts().size(); p++) {
//				int metabId = ((SBMLReactionEquation) LocalConfig.getInstance().getReactionEquationMap().get(j)).getProducts().get(p).getMetaboliteId();
//				if (LocalConfig.getInstance().getMetaboliteIdKeggIdMap().get(Integer.toString(metabId)) != null) {
//					//System.out.println("k p " + LocalConfig.getInstance().getMetaboliteIdKeggIdMap().get(Integer.toString(metabId)));
//					keggProductIds.add(LocalConfig.getInstance().getMetaboliteIdKeggIdMap().get(Integer.toString(metabId)));
//				}  else {
//					found = false;
//					keggProductIds.clear();
//				}
//			}
			//System.out.println(reactionId + " found=" + found);
//			if (found) {
//				prd.setKeggReactantIds(keggReactantIds);
//				prd.setKeggProductIds(keggProductIds);
//				modelKeggEquationMap.put(Integer.toString(reactionId), prd);
//			}
		}
		LocalConfig.getInstance().setModelKeggEquationMap(modelKeggEquationMap);
		//System.out.println(LocalConfig.getInstance().getModelKeggEquationMap());
		LocalConfig.getInstance().setReactionsMissingKeggId(reactionsMissingKeggId);
		LocalConfig.getInstance().setReactionsContainingKeggIdsNotInGraph(reactionsContainingKeggIdsNotInGraph);
	}

}
