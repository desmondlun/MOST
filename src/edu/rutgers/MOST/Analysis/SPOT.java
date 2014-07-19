package edu.rutgers.MOST.Analysis;

import java.io.File;
import java.util.ArrayList;

import edu.rutgers.MOST.optimization.solvers.NonlinearSolver;
import edu.rutgers.MOST.optimization.solvers.Solver;
import edu.rutgers.MOST.optimization.solvers.SolverFactory;
import edu.rutgers.MOST.presentation.GraphicalInterface;

public class SPOT extends Analysis
{
	protected NonlinearSolver nonlinearSolver = SolverFactory.createNonlinearSolver();
	
	public SPOT()
	{
		super();
	}

	public ArrayList< Double > run()
	{
		ModelFormatter modelFormatter = new ModelFormatter();
		File file = GraphicalInterface.chooseCSVFile( "Load Gene Expression Data" );
		nonlinearSolver.setGeneExpr( 
				modelFormatter.parseGeneExpressionDataSPOT( file, this.model ) );
		return super.run();
	}

	@Override
	public Solver getSolver()
	{
		return nonlinearSolver;
	}
}
