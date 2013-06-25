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

	private static int numReactionsMetaColumns = 15;
	private static int numMetabolitesMetaColumns = 15;
	
	public void createDatabase(String databaseName) {

		createReactionsTable(databaseName, "reactions");
		createMetabolitesTable(databaseName, "metabolites");
		createReactionReactantsTable(databaseName, "reaction_reactants");
		createReactionProductsTable(databaseName, "reaction_products");
		
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

	public void createReactionsTable(String databaseName, String tableName) {

		String metaString = "";
		for (int i = 0; i < numReactionsMetaColumns; i++) {
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
			stat.executeUpdate("drop table if exists " + tableName + ";");	
			stat.executeUpdate("create table " + tableName + " (id INTEGER PRIMARY KEY, " 
					+ " knockout varchar(6), flux_value double, reaction_abbreviation varchar(255), reaction_name varchar(500), "
					+ " reaction_equn_abbr varchar(500), reaction_equn_names varchar(500), reversible varchar(6), lower_bound double, " 
					+ " upper_bound double, biological_objective double, synthetic_objective double, gene_associations varchar(255)" + metaString + ");");
			
			conn.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
	
	public void createMetabolitesTable(String databaseName, String tableName) {

		String metaString = "";
		for (int i = 0; i < numMetabolitesMetaColumns; i++) {
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
			stat.executeUpdate("drop table if exists " + tableName + ";");
			stat.executeUpdate("create table " + tableName + " (id INTEGER PRIMARY KEY, " 
					+ " metabolite_abbreviation varchar(255), metabolite_name varchar(255), "
					+ " charge varchar(5), compartment varchar(255), boundary varchar(5) " 
					+ metaString + ");");	
			
			conn.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
	
	public void addReactionRows(String databaseName, int numReactionRows) {
		
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
				for (int r = 1; r < numReactionRows + 1; r++) {
					String reacInsert = "insert into reactions (id, knockout, flux_value, reversible, lower_bound, upper_bound, biological_objective, synthetic_objective) values " 
							+ "(" + r + ", '" + GraphicalInterfaceConstants.KO_DEFAULT + "', " + GraphicalInterfaceConstants.FLUX_VALUE_DEFAULT 
							+ ", '"  + GraphicalInterfaceConstants.REVERSIBLE_DEFAULT + "', " + GraphicalInterfaceConstants.LOWER_BOUND_DEFAULT 
							+ ", " + GraphicalInterfaceConstants.UPPER_BOUND_DEFAULT + ", " + GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_DEFAULT 
							+ ", " + GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_DEFAULT + ");";
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
	
	public void addMetaboliteRows(String databaseName, int numMetaboliteRows) {
		
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
					String metabInsert = "insert into metabolites (id, boundary) values (" + m + ", '" + GraphicalInterfaceConstants.BOUNDARY_DEFAULT + "');";
					stat.executeUpdate(metabInsert);				
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

		try {
			Connection conn =
				DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db");

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

		try {
			Connection conn =
				DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db");

			PreparedStatement prep = conn.prepareStatement(
			"insert into reactions (id, knockout, flux_value, reversible, lower_bound, upper_bound, biological_objective, synthetic_objective) values (?, ?, ?, ?, ?, ?, ?, ?);");

			prep.setInt(1, maxReactionId(databaseName) + 1);
			prep.setString(2, GraphicalInterfaceConstants.KO_DEFAULT);
			prep.setDouble(3, GraphicalInterfaceConstants.FLUX_VALUE_DEFAULT);
			prep.setString(4, GraphicalInterfaceConstants.REVERSIBLE_DEFAULT);
			prep.setDouble(5, GraphicalInterfaceConstants.LOWER_BOUND_DEFAULT);
			prep.setDouble(6, GraphicalInterfaceConstants.UPPER_BOUND_DEFAULT);
			prep.setDouble(7, GraphicalInterfaceConstants.BIOLOGICAL_OBJECTIVE_DEFAULT);
			prep.setDouble(7, GraphicalInterfaceConstants.SYNTHETIC_OBJECTIVE_DEFAULT);

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

	public void createReactionReactantsTable(String databaseName, String tableName) {
		
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
			stat.executeUpdate("drop table if exists " + tableName + ";");
			stat.executeUpdate("CREATE TABLE " + tableName + " (reaction_id INTEGER, " 
					+ " metabolite_id INTEGER, stoic FLOAT);");

			conn.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
    public void createReactionProductsTable(String databaseName, String tableName) {
		
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
			stat.executeUpdate("drop table if exists " + tableName + ";");
			stat.executeUpdate("CREATE TABLE " + tableName + " (reaction_id INTEGER, " 
					+ " metabolite_id INTEGER, stoic FLOAT);");

			conn.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
	}
	
    public void copyTable(String databaseName, String source, String destination) {
    	
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
			stat.executeUpdate("INSERT INTO " + destination + " SELECT * FROM " + source + ";");

			conn.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
    }
    
    public void copyReactionTables(String databaseName, String suffix) {
    	createReactionsTable(databaseName, "reactions" + suffix);		
		createReactionReactantsTable(databaseName, "reaction_reactants" + suffix);
		createReactionProductsTable(databaseName, "reaction_products" + suffix);
    	copyTable(databaseName, "reactions", "reactions" + suffix);
		copyTable(databaseName, "reaction_reactants", "reaction_reactants" + suffix);
		copyTable(databaseName, "reaction_products", "reaction_products" + suffix);
    }
    
    public void copyMetabolitesTable(String databaseName, String suffix) {
    	createMetabolitesTable(databaseName, "metabolites" + suffix);
    	copyTable(databaseName, "metabolites", "metabolites" + suffix);
    }
    
    public void copyTables(String databaseName, String suffix) {
    	copyReactionTables(databaseName, suffix);
    	copyMetabolitesTable(databaseName, suffix);
    }
    
	// TODO: delete these methods after testing, probably not used
	/*
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
			
			conn.close();

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

			conn.close();
			
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
			
			conn.close();

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

			conn.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
	*/
	
	// method used to create blank tables for initial load and clear
	public void addRows(String databaseName, int numMetaboliteRows, int numReactionRows) {		
		addReactionRows(databaseName, numReactionRows);
		addMetaboliteRows(databaseName, numMetaboliteRows);
	}
	
	// method used when loading a metabolite csv file only to create a blank reactions table
	public void createBlankReactionsTable(String databaseName, int numReactionRows) {
		createReactionsTable(databaseName, "reactions");
		addReactionRows(databaseName, numReactionRows);				
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

	public void deleteTable(String databaseName, String tableName) {
		
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

			stat.executeUpdate("drop table if exists " + tableName + ";");		    

			conn.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
	}
	
	public void deleteReactionsTables(String databaseName, String suffix) {
		deleteTable(databaseName, "reactions" + suffix);
		deleteTable(databaseName, "reaction_reactants" + suffix);
		deleteTable(databaseName, "reaction_products" + suffix);
	}
	
}

