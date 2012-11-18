package edu.rutgers.MOST.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReactionParser1 {
	
	static boolean parse = true;

	public static ArrayList<ArrayList> reactionList(String reactionEquation){
		reactionEquation = compartmentPrefixRemoved(reactionEquation);
		ArrayList<ArrayList> reactionList = new ArrayList();
		ArrayList<String> reactantAndStoicList = new ArrayList();
		ArrayList<String> productAndStoicList = new ArrayList();
		
		java.util.List<String> halfEquations = Arrays.asList(reactionEquation.split(splitString(reactionEquation)));	
		if (reactionEquation.trim().startsWith(splitString(reactionEquation))) {
			reactantAndStoicList.add("0");
			
			ArrayList<ArrayList> reactants = new ArrayList();
			reactants.add(reactantAndStoicList);
			reactionList.add(reactants);
			
			String productHalfEquation = reactionEquation.substring(splitString(reactionEquation).length(), reactionEquation.length());
			java.util.List<String> productsAndCoeff = Arrays.asList(productHalfEquation.split("\\s+"));
			
			ArrayList<ArrayList> rawProducts = rawSpeciesAndCoeffList(productsAndCoeff);
			ArrayList<ArrayList> products = stoicAndSpeciesList(rawProducts);			
			reactionList.add(products);
			
		} else if (reactionEquation.trim().endsWith(splitString(reactionEquation).trim())) {			
			
			String reactantHalfEquation = reactionEquation.substring(0, reactionEquation.length() - splitString(reactionEquation).length());
			java.util.List<String> reactantsAndCoeff = Arrays.asList(reactantHalfEquation.split("\\s+"));			
			
			ArrayList<ArrayList> rawReactants = rawSpeciesAndCoeffList(reactantsAndCoeff);
			ArrayList<ArrayList> reactants = stoicAndSpeciesList(rawReactants);			
			reactionList.add(reactants);
			
			productAndStoicList.add("0");
			
			ArrayList<ArrayList> products = new ArrayList();
			products.add(productAndStoicList);
			reactionList.add(products);
			
		} else {	
			String reactantHalfEquation = halfEquations.get(0).trim();
			java.util.List<String> reactantsAndCoeff = Arrays.asList(halfEquations.get(0).trim().split("\\s+"));
						
			ArrayList<ArrayList> rawReactants = rawSpeciesAndCoeffList(reactantsAndCoeff);
			ArrayList<ArrayList> reactants = stoicAndSpeciesList(rawReactants);
			reactionList.add(reactants);
			
			String productHalfEquation = halfEquations.get(1).trim();
			java.util.List<String> productsAndCoeff = Arrays.asList(productHalfEquation.split("\\s+"));
			
			ArrayList<ArrayList> rawProducts = rawSpeciesAndCoeffList(productsAndCoeff);
			ArrayList<ArrayList> products = stoicAndSpeciesList(rawProducts);
			reactionList.add(products);
		}		
		
		return reactionList;		
	}
	
	//creates list of raw lists of coeff and species from half equations
	public static ArrayList<ArrayList> rawSpeciesAndCoeffList(List<String> halfEquation) {
		//need to make an array of speciesAndCoeff lists
		ArrayList<ArrayList> rawSpeciesAndCoeffList = new ArrayList();
		//list of coeff and species or species only
		ArrayList<String> speciesAndCoeff[] = new ArrayList[getNumberOfSpecies(halfEquation)];
		int currentSpecies = 0;
		speciesAndCoeff[currentSpecies] = new ArrayList();
		for (int i = 0; i < halfEquation.size(); i++) {				
			if (halfEquation.get(i).compareTo("+") != 0) {				
				speciesAndCoeff[currentSpecies].add(halfEquation.get(i));				
			} else {
				rawSpeciesAndCoeffList.add(speciesAndCoeff[currentSpecies]);
				currentSpecies += 1;
				speciesAndCoeff[currentSpecies] = new ArrayList();
			}			
		}
		
		rawSpeciesAndCoeffList.add(speciesAndCoeff[currentSpecies]);
		
		return rawSpeciesAndCoeffList;
	}
	
	public static ArrayList<ArrayList> stoicAndSpeciesList(ArrayList<ArrayList> rawSpeciesList) {
		ArrayList<ArrayList> stoicAndSpeciesList = new ArrayList();
		ArrayList<String> stoicAndSpecies[] = new ArrayList[rawSpeciesList.size()];
		for (int i = 0; i < rawSpeciesList.size(); i++) {
			stoicAndSpecies[i] = stoicAndSpecies((ArrayList) rawSpeciesList.get(i));
			stoicAndSpeciesList.add(stoicAndSpecies[i]);
		}

		return stoicAndSpeciesList;
	}
	
	//converts raw lists of coeff and species or species only from rawSpeciesAndCoeffList to
	//list of stoic and species, if 1 is not expressed, it is added as stoic
	public static ArrayList<String> stoicAndSpecies(ArrayList<String> rawSpeciesAndCoeff) {
		ArrayList<String> stoicAndSpecies = new ArrayList<String>();
		String stoic = "1.0";
		String reactant = "";
		if (rawSpeciesAndCoeff.size() > 1) {
			//if number is in parenthesis
			if (rawSpeciesAndCoeff.get(0).startsWith("(")) {
				String firstString = rawSpeciesAndCoeff.get(0).substring(1, rawSpeciesAndCoeff.get(0).length() - 1);
				if (isNumber(firstString)) {
					stoic = firstString;
					reactant = species(rawSpeciesAndCoeff, 1);
				} else {
					reactant = species(rawSpeciesAndCoeff, 0);
				}
			//number not in parenthesis	
			} else {
				if (isNumber(rawSpeciesAndCoeff.get(0))) {
					stoic = rawSpeciesAndCoeff.get(0);
					reactant = species(rawSpeciesAndCoeff, 1);
				} else 
					reactant = species(rawSpeciesAndCoeff, 0);
				}
			//length 1
		} else {
			reactant = rawSpeciesAndCoeff.get(0);					
		}	
		
		if (reactant.contains("+")) {
			if ((isNumber(reactant.substring(reactant.lastIndexOf("+") + 1, reactant.length()))) || reactant.endsWith("+")) {
				stoicAndSpecies.add(stoic);
				stoicAndSpecies.add(reactant);
			} else {
				parse = false;
			}
		} else {
			stoicAndSpecies.add(stoic);
			stoicAndSpecies.add(reactant);
		}
			
		return stoicAndSpecies;
	}
	
	//removes compartment prefix such as "[c] :"
	public static String compartmentPrefixRemoved(String reactionEquation) {
		String correctedReaction = "";
		if (reactionEquation.startsWith("[") && reactionEquation.indexOf("]") == 2 && reactionEquation.contains(":")) {
			   correctedReaction = reactionEquation.substring(5, reactionEquation.length()).trim();
			   return correctedReaction;			   
		   }
		return reactionEquation;
	}
	
	public static boolean isNumber(String s) {
		try {
			Double.parseDouble(s);
		}
		catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
	
	public static String splitString(String reactionEquation) {
		String splitString = "";
		//reversible options
		if (reactionEquation.contains("<==>")) {
			//trailing space on splitString gets rid of preceding space on first split
			//of productsAndCoeff
			splitString = "<==> ";
		} else if (reactionEquation.contains("<=>")) {
			splitString = "<=> ";
		} else if (reactionEquation.contains("=") && !reactionEquation.contains(">")) {
			splitString = "= ";
			//not reversible options
		} else if (reactionEquation.contains("=>")) {
			splitString = "=> ";
		} else if (reactionEquation.contains("-->")) {
			splitString = "--> ";
		} else if (reactionEquation.contains("->")) {
			splitString = "-> ";
		}
		
		return splitString;		
	}
	
	//concatenates species if there are spaces
	public static String species(List<String> reactantsAndCoeff, int stoicCorrection) {
		String species = "";
		if (reactantsAndCoeff.size() - stoicCorrection > 2) {
			for (int i = stoicCorrection; i < reactantsAndCoeff.size() - 1; i++) {
				species = species + reactantsAndCoeff.get(i) + " ";
			}
			species = species + reactantsAndCoeff.get(reactantsAndCoeff.size() - 1);
		} else if (reactantsAndCoeff.size() - stoicCorrection == 2) {
			species = reactantsAndCoeff.get(0 + stoicCorrection) + " " + reactantsAndCoeff.get(1 + stoicCorrection);
		} else {
			species = reactantsAndCoeff.get(0 + stoicCorrection);
		}
		return species;
	}
	
	public static int getNumberOfSpecies(java.util.List<String> halfEquation) {
		int numSpecies = 0;
		for (int i = 0; i < halfEquation.size(); i++) {			 
			if (halfEquation.get(i).compareTo("+") != 0) {	
				numSpecies += 1;
			}
		}
		numSpecies += 1;
		return numSpecies;		
	}
	
	public boolean isValid(String reactionEquation) {
		if (reactionEquation != null) {
			if (reactionEquation.contains(">") || reactionEquation.contains("=")) {
				return true;
			}
		}		
		return false;
	}
	
	public static void main(String[] args) {
		//String reactionEquation = "a+2 + 3 h+ + c => c+ + d";
		//ArrayList reaction = reactionList(reactionEquation);
		//System.out.println(isNumber(s));
	}
	
}

