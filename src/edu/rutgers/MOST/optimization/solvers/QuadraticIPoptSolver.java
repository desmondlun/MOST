package edu.rutgers.MOST.optimization.solvers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import org.coinor.Ipopt;

import edu.rutgers.MOST.presentation.ResizableDialog;

public class QuadraticIPoptSolver extends Ipopt implements QuadraticSolver
{
	private ArrayList< Double > soln = new ArrayList< Double >();
	private SolverComponent component = new SolverComponent();
	protected ResizableDialog dialog = new ResizableDialog( "Error",
			"IPopt Quadratic Solver Error", "IPopt Quadratic Solver Error" );
	
	/**
	 * Process the stack trace for exceptions or errors
	 * @param thrown The thrown object
	 */
	protected void processStackTrace( Throwable thrown )
	{
		//except.printStackTrace();
		StringWriter errors = new StringWriter();
		thrown.printStackTrace( new PrintWriter( errors ) );
		dialog.setErrorMessage( errors.toString() );
		// centers dialog
		dialog.setLocationRelativeTo(null);
		dialog.setModal(true);
		dialog.setVisible( true );
	}
	/**
	 * Used by E-Flux2
	 * Minimize the Euclidean norm of the flux vectors returned by FBA using IPOPT
	 * 
	 * @return The minimized Euclidean vector (ArrayList) of fluxes
	 * @param objCoefs The coefficient vector of the Objective function used in FBA, it is used as an extra constraint
	 * @param objVal The value of the objective function returned by FBA
	 * @param componentSource The solverComponent that contains the constraint and variable matrix
	 * @see Eflux-2
	 * @see FBA
	 */
	@Override
	public ArrayList< Double > minimizeEuclideanNorm(
			ArrayList< Double > objCoefs, Double objVal,
			SolverComponent componentSource )
	{
		// Fv = z extra constraint
		try
		{
		component = componentSource;
		component.addConstraint( objCoefs, ConType.EQUAL, objVal );
		
		// set up the constraints and variables
		double[] x_L = new double[ component.variables.size() ];
		double[] x_U = new double[ component.variables.size() ];
		double[] g_L = new double[ component.constraints.size() ]; 
		double[] g_U = new double[ component.constraints.size() ];
		
		for( int j = 0; j < component.variables.size(); ++j )
		{
			x_L[ j ] = component.variables.get( j ).lb;
			x_U[ j ] = component.variables.get( j ).ub;
		}
		
		for( int i = 0; i < component.constraints.size(); ++i )
		{
			switch( component.constraints.get( i ).type )
			{
			case LESS_EQUAL:
				g_L[ i ] = Double.NEGATIVE_INFINITY;
				g_U[ i ] = component.constraints.get( i ).value;
				break;
			case EQUAL:
				g_L[ i ] = component.constraints.get( i ).value;
				g_U[ i ] = component.constraints.get( i ).value;
				break;
			case GREATER_EQUAL:
				g_L[ i ] = component.constraints.get( i ).value;
				g_U[ i ] = Double.POSITIVE_INFINITY;
				break;
			}
		}
		
		this.create( component.variables.size(), x_L, x_U, component.constraints.size(), g_L, g_U,
				component.constraints.size() * component.variables.size(), component.variables.size(), Ipopt.C_STYLE );
		
		double[] vars = new double[ component.variables.size() ];
		for( int j = 0; j < vars.length; ++j )
			vars[ j ] = 0;
		
		// this.addNumOption( KEY_OBJ_SCALING_FACTOR, -1.0 );
		this.addIntOption( "mumps_mem_percent", 500 );
		this.solve( vars );
		
		for( double d : vars )
			soln.add( d );		
		}
		catch( Error | Exception thrown )
		{
			processStackTrace( thrown );
		}
		
		return soln;
	}

	@Override
	protected boolean eval_f( int n, double[] x, boolean new_x,
			double[] obj_value )
	{
		double value = 0.0;
		for( int j = 0; j < component.variables.size(); ++j )
			value += x[ j ] * x[ j ];
		obj_value[ 0 ] = value;
		
		return true;
	}

	@Override
	protected boolean eval_grad_f( int n, double[] x, boolean new_x,
			double[] grad_f )
	{
		for( int j = 0; j < component.variables.size(); ++j )
			grad_f[ j ] = 2 * x[ j ];
		
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
		// The Hessian constraint of the jacobian
		if( values == null )
		{
			// [ x 0 0 0 ]
			// [ 0 x 0 0 ]
			// [ 0 0 x 0 ]
			// [ 0 0 0 x ]
			for( int j = 0; j < component.variables.size(); ++j )
			{
				iRow[ j ] = j;
				jCol[ j ] = j;
			}
		}
		else
		{
			for( int j = 0; j < component.variables.size(); ++j )
				values[ j ] = obj_factor * 2;
		}
		
		return true;
	}
}
