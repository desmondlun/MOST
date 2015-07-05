package edu.rutgers.MOST.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import au.com.bytecode.opencsv.CSVReader;
import edu.rutgers.MOST.config.LocalConfig;
//import edu.rutgers.MOST.logic.ReactionParser;
import edu.rutgers.MOST.logic.ReactionParser;
import edu.rutgers.MOST.presentation.GraphicalInterface;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;
import edu.rutgers.MOST.presentation.Utilities;

public class TextReactionsModelReader {
	
//	public boolean noReactants;     // type ==> p
//	public boolean noProducts;      // type r ==>
	public boolean addMetabolite;
	
	private static DefaultTableModel reactionsTableModel;
	
	public static DefaultTableModel getReactionsTableModel() {
		return reactionsTableModel;
	}

	public static void setReactionsTableModel(DefaultTableModel reactionsTableModel) {
		TextReactionsModelReader.reactionsTableModel = reactionsTableModel;
	}
	
	private static DefaultTableModel metabolitesTableModel;

	public static DefaultTableModel getMetabolitesTableModel() {
		return metabolitesTableModel;
	}

	public static void setMetabolitesTableModel(
			DefaultTableModel metabolitesTableModel) {
		TextReactionsModelReader.metabolitesTableModel = metabolitesTableModel;
	}

	public ArrayList<String> columnNamesFromFile(File file, int row) {
		ArrayList<String> columnNamesFromFile = new ArrayList<String>();
		
		String[] dataArray = null;
		
		//use fileReader to read first line to get headers
		try {
			CSVReader csv_reader = new CSVReader( new FileReader( file ) );
			dataArray = csv_reader.readNext();	

			//add all column names to list			
			for (int h = 0; h < dataArray.length; h++) { 

				//remove quotes if exist
				if (dataArray[h].startsWith("\"")) {
					//removes " " and null strings
					if (dataArray[h].compareTo("\" \"") != 0 && dataArray[h].trim().length() > 0) {
						columnNamesFromFile.add(dataArray[h].substring(1, dataArray[h].length() - 1));
					}					
				} else {
					if (dataArray[h].trim().length() > 0) {
						columnNamesFromFile.add(dataArray[h]);
					}					
				}			
			}

			if (row > 0) {
				for (int i = 0; i < row; i++) {
					dataArray = csv_reader.readNext();							
					columnNamesFromFile.clear();
					//add all column names to list			
					for (int h = 0; h < dataArray.length; h++) { 
						//remove quotes if exist
						if (dataArray[h].startsWith("\"")) {
							//removes " " and null strings
							if (dataArray[h].compareTo("\" \"") != 0 && dataArray[h].trim().length() > 0) {
								columnNamesFromFile.add(dataArray[h].substring(1, dataArray[h].length() - 1));
							}					
						} else {
							if (dataArray[h].trim().length() > 0) {
								columnNamesFromFile.add(dataArray[h]);
							}					
						}			
					} 
				}				
			}
			csv_reader.close();

		} catch (FileNotFoundException e1) {
			JOptionPane.showMessageDialog(null,                
					"File Not Found Error.",                
					"Error",                                
					JOptionPane.ERROR_MESSAGE);
			//e1.printStackTrace();							
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null,                
					"File Not Found Error.",                
					"Error",                                
					JOptionPane.ERROR_MESSAGE);
			//e1.printStackTrace();
		} 

