package edu.rutgers.MOST.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import edu.rutgers.MOST.config.LocalConfig;

public class TransportReactionCategorizer {

	TransportCollectionsCreator transportCollectionsCreator = new TransportCollectionsCreator();
	public void removeExternalReactions() {
		ReactionFactory rf = new ReactionFactory("SBML");
		Vector<SBMLReaction> reactions = rf.getAllReactions();
		ArrayList<Integer> externalReactionIds = new ArrayList<Integer>();
		MetaboliteFactory f = new MetaboliteFactory("SBML");
		ArrayList<Integer> metaboliteExternalIdList = f.metaboliteExternalIdList();
		for (int i = 0; i < reactions.size(); i++) {
			int id = reactions.get(i).getId();
			SBMLReactionEquation equn = (SBMLReactionEquation) LocalConfig.getInstance().getReactionEquationMap().get(id);
			//System.out.println("eq " + equn);
			// get external reactions, not plotted
			ArrayList<SBMLReactant> reactants = equn.getReactants();
			for (int r = 0; r < reactants.size(); r++) {
				int metabId = reactants.get(r).getMetaboliteId();
				if (metaboliteExternalIdList.contains(metabId)) {
					if (!externalReactionIds.contains(id)) {
						externalReactionIds.add(id);
					}
				}
			}
			ArrayList<SBMLProduct> products = equn.getProducts();
			for (int p = 0; p < products.size(); p++) {
				int metabId = products.get(p).getMetaboliteId();
				if (metaboliteExternalIdList.contains(metabId)) {
					if (!externalReactionIds.contains(id)) {
						externalReactionIds.add(id);
					}
				}
			}
			// remove reactions of a --> or --> a type
			if (reactants.size() == 0 || products.size() == 0) {
				externalReactionIds.add(id);
			}
		}
		//System.out.println("ext rxns " + externalReactionIds);
		LocalConfig.getInstance().setExternalReactionIds(externalReactionIds);
		// faster to make new list than remove from old list
		ArrayList<Integer> unplottedIds = new ArrayList<Integer>();
		for (int n = 0; n < LocalConfig.getInstance().getUnplottedReactionIds().size(); n++) {
			if (!externalReactionIds.contains(LocalConfig.getInstance().getUnplottedReactionIds().get(n))) {
				unplottedIds.add(LocalConfig.getInstance().getUnplottedReactionIds().get(n));
			}
		}
		LocalConfig.getInstance().setUnplottedReactionIds(unplottedIds);
//		for (int n = 0; n < externalReactionIds.size(); n++) {
//			if (LocalConfig.getInstance().getUnplottedReactionIds().contains(externalReactionIds.get(n))) {
//				LocalConfig.getInstance().getUnplottedReactionIds().remove(LocalConfig.getInstance().getUnplottedReactionIds().indexOf(externalReactionIds.get(n)));
//			}
//		}
//		Collections.sort(LocalConfig.getInstance().getUnplottedReactionIds());
//		System.out.println("not plotted no ext " + LocalConfig.getInstance().getUnplottedReactionIds());
	}
	
	public void removeBiomassReactions() {
		ReactionFactory rf = new ReactionFactory("SBML");
		Vector<SBMLReaction> reactions = rf.getAllReactions();
		for (int i = 0; i < reactions.size(); i++) {
			//			for (int j = 0; j < LocalConfig.getInstance().getUnplottedReactionIds().size(); j++) {
			int id = reactions.get(i).getId();
			//				int id = LocalConfig.getInstance().getUnplottedReactionIds().get(j);
			SBMLReactionEquation equn = (SBMLReactionEquation) LocalConfig.getInstance().getReactionEquationMap().get(id);
			//System.out.println("eq " + equn);
			// get external reactions, not plotted
			ArrayList<SBMLReactant> reactants = equn.getReactants();
			ArrayList<SBMLProduct> products = equn.getProducts();
//			System.out.println(reactants.size());
//			System.out.println(products.size());
			if (reactants.size() > 15 || products.size() > 15) {
				if (LocalConfig.getInstance().getUnplottedReactionIds().contains(id)) {
					LocalConfig.getInstance().getUnplottedReactionIds().remove(LocalConfig.getInstance().getUnplottedReactionIds().indexOf(id));
				}
			}
		}
	}
	
