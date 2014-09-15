package edu.rutgers.MOST.Analysis;

import java.io.File;
import java.util.ArrayList;

import edu.rutgers.MOST.optimization.solvers.*;
import edu.rutgers.MOST.presentation.GraphicalInterface;
import edu.rutgers.MOST.presentation.SimpleProgressBar;

public class Eflux2 extends Analysis
{	
	private LinearSolver linearSolver = SolverFactory.createFBASolver();
	
 	public Eflux2()
	{
		super();
	}
 	
 	public ArrayList< Double > run() throws Exception
 	{
 		SimpleProgressBar pb = null;
 		try
 		{
 			ModelFormatter modelFormatter = new ModelFormatter();
 			File csvFile = GraphicalInterface.chooseCSVFile( "Load Gene Expressions" );
 			
 			pb = new SimpleProgressBar( "Calculating E-flux2", "Loading, please wait..." );
			pb.setLocationRelativeTo( null );
			pb.progressBar.setIndeterminate( true );
			pb.setAlwaysOnTop( true );
			pb.progressBar.setString( "integrating gene expression data..." );
	 		pb.setVisible( true );
 			modelFormatter.formatFluxBoundsfromGeneExpressionData( csvFile, this.model, pb );
			pb.progressBar.setString( "predicting biomass..." );
 		
 			this.setSolverParameters();
 			this.maxObj = linearSolver.optimize();
 			pb.progressBar.setString( "searching for unique flux set..." );
 			QuadraticSolver quadraticSolver = SolverFactory.createQuadraticSolver();
 			return quadraticSolver.minimizeEuclideanNorm( linearSolver.getObjectiveCoefs(), this.getMaxObj(), linearSolver.getSolverComponent() );
 		}
 		catch( Exception e )
 		{
 			throw e;
 		}
 		finally
 		{
 			if( pb != null )
 			{
				pb.setVisible( false );
				pb.dispose();
 			}
 		}
 	}

	@Override
	public Solver getSolver()
	{
		return linearSolver;
	}
}
