package edu.rutgers.MOST.data;

import java.util.ArrayList;

import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;

public class SBMLReactionEquation implements ModelReactionEquation {

	private ArrayList<SBMLReactant> reactants;
	private ArrayList<SBMLProduct> products;
	private String reversible;
	private String reversibleArrow;
	private String irreversibleArrow;
	public String equationAbbreviations;
	public String equationNames;
	private ArrayList<String> compartmentList;

	public ArrayList<SBMLReactant> getReactants() {
		return reactants;
	}

	public void setReactants(ArrayList<SBMLReactant> reactants) {
		this.reactants = reactants;
	}

	public ArrayList<SBMLProduct> getProducts() {
		return products;
	}

	public void setProducts(ArrayList<SBMLProduct> products) {
		this.products = products;
	}

	public String getReversible() {
		return reversible;
	}

	public void setReversible(String reversible) {
		this.reversible = reversible;
	}

	public String getReversibleArrow() {
		return reversibleArrow;
	}


	public void setReversibleArrow(String reversibleArrow) {
		this.reversibleArrow = reversibleArrow;
	}


	public String getIrreversibleArrow() {
		return irreversibleArrow;
	}

	public void setIrreversibleArrow(String irreversibleArrow) {
		this.irreversibleArrow = irreversibleArrow;
	}
	
	public ArrayList<String> getCompartmentList() {
		return compartmentList;
	}

	public void setCompartmentList(ArrayList<String> compartmentList) {
		this.compartmentList = compartmentList;
	}

	public void writeReactionEquation() {
		StringBuffer reacBfr = new StringBuffer();
		StringBuffer reacNamesBfr = new StringBuffer();
		StringBuffer prodBfr = new StringBuffer();
		StringBuffer prodNamesBfr = new StringBuffer();
		StringBuffer rxnBfr = new StringBuffer();
		StringBuffer rxnNamesBfr = new StringBuffer();
		
		for (int r = 0; r < reactants.size(); r++) {
			double stoic = ((SBMLReactant) reactants.get(r)).getStoic();
			String stoicStr = Double.toString(stoic);
			String metabAbbrev = ((SBMLReactant) reactants.get(r)).getMetaboliteAbbreviation();
			String metabName = ((SBMLReactant) reactants.get(r)).getMetaboliteName();
			String compartment = ((SBMLReactant) reactants.get(r)).getCompartment();
			String name = metaboliteNameString(metabAbbrev, metabName, compartment);
			if (r == 0) {
				if (stoic == 1.0) {
					reacBfr.append(metabAbbrev);
					reacNamesBfr.append(name);								
				} else {
					if (stoicStr.endsWith(".0")) {
						stoicStr = stoicStr.substring(0, stoicStr.length() - 2);
					}
					reacBfr.append(stoicStr + " " + metabAbbrev);
					reacNamesBfr.append(stoicStr + " " + name);								
				}
			} else {
				if (stoic == 1.0) {
					reacBfr.append(" + " + metabAbbrev);
					reacNamesBfr.append(" + " + name);			
				} else {
					if (stoicStr.endsWith(".0")) {
						stoicStr = stoicStr.substring(0, stoicStr.length() - 2);
					}
					reacBfr.append(" + " + stoicStr + " " + metabAbbrev);
					reacNamesBfr.append(" + " + stoicStr + " " + name);								
				}				
			}			
		}
		
		for (int p = 0; p < products.size(); p++) {
			double stoic = ((SBMLProduct) products.get(p)).getStoic();
			String stoicStr = Double.toString(stoic);
			String metabAbbrev = ((SBMLProduct) products.get(p)).getMetaboliteAbbreviation();
			String metabName = ((SBMLProduct) products.get(p)).getMetaboliteName();
			String compartment = ((SBMLProduct) products.get(p)).getCompartment();
			String name = metaboliteNameString(metabAbbrev, metabName, compartment);
			if (p == 0) {
				if (stoic == 1.0) {
					prodBfr.append(metabAbbrev);
					prodNamesBfr.append(name);								
				} else {
					if (stoicStr.endsWith(".0")) {
						stoicStr = stoicStr.substring(0, stoicStr.length() - 2);
					}
					prodBfr.append(stoicStr + " " + metabAbbrev);
					prodNamesBfr.append(stoicStr + " " + name);								
				}

			} else {
				if (stoic == 1.0) {
					prodBfr.append(" + " + metabAbbrev);
					prodNamesBfr.append(" + " + name);
				} else {
					if (stoicStr.endsWith(".0")) {
						stoicStr = stoicStr.substring(0, stoicStr.length() - 2);
					}
					prodBfr.append(" + " + stoicStr + " " + metabAbbrev);
					prodNamesBfr.append(" + " + stoicStr + " " + name);							
				}				
			}			
		}
		
		if (this.irreversibleArrow == null) {
			this.irreversibleArrow = GraphicalInterfaceConstants.NOT_REVERSIBLE_ARROWS[2];
		}
		if (this.reversibleArrow == null) {
			this.reversibleArrow = GraphicalInterfaceConstants.REVERSIBLE_ARROWS[1];
		}
		// prevents arrow only from being displayed if there are no reactants and no products
		if (reacBfr.toString().trim().length() > 0 || prodBfr.toString().trim().length() > 0) {
			if (reversible.equals(GraphicalInterfaceConstants.BOOLEAN_VALUES[0])) {
				rxnBfr.append(reacBfr).append(" " + this.irreversibleArrow).append(prodBfr);
				rxnNamesBfr.append(reacNamesBfr).append(" " + this.irreversibleArrow).append(prodNamesBfr);
			} else {
				rxnBfr.append(reacBfr).append(" " + this.reversibleArrow).append(prodBfr);
				rxnNamesBfr.append(reacNamesBfr).append(" " + this.reversibleArrow).append(prodNamesBfr);
			}
		}
			
		equationAbbreviations = rxnBfr.toString();
		equationNames = rxnNamesBfr.toString();
		//System.out.println(rxnBfr.toString());
		//System.out.println(rxnNamesBfr.toString());
		
	}
	
