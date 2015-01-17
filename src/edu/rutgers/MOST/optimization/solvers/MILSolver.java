package edu.rutgers.MOST.optimization.solvers;

import edu.rutgers.MOST.data.ModelCompressor;

public interface MILSolver extends LinearSolver
{
	/**
	 * Returns the SolverComponent
	 * @return The SolverComponent
	 * @see SolverComponent
	 */
	public abstract SolverComponent getSolverComponent();
	
	public abstract void setModelCompressor( ModelCompressor compressor );
	
	public abstract void postCheck();
}
