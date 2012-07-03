package edu.rutgers.MOST.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import edu.rutgers.MOST.config.LocalConfig;

public class SBMLReaction implements ModelReaction {

	/**
	 * @param args
	 */
	
	private String databaseName;	
	private Integer id; 
	private String knockout;
	private String reactionAbbreviation;
	private String reactionName;
	private String reactionString;
	private String reversible;
	private double lowerBound;
	private double upperBound;	
	private double biologicalObjective;
	private double fluxValue;
	private String meta1;
	private String meta2;
	private String meta3;
	private String meta4;
	private String meta5;
	private String meta6;
	private String meta7;
	private String meta8;
	private String meta9;
	private String meta10;
	private String meta11;
	private String meta12;
	private String meta13;
	private String meta14;
	private String meta15;	
	
	private ArrayList reactantsList;
	private ArrayList productsList;
	
		
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	
	public String getDatabaseName() {
		return databaseName;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setKnockout(String knockout) {
		this.knockout = knockout;
	}

	public String getKnockout() {
		return knockout;
	}
	
	public String getReactionAbbreviation() {
		return reactionAbbreviation;
	}

	public void setReactionAbbreviation(String reactionAbbreviation) {
		this.reactionAbbreviation = reactionAbbreviation;
	}

	public String getReactionName() {
		return reactionName;
	}

	public void setReactionName(String reactionName) {
		this.reactionName = reactionName;
	}
	
	public void setReactionString(String reactionString) {
		this.reactionString = reactionString;
	}

	public String getReactionString() {
		return reactionString;
	}
	
	public String getReversible() {
		return reversible;
	}

	public void setReversible(String reversible) {
		this.reversible = reversible;
	}
		
	public double getLowerBound() {
		return lowerBound;
	}

	public void setLowerBound(double lowerBound) {
		this.lowerBound = lowerBound;
	}

	public double getUpperBound() {
		return upperBound;
	}

	public void setUpperBound(double upperBound) {
		this.upperBound = upperBound;
	}

	public double getBiologicalObjective() {
		return biologicalObjective;
	}

	public void setBiologicalObjective(double biologicalObjective) {
		this.biologicalObjective = biologicalObjective;
	}
	
	public double getFluxValue() {
		return fluxValue;
	}

	public void setFluxValue(double fluxValue) {
		this.fluxValue = fluxValue;
	}
	
	public void setReactantsList(ArrayList reactantsList) {
		this.reactantsList = reactantsList;
	}

	public ArrayList getReactantsList() {
		return reactantsList;
	}
	
	public void setProductsList(ArrayList productsList) {
		this.productsList = productsList;
	}

	public ArrayList getProductsList() {
		return productsList;
	}
	
	// SQL Persistence/ORM Below:

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
			        .prepareStatement("update reactions set knockout=?, flux_value=?, reaction_abbreviation=?, reaction_name=?, " 
			        		+ " reaction_string=?, reversible=?, lower_bound=?, upper_bound=?, biological_objective=?, " 
			        		+ " meta_1=?, meta_2=?, meta_3=?, meta_4=?, meta_5=?, meta_6=?, meta_7=?, meta_8=?, "
			        		+ " meta_9=?, meta_10=?, meta_11=?, meta_12=?, meta_13=?, meta_14=?, meta_15=? where id=?;");
			prep.setString(1, this.getKnockout());
			prep.setDouble(2, this.getFluxValue());	
			prep.setString(3, this.getReactionAbbreviation());
			prep.setString(4, this.getReactionName());
			prep.setString(5, this.getReactionString());
			prep.setString(6, this.getReversible());			
			prep.setDouble(7, this.getLowerBound());
			prep.setDouble(8, this.getUpperBound());
			prep.setDouble(9, this.getBiologicalObjective());			
			prep.setString(10, this.getMeta1());
			prep.setString(11, this.getMeta2());
			prep.setString(12, this.getMeta3());
			prep.setString(13, this.getMeta4());
			prep.setString(14, this.getMeta5());
			prep.setString(15, this.getMeta6());
			prep.setString(16, this.getMeta7());
			prep.setString(17, this.getMeta8());
			prep.setString(18, this.getMeta9());
			prep.setString(19, this.getMeta10());
			prep.setString(20, this.getMeta11());
			prep.setString(21, this.getMeta12());
			prep.setString(22, this.getMeta13());
			prep.setString(23, this.getMeta14());
			prep.setString(24, this.getMeta15());	
			prep.setInt(25, this.getId());
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

	public boolean loadById(Integer id) {

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
					.prepareStatement("select id, knockout, flux_value, reaction_abbreviation, reaction_name, reaction_string, "
							+ " reversible, lower_bound, upper_bound, biological_objective, "
							+ " meta_1, meta_2, meta_3, meta_4, meta_5, meta_6, meta_7, meta_8, "
							+ " meta_9, meta_10, meta_11, meta_12, meta_13, meta_14, meta_15 "
							+ " from reactions where id = ?;");
			prep.setInt(1, id);
			conn.setAutoCommit(true);
			ResultSet rs = prep.executeQuery();
			while (rs.next()) {
				this.setId(rs.getInt("id"));
				this.setKnockout(rs.getString("knockout"));
				this.setFluxValue(rs.getDouble("flux_value"));
				this.setReactionAbbreviation(rs.getString("reaction_abbreviation"));
				this.setReactionName(rs.getString("reaction_name"));
				this.setReactionString(rs.getString("reaction_string"));
				this.setReversible(rs.getString("reversible"));				
				this.setLowerBound(rs.getDouble("lower_bound"));
				this.setUpperBound(rs.getDouble("upper_bound"));
				this.setBiologicalObjective(rs.getDouble("biological_objective"));			
				this.setMeta1(rs.getString("meta_1"));
				this.setMeta2(rs.getString("meta_2"));
				this.setMeta3(rs.getString("meta_3"));
				this.setMeta4(rs.getString("meta_4"));
				this.setMeta5(rs.getString("meta_5"));
				this.setMeta6(rs.getString("meta_6"));
				this.setMeta7(rs.getString("meta_7"));
				this.setMeta8(rs.getString("meta_8"));
				this.setMeta9(rs.getString("meta_9"));
				this.setMeta10(rs.getString("meta_10"));
				this.setMeta11(rs.getString("meta_11"));
				this.setMeta12(rs.getString("meta_12"));
				this.setMeta13(rs.getString("meta_13"));
				this.setMeta14(rs.getString("meta_14"));
				this.setMeta15(rs.getString("meta_15"));
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
		return "SBMLReaction [id=" + id + ", reactionAbbreviation=" + reactionAbbreviation
				+ ", biologicalObjective=" + biologicalObjective
				+ ", upperBound=" + upperBound + ", lowerBound=" + lowerBound
				+ ", reactionName=" + reactionName + ", reversible="
				+ reversible + ", knockout=" + knockout + "]";
	}
	
	public boolean clearReactants() {
		
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		Connection conn;
		try {
			conn = DriverManager.getConnection(createConnectionStatement(LocalConfig.getInstance().getDatabaseName())); // TODO:
	    	
			PreparedStatement prep = conn
			        .prepareStatement("delete from reaction_reactants where reaction_id=?;");
			prep.setInt(1, this.getId());
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
	
    public boolean clearProducts() {
		
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		Connection conn;
		try {
			conn = DriverManager.getConnection(createConnectionStatement(LocalConfig.getInstance().getDatabaseName())); // TODO:
	    	
			PreparedStatement prep = conn
	        		.prepareStatement("delete from reaction_products where reaction_id=?;");
	        prep.setInt(1, this.getId());
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
	
	public void updateReactants() {
		for (int r = 0; r < getReactantsList().size(); r++) {
			
        	SBMLReactant aReactant = (SBMLReactant) getReactantsList().get(r);
        	aReactant.update();
        }
	}
	
	public void updateProducts() {
        for (int p = 0; p < getProductsList().size(); p++) {
			
        	SBMLProduct aProduct = (SBMLProduct) getProductsList().get(p);
        	aProduct.update();
        }		
	}
	
	public String createConnectionStatement(String databaseName) {
		return "jdbc:sqlite:" + databaseName + ".db";
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public void setMeta1(String meta1) {
		this.meta1 = meta1;
	}

	public String getMeta1() {
		return meta1;
	}

	public void setMeta2(String meta2) {
		this.meta2 = meta2;
	}

	public String getMeta2() {
		return meta2;
	}

	public void setMeta3(String meta3) {
		this.meta3 = meta3;
	}

	public String getMeta3() {
		return meta3;
	}

	public void setMeta4(String meta4) {
		this.meta4 = meta4;
	}

	public String getMeta4() {
		return meta4;
	}

	public void setMeta5(String meta5) {
		this.meta5 = meta5;
	}

	public String getMeta5() {
		return meta5;
	}

	public void setMeta6(String meta6) {
		this.meta6 = meta6;
	}

	public String getMeta6() {
		return meta6;
	}

	public void setMeta7(String meta7) {
		this.meta7 = meta7;
	}

	public String getMeta7() {
		return meta7;
	}

	public void setMeta8(String meta8) {
		this.meta8 = meta8;
	}

	public String getMeta8() {
		return meta8;
	}

	public void setMeta9(String meta9) {
		this.meta9 = meta9;
	}

	public String getMeta9() {
		return meta9;
	}

	public void setMeta10(String meta10) {
		this.meta10 = meta10;
	}

	public String getMeta10() {
		return meta10;
	}
	
	public void setMeta11(String meta11) {
		this.meta11 = meta11;
	}

	public String getMeta11() {
		return meta11;
	}

	public void setMeta12(String meta12) {
		this.meta12 = meta12;
	}

	public String getMeta12() {
		return meta12;
	}

	public void setMeta13(String meta13) {
		this.meta13 = meta13;
	}

	public String getMeta13() {
		return meta13;
	}

	public void setMeta14(String meta14) {
		this.meta14 = meta14;
	}

	public String getMeta14() {
		return meta14;
	}

	public void setMeta15(String meta15) {
		this.meta15 = meta15;
	}

	public String getMeta15() {
		return meta15;
	}

}

