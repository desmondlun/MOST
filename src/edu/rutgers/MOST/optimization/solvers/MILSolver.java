package edu.rutgers.MOST.optimization.solvers;

public interface MILSolver extends LinearSolver
{
	/**
	 * Returns the SolverComponent
	 * @return The SolverComponent
	 * @see SolverComponent
	 */
	public abstract SolverComponent getSolverComponent();
}
