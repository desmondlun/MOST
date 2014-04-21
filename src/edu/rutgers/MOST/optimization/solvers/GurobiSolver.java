package edu.rutgers.MOST.optimization.solvers;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

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
import gurobi.GRB;
import gurobi.GRBCallback;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;
import gurobi.GRBLinExpr;
import gurobi.GRB.DoubleAttr;
import gurobi.GRB.DoubleParam;
import gurobi.GRB.IntParam;

public class GurobiSolver extends Solver
{
	private enum SolverKind
	{
		FBASolver,
		GDBBSolver
	}
	
	private ArrayList< GRBVar > vars = new ArrayList< GRBVar >();
	private ArrayList< Double > soln = new ArrayList< Double >();
	private double objval;
	private GRBModel model;
	private GRBEnv env;
	private double obj;
	private ObjType objType;
	private SolverKind solverKind = SolverKind.FBASolver; //default may change in SetVar()
	
	private char getGRBVarType(VarType type)
	{
		switch( type )
		{
		case CONTINUOUS:
			return GRB.CONTINUOUS;
		case BINARY:
			return GRB.BINARY;
		case INTEGER:
			return GRB.INTEGER;
		case SEMICONT:
			return GRB.SEMICONT;
		case SEMIINT:
			return GRB.SEMIINT;
		default:
			return GRB.CONTINUOUS;
		}
	}
	private char getGRBConType( ConType type )
	{
		switch( type )
		{
		case LESS_EQUAL:
			return GRB.LESS_EQUAL;
		case EQUAL:
			return GRB.EQUAL;
		case GREATER_EQUAL:
			return GRB.GREATER_EQUAL;
		default:
			return GRB.LESS_EQUAL;
		}
	}
	private int getGRBObjType( ObjType objType )
	{
		switch( objType )
		{
		case Minimize:
			return GRB.MINIMIZE;
		case Maximize:
			return GRB.MAXIMIZE;
		default:
			return GRB.MINIMIZE;	
		}
	}
	
	public GurobiSolver()
	{
		try
		{
			env = new GRBEnv();
			env.set( GRB.DoubleParam.IntFeasTol, 1.0E-9 );
			env.set( GRB.DoubleParam.FeasibilityTol, 1.0E-9 );
			env.set( GRB.IntParam.OutputFlag, 0 );
			model = new GRBModel( env );
			model.setCallback( new GRBCallback(){
				@Override
				protected void callback()
				{
					try
					{
						if( abort )
							this.abort();
						else if( this.where == GRB.CB_SIMPLEX )
							objval = getDoubleInfo(GRB.CB_SPX_OBJVAL);
						else if( this.where == GRB.CB_MIPSOL )
						{
							GDBB.intermediateSolution.add( new Solution( 
									this.getDoubleInfo( GRB.CB_MIPSOL_OBJ ),
									this.getSolution( model.getVars() ) ) );
							objval = getDoubleInfo( GRB.CB_MIPSOL_OBJ );
						}
					}
					catch( GRBException e )
					{
						e.printStackTrace();
					}
				}
				
			});
		}
		catch( GRBException e )
		{
			e.printStackTrace();
		}

	}
	@Override
	public String getName()
	{
		return "GurobiSolver";
	}
	@Override
	public ArrayList< Double > getSoln()
	{
		//return the column list
		return soln;
	}
	@Override
	public void setVar( String varName, VarType types, double lb, double ub )
	{
		//column definitions
		try
		{
			if( varName == null || types == null || model == null)
				return;
			vars.add( model.addVar( lb, ub, 0.0, getGRBVarType( types ), varName ) );
			model.update();
		}
		catch ( GRBException e )
		{
			e.printStackTrace();
		}
		
	}
	@Override
	public void setObjType( ObjType objType )
	{
		this.objType = objType;
	}
	@Override
	public void setObj( Map< Integer, Double > map )
	{
		//objective definition	
		try
		{
			GRBLinExpr expr = new GRBLinExpr();
			
			for( Entry< Integer, Double > entry : map.entrySet() )
				expr.addTerm( entry.getValue(), vars.get( entry.getKey() ) );
			
			model.setObjective( expr, getGRBObjType( objType ) );
		}
		catch( GRBException e )
		{
			e.printStackTrace();
		}
		
	}
	@Override
	public void addConstraint( Map< Integer, Double > map, ConType con,
			double value )
	{
		//row definitions
		try
		{
			GRBLinExpr expr = new GRBLinExpr();
	
			for( Entry< Integer, Double > entry : map.entrySet() )
			{
				int key = entry.getKey();
				double kvalue = entry.getValue();
				expr.addTerm( kvalue, vars.get( key ) );
			}
			model.addConstr( expr, getGRBConType( con ), value, null );
		}
		catch( GRBException e )
		{
			e.printStackTrace();
		}
		
	}

	@Override
	public double optimize()
	{
		//optimize the solution and return the objective value
		
		try
		{			
			model.optimize();
			objval = model.get( GRB.DoubleAttr.ObjVal );
			
			//clean up
			model.dispose();
			env.dispose();
		}
		catch( GRBException e )
		{
			e.printStackTrace();
		}
		
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
}