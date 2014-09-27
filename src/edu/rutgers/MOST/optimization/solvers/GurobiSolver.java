
package edu.rutgers.MOST.optimization.solvers;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import edu.rutgers.MOST.presentation.AbstractParametersDialog;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;
import edu.rutgers.MOST.presentation.GurobiParameters;
import edu.rutgers.MOST.presentation.ResizableDialog;
import edu.rutgers.MOST.presentation.GraphicalInterface;
import edu.rutgers.MOST.presentation.SimpleProgressBar;
import edu.rutgers.MOST.presentation.Utilities;
import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.data.Model;
import edu.rutgers.MOST.data.ModelCompressor;
import gurobi.GRB;
import gurobi.GRBCallback;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBModel;
import gurobi.GRBVar;
import gurobi.GRBLinExpr;

public abstract class GurobiSolver implements MILSolver
{
	protected Model dataModel = null;
	protected ModelCompressor compressor = null;
	protected ArrayList< Double > soln = new ArrayList< Double >();
	protected Vector< Double > geneExpr = new Vector< Double >();
	protected double objval;
	protected GRBEnv env = null;
	protected ObjType objType;
	protected ResizableDialog dialog = new ResizableDialog( "Error",
			"Gurobi Solver Error", "Gurobi Solver Error" );
	protected boolean abort = false;
	protected SolverComponent component = new SolverComponentLightWeight();
	protected ArrayList< Double > objCoefs = new ArrayList< Double >();
	protected GRBModel model = null;
	protected boolean showErrorMessages = true;
	public static boolean isGurobiLinked()
	{
		try
		{
			try
			{
				GRBEnv env = new GRBEnv();
				env.dispose();
			}
			catch ( GRBException e ) // necessary due to throws declaration
			{
			}
		}
		catch ( UnsatisfiedLinkError | NoClassDefFoundError except )
		{
			return false; // gurobi does not link
		}

		return true; // gurobi does link
	}
	protected void processStackTrace( Exception e )
	{
		//e.printStackTrace();
		if( showErrorMessages )
		{
			StringWriter errors = new StringWriter();
			e.printStackTrace( new PrintWriter( errors ) );
			dialog.setErrorMessage( errors.toString() + "</p></html>" );
			// centers dialog
			dialog.setLocationRelativeTo(null);
			dialog.setModal(true);		
			dialog.setVisible( true );
		}
	}
	protected void promptGRBError( GRBException e )
	{
		abort();
		if( showErrorMessages )
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
	}
	protected char getGRBVarType( VarType type )
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
	protected char getGRBConType( ConType type )
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
	protected int getGRBObjType( ObjType objType )
	{
		switch( objType )
		{
		case Minimize:
			return GRB.MINIMIZE;
		case Maximize:
			return GRB.MAXIMIZE;
		default:
			return GRB.MINIMIZE;
		}
	}

