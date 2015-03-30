package edu.rutgers.MOST.Analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import edu.rutgers.MOST.data.Model;
import edu.rutgers.MOST.data.SBMLReaction;
import edu.rutgers.MOST.optimization.solvers.LinearSolver;
import edu.rutgers.MOST.optimization.solvers.NonlinearSolver;
import edu.rutgers.MOST.optimization.solvers.ObjType;
import edu.rutgers.MOST.optimization.solvers.Solver;
import edu.rutgers.MOST.optimization.solvers.SolverFactory;
import edu.rutgers.MOST.presentation.GraphicalInterface;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;
import edu.rutgers.MOST.presentation.SimpleProgressBar;

public class SPOT extends Analysis
{
	protected LinearSolver linearSolver = SolverFactory.CreateSPOTv3Solver();
	
	public SPOT()
	{
		super();
	}

	public ArrayList< Double > run() throws Exception
	{
		SimpleProgressBar pb = null;
		try
		{
			
			ModelFormatter modelFormatter = new ModelFormatter();
			File file = GraphicalInterface.chooseCSVFile( "Load Gene Expression Data" );
			pb = new SimpleProgressBar( "Calculation SPOT", "Loading, please wait..." );
			pb.setLocationRelativeTo( null );
			pb.setAlwaysOnTop( true );
			pb.setVisible( true );
		//	pb.progressBar.setIndeterminate( true );
			pb.progressBar.setString( "Integrating gene expression data..." );
			Vector< Double > geneExpr = modelFormatter.parseGeneExpressionDataSPOT( file, this.model, false, pb );
			
			pb.progressBar.setString( "Setting up the model..." );
			ArrayList< Map< Integer, Double > > sMatrix = this.model.getSMatrix();
			ArrayList< Map< Integer, Double > > sMatrix_res = new ArrayList< Map< Integer, Double > >();
			ArrayList< Double > lbs = new ArrayList< Double >();
			ArrayList< Double > ubs = new ArrayList< Double >();
			ArrayList< Integer > fluxIds = new ArrayList< Integer >();
			ArrayList< Double > lbs_res = new ArrayList< Double >();
			ArrayList< Double > ubs_res = new ArrayList< Double >();
			ArrayList< Double > geneExpr_res = new ArrayList< Double >();
			
			for( SBMLReaction reaction : this.model.getReactions() )
			{
				lbs.add( reaction.getLowerBound() );
				ubs.add( reaction.getUpperBound() );
			}
			
			modelFormatter.formatParamsForSPOT( sMatrix, lbs, ubs, fluxIds, new ArrayList<Double>(geneExpr),
				sMatrix_res, lbs_res, ubs_res, geneExpr_res );
			
			Model updatedModel = new Model();
			
			// update the reactions
			Vector< SBMLReaction > updated_reactions = new Vector< SBMLReaction >();
			for( int i = 0; i < lbs_res.size(); ++i )
			{
				SBMLReaction reaction = new SBMLReaction();
				reaction.setLowerBound( lbs_res.get( i ) );
				reaction.setUpperBound( ubs_res.get( i ) );
				reaction.setKnockout( GraphicalInterfaceConstants.BOOLEAN_VALUES[0] );
				updated_reactions.add( reaction );
			}
			updatedModel.setReactions( updated_reactions );
			
			// update the constraints
			updatedModel.setSMatrix( sMatrix_res );			
			
			//update the objective
			updatedModel.setObjective( new Vector< Double >( geneExpr_res ) );
			
			Model original = this.model;
			this.model = updatedModel;
			
			pb.progressBar.setString( "Finding an optimal solution..." );
			this.setSolverParameters();
			linearSolver.setObjType( ObjType.Maximize );
			this.maxObj = linearSolver.optimize();
			ArrayList< Double > fluxes = linearSolver.getSoln();
			
			pb.progressBar.setString( "Calculating V*G / (||V|| ||G||)" );
			ArrayList< Double > optimizedFluxes = fluxes;
			
			double lengthG = 0.0;
			double lengthV = 0.0;
			
			for( int i = 0; i < optimizedFluxes.size(); ++i )
			{
				lengthG += geneExpr_res.get( i ) * geneExpr_res.get( i );
				lengthV += optimizedFluxes.get( i ) * optimizedFluxes.get( i );
			}
			
			double SPOTVal = this.maxObj / (  Math.sqrt( lengthG ) * Math.sqrt( lengthV ) );
			this.maxObj = SPOTVal;
			
			pb.progressBar.setString( "Updating table..." );
			
			while( fluxIds.size() > 0 )
			{
				int lastIdx = fluxIds.size() - 1;
				int idx = fluxIds.get( lastIdx );
				
				if( !geneExpr_res.get( idx ).equals( geneExpr_res.get( idx ) ) )
					throw new Exception( "GeneExpr vals not equal!" );
				
				optimizedFluxes.set( idx, optimizedFluxes.get( idx ) - optimizedFluxes.get( idx + 1 ) );
				optimizedFluxes.remove( idx + 1 );
				fluxIds.remove( lastIdx );
			}
			
			for( int i = 0; i < optimizedFluxes.size(); ++i )
				original.getReactions().get( i ).setFluxValue( optimizedFluxes.get( i ) );
			
			pb.setVisible( false );
			pb.dispose();
			pb = null;
			
			return optimizedFluxes;
		}
		catch( Exception e )
		{
			e.printStackTrace();
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
