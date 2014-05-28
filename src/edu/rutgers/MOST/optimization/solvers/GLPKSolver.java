package edu.rutgers.MOST.optimization.solvers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.ArrayUtils;
import org.gnu.glpk.GLPK;
import org.gnu.glpk.GLPKConstants;
import org.gnu.glpk.GlpkCallback;
import org.gnu.glpk.GlpkCallbackListener;
import org.gnu.glpk.SWIGTYPE_p_double;
import org.gnu.glpk.SWIGTYPE_p_int;
import org.gnu.glpk.glp_iocp;
import org.gnu.glpk.glp_prob;
import org.gnu.glpk.glp_tree;

import edu.rutgers.MOST.data.Solution;
import edu.rutgers.MOST.optimization.GDBB.GDBB;
import edu.rutgers.MOST.presentation.ResizableDialog;

public class GLPKSolver extends Solver implements GlpkCallbackListener
{
	private enum SolverKind
	{
		FBASolver, GDBBSolver
	}

	private class RowEntry
	{
		public int idx;
		public double value;

		RowEntry( int i, double v )
		{
			idx = i;
			value = v;
		}
	}

	private class RowType
	{
		public double val;
		public int type;
		public double lb;
		public double ub;
		public Vector< RowEntry > entries = new Vector< RowEntry >();

		RowType( double v, int t, double l, double u )
		{
			val = v;
			type = t;
			lb = l;
			ub = u;
		}
	}

	private class ColumnType
	{
		public String name;
		public int kind;
		public int type;
		public double lb;
		public double ub;

		ColumnType( String n, int k, int t, double l, double u )
		{
			name = n;
			kind = k;
			type = t;
			lb = l;
			ub = u;
		}
	}

	private class ObjectiveType
	{
		int dir;
		Vector< RowEntry > coefs = new Vector< RowEntry >();
	}

	private Vector< RowType > rows = new Vector< RowType >();
	private Vector< ColumnType > columns = new Vector< ColumnType >();
	private ObjectiveType objective = new ObjectiveType();
	private ArrayList< Double > soln = new ArrayList< Double >();
	private double objval;
	private glp_prob problem_tmp;
	private SolverKind solverKind = SolverKind.FBASolver;
	private ResizableDialog dialog = new ResizableDialog( "Error",
			"GLPK Solver Error", "GLPK Solver Error" );