	public GurobiSolver()
	{
		// set the dialog
		final ArrayList< Image > icons = new ArrayList< Image >();
		icons.add( new ImageIcon( "etc/most16.jpg" ).getImage() );
		icons.add( new ImageIcon( "etc/most32.jpg" ).getImage() );
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

		if( !isGurobiLinked() )
		{
			String msg1 = "Java could not link to the Gurobi dependencies";
			String msg2 = "Please check if your Gurobi environment variables match the location of Gurobi dependencies";
			String msg3 = "Your " + System.getProperty( "sun.arch.data.model" )
					+ " bit JVM is trying to launch "
					+ System.getProperty( "sun.arch.data.model" )
					+ " bit Gurobi";
			String msg4 = "The current JVM specs are: "
					+ System.getProperty( "java.runtime.version" );
			Object[] options = { "    OK    " };
			JOptionPane.showOptionDialog( null, msg1 + "\n" + msg2 + "\n"
					+ msg3 + "\n" + msg4, "Linking Error",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
					null, options, options[0] );
			return;
		}
		try
		{
			// set up environment and the model/problem objects
			if( env == null )
				env = new GRBEnv();
			env.set( GRB.IntParam.OutputFlag, 0 );
			
		}
		catch ( GRBException e )
		{
			promptGRBError( e );
		}
		catch ( Exception except ) // unexpected
		{
			processStackTrace( except );
		}

	}
	@Override
	public String getName()
	{
		return "GurobiSolver";
	}
	@Override
	public ArrayList< Double > getSoln()
	{
		// return the column list
		return soln;
	}
	@Override
	public void setVar( String varName, VarType type, double lb, double ub )
	{
		component.addVariable( type, lb, ub );
	}
	@Override
	public void setObjType( ObjType objType )
	{
		this.objType = objType;
	}
	@Override
	public void setObj( Map< Integer, Double > map )
	{
		if( objCoefs.size() == 0 )
		{
			for( int j = 0; j < component.variableCount(); ++j )
				objCoefs.add( new Double( 0.0 ) );
		}
		
		for( Entry< Integer, Double > coef : map.entrySet() )
			objCoefs.set( coef.getKey(), coef.getValue() );
	}
	@Override
	public void addConstraint( Map< Integer, Double > map, ConType conType,
			double value )
	{
		component.addConstraint( map, conType, value );
	}

