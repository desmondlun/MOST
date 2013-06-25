package edu.rutgers.MOST.optimization.solvers;

import edu.rutgers.MOST.data.Solution;
import edu.rutgers.MOST.optimization.GDBB.GDBB;

import gurobi.*;

public class Callback extends GRBCallback {
	
	private GRBVar[] vars;
	private static boolean isAbort = false;

	public static void setAbort(boolean isAbort) {
		Callback.isAbort = isAbort;
	}

	public Callback(GRBVar[] xvars) {
		vars = xvars;
	}
	
	protected void callback() {
		if (isAbort) {
			abort();
		}
		try {
			if (where == GRB.CB_POLLING) {
			    /* Ignore polling callback */
			}
			else if (where == GRB.CB_MIP) {
				/* Do nothing */
			}
			else if (where == GRB.CB_MIPSOL) {
				GDBB.intermediateSolution.add(new Solution(getDoubleInfo(GRB.CB_MIPSOL_OBJ), getSolution(vars)));
			}
		}
		catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
			          e.getMessage());
			      e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
	}
	
}
