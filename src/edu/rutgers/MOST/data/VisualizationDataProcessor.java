package edu.rutgers.MOST.data;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.Icon;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;
import edu.rutgers.MOST.presentation.PathwaysFrameConstants;
import edu.rutgers.MOST.presentation.ProgressConstants;
import edu.rutgers.MOST.presentation.Utilities;

public class VisualizationDataProcessor {

	// map with node names and positions
	Map<String, String[]> nodeNamePositionMap = new HashMap<String, String[]>();
	// keyset of node names
	ArrayList<String> nodeNameList = new ArrayList<String>(); 

	// key = name of rxn, value = reactant, product, reversible
	Map<String, String[]> reactionMap = new HashMap<String, String[]>(); 
	// keyset of reactions
	ArrayList<String> reactionList = new ArrayList<String>();

	// lists used to distinguish node types
	ArrayList<String> borderList = new ArrayList<String>();   // compartment border
	ArrayList<String> noBorderList = new ArrayList<String>();   // metabolite node border
	ArrayList<String> pathwayNames = new ArrayList<String>();
	ArrayList<String> fluxRangeNames = new ArrayList<String>();
	ArrayList<String> fluxRangeWidths = new ArrayList<String>();
	ArrayList<String> mainMetabolites = new ArrayList<String>();
	ArrayList<String> smallMainMetabolites = new ArrayList<String>();
	ArrayList<String> sideMetabolites = new ArrayList<String>();
	ArrayList<String> cofactors = new ArrayList<String>();
	ArrayList<String> reactions = new ArrayList<String>();
	Map<String, Double> fluxMap = new HashMap<String, Double>(); 
	Map<String, Double> colorMap = new HashMap<String, Double>();
	ArrayList<String> koReactions = new ArrayList<String>();
	ArrayList<String> foundMetabolitesList = new ArrayList<String>();
	ArrayList<String> foundReactionsList = new ArrayList<String>();
	ArrayList<String> foundPathwayNamesList = new ArrayList<String>();
	Map<String, Icon> iconMap = new HashMap<String, Icon>(); 
	ArrayList<Integer> plottedIds = new ArrayList<Integer>();
	Map<String, String> oldNameNewNameMap = new HashMap<String, String>(); 

	// maps for find - exact match
	HashMap<String, ArrayList<String[]>> metaboliteAbbrPositionsMap = new HashMap<String, ArrayList<String[]>>();
	HashMap<String, ArrayList<String[]>> keggMetaboliteIdPositionsMap = new HashMap<String, ArrayList<String[]>>();
	HashMap<String, ArrayList<String[]>> ecNumberPositionsMap = new HashMap<String, ArrayList<String[]>>();
	HashMap<String, ArrayList<String[]>> keggReactionIdPositionsMap = new HashMap<String, ArrayList<String[]>>();
	HashMap<String, ArrayList<String[]>> reactionAbbrPositionsMap = new HashMap<String, ArrayList<String[]>>();

	ArrayList<Integer> biomassIds = new ArrayList<Integer>();
	
	PathwayReactionNodeFactory prnf = new PathwayReactionNodeFactory();
	PathwayMetaboliteNodeFactory pmnf = new PathwayMetaboliteNodeFactory();
	Utilities util = new Utilities();

	String compartmentLabel = "Model Name: " + LocalConfig.getInstance().getModelName();
	String legendLabel = "Flux Value";

	private Map<String, ArrayList<Double>> startPosMap = new HashMap<String, ArrayList<Double>>();
	private double startX = 2*PathwaysFrameConstants.HORIZONTAL_INCREMENT;
	private double startY = PathwaysFrameConstants.START_Y;
	
	DecimalFormat sciFormatter = PathwaysFrameConstants.SCIENTIFIC_FLUX_FORMATTER;

	public String report = "";
	
	private boolean calvinCycleRequiredReactionIdsFound = false;

	public Map<Integer, SBMLReaction> createCompartmentIdReactionMap(Vector<SBMLReaction> rxns) {
		Map<Integer, SBMLReaction> idReactionMap = new HashMap<Integer, SBMLReaction>();
		for (int i = 0; i < rxns.size(); i++) {
			idReactionMap.put(rxns.get(i).getId(), rxns.get(i));
		}

		return idReactionMap;
	}

