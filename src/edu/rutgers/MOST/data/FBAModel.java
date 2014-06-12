package edu.rutgers.MOST.data;

import java.util.ArrayList;
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
		class Interpreter
		{
			class Lexicon
			{
				String string;
				int idx = 0;
				
				String word = "";
				
				public Lexicon( String string )
				{
					this.string = string;
					advance();
				}
				void skip()
				{
					while( idx < string.length()
							&& Character.isWhitespace( string.charAt( idx ) ) )
						++idx;
				}
				String advance()
				{
					skip();
					word = "";
					if( idx < string.length() )
						switch( string.charAt( idx ) )
						{
						case '(':
						case ')':
						case '-':
							word += string.charAt( idx++ );
							return word;
						default:
							while( idx < string.length() &&
									( Character.isAlphabetic( string.charAt( idx ) )
									|| Character.isDigit( string.charAt( idx ) )
									|| string.charAt( idx ) == '.' )
								)
							{
								word += string.charAt( idx++ );
							}
						}
					return word;
				}
				String getToken()
				{
					return word;
				}
			}
			Lexicon lexer;
			Vector< String > byteCode = new Vector< String >();
			Vector< Double > values = new Vector< Double >();
			Vector< Double > stack = new Vector< Double >();
			
			public Interpreter( String expression ) throws Exception
			{
				lexer = new Lexicon( expression );
				parseExpression();
			}
			public Double getValue() throws Exception
			{
				while( !byteCode.isEmpty() )
				{
					switch( byteCode.firstElement() )
					{
					case "push":
						stack.add( values.firstElement() );
						values.remove( 0 );
						break;
					case "and":
						stack.set( 0, Math.min( stack.get( 0 ), stack.get( 1 ) ) );
						stack.remove( 1 );
						break;
					case "or":
						stack.set( 0, stack.get( 0 ) + stack.get( 1 ) );
						stack.remove( 1 );
						break;
					case "negate":
						stack.set( 0, -stack.get( 0 ) );
						break;
						default:
							throw new Exception( "Unsupportd operation \""
									+ byteCode.firstElement() + " \"" );
					}
					byteCode.remove( 0 );
				}
				if( stack.size() != 1 )
					throw new Exception( "Interpretor getValue() error - stack size" );
				return stack.firstElement();
			}
			
			private void parseExpression() throws Exception
			{
				parseOperation();
			}
			private void parseOperation() throws Exception
			{
				parsePrefix();
				while( lexer.getToken().toLowerCase().equals( "or" )
					|| lexer.getToken().toLowerCase().equals( "and" ) )
				{
					String token = lexer.getToken().toLowerCase();
					lexer.advance();
					parsePrefix();
					byteCode.add( token );
				}
			}
			private void parsePrefix() throws Exception
			{
				Vector< String > codeAppend = new Vector< String >();
				while( lexer.getToken().equals( "-" ) )
				{
					codeAppend.add( "negate" );
					lexer.advance();
				}
				parsePostfix();
				for( String code : codeAppend )
					byteCode.add( code );
			}
			private void parsePostfix() throws Exception
			{
				parseClause();
				//no postfix necessary
			}
			private void parseClause() throws Exception
			{
				if( lexer.getToken().equals( "(" ) )
					parseParenthesis();
				else
				{
					if( !data.containsKey( lexer.getToken() ) )
						throw new Exception( "Gene \"" + lexer.getToken() + "\" not in CSV database" );
					values.add( data.get( lexer.getToken() ) );
					byteCode.add( "push" );
					lexer.advance();
				}
			}
			private void parseParenthesis() throws Exception
			{
				if( !lexer.getToken().equals( "(" ) )
					throw new Exception( "\"(\" expected" );
				lexer.advance();
				parseExpression();
				if( !lexer.getToken().equals( ")" ) )
					throw new Exception( "Parser error: missing \"(\"" );
				lexer.advance();
			}
			
		}
		try
		{
			for( SBMLReaction reaction : reactions )
			{
				try
				{
					Interpreter parser = new Interpreter( reaction.getGeneAssociation() );
					double fluxBound = parser.getValue();
					if( reaction.getReversible().toLowerCase().equals( "true" ) )
						reaction.setLowerBound( -fluxBound );
					else
						reaction.setLowerBound( 0 );
					reaction.setUpperBound( fluxBound );
				}
				catch( Exception e )
				{
					System.out.println( e.getMessage() );
				}
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
