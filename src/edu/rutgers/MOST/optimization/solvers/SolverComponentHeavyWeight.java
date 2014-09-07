package edu.rutgers.MOST.optimization.solvers;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

public class SolverComponentHeavyWeight implements SolverComponent
{
	private class HeavyConstraint extends Constraint
	{
		public ArrayList< Double > coefs = new ArrayList< Double >();
		
		@Override
		public Double getCoefficient( int j )
		{
			return coefs.get( j );
		}		
	}

	ArrayList< Variable > variables = new ArrayList< Variable >();
	ArrayList< HeavyConstraint > constraints = new ArrayList< HeavyConstraint >();
	
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
		HeavyConstraint constraint = new HeavyConstraint();
		for( int j = 0; j < variables.size(); ++j )
			constraint.coefs.add( new Double( 0.0 ) );
		
		constraint.type = conType;
		constraint.value = value;
		
		for( Entry< Integer, Double > term : map.entrySet() )
		{
			if( variables.size() < term.getKey() )
				return false;
			constraint.coefs.set( term.getKey(), term.getValue() );
		}
		
		constraints.add( constraint );
		
		return true;
	}
	
	@Override
	public boolean addConstraint( ArrayList< Double > coefs, ConType conType, double value )
	{
		if( coefs.size() != variables.size() )
			return false;
		
		HeavyConstraint constraint = new HeavyConstraint();
		constraint.type = conType;
		constraint.value = value;
		for( Double coef : coefs )
			constraint.coefs.add( new Double( coef.doubleValue() ) );
		
		constraints.add( constraint );
		
		return true;
	}
	
	@Override
	public SolverComponentHeavyWeight clone()
	{
		SolverComponentHeavyWeight clone = new SolverComponentHeavyWeight();
		for( Constraint constraint : constraints )
		{
			HeavyConstraint constraintCopy = new HeavyConstraint();
			constraintCopy.value = new Double( constraint.getValue().doubleValue() );
			constraintCopy.type = constraint.type;
			
			for( int j = 0; j < variables.size(); ++j )
				constraintCopy.coefs.add( constraint.getCoefficient( j ) );
			
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
	public Variable getVariable( int j )
	{
		return variables.get( j );
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

	@Override
	public void compressNet()
	{		
	}
}
