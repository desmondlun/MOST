package edu.rutgers.MOST.optimization.solvers;

import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import org.coinor.Ipopt;

import edu.rutgers.MOST.data.Model;
import edu.rutgers.MOST.presentation.AbstractParametersDialog;
import edu.rutgers.MOST.presentation.GraphicalInterface;
import edu.rutgers.MOST.presentation.IPoptParameters;
import edu.rutgers.MOST.presentation.SimpleProgressBar;

public  abstract class IPoptSolver extends Ipopt implements NonlinearSolver, LinearSolver
{
	protected Model dataModel;
	private boolean usingNormalConstraint = false;
	private boolean obj_set = false;
	SolverComponent component = new SolverComponentHeavyWeight();
	protected ArrayList< Double > objCoefs = new ArrayList< Double >();
	private ArrayList< Double > soln = new ArrayList< Double >();
	protected Vector< Double > geneExpr = new Vector< Double >();
	protected ArrayList< Double > startingPoint = new ArrayList< Double >();
	protected SimpleProgressBar pb = null;
	
	public IPoptSolver()
	{
	}

	@Override
	public String getName()
	{
		return "IPopt Solver";
	}

	@Override
	public ArrayList< Double > getSoln()
	{
		return soln;
	}

	@Override
	public void setVar( String varName, VarType type, double lb, double ub )
	{
		component.addVariable( type, lb, ub );
	}

	@Override
	public void setObjType( ObjType objType )
	{
	}

	@Override
	public void setObj( Map< Integer, Double > map )
	{
		if( !obj_set )
		{
			for( int j = 0; j < component.variableCount(); ++j )
				objCoefs.add( new Double( 0 ) );
			obj_set = true;
		}
		
		for( Entry< Integer, Double > term : map.entrySet() )
			objCoefs.set( term.getKey(), term.getValue() );
	}

	@Override
	public void addConstraint( Map< Integer, Double > map, ConType conType,
			double value )
	{
		component.addConstraint( map, conType, value );
	}

	@Override
	public double optimize()
	{
		try
		{
			pb = new SimpleProgressBar( "IPopt Progress", "Calculating objective" );
			pb.setLocationRelativeTo( null );
			pb.progressBar.setString( "" );
			pb.progressBar.setIndeterminate( true );
			pb.setVisible( true );
			if( startingPoint.size() == 0 )
				for( int j = 0; j < component.variableCount(); ++j )
					startingPoint.add( 0.0 );
			
			int constraintCount = component.constraintCount() + (usingNormalConstraint? 1 : 0 );
			double[] x_L = new double[ component.variableCount() ];
			double[] x_U = new double[ component.variableCount() ];
			double[] g_L = new double[ constraintCount ];
			double[] g_U = new double[ constraintCount ];
			
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
			
			if( this.usingNormalConstraint )
			{
				g_L[ component.constraintCount() ] = 0.0;
				g_U[ component.constraintCount() ] = 1.0;
			}
			
			this.create( component.variableCount(), x_L, x_U, constraintCount, g_L, g_U,
					constraintCount * component.variableCount(), component.variableCount() * component.variableCount(), Ipopt.C_STYLE );
			
			double[] vars = new double[ component.variableCount() ];
			for( int j = 0; j < vars.length; ++j )
				vars[ j ] = (this.usingNormalConstraint? 0.0: startingPoint.get( j ) );
			
			// set IPopt settings
			AbstractParametersDialog params = GraphicalInterface.getIPOptParameters();
			this.addIntOption( KEY_MAX_ITER, 
				Integer.valueOf( params.getParameter( IPoptParameters.MAXITER_NAME ) ) );
			this.addNumOption( KEY_TOL,
				Double.valueOf( params.getParameter( IPoptParameters.FEASIBILITYTOL_NAME ) ) );
			this.addNumOption( KEY_DUAL_INF_TOL,
				Double.valueOf( params.getParameter( IPoptParameters.DUALFEASIBILITYTOL_NAME ) ) );
			this.addNumOption( KEY_CONSTR_VIOL_TOL,
				Double.valueOf( params.getParameter( IPoptParameters.CONSTRAINTOL_NAME ) ) );
			
			
			this.addNumOption( KEY_OBJ_SCALING_FACTOR, -1.0 );
			this.addIntOption( "mumps_mem_percent", 200 );
			this.addStrOption( KEY_HESSIAN_APPROXIMATION, "limited-memory" );
			this.solve( vars );
			
			double value = 0.0;
			if( objCoefs.size() != 0 )
			for( int j = 0; j < component.variableCount(); ++j )
				value += objCoefs.get( j ) * vars[ j ];
			
			for( double d : vars )
				soln.add( d );
			return value;
		}
		catch( Exception e )
		{
			throw e;
		}
		finally
		{
			if( pb != null )
			{
				pb.setVisible( false );
				pb.dispose();
			}
		}
	}

	@Override
	public void setEnv( double timeLimit, int numThreads )
	{
	}

	@Override
	public void setVars( VarType[] types, double[] lb, double[] ub )
	{
	}

	@Override
	public void abort()
	{
	}

	@Override
	public void enable()
	{
	}

	@Override
	public void setAbort( boolean abort )
	{
	}

	@Override
	public void setGeneExpr( Vector< Double > geneExpr )
	{
		this.geneExpr = geneExpr;
	}

	@Override
	public SolverComponent getSolverComponent()
	{
		return component;
	}

	@Override
	public ArrayList< Double > getObjectiveCoefs()
	{
		return this.objCoefs;
	}
	
	@Override
	public void setSolverComponent( SolverComponent source )
	{
		this.component = source;
	}
	
	@Override
	public double optimize( ArrayList< Double > x0 )
	{
		this.startingPoint = x0;
		return optimize();
	}

	@Override
	public void addNormalizeConstraint()
	{
		usingNormalConstraint = true;
	}
	
	@Override
	public void setDataModel( Model model )
	{
		this.dataModel = model;
	}
	
	@Override
	public void FVA( ArrayList< Double > objCoefs, Double objVal, ArrayList< Double > fbaSoln,
			ArrayList< Double > min, ArrayList< Double > max, SolverComponent component ) throws Exception
	{
	}
}
