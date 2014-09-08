package edu.rutgers.MOST.optimization.solvers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SolverComponentLightWeight implements SolverComponent
{
	private class LightWeightConstraint extends Constraint
	{
		public Map< Integer, Double > coefs = new HashMap< Integer, Double >();
		
		@Override
		public Double getCoefficient( int j )
		{
			Double res = coefs.get( j );
			return res == null ? new Double( 0.0 ) : coefs.get( j );
		}		
	}
	
	private ArrayList< Variable > variables = new ArrayList< Variable >();
	private ArrayList< LightWeightConstraint > constraints = new ArrayList< LightWeightConstraint >();

	private int rowCount()
	{
		return constraints.size();
	}
	private int columnCount()
	{
		return variables.size();
	}
	private Double getMat( int i, int j )
	{
		return constraints.get( i ).getCoefficient( j );
	}
	private void setMat( int i, int j, double val )
	{
		constraints.get( i ).coefs.put( j, val );
	}
	
	/**
	 * Zeros the row out
	 * @param the row
	 */
	private void removeRow( int n )
	{
		/*
		for( Entry< Integer, Double > term : constraints.get( n ).coefs.entrySet() )
			term.setValue( 0.0 );
		*/
		constraints.remove( n );
	}
	
	/**
	 * Zeros the column out
	 * @param the column
	 */
	private void removeColumn( int n )
	{
		for( LightWeightConstraint con : constraints )
			for( Entry< Integer, Double > term : con.coefs.entrySet() )
				if( term.getKey().equals( n ) )
					term.setValue( 0.0 );
	}
	
	public void compressNet()
	{
		
		// keep only the columns that have a nonzero value
		// (Y-dimension) across the matrix
		for( int j = 0; j < columnCount(); ++j )
		{
			boolean isZeroColumn = true;
			for( int i = 0; i < rowCount(); ++i )
				if( getMat( i, j ) != 0.0 )
					isZeroColumn = false;
			if( isZeroColumn )
				this.removeColumn( j );
		}
		
		// remove the rows (reactions) that have only 1 nonzero column (flux)
		// due to steady-state constraint, it will optimize to be 0 anyway
		for( int i = 0; i < rowCount(); ++i )
		{
			ArrayList< Integer > cols = new ArrayList< Integer >();
			for( int j = 0; j < columnCount(); ++j )
				if( getMat( i, j ) != 0.0 )
					cols.add( j );
			if( cols.size() == 1 )
			{
				removeColumn( cols.get( 0 ) );
				removeRow( i );
			}
		}
		
	/*	for( boolean repeat = true; repeat; )
		{
			// find the rows that have only 2 nonzero columns (fluxes)
			// and merge them
			ArrayList< Integer > mergecols = new ArrayList< Integer >();
			ArrayList< Double > mergecoefs = new ArrayList< Double >();

			for( int i = 0; i < rowCount(); ++i )
			{
				rownum.add( i );
				for( int j = 0; j < columnCount(); ++j )
				{
					if( getMat( i, j ) != 0 )
					{
						mergecols.add( j );
						mergecoefs.add( getMat( i, j ) );
					}
				}
				if( mergecols.size() == 2 )
					break;
				else
				{
					mergecols.clear();
					mergecoefs.clear();
				}
			}
			
			if( mergecols.size() != 2 )
			{
				repeat = false;
				continue;
			}
			
			for( int i = 0; i < rowCount(); ++i )
			{
				double val0 = getMat( i, mergecols.get( 0 ) ); // current row
				double val1 = getMat( i, mergecols.get( 1 ) ); // current row
				setMat( i, mergecols.get( 0 ), val0 - ( val1 / mergecoefs.get( 1 ) * mergecoefs.get( 0 ) ) );
			}
		}
		
		*/
		
	}
	
	@Override
	public boolean addVariable( VarType type, double lb, double ub )
	{
		Variable var = new Variable();
		var.lb = lb;
		var.ub = ub;
		var.type = type;
		variables.add( var );
		
		return true;
	}

	@Override
	public boolean addConstraint( Map< Integer, Double > map, ConType conType, double value )
	{
		LightWeightConstraint constraint = new LightWeightConstraint();
		
		constraint.type = conType;
		constraint.value = value;
		
		constraint.coefs = map;
		
		constraints.add( constraint );
		
		return true;
	}

	@Override
	public boolean addConstraint( ArrayList< Double > coefs, ConType conType,
			double value )
	{
		
		LightWeightConstraint constraint = new LightWeightConstraint();
		
		constraint.type = conType;
		constraint.value = value;
		
		for( int j = 0; j < variables.size(); ++j )
			if( coefs.get( j ).doubleValue() != 0.0 )
				constraint.coefs.put( j, coefs.get( j ).doubleValue() );
		
		constraints.add( constraint );
		
		return true;
	}

	@Override
	public Variable getVariable( int j )
	{
		return variables.get( j );
	}

	@Override
	public SolverComponentLightWeight clone()
	{
		SolverComponentLightWeight clone = new SolverComponentLightWeight();
		for( LightWeightConstraint constraint : constraints )
		{
			LightWeightConstraint constraintCopy = new LightWeightConstraint();
			constraintCopy.value = new Double( constraint.getValue().doubleValue() );
			constraintCopy.type = constraint.type;
			
			for( Entry< Integer, Double > coefs : constraint.coefs.entrySet() )
				constraintCopy.coefs.put( coefs.getKey(), coefs.getValue() );
			
			clone.constraints.add( constraintCopy );
		}
		for( Variable variable : variables )
		{
			Variable variableCopy = new Variable();
			variableCopy.lb = new Double( variable.lb.doubleValue() );
			variableCopy.ub = new Double( variable.ub.doubleValue() );
			variableCopy.type = variable.type;
			clone.variables.add( variableCopy );
		}
		
		return clone;
	}

	@Override
	public Constraint getConstraint( int i )
	{
		return constraints.get( i );
	}

	@Override
	public int constraintCount()
	{
		return constraints.size();
	}

	@Override
	public int variableCount()
	{
		return variables.size();
	}
	
	@Override
	public boolean removeConstraint( int i )
	{
		constraints.remove( i );
		return true;
	}
}
