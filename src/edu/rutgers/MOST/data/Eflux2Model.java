package edu.rutgers.MOST.data;

import java.util.Map;

public class Eflux2Model extends Model
{
	public void formatFluxBoundsfromTransciptomicData( final Map< String, Double > data )
	{
		try
		{
			for( SBMLReaction reaction : reactions )
			{
				try
				{
					ModelParser parser = new ModelParser( reaction.getGeneAssociation(), data )
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
					reaction.setUpperBound( reaction.getUpperBound() < 0.0 ? 0.0 : fluxBound  );
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
		
		setReactions( reactions );
	}
}
