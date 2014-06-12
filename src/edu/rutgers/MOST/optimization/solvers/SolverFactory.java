package edu.rutgers.MOST.optimization.solvers;

import edu.rutgers.MOST.presentation.GraphicalInterface;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;

public class SolverFactory {
	
	public static Solver createSolver(){
		Solver solver = new GLPKSolver();
		if (GraphicalInterface.getSolverName().equals(GraphicalInterfaceConstants.GLPK_SOLVER_NAME)) {
			solver = new GLPKSolver();
		} else if (GraphicalInterface.getSolverName().equals(GraphicalInterfaceConstants.GUROBI_SOLVER_NAME)) {
			solver = new GurobiSolver();
		}
		//Solver solver = new GurobiSolver(config.getModelName() + dateTimeStamp + GraphicalInterfaceConstants.MIP_SUFFIX + ".log");
		return solver;
	}
}
