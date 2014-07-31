package edu.rutgers.MOST.optimization.solvers;

public class SPOTv2IPoptSolver extends NonlinearIPoptSolver
{
	public SPOTv2IPoptSolver()
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
		
		obj_value[ 0 ] = dotProduct;
		
		return true;
	}

	@Override
	protected boolean eval_grad_f( int n, double[] x, boolean new_x,
			double[] grad_f )
	{
		// h = x dot g
		// h'= g_i
		for( int i = 0; i < component.variableCount(); ++i )
		{			
			// partial of obj / partial of x
			grad_f[ i ] = geneExpr.get( i );
		}
		
		return true;
	}

	@Override
	protected boolean eval_g( int n, double[] x, boolean new_x, int m,
			double[] g )
	{
		int i = 0;
		for( i = 0; i < component.constraintCount(); ++i )
		{
			double value = 0.0;
			for( int j = 0; j < component.variableCount(); ++j )
			{
				value += component.getConstraint( i ).getCoefficient( j ) * x[ j ];
			}
			g[ i ] = value;
		}
		
		{ // || x || <= 1
			double value = 0.0;
			for( int j = 0; j < component.variableCount(); ++j )
			{
				value += x[ j ] * x[ j ];
			}
			g[ g.length - 1 ] = value;
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
			for( int i = 0; i < component.constraintCount() + 1; ++i )
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
				
				// ||x|| <= 1
				{
					double length_x_squared = 0.0;
					for( j = 0; j < component.variableCount(); ++j )
						length_x_squared += x[ j ] * x[ j ];
					for( j = 0; j < component.variableCount(); ++j )
						values[ idx++ ] = Math.pow( length_x_squared, -1.0/2.0 ) * x[ j ];
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
		if( values == null )
		{
			int idx = 0;
			for( int i = 0; i < component.variableCount(); ++i )
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
			double length_x_squared = 0.0;
			for( int i = 0; i < component.variableCount(); ++i )
				length_x_squared += x[ i ] * x[ i ];
			
			for( int i = 0; i < component.variableCount(); ++i )
			{
				for( int j = 0; j < component.variableCount(); ++j )
					values[ idx++ ] = -lambda[ component.constraintCount() ] * (-Math.pow( length_x_squared, -3.0/2.0 )*x[j]*x[i] + Math.pow( length_x_squared, -1.0/2.0 )*(i==j?1.0:0.0));
			}
		}
		return true;
	}
}
