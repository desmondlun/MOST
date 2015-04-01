package edu.rutgers.MOST.optimization.solvers;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.presentation.GraphicalInterface;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;
import edu.rutgers.MOST.presentation.ResizableDialog;
import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBQuadExpr;
import gurobi.GRBVar;
import gurobi.GRB.DoubleAttr;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

public class QuadraticGurobiSolver implements QuadraticSolver
{ 
	
	private void promptGRBError( GRBException e )
	{
		String errMsg;
		int code = e.getErrorCode();
		switch( code )
		{
		case GRB.Error.NO_LICENSE:
			errMsg = "<html><p>No validation file - run 'grbgetkey' to refresh it.</p></html>";
			LocalConfig.getInstance().hasValidGurobiKey = false;
			break;
		case GRB.Error.FAILED_TO_CREATE_MODEL:
			errMsg = "<html><p>Gurobi failed to create the model";
		case GRB.Error.NOT_SUPPORTED:
			errMsg = "<html><p>This optimization is not supported by Gurobi";
			break;
		case GRB.Error.INVALID_ARGUMENT:
			errMsg = "<html><p>Gurobi encountered an invalid argument";
			break;
		case GRB.Error.IIS_NOT_INFEASIBLE:
			errMsg = "<html><p>Gurobi determined the IIS is not feasable";
			break;
		case GRB.Error.NUMERIC:
			errMsg = "<html><p>Gurobi encountered a numerical error while optimizing the model";
			break;
		case GRB.Error.INTERNAL:
			errMsg = "<html><p>Gurobi has encountered an internal error!";
			break;
		case 0:
			errMsg = e.getMessage() + "\n";
			break;
		default:
			errMsg = "<html><p>Gurobi encountered an error optimizing the model - <br> "
					+ " <a href=" + GraphicalInterfaceConstants.GUROBI_ERROR_CODE_URL
					+ ">Error Code:" + code + "</a><br>\n";
		}
		if( GraphicalInterface.getGdbbDialog() != null )
			GraphicalInterface.getGdbbDialog().setVisible( false );

		processStackTrace( new Exception( errMsg ) );
		LocalConfig.getInstance().getOptimizationFilesList().clear();
	}
	private void processStackTrace( Exception e )
	{
		final ArrayList< Image > icons = new ArrayList< Image >();
		final ResizableDialog dialog = new ResizableDialog( "Error",
				"Gurobi Solver Error", "Gurobi Solver Error" );
		dialog.setIconImages( icons );
		dialog.setLocationRelativeTo( null );
		dialog.setVisible( false );
		dialog.setDefaultCloseOperation( javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE );
		dialog.addWindowListener( new WindowAdapter()
		{
			public void windowClosing( WindowEvent evt )
			{
				dialog.setVisible( false );
			}
		} );

		dialog.OKButton.addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent ae )
			{
				dialog.setVisible( false );
			}

		} );

		//e.printStackTrace();
		StringWriter errors = new StringWriter();
		e.printStackTrace( new PrintWriter( errors ) );
		dialog.setErrorMessage( errors.toString() + "</p></html>" );
		// centers dialog
		dialog.setLocationRelativeTo(null);
		dialog.setModal(true);		
		dialog.setVisible( true );
	}	
	private char getGRBVarType( VarType type )
	{
		switch( type )
		{
		case CONTINUOUS:
			return GRB.CONTINUOUS;
		case BINARY:
			return GRB.BINARY;
		case INTEGER:
			return GRB.INTEGER;
		case SEMICONT:
			return GRB.SEMICONT;
		case SEMIINT:
			return GRB.SEMIINT;
		default:
			return GRB.CONTINUOUS;
		}
	}
	private char getGRBConType( ConType type )
	{
		switch( type )
		{
		case LESS_EQUAL:
			return GRB.LESS_EQUAL;
		case EQUAL:
			return GRB.EQUAL;
		case GREATER_EQUAL:
			return GRB.GREATER_EQUAL;
		default:
			return GRB.LESS_EQUAL;
		}
	}
	
	/**
	 * Used by E-Flux2
	 * Minimize the Euclidean norm of the flux vectors returned by FBA using Gurobi
	 * 
	 * @return The minimized Euclidean vector (ArrayList) of fluxes
	 * @param objCoefs The coefficient vector of the Objective function used in FBA, it is used as an extra constraint
	 * @param objVal The value of the objective function returned by FBA
	 * @param componentSource The solverComponent that contains the constraint and variable matrix
	 * @see Eflux-2
	 * @see FBA
	 */
	@Override
	public ArrayList< Double > minimizeEuclideanNorm( ArrayList< Double > objCoefs, Double objVal, SolverComponent componentSource ) throws Exception
	{
		ArrayList< Double > soln = new ArrayList< Double >();
		final SolverComponent component = componentSource;
		
		try
		{
			// set up the quadratic environment model
			GRBEnv quad_env = new GRBEnv();
			GRBModel quad_model = new GRBModel( quad_env );
			ArrayList< GRBVar > vars = new ArrayList< GRBVar >();
			quad_env.set( GRB.DoubleParam.IntFeasTol, 1.0E-9 );
			quad_env.set( GRB.DoubleParam.FeasibilityTol, 1.0E-9 );
			quad_env.set( GRB.IntParam.OutputFlag, 0 );
			
			// create the variables
			for( int j = 0; j < component.variableCount(); ++ j )
			{
				Variable var = component.getVariable( j );

				vars.add( quad_model.addVar( var.lb, var.ub, 0.0, getGRBVarType( var.type ),
						null ) );
			}
			quad_model.update();
			
			// Fv = z extra constraint
			component.addConstraint( objCoefs, ConType.EQUAL, objVal );
			
			// set constraints to Gurobi
			for( int i = 0; i < component.constraintCount(); ++i )
			{
				Constraint constraint = component.getConstraint( i );

				GRBLinExpr expr = new GRBLinExpr();
				for( int j = 0; j < component.variableCount(); ++j )
				{
					expr.addTerm( constraint.getCoefficient( j ), vars.get( j ) );
				}
				quad_model.addConstr( expr, getGRBConType( constraint.type ), constraint.value, null );
			}
	
			// set the objective
			GRBQuadExpr expr = new GRBQuadExpr();
			for( GRBVar var : quad_model.getVars() ) 
				expr.addTerm( 1.0, var, var );
			quad_model.setObjective( expr, GRB.MINIMIZE );
			
			// optimize the model
			quad_model.optimize();
			
			// remove the extra constraint
			component.removeConstraint( component.constraintCount() - 1 );
			
			// get min of sum of min v^2
			// objval = result = quad_model.get( GRB.DoubleAttr.ObjVal );
			
			for( GRBVar var : vars)
				soln.add( var.get( GRB.DoubleAttr.X ) );
			
			// clean up
			quad_model.dispose();
			quad_env.dispose();
		}
		catch( GRBException e )
		{
			promptGRBError( e );
		}
		
		return soln;
	}
	@Override
	public ArrayList< Double > SPOTAlgorithm( ArrayList< Double > objCoefs,
		SolverComponent component ) throws Exception
	{
		ArrayList< Double > soln = new ArrayList< Double >();
		
		try
		{
			// set up the quadratic environment model
			GRBEnv quad_env = new GRBEnv();
			GRBModel quad_model = new GRBModel( quad_env );
			ArrayList< GRBVar > vars = new ArrayList< GRBVar >();
			quad_env.set( GRB.DoubleParam.IntFeasTol, 1.0E-9 );
			quad_env.set( GRB.DoubleParam.FeasibilityTol, 1.0E-9 );
			quad_env.set( GRB.IntParam.OutputFlag, 0 );
			
			// create the variables
			for( int j = 0; j < component.variableCount(); ++ j )
			{
				Variable var = component.getVariable( j );
				vars.add( quad_model.addVar( var.lb, var.ub, 0.0, getGRBVarType( var.type ),
						null ) );
			}
			quad_model.update();
			
			// set constraints to Gurobi
			// steady state constraint
			for( int i = 0; i < component.constraintCount(); ++i )
			{
				Constraint constraint = component.getConstraint( i );

				GRBLinExpr expr = new GRBLinExpr();
				for( int j = 0; j < component.variableCount(); ++j )
				{
					expr.addTerm( constraint.getCoefficient( j ), vars.get( j ) );
				}
				quad_model.addConstr( expr, getGRBConType( constraint.type ), constraint.value, null );
			}
			
			// ||V||^2 = 1 constraint
			GRBQuadExpr normcon = new GRBQuadExpr();
			for( GRBVar var : vars )
			{
				normcon.addTerm( 1.0, var, var );
			}
			quad_model.addQConstr( normcon, GRB.LESS_EQUAL, 1.0, null );
	
			// set the objective
			GRBLinExpr expr = new GRBLinExpr();
			for( int i = 0; i < objCoefs.size(); ++i )
				expr.addTerm( objCoefs.get( i ), vars.get( i ) );
			quad_model.setObjective( expr, GRB.MAXIMIZE );
			
			// optimize the model
			quad_model.optimize();
			
			for( GRBVar var : vars)
				soln.add( var.get( GRB.DoubleAttr.X ) );
			
			// clean up
			quad_model.dispose();
			quad_env.dispose();
		}
		catch( GRBException e )
		{
			promptGRBError( e );
		}
		
		
		// check against constraints
		ArrayList< Double > checks = new ArrayList< Double >();
		for( int i = 0; i < component.constraintCount(); ++i )
		{
			double c = 0.0;
			for( int j = 0; j < component.variableCount(); ++j )
			{
				c += component.getConstraint( i ).getCoefficient( j ) * soln.get( j );
			}
			checks.add( c );
			if( c > 1E-8 )
				System.out.println( "Problem!" );
		}
		
		
		return soln;
	}
}
