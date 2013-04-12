package edu.rutgers.MOST.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;

public class DatabaseCreator {

	public void createDatabase(String databaseName) {

		String metaString = "";
		for (int i = 0; i < 15; i++) {
			String meta = ", meta_" + (i + 1)+ " varchar(500)";
			metaString += meta;
		}
		
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//closeConnection();
		try {
			Connection conn =
				DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db");
			LocalConfig.getInstance().setCurrentConnection(conn);
			Statement stat = conn.createStatement();

			stat.executeUpdate("drop table if exists metabolites;");
			stat.executeUpdate("create table metabolites (id INTEGER PRIMARY KEY, " 
					+ " metabolite_abbreviation varchar(255), metabolite_name varchar(255), "
					+ " charge varchar(5), compartment varchar(255), boundary varchar(5) " 
					+ metaString + ");");				

			stat.executeUpdate("drop table if exists reactions;");
			stat.executeUpdate("create table reactions (id INTEGER PRIMARY KEY, " 
					+ " knockout varchar(6), flux_value double, reaction_abbreviation varchar(255), reaction_name varchar(500), "
					+ " reaction_string varchar(500), reversible varchar(6), lower_bound double, " 
					+ " upper_bound double, biological_objective double" + metaString + ");");

			stat.executeUpdate("drop table if exists reaction_reactants;");
			stat.executeUpdate("CREATE TABLE reaction_reactants (reaction_id INTEGER, " 
					+ " metabolite_id INTEGER, stoic FLOAT);");

			stat.executeUpdate("drop table if exists reaction_products;");
			stat.executeUpdate("CREATE TABLE reaction_products (reaction_id INTEGER, " 
					+ " metabolite_id INTEGER, stoic FLOAT);");

			stat.executeUpdate("drop table if exists reactions_meta_info;");		    
			stat.executeUpdate("CREATE TABLE reactions_meta_info (id INTEGER PRIMARY KEY, meta_column_name varchar(100));");

			stat.executeUpdate("drop table if exists metabolites_meta_info;");		    
			stat.executeUpdate("CREATE TABLE metabolites_meta_info (id INTEGER PRIMARY KEY, meta_column_name varchar(100));");

			conn.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
	}

	public void addRows(String databaseName, int numMetaboliteRows, int numReactionRows) {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		closeConnection();
		try {
			Connection conn =
				DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db");
			LocalConfig.getInstance().setCurrentConnection(conn);
			Statement stat = conn.createStatement();
			
			try {			
				stat.executeUpdate("BEGIN TRANSACTION");			
				for (int m = 1; m < numMetaboliteRows + 1; m++) {
					String metabInsert = "insert into metabolites (id, boundary) values (" + m + ", '" + GraphicalInterfaceConstants.BOUNDARY_DEFAULT + "');";
					stat.executeUpdate(metabInsert);				
				}
				for (int r = 1; r < numReactionRows + 1; r++) {
					String reacInsert = "insert into reactions (id, knockout, flux_value, reversible, lower_bound, upper_bound, biological_objective) values " 
							+ "(" + r + ", '" + GraphicalInterfaceConstants.KO_DEFAULT + "', " + GraphicalInterfaceConstants.FLUX_VALUE_DEFAULT + "" 
							+ ", '"  + GraphicalInterfaceConstants.REVERSIBLE_DEFAULT + "', " + GraphicalInterfaceConstants.LOWER_BOUND_DEFAULT + "" 
							+ ", " + GraphicalInterfaceConstants.UPPER_BOUND_DEFAULT + ", " + GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_DEFAULT + ");";
					stat.executeUpdate(reacInsert);				
				}
				stat.executeUpdate("COMMIT");
			} catch (Exception e) {
				stat.executeUpdate("ROLLBACK"); // throw away all updates since BEGIN TRANSACTION
			}
			conn.close();
		}catch(SQLException e){

			e.printStackTrace();

		}
	}

	public void addMetaboliteRow(String databaseName) {

		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		closeConnection();
		try {
			Connection conn =
				DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db");
			LocalConfig.getInstance().setCurrentConnection(conn);
			
			PreparedStatement prep = conn.prepareStatement(
			"insert into metabolites (id, boundary) values (?, ?);");

			prep.setInt(1, maxMetaboliteId(databaseName) + 1);
			prep.setString(2, GraphicalInterfaceConstants.BOUNDARY_DEFAULT);

			prep.addBatch();

			conn.setAutoCommit(false);
			prep.executeBatch();
			conn.setAutoCommit(true);

			conn.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void addReactionRow(String databaseName) {

		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		closeConnection();
		try {
			Connection conn =
				DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db");
			LocalConfig.getInstance().setCurrentConnection(conn);
			
			PreparedStatement prep = conn.prepareStatement(
			"insert into reactions (id, knockout, flux_value, reversible, lower_bound, upper_bound, biological_objective) values (?, ?, ?, ?, ?, ?, ?);");

			prep.setInt(1, maxReactionId(databaseName) + 1);
			prep.setString(2, GraphicalInterfaceConstants.KO_DEFAULT);
			prep.setDouble(3, GraphicalInterfaceConstants.FLUX_VALUE_DEFAULT);
			prep.setString(4, GraphicalInterfaceConstants.REVERSIBLE_DEFAULT);
			prep.setDouble(5, GraphicalInterfaceConstants.LOWER_BOUND_DEFAULT);
			prep.setDouble(6, GraphicalInterfaceConstants.UPPER_BOUND_DEFAULT);
			prep.setDouble(7, GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_DEFAULT);

			prep.addBatch();

			conn.setAutoCommit(false);
			prep.executeBatch();
			conn.setAutoCommit(true);
			
			conn.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void deleteReactionRow(String databaseName, int id) {

		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		closeConnection();
		try {
			Connection conn =
				DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db");
			LocalConfig.getInstance().setCurrentConnection(conn);
			
			PreparedStatement prep = conn.prepareStatement(
			"delete from reactions where id = ?;");

			prep.setInt(1, id);

			prep.addBatch();

			conn.setAutoCommit(false);
			prep.executeBatch();
			conn.setAutoCommit(true);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		closeConnection();
		try {
			Connection conn =
				DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db");
			LocalConfig.getInstance().setCurrentConnection(conn);

			PreparedStatement prep = conn.prepareStatement(
			"delete from reaction_reactants where reaction_id = ?;");

			prep.setInt(1, id);

			prep.addBatch();

			conn.setAutoCommit(false);
			prep.executeBatch();
			conn.setAutoCommit(true);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		closeConnection();
		try {
			Connection conn =
				DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db");
			LocalConfig.getInstance().setCurrentConnection(conn);

			PreparedStatement prep = conn.prepareStatement(
			"delete from reaction_products where reaction_id = ?;");

			prep.setInt(1, id);

			prep.addBatch();

			conn.setAutoCommit(false);
			prep.executeBatch();
			conn.setAutoCommit(true);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void deleteMetabolitesRow(String databaseName, int id) {

		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		closeConnection();
		try {
			Connection conn =
				DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db");
			LocalConfig.getInstance().setCurrentConnection(conn);

			PreparedStatement prep = conn.prepareStatement(
			"delete from metabolites where id = ?;");

			prep.setInt(1, id);

			prep.addBatch();

			conn.setAutoCommit(false);
			prep.executeBatch();
			conn.setAutoCommit(true);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	} 

	public void createBlankReactionsTable(String databaseName, int numReactionRows) {

		String metaString = "";
		for (int i = 0; i < 15; i++) {
			String meta = ", meta_" + (i + 1)+ " varchar(500)";
			metaString += meta;
		}
		
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		closeConnection();
		try {
			Connection conn =
				DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db");
			LocalConfig.getInstance().setCurrentConnection(conn);

			Statement stat = conn.createStatement();
			stat.executeUpdate("drop table if exists reactions;");	
			stat.executeUpdate("create table reactions (id INTEGER PRIMARY KEY, " 
					+ " knockout varchar(6), flux_value double, reaction_abbreviation varchar(255), reaction_name varchar(500), "
					+ " reaction_string varchar(500), reversible varchar(6), lower_bound double, " 
					+ " upper_bound double, biological_objective double" + metaString + ");");
			
			try {			
				stat.executeUpdate("BEGIN TRANSACTION");			
				for (int r = 1; r < numReactionRows + 1; r++) {
					String reacInsert = "insert into reactions (id, knockout, flux_value, reversible, lower_bound, upper_bound, biological_objective) values " 
							+ "(" + r + ", '" + GraphicalInterfaceConstants.KO_DEFAULT + "', " + GraphicalInterfaceConstants.FLUX_VALUE_DEFAULT + "" 
							+ ", '"  + GraphicalInterfaceConstants.REVERSIBLE_DEFAULT + "', " + GraphicalInterfaceConstants.LOWER_BOUND_DEFAULT + "" 
							+ ", " + GraphicalInterfaceConstants.UPPER_BOUND_DEFAULT + ", " + GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_DEFAULT + ");";
					//System
					stat.executeUpdate(reacInsert);				
				}
				stat.executeUpdate("COMMIT");
			} catch (Exception e) {
				stat.executeUpdate("ROLLBACK"); // throw away all updates since BEGIN TRANSACTION
			}
			conn.close();


		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	} 
	
	public int maxMetaboliteId(String databaseName) {
		int maxMetaboliteId = 0;
		closeConnection();
		String queryString = "jdbc:sqlite:" + databaseName + ".db"; 
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Connection conn = DriverManager.getConnection(queryString);
			LocalConfig.getInstance().setCurrentConnection(conn);
			PreparedStatement prep = conn
			.prepareStatement("SELECT MAX(id) FROM metabolites;");
			conn.setAutoCommit(true);
			ResultSet rs1 = prep.executeQuery();
			maxMetaboliteId = rs1.getInt("MAX(id)"); 
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}    
		return maxMetaboliteId;
		
	}
	
	public int maxReactionId(String databaseName) {
		int maxReactionId = 0;
		String queryString = "jdbc:sqlite:" + databaseName + ".db";
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		closeConnection();
		try {
			Connection conn = DriverManager.getConnection(queryString);
			LocalConfig.getInstance().setCurrentConnection(conn);
			PreparedStatement prep = conn
			.prepareStatement("SELECT MAX(id) FROM reactions;");
			conn.setAutoCommit(true);
			ResultSet rs1 = prep.executeQuery();
			maxReactionId = rs1.getInt("MAX(id)"); 
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}    
		return maxReactionId;
		
	}
	
	public void closeConnection() {
		if (LocalConfig.getInstance().getCurrentConnection() != null) {
        	try {
				LocalConfig.getInstance().getCurrentConnection().close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
	}

}

