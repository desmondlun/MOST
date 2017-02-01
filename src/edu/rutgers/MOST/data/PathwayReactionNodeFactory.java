package edu.rutgers.MOST.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.presentation.GraphicalInterface;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;
import edu.rutgers.MOST.presentation.PathwaysFrameConstants;
import edu.rutgers.MOST.presentation.Utilities;

public class PathwayReactionNodeFactory {

	/**
	 * Creates PathwayReactionNode for a given compartment from list of EC Numbers.
	 * If no compartment specified, node created for entire list of EC Numbers.
	 * SBMLReactions and parameters from enzyme.dat attributes set. 
	 * @param ec
	 * @param compartment
	 * @return
	 */
	
	Utilities util = new Utilities();
	
	private Map<String, ArrayList<String>> renameMetabolitesMap = new HashMap<String, ArrayList<String>>();

	public Map<String, ArrayList<String>> getRenameMetabolitesMap() {
		return renameMetabolitesMap;
	}

	public void setRenameMetabolitesMap(
			Map<String, ArrayList<String>> renameMetabolitesMap) {
		this.renameMetabolitesMap = renameMetabolitesMap;
	}
	
	public ArrayList<Integer> plottedIds = new ArrayList<Integer>();

	public PathwayReactionNode createPathwayReactionNode(PathwayReactionData prd, 
			String compartment, int component, 
			Vector<SBMLReaction> allReactions, Map<Integer, SBMLReaction> idReactionMap) {
		PathwayReactionNode pn = new PathwayReactionNode();
		ArrayList<SBMLReaction> reactions = new ArrayList<SBMLReaction>();
		
		for (int m = 0; m < prd.getEcNumbers().size(); m++) {
			if (LocalConfig.getInstance().getEcNumberReactionMap().containsKey(prd.getEcNumbers().get(m))) {
				// attributes from SBML Reaction
				ArrayList<SBMLReaction> reac = LocalConfig.getInstance().getEcNumberReactionMap().get(prd.getEcNumbers().get(m));
				addReactions(reactions, reac, compartment, prd, false);
			} 
		}
		for (int n = 0; n < prd.getKeggReactionIds().size(); n++) {
			if (LocalConfig.getInstance().getKeggIdReactionMap() != null && 
					LocalConfig.getInstance().getKeggIdReactionMap().containsKey(prd.getKeggReactionIds().get(n))) {
				ArrayList<SBMLReaction> reac = LocalConfig.getInstance().getKeggIdReactionMap().get(prd.getKeggReactionIds().get(n));
				addReactions(reactions, reac, compartment, prd, false);
			}
		}
		for (int i = 0; i < LocalConfig.getInstance().getNoIdentifierIds().size(); i++) {
			SBMLReaction r = idReactionMap.get(LocalConfig.getInstance().getNoIdentifierIds().get(i));
			ArrayList<SBMLReaction> reac = new ArrayList<SBMLReaction>();
			reac.add(r);
			addReactions(reactions, reac, compartment, prd, true);
		}
		for (int u = 0; u < LocalConfig.getInstance().getUnplottedReactionIds().size(); u++) {
			SBMLReaction r = idReactionMap.get(LocalConfig.getInstance().getUnplottedReactionIds().get(u));
			ArrayList<SBMLReaction> reac = new ArrayList<SBMLReaction>();
			reac.add(r);
			addReactions(reactions, reac, compartment, prd, true);
		}
		if (reactions.size() > 0) {
			setReversibilityAndDirection(prd, pn, reactions);
		}
		pn.setDataId(prd.getReactionId());
		pn.setReactions(reactions);
		
		for (int i = 0; i < prd.getReactantIds().size(); i++) {
			if (LocalConfig.getInstance().getMetaboliteIdDataMap().containsKey(prd.getReactantIds().get(i))) {
				PathwayMetaboliteData pmd = LocalConfig.getInstance().getMetaboliteIdDataMap().get(prd.getReactantIds().get(i));
				if (pmd.getType().equals(PathwaysCSVFileConstants.SIDE_METABOLITE_TYPE)) {
					if (LocalConfig.getInstance().getAlternateMetabolitesMap().containsKey(pmd.getKeggId())) {
						if (reactions.size() > 0) {
							updateAlternateMetabNodes(reactions, pmd);
						}
					}
				}
			}
		}
		
		for (int i = 0; i < prd.getProductIds().size(); i++) {
			if (LocalConfig.getInstance().getMetaboliteIdDataMap().containsKey(prd.getProductIds().get(i))) {
				PathwayMetaboliteData pmd = LocalConfig.getInstance().getMetaboliteIdDataMap().get(prd.getProductIds().get(i));
				if (pmd.getType().equals(PathwaysCSVFileConstants.SIDE_METABOLITE_TYPE)) {
					if (LocalConfig.getInstance().getAlternateMetabolitesMap().containsKey(pmd.getKeggId())) {
						if (reactions.size() > 0) {
							updateAlternateMetabNodes(reactions, pmd);
						}
					}
				}
			}
		}
		
		return pn;
	}
	
	
	
