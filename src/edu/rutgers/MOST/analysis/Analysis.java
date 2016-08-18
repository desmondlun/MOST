package edu.rutgers.MOST.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import edu.rutgers.MOST.data.*;
import edu.rutgers.MOST.optimization.solvers.*;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;

public abstract class Analysis
{
	protected Model model = null;
	protected double maxObj = Double.NaN;
	private ModelCompressor compressor = new ModelCompressor();

	protected void setVars()
	{
	
		Vector< SBMLReaction > reactions = this.model.getReactions();
		for( int i = 0; i < reactions.size(); i++)
		{
			SBMLReaction reac = (SBMLReaction)( reactions.elementAt( i ) );
			String varName = Integer.toString( (Integer)this.model
					.getReactionsIdPositionMap().get( reac.getId() ) );
			// String varName = Integer.toString(reac.getId());

			this.getSolver().setVar( varName, VarType.CONTINUOUS, reac.getLowerBound(), reac.getUpperBound() );
		}
	
	}

	protected void setConstraints()
	{
		Vector< SBMLReaction > reactions = this.model.getReactions();
		setConstraints( reactions, ConType.EQUAL, 0.0 );
	}

	protected void setConstraints( Vector< SBMLReaction > reactions,
			ConType conType, double bValue )
	{
		ArrayList< Map< Integer, Double >> sMatrix = this.model.getSMatrix();
		for( SBMLReaction reac : reactions )
		{
			boolean ko = reac.getKnockout().equals(
					GraphicalInterfaceConstants.BOOLEAN_VALUES[1] );
			
			if( ko )
			{
				Map< Integer, Double > knockoutConstraint = new HashMap< Integer, Double >();
				knockoutConstraint.put( reac.getId(), 1.0 );
				sMatrix.add( knockoutConstraint );
			}
		}
		compressor.setsMatrix( sMatrix );
		compressor.setReactions( reactions );
		// compressor.compressNet();
		sMatrix = compressor.getsMatrix();
	
		
		for( int i = 0; i < sMatrix.size(); i++)
		{
			this.getSolver().addConstraint( sMatrix.get( i ), conType, bValue );
		}
	}

	protected void setObjective()
	{
		this.getSolver().setObjType( ObjType.Maximize );
		Vector< Double > objective = this.model.getObjective();
		Map< Integer, Double > map = new HashMap< Integer, Double >();
		for( int i = 0; i < objective.size(); i++)
		{
			if( objective.elementAt( i ) != 0.0 )
			{
				map.put( i, objective.elementAt( i ) );
			}
		}
		this.getSolver().setObj( map );
	}

	public void setModel( Model m )
	{
		this.model = m;
	}
	
	public void setSolverParameters()
	{
		this.setVars();
		this.setConstraints();
		this.setObjective();
	}
	
	public ArrayList< Double > run() throws Exception
	{
		this.setSolverParameters();
		this.maxObj = this.getSolver().optimize();

		return compressor.decompress( this.getSolver().getSoln() );
	}
	
	public abstract Solver getSolver();

	public double getMaxObj()
	{
		return this.maxObj;
	}
}
