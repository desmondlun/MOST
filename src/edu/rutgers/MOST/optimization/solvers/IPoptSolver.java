package edu.rutgers.MOST.optimization.solvers;

import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import org.coinor.Ipopt;

public  abstract class IPoptSolver extends Ipopt implements NonlinearSolver, LinearSolver
{
	private boolean obj_set = false;
	SolverComponent component = new SolverComponentLightWeight();
	protected ArrayList< Double > objCoefs = new ArrayList< Double >();
	private ArrayList< Double > soln = new ArrayList< Double >();
	protected Vector< Double > geneExpr = new Vector< Double >();
	
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
		double[] x_L = new double[ component.variableCount() ];
		double[] x_U = new double[ component.variableCount() ];
		double[] g_L = new double[ component.constraintCount() ];
		double[] g_U = new double[ component.constraintCount() ];
		
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
		
		this.create( component.variableCount(), x_L, x_U, component.constraintCount(), g_L, g_U,
				component.constraintCount() * component.variableCount(), 0, Ipopt.C_STYLE );
		
		double[] vars = new double[ component.variableCount() ];
		for( int j = 0; j < vars.length; ++j )
			vars[ j ] = 0;
		
		this.addNumOption( KEY_OBJ_SCALING_FACTOR, -1.0 );
		this.addIntOption( "mumps_mem_percent", 500 );
		this.addNumOption( KEY_ACCEPTABLE_TOL, 1e-9 );
		this.solve( vars );
		
		double value = 0.0;
		for( int j = 0; j < component.variableCount(); ++j )
			value += objCoefs.get( j ) * vars[ j ];
		
		for( double d : vars )
			soln.add( d );
		return value;
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
}