	// TODO: check if compartment is redundant since reactions from reaction factory are already
	// given by compartment
	public void addReactions(ArrayList<SBMLReaction> reactions, ArrayList<SBMLReaction> reac, String compartment, 
			PathwayReactionData prd, boolean exactMatch) {
		for (int r = 0; r < reac.size(); r++) {
			if (compartment != null && compartment.length() > 0) {
				if (reac.get(r) != null) {
					SBMLReactionEquation equn = (SBMLReactionEquation) LocalConfig.getInstance().getReactionEquationMap().get(reac.get(r).getId());
					if (equn.getCompartmentList().size() == 1 && equn.getCompartmentList().contains(compartment)) {
						if (addReactionIfNotPresent(reactions, reac.get(r), prd, exactMatch)) {
							// can't remove ids from unplotted ids, reactions skipped. instead, save
							// ids and remove after creating all nodes
							plottedIds.add(reac.get(r).getId());
						}
					} 
				}
			} 
		}
	}
	
	/**
	 * Contains not working for adding SBMLReactions to list
	 * @param reac
	 * @param r
	 */
	public boolean addReactionIfNotPresent(ArrayList<SBMLReaction> reactions, SBMLReaction r,
			PathwayReactionData prd, boolean exactMatch) {
		boolean match = false;
		int id = r.getId();
		ArrayList<Integer> idList = new ArrayList<Integer>();
		for (int i = 0; i < reactions.size(); i++) {
			idList.add(reactions.get(i).getId());
		}
		if (!idList.contains(id)) {
			if (correctMainSpecies(r, prd, exactMatch)) {
				reactions.add(r);
				match = true;
			} else {
				if (!LocalConfig.getInstance().getUnplottedReactionIds().contains(id)) {
					LocalConfig.getInstance().getUnplottedReactionIds().add(id);
				}
			}
		}
		return match;
	}
	
	/**
	 * Return true if all KEGG Ids of metabolite nodes in a reaction are present, else false
	 * @param r
	 * @param keggReactantIds
	 * @param keggProductIds
	 * @return
	 */
	public boolean correctMainSpecies(SBMLReaction r, PathwayReactionData prd, 
			boolean exactMatch) {
		boolean match = false;
		if (r != null && LocalConfig.getInstance().getModelKeggEquationMap().containsKey(Integer.toString(r.getId()))) {
			PathwayReactionData modelData = LocalConfig.getInstance().getModelKeggEquationMap().get(Integer.toString(r.getId()));
			if (exactMatch) {
				if (speciesExactMatch(prd.getKeggReactantIds(), prd.getKeggReactantIdsDataMap(), modelData.getKeggReactantIds()) && 
						speciesExactMatch(prd.getKeggProductIds(), prd.getKeggProductIdsDataMap(), modelData.getKeggProductIds())) {
					doesSetDirectionMatchDirection(prd, PathwaysCSVFileConstants.FORWARD_DIRECTION);
					prd.setDirection(PathwaysCSVFileConstants.FORWARD_DIRECTION);
					match = true;
				} else if (speciesExactMatch(prd.getKeggReactantIds(), prd.getKeggReactantIdsDataMap(), modelData.getKeggProductIds()) && 
						speciesExactMatch(prd.getKeggProductIds(), prd.getKeggProductIdsDataMap(), modelData.getKeggReactantIds())) {
					doesSetDirectionMatchDirection(prd, PathwaysCSVFileConstants.REVERSE_DIRECTION);
					prd.setDirection(PathwaysCSVFileConstants.REVERSE_DIRECTION);
					match = true;
				}
			} else {
				if (speciesMatch(prd.getKeggReactantIds(), prd.getKeggReactantIdsDataMap(), modelData.getKeggReactantIds()) && 
						speciesMatch(prd.getKeggProductIds(), prd.getKeggProductIdsDataMap(), modelData.getKeggProductIds())) {
					doesSetDirectionMatchDirection(prd, PathwaysCSVFileConstants.FORWARD_DIRECTION);
					prd.setDirection(PathwaysCSVFileConstants.FORWARD_DIRECTION);
					match = true;
				} else if (speciesMatch(prd.getKeggReactantIds(), prd.getKeggReactantIdsDataMap(), modelData.getKeggProductIds()) && 
						speciesMatch(prd.getKeggProductIds(), prd.getKeggProductIdsDataMap(), modelData.getKeggReactantIds())) {
					doesSetDirectionMatchDirection(prd, PathwaysCSVFileConstants.REVERSE_DIRECTION);
					prd.setDirection(PathwaysCSVFileConstants.REVERSE_DIRECTION);
					match = true;
				}
			}
		}
		
		return match;
		
	}
	
