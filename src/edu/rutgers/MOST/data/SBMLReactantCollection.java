package edu.rutgers.MOST.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class SBMLReactantCollection implements ModelReactantCollection {
    
	private String databaseName;
	private Integer reactionId;
	private ArrayList<ModelReactant> reactantList;
	

	public ArrayList<ModelReactant> getReactantList() {
		return reactantList;
	}

	public void setReactantList(ArrayList<ModelReactant> reactantList) {
		this.reactantList = reactantList;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	
	public String getDatabaseName() {
		return databaseName;
	}
	
	public void setReactionId(Integer reactionId) {
		this.reactionId = reactionId;
	}

	public Integer getReactionId() {
		return reactionId;
	}

	public boolean loadByReactionId(Integer reactionId) {

		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		Connection conn;
		try {
			conn = DriverManager.getConnection(createConnectionStatement(getDatabaseName())); // TODO:
																		// Make
																		// this
																		// configurable
			PreparedStatement prep = conn
					.prepareStatement("select reaction_id, metabolite_id, stoic, metabolite_abbreviation from reaction_reactants, metabolites where reaction_reactants.metabolite_id = metabolites.id and reaction_id = ?;");
			prep.setInt(1, reactionId);
			conn.setAutoCommit(true);
			ResultSet rs = prep.executeQuery();
			reactantList = new ArrayList<ModelReactant>();
			
			while (rs.next()) {
				SBMLReactant aReactant = new SBMLReactant();
				aReactant.setReactionId(rs.getInt("reaction_id"));
				aReactant.setMetaboliteId(rs.getInt("metabolite_id"));
				aReactant.setStoic(rs.getDouble("stoic"));
				aReactant.setMetaboliteAbbreviation(rs.getString("metabolite_abbreviation"));
				this.reactantList.add(aReactant);
			}
			
			rs.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean loadAll() {

		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		Connection conn;
		try {
			conn = DriverManager.getConnection(createConnectionStatement(getDatabaseName())); // TODO:
																		// Make
																		// this
																		// configurable
			PreparedStatement prep = conn
					.prepareStatement("select reaction_id, metabolite_id, stoic, metabolite_abbreviation from reaction_reactants, metabolites where reaction_reactants.metabolite_id = metabolites.id;");
			conn.setAutoCommit(true);
			ResultSet rs = prep.executeQuery();
			reactantList = new ArrayList<ModelReactant>();
			
			while (rs.next()) {
				SBMLReactant aReactant = new SBMLReactant();
				aReactant.setReactionId(rs.getInt("reaction_id"));
				aReactant.setMetaboliteId(rs.getInt("metabolite_id"));
				aReactant.setStoic(rs.getDouble("stoic"));
				aReactant.setMetaboliteAbbreviation(rs.getString("metabolite_abbreviation"));
				this.reactantList.add(aReactant);
			}
			
			rs.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}	
	
	public String createConnectionStatement(String databaseName) {
		return "jdbc:sqlite:" + getDatabaseName() + ".db";
	}
	
	

	

	public static void main(String[] args) {
		ReactantFactory aReactantFactory = new ReactantFactory("SBML", "test_03182012");
		ArrayList<ModelReactant> reactants = aReactantFactory.getReactantsByReactionId(1);
		Iterator<ModelReactant> iterator = reactants.iterator();
		 
		while(iterator.hasNext()){
			SBMLReactant aReactant = (SBMLReactant)iterator.next();
			System.out.print("\nabbr" + aReactant.toString());
		}
	}

}