	public void setDataModel( Model model )
	{
		this.dataModel = model;
	}
	protected double minimizeEuclideanNorm() throws Exception
	{
		double result = 0.0;
		
		QuadraticGurobiSolver quadSolver = new QuadraticGurobiSolver();
		this.soln = quadSolver.minimizeEuclideanNorm( new ArrayList< Double >( this.objCoefs ), this.objval, this.component );
		
		for( Double val : soln )
			result += val * val;
		
		result = Math.sqrt( result );
		
		return result;
	}
	@Override
	public double optimize() throws Exception
	{
		try
		{
			AbstractParametersDialog params = GraphicalInterface.getGurobiParameters();
			env.set( GRB.IntParam.Threads, 
				Integer.valueOf( params.getParameter( GurobiParameters.NUM_THREADS_NAME ) ) );
			env.set( GRB.IntParam.MIPFocus,
				Integer.valueOf( params.getParameter( GurobiParameters.MIPFOCUS_NAME ) ) );
			env.set( GRB.DoubleParam.FeasibilityTol,
				Double.valueOf( params.getParameter( GurobiParameters.FEASIBILITYTOL_NAME ) ) );
			env.set( GRB.DoubleParam.IntFeasTol,
				Double.valueOf( params.getParameter( GurobiParameters.INTFEASIBILITYTOL_NAME ) ) );
			env.set( GRB.DoubleParam.Heuristics, 
				Double.valueOf( params.getParameter( GurobiParameters.HEURISTICS_NAME ) ) );
			env.set( GRB.DoubleParam.OptimalityTol,
				Double.valueOf( params.getParameter( GurobiParameters.OPTIMALITYTOL_NAME ) ) );
			env.set( GRB.IntParam.OutputFlag, 0 );

			model = new GRBModel( env );
			ArrayList< GRBVar > vars = new ArrayList< GRBVar >();
			
			try
			{
				// set the callback
				model.setCallback( this.createGRBCallback() );
				
				// add columns
				for( int j = 0; j < component.variableCount(); ++j )
				{
					Variable var = component.getVariable( j );
					vars.add( model.addVar( var.lb, var.ub, 0.0, getGRBVarType( var.type ),
							null ) );
				}
				model.update();
				
				for( int i = 0; i < component.constraintCount(); ++i )
				{
					Constraint constraint = component.getConstraint( i );
					GRBLinExpr expr = new GRBLinExpr();
					for( int j = 0; j < component.variableCount(); ++j )
						expr.addTerm( constraint.getCoefficient( j ), vars.get( j ) );
					model.addConstr( expr, getGRBConType( constraint.type ), constraint.value, null );
				}
				
				
				// set the objective
				GRBLinExpr expr = new GRBLinExpr();
	
				// set the terms & coefficients defining the objective function
				for( int j = 0; j < component.variableCount(); ++j )
					expr.addTerm( objCoefs.get( j ), vars.get( j ) );
	
				// set the objective
				model.setObjective( expr, getGRBObjType( objType ) );
				
				// perform the optimization and get the objective value
				model.optimize();
				
				// write to MPS file if specified
				if( params.getParameter(
						GurobiParameters.SAVE_TO_MPS_NAME ).equals( Boolean.toString( true ) ) )
					model.write( Utilities.getMOSTSettingsPath() + "LastProblem_Gurobi.mps" );
				
				if( !abort )
				{
					switch( model.get( GRB.IntAttr.Status ) )
					{
					case GRB.LOADED:
						throw new GRBException( "Model is loaded, but no solution information is available." );
					case GRB.INFEASIBLE:
						throw new GRBException( "Model was proven to be infeasible." );
					case GRB.INF_OR_UNBD:
						throw new GRBException( "Model was proven to be either infeasible or unbounded." );
					case GRB.UNBOUNDED:
						break;
					case GRB.CUTOFF:
						break;
					case GRB.NODE_LIMIT:
						break;
					case GRB.TIME_LIMIT:
						break;
					case GRB.SOLUTION_LIMIT:
						break;
					case GRB.INTERRUPTED:
						break;
					case GRB.NUMERIC:
						throw new GRBException( "Optimization was terminated due to unrecoverable numerical difficulties." );
					case GRB.SUBOPTIMAL:
						break;
					case GRB.INPROGRESS:
						break;
						
						
					}
					objval = model.get( GRB.DoubleAttr.ObjVal );
		
					// get the flux values
					for( GRBVar var : vars)
						soln.add( var.get( GRB.DoubleAttr.X ) );
				}
			}
			catch( GRBException e )
			{
				throw e;
			}
			finally
			{
				// clean up
				model.dispose();
				env.dispose();
				vars.clear();
			}
		}
		catch ( GRBException e )
		{
			promptGRBError( e );
			throw new Exception( e );
		}

		return objval;
	}
	@Override
	public void setEnv( double timeLimit, int numThreads )
	{		
		try
		{
			if( env == null )
				env = new GRBEnv();
			env.set( GRB.DoubleParam.TimeLimit, timeLimit );
			//env.set( GRB.IntParam.Threads, numThreads );
			//env.set( GRB.DoubleParam.Heuristics, 1.0 );
			//env.set( GRB.DoubleParam.ImproveStartGap, Double.POSITIVE_INFINITY );
			//env.set( GRB.IntParam.MIPFocus, 1 );
			//env.set( GRB.IntParam.Presolve, 2 );
			//env.set( GRB.IntParam.PreDepRow, 1 );
			//env.set( GRB.IntParam.PreSparsify, 1 );
		}
		catch ( GRBException e )
		{
			promptGRBError( e );
		}
	}
	@Override
	public void setVars( VarType[] types, double[] lb, double[] ub )
	{
	}
	@Override
	public void abort()
	{
		abort = true;
	}
	@Override
	public void enable()
	{
		abort = false;
	}
	@Override
	public synchronized void setAbort( boolean abort )
	{
		this.abort = abort;
	}
	@Override
	public void setGeneExpr( Vector< Double > geneExpr )
	{
		this.geneExpr = geneExpr;
	}
	protected synchronized boolean aborted()
	{
		return this.abort;
	}
	protected abstract GRBCallback createGRBCallback();
	public SolverComponent getSolverComponent()
	{
		return component;
	}
	public ArrayList< Double > getObjectiveCoefs()
	{
		return this.objCoefs;
	}
	
	@Override
	public void disableErrors()
	{
		this.showErrorMessages = false;
	}
	
