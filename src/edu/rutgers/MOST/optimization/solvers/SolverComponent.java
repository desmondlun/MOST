package edu.rutgers.MOST.optimization.solvers;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

public class SolverComponent
{
	public class Constraint
	{
		public Double value = new Double( 0.0 );
		public ConType type = ConType.EQUAL;
		public ArrayList< Double > coefficients = new ArrayList< Double >();
	}
	public class Variable
	{
		public Double lb = new Double( 0.0 );
		public Double ub = new Double( 0.0 );
		VarType type;
	}
	
	public ArrayList< Constraint > constraints = new ArrayList< Constraint >();
	public ArrayList< Variable > variables = new ArrayList< Variable >();
	
	public boolean addVariable( VarType type, double lb, double ub )
	{
		Variable var = new Variable();
		var.lb = lb;
		var.ub = ub;
		var.type = type;
		variables.add( var );
		
		return true;
	}
	public boolean addConstraint( Map< Integer, Double > map, ConType conType, double value )
	{
		Constraint constraint = new Constraint();
		for( int j = 0; j < variables.size(); ++j )
			constraint.coefficients.add( new Double( 0.0 ) );
		
		constraint.type = conType;
		constraint.value = value;
		
		for( Entry< Integer, Double > term : map.entrySet() )
		{
			if( variables.size() < term.getKey() )
				return false;
			constraint.coefficients.set( term.getKey(), term.getValue() );
		}
		
		constraints.add( constraint );
		
		return true;
	}
	public boolean addConstraint( ArrayList< Double > coefs, ConType conType, double value )
	{
		if( coefs.size() != variables.size() )
			return false;
		
		Constraint constraint = new Constraint();
		constraint.type = conType;
		constraint.value = value;
		for( Double coef : coefs )
			constraint.coefficients.add( new Double( coef.doubleValue() ) );
		
		constraints.add( constraint );
		
		return true;
	}
	public SolverComponent clone()
	{
		SolverComponent clone = new SolverComponent();
		for( Constraint constraint : constraints )
		{
			Constraint constraintCopy = new Constraint();
			constraintCopy.value = new Double( constraint.value.doubleValue() );
			constraintCopy.type = constraint.type;
			for( Double coef : constraint.coefficients )
				constraintCopy.coefficients.add( new Double( coef.doubleValue() ) );
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
}