	/**
	 * If the same reaction list contains reactions in different directions, 
	 * set DirectionsMatch to false
	 * @param prd
	 * @param direction
	 */
	private void doesSetDirectionMatchDirection(PathwayReactionData prd, String direction) {
		if (prd.getDirection() != null) {
			if (!prd.getDirection().equals(direction)) {
				prd.setDirectionsMatch(false);
			}
		}
	}
	
	/**
	 * Checks if all KEGG Ids from data are present in KEGG Id list from model. 
	 * If any species is in alternatives or substitutions, speciesExactMatch
	 * is used.
	 * @param dataIds
	 * @param keggIdsDataMap
	 * @param modelIds
	 * @return
	 */
	public boolean speciesMatch(ArrayList<String> dataIds, Map<String, PathwayMetaboliteData> keggIdsDataMap, ArrayList<String> modelIds) {
//		if ((dataIds.contains("C00013") && (modelIds.contains("C00002") || modelIds.contains("C00044"))) ||
//			((dataIds.contains("C00536") && (modelIds.contains("C00008") || modelIds.contains("C00035"))))) {
//			System.out.println("data " + dataIds);
//			System.out.println("model " + modelIds);
//		}
//		System.out.println("d " + dataIds);
//		System.out.println("m " + modelIds);
		boolean speciesMatch = true;
		boolean containsProton = false;
		if (modelIds.contains("C00080")) {
			containsProton = true;
		}
		ArrayList<String> data = dataIds;
		for (int i = 0; i < data.size(); i++) {
			if (!modelIds.contains(data.get(i))) {
				speciesMatch = false;
				if (!speciesMatch) {
					break;
				} 
			}
		}
		if (speciesMatch) {
			updateRenameMetabolitesMapFromModelIds(keggIdsDataMap, modelIds, containsProton);
		} else {
			if (speciesExactMatch(dataIds, keggIdsDataMap, modelIds)) {
				speciesMatch = true;
			}
		}
		
		return speciesMatch;
	}
	