	@Override
	public void setModelCompressor( ModelCompressor compressor )
	{
		this.compressor = compressor;
	}
	
	@Override
	public void FVA( ArrayList< Double > objCoefs, Double objVal, ArrayList< Double > fbaSoln,
			ArrayList< Double > min, ArrayList< Double > max, SolverComponent component ) throws Exception
	{
		GRBEnv quad_env = null;
		GRBModel quad_model = null;
		try
		{
			AbstractParametersDialog params = GraphicalInterface.getGurobiParameters();
			quad_env = new GRBEnv();
			quad_env.set( GRB.IntParam.Threads, 
					Integer.valueOf( params.getParameter( GurobiParameters.NUM_THREADS_NAME ) ) );
				quad_env.set( GRB.IntParam.MIPFocus,
					Integer.valueOf( params.getParameter( GurobiParameters.MIPFOCUS_NAME ) ) );
				quad_env.set( GRB.DoubleParam.FeasibilityTol,
					Double.valueOf( params.getParameter( GurobiParameters.FEASIBILITYTOL_NAME ) ) );
				quad_env.set( GRB.DoubleParam.IntFeasTol,
					Double.valueOf( params.getParameter( GurobiParameters.INTFEASIBILITYTOL_NAME ) ) );
				quad_env.set( GRB.DoubleParam.Heuristics, 
					Double.valueOf( params.getParameter( GurobiParameters.HEURISTICS_NAME ) ) );
				quad_env.set( GRB.DoubleParam.OptimalityTol,
					Double.valueOf( params.getParameter( GurobiParameters.OPTIMALITYTOL_NAME ) ) );
				quad_env.set( GRB.IntParam.OutputFlag, 0 );
			quad_model = new GRBModel( quad_env );
			ArrayList< GRBVar > vars = new ArrayList< GRBVar >();
			
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
			
			SimpleProgressBar progress = new SimpleProgressBar( "Flux Variability Analysis", "Progress" );
			progress.progressBar.setIndeterminate( false );
			progress.progressBar.setMaximum( component.variableCount() );
			progress.progressBar.setValue( 0 );
			progress.progressBar.setStringPainted( true );
			progress.setAlwaysOnTop( true );
			progress.setLocationRelativeTo( null );
			
			for( int j = 0; j < component.variableCount(); ++j )
			{
				if( !progress.isVisible() ) {
					// this allows x button of Graphical Interface to work
					// correctly if progress closed
					LocalConfig.getInstance().fvaDone = true;
					throw new Exception( "Exit" );
				}
				progress.progressBar.setValue( j );
				// add the term to the objective expression
				GRBLinExpr objExpr = new GRBLinExpr();
				objExpr.addTerm( 1.0, vars.get( j ) );
				
				// set the objective to minimize the flux
				quad_model.setObjective( objExpr, GRB.MINIMIZE );
				
				// optimize the model
				quad_model.optimize();
				
				// add to the minimized flux vector
				min.add( vars.get( j ).get( GRB.DoubleAttr.X ) );
				
				// set the objective to maximize the flux
				quad_model.setObjective( objExpr, GRB.MAXIMIZE );
				
				// optimize the model
				quad_model.optimize();
				
				// add to the maximized flux vector
				max.add( vars.get( j ).get( GRB.DoubleAttr.X ) );
			}
			LocalConfig.getInstance().fvaDone = true;
			progress.setVisible( false );
			progress.dispose();
			
			// remove the extra constraint
			component.removeConstraint( component.constraintCount() - 1 );
			
			// clean up
		}
		catch( GRBException e )
		{
			promptGRBError( e );
			throw new Exception( e );
		}
		catch( Exception e )
		{
			e.printStackTrace();
			throw new Exception( e );
		}
		finally
		{
			if( quad_model != null )
				quad_model.dispose();
			if( quad_env != null )
				quad_env.dispose();
		}
	}
}