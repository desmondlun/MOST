package edu.rutgers.MOST.Analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.JOptionPane;

import au.com.bytecode.opencsv.CSVReader;
import edu.rutgers.MOST.data.Model;
import edu.rutgers.MOST.data.ModelParser;
import edu.rutgers.MOST.data.SBMLReaction;
import edu.rutgers.MOST.presentation.SimpleProgressBar;

public class ModelFormatter
{
	private boolean promptExceptionMessage( Exception e )
	{
		return JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog( null, e.getMessage()
				+ "\nDo you still want to continue?",
				"Parser Error",
				JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE );
	}
	public Vector< Double > formatFluxBoundsfromGeneExpressionData( File file, Model model, SimpleProgressBar pb ) throws Exception
	{		
		Vector< Double > gene_expr = new Vector< Double >();
		if( file == null || !file.exists() )
		{
			throw new FileNotFoundException( "Improper file format" );
		}
		try
		{
			
			CSVReader csvReader = new CSVReader( new FileReader( file ) );
			List< String[] > all = csvReader.readAll();
			csvReader.close();
			Map< String, Double > expressionLevels = new HashMap< String, Double >();
			Map< String, Vector< Double > > levels = new HashMap< String, Vector< Double > >();
			
			for( String[] keyval : all )
			{
				if( !levels.containsKey( keyval[0] ) )
					levels.put( keyval[0], new Vector< Double >() );

				levels.get( keyval[0] ).add( Double.valueOf( keyval[1] ) );
			}
			for( String[] keyval : all )
			{
				Double val = 0.0;
				for( Double d : levels.get( keyval[0] ))
					val += d;
				val = val / levels.get( keyval[0] ).size();
				expressionLevels.put( keyval[0], val );
			}
			
			int matchCount = 0;
			for( SBMLReaction reaction : model.getReactions() )
			{
				ModelParser parser = new ModelParser( reaction.getGeneAssociation(), expressionLevels )
				{
					@Override
					protected Double substitute( String token )
					{
						System.out.println( "Gene \"" + token + "\" not in database, replacing val with infinity" );
						return Double.POSITIVE_INFINITY;
					}
				};
				double fluxBound = parser.getValue();
				matchCount += parser.getMatchCount();
				reaction.setLowerBound( reaction.getLowerBound() >= 0.0 ? 0.0 : -fluxBound );
				reaction.setUpperBound( reaction.getUpperBound() <= 0.0 ? 0.0 : fluxBound  );
				gene_expr.add( fluxBound );
			}
			model.setReactions( model.getReactions() );
			
			if( matchCount == 0 )
				throw new Exception( "No gene association matches" );
		}
		catch ( Exception e )
		{
			pb.dispose();
			if( !promptExceptionMessage( e ) )
				throw e;
		}
		return gene_expr;
	}
	Vector< Double > parseGeneExpressionDataSPOT( File file, Model model, boolean originalSPOT, SimpleProgressBar pb ) throws Exception
	{
		Vector< Double > gene_expr = new Vector< Double >();
		if( file == null || !file.exists() )
			throw new FileNotFoundException( "Improper file format" );
		try
		{
			
			CSVReader csvReader = new CSVReader( new FileReader( file ) );
			List< String[] > all = csvReader.readAll();
			csvReader.close();
			Map< String, Double > expressionLevels = new HashMap< String, Double >();
			Map< String, Vector< Double > > levels = new HashMap< String, Vector< Double > >();
			
			for( String[] keyval : all )
			{
				if( !levels.containsKey( keyval[0] ) )
					levels.put( keyval[0], new Vector< Double >() );

				levels.get( keyval[0] ).add( Double.valueOf( keyval[1] ) );
			}
			for( String[] keyval : all )
			{
				Double val = 0.0;
				for( Double d : levels.get( keyval[0] ))
					val += d;
				val = val / levels.get( keyval[0] ).size();
				expressionLevels.put( keyval[0], val );
			}
			
			int matchCount = 0;
			for( SBMLReaction reaction : model.getReactions() )
			{
				ModelParser parser = new ModelParser( reaction.getGeneAssociation(), expressionLevels )
				{
					@Override
					protected Double substitute( String token )
					{
						System.out.println( "Gene \"" + token + "\" not in database, replacing val with infinity" );
						return Double.POSITIVE_INFINITY;
					}
				};
				Double parse_expr = new Double( parser.getValue() );
				matchCount += parser.getMatchCount();
				
				if( originalSPOT )
				{
                    //Removed constant bounds code. Therefore this code will not work.
//					if( !LocalConfig.getInstance().getConstantBoundsIdList().contains( reaction.getId() ) )
//					{
//						reaction.setLowerBound( reaction.getLowerBound() < 0.0 ? Double.NEGATIVE_INFINITY : 0.0 );
//						reaction.setUpperBound( reaction.getUpperBound() > 0.0 ? Double.POSITIVE_INFINITY : 0.0 );
//					}
					
					if( parse_expr.isInfinite() || reaction.getReversible().toLowerCase().equals( "true" ) )
						gene_expr.add( 0.0 );
					else
						gene_expr.add( parse_expr );
				}
				else
				{
					if( !parse_expr.isInfinite() && reaction.getLowerBound() >= 0.0 )
						gene_expr.add( parse_expr );
					else if( !parse_expr.isInfinite() && reaction.getUpperBound() <= 0.0 )
						gene_expr.add( -parse_expr );
					else
						gene_expr.add( 0.0 );
				}
			}
			
			model.setReactions( model.getReactions() );
			if( matchCount == 0 )
				throw new Exception( "No gene association matches" );
		}
		catch ( Exception e )
		{
			pb.dispose();
			if( !promptExceptionMessage( e ) );
				throw e;
		}
		return gene_expr;
	}
	public void formatSMatrixSPOT( ArrayList< Map< Integer, Double > > sMatrix, 
		ArrayList< Double > lb, ArrayList< Double > ub, ArrayList< Integer > vNetIdxs, 
		 ArrayList< Map< Integer, Double > > result_sMatrix, ArrayList< Double > result_lb,
		 ArrayList< Double > result_ub )
	{		
		try
		{
			ArrayList< Map< Integer, Double > > result_s = sMatrix;
			vNetIdxs.clear();
			result_sMatrix.clear();
			result_lb.clear();
			result_ub.clear();
			
			int n = 0; // extra fluxes
			for( int i = 0; i < lb.size(); ++i )
			{
				vNetIdxs.add( i + n );
				result_lb.add( Math.abs( lb.get( i ) ) );
				result_ub.add( Math.abs( ub.get( i ) ) ); // will account for later if negative
				
				if( lb.get( i ) < 0.0 )
				{
					// form a new sMatrix with [ ..., V_j-1, v_j_f, V_j_b, V_j+1, ... ]
					ArrayList< Map< Integer, Double > > new_S = new ArrayList< Map< Integer, Double > >();
					
					for( Map< Integer, Double > con : result_s )
					{
						Map< Integer, Double > new_con = new HashMap< Integer, Double >();
						
						for( Entry< Integer, Double > term : con.entrySet() )
						{
							int key = term.getKey();
							new_con.put( key > i + n ? key + n + 1 : key , term.getValue() );
						}
						
						if( con.containsKey( i ) )
						{
							new_con.put( i + n + 0, con.get( i + n + 0 ) * ( ub.get( i ) < 0.0 ? -1.0 : 1.0 )  ); // if v_i_f < 0
							new_con.put( i + n + 1, con.get( i + n + 1 ) * -1.0 ); // because of: if( lb.get( i ) < 0.0 )
						}
						new_S.add( new_con );
					}
					
					result_s = new_S;
					++n;
				}
			}
			
			for( Map< Integer, Double > con : result_s )
				result_sMatrix.add( con );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
}
