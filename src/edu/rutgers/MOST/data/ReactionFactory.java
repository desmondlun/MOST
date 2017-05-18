package edu.rutgers.MOST.data;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.presentation.GraphicalInterface;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;
import edu.rutgers.MOST.presentation.ResizableDialog;

public class ReactionFactory {
	private String sourceType;
	private Map<Object, Object> reactionsIdPositionMap;
	private static String columnName;
	private boolean objVecWarning = true;
	private ResizableDialog dialog = new ResizableDialog( "Error",
			"Error", "Error" );

	public ReactionFactory(String sourceType) {
		this.sourceType = sourceType;
	}

	public Map<Object, Object> getReactionsIdPositionMap() {
		return reactionsIdPositionMap;
	}

	public void setReactionsIdPositionMap(Map<Object, Object> reactionsIdPositionMap) {
		this.reactionsIdPositionMap = reactionsIdPositionMap;
	}

	public void disableObjVectorWarning()
	{
		this.objVecWarning = false;
	}
	
	public SBMLReaction getReactionById(Integer reactionId){
		if("SBML".equals(sourceType)){
			SBMLReaction reaction = new SBMLReaction();
			reaction.loadById(reactionId);
			return reaction;
		}
		return new SBMLReaction(); //Default behavior.
	}

	public SBMLReaction getReactionByRow(Integer row){
		if("SBML".equals(sourceType)){
			SBMLReaction reaction = new SBMLReaction();
			reaction.loadByRow(row);
			return reaction;
		}
		return new SBMLReaction(); //Default behavior.
	}
	
