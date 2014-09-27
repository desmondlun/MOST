package edu.rutgers.MOST.Analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import edu.rutgers.MOST.data.SBMLReaction;
import edu.rutgers.MOST.optimization.solvers.ConType;
import edu.rutgers.MOST.optimization.solvers.LinearSolver;
import edu.rutgers.MOST.optimization.solvers.NonlinearSolver;
import edu.rutgers.MOST.optimization.solvers.Solver;
import edu.rutgers.MOST.optimization.solvers.SolverFactory;
import edu.rutgers.MOST.presentation.GraphicalInterface;
import edu.rutgers.MOST.presentation.SimpleProgressBar;

public class SPOT extends Analysis
{
	protected LinearSolver linearSolver = SolverFactory.createFBASolver();
	
	public SPOT()
	{
		super();
	}

	public ArrayList< Double > run() throws Exception
	{
		SimpleProgressBar pb = null;
		try
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
			pb = new SimpleProgressBar( "Calculation SPOT", "Loading, please wait..." );
			pb.setLocationRelativeTo( null );
			pb.setAlwaysOnTop( true );
			pb.setVisible( true );
			pb.progressBar.setIndeterminate( true );
			pb.progressBar.setString( "Integrating gene expression data..." );
			Vector< Double > geneExpr = modelFormatter.parseGeneExpressionDataSPOT( file, this.model, usingNormalSPOT, pb );
			linearSolver.setGeneExpr( geneExpr );
			NonlinearSolver nonlinearSolver = null;
			pb.progressBar.setString( "Finding a feasible starting point..." );
			if( usingNormalSPOT )
			{
				nonlinearSolver = SolverFactory.CreateSPOTv1Solver();
			}
			else
			{
				nonlinearSolver = SolverFactory.CreateSPOTv2Solver();
				nonlinearSolver.addNormalizeConstraint();
			}
			nonlinearSolver.setSolverComponent( linearSolver.getSolverComponent() );
			nonlinearSolver.setGeneExpr( geneExpr );
			super.setVars();
			super.setConstraints();
			super.setObjective();
			linearSolver.getSolverComponent().addConstraint( new ArrayList< Double >(model.getObjective()), ConType.EQUAL, 0.0 );
			linearSolver.optimize();
			linearSolver.getSolverComponent().removeConstraint( linearSolver.getSolverComponent().constraintCount() - 1 );
			pb.setVisible( false );
			pb.dispose();
			pb = null;
			nonlinearSolver.optimize( linearSolver.getSoln() );
			
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
			
			for( SBMLReaction r : this.model.getReactions() )
				r.setFluxValue( optimizedFluxes.get( r.getId() ) );
			
			return optimizedFluxes;
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
