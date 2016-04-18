package edu.rutgers.MOST.data;

public class PathwayMetaboliteNode {
	private String compartment;
	// abbreviation will be key, from PathwayMetaboliteData abbreviation
	// + compartment abbreviation, such as "_c"
	private String abbreviation;
	// key, used for tooltip
	private String name;
	private String reactionName;
	// links node to id field
	private String dataId;
	// links to model kegg id
	private String keggId;
	private String chebiId;
	private double xPosition;
	private double yPosition;
	private String border;
	private String type;
	
	public String getCompartment() {
		return compartment;
	}

	public void setCompartment(String compartment) {
		this.compartment = compartment;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReactionName() {
		return reactionName;
	}

	public void setReactionName(String reactionName) {
		this.reactionName = reactionName;
	}

	public String getDataId() {
		return dataId;
	}

	public void setDataId(String dataId) {
		this.dataId = dataId;
	}

	public String getKeggId() {
		return keggId;
	}

	public void setKeggId(String keggId) {
		this.keggId = keggId;
	}

	public String getChebiId()
	{
		return chebiId;
	}

	public void setChebiId( String chebiId )
	{
		this.chebiId = chebiId;
	}

	public double getxPosition() {
		return xPosition;
	}

	public void setxPosition(double xPosition) {
		this.xPosition = xPosition;
	}

	public double getyPosition() {
		return yPosition;
	}

	public void setyPosition(double yPosition) {
		this.yPosition = yPosition;
	}

	public String getBorder() {
		return border;
	}

	public void setBorder(String border) {
		this.border = border;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "PathwayMetabolite Node [compartment=" + compartment
		+ ", abbreviation=" + abbreviation
		+ ", name=" + name
		+ ", reactionName=" + reactionName
		+ ", dataId=" + dataId
		+ ", keggId=" + keggId
		+ ", chebiId=" + chebiId
		+ ", xPosition=" + xPosition
		+ ", yPosition=" + yPosition	
		+ ", border=" + border
		+ ", type=" + type + "]";
	}
}