		return columnNamesFromFile;
	}	

	public Integer numberOfLines(File file) {
		int count = 0;
		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(file), ',');
			try {
				while ((reader.readNext()) != null) {
					count++; 	
				}
				reader.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,                
						"File Not Found Error.",                
						"Error",                                
						JOptionPane.ERROR_MESSAGE);
				//e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null,                
					"File Not Found Error.",                
					"Error",                                
					JOptionPane.ERROR_MESSAGE);
			//e.printStackTrace();
		}
		return count;		
	}

	public void load(File file){
		Utilities u = new Utilities();
		LocalConfig.getInstance().getMetaboliteUsedMap().clear();
		LocalConfig.getInstance().getSuspiciousMetabolites().clear();
		LocalConfig.getInstance().getInvalidLowerBoundReversibleCombinations().clear();
		LocalConfig.getInstance().getInvalidEquationReversibleCombinations().clear();
		LocalConfig.getInstance().getReactionAbbreviationIdMap().clear();
		
		DefaultTableModel reacTableModel = new DefaultTableModel();
		if (LocalConfig.getInstance().hasMetabolitesFile) {
			DefaultTableModel model = (DefaultTableModel) GraphicalInterface.metabolitesTable.getModel();
			setMetabolitesTableModel(model);
		} else {
			DefaultTableModel model = createBlankMetabolitesTableModel();
			setMetabolitesTableModel(model);
		}		
		for (int m = 0; m < GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length; m++) {
			reacTableModel.addColumn(GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES[m]);
		}
		for (int n = 0; n < LocalConfig.getInstance().getReactionsMetaColumnNames().size(); n++) {
			reacTableModel.addColumn(LocalConfig.getInstance().getReactionsMetaColumnNames().get(n));
		}		
		//if first row of file in not column names, starts reading after row that contains names
		int correction = LocalConfig.getInstance().getReactionsNextRowCorrection();
		int row = 1;
		
		//LocalConfig.getInstance().getMetaboliteUsedMap().clear();
		
		//LocalConfig.getInstance().addMetaboliteOption = true;
		
		if (!LocalConfig.getInstance().hasMetabolitesFile) {
			LocalConfig.getInstance().getMetaboliteUsedMap().clear();
			LocalConfig.getInstance().getSuspiciousMetabolites().clear();
			LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().clear();
			LocalConfig.getInstance().setMaxMetabolite(0);
			//LocalConfig.getInstance().setMaxMetaboliteId(0);
			//maxMetabId = 0;
			
		}
		
		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(file), ',');
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null,                
					"File Not Found Error.",                
					"Error",                                
					JOptionPane.ERROR_MESSAGE);
			//e.printStackTrace();
		}
		
		try {
			reader = new CSVReader(new FileReader(file), ',');
			int numLines = numberOfLines(file);
			
			LocalConfig.getInstance().setMaxReactionId(numLines - 1 - correction);
			
			int id = 0;
			for (int i = 0; i < numLines; i++) {
				String [] dataArray = reader.readNext();
				for (int s = 0; s < dataArray.length; s++) {
					if (dataArray[s].length() > 0 && dataArray[s].substring(0,1).matches("\"")) {
						dataArray[s] = dataArray[s].substring(1, (dataArray[s].length() - 1));			
					}
				}
			
				if (i >= (row + correction)) {
					Vector <String> reacRow = new Vector<String>();
					reacRow.add(Integer.toString(id));
					String knockout = GraphicalInterfaceConstants.KO_DEFAULT;
					Double fluxValue = GraphicalInterfaceConstants.FLUX_VALUE_DEFAULT;
					Double minFlux = GraphicalInterfaceConstants.MIN_FLUX_DEFAULT;
					Double maxFlux = GraphicalInterfaceConstants.MAX_FLUX_DEFAULT;
					String reactionAbbreviation = "";
					String reactionName = "";
					String reactionEqunAbbr = "";
					String reversible = "";
					Double lowerBound = GraphicalInterfaceConstants.LOWER_BOUND_REVERSIBLE_CSV_DEFAULT;
					Double upperBound =	GraphicalInterfaceConstants.UPPER_BOUND_CSV_DEFAULT;
					Double biologicalObjective = GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_DEFAULT;
					Double syntheticObjective = GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_DEFAULT;
					String geneAssociation = "";
					String proteinAssociation = "";
					String subsystem = "";
					String proteinClass = "";
                    
					if (LocalConfig.getInstance().getKnockoutColumnIndex() > -1) {
						if (dataArray[LocalConfig.getInstance().getKnockoutColumnIndex()].compareTo("false") == 0 || dataArray[LocalConfig.getInstance().getKnockoutColumnIndex()].compareTo("FALSE") == 0 || dataArray[LocalConfig.getInstance().getKnockoutColumnIndex()].compareTo("0") == 0 || dataArray[LocalConfig.getInstance().getKnockoutColumnIndex()].compareTo("0.0") == 0) {
							knockout = GraphicalInterfaceConstants.BOOLEAN_VALUES[0];
						} else if (dataArray[LocalConfig.getInstance().getKnockoutColumnIndex()].compareTo("true") == 0 || dataArray[LocalConfig.getInstance().getKnockoutColumnIndex()].compareTo("TRUE") == 0 || dataArray[LocalConfig.getInstance().getKnockoutColumnIndex()].compareTo("1") == 0 || dataArray[LocalConfig.getInstance().getKnockoutColumnIndex()].compareTo("1.0") == 0) {
							knockout = GraphicalInterfaceConstants.BOOLEAN_VALUES[1];													
						} 
					}
					reacRow.add(knockout);
					if (LocalConfig.getInstance().getFluxValueColumnIndex() > -1) {
						if (isNumber(dataArray[LocalConfig.getInstance().getFluxValueColumnIndex()])) {
							fluxValue = Double.valueOf(dataArray[LocalConfig.getInstance().getFluxValueColumnIndex()]);
						} 
					} 
					reacRow.add(Double.toString(fluxValue));
					reacRow.add(Double.toString(minFlux));
					reacRow.add(Double.toString(maxFlux));
					reactionAbbreviation = dataArray[LocalConfig.getInstance().getReactionAbbreviationColumnIndex()];
					
					// appends suffix on duplicate abbreviations
					if (reactionAbbreviation == null || reactionAbbreviation.trim().length() == 0) {
						
					} else {
						if (LocalConfig.getInstance().getReactionAbbreviationIdMap().containsKey(reactionAbbreviation)) {
							reactionAbbreviation = reactionAbbreviation + u.duplicateSuffix(reactionAbbreviation, LocalConfig.getInstance().getReactionAbbreviationIdMap());
						}
						LocalConfig.getInstance().getReactionAbbreviationIdMap().put(reactionAbbreviation, id);
					}
					
					LocalConfig.getInstance().getReactionAbbreviationIdMap().put(reactionAbbreviation, id);
					reacRow.add(reactionAbbreviation);
					if (LocalConfig.getInstance().getReactionNameColumnIndex() > -1) {
						reactionName = dataArray[LocalConfig.getInstance().getReactionNameColumnIndex()];
					}
					reacRow.add(reactionName);	
					
					reactionEqunAbbr = dataArray[LocalConfig.getInstance().getReactionEquationColumnIndex()];
					reactionEqunAbbr = reactionEqunAbbr.trim();
					
					ReactionParser parser = new ReactionParser();
					if (parser.isValid(reactionEqunAbbr)) {
						parser.reactionList(reactionEqunAbbr);
						SBMLReactionEquation equn = new SBMLReactionEquation();
						equn = ReactionParser.getEquation();
						updateReactionEquation(reactionEqunAbbr, id, equn, reacRow);
						reversible = equn.getReversible();
					} else {
						if (reactionEqunAbbr != null && reactionEqunAbbr.trim().length() > 0) {
							LocalConfig.getInstance().getInvalidReactions().add(reactionEqunAbbr);
						}
						reacRow.add(reactionEqunAbbr);
						reacRow.add(reactionEqunAbbr);
						reversible = GraphicalInterfaceConstants.REVERSIBLE_DEFAULT;
						//reacRow.add(GraphicalInterfaceConstants.REVERSIBLE_DEFAULT);
					}
					
					// read value in reversible column if exists, if not equal to value from
					// reaction equation, add to list for use in highlighting invalid combination.
					String reversibleFromFile = reversible;
					if (LocalConfig.getInstance().getReversibleColumnIndex() > -1) {
						//System.out.println(dataArray[LocalConfig.getInstance().getReversibleColumnIndex()]);
						if (dataArray[LocalConfig.getInstance().getReversibleColumnIndex()].compareTo("false") == 0 || dataArray[LocalConfig.getInstance().getReversibleColumnIndex()].compareTo("FALSE") == 0 || dataArray[LocalConfig.getInstance().getReversibleColumnIndex()].compareTo("0") == 0 || dataArray[LocalConfig.getInstance().getReversibleColumnIndex()].compareTo("0.0") == 0) {
							reversibleFromFile = GraphicalInterfaceConstants.BOOLEAN_VALUES[0];
						} else if (dataArray[LocalConfig.getInstance().getReversibleColumnIndex()].compareTo("true") == 0 || dataArray[LocalConfig.getInstance().getReversibleColumnIndex()].compareTo("TRUE") == 0 || dataArray[LocalConfig.getInstance().getReversibleColumnIndex()].compareTo("1") == 0 || dataArray[LocalConfig.getInstance().getReversibleColumnIndex()].compareTo("1.0") == 0) {
							reversibleFromFile = GraphicalInterfaceConstants.BOOLEAN_VALUES[1];
						} 
						//System.out.println(reversibleFromFile);
						if (!reversible.equals(reversibleFromFile)) {
							LocalConfig.getInstance().getInvalidEquationReversibleCombinations().add(id);
						}
						reversible = reversibleFromFile;
					} 
					reacRow.add(reversible);
					
					if (LocalConfig.getInstance().getLowerBoundColumnIndex() > -1) {
						// if value in csv is numeric, use the value, else if reversible, set value as negative
						// infinity, else set value as zero. to change what value to use as infinity, change
						// LOWER_BOUND_REVERSIBLE_CSV_DEFAULT and UPPER_BOUND_CSV_DEFAULT in GraphicalInterfaceConstants
						if (isNumber(dataArray[LocalConfig.getInstance().getLowerBoundColumnIndex()])) {
							//System.out.println(dataArray[LocalConfig.getInstance().getLowerBoundColumnIndex()]);
							lowerBound = Double.valueOf(dataArray[LocalConfig.getInstance().getLowerBoundColumnIndex()]);
						} else {
							// false
							if (reversible.equals(GraphicalInterfaceConstants.BOOLEAN_VALUES[0])) {
								lowerBound = GraphicalInterfaceConstants.LOWER_BOUND_DEFAULT;
								// true
							} else if (reversible.equals(GraphicalInterfaceConstants.BOOLEAN_VALUES[1])) {
								lowerBound = GraphicalInterfaceConstants.LOWER_BOUND_REVERSIBLE_CSV_DEFAULT;
							}
						}
					} 
					
					// error messaging: exception handling to outline precisely where any 'error' of
					// interest can be in code. Easy hop by clicking on stack-trace
//					try
//					{
//						// reversible = false
//						// CSV-Override: if LB < 0 and reaction.isNotReversible, 
//						//               then use LB and set reaction.reversibility to True
//						if (lowerBound < 0.0 && reversible.equals(GraphicalInterfaceConstants.BOOLEAN_VALUES[0])) 
//						{
//	//						System.out.println("lb " + lowerBound);
//	//						System.out.println(reversible);
//							//lowerBound = GraphicalInterfaceConstants.LOWER_BOUND_DEFAULT;
//							reversible = GraphicalInterfaceConstants.BOOLEAN_VALUES[1];
//							if( GraphicalInterfaceConstants.DEBUG_MODE )
//								throw new Exception( "flux bound non-conforming" );
//						}
//					}
//					catch( Exception e )
//					{
//						e.printStackTrace();
//					}
					
					// instead of printing error message, if lower bound < 0 and reversible = false, the
					// reaction will be highlighted
					if (lowerBound < 0.0 && reversible.equals(GraphicalInterfaceConstants.BOOLEAN_VALUES[0])) {
						LocalConfig.getInstance().getInvalidLowerBoundReversibleCombinations().add(id);
					}
					
					reacRow.add(Double.toString(lowerBound));
					if (LocalConfig.getInstance().getUpperBoundColumnIndex() > -1) {
						if (isNumber(dataArray[LocalConfig.getInstance().getUpperBoundColumnIndex()])) {
							upperBound = Double.valueOf(dataArray[LocalConfig.getInstance().getUpperBoundColumnIndex()]);							
						}
					} 
					reacRow.add(Double.toString(upperBound));
					if (LocalConfig.getInstance().getBiologicalObjectiveColumnIndex() > -1) {
						if (isNumber(dataArray[LocalConfig.getInstance().getBiologicalObjectiveColumnIndex()])) {
							biologicalObjective = Double.valueOf(dataArray[LocalConfig.getInstance().getBiologicalObjectiveColumnIndex()]);							
						} 							
					} 
					reacRow.add(Double.toString(biologicalObjective));
					if (LocalConfig.getInstance().getSyntheticObjectiveColumnIndex() > -1) {
						if (isNumber(dataArray[LocalConfig.getInstance().getSyntheticObjectiveColumnIndex()])) {
							syntheticObjective = Double.valueOf(dataArray[LocalConfig.getInstance().getSyntheticObjectiveColumnIndex()]);							
						} 							
					} 
					reacRow.add(Double.toString(syntheticObjective));
					if (LocalConfig.getInstance().getGeneAssociationColumnIndex() > -1) {
						geneAssociation = dataArray[LocalConfig.getInstance().getGeneAssociationColumnIndex()];						 							
					} 
					reacRow.add(geneAssociation);
					if (LocalConfig.getInstance().getProteinAssociationColumnIndex() > -1) {
						proteinAssociation = dataArray[LocalConfig.getInstance().getProteinAssociationColumnIndex()];						 							
					} 
					reacRow.add(proteinAssociation);
					if (LocalConfig.getInstance().getSubsystemColumnIndex() > -1) {
						subsystem = dataArray[LocalConfig.getInstance().getSubsystemColumnIndex()];						 							
					} 
					reacRow.add(subsystem);
					if (LocalConfig.getInstance().getProteinClassColumnIndex() > -1) {
						proteinClass = dataArray[LocalConfig.getInstance().getProteinClassColumnIndex()];						 							
					} 
					reacRow.add(proteinClass);
					if (LocalConfig.getInstance().getReactionsMetaColumnIndexList().size() > 0) {
						for (int m = 0; m < LocalConfig.getInstance().getReactionsMetaColumnIndexList().size(); m++) {
							reacRow.add(dataArray[LocalConfig.getInstance().getReactionsMetaColumnIndexList().get(m)]);
						}
					}

					reacTableModel.addRow(reacRow);
					id += 1;
				}
				//LocalConfig.getInstance().noButtonClicked = false;
			}
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null,                
					"File Not Found Error.",                
					"Error",                                
					JOptionPane.ERROR_MESSAGE);
			//e.printStackTrace();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,                
					"File Not Found Error.",                
					"Error",                                
					JOptionPane.ERROR_MESSAGE);
			//e.printStackTrace();
		}
		GraphicalInterface.showPrompt = true;
		LocalConfig.getInstance().hasMetabolitesFile = false;
		setReactionsTableModel(reacTableModel);