	public Vector<SBMLReaction> getAllReactions() {
		Vector<SBMLReaction> reactions = new Vector<SBMLReaction>();
		Map<Object, Object> reactionsIdPositionMap = new HashMap<Object, Object>();
		int count = 0;

		if("SBML".equals(sourceType)){
			// returns a list of SBMLReactions
			// what parameters are actually needed, for example
			// reaction name is not going to be changed by any analysis			
			for (int i = 0; i < GraphicalInterface.reactionsTable.getRowCount(); i++) {
//				if (GraphicalInterface.reactionsTable.getModel().getValueAt(i, GraphicalInterfaceConstants.REACTION_ABBREVIATION_COLUMN) != null &&
//						((String) GraphicalInterface.reactionsTable.getModel().getValueAt(i, GraphicalInterfaceConstants.REACTION_ABBREVIATION_COLUMN)).trim().length() > 0) {
				if (((String) GraphicalInterface.reactionsTable.getModel().getValueAt(i, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN)).length() > 0) {
					SBMLReaction reaction = new SBMLReaction();
					reaction.setId(Integer.valueOf((String) GraphicalInterface.reactionsTable.getModel().getValueAt(i, GraphicalInterfaceConstants.REACTIONS_ID_COLUMN)));
					reaction.setKnockout((String) GraphicalInterface.reactionsTable.getModel().getValueAt(i, GraphicalInterfaceConstants.KO_COLUMN));
					reaction.setFluxValue(Double.valueOf((String) GraphicalInterface.reactionsTable.getModel().getValueAt(i, GraphicalInterfaceConstants.FLUX_VALUE_COLUMN)));
					reaction.setMinFlux(Double.valueOf((String) GraphicalInterface.reactionsTable.getModel().getValueAt(i, GraphicalInterfaceConstants.MIN_FLUX_COLUMN)));
					reaction.setMaxFlux(Double.valueOf((String) GraphicalInterface.reactionsTable.getModel().getValueAt(i, GraphicalInterfaceConstants.MAX_FLUX_COLUMN)));
					reaction.setReactionAbbreviation((String) GraphicalInterface.reactionsTable.getModel().getValueAt(i, GraphicalInterfaceConstants.REACTION_ABBREVIATION_COLUMN));
					reaction.setReactionName((String) GraphicalInterface.reactionsTable.getModel().getValueAt(i, GraphicalInterfaceConstants.REACTION_NAME_COLUMN));
					reaction.setReactionEqunAbbr((String) GraphicalInterface.reactionsTable.getModel().getValueAt(i, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN));
					reaction.setReactionEqunNames((String) GraphicalInterface.reactionsTable.getModel().getValueAt(i, GraphicalInterfaceConstants.REACTION_EQUN_NAMES_COLUMN));
					reaction.setReversible((String) GraphicalInterface.reactionsTable.getModel().getValueAt(i, GraphicalInterfaceConstants.REVERSIBLE_COLUMN));				
					reaction.setLowerBound(Double.valueOf((String) GraphicalInterface.reactionsTable.getModel().getValueAt(i, GraphicalInterfaceConstants.LOWER_BOUND_COLUMN)));
					reaction.setUpperBound(Double.valueOf((String) GraphicalInterface.reactionsTable.getModel().getValueAt(i, GraphicalInterfaceConstants.UPPER_BOUND_COLUMN)));
					reaction.setBiologicalObjective(Double.valueOf((String) GraphicalInterface.reactionsTable.getModel().getValueAt(i, GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN)));
					reaction.setSyntheticObjective(Double.valueOf((String) GraphicalInterface.reactionsTable.getModel().getValueAt(i, GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_COLUMN)));
					reaction.setGeneAssociation((String) GraphicalInterface.reactionsTable.getModel().getValueAt(i, GraphicalInterfaceConstants.GENE_ASSOCIATION_COLUMN));
					reaction.setProteinAssociation((String) GraphicalInterface.reactionsTable.getModel().getValueAt(i, GraphicalInterfaceConstants.PROTEIN_ASSOCIATION_COLUMN));
					reaction.setSubsystem((String) GraphicalInterface.reactionsTable.getModel().getValueAt(i, GraphicalInterfaceConstants.SUBSYSTEM_COLUMN));
					reaction.setProteinClass((String) GraphicalInterface.reactionsTable.getModel().getValueAt(i, GraphicalInterfaceConstants.PROTEIN_CLASS_COLUMN));
					if (LocalConfig.getInstance().getEcNumberColumn() > -1) {
						reaction.setEcNumber((String) GraphicalInterface.reactionsTable.getModel().getValueAt(i, LocalConfig.getInstance().getEcNumberColumn()));
					}
					if (LocalConfig.getInstance().getKeggReactionIdColumn() > -1) {
						try {
							// accounts for models where KEGG ids start with "r"
							String keggId = (String) GraphicalInterface.reactionsTable.getModel().getValueAt(i, LocalConfig.getInstance().getKeggReactionIdColumn());
							reaction.setKeggReactionId(keggId.toUpperCase());
						} catch (Exception e) {
							
						}
					}
					reactions.add(reaction);
					reactionsIdPositionMap.put(reaction.getId(), count);
					count += 1;
				}					
				//}				
			}
			setReactionsIdPositionMap(reactionsIdPositionMap);
		}
		
		return reactions;
	}

