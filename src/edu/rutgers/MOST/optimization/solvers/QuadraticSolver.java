package edu.rutgers.MOST.optimization.solvers;

import java.util.ArrayList;

public interface QuadraticSolver
{
	public ArrayList< Double > minimizeEuclideanNorm( ArrayList< Double > objCoefs, Double objVal, SolverComponent component );
}
