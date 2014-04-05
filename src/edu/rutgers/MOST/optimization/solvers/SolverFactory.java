package edu.rutgers.MOST.optimization.solvers;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.optimization.FBA.FBA;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;

public class SolverFactory {
	static Logger log = Logger.getLogger(SolverFactory.class);
	
	public static Solver createSolver(){
		log.debug("creating a solver");
		//Create the solver according to the global configuration
		LocalConfig config = LocalConfig.getInstance();
		Date date = new Date();
		Format formatter;
		formatter = new SimpleDateFormat("_yyMMdd_HHmmss");
		String dateTimeStamp = formatter.format(date);
		
		Solver solver = new GLPKSolver();
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
