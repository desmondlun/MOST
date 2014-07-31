package edu.rutgers.MOST.Analysis;

import java.util.ArrayList;

import edu.rutgers.MOST.optimization.solvers.*;
import edu.rutgers.MOST.presentation.GraphicalInterface;

public class Eflux2 extends Analysis
{	
	private LinearSolver linearSolver = SolverFactory.createFBASolver();
	
 	public Eflux2()
	{
		super();
	}
 	
 	public ArrayList< Double > run() throws Exception
 	{
 		ModelFormatter modelFormatter = new ModelFormatter();
 		modelFormatter.formatFluxBoundsfromGeneExpressionData( GraphicalInterface.chooseCSVFile( "Load Gene Expressions" ), this.model );
 		
 		this.setSolverParameters();
 		this.maxObj = linearSolver.optimize();
 		QuadraticSolver quadraticSolver = SolverFactory.createQuadraticSolver();
 		return quadraticSolver.minimizeEuclideanNorm( linearSolver.getObjectiveCoefs(), this.getMaxObj(), linearSolver.getSolverComponent() );
 	}

	@Override
	public Solver getSolver()
	{
		return linearSolver;
	}
}
