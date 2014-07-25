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
		// f = x dot geneExpr
		// f' = geneExpr		
		// g = || sum x^2 ||^-1/2
		// g' = -1/2 * || sum x^2 ||^-3/2 * 2x_i
		
		// compute f
		double f = 0.0;
		for( int j = 0; j < component.variableCount(); ++j )
			f += geneExpr.get( j ) * x[ j ];
		
		//compute g
		double sum_x_squared = 0.0;
		for( int j = 0; j < component.variableCount(); ++j )
			sum_x_squared += x[ j ] * x[ j ];
		double g = Math.pow( sum_x_squared, -0.5 );
		
		//compute part of g', omit 2x_i for gradient part
		double grad_g_part = -0.5 * Math.pow( sum_x_squared, -3.0/2.0 );
		
		// gradient of objective
		for( int j = 0; j < component.variableCount(); ++j )
		{
			// f'g + fg'
			grad_f[ j ] = geneExpr.get(j)*g  +  f*grad_g_part*2*x[j];
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
		// f = x dot geneExpr
		// f' = geneExpr
		// g = || sum x^2 ||^-1/2
		// g' = -1/2 * || sum x^2 ||^-3/2 * 2x_i
		
		// u = -1/2 * || sum x^2 ||^-3/2
		// v = 2x_i
		// u' = 3/4 * || sum x^2 ||^-5/2
		// v' = 2
		// d/dx ( v*g ) = u'v + uv'
		// u'v =  3/4 * || sum x^2 ||^-5/2 * 2x_i
		// uv' = -1/2 * || sum x^2 ||^-3/2 * 2
		
		// g'' = 3/4 * || sum x^2 ||^-5/2 * 2x_i * 2x_j ---> i =/= j
		// g'' = u'v + v'u  ---> i==j
		
		// compute f
		double f = 0.0;
		for( int j = 0; j < component.variableCount(); ++j )
			f += geneExpr.get( j ) * x[ j ];
		
		// compute g
		double sum_x_squared = 0.0;
		for( int j = 0; j < component.variableCount(); ++j )
			sum_x_squared += x[ j ] * x[ j ];
		
		double g = Math.pow( sum_x_squared, -0.5 );
		
		// compute part of u
		double u = -0.5 * Math.pow( sum_x_squared, -3.0/2.0 );
		
		// compute u'
		double u_prime = 0.75 * Math.pow( sum_x_squared, -5.0/2.0 );
		
		if( values == null )
		{
			int idx = 0;
			for( int i = 0; i < component.variableCount(); ++i )
			{
				for( int j = 0; j < component.variableCount(); ++j )
				{
					iRow[ idx ] = i;
					jCol[ idx ] = j;
				}
			}
		}
		else
		{			
			int idx = 0;
			for( int i = 0; i < component.variableCount(); ++i )
			{
				// compute v
				double v = 2.0*x[i];
				
				// compute v'
				double v_prime = 2.0;
				
				for( int j = 0; j < component.variableCount(); ++j )
				{
					// d/dx(f'g + fg') = fg''
					if( i == j )
						values[ idx++ ] = obj_factor * f *(u_prime * v + v_prime * u);
					else
						values[ idx++ ] = obj_factor * f * 3.0/4.0 * Math.pow( sum_x_squared,-5.0/2.0 ) * 2*x[i] * 2*x[j];

						
				}
			}
		}
		return true;
	}
}