	public void processData(int component, Vector<SBMLReaction> rxns) {
		ReactionFactory f = new ReactionFactory("SBML");
		Map<Integer, SBMLReaction> idReactionMapAllReactions = new HashMap<Integer, SBMLReaction>();
		Vector<SBMLReaction> allReactions = f.getAllReactions();
		for (int i = 0; i < allReactions.size(); i++) {
			idReactionMapAllReactions.put(allReactions.get(i).getId(), allReactions.get(i));
		}
		Map<Integer, SBMLReaction> idReactionMap = createCompartmentIdReactionMap(rxns);
		MetabolicPathway pathway = LocalConfig.getInstance().getMetabolicPathways().get("0");
		if (startPosMap.containsKey(pathway.getId())) {
			startX = startPosMap.get(pathway.getId()).get(0);
			startY = startPosMap.get(pathway.getId()).get(1);
		}
		checkForRequiredReactionIDs(pathway, component, rxns, idReactionMap);
		drawMetabolites(pathway, component, LocalConfig.getInstance().getSelectedCompartmentName());
		drawReactions(pathway, component, rxns, idReactionMap);

		// create reports for reactions found and not found
		VisualizationReportGenerator reportGenerator = new VisualizationReportGenerator();
		Collections.sort(plottedIds);
		ArrayList<Integer> missingKeggId = new ArrayList<Integer>();
		ArrayList<Integer> unplottedIds = new ArrayList<Integer>();
		for (int r = 0; r < rxns.size(); r++) {
			if (LocalConfig.getInstance().getReactionsMissingKeggId().contains(rxns.get(r).getId())) {
				if (!LocalConfig.getInstance().getExternalReactionIds().contains(rxns.get(r).getId())) {
					missingKeggId.add(rxns.get(r).getId());
				}
			} else {
				if (!plottedIds.contains(rxns.get(r).getId())) {
					if (!LocalConfig.getInstance().getExternalReactionIds().contains(rxns.get(r).getId())) {
						if (!biomassIds.contains(rxns.get(r).getId())) {
							unplottedIds.add(rxns.get(r).getId());
						}
					}
				}
			}
		}
		Collections.sort(missingKeggId);
		//System.out.println("missing KEGG id " + missingKeggId);
		Collections.sort(unplottedIds);
		//System.out.println("unplotted " + unplottedIds);
		String unplottedTitle = "Unplotted Reactions - Reaction not in Database: ";
		String missingKeggIdTitle = "Unplotted Reactions - Missing KEGG ID(s): ";
		String plottedTitle = "Plotted Reactions: ";
		report += reportGenerator.reportSection(unplottedTitle, idReactionMapAllReactions, unplottedIds);
		report += "\n";
		report += reportGenerator.reportSection(missingKeggIdTitle, idReactionMapAllReactions, missingKeggId);
		report += "\n";
		report += reportGenerator.reportSection(plottedTitle, idReactionMapAllReactions, plottedIds);
		
		drawPathwayNames(component);

		String borderLeftX = Double.toString(PathwaysFrameConstants.HORIZONTAL_INCREMENT*PathwaysFrameConstants.BORDER_LEFT);
		String borderRightX = Double.toString(PathwaysFrameConstants.HORIZONTAL_INCREMENT*PathwaysFrameConstants.BORDER_RIGHT);
		String borderTopY = Double.toString(PathwaysFrameConstants.VERTICAL_INCREMENT*PathwaysFrameConstants.BORDER_TOP);
		String borderBottomY = Double.toString(PathwaysFrameConstants.VERTICAL_INCREMENT*PathwaysFrameConstants.BORDER_BOTTOM);

		// draw cell border
		drawCompartmentBorder(borderLeftX, borderRightX, borderTopY, borderBottomY, 0);

		String compartmentLabelXOffset = Double.toString(
				PathwaysFrameConstants.BORDER_TOP*PathwaysFrameConstants.VERTICAL_INCREMENT + 
				PathwaysFrameConstants.COMPARTMENT_LABEL_NODE_WIDTH/2 + 
				PathwaysFrameConstants.COMPARTMENT_LABEL_TOP_PADDING);
		String compartmentLabelYOffset = Double.toString(
				PathwaysFrameConstants.BORDER_LEFT*PathwaysFrameConstants.HORIZONTAL_INCREMENT + 
				PathwaysFrameConstants.COMPARTMENT_LABEL_NODE_HEIGHT/2 + 
				PathwaysFrameConstants.COMPARTMENT_LABEL_LEFT_PADDING);

		drawCompartmentLabel(compartmentLabel, compartmentLabelXOffset, compartmentLabelYOffset);
		
		// only draw flux legend is there is are non-zero fluxes and edge thicknesses are scaled
		// otherwise legend is meaningless
		if (LocalConfig.getInstance().getSecondaryMaxFlux() > 0 && LocalConfig.getInstance().isScaleEdgeThicknessSelected()) {
			drawLegendLabel(legendLabel, Integer.toString(PathwaysFrameConstants.LEGEND_LABEL_X_POSITION), 
				Integer.toString(PathwaysFrameConstants.LEGEND_LABEL_Y_POSITION));
			
			drawLegend(component);
			String legendBorderLeftX = Double.toString(PathwaysFrameConstants.LEGEND_BORDER_LEFT_X);
			String legendBorderRightX = Double.toString(PathwaysFrameConstants.LEGEND_BORDER_RIGHT_X);
			String legendBorderTopY = Double.toString(PathwaysFrameConstants.LEGEND_BORDER_TOP_Y);
			String legendBorderBottomY = Double.toString(PathwaysFrameConstants.LEGEND_BORDER_BOTTOM_Y);

			// draw legend border
			drawCompartmentBorder(legendBorderLeftX, legendBorderRightX, legendBorderTopY, legendBorderBottomY, 4);
		}

		reactionList = new ArrayList<String>(reactionMap.keySet()); 
		Collections.sort(reactionList);
		LocalConfig.getInstance().setVisualizationsProgress(100);
		
		VisualizationData visualizationData = new VisualizationData();
		visualizationData.setNodeNamePositionMap(nodeNamePositionMap);
		visualizationData.setNodeNameList(nodeNameList);
		visualizationData.setReactionMap(reactionMap);
		visualizationData.setReactionList(reactionList);
		visualizationData.setBorderList(borderList);
		visualizationData.setNoBorderList(noBorderList);
		visualizationData.setPathwayNames(pathwayNames);
		visualizationData.setFluxRangeNames(fluxRangeNames);
		visualizationData.setFluxRangeWidths(fluxRangeWidths);
		visualizationData.setMainMetabolites(mainMetabolites);
		visualizationData.setSmallMainMetabolites(smallMainMetabolites);
		visualizationData.setSideMetabolites(sideMetabolites);
		visualizationData.setCofactors(cofactors);
		visualizationData.setReactions(reactions);
		visualizationData.setFluxMap(fluxMap);
		visualizationData.setColorMap(colorMap);
		visualizationData.setKoReactions(koReactions);
		visualizationData.setFoundMetabolitesList(foundMetabolitesList);
		visualizationData.setFoundReactionsList(foundReactionsList);
		visualizationData.setFoundPathwayNamesList(foundPathwayNamesList);
		visualizationData.setIconMap(iconMap);
		visualizationData.setPlottedIds(plottedIds);
		visualizationData.setOldNameNewNameMap(oldNameNewNameMap);
		visualizationData.setMetaboliteAbbrPositionsMap(metaboliteAbbrPositionsMap);
		visualizationData.setKeggMetaboliteIdPositionsMap(keggMetaboliteIdPositionsMap);
		visualizationData.setEcNumberPositionsMap(ecNumberPositionsMap);
		visualizationData.setKeggReactionIdPositionsMap(keggReactionIdPositionsMap);
		visualizationData.setReactionAbbrPositionsMap(reactionAbbrPositionsMap);
		visualizationData.setReport(report);
		visualizationData.setCompartmentLabel(compartmentLabel);
		visualizationData.setLegendLabel(legendLabel);
		LocalConfig.getInstance().setVisualizationData(visualizationData);
	}
	
	/**
	 * This method allows pathways to be drawn or not drawn based on the presence or absence
	 * of one or more reactions in a list
	 * @param pathway
	 * @param component
	 * @param rxns
	 * @param idReactionMap
	 */
	public void checkForRequiredReactionIDs(MetabolicPathway pathway, int component, Vector<SBMLReaction> rxns, Map<Integer, SBMLReaction> idReactionMap) {
		ArrayList<String> foundIds = new ArrayList<String>();
		for (int k = 0; k < pathway.getReactionsData().size(); k++) {
			if (k%10 == 0) {
				LocalConfig.getInstance().setVisualizationsProgress((k * ProgressConstants.CHECK_FOR_REQUIRED_REACTIONS_LOAD_PERCENT) / pathway.getReactionsData().size()
						+ ProgressConstants.VISUALIZATIONS_LOAD_PERCENT);	
			}
			if (pathway.getComponent() == component) {
				PathwayReactionNode pn = prnf.createPathwayReactionNode(pathway.getReactionsData().get(Integer.toString(k)), 
					LocalConfig.getInstance().getSelectedCompartmentName(), pathway.getComponent(), rxns, 
					idReactionMap);
				// reaction found in model
				if (pn.getReactions().size() > 0) {
					foundIds.add(pn.getDataId());
				}
			}
		}
		boolean found = true;
		for (int j = 0; j < PathwaysFrameConstants.calvinCycleRequiredReactionIDsList.size(); j++) {
			if (!foundIds.contains(PathwaysFrameConstants.calvinCycleRequiredReactionIDsList.get(j))) {
				found = false;
				break;
			}
		}
		if (found) {
			calvinCycleRequiredReactionIdsFound = true;
		}
	}

