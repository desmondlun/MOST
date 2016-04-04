package edu.rutgers.MOST.data;

import java.util.ArrayList;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.presentation.GraphicalInterface;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;

public class SBMLMetabolite implements ModelMetabolite {
	
	private Integer id;
	private String metaboliteAbbreviation;
	private String metaboliteName;
	private String compartment;
	private String charge;	
	private String boundary;
	private String keggId;
	private String chebiId;
	private ArrayList<String> metaValues;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setMetaboliteAbbreviation(String metaboliteAbbreviation) {
		this.metaboliteAbbreviation = metaboliteAbbreviation;
	}
	
	public String getMetaboliteAbbreviation() {
		return metaboliteAbbreviation;
	}

	public String getMetaboliteName() {
		return metaboliteName;
	}
	
	public void setMetaboliteName(String metaboliteName) {
		this.metaboliteName = metaboliteName;
	}
	
	public String getCompartment() {
		return compartment;
	}
	
	public void setCompartment(String compartment) {
		this.compartment = compartment;
	}
	
	public String getCharge() {
		return charge;
	}
	
	public void setCharge(String charge) {
		this.charge = charge;
	}	

	public String getBoundary() {
		return boundary;
	}
	
	public void setBoundary(String boundary) {
		this.boundary = boundary;
	}
	
	public String getKeggId() {
		return keggId;
	}

	public void setKeggId(String keggId) {
		this.keggId = keggId;
	}

	public String getChebiId() {
		return chebiId;
	}

	public void setChebiId(String chebiId) {
		this.chebiId = chebiId;
	}

	public ArrayList<String> getMetaValues() {
		return metaValues;
	}

	public void setMetaValues(ArrayList<String> metaValues) {
		this.metaValues = metaValues;
	}

	public void update() {

	}

	public void loadById(Integer id) {

	}

	public void loadByRow(Integer row) {
		ArrayList<String> meta = new ArrayList<String>();
		this.setId(Integer.valueOf((String) GraphicalInterface.metabolitesTable.getModel().getValueAt(row, GraphicalInterfaceConstants.METABOLITE_ID_COLUMN)));
		this.setMetaboliteAbbreviation((String) GraphicalInterface.metabolitesTable.getModel().getValueAt(row, GraphicalInterfaceConstants.METABOLITE_ABBREVIATION_COLUMN));
		this.setMetaboliteName((String) GraphicalInterface.metabolitesTable.getModel().getValueAt(row, GraphicalInterfaceConstants.METABOLITE_NAME_COLUMN));
		this.setCharge((String) GraphicalInterface.metabolitesTable.getModel().getValueAt(row, GraphicalInterfaceConstants.CHARGE_COLUMN));
		this.setCompartment((String) GraphicalInterface.metabolitesTable.getModel().getValueAt(row, GraphicalInterfaceConstants.COMPARTMENT_COLUMN));
		this.setBoundary((String) GraphicalInterface.metabolitesTable.getModel().getValueAt(row, GraphicalInterfaceConstants.BOUNDARY_COLUMN));
		for (int i = 0; i < LocalConfig.getInstance().getMetabolitesMetaColumnNames().size(); i++) {
			// Strange non-reproducible error when saving as SBML: 
			// java.lang.ArrayIndexOutOfBoundsException: 6 >= 6 at java.util.Vector.elementAt(Unknown Source)
			// at javax.swing.table.DefaultTableModel.getValueAt(Unknown Source) at edu.rutgers.MOST.data.SBMLMetabolite.loadByRow(SBMLMetabolite.java:92)...
			// This error may not show up one time then show next time for same exact model being saved.
			// The problem that may be caused would be that the values in meta columns will be missing.
			meta.add((String) GraphicalInterface.metabolitesTable.getModel().getValueAt(row, GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length + i));	
		}
		this.setMetaValues(meta);
	}
	
	/*
	@Override
	public String toString() {
		return "SBMLMetabolite [id=" + id + ", metaboliteAbbreviation=" + metaboliteAbbreviation
		+ ", metaboliteName=" + metaboliteName
		+ ", compartment=" + compartment
		+ ", charge=" + charge
		+ ", boundary=" + boundary + "]";
	}
	*/
	
	@Override
	public String toString() {
		return "SBMLMetabolite [id=" + id 
		+ ", metaboliteAbbreviation=" + metaboliteAbbreviation
		+ ", metaboliteName=" + metaboliteName
		+ ", compartment=" + compartment
		+ ", charge=" + charge
		+ ", boundary=" + boundary
		+ ", keggId=" + keggId
		+ ", chebiId=" + chebiId
		+ ", metaValues=" + metaValues + "]";
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
