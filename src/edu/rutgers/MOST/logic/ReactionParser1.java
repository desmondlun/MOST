package edu.rutgers.MOST.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReactionParser1 {
	
	public static boolean parse = true; //TODO: determine if needed
	// if reaction starts with [c]: for example, suffix will be appended to all
	// species in the reaction when adding to metabolites table and maps
	public static boolean hasPrefix = false;
	public static String suffixFromPrefix = "";
	// reaction with prefix cannot contain any suffixes
	public static boolean hasSuffix = false;
	// if reaction contains prefix and suffix
	public static boolean invalidSyntax = false;

	public static ArrayList<ArrayList> reactionList(String reactionEquation){
		invalidSyntax = false;
		ArrayList<ArrayList> reactionList = new ArrayList();
		ArrayList<String> reactantAndStoicList = new ArrayList();
		ArrayList<String> productAndStoicList = new ArrayList();
		if (reactionEquation != null) {
			reactionEquation = compartmentPrefixRemoved(reactionEquation);
			
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
		}
		
		hasPrefix = false;
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
			reactant = species(rawSpeciesAndCoeff, 0);				
		}	
		
		// checks if reactant contains a charge - ex H+2
		if (reactant.contains("+")) {
			if ((isNumber(reactant.substring(reactant.lastIndexOf("+") + 1, reactant.length()))) || reactant.endsWith("+")) {
				stoicAndSpecies.add(stoic);
				stoicAndSpecies.add(reactant);
			} else {
				parse = false; //TODO: determine if needed
			}
		} else {
			stoicAndSpecies.add(stoic);
			stoicAndSpecies.add(reactant);
		}
			
		return stoicAndSpecies;
	}
	
	//removes compartment prefix such as "[c]:"
	public static String compartmentPrefixRemoved(String reactionEquation) {
		if (reactionEquation != null) {
			String correctedReaction = "";
			if (reactionEquation.startsWith("[") && reactionEquation.indexOf("]") == 2 && reactionEquation.contains(":")) {
				correctedReaction = reactionEquation.substring(reactionEquation.indexOf(":") + 1, reactionEquation.length()).trim();
				hasPrefix = true;
				suffixFromPrefix = reactionEquation.substring(0, 3);
				return correctedReaction;
			}
			hasPrefix = false;
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
		if (reactionEquation != null) {
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
		}
		
		return splitString;		
	}
	
	//concatenates species if there are spaces, stoic correction = 1 if stoic exists, 0 otherwise
	public static String species(List<String> reactantsAndCoeff, int stoicCorrection) {
		String species = "";
		// coefficient if exists + species substring 1 + space + species substring 2 + space + ... species substring n - 1
		if (reactantsAndCoeff.size() - stoicCorrection > 2) {
			for (int i = stoicCorrection; i < reactantsAndCoeff.size() - 1; i++) {
				species = species + reactantsAndCoeff.get(i) + " ";
			}
			// append species substring n
			species = species + reactantsAndCoeff.get(reactantsAndCoeff.size() - 1);
		// coefficient if exists + species substring 1 + space + species substring 2
		} else if (reactantsAndCoeff.size() - stoicCorrection == 2) {
			species = reactantsAndCoeff.get(0 + stoicCorrection) + " " + reactantsAndCoeff.get(1 + stoicCorrection);
		} else {
			// no spaces
			species = reactantsAndCoeff.get(0 + stoicCorrection);
		}
		
		String existingSuffix = existingSuffix(species);
		if (hasPrefix) {
			if (hasSuffix) {
				invalidSyntax = true;
			} else {
				// append suffix if prefix exists
				species += suffixFromPrefix;
			}			
		}
		hasSuffix = false;
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
			if (splitString(reactionEquation).length() < 1) {
				return false;
			}
			else if (reactionEquation.trim().startsWith(splitString(reactionEquation))) {
				if (!reactionEquation.trim().contains(splitString(reactionEquation))) {
					return false;
				}
			} else if (reactionEquation.trim().endsWith(splitString(reactionEquation).trim())) {
				if (!reactionEquation.trim().contains(" " + splitString(reactionEquation).trim())) {
					return false;
				}
			} else {
				if (!reactionEquation.trim().contains(" " + splitString(reactionEquation))) {
					return false;
				}
			}
		}
		return true;
	}

	
	public static String existingSuffix(String species) {
		String existingSuffix = "";
		if (species.indexOf("[") == species.length() - 3 && species.endsWith("]")) {
			existingSuffix = species.substring(species.length() - 3, species.length());
			hasSuffix = true;
		}
		return existingSuffix;		
	}
	
	public static void main(String[] args) {
		//String reactionEquation = "a+2 + 3 h+ + c => c+ + d";
		//ArrayList reaction = reactionList(reactionEquation);
		//System.out.println(isNumber(s));
	}
	
}

