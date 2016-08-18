package edu.rutgers.MOST.optimization.solvers;

import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import org.coinor.Ipopt;

public  abstract class IPoptSolver extends Ipopt implements NonlinearSolver, LinearSolver
{
	private static String dependsFolder = "lib/";
	static {
		if( System.getProperty( "os.name" ).toLowerCase().contains( "windows" ) )
		{
			dependsFolder += "win" + System.getProperty( "sun.arch.data.model" );
		}
		else if( System.getProperty( "os.name" ).toLowerCase()
				.contains( "mac os x" ) )
			dependsFolder += "mac";
		else
			dependsFolder += "linux";
	}
	
	private boolean usingNormalConstraint = false;
	private boolean obj_set = false;
	SolverComponent component = new SolverComponentHeavyWeight();
	protected ArrayList< Double > objCoefs = new ArrayList< Double >();
	private ArrayList< Double > soln = new ArrayList< Double >();
	protected Vector< Double > geneExpr = new Vector< Double >();
	protected ArrayList< Double > startingPoint = new ArrayList< Double >();
	
	public IPoptSolver()
	{
		super(dependsFolder, Ipopt.DLLNAME);
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
		int constraintCount = component.constraintCount() + (usingNormalConstraint? 1 : 0 );
		
		this.create( component.variableCount(), constraintCount,
				constraintCount * component.variableCount(), component.variableCount() * component.variableCount(), Ipopt.C_STYLE );
		
		this.setNumericOption( "obj_scaling_factor", -1.0 );
		this.setIntegerOption( "mumps_mem_percent", 500 );
		this.setIntegerOption( "max_iter", 30000 );
		this.setStringOption( "hessian_approximation", "limited-memory" );
		//this.addNumOption( "acceptable_tol", 1e-9 );
		this.OptimizeNLP();
		
		for( double d : this.getVariableValues() )
			soln.add( d );
		
		double value = 0.0;
		if( objCoefs.size() != 0 )
			for( int j = 0; j < component.variableCount(); ++j )
				value += objCoefs.get( j ) * soln.get( j );
		
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
	
    protected boolean get_starting_point(int n, boolean init_x, double[] x,
            boolean init_z, double[] z_L, double[] z_U,
            int m, boolean init_lambda,double[] lambda){
      
    	if( this.startingPoint.size() == 0 )
    		for( int j = 0; j < component.variableCount(); ++j )
				startingPoint.add( 0.0 );
    	
    	for( int j = 0; j < component.variableCount(); ++j )
			x[ j ] = (this.usingNormalConstraint? 0.0: startingPoint.get( j ) );
        
        return true;
    }
	
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
		
		if( this.usingNormalConstraint )
		{
			g_L[ component.constraintCount() ] = 0.0;
			g_U[ component.constraintCount() ] = 1.0;
		}
		
		return true;
	 }
	
	@Override
	public void addNormalizeConstraint()
	{
		usingNormalConstraint = true;
	}
}
