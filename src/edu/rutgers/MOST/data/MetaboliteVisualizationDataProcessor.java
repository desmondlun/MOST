package edu.rutgers.MOST.data;

import java.util.ArrayList;
import java.util.Vector;

import edu.rutgers.MOST.config.LocalConfig;

public class MetaboliteVisualizationDataProcessor {

	public void processMetabolitesData() {
		MetaboliteFactory f = new MetaboliteFactory("SBML");
		if (LocalConfig.getInstance().getKeggMetaboliteIdColumn() > -1 || 
				LocalConfig.getInstance().getChebiIdColumn() > -1) {
			Vector<SBMLMetabolite> metabolites = f.getAllMetabolites();
			for (int i = 0; i < metabolites.size(); i++) {
				// if metabolite abbreviation is not empty and KEGG id is not empty
				// and not a boundary metabolite, process metabolite
				if (metaboliteAbbreviationValid(metabolites.get(i)) &&
						!isBoundaryMetabolite(metabolites.get(i))) {
					String metabId = Integer.toString(metabolites.get(i).getId());
					String keggId = metabolites.get(i).getKeggId();
					if (keggId == null || keggId.length() == 0) {
						if (LocalConfig.getInstance().getChebiIdColumn() > -1) {
							String chebiId = metabolites.get(i).getChebiId();
							if (chebiId != null && chebiId.length() > 0) {
								if (LocalConfig.getInstance().getChebiIdKeggIdMap().containsKey(chebiId)) {
//									System.out.println(chebiId);
//									System.out.println(LocalConfig.getInstance().getChebiIdKeggIdMap().get(chebiId));
									keggId = LocalConfig.getInstance().getChebiIdKeggIdMap().get(chebiId);
								}
							}
						}
					}
					if (keggId != null && keggId.length() > 0) {
						if (keggId.contains("|")) {
							keggId = keggId.substring(0, keggId.indexOf("|"));
							//System.out.println(keggId);
						}
						// map used to match metabolite ids from model with KEGG ids
						LocalConfig.getInstance().getMetaboliteIdKeggIdMap().put(metabId, keggId);
						if (LocalConfig.getInstance().getKeggIdMetaboliteMap().containsKey(keggId)) {
							ArrayList<SBMLMetabolite> metabolitesList = LocalConfig.getInstance().getKeggIdMetaboliteMap().get(keggId);
							metabolitesList.add(metabolites.get(i));
							// key - kegg id value SBMLMetabolite list, used to get data from model when
							// constructing nodes
							LocalConfig.getInstance().getKeggIdMetaboliteMap().put(keggId, metabolitesList);
						} else {
							ArrayList<SBMLMetabolite> metabolitesList = new ArrayList<SBMLMetabolite>();
							metabolitesList.add(metabolites.get(i));
							LocalConfig.getInstance().getKeggIdMetaboliteMap().put(keggId, metabolitesList);
						}
						if (!LocalConfig.getInstance().getKeggIdCompartmentMap().containsKey(keggId)) {
							ArrayList<String> compList = new ArrayList<String>();
							compList.add(metabolites.get(i).getCompartment());
							// used to determine which compartments a KEGG id occurs in
							LocalConfig.getInstance().getKeggIdCompartmentMap().put(keggId, compList);
						} else {
							ArrayList<String> compList = LocalConfig.getInstance().getKeggIdCompartmentMap().get(keggId);
							compList.add(metabolites.get(i).getCompartment());
							LocalConfig.getInstance().getKeggIdCompartmentMap().put(keggId, compList);
						}
					}
				}
			}
//			System.out.println("k " + LocalConfig.getInstance().getKeggIdCompartmentMap());
//			System.out.println("m " + LocalConfig.getInstance().getMetaboliteIdKeggIdMap());
//			System.out.println("i " + LocalConfig.getInstance().getKeggIdMetaboliteMap());
		}
	}

	private boolean metaboliteAbbreviationValid(SBMLMetabolite metabolite) {
		if (metabolite.getMetaboliteAbbreviation() != null &&
						metabolite.getMetaboliteAbbreviation().length() > 0) {
			return true;
		}
		return false;
		
	}
	
	private boolean isBoundaryMetabolite(SBMLMetabolite metabolite) {
		if (metabolite.getMetaboliteAbbreviation().endsWith("_b")) {
			return true;
		}
		return false;
		
	}
	
}
