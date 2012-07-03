package edu.rutgers.MOST.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.rutgers.MOST.config.LocalConfig;

public class SBMLProduct implements ModelProduct {
    
	private String databaseName;
	private Integer reactionId;
	private Integer metaboliteId;
	private String metaboliteAbbreviation;
	private double stoic;

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

	public void setStoic(double stoic) {
		this.stoic = stoic;
	}

	public double getStoic() {
		return stoic;
	}

	public boolean update() {

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
	    
			PreparedStatement prep = conn
	                 .prepareStatement("insert into reaction_products (reaction_id, metabolite_id, stoic) values (?, (select id from metabolites where metabolite_abbreviation = ?), ?);");
			prep.setInt(1, this.getReactionId());
			prep.setString(2, this.getMetaboliteAbbreviation());
			prep.setDouble(3, this.getStoic());
			conn.setAutoCommit(true);
			prep.executeUpdate();
			
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
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
					.prepareStatement("select reaction_id, metabolite_id, stoic from reaction_products where reaction_id = ?;");
			prep.setInt(1, reactionId);
			conn.setAutoCommit(true);
			ResultSet rs = prep.executeQuery();
			while (rs.next()) {
				this.setReactionId(rs.getInt("reaction_id"));
				this.setMetaboliteId(rs.getInt("metabolite_id"));
				this.setStoic(rs.getDouble("stoic"));
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

	@Override
	public String toString() {
		return "SBMLProduct [reactionId=" + reactionId
				+ ", metaboliteAbbreviation=" + metaboliteAbbreviation
				+ ", stoic=" + stoic + "]";
	}
	
	public String createConnectionStatement(String databaseName) {
		return "jdbc:sqlite:" + databaseName + ".db";
	}
	
	public void getAbbreviationFromId(int id) {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Connection conn;
		try {
			conn = DriverManager.getConnection(createConnectionStatement(LocalConfig.getInstance().getDatabaseName())); // TODO:
																		// Make
																		// this
																		// configurable
			PreparedStatement prep = conn
					.prepareStatement("select metabolite_abbreviation from metabolites where id = ?;");
			prep.setInt(1, id);
			conn.setAutoCommit(true);
			ResultSet rs = prep.executeQuery();
			while (rs.next()) {
				this.setMetaboliteAbbreviation(rs.getString("metabolite_abbreviation"));
			}
			rs.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}



