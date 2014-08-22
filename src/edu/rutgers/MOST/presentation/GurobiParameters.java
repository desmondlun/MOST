package edu.rutgers.MOST.presentation;

public class GurobiParameters {

	// names of parameters from http://www.gurobi.com/documentation/5.6/reference-manual/parameters
	
	public static final String FEASIBILITYTOL_NAME = "Feasibility Tolerance";
	public static final double FEASIBILITYTOL_DEFAULT_VALUE = 1.0E-6;
	public static final double FEASIBILITYTOL_MINIMUM_VALUE = 1.0E-9;
	public static final double FEASIBILITYTOL_MAXIMUM_VALUE = 1.0E-2;
	
	public static final String INTFEASIBILITYTOL_NAME = "Integer Feasibility Tolerance";
	public static final double INTFEASIBILITYTOL_DEFAULT_VALUE = 1.0E-5;
	public static final double INTFEASIBILITYTOL_MINIMUM_VALUE = 1.0E-9;
	public static final double INTFEASIBILITYTOL_MAXIMUM_VALUE = 1.0E-1;
	
	public static final String OPTIMALITYTOL_NAME = "Dual Feasibility Tolerance";
	public static final double OPTIMALITYTOL_DEFAULT_VALUE = 1.0E-6;
	public static final double OPTIMALITYTOL_MINIMUM_VALUE = 1.0E-9;
	public static final double OPTIMALITYTOL_MAXIMUM_VALUE = 1.0E-2;
	
	public static final String HEURISTICS_NAME = "Heuristics";
	public static final double HEURISTICS_DEFAULT_VALUE = 0.05;
	public static final double HEURISTICS_MINIMUM_VALUE = 0;
	public static final double HEURISTICS_MAXIMUM_VALUE = 1;
	
	public static final String MIPFOCUS_NAME = "MIP Focus";
	public static final int MIPFOCUS_DEFAULT_VALUE = 0;
	public static final int MIPFOCUS_MINIMUM_VALUE = 0;
	public static final int MIPFOCUS_MAXIMUM_VALUE = 3;
	
	public static final String THREADS_NAME = "Number of Threads";
	public static final int MAX_NUM_THREADS = Runtime.getRuntime().availableProcessors();
	
}
