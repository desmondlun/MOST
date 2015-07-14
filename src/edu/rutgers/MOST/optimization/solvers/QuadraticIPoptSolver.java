package edu.rutgers.MOST.optimization.solvers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Vector;

import org.coinor.Ipopt;

import edu.rutgers.MOST.presentation.ResizableDialog;

public class QuadraticIPoptSolver extends Ipopt implements QuadraticSolver
{
	private ArrayList< Double > soln = new ArrayList< Double >();
	private SolverComponent component = new SolverComponentLightWeight();
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
		dialog.setErrorMessage( "<html><p>" + errors.toString() + "</p></html>" );
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
		try
		{
			// Fv = z extra constraint
			component = componentSource;
			component.addConstraint( objCoefs, ConType.EQUAL, objVal );
			
			this.create( component.variableCount(),component.constraintCount(),
					component.constraintCount() * component.variableCount(), component.variableCount(), Ipopt.C_STYLE );
			
			
			this.setIntegerOption( "mumps_mem_percent", 500 );
			this.OptimizeNLP();
			
			// remove the extra constraint
			component.removeConstraint( component.constraintCount() - 1 );
			
			for( double d : this.getVariableValues() )
				soln.add( d );		
		}
		catch( Error | Exception thrown )
		{
			processStackTrace( thrown );
		}
		
		return soln;
	}

	@Override
    protected boolean get_starting_point(int n, boolean init_x, double[] x,
            boolean init_z, double[] z_L, double[] z_U,
            int m, boolean init_lambda,double[] lambda){
      
		for( int j = 0; j < component.variableCount(); ++j )
			x[ j ] = 0;
        
        return true;
    }
	
	@Override
	 protected boolean get_bounds_info(int n, double[] x_L, double[] x_U,
	            int m, double[] g_L, double[] g_U){
		
		for( int j = 0; j < component.variableCount(); ++j )
		{
			x_L[ j ] = component.getVariable( j ).lb;
			x_U[ j ] = component.getVariable( j ).ub;
		}
		
		for( int i = 0; i < component.constraintCount(); ++i )
		{
			switch( component.getConstraint( i ).type )
			{
			case LESS_EQUAL:
				g_L[ i ] = Double.NEGATIVE_INFINITY;
				g_U[ i ] = component.getConstraint( i ).value;
				break;
			case EQUAL:
				g_L[ i ] = component.getConstraint( i ).value;
				g_U[ i ] = component.getConstraint( i ).value;
				break;
			case GREATER_EQUAL:
				g_L[ i ] = component.getConstraint( i ).value;
				g_U[ i ] = Double.POSITIVE_INFINITY;
				break;
			}
		}
		
		return true;
	 }
	
	@Override
	protected boolean eval_f( int n, double[] x, boolean new_x,
			double[] obj_value )
	{
		double value = 0.0;
		for( int j = 0; j < component.variableCount(); ++j )
			value += x[ j ] * x[ j ];
		obj_value[ 0 ] = value;
		
		return true;
	}

	@Override
	protected boolean eval_grad_f( int n, double[] x, boolean new_x,
			double[] grad_f )
	{
		for( int j = 0; j < component.variableCount(); ++j )
			grad_f[ j ] = 2 * x[ j ];
		
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
		// The Hessian constraint of the jacobian
		if( values == null )
		{
			// [ x 0 0 0 ]
			// [ 0 x 0 0 ]
			// [ 0 0 x 0 ]
			// [ 0 0 0 x ]
			for( int j = 0; j < component.variableCount(); ++j )
			{
				iRow[ j ] = j;
				jCol[ j ] = j;
			}
		}
		else
		{
			for( int j = 0; j < component.variableCount(); ++j )
				values[ j ] = obj_factor * 2;
		}
		
		return true;
	}
	@Override
	public ArrayList< Double > SPOTAlgorithm( ArrayList< Double > objCoefs,
		SolverComponent component ) throws Exception
	{
		SPOTv2IPoptSolver solver = new SPOTv2IPoptSolver();
		solver.setSolverComponent( component );
		solver.setGeneExpr( new Vector< Double >(objCoefs) );
		solver.addNormalizeConstraint();
		solver.optimize();
		this.soln = solver.getSoln();
		return soln;
	}
}
