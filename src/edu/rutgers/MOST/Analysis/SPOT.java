package edu.rutgers.MOST.Analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import edu.rutgers.MOST.data.SBMLReaction;
import edu.rutgers.MOST.optimization.solvers.LinearSolver;
import edu.rutgers.MOST.optimization.solvers.NonlinearSolver;
import edu.rutgers.MOST.optimization.solvers.Solver;
import edu.rutgers.MOST.optimization.solvers.SolverFactory;
import edu.rutgers.MOST.presentation.GraphicalInterface;

public class SPOT extends Analysis
{
	protected LinearSolver linearSolver = SolverFactory.createFBASolver();
	
	public SPOT()
	{
		super();
	}

	public ArrayList< Double > run()
	{
		boolean usingNormalSPOT = false;
		for( SBMLReaction reaction : model.getReactions() )
		{
			if( reaction.getLowerBound() > 0.0 || reaction.getUpperBound() < 0.0 )
			{
				usingNormalSPOT = true;
				break;
			}
		}
		
		ModelFormatter modelFormatter = new ModelFormatter();
		File file = GraphicalInterface.chooseCSVFile( "Load Gene Expression Data" );
		Vector< Double > geneExpr = modelFormatter.parseGeneExpressionDataSPOT( file, this.model, usingNormalSPOT );
		linearSolver.setGeneExpr( geneExpr );
		NonlinearSolver nonlinearSolver = null;
		if( usingNormalSPOT )
			nonlinearSolver = SolverFactory.CreateSPOTv1Solver();
		else
		{
			nonlinearSolver = SolverFactory.CreateSPOTv2Solver();
			nonlinearSolver.addNormalizeConstraint();
		}
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
