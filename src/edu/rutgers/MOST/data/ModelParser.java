package edu.rutgers.MOST.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class ModelParser
{
	private class Lexicon
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
			    //case '-':   //some gene names include '-' (e.g. 'YML081C-A' in S.cerevisiae)	
					word += string.charAt( idx++ );
					return word;
				case '_':
					word += string.charAt( idx++ );
					while( Character.isAlphabetic( string.charAt( idx ) ) )
						word += string.charAt( idx++ );
					if( string.charAt( idx ) == '_' )
					{
						word += string.charAt( idx++ );
						if( word.equals( "_OR_" ) )
						{
							word = "or";
							break;
						}
						else if( word.equals( "_AND_" ) )
						{
							word = "and";
							break;
						}
					}
				default:
					while( idx < string.length() &&
							( Character.isAlphabetic( string.charAt( idx ) )
							|| Character.isDigit( string.charAt( idx ) )
							|| string.charAt( idx ) == '-' 
							|| string.charAt( idx ) == '.' 
							|| string.charAt( idx ) == '_' )
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
	private Lexicon lexer;
	private Vector< String > byteCode = new Vector< String >();
	private Map< String, Double > data = new HashMap< String, Double >();
	private Vector< Double > values = new Vector< Double >();
	private Vector< Double > stack = new Vector< Double >();
	private int matchCount;
	
	public ModelParser( String expression, Map< String, Double > data ) throws Exception
	{
		lexer = new Lexicon( expression );
		this.data = data;
		matchCount = 0;
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
				stack.set( stack.size() - 2, Math.min( stack.get( stack.size() - 2 ), stack.get( stack.size() - 1 ) ) );
				stack.remove( stack.size() - 1 );
				break;
			case "or":
				stack.set( stack.size() - 2, stack.get( stack.size() - 2 ) +  stack.get( stack.size() - 1 ) );
				stack.remove( stack.size() - 1 );
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
	public int getMatchCount()
	{
		return matchCount;
	}
	
	private void parseExpression() throws Exception
	{
		parseOr();
	}
	private void parseOr() throws Exception
	{
		parseAnd();
		while( lexer.getToken().toLowerCase().equals( "or" ) )
		{
			String token = lexer.getToken().toLowerCase();
			lexer.advance();
			parseAnd();
			byteCode.add( token );
		}
	}
	private void parseAnd() throws Exception
	{
		parsePrefix();
		while( lexer.getToken().toLowerCase().equals( "and" ) )
		{
			String token = lexer.getToken().toLowerCase();
			lexer.advance();
			parsePrefix();
			byteCode.add( token );
		}
	}
	private void parsePrefix() throws Exception
	{

		parsePostfix();
		// no prefix necessary
		
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
			if( data.containsKey( lexer.getToken() ) )
			{
				values.add( data.get( lexer.getToken() ) );
				++matchCount;
			}
			else
				values.add( substitute( lexer.getToken() ) );
			
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
			throw new Exception( "Parser error: missing \")\"" );
		lexer.advance();
	}

	protected Double substitute( String token )
	{
		System.out.println( "Token \"" + token + "\" not in database, replacing val with zero" );
		return 0.0;
	}
}
