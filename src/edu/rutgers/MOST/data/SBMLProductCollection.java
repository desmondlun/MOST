package edu.rutgers.MOST.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class SBMLProductCollection implements ModelProductCollection {
    
	private String databaseName;
	private Integer reactionId;
	private ArrayList<ModelProduct> productList;
	

	public ArrayList<ModelProduct> getProductList() {
		return productList;
	}

	public void setProductList(ArrayList<ModelProduct> productList) {
		this.productList = productList;
	}

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
					.prepareStatement("select reaction_id, metabolite_id, stoic, metabolite_abbreviation from reaction_products, metabolites where reaction_products.metabolite_id = metabolites.id and reaction_id = ?;");
			prep.setInt(1, reactionId);
			conn.setAutoCommit(true);
			ResultSet rs = prep.executeQuery();
			productList = new ArrayList<ModelProduct>();
			
			while (rs.next()) {
				SBMLProduct aProduct = new SBMLProduct();
				aProduct.setReactionId(rs.getInt("reaction_id"));
				aProduct.setMetaboliteId(rs.getInt("metabolite_id"));
				aProduct.setStoic(rs.getDouble("stoic"));
				aProduct.setMetaboliteAbbreviation(rs.getString("metabolite_abbreviation"));
				this.productList.add(aProduct);
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

	public boolean loadAll() {

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
					.prepareStatement("select reaction_id, metabolite_id, stoic, metabolite_abbreviation from reaction_products, metabolites where reaction_products.metabolite_id = metabolites.id;");
			conn.setAutoCommit(true);
			ResultSet rs = prep.executeQuery();
			productList = new ArrayList<ModelProduct>();
			
			while (rs.next()) {
				SBMLProduct aProduct = new SBMLProduct();
				aProduct.setReactionId(rs.getInt("reaction_id"));
				aProduct.setMetaboliteId(rs.getInt("metabolite_id"));
				aProduct.setStoic(rs.getDouble("stoic"));
				aProduct.setMetaboliteAbbreviation(rs.getString("metabolite_abbreviation"));
				this.productList.add(aProduct);
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

	public String createConnectionStatement(String databaseName) {
		return "jdbc:sqlite:" + getDatabaseName() + ".db";
	}
	
	

	

	public static void main(String[] args) {
		ProductFactory aProductFactory = new ProductFactory("SBML", "test_03182012");
		ArrayList<ModelProduct> products = aProductFactory.getProductsByReactionId(1);
		Iterator<ModelProduct> iterator = products.iterator();
		 
		while(iterator.hasNext()){
			SBMLProduct aProduct = (SBMLProduct)iterator.next();
			//System.out.print("\nabbr" + aProduct.toString());
		}
	}

}










 



