	public void setAllReactions( Vector< SBMLReaction > reactions )
	{
		if( !"SBML".equals( sourceType ) )
			return;
		for( int i = 0; i < GraphicalInterface.reactionsTable.getRowCount(); i++)
		{
//			if (GraphicalInterface.reactionsTable.getModel().getValueAt(i, GraphicalInterfaceConstants.REACTION_ABBREVIATION_COLUMN) != null &&
//					((String) GraphicalInterface.reactionsTable.getModel().getValueAt(i, GraphicalInterfaceConstants.REACTION_ABBREVIATION_COLUMN)).trim().length() > 0) {
			if( ( (String)GraphicalInterface.reactionsTable.getModel().getValueAt( i, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN ) ).length() > 0 )
			{
				TableModel tmodel = GraphicalInterface.reactionsTable.getModel();
				SBMLReaction reaction = reactions.elementAt( i );
				tmodel.setValueAt( Integer.toString( reaction.getId() ), i, GraphicalInterfaceConstants.REACTIONS_ID_COLUMN );
				tmodel.setValueAt( reaction.getKnockout(), i, GraphicalInterfaceConstants.KO_COLUMN );
				tmodel.setValueAt( Double.toString( reaction.getFluxValue() ), i, GraphicalInterfaceConstants.FLUX_VALUE_COLUMN );
				tmodel.setValueAt( Double.toString( reaction.getMinFlux() ), i, GraphicalInterfaceConstants.MIN_FLUX_COLUMN );
				tmodel.setValueAt( Double.toString( reaction.getMaxFlux() ), i, GraphicalInterfaceConstants.MAX_FLUX_COLUMN );
				tmodel.setValueAt( reaction.getReactionAbbreviation(), i, GraphicalInterfaceConstants.REACTION_ABBREVIATION_COLUMN );
				tmodel.setValueAt( reaction.getReactionName(), i, GraphicalInterfaceConstants.REACTION_NAME_COLUMN );
				tmodel.setValueAt( reaction.getReactionEqunAbbr(), i, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN );
				tmodel.setValueAt( reaction.getReactionEqunNames(), i, GraphicalInterfaceConstants.REACTION_EQUN_NAMES_COLUMN );
				tmodel.setValueAt( reaction.getReversible(), i, GraphicalInterfaceConstants.REVERSIBLE_COLUMN );
				tmodel.setValueAt( Double.toString( reaction.getLowerBound() ), i, GraphicalInterfaceConstants.LOWER_BOUND_COLUMN );
				tmodel.setValueAt( Double.toString( reaction.getUpperBound() ), i, GraphicalInterfaceConstants.UPPER_BOUND_COLUMN );
				tmodel.setValueAt( Double.toString( reaction.getBiologicalObjective() ), i, GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN );
				tmodel.setValueAt( Double.toString( reaction.getSyntheticObjective() ), i, GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_COLUMN );
				tmodel.setValueAt( reaction.getGeneAssociation(), i, GraphicalInterfaceConstants.GENE_ASSOCIATION_COLUMN );
				tmodel.setValueAt( reaction.getProteinAssociation(), i, GraphicalInterfaceConstants.PROTEIN_ASSOCIATION_COLUMN );
				tmodel.setValueAt( reaction.getSubsystem(), i, GraphicalInterfaceConstants.SUBSYSTEM_COLUMN );
				tmodel.setValueAt( reaction.getProteinClass(), i, GraphicalInterfaceConstants.PROTEIN_CLASS_COLUMN );
				reactionsIdPositionMap.put(reaction.getId(), i);
			}
		}
		
	}
	
	public Vector<Double> getObjective() {
		Vector<SBMLReaction> reactions = getAllReactions();
		Vector<Double> objective = new Vector<Double>(reactions.size());

		if("SBML".equals(sourceType)){
			double max = 0;
			for (int i = 0; i < reactions.size(); i++) {
				int id = reactions.get(i).getId();
				Double obj = reactions.get(i).getBiologicalObjective();
				if (obj != 0) {
					max = obj;
				}
				objective.add((Integer) reactionsIdPositionMap.get(id), obj);
			}
			if (max == 0 && !LocalConfig.getInstance().noBiolObjWarningShown && objVecWarning) {
				JOptionPane.showMessageDialog(null,                
						"No Biological Objective Set.",                
						"Warning",                                
						JOptionPane.WARNING_MESSAGE);
				LocalConfig.getInstance().noBiolObjWarningShown = true;
			}
		}

		return objective;
	}
	
	/**
	 * resets all elements in the Knockout column to false
		 */
	public void resetKnockOuts()
	{
		for (int i = 0; i < GraphicalInterface.reactionsTable.getRowCount(); i++)
			GraphicalInterface.reactionsTable.getModel().setValueAt( "false", i, GraphicalInterfaceConstants.KO_COLUMN );
	}

	public void setFluxes(ArrayList<Double> fluxes, int columnIndex, DefaultTableModel reactionsOptModel) {
		//DefaultTableModel reactionsOptModel = (DefaultTableModel) GraphicalInterface.reactionsTable.getModel();
		Vector<SBMLReaction> reactions = getAllReactions();
		Map<String, Object> reactionsIdRowMap = new HashMap<String, Object>();
		for (int i = 0; i < GraphicalInterface.reactionsTable.getRowCount(); i++) {
			reactionsIdRowMap.put((String) GraphicalInterface.reactionsTable.getModel().getValueAt(i, GraphicalInterfaceConstants.REACTIONS_ID_COLUMN), i);
		}
		for( int i = 0; i < fluxes.size(); ++i )
		{
			int id = ((SBMLReaction) reactions.get(i)).getId();
			String row = (reactionsIdRowMap.get(Integer.toString(id))).toString();
			int rowNum = Integer.valueOf(row);
			try {
				reactionsOptModel.setValueAt(fluxes.get(i).toString(), rowNum, columnIndex);
			} catch (Exception e) {
				processStackTrace(e);
			}			
		}
	}

