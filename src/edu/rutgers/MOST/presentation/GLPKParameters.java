package edu.rutgers.MOST.presentation;

public class GLPKParameters
{
	public static final String TITLE = "GLPK Parameters";
	public static final String GLPK_PARAMETERS_MENU_ITEM = "Set GLPK Properties";
	
	// source of names of parameters
	// http://www.maximalsoftware.com/solvopt/optglpk.html
	
	public static final String FEASIBILITYTOL_NAME = "Feasibility Tolerance";
	public static final double FEASIBILITYTOL_DEFAULT_VALUE = 1e-8;
	public static final double FEASIBILITYTOL_MINIMUM_VALUE = 0.0;
	public static final double FEASIBILITYTOL_MAXIMUM_VALUE = Double.MAX_VALUE;
	
	public static final String INTFEASIBILITYTOL_NAME = "Integer Feasibility Tolerance";
	public static final double INTFEASIBILITYTOL_DEFAULT_VALUE = 1.0E-6;
	public static final double INTFEASIBILITYTOL_MINIMUM_VALUE = 0.0;
	public static final double INTFEASIBILITYTOL_MAXIMUM_VALUE = Double.MAX_VALUE;
	
	public static final String DUALFEASIBILITYTOL_NAME = "Dual Feasability Tolerance";
	public static final double DUALFEASIBILITYTOL_DEFAULT_VALUE = 1E-8;
	public static final double DUALFEASIBILITYTOL_MIN_VALUE = 0.0;
	public static final double DUALFEASIBILITYTOL_MAX_VALUE = Double.MAX_VALUE;
	
	public static final String RELAXATION_NAME = "Relaxation";
	public static final double RELAXATION_DEFAULT_VALUE = 0.7;
	public static final double RELAXATION_MIN_VALUE = 0.0;
	public static final double RELAXATION_MAX_VALUE = Double.MAX_VALUE;
	
	public static final String RELOBJGAP_NAME = "Relative Objective Gap";
	public static final double RELOBJGAP_DEFAULT_VALUE = 1E-8;
	public static final double RELOBJGAP_MIN_VALUE = 0.0;
	public static final double RELOBJGAP_MAX_VALUE = Double.MAX_VALUE;
	
}
