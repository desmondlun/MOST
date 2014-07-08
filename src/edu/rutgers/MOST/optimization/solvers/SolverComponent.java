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
	
	ArrayList< Constraint > constraints = new ArrayList< Constraint >();
	ArrayList< Variable > variables = new ArrayList< Variable >();
	ArrayList< Double > objectiveCoefs = new ArrayList< Double >();
	
	public boolean addVariable( VarType type, double lb, double ub )
	{
		Variable var = new Variable();
		var.lb = lb;
		var.ub = ub;
		var.type = type;
		variables.add( var );
		
		return true;
	}
	public boolean addConstraint(  Map< Integer, Double > map, ConType conType, double value )
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
	public boolean setObjective( Map< Integer, Double > map )
	{
		if( objectiveCoefs.size() == 0 )
		{
			for( int j = 0; j < variables.size(); ++j )
				objectiveCoefs.add( new Double( 0.0 ) );
		}
		
		for( Entry< Integer, Double > term : map.entrySet() )
		{
			if( objectiveCoefs.size() < term.getKey() )
				return false;
			objectiveCoefs.add( term.getKey(), term.getValue() );
		}
		
		return true;
	}
}
