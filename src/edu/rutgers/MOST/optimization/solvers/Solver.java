package edu.rutgers.MOST.optimization.solvers;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import org.coinor.Ipopt;

public abstract class Solver extends Ipopt 
{
	protected ObjType objType;
	protected VarType[] varTypes;
	protected Map<Integer,Double> obj = new HashMap<Integer, Double>();
	private Algorithm algorithm;
	
	public Solver( Algorithm algorithm )
	{
		this.setAlgorithm( algorithm );
	}
	public abstract String getName();
	public abstract ArrayList<Double> getSoln(); 
	
	public abstract void setVar(String varName,VarType types, double lb, double ub);
	// method is not used in fba
	//public abstract void setVars(VarType[] types, double[] lb, double[] ub);
	public abstract void setObjType(ObjType objType);
	public abstract void setObj(Map<Integer, Double>map);
	public abstract void addConstraint(Map<Integer, Double>map,ConType con,double value);
	public abstract double optimize();
	public abstract void setEnv(double timeLimit, int numThreads);
	public abstract void setVars(VarType[] types, double[] lb, double[] ub);
	public abstract void abort();
	public abstract void enable();
	
	public boolean abort;
	public abstract void setAbort(boolean abort);
	public Algorithm getAlgorithm()
	{
		return algorithm;
	}
	public void setAlgorithm( Algorithm algorithm )
	{
		this.algorithm = algorithm;
	}
	public int getJacobianNonZeros( int varCount, int constraintCount )
	{
		return varCount * constraintCount;
	}
	public int getHeissanNonZeros( int varCount )
	{
		return ( varCount * ( varCount + 1 ) ) / 2;
	}
}
