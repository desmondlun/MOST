package edu.rutgers.MOST.data;

import java.util.ArrayList;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.presentation.GraphicalInterface;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;

public class SBMLReaction implements ModelReaction {
	
	private int id;	
	private String reactionAbbreviation;
	private String reactionName;
	private String knockout;
	private double fluxValue;
	private double minFlux;
	private double maxFlux;
	private String reactionEqunAbbr;
	private String reactionEqunNames;
	private String reversible;
	private double biologicalObjective;
	private double syntheticObjective;
	private double upperBound;
	private double lowerBound;	
	private String geneAssociation;
	private String proteinAssociation;
	private String subsystem;
	private String proteinClass;
	private ArrayList<String> metaValues;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getReactionAbbreviation() {
		return reactionAbbreviation;
	}

	public void setReactionAbbreviation(String reactionAbbreviation) {
		this.reactionAbbreviation = reactionAbbreviation;
	}
	
	public String getReactionName() {
		return reactionName;
	}

	public void setReactionName(String reactionName) {
		this.reactionName = reactionName;
	}

	public String getKnockout() {
		return knockout;
	}

	public void setKnockout(String knockout) {
		this.knockout = knockout;
	}

	public double getFluxValue() {
		return fluxValue;
	}

	public void setFluxValue(double fluxValue) {
		this.fluxValue = fluxValue;
	}
	
	public double getMinFlux() {
		return minFlux;
	}

	public void setMinFlux(double minFlux) {
		this.minFlux = minFlux;
	}

	public double getMaxFlux() {
		return maxFlux;
	}

	public void setMaxFlux(double maxFlux) {
		this.maxFlux = maxFlux;
	}

	public String getReactionEqunAbbr() {
		return reactionEqunAbbr;
	}

	public void setReactionEqunAbbr(String reactionEqunAbbr) {
		this.reactionEqunAbbr = reactionEqunAbbr;
	}

	public String getReactionEqunNames() {
		return reactionEqunNames;
	}

	public void setReactionEqunNames(String reactionEqunNames) {
		this.reactionEqunNames = reactionEqunNames;
	}
	
	public String getReversible() {
		return reversible;
	}

	public void setReversible(String reversible) {
		this.reversible = reversible;
	}
	
	public double getBiologicalObjective() {
		return biologicalObjective;
	}

	public void setBiologicalObjective(double biologicalObjective) {
		this.biologicalObjective = biologicalObjective;
	}

	public double getSyntheticObjective() {
		return syntheticObjective;
	}

	public void setSyntheticObjective(double syntheticObjective) {
		this.syntheticObjective = syntheticObjective;
	}

	public double getLowerBound() {
		return lowerBound;
	}

	public void setLowerBound(double lowerBound) {
		this.lowerBound = lowerBound;
	}
	
	public double getUpperBound() {
		return upperBound;
	}

	public void setUpperBound(double upperBound) {
		this.upperBound = upperBound;
	}

	public void setGeneAssociation(String geneAssociation) {
		this.geneAssociation = geneAssociation;
	}

	public String getGeneAssociation() {
		return geneAssociation;
	}

	public void setProteinAssociation(String proteinAssociation) {
		this.proteinAssociation = proteinAssociation;
	}

	public String getProteinAssociation() {
		return proteinAssociation;
	}

	public void setSubsystem(String subsystem) {
		this.subsystem = subsystem;
	}

	public String getSubsystem() {
		return subsystem;
	}

	public void setProteinClass(String proteinClass) {
		this.proteinClass = proteinClass;
	}

	public String getProteinClass() {
		return proteinClass;
	}
	
	public ArrayList<String> getMetaValues() {
		return metaValues;
	}

	public void setMetaValues(ArrayList<String> metaValues) {
		this.metaValues = metaValues;
	}

	public void update() {

		
	}

	public void loadById(Integer reactionId) {

		
	}

