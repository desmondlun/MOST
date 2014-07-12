package edu.rutgers.MOST.optimization.solvers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import org.gnu.glpk.GLPK;
import org.gnu.glpk.GLPKConstants;
import org.gnu.glpk.GlpkCallback;
import org.gnu.glpk.GlpkCallbackListener;
import org.gnu.glpk.SWIGTYPE_p_double;
import org.gnu.glpk.SWIGTYPE_p_int;
import org.gnu.glpk.glp_iocp;
import org.gnu.glpk.glp_prob;
import org.gnu.glpk.glp_tree;

import edu.rutgers.MOST.presentation.ResizableDialog;

public abstract class GLPKSolver implements Solver, LinearSolver, MILSolver, GlpkCallbackListener
{
	protected class RowEntry
	{
		public int idx;
		public double value;
	
		RowEntry( int i, double v )
		{
			idx = i;
			value = v;
		}
	}

	protected class ObjectiveType
	{
		public int dir;
		public Vector< RowEntry > coefs = new Vector< RowEntry >();
	}
	
	protected SolverComponent component = new SolverComponentHeavyWeight();
	protected ObjectiveType objective = new ObjectiveType();
	protected ArrayList< Double > soln = new ArrayList< Double >();
	protected double objval;
	protected glp_prob problem_tmp;
	protected ResizableDialog dialog = new ResizableDialog( "Error",
			"GLPK Solver Error", "GLPK Solver Error" );
	private boolean abort = false;
	protected Vector< Double > geneExpr = new Vector< Double >();
	
	protected static void addLibraryPath( String pathToAdd ) throws Exception
	{
		final Field usrPathsField = ClassLoader.class
				.getDeclaredField( "usr_paths" );
		usrPathsField.setAccessible( true );
	
		// get array of paths
		final String[] paths = (String[])usrPathsField.get( null );
	
		// check if the path to add is already present
		for( String path : paths)
		{
			if( path.equals( pathToAdd ) )
			{
				return;
			}
		}
	
		// add the new path
		final String[] newPaths = Arrays.copyOf( paths, paths.length + 1 );
		newPaths[newPaths.length - 1] = pathToAdd;
		usrPathsField.set( null, newPaths );
	}
	protected void processStackTrace( Exception except )
	{
		//except.printStackTrace();
		StringWriter errors = new StringWriter();
		except.printStackTrace( new PrintWriter( errors ) );
		dialog.setErrorMessage( errors.toString() );
		// centers dialog
		dialog.setLocationRelativeTo(null);
		dialog.setModal(true);
		dialog.setVisible( true );
	}
	