	/**
	 * Checks for exact match from data are present in KEGG Id list from model.
	 * Some species removed before check.
	 * @param dataIds
	 * @param modelIds
	 * @return
	 */
	public boolean speciesExactMatch(ArrayList<String> dataIds, Map<String, PathwayMetaboliteData> keggIdsDataMap, ArrayList<String> modelIds) {
		boolean speciesMatch = false;
		boolean containsProton = false;
//		System.out.println("data " + dataIds);
//		System.out.println("model " + modelIds);
		if (modelIds.contains("C00080")) {
			containsProton = true;
		}
		ArrayList<String> ignoreItems = new ArrayList<String>();
		//if (LocalConfig.getInstance().isIgnoreProtonSelected()) {
		ignoreItems.add("C00080");
		//}
		if (LocalConfig.getInstance().isIgnoreWaterSelected()) {
			ignoreItems.add("C00001");
		}
		ArrayList<String> data = removedSpeciesBeforeComparison(ignoreItems, dataIds);
		//System.out.println("d " + data);
		ArrayList<String> model = removedSpeciesBeforeComparison(ignoreItems, modelIds);
		//System.out.println("m " + model);
		if (data.size() != model.size()) {
			return false;
		} 
		Collections.sort(data);
		Collections.sort(model);
		if (data.equals(model)) {
			updateRenameMetabolitesMapFromModelIds(keggIdsDataMap, model, containsProton);
			speciesMatch = true;
		} else {
			ArrayList<String> data1 = new ArrayList<String>();
			ArrayList<String> model1 = new ArrayList<String>();
			// copy lists to avoid altering original lists for future comparison
			for (int i = 0; i < data.size(); i++) {
				data1.add(data.get(i));
			}
			for (int i = 0; i < model.size(); i++) {
				model1.add(model.get(i));
			}
			// remove all common entries
			ArrayList<String> itemsToRemove = new ArrayList<String>();
			for (int i = 0; i < data1.size(); i++) {
				if (model1.contains(data1.get(i))) {
					itemsToRemove.add(data1.get(i));
				}
			}
			for (int i = 0; i < itemsToRemove.size(); i++) {
				String ki = itemsToRemove.get(i);
				model1.remove(model1.indexOf(ki));
				data1.remove(data1.indexOf(ki));
				// add any removed keys in alternate or substitution to map
//				if (LocalConfig.getInstance().getAlternateMetabolitesMap().containsKey(ki) ||
//						LocalConfig.getInstance().getMetaboliteSubstitutionsMap().containsKey(ki)) {
				if (LocalConfig.getInstance().getMetaboliteSubstitutionsMap().containsKey(ki)) {
					if (keggIdsDataMap.containsKey(ki)) {
						String name = keggIdsDataMap.get(ki).getName();
						updateRenameMetabolitesMap(name, ki, containsProton);
					} 
				}
			}

			// may not be necessary, should already be sorted
			Collections.sort(data1);
			Collections.sort(model1);
			ArrayList<String> model2 = new ArrayList<String>();
			// data1 and model1 should be same size. replace substitutions and alternates (if side
			// species) with keys to enforce A -> B model where if B is present it is considered
			// to be a replacement for A. Substitutions can be main nodes or side nodes, alternates 
			// can only be side nodes. This prevents NAD, ATP main nodes from having multiple 
			// entries, but fatty acids can have multiple entries.
			Map<String, String> nameReplacedId = new HashMap<String, String>();
			for (int i = 0; i < data1.size(); i++) {
				String entry = model1.get(i);
				if (LocalConfig.getInstance().getMetaboliteSubstitutionsMap().containsKey(data1.get(i))) {
					for (int j = 0; j < model1.size(); j++) {
						if (LocalConfig.getInstance().getMetaboliteSubstitutionsMap().get(data1.get(i)).contains(model1.get(j))) {
							if (keggIdsDataMap.containsKey(data1.get(i))) {
								// replace substitution with key
								entry = data1.get(i);
								nameReplacedId.put(keggIdsDataMap.get(data1.get(i)).getName(), model1.get(j));
							}
						} 
					}
				} else if (keggIdsDataMap.get(data1.get(i)).getType().equals(PathwaysCSVFileConstants.SIDE_METABOLITE_TYPE) &&
						LocalConfig.getInstance().getAlternateMetabolitesMap().containsKey(data1.get(i))) {
					for (int j = 0; j < model1.size(); j++) {
						if (LocalConfig.getInstance().getAlternateMetabolitesMap().get(data1.get(i)).contains(model1.get(j))) {
							if (keggIdsDataMap.containsKey(data1.get(i))) {
								// replace alternate with key
								entry = data1.get(i);
							}
						} 
					}
				}
				model2.add(entry);
			}
			Collections.sort(model2);
			if (data1.equals(model2)) {
				speciesMatch = true;
				ArrayList<String> names = new ArrayList<String>(nameReplacedId.keySet());
				for (int i = 0; i < names.size(); i++) {
					updateRenameMetabolitesMap(names.get(i), nameReplacedId.get(names.get(i)), containsProton);
				}
			}
		}
		
		return speciesMatch;
	}
	
	public void updateRenameMetabolitesMap(String name, String keggId, boolean containsProton) {
		if (renameMetabolitesMap.containsKey(name)) {
			ArrayList<String> keggIds = renameMetabolitesMap.get(name);
			updateKeggIdList(name, keggId, keggIds, containsProton);
		} else {
			ArrayList<String> keggIds = new ArrayList<String>();
			updateKeggIdList(name, keggId, keggIds, containsProton);
		}
	}
	