	public ArrayList<String> setKnockouts(List<Double> knockouts) {
		ArrayList<String> knockoutGenes = new ArrayList<String>();

		ArrayList<Double> kVector = new ArrayList<Double>();

		Vector<String> uniqueGeneAssociations = getUniqueGeneAssociations();
		Vector<String> geneAssocaitons = getGeneAssociations();
		ArrayList<Integer> rowList = new ArrayList<Integer>();

		for (int i = 0; i < geneAssocaitons.size(); i++) {
			for (int j = 0; j < uniqueGeneAssociations.size(); j++) {
				if (geneAssocaitons.elementAt(i).equals(uniqueGeneAssociations.elementAt(j))) {
					kVector.add(knockouts.get(j).doubleValue());
				}

				if (knockouts.get(j).doubleValue() != 0.0) {
					knockoutGenes.add(uniqueGeneAssociations.elementAt(j));
				}
			}

			if(kVector.get(i).doubleValue() != 0.0) {
				rowList.add(i);
			}
		}

		//			for (int i = 0; i < uniqueGeneAssociations.size(); i++) {
		//				if (knockouts.get(i).doubleValue() != 0) {
		//					knockoutGenes.add(uniqueGeneAssociations.elementAt(i));
		////					System.out.println(uniqueGeneAssociations.elementAt(i));
		//				}
		//			}

		DefaultTableModel reactionsOptModel = (DefaultTableModel) GraphicalInterface.reactionsTable.getModel();
		setKnockoutValues(rowList, reactionsOptModel);
//		Vector<ModelReaction> reactions = getAllReactions();
//		Map<String, Object> reactionsIdRowMap = new HashMap<String, Object>();
//		for (int i = 0; i < GraphicalInterface.reactionsTable.getRowCount(); i++) {
//			reactionsIdRowMap.put((String) GraphicalInterface.reactionsTable.getModel().getValueAt(i, GraphicalInterfaceConstants.REACTIONS_ID_COLUMN), i);
//		}
//		LocalConfig.getInstance().setGdbbKnockoutsList(rowList);
//		for (int j = 0; j < rowList.size(); j++) {
//			int id = ((SBMLReaction) reactions.get(rowList.get(j))).getId();
//			String row = (reactionsIdRowMap.get(Integer.toString(id))).toString();
//			int rowNum = Integer.valueOf(row);	
//			try {
//				reactionsOptModel.setValueAt(GraphicalInterfaceConstants.BOOLEAN_VALUES[1], rowNum, GraphicalInterfaceConstants.KO_COLUMN);
//			} catch (Exception e) {
//				processStackTrace(e);
//			}	
//		}	

		return knockoutGenes;
	}
	
	public void updateKnockouts(ArrayList<Integer> rowList) {
		DefaultTableModel reactionsOptModel = (DefaultTableModel) GraphicalInterface.reactionsTable.getModel();
		// reset all values to false
		for (int h = 0; h < GraphicalInterface.reactionsTable.getRowCount(); h++) {
			reactionsOptModel.setValueAt(GraphicalInterfaceConstants.BOOLEAN_VALUES[0], h, GraphicalInterfaceConstants.KO_COLUMN);
		}
		// set knockouts to true
		setKnockoutValues(rowList, reactionsOptModel);
//		Vector<ModelReaction> reactions = getAllReactions();
//		Map<String, Object> reactionsIdRowMap = new HashMap<String, Object>();
//		for (int i = 0; i < GraphicalInterface.reactionsTable.getRowCount(); i++) {
//			reactionsIdRowMap.put((String) GraphicalInterface.reactionsTable.getModel().getValueAt(i, GraphicalInterfaceConstants.REACTIONS_ID_COLUMN), i);
//		}
//		LocalConfig.getInstance().setGdbbKnockoutsList(rowList);
//		for (int j = 0; j < rowList.size(); j++) {
//			int id = ((SBMLReaction) reactions.get(rowList.get(j))).getId();
//			String row = (reactionsIdRowMap.get(Integer.toString(id))).toString();
//			int rowNum = Integer.valueOf(row);	
//			try {
//				reactionsOptModel.setValueAt(GraphicalInterfaceConstants.BOOLEAN_VALUES[1], rowNum, GraphicalInterfaceConstants.KO_COLUMN);
//			} catch (Exception e) {
//				processStackTrace(e);
//			}	
//		}	
	}

