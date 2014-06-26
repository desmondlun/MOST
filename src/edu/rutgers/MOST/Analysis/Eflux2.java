package edu.rutgers.MOST.Analysis;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import au.com.bytecode.opencsv.CSVReader;
import edu.rutgers.MOST.data.*;
import edu.rutgers.MOST.optimization.solvers.*;

public class Eflux2 extends Analysis
{
	public Eflux2()
	{
		super( Algorithm.Eflux2 );
	}
	
	public void formatFluxBoundsfromGeneExpressionData( File file )
	{
		if( file == null || !file.exists() )
			return;
		try
		{
			CSVReader csvReader = new CSVReader( new FileReader( file ) );
			List< String[] > all2 = csvReader.readAll();
			csvReader.close();
			Map< String, Double > expressionLevels = new HashMap< String, Double >();
			Map< String, Vector< Double > > levels = new HashMap< String, Vector< Double > >();
			for( String[] keyval : all2)
			{
				if( !levels.containsKey( keyval[0] ) )
					levels.put( keyval[0], new Vector< Double >() );

				levels.get( keyval[0] ).add( Double.valueOf( keyval[1] ) );
			}
			for( String[] keyval : all2)
			{
				Double val = 0.0;
				for( Double d : levels.get( keyval[0] ))
					val += d;
				val = val / levels.get( keyval[0] ).size();
				expressionLevels.put( keyval[0], val );
			}
			
			try
			{
				for( SBMLReaction reaction : model.getReactions() )
				{
					try
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
					}
					catch( Exception e )
					{
						e.printStackTrace();
					}
				}
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
			
			model.setReactions( model.getReactions() );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}

}