	private static void addLibraryPath( String pathToAdd ) throws Exception
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
	private void processStackTrace( Exception except )
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
		Object[] options = { "    OK    " };
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
		// column definitions
		int kind;
		switch( types )
		{
		case INTEGER:
			solverKind = SolverKind.GDBBSolver;
			kind = GLPKConstants.GLP_IV;
			break;
		case BINARY:
			solverKind = SolverKind.GDBBSolver;
			kind = GLPKConstants.GLP_BV;
			break;
		default:
		case CONTINUOUS:
			kind = GLPKConstants.GLP_CV;
			break;
		}
		columns.add( new ColumnType( varName, kind,
				lb != ub ? GLPKConstants.GLP_DB : GLPKConstants.GLP_FX, lb, ub ) );

	}
	@Override
	public void setObjType( ObjType objType )
	{
		this.objType = objType;
		int dir = objType == ObjType.Minimize ? GLPKConstants.GLP_MIN
				: GLPKConstants.GLP_MAX;
		objective.dir = dir;
	}
	@Override
	public void setObj( Map< Integer, Double > map )
	{
		// objective definition
		for( Entry< Integer, Double > entry : map.entrySet())
			objective.coefs.add( new RowEntry( 1 + entry.getKey(), entry
					.getValue() ) );
	}
	@Override
	public void addConstraint( Map< Integer, Double > map, ConType con,
			double value )
	{
		// row definitions
		double lb = 0;
		double ub = 0;
		int type = 0;
		switch( con )
		{
		case LESS_EQUAL:
			ub = value;
			type = GLPKConstants.GLP_UP;
			break;
		case EQUAL:
			ub = lb = value;
			type = GLPKConstants.GLP_FX;
			break;
		case GREATER_EQUAL:
			type = GLPKConstants.GLP_LO;
			lb = value;
			break;
		}

		RowType row = new RowType( value, type, lb, ub );
		for( Entry< Integer, Double > entry : map.entrySet())
		{
			int key = entry.getKey();
			double kvalue = entry.getValue();
			row.entries.add( new RowEntry( key, kvalue ) );
		}
		rows.add( row );

	}
	@Override
	public double optimize()
	{
		// optimize the solution and return the objective value
		boolean terminalOutput = false;
		GLPK.glp_term_out( terminalOutput ? GLPKConstants.GLP_ON
				: GLPKConstants.GLP_OFF );

		// set up
		glp_prob problem = GLPK.glp_create_prob();
		GLPK.glp_set_prob_name( problem, "GLPK Problem" );
		problem_tmp = problem;

		// add columns
		for( ColumnType it : columns)
		{
			int colNum = GLPK.glp_add_cols( problem, 1 );
			GLPK.glp_set_col_name( problem, colNum, it.name );
			GLPK.glp_set_col_kind( problem, colNum, it.kind );
			GLPK.glp_set_col_bnds( problem, colNum, it.type, it.lb, it.ub );
		}
		columns.clear();

		// add rows
		for( RowType it : rows)
		{
			int rowNum = GLPK.glp_add_rows( problem, 1 );
			GLPK.glp_set_row_bnds( problem, rowNum, it.type, it.lb, it.ub );

			SWIGTYPE_p_int ind = GLPK.new_intArray( 1 + it.entries.size() );
			SWIGTYPE_p_double val = GLPK
					.new_doubleArray( 1 + it.entries.size() );
			int index = 1;
			for( RowEntry entry : it.entries)
			{
				GLPK.intArray_setitem( ind, index, 1 + entry.idx );
				GLPK.doubleArray_setitem( val, index, entry.value );
				++index;
			}

			GLPK.glp_set_mat_row( problem, rowNum, it.entries.size(), ind, val );
			GLPK.delete_intArray( ind );
			GLPK.delete_doubleArray( val );
		}
		rows.clear();

		// set the objective
		GLPK.glp_set_obj_dir( problem, objective.dir );
		for( RowEntry coef : objective.coefs)
			GLPK.glp_set_obj_coef( problem, coef.idx, coef.value );

		GlpkCallback.addListener( this );
		glp_iocp parm = new glp_iocp();
		GLPK.glp_init_iocp( parm );
		parm.setPresolve( GLPK.GLP_ON );
		parm.setTol_int( 1E-9 );
		parm.setTol_obj( 1E-9 );
		
		try
		{
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
		// TODO Auto-generated method stub
	}
	@Override
	public void setVars( VarType[] types, double[] lb, double[] ub )
	{
		// TODO Auto-generated method stub
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
	@Override
	public void callback( glp_tree tree )
	{
		int reason = GLPK.glp_ios_reason( tree );
		if( abort )
		{
			GLPK.glp_ios_terminate( tree );
		}
		else if( reason == GLPKConstants.GLP_IBINGO )
		{
			// get the solution columns
			soln.clear();
			int columnCount = GLPK.glp_get_num_cols( problem_tmp );
			for( int i = 1; i <= columnCount; ++i)
				// soln.add( GLPK.glp_get_col_prim( problem, i ) );
				soln.add( GLPK.glp_mip_col_val( problem_tmp, i ) );

			// objval = GLPK.glp_get_obj_val( problem );
			objval = GLPK.glp_mip_obj_val( problem_tmp );
			double[] darray = ArrayUtils.toPrimitive( soln
					.toArray( new Double[] {} ) );
			Solution sn = new Solution( objval, darray );
			if( solverKind == SolverKind.GDBBSolver )
				GDBB.intermediateSolution.add( sn );
		}
	}

}