	public GLPKSolver()
	{
		String dependsFolder = "lib/";
		if( System.getProperty( "os.name" ).toLowerCase().contains( "windows" ) )
		{
			dependsFolder += "win" + System.getProperty( "sun.arch.data.model" );
		}
		else if( System.getProperty( "os.name" ).toLowerCase()
				.contains( "mac os x" ) )
			dependsFolder += "mac";
		else
			dependsFolder += "linux";
	
		try
		{
			addLibraryPath( dependsFolder );
			@SuppressWarnings( "unused" )
			int x = GLPKConstants.GLP_JAVA_A_X;
		}
		catch ( UnsatisfiedLinkError | Exception except )
		{
			processStackTrace( new Exception(
					"The dynamic link library for GLPK 4.53 for Java could not be "
							+ "loaded from "
							+ Paths.get( dependsFolder ).toAbsolutePath()
									.toString() ) );
		}
	}
	@Override
	public String getName()
	{
		return "GLPKSolver Version " + GLPK.glp_version();
	}
	@Override
	public ArrayList< Double > getSoln()
	{
		// return the column list
		return soln;
	}
	@Override
	public void setVar( String varName, VarType types, double lb, double ub )
	{
		component.addVariable( types, lb, ub );
	}
	@Override
	public void setObjType( ObjType objType )
	{
		int dir = objType == ObjType.Minimize ? GLPKConstants.GLP_MIN
				: GLPKConstants.GLP_MAX;
		objective.dir = dir;
	}
	@Override
	public void setObj( Map< Integer, Double > map )
	{
		// objective definition
		for( Entry< Integer, Double > entry : map.entrySet())
			objective.coefs.add( new RowEntry( entry.getKey(), entry
					.getValue() ) );
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
		// optimize the solution and return the objective value
		glp_prob problem = null;
		try
		{
			boolean terminalOutput = false;
			GLPK.glp_term_out( terminalOutput ? GLPKConstants.GLP_ON
					: GLPKConstants.GLP_OFF );
		
			// set up
			problem = GLPK.glp_create_prob();
			GLPK.glp_set_prob_name( problem, "GLPK Problem" );
			problem_tmp = problem;
			
			// set the variables
			for( int j = 0; j < component.variableCount(); ++j )
			{
				Variable var = component.getVariable( j );

				int colNum = GLPK.glp_add_cols( problem,  1 );
				GLPK.glp_set_col_name( problem, colNum, null );
				int kind = 0;
				switch( var.type )
				{
				case INTEGER:
					kind = GLPKConstants.GLP_IV;
					break;
				case BINARY:
					kind = GLPKConstants.GLP_BV;
					break;
				default:
				case CONTINUOUS:
					kind = GLPKConstants.GLP_CV;
					break;
				}
				int type = -1;
				if( var.lb.equals( Double.NEGATIVE_INFINITY ) && var.ub.equals( Double.POSITIVE_INFINITY ) )
						type = GLPKConstants.GLP_FR;
				else if( var.lb.equals( Double.NEGATIVE_INFINITY ) && !var.ub.equals( Double.POSITIVE_INFINITY ) )
					type = GLPKConstants.GLP_UP;
				else if( !var.lb.equals( Double.NEGATIVE_INFINITY ) && var.ub.equals( Double.POSITIVE_INFINITY ) )
					type = GLPKConstants.GLP_LO;
				else if( !var.lb.isInfinite() && !var.ub.isInfinite() && Double.compare( var.lb, var.ub ) < 0 )
					type = GLPKConstants.GLP_DB;
				else if( var.lb.equals( var.ub ) && !var.lb.equals( Double.NEGATIVE_INFINITY ) && !var.lb.equals( Double.POSITIVE_INFINITY ) )
					type = GLPKConstants.GLP_FX;
				else
					throw new Exception( "Invalid variable bounds are set" );
				GLPK.glp_set_col_kind( problem, colNum, kind );
				GLPK.glp_set_col_bnds( problem, colNum, type, var.lb, var.ub );
			}
			
			// set the constraints
			for( int i = 0; i < component.constraintCount(); ++i )
			{
				Constraint constraint = component.getConstraint( i );
				
				int rowNum = GLPK.glp_add_rows( problem, 1 );
				int type = 0;
				double lb = Double.NEGATIVE_INFINITY;
				double ub = Double.POSITIVE_INFINITY;
				switch( constraint.type )
				{
				case LESS_EQUAL:
					ub = constraint.value;
					type = GLPKConstants.GLP_UP;
					break;
				case EQUAL:
					ub = lb = constraint.value;
					type = GLPKConstants.GLP_FX;
					break;
				case GREATER_EQUAL:
					type = GLPKConstants.GLP_LO;
					lb = constraint.value;
					break;
				}
				
				SWIGTYPE_p_int ind = GLPK.new_intArray( 1 + component.variableCount() );
				SWIGTYPE_p_double val = GLPK.new_doubleArray( 1 + component.variableCount() );
				
				for( int j = 0; j < component.variableCount(); ++j )
				{
					GLPK.intArray_setitem( ind, j+1, j+1 );
					GLPK.doubleArray_setitem( val, j+1, constraint.getCoefficient( j ) );
				}
		
				if( type == GLPKConstants.GLP_FX && lb != ub )
					System.out.println( "Here! at Constraints setup!" );
				GLPK.glp_set_row_bnds( problem, rowNum, type, lb, ub );
				GLPK.glp_set_mat_row( problem, rowNum, component.variableCount(), ind, val );
				GLPK.delete_intArray( ind );
				GLPK.delete_doubleArray( val );
			}
		
			
			// set the objective
			GLPK.glp_set_obj_dir( problem, objective.dir );
			for( RowEntry coef : objective.coefs )
				GLPK.glp_set_obj_coef( problem, 1 + coef.idx, coef.value );
		
			GlpkCallback.addListener( this );
			glp_iocp parm = new glp_iocp();
			GLPK.glp_init_iocp( parm );
			parm.setPresolve( GLPK.GLP_ON );
			parm.setTol_int( 1E-9 );
			parm.setTol_obj( 1E-9 );
		
		
			/* int optres = */// GLPK.glp_simplex( problem, null );
			int optres = GLPK.glp_intopt( problem, parm );
				
			if( optres != 0 )
			{
				objval = Double.NaN;
				
				if( optres == GLPKConstants.GLP_EBOUND )
					throw new Exception( "Double-bounded variables are set to invalid bounds" );
				else if( optres == GLPKConstants.GLP_EROOT )
					throw new Exception( "optimal basis for initial LP relaxation is not provided" );
				else if( optres == GLPKConstants.GLP_ENOPFS )
					throw new Exception( "LP relaxation of MIP problem has no primal feasable solution" );
				else if( optres == GLPKConstants.GLP_ENODFS )
					throw new Exception( "LP relaxation of the MIP problem instance has no dual feasible solution" );
				else if( optres == GLPKConstants.GLP_EFAIL )
					throw new Exception( "The search was prematurely terminated due to the solver failure." );
				else if( optres == GLPKConstants.GLP_EMIPGAP )
					throw new Exception( "The relative mip gap tolerance has been reached." );
				else if( optres == GLPKConstants.GLP_ETMLIM )
					throw new Exception( "The time limit has been exceeded" );
				else if( optres == GLPKConstants.GLP_ESTOP ); //terminated by application, no real exception thrown
			}
		}
		catch( Exception except )
		{
			except.printStackTrace();
			processStackTrace( except );
		}
	
		// clean up
		GlpkCallback.removeListener( this );
		GLPK.glp_delete_prob( problem );
		problem_tmp = null;
	
		return objval;
	}
	@Override
	public void setEnv( double timeLimit, int numThreads )
	{
	}
	@Override
	public void setVars( VarType[] types, double[] lb, double[] ub )
	{
		if( types.length == lb.length && lb.length == ub.length )
		{
			for( int j = 0; j < types.length; ++ j )
				component.addVariable( types[ j ], lb[ j ], ub[ j ] );
		}
	}
	@Override
	public void abort()
	{
		abort = true;
	}
	@Override
	public void enable()
	{
		abort = false;
	}
	@Override
	public void setAbort( boolean abort )
	{
		this.abort = abort;
	}
	public boolean aborted()
	{
		return this.abort;
	}
	@Override
	public abstract void callback( glp_tree tree );
	@Override
	public void setGeneExpr( Vector< Double > geneExpr )
	{
		this.geneExpr = geneExpr;
	}
	public SolverComponent getSolverComponent()
	{
		return component;
	}
	public ArrayList< Double > getObjectiveCoefs()
	{
		ArrayList< Double > objCoefs = new ArrayList< Double >();
		for( int j = 0; j < component.variableCount(); ++j )
			objCoefs.add( new Double( 0.0 ) );
		
		for( RowEntry coef : objective.coefs )
			objCoefs.set( coef.idx, coef.value );
		
		return objCoefs;
	}
}
