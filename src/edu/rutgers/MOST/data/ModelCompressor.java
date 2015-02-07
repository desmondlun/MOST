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

import edu.rutgers.MOST.presentation.GraphicalInterface;

/**
 * This is a model reducer class used for
 * compressing FBA and GDBB models
 * @author Tony
 */
public class ModelCompressor
{
	private Vector< SBMLReaction > reactions = null;
	Vector< ModelMetabolite > metabolites = null;
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
	private boolean abortFlag = false;
	
	public synchronized void abort()
	{
		this.abortFlag = true;
	}
	
	private void setStatus( String status ) throws Exception
	{
	//	GraphicalInterface.getGdbbDialog().getti
		GraphicalInterface.getGdbbDialog().getCounterLabel().setText( status );
		
		if( abortFlag )
			throw new Exception( "AbortFlag" );
	}
	
	@SuppressWarnings( "resource" )
	public static void compareCSV( String file1, String file2, String delim )
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
					Double valMatlab = Double.valueOf( valsMatlab[i] ) + 0.0;
					Double valMost = Double.valueOf( valsMost[i] ) + 0.0;
					
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
	
	public static void dump( String filename, ArrayList< Map< Integer, Double > > mat, int nrxn )
	{
		PrintWriter writer = null;
		try
		{
			writer = new PrintWriter( filename, "US-ASCII" );
			
			for( Map< Integer, Double > m : mat )
			{
				String delim = "";
				for( int i = 0; i < nrxn; ++i )
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
	
	public static void dump( String filename, ArrayList< Double > v )
	{
		PrintWriter writer = null;
		try
		{
			writer = new PrintWriter( filename, "US-ASCII" );
					
			String delim = "";
			for( Double d : v )
			{
				writer.write( delim + d.toString() );
				delim = "\r\n";
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

	@SuppressWarnings( "unchecked" )
	public void setReactions( Vector< SBMLReaction > reactions )
	{
		this.reactions = (Vector< SBMLReaction >)reactions.clone();
		this.lowerBounds = new ArrayList< Double >();
		this.upperBounds = new ArrayList< Double >();
		
		for( SBMLReaction r : reactions )
		{
			lowerBounds.add( r.getLowerBound() );
			upperBounds.add( r.getUpperBound() );
		}
		
		this.or_column_count = reactions.size();
		
		createRecMat();
			
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
		for( int i = 0; i < reactions.size(); ++i )
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
	
	public void compressNetDebug() throws Exception
	{
		if( sMatrix == null || /*gMatrix == null ||*/
				objVec == null || lowerBounds == null || upperBounds == null )
			return;
		
		setStatus( "Preparing to compress model..." );
		// start the compression
		int orColCount;
		int orRowCount;
		do
		{
			
			
	//		dump( "MostSMatrix-red-part.txt", sMatrix );
	//		dump( "MostGMatrix-red-part.txt", gMatrix );
	//		dump( "MostLB-red-part.txt", lowerBounds );
	//		dump( "MostUB-red-part.txt", upperBounds );
	//		compareCSV( "MostSMatrix-red-part.txt", "MatlabSMatrix-red-part.txt", "\t" );
	//		compareCSV( "MostGMatrix-red-part.txt", "MatlabGMatrix-red-part.txt", "\t" );
	//		compareCSV( "MostLB-red-part.txt", "MatlabLB-red-part.txt", "\t" );
	//		compareCSV( "MostUB-red-part.txt", "MatlabUB-red-part.txt", "\t" );
			
			orColCount = columnCount();
			orRowCount = rowCount();
			
			// remove the 0-value rows
			for( int i = rowCount() - 1; i >= 0; --i )
			{
				int nonzerocount = 0;
				for( int j = 0; j < columnCount(); ++j )
					if( getsMat( i, j ) + 0.0 != 0.0 )
						++nonzerocount;
				if( nonzerocount == 0 )
				{
					removeRow( i );
					setStatus( "Removing redundant constraint: row " + Integer.toString( i+1 ) );
				}
			}
			
			// keep only the columns that have a nonzero value
			// (Y-dimension) across the matrix
			// debug code
			ArrayList< Integer > badrows = new ArrayList< Integer >();
			for( int i = rowCount() - 1; i >= 0; --i )
			{
				int nonzerocount = 0;
				for( int j = 0; j < columnCount(); ++j )
					if( getsMat( i, j ) + 0.0 != 0.0 )
						++nonzerocount;
				if( nonzerocount == 1 )
				{
					badrows.add( i );
					setStatus( "Found a redundant constraint: row " + (i+1) );
				}
			}
			
			// remove the rows (reactions) that have only 1 nonzero column (flux)
			// due to steady-state constraint, it will optimize to be 0 anyway
			ArrayList< Integer > badcols = new ArrayList< Integer >();
			for( int i : badrows )
			{
				ArrayList< Integer > cols = new ArrayList< Integer >();
				for( int j = 0; j < columnCount(); ++j )
					if( getsMat( i, j ) + 0.0 != 0.0 )
						cols.add( j );
				if( cols.size() == 1 )
					if( !badcols.contains( cols.get( 0 ) ) )
					{
						badcols.add( cols.get( 0 ) );
						setStatus( "Found a redundant reaction: column " + (cols.get( 0 )+1) );
					}
			}
			
			setStatus( "Applying changes..." );
			Collections.sort( badcols, Collections.reverseOrder() );
			
			for( int i : badcols )
				removeColumn( i );
			for( int i : badrows )
				removeRow( i );
			
			//again, remove the 0-value rows
			for( int i = rowCount() - 1; i >= 0; --i )
			{
				int nonzerocount = 0;
				for( int j = 0; j < columnCount(); ++j )
					if( getsMat( i, j ) + 0.0 != 0.0 )
						++nonzerocount;
				if( nonzerocount == 0 )
				{
					removeRow( i );
					setStatus( "Removing redundant constraint: row " + Integer.toString( i+1 ) );
				}
			}
		
	//		dump( "MostSMatrix-red-part.txt", sMatrix );
	//		dump( "MostGMatrix-red-part.txt", gMatrix );
	//		dump( "MostLB-red-part.txt", lowerBounds );
	//		dump( "MostUB-red-part.txt", upperBounds );
	//		compareCSV( "MostSMatrix-red-part.txt", "MatlabSMatrix-red-part.txt", "\t" );
	//		compareCSV( "MostGMatrix-red-part.txt", "MatlabGMatrix-red-part.txt", "\t" );
	//		compareCSV( "MostLB-red-part.txt", "MatlabLB-red-part.txt", "\t" );
	//		compareCSV( "MostUB-red-part.txt", "MatlabUB-red-part.txt", "\t" );
			
			for( boolean repeat = true; repeat; )
			{
				// find the rows that have only 2 nonzero columns (fluxes)
				// and merge them
				setStatus( "Searching for candidate masses to merge..." );
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
				
				setStatus( "Candidate masses found! \nColumns " + mergecols.get( 0 ) + " and " + mergecols.get( 1 ) );
				
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

				setStatus( "Applying merge.." );
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
	//		dump( "MostLB-red-part.txt", lowerBounds );
	//		dump( "MostUB-red-part.txt", upperBounds );
	//		compareCSV( "MostSMatrix-red-part.txt", "MatlabSMatrix-red-part.txt", "\t" );
	//		compareCSV( "MostGMatrix-red-part.txt", "MatlabGMatrix-red-part.txt", "\t" );
	//		compareCSV( "MostLB-red-part.txt", "MatlabLB-red-part.txt", "\t" );
	//		compareCSV( "MostUB-red-part.txt", "MatlabUB-red-part.txt", "\t" );
			
		} while( rowCount() < orRowCount || columnCount() < orColCount );
		
	//	dump( "MostSMatrix-red-part.txt", sMatrix );
	//	dump( "MostGMatrix-red-part.txt", gMatrix );
	//	dump( "MostLB-red-part.txt", lowerBounds );
	//	dump( "MostUB-red-part.txt", upperBounds );
	//	compareCSV( "MostSMatrix-red-part.txt", "MatlabSMatrix-red-part.txt", "\t" );
	//	compareCSV( "MostGMatrix-red-part.txt", "MatlabGMatrix-red-part.txt", "\t" );
	//	compareCSV( "MostLB-red-part.txt", "MatlabLB-red-part.txt", "\t" );
	//	compareCSV( "MostUB-red-part.txt", "MatlabUB-red-part.txt", "\t" );
		
		for( int i = 0; i < reactions.size(); ++i )
		{
			reactions.get( i ).setLowerBound( lowerBounds.get( i ) );
			reactions.get( i ).setUpperBound( upperBounds.get( i ) );
		}
		setStatus( "Done!" );
	//	System.out.println( "Done!" );
	}
	
	public void compressNet() throws Exception
	{
		if( sMatrix == null || /*gMatrix == null ||*/
				objVec == null || lowerBounds == null || upperBounds == null )
			return;
		
		setStatus( "Preparing to compress model..." );
		// start the compression
		int orColCount;
		int orRowCount;
		do
		{
			
			
	//		dump( "MostSMatrix-red-part.txt", sMatrix );
	//		dump( "MostGMatrix-red-part.txt", gMatrix );
	//		dump( "MostLB-red-part.txt", lowerBounds );
	//		dump( "MostUB-red-part.txt", upperBounds );
	//		compareCSV( "MostSMatrix-red-part.txt", "MatlabSMatrix-red-part.txt", "\t" );
	//		compareCSV( "MostGMatrix-red-part.txt", "MatlabGMatrix-red-part.txt", "\t" );
	//		compareCSV( "MostLB-red-part.txt", "MatlabLB-red-part.txt", "\t" );
	//		compareCSV( "MostUB-red-part.txt", "MatlabUB-red-part.txt", "\t" );
			
			orColCount = columnCount();
			orRowCount = rowCount();
			
			// remove the 0-value rows
			for( int i = rowCount() - 1; i >= 0; --i )
			{
				int nonzerocount = 0;
				for( Entry< Integer, Double > rowEntry : sMatrix.get( i ).entrySet() )
					if( rowEntry.getValue().doubleValue() + 0.0 != 0.0 )
						++nonzerocount;
				
				if( nonzerocount == 0 )
				{
					removeRow( i );
					setStatus( "Removing redundant constraint: row " + Integer.toString( i+1 ) );
				}
			}
			
			// keep only the columns that have a nonzero value
			// (Y-dimension) across the matrix
			// debug code
			ArrayList< Integer > badrows = new ArrayList< Integer >();
			for( int i = rowCount() - 1; i >= 0; --i )
			{
				int nonzerocount = 0;
				for( Entry< Integer, Double > rowEntry : sMatrix.get( i ).entrySet() )
					if( rowEntry.getValue().doubleValue() + 0.0 != 0.0 )
						++nonzerocount;
				if( nonzerocount == 1 )
				{
					badrows.add( i );
					setStatus( "Found a redundant constraint: row " + (i+1) );
				}
			}
			
			// remove the rows (reactions) that have only 1 nonzero column (flux)
			// due to steady-state constraint, it will optimize to be 0 anyway
			ArrayList< Integer > badcols = new ArrayList< Integer >();
			for( int i : badrows )
			{
				ArrayList< Integer > cols = new ArrayList< Integer >();
				for( int j = 0; j < columnCount(); ++j )
					if( getsMat( i, j ) + 0.0 != 0.0 )
						cols.add( j );
				if( cols.size() == 1 )
					if( !badcols.contains( cols.get( 0 ) ) )
					{
						badcols.add( cols.get( 0 ) );
						setStatus( "Found a redundant reaction: column " + (cols.get( 0 )+1) );
					}
			}
			
			setStatus( "Applying changes..." );
			Collections.sort( badcols, Collections.reverseOrder() );
			
			for( int i : badcols )
				removeColumn( i );
			for( int i : badrows )
				removeRow( i );
			
			//again, remove the 0-value rows
			for( int i = rowCount() - 1; i >= 0; --i )
			{
				int nonzerocount = 0;
				
				for( Entry< Integer, Double > rowEntry : sMatrix.get( i ).entrySet() )
					if( rowEntry.getValue().doubleValue() + 0.0 != 0.0 )
						++nonzerocount;
				
				if( nonzerocount == 0 )
				{
					removeRow( i );
					setStatus( "Removing redundant constraint: row " + Integer.toString( i+1 ) );
				}
			}
		
	//		dump( "MostSMatrix-red-part.txt", sMatrix );
	//		dump( "MostGMatrix-red-part.txt", gMatrix );
	//		dump( "MostLB-red-part.txt", lowerBounds );
	//		dump( "MostUB-red-part.txt", upperBounds );
	//		compareCSV( "MostSMatrix-red-part.txt", "MatlabSMatrix-red-part.txt", "\t" );
	//		compareCSV( "MostGMatrix-red-part.txt", "MatlabGMatrix-red-part.txt", "\t" );
	//		compareCSV( "MostLB-red-part.txt", "MatlabLB-red-part.txt", "\t" );
	//		compareCSV( "MostUB-red-part.txt", "MatlabUB-red-part.txt", "\t" );
			
			for( boolean repeat = true; repeat; )
			{
				// find the rows that have only 2 nonzero columns (fluxes)
				// and merge them
				setStatus( "Searching for candidate masses to merge..." );
				ArrayList< Integer > mergecols = new ArrayList< Integer >();
				ArrayList< Double > mergecoefs = new ArrayList< Double >();
				int candmass = 0;

		
				for( Map< Integer, Double > row : sMatrix )
				{
					for( Entry< Integer, Double > rowEntry : row.entrySet() )
					{
						if( rowEntry.getValue().doubleValue() != 0 )
						{
							mergecols.add( rowEntry.getKey() );
							mergecoefs.add( rowEntry.getValue() );
						}
					}
					if( mergecols.size() == 2 )
					{
						if( mergecols.get( 0 ) > mergecols.get( 1 ) )
						{
							int tmpidx = mergecols.get( 0 );
							mergecols.set( 0, mergecols.get( 1 ) );
							mergecols.set( 1, tmpidx );
							double tmpval = mergecoefs.get( 0 );
							mergecoefs.set( 0, mergecoefs.get( 1 ) );
							mergecoefs.set( 1, tmpval );
						}
						break;
					}
					else
					{
						mergecols.clear();
						mergecoefs.clear();
					}

					++candmass;
				}
		
		
				
				if( mergecols.size() != 2 )
				{
					repeat = false;
					continue;
				}
				
		//		System.out.println( "merging columns:[" + Integer.toString( mergecols.get( 0 ) +1) + " " + Integer.toString( mergecols.get( 1 ) +1) + "]" );
				
				setStatus( "Candidate masses found! \nColumns " + mergecols.get( 0 ) + " and " + mergecols.get( 1 ) );
				
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

				setStatus( "Applying merge.." );
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
	//		dump( "MostLB-red-part.txt", lowerBounds );
	//		dump( "MostUB-red-part.txt", upperBounds );
	//		compareCSV( "MostSMatrix-red-part.txt", "MatlabSMatrix-red-part.txt", "\t" );
	//		compareCSV( "MostGMatrix-red-part.txt", "MatlabGMatrix-red-part.txt", "\t" );
	//		compareCSV( "MostLB-red-part.txt", "MatlabLB-red-part.txt", "\t" );
	//		compareCSV( "MostUB-red-part.txt", "MatlabUB-red-part.txt", "\t" );
			
		} while( rowCount() < orRowCount || columnCount() < orColCount );
		
	//	dump( "MostSMatrix-red-part.txt", sMatrix );
	//	dump( "MostGMatrix-red-part.txt", gMatrix );
	//	dump( "MostLB-red-part.txt", lowerBounds );
	//	dump( "MostUB-red-part.txt", upperBounds );
	//	compareCSV( "MostSMatrix-red-part.txt", "MatlabSMatrix-red-part.txt", "\t" );
	//	compareCSV( "MostGMatrix-red-part.txt", "MatlabGMatrix-red-part.txt", "\t" );
	//	compareCSV( "MostLB-red-part.txt", "MatlabLB-red-part.txt", "\t" );
	//	compareCSV( "MostUB-red-part.txt", "MatlabUB-red-part.txt", "\t" );
		
		for( int i = 0; i < reactions.size(); ++i )
		{
			reactions.get( i ).setLowerBound( lowerBounds.get( i ) );
			reactions.get( i ).setUpperBound( upperBounds.get( i ) );
		}
		setStatus( "Done!" );
	//	System.out.println( "Done!" );
	}
	
	/**
	 * For decompressing FBA model fluxes
	 * @param v the flux vector
	 * @return the decompressed flux vector
	 */
	public ArrayList< Double > decompress( ArrayList< Double > v )
	{
		ArrayList< Double > result = new ArrayList< Double >();
		for( Map< Integer, Double > row : recMat ) // recMat.size() == reactions.size()
		{
			double dot = 0.0;
			
			for( Entry< Integer, Double > rowEntry : row.entrySet() )
				dot += rowEntry.getValue() * v.get( rowEntry.getKey() );
			
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
	 	X } 3n + m (fluxes and metabs) (compressed)
	 	X
	 	_
	 	X
	 	X } u (knockouts:unique gene associations) (uncompressed)
	 	X
	 	_
	*/
		double[] result = new double[ or_column_count + geneAssociations.size() ];
		
		// fill in the fluxes part
		ArrayList< Double > vecFluxes = new ArrayList< Double >();
		for( int j = 0; j < this.reactions.size(); ++j )
			vecFluxes.add( v[ j ] );
		vecFluxes = decompress( vecFluxes );
		
		for( int i = 0; i < vecFluxes.size(); ++i )
			result[ i ] = vecFluxes.get( i );
		
		// fill in the knockouts part (nbin)
		for( int i = v.length - geneAssociations.size(); i < v.length; ++i )
			vecFluxes.add( v[ i ] );
		
		for( int i = 0; i < vecFluxes.size(); ++i )
			result[ i ] = vecFluxes.get( i );
				
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

	public void setSynthObjVec( Map< Integer, Double > mapSyntheticObjective )
	{
		this.synthObjVec = mapSyntheticObjective;
	}
	
	public Map< Integer, Double > getSynthObjVec()
	{
		return this.synthObjVec;
	}
	
	@SuppressWarnings( "unchecked" )
	public void setMetabolites( Vector< ModelMetabolite > metabolites )
	{
		this.metabolites = (Vector< ModelMetabolite >)metabolites.clone();
	}
	
	public Vector< ModelMetabolite > getMetabolites()
	{
		return metabolites;
	}

	public Vector<String> getGeneAssociations()
	{
		return geneAssociations;
	}

	@SuppressWarnings( "unchecked" )
	public void setGeneAssociations( Vector<String> geneAssociations )
	{
		this.geneAssociations = (Vector<String>)geneAssociations.clone();
	}

	public ArrayList< Map< Integer, Double > > getSMatrix()
	{
		return this.sMatrix;
	}

	public ArrayList< Map< Integer, Double > > getGprMatrix()
	{
		return this.gMatrix;
	}

	public ArrayList< Double > getSyntheticObjective()
	{
		ArrayList< Double > result = new ArrayList< Double >();
		for( int i = 0; i < this.reactions.size(); ++i )
			result.add( 0.0 );
		
		for( Entry< Integer, Double > entry : this.synthObjVec.entrySet() )
			result.set( entry.getKey(), entry.getValue() );
		
		return result;
	}

	public ArrayList< Double > getObjective()
	{
		ArrayList< Double > result = new ArrayList< Double >();
		for( int i = 0; i < this.reactions.size(); ++i )
			result.add( 0.0 );
		
		for( Entry< Integer, Double > entry : this.objVec.entrySet() )
			result.set( entry.getKey(), entry.getValue() );
		
		return result;
	}
}
