package edu.rutgers.MOST.optimization.Eflux2;

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

public class Eflux2 {
	
	private Eflux2Model model;
	private static Solver solver;
	private Vector<String> varNames;
	private double maxObj;

	public Eflux2() {
		Eflux2.setSolver(SolverFactory.createSolver( Algorithm.Eflux2 ));
		this.varNames = new Vector<String>();
	}

	public Eflux2(Eflux2Model m) {
		this.model = m;
		Eflux2.setSolver(SolverFactory.createSolver( Algorithm.Eflux2 ));
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

			Eflux2.getSolver().setVar(varName, VarType.CONTINUOUS, lb, ub);

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
			Eflux2.getSolver().addConstraint(sMatrix.get(i), conType, bValue);
		}
	}

	private void setObjective() {
		Eflux2.getSolver().setObjType(ObjType.Maximize);
		Vector<Double> objective = this.model.getObjective();
		Map<Integer, Double> map = new HashMap<Integer, Double>();
		for (int i = 0; i < objective.size(); i++) {
			if (objective.elementAt(i) != 0.0) {
				map.put(i, objective.elementAt(i));
			}
		}
		Eflux2.getSolver().setObj(map);
	}

	public void setEflux2Model(Eflux2Model m) {
		this.model = m;
	}
	
	public void formatFluxBoundsfromTransciptomicData( File file )
	{
		if( file == null || !file.exists() )
			return;
		try
		{			
			CSVReader csvReader = new CSVReader( new FileReader( file ) );
			List< String[] > all2 = csvReader.readAll();
			csvReader.close();
			Map< String, Double > expressionLevels = new HashMap< String, Double >();
			Map< String, Vector< Double > > levels = new HashMap< String, Vector< Double > >();
			for( String[] keyval : all2 )
			{
				if( !levels.containsKey( keyval[ 0 ] ) )
					levels.put( keyval[ 0 ], new Vector< Double >() );
				
				levels.get( keyval[ 0 ] ).add( Double.valueOf( keyval[ 1 ] ) );
			}
			for( String[] keyval : all2 )
			{
				Double val = 0.0;
				for( Double d : levels.get( keyval[ 0 ] ) )
					val += d;
				val = val / levels.get( keyval[ 0 ] ).size();
				expressionLevels.put( keyval[ 0 ], val );
			}
			model.formatFluxBoundsfromTransciptomicData( expressionLevels );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}

	public ArrayList<Double> run() {
		this.setVars();
		this.setConstraints();
		this.setObjective();
		this.maxObj = Eflux2.getSolver().optimize();
		
		return Eflux2.getSolver().getSoln();
	}

	public double getMaxObj() {
		return this.maxObj;
	}
	
	public static Solver getSolver() {
		return solver;
	}

	public static void setSolver(Solver solver) {
		Eflux2.solver = solver;
	}
}