	public void drawMetabolites(MetabolicPathway pathway, int component, String compartment) {
		//    	LocalConfig.getInstance().setVisualizationsProgress(100);
		for (int j = 0; j < pathway.getMetabolitesData().size(); j++) {
			if (j%10 == 0) {
				LocalConfig.getInstance().setVisualizationsProgress((j * ProgressConstants.VISUALIZATIONS_METABOLITE_LOAD_PERCENT) / pathway.getMetabolitesData().size()
						+ ProgressConstants.VISUALIZATIONS_LOAD_PERCENT + ProgressConstants.CHECK_FOR_REQUIRED_REACTIONS_LOAD_PERCENT);	
				//System.out.println(LocalConfig.getInstance().getVisualizationsProgress());
			}
			if (pathway.getComponent() == component) {
				String metabName = pathway.getMetabolitesData().get(Integer.toString(j)).getName();
				String type = pathway.getMetabolitesData().get(Integer.toString(j)).getType();
				String keggId = pathway.getMetabolitesData().get(Integer.toString(j)).getKeggId();
				boolean drawMetabolite = true;
				String id = pathway.getMetabolitesData().get(Integer.toString(j)).getId();
				ArrayList<String> abbrList = new ArrayList<String>();
				if (LocalConfig.getInstance().getKeggIdMetaboliteMap().containsKey(keggId)) {
					for (int k = 0; k < LocalConfig.getInstance().getKeggIdMetaboliteMap().get(keggId).size(); k++) {
						if (LocalConfig.getInstance().getKeggIdMetaboliteMap().get(keggId).get(k).getCompartment().
								equals(compartment)) {
							//foundMetabolitesList.add(metabName);
							// kegg ids in these maps will be substituted if node is side type
							if (type.equals(PathwaysCSVFileConstants.SIDE_METABOLITE_TYPE) && (LocalConfig.getInstance().getAlternateMetabolitesMap().containsKey(keggId) ||
									LocalConfig.getInstance().getMetaboliteSubstitutionsMap().containsKey(keggId))) {

							} else {
								abbrList.add(LocalConfig.getInstance().getKeggIdMetaboliteMap().get(keggId).get(k).getMetaboliteAbbreviation());
							}
						} 
					}
					//System.out.println(abbrList);
				} else {
					if (!LocalConfig.getInstance().isGraphMissingMetabolitesSelected()) {
						if (LocalConfig.getInstance().getAlternateMetabolitesMap().containsKey(keggId) ||
								LocalConfig.getInstance().getMetaboliteSubstitutionsMap().containsKey(keggId)) {

						} else {
							drawMetabolite = false;
						}
					}
				}
				// Draw everything if graph all in database, else if Always Draw Calvin cycle
				// is not selected, don't draw Calvin cycle unless required reactions are found
				// in the model
				if (!LocalConfig.getInstance().isGraphMissingMetabolitesSelected()) {
					if (!LocalConfig.getInstance().isGraphCalvinCycleSelected() && 
						PathwaysFrameConstants.calvinCycleMetaboliteIDsList.contains(id)) {
						if (!calvinCycleRequiredReactionIdsFound) {
							drawMetabolite = false;
						}
					}
				}
				if (drawMetabolite) {
//					if (pathway.getMetabolitesData().get(Integer.toString(j)).getBorder().equals("0")) {
//						noBorderList.add(pathway.getMetabolitesData().get(Integer.toString(j)).getName());
//					}
					classifyMetabolite(type, metabName, keggId);
					double x = 0;
					double y = 0;
					x = startX + PathwaysFrameConstants.HORIZONTAL_INCREMENT*pathway.getMetabolitesData().get(Integer.toString(j)).getLevel();
					y = startY + PathwaysFrameConstants.VERTICAL_INCREMENT*pathway.getMetabolitesData().get(Integer.toString(j)).getLevelPosition();
					nodeNamePositionMap.put(metabName, new String[] {Double.toString(x), Double.toString(y)});
					for (int i = 0; i < abbrList.size(); i++) {
						updateFindPositionsMap(metaboliteAbbrPositionsMap, abbrList.get(i), 
								new String[] {Double.toString(x), Double.toString(y)});
					}
					updateFindPositionsMap(keggMetaboliteIdPositionsMap, keggId, 
							new String[] {Double.toString(x), Double.toString(y)});
				}
			}
		}
	}
	
