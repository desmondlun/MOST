package edu.rutgers.MOST.optimization.solvers;

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
		try {
			if (where == GRB.CB_POLLING) {
			    /* Ignore polling callback */
			}
			else if (where == GRB.CB_MIP) {
				/* Do nothing */
			}
			else if (where == GRB.CB_MIPSOL) {
//				double obj = getDoubleInfo(GRB.CB_MIPSOL_OBJ);
//				int nodecnt = (int) getDoubleInfo(GRB.CB_MIPSOL_NODCNT);
//				int solcnt = getIntInfo(GRB.CB_MIPSOL_SOLCNT);
//				double[] x = getSolution(vars);
				
//				double objBst = getDoubleInfo(GRB.CB_MIPSOL_OBJBST); 
//				System.out.println("**** New solution at node " + nodecnt + ", obj " + obj + ", sol " + solcnt + ", objBst " + objBst + " ****");
				
//				for (int i = 0; i < x.length; i++) {
//					System.out.print(" " + x[i]);
//				}
				
				GDBB.objIntermediate.add(getDoubleInfo(GRB.CB_MIPSOL_OBJ));
				GDBB.knockoutVectors.add(getSolution(vars));
			}
			if (isAbort) {
				abort();
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

//	public void callbackAbort() {
//		isAbort = true;
//	}
	
}
