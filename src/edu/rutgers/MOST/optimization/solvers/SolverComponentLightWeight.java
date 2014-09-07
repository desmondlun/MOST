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

	private int rowSize()
	{
		return constraints.size();
	}
	private int columnSize()
	{
		return variables.size();
	}
	private Double getMat( int i, int j )
	{
		return constraints.get( i ).getCoefficient( j );
	}
	
	/**
	 * Zeros the row out
	 * @param the row
	 */
	private void removeRow( int n )
	{
		for( Entry< Integer, Double > term : constraints.get( n ).coefs.entrySet() )
			term.setValue( 0.0 );
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
		for( int j = 0; j < columnSize(); ++j )
		{
			boolean isZeroColumn = true;
			for( int i = 0; i < rowSize(); ++i )
				if( getMat( i, j ) != 0.0 )
					isZeroColumn = false;
			if( isZeroColumn )
				this.removeColumn( j );
		}
		
		for( int i = 0; i < rowSize(); ++i )
		{
			ArrayList< Integer > cols = new ArrayList< Integer >();
			for( int j = 0; j < columnSize(); ++j )
				if( getMat( i, j ) != 0.0 )
					cols.add( j );
			if( cols.size() == 1 )
				removeColumn( cols.get( 0 ) );
			
		}
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
