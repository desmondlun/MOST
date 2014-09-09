package edu.rutgers.MOST.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This is a model reducer class used for
 * compressing FBA and GDBB models
 * @author Tony
 */
public class ModelCompressor
{
	int sMatColumnCount;
	private ArrayList< Map< Integer, Double > > sMatrix = null;
	private ArrayList< Map< Integer, Double > > gMatrix = null;
	private ArrayList< Map< Integer, Double > > recMat = null;
	private ArrayList< Double > lowerBounds = null;
	private ArrayList< Double > upperBounds = null;
	
	private int rowCount()
	{
		return sMatrix.size();
	}
	
	private int columnCount()
	{
		return sMatColumnCount;
	}
	
	private double getsMat( int i, int j )
	{
		Double res = sMatrix.get( i ).get( j );
		return res == null ? 0.0 : res;
	}
	
	private void setsMat( int i, int j, double val )
	{
		sMatrix.get( i ).put( j, val );
	}
	
	private double getgMat( int i, int j )
	{
		Double res = gMatrix.get( i ).get( j );
		return res == null ? new Double( 0.0 ) : res;
	}
	
	private void setgMat( int i, int j, double val )
	{
		gMatrix.get( i ).put( j, val );
	}
	
	private void createRecMat()
	{
		recMat = new ArrayList< Map< Integer, Double > >();
		int j = 0;
		for( int i = 0; i < columnCount(); ++i )
		{
			HashMap< Integer, Double > map = new HashMap< Integer, Double >();
			map.put( j++, 1.0 );
			recMat.add( map );
		}
	}

	public double getrMat( int i, int j )
	{
		Double res = recMat.get( i ).get( j );
		return res == null ? 0.0 : res;
	}

	public ModelCompressor()
	{
	}

	public ArrayList< Map< Integer, Double > > getsMatrix()
	{
		return sMatrix;
	}

	public void setsMatrix( ArrayList< Map< Integer, Double > > sMatrix )
	{
		this.sMatrix = sMatrix;
	}

	public ArrayList< Map< Integer, Double > > getgMatrix()
	{
		return gMatrix;
	}

	public void setgMatrix( ArrayList< Map< Integer, Double > > gMatrix )
	{
		this.gMatrix = gMatrix;
	}
	
	public void compressNet()
	{
		if( sMatrix == null || /*gMatrix == null ||*/ lowerBounds == null || upperBounds == null )
			return;
		
		// create the recmap
		createRecMat();
		
		// keep only the columns that have a nonzero value
		// (Y-dimension) across the matrix
		for( int j = 0; j < columnCount(); ++j )
		{
			boolean isZeroColumn = true;
			for( int i = 0; i < rowCount(); ++i )
				if( getsMat( i, j ) != 0.0 )
					isZeroColumn = false;
			if( isZeroColumn )
				this.removeColumn( j );
		}
		
		// remove the rows (reactions) that have only 1 nonzero column (flux)
		// due to steady-state constraint, it will optimize to be 0 anyway
		for( int i = 0; i < rowCount(); ++i )
		{
			ArrayList< Integer > cols = new ArrayList< Integer >();
			for( int j = 0; j < columnCount(); ++j )
				if( getsMat( i, j ) != 0.0 )
					cols.add( j );
			if( cols.size() == 1 )
			{
				removeColumn( cols.get( 0 ) );
				removeRow( i );
			}
		}
		
		/*
		for( boolean repeat = true; repeat; )
		{
			// find the rows that have only 2 nonzero columns (fluxes)
			// and merge them
			ArrayList< Integer > mergecols = new ArrayList< Integer >();
			ArrayList< Double > mergecoefs = new ArrayList< Double >();

			for( int i = 0; i < rowCount(); ++i )
			{
				for( int j = 0; j < columnCount(); ++j )
				{
					if( getsMat( i, j ) != 0 )
					{
						mergecols.add( j );
						mergecoefs.add( getsMat( i, j ) );
					}
				}
				if( mergecols.size() == 2 )
					break;
				else
				{
					mergecols.clear();
					mergecoefs.clear();
				}
			}
			
			if( mergecols.size() != 2 )
			{
				repeat = false;
				continue;
			}
			
			for( int i = 0; i < rowCount(); ++i )
			{
				double val0 = getsMat( i, mergecols.get( 0 ) ); // current row
				double val1 = getsMat( i, mergecols.get( 1 ) ); // current row
				setsMat( i, mergecols.get( 0 ), val0 - ( val1 / mergecoefs.get( 1 ) * mergecoefs.get( 0 ) ) );
			}
		}
		*/
		
	}

	public ArrayList< Double > decompress( ArrayList< Double > v )
	{
		ArrayList< Double > result = new ArrayList< Double >();
		for( int i = 0; i < recMat.size(); ++i )
		{
			double dot = 0.0;
			for( int j = 0; j < v.size(); ++j )
			{
				dot += recMat.get( i ).get( j ) * v.get( j );
			}
			result.add( dot );
		}
		
		return result;
	}
	
	private void removeColumn( int j )
	{
		for( Map< Integer, Double > con : sMatrix )
			for( Entry< Integer, Double > term : con.entrySet() )
				if( term.getKey().equals( j ) )
					term.setValue( 0.0 );
	/*
	 	for( Map< Integer, Double > row : sMatrix )
	 		row.remove( j );
	 	for( Map< Integer, Double > row : recMat )
	 		row.remove( j );
	*/
	}
	
	private void removeRow( int i )
	{
		sMatrix.get( i ).clear();
		
		//sMatrix.remove( i );
	}

	public void setLowerBounds( ArrayList< Double > lowerBounds )
	{
		this.lowerBounds = lowerBounds;
		this.sMatColumnCount = lowerBounds.size();
	}

	public void setUpperBounds( ArrayList< Double > upperBounds )
	{
		this.upperBounds = upperBounds;
	}
}
