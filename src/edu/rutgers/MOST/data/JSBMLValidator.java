package edu.rutgers.MOST.data;

import java.util.ArrayList;

public class JSBMLValidator {

	public String makeValidID(String mAbrv) {
		//mAbrv = replaceInvalidSBMLIdCharacters(mAbrv);
		if (mAbrv.contains("[") && mAbrv.contains("]")) {
			mAbrv = mAbrv.replace("[","_");
			mAbrv = mAbrv.replace("]","");
		}
		mAbrv = replaceInvalidSBMLIdCharacters(mAbrv);
		
		if (mAbrv.contains("+")) {
			mAbrv = mAbrv.replace("+", SBMLConstants.PLUS_SIGN_REPLACEMENT);
		}
		
		boolean valid = false;
		for (int i = 0; i < SBMLConstants.METABOLITE_ABBREVIATION_PREFIXES.length; i++) {
			if (mAbrv.startsWith(SBMLConstants.METABOLITE_ABBREVIATION_PREFIXES[i])) {
				valid = true;
				break;
			}
		}
		if (!valid) {
			mAbrv = SBMLConstants.METABOLITE_ABBREVIATION_PREFIX + mAbrv;
		}
			
		return mAbrv;

	}
	
	public String makeValidReactionID(String rAbrv) {
		//rAbrv = replaceInvalidSBMLIdCharacters(rAbrv);
		if (rAbrv.contains("[") && rAbrv.contains("]")) {
			rAbrv = rAbrv.replace("[","_");
			rAbrv = rAbrv.replace("]","");
		}
		rAbrv = replaceInvalidSBMLIdCharacters(rAbrv);
		
		boolean valid = false;
		for (int i = 0; i < SBMLConstants.REACTION_ABBREVIATION_PREFIXES.length; i++) {
			if (rAbrv.startsWith(SBMLConstants.REACTION_ABBREVIATION_PREFIXES[i])) {
				valid = true;
				break;
			}
		}
		if (!valid) {
			rAbrv = SBMLConstants.REACTION_ABBREVIATION_PREFIX + rAbrv;
		}
						
		return rAbrv;

	}
	
	public String replaceInvalidSBMLIdCharacters(String value) {		
		// http://stackoverflow.com/questions/1805518/replacing-all-non-alphanumeric-characters-with-empty-strings/1805527#1805527
		value = value.replaceAll("[^A-Za-z0-9_]", "_");

		return value;
		
	}
	
	public String replaceInvalidSBMLCharacters(String value) {
		// SBML does not permit ampersand 
		// see sbml-level-3-version-1-core-rel-1.pdf section 3.1.1
		if (value.contains("&")) {
			value = value.replace("&", SBMLConstants.AMPERSAND_REPLACEMENT);
		}
		// http://stackoverflow.com/questions/10574289/remove-non-ascii-characters-from-string-in-java
		String fixed = value.replaceAll("[^\\x20-\\x7e]", "");
		value = fixed;
		
		return value;
		
	}
	
	public String duplicateSuffix(String value, ArrayList<String> list) {
		String duplicateSuffix = "_1";
		if (list.contains(value + duplicateSuffix)) {
			int duplicateCount = Integer.valueOf(duplicateSuffix.substring(1, duplicateSuffix.length()));
			while (list.contains(value + duplicateSuffix.replace("1", Integer.toString(duplicateCount + 1)))) {
				duplicateCount += 1;
			}
			duplicateSuffix = duplicateSuffix.replace("1", Integer.toString(duplicateCount + 1));
		}
		return duplicateSuffix;
	}
	
}
