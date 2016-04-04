package edu.rutgers.MOST.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.presentation.GraphicalInterface;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;

public class MetaboliteFactory {
	private String sourceType;
	private Map<Object, Object> internalMetabolitesIdPositionMap;
	
	public Map<Object, Object> getInternalMetabolitesIdPositionMap() {
		return internalMetabolitesIdPositionMap;
	}

	public void setInternalMetabolitesIdPositionMap(
			Map<Object, Object> internalMetabolitesIdPositionMap) {
		this.internalMetabolitesIdPositionMap = internalMetabolitesIdPositionMap;
	}

	public MetaboliteFactory(String sourceType) {
		this.sourceType = sourceType;
	}
	
	public ModelMetabolite getMetaboliteById(Integer metaboliteId){


		if("SBML".equals(sourceType)){
			SBMLMetabolite metabolite = new SBMLMetabolite();
			metabolite.loadById(metaboliteId);
			return metabolite;
		}
		return new SBMLMetabolite(); //Default behavior.
	}

	public ModelMetabolite getMetaboliteByRow(Integer row){


		if("SBML".equals(sourceType)){
			SBMLMetabolite metabolite = new SBMLMetabolite();
			metabolite.loadByRow(row);
			return metabolite;
		}
		return new SBMLMetabolite(); //Default behavior.
	}
	
	public ArrayList<Integer> participatingReactions(String metaboliteAbbreviation) {
		ArrayList<Integer> participatingReactions = new ArrayList<Integer>();
		if("SBML".equals(sourceType)){
			if (metaboliteAbbreviation != null) {
				ReactantFactory reactantFactory = new ReactantFactory("SBML");
				ArrayList<SBMLReactant> reactantList = reactantFactory.getAllReactants();
				for (int i = 0; i < reactantList.size(); i++) {
					if (reactantList.get(i).getMetaboliteAbbreviation() != null) {
						if (reactantList.get(i).getMetaboliteAbbreviation().equals(metaboliteAbbreviation)) {
							if (!participatingReactions.contains(reactantList.get(i).getReactionId())) {
								participatingReactions.add(reactantList.get(i).getReactionId());
							}
						}
					}					
				}
				ProductFactory productFactory = new ProductFactory("SBML");
				ArrayList<SBMLProduct> productList = productFactory.getAllProducts();
				for (int j = 0; j < productList.size(); j++) {
					if (productList.get(j).getMetaboliteAbbreviation() != null) {
						if (productList.get(j).getMetaboliteAbbreviation().equals(metaboliteAbbreviation)) {
							if (!participatingReactions.contains(productList.get(j).getReactionId())) {
								participatingReactions.add(productList.get(j).getReactionId());
							}
						}
					}					
				}
			}
		}
		
		return participatingReactions;
	}

	public Vector<ModelMetabolite> getAllInternalMetabolites() {
		Vector<ModelMetabolite> metabolites = new Vector<ModelMetabolite>();
		Map<Object, Object> internalMetabolitesIdPositionMap = new HashMap<Object, Object>();
		int count = 0;

		if("SBML".equals(sourceType)){			
			for (int i = 0; i < GraphicalInterface.metabolitesTable.getRowCount(); i++) {
				if (GraphicalInterface.metabolitesTable.getModel().getValueAt(i, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN) != null &&
					((String) GraphicalInterface.metabolitesTable.getModel().getValueAt(i, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN)).trim().length() > 0) {
					String boundary = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(i, GraphicalInterfaceConstants.BOUNDARY_COLUMN);
					if (boundary.equals(GraphicalInterfaceConstants.BOOLEAN_VALUES[0])) {
						SBMLMetabolite metabolite = new SBMLMetabolite();
						metabolite.setId(Integer.valueOf((String) GraphicalInterface.metabolitesTable.getModel().getValueAt(i, GraphicalInterfaceConstants.METABOLITE_ID_COLUMN)));
						metabolite.setBoundary((String) GraphicalInterface.metabolitesTable.getModel().getValueAt(i, GraphicalInterfaceConstants.BOUNDARY_COLUMN));
						metabolites.add(metabolite);
						internalMetabolitesIdPositionMap.put(metabolite.getId(), count);
						count += 1;
					}
				}							
			}

			setInternalMetabolitesIdPositionMap(internalMetabolitesIdPositionMap);
		}

		return metabolites;
	}

