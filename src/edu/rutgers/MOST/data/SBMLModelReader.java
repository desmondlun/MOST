package edu.rutgers.MOST.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import org.sbml.jsbml.*;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;
import edu.rutgers.MOST.presentation.ProgressConstants;

public class SBMLModelReader {

	private SBMLDocument doc;
	private boolean finished = false;

	private static DefaultTableModel metabolitesTableModel;

	public static DefaultTableModel getMetabolitesTableModel() {
		return metabolitesTableModel;
	}

	public static void setMetabolitesTableModel(
			DefaultTableModel metabolitesTableModel) {
		SBMLModelReader.metabolitesTableModel = metabolitesTableModel;
	}

	public synchronized boolean isFinished()
	{
		return finished;
	}
	private static DefaultTableModel reactionsTableModel;

	public static DefaultTableModel getReactionsTableModel() {
		return reactionsTableModel;
	}

	public static void setReactionsTableModel(DefaultTableModel reactionsTableModel) {
		SBMLModelReader.reactionsTableModel = reactionsTableModel;
	}
	
	public static Map<String, Object> metaboliteAbbreviationIdMap = new HashMap<String, Object>();
	// id name map used only to set metabolite names in SBMLReactants and SBMLProducts
	public static Map<Object, String> metaboliteIdNameMap = new HashMap<Object, String>();
	public static Map<Object, String> metaboliteIdCompartmentMap = new HashMap<Object, String>();
	private static Map<Object, ModelReactionEquation> reactionEquationMap = new HashMap<Object, ModelReactionEquation>();
	
	public static Map<String, Object> reactionAbbreviationIdMap = new HashMap<String, Object>();
	
	public SBMLModelReader(SBMLDocument doc) {
		this.doc = doc;
	}

