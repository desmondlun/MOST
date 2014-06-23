package edu.rutgers.MOST.Analysis;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import edu.rutgers.MOST.data.ModelParser;
import edu.rutgers.MOST.data.SBMLReaction;
import au.com.bytecode.opencsv.CSVReader;

public class SPOT extends Eflux2
{
	//calculate correlation between in VITRO and in Silico fluxes
	public Double calculateCorrelations( File file )
	{
		if( file == null || !file.exists() )
			return Double.NaN;
		Double result = 0.0;
		try
		{
			//read the CSV file
			CSVReader csvReader = new CSVReader( new FileReader( file ) );
			List< String[] > all = csvReader.readAll();
			csvReader.close();
			
			//put the arrays into a hashmap for the modelparser database
			Map< String, Double > inSilico = new HashMap< String, Double >();
			model.updateFromrFactory();
			for( SBMLReaction reaction : model.getReactions() )
			{
				inSilico.put( reaction.getReactionAbbreviation().replace( "R_", "" ), reaction.getFluxValue() );
			}
			
			//parser the arrays and fill the vectors
			Vector< Double > inSilicoVector = new Vector< Double >();
			Vector< Double > inVitroVector = new Vector< Double >();
			for( String[] keyval : all)
			{
				ModelParser parser = new ModelParser( keyval[ 0 ], inSilico );
				inVitroVector.add( Double.valueOf( keyval[ 1 ].equals( "NaN" )? "0" : keyval[ 1 ] ) / 100.0 );
				inSilicoVector.add( parser.getValue() );
			}
			
			if( inSilicoVector.size() != inVitroVector.size() )
				throw new Exception( "inSilico vector size does not match inViro vector size" );
			
			Double dotProduct = 0.0;
			for( int i = 0; i < inSilicoVector.size(); ++i )
				dotProduct += inSilicoVector.get( i ) * inVitroVector.get( i );
			
			Double lengthInVitro = 0.0;
			for( Double val : inVitroVector )
				lengthInVitro += val * val;
			lengthInVitro = Math.sqrt( lengthInVitro );
			
			Double lengthInSilico = 0.0;
			for( Double val : inSilicoVector )
				lengthInSilico += val * val;
			lengthInSilico = Math.sqrt( lengthInSilico );
			
			result = dotProduct / (lengthInSilico * lengthInVitro );
		}
		catch( Exception e )
		{
			e.printStackTrace();
			result = Double.NaN;
		}
		return result;
	}
}
