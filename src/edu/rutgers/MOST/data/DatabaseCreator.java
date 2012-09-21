package edu.rutgers.MOST.data;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import au.com.bytecode.opencsv.CSVReader;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.presentation.GraphicalInterface;
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

		try {
			Connection conn =
				DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db");
			Statement stat = conn.createStatement();

			stat.executeUpdate("drop table if exists metabolites;");
			stat.executeUpdate("create table metabolites (id INTEGER PRIMARY KEY, " 
					+ " metabolite_abbreviation varchar(40), metabolite_name varchar(200), "
					+ " charge varchar(5), compartment varchar(40), boundary varchar(5) " 
					+ metaString + ", used varchar(5));");				

			stat.executeUpdate("drop table if exists reactions;");
			stat.executeUpdate("create table reactions (id INTEGER PRIMARY KEY, " 
					+ " knockout varchar(6), flux_value double, reaction_abbreviation varchar(40), reaction_name varchar(500), "
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

		try {
			Connection conn =
				DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db");
			Statement stat = conn.createStatement();
			
			try {			
				stat.executeUpdate("BEGIN TRANSACTION");			
				for (int m = 1; m < numMetaboliteRows + 1; m++) {
					String metabInsert = "insert into metabolites (id) values (" + m + ");";
					stat.executeUpdate(metabInsert);				
				}
				for (int r = 1; r < numReactionRows + 1; r++) {
					String reacInsert = "insert into reactions (id) values (" + r + ");";
					stat.executeUpdate(reacInsert);				
				}
				String metabUpdate = "update metabolites set boundary = 'false', used = 'false';";
				stat.executeUpdate(metabUpdate);
				String reacUpdate = "update reactions set reversible = 'false', biological_objective = 0.0,lower_bound = -999999.0, upper_bound = 999999.0, flux_value = 0.0, knockout = 'false';";
				stat.executeUpdate(reacUpdate);
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

		try {
			Connection conn =
				DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db");

			PreparedStatement prep = conn.prepareStatement(
			"insert into metabolites (id) values (?);");

			prep.setInt(1, maxMetaboliteId(databaseName) + 1);

			prep.addBatch();

			conn.setAutoCommit(false);
			prep.executeBatch();
			conn.setAutoCommit(true);

			PreparedStatement prep1 = conn.prepareStatement("update metabolites set boundary = 'false', used = 'false' where id=?;");

			prep1.setInt(1, maxMetaboliteId(databaseName));

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

	public void addReactionRow(String databaseName) {

		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			Connection conn =
				DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db");

			PreparedStatement prep = conn.prepareStatement(
			"insert into reactions (id) values (?);");

			prep.setInt(1, maxReactionId(databaseName) + 1);

			prep.addBatch();

			conn.setAutoCommit(false);
			prep.executeBatch();
			conn.setAutoCommit(true);

			PreparedStatement prep1 = conn.prepareStatement("update reactions set reversible = 'false', biological_objective = 0.0, lower_bound = -999999.0, upper_bound = 999999.0, knockout = 'false', flux_value = 0.0 where id=?;");

			prep1.setInt(1, maxReactionId(databaseName));

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

	public void deleteReactionRow(String databaseName, int id) {

		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			Connection conn =
				DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db");

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

		try {
			Connection conn =
				DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db");

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

		try {
			Connection conn =
				DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db");

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

		try {
			Connection conn =
				DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db");

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

	public void createBlankReactionsTable(String databaseName) {

		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			Connection conn =
				DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db");

			Statement stat = conn.createStatement();
			stat.executeUpdate("drop table if exists reactions;");

			stat.executeUpdate("create table reactions (id INTEGER PRIMARY KEY, " 
					+ " knockout varchar(6), flux_value double, reaction_abbreviation varchar(40), reaction_name varchar(500), "
					+ " reaction_string varchar(500), reversible varchar(6), lower_bound double, " 
					+ " upper_bound double, biological_objective double, meta_1 varchar(500), " 
					+ " meta_2 varchar(500), meta_3 varchar(500), meta_4 varchar(500), meta_5 varchar(500), "
					+ " meta_6 varchar(500), meta_7 varchar(500), meta_8 varchar(500), meta_9 varchar(500), "
					+ " meta_10 varchar(500), meta_11 varchar(500), meta_12 varchar(500), "
					+ " meta_13 varchar(500), meta_14 varchar(500), meta_15 varchar(500));");	

			stat.executeUpdate("drop table if exists reactions_meta_info;");		    
			stat.executeUpdate("CREATE TABLE reactions_meta_info (id INTEGER, meta_column_name varchar(100));");

			//Create blank reactionTable
			for (int r = 1; r < GraphicalInterfaceConstants.BLANK_DB_NUMBER_OF_ROWS + 1; r++) {
				PreparedStatement prep2 = conn.prepareStatement(
						"insert into reactions (id) values (?);");

				prep2.setDouble(1, r);

				prep2.addBatch();

				conn.setAutoCommit(false);
				prep2.executeBatch();
				conn.setAutoCommit(true);
			}

			Statement st2 = conn.createStatement();
			String str2 = ("update reactions set reversible = 'false', biological_objective = 0.0,lower_bound = -999999.0, upper_bound = 999999.0, knockout = 'false', flux_value = 0.0;");

			st2.executeUpdate(str2);
			//end create blank reactionTable


		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	} 
	
	public int maxMetaboliteId(String databaseName) {
		int maxMetaboliteId = 0;
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
		String queryString = "jdbc:sqlite:" + databaseName + ".db"; //TODO:DEGEN:Call LocalConfig
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Connection conn;
		try {
			conn = DriverManager.getConnection(queryString);
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

}

