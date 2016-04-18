package edu.rutgers.MOST.data;

import java.util.ArrayList;
import java.util.Map;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;
import edu.rutgers.MOST.presentation.Utilities;

public class PathwayMetaboliteNodeFactory {
	
	Utilities util = new Utilities();

	public PathwayMetaboliteNode createPathwayMetaboliteNode(String dataId, double x, double y,  
			String type, String abbreviation, String name, String keggId) {
		PathwayMetaboliteNode pn = new PathwayMetaboliteNode();
		pn.setxPosition(x);
		pn.setyPosition(y);
		pn.setType(type);
		pn.setAbbreviation(abbreviation);
		pn.setName(name);
		//createDisplayName(abbreviation, name, keggId);
		return pn;
	}
	
	public String displayName(ArrayList<String> metaboliteNames) {
		String mn = "";
		if (metaboliteNames.size() > 0) {
			mn = metaboliteNames.get(0);
		}
		if (metaboliteNames.size() > 1) {
			mn = metaboliteNames.toString();
		}
		return mn;
	}
	
	public String displayMetaboliteAbbreviation(ArrayList<String> metaboliteAbbrevations) {
		return util.maybeMakeList(metaboliteAbbrevations, "Metabolite Abbreviation");
	}
	
	public String displayMetaboliteName(ArrayList<String> metaboliteNames) {
		return util.maybeMakeList(metaboliteNames, "Metabolite Name");
	}
	
	public String displayKeggId(ArrayList<String> keggMetaboliteIds) {
		return util.maybeMakeList(keggMetaboliteIds, "KEGG ID");
	}
	
	/**
	 * Creates html name from lists of parameters
	 * @param name
	 * @param abbrList
	 * @param nameList
	 * @param keggIdList
	 * @param chargeList
	 * @return
	 */
	public String htmlDisplayName(String name, ArrayList<String> nameList, ArrayList<String> abbrList,
			ArrayList<String> keggIdList, ArrayList<String> chebiIdList, ArrayList<String> chargeList, String id) {
		String htmlName = "<html>" + name + "<p>Metabolite Name(s): " + nameList.toString() +
				"<p>Metabolite Abbreviation(s): " + abbrList.toString() +
				"<p>KEGG Id(s): " + keggIdList.toString() +
				"<p>CHEBI Id(s): " + chebiIdList.toString() +
				"<p>Charge: " + chargeList.toString() + 
				"<p>Metabolite Database Id: " + id + "<p>";
		return htmlName;
	}
	
	// TODO: figure out how to get database id here from metabolite node positions file
	// so node info can be in the same format as other nodes
	public String htmlDisplayNameRenamed(String name, ArrayList<String> nameList, ArrayList<String> abbrList,
		ArrayList<String> keggIdList, ArrayList<String> chebiIdList, ArrayList<String> chargeList) {
		String htmlName = "<html>" + name + "<p>Metabolite Name(s): " + nameList.toString() +
			"<p>Metabolite Abbreviation(s): " + abbrList.toString() +
			"<p>KEGG Id(s): " + keggIdList.toString() +
			"<p>CHEBI Id(s): " + chebiIdList.toString() +
			"<p>Charge: " + chargeList.toString() + "<p>";
		return htmlName;
	}
	
	/**
	 * Adds suffix to duplicate metabolite names
	 * @param value
	 * @param metaboliteNameAbbrMap
	 * @return
	 */
	public String duplicateSuffix(String value, Map<String, String> metaboliteNameAbbrMap) {
		String duplicateSuffix = GraphicalInterfaceConstants.DUPLICATE_SUFFIX;
		if (metaboliteNameAbbrMap.containsKey(value + duplicateSuffix)) {
			int duplicateCount = Integer.valueOf(duplicateSuffix.substring(1, duplicateSuffix.length() - 1));
			while (metaboliteNameAbbrMap.containsKey(value + duplicateSuffix.replace("1", Integer.toString(duplicateCount + 1)))) {
				duplicateCount += 1;
			}
			duplicateSuffix = duplicateSuffix.replace("1", Integer.toString(duplicateCount + 1));
		}
		return duplicateSuffix;
	}
	
}
