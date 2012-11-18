package edu.rutgers.MOST.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MetaboliteFactory {

	public ModelMetabolite getMetaboliteById(Integer metaboliteId, String sourceType, String databaseName){


		if("SBML".equals(sourceType)){
			SBMLMetabolite metabolite = new SBMLMetabolite();
			metabolite.setDatabaseName(databaseName);
			metabolite.loadById(metaboliteId);
			return metabolite;
		}
		return new SBMLMetabolite(); //Default behavior.
	}

	public Integer metaboliteId(String databaseName, String metaboliteAbbreviation) {
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

	public static void main(String[] args) {
		
	}

}

