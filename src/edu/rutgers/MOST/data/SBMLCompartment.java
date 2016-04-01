package edu.rutgers.MOST.data;

public class SBMLCompartment {
	
	private String id;
	private String name;
	private String outside;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOutside() {
		return outside;
	}
	public void setOutside(String outside) {
		this.outside = outside;
	}

	@Override
	public String toString() {
		return "SBMLCompartment [id=" + id 
		+ ", name=" + name
		+ ", outside=" + outside + "]";
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
}
