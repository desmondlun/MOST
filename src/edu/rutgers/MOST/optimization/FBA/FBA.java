package edu.rutgers.MOST.optimization.FBA;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import au.com.bytecode.opencsv.CSVReader;
import edu.rutgers.MOST.data.*;
import edu.rutgers.MOST.optimization.solvers.*;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;

public class FBA {
	
	private FBAModel model;
	private static Solver solver;
	private Vector<String> varNames;
	private double maxObj;

	public FBA() {
		FBA.setSolver(SolverFactory.createSolver());
		this.varNames = new Vector<String>();
	}

	public FBA(FBAModel m) {
		this.model = m;
		FBA.setSolver(SolverFactory.createSolver());
		this.varNames = new Vector<String>();
	}

	private void setVars() {
		Vector< SBMLReaction > reactions = this.model.getReactions();
		for (int i = 0; i < reactions.size(); i++) {
			SBMLReaction reac = (SBMLReaction) (reactions.elementAt(i));
			String varName = Integer.toString((Integer)this.model.getReactionsIdPositionMap().get(reac.getId()));
			//String varName = Integer.toString(reac.getId());
			double lb = reac.getLowerBound();
			double ub = reac.getUpperBound();
			
			if (reac.getKnockout().equals(GraphicalInterfaceConstants.BOOLEAN_VALUES[1])) {
				lb = 0;
				ub = 0;
			}

			FBA.getSolver().setVar(varName, VarType.CONTINUOUS, lb, ub);

			this.varNames.add(varName);
		}
	}
	
	private void setConstraints() {
		Vector< SBMLReaction > reactions = this.model.getReactions();
		setConstraints(reactions,ConType.EQUAL,0.0);
	}	
	
	private void setConstraints(Vector< SBMLReaction > reactions, ConType conType, double bValue) {
		ArrayList<Map<Integer, Double>> sMatrix = this.model.getSMatrix();
		for (int i = 0; i < sMatrix.size(); i++) {
			FBA.getSolver().addConstraint(sMatrix.get(i), conType, bValue);
		}
	}

	private void setObjective() {
		FBA.getSolver().setObjType(ObjType.Maximize);
		Vector<Double> objective = this.model.getObjective();
		Map<Integer, Double> map = new HashMap<Integer, Double>();
		for (int i = 0; i < objective.size(); i++) {
			if (objective.elementAt(i) != 0.0) {
				map.put(i, objective.elementAt(i));
			}
		}
		FBA.getSolver().setObj(map);
	}

	public void setFBAModel(FBAModel m) {
		this.model = m;
	}
	
	public void formatFluxBoundsfromTransciptomicData( File file )
	{
		if( file == null || !file.exists() )
			return;
		try
		{
			CSVReader csvReader = new CSVReader( new FileReader( file ) );
			List< String[] > all = csvReader.readAll();
			csvReader.close();
			Map< String, Double > expressionLevels = new HashMap< String, Double >();
			for( String[] keyval : all )
				expressionLevels.put( keyval[ 0 ], Double.valueOf( keyval[ 1 ] ) );
			model.formatFluxBoundsfromTransciptomicData( expressionLevels );
		}
		catch ( Exception e )
		{
		}
	}

	public ArrayList<Double> run() {
		this.setVars();
		this.setConstraints();
		this.setObjective();
		this.maxObj = FBA.getSolver().optimize();
		
		return FBA.getSolver().getSoln();
	}

	public double getMaxObj() {
		return this.maxObj;
	}

	public static void main(String[] argv) {
		
	}
	
	public static Solver getSolver() {
		return solver;
	}

	public static void setSolver(Solver solver) {
		FBA.solver = solver;
	}
}