	public void categorizeTransportReactions() {
		ReactionFactory rf = new ReactionFactory("SBML");
		removeExternalReactions();
		removeBiomassReactions();
		ArrayList<TransportReactionsByCompartments> transportReactionsByCompartmentsList = new ArrayList<TransportReactionsByCompartments>();
		for (int i = 0; i < LocalConfig.getInstance().getListOfCompartmentLists().size(); i++) {
			TransportReactionsByCompartments trbc = new TransportReactionsByCompartments();
			ArrayList<SBMLReactionEquation> diffusionReactions = new ArrayList<SBMLReactionEquation>();
			ArrayList<SBMLReactionEquation> symportProtonReactions = new ArrayList<SBMLReactionEquation>();
			ArrayList<SBMLReactionEquation> symportPhosphateReactions = new ArrayList<SBMLReactionEquation>();
			ArrayList<SBMLReactionEquation> symportSodiumReactions = new ArrayList<SBMLReactionEquation>();
			ArrayList<SBMLReactionEquation> symportReactions = new ArrayList<SBMLReactionEquation>();
			ArrayList<SBMLReactionEquation> antiportProtonReactions = new ArrayList<SBMLReactionEquation>();
			ArrayList<SBMLReactionEquation> antiportPhosphateReactions = new ArrayList<SBMLReactionEquation>();
			ArrayList<SBMLReactionEquation> antiportSodiumReactions = new ArrayList<SBMLReactionEquation>();
			ArrayList<SBMLReactionEquation> antiportReactions = new ArrayList<SBMLReactionEquation>();
			ArrayList<SBMLReactionEquation> ptsReactions = new ArrayList<SBMLReactionEquation>();
			ArrayList<SBMLReactionEquation> abcReactions = new ArrayList<SBMLReactionEquation>();
			ArrayList<SBMLReactionEquation> otherTransportReactions = new ArrayList<SBMLReactionEquation>();
			trbc.setCompartmentIdsList(LocalConfig.getInstance().getListOfCompartmentLists().get(i));
			if (LocalConfig.getInstance().getListOfCompartmentLists().get(i).size() == 2) {
				Vector<SBMLReaction> transportRxns = rf.getTransportReactionsByCompartments(LocalConfig.getInstance().getListOfCompartmentLists().get(i).get(0), 
						LocalConfig.getInstance().getListOfCompartmentLists().get(i).get(1));
				for (int j = 0; j < transportRxns.size(); j++) {
					SBMLReactionEquation equn = (SBMLReactionEquation) LocalConfig.getInstance().getReactionEquationMap().get(transportRxns.get(j).getId());
					if (LocalConfig.getInstance().getModelKeggEquationMap().containsKey(Integer.toString(transportRxns.get(j).getId()))) {
						PathwayReactionData modelData = LocalConfig.getInstance().getModelKeggEquationMap().get(Integer.toString(transportRxns.get(j).getId()));
						// symport reactions
						if (equn.getCompartmentReactantsList().size() == 1 && equn.getCompartmentProductsList().size() == 1) {
							if (modelData.getKeggReactantIds().size() == 1 && modelData.getKeggProductIds().size() == 1) {
								if (modelData.getKeggReactantIds().equals(modelData.getKeggProductIds())) {
									// diffusion, passive transport
									diffusionReactions.add(equn);
//									System.out.println("e " + modelData.getKeggReactantIds());
								} else {
									// this doesn't seem to occur
//									System.out.println("r " + modelData.getKeggReactantIds());
//									System.out.println("p " + modelData.getKeggProductIds());
								}
								// symport reactions
							} else {
//								// proton symport
								if (modelData.getKeggReactantIds().size() == 2 && modelData.getKeggProductIds().size() == 2 &&
										modelData.getKeggReactantIds().contains("C00080") && modelData.getKeggReactantIds().contains("C00080")) {
									symportProtonReactions.add(equn);
									//System.out.println(equn.equationAbbreviations);
									// pi symport
								} else if (modelData.getKeggReactantIds().size() == 2 && modelData.getKeggProductIds().size() == 2 &&
										modelData.getKeggReactantIds().contains("C00009") && modelData.getKeggReactantIds().contains("C00009")) {
									symportPhosphateReactions.add(equn);
									//System.out.println(equn.equationAbbreviations);
									// na1 symport
								} else if (modelData.getKeggReactantIds().size() == 2 && modelData.getKeggProductIds().size() == 2 &&
										modelData.getKeggReactantIds().contains("C01330") && modelData.getKeggReactantIds().contains("C01330")) {
									symportSodiumReactions.add(equn);
									//System.out.println(equn.equationAbbreviations);
								} else {
									symportReactions.add(equn);
									//System.out.println(equn.equationAbbreviations);
								}
							}
							// antiport reactions
						} else if (equn.getCompartmentReactantsList().size() == 2 && equn.getCompartmentProductsList().size() == 2) {
							//System.out.println(equn.equationAbbreviations);
							// proton antiport
							if (modelData.getKeggReactantIds().size() == 2 && modelData.getKeggProductIds().size() == 2 &&
									modelData.getKeggReactantIds().contains("C00080") && modelData.getKeggReactantIds().contains("C00080")) {
								antiportProtonReactions.add(equn);
								//System.out.println(equn.equationAbbreviations);
							// pi antiport
							} else if (modelData.getKeggReactantIds().size() == 2 && modelData.getKeggProductIds().size() == 2 &&
									modelData.getKeggReactantIds().contains("C00009") && modelData.getKeggReactantIds().contains("C00009")) {
								antiportPhosphateReactions.add(equn);
								//System.out.println(equn.equationAbbreviations);
							// na1 antiport
							} else if (modelData.getKeggReactantIds().size() == 2 && modelData.getKeggProductIds().size() == 2 &&
									modelData.getKeggReactantIds().contains("C01330") && modelData.getKeggReactantIds().contains("C01330")) {
								antiportSodiumReactions.add(equn);
								//System.out.println(equn.equationAbbreviations);
							} else {
								antiportReactions.add(equn);
								//System.out.println(equn.equationAbbreviations);
							}
						} else {
							// get Phosphotransferase System (PTS) reactions
							if ((modelData.getKeggReactantIds().contains("C00074") && modelData.getKeggProductIds().contains("C00022")) ||
									modelData.getKeggProductIds().contains("C00074") && modelData.getKeggReactantIds().contains("C00022")) {
								ptsReactions.add(equn);
								//System.out.println(equn.equationAbbreviations);
							// get ABC transporters (ATP/ADP)
							} else if ((modelData.getKeggReactantIds().contains("C00002") && modelData.getKeggProductIds().contains("C00008")) ||
									modelData.getKeggProductIds().contains("C00002") && modelData.getKeggReactantIds().contains("C00008")) {
								abcReactions.add(equn);
								//System.out.println(equn.equationAbbreviations);
							} else {
								otherTransportReactions.add(equn);
								otherTransportReactions.add(equn);
								//System.out.println(equn.equationAbbreviations);								
							}
						}
					}
				}
				removeTransportReactionsFromUnplottedList(transportRxns);
			} else if (LocalConfig.getInstance().getListOfCompartmentLists().get(i).size() > 2) {
				if (LocalConfig.getInstance().getListOfCompartmentLists().get(i).size() == 3) {
					Vector<SBMLReaction> transportRxns = rf.getTransportReactionsByThreeCompartments(LocalConfig.getInstance().getListOfCompartmentLists().get(i).get(0), 
							LocalConfig.getInstance().getListOfCompartmentLists().get(i).get(1), LocalConfig.getInstance().getListOfCompartmentLists().get(i).get(2));
					for (int j = 0; j < transportRxns.size(); j++) {
						SBMLReactionEquation equn = (SBMLReactionEquation) LocalConfig.getInstance().getReactionEquationMap().get(transportRxns.get(j).getId());
//						if (LocalConfig.getInstance().getModelKeggEquationMap().containsKey(Integer.toString(transportRxns.get(j).getId()))) {
//							PathwayReactionData modelData = LocalConfig.getInstance().getModelKeggEquationMap().get(Integer.toString(transportRxns.get(j).getId()));
//						}
						//System.out.println(equn.equationAbbreviations);
					}
				} else {
					// it is doubtful that there will be a reaction with four compartments
					System.out.println(LocalConfig.getInstance().getListOfCompartmentLists().get(i));
				}
			}
			trbc.setDiffusionReactions(diffusionReactions);
			trbc.setSymportProtonReactions(symportProtonReactions);
			trbc.setSymportPhosphateReactions(symportPhosphateReactions);
			trbc.setSymportSodiumReactions(symportSodiumReactions);
			trbc.setSymportReactions(symportReactions);
			trbc.setAntiportProtonReactions(antiportProtonReactions);
			trbc.setAntiportPhosphateReactions(antiportPhosphateReactions);
			trbc.setAntiportSodiumReactions(antiportSodiumReactions);
			trbc.setAntiportReactions(antiportReactions);
			trbc.setPtsReactions(ptsReactions);
			trbc.setAbcReactions(abcReactions);
			trbc.setOtherTransportReactions(otherTransportReactions);
			transportReactionsByCompartmentsList.add(trbc);
		}
		LocalConfig.getInstance().setNoTransportReactionIds(LocalConfig.getInstance().getUnplottedReactionIds());
		LocalConfig.getInstance().setTransportReactionsByCompartmentsList(transportReactionsByCompartmentsList);
//		if (LocalConfig.getInstance().getSelectedCompartmentName() != null &&
//				LocalConfig.getInstance().getSelectedCompartmentName().length() > 0 &&
//				LocalConfig.getInstance().getOutsideName() != null &&
//				LocalConfig.getInstance().getOutsideName().length() > 0) {
//			compartmentOutsideRxns = rf.getTransportReactionsByCompartments(LocalConfig.getInstance().getSelectedCompartmentName(), 
//					LocalConfig.getInstance().getOutsideName());
//			//System.out.println(compartmentOutsideRxns);
//			removeTransportReactionsFromUnplottedList(compartmentOutsideRxns);
//			if (LocalConfig.getInstance().getMembraneName() != null &&
//					LocalConfig.getInstance().getMembraneName().length() > 0) {
//				compartmentMembraneRxns = rf.getTransportReactionsByCompartments(LocalConfig.getInstance().getSelectedCompartmentName(), 
//						LocalConfig.getInstance().getMembraneName());
//				removeTransportReactionsFromUnplottedList(compartmentMembraneRxns);
//				membraneOutsideRxns = rf.getTransportReactionsByCompartments(LocalConfig.getInstance().getMembraneName(), 
//						LocalConfig.getInstance().getOutsideName());
//				removeTransportReactionsFromUnplottedList(membraneOutsideRxns);
//			}
//			LocalConfig.getInstance().setNoTransportReactionIds(LocalConfig.getInstance().getUnplottedReactionIds());
//		}
	}
	
