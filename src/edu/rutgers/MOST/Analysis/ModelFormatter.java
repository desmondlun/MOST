package edu.rutgers.MOST.Analysis;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import au.com.bytecode.opencsv.CSVReader;
import edu.rutgers.MOST.data.Model;
import edu.rutgers.MOST.data.ModelParser;
import edu.rutgers.MOST.data.SBMLReaction;

public class ModelFormatter
{
	public Vector< Double > formatFluxBoundsfromGeneExpressionData( File file, Model model )
	{		
		Vector< Double > gene_expr = new Vector< Double >();
		if( file == null || !file.exists() )
			return gene_expr;
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
				reaction.setLowerBound( reaction.getLowerBound() >= 0.0 ? 0.0 : -fluxBound );
				reaction.setUpperBound( reaction.getUpperBound() <= 0.0 ? 0.0 : fluxBound  );
				gene_expr.add( fluxBound );
			}
			model.setReactions( model.getReactions() );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
		return gene_expr;
	}
	Vector< Double > parseGeneExpressionDataSPOT( File file, Model model )
	{
		Vector< Double > gene_expr = new Vector< Double >();
		if( file == null || !file.exists() )
			return gene_expr;
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
				Double fluxBound = new Double( parser.getValue() );
				
				reaction.setLowerBound( reaction.getLowerBound() < 0.0 ? -fluxBound : 0.0 );
				reaction.setUpperBound( reaction.getUpperBound() > 0.0 ?  fluxBound : 0.0 );
/*				if( reaction.getReactionName().toLowerCase().contains( "biomass" ) )
				{
					reaction.setLowerBound( 0.2 );
					reaction.setUpperBound( 0.2 );
				}
*/
				
				if( fluxBound.isInfinite() || reaction.getReversible().toLowerCase().equals( "true" ) )
					fluxBound = new Double( 0.0 );
				gene_expr.add( fluxBound );
			}
			
			model.setReactions( model.getReactions() );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
		return gene_expr;
	}
}