	public void updateRenameMetabolitesMapFromModelIds(Map<String, PathwayMetaboliteData> keggIdsDataMap, 
		ArrayList<String> modelIds, boolean containsProton) {
		for (int j = 0; j < modelIds.size(); j++) {
//			if (LocalConfig.getInstance().getAlternateMetabolitesMap().containsKey(modelIds.get(j)) ||
//					LocalConfig.getInstance().getMetaboliteSubstitutionsMap().containsKey(modelIds.get(j))) {
			if (LocalConfig.getInstance().getMetaboliteSubstitutionsMap().containsKey(modelIds.get(j))) {
				if (keggIdsDataMap.containsKey(modelIds.get(j))) {
					String name = keggIdsDataMap.get(modelIds.get(j)).getName();
					updateRenameMetabolitesMap(name, modelIds.get(j), containsProton);
				} 
			}
		}
	}
	
	public void updateKeggIdList(String name, String keggId, ArrayList<String> keggIds, boolean containsProton) {
		if (!keggIds.contains(keggId)) {
			keggIds.add(keggId);
			if (containsProton && maybeAddProton(keggId) && !keggIds.contains("C00080")) {
				keggIds.add("C00080");
				//System.out.println(keggIds);
			}
			renameMetabolitesMap.put(name, keggIds);
		}
	}
	
	public ArrayList<String> removedSpeciesBeforeComparison(ArrayList<String> removeList, ArrayList<String> list) {
		ArrayList<String> list2 = new ArrayList<String>();
		// make a new list since removing items modifies original list
		for (int i = 0; i < list.size(); i++) {
			if (!removeList.contains(list.get(i))) {
				list2.add(list.get(i));
			}
		}
		return list2;
	}
	
	public boolean maybeAddProton(String keggId) {
		//System.out.println(keggId);
		for (int i = 0; i < VisualizationConstants.REDUCED_SPECIES_WITH_PROTON.length; i++) {
			if (VisualizationConstants.REDUCED_SPECIES_WITH_PROTON[i].equals(keggId)) {
				return true;
			} else if (LocalConfig.getInstance().getMetaboliteSubstitutionsMap().get("C00030").contains(keggId) ||
					LocalConfig.getInstance().getMetaboliteSubstitutionsMap().get("C00028").contains(keggId)) {
				return true;
			}
		}
		return false;
		
	}
	
	/**
	 * Reversible obtained from model for each item in list of reactions. 
	 * If all reactions are irreversible and directions match, set reaction
	 * as irreversible. Else set reaction as reversible.
	 * For reversible reactions, if all directions match set direction,
	 * else, set direction as forward.
	 * @param prd
	 * @param pn
	 * @param reactions
	 */
	private void setReversibilityAndDirection(PathwayReactionData prd, PathwayReactionNode pn, 
			ArrayList<SBMLReaction> reactions) {
		String reversible = GraphicalInterfaceConstants.BOOLEAN_VALUES[0];
		ArrayList<String> directions = new ArrayList<String>();
		for (int j = 0; j < reactions.size(); j++) {
			// get directions set in correctMainSpecies method
			if (!directions.contains(prd.getDirection())) {
				directions.add(prd.getDirection());
			}
			if (reactions.get(j).getReversible().equals(GraphicalInterfaceConstants.BOOLEAN_VALUES[1])) {
				reversible = GraphicalInterfaceConstants.BOOLEAN_VALUES[1];
			}
		}
		// if reaction is not reversible and all directions are not the same
		// set reaction as irreversible, else keep irreversible
		if (reversible.equals(GraphicalInterfaceConstants.BOOLEAN_VALUES[0])) {
			if (directions.size() != 1) {
				reversible = GraphicalInterfaceConstants.BOOLEAN_VALUES[1];
			} 
		}
		if (directions.size() == 1) {
			pn.setDirection(directions.get(0));
			if (!prd.isDirectionsMatch()) {
				reversible = GraphicalInterfaceConstants.BOOLEAN_VALUES[1];
			}
		} else {
			pn.setDirection(PathwaysCSVFileConstants.FORWARD_DIRECTION);
		}
		pn.setReversible(reversible);
		pn.setReactions(reactions);
	}
	
