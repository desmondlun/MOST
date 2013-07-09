package edu.rutgers.MOST.presentation;

public class EntryValidator {

	public boolean isNumber(String value) {
		try
		{
			Double.parseDouble(value); 
		}
		catch (NumberFormatException nfe) {
			return false;
		} 
		return true;
		
	}
	
	// check if lower bound is >= 0 if reversible = false, and upper bound > lower bound
	public boolean lowerBoundReversibleValid(Double lowerBound, Double upperBound, String reversible) {
		if (reversible.compareTo("false") == 0 && lowerBound < 0) {
			return false;
		}
		
		return true;
		
	}
	
	// these two methods below used for true/false autofill, if entry starts with "t" of "f"
	// case insensitive, it fills in true/false, else throws error
	public boolean validTrueEntry(String value) {
		if (value.toLowerCase().startsWith(GraphicalInterfaceConstants.VALID_TRUE_VALUES[0])) {
			return true;
		}
		return false;
		
	}
	
	public boolean validFalseEntry(String value) {
		if (value.toLowerCase().startsWith(GraphicalInterfaceConstants.VALID_FALSE_VALUES[0])) {
			return true;
		}
		return false;
		
	}
	
}
