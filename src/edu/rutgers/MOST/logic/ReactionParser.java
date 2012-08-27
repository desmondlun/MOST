package edu.rutgers.MOST.logic;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JOptionPane;

import edu.rutgers.MOST.config.ConfigConstants;
import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.data.DatabaseCreator;
import edu.rutgers.MOST.data.MetaboliteFactory;
import edu.rutgers.MOST.data.ProductFactory;
import edu.rutgers.MOST.data.ReactantFactory;
import edu.rutgers.MOST.data.ReactionFactory;
import edu.rutgers.MOST.data.SBMLProduct;
import edu.rutgers.MOST.data.SBMLReactant;
import edu.rutgers.MOST.data.SBMLReaction;
import edu.rutgers.MOST.presentation.GraphicalInterface;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;

public class ReactionParser {

	//This class requires spaces between coefficients and metabolites, use of
	//predefined "arrow" strings, and no spaces in metabolite abbreviations

	boolean addMetaboliteOption = true;	

	public ArrayList parseReaction(String reactionEquation, int reactionId, String databaseName) {
		DatabaseCreator creator = new DatabaseCreator();

		ReactionFactory rFactory = new ReactionFactory();			
		SBMLReaction aReaction = (SBMLReaction)rFactory.getReactionById(reactionId, "SBML", LocalConfig.getInstance().getDatabaseName()); 
		ArrayList<SBMLReactant> reactantList = new ArrayList();
		ArrayList<SBMLProduct> productList = new ArrayList();
		ArrayList<ArrayList> reactantsAndProductsList = new ArrayList();

		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		reactionEquation = reactionEquation.trim();
		String noPrefix = compartmentPrefixRemoved(reactionEquation);
		String correctedReaction = correctReaction(noPrefix);
		
		String splitString = "";
		//reversible options
		if (correctedReaction.contains("<==>")) {
			//trailing space on splitString gets rid of preceding space on first split
			//of productsAndCoeff
			splitString = "<==> ";
		} else if (correctedReaction.contains("<=>")) {
			splitString = "<=> ";
		} else if (correctedReaction.contains("=") && !correctedReaction.contains(">")) {
			splitString = "= ";
			//not reversible options
		} else if (correctedReaction.contains("=>")) {
			splitString = "=> ";
		} else if (correctedReaction.contains("-->")) {
			splitString = "--> ";
		} else if (correctedReaction.contains("->")) {
			splitString = "-> ";
		}

		//space after splitString not needed and removed to check for reactions of
		//the type "metabolite <==>" (no product), removes splitString from correctedReaction
		if (correctedReaction.endsWith(splitString.substring(0, splitString.length() - 1))) {
			correctedReaction = correctedReaction.substring(0, correctedReaction.length() - (splitString.length() - 1));
		}
		java.util.List<String> halfEquations = Arrays.asList(correctedReaction.split(splitString));	
		java.util.List<String> reactantsAndCoeff = Arrays.asList(halfEquations.get(0).split("\\s+"));

		//create arrays of SBMLReactants and factories
		ReactantFactory aFactory[] = new ReactantFactory[getNumberOfReactants(reactantsAndCoeff)];
		SBMLReactant aReactant[] = new SBMLReactant[getNumberOfReactants(reactantsAndCoeff)];

		//create temporary list to hold reactant or stoic/reactant pair
		ArrayList<String> reactants = new ArrayList();
		Double stoic;
		String reactant;

		int currentReactant = 0;
		for (int i = 0; i < reactantsAndCoeff.size(); i++) {			
			if (reactantsAndCoeff.get(i).compareTo("+") != 0) {				
				reactants.add(reactantsAndCoeff.get(i));				
			} else {
				if (reactants.size() == 1) {					
					stoic = (double) 1;
					reactant = reactants.get(0);

					//coefficient is expressed
				} else {
					if (reactants.get(0).startsWith("(")) {
						stoic = Double.valueOf(reactants.get(0).substring(1, reactants.get(0).length() - 1));
						reactant = reactants.get(1);
					} else {
						stoic = Double.valueOf(reactants.get(0));
						reactant = reactants.get(1);
					}					
				}

				MetaboliteFactory mFactory = new MetaboliteFactory();

				if (mFactory.metaboliteCount(reactant, databaseName) == 0) {
					if (addMetaboliteOption) {
						if (GraphicalInterface.showPrompt) {
							addMetabolitePrompt(mFactory, reactant, reactionId, databaseName);
						} else {
							mFactory.addMetabolite(reactant, databaseName);
						}
					}				    					
				} 

				aFactory[currentReactant] = new ReactantFactory();   		    
				aReactant[currentReactant] = (SBMLReactant)aFactory[currentReactant].getReactantByReactionId(reactionId, "SBML", LocalConfig.getInstance().getDatabaseName());
				aReactant[currentReactant].setReactionId(reactionId);
				aReactant[currentReactant].setStoic(stoic);
				aReactant[currentReactant].setMetaboliteAbbreviation(reactant);
				reactantList.add(currentReactant, aReactant[currentReactant]);
				currentReactant += 1;
				reactants.clear();
			}			
		}		
		//adds reactant after last + or single reactant where there is no +
		if (reactants.size() == 1) {					
			stoic = (double) 1;
			reactant = reactants.get(0);

			//coefficient is expressed
		} else {
			if (reactants.get(0).startsWith("(")) {
				stoic = Double.valueOf(reactants.get(0).substring(1, reactants.get(0).length() - 1));
				reactant = reactants.get(1);
			} else {
				stoic = Double.valueOf(reactants.get(0));
				reactant = reactants.get(1);
			}					
		}	

		MetaboliteFactory nFactory = new MetaboliteFactory();
		if (nFactory.metaboliteCount(reactant, databaseName) == 0) {
			//if user enters values into blank tables on original load of gui, 
			//this code gets rid of blank rows 
			if (LocalConfig.getInstance().getDatabaseName() == ConfigConstants.DEFAULT_DATABASE_NAME) {
				for (int r = 0; r < GraphicalInterface.metabolitesTable.getModel().getRowCount(); r++) {	
					if (GraphicalInterface.metabolitesTable.getModel().getValueAt(r, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN) == null) {	
						creator.deleteMetabolitesRow(ConfigConstants.DEFAULT_DATABASE_NAME, r + 1);
					}				    	    
				}
			}
			if (addMetaboliteOption) {
				if (GraphicalInterface.showPrompt) {
					addMetabolitePrompt(nFactory, reactant, reactionId, databaseName);
				} else {
					nFactory.addMetabolite(reactant, databaseName);
				}
			}		    
		} 

		aFactory[currentReactant] = new ReactantFactory();   		    
		aReactant[currentReactant] = (SBMLReactant)aFactory[currentReactant].getReactantByReactionId(reactionId, "SBML", LocalConfig.getInstance().getDatabaseName());
		aReactant[currentReactant].setReactionId(reactionId);
		aReactant[currentReactant].setStoic(stoic);
		aReactant[currentReactant].setMetaboliteAbbreviation(reactant);
		reactantList.add(currentReactant, aReactant[currentReactant]);

		reactantsAndProductsList.add(reactantList);

		//for reactions with no products (metabolite <==>), there will be no 2nd
		//list item, no product list to parse
		if (halfEquations.size() > 1) {
			String productHalfEquation = halfEquations.get(1).trim();
			java.util.List<String> productsAndCoeff = Arrays.asList(productHalfEquation.split("\\s+"));

			ProductFactory pFactory[] = new ProductFactory[getNumberOfProducts(productsAndCoeff)];
			SBMLProduct aProduct[] = new SBMLProduct[getNumberOfProducts(productsAndCoeff)];

			ArrayList<String> products = new ArrayList();
			Double prodStoic;
			String product;

			int currentProduct = 0;
			for (int i = 0; i < productsAndCoeff.size(); i++) {			
				if (productsAndCoeff.get(i).compareTo("+") != 0) {				
					products.add(productsAndCoeff.get(i));				
				} else {
					if (products.size() == 1) {					
						prodStoic = (double) 1;
						product = products.get(0);

						//coefficient is expressed
					} else {
						if (products.get(0).startsWith("(")) {
							prodStoic = Double.valueOf(products.get(0).substring(1, products.get(0).length() - 1));
							product = products.get(1);
						} else {
							prodStoic = Double.valueOf(products.get(0));
							product = products.get(1);
						}					
					}

					MetaboliteFactory mFactory = new MetaboliteFactory();
					if (mFactory.metaboliteCount(product, databaseName) == 0) {

						if (addMetaboliteOption) {
							if (GraphicalInterface.showPrompt) {
								addMetabolitePrompt(mFactory, product, reactionId, databaseName);
							} else {
								mFactory.addMetabolite(product, databaseName);
							}
						}

					} 

					pFactory[currentProduct] = new ProductFactory();   		    
					aProduct[currentProduct] = (SBMLProduct)pFactory[currentProduct].getProductByReactionId(reactionId, "SBML", LocalConfig.getInstance().getDatabaseName());
					aProduct[currentProduct].setReactionId(reactionId);
					aProduct[currentProduct].setStoic(prodStoic);
					aProduct[currentProduct].setMetaboliteAbbreviation(product);
					productList.add(currentProduct, aProduct[currentProduct]);
					currentProduct += 1;
					products.clear();
				}			
			}		
			//adds reactant after last + or single reactant where there is no +
			if (products.size() == 1) {					
				prodStoic = (double) 1;
				product = products.get(0);

				//coefficient is expressed
			} else {
				if (products.get(0).startsWith("(")) {
					prodStoic = Double.valueOf(products.get(0).substring(1, products.get(0).length() - 1));
					product = products.get(1);
				} else {
					prodStoic = Double.valueOf(products.get(0));
					product = products.get(1);
				}					
			}

			MetaboliteFactory oFactory = new MetaboliteFactory();
			if (oFactory.metaboliteCount(product, databaseName) == 0) {

				if (addMetaboliteOption) {
					if (GraphicalInterface.showPrompt) {
						addMetabolitePrompt(oFactory, product, reactionId, databaseName);
					} else {
						oFactory.addMetabolite(product, databaseName);
					}
				}				
			}

			pFactory[currentProduct] = new ProductFactory();   		    
			aProduct[currentProduct] = (SBMLProduct)pFactory[currentProduct].getProductByReactionId(reactionId, "SBML", LocalConfig.getInstance().getDatabaseName());
			aProduct[currentProduct].setReactionId(reactionId);
			aProduct[currentProduct].setStoic(prodStoic);
			aProduct[currentProduct].setMetaboliteAbbreviation(product);
			productList.add(currentProduct, aProduct[currentProduct]);

			reactantsAndProductsList.add(productList);

		}
		return reactantsAndProductsList;		
	}	