	public void drawReactions(MetabolicPathway pathway, int component, Vector<SBMLReaction> rxns, Map<Integer, SBMLReaction> idReactionMap) {
		ArrayList<String> metabPosKeys = new ArrayList<String>(nodeNamePositionMap.keySet());
		double minFlux = Double.parseDouble(util.roundToSignificantFigures(PathwaysFrameConstants.MINIMUM_FLUX_RATIO*LocalConfig.getInstance().getSecondaryMaxFlux(), 
			PathwaysFrameConstants.FLUX_LEGEND_SIGNIFICANT_FIGURES, sciFormatter, PathwaysFrameConstants.MIN_DECIMAL_FORMAT, PathwaysFrameConstants.MAX_DECIMAL_FORMAT));
		double lowerMidFlux = Double.parseDouble(util.roundToSignificantFigures(PathwaysFrameConstants.LOWER_MID_FLUX_RATIO*LocalConfig.getInstance().getSecondaryMaxFlux(), 
			PathwaysFrameConstants.FLUX_LEGEND_SIGNIFICANT_FIGURES, sciFormatter, PathwaysFrameConstants.MIN_DECIMAL_FORMAT, PathwaysFrameConstants.MAX_DECIMAL_FORMAT));
		double lowMidFlux = Double.parseDouble(util.roundToSignificantFigures(PathwaysFrameConstants.LOW_MID_FLUX_RATIO*LocalConfig.getInstance().getSecondaryMaxFlux(), 
			PathwaysFrameConstants.FLUX_LEGEND_SIGNIFICANT_FIGURES, sciFormatter, PathwaysFrameConstants.MIN_DECIMAL_FORMAT, PathwaysFrameConstants.MAX_DECIMAL_FORMAT));
		double midFlux = Double.parseDouble(util.roundToSignificantFigures(PathwaysFrameConstants.MID_FLUX_RATIO*LocalConfig.getInstance().getSecondaryMaxFlux(), 
			PathwaysFrameConstants.FLUX_LEGEND_SIGNIFICANT_FIGURES, sciFormatter, PathwaysFrameConstants.MIN_DECIMAL_FORMAT, PathwaysFrameConstants.MAX_DECIMAL_FORMAT));
		double topFlux = Double.parseDouble(util.roundToSignificantFigures(PathwaysFrameConstants.TOP_FLUX_RATIO*LocalConfig.getInstance().getSecondaryMaxFlux(), 
			PathwaysFrameConstants.FLUX_LEGEND_SIGNIFICANT_FIGURES, sciFormatter, PathwaysFrameConstants.MIN_DECIMAL_FORMAT, PathwaysFrameConstants.MAX_DECIMAL_FORMAT));
		double secMaxFlux = Double.parseDouble(util.roundToSignificantFigures(LocalConfig.getInstance().getSecondaryMaxFlux(), 
			PathwaysFrameConstants.FLUX_LEGEND_SIGNIFICANT_FIGURES, sciFormatter, PathwaysFrameConstants.MIN_DECIMAL_FORMAT, PathwaysFrameConstants.MAX_DECIMAL_FORMAT));
		//    	LocalConfig.getInstance().setVisualizationsProgress(100);
		for (int k = 0; k < pathway.getReactionsData().size(); k++) {
			if (k%10 == 0) {
				LocalConfig.getInstance().setVisualizationsProgress((k * ProgressConstants.VISUALIZATIONS_REACTION_LOAD_PERCENT) / pathway.getReactionsData().size()
						+ ProgressConstants.VISUALIZATIONS_LOAD_PERCENT + ProgressConstants.VISUALIZATIONS_METABOLITE_LOAD_PERCENT +
						ProgressConstants.CHECK_FOR_REQUIRED_REACTIONS_LOAD_PERCENT);	
			}
			if (pathway.getComponent() == component) {
				ArrayList<String> reacAbbrList = new ArrayList<String>();
				PathwayReactionNode pn = prnf.createPathwayReactionNode(pathway.getReactionsData().get(Integer.toString(k)), 
						LocalConfig.getInstance().getSelectedCompartmentName(), pathway.getComponent(), rxns, 
						idReactionMap);
				String displayName = prnf.createDisplayName(pathway.getReactionsData().get(Integer.toString(k)).getDisplayName(),
						pathway.getReactionsData().get(Integer.toString(k)).getName(), pathway.getReactionsData().get(Integer.toString(k)).getReactionId(),
						pn.getReactions(), idReactionMap);
				// update temporary lists to keep track of what ec numbers have been found
				double edgeColor = PathwaysFrameConstants.BLACK_COLOR_VALUE;
				double flux = 0;
				for (int z = 0; z < pn.getReactions().size(); z++) {
					if (pn.getReactions().get(z) != null) {
						flux += pn.getReactions().get(z).getFluxValue();
						if (pn.getReactions().get(z).getKnockout().equals(GraphicalInterfaceConstants.BOOLEAN_VALUES[1])) {
							koReactions.add(displayName);
							edgeColor = PathwaysFrameConstants.RED_COLOR_VALUE;
						}
					}
				}
				// set sum for flux
				pn.setFluxValue(flux);
				boolean drawReaction = true;
				if (pn.getReactions().size() > 0) {
					for (int i = 0; i < pn.getReactions().size(); i++) {
						reacAbbrList.add(pn.getReactions().get(i).getReactionAbbreviation());
					}
					//System.out.println(reacAbbrList);
					foundReactionsList.add(displayName);
					for (int r = 0; r < pathway.getReactionsData().get(Integer.toString(k)).getReactantIds().size(); r++) {
						try {
							String metabName = pathway.getMetabolitesData().get(pathway.getReactionsData().get(Integer.toString(k)).getReactantIds().get(r)).getName();
							if (!foundMetabolitesList.contains(metabName)) {
								//System.out.println(metabName);
								foundMetabolitesList.add(metabName);
							}
						} catch (Exception e) {

						}
					}
					for (int p = 0; p < pathway.getReactionsData().get(Integer.toString(k)).getProductIds().size(); p++) {
						try {
							String metabName = pathway.getMetabolitesData().get(pathway.getReactionsData().get(Integer.toString(k)).getProductIds().get(p)).getName();
							if (!foundMetabolitesList.contains(metabName)) {
								//System.out.println(metabName);
								foundMetabolitesList.add(metabName);
							}
						} catch (Exception e) {

						}
					}
					// for bookkeeping only
					for (int z = 0; z < pn.getReactions().size(); z++) {
						if (pn.getReactions().get(z) != null) {
							if (!plottedIds.contains(pn.getReactions().get(z).getId())) {
								plottedIds.add(pn.getReactions().get(z).getId());
							}
						}	
					}
				} else {
					if (!LocalConfig.getInstance().isGraphMissingMetabolitesSelected()) {
						drawReaction = false;
					} 
				}
				// Draw everything if graph all in database, else if Always Draw Calvin cycle
				// is not selected, don't draw Calvin cycle unless required reactions are found
				// in the model
				if (!LocalConfig.getInstance().isGraphMissingMetabolitesSelected()) {
					if (!LocalConfig.getInstance().isGraphCalvinCycleSelected() && 
						PathwaysFrameConstants.calvinCycleReactionIDsList.contains(pn.getDataId())) {
						if (!calvinCycleRequiredReactionIdsFound) {
							drawReaction = false;
						}
					}
				}
				if (drawReaction) {
					//System.out.println(pathway.getReactionsData().get(Integer.toString(k)).getEcNumbers());
					reactions.add(displayName);
					double x = 0;
					double y = 0;
					x = startX + PathwaysFrameConstants.HORIZONTAL_INCREMENT*pathway.getReactionsData().get(Integer.toString(k)).getLevel();
					y = startY + PathwaysFrameConstants.VERTICAL_INCREMENT*pathway.getReactionsData().get(Integer.toString(k)).getLevelPosition();
					nodeNamePositionMap.put(displayName, new String[] {Double.toString(x), Double.toString(y)});  
					pn.setDataId(pathway.getReactionsData().get(Integer.toString(k)).getReactionId());
					pn.setxPosition(x);
					pn.setyPosition(y);
					String reversible = prnf.reversibleString(pathway.getReactionsData().get(Integer.toString(k)).getReversible());
					String direction = PathwaysCSVFileConstants.FORWARD_DIRECTION;
					// use reversible from reactions in model if set
					if (pn.getReversible() != null && pn.getReversible().length() > 0) {
						reversible = pn.getReversible();
					} 
					if (pn.getDirection() != null && pn.getDirection().length() > 0) {
						direction = pn.getDirection();
					} 
					pn.setReversible(reversible);
					pathway.getReactionsNodes().put(pn.getDataId(), pn);
					// update maps for find exact match
					for (int e = 0; e < pathway.getReactionsData().get(Integer.toString(k)).getEcNumbers().size(); e++) {
						updateFindPositionsMap(ecNumberPositionsMap, pathway.getReactionsData().get(Integer.toString(k)).getEcNumbers().get(e), 
								new String[] {Double.toString(x), Double.toString(y)});
					}
					for (int r = 0; r < pathway.getReactionsData().get(Integer.toString(k)).getKeggReactionIds().size(); r++) {
						updateFindPositionsMap(keggReactionIdPositionsMap, pathway.getReactionsData().get(Integer.toString(k)).getKeggReactionIds().get(r), 
								new String[] {Double.toString(x), Double.toString(y)});
					}
					for (int a = 0; a < reacAbbrList.size(); a++) {
						updateFindPositionsMap(reactionAbbrPositionsMap, reacAbbrList.get(a), 
								new String[] {Double.toString(x), Double.toString(y)});
					}

					String drawForwardArrow = GraphicalInterfaceConstants.BOOLEAN_VALUES[1];
					String drawReverseArrow = GraphicalInterfaceConstants.BOOLEAN_VALUES[1];
					// for irreversible reactions, if direction is forward, draw -->
					// if direction is reverse, draw <--
					// for reversible reactions <-->
					if (reversible == GraphicalInterfaceConstants.BOOLEAN_VALUES[0]) {
						if (direction.equals(PathwaysCSVFileConstants.FORWARD_DIRECTION)) {
							drawReverseArrow = GraphicalInterfaceConstants.BOOLEAN_VALUES[0];
						} else if (direction.equals(PathwaysCSVFileConstants.REVERSE_DIRECTION)) {
							drawForwardArrow = GraphicalInterfaceConstants.BOOLEAN_VALUES[0];
						}
					}
					for (int r = 0; r < pathway.getReactionsData().get(Integer.toString(k)).getReactantIds().size(); r++) {
						if (pathway.getMetabolitesData().containsKey((pathway.getReactionsData().get(Integer.toString(k)).getReactantIds().get(r)))) {
							String reac = pathway.getMetabolitesData().get((pathway.getReactionsData().get(Integer.toString(k)).getReactantIds().get(r))).getName();
							reactionMap.put(displayName + "reactant " + Integer.toString(r), new String[] {displayName, reac, drawReverseArrow});
							fluxMap.put(displayName + "reactant " + Integer.toString(r), edgeThickness(pn.getFluxValue(), minFlux, lowerMidFlux, 
								lowMidFlux, midFlux, topFlux, secMaxFlux));
							if (pn.getFluxValue() == 0 && !koReactions.contains(displayName) &&
								LocalConfig.getInstance().isScaleEdgeThicknessSelected()) {
								edgeColor = PathwaysFrameConstants.GRAY_COLOR_VALUE;
							} 
							if (!foundReactionsList.contains(displayName)) {
								edgeColor = PathwaysFrameConstants.BLUE_NOT_FOUND_COLOR_VALUE;
							}
							colorMap.put(displayName + "reactant " + Integer.toString(r), edgeColor);
						}
					}
					for (int p = 0; p < pathway.getReactionsData().get(Integer.toString(k)).getProductIds().size(); p++) {
						if (pathway.getMetabolitesData().containsKey((pathway.getReactionsData().get(Integer.toString(k)).getProductIds().get(p)))) {
							String prod = pathway.getMetabolitesData().get((pathway.getReactionsData().get(Integer.toString(k)).getProductIds().get(p))).getName();
							reactionMap.put(displayName + "product " + Integer.toString(p), new String[] {displayName, prod, drawForwardArrow});
							fluxMap.put(displayName + "product " + Integer.toString(p), edgeThickness(pn.getFluxValue(), minFlux, lowerMidFlux, 
								lowMidFlux, midFlux, topFlux, secMaxFlux));
							if (pn.getFluxValue() == 0 && !koReactions.contains(displayName) &&
								LocalConfig.getInstance().isScaleEdgeThicknessSelected()) {
								edgeColor = PathwaysFrameConstants.GRAY_COLOR_VALUE;
							} 
							if (!foundReactionsList.contains(displayName)) {
								edgeColor = PathwaysFrameConstants.BLUE_NOT_FOUND_COLOR_VALUE;
							}
							colorMap.put(displayName + "product " + Integer.toString(p), edgeColor);
						}
					}
				}
			}
		}
		for (int i = 0; i < prnf.plottedIds.size(); i++) {
			if (LocalConfig.getInstance().getUnplottedReactionIds().contains(prnf.plottedIds.get(i))) {
				//				//System.out.println(reac.get(r).getId());
				LocalConfig.getInstance().getUnplottedReactionIds().remove(LocalConfig.getInstance().getUnplottedReactionIds().indexOf(prnf.plottedIds.get(i)));
			}
		}
		// removes "orphan" nodes
		if (!LocalConfig.getInstance().isGraphMissingMetabolitesSelected()) {
			for (int i = 0; i < metabPosKeys.size(); i++) {
				if (!foundMetabolitesList.contains(metabPosKeys.get(i))) {
					nodeNamePositionMap.remove(metabPosKeys.get(i));
				}
			}
		}
		//    	System.out.println("m " + prnf.getRenameMetabolitesMap());
		ArrayList<String> renameMetaboliteKeys = new ArrayList<String>(prnf.getRenameMetabolitesMap().keySet());
		for (int y = 0; y < renameMetaboliteKeys.size(); y++) {
			// construct new name from kegg ids and put name and abbr into metaboliteNameAbbrMap
			// get old abbreviation in case MetaboliteNameAbbrMap doesn't contain key
			String abbr = "";
			if (LocalConfig.getInstance().getMetaboliteNameAbbrMap().containsKey(renameMetaboliteKeys.get(y))) {
				//    			System.out.println("a " + LocalConfig.getInstance().getMetaboliteNameAbbrMap().get(renameMetaboliteKeys.get(y)));
				abbr = LocalConfig.getInstance().getMetaboliteNameAbbrMap().get(renameMetaboliteKeys.get(y));
			}
			String metabName = renameMetaboliteKeys.get(y);
			String metabAbbr = "";
			String name = "";
			ArrayList<String> abbrList = new ArrayList<String>();
			ArrayList<String> nameList = new ArrayList<String>();
			ArrayList<String> keggList = new ArrayList<String>();
			ArrayList<String> keggIdList = prnf.getRenameMetabolitesMap().get(renameMetaboliteKeys.get(y));
			boolean containsProton = false;
			String compProtonAbbr = "";
			if (keggIdList.contains("C00080")) {
				for (int k = 0; k < LocalConfig.getInstance().getKeggIdMetaboliteMap().get("C00080").size(); k++) {
					if (LocalConfig.getInstance().getKeggIdMetaboliteMap().get("C00080").get(k).getCompartment().
							equals(LocalConfig.getInstance().getSelectedCompartmentName())) {
						compProtonAbbr = LocalConfig.getInstance().getKeggIdMetaboliteMap().get("C00080").get(k).getMetaboliteAbbreviation();
						containsProton = true;
					}
				}
			}
			ArrayList<String> chebiIdList = new ArrayList<String>();
			ArrayList<String> chargeList = new ArrayList<String>();
			for (int z = 0; z < prnf.getRenameMetabolitesMap().get(renameMetaboliteKeys.get(y)).size(); z++) {
				////    			System.out.println("k1 " + prnf.getRenameMetabolitesMap().get(renameMetaboliteKeys.get(y)).get(z));
				if (LocalConfig.getInstance().getKeggIdMetaboliteMap().containsKey(prnf.getRenameMetabolitesMap().get(renameMetaboliteKeys.get(y)).get(z))) {	
					for (int j = 0; j < LocalConfig.getInstance().getKeggIdMetaboliteMap().get(prnf.getRenameMetabolitesMap().get(renameMetaboliteKeys.get(y)).get(z)).size(); j++) {
						if (!prnf.getRenameMetabolitesMap().get(renameMetaboliteKeys.get(y)).get(z).equals("C00080")) {
							if (LocalConfig.getInstance().getKeggIdMetaboliteMap().get(prnf.getRenameMetabolitesMap().get(renameMetaboliteKeys.get(y)).get(z)).get(j).getCompartment().
								equals(LocalConfig.getInstance().getSelectedCompartmentName())) {
								metabAbbr = LocalConfig.getInstance().getKeggIdMetaboliteMap().get(prnf.getRenameMetabolitesMap().get(renameMetaboliteKeys.get(y)).get(z)).get(j).getMetaboliteAbbreviation();
								if (!abbrList.contains(LocalConfig.getInstance().getKeggIdMetaboliteMap().get(prnf.getRenameMetabolitesMap().get(renameMetaboliteKeys.get(y)).get(z)).get(j).getMetaboliteAbbreviation())) {
									abbrList.add(LocalConfig.getInstance().getKeggIdMetaboliteMap().get(prnf.getRenameMetabolitesMap().get(renameMetaboliteKeys.get(y)).get(z)).get(j).getMetaboliteAbbreviation());
								}
								if (!nameList.contains(LocalConfig.getInstance().getKeggIdMetaboliteMap().get(prnf.getRenameMetabolitesMap().get(renameMetaboliteKeys.get(y)).get(z)).get(j).getMetaboliteName())) {
									nameList.add(LocalConfig.getInstance().getKeggIdMetaboliteMap().get(prnf.getRenameMetabolitesMap().get(renameMetaboliteKeys.get(y)).get(z)).get(j).getMetaboliteName());
								}
								if (!keggList.contains(LocalConfig.getInstance().getKeggIdMetaboliteMap().get(prnf.getRenameMetabolitesMap().get(renameMetaboliteKeys.get(y)).get(z)).get(j).getKeggId())) {
									keggList.add(LocalConfig.getInstance().getKeggIdMetaboliteMap().get(prnf.getRenameMetabolitesMap().get(renameMetaboliteKeys.get(y)).get(z)).get(j).getKeggId());
								}
								String chebiId = LocalConfig.getInstance().getKeggIdMetaboliteMap().get(prnf.getRenameMetabolitesMap().get(renameMetaboliteKeys.get(y)).get(z)).get(j).getChebiId();
								if (chebiId != null && chebiId.length() > 0 && !chebiIdList.contains(chebiId.trim())) {
									chebiIdList.add(chebiId.trim());
								}
								String charge = LocalConfig.getInstance().getKeggIdMetaboliteMap().get(prnf.getRenameMetabolitesMap().get(renameMetaboliteKeys.get(y)).get(z)).get(j).getCharge();
								if (charge != null && charge.length() > 0 && !chargeList.contains(charge.trim())) {
									chargeList.add(charge.trim());
								}
							}
						}
					}
					if (abbrList.size() > 0) {
						name = util.makeCommaSeparatedList(abbrList);
						metabAbbr = name;
					} else {
						name = metabAbbr;
					}
					if (abbrList.size() > 1) {
						ArrayList<String> abbrNoPrefixOrSuffix = new ArrayList<String>();
						for (int p = 0; p < abbrList.size(); p++) {
							abbrNoPrefixOrSuffix.add(util.maybeRemovePrefixAndSuffix(abbrList.get(p)));
						}
						abbr = util.makeCommaSeparatedList(abbrNoPrefixOrSuffix);
					} else {
						abbr = util.maybeRemovePrefixAndSuffix(metabAbbr);
					}
					if (nodeNamePositionMap.containsKey(metabName)) {
						updateFindPositionsMap(metaboliteAbbrPositionsMap, abbr, 
								new String[] {nodeNamePositionMap.get(metabName)[0], nodeNamePositionMap.get(metabName)[1]});
					}
					// TODO: figure out how to get database id here from metabolite node positions file
					// so node info can be in the same format as other nodes
					name = pmnf.htmlDisplayNameRenamed(abbr, nameList, abbrList, keggIdList, chebiIdList, chargeList);
					oldNameNewNameMap.put(metabName, name);
					LocalConfig.getInstance().getMetaboliteNameAbbrMap().put(metabName, abbr);
				}
			}
			//    		System.out.println("abbr " + abbr);
			//			System.out.println("name " + name);
			//			System.out.println("old " + metabName);
			if (containsProton) {
				String protonAbbr = util.maybeRemovePrefixAndSuffix(LocalConfig.getInstance().getKeggIdMetaboliteMap().get("C00080").get(0).getMetaboliteAbbreviation());
				abbr += " + " + protonAbbr;
				nameList.add(LocalConfig.getInstance().getKeggIdMetaboliteMap().get("C00080").get(0).getMetaboliteName());
				keggList.add("C00080");
				if (abbrList.contains(compProtonAbbr)) {
					abbrList.add(compProtonAbbr);
				}
				if (nodeNamePositionMap.containsKey(metabName)) {
					updateFindPositionsMap(metaboliteAbbrPositionsMap, compProtonAbbr, 
							new String[] {nodeNamePositionMap.get(metabName)[0], nodeNamePositionMap.get(metabName)[1]});
					for (int k = 0; k < LocalConfig.getInstance().getKeggIdMetaboliteMap().get("C00080").size(); k++) {
						if (LocalConfig.getInstance().getKeggIdMetaboliteMap().get("C00080").get(k).getCompartment().
								equals(LocalConfig.getInstance().getSelectedCompartmentName())) {
							abbrList.add(LocalConfig.getInstance().getKeggIdMetaboliteMap().get("C00080").get(k).getMetaboliteAbbreviation());
						}
					}
					// TODO: figure out how to get database id here from metabolite node positions file
					// so node info can be in the same format as other nodes
					name = pmnf.htmlDisplayNameRenamed(abbr, nameList, abbrList, keggList, chebiIdList, chargeList);
					oldNameNewNameMap.put(metabName, name);
					LocalConfig.getInstance().getMetaboliteNameAbbrMap().put(metabName, abbr);
					// add positions to map for find exact match
					for (int n = 0; n < abbrList.size(); n++) {
						updateFindPositionsMap(metaboliteAbbrPositionsMap, abbrList.get(n), 
								new String[] {nodeNamePositionMap.get(metabName)[0], nodeNamePositionMap.get(metabName)[1]});
					}
				}
			} 
		}
	}

