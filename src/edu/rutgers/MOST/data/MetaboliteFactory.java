package edu.rutgers.MOST.data;

import edu.rutgers.MOST.config.LocalConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

public class MetaboliteFactory {
	private String sourceType;
	private String databaseName;
	
	public MetaboliteFactory(String sourceType, String databaseName) {
		this.sourceType = sourceType;
		this.databaseName = databaseName;
	}
	
	public ModelMetabolite getMetaboliteById(Integer metaboliteId){


		if("SBML".equals(sourceType)){
			SBMLMetabolite metabolite = new SBMLMetabolite();
			metabolite.setDatabaseName(databaseName);
			metabolite.loadById(metaboliteId);
			return metabolite;
		}
		return new SBMLMetabolite(); //Default behavior.
	}

	public ArrayList<Integer> participatingReactions(String metaboliteAbbreviation) {
		int reactionId = 0;
		ArrayList<Integer> participatingReactions = new ArrayList<Integer>();
		System.out.println("id mf " + LocalConfig.getInstance().getMetaboliteIdNameMap());
		if (metaboliteAbbreviation != null) {
			int metabId = (Integer) LocalConfig.getInstance().getMetaboliteIdNameMap().get(metaboliteAbbreviation);
			System.out.println(metabId);
			
			String queryString = "jdbc:sqlite:" + databaseName + ".db";
			try {
				Class.forName("org.sqlite.JDBC"); 
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Connection conn;
			try {
				conn = DriverManager.getConnection(queryString);
				PreparedStatement prep1 = conn.prepareStatement("select reaction_id from reaction_reactants where metabolite_id=?;");
				prep1.setInt(1, metabId);

				conn.setAutoCommit(true);
				ResultSet rs1 = prep1.executeQuery();			
				while (rs1.next()) {
					reactionId = rs1.getInt("reaction_id");
					participatingReactions.add(reactionId);
				}

				PreparedStatement prep2 = conn.prepareStatement("select reaction_id from reaction_products where metabolite_id=?;");
				prep2.setInt(1, metabId);

				conn.setAutoCommit(true);
				ResultSet rs2 = prep2.executeQuery();			
				while (rs2.next()) {
					reactionId = rs2.getInt("reaction_id");
					if (!participatingReactions.contains(reactionId)) {
						participatingReactions.add(reactionId);
					}
				}
				
				conn.close();				
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();			
			}	
		}
		
		
		
		System.out.println(participatingReactions);
		return participatingReactions;
	}

	public Vector<ModelMetabolite> getAllInternalMetabolites() {
		Vector<ModelMetabolite> metabolites = new Vector<ModelMetabolite>();
		
		if("SBML".equals(sourceType)){
			try {
				Class.forName("org.sqlite.JDBC");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return metabolites;
			}
			Connection conn;
			try {
				conn = DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db"); // TODO:
				// Make
				// this
				// configurable
				PreparedStatement prep = conn
				.prepareStatement("select id, metabolite_abbreviation, metabolite_name, charge, compartment, "
						+ " boundary "
						+ " from metabolites where length(metabolite_abbreviation) > 0 and boundary = 'false';");
				conn.setAutoCommit(true);
				ResultSet rs = prep.executeQuery();
				while (rs.next()) {
					SBMLMetabolite metabolite = new SBMLMetabolite();
					metabolite.setId(rs.getInt("id"));
					metabolite.setMetaboliteAbbreviation(rs.getString("metabolite_abbreviation"));
					metabolite.setMetaboliteName(rs.getString("metabolite_name"));
					metabolite.setCharge(rs.getString("charge"));
					metabolite.setCompartment(rs.getString("compartment"));
					metabolite.setBoundary(rs.getString("boundary"));

					metabolites.add(metabolite);
				}
				rs.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return metabolites;
			}
		}
		
		return metabolites;
	}
	
	public Integer metaboliteId(String metaboliteAbbreviation) {
		Integer metaboliteId = 0;

		String queryString = "jdbc:sqlite:" + databaseName + ".db";
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Connection conn;
		try {
			conn = DriverManager.getConnection(queryString);
			PreparedStatement prep1 = conn.prepareStatement("select id from metabolites where metabolite_abbreviation=?;");
			prep1.setString(1, metaboliteAbbreviation);

			conn.setAutoCommit(true);
			ResultSet rs = prep1.executeQuery();
			metaboliteId = rs.getInt("id");

			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}	

		return metaboliteId;
	}
	
	public int maximumId() {
		int max = 0;
		String queryString = "jdbc:sqlite:" + databaseName + ".db";
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Connection conn;
		try {
			conn = DriverManager.getConnection(queryString);
			PreparedStatement prep = conn.prepareStatement("select max(id) from metabolites;");
			ResultSet rs = prep.executeQuery();
			//if metabolite_abbreviation is in table will return 1, else 0
			max = rs.getInt("max(id)");			
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}
		return max;
	}
	
	public static void main(String[] args) {
		
	}

}

