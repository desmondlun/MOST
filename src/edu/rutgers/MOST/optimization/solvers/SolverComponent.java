package edu.rutgers.MOST.optimization.solvers;

import java.util.ArrayList;
import java.util.Map;

public interface SolverComponent
{	
	public abstract boolean addVariable( VarType type, double lb, double ub );
	public abstract boolean addConstraint( Map< Integer, Double > map, ConType conType, double value );
	public abstract boolean addConstraint( ArrayList< Double > coefs, ConType conType, double value );
	public abstract boolean removeConstraint( int i );
	public abstract Variable getVariable( int j );
	public abstract Constraint getConstraint( int i );
	public abstract int constraintCount();
	public abstract int variableCount();
	public abstract SolverComponent clone();
}
