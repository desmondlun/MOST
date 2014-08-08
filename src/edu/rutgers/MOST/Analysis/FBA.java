package edu.rutgers.MOST.Analysis;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import edu.rutgers.MOST.optimization.solvers.LinearSolver;
import edu.rutgers.MOST.optimization.solvers.QuadraticSolver;
import edu.rutgers.MOST.optimization.solvers.Solver;
import edu.rutgers.MOST.optimization.solvers.SolverFactory;
import edu.rutgers.MOST.presentation.GraphicalInterface;

public class FBA extends Analysis
{
	protected LinearSolver linearSolver = SolverFactory.createFBASolver();
	public boolean FVASelected = false;
	public ArrayList< Double > minVariability = new ArrayList< Double >();
	public ArrayList< Double > maxVariability = new ArrayList< Double >();
	
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
 			FVASelected = true;
 	 		QuadraticSolver quadraticSolver = SolverFactory.createQuadraticSolver();
 	 		
	 		quadraticSolver.FVA( linearSolver.getObjectiveCoefs(), this.getMaxObj(), linearSolver.getSoln(), minVariability,
	 				maxVariability, linearSolver.getSolverComponent() );
	 		
	 		for( int i = 0; i < linearSolver.getSolverComponent().variableCount(); ++i )
	 		{
	 			if( !minVariability.get( i ).equals( maxVariability.get( i ) ) )
	 				System.out.println( "index: " + i + "\nmin: " + minVariability.get( i ) + "\nmax: " + maxVariability.get( i ) + "\n" );
	 		}
 		}
 		else
 			FVASelected = false;
 		return linearSolver.getSoln();
 	}

	@Override
	public Solver getSolver()
	{
		return linearSolver;
	}
}