	public boolean isValid(String reactionEquation) {
		if (reactionEquation != null) {
			if (reactionEquation.contains(">") || reactionEquation.contains("=")) {
				return true;
			}
		}		
		return false;
	}

	public int getNumberOfReactants(java.util.List<String> reactantsAndCoeff) {
		int numReactants = 0;
		for (int i = 0; i < reactantsAndCoeff.size(); i++) {			 
			if (reactantsAndCoeff.get(i).compareTo("+") != 0) {	
				numReactants += 1;
			}
		}
		numReactants += 1;
		return numReactants;		
	}

	public int getNumberOfProducts(java.util.List<String> productsAndCoeff) {
		int numProducts = 0;
		for (int i = 0; i < productsAndCoeff.size(); i++) {			 
			if (productsAndCoeff.get(i).compareTo("+") != 0) {	
				numProducts += 1;
			}
		}
		numProducts += 1;
		return numProducts;		
	}

	public void addMetabolitePrompt(MetaboliteFactory mFactory, String reactant, int reactionId, String databaseName)
	{
		if (reactant != null || reactant.trim().length() > 0) {
			// display the showOptionDialog
			Object[] options = {"Yes",
					"Yes to All",
			"No"};

			int choice = JOptionPane.showOptionDialog(null, 
					"The metabolite " + reactant + " does not exist. Do you wish to add it?", 
					"Add Metabolite?", 
					JOptionPane.YES_NO_CANCEL_OPTION, 
					JOptionPane.QUESTION_MESSAGE, 
					null, options, options[0]);
			//options[0] sets "Yes" as default button

			// interpret the user's choice	  
			if (choice == JOptionPane.YES_OPTION)
			{
				mFactory.addMetabolite(reactant, databaseName);
			}
			//No option actually corresponds to "Yes to All" button
			if (choice == JOptionPane.NO_OPTION)
			{
				GraphicalInterface.showPrompt = false;
				mFactory.addMetabolite(reactant, databaseName);
			}
			//Cancel option actually corresponds to "No" button
			if (choice == JOptionPane.CANCEL_OPTION) {
				addMetaboliteOption = false;
				clearReactionString(reactionId, LocalConfig.getInstance().getDatabaseName());
			}	  
		}

	}