//		System.out.println(LocalConfig.getInstance().getReactionAbbreviationIdMap());
//		System.out.println(LocalConfig.getInstance().getReactionEquationMap());
//		System.out.println(LocalConfig.getInstance().getMetaboliteUsedMap());
//		System.out.println(LocalConfig.getInstance().getMetaboliteAbbreviationIdMap());
	}
	
	public void updateReactionEquation(String reactionEqun, int id, SBMLReactionEquation equation, Vector<String> reacRow) {
		SBMLReactionEquation equn = new SBMLReactionEquation();	
		ArrayList<SBMLReactant> reactants = new ArrayList<SBMLReactant>();
		ArrayList<SBMLProduct> products = new ArrayList<SBMLProduct>();
		ReactionParser parser = new ReactionParser();
		for (int i = 0; i < equation.getReactants().size(); i++){
			maybeAddSpecies(equation.getReactants().get(i).getMetaboliteAbbreviation(), equation, "reactant", i);
			if (addMetabolite) {
				equation.getReactants().get(i).setReactionId(id);
				Integer metabId = (Integer) LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().get(equation.getReactants().get(i).getMetaboliteAbbreviation());				
				equation.getReactants().get(i).setMetaboliteId(metabId);
				if (LocalConfig.getInstance().getMetaboliteIdNameMap().containsKey(metabId)) {
					equation.getReactants().get(i).setMetaboliteName(LocalConfig.getInstance().getMetaboliteIdNameMap().get(metabId));
					equation.getReactants().get(i).setCompartment(LocalConfig.getInstance().getMetaboliteIdCompartmentMap().get(metabId));
				}		
				reactants.add(equation.getReactants().get(i));
				if (parser.isSuspicious(equation.getReactants().get(i).getMetaboliteAbbreviation())) {
					if (!LocalConfig.getInstance().getSuspiciousMetabolites().contains(metabId)) {
						LocalConfig.getInstance().getSuspiciousMetabolites().add(metabId);
					}							
				}
			}			
		}	
		for (int i = 0; i < equation.getProducts().size(); i++){
			maybeAddSpecies(equation.getProducts().get(i).getMetaboliteAbbreviation(), equation, "product", i);
			if (addMetabolite) {
				equation.getProducts().get(i).setReactionId(id);
				Integer metabId = (Integer) LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().get(equation.getProducts().get(i).getMetaboliteAbbreviation());				
				equation.getProducts().get(i).setMetaboliteId(metabId);
				if (LocalConfig.getInstance().getMetaboliteIdNameMap().containsKey(metabId)) {
					equation.getProducts().get(i).setMetaboliteName(LocalConfig.getInstance().getMetaboliteIdNameMap().get(metabId));
					equation.getProducts().get(i).setCompartment(LocalConfig.getInstance().getMetaboliteIdCompartmentMap().get(metabId));
				}				
				products.add(equation.getProducts().get(i));
				if (parser.isSuspicious(equation.getProducts().get(i).getMetaboliteAbbreviation())) {
					if (!LocalConfig.getInstance().getSuspiciousMetabolites().contains(metabId)) {
						LocalConfig.getInstance().getSuspiciousMetabolites().add(metabId);
					}							
				}
			}			
		}
		equn.setReactants(reactants);
		equn.setProducts(products);
		equn.setReversible(equation.getReversible());
		equn.setReversibleArrow(equation.getReversibleArrow());
		equn.setIrreversibleArrow(equation.getIrreversibleArrow());
		equn.writeReactionEquation();
		reacRow.add(equn.equationAbbreviations);
		reacRow.add(equn.equationNames);
		// reversible now set from Reversible column, not equation,
		// please do not uncommment
		//reacRow.add(equn.getReversible());
		LocalConfig.getInstance().getReactionEquationMap().put(id, equn);
		
	}
	
	public void maybeAddSpecies(String species, SBMLReactionEquation equation, String type, int index) {
		addMetabolite = true;
		int maxMetabId = LocalConfig.getInstance().getMaxMetabolite();
		boolean newMetabolite = false;
		if (!(LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().containsKey(species.trim()))) {
			newMetabolite = true;
			if (GraphicalInterface.showPrompt && !(GraphicalInterface.replaceAllMode && LocalConfig.getInstance().yesToAllButtonClicked)) {
				Object[] options = {"Yes",
						"Yes to All",
				"No"};
				LocalConfig.getInstance().addReactantPromptShown = true;
				int choice = JOptionPane.showOptionDialog(null, 
						"The metabolite " + species + " does not exist. Do you wish to add it?", 
						"Add Metabolite?", 
						JOptionPane.YES_NO_CANCEL_OPTION, 
						JOptionPane.QUESTION_MESSAGE, 
						null, options, options[0]);
				//options[0] sets "Yes" as default button

				// interpret the user's choice	  
				if (choice == JOptionPane.YES_OPTION)
				{
					LocalConfig.getInstance().addMetaboliteOption = true;
					LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().put(species, maxMetabId);
					//LocalConfig.getInstance().getAddedMetabolites().add((maxMetabId));
					addNewMetabolite(maxMetabId, species);
					maxMetabId += 1;
				}
				//No option actually corresponds to "Yes to All" button
				if (choice == JOptionPane.NO_OPTION)
				{
					LocalConfig.getInstance().addMetaboliteOption = true;
					GraphicalInterface.showPrompt = false;
					LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().put(species, maxMetabId);
					//LocalConfig.getInstance().getAddedMetabolites().add((maxMetabId));
					addNewMetabolite(maxMetabId, species);
					maxMetabId += 1;
					LocalConfig.getInstance().yesToAllButtonClicked = true;
				}
				//Cancel option actually corresponds to "No" button
				if (choice == JOptionPane.CANCEL_OPTION) {
					addMetabolite = false;
					// TODO; determine if any of these two bool below necessary
					LocalConfig.getInstance().addMetaboliteOption = false;
					LocalConfig.getInstance().noButtonClicked = true;
					// TODO; determine if necessary since these are removed elsewhere
					if (type == "reactant") {
						equation.getReactants().remove(index);
					} else if (type == "product") {
						equation.getProducts().remove(index);
					} 					
				}	
			} else {
				//LocalConfig.getInstance().getMetaboliteNameIdMap().put(species, maxMetabId);
				//LocalConfig.getInstance().getAddedMetabolites().add((maxMetabId));
				addNewMetabolite(maxMetabId, species);
				maxMetabId += 1;
			}
			//if (LocalConfig.getInstance().getMaxMetabolite() == LocalConfig.getInstance().getMaxMetaboliteId()) {
				LocalConfig.getInstance().setMaxMetaboliteId(maxMetabId);
			//}
			LocalConfig.getInstance().setMaxMetabolite(maxMetabId);
		}
		if (!newMetabolite) {
		//if (!newMetabolite || LocalConfig.getInstance().addMetaboliteOption) {
			if (LocalConfig.getInstance().getMetaboliteUsedMap().containsKey(species)) {
				int usedCount = (Integer) LocalConfig.getInstance().getMetaboliteUsedMap().get(species);
				LocalConfig.getInstance().getMetaboliteUsedMap().put(species, new Integer(usedCount + 1));									
			} else {
				LocalConfig.getInstance().getMetaboliteUsedMap().put(species, new Integer(1));
			}	
		}
	}
	
	public void addNewMetabolite(int maxMetabId, String species) {
		DefaultTableModel model = getMetabolitesTableModel();
		model.addRow(createMetabolitesRow(maxMetabId));
		model.setValueAt(species, maxMetabId, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN);
		LocalConfig.getInstance().getMetaboliteAbbreviationIdMap().put(species, maxMetabId);
		LocalConfig.getInstance().getMetaboliteUsedMap().put(species, new Integer(1));
//		if (LocalConfig.getInstance().getMetaboliteUsedMap().containsKey(species)) {
//			int usedCount = (Integer) LocalConfig.getInstance().getMetaboliteUsedMap().get(species);
//			LocalConfig.getInstance().getMetaboliteUsedMap().put(species, new Integer(usedCount + 1));
//		} else {
			LocalConfig.getInstance().getMetaboliteUsedMap().put(species, new Integer(1));
//		}
		setMetabolitesTableModel(model);
	}
	
	public Vector<String> createMetabolitesRow(int id)
	{
		Vector<String> row = new Vector<String>();
		row.addElement(Integer.toString(id));
		for (int i = 1; i < GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length; i++) {
			if (i==GraphicalInterfaceConstants.BOUNDARY_COLUMN) {
				row.addElement(GraphicalInterfaceConstants.BOUNDARY_DEFAULT);
			} else {
				row.addElement("");
			}	
		}
		return row;
	}
	
	public boolean isNumber(String s) {
		try {
			Double.parseDouble(s);
		}
		catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
	
	public DefaultTableModel createBlankMetabolitesTableModel() {
		DefaultTableModel blankMetabModel = new DefaultTableModel();
		for (int m = 0; m < GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length; m++) {
			blankMetabModel.addColumn(GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES[m]);
		}
		return blankMetabModel;
	}
	
}



