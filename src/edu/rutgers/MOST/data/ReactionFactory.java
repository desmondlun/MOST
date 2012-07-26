package edu.rutgers.MOST.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.presentation.ProgressConstants;

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

	public void setMetabolitesUsedStatus(String databaseName) {
		MetaboliteFactory mFactory = new MetaboliteFactory();
		int numMetabolites = 0;
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
			.prepareStatement("SELECT MAX(id) FROM metabolites;");
			conn.setAutoCommit(true);
			ResultSet rs1 = prep.executeQuery();
			numMetabolites = rs1.getInt("MAX(id)"); 
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}    
		for (int i = 1; i < numMetabolites + 1; i++) {
			if (i%10 == 0) {
				LocalConfig.getInstance().setProgress((i*ProgressConstants.UPDATE_USED_PERCENT)/numMetabolites + ProgressConstants.METABOLITE_LOAD_PERCENT + ProgressConstants.REACTION_LOAD_PERCENT);
			}
			if ((reactantUsedCount(i, databaseName) + productUsedCount(i, databaseName)) > 0) {
				mFactory.setMetaboliteUsedValue(i, databaseName, "true");  
			}		    
		}
		LocalConfig.getInstance().setProgress(100);
	}

	public int reactantUsedCount(Integer id, String databaseName) {
		int count = 0;
		String queryString = "jdbc:sqlite:" + databaseName + ".db"; //TODO:DEGEN:Call LocalConfig
		//not necessary to call LocalConfig since this method is only called in the Graphical Interface 
		//where the database name is supplied
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
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}
		return count;
	}

	public int productUsedCount(Integer id, String databaseName) {
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
			PreparedStatement prep = conn.prepareStatement("select count(metabolite_id) from reaction_products where metabolite_id=?;");
			prep.setInt(1, id);
			ResultSet rs = prep.executeQuery();
			count = rs.getInt("count(metabolite_id)");						
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}
		return count;
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

	}

}
