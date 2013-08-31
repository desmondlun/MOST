package edu.rutgers.MOST.optimization.solvers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.presentation.GraphicalInterface;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;

import gurobi.*;

public class GurobiSolver extends Solver {

	static Logger log = Logger.getLogger(GurobiSolver.class);
	private GRBEnv env;
	private GRBModel model;
	private ArrayList<GRBVar> vars = new ArrayList<GRBVar>();
	private Callback callback;

	public GurobiSolver(String logName) {
		try {
			log.debug("creating Gurobi environment");
//			System.loadLibrary("GurobiJni50.dll");
			env = new GRBEnv(logName);
			log.debug("setting Gurobi parameters");
			env.set(GRB.IntParam.Presolve, 0);
			env.set(GRB.DoubleParam.FeasibilityTol, 1.0E-9);
			env.set(GRB.DoubleParam.IntFeasTol, 1.0E-9);
			env.set(GRB.IntParam.Threads, 1);
			env.set(GRB.IntParam.OutputFlag, 0);
			
			log.debug("creating Gurobi Model");
			model = new GRBModel(env);
			this.objType = ObjType.Minimize;

		} catch (Exception e) {
			GraphicalInterface.getTextInput().setVisible(false);
			LocalConfig.getInstance().hasValidGurobiKey = false;
			GraphicalInterface.outputTextArea.setText("ERROR: No validation file - run 'grbgetkey' to refresh it.");
			Object[] options = {"    OK    "};
			int choice = JOptionPane.showOptionDialog(null, 
					"ERROR: No validation file - run 'grbgetkey' to refresh it.", 
					GraphicalInterfaceConstants.GUROBI_KEY_ERROR_TITLE, 
					JOptionPane.YES_NO_OPTION, 
					JOptionPane.QUESTION_MESSAGE, 
					null, options, options[0]);
			if (choice == JOptionPane.YES_OPTION) {
				try{
					//Process p;
					//p = Runtime.getRuntime().exec("cmd /c start cmd");
					
				}catch(Exception e1){}

			}
            /*
			if (choice == JOptionPane.NO_OPTION) {

			}
			*/
			//log.error("Error code: " + e.getMessage() + ". "
					//+ e.getMessage());
		}
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "GurobiSovler";
	}

