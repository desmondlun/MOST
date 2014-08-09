package edu.rutgers.MOST.Analysis;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.optimization.solvers.LinearSolver;
import edu.rutgers.MOST.optimization.solvers.QuadraticSolver;
import edu.rutgers.MOST.optimization.solvers.Solver;
import edu.rutgers.MOST.optimization.solvers.SolverFactory;

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
 		int selectedOption = JOptionPane.showConfirmDialog( null, 
 				"Do you want to perform Flux Variability Analysis?", "FVA analysis", 
 				JOptionPane.YES_NO_OPTION );
 		
 		if( JOptionPane.CLOSED_OPTION == selectedOption )
 			throw new Exception( "FVA dialog closed" );
 		
 		this.setSolverParameters();
 		this.maxObj = linearSolver.optimize();
 		
 		if( JOptionPane.YES_OPTION == selectedOption )
 		{
 			LocalConfig.getInstance().fvaDone = false;
 			FVASelected = true;
 	 		QuadraticSolver quadraticSolver = SolverFactory.createQuadraticSolver();
 	 		
	 		quadraticSolver.FVA( linearSolver.getObjectiveCoefs(), this.getMaxObj(), linearSolver.getSoln(), minVariability,
	 				maxVariability, linearSolver.getSolverComponent() );
 		}
 		return linearSolver.getSoln();
 	}

	@Override
	public Solver getSolver()
	{
		return linearSolver;
	}
}
