package edu.rutgers.MOST.optimization.solvers;

import java.util.ArrayList;
import java.util.Map;

import org.gnu.glpk.GLPK;

public class GLPKSolver extends Solver
{
	@Override
	public String getName()
	{
		return "GLPKSolver Version " + GLPK.glp_version();
	}
	@Override
	public ArrayList< Double > getSoln()
	{
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setVar( String varName, VarType types, double lb, double ub )
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setObjType( ObjType objType )
	{
		// TODO Auto-generated method stub

	}
	@Override
	public void setObj( Map< Integer, Double > map )
	{
		// TODO Auto-generated method stub

	}
	@Override
	public void addConstraint( Map< Integer, Double > map, ConType con,
			double value )
	{
		// TODO Auto-generated method stub

	}
	@Override
	public double optimize()
	{
		// TODO Auto-generated method stub
		return 0;
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
