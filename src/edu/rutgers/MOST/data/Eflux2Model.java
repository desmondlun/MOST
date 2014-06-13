package edu.rutgers.MOST.data;

import java.util.Map;
import java.util.Vector;

public class Eflux2Model extends FBAModel
{
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
				}
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		
		setReactions( reactions );
	}

}
