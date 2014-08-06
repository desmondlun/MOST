package edu.rutgers.MOST.Analysis;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import edu.rutgers.MOST.optimization.solvers.LinearSolver;
import edu.rutgers.MOST.optimization.solvers.QuadraticSolver;
import edu.rutgers.MOST.optimization.solvers.Solver;
import edu.rutgers.MOST.optimization.solvers.SolverFactory;

public class FBA extends Analysis
{
	protected LinearSolver linearSolver = SolverFactory.createFBASolver();
	
	public FBA()
	{
		super();
	}
	
	@Override
 	public ArrayList< Double > run() throws Exception
 	{	
 		this.setSolverParameters();
 		this.maxObj = linearSolver.optimize();
 		
 		if( JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog( null, 
 				"Do you want to perform Flux Variability Analysis?", "FVA analysis", 
 				JOptionPane.YES_NO_OPTION ) )
 		{
 	 		QuadraticSolver quadraticSolver = SolverFactory.createQuadraticSolver();
 	 		ArrayList< Double > minVariability = new ArrayList< Double >();
 	 		ArrayList< Double > maxVariability = new ArrayList< Double >();
 	 		
	 		quadraticSolver.FVA( linearSolver.getObjectiveCoefs(), this.getMaxObj(), linearSolver.getSoln(), minVariability,
	 				maxVariability, linearSolver.getSolverComponent() );
	 		
	 		for( int i = 0; i < linearSolver.getSolverComponent().variableCount(); ++i )
	 		{
	 			if( !minVariability.get( i ).equals( maxVariability.get( i ) ) )
	 				System.out.println( "index: " + i + "\nmin: " + minVariability.get( i ) + "\nmax: " + maxVariability.get( i ) + "\n" );
	 		}
 		}
 		return linearSolver.getSoln();
 	}

	@Override
	public Solver getSolver()
	{
		return linearSolver;
	}
}