	public void createUnplottedReactionsList() {
		ArrayList<Object> unplottedReactions = new ArrayList<Object>(LocalConfig.getInstance().getReactionEquationMap().keySet());
		ArrayList<Integer> unplottedReactionIds = new ArrayList<Integer>();
		for (int i = 0; i < unplottedReactions.size(); i++) {
			unplottedReactionIds.add((int) unplottedReactions.get(i));
		}
		LocalConfig.getInstance().setUnplottedReactionIds(unplottedReactionIds);
	}
	
	public void removeTransportReactionsFromUnplottedList(Vector<SBMLReaction> rxns) {
//		System.out.println(LocalConfig.getInstance().getUnplottedReactionIds());
//		System.out.println(rxns);
		ArrayList<Integer> rxnIds = new ArrayList<Integer>();
		for (int i = 0; i < rxns.size(); i++) {
			rxnIds.add(rxns.get(i).getId());
		}
		// faster to make new list than remove from old list
		ArrayList<Integer> unplottedIds = new ArrayList<Integer>();
		for (int n = 0; n < LocalConfig.getInstance().getUnplottedReactionIds().size(); n++) {
			if (!rxnIds.contains(LocalConfig.getInstance().getUnplottedReactionIds().get(n))) {
				unplottedIds.add(LocalConfig.getInstance().getUnplottedReactionIds().get(n));
			}
		}
		LocalConfig.getInstance().setUnplottedReactionIds(unplottedIds);
		//System.out.println("no trans " + LocalConfig.getInstance().getUnplottedReactionIds());
	}
	
//	public void createTransportCollectionFromList(ArrayList<Integer> idsList, Map<Integer, SBMLReaction> idReactionMap) {
//		for (int n = 0; n < idsList.size(); n++) {
//			transportCollectionsCreator.createTransportCollections(idsList.get(n), 
//					idReactionMap, TransportReactionConstants.PERIPLASM_EXTRAORGANISM_TRANSPORT);
//			if (LocalConfig.getInstance().getUnplottedReactionIds().contains(idsList.get(n))) {
//				LocalConfig.getInstance().getUnplottedReactionIds().remove(LocalConfig.getInstance().getUnplottedReactionIds().indexOf(idsList.get(n)));
//			}
//		}
//	}
}
