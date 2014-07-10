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
	public abstract ArrayList< Double > getObjectiveCoefs();
}
