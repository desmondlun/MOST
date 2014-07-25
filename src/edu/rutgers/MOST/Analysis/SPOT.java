package edu.rutgers.MOST.Analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import edu.rutgers.MOST.optimization.solvers.LinearSolver;
import edu.rutgers.MOST.optimization.solvers.NonlinearSolver;
import edu.rutgers.MOST.optimization.solvers.Solver;
import edu.rutgers.MOST.optimization.solvers.SolverFactory;
import edu.rutgers.MOST.presentation.GraphicalInterface;

public class SPOT extends Analysis
{
	protected LinearSolver linearSolver = SolverFactory.createLinearSolver();
	
	public SPOT()
	{
		super();
	}

	public ArrayList< Double > run()
	{
		ModelFormatter modelFormatter = new ModelFormatter();
		File file = GraphicalInterface.chooseCSVFile( "Load Gene Expression Data" );
		Vector< Double > geneExpr = modelFormatter.parseGeneExpressionDataSPOT( file, this.model );
		linearSolver.setGeneExpr( geneExpr );
		NonlinearSolver nonlinearSolver = SolverFactory.createNonlinearSolver();
		nonlinearSolver.setSolverComponent( linearSolver.getSolverComponent() );
		nonlinearSolver.setGeneExpr( geneExpr );
		nonlinearSolver.optimize( super.run() );
		return nonlinearSolver.getSoln();
	}

	@Override
	public Solver getSolver()
	{
		return linearSolver;
	}
}