	public void drawPathwayNames(int component) {
		if (component == PathwaysFrameConstants.PATHWAYS_COMPONENT) {
			for(int p = 0; p < LocalConfig.getInstance().getPathwayNameMap().size(); p++) {
				ArrayList<String> metabList = LocalConfig.getInstance().getPathwayNameMap().get(Integer.toString(p)).getMetabolites();
				boolean drawPathwayName = true;
				boolean found = pathwayNameMetaboliteFound(metabList);
				if (!LocalConfig.getInstance().isGraphMissingMetabolitesSelected()) {
					if (!found) {
						drawPathwayName = false;
					}
				}
				String pathwayName = LocalConfig.getInstance().getPathwayNameMap().get(Integer.toString(p)).getName();
				if (!LocalConfig.getInstance().isGraphMissingMetabolitesSelected()) {
					if (!LocalConfig.getInstance().isGraphCalvinCycleSelected() && (pathwayName.equals("Calvin Cycle") ||
						pathwayName.equals(" CO2 Pumping (C4 plants)"))) {
						if (!calvinCycleRequiredReactionIdsFound) {
							drawPathwayName = false;
						}
					}
				}
				if (drawPathwayName) {
					//String pathwayName = LocalConfig.getInstance().getPathwayNameMap().get(Integer.toString(p)).getName();
					pathwayNames.add(pathwayName);
					PathwayNameNode pnn = new PathwayNameNode();
					double x = 0;
					double y = 0;
					pnn.setDataId(LocalConfig.getInstance().getPathwayNameMap().get(Integer.toString(p)).getId());
					pnn.setName(pathwayName);
					x = startX + PathwaysFrameConstants.HORIZONTAL_INCREMENT*LocalConfig.getInstance().getPathwayNameMap().get(Integer.toString(p)).getLevel();
					y = startY + PathwaysFrameConstants.VERTICAL_INCREMENT*LocalConfig.getInstance().getPathwayNameMap().get(Integer.toString(p)).getLevelPosition();
					pnn.setxPosition(x);
					pnn.setyPosition(y);
					nodeNamePositionMap.put(pathwayName, new String[] {Double.toString(x), Double.toString(y)});
					if (found) {
						foundPathwayNamesList.add(pathwayName);
					}
				}
			}
		}
	}
	
