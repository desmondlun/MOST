package edu.rutgers.MOST.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

/**
 * This is a model reducer class used for
 * compressing FBA and GDBB models
 * @author Tony
 */
public class ModelCompressor
{
	private Vector< SBMLReaction > reactions = null;
	Vector< ModelMetabolite > metabolites = null;
	Vector< ModelMetabolite > metabolitesCopy = null;
	private Vector< SBMLReaction > reactionsCopy = null;
	private Vector<String> geneAssociations = null;
	private ArrayList< Map< Integer, Double > > sMatrix = null;
	private ArrayList< Map< Integer, Double > > gMatrix = null;
	private ArrayList< Map< Integer, Double > > recMat = null;
	private Map< Integer, Double > objVec = null;
	private Map< Integer, Double > synthObjVec = null;
	private ArrayList< Double > lowerBounds = null;
	private ArrayList< Double > upperBounds = null;
	private int or_column_count = 0;
	private int or_row_count = 0;
	
	@SuppressWarnings( "resource" )
	private void compareCSV( String file1, String file2, String delim )
	{
		BufferedReader brMost = null;
		BufferedReader brMatlab = null;
		try
		{
			brMost = new BufferedReader( new FileReader( file1 ) );
			brMatlab = new BufferedReader( new FileReader( file2 ) );
			
			String lineMost = "";
			String lineMatlab = "";
			
			int row = 0;
			boolean same = true;
			while( (lineMatlab = brMatlab.readLine()) != null )
			{
				++row;
				lineMost = brMost.readLine();
				if( lineMost.equals( "" ) || lineMatlab.equals( "" ) )
					break;
				
				String[] valsMost = lineMost.split( delim );
				String[] valsMatlab = lineMatlab.split( delim );
				
				if( valsMost.length != valsMatlab.length )
					throw new Exception( "Warning! matrix columns are not the same!" );
				
				for( int i = 0; i < valsMatlab.length; ++i )
				{
					Double valMatlab = Double.valueOf( valsMatlab[i] );
					Double valMost = Double.valueOf( valsMost[i] );
					
					if( !valMatlab.equals( valMost ) )
					{
						System.out.println( "difference in row " + row + " column " + (i+1) );
						System.out.println( "correct: " + valMatlab + "\tcurrent: " + valMost );
						same = false;
					}
					
				}
				
			}
			if( same )
				System.out.println( file1 + " and " + file2 + "have the same values. Parsed " + row + " rows" );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				brMost.close();
				brMatlab.close();
			}
			catch ( Exception e )
			{
				e.printStackTrace();
			}
		}
	}
	
	private void dump( String filename, ArrayList< Map< Integer, Double > > mat )
	{
		PrintWriter writer = null;
		try
		{
			writer = new PrintWriter( filename, "US-ASCII" );
			
			for( Map< Integer, Double > m : mat )
			{
				String delim = "";
				for( int i = 0; i < lowerBounds.size(); ++i )
				{
					Double value = m.get( i );
					if( value == null )
						value = 0.0;
					writer.write( delim + value.toString() );
					delim = "\t";
				}
				writer.write( "\r\n" );
			}
		}
		catch( Exception e )
		{
		}
		finally
		{
			if( writer !=null )
				writer.close();
		}

	}
	
	public Vector< SBMLReaction > getReactions()
	{
		return reactions;
	}

	public void setReactions( Vector< SBMLReaction > reactions )
	{
		this.reactions = reactions;
		this.reactionsCopy = new Vector< SBMLReaction >( reactions );
	}
	
	public Vector< SBMLReaction > getReactionsCopy()
	{
		return reactionsCopy;
	}

	private int rowCount()
	{
		return sMatrix.size();
	}
	
	private int columnCount()
	{
		return lowerBounds.size();
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
	
	public void setrMat( int i, int j, double val )
	{
		recMat.get( i ).put( j, val );
	}

	public Map< Integer, Double > getObjVec()
	{
		return objVec;
	}

	public void setObjVec( Map< Integer, Double > objVec )
	{
		this.objVec = objVec;
	}

	public int getOrRowCount()
	{
		return or_row_count;
	}
	
	public int getOrColumnCount()
	{
		return or_column_count;
	}
	
	public ArrayList< Double > getLowerBounds()
	{
		return this.lowerBounds;
	}
	
	public ArrayList< Double > getUpperBounds()
	{
		return this.upperBounds;
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
		if( sMatrix != null )
			or_row_count = sMatrix.size();
	}

	public ArrayList< Map< Integer, Double > > getgMatrix()
	{
		return gMatrix;
	}

	public void setgMatrix( ArrayList< Map< Integer, Double > > gMatrix )
	{
		this.gMatrix = gMatrix;
	}
	
	private void shift( int col0, int col1, double coef0, double coef1, Map< Integer, Double > vec )
	{
		Double v0 = vec.get( col0 );
		v0 = v0 == null ? 0.0 : v0;
		Double v1 = vec.get( col1 );
		v1 = v1 == null ? 0.0 : v1;
		vec.put( col0, v0 - v1 / coef1 * coef0 );
		if( vec.get( col0 ).equals( 0.0 ) )
			vec.remove( col0 );
	}
	
	public void compressNet()
	{
		if( sMatrix == null || /*gMatrix == null ||*/
				objVec == null || lowerBounds == null || upperBounds == null )
			return;

		// create the recmap
		createRecMat();
		
		// start the compression
		int orColCount;
		int orRowCount;
		do
		{
			
			
	//		dump( "MostSMatrix-red-part.txt", sMatrix );
	//		dump( "MostGMatrix-red-part.txt", gMatrix );
	//		compareCSV( "MostSMatrix-red-part.txt", "MatlabSMatrix-red-part.txt", "\t" );
	//		compareCSV( "MostGMatrix-red-part.txt", "MatlabGMatrix-red-part.txt", "\t" );
			
			orColCount = columnCount();
			orRowCount = rowCount();
			
			// remove the 0-value rows
			for( int i = rowCount() - 1; i >= 0; --i )
			{
				int nonzerocount = 0;
				for( int j = 0; j < columnCount(); ++j )
					if( getsMat( i, j ) != 0.0 )
						++nonzerocount;
				if( nonzerocount == 0 )
					removeRow( i );
			}
			
			// keep only the columns that have a nonzero value
			// (Y-dimension) across the matrix
			// debug code
			ArrayList< Integer > badrows = new ArrayList< Integer >();
			for( int i = rowCount() - 1; i >= 0; --i )
			{
				int nonzerocount = 0;
				for( int j = 0; j < columnCount(); ++j )
					if( getsMat( i, j ) != 0.0 )
						++nonzerocount;
				if( nonzerocount == 1 )
					badrows.add( i );
			}
			
			// more debug code
			ArrayList< Integer > badrows_mat = new ArrayList< Integer >();
			for( int i = badrows.size() - 1; i >= 0; --i )
				badrows_mat.add( badrows.get( i ) + 1 );
			
			// remove the rows (reactions) that have only 1 nonzero column (flux)
			// due to steady-state constraint, it will optimize to be 0 anyway
			ArrayList< Integer > badcols = new ArrayList< Integer >();
			for( int i : badrows )
			{
				ArrayList< Integer > cols = new ArrayList< Integer >();
				for( int j = 0; j < columnCount(); ++j )
					if( getsMat( i, j ) != 0.0 )
						cols.add( j );
				if( cols.size() == 1 )
					if( !badcols.contains( cols.get( 0 ) ) )
						badcols.add( cols.get( 0 ) );
			}
			
			ArrayList< Integer > badcols_mat = new ArrayList< Integer >();
			for( int i = badcols.size()-1; i >= 0; --i )
				badcols_mat.add( badcols.get( i ) + 1 );
			
			Collections.sort( badcols, Collections.reverseOrder() );
			Collections.sort( badcols_mat, Collections.reverseOrder() );
			
			for( int i : badcols )
				removeColumn( i );
			for( int i : badrows )
				removeRow( i );
			
			//again, remove the 0-value rows
			for( int i = rowCount() - 1; i >= 0; --i )
			{
				int nonzerocount = 0;
				for( int j = 0; j < columnCount(); ++j )
					if( getsMat( i, j ) != 0.0 )
						++nonzerocount;
				if( nonzerocount == 0 )
					removeRow( i );
			}
		
	//		dump( "MostSMatrix-red-part.txt", sMatrix );
	//		dump( "MostGMatrix-red-part.txt", gMatrix );
	//		compareCSV( "MostSMatrix-red-part.txt", "MatlabSMatrix-red-part.txt", "\t" );
	//		compareCSV( "MostGMatrix-red-part.txt", "MatlabGMatrix-red-part.txt", "\t" );
	//		compareCSV( "MOSTpass.txt", "Matlabpass.txt", "\t" );
			
			for( boolean repeat = true; repeat; )
			{
				// find the rows that have only 2 nonzero columns (fluxes)
				// and merge them
				ArrayList< Integer > mergecols = new ArrayList< Integer >();
				ArrayList< Double > mergecoefs = new ArrayList< Double >();
				int candmass = 0;
	
				for( int i = 0; i < rowCount(); ++i )
				{
					candmass = i;
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
				
		//		System.out.println( "merging columns:[" + Integer.toString( mergecols.get( 0 ) +1) + " " + Integer.toString( mergecols.get( 1 ) +1) + "]" );
				
				if( candmass == 234 )
					System.out.println( "Row 234 commensing.. watch column 24" );
				// for sMatrix
				for( int i = 0; i < rowCount(); ++i )
				{
					double val0 = getsMat( i, mergecols.get( 0 ) ); // current row
					double val1 = getsMat( i, mergecols.get( 1 ) ); // current row
					
					// for sMatrix
					setsMat( i, mergecols.get( 0 ), val0 - val1 * mergecoefs.get( 0 ) / mergecoefs.get( 1 ) );
				}
				
				// for recMat
				for( int i = 0; i < recMat.size(); ++i )
				{
					double val0_rec = getrMat( i, mergecols.get( 0 ) ); // current row reccoef
					double val1_rec = getrMat( i, mergecols.get( 1 ) ); // current row reccoef
					
					setrMat( i, mergecols.get( 0 ), val0_rec - val1_rec * mergecoefs.get( 0 ) / mergecoefs.get( 1 ) );
				}
				
				// for gMat
				if( gMatrix != null )
				{
					for( int i = 0; i < gMatrix.size(); ++i )
					{
						boolean b0 = getgMat( i, mergecols.get( 0 ) ) != 0.0;
						boolean b1 = getgMat( i, mergecols.get( 1 ) ) != 0.0;
						if( b0 || b1 ) setgMat( i, mergecols.get( 0 ), 1.0 );
					}
				}
				
				// shift the objective
				shift( mergecols.get( 0 ), mergecols.get( 1 ), mergecoefs.get( 0 ), mergecoefs.get( 1 ), objVec );
				
				// shift the synthetic objective
				if( synthObjVec != null )
					shift( mergecols.get( 0 ), mergecols.get( 1 ), mergecoefs.get( 0 ), mergecoefs.get( 1 ), synthObjVec );
				
				// shift the upper/lower bounds
				if( (mergecoefs.get( 0 ) / mergecoefs.get( 1 )) > 0 )
				{
					lowerBounds.set( mergecols.get( 0 ), Math.max( lowerBounds.get( mergecols.get( 0 ) ), -upperBounds.get( mergecols.get( 1 ) ) * mergecoefs.get( 1 ) / mergecoefs.get( 0 ) ) );
					upperBounds.set( mergecols.get( 0 ), Math.min( upperBounds.get( mergecols.get( 0 ) ), -lowerBounds.get( mergecols.get( 1 ) ) * mergecoefs.get( 1 ) / mergecoefs.get( 0 ) ) );
				}
				else
				{
					lowerBounds.set( mergecols.get( 0 ), Math.max( lowerBounds.get( mergecols.get( 0 ) ), -lowerBounds.get( mergecols.get( 1 ) ) * mergecoefs.get( 1 ) / mergecoefs.get( 0 ) ) );
					upperBounds.set( mergecols.get( 0 ), Math.min( upperBounds.get( mergecols.get( 0 ) ), -upperBounds.get( mergecols.get( 1 ) ) * mergecoefs.get( 1 ) / mergecoefs.get( 0 ) ) );
				}

				 boolean removeExtra = (lowerBounds.get( mergecols.get( 0 ) ) >= upperBounds.get( mergecols.get( 0 ) ));
				removeColumn( mergecols.get( 1 ) );
				if( removeExtra )
					removeColumn( mergecols.get( 0 ) );
				removeRow( candmass );
				
	//			System.out.println( Integer.toString( candmass+1 ) + "\t" + Integer.toString( mergecols.get( 0 ) +1) + "\t"
	//				+ Integer.toString( mergecols.get( 1 ) +1) + "\t" + (removeExtra ? "1" : "0") );
			}
			
	//		dump( "MostSMatrix-red-part.txt", sMatrix );
	//		dump( "MostGMatrix-red-part.txt", gMatrix );
	//		compareCSV( "MostSMatrix-red-part.txt", "MatlabSMatrix-red-part.txt", "\t" );
	//		compareCSV( "MostGMatrix-red-part.txt", "MatlabGMatrix-red-part.txt", "\t" );
			
		} while( rowCount() < orRowCount || columnCount() < orColCount );
		
		dump( "MostSMatrix-red-part.txt", sMatrix );
	//	dump( "MostGMatrix-red-part.txt", gMatrix );
		compareCSV( "MostSMatrix-red-part.txt", "MatlabSMatrix-red-part.txt", "\t" );
	//	compareCSV( "MostGMatrix-red-part.txt", "MatlabGMatrix-red-part.txt", "\t" );
		
		System.out.println( "Done!" );
	}
	
	/**
	 * For decompressing FBA model fluxes
	 * @param v the flux vector
	 * @return the decompressed flux vector
	 */
	public ArrayList< Double > decompress( ArrayList< Double > v )
	{
		ArrayList< Double > result = new ArrayList< Double >();
		for( int i = 0; i < recMat.size(); ++i )
		{
			double dot = 0.0;
			for( int j = 0; j < v.size(); ++j )
			{
				dot += getrMat(i,j) * v.get( j );
			}
			result.add( dot );
		}
		
		return result;
	}
	
	/**
	 * For decompressing GDBB  model fluxes
	 * @param v the flux vector
	 * @return the decompressed flux vector
	 */
	public double[] decompress( double[] v )
	{
	/*
	 	_
	 	X
	 	X } n (fluxes) (compressed)
	 	X
	 	_
	 	X
	 	X } 3n + m (???) (compressed)
	 	X
	 	_
	 	X
	 	X } u (knockouts:unique gene associations) (compressed)
	 	X
	 	_
	*/
		double[] result = new double[ 4 * or_column_count + or_row_count + geneAssociations.size() ];
		
		// fill in the fluxes part
		ArrayList< Double > vecFluxes = new ArrayList< Double >();
		for( int j = 0; j < lowerBounds.size(); ++j )
			vecFluxes.add( v[ j ] );
		vecFluxes = decompress( vecFluxes );
		for( int j = 0; j < or_column_count; ++j )
			result[ j ] = vecFluxes.get( j );
		vecFluxes.clear();
		
		// fill in the knockouts part
		ArrayList< Double > knockouts = new ArrayList< Double >();
		for( int j = 4 * lowerBounds.size() + sMatrix.size(); j < 4 * lowerBounds.size() + sMatrix.size() + geneAssociations.size(); ++j )
			knockouts.add( v[ j ] );
		//knockouts = decompressKO( knockouts );
		for( int j = 4 * or_column_count + or_row_count; j < 4 * or_column_count + or_row_count + geneAssociations.size(); ++j )
			result[ j ] = knockouts.get( j - (4 * or_column_count + or_row_count) );
		knockouts.clear();
				
		return result;
	}
		
	public double getMaxObj( ArrayList< Double > v )
	{
		double result = 0.0;
		for( Entry< Integer, Double > entry : objVec.entrySet() )
			result += v.get( entry.getKey() ) * entry.getValue();
		return result;
	}
	
	public double getMaxSynthObj( double[] v )
	{
		double result = 0.0;
		for( Entry< Integer, Double > entry : synthObjVec.entrySet() )
			result += v[ entry.getKey() ] * entry.getValue();
		return result;
	}
	
	private Map< Integer, Double > removeColumn( int j, Map< Integer, Double > oldRow )
	{
		Map< Integer, Double > newRow = new HashMap< Integer, Double >();
 		for( Entry< Integer, Double > entry : oldRow.entrySet() )
 			if( !entry.getValue().equals( 0.0 ) )
	 			if( entry.getKey() < j )
	 				newRow.put( entry.getKey(), entry.getValue() );
	 			else if( entry.getKey() > j )
	 				newRow.put( entry.getKey() - 1, entry.getValue() );
 		return newRow;
	}
	
	private void removeColumn( int j, ArrayList< Map< Integer, Double > > vvec, boolean removeRows )
	{
		if( !removeRows )
			for( int i = 0; i < vvec.size(); ++i )
				vvec.set( i, removeColumn( j, vvec.get( i ) ) );
		else
			for( int i = 0; i < vvec.size(); ++i )
			{
				Map< Integer, Double > newvec = removeColumn( j, vvec.get( i ) );
				if( newvec.size() > 0 )
					vvec.set( i, newvec );
				else
					vvec.remove( i-- );
			}
	}
	
	private void removeColumn( int j )
	{
	
		// sMat
	 	removeColumn( j, sMatrix, false );
	 	
	 	// recMat
	 	removeColumn( j, recMat, false );
	 	
	 	// gMat
	 	if( gMatrix != null )
		 	removeColumn( j, gMatrix, false );
	 	
	 	//objVec
 		objVec = removeColumn( j, objVec );
 		
 		//synthObjVec
 		if( synthObjVec != null )
	 		synthObjVec = removeColumn( j, synthObjVec );
 		
	 	lowerBounds.remove( j );
	 	upperBounds.remove( j );
	 	if( reactions != null )
	 		reactions.remove( j );
	
	}
	
	private void removeRow( int i )
	{
		// sMatrix.get( i ).clear();
		
		sMatrix.remove( i );
		
		if( this.metabolites != null )
			metabolites.remove( i );
	}

	public void setLowerBounds( ArrayList< Double > lowerBounds )
	{
		this.lowerBounds = lowerBounds;
		if( lowerBounds != null )
			or_column_count = lowerBounds.size();
	}

	public void setUpperBounds( ArrayList< Double > upperBounds )
	{
		this.upperBounds = upperBounds;
	}

	public void setSynthObjVec( Map< Integer, Double > mapSyntheticObjective )
	{
		this.synthObjVec = mapSyntheticObjective;
	}
	
	public Map< Integer, Double > getSynthObjVec()
	{
		return this.synthObjVec;
	}
	
	public void setMetabolites( Vector< ModelMetabolite > metabolites )
	{
		this.metabolites = metabolites;
		this.metabolitesCopy = new Vector< ModelMetabolite >( metabolites );
	}
	
	public Vector< ModelMetabolite > getMetabolitesCopy()
	{
		return metabolitesCopy;
	}

	public Vector<String> getGeneAssociations()
	{
		return geneAssociations;
	}

	public void setGeneAssociations( Vector<String> geneAssociations )
	{
		this.geneAssociations = geneAssociations;
	}
}