	/** 
	 * Display name for tooltip created from input names if length of list of SBMLReactions = 0.
	 * Else display name created from SBMLReaction attributes.  
	 * @param displayName
	 * @param name
	 * @param reactions
	 * @return
	 */
	public String createDisplayName(String displayName, String name, String id, ArrayList<SBMLReaction> reactions, Map<Integer, SBMLReaction> idReactionMap) {
		ArrayList<String> reactionAbbrevations = new ArrayList<String>();
		ArrayList<String> reactionNames = new ArrayList<String>();
		ArrayList<String> ecNumbers = new ArrayList<String>();
		ArrayList<String> keggReactionIds = new ArrayList<String>();
		ArrayList<String> equations = new ArrayList<String>();
		ArrayList<String> equationNames = new ArrayList<String>();
		ArrayList<String> subsystems = new ArrayList<String>();
		ArrayList<Double> fluxes = new ArrayList<Double>();
		if (reactions.size() > 0) {
			for (int i = 0; i < reactions.size(); i++) {
				if (reactions.get(i) != null) {
					if (!reactionAbbrevations.contains(reactions.get(i).getReactionAbbreviation())) {
						reactionAbbrevations.add(reactions.get(i).getReactionAbbreviation());
					}
					if (!reactionNames.contains(reactions.get(i).getReactionName())) {
						reactionNames.add(reactions.get(i).getReactionName());
					}
					if (reactions.get(i).getEcNumber() != null && reactions.get(i).getEcNumber().length() > 0 && !ecNumbers.contains(reactions.get(i).getEcNumber())) {
						ecNumbers.add(reactions.get(i).getEcNumber());
					}
					// since ec number reaction map made before kegg reaction ids assigned, these are
					// null in ec list and need to be obtained from map made more recently
					if (reactions.get(i) != null && idReactionMap.containsKey(reactions.get(i).getId())) {
						String keggReactionId = idReactionMap.get(reactions.get(i).getId()).getKeggReactionId();
						if (keggReactionId != null && keggReactionId.length() > 0) {
							if (!keggReactionIds.contains(keggReactionId)) {
								keggReactionIds.add(keggReactionId);
							}
						}
					}
					if (!equations.contains(reactions.get(i).getReactionEqunAbbr())) {
						String htmlEquation = reactions.get(i).getReactionEqunAbbr().replace("<", "&lt;");
						equations.add(htmlEquation);
					}
					if (!equationNames.contains(reactions.get(i).getReactionEqunNames())) {
						String htmlEquation = reactions.get(i).getReactionEqunNames().replace("<", "&lt;");
						equationNames.add(htmlEquation);
					}
					if (!subsystems.contains(reactions.get(i).getSubsystem())) {
						subsystems.add(reactions.get(i).getSubsystem());
					}
//					if (!fluxes.contains(reactions.get(i).getFluxValue())) {
						fluxes.add(reactions.get(i).getFluxValue());
//					}
					//System.out.println("flux " + reactions.get(i).getFluxValue() + " log " + Math.log10(Math.abs(reactions.get(i).getFluxValue())));
				}
			}
			displayName = "<html>" + displayName(reactionAbbrevations)
					+ displayReactionName(reactionNames)
					+ displayReactionAbbreviation(reactionAbbrevations)
					+ displayECNumber(ecNumbers)
					+ displayKeggReactionId(keggReactionIds)
					// equation from database is not necessary if reaction found in model
					//+ "<p>Equation: " + name
					+ displaySubsystem(subsystems)
					+ displayModelEquation(equations, "<p>Equation(s) (Abbreviations) from Model: ")
					+ displayModelEquation(equationNames, "<p>Equation(s) (Names) from Model: ")
					+ "<p>Fluxes: " + fluxes.toString()
					+ "<p>" + PathwaysFrameConstants.REACTIONS_DB_ID_HEADING + id;
		}
		return displayName;
	}
	
	public String displayName(ArrayList<String> reactionNames) {
		String rn = "";
		if (reactionNames.size() > 0) {
			rn = reactionNames.get(0);
		}
		if (reactionNames.size() > 1) {
			rn = reactionNames.toString();
		}
		return rn;
	}
	
	public String displayReactionAbbreviation(ArrayList<String> reactionAbbrevations) {
		return util.maybeMakeList(reactionAbbrevations, "Reaction Abbreviation");
	}
	
	public String displayECNumber(ArrayList<String> ecnumbers) {
		return util.maybeMakeList(ecnumbers, "EC Number");
	}
	