	public void drawLegend(int component) {
		if (component == PathwaysFrameConstants.PATHWAYS_COMPONENT) {
			// first value is greater than 0 to less than first value
			// after that, >= to first value and < second value
			String previousValue = "0";
			String comparator = "> ";
			double x = PathwaysFrameConstants.FLUX_RANGE_START_X_POSITION;
			double y = PathwaysFrameConstants.FLUX_RANGE_START_Y_POSITION;
			String zero = "= 0";
			updateCollectionsForFluxRange(zero, Double.toString(x), Double.toString(y), 1.0, PathwaysFrameConstants.ZERO_FLUX_WIDTH_NAME);
			y += PathwaysFrameConstants.FLUX_RANGE_START_Y_INCREMENT;
			for (int i = 0; i < PathwaysFrameConstants.FLUX_WIDTH_RATIOS.length; i++) {
				String value = util.roundToSignificantFigures(PathwaysFrameConstants.FLUX_WIDTH_RATIOS[i]*LocalConfig.getInstance().getSecondaryMaxFlux(), 
					PathwaysFrameConstants.FLUX_LEGEND_SIGNIFICANT_FIGURES, sciFormatter, PathwaysFrameConstants.MIN_DECIMAL_FORMAT, PathwaysFrameConstants.MAX_DECIMAL_FORMAT);
				String name = comparator + previousValue + " to < " + value;
				updateCollectionsForFluxRange(name, Double.toString(x), Double.toString(y), PathwaysFrameConstants.FLUX_WIDTHS[i],
					PathwaysFrameConstants.WIDTH_PREFIX + Double.toString(PathwaysFrameConstants.FLUX_WIDTHS[i]));
				y += PathwaysFrameConstants.FLUX_RANGE_START_Y_INCREMENT;
				comparator = ">= ";
				previousValue = value;
			}
			String secMax = util.roundToSignificantFigures(LocalConfig.getInstance().getSecondaryMaxFlux(), 
				PathwaysFrameConstants.FLUX_LEGEND_SIGNIFICANT_FIGURES, sciFormatter, PathwaysFrameConstants.MIN_DECIMAL_FORMAT, PathwaysFrameConstants.MAX_DECIMAL_FORMAT);
			String name = comparator + previousValue + " to < " + secMax;
			updateCollectionsForFluxRange(name, Double.toString(x), Double.toString(y), PathwaysFrameConstants.SECONDARY_MAX_FLUX_WIDTH,
				PathwaysFrameConstants.WIDTH_PREFIX + Double.toString(PathwaysFrameConstants.SECONDARY_MAX_FLUX_WIDTH));
			y += PathwaysFrameConstants.FLUX_RANGE_START_Y_INCREMENT;
			String maxFluxLimit = util.roundToSignificantFigures(PathwaysFrameConstants.INFINITE_FLUX_RATIO*LocalConfig.getInstance().getMaxFlux(), 
				PathwaysFrameConstants.FLUX_LEGEND_SIGNIFICANT_FIGURES, sciFormatter, PathwaysFrameConstants.MIN_DECIMAL_FORMAT, PathwaysFrameConstants.MAX_DECIMAL_FORMAT);
			String secMaxToMaxFluxLimit = ">= " + secMax + " to <= " + maxFluxLimit;
			updateCollectionsForFluxRange(secMaxToMaxFluxLimit, Double.toString(x), Double.toString(y), PathwaysFrameConstants.ABOVE_SECONDARY_MAX_FLUX_WIDTH, 
				PathwaysFrameConstants.WIDTH_PREFIX + Double.toString(PathwaysFrameConstants.ABOVE_SECONDARY_MAX_FLUX_WIDTH));
			y += PathwaysFrameConstants.FLUX_RANGE_START_Y_INCREMENT;
			String maxFluxLimitEntry = "> " + maxFluxLimit;
			// prevent > Infinity from appearing in key!!  
			if (!maxFluxLimit.equals(GraphicalInterfaceConstants.VALID_INFINITY_ENTRY)) {
				updateCollectionsForFluxRange(maxFluxLimitEntry, Double.toString(x), Double.toString(y), PathwaysFrameConstants.INFINITE_FLUX_WIDTH,
					PathwaysFrameConstants.WIDTH_PREFIX + Double.toString(PathwaysFrameConstants.INFINITE_FLUX_WIDTH));
			}
		}
	}
	
