package edu.rutgers.MOST.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.presentation.GraphicalInterface;

public class ReactionFactory {
	
	public ModelReaction getReactionById(Integer reactionId, String sourceType, String databaseName){
		
		
		if("SBML".equals(sourceType)){
			SBMLReaction reaction = new SBMLReaction();
			reaction.setDatabaseName(databaseName);
			reaction.loadById(reactionId);
			return reaction;
		}
		return new SBMLReaction(); //Default behavior.
	}
	
	public void listUsedMetabolites(String databaseName) {
    	int count = 0;
    	//String queryString = "jdbc:sqlite:" + databaseName + ".db";
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
		    stat.executeUpdate("drop table if exists used_metabolites;");
		    
		    stat.executeUpdate("CREATE TABLE used_metabolites (metabolite_id integer);");
		    
		    ResultSet rs = stat.executeQuery("select distinct id from metabolites join reaction_reactants where metabolites.id = reaction_reactants.metabolite_id;");
		    
		    while(rs.next()){
            	PreparedStatement prep3 = conn.prepareStatement(
            		      "insert into used_metabolites (metabolite_id) values (?);"); 
            	prep3.setInt(1, rs.getInt(1));
            	prep3.addBatch();

            	conn.setAutoCommit(false);
            	prep3.executeBatch();
            	conn.setAutoCommit(true);    
            }
		    
            rs.close();
		    
            ResultSet rsProd = stat.executeQuery("select distinct id from metabolites join reaction_products where metabolites.id = reaction_products.metabolite_id;");
            while(rsProd.next()){  
            	PreparedStatement prep = conn.prepareStatement("select count(metabolite_id) from used_metabolites where metabolite_id=?;");
    			prep.setInt(1, rsProd.getInt(1)); 			
    			ResultSet rsProdCount = prep.executeQuery();    			
    			//if metabolite_abbreviation is in table will return 1, else 0
    			count = rsProdCount.getInt("count(metabolite_id)");
    			if (count == 0) {    				
    				PreparedStatement prep4 = conn.prepareStatement(
      		      "insert into used_metabolites (metabolite_id) values (?);"); 
    				prep4.setInt(1, rsProd.getInt(1));
    				prep4.addBatch();

    				conn.setAutoCommit(false);
    				prep4.executeBatch();
    				conn.setAutoCommit(true); 
    			}
    			rsProdCount.close();
            }
            
            rsProd.close();
            
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}	

    }
	
	public int usedMetaboliteCount(Integer id, String databaseName) {
    	int count = 0;
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
			PreparedStatement prep = conn.prepareStatement("select count(metabolite_id) from used_metabolites where metabolite_id=?;");
			prep.setInt(1, id);
			ResultSet rs = prep.executeQuery();
			//if metabolite_abbreviation is in table will return 1, else 0
			count = rs.getInt("count(metabolite_id)");			
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}
		return count;
    }
	
	public void addIdToUsedMetabolites(Integer id, String databaseName) {
		String queryString = "jdbc:sqlite:" + LocalConfig.getInstance().getDatabaseName() + ".db"; //TODO:DEGEN:Call LocalConfig
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Connection conn;
		try {
			conn = DriverManager.getConnection(queryString);
			PreparedStatement prep = conn.prepareStatement("insert into used_metabolites (metabolite_id) VALUES(?);");
			prep.setInt(1, id);
			prep.execute();
		    conn.setAutoCommit(true); 		
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}
	}
	
	public int reactantUsedCount(Integer id, String databaseName) {
    	int count = 0;
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
			PreparedStatement prep = conn.prepareStatement("select count(metabolite_id) from reaction_reactants where metabolite_id=?;");
			prep.setInt(1, id);
			ResultSet rs = prep.executeQuery();
			count = rs.getInt("count(metabolite_id)");			
			System.out.println("rr " + count);			
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}
		return count;
    }
	
	public int productUsedCount(Integer id, String databaseName) {
    	int count = 0;
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
			PreparedStatement prep = conn.prepareStatement("select count(metabolite_id) from reaction_products where metabolite_id=?;");
			prep.setInt(1, id);
			ResultSet rs = prep.executeQuery();
			count = rs.getInt("count(metabolite_id)");			
			System.out.println("rp " + count);			
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}
		return count;
    }
	
	public void removeIdFromUsedMetabolites(Integer id, String databaseName) {
		String queryString = "jdbc:sqlite:" + LocalConfig.getInstance().getDatabaseName() + ".db"; //TODO:DEGEN:Call LocalConfig
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Connection conn;
		try {
			conn = DriverManager.getConnection(queryString);
			PreparedStatement prep = conn.prepareStatement("delete from used_metabolites where metabolite_id=?;");
			prep.setInt(1, id);
			prep.execute();
		    conn.setAutoCommit(true); 		
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}
	}
	
	public Vector<ModelReaction> getAllReactions(String sourceType, String databaseName) {
		Vector<ModelReaction> reactions = new Vector<ModelReaction>();
		
		if("SBML".equals(sourceType)){
			try {
				Class.forName("org.sqlite.JDBC");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return reactions;
			}
			Connection conn;
			try {
				conn = DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db"); // TODO:
																			// Make
																			// this
																			// configurable
				PreparedStatement prep = conn
						.prepareStatement("select id, knockout, reaction_abbreviation, reaction_name, reaction_string, reversible, "
								+ " biological_objective, lower_bound, upper_bound, flux_value "
								+ " from reactions  where length(reaction_abbreviation) > 0;");
				conn.setAutoCommit(true);
				ResultSet rs = prep.executeQuery();
				while (rs.next()) {
					SBMLReaction reaction = new SBMLReaction();
					reaction.setId(rs.getInt("id"));
					reaction.setKnockout(rs.getString("knockout"));
					reaction.setReactionAbbreviation(rs.getString("reaction_abbreviation"));
					reaction.setReactionName(rs.getString("reaction_name"));
					reaction.setReactionString(rs.getString("reaction_string"));
					reaction.setReversible(rs.getString("reversible"));
					reaction.setBiologicalObjective(rs.getDouble("biological_objective"));
					reaction.setLowerBound(rs.getDouble("lower_bound"));
					reaction.setUpperBound(rs.getDouble("upper_bound"));
					reaction.setFluxValue(rs.getDouble("flux_value"));
					
					reactions.add(reaction);
				}
				rs.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return reactions;
			}
		}
		
		return reactions;
	}
	/**
	 * @param args
	 */
	
	public Vector<Integer> getObjectiveFunctions(String sourceType, String databaseName) {
		Vector<Integer> objectiveFunctions = new Vector<Integer>();
		
		if("SBML".equals(sourceType)){
			try {
				Class.forName("org.sqlite.JDBC");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return objectiveFunctions;
			}
			Connection conn;
			try {
				conn = DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db"); 
				
				Statement stat = conn.createStatement();
			    ResultSet rs = stat.executeQuery("select id from reactions where biological_objective > 0;");
				
				while (rs.next()) {
					Integer id = rs.getInt("id");
					
					objectiveFunctions.add(id -1); //indexing starts at zero for reactions, 1 in the DB
				}
				rs.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return objectiveFunctions;
			}
		}
		
		return objectiveFunctions;
	}
	
	//method not used
	public ArrayList<Integer> speciesIdList(Integer reactionId, String sourceType, String databaseName){
		ArrayList<Integer> speciesIdList = new ArrayList();
		
		if("SBML".equals(sourceType)){
			try {
				Class.forName("org.sqlite.JDBC");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Connection conn;
			try {
				conn = DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db"); // TODO:
																			// Make
																			// this
																			// configurable
				PreparedStatement prep = conn
						.prepareStatement("select metabolite_id from reaction_reactants where reaction_id = ?;");				
				prep.setInt(1, reactionId);
				conn.setAutoCommit(true);
				ResultSet rs = prep.executeQuery();
				while (rs.next()) {
					speciesIdList.add(rs.getInt("metabolite_id"));
				}
				rs.close();
				
				PreparedStatement prep2 = conn
				.prepareStatement("select metabolite_id from reaction_products where reaction_id = ?;");				
		        prep2.setInt(1, reactionId);
		        conn.setAutoCommit(true);
		        ResultSet rs2 = prep.executeQuery();
		        while (rs2.next()) {
			        speciesIdList.add(rs2.getInt("metabolite_id"));
		        }
		        rs2.close();
		        conn.close();
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return speciesIdList;
	}
	
	public static void main(String[] args) {
		
		ReactionFactory aFactory = new ReactionFactory();
//		SBMLReaction aReaction = (SBMLReaction)aFactory.getReactionById("1", "SBML","small"); //You can change this reactionId to be any reaction ID string
//		aReaction.setBiologicalObjective(8.999); //testing update of biological Objective
//		aReaction.update();
		//Vector<ModelReaction> reactions =  aFactory.getAllReactions("SBML", "small");
		//System.out.println(reactions.size());
		//System.out.println(((SBMLReaction)reactions.elementAt(0)).getId());
		//ReactantFactory rFactory = new ReactantFactory();
		//ArrayList<ModelReactant> reactants = rFactory.getReactantsByReactionId(1, "SBML", "small");
		//System.out.println(reactants.size());
		
		//test for get objective functions - need to make method static to test.
		//Vector<Integer> objective =  getObjectiveFunctions("SBML", "small");
		//Vector<Integer> objective =  getObjectiveFunctions("SBML", "Ec_core_flux1");
		//for (int i = 0; i < objective.size(); i++) {
			//System.out.println(objective.get(i));
		//}
		
	}

	
}
