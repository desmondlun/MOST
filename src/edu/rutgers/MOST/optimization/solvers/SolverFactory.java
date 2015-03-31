package edu.rutgers.MOST.optimization.solvers;

import edu.rutgers.MOST.presentation.GraphicalInterface;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;

public class SolverFactory
{	
	/**
	 * Create a solver capable of linear optimizations
	 * @param algorithm FBA or GDBB only
	 * @return A linear optimizer
	 * @see edu.rutgers.MOST.Analysis.FBA
	 */
	public static LinearSolver createFBASolver()
	{
		LinearSolver solver = null;
		switch( GraphicalInterface.getMixedIntegerLinearSolverName() )
		{
		case GraphicalInterfaceConstants.GLPK_SOLVER_NAME:
			solver = new LinearGLPKSolver();
			break;
		case GraphicalInterfaceConstants.GUROBI_SOLVER_NAME:
			solver = new LinearGurobiSolver();
			break;
		}
		return solver;
	}
	
	/**
	 * Create a solver capable of mixed-integer linear optimizations
	 * @param algorithm FBA or GDBB only
	 * @return A linear optimizer
	 * @see edu.rutgers.MOST.Analysis.FBA
	 * @see edu.rutgers.MOST.Analysis.GDBB
	 */
	public static MILSolver createGDBBSolver()
	{
		MILSolver solver = null;
		switch( GraphicalInterface.getMixedIntegerLinearSolverName() )
		{
		case GraphicalInterfaceConstants.GLPK_SOLVER_NAME:
			solver = new MILGLPKSolver();
			break;
		case GraphicalInterfaceConstants.GUROBI_SOLVER_NAME:
			solver = new MILGurobiSolver();
			break;
		}
		return solver;
	}
	
	/**
	 * Create a solver capable of quadratic optimizations
	 * @return A quadratic optimizer
	 * @see edu.rutgers.MOST.Analysis.Eflux2
	 */
	public static QuadraticSolver createQuadraticSolver()
	{
		QuadraticSolver solver = null;
		switch( GraphicalInterface.getQuadraticSolverName() )
		{
		case GraphicalInterfaceConstants.GUROBI_SOLVER_NAME:
			solver = new QuadraticGurobiSolver();
			break;
			
		case GraphicalInterfaceConstants.IPOPT_SOLVER_NAME:
			solver = new QuadraticIPoptSolver();
			break;			
		}
		return solver;
	}
	
	/**
	 * Create a solver capable of the nonlinear SPOTv1 optimization
	 * @return A nonlinear optimizer
	 */
	public static NonlinearSolver CreateSPOTv1Solver()
	{
		NonlinearSolver solver = null;
		switch( GraphicalInterface.getNonlinearSolverName() )
		{
		case GraphicalInterfaceConstants.IPOPT_SOLVER_NAME:
			solver = new SPOTv1IPoptSolver();
			break;
		}
		return solver;
	}
	
	/**
	 * Create a solver capable of the nonlinear SPOTv2 optimization
	 * @return A nonlinear optimizer
	 */
	public static NonlinearSolver CreateSPOTv2Solver()
	{
		NonlinearSolver solver = null;
		switch( GraphicalInterface.getNonlinearSolverName() )
		{
		case GraphicalInterfaceConstants.IPOPT_SOLVER_NAME:
			solver = new SPOTv2IPoptSolver();
			break;
		}
		return solver;
	}

	public static QuadraticSolver CreateSPOTv3Solver()
	{
		QuadraticSolver solver = null;
		switch( GraphicalInterface.getQuadraticSolverName() )
		{
		case GraphicalInterfaceConstants.GUROBI_SOLVER_NAME:
			solver = new QuadraticGurobiSolver();
			break;
			
		case GraphicalInterfaceConstants.IPOPT_SOLVER_NAME:
			solver = new QuadraticIPoptSolver();
			break;			
		}
		return solver;
	}
}