	public void updateCollectionsForFluxRange(String entry, String x, String y, Double width, String widthName) {
		fluxRangeNames.add(entry);
		fluxRangeWidths.add(widthName);
		String rightNode = widthName + PathwaysFrameConstants.WIDTH_RIGHT_NODE_SUFFIX;
		fluxRangeWidths.add(rightNode);
		nodeNamePositionMap.put(entry, new String[] {x, y});
		// draw invisible nodes and connect with edges for legend since filled rectangles
		// did not work
		double edgeX = Double.parseDouble(x) + PathwaysFrameConstants.FLUX_RANGE_EDGE_X_OFFSET;
		double edgeRightX = Double.parseDouble(x) + PathwaysFrameConstants.FLUX_RANGE_EDGE_X_OFFSET +
			PathwaysFrameConstants.FLUX_RANGE_EDGE_LENGTH;
		nodeNamePositionMap.put(widthName, new String[] {Double.toString(edgeX), y});
		nodeNamePositionMap.put(rightNode, 
			new String[] {Double.toString(edgeRightX), y});
		reactionMap.put(widthName, new String[] {widthName, rightNode, "false"});
		if (widthName.equals(PathwaysFrameConstants.ZERO_FLUX_WIDTH_NAME)) {
			colorMap.put(widthName, PathwaysFrameConstants.GRAY_COLOR_VALUE);
			fluxMap.put(widthName, 1.0);
		} else {
			fluxMap.put(widthName, width);
		}
		noBorderList.add(widthName);
		noBorderList.add(rightNode);
	}

