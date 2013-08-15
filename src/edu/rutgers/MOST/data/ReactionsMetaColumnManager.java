package edu.rutgers.MOST.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ReactionsMetaColumnManager {

	public ArrayList<String> metaColumnNames;

	public void setMetaColumnName(ArrayList<String> metaColumnNames) {
		this.metaColumnNames = metaColumnNames;
	}

	public ArrayList<String> getMetaColumnNames() {
		return metaColumnNames;
	}

	public void addColumnNames(String databaseName, ArrayList<String> metaColumnNames) {
		setMetaColumnName(metaColumnNames);	
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
			stat.executeUpdate("drop table if exists reactions_meta_info;");		    
			stat.executeUpdate("CREATE TABLE reactions_meta_info (id INTEGER, meta_column_name varchar(100));");
			for (int m = 0; m < metaColumnNames.size(); m++) {
				PreparedStatement prep1 = conn.prepareStatement("insert into reactions_meta_info (id, meta_column_name) values (?, ?);");
				prep1.setInt(1, m + 1);
				prep1.setString(2, metaColumnNames.get(m));

				prep1.addBatch();

				conn.setAutoCommit(false);
				prep1.executeBatch();
				conn.setAutoCommit(true);
			}

			conn.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}
	}

	public Integer getMetaColumnCount(String databaseName) {
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
			Statement stat = conn.createStatement();	
			ResultSet rs = stat.executeQuery("select max(id) from reactions_meta_info;");
			count = rs.getInt("max(id)");	
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}
		return count;
	}

	public String getColumnName(String databaseName, int id) {
		String columnName = "";
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
			PreparedStatement prep1 = conn.prepareStatement("select meta_column_name from reactions_meta_info where id=?;");
			prep1.setInt(1, id);

			ResultSet rs = prep1.executeQuery();
			columnName = rs.getString("meta_column_name");

			conn.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}
		return columnName;

	}
	
	public ArrayList<String> getColumnNames(String databaseName) {
		ArrayList<String> columnNames = new ArrayList<String>();
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
			PreparedStatement prep1 = conn.prepareStatement("select meta_column_name from reactions_meta_info;");

			ResultSet rs = prep1.executeQuery();
			while (rs.next()) {
				columnNames.add(rs.getString("meta_column_name"));
			}

			conn.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}
		return columnNames;

	}
	
	public void addColumnName(String databaseName, String columnName) {
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
			PreparedStatement prep1 = conn.prepareStatement("insert into reactions_meta_info (id, meta_column_name) values (?, ?);");
			prep1.setInt(1, (getMetaColumnCount(databaseName) + 1));
			prep1.setString(2, columnName);

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

	public void changeColumnName(String databaseName, String columnName, int id) {
		String queryString = "jdbc:sqlite:" + databaseName + ".db";
		//System.out.println("name " + columnName + "id " + id);
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Connection conn;
		try {
			conn = DriverManager.getConnection(queryString);
			PreparedStatement prep1 = conn.prepareStatement("update reactions_meta_info set meta_column_name=? where id=?;");
			prep1.setString(1, columnName);
			prep1.setInt(2, id);

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

}
