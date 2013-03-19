package edu.rutgers.MOST.optimization.GDBB;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import edu.rutgers.MOST.data.*;
import edu.rutgers.MOST.optimization.solvers.*;


public class GDBB extends Thread {

	static Logger log = Logger.getLogger(GDBB.class);
	
	private GDBBModel model;
	private static Solver solver;
	private Vector<String> varNames;
	private double maxObj;
	
	public static ArrayList<Double> objIntermediate;
	public static ArrayList<double[]> knockoutVectors;
	
	/*	Ma:
	 *	[I(n, n) 0(n, n) 0(n, n) 0(n, n) 0(n, m) ad.G']
	 *	[0(n, n) 0(n, n) 0(n, n) I(n, n) 0(n, m) D.G' ]
	 */
	private ArrayList<Map<Integer, Double>> Ma;
	
	/*
	 * 	Mb:
	 *	[I(n, n) 0(n, n) 0(n, n) I(n, n) 0(n, m) bd.G'  ]
	 *	[0(n, n) 0(n, n) 0(n, n) I(n, n) 0(n, n) -D.G'	]
	 *	[0(n, n) 0(n, n) 0(n, n) 0(n, n) 0(n, m) 1(1, L)]
	 */
	private ArrayList<Map<Integer, Double>> Mb;
	
	/*
	 * 	E:
	 *	[S(m, n)   0(m, n)   0(m, n)  0(m, n)  0(m, n)  0(m, L)]
	 *	[0(n, n)  -I(n, n)   I(n, n)  I(n, n)  S'(n, m) 0(n, L)]
	 *	[f'(1, n)  a'(1, n) -b'(1, n) 0(1, n)  0(1, m)  0(1, L)]
	 */
	private ArrayList<Map<Integer, Double>> E;

	private ArrayList<Map<Integer, Double>> sMatrix;

	private int reactions_2;
	private int reactions_3;
	private int reactions_4;
	private int reactions_4_sMatrix;
	private int reactions_4_sMatrix_gprMatrix;

	private ArrayList<Double> solution;

	public GDBB() {
		GDBB.setSolver(SolverFactory.createSolver());
		this.varNames = new Vector<String>();
		objIntermediate = new ArrayList<Double>();
		knockoutVectors = new ArrayList<double[]>();
	}

	public GDBB(GDBBModel m) {
		this.model = m;
		GDBB.setSolver(SolverFactory.createSolver());
		this.varNames = new Vector<String>();
	}

	private void setVars() {
		Vector<ModelReaction> reactions = this.model.getReactions();
		
		sMatrix = this.model.getSMatrix();
		String varName;
		//	For v
		for (int i = 0; i < reactions.size(); i++) {
			SBMLReaction reac = (SBMLReaction) (reactions.elementAt(i));
			varName = Integer.toString(reac.getId());			
			GDBB.getSolver().setVar(varName, VarType.CONTINUOUS, -999999.0, 999999.0);
			this.varNames.add(varName);
		}
		
		reactions_3 = 3*reactions.size();
		for (int i = reactions.size(); i < reactions_3; i++) {
			varName = Integer.toString(i);			
			GDBB.getSolver().setVar(varName, VarType.CONTINUOUS, 0.0, 999999.0);
			this.varNames.add(varName);
		}
		
		reactions_4_sMatrix = reactions_3 + reactions.size() + sMatrix.size();
		for (int i = reactions_3; i < reactions_4_sMatrix; i++) {
			varName = Integer.toString(i);
			GDBB.getSolver().setVar(varName, VarType.CONTINUOUS, -999999.0, 999999.0);
			this.varNames.add(varName);
		}
		
		reactions_4_sMatrix_gprMatrix = reactions_4_sMatrix + this.model.getGprMatrix().size();
		for (int i = reactions_4_sMatrix; i < reactions_4_sMatrix_gprMatrix; i++) {
			varName = Integer.toString(i);		
			GDBB.getSolver().setVar(varName, VarType.BINARY, 0, 1);
			this.varNames.add(varName);
		}
	}
	
	private void setConstraints() {
		Vector<ModelReaction> reactions = this.model.getReactions();
		setConstraints(reactions,ConType.EQUAL,0.0);
	}	
	