	public String displayKeggReactionId(ArrayList<String> keggReactionIds) {
		return util.maybeMakeList(keggReactionIds, "KEGG Reaction ID");
	}
	
	public String displaySubsystem(ArrayList<String> subsystems) {
		return util.maybeMakeList(subsystems, "Subsystem");
	}
	
	public String displayReactionName(ArrayList<String> names) {
		// since equations can be quite long and a list of reactions may not fit on screen,
		// each reaction is put on a separate line
		String reactionNameString = "";
		if (names.size() > 0) {
			reactionNameString = "<p>Reaction Name: " + names.get(0);
		}
		if (names.size() > 1) {
			reactionNameString = "<p>Reaction Names: " + names.get(0);
			for (int m = 1; m < names.size(); m++) {
				reactionNameString += ", <p>" + names.get(m);
			}
		}
		return reactionNameString;
	}
	
	public String displayModelEquation(ArrayList<String> equations, String description) {
		// since equations can be quite long and a list of reactions may not fit on screen,
		// each reaction is put on a separate line
		String modelEquationString = "";
		if (equations.size() > 0) {
			modelEquationString = description + equations.get(0);
		}
		if (equations.size() > 1) {
			modelEquationString = description + equations.get(0);
			for (int m = 1; m < equations.size(); m++) {
				modelEquationString += ", <p>" + equations.get(m);
			}
		}
		return modelEquationString;
	}
	
	public String reversibleString(String reversibleValue) {
		String reversible = "";
		if (reversibleValue.equals("0")) {
			reversible = "false";
		} else if (reversibleValue.equals("1")) {
			reversible = "true";
		}
		return reversible;
	}
	
	public void updateAlternateMetabNodes(ArrayList<SBMLReaction> reactions, PathwayMetaboliteData pmd) {
		boolean containsProton = false;
		for (int j = 0; j < reactions.size(); j++) {
			SBMLReactionEquation equn = ((SBMLReactionEquation) LocalConfig.getInstance().getReactionEquationMap().get(reactions.get(j).getId()));
			for (int r = 0; r < equn.getReactants().size(); r++) {
				String keggId = LocalConfig.getInstance().getMetaboliteIdKeggIdMap().get(Integer.toString(equn.getReactants().get(r).getMetaboliteId()));
				if (keggId.equals("C00080")) {
					containsProton = true;
				}
			}
			for (int r = 0; r < equn.getReactants().size(); r++) {
				String keggId = LocalConfig.getInstance().getMetaboliteIdKeggIdMap().get(Integer.toString(equn.getReactants().get(r).getMetaboliteId()));
				if (LocalConfig.getInstance().getAlternateMetabolitesMap().containsKey(pmd.getKeggId())) {
					updateRenameMetabMapAlternates(pmd, keggId, containsProton);
				}
			}
			for (int p = 0; p < equn.getProducts().size(); p++) {
				String keggId = LocalConfig.getInstance().getMetaboliteIdKeggIdMap().get(Integer.toString(equn.getProducts().get(p).getMetaboliteId()));
				if (keggId.equals("C00080")) {
					containsProton = true;
				}
			}
			for (int p = 0; p < equn.getProducts().size(); p++) {
				String keggId = LocalConfig.getInstance().getMetaboliteIdKeggIdMap().get(Integer.toString(equn.getProducts().get(p).getMetaboliteId()));
				if (LocalConfig.getInstance().getAlternateMetabolitesMap().containsKey(pmd.getKeggId())) {
					updateRenameMetabMapAlternates(pmd, keggId, containsProton);
				}
			}
		}
	}
	
	public void updateRenameMetabMapAlternates(PathwayMetaboliteData pmd, String keggId, boolean containsProton) {
		if (pmd.getKeggId().equals(keggId)) {
//			System.out.println(pmd.getKeggId());
//			System.out.println(pmd.getName());
//			System.out.println(keggId);
//			System.out.println(containsProton);
			updateRenameMetabolitesMap(pmd.getName(), keggId, containsProton);
		}
		if (LocalConfig.getInstance().getAlternateMetabolitesMap().get(pmd.getKeggId()).contains(keggId)) {
//			System.out.println("k " + pmd.getKeggId());
//			System.out.println(pmd.getName());
//			System.out.println(keggId);
//			System.out.println(containsProton);
			updateRenameMetabolitesMap(pmd.getName(), keggId, containsProton);
		}
	}

}
