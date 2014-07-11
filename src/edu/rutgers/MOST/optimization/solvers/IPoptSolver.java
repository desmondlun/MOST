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

	/*
	@Override
	protected boolean eval_f( int n, double[] x, boolean new_x,
			double[] obj_value )
	{
		switch( algorithm )
		{
		case FBA:
			double value = 0.0;
			for( int j = 0; j < component.variableCount(); ++j )
				value += objCoefs.get( j ) * x[ j ];
			
			obj_value[ 0 ] = value;
			break;
			
		case SPOT:
			{
				Vector< Double > flux_v = new Vector< Double >();
				Vector< Double > gene_v = new Vector< Double >();
				
				for( int i = 0; i < component.constraintCount(); ++i )
				{
					Double g_i = geneExpr.get( i ); // updated from SPOT.run() and modelFormatter method
					Double v_i = 0.0;
					for( int j = 0; j < component.variableCount(); ++j )
						v_i += component.getConstraint( i ).coefficients.get( j ) * x[ j ];
					flux_v.add( v_i );
					gene_v.add( Double.isInfinite( g_i ) ? v_i : g_i );
				}
				
				// calculate the dot product between flux_v and gene_v
				double dotProduct = 0.0;
				assert( flux_v.size() == gene_v.size() );
				for( int i = 0; i < flux_v.size(); ++i )
					dotProduct += flux_v.get( i ) * gene_v.get( i );
	
				// calculate length of flux_v
				double length_flux_v = 0;
				for( Double v_i : flux_v )
					length_flux_v += v_i * v_i;
				length_flux_v = Math.sqrt( length_flux_v );
	
				// -1 <= ( flux_v dot gene_v ) / ( ||flux_v|| ) <= 1
				obj_value[ 0 ] = dotProduct / ( length_flux_v );
			}
			break;
			
		default:
			return false;
		}
		
		return true;
	}

	@Override
	protected boolean eval_grad_f( int n, double[] x, boolean new_x,
			double[] grad_f )
	{
		switch( algorithm )
		{
		case FBA:
			for( int j = 0; j < component.variableCount(); ++j )
			{
				double value = 0.0;
				value = objCoefs.get( j );
				grad_f[ j ] = value;
			}
			break;
			
		case SPOT:
			{
				for( int j = 0; j < component.variableCount(); ++j )
				{
					Vector< Double > flux_v = new Vector< Double >();
					Vector< Double > gene_v = new Vector< Double >();
					// fill in flux_v using variable 'x', fill in gene_v given value from file
			
					for( int i = 0; i < component.constraintCount(); ++i )
					{
						Double g_i = geneExpr.get( i );
						Double v_i = component.getConstraint( i ).coefficients.get( j );
						flux_v.add( v_i );
						gene_v.add( Double.isInfinite( g_i ) ? v_i : g_i );
					}
			
					// calculate the dot product between flux_v' and gene_v
					double dotProduct = 0.0;
					for( int i = 0; i < flux_v.size(); ++i )
						dotProduct += flux_v.get( i ) * gene_v.get( i );
			
					// calculate length of flux_v'
					double length_flux_v = 0;
					for( Double v_i : flux_v )
						length_flux_v += v_i * v_i;
					length_flux_v = Math.sqrt( length_flux_v );
			
					// -1 <= ( flux_v' dot gene_v ) / ( ||flux_v'|| ) <= 1
					grad_f[ j ] = dotProduct / ( length_flux_v );
				}
			}
			break;
			
		default:
			return false;
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
				value += component.getConstraint( i ).coefficients.get( j ) * x[ j ];
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
						values[ idx++ ] = component.getConstraint( i ).coefficients.get( j );
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
	*/
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