	public void removeReactantByAbbr(String abbr) {
		for (int i = 0; i < reactants.size(); i++) {
			if (reactants.get(i).getMetaboliteAbbreviation().equals(abbr)) {
				reactants.remove(i);
			}
		}		
	}
	
	public void removeProductByAbbr(String abbr) {
		for (int i = 0; i < products.size(); i++) {
			if (products.get(i).getMetaboliteAbbreviation().equals(abbr)) {
				products.remove(i);
			}
		}		
	}
	
	public String metaboliteNameString(String metabAbbrev, String metabName, String compartment) {
		String metaboliteNameString = metabAbbrev;
		
		if (metabName != null && metabName.trim().length() > 0) {
			metaboliteNameString = metabName;
		}
		if (compartment != null && compartment.trim().length() > 0) {
			metaboliteNameString = compartment + "." + metaboliteNameString;
		}
		
		return metaboliteNameString;
	}
	
	@Override
	public String toString() {
		String reactantsString = "";
		for (int i = 0; i < reactants.size(); i++) {
			reactantsString += reactants.get(i).toString() + " ";
		}
		String productsString = "";
		for (int j = 0; j < products.size(); j++) {
			productsString += products.get(j).toString() + " ";
		}
		
		return "SBMLReactionEquation [reversible=" + reversible
		+ ", reversibleArrow=" + reversibleArrow
		+ ", irreversibleArrow=" + irreversibleArrow
		+ ", reactants=" + reactantsString
		+ ", products=" + productsString
		+ ", equationAbbreviations=" + equationAbbreviations
		+ ", equationNames=" + equationNames + "]\n";
	}
}
