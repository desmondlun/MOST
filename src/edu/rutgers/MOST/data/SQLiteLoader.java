package edu.rutgers.MOST.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.rutgers.MOST.config.LocalConfig;

// creates invalid reaction list, metabolite id name map, and metabolite used map 
// when loading database for use in highlighting and deleting unused metabolites
public class SQLiteLoader {

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
				if (reaction != null) {
					invalidReactions.add(reaction);
				}				
			}	
					
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}	
		
		return invalidReactions;
		
	}
	
	public Map<String, Object> metaboliteIdNameMap(String databaseName) {
		Map<String, Object> metaboliteIdNameMap = new HashMap<String, Object>();
		
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
			ResultSet rs = stat.executeQuery("select id, metabolite_abbreviation from metabolites;");
			while (rs.next()) {
				int id = rs.getInt("id");
				String metaboliteAbbreviation = rs.getString("metabolite_abbreviation");
				metaboliteIdNameMap.put(metaboliteAbbreviation, new Integer(id));
			}
					
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}	
		
		return metaboliteIdNameMap;
	}
	
	public Map<Object, String> metaboliteNameIdMap(String databaseName) {
		HashMap<Object, String> metaboliteNameIdMap = new HashMap<Object, String>();
		
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
			ResultSet rs = stat.executeQuery("select id, metabolite_abbreviation from metabolites;");
			while (rs.next()) {
				int id = rs.getInt("id");
				String metaboliteAbbreviation = rs.getString("metabolite_abbreviation");
				metaboliteNameIdMap.put(new Integer(id), metaboliteAbbreviation);
			}
					
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}	
		
		return metaboliteNameIdMap;
	}
	
	public Map<String, Object> metaboliteUsedMap(String databaseName) {
		Map<String, Object> metaboliteUsedMap = new HashMap<String, Object>();
		Map<Object, String> metaboliteNameIdMap = metaboliteNameIdMap(databaseName);
		
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
			ResultSet rsReac = stat.executeQuery("select metabolite_id from reaction_reactants;");
			while (rsReac.next()) {
				int reacId = rsReac.getInt("metabolite_id");
				String reactant = metaboliteNameIdMap.get(reacId);
				if (metaboliteUsedMap.containsKey(reactant)) {
					int usedCount = (Integer) metaboliteUsedMap.get(reactant);
					metaboliteUsedMap.put(reactant, new Integer(usedCount + 1));									
				} else {
					metaboliteUsedMap.put(reactant, new Integer(1));
				}
			}
			
			ResultSet rsProd = stat.executeQuery("select metabolite_id from reaction_products;");
			while (rsProd.next()) {
				int prodId = rsProd.getInt("metabolite_id");
				String product = metaboliteNameIdMap.get(prodId);
				if (metaboliteUsedMap.containsKey(product)) {
					int usedCount = (Integer) metaboliteUsedMap.get(product);
					metaboliteUsedMap.put(product, new Integer(usedCount + 1));									
				} else {
					metaboliteUsedMap.put(product, new Integer(1));
				}
			}
					
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}	
		
		return metaboliteUsedMap;
	}
	
}
