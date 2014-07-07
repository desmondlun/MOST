package edu.rutgers.MOST.optimization.solvers;

import gurobi.GRB;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.coinor.Ipopt;

public class NonlinearSolver extends Ipopt implements Solver
{
	private class Constraint
	{
		public char conType;
		public Double value;
		public Vector< Double > terms = new Vector< Double >();
	}
	private class Variable
	{
		double lb;
		double ub;
	}
	
	boolean obj_set = false;
	private Vector< Constraint > constraints = new Vector< Constraint >();
	private Vector< Double > objTerms = new Vector< Double >();
	private Vector< Variable > variables = new Vector< Variable >();
	private ArrayList< Double > soln = new ArrayList< Double >();
	private Algorithm algorithm;
	
	public NonlinearSolver( Algorithm algorithm )
	{
		this.algorithm = algorithm;
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
	public void setVar( String varName, VarType types, double lb, double ub )
	{
		Variable v = new Variable();
		v.lb = lb;
		v.ub = ub;
		variables.add( v );
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
			for( int j = 0; j < variables.size(); ++j )
				objTerms.add( new Double( 0 ) );
			obj_set = true;
		}
		
		for( Entry< Integer, Double > term : map.entrySet() )
			objTerms.set( term.getKey(), term.getValue() );
	}

	@Override
	public void addConstraint( Map< Integer, Double > map, ConType con,
			double value )
	{
		Constraint c = new Constraint();
		for( int j = 0; j < variables.size(); ++j )
			c.terms.add( 0.0 );
		
		for( Entry< Integer, Double > term : map.entrySet() )
			c.terms.set( term.getKey(), term.getValue() );
		
		constraints.add( c );		
	}

	@Override
	public double optimize()
	{
		double[] x_L = new double[ variables.size() ];
		double[] x_U = new double[ variables.size() ];
		double[] g_L = new double[ constraints.size() ];
		double[] g_U = new double[ constraints.size() ];
		
		for( int j = 0; j < variables.size(); ++j )
		{
			x_L[ j ] = variables.get( j ).lb;
			x_U[ j ] = variables.get( j ).ub;
		}
		
		for( int i = 0; i < constraints.size(); ++i )
		{
			switch( constraints.get( i ).conType )
			{
			case GRB.LESS_EQUAL:
				g_L[ i ] = Double.NEGATIVE_INFINITY;
				g_U[ i ] = constraints.get( i ).value;
				break;
			case GRB.EQUAL:
				g_L[ i ] = constraints.get( i ).value;
				g_U[ i ] = constraints.get( i ).value;
				break;
			case GRB.GREATER_EQUAL:
				g_L[ i ] = constraints.get( i ).value;
				g_U[ i ] = Double.POSITIVE_INFINITY;
				break;
			}
		}
		
		this.create( variables.size(), x_L, x_U, constraints.size(), g_L, g_U,
				constraints.size() * variables.size(), 0, Ipopt.C_STYLE );
		
		double[] vars = new double[ variables.size() ];
		for( int j = 0; j < vars.length; ++j )
			vars[ j ] = 0;
		
		this.addNumOption( KEY_OBJ_SCALING_FACTOR, -1.0 );
	//	this.addIntOption( "mumps_mem_percent", 500 );
		this.solve( vars );
		
		double value = 0.0;
		for( int j = 0; j < variables.size(); ++j )
			value += objTerms.get( j ) * vars[ j ];
		
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
	protected boolean eval_f( int n, double[] x, boolean new_x,
			double[] obj_value )
	{
		double value = 0.0;
		for( int j = 0; j < variables.size(); ++j )
			value += objTerms.get( j ) * x[ j ];
		
		obj_value[ 0 ] = value;
		
		return true;
	}

	@Override
	protected boolean eval_grad_f( int n, double[] x, boolean new_x,
			double[] grad_f )
	{
		for( int j = 0; j < variables.size(); ++j )
		{
			double value = 0.0;
			value = objTerms.get( j );
			grad_f[ j ] = value;
		}
		return true;
	}

	@Override
	protected boolean eval_g( int n, double[] x, boolean new_x, int m,
			double[] g )
	{
		for( int i = 0; i < constraints.size(); ++i )
		{
			double value = 0.0;
			for( int j = 0; j < variables.size(); ++j )
			{
				value += constraints.get( i ).terms.get( j ) * x[ j ];
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
			for( int i = 0; i < constraints.size(); ++i )
			{
				for( int j = 0; j < variables.size(); ++j )
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
				for( i = 0; i < constraints.size(); ++i )
				{
					for( j = 0; j < variables.size(); ++j )
						values[ idx++ ] = constraints.get( i ).terms.get( j );
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

	@Override
	public void setGeneExpr( Vector< Double > geneExpr )
	{
		// TODO Auto-generated method stub
		
	}
}
