package edu.rutgers.MOST.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ReactantFactory {

	
	
	public ModelReactant getReactantByReactionId(Integer reactionId, String sourceType, String databaseName){
		
		
		if("SBML".equals(sourceType)){
			SBMLReactant reactant = new SBMLReactant();
			reactant.setDatabaseName(databaseName);
			reactant.loadByReactionId(reactionId);
			return reactant;
		}
		return new SBMLReactant(); //Default behavior.
	}
	
	public ArrayList<ModelReactant> getReactantsByReactionId(Integer reactionId,
			String sourceType, String databaseName){
		ArrayList<ModelReactant> reactants = new ArrayList();
		
		if("SBML".equals(sourceType)){
			
			try {
				Class.forName("org.sqlite.JDBC");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return reactants;
			}
			Connection conn;
			try {
				conn = DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db"); // TODO:
																			// Make
																			// this
																			// configurable
				PreparedStatement prep = conn
						.prepareStatement("select reaction_id, metabolite_id, stoic from reaction_reactants where reaction_id = ?;");
				prep.setInt(1, reactionId);
				conn.setAutoCommit(true);
				ResultSet rs = prep.executeQuery();
				while (rs.next()) {
					SBMLReactant reactant = new SBMLReactant();
					reactant.setReactionId(rs.getInt("reaction_id"));
					reactant.setMetaboliteId(rs.getInt("metabolite_id"));
					reactant.setStoic(rs.getDouble("stoic"));
					reactants.add(reactant);
				}
				rs.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return reactants;
			}
		}
		return reactants;
	}
}
