package edu.rutgers.MOST.optimization.solvers;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.gnu.glpk.GLPK;
import org.gnu.glpk.GLPKConstants;
import org.gnu.glpk.SWIGTYPE_p_double;
import org.gnu.glpk.SWIGTYPE_p_int;
import org.gnu.glpk.glp_prob;


//we still need to delete the problem object at some point
public class GLPKSolver extends Solver
{
	private glp_prob problem = GLPK.glp_create_prob();
	
	public GLPKSolver()
	{
		GLPK.glp_set_prob_name( problem, "GLPK Problem" );
	}
	@Override
	public String getName()
	{
		return "GLPKSolver Version " + GLPK.glp_version();
	}
	@Override
	public ArrayList< Double > getSoln()
	{
		//return the column list
		ArrayList< Double > soln = new ArrayList< Double >();
		int columnCount = GLPK.glp_get_num_cols( problem );
		for( int i = 1; i < columnCount; ++i )
			 soln.add( GLPK.glp_get_col_prim( problem, i ) );
		return soln;
	}
	@Override
	public void setVar( String varName, VarType types, double lb, double ub )
	{
		//column definitions
		int kind;
		switch( types )
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
		
		int colNum = GLPK.glp_add_cols( problem, 1 );
		GLPK.glp_set_col_name( problem, colNum, varName );
		GLPK.glp_set_col_kind( problem, colNum, kind );
		GLPK.glp_set_col_bnds( problem, colNum, lb != ub ? GLPKConstants.GLP_DB : GLPKConstants.GLP_FX, lb, ub );
		
	}
	@Override
	public void setObjType( ObjType objType )
	{
		this.objType = objType;
		int dir = objType == ObjType.Minimize ? 
				GLPKConstants.GLP_MIN : GLPKConstants.GLP_MAX;
		GLPK.glp_set_obj_dir( problem, dir );
	}
	@Override
	public void setObj( Map< Integer, Double > map )
	{
		//objective definition
		for( Entry< Integer, Double > entry : map.entrySet() )
			GLPK.glp_set_obj_coef( problem, 1 + entry.getKey(), entry.getValue() );
	}
	@Override
	public void addConstraint( Map< Integer, Double > map, ConType con,
			double value )
	{
		//row definitions
		int rowNum = GLPK.glp_add_rows( problem, 1 );
		int colCount = GLPK.glp_get_num_cols( problem );
		switch( con )
		{
		case LESS_EQUAL:
			GLPK.glp_set_row_bnds( problem, rowNum, GLPKConstants.GLP_UP, 0, value );
			break;
		case EQUAL:
			GLPK.glp_set_row_bnds( problem, rowNum, GLPKConstants.GLP_FX, value, value );
			break;
		case GREATER_EQUAL:
			GLPK.glp_set_row_bnds( problem, rowNum, GLPKConstants.GLP_LO, value, 0 );
			break;
		}
		
		int rowLength = map.size();
		SWIGTYPE_p_int ind = GLPK.new_intArray( 1 + rowLength );
		SWIGTYPE_p_double val = GLPK.new_doubleArray( 1 + rowLength );

		int index = 1;
		for( Entry< Integer, Double > entry : map.entrySet() )
		{
			int key = entry.getKey();
			double kvalue = entry.getValue();
			GLPK.intArray_setitem( ind, index, 1 + key );
			GLPK.doubleArray_setitem( val, index, kvalue );
			++index;
		}
		
		GLPK.glp_set_mat_row( problem, rowNum, rowLength, ind, val );
		
		GLPK.delete_intArray( ind );
		GLPK.delete_doubleArray( val );
		
	}
	@Override
	public double optimize()
	{
		//optimize the solution and return the objective value
		boolean terminalOutput = true;
		GLPK.glp_term_out( terminalOutput? GLPKConstants.GLP_ON : GLPKConstants.GLP_OFF );
		int glpkres = GLPK.glp_simplex( problem, null );
		if( glpkres != 0 )
		{
			System.out.println( "The problem could not be solved" );
			return Double.NaN;
		}

		return GLPK.glp_get_obj_val( problem );
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
		// TODO Auto-generated method stub

	}
	@Override
	public void enable()
	{
		// TODO Auto-generated method stub

	}
	@Override
	public void setAbort( boolean abort )
	{
		// TODO Auto-generated method stub

	}
}