	public ArrayList<Integer> metaboliteInternalIdList() {
		ArrayList<Integer> metaboliteInternalIdList = new ArrayList<Integer>();

		if("SBML".equals(sourceType)){
			for (int i = 0; i < GraphicalInterface.metabolitesTable.getRowCount(); i++) {
				if (GraphicalInterface.metabolitesTable.getModel().getValueAt(i, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN) != null &&
					((String) GraphicalInterface.metabolitesTable.getModel().getValueAt(i, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN)).trim().length() > 0) {
					String boundary = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(i, GraphicalInterfaceConstants.BOUNDARY_COLUMN);
					if (boundary.equals(GraphicalInterfaceConstants.BOOLEAN_VALUES[0])) {
						int id = Integer.valueOf((String) GraphicalInterface.metabolitesTable.getModel().getValueAt(i, GraphicalInterfaceConstants.METABOLITE_ID_COLUMN));
						metaboliteInternalIdList.add(id);
					}
				}				
			}
		}

		return metaboliteInternalIdList;
	}

	public ArrayList<Integer> metaboliteExternalIdList() {
		ArrayList<Integer> metaboliteExternalIdList = new ArrayList<Integer>();

		if("SBML".equals(sourceType)){
			for (int i = 0; i < GraphicalInterface.metabolitesTable.getRowCount(); i++) {
				if (GraphicalInterface.metabolitesTable.getModel().getValueAt(i, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN) != null &&
					((String) GraphicalInterface.metabolitesTable.getModel().getValueAt(i, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN)).trim().length() > 0) {
					String boundary = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(i, GraphicalInterfaceConstants.BOUNDARY_COLUMN);
					if (boundary.equals(GraphicalInterfaceConstants.BOOLEAN_VALUES[1])) {
						int id = Integer.valueOf((String) GraphicalInterface.metabolitesTable.getModel().getValueAt(i, GraphicalInterfaceConstants.METABOLITE_ID_COLUMN));
						metaboliteExternalIdList.add(id);
					}
				}				
			}
		}

		return metaboliteExternalIdList;
	}

	public ArrayList<Integer> metaboliteIdList() {
		ArrayList<Integer> metaboliteIdList = new ArrayList<Integer>();

		if("SBML".equals(sourceType)){
			for (int i = 0; i < GraphicalInterface.metabolitesTable.getRowCount(); i++) {
				int id = Integer.valueOf((String) GraphicalInterface.metabolitesTable.getModel().getValueAt(i, GraphicalInterfaceConstants.METABOLITE_ID_COLUMN));
				metaboliteIdList.add(id);
			}
		}

		return metaboliteIdList;
	}

	public ArrayList<String> metaboliteAbbreviationList() {
		ArrayList<String> metaboliteAbbreviationList = new ArrayList<String>();

		if("SBML".equals(sourceType)){
			for (int i = 0; i < GraphicalInterface.metabolitesTable.getRowCount(); i++) {
				if (GraphicalInterface.metabolitesTable.getModel().getValueAt(i, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN) != null &&
					((String) GraphicalInterface.metabolitesTable.getModel().getValueAt(i, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN)).trim().length() > 0) {
					String abbrev = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(i, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN);
					if (!metaboliteAbbreviationList.contains(abbrev)) {
						metaboliteAbbreviationList.add(abbrev);
					}					
				}				
			}
		}

		return metaboliteAbbreviationList;
	}

