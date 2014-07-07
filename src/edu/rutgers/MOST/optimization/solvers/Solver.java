package edu.rutgers.MOST.optimization.solvers;

import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

public interface Solver
{
	public abstract String getName();
	public abstract ArrayList<Double> getSoln(); 
	
	public abstract void setVar(String varName,VarType types, double lb, double ub);
	public abstract void setObjType(ObjType objType);
	public abstract void setObj(Map<Integer, Double>map);
	public abstract void addConstraint(Map<Integer, Double>map,ConType con,double value);
	public abstract double optimize();
	public abstract void setEnv(double timeLimit, int numThreads);
	public abstract void setVars(VarType[] types, double[] lb, double[] ub);
	public abstract void abort();
	public abstract void enable();
	public abstract void setAbort(boolean abort);
	public abstract void setGeneExpr( Vector< Double > geneExpr );
}