	// updates knockout values in reactions table
	public void setKnockoutValues(ArrayList<Integer> rowList, DefaultTableModel reactionsOptModel) {
		Vector<SBMLReaction> reactions = getAllReactions();
		Map<String, Object> reactionsIdRowMap = new HashMap<String, Object>();
		for (int i = 0; i < GraphicalInterface.reactionsTable.getRowCount(); i++) {
			reactionsIdRowMap.put((String) GraphicalInterface.reactionsTable.getModel().getValueAt(i, GraphicalInterfaceConstants.REACTIONS_ID_COLUMN), i);
		}
		LocalConfig.getInstance().setGdbbKnockoutsList(rowList);
		for (int j = 0; j < rowList.size(); j++) {
			int id = ((SBMLReaction) reactions.get(rowList.get(j))).getId();
			String row = (reactionsIdRowMap.get(Integer.toString(id))).toString();
			int rowNum = Integer.valueOf(row);	
			try {
				reactionsOptModel.setValueAt(GraphicalInterfaceConstants.BOOLEAN_VALUES[1], rowNum, GraphicalInterfaceConstants.KO_COLUMN);
			} catch (Exception e) {
				processStackTrace(e);
			}	
		}
	}
	
	/**
	 * @param args
	 */

	public Vector<String> getGeneAssociations() {
		Vector<SBMLReaction> reactions = getAllReactions();
		Vector<String> geneAssociations = new Vector<String>();

		if("SBML".equals(sourceType)){
			for (int i = 0; i < reactions.size(); i++) {
				int id = ((SBMLReaction) reactions.get(i)).getId();
				String geneAssoc = ((SBMLReaction) reactions.get(i)).getGeneAssociation();
				geneAssociations.add((Integer) reactionsIdPositionMap.get(id), geneAssoc);
			}
		}
		//System.out.println("gene assoc " + geneAssociations);

		return geneAssociations;
	}

	public Vector<String> getUniqueGeneAssociations() {
		Vector<String> geneAssociations = getGeneAssociations();
		Vector<String> uniqueGeneAssociations = new Vector<String>();

		if("SBML".equals(sourceType)){
			for (int i = 0; i < geneAssociations.size(); i++) {
				if (!uniqueGeneAssociations.contains(geneAssociations.get(i))) {
					uniqueGeneAssociations.add(geneAssociations.get(i));
				}
			}
		}
		//System.out.println("unique gene assoc " + uniqueGeneAssociations);
		
		return uniqueGeneAssociations;
	}

	public Vector<Double> getSyntheticObjectiveVector() {
		Vector<SBMLReaction> reactions = getAllReactions();
		Vector<Double> syntheticObjectiveVector = new Vector<Double>();

		if("SBML".equals(sourceType)){
			double max = 0;
			for (int i = 0; i < reactions.size(); i++) {
				int id = ((SBMLReaction) reactions.get(i)).getId();
				Double obj = ((SBMLReaction) reactions.get(i)).getSyntheticObjective();
				if (obj != 0) {
					max = obj;
				}
				syntheticObjectiveVector.add((Integer) reactionsIdPositionMap.get(id), obj);
			}
			if (max == 0 && !LocalConfig.getInstance().noSynObjWarningShown) {
				JOptionPane.showMessageDialog(null,                
						"No Synthetic Objective Set.",                
						"Warning",                                
						JOptionPane.WARNING_MESSAGE);
				LocalConfig.getInstance().noSynObjWarningShown = true;
			}
		}
		//System.out.println("syn" + syntheticObjectiveVector);
		
		return syntheticObjectiveVector;
	}

