package edu.rutgers.MOST.Analysis;

import edu.rutgers.MOST.optimization.solvers.LinearSolver;
import edu.rutgers.MOST.optimization.solvers.Solver;
import edu.rutgers.MOST.optimization.solvers.SolverFactory;

public class FBA extends Analysis
{
	protected LinearSolver linearSolver = SolverFactory.createLinearSolver();
	
	public FBA()
	{
		super();
	}

	@Override
	public Solver getSolver()
	{
		return linearSolver;
	}
}
