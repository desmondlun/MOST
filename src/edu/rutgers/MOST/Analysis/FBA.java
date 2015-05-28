package edu.rutgers.MOST.Analysis;

import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.optimization.solvers.LinearSolver;
import edu.rutgers.MOST.optimization.solvers.Solver;
import edu.rutgers.MOST.optimization.solvers.SolverFactory;
import edu.rutgers.MOST.presentation.GraphicalInterface;
import edu.rutgers.MOST.presentation.SimpleProgressBar;

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
	
	public void disableSolverErrors()
	{
		linearSolver.disableErrors();
	}
	
	@Override
 	public ArrayList< Double > run() throws Exception
 	{
		SimpleProgressBar pb = null;
		try
		{
			JCheckBox FVACheckBox = new JCheckBox( "Run FVA analysis" );
			JCheckBox minEucBox = new JCheckBox( "Run Euclidean normalization" );
			Object[] params = { "Select optional analysis along FBA", FVACheckBox, minEucBox };
	 		int selectedOption = JOptionPane.showConfirmDialog( null, 
	 				params, "FVA analysis", 
	 				JOptionPane.OK_CANCEL_OPTION );
	 		
	 		if( JOptionPane.CANCEL_OPTION == selectedOption || JOptionPane.CLOSED_OPTION == selectedOption ) {
	 			GraphicalInterface.analysisRunning = false;
	 			throw new Exception( "FVA dialog closed" );
	 		}
	 		
	 		
			pb = new SimpleProgressBar( "Calculating FBA", "progressing..." );
			pb.setAlwaysOnTop( true );
			pb.progressBar.setString( "Loading, please wait..." );
			pb.progressBar.setIndeterminate( true );
			pb.setLocationRelativeTo( null );
	 		pb.setVisible( true );
	 		
	 		this.setSolverParameters();
	 		this.maxObj = linearSolver.optimize();
	 		
	 		if( FVACheckBox.isSelected() )
	 		{
	 			pb.dispose();
	 			LocalConfig.getInstance().fvaDone = false;
	 			FVASelected = true;	 	 		
		 		linearSolver.FVA( linearSolver.getObjectiveCoefs(), this.getMaxObj(), linearSolver.getSoln(), minVariability,
		 				maxVariability, linearSolver.getSolverComponent() );
	 		}
	 		return linearSolver.getSoln();
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

	public void solve() throws Exception
	{
		this.setSolverParameters();
		this.maxObj = linearSolver.optimize();
	}
	
	@Override
	public Solver getSolver()
	{
		return linearSolver;
	}
}
