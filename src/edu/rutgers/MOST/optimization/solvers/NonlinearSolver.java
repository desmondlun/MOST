package edu.rutgers.MOST.optimization.solvers;

import java.util.ArrayList;

public interface NonlinearSolver extends Solver
{
	/**
	 * Set the solver component to the source
	 * @param source The source component
	 */
	abstract void setSolverComponent( SolverComponent source );
	
	/**
	 * Optimize starting at x0
	 * @param x0 The starting point
	 * @return The optimized values
	 */
	abstract double optimize( ArrayList< Double > x0 );
}
