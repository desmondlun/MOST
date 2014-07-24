package edu.rutgers.MOST.optimization.solvers;

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
		// calculate the dot product between geneExpr and fluxes
		double dotProduct = 0.0;
		for( int i = 0; i < geneExpr.size(); ++i )
			dotProduct += geneExpr.get( i ) * x[ i ];
	
		// calculate length of flux_v
		double length_flux_v = 0;
		for( double x_i : x )
			length_flux_v += x_i * x_i;
		length_flux_v = Math.sqrt( length_flux_v );
	
		// -1 <= ( flux_v dot gene_v ) / ( ||flux_v|| ) <= 1
		obj_value[ 0 ] = dotProduct / ( length_flux_v );
		
		return true;
	}

	@Override
	protected boolean eval_grad_f( int n, double[] x, boolean new_x,
			double[] grad_f )
	{
		// gradient of objective
		for( int j = 0; j < component.variableCount(); ++j )
			grad_f[ j ] = 0; // d/dx  v*g/sqrt(v^2) = 0
			
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
