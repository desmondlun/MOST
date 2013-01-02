package edu.rutgers.MOST.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

// checks database when loading for value errors
public class DatabaseErrorChecker {

	// since invalid reactions are not added to reaction_reactants and
	// reaction_products tables, checking which id's in reactions table
	// are not present in these tables will produce list of invalid reactions.
	public ArrayList<String> invalidReactions(String databaseName) {
		ArrayList<String> invalidReactions = new ArrayList<String>();
		ArrayList<Integer> reactantIds = new ArrayList<Integer>();
		ArrayList<Integer> reactionIds = new ArrayList<Integer>();
		ArrayList<Integer> invalidReactionIds = new ArrayList<Integer>();
		
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
			Statement stat = conn.createStatement();
			ResultSet rsReactantIds = stat.executeQuery("select reaction_id from reaction_reactants;");
			while (rsReactantIds.next()) {
				int id = rsReactantIds.getInt("reaction_id");
				if (!reactantIds.contains(id)) {
					reactantIds.add(id);
				}
			}
			ResultSet rsProductIds = stat.executeQuery("select reaction_id from reaction_products;");
			while (rsProductIds.next()) {
				int id = rsProductIds.getInt("reaction_id");
				if (!reactantIds.contains(id)) {
					reactantIds.add(id);
				}
			}
			ResultSet rsIds = stat.executeQuery("select id from reactions;");
			while (rsIds.next()) {
				int id = rsIds.getInt("id");
				reactionIds.add(id);
			}
			
			for (int i = 0; i < reactionIds.size(); i++) {
				if (!reactantIds.contains(reactionIds.get(i))) {
					invalidReactionIds.add(reactionIds.get(i));
				}
			}
			
			PreparedStatement prep = conn.prepareStatement("select reaction_string from reactions where id=?;");			
			for (int i = 0; i < invalidReactionIds.size(); i++) {
				prep.setInt(1, invalidReactionIds.get(i));	
				ResultSet rs = prep.executeQuery();
				String reaction = rs.getString("reaction_string");					
				invalidReactions.add(reaction);
			}	
					
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}	
		
		return invalidReactions;
		
	}
	
}