	public Vector<SBMLMetabolite> getAllMetabolites() {
		Vector<SBMLMetabolite> metabolites = new Vector<SBMLMetabolite>();
		Map<Object, Object> internalMetabolitesIdPositionMap = new HashMap<Object, Object>();

		if("SBML".equals(sourceType)){			
			for (int i = 0; i < GraphicalInterface.metabolitesTable.getRowCount(); i++) {
				if (GraphicalInterface.metabolitesTable.getModel().getValueAt(i, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN) != null &&	
					((String) GraphicalInterface.metabolitesTable.getModel().getValueAt(i, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN)).trim().length() > 0) {
					Integer.valueOf((String) GraphicalInterface.metabolitesTable.getModel().getValueAt(i, GraphicalInterfaceConstants.METABOLITE_ID_COLUMN));	
					SBMLMetabolite metabolite = new SBMLMetabolite();
					metabolite.setId(Integer.valueOf((String) GraphicalInterface.metabolitesTable.getModel().getValueAt(i, GraphicalInterfaceConstants.METABOLITE_ID_COLUMN)));
					metabolite.setMetaboliteAbbreviation((String) GraphicalInterface.metabolitesTable.getModel().getValueAt(i, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN));
					metabolite.setMetaboliteName((String) GraphicalInterface.metabolitesTable.getModel().getValueAt(i, GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN));
					metabolite.setCharge((String) GraphicalInterface.metabolitesTable.getModel().getValueAt(i, GraphicalInterfaceConstants.CHARGE_COLUMN));
					metabolite.setCompartment((String) GraphicalInterface.metabolitesTable.getModel().getValueAt(i, GraphicalInterfaceConstants.COMPARTMENT_COLUMN));
					metabolite.setBoundary((String) GraphicalInterface.metabolitesTable.getModel().getValueAt(i, GraphicalInterfaceConstants.BOUNDARY_COLUMN));
					if (LocalConfig.getInstance().getKeggMetaboliteIdColumn() > -1) {
						String keggId = (String) GraphicalInterface.metabolitesTable.getModel().getValueAt(i, LocalConfig.getInstance().getKeggMetaboliteIdColumn());
						// accounts for models where KEGG ids start with "c"
						if (keggId != null) {
							metabolite.setKeggId(keggId.toUpperCase());
						}
					}
					if (LocalConfig.getInstance().getChebiIdColumn() > -1) {
						metabolite.setChebiId((String) GraphicalInterface.metabolitesTable.getModel().getValueAt(i, LocalConfig.getInstance().getChebiIdColumn()));
					}
					metabolites.add(metabolite);
				}													
			}
			setInternalMetabolitesIdPositionMap(internalMetabolitesIdPositionMap);
		}

		return metabolites;
	}

	// get index of column with Kegg Id
	public Integer locateKeggIdColumn() {
		int index = -1;
		for (int i = 0; i < LocalConfig.getInstance().getMetabolitesMetaColumnNames().size(); i++) {
			for (int j = 0; j < GraphicalInterfaceConstants.KEGG_ID_METABOLITES_COLUMN_NAMES.length; j++) {
				if (LocalConfig.getInstance().getMetabolitesMetaColumnNames().get(i).contains(GraphicalInterfaceConstants.KEGG_ID_METABOLITES_COLUMN_NAMES[j])) {
					index = GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length + i;
				}
			}
		}

		return index;
	}

	// get index of column with Kegg Id
	public Integer locateChebiIdColumn() {
		int index = -1;
		for (int i = 0; i < LocalConfig.getInstance().getMetabolitesMetaColumnNames().size(); i++) {
			for (int j = 0; j < GraphicalInterfaceConstants.CHEBI_ID_METABOLITES_COLUMN_NAMES.length; j++) {
				if (LocalConfig.getInstance().getMetabolitesMetaColumnNames().get(i).contains(GraphicalInterfaceConstants.CHEBI_ID_METABOLITES_COLUMN_NAMES[j])) {
					index = GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length + i;
				}
			}
		}

		return index;
	}

	public static void main(String[] args) {

	}

}

