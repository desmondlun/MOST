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
		// h = x dot g
		// h'= g_i
		// k = pow(length_x_squared,-1/2)
		// k'= -pow(length_x_squared,-3/2)*x_i
		// grad_obj = h'*k + h*k'
		
		// compute h
		double h = 0.0;
		for( int i = 0; i < component.variableCount(); ++i )
			h += x[ i ] * geneExpr.get( i );
		
		// compute k
		double length_x_squared = 0.0;
		for( int i = 0; i < component.variableCount(); ++i )
			length_x_squared += x[ i ] * x[ i ];
		double k = Math.pow( length_x_squared, -1.0/2.0 );
		
		for( int i = 0; i < component.variableCount(); ++i )
		{
			// compute h'
			double h_prime = geneExpr.get( i );
			
			//compute k'
			double k_prime = -Math.pow( length_x_squared, -3.0/2.0 ) * x[ i ];
			
			// partial of obj / partial of x
			grad_f[ i ] = h_prime*k + h*k_prime;
			
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
			// h0 = g_i
			// h0'= 0
			// k0 = pow( length_x_squared, -1/2 )
			// k0'= -pow( length_x_squared, -3/2 )*x_j
			// s' = h0'*k0 + h0*k0' = h0*k0'
			// h1 = (x dot g)*x_i
			// h1'= g_j*x_i		i != j
			// h1'= g_j*x_i + (x dot g)	i==j
			// k1 = pow( length_x_squared, -3/2 )
			// k1'= -3*pow( length_x_squared, -5/2 )*x_j
			// t' = h1'*k1 + h1*k1'
			// grad_grad_obj = s' - t'
			
			double x_dot_g = 0.0;
			double length_x_squared = 0.0;
			for( int i = 0; i < component.variableCount(); ++i )
			{
				x_dot_g += x[ i ] * geneExpr.get( i );
				length_x_squared = x[ i ] * x[ i ];
			}
			
			// compute k1
			double k1 = Math.pow( length_x_squared, -3.0/2.0 );
			
			
			int idx = 0;
			for( int i = 0; i < component.variableCount(); ++i )
			{
				// compute h0
				double h0 = geneExpr.get( i );
				
				// compute h1
				double h1 = x_dot_g*x[ i ];				

				for( int j = 0; j < component.variableCount(); ++j )
				{
					//compute h1'
					double h1_prime = 0.0;
					if( i != j )
						h1_prime = geneExpr.get( j ) * x[ i ];
					else
						h1_prime = geneExpr.get( j ) * x[ i ] + x_dot_g;
					// compute k0'
					double k0_prime = -Math.pow( length_x_squared, -3.0/2.0 )*x[ j ];
					
					//compute k1'
					double k1_prime = -3.0*Math.pow( length_x_squared, -5.0/2.0 )*x[ j ];
					
					// compute s_prime
					double s_prime = h0*k0_prime;
					
					// comput t_prime
					double t_prime = h1_prime*k1 + h1*k1_prime; 
					
					// partial^2 of obj / partial of x^2
					values[ idx++ ] = s_prime - t_prime;
				}
			}
		}
		return true;
	}
}