	@SuppressWarnings("deprecation")
	public void load(){
		//String id = doc.getModel().getId(); 
		//System.out.println(id);
		
		LocalConfig.getInstance().getMetaboliteUsedMap().clear();
		
		ArrayList<SBMLCompartment> listOfCompartments = new ArrayList<SBMLCompartment>();
		for (int c = 0; c < doc.getModel().getListOfCompartments().size(); c++) {
			SBMLCompartment comp = new SBMLCompartment();
			comp.setId(doc.getModel().getCompartment(c).getId().toString());
			if (doc.getModel().getCompartment(c).getName() != null &&
					doc.getModel().getCompartment(c).getName().toString().length() > 0) {
				comp.setName(doc.getModel().getCompartment(c).getName().toString());
			} else {
				comp.setName(doc.getModel().getCompartment(c).getId().toString());
			}
			if (doc.getModel().getCompartment(c).getOutside() != null &&
					doc.getModel().getCompartment(c).getOutside().toString().length() > 0) {
				comp.setOutside(doc.getModel().getCompartment(c).getOutside().toString());
			} 
			listOfCompartments.add(comp);
		}
		LocalConfig.getInstance().setListOfCompartments(listOfCompartments);
		
		//ArrayList<String> compartmentsList = new ArrayList<String>();
		ArrayList<String> metabKeggIds = new ArrayList<String>();
		ArrayList<String> reacKeggIds = new ArrayList<String>();
		ArrayList<String> chebiIds = new ArrayList<String>();
		
		boolean metabKeggIdAnnotationSet = false;
		boolean reacKeggIdAnnotationSet = false;
		boolean chebiIdAnnotationSet = false;

		DefaultTableModel metabTableModel = new DefaultTableModel();
		for (int m = 0; m < GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length; m++) {
			metabTableModel.addColumn(GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES[m]);
		}
		LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().clear();
		Map<String, Object> metaboliteNameIdMap = new HashMap<String, Object>();

		ArrayList<String> metabolitesMetaColumnNames = new ArrayList<String>();
		ListOf<Species> metabolites = doc.getModel().getListOfSpecies();
		for (int i = 0; i < metabolites.size(); i++) {
			if (i%10 == 0) {
				LocalConfig.getInstance().setProgress((i * ProgressConstants.METABOLITE_LOAD_PERCENT) / metabolites.size()
						+ ProgressConstants.SBML_LOAD_PERCENT);		
			}
			
			String charge = "";
			Vector <String> metabRow = new Vector<String>();
			metabRow.add(Integer.toString(i));
			String metabAbbr = metabolites.get(i).getId();
			metabRow.add(metabAbbr);
			metaboliteAbbreviationIdMap.put(metabolites.get(i).getId(), new Integer(i));
			String metabName = metabolites.get(i).getName();
			metabRow.add(metabName);	
			metaboliteIdNameMap.put(new Integer(i), metabolites.get(i).getName());
			metaboliteIdCompartmentMap.put(new Integer(i), metabolites.get(i).getCompartment());
			metaboliteNameIdMap.put(metabolites.get(i).getId(), new Integer(i));
			if (metabolites.get(i).isSetCharge()) {
				charge = Integer.toString(metabolites.get(i).getCharge());
			}
			
			Map<String, String> metabolitesMetaColumnMap = new HashMap<String, String>();
			if (metabolites.get(i).isSetNotes()) {
				ArrayList<String> metabNoteItemList = new ArrayList<String>();

				for (int u = 0; u < metabolites.get(i).getNotes().getChildCount(); u++) {
					if (!metabolites.get(i).getNotes().getChildAt(u).getName().isEmpty()) {
						String noteString = metabolites.get(i).getNotes().getChildAt(u).toXMLString();
						String noteItem = "";
						//removes xmlns (xml namespace tags)
						if (noteString.contains("xmlns")) {
							noteString = noteString.substring(noteString.indexOf('>') + 1, noteString.lastIndexOf('<'));
							String endtag = noteString.substring(noteString.lastIndexOf('<'));
							String[] nameSpaces = noteString.split(endtag);
							for (int n = 0; n < nameSpaces.length; n++) {
								noteItem = nameSpaces[n].substring(nameSpaces[n].indexOf('>') + 1); 
								metabNoteItemList.add(noteItem);
							}
						} else {
							//for "<>", "</>" types of nodes, tags are removed
							noteItem = noteString.substring(noteString.indexOf('>') + 1, noteString.lastIndexOf('<'));
							metabNoteItemList.add(noteItem);
						}
					}
				}

				if (i == 0) {
					//set list of notes names to meta columns			
					for (int n = 0; n < metabNoteItemList.size(); n++) {
						if (metabNoteItemList.get(n).contains(":")) {
							//accounts for condition of multiple ":"
							String columnName = metabNoteItemList.get(n).substring(0, metabNoteItemList.get(n).indexOf(":"));
							boolean contains = false;

							if (metabolitesMetaColumnNames.contains(columnName)) {
								Object[] options = {"    Yes    ", "    No    ",};
								int choice = JOptionPane.showOptionDialog(null, "Duplicate " + columnName + " columns. " +
										SBMLConstants.DUPLICATE_COLUMN_ERROR_MESSAGE,
										SBMLConstants.DUPLICATE_METAB_COLUMN_ERROR_TITLE, 
										JOptionPane.YES_NO_OPTION, 
										JOptionPane.QUESTION_MESSAGE, 
										null, options, options[0]);
								// set lower bound to 0 and set new equation
								if (choice == JOptionPane.YES_OPTION) {
									String displayName = columnName;
									Object[] options2 = {"    Yes    ", "    No    ",};
									int choice2 = JOptionPane.showOptionDialog(null, 
											SBMLConstants.RENAME_COLUMN_MESSAGE + displayName + " and " 
											+ displayName + SBMLConstants.DUPLICATE_COLUMN_SUFFIX + ".", 
											SBMLConstants.DUPLICATE_METAB_COLUMN_ERROR_TITLE, 
											JOptionPane.YES_NO_OPTION, 
											JOptionPane.QUESTION_MESSAGE, 
											null, options2, options2[0]);
									// set lower bound to 0 and set new equation
									if (choice2 == JOptionPane.YES_OPTION) {
										displayName = JOptionPane.showInputDialog(null, "Enter Column Name", GraphicalInterfaceConstants.COLUMN_RENAME_INTERFACE_TITLE, JOptionPane.WARNING_MESSAGE);
										if (displayName != null && displayName.length() > 0) {
											//duplicateMetabColumnMap.put(new Integer(metaColCount + 1), displayName);
										} else {
											// If user hits cancel or doesn't enter anything default name assigned
											displayName = columnName + SBMLConstants.DUPLICATE_COLUMN_SUFFIX;
											//duplicateMetabColumnMap.put(new Integer(metaColCount + 1), displayName);
										}		
									}
									if (choice2 == JOptionPane.NO_OPTION) {													
										displayName = columnName + SBMLConstants.DUPLICATE_COLUMN_SUFFIX;
										//duplicateMetabColumnMap.put(new Integer(metaColCount + 1), displayName);

									}
								}
								if (choice == JOptionPane.NO_OPTION) {
									//LocalConfig.getInstance().getHiddenMetabolitesColumns().add(GraphicalInterfaceConstants.METABOLITES_DB_COLUMN_NAMES.length + metaColCount);
								}
							}

							for (int s = 0; s < SBMLConstants.METABOLITES_IGNORE_LIST.length; s++) {
								if ((SBMLConstants.METABOLITES_IGNORE_LIST[s].compareTo(columnName.trim()) == 0)) {
									contains = true;
									break;						
								}
							}
							if (!contains) {
								metabolitesMetaColumnNames.add(columnName);
							}	
						}
					}
				}
				for (int n = 0; n < metabNoteItemList.size(); n++) {
					if (metabNoteItemList.get(n).contains(":")) {
						//accounts for condition of multiple ":"
						String columnName = metabNoteItemList.get(n).substring(0, metabNoteItemList.get(n).indexOf(":"));
						String value = "";
						if (metabNoteItemList.get(n).substring(metabNoteItemList.get(n).indexOf(":") + 1).contains("'")) {
							value = metabNoteItemList.get(n).substring(metabNoteItemList.get(n).indexOf(":") + 1).replaceAll("'", "''");
						} else {
							value = metabNoteItemList.get(n).substring(metabNoteItemList.get(n).indexOf(":") + 1);
						}
						if (!(columnName.trim().equals("CHARGE"))) {
							if (metabolitesMetaColumnNames.contains(columnName)) {
								metabolitesMetaColumnMap.put(columnName, value.trim());
							}
						// some models have charge in notes, but do not want empty charge
						// value in notes to overwrite previous value from species
						} else {
							if (charge == null || charge.trim().length() == 0) {
								charge = value;
							}							
						}
					}
				}
			} 
			metabRow.add(charge);	
			metabRow.add(metabolites.get(i).getCompartment());
			String boundary = "";
			if (!metabolites.get(i).getBoundaryCondition()) {
				boundary = GraphicalInterfaceConstants.BOOLEAN_VALUES[0]; 
			} else {
				boundary = GraphicalInterfaceConstants.BOOLEAN_VALUES[1];
			}
			metabRow.add(boundary);

			for (int m = 0; m < metabolitesMetaColumnNames.size(); m++) {
				if (i == 0) {
					metabTableModel.addColumn(metabolitesMetaColumnNames.get(m));
				}
				metabRow.add(metabolitesMetaColumnMap.get(metabolitesMetaColumnNames.get(m)));
			}	
			
			String keggId = "";
			String chebiId = "";
			if (metabolites.get(i).isSetAnnotation()) {
				//metabKeggIdAnnotationSet = true;
				if (metabolites.get(i).getAnnotationString().contains("kegg.compound")) {
					metabKeggIdAnnotationSet = true;
					// first species may not contain kegg id, later species may
					if (!metabolitesMetaColumnNames.contains(GraphicalInterfaceConstants.METABOLITE_KEGG_ID_COLUMN_NAME)) {
					//if (i == 0) {
						metabolitesMetaColumnNames.add(GraphicalInterfaceConstants.METABOLITE_KEGG_ID_COLUMN_NAME);
						metabTableModel.addColumn(GraphicalInterfaceConstants.METABOLITE_KEGG_ID_COLUMN_NAME);
					} 
					keggId = metabolites.get(i).getAnnotationString().substring(metabolites.get(i).getAnnotationString().indexOf("kegg.compound") + 14, metabolites.get(i).getAnnotationString().indexOf("kegg.compound") + 20);
				} 
				if (metabolites.get(i).getAnnotationString().contains("chebi/CHEBI")) {
					chebiIdAnnotationSet = true;
					// first species may not contain chebi id, later species may
					if (!metabolitesMetaColumnNames.contains(GraphicalInterfaceConstants.CHEBI_ID_COLUMN_NAME)) {
//					//if (i == 0) {
						metabolitesMetaColumnNames.add(GraphicalInterfaceConstants.CHEBI_ID_COLUMN_NAME);
						metabTableModel.addColumn(GraphicalInterfaceConstants.CHEBI_ID_COLUMN_NAME);
					} 
					chebiId = metabolites.get(i).getAnnotationString().substring(metabolites.get(i).getAnnotationString().indexOf("chebi/CHEBI") + 12, metabolites.get(i).getAnnotationString().indexOf("chebi/CHEBI") + 17);
					if (chebiId.endsWith("\"")) {
						chebiId = chebiId.replace("\"", "");
					}
				} 
			}
			metabKeggIds.add(keggId);
			chebiIds.add(chebiId);
			
			metabTableModel.addRow(metabRow);
		}
		
		if (metabKeggIdAnnotationSet) {
			int keggIdColIndex = GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length + metabolitesMetaColumnNames.indexOf(GraphicalInterfaceConstants.METABOLITE_KEGG_ID_COLUMN_NAME);
			for (int k = 0; k < metabKeggIds.size(); k++) {
				metabTableModel.setValueAt(metabKeggIds.get(k), k, keggIdColIndex);
			}
		}
		if (chebiIdAnnotationSet) {
			int chebiIdColIndex = GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length + metabolitesMetaColumnNames.indexOf(GraphicalInterfaceConstants.CHEBI_ID_COLUMN_NAME);
			for (int k = 0; k < chebiIds.size(); k++) {
				metabTableModel.setValueAt(chebiIds.get(k), k, chebiIdColIndex);
			}
		}
		
		setMetabolitesTableModel(metabTableModel);
		LocalConfig.getInstance().setMaxMetabolite(metabolites.size());
		LocalConfig.getInstance().setMaxMetaboliteId(metabolites.size());
		LocalConfig.getInstance().setMetabolitesMetaColumnNames(metabolitesMetaColumnNames);
        // end metabolites read
		
		boolean containsMinFlux = false;
		boolean containsMaxFlux = false;
		
		// begin reactions read
		DefaultTableModel reacTableModel = new DefaultTableModel();
		for (int r = 0; r < GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length; r++) {
			reacTableModel.addColumn(GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[r]);
		}
		
		LocalConfig.getInstance().getReactionAbbreviationIdMap().clear();

		ArrayList<String> reactionsMetaColumnNames = new ArrayList<String>();
		ListOf<Reaction> reactions = doc.getModel().getListOfReactions();
		ArrayList<ArrayList<String>> listOfCompartmentLists = new ArrayList<ArrayList<String>>();
		boolean locusColumnName = false;
		for (int j = 0; j < reactions.size(); j++) {
			if (j%10 == 0) {
				LocalConfig.getInstance().setProgress((j * ProgressConstants.REACTION_LOAD_PERCENT) / reactions.size() 
						+ ProgressConstants.METABOLITE_LOAD_PERCENT + ProgressConstants.SBML_LOAD_PERCENT);		
			}
		
			String fluxValue = GraphicalInterfaceConstants.FLUX_VALUE_DEFAULT_STRING;
			String minFlux = GraphicalInterfaceConstants.MIN_FLUX_DEFAULT_STRING;
			String maxFlux = GraphicalInterfaceConstants.MAX_FLUX_DEFAULT_STRING;
			String geneAssociation = "";
			String proteinAssociation = "";
			String subsystem = "";
			String proteinClass = "";
			String lowerBound = GraphicalInterfaceConstants.LOWER_BOUND_DEFAULT_IRREVERBIBLE_STRING;
			String upperBound = GraphicalInterfaceConstants.UPPER_BOUND_DEFAULT_STRING;
			String biologicalObjective = GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_DEFAULT_STRING;
			String syntheticObjective = GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_DEFAULT_STRING;
			
			Vector <String> reacRow = new Vector<String>();
			reacRow.add(Integer.toString(j));
			reacRow.add(GraphicalInterfaceConstants.BOOLEAN_VALUES[0]);
			if (reactions.get(j).isSetKineticLaw()) {
				for(int k = 0; k < reactions.get(j).getKineticLaw().getListOfLocalParameters().size(); k++) {
					if (reactions.get(j).getKineticLaw().getListOfLocalParameters().get(k).getId().matches("FLUX_VALUE")) {
						fluxValue = Double.toString(reactions.get(j).getKineticLaw().getLocalParameter("FLUX_VALUE").getValue());
					}
				}				
			}
			reacRow.add(fluxValue);
			reacRow.add(minFlux);
			reacRow.add(maxFlux);
			
			reacRow.add(reactions.get(j).getId());
			reactionAbbreviationIdMap.put(reactions.get(j).getId(), new Integer(j));
			reacRow.add(reactions.get(j).getName());

			String reversible = "";
			if (!reactions.get(j).getReversible()) {
				reversible = GraphicalInterfaceConstants.BOOLEAN_VALUES[0];
			} else {
				reversible = GraphicalInterfaceConstants.BOOLEAN_VALUES[1];
				// set lower bound to default reversible so if lower bound is not found in file
				// it will not be 0
				lowerBound = GraphicalInterfaceConstants.LOWER_BOUND_DEFAULT_REVERSIBLE_STRING;
			}

			SBMLReactionEquation equation = new SBMLReactionEquation();
			ArrayList<SBMLReactant> equnReactants = new ArrayList<SBMLReactant>();
			ArrayList<SBMLProduct> equnProducts = new ArrayList<SBMLProduct>();
			ArrayList<String> compartmentList = new ArrayList<String>();
			ArrayList<String> compartmentReactantsList = new ArrayList<String>();
			ArrayList<String> compartmentProductsList = new ArrayList<String>();
			
			ListOf<SpeciesReference> reactants = reactions.get(j).getListOfReactants();
			
			int id = 0;
			for (int r = 0; r < reactants.size(); r++) {
				if (reactants.get(r).isSetSpecies()) {
					// if a metabolite is missing in the model, but used in an equation, 
					// null error caught here
					if (metaboliteAbbreviationIdMap.get(reactants.get(r).getSpecies()) == null) {
						// add missing metabolite to maps and model
						id = LocalConfig.getInstance().getMaxMetabolite() + 1;
						LocalConfig.getInstance().setMaxMetabolite(id);
						LocalConfig.getInstance().setMaxMetaboliteId(id);
						metaboliteAbbreviationIdMap.put(reactants.get(r).getSpecies(), id);
						LocalConfig.getInstance().setMetaboliteAbbreviationIdMap(metaboliteAbbreviationIdMap);
						Vector <String> metabRow = new Vector<String>();
						metabRow.add(Integer.toString(id));
						metabRow.add(reactants.get(r).getSpecies());
						metabRow.add("");
						metabRow.add("");
						metabRow.add("");
						metabRow.add(GraphicalInterfaceConstants.BOUNDARY_DEFAULT);
						getMetabolitesTableModel().addRow(metabRow);
					} else {
						id = (Integer) metaboliteAbbreviationIdMap.get(reactants.get(r).getSpecies());									
					}
					if (LocalConfig.getInstance().getMetaboliteUsedMap().containsKey(reactants.get(r).getSpecies())) {
						int usedCount = (Integer) LocalConfig.getInstance().getMetaboliteUsedMap().get(reactants.get(r).getSpecies());
						LocalConfig.getInstance().getMetaboliteUsedMap().put(reactants.get(r).getSpecies(), new Integer(usedCount + 1));
					} else {
						LocalConfig.getInstance().getMetaboliteUsedMap().put(reactants.get(r).getSpecies(), new Integer(1));
					}
				}
				SBMLReactant reactant = new SBMLReactant();
				reactant.setReactionId(j);
				reactant.setMetaboliteId(id);				
				reactant.setStoic(reactants.get(r).getStoichiometry());
				reactant.setMetaboliteAbbreviation(reactants.get(r).getSpecies());
				reactant.setMetaboliteName(metaboliteIdNameMap.get(id));
				reactant.setCompartment(metaboliteIdCompartmentMap.get(id));
				if (!compartmentList.contains(metaboliteIdCompartmentMap.get(id))) {
					compartmentList.add(metaboliteIdCompartmentMap.get(id));
				}
				if (!compartmentReactantsList.contains(metaboliteIdCompartmentMap.get(id))) {
					compartmentReactantsList.add(metaboliteIdCompartmentMap.get(id));
				}
				equnReactants.add(reactant);
			}

			ListOf<SpeciesReference> products = reactions.get(j).getListOfProducts();
			for (int p = 0; p < products.size(); p++) {
				if (products.get(p).isSetSpecies()) {
					// if a metabolite is missing in the model, but used in an equation, 
					// null error caught here
					if (metaboliteAbbreviationIdMap.get(products.get(p).getSpecies()) == null) {
						// add missing metabolite to maps and model
						id = LocalConfig.getInstance().getMaxMetabolite() + 1;
						LocalConfig.getInstance().setMaxMetabolite(id);
						LocalConfig.getInstance().setMaxMetaboliteId(id);
						metaboliteAbbreviationIdMap.put(products.get(p).getSpecies(), id);
						LocalConfig.getInstance().setMetaboliteAbbreviationIdMap(metaboliteAbbreviationIdMap);
						Vector <String> metabRow = new Vector<String>();
						metabRow.add(Integer.toString(id));
						metabRow.add(products.get(p).getSpecies());
						metabRow.add("");
						metabRow.add("");
						metabRow.add("");
						metabRow.add(GraphicalInterfaceConstants.BOUNDARY_DEFAULT);
						getMetabolitesTableModel().addRow(metabRow);
					} else {
						id = (Integer) metaboliteAbbreviationIdMap.get(products.get(p).getSpecies());									
					}
					if (LocalConfig.getInstance().getMetaboliteUsedMap().containsKey(products.get(p).getSpecies())) {
						int usedCount = (Integer) LocalConfig.getInstance().getMetaboliteUsedMap().get(products.get(p).getSpecies());
						LocalConfig.getInstance().getMetaboliteUsedMap().put(products.get(p).getSpecies(), new Integer(usedCount + 1));
					} else {
						LocalConfig.getInstance().getMetaboliteUsedMap().put(products.get(p).getSpecies(), new Integer(1));
					}
				}
				SBMLProduct product = new SBMLProduct();
				product.setReactionId(j);
				product.setMetaboliteId(id);				
				product.setStoic(products.get(p).getStoichiometry());
				product.setMetaboliteAbbreviation(products.get(p).getSpecies());
				product.setMetaboliteName(metaboliteIdNameMap.get(id));
				product.setCompartment(metaboliteIdCompartmentMap.get(id));
				if (!compartmentList.contains(metaboliteIdCompartmentMap.get(id))) {
					compartmentList.add(metaboliteIdCompartmentMap.get(id));
				}
				if (!compartmentProductsList.contains(metaboliteIdCompartmentMap.get(id))) {
					compartmentProductsList.add(metaboliteIdCompartmentMap.get(id));
				}
				equnProducts.add(product);
			}
            equation.setReactants(equnReactants);
            equation.setProducts(equnProducts);
            equation.setReversible(reversible);
            equation.setReversibleArrow(GraphicalInterfaceConstants.REVERSIBLE_ARROWS[0]);
            equation.setIrreversibleArrow(GraphicalInterfaceConstants.NOT_REVERSIBLE_ARROWS[1]);
            equation.writeReactionEquation();
            equation.setCompartmentList(compartmentList);
            if (compartmentList.size()> 1) {
            	Collections.sort(compartmentList);
            	if (!listOfCompartmentLists.contains(compartmentList)) {
            		listOfCompartmentLists.add(compartmentList);
            	}
            }
            equation.setCompartmentReactantsList(compartmentReactantsList);
            equation.setCompartmentProductsList(compartmentProductsList);
            reactionEquationMap.put(j, equation);

			String reactionEquationAbbr = equation.equationAbbreviations;
			reacRow.add(reactionEquationAbbr);
			String reactionEquationNames = equation.equationNames;
			reacRow.add(reactionEquationNames);	
			
			reacRow.add(reversible);

			if (reactions.get(j).isSetKineticLaw()) {
				for(int k = 0; k < reactions.get(j).getKineticLaw().getListOfLocalParameters().size(); k++) {
					if (reactions.get(j).getKineticLaw().getListOfLocalParameters().get(k).getId().matches("LOWER_BOUND")) {
						lowerBound = Double.toString(reactions.get(j).getKineticLaw().getLocalParameter("LOWER_BOUND").getValue());
						// if reaction is not reversible and lower bound < 0, lower bound set to 0
						if (reactions.get(j).getKineticLaw().getLocalParameter("LOWER_BOUND").getValue() < 0.0 && reversible.equals(GraphicalInterfaceConstants.BOOLEAN_VALUES[0])) {
							// no longer printing exception. incorrect values highlighted, message displayed at
							// end of load and on status bar
							LocalConfig.getInstance().getInvalidLowerBoundReversibleCombinations().add(j);
//SBML override //			lowerBound = GraphicalInterfaceConstants.LOWER_BOUND_DEFAULT_IRREVERBIBLE_STRING;
//							reversible = GraphicalInterfaceConstants.BOOLEAN_VALUES[1];
//							Exception e = new Exception( "Forcing reversibility to conform to flux bound" );
//							if( GraphicalInterfaceConstants.DEBUG_MODE ) e.printStackTrace();
						}
					} else if (reactions.get(j).getKineticLaw().getListOfLocalParameters().get(k).getName().matches("LOWER_BOUND")) {
						lowerBound = Double.toString(reactions.get(j).getKineticLaw().getListOfLocalParameters().get(k).getValue());
						// if reaction is not reversible and lower bound < 0, lower bound set to 0
						if (reactions.get(j).getKineticLaw().getListOfLocalParameters().get(k).getValue() < 0.0 && reversible.equals(GraphicalInterfaceConstants.BOOLEAN_VALUES[0])) {
							lowerBound = GraphicalInterfaceConstants.LOWER_BOUND_DEFAULT_IRREVERBIBLE_STRING;
						}
					}
					if (reactions.get(j).getKineticLaw().getListOfLocalParameters().get(k).getId().matches("UPPER_BOUND")) {
						upperBound = Double.toString(reactions.get(j).getKineticLaw().getLocalParameter("UPPER_BOUND").getValue());			
					} else if (reactions.get(j).getKineticLaw().getListOfLocalParameters().get(k).getName().matches("UPPER_BOUND")) {
						upperBound = Double.toString(reactions.get(j).getKineticLaw().getListOfLocalParameters().get(k).getValue());
					}
					if (reactions.get(j).getKineticLaw().getListOfLocalParameters().get(k).getId().matches("OBJECTIVE_COEFFICIENT")) {
						biologicalObjective = Double.toString(reactions.get(j).getKineticLaw().getLocalParameter("OBJECTIVE_COEFFICIENT").getValue());			
					} else if (reactions.get(j).getKineticLaw().getListOfLocalParameters().get(k).getName().matches("OBJECTIVE_COEFFICIENT")) {
						biologicalObjective = Double.toString(reactions.get(j).getKineticLaw().getListOfLocalParameters().get(k).getValue());
					}
				}
			} 
			
			reacRow.add(lowerBound);
			reacRow.add(upperBound);
			reacRow.add(biologicalObjective);
			
			Map<String, String> reactionsMetaColumnMap = new HashMap<String, String>();
			if (reactions.get(j).isSetNotes()) {
				ArrayList<String> noteItemList = new ArrayList<String>();
				for (int u = 0; u < reactions.get(j).getNotes().getChildCount(); u++) {
					if (!reactions.get(j).getNotes().getChildAt(u).getName().isEmpty()) {
						String noteString = reactions.get(j).getNotes().getChildAt(u).toXMLString();
						String noteItem = "";
						//removes xmlns (xml namespace tags)
						if (noteString.contains("xmlns")) {
							if (!noteString.endsWith("/>")) {
								if (noteString.contains("<p/>")) {
									noteString = noteString.replace("<p/>", "");
								}
								noteString = noteString.substring(noteString.indexOf(">") + 1, noteString.lastIndexOf("<"));
								if (noteString.contains("<")) {
									String endtag = noteString.substring(noteString.lastIndexOf("<"));
									//System.out.println("endtag " + endtag);
									String[] nameSpaces = noteString.split(endtag);
									for (int n = 0; n < nameSpaces.length; n++) {
										noteItem = nameSpaces[n].substring(nameSpaces[n].indexOf(">") + 1); 
										//System.out.println(noteItem);
										noteItemList.add(noteItem);										
									}
								} else {
									noteItemList.add(noteString);
								}
							}
						} else {
							if ((noteString.indexOf(">") + 1) < noteString.lastIndexOf("<")) {
								//for "<>", "</>" types of nodes, tags are removed
								noteItem = noteString.substring(noteString.indexOf(">") + 1, noteString.lastIndexOf("<"));
								noteItemList.add(noteItem);	
							} else if (noteString.contains("listOfGenes")) {
								noteItemList.add("listOfGenes");
							}
						}	
					}
				}

				StringBuffer locusBfr = new StringBuffer();
				if (j == 0) {
					//set list of notes names to meta columns							
					boolean genes = false;
					for (int n = 0; n < noteItemList.size(); n++) {
						if (noteItemList.get(n).contains(":")) {
							//accounts for condition of multiple ":"
							String columnName = noteItemList.get(n).substring(0, noteItemList.get(n).indexOf(":"));
							boolean contains = false;

							if (columnName.compareTo("genes") == 0) {
								genes = true;
							}
							if (genes) {
								if (columnName.compareTo("LOCUS") == 0) {
									locusColumnName = true;
								}
							}
							if (reactionsMetaColumnNames.contains(columnName)) {
								String displayName = columnName;
								if (locusColumnName) {
									displayName = SBMLConstants.LOCUS_COLUMN_DISPLAY_NAME;
									//LocalConfig.getInstance().getHiddenReactionsColumns().add(GraphicalInterfaceConstants.REACTIONS_DB_COLUMN_NAMES.length + metaColCount);
								} else {
									Object[] options = {"    Yes    ", "    No    ",};
									int choice = JOptionPane.showOptionDialog(null, "Duplicate " + displayName + " columns. " +
											SBMLConstants.DUPLICATE_COLUMN_ERROR_MESSAGE,
											SBMLConstants.DUPLICATE_REAC_COLUMN_ERROR_TITLE, 
											JOptionPane.YES_NO_OPTION, 
											JOptionPane.QUESTION_MESSAGE, 
											null, options, options[0]);
									// set lower bound to 0 and set new equation
									if (choice == JOptionPane.YES_OPTION) {					
										Object[] options2 = {"    Yes    ", "    No    ",};
										int choice2 = JOptionPane.showOptionDialog(null, 
												SBMLConstants.RENAME_COLUMN_MESSAGE + displayName + " and " 
												+ displayName + SBMLConstants.DUPLICATE_COLUMN_SUFFIX + ".", 
												SBMLConstants.DUPLICATE_REAC_COLUMN_ERROR_TITLE, 
												JOptionPane.YES_NO_OPTION, 
												JOptionPane.QUESTION_MESSAGE, 
												null, options2, options2[0]);
										// set lower bound to 0 and set new equation
										if (choice2 == JOptionPane.YES_OPTION) {
											displayName = JOptionPane.showInputDialog(null, "Enter Column Name", GraphicalInterfaceConstants.COLUMN_RENAME_INTERFACE_TITLE, JOptionPane.WARNING_MESSAGE);
											if (displayName != null && displayName.length() > 0) {
												//duplicateReacColumnMap.put(new Integer(metaColCount + 1), displayName);
											} else {
												// If user hits cancel or doesn't enter anything default name assigned
												displayName = columnName + SBMLConstants.DUPLICATE_COLUMN_SUFFIX;
												//duplicateReacColumnMap.put(new Integer(metaColCount + 1), displayName);
											}
										}
										if (choice2 == JOptionPane.NO_OPTION) {								
											displayName = columnName + SBMLConstants.DUPLICATE_COLUMN_SUFFIX;
											//duplicateReacColumnMap.put(new Integer(metaColCount + 1), displayName);											
										}
									}
									if (choice == JOptionPane.NO_OPTION) {
										
									}
								}										
							}

							for (int s = 0; s < SBMLConstants.REACTIONS_IGNORE_LIST.length; s++) {
								if ((SBMLConstants.REACTIONS_IGNORE_LIST[s].compareTo(columnName.trim()) == 0)) {
									contains = true;
									break;						
								}
							}
							if (!contains) {
								if (!reactionsMetaColumnNames.contains(columnName)) {
									reactionsMetaColumnNames.add(columnName);
								}					
							}
						}
					}
				}
				for (int n = 0; n < noteItemList.size(); n++) {
					if (noteItemList.get(n).contains(":")) {
						//accounts for condition of multiple ":"
						String columnName = noteItemList.get(n).substring(0, noteItemList.get(n).indexOf(":"));
						String value = "";
						if (locusColumnName && reactionsMetaColumnNames.size() > 0 && columnName.compareTo("LOCUS") == 0) {
							value = noteItemList.get(n).substring(noteItemList.get(n).indexOf(":") + 1);
							locusBfr.append("LOCUS:" + value + ", ");
						}
					}
				}
				String locusBfrStr = locusBfr.toString();
				if (locusBfrStr.length() > 1) {
					locusBfrStr = locusBfrStr.substring(0, locusBfrStr.length() - 2);
				}
							
				for (int n = 0; n < noteItemList.size(); n++) {
					if (noteItemList.get(n).contains(":")) {
						//accounts for condition of multiple ":"
						String columnName = noteItemList.get(n).substring(0, noteItemList.get(n).indexOf(":"));
						String value = "";
						if (noteItemList.get(n).substring(noteItemList.get(n).indexOf(":") + 1).contains("'")) {
							value = noteItemList.get(n).substring(noteItemList.get(n).indexOf(":") + 1).replaceAll("'", "''");
						} else {
							value = noteItemList.get(n).substring(noteItemList.get(n).indexOf(":") + 1);
						}
						if (columnName.compareTo("GENE ASSOCIATION") == 0 || columnName.compareTo("GENE_ASSOCIATION") == 0) {
							geneAssociation = value.trim();
						}
						if (columnName.compareTo("PROTEIN ASSOCIATION") == 0 || columnName.compareTo("PROTEIN_ASSOCIATION") == 0) {
							proteinAssociation = value.trim();
						}
						if (columnName.compareTo("SUBSYSTEM") == 0) {
							subsystem = value.trim();
						}
						if (columnName.compareTo("PROTEIN CLASS") == 0 || columnName.compareTo("PROTEIN_CLASS") == 0) {
							proteinClass = value.trim();
						} 
						if (columnName.compareTo("SYNTHETIC_OBJECTIVE") == 0 || columnName.compareTo("SYNTHETIC OBJECTIVE") == 0) {
							syntheticObjective = value.trim();
						}
						if (columnName.compareTo(SBMLConstants.MIN_FLUX_NOTES_NAME) == 0) {
							minFlux = value.trim();
							reacRow.set(GraphicalInterfaceConstants.MIN_FLUX_COLUMN, minFlux);
							containsMinFlux = true;
						}
						if (columnName.compareTo(SBMLConstants.MAX_FLUX_NOTES_NAME) == 0) {
							maxFlux = value.trim();
							reacRow.set(GraphicalInterfaceConstants.MAX_FLUX_COLUMN, maxFlux);
							containsMaxFlux = true;
						}
						if (columnName.compareTo("LOCUS") == 0) {
							reactionsMetaColumnMap.put("Genes", locusBfrStr);
						} else {
							if (reactionsMetaColumnNames.contains(columnName)) {
								reactionsMetaColumnMap.put(columnName, value.trim());
							}							
						}					    
					}					
				}
			}
			reacRow.add(syntheticObjective);
			reacRow.add(geneAssociation);
			reacRow.add(proteinAssociation);
			reacRow.add(subsystem);
			reacRow.add(proteinClass);
			
			// remove duplicate column names (should only be duplicate LOCUS)
			ArrayList<String> reactionsMetaColumnNames2 = new ArrayList<String>();
			for (int m = 0; m < reactionsMetaColumnNames.size(); m++) {
				if (!reactionsMetaColumnNames2.contains(reactionsMetaColumnNames.get(m))) {
					reactionsMetaColumnNames2.add(reactionsMetaColumnNames.get(m));
				}
			}
			for (int m = 0; m < reactionsMetaColumnNames2.size(); m++) {
				if (reactionsMetaColumnNames2.get(m).compareTo("LOCUS") == 0) {
					if (j == 0) {
						reacTableModel.addColumn(SBMLConstants.LOCUS_COLUMN_DISPLAY_NAME);
					}
					reacRow.add(reactionsMetaColumnMap.get(SBMLConstants.LOCUS_COLUMN_DISPLAY_NAME));
				} else {
					if (j == 0) {
						reacTableModel.addColumn(reactionsMetaColumnNames2.get(m));
						//System.out.println("add reac col " + reactionsMetaColumnNames2);
					}
					reacRow.add(reactionsMetaColumnMap.get(reactionsMetaColumnNames2.get(m)));
				}
			}	
			
			String keggId = "";
			if (reactions.get(j).isSetAnnotation()) {
				reacKeggIdAnnotationSet = true;
				if (reactions.get(j).getAnnotationString().contains("kegg.reaction")) {
					// first reaction may not contain kegg id, later reactions may
					if (!reactionsMetaColumnNames.contains(GraphicalInterfaceConstants.REACTION_KEGG_ID_COLUMN_NAME)) {
					//if (j == 0) {
						reactionsMetaColumnNames.add(GraphicalInterfaceConstants.REACTION_KEGG_ID_COLUMN_NAME);
						reacTableModel.addColumn(GraphicalInterfaceConstants.REACTION_KEGG_ID_COLUMN_NAME);
						LocalConfig.getInstance().setKeggReactionIdColumnName(GraphicalInterfaceConstants.REACTION_KEGG_ID_COLUMN_NAME);
					} 
					keggId = reactions.get(j).getAnnotationString().substring(reactions.get(j).getAnnotationString().indexOf("kegg.reaction") + 14, reactions.get(j).getAnnotationString().indexOf("kegg.reaction") + 20);
				}
			}
			reacKeggIds.add(keggId);
			
			reacTableModel.addRow(reacRow);
		}
		
		if (reacKeggIdAnnotationSet) {
			int keggIdColIndex = GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length + reactionsMetaColumnNames.indexOf(GraphicalInterfaceConstants.REACTION_KEGG_ID_COLUMN_NAME);
			for (int k = 0; k < reacKeggIds.size(); k++) {
				reacTableModel.setValueAt(reacKeggIds.get(k), k, keggIdColIndex);
			}
		}
		
		setReactionsTableModel(reacTableModel);
		LocalConfig.getInstance().setMaxReactionId(reactions.size());
		LocalConfig.getInstance().setReactionsMetaColumnNames(reactionsMetaColumnNames);
		LocalConfig.getInstance().setMetaboliteAbbreviationIdMap(metaboliteAbbreviationIdMap);
		LocalConfig.getInstance().setMetaboliteIdNameMap(metaboliteIdNameMap);
		LocalConfig.getInstance().setMetaboliteIdCompartmentMap(metaboliteIdCompartmentMap);
		LocalConfig.getInstance().setReactionEquationMap(reactionEquationMap);
		LocalConfig.getInstance().setReactionAbbreviationIdMap(reactionAbbreviationIdMap);
		LocalConfig.getInstance().setProgress(100);	
		if (containsMinFlux && containsMaxFlux) {
			LocalConfig.getInstance().getShowFVAColumnsList().add(LocalConfig.getInstance().getModelName());
		}
		LocalConfig.getInstance().setListOfCompartmentLists(listOfCompartmentLists);
		finished = true;
		//System.out.println("Done");

	}
}