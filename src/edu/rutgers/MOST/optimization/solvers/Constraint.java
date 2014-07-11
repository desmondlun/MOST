package edu.rutgers.MOST.optimization.solvers;

public abstract class Constraint
{
	public Double value = new Double( 0.0 );
	public ConType type = ConType.EQUAL;
	
	/**
	 * Get the coefficient at the given term
	 * @param term The coefficient for the variable v_j
	 * @return The coefficient value
	 * @see Variable
	 */
	abstract Double getCoefficient( int j );
	
	/**
	 * Get the boundary value of the constraint
	 * @return The constraint value
	 */
	Double getValue()
	{
		return value;
	}
	
	/**
	 * Get the constraint type
	 * @return The type of constraint
	 * @see ConType
	 */
	ConType getConType()
	{
		return type;
	}
}
