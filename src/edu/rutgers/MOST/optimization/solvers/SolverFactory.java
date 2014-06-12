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
	
	public static void main(String []args){
		Solver solver = SolverFactory.createSolver();
		
		VarType []varTypes = {VarType.BINARY,VarType.BINARY,VarType.BINARY};
		double  []lb       = {0.0, 0.0, 0.0};
		double  []ub       = {1.0, 1.0, 1.0};
		
		//solver.setVars(varTypes, lb, ub);
//		solver.setVar("0", VarType.BINARY, 0.0, 1.0);
//		solver.setVar("1", VarType.BINARY, 0.0, 1.0);
//		solver.setVar("2", VarType.BINARY, 0.0, 1.0);
		
		solver.setObjType(ObjType.Maximize);
		
		Map<Integer, Double> map = new HashMap<Integer, Double>();
		
		map.put(0, 1.0);
		map.put(1, 1.0);
		map.put(2, 2.0);
		
		solver.setObj(map);
		
		map.clear();
		map.put(0, 1.0);
		map.put(1, 2.0);
		map.put(2, 3.0);
		
		solver.addConstraint(map, ConType.LESS_EQUAL, 4);
		
		map.clear();
		map.put(0, 1.0);
		map.put(1, 1.0);
		
		solver.addConstraint(map, ConType.GREATER_EQUAL, 1);
		
		double obj = solver.optimize();
		
		//System.out.println(obj);
	}
}
