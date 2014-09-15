package edu.rutgers.MOST.optimization.solvers;

import java.util.ArrayList;

public interface QuadraticSolver
{
	/**
	 * Minimizes the ||v|| with the given constraints
	 * @param objCoefs Coefficient vector of the objective function
	 * @param objVal The value of the objective function
	 * @param component The solver component containing the constraints
	 * @return The optimized flux vector
	 */
	public ArrayList< Double > minimizeEuclideanNorm( ArrayList< Double > objCoefs, Double objVal, SolverComponent component ) throws Exception;
}