	@Override
	public void setVar(String varName, VarType types, double lb, double ub) {
		// TODO Auto-generated method stub
		if (varName != null && types != null) {
			try {
				GRBVar var = this.model.addVar(lb, ub, 0.0, getGRBVarType(types),
						varName);
//				System.out.println("adding var: lb = " + lb + " ub = " + ub +
//				 " type = " + types + " name = " + varName);
				this.vars.add(var);			
			} catch (GRBException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();		
			} catch (Throwable t) {
				
			}
			try {
				model.update();
			} catch (GRBException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (Throwable t) {
				
			}
		}		
	}
	
	public ArrayList<Double> getSoln() {
		ArrayList<Double> soln = new ArrayList<Double>(vars.size());
		if (this.vars.size() != 0) {
			for (int i = 0; i < this.vars.size(); i++) {
				try {
					soln.add(this.vars.get(i).get(GRB.DoubleAttr.X));
				} catch (GRBException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();				
				}
			}
		}
		return soln;
	}
	
	@Override
	public void setVars(VarType[] types, double[] lb, double[] ub) {
		// TODO Auto-generated method stub

		if (types.length == lb.length && lb.length == ub.length) {
			for (int i = 0; i < lb.length; i++) {
				try {
					GRBVar var = this.model.addVar(lb[i], ub[i], 0.0,
							getGRBVarType(types[i]), Integer.toString(i));
					// System.out.println("adding var: lb = " + lb[i] + "ub=" +
					// ub[i] + "type =" + types[i] + "name=" +
					// Integer.toString(i));
					this.vars.add(var);
				} catch (GRBException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
			try {

				model.update();
			} catch (GRBException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
	}

	@Override
	public void setObjType(ObjType objType) {
		// TODO Auto-generated method stub
		this.objType = objType;
	}

	@Override
	public void setObj(Map<Integer, Double> map) {
		// TODO Auto-generated method stub
		Set s = map.entrySet();
		Iterator it = s.iterator();
		GRBLinExpr expr = new GRBLinExpr();

		while (it.hasNext()) {
			Map.Entry m = (Map.Entry) it.next();
			int key = (Integer) m.getKey();
			Double value = (Double) m.getValue();
			GRBVar var = this.vars.get(key);
			expr.addTerm(value, var);
			//System.out.println("key = " + key + " value = " + value);
			//System.out.println("objType: " + this.objType);
		}

		try {
			//System.out.println(expr);
			model.setObjective(expr, getGRBObjType(this.objType));
			//DEGEN: Debugging to see model
			
			
		} catch (GRBException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (Throwable t) {
			
		}
	}

	@Override
	public void addConstraint(Map<Integer, Double> map, ConType con,
			double value) {
		// TODO Auto-generated method stub

		try {

			Set s = map.entrySet();
			Iterator it = s.iterator();
			GRBLinExpr expr = new GRBLinExpr();

			while (it.hasNext()) {
				Map.Entry m = (Map.Entry) it.next();
				int key = (Integer) m.getKey();
				Double v = (Double) m.getValue();
				expr.addTerm(v, this.vars.get(key));
				
			}
			model.addConstr(expr, getGRBConType(con), value, null);

		} catch (GRBException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}

	}

	public void finalize() {
		// Not guaranteed to be invoked
		this.model.dispose();
		try {
			this.env.dispose();
		} catch (GRBException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}

	}

	private char getGRBVarType(VarType type) {
		switch (type) {
		case CONTINUOUS:
			return GRB.CONTINUOUS;
		case BINARY:
			return GRB.BINARY;
		case INTEGER:
			return GRB.INTEGER;
		case SEMICONT:
			return GRB.SEMICONT;
		default:
			return GRB.SEMIINT;
		}
	}

	private int getGRBObjType(ObjType type) {
		switch (type) {
		case Minimize:
			return GRB.MINIMIZE;
		case Maximize:
			return GRB.MAXIMIZE;
		default:
			return GRB.MAXIMIZE;
		}
	}

	private char getGRBConType(ConType type) {
		switch (type) {
		case LESS_EQUAL:
			return GRB.LESS_EQUAL;
		case EQUAL:
			return GRB.EQUAL;
		case GREATER_EQUAL:
			return GRB.GREATER_EQUAL;
		default:
			return GRB.GREATER_EQUAL;
		}
	}

	@Override
	public double optimize() {
		try {
			
//			Callback logic
			GRBVar[] vars   = model.getVars();
			callback = new Callback(vars); 
			model.setCallback(callback);
			
			model.optimize();
//			model.write("model.lp");
//			model.write("model.mps");
		} catch (GRBException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();	
		} catch (Throwable t) {
			
		}
		
		try {
			return this.model.get(GRB.DoubleAttr.ObjVal);
		} catch (GRBException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (Throwable t) {
			
		}
		
		return 0;
	}

	public void setEnv(double timeLimit, int threadNum) {
		try {
			log.debug("setting Gurobi parameters");
			model.getEnv().set(GRB.DoubleParam.Heuristics, 1.0);
			model.getEnv().set(GRB.IntParam.MIPFocus, 1);
			model.getEnv().set(GRB.DoubleParam.ImproveStartGap, Double.POSITIVE_INFINITY);
			model.getEnv().set(GRB.DoubleParam.TimeLimit, timeLimit);
			model.getEnv().set(GRB.IntParam.Threads, threadNum);
		} catch (Exception e) {			
			log.error("Error code: " + e.getMessage() + ". "
					+ e.getMessage());
		}
	}
	
	public void abort() {
		Callback.setAbort(true);
	}

	@Override
	public void enable() {
		Callback.setAbort(false);
	}
}
