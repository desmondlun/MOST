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

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import edu.rutgers.MOST.presentation.ResizableDialog;
import edu.rutgers.MOST.presentation.GraphicalInterface;
import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.data.Solution;
import edu.rutgers.MOST.optimization.GDBB.GDBB;

import gurobi.GRB;
import gurobi.GRBCallback;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBModel;
import gurobi.GRBVar;
import gurobi.GRBLinExpr;

public class GurobiSolver extends Solver
{
	private enum SolverKind
	{
		FBASolver, GDBBSolver
	}

	private ArrayList< GRBVar > vars = new ArrayList< GRBVar >();
	private ArrayList< Double > soln = new ArrayList< Double >();
	private double objval;
	private GRBModel model;
	private GRBEnv env;
	private ObjType objType;
	private SolverKind solverKind = SolverKind.FBASolver;
	private ResizableDialog dialog = new ResizableDialog( "Error",
			"Gurobi Solver Error", "Gurobi Solver Error" );

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
		catch ( UnsatisfiedLinkError except )
		{
			return false; // gurobi does not link
		}

		return true; // gurobi does link
	}
	private void processStackTrace( Exception e )
	{
		//e.printStackTrace();
		StringWriter errors = new StringWriter();
		e.printStackTrace( new PrintWriter( errors ) );
		dialog.setErrorMessage( errors.toString() );
		// centers dialog
		dialog.setLocationRelativeTo(null);
		dialog.setModal(true);
		dialog.setVisible( true );
	}
	private void promptGRBError( GRBException e )
	{
		abort();
		String errMsg;
		int code = e.getErrorCode();
		switch( code )
		{
		case GRB.Error.NO_LICENSE:
			errMsg = "No validation file - run 'grbgetkey' to refresh it.";
			LocalConfig.getInstance().hasValidGurobiKey = false;
			break;
		case GRB.Error.FAILED_TO_CREATE_MODEL:
			errMsg = "Gurobi failed to create the model";
		case GRB.Error.NOT_SUPPORTED:
			errMsg = "This optimization is not supported by Gurobi";
			break;
		case GRB.Error.INVALID_ARGUMENT:
			errMsg = "Gurobi encountered an invalid argument";
			break;
		case GRB.Error.IIS_NOT_INFEASIBLE:
			errMsg = "Gurobi determined the IIS is not feasable";
			break;
		case GRB.Error.NUMERIC:
			errMsg = "Gurobi encountered a numerical error while optimizing the model";
			break;
		case GRB.Error.INTERNAL:
			errMsg = "Gurobi has encountered an internal error!";
			break;
		default:
			errMsg = "Gurobi encountered an error optimizing the model\nError Code: "
					+ code;
		}
		if( GraphicalInterface.getGdbbDialog() != null )
			GraphicalInterface.getGdbbDialog().setVisible( false );

		processStackTrace( new Exception( errMsg ) );
		LocalConfig.getInstance().getOptimizationFilesList().clear();
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
	private int getGRBObjType( ObjType objType )
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
			env = new GRBEnv();
			env.set( GRB.DoubleParam.IntFeasTol, 1.0E-9 );
			env.set( GRB.DoubleParam.FeasibilityTol, 1.0E-9 );
			env.set( GRB.IntParam.OutputFlag, 0 );
			model = new GRBModel( env );

			// set the callback
			model.setCallback( new GRBCallback()
			{
				@Override
				protected void callback()
				{
					try
					{
						if( abort )
							this.abort();
						else if( this.where == GRB.CB_SIMPLEX )
							objval = getDoubleInfo( GRB.CB_SPX_OBJVAL ); // FBA
																			// objective
						else if( this.where == GRB.CB_MIPSOL )
						{
							// GDBB intermediate solutions
							solverKind = SolverKind.GDBBSolver;
							GDBB.intermediateSolution.add( new Solution( this
									.getDoubleInfo( GRB.CB_MIPSOL_OBJ ), this
									.getSolution( model.getVars() ) ) );
							objval = getDoubleInfo( GRB.CB_MIPSOL_OBJ ); // MIP
																			// objective
						}
					}
					catch ( GRBException e )
					{
						processStackTrace( e );
					}
				}

			} );
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
	public void setVar( String varName, VarType types, double lb, double ub )
	{
		try
		{
			// column definitions
			if( varName == null || types == null || model == null )
				return;
			vars.add( model.addVar( lb, ub, 0.0, getGRBVarType( types ),
					varName ) );
			model.update();
		}
		catch ( GRBException e )
		{
			processStackTrace( e );
		}

	}
	@Override
	public void setObjType( ObjType objType )
	{
		this.objType = objType;
	}
	@Override
	public void setObj( Map< Integer, Double > map )
	{
		try
		{
			// objective definition
			GRBLinExpr expr = new GRBLinExpr();

			// add the terms
			for( Entry< Integer, Double > entry : map.entrySet())
				expr.addTerm( entry.getValue(), vars.get( entry.getKey() ) );

			// set the objective
			model.setObjective( expr, getGRBObjType( objType ) );
		}
		catch ( GRBException e )
		{
			processStackTrace( e );
		}

	}
	@Override
	public void addConstraint( Map< Integer, Double > map, ConType con,
			double value )
	{
		try
		{
			// row definitions
			GRBLinExpr expr = new GRBLinExpr();

			for( Entry< Integer, Double > entry : map.entrySet())
			{
				// add the terms in the expression
				int key = entry.getKey();
				double kvalue = entry.getValue();
				expr.addTerm( kvalue, vars.get( key ) );
			}
			// add the constraint
			model.addConstr( expr, getGRBConType( con ), value, null );
		}
		catch ( GRBException e )
		{
			processStackTrace( e );
		}
	}

	@Override
	public double optimize()
	{
		try
		{
			// preform the optimization and get the objective value
			model.optimize();
			if( !abort )
			{
				objval = model.get( GRB.DoubleAttr.ObjVal );
	
				// get the flux values
				for( GRBVar var : vars)
					soln.add( var.get( GRB.DoubleAttr.X ) );
			}

			// clean up
			model.dispose();
			env.dispose();
			vars.clear();
		}
		catch ( GRBException e )
		{
			promptGRBError( e );
			return Double.NaN;
		}

		return objval;
	}
	@Override
	public void setEnv( double timeLimit, int numThreads )
	{
		// TODO Auto-generated method stub
	}
	@Override
	public void setVars( VarType[] types, double[] lb, double[] ub )
	{
		// TODO Auto-generated method stub
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
	public void setAbort( boolean abort )
	{
		this.abort = abort;
	}
}