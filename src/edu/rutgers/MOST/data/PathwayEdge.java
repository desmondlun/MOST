package edu.rutgers.MOST.data;

public class PathwayEdge {
	
	private String startMetabolite;
	private String endReaction;
	private String reversible;
	private int thickness;
	
	public String getStartMetabolite() {
		return startMetabolite;
	}
	public void setStartMetabolite(String startMetabolite) {
		this.startMetabolite = startMetabolite;
	}
	public String getEndReaction() {
		return endReaction;
	}
	public void setEndReaction(String endReaction) {
		this.endReaction = endReaction;
	}
	public String getReversible() {
		return reversible;
	}
	public void setReversible(String reversible) {
		this.reversible = reversible;
	}
	public int getThickness() {
		return thickness;
	}
	public void setThickness(int thickness) {
		this.thickness = thickness;
	}
	
	@Override
	public String toString() {
		return "Pathway Edge [startMetabolite=" + startMetabolite
		+ ", endReaction=" + endReaction
		+ ", reversible=" + reversible + "]";
	}

}
