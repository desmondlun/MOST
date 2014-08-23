package edu.rutgers.MOST.presentation;

public class IPoptParameters
{
	public static final String TITLE = "IPopt Parameters";
	public static final String GLPK_PARAMETERS_MENU_ITEM = "Set IPopt Properties";
	
	// source of names of parameters
	// http://www.coin-or.org/Ipopt/documentation/node41.html
	
	public static final String FEASIBILITYTOL_NAME = "Feasibility Tolerance";
	public static final double FEASIBILITYTOL_DEFAULT_VALUE = 1E-8;
	public static final double FEASIBILITYTOL_MIN_VALUE = 0.0;
	public static final double FEASIBILITYTOL_MAX_VALUE = Double.POSITIVE_INFINITY;
	
	public static final String MAXITER_NAME = "Maximum number of Iterations";
	public static final int MAXITER_DEFAULT_VALUE = 3000;
	public static final int MAXITER_MIN_VALUE = 0;
	public static final int MAXITER_MAX_VALUE = Integer.MAX_VALUE;
	
	public static final String DUALFEASABILITYTOL_NAME = "Dual Feasibility Tolerance";
	public static final double DUALFEASABILITYTOL_DEFAULT_VALUE = 1.0;
	public static final double DUALFEASABILITYTOL_MIN_VALUE = 0.0;
	public static final double DUALFEASABILITYTOL_MAX_VALUE = Double.POSITIVE_INFINITY;
	
	public static final String CONSTRAINTOL_NAME = "Constraint Tolerance";
	public static final double CONSTRAINTOL_DEFAULT_VALUE = 1E-4;
	public static final double CONSTRAINTOL_MIN_VALUE = 0.0;
	public static final double CONSTRAINTOL_MAX_VALUE = Double.POSITIVE_INFINITY;
	
	public static final String DIVITER_TOL_NAME = "Diverging Iterates Tolerance";
	public static final int DIVITER_TOL_DEFAULT_VALUE = (int)1E20;
	public static final int DIVITER_TOL_MIN_VALUE = 0;
	public static final int DIVITER_TOL_MAX_VALUE = Integer.MAX_VALUE;
	
}
