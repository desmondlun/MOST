package edu.rutgers.MOST.data;

public class SBMLReactant implements ModelReactant {

	private Integer reactionId;
	private Integer metaboliteId;
	private String metaboliteAbbreviation;
	private String metaboliteName;
	private String compartment;
	private double stoic;

	public Integer getReactionId() {
		return reactionId;
	}

	public void setReactionId(Integer reactionId) {
		this.reactionId = reactionId;
	}
	
	public void setMetaboliteId(Integer metaboliteId) {
		this.metaboliteId = metaboliteId;
	}

	public Integer getMetaboliteId() {
		return metaboliteId;
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

	public void setStoic(double stoic) {
		this.stoic = stoic;
	}

	public double getStoic() {
		return stoic;
	}

	public void update() {

	}

	public void loadByReactionId(Integer reactionId) {

	}

	@Override
	public String toString() {
		return "SBMLReactant [metaboliteId=" + metaboliteId
		+ ", reactionId=" + reactionId
		+ ", metaboliteAbbreviation=" + metaboliteAbbreviation
		+ ", metaboliteName=" + metaboliteName
		+ ", stoic=" + stoic + "]";
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}