	private void setConstraints(Vector<ModelReaction> reactions, ConType conType, double bValue) {
		
		int rowsE = sMatrix.size() + reactions.size() + 1;	//	m + n + 1
		E = new ArrayList<Map<Integer, Double>>(rowsE);
		
		for (int i = 0; i < rowsE; i++) {
			Map<Integer, Double> eRow = new HashMap<Integer, Double>();
			E.add(eRow);
		}
		
		//	E <- S 0 0 0 0 0
		for(int i = 0; i < sMatrix.size(); i++)	{
			for(int j = 0; j < reactions.size(); j++) {
				if(sMatrix.get(i).get(j) != null) {
					E.get(i).put(j, sMatrix.get(i).get(j).doubleValue());
				}
			}
		}
		
		reactions_2 = 2*reactions.size();
		reactions_4 = reactions_3 + reactions.size();
		
		int rowsE_1 = rowsE - 1;
		
		Vector<Double> objective = this.model.getObjective();
		for(int i = 0; i < reactions.size(); i++)	{
			int i_sMatrix = i + sMatrix.size();
			E.get(i_sMatrix).put(i + reactions.size(), -1.0);
			E.get(i_sMatrix).put(i + reactions_2, 1.0);
			E.get(i_sMatrix).put(i + reactions_3, 1.0);
		
			//	Blocking can be used
			for(int j = 0; j < sMatrix.size(); j++) {
				if(sMatrix.get(j).get(i) != null) {
					E.get(i_sMatrix).put(j + reactions_4, sMatrix.get(j).get(i).doubleValue());
				}
			}
			
//			System.out.println("GDBB: " + model);
			//	E <- S   0  0  0 0  0
			//		 0  -I  I  I S' 0
			//		 f' a' -b' 0 0  0
			if (objective.size() != 0) {
				E.get(rowsE_1).put(i, objective.get(i).doubleValue());
			}
			
			SBMLReaction reac = (SBMLReaction) (reactions.elementAt(i));
			E.get(rowsE_1).put(i + reactions.size(), reac.getLowerBound());
			
			E.get(rowsE_1).put(i + reactions_2, -reac.getUpperBound());
		}
		
		//	Ad*G'
		ArrayList<Map<Integer, Double>> Gad = new ArrayList<Map<Integer, Double>>();
		for (int i = 0; i < reactions.size(); i++) {
			Map<Integer, Double> gadRow = new HashMap<Integer, Double>();
			Gad.add(gadRow);
		}
		
		//	Blocking can be used
		ArrayList<Map<Integer, Double>> gprMatrix = this.model.getGprMatrix();
		for (int i = 0; i < reactions.size(); i++) {
			SBMLReaction reac = (SBMLReaction) (reactions.elementAt(i));
			for (int j = 0; j < gprMatrix.size(); j++) {
				if(gprMatrix.get(j).get(i) != null) {
					Gad.get(i).put(j, reac.getLowerBound()*gprMatrix.get(j).get(i).doubleValue());
				}
			}
		}
		
		//	Constructing Ma
		Ma = new ArrayList<Map<Integer, Double>>();
		
		for (int i = 0; i < reactions_2; i++) {
			Map<Integer, Double> maRow = new HashMap<Integer, Double>();
			Ma.add(maRow);
		}
		
		for(int i = 0; i < reactions.size(); i++)	{
			Ma.get(i).put(i, 1.0);
		
			//	Blocking can be used
			for(int j = 0; j < gprMatrix.size(); j++) {
				if(Gad.get(i).get(j) != null) {
					Ma.get(i).put(j + reactions_4_sMatrix, Gad.get(i).get(j).doubleValue());
				}
				
				if(gprMatrix.get(j).get(i) != null) {
					Ma.get(i + reactions.size()).put(j + reactions_4_sMatrix, this.model.getD()*gprMatrix.get(j).get(i).doubleValue());
				}
			}
			
			Ma.get(i + reactions.size()).put(i + reactions_3, 1.0);
		}
		
		//	Bd*G'
		ArrayList<Map<Integer, Double>> Gbd = new ArrayList<Map<Integer, Double>>();
		for (int i = 0; i < reactions.size(); i++) {
			Map<Integer, Double> gbdRow = new HashMap<Integer, Double>();
			Gbd.add(gbdRow);
		}

		for (int i = 0; i < reactions.size(); i++) {
			SBMLReaction reac = (SBMLReaction) (reactions.elementAt(i));
			for (int j = 0; j < gprMatrix.size(); j++) {
				if(gprMatrix.get(j).get(i) != null) {
					Gbd.get(i).put(j, reac.getUpperBound()*gprMatrix.get(j).get(i).doubleValue());
				}
			}
		}
		
		Mb = new ArrayList<Map<Integer, Double>>();
		
		int reactions_2_1 = reactions_2 + 1;
		for (int i = 0; i < reactions_2_1; i++) {
			Map<Integer, Double> maRow = new HashMap<Integer, Double>();
			Mb.add(maRow);
		}
		
		for(int i = 0; i < reactions.size(); i++)	{
			Mb.get(i).put(i, 1.0);
		
			for(int j = 0; j < gprMatrix.size(); j++) {
				if(Gbd.get(i).get(j) != null) {
					Mb.get(i).put(j + reactions_4_sMatrix, Gbd.get(i).get(j).doubleValue());
				}
				
				if(gprMatrix.get(j).get(i) != null) {
					Mb.get(i + reactions.size()).put(j + reactions_4_sMatrix, -this.model.getD()*gprMatrix.get(j).get(i).doubleValue());
				}
			}
			
			Mb.get(i + reactions.size()).put(i + reactions_3, 1.0);
		}
		
		for (int i = reactions_4_sMatrix; i < reactions_4_sMatrix_gprMatrix; i++) {
			Mb.get(reactions_2).put(i, 1.0);
		}
		
//		System.out.println("Mb:");
//		System.out.println(Mb);
		
		for (int i = 0; i < sMatrix.size(); i++) {
			GDBB.getSolver().addConstraint(E.get(i), conType, bValue);
		}
		
		if (objective.size() != 0) {
			for (int i = 0; i < reactions.size(); i++) {
				GDBB.getSolver().addConstraint(E.get(i + sMatrix.size()), conType, this.model.getObjective().get(i).doubleValue());
			}
		}
		else {
			for (int i = 0; i < reactions.size(); i++) {
				GDBB.getSolver().addConstraint(E.get(i + sMatrix.size()), conType, 0.0);
			}
		}
		
		GDBB.getSolver().addConstraint(E.get(rowsE_1), conType, bValue);
		
		for (int i = 0; i < reactions.size(); i++) {
			SBMLReaction reac = (SBMLReaction) (reactions.elementAt(i));
			GDBB.getSolver().addConstraint(Ma.get(i), ConType.GREATER_EQUAL, reac.getLowerBound());
		}
		
		for (int i = reactions.size(); i < reactions_2; i++) {
			GDBB.getSolver().addConstraint(Ma.get(i), ConType.GREATER_EQUAL, bValue);
		}
		
		for (int i = 0; i < reactions.size(); i++) {
			SBMLReaction reac = (SBMLReaction) (reactions.elementAt(i));
			GDBB.getSolver().addConstraint(Mb.get(i), ConType.LESS_EQUAL, reac.getUpperBound());
		}
		
		for (int i = reactions.size(); i < reactions_2; i++) {
			GDBB.getSolver().addConstraint(Mb.get(i), ConType.LESS_EQUAL, bValue);
		}
		
		GDBB.getSolver().addConstraint(Mb.get(reactions_2), ConType.LESS_EQUAL, this.model.getC());
	}
	