	public void clearReactionString(int reactionId, String databaseName) {
		ReactionFactory rFactory = new ReactionFactory();			
		SBMLReaction aReaction = (SBMLReaction)rFactory.getReactionById(reactionId, "SBML", LocalConfig.getInstance().getDatabaseName()); 
		aReaction.setReactionString("");
		aReaction.update();
		aReaction.clearReactants();
		aReaction.clearProducts();
	}

	public ArrayList<Integer> speciesIdList(String reactionEquation, String databaseName) {
		ArrayList<Integer> speciesIdList = new ArrayList();

		reactionEquation = reactionEquation.trim();
		String noPrefix = compartmentPrefixRemoved(reactionEquation);
		String correctedReaction = correctReaction(noPrefix);
        
		String splitString = "";
		//reversible options
		if (correctedReaction.contains("<==>")) {
			//trailing space on splitString gets rid of preceding space on first split
			//of productsAndCoeff
			splitString = "<==> ";
		} else if (correctedReaction.contains("<=>")) {
			splitString = "<=> ";
		} else if (correctedReaction.contains("=") && !correctedReaction.contains(">")) {
			splitString = "= ";
			//not reversible options
		} else if (correctedReaction.contains("=>")) {
			splitString = "=> ";
		} else if (correctedReaction.contains("-->")) {
			splitString = "--> ";
		} else if (correctedReaction.contains("->")) {
			splitString = "-> ";
		}
		
		//space after splitString not needed and removed to check for reactions of
		//the type "metabolite <==>" (no product), removes splitString from correctedReaction
		if (correctedReaction.endsWith(splitString.substring(0, splitString.length() - 1))) {
			correctedReaction = correctedReaction.substring(0, correctedReaction.length() - (splitString.length() - 1));
		}
		java.util.List<String> halfEquations = Arrays.asList(correctedReaction.split(splitString));	
		java.util.List<String> reactantsAndCoeff = Arrays.asList(halfEquations.get(0).split("\\s+"));

		MetaboliteFactory mFactory = new MetaboliteFactory();
		ArrayList<String> reactants = new ArrayList();
		String reactant;

		int currentReactant = 0;
		for (int i = 0; i < reactantsAndCoeff.size(); i++) {			
			if (reactantsAndCoeff.get(i).compareTo("+") != 0) {				
				reactants.add(reactantsAndCoeff.get(i));				
			} else {
				if (reactants.size() == 1) {					
					reactant = reactants.get(0);

					//coefficient is expressed
				} else {					
					reactant = reactants.get(1);				
				}

				speciesIdList.add(mFactory.metaboliteId(databaseName, reactant));

				currentReactant += 1;
				reactants.clear();
			}			
		}
		//adds reactant after last + or single reactant where there is no +
		if (reactants.size() == 1) {					
			reactant = reactants.get(0);

			//coefficient is expressed
		} else {					
			reactant = reactants.get(1);				
		}

		speciesIdList.add(mFactory.metaboliteId(databaseName, reactant));

		//for reactions with no products (metabolite <==>), there will be no 2nd
		//list item, no product list to parse
		if (halfEquations.size() > 1) {
			String productHalfEquation = halfEquations.get(1).trim();
			java.util.List<String> productsAndCoeff = Arrays.asList(productHalfEquation.split("\\s+"));

			ArrayList<String> products = new ArrayList();
			String product;

			int currentProduct = 0;
			for (int i = 0; i < productsAndCoeff.size(); i++) {			
				if (productsAndCoeff.get(i).compareTo("+") != 0) {				
					products.add(productsAndCoeff.get(i));				
				} else {
					if (products.size() == 1) {					
						product = products.get(0);
					} else {					
						product = products.get(1);				
					}

					//avoids duplicate entries
					Integer id = mFactory.metaboliteId(databaseName, product);
					if (!speciesIdList.contains(id)) {
						speciesIdList.add(id);
					}

					currentProduct += 1;
					products.clear();
				}			
			}
			//adds product after last + or single product where there is no +
			if (products.size() == 1) {					
				product = products.get(0);
			} else {					
				product = products.get(1);				
			}

			//avoids duplicate entries
			Integer id = mFactory.metaboliteId(databaseName, product);
			if (!speciesIdList.contains(id)) {
				speciesIdList.add(id);
			}
		}

		return speciesIdList;
	}

	public String correctReaction(String reactionEquation) {
		String correctedReaction = "";
		if (reactionEquation.contains("+")) {
			char[] rxnChar = reactionEquation.toCharArray();
			StringBuffer newRxn = new StringBuffer(reactionEquation); 
			int insertCorrection = 0;
			for (int i = 0; i < reactionEquation.length(); i++) {
				if (rxnChar[i] == '+') {
					if (rxnChar[i - 1] != ' ') {
						newRxn.insert(i + insertCorrection, " ");
						insertCorrection += 1; 
					}
					if (rxnChar[i + 1] != ' ') {
						newRxn.insert(i + 1 + insertCorrection, " ");
						insertCorrection += 1;
					} 	
				}
			}
			correctedReaction = newRxn.toString();
			return correctedReaction;
		}		
		return reactionEquation;		
	}
	
	//removes compartment prefix such as "[c] :"
	public String compartmentPrefixRemoved(String reactionEquation) {
		String correctedReaction = "";
		if (reactionEquation.startsWith("[") && reactionEquation.indexOf("]") == 2 && reactionEquation.contains(":")) {
			   correctedReaction = reactionEquation.substring(5, reactionEquation.length()).trim();
			   return correctedReaction;			   
		   }
		return reactionEquation;
	}
	
	public static void main(String[] args) {
       
	}		
}



