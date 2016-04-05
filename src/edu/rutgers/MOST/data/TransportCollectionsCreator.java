package edu.rutgers.MOST.data;

import java.util.ArrayList;
import java.util.Map;

import edu.rutgers.MOST.config.LocalConfig;

public class TransportCollectionsCreator {

//	public void createTransportCollections(int id, Map<Integer, SBMLReaction> idReactionMap, String transportType) {
//		//System.out.println(idReactionMap.get(id).getReactionAbbreviation());
//		if (LocalConfig.getInstance().getModelKeggEquationMap().containsKey(Integer.toString(id))) {
//			ArrayList<String> keggReactantIds = LocalConfig.getInstance().getModelKeggEquationMap().get(Integer.toString(id)).getKeggReactantIds();
//			ArrayList<String> keggProductIds = LocalConfig.getInstance().getModelKeggEquationMap().get(Integer.toString(id)).getKeggProductIds();
//			String sideSpecies = "";
//			if (keggReactantIds.contains("C00080") && keggProductIds.contains("C00080")) {
//				if (keggReactantIds.size() > 1 && keggProductIds.size() > 1) {
//					keggReactantIds.remove(keggReactantIds.indexOf("C00080"));
//					keggProductIds.remove(keggProductIds.indexOf("C00080"));
//					sideSpecies = "C00080";
//				} 
//			}
//			if (keggReactantIds.contains("C00009") && keggProductIds.contains("C00009")) {
//				if (keggReactantIds.size() > 1 && keggProductIds.size() > 1) {
//					keggReactantIds.remove(keggReactantIds.indexOf("C00009"));
//					keggProductIds.remove(keggProductIds.indexOf("C00009"));
//					sideSpecies = "C00009";
//				} 
//			}
//			// note: there are a few transport reactions with Na (C01330) where Na and
//			// a second species are transported from c to p
//			if (keggReactantIds.equals(keggProductIds)) {
//				createTransportReactionNodes(id, idReactionMap, transportType, 
//						keggReactantIds, sideSpecies);
//			}
//		}
//	}
	
//	public void createTransportReactionNodes(int id, Map<Integer, SBMLReaction> idReactionMap, String transportType, 
//			ArrayList<String> keggReactantIds, String sideSpecies) {
//		TransportReactionNodeCreator creator = new TransportReactionNodeCreator();
//		if (keggReactantIds.size() > 0) {
//			String metabAbbr = createMetaboliteAbbreviation(LocalConfig.getInstance().getKeggIdMetaboliteMap().get(keggReactantIds.get(0)).get(0).getMetaboliteAbbreviation());
//			//System.out.println(metabAbbr);
//			//System.out.println(keggReactantIds);
//			if (LocalConfig.getInstance().getSideSpeciesList().contains(keggReactantIds.get(0))) {
//				if (!LocalConfig.getInstance().getTransportMetaboliteIds().contains(keggReactantIds.get(0))) {
//					LocalConfig.getInstance().getSideSpeciesTransportMetaboliteKeggIdMap().put(metabAbbr, LocalConfig.getInstance().getKeggIdMetaboliteMap().get(keggReactantIds.get(0)).get(0).getKeggId());
//					if (keggReactantIds.size() == 1) {
//						TransportReactionNode trn = creator.createTransportReactionNode(keggReactantIds, metabAbbr, 
//								id, idReactionMap, sideSpecies, transportType);
//						if (LocalConfig.getInstance().getSideSpeciesTransportReactionNodeMap().containsKey(keggReactantIds.get(0))) {
//							ArrayList<TransportReactionNode> trnList = LocalConfig.getInstance().getSideSpeciesTransportReactionNodeMap().get(keggReactantIds.get(0));
//							trnList.add(trn);
//							LocalConfig.getInstance().getSideSpeciesTransportReactionNodeMap().put(keggReactantIds.get(0), trnList);
//						} else {
//							ArrayList<TransportReactionNode> trnList = new ArrayList<TransportReactionNode>();
//							trnList.add(trn);
//							LocalConfig.getInstance().getSideSpeciesTransportReactionNodeMap().put(keggReactantIds.get(0), trnList);
//						}
//						//System.out.println(idReactionMap.get(id).getReactionAbbreviation());
//					}
//				}
//			} else if (LocalConfig.getInstance().getTransportMetaboliteIds().contains(keggReactantIds.get(0))) {
//				if (keggReactantIds.size() == 1) {
//					TransportReactionNode trn = creator.createTransportReactionNode(keggReactantIds, metabAbbr, 
//							id, idReactionMap, sideSpecies, transportType);
////					System.out.println("abbr " + idReactionMap.get(id).getReactionAbbreviation());
////					System.out.println(trn);
//					if (LocalConfig.getInstance().getKeggIdTransportReactionsMap().containsKey(keggReactantIds.get(0))) {
//						ArrayList<TransportReactionNode> trnList = LocalConfig.getInstance().getKeggIdTransportReactionsMap().get(keggReactantIds.get(0));
//						trnList.add(trn);
//						LocalConfig.getInstance().getKeggIdTransportReactionsMap().put(keggReactantIds.get(0), trnList);
//					} else {
//						ArrayList<TransportReactionNode> trnList = new ArrayList<TransportReactionNode>();
//						trnList.add(trn);
//						LocalConfig.getInstance().getKeggIdTransportReactionsMap().put(keggReactantIds.get(0), trnList);
//					}
//				}
//			}
//		}
//	}
	
	public String createMetaboliteAbbreviation(String metabAbbr) {
		// check if metabolite ends with "_x"
		String ch = metabAbbr.substring(metabAbbr.length() - 2, metabAbbr.length() - 1);
		//System.out.println("ch " + ch);
		if (ch.equals("_")) {
			metabAbbr = metabAbbr.substring(2, metabAbbr.length() - 2);
		} else {
			metabAbbr = metabAbbr.substring(2);
		}
		return metabAbbr;
	}
}
