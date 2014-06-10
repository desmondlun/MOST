package edu.rutgers.MOST.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class FBAModel {

	protected Vector< SBMLReaction > reactions;
	protected Vector<ModelMetabolite> metabolites;
	protected Vector<Double> objective;
	protected ArrayList<Map<Integer, Double>> sMatrix;
	protected ArrayList<Integer> metaboliteInternalIdList;
	protected ArrayList<Integer> reactionIdList;
	protected Map<Object, Object> reactionsIdPositionMap;
	protected ReactionFactory rFactory = new ReactionFactory("SBML");
	
	public Map<Object, Object> getReactionsIdPositionMap() {
		return reactionsIdPositionMap;
	}

	public void setReactionsIdPositionMap(Map<Object, Object> reactionsIdPositionMap) {
		this.reactionsIdPositionMap = reactionsIdPositionMap;
	}

	protected Map<Object, Object> metaboliteInternalIdMap;
	
	public FBAModel() {
		this.reactions = rFactory.getAllReactions(); 
		this.objective = rFactory.getObjective();
		this.reactionIdList = rFactory.reactionIdList();
		this.reactionsIdPositionMap = rFactory.getReactionsIdPositionMap();
		
		MetaboliteFactory mFactory = new MetaboliteFactory("SBML");
		this.metabolites = mFactory.getAllInternalMetabolites();
		this.metaboliteInternalIdList = mFactory.metaboliteInternalIdList();
		this.metaboliteInternalIdMap = mFactory.getInternalMetabolitesIdPositionMap();

		ReactantFactory reactantFactory = new ReactantFactory("SBML");
		ArrayList<SBMLReactant> reactantList = reactantFactory.getAllReactants();		
		ProductFactory productFactory = new ProductFactory("SBML");
		ArrayList<SBMLProduct> productList = productFactory.getAllProducts();
		
		this.sMatrix = new ArrayList<Map<Integer, Double>>(metaboliteInternalIdList.size());
		for (int i = 0; i < metaboliteInternalIdList.size(); i++) {
			Map<Integer, Double> sRow = new HashMap<Integer, Double>();
			sMatrix.add(sRow);
		}
		
		for (int i = 0; i < reactantList.size(); i++) {
			SBMLReactant reactant = (SBMLReactant) reactantList.get(i);
			if (metaboliteInternalIdList.contains(reactant.getMetaboliteId()) && reactionIdList.contains(reactant.getReactionId())) {
				sMatrix.get((Integer) metaboliteInternalIdMap.get(reactant.getMetaboliteId())).put((Integer) reactionsIdPositionMap.get(reactant.getReactionId()), -reactant.getStoic());
			}
		}
		
		for (int i = 0; i < productList.size(); i++) {
			SBMLProduct product = (SBMLProduct) productList.get(i);
			if (metaboliteInternalIdList.contains(product.getMetaboliteId()) && reactionIdList.contains(product.getReactionId())) {			
				sMatrix.get((Integer) metaboliteInternalIdMap.get(product.getMetaboliteId())).put((Integer) reactionsIdPositionMap.get(product.getReactionId()), product.getStoic());
			}
		}
		
		//System.out.println(sMatrix);

		
//		for (int i = 0; i < metabolites.size(); i++) {
//			Iterator<Integer> iterator = sMatrix.get(i).keySet().iterator();
//			
//			while (iterator.hasNext()) {
//				Integer j = iterator.next();
//				Double s = sMatrix.get(i).get(j);
//				
//				System.out.println((i + 1) + "\t" + (j + 1) + "\t" + s);
//			}
//		}
	}
	
	public Vector< SBMLReaction > getReactions() {
		return this.reactions;
	}
	
	public void setReactions( Vector< SBMLReaction > reactions )
	{
		this.reactions = reactions;
		rFactory.setAllReactions( reactions );
	}
	
	public void formatFluxBoundsfromTransciptomicData( final Map< String, Double > data )
	{
		
		try
		{
			for( SBMLReaction reaction : reactions )
			{
				boolean skip = false;
				if( reaction.getGeneAssociation().isEmpty() )
					continue;
				Vector< String > operations = new Vector< String >();
				Vector< Double > geneValues = new Vector< Double >();
				String str = reaction.getGeneAssociation().replace( "(", "" ).replace( ")", "" );
				Vector< String > expression = new Vector< String >( Arrays.asList( str.split( " " ) ) );
				for( int i = 0; i < expression.size(); ++i )
				{
					if( expression.elementAt( i ).equals( "" ))
					{
						expression.remove( i );
						--i;
					}
				}
				
				for( String val : expression )
				{
					val = val.toLowerCase();
					if( val.equals( "or" ) || val.equals( "and" ) )
						operations.add( val );
					else if( data.containsKey( val ) )
						geneValues.add( data.get( val ) );
					else
					{
						System.out.println( "The gene \"" + val + "\" is not in the CSV database" );
						skip = true;
					}
				}
				if( skip ) continue;
				while( geneValues.size() > 1 )
				{
					if( operations.isEmpty() )
						break;
					if( operations.firstElement().equals( "or" ) )
						geneValues.set( 0, geneValues.get( 0 ) + geneValues.get( 1 ) );
					else
						geneValues.set( 0, Math.min( geneValues.get( 0 ), geneValues.get( 1 ) ) );
					geneValues.remove( 1 );
				}
				double fluxBound = geneValues.get( 0 );
				if( reaction.getReversible().toLowerCase().equals( "true" ) )
					reaction.setLowerBound( -fluxBound );
				else
					reaction.setLowerBound( 0 );
				reaction.setUpperBound( fluxBound );
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		
		setReactions( reactions );
	}
	
	public int getNumMetabolites() {
		return this.metabolites.size();
	}
	
	public int getNumReactions() {
		return this.reactions.size();
	}
	
	public Vector<Double> getObjective() {
	    return this.objective;
	}
	
	public ArrayList<Map<Integer, Double>> getSMatrix() {
		return this.sMatrix;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public String toString() {
		return "FBAModel [reactions=" + reactions + ", metabolites="
				+ metabolites + ", objective=" + objective + "]";
	}

}
