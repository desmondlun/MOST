package edu.rutgers.MOST.optimization.solvers;

import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

public interface Solver
{
	/**
	 * Get the Solver name
	 * @return The name of type of Solver
	 */
	public abstract String getName();
	
	/**
	 * 
	 * @return the Array of fluxes
	 */
	public abstract ArrayList<Double> getSoln(); 
	
	/**
	 * 
	 * Adds a new flux to the model
	 * @param varName (optional) name of the flux
	 * @param types The type of flux
	 * @param lb The Upper bound of the flux
	 * @param ub The Lower bound of the flux
	 */
	public abstract void setVar(String varName,VarType types, double lb, double ub);
	
	/**
	 * 
	 * @param objType The objective direction
	 */
	public abstract void setObjType(ObjType objType);
	
	/**
	 * 
	 * @param map The coefficient vector of the objective function
	 */
	public abstract void setObj(Map<Integer, Double>map);
	
	/**
	 * 
	 * @param map The coefficient vector of the new constraint
	 * @param con The constraint type of the new constraint
	 * @param value the constraint right-hand side of the new constraint
	 */
	public abstract void addConstraint(Map<Integer, Double>map,ConType con,double value);
	
	/**
	 * Optimize the objective
	 * @return The optimized objective value
	 * @throws Exception Exception is thrown if something goes wrong
	 */
	public abstract double optimize() throws Exception;
	
	/**
	 * (Gurobi only) This will be removed from the interface in time
	 * @param timeLimit The limit in seconds
	 * @param numThreads The maximum number of threads for optimization
	 */
	public abstract void setEnv(double timeLimit, int numThreads);
	
	/**
	 * 
	 * @param types
	 * @param lb
	 * @param ub
	 */
	public abstract void setVars(VarType[] types, double[] lb, double[] ub);
	
	/**
	 * 
	 * Abort the optimization
	 */
	public abstract void abort();
	
	/**
	 * Enable the optimization (only need to set after an abort)
	 */
	public abstract void enable();
	
	/**
	 * Set the abort
	 * @param abort Set true to abort
	 */
	public abstract void setAbort(boolean abort);
	
	/**
	 *  Set the vector of gene expression values for the fluxes.
	 *  The value of each index corresponds to the value of the same index in the flux vector
	 * @param geneExpr The gene expression vector
	 * @see edu.rutgers.MOST.Analysis.Eflux2
	 * @see edu.rutgers.MOST.Analysis.SPOT
	 */
	public abstract void setGeneExpr( Vector< Double > geneExpr );
}