	public void loadByRow(Integer row) {
		ArrayList<String> meta = new ArrayList<String>();
		this.setId(Integer.valueOf((String) GraphicalInterface.reactionsTable.getModel().getValueAt(row, GraphicalInterfaceConstants.REACTIONS_ID_COLUMN)));
		this.setKnockout((String) GraphicalInterface.reactionsTable.getModel().getValueAt(row, GraphicalInterfaceConstants.KO_COLUMN));
		this.setFluxValue(Double.valueOf((String) GraphicalInterface.reactionsTable.getModel().getValueAt(row, GraphicalInterfaceConstants.FLUX_VALUE_COLUMN)));
		this.setMinFlux(Double.valueOf((String) GraphicalInterface.reactionsTable.getModel().getValueAt(row, GraphicalInterfaceConstants.MIN_FLUX_COLUMN)));
		this.setMaxFlux(Double.valueOf((String) GraphicalInterface.reactionsTable.getModel().getValueAt(row, GraphicalInterfaceConstants.MAX_FLUX_COLUMN)));
		this.setReactionAbbreviation((String) GraphicalInterface.reactionsTable.getModel().getValueAt(row, GraphicalInterfaceConstants.REACTION_ABBREVIATION_COLUMN));
		this.setReactionName((String) GraphicalInterface.reactionsTable.getModel().getValueAt(row, GraphicalInterfaceConstants.REACTION_NAME_COLUMN));
		this.setReactionEqunAbbr((String) GraphicalInterface.reactionsTable.getModel().getValueAt(row, GraphicalInterfaceConstants.REACTION_EQUN_ABBR_COLUMN));
		this.setReversible((String) GraphicalInterface.reactionsTable.getModel().getValueAt(row, GraphicalInterfaceConstants.REVERSIBLE_COLUMN));				
		this.setLowerBound(Double.valueOf((String) GraphicalInterface.reactionsTable.getModel().getValueAt(row, GraphicalInterfaceConstants.LOWER_BOUND_COLUMN)));
		this.setUpperBound(Double.valueOf((String) GraphicalInterface.reactionsTable.getModel().getValueAt(row, GraphicalInterfaceConstants.UPPER_BOUND_COLUMN)));
		this.setBiologicalObjective(Double.valueOf((String) GraphicalInterface.reactionsTable.getModel().getValueAt(row, GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_COLUMN)));
		this.setSyntheticObjective(Double.valueOf((String) GraphicalInterface.reactionsTable.getModel().getValueAt(row, GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_COLUMN)));
		this.setGeneAssociation((String) GraphicalInterface.reactionsTable.getModel().getValueAt(row, GraphicalInterfaceConstants.GENE_ASSOCIATION_COLUMN));
		this.setProteinAssociation((String) GraphicalInterface.reactionsTable.getModel().getValueAt(row, GraphicalInterfaceConstants.PROTEIN_ASSOCIATION_COLUMN));
		this.setSubsystem((String) GraphicalInterface.reactionsTable.getModel().getValueAt(row, GraphicalInterfaceConstants.SUBSYSTEM_COLUMN));
		this.setProteinClass((String) GraphicalInterface.reactionsTable.getModel().getValueAt(row, GraphicalInterfaceConstants.PROTEIN_CLASS_COLUMN));
		for (int i = 0; i < LocalConfig.getInstance().getReactionsMetaColumnNames().size(); i++) {
			meta.add((String) GraphicalInterface.reactionsTable.getModel().getValueAt(row, GraphicalInterfaceConstants.REACTIONS_COLUMN_NAMES.length + i));			
		}
		this.setMetaValues(meta);
	}
	
	/*
	@Override
	public String toString() {
		return "SBMLReaction [id=" + id + ", reactionAbbreviation=" + reactionAbbreviation
				+ ", biologicalObjective=" + biologicalObjective
				+ ", upperBound=" + upperBound + ", lowerBound=" + lowerBound
				+ ", fluxValue=" + fluxValue
				+ ", geneAssociation=" + geneAssociation
				+ ", reactionName=" + reactionName + ", reversible="
				+ reversible + "]";
	}
	*/
	
	@Override
	public String toString() {
		return "SBMLReaction [id=" + id
		        + ", fluxValue=" + fluxValue
				+ ", biologicalObjective=" + biologicalObjective
				+ ", upperBound=" + upperBound
				+ ", lowerBound=" + lowerBound				
				+ ", geneAssociation=" + geneAssociation
				+ ", knockout=" + knockout + "]";
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