	//	Setting Synthetic Objective Function
	private void setSyntheticObjective() {
		GDBB.getSolver().setObjType(ObjType.Maximize);
		Vector<Double> objective = this.model.getSyntheticObjective();

		Map<Integer, Double> map = new HashMap<Integer, Double>();
		for (int i = 0; i < objective.size(); i++) {
			if (objective.elementAt(i) != 0.0) {
				map.put(i, objective.elementAt(i));
			}
		}
		GDBB.getSolver().setObj(map);
		
	}

	public void setGDBBModel(GDBBModel m) {
		this.model = m;
	}

//	public ArrayList<Double> run() {
	public void run() {
		log.debug("setEnv");
		this.setEnv(model.getTimeLimit(), model.getThreadNum());
		log.debug("Set Vars");
		this.setVars();
		log.debug("setConstraints");
		this.setConstraints();
//		log.debug("setObjective");
//		this.setObjective();
		log.debug("setSyntheticObjective");
		this.setSyntheticObjective();
		log.debug("optimize");
		this.maxObj = GDBB.getSolver().optimize();
		
//		return GDBB.getSolver().getSoln();
		solution = GDBB.getSolver().getSoln();
	}

	public void setTimeLimit(double timeLimit) {
	}

	private void setEnv(double timeLimit, int threadNum) {
		GDBB.getSolver().setEnv(timeLimit, threadNum);
	}

	public double getMaxObj() {
		return this.maxObj;
	}

	public static void main(String[] argv) {
		String databaseName = "Ec_iAF1260_anaerobic_glc10_acetate";
//		String databaseName = "Ec_core_flux1";
				
		GDBB gdbb = new GDBB();
		
		GDBBModel model = new GDBBModel(databaseName);
		gdbb.setGDBBModel(model);
		 
//		ArrayList<Double> variables = gdbb.run();
		gdbb.start();
		
		ArrayList<Double> variables = gdbb.getSolution();
		
		try {
			System.out.println("Main Thread");
			gdbb.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Max objective: " + gdbb.getMaxObj());
		System.out.println("Variables:");
		System.out.println(variables);
	}
	
	public void stopGDBB() {
		GDBB.getSolver().abort();
	}
	
	public ArrayList<Double> getSolution() {
		return solution;
	}
	
	public static Solver getSolver() {
		return solver;
	}

	public static void setSolver(Solver solver) {
		GDBB.solver = solver;
	}

	public void enableGDBB() {
		GDBB.getSolver().enable();
	}
}