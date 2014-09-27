package edu.rutgers.MOST.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import edu.rutgers.MOST.config.LocalConfig;

public class Model
{
	protected Vector< SBMLReaction > reactions;
	protected Vector< ModelMetabolite > metabolites;
	protected Vector< Double > objective;
	protected ArrayList< Map< Integer, Double >> sMatrix;
	protected ArrayList< Integer > metaboliteInternalIdList;
	protected ArrayList< Integer > reactionIdList;
	protected Map< Object, Object > reactionsIdPositionMap;
	protected ReactionFactory rFactory = new ReactionFactory( "SBML" );
	protected boolean bioObjVecWarning = true;
	

	public Map< Object, Object > getReactionsIdPositionMap()
	{
		return reactionsIdPositionMap;
	}

	public void setReactionsIdPositionMap(
			Map< Object, Object > reactionsIdPositionMap )
	{
		this.reactionsIdPositionMap = reactionsIdPositionMap;
	}

	protected Map< Object, Object > metaboliteInternalIdMap;
	
	private void setup()
	{
		if( !this.bioObjVecWarning )
			this.rFactory.disableObjVectorWarning();
		this.reactions = rFactory.getAllReactions();
		this.objective = rFactory.getObjective();
		this.reactionIdList = rFactory.reactionIdList();
		this.reactionsIdPositionMap = rFactory.getReactionsIdPositionMap();

		MetaboliteFactory mFactory = new MetaboliteFactory( "SBML" );
		this.metabolites = mFactory.getAllInternalMetabolites();
		this.metaboliteInternalIdList = mFactory.metaboliteInternalIdList();
		this.metaboliteInternalIdMap = mFactory
				.getInternalMetabolitesIdPositionMap();

		ReactantFactory reactantFactory = new ReactantFactory( "SBML" );
		ArrayList< SBMLReactant > reactantList = reactantFactory
				.getAllReactants();
		ProductFactory productFactory = new ProductFactory( "SBML" );
		ArrayList< SBMLProduct > productList = productFactory.getAllProducts();

		this.sMatrix = new ArrayList< Map< Integer, Double >>(
				metaboliteInternalIdList.size() );
		for( int i = 0; i < metaboliteInternalIdList.size(); i++)
		{
			Map< Integer, Double > sRow = new HashMap< Integer, Double >();
			sMatrix.add( sRow );
		}

		for( int i = 0; i < reactantList.size(); i++)
		{
			SBMLReactant reactant = (SBMLReactant)reactantList.get( i );
			if( metaboliteInternalIdList.contains( reactant.getMetaboliteId() )
					&& reactionIdList.contains( reactant.getReactionId() ) )
			{
				sMatrix.get(
						(Integer)metaboliteInternalIdMap.get( reactant
								.getMetaboliteId() ) ).put(
						(Integer)reactionsIdPositionMap.get( reactant
								.getReactionId() ), -reactant.getStoic() );
			}
		}

		for( int i = 0; i < productList.size(); i++)
		{
			SBMLProduct product = (SBMLProduct)productList.get( i );
			if( metaboliteInternalIdList.contains( product.getMetaboliteId() )
					&& reactionIdList.contains( product.getReactionId() ) )
			{
				sMatrix.get(
						(Integer)metaboliteInternalIdMap.get( product
								.getMetaboliteId() ) ).put(
						(Integer)reactionsIdPositionMap.get( product
								.getReactionId() ), product.getStoic() );
			}
		}
	}

	public Model()
	{
		setup();
	}
	
	public Model( boolean bioObjWarning )
	{
		this.bioObjVecWarning = bioObjWarning;
		setup();
	}

	public void updateFromrFactory()
	{
		this.reactions = rFactory.getAllReactions();
	}
	
	public Vector< SBMLReaction > getReactions()
	{
		return this.reactions;
	}
	
	public Vector< ModelMetabolite > getMetabolites()
	{
		return metabolites;
	}
	
	public void setMetabolites( Vector< ModelMetabolite > metabs )
	{
		this.metabolites = metabs;
	}
	
	public void setReactions( Vector< SBMLReaction > reactions )
	{
		this.reactions = reactions;
		//rFactory.setAllReactions( reactions );
	}
	
	public void updateGUITableModel()
	{
		rFactory.setAllReactions( this.reactions );
	}

	public int getNumMetabolites()
	{
		return this.metabolites.size();
	}

	public int getNumReactions()
	{
		return this.reactions.size();
	}

	public Vector< Double > getObjective()
	{
		return this.objective;
	}
	
	public void setObjective( Vector< Double > objective )
	{
		this.objective = objective;
	}

	public ArrayList< Map< Integer, Double >> getSMatrix()
	{
		return this.sMatrix;
	}
	
	public int getMatrixIdFromReactionAbbreviation(String reactionAbbreviation) {
		int matrixId = -1;
		if (LocalConfig.getInstance().getReactionAbbreviationIdMap().containsKey(reactionAbbreviation)) {
			int id = (int) LocalConfig.getInstance().getReactionAbbreviationIdMap().get(reactionAbbreviation);
			matrixId = (Integer)reactionsIdPositionMap.get(id);
		}
		return matrixId;
	}

	@Override
	public String toString()
	{
		return "Model [reactions=" + reactions + ", metabolites=" + metabolites
				+ ", objective=" + objective + "]";
	}
}