	public boolean pathwayNameMetaboliteFound(ArrayList<String> list) {
		boolean found = false;
		for (int i = 0; i < list.size(); i++) {
			if (LocalConfig.getInstance().getKeggIdMetaboliteMap().containsKey(list.get(i))) {
				ArrayList<SBMLMetabolite> m = LocalConfig.getInstance().getKeggIdMetaboliteMap().get(list.get(i));
				for (int j = 0; j < m.size(); j++) {
					if (m.get(j).getCompartment().equals(LocalConfig.getInstance().getSelectedCompartmentName())) {
						found = true;
					}
				}
			}
		}

		return found;

	}

	public void classifyMetabolite(String type, String metabName, String keggId) {
		if (type.equals(PathwaysCSVFileConstants.MAIN_METABOLITE_TYPE)) {
			if (!mainMetabolites.contains(metabName)) {
				mainMetabolites.add(metabName);
			}
		} else if (type.equals(PathwaysCSVFileConstants.SMALL_MAIN_METABOLITE_TYPE)) {
			if (!smallMainMetabolites.contains(metabName)) {
				smallMainMetabolites.add(metabName);
			}
		} else if (type.equals(PathwaysCSVFileConstants.SIDE_METABOLITE_TYPE)) {
			if (!sideMetabolites.contains(metabName)) {
				sideMetabolites.add(metabName);
				if (PathwaysFrameConstants.cofactorList.contains(keggId)) {
					if (!cofactors.contains(metabName)) {
						cofactors.add(metabName);
					}
				}
			}
		}
	}

	public void drawCompartmentBorder(String borderLeftX, String borderRightX, 
			String borderTopY, String borderBottomY, int startNumber) {
		// draw cell border
		String topLeft = Integer.toString(startNumber + 1);
		String topRight = Integer.toString(startNumber + 2);
		String bottomRight = Integer.toString(startNumber + 3);
		String bottomLeft = Integer.toString(startNumber + 4);

		nodeNamePositionMap.put(topLeft, new String[] {borderLeftX, borderTopY}); 
		nodeNamePositionMap.put(topRight, new String[] {borderRightX, borderTopY}); 
		nodeNamePositionMap.put(bottomRight, new String[] {borderRightX, borderBottomY});
		nodeNamePositionMap.put(bottomLeft, new String[] {borderLeftX, borderBottomY}); 

		reactionMap.put(topLeft, new String[] {topLeft, topRight, "false"});
		reactionMap.put(topRight, new String[] {topRight, bottomRight, "false"});
		reactionMap.put(bottomRight, new String[] {bottomRight, bottomLeft, "false"});
		reactionMap.put(bottomLeft, new String[] {bottomLeft, topLeft, "false"});

		fluxMap.put(topLeft, PathwaysFrameConstants.BORDER_THICKNESS);
		fluxMap.put(topRight, PathwaysFrameConstants.BORDER_THICKNESS);
		fluxMap.put(bottomRight, PathwaysFrameConstants.BORDER_THICKNESS);
		fluxMap.put(bottomLeft, PathwaysFrameConstants.BORDER_THICKNESS);

		for (int b = startNumber + 1; b < startNumber + 5; b++) {
			borderList.add(Integer.toString(b));
		}
	}

	public void drawCompartmentLabel(String text, String xOffset, String yOffset) {
		nodeNamePositionMap.put(text, new String[] {xOffset, yOffset});
	}
	
	public void drawLegendLabel(String text, String xOffset, String yOffset) {
		nodeNamePositionMap.put(text, new String[] {xOffset, yOffset});
	}

	public double edgeThickness(double fluxValue, double minFlux, double lowerMidFlux, 
		double lowMidFlux, double midFlux, double topFlux, double secMaxFlux) {
		double thickness = PathwaysFrameConstants.DEFAULT_EDGE_WIDTH;
		if (Math.abs(fluxValue) > PathwaysFrameConstants.INFINITE_FLUX_RATIO*LocalConfig.getInstance().getMaxFlux()) {
			//System.out.println("flux " + pn.getFluxValue());
			thickness = PathwaysFrameConstants.INFINITE_FLUX_WIDTH;
		} else if (Math.abs(fluxValue) > 0) {
			if (Math.abs(fluxValue) < minFlux) {
				thickness = PathwaysFrameConstants.MINIMUM_FLUX_WIDTH;
			} else if (Math.abs(fluxValue) < lowerMidFlux) {
				thickness = PathwaysFrameConstants.LOWER_MID_FLUX_WIDTH;
			} else if (Math.abs(fluxValue) < lowMidFlux) {
				thickness = PathwaysFrameConstants.LOW_MID_FLUX_WIDTH;
			} else if (Math.abs(fluxValue) < midFlux) {
				thickness = PathwaysFrameConstants.MID_FLUX_WIDTH;
			} else if (Math.abs(fluxValue) < topFlux) {
				thickness = PathwaysFrameConstants.TOP_FLUX_WIDTH;
			} else if (Math.abs(fluxValue) <= secMaxFlux) {
				thickness = PathwaysFrameConstants.SECONDARY_MAX_FLUX_WIDTH;
			} else {
				thickness = PathwaysFrameConstants.ABOVE_SECONDARY_MAX_FLUX_WIDTH;
			}
		}

		return thickness;
	}

	public void updateFindPositionsMap(HashMap<String, ArrayList<String[]>> positionsMap, String key, String[] pos) {
		if (key != null && key.length() > 0) {
			if (positionsMap.containsKey(key)) {
				ArrayList<String[]> positions = positionsMap.get(key);
				positions.add(pos);
				positionsMap.put(key, positions);
			} else {
				ArrayList<String[]> positions = new ArrayList<String[]>();
				positions.add(pos);
				positionsMap.put(key, positions);
			}
		}
	}
	
	public void removeBiomassReactions(Vector<SBMLReaction> reactions) {
		ArrayList<Integer> indices = new ArrayList<Integer>();
		for (int i = 0; i < reactions.size(); i++) {
			int id = reactions.get(i).getId();
			SBMLReactionEquation equn = (SBMLReactionEquation) LocalConfig.getInstance().getReactionEquationMap().get(id);
			ArrayList<SBMLReactant> reactants = equn.getReactants();
			ArrayList<SBMLProduct> products = equn.getProducts();
			if (reactants.size() > 10 || products.size() > 10) {
				indices.add(i);
				biomassIds.add(id);
				if (LocalConfig.getInstance().getUnplottedReactionIds().contains(id)) {
					LocalConfig.getInstance().getUnplottedReactionIds().remove(LocalConfig.getInstance().getUnplottedReactionIds().indexOf(id));
				}
			}
		}
		for (int j = indices.size() - 1; j >= 0; j--) {
			reactions.remove(indices.get(j));
		}
	}

}
