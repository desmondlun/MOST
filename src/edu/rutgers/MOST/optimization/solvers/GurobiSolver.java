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

import org.coinor.Ipopt;

import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;
import edu.rutgers.MOST.presentation.ResizableDialog;
import edu.rutgers.MOST.presentation.GraphicalInterface;
import edu.rutgers.MOST.Analysis.GDBB;
import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.data.Solution;
import gurobi.GRB;
import gurobi.GRBCallback;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBModel;
import gurobi.GRBQuadExpr;
import gurobi.GRBVar;
import gurobi.GRBLinExpr;

public class GurobiSolver extends Ipopt implements Solver
{
	private ArrayList< Double > soln = new ArrayList< Double >();
	Vector< Double > geneExpr = new Vector< Double >();
	private double objval;
	private GRBEnv env = null;
	private ObjType objType;
	private ResizableDialog dialog = new ResizableDialog( "Error",
			"Gurobi Solver Error", "Gurobi Solver Error" );
	private Algorithm algorithm;
	private boolean abort = false;
	private SolverComponent component = new SolverComponent();
	private Vector< Double > objCoefs = new Vector< Double >();
	
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
	private void processStackTrace( Exception e )
	{
		//e.printStackTrace();
		StringWriter errors = new StringWriter();
		e.printStackTrace( new PrintWriter( errors ) );
		dialog.setErrorMessage( errors.toString() + "</p></html>" );
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

	public GurobiSolver( Algorithm algorithm )
	{
		// set the dialog
		this.algorithm = algorithm;
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
			if( env  == null )
				env = new GRBEnv();
			env.set( GRB.DoubleParam.IntFeasTol, 1.0E-9 );
			env.set( GRB.DoubleParam.FeasibilityTol, 1.0E-9 );
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
			for( int j = 0; j < component.variables.size(); ++j )
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

	private double minimizeEuclideanNorm()
	{
		double result = 0.0;
		
		try
		{
			// set up the quadratic environment model
			GRBEnv quad_env = new GRBEnv();
			GRBModel quad_model = new GRBModel( env );
			ArrayList< GRBVar > vars = new ArrayList< GRBVar >();
			quad_env.set( GRB.DoubleParam.IntFeasTol, 1.0E-9 );
			quad_env.set( GRB.DoubleParam.FeasibilityTol, 1.0E-9 );
			quad_env.set( GRB.IntParam.OutputFlag, 0 );
			
			// create the variables
			for( SolverComponent.Variable var : component.variables )
			{
				vars.add( quad_model.addVar( var.lb, var.ub, 0.0, getGRBVarType( var.type ),
						null ) );
			}
			quad_model.update();
			
			// add rows / constraints
			for( SolverComponent.Constraint constraint : component.constraints )
			{
				GRBLinExpr expr = new GRBLinExpr();
				for( int j = 0; j < component.variables.size(); ++j )
				{
					expr.addTerm( constraint.coefficients.get( j ), vars.get( j ) );
				}
				quad_model.addConstr( expr, getGRBConType( constraint.type ), constraint.value, null );
			}
			
			// add the Maximum objective constraint
			GRBLinExpr maxObj = new GRBLinExpr();
			for( int j = 0; j < component.variables.size(); ++j )
				maxObj.addTerm( component.objectiveCoefs.get( j ), vars.get( j ) );
			GRBVar objValue = quad_model.addVar( this.objval, this.objval, 0.0, GRB.CONTINUOUS, null );
			quad_model.update(); // due to adding a new variable
			quad_model.addConstr( maxObj, GRB.EQUAL, objValue, null );
			
			// set the objective
			GRBQuadExpr expr = new GRBQuadExpr();
			for( GRBVar var : quad_model.getVars() ) 
				expr.addTerm( 1.0, var, var );
			quad_model.setObjective( expr );
			
			// optimize the model
			quad_model.optimize();
			
			// get min of sum of min v^2
			// objval = result = quad_model.get( GRB.DoubleAttr.ObjVal );
			
			soln.clear();
			
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
		
		return result;
	}
	@Override
	public double optimize()
	{
		try
		{
			final GRBModel model = new GRBModel( env );
			ArrayList< GRBVar > vars = new ArrayList< GRBVar >();
			
			try
			{
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
							else if( this.where == GRB.CB_SIMPLEX ) //FBA
								objval = getDoubleInfo( GRB.CB_SPX_OBJVAL );
							else if( this.where == GRB.CB_MIPSOL ) //MIP
							{
								// GDBB intermediate solutions
								GDBB.intermediateSolution.add( new Solution( this
										.getDoubleInfo( GRB.CB_MIPSOL_OBJ ), this
										.getSolution( model.getVars() ) ) );
								objval = getDoubleInfo( GRB.CB_MIPSOL_OBJ ); 
							}
						}
						catch ( GRBException e )
						{
							processStackTrace( e );
						}
					}
				} );
				
				// add columns
				for( SolverComponent.Variable var : component.variables )
				{
					vars.add( model.addVar( var.lb, var.ub, 0.0, getGRBVarType( var.type ),
							null ) );
				}
				model.update();
				
				
				for( SolverComponent.Constraint constraint : component.constraints )
				{
					GRBLinExpr expr = new GRBLinExpr();
					for( int j = 0; j < constraint.coefficients.size(); ++j )
						expr.addTerm( constraint.coefficients.get( j ), vars.get( j ) );
					model.addConstr( expr, getGRBConType( constraint.type ), constraint.value, null );
				}
				
				
				// set the objective
				GRBLinExpr expr = new GRBLinExpr();
	
				// set the terms & coefficients defining the objective function
				for( int j = 0; j < component.variables.size(); ++j )
					expr.addTerm( objCoefs.get( j ), vars.get( j ) );
	
				// set the objective
				model.setObjective( expr, getGRBObjType( objType ) );
				
				// perform the optimization and get the objective value
				model.optimize();				
				
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
					
					if( getAlgorithm() == Algorithm.Eflux2 )
						this.minimizeEuclideanNorm();
					
					if( getAlgorithm() == Algorithm.SPOT )
					{
						// set up the nlp
						double[] x_L = new double[ component.variables.size() ];
						double[] x_U = new double[ component.variables.size() ];
						
						// set the var upper and lower bounds
						for( int i = 0; i < component.variables.size(); ++i )
						{
							x_L[ i ] = component.variables.get( i ).lb;
							x_U[ i ] = component.variables.get( i ).ub;
						}
						
						double[] g_L = new double[ component.constraints.size() ];
						double[] g_U = new double[ component.constraints.size() ];
						
						// set the constraint upper and lower bounds
						for( int i = 0; i < component.constraints.size(); ++i )
						{
							if( component.constraints.get( i ).type == ConType.LESS_EQUAL )
							{
								g_L[ i ] = Double.NEGATIVE_INFINITY;
								g_U[ i ] = component.constraints.get( i ).value;
							}
							else if( component.constraints.get( i ).type == ConType.EQUAL )
							{
								g_L[ i ] = component.constraints.get( i ).value;
								g_U[ i ] = component.constraints.get( i ).value;
							}
							else if( component.constraints.get( i ).type ==ConType.GREATER_EQUAL )
							{
								g_L[ i ] = component.constraints.get( i ).value;
								g_U[ i ] = Double.POSITIVE_INFINITY;
							}
						}
						
						this.create( component.variables.size(), x_L, x_U, component.constraints.size(),
								g_L, g_U, component.constraints.size() * component.variables.size(),
								0, Ipopt.C_STYLE );
						double[] x = new double[ soln.size() ];
						for( int i = 0; i < soln.size(); ++i )
							x[ i ] = soln.get( i );
						
						this.addNumOption( KEY_OBJ_SCALING_FACTOR, -1.0 );
						this.addIntOption( "mumps_mem_percent", 500 );
						
						this.solve( x );
						double obj_value = 0;
						for( int j = 0; j < component.variables.size(); ++j )
							obj_value += component.objectiveCoefs.get( j ) * x[ j ];
					
						objval = obj_value;
						soln.clear();
						for( int j = 0; j < component.variables.size(); ++j )
							soln.add( x[ j ] );
							
						System.out.println( "success!" );
						System.out.println( "new obj is: " + objval );
						
					}
				}
			}
			catch( GRBException e )
			{
				throw e;
			}
			finally
			{
				// clean up
				component.variables.clear();
				component.constraints.clear();
				component.objectiveCoefs.clear();
				model.dispose();
				env.dispose();
				vars.clear();
			}
		}
		catch ( GRBException e )
		{
			promptGRBError( e );
			return Double.NaN;
		}

		return objval;
	}
	private Algorithm getAlgorithm()
	{
		return algorithm;
	}
	@Override
	public void setEnv( double timeLimit, int numThreads )
	{
		if( env != null )
			return;
		
		try
		{
			env = new GRBEnv();
			env.set( GRB.DoubleParam.Heuristics, 1.0 );
			env.set( GRB.DoubleParam.ImproveStartGap, Double.POSITIVE_INFINITY );
			env.set( GRB.DoubleParam.TimeLimit, timeLimit );
			env.set( GRB.IntParam.MIPFocus, 1 );
			env.set( GRB.IntParam.Threads, numThreads );
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
	public void setAbort( boolean abort )
	{
		this.abort = abort;
	}
	@Override
	protected boolean eval_f( int n, double[] x, boolean new_x,
			double[] obj_value )
	{
		Vector< Double > flux_v = new Vector< Double >();
		Vector< Double > gene_v = new Vector< Double >();
		
		for( int i = 0; i < component.constraints.size(); ++i )
		{
			Double g_i = geneExpr.get( i ); // updated from SPOT.run() and modelFormatter method
			Double v_i = 0.0;
			for( int j = 0; j < component.variables.size(); ++j )
				v_i += component.constraints.get( i ).coefficients.get( j ) * x[ j ];
			flux_v.add( v_i );
			gene_v.add( Double.isInfinite( g_i ) ? v_i : g_i );
		}
		
		// calculate the dot product between flux_v and gene_v
		double dotProduct = 0.0;
		assert( flux_v.size() == gene_v.size() );
		for( int i = 0; i < flux_v.size(); ++i )
			dotProduct += flux_v.get( i ) * gene_v.get( i );

		// calculate length of flux_v
		double length_flux_v = 0;
		for( Double v_i : flux_v )
			length_flux_v += v_i * v_i;
		length_flux_v = Math.sqrt( length_flux_v );

		// -1 <= ( flux_v dot gene_v ) / ( ||flux_v|| ) <= 1
		obj_value[ 0 ] = dotProduct / ( length_flux_v );
		return true;
	}
	@Override
	protected boolean eval_grad_f( int n, double[] x, boolean new_x,
			double[] grad_f )
	{
		for( int j = 0; j < component.variables.size(); ++j )
		{
			Vector< Double > flux_v = new Vector< Double >();
			Vector< Double > gene_v = new Vector< Double >();
			// fill in flux_v using variable 'x', fill in gene_v given value from file
	
			for( int i = 0; i < component.constraints.size(); ++i )
			{
				Double g_i = geneExpr.get( i );
				Double v_i = component.constraints.get( i ).coefficients.get( j );
				flux_v.add( v_i );
				gene_v.add( Double.isInfinite( g_i ) ? v_i : g_i );
			}
	
			// calculate the dot product between flux_v' and gene_v
			double dotProduct = 0.0;
			for( int i = 0; i < flux_v.size(); ++i )
				dotProduct += flux_v.get( i ) * gene_v.get( i );
	
			// calculate length of flux_v'
			double length_flux_v = 0;
			for( Double v_i : flux_v )
				length_flux_v += v_i * v_i;
			length_flux_v = Math.sqrt( length_flux_v );
	
			// -1 <= ( flux_v' dot gene_v ) / ( ||flux_v'|| ) <= 1
			grad_f[ j ] = dotProduct / ( length_flux_v );
		}
		
		return true;
	}
	@Override
	protected boolean eval_g( int n, double[] x, boolean new_x, int m,
			double[] g )
	{
		// define the constraints
		// Sv = 0.0 (steady state constraint)
		
		for( int i = 0; i < component.constraints.size(); ++i )
		{
			//
			double value = 0.0;
			for( int j = 0; j < component.variables.size(); ++j )
				value += component.constraints.get( i ).coefficients.get( j ) * x[ j ];
			g[ i ] = value;
		}
		
		return true;
	}
	@Override
	protected boolean eval_jac_g( int n, double[] x, boolean new_x, int m,
			int nele_jac, int[] iRow, int[] jCol, double[] values )
	{
		// define the jacobian of the constraints
		// [ d/dx_0 g_0, d/dx_1 g_0, ..., d/dx_n g_0 ]
		// [ d/dx_0 g_1, d/dx_1 g_1, ..., d/dx_n g_1  ]
		// [   ....   ]
		// [ d/dx_n g_n, d/dx_1 g_n, ..., d/dx_n g_n  ]
		
		if( values == null )
		{
			int idx = 0;
			for( int i = 0; i < component.constraints.size(); ++i )
			{
				for( int j = 0; j < component.variables.size(); ++j )
				{
					iRow[ idx ] = i;
					jCol[ idx ] = j;
					idx++;
				}
			}
		}
		else
		{
			int idx = 0;
			for( int i = 0; i < component.constraints.size(); ++i )
			{
				for( int j = 0; j < component.variables.size(); ++j )
					values[ idx++ ] = component.constraints.get( i ).coefficients.get( j );
			}
		}
		
		return true;
	}
	@Override
	protected boolean eval_h( int n, double[] x, boolean new_x,
			double obj_factor, int m, double[] lambda, boolean new_lambda,
			int nele_hess, int[] iRow, int[] jCol, double[] values )
	{
		return true;
	}
	@Override
	public void setGeneExpr( Vector< Double > geneExpr )
	{
		this.geneExpr = geneExpr;
	}
}