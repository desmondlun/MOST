package edu.rutgers.MOST.optimization.solvers;

import java.util.Vector;

public class NonlinearIPoptSolver extends IPoptSolver implements NonlinearSolver
{
	public NonlinearIPoptSolver()
	{
		super();
	}
	
	@Override
	protected boolean eval_f( int n, double[] x, boolean new_x,
			double[] obj_value )
	{

		Vector< Double > flux_v = new Vector< Double >();
		Vector< Double > gene_v = new Vector< Double >();
				
		for( int i = 0; i < component.constraintCount(); ++i )
		{
			Double g_i = geneExpr.get( i ); // updated from SPOT.run() and modelFormatter method
			Double v_i = 0.0;
			for( int j = 0; j < component.variableCount(); ++j )
				v_i += component.getConstraint( i ).getCoefficient( j ) * x[ j ];
			flux_v.add( v_i );
			gene_v.add( Double.isInfinite( g_i ) ? 0 : g_i );
		}
				
		// calculate the dot product between flux_v and gene_v
		double dotProduct = 0.0;
		assert( flux_v.size() == gene_v.size() );
		for( int i = 0; i < flux_v.size(); ++i )
			dotProduct += flux_v.get( i ) * gene_v.get( i );
	
		// calculate length of flux_v
		double length_flux_v = 0;
		for( Double v_i : flux_v )
			length_flux_v += v_i * v_i;
		length_flux_v = Math.sqrt( length_flux_v );
	
		// -1 <= ( flux_v dot gene_v ) / ( ||flux_v|| ) <= 1
		obj_value[ 0 ] = dotProduct / ( length_flux_v );
		
		return true;
	}

	@Override
	protected boolean eval_grad_f( int n, double[] x, boolean new_x,
			double[] grad_f )
	{
		
		for( int j = 0; j < component.variableCount(); ++j )
		{
			Vector< Double > flux_v = new Vector< Double >();
			Vector< Double > gene_v = new Vector< Double >();
			// fill in flux_v using variable 'x', fill in gene_v given value from file
	
			for( int i = 0; i < component.constraintCount(); ++i )
			{
				Double g_i = geneExpr.get( i );
				Double v_i = component.getConstraint( i ).getCoefficient( j );
				flux_v.add( v_i );
				gene_v.add( Double.isInfinite( g_i ) ? 0 : g_i );
			}
	
			// calculate the dot product between flux_v' and gene_v
			double dotProduct = 0.0;
			for( int i = 0; i < flux_v.size(); ++i )
				dotProduct += flux_v.get( i ) * gene_v.get( i );
	
			// calculate length of flux_v'
			double length_flux_v = 0;
			for( Double v_i : flux_v )
				length_flux_v += v_i * v_i;
			length_flux_v = Math.sqrt( length_flux_v );
	
			// -1 <= ( flux_v' dot gene_v ) / ( ||flux_v'|| ) <= 1
			grad_f[ j ] = dotProduct / ( length_flux_v );
		}
			
		return true;
	}

	@Override
	protected boolean eval_g( int n, double[] x, boolean new_x, int m,
			double[] g )
	{
		for( int i = 0; i < component.constraintCount(); ++i )
		{
			double value = 0.0;
			for( int j = 0; j < component.variableCount(); ++j )
			{
				value += component.getConstraint( i ).getCoefficient( j ) * x[ j ];
			}
			g[ i ] = value;
		}
		return true;
	}

	@Override
	protected boolean eval_jac_g( int n, double[] x, boolean new_x, int m,
			int nele_jac, int[] iRow, int[] jCol, double[] values )
	{
		if( values == null )
		{
			int idx = 0;
			for( int i = 0; i < component.constraintCount(); ++i )
			{
				for( int j = 0; j < component.variableCount(); ++j )
				{
					iRow[ idx ] = i;
					jCol[ idx ] = j;
					idx++;
				}
			}
		}
		
		else
		{
			int idx = 0;
			int i=0;
			int j=0;
			try
			{
				for( i = 0; i < component.constraintCount(); ++i )
				{
					for( j = 0; j < component.variableCount(); ++j )
						values[ idx++ ] = component.getConstraint( i ).getCoefficient( j );
				}
			}
			catch( Exception e )
			{
				e.printStackTrace();
				System.out.println( "i = " + i + ", j = " + j );
			}
		}
		return true;
	}

	@Override
	protected boolean eval_h( int n, double[] x, boolean new_x,
			double obj_factor, int m, double[] lambda, boolean new_lambda,
			int nele_hess, int[] iRow, int[] jCol, double[] values )
	{
		return true;
	}
}