	public Vector<String> getReactionAbbreviations() {
		Vector<SBMLReaction> reactions = getAllReactions();
		Vector<String> reactionAbbreviations = new Vector<String>();

		if("SBML".equals(sourceType)){
			for (int i = 0; i < reactions.size(); i++) {
				int id = ((SBMLReaction) reactions.get(i)).getId();
				String reacAbbr = ((SBMLReaction) reactions.get(i)).getReactionAbbreviation();
				reactionAbbreviations.add((Integer) reactionsIdPositionMap.get(id), reacAbbr);
			}
		}
        //System.out.println(reactionAbbreviations);
		return reactionAbbreviations;
	}
	
	public ArrayList<Integer> reactionIdList() {
		ArrayList<Integer> reactionIdList = new ArrayList<Integer>();

		if("SBML".equals(sourceType)){
			for (int i = 0; i < GraphicalInterface.reactionsTable.getRowCount(); i++) {
				int id = Integer.valueOf((String) GraphicalInterface.reactionsTable.getModel().getValueAt(i, GraphicalInterfaceConstants.REACTIONS_ID_COLUMN));
				reactionIdList.add(id);
			}
		}

		return reactionIdList;
	}
	
	public static String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		ReactionFactory.columnName = columnName;
	}

	// get index of column with EC numbers
	public Integer locateECColumn() {
		int index = -1;
		for (int i = 0; i < LocalConfig.getInstance().getReactionsMetaColumnNames().size(); i++) {
			for (int j = 0; j < GraphicalInterfaceConstants.EC_NUMBER_COLUMN_NAMES.length; j++) {
				if (LocalConfig.getInstance().getReactionsMetaColumnNames().get(i).equals(GraphicalInterfaceConstants.EC_NUMBER_COLUMN_NAMES[j])) {
					index = GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length + i;
				}
			}
		}
		if (index == -1) {
			index = GraphicalInterfaceConstants.PROTEIN_CLASS_COLUMN;
		}

		return index;
	}

	// get index of column with Kegg Id
	public Integer locateKeggIdColumn() {
		int index = -1;
		for (int i = 0; i < LocalConfig.getInstance().getReactionsMetaColumnNames().size(); i++) {
			for (int j = 0; j < GraphicalInterfaceConstants.KEGG_ID_REACTIONS_COLUMN_NAMES.length; j++) {
				if (LocalConfig.getInstance().getReactionsMetaColumnNames().get(i).contains(GraphicalInterfaceConstants.KEGG_ID_REACTIONS_COLUMN_NAMES[j])) {
					index = GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length + i;
				}
			}
		}

		return index;
	}

	public Vector<SBMLReaction> getReactionsByCompartment(String comp) {
		Vector<SBMLReaction> reactions = getAllReactions();
		Vector<SBMLReaction> reactionsByCompartment = new Vector<SBMLReaction>();
		for (int i = 0; i < reactions.size(); i++) {
			if (reactions.get(i) != null && LocalConfig.getInstance().getReactionEquationMap().containsKey(reactions.get(i).getId())) {
				SBMLReactionEquation equn = (SBMLReactionEquation) LocalConfig.getInstance().getReactionEquationMap().get(reactions.get(i).getId());
				boolean containsProton = false;
				for (int q = 0; q < equn.getReactants().size(); q++) {
					//System.out.println(equn.getReactants().get(q).getMetaboliteAbbreviation());
					if (LocalConfig.getInstance().getMetaboliteIdKeggIdMap().get(Integer.toString(equn.getReactants().get(q).getMetaboliteId())) != null) {
						String keggId = LocalConfig.getInstance().getMetaboliteIdKeggIdMap().get(Integer.toString(equn.getReactants().get(q).getMetaboliteId()));
						if (keggId.equals("C00080")) {
							containsProton = true;
						}
					}
				}
				for (int p = 0; p < equn.getProducts().size(); p++) {
					if (LocalConfig.getInstance().getMetaboliteIdKeggIdMap().get(Integer.toString(equn.getProducts().get(p).getMetaboliteId())) != null) {
						String keggId = LocalConfig.getInstance().getMetaboliteIdKeggIdMap().get(Integer.toString(equn.getProducts().get(p).getMetaboliteId()));
						if (keggId.equals("C00080")) {
							containsProton = true;
						}
					}
				}
				ArrayList<String> compList = new ArrayList<String>();
				if (containsProton) {
					for (int q = 0; q < equn.getReactants().size(); q++) {
						if (LocalConfig.getInstance().getMetaboliteIdKeggIdMap().get(Integer.toString(equn.getReactants().get(q).getMetaboliteId())) != null) {
							String keggId = LocalConfig.getInstance().getMetaboliteIdKeggIdMap().get(Integer.toString(equn.getReactants().get(q).getMetaboliteId()));
							if (!keggId.equals("C00080")) {
								if (!compList.contains(equn.getReactants().get(q).getCompartment())) {
									compList.add(equn.getReactants().get(q).getCompartment());
								}
							}
						}
					}
					for (int p = 0; p < equn.getProducts().size(); p++) {
						if (LocalConfig.getInstance().getMetaboliteIdKeggIdMap().get(Integer.toString(equn.getProducts().get(p).getMetaboliteId())) != null) {
							String keggId = LocalConfig.getInstance().getMetaboliteIdKeggIdMap().get(Integer.toString(equn.getProducts().get(p).getMetaboliteId()));
							if (!keggId.equals("C00080")) {
								if (!compList.contains(equn.getProducts().get(p).getCompartment())) {
									compList.add(equn.getProducts().get(p).getCompartment());
								}
							}
						}
					}
				}
				equn.setCompartmentListIgnoreProton(compList);
				if ((equn.getCompartmentList().size() == 1 || 
					(equn.getCompartmentListIgnoreProton().size() == 1) && 
					equn.getCompartmentListIgnoreProton().contains(comp)) 
					&& equn.getCompartmentList().contains(comp)) {
					reactionsByCompartment.add(reactions.get(i));
				} 
			}
		}

		return reactionsByCompartment;
	}

	public Vector<SBMLReaction> getTransportReactionsByCompartments(String comp1, String comp2) {
		Vector<SBMLReaction> reactions = getAllReactions();
		Vector<SBMLReaction> transportReactionsByCompartments = new Vector<SBMLReaction>();
		for (int i = 0; i < reactions.size(); i++) {
			SBMLReactionEquation equn = (SBMLReactionEquation) LocalConfig.getInstance().getReactionEquationMap().get(reactions.get(i).getId());
			if (equn.getCompartmentList().contains(comp1) && equn.getCompartmentList().contains(comp2)) {
				//System.out.println(equn.getCompartmentList());
				transportReactionsByCompartments.add(reactions.get(i));
			}
		}
		//System.out.println(transportReactionsByCompartments);
		return transportReactionsByCompartments;
	}

	/**
	 * This condition only appears to occur in bacteria for a few reactions
	 * @param comp1
	 * @param comp2
	 * @return
	 */
	public Vector<SBMLReaction> getTransportReactionsByThreeCompartments(String comp1, String comp2, String comp3) {
		Vector<SBMLReaction> reactions = getAllReactions();
		Vector<SBMLReaction> transportReactionsByCompartments = new Vector<SBMLReaction>();
		for (int i = 0; i < reactions.size(); i++) {
			SBMLReactionEquation equn = (SBMLReactionEquation) LocalConfig.getInstance().getReactionEquationMap().get(reactions.get(i).getId());
			if (equn.getCompartmentList().contains(comp1) && equn.getCompartmentList().contains(comp2) && equn.getCompartmentList().contains(comp3)) {
				//System.out.println(equn.getCompartmentList());
				transportReactionsByCompartments.add(reactions.get(i));
			}
		}
		//System.out.println(transportReactionsByCompartments);
		return transportReactionsByCompartments;
	}

	private void processStackTrace( Exception e ) {
		//e.printStackTrace();
		StringWriter errors = new StringWriter();
		e.printStackTrace( new PrintWriter( errors ) );
		dialog.setErrorMessage(  "<html><p>" + errors.toString() + "</p></html>" );
		// centers dialog
		dialog.setLocationRelativeTo(null);
		dialog.setModal(true);
		dialog.setVisible( true );
	}

	public static void main(String[] args) {

	}

}
