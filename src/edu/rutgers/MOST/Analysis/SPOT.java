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

	public ArrayList< Double > run() throws Exception
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
		
		ArrayList< Double > optimizedFluxes = nonlinearSolver.getSoln();
		
		Double v_length = 0.0;
		Double g_length = 0.0;
		Double g_dot_v = 0.0;
		for( int j = 0; j < optimizedFluxes.size(); ++j )
		{
			g_dot_v += geneExpr.get( j ) * optimizedFluxes.get( j );
			v_length += optimizedFluxes.get( j ) * optimizedFluxes.get( j );
			g_length += geneExpr.get( j ) * geneExpr.get( j );
		}
		v_length = Math.sqrt( v_length );
		g_length = Math.sqrt( g_length );
		this.maxObj = g_dot_v / (v_length*g_length);
		
		return optimizedFluxes;
	}

	@Override
	public Solver getSolver()
	{
		return linearSolver;
	}
}
