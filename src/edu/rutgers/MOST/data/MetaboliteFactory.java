package edu.rutgers.MOST.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.presentation.GraphicalInterface;

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

	public int metaboliteCount(String metabolite, String databaseName) {
		int count = 0;
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
			PreparedStatement prep = conn.prepareStatement("select count(metabolite_abbreviation) from metabolites where metabolite_abbreviation=?;");
			prep.setString(1, metabolite);
			ResultSet rs = prep.executeQuery();
			//if metabolite_abbreviation is in table will return 1, else 0
			count = rs.getInt("count(metabolite_abbreviation)");			
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}
		return count;
	}

	public void addMetabolite(String metabolite, String databaseName) {
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
			PreparedStatement prep1 = conn.prepareStatement("insert into metabolites (metabolite_abbreviation, boundary, used) values(?, 'false', 'true');");
			prep1.setString(1, metabolite);

			prep1.addBatch();

			conn.setAutoCommit(false);
			prep1.executeBatch();
			conn.setAutoCommit(true);

			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}		
	}

	public boolean isUnused(int id, String databaseName) {
		String used = "";
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
			PreparedStatement prep1 = conn.prepareStatement("select used from metabolites where id=?;");
			prep1.setInt(1, id);

			ResultSet rs = prep1.executeQuery();
			used = rs.getString("used");

			conn.close();

			if (used.compareTo("false") == 0) {
				return true;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}
		return false;		
	}

	public void setMetaboliteUsedValue(Integer metaboliteId, String databaseName, String booleanValue) {
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
			PreparedStatement prep1 = conn.prepareStatement(
			"update metabolites set used=? where id=?;");
			prep1.setString(1, booleanValue);
			prep1.setInt(2, metaboliteId);

			prep1.execute();
			conn.setAutoCommit(true);   		    

			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}
	}

	public int maximumId(String databaseName) {
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

	public void deleteAllUnusedMetabolites(String databaseName) {
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
			Statement st = conn.createStatement();
			String str = ("DELETE FROM metabolites where used like 'false';");

			st.executeUpdate(str);

			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}		
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
		String databaseName = "Model_Reconstruction_corrected";
		MetaboliteFactory mFactory = new MetaboliteFactory();
		mFactory.deleteAllUnusedMetabolites(databaseName);
	}

}

