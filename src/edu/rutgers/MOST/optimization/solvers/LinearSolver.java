package edu.rutgers.MOST.optimization.solvers;

import java.util.ArrayList;

public interface LinearSolver extends Solver
{
	/**
	 * Returns the SolverComponent
	 * @return The SolverComponent
	 * @see SolverComponent
	 */
	public abstract SolverComponent getSolverComponent();
	
	/**
	 * Returns the objective coefficient vector
	 * @return The objective coefficient vector
	 */
	public abstract ArrayList< Double > getObjectiveCoefs();
	
	/**
	 * Perform the FVA optimization associated with FBA using the same SolverComponent
	 * @param objCoefs Coefficient vector of the objective function
	 * @param objVal The value of the objective function
	 * @param min The minimum variability of the flux vector
	 * @param max The maximum variability of the flux vector
	 * @param component The solver component containing the constraints
	 */
	public void FVA( ArrayList< Double > objCoefs, Double objVal, ArrayList< Double > fbasoln, ArrayList< Double > min, ArrayList< Double > max, SolverComponent component ) throws Exception;

	/**
	 * Disable solver error messages such as infeasibility
	 */
	public abstract void disableErrors();
}
