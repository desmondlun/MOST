package edu.rutgers.MOST.optimization.solvers;

public class LinearIPoptSolver extends IPoptSolver implements LinearSolver
{
	public LinearIPoptSolver()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	protected boolean eval_f( int n, double[] x, boolean new_x,
			double[] obj_value )
	{

		double value = 0.0;
		for( int j = 0; j < component.variables.size(); ++j )
			value += objCoefs.get( j ) * x[ j ];
		
		obj_value[ 0 ] = value;
		
		return true;
	}

	@Override
	protected boolean eval_grad_f( int n, double[] x, boolean new_x,
			double[] grad_f )
	{
		for( int j = 0; j < component.variables.size(); ++j )
		{
			double value = 0.0;
			value = objCoefs.get( j );
			grad_f[ j ] = value;
		}

		return true;
	}

	@Override
	protected boolean eval_g( int n, double[] x, boolean new_x, int m,
			double[] g )
	{
		for( int i = 0; i < component.constraints.size(); ++i )
		{
			double value = 0.0;
			for( int j = 0; j < component.variables.size(); ++j )
			{
				value += component.constraints.get( i ).coefficients.get( j ) * x[ j ];
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
			for( int i = 0; i < component.constraints.size(); ++i )
			{
				for( int j = 0; j < component.variables.size(); ++j )
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
				for( i = 0; i < component.constraints.size(); ++i )
				{
					for( j = 0; j < component.variables.size(); ++j )
						values[ idx++ ] = component.constraints.get( i ).coefficients.get( j );
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
