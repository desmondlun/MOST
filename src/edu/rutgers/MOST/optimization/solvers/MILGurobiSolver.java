package edu.rutgers.MOST.optimization.solvers;

import edu.rutgers.MOST.Analysis.GDBB;
import edu.rutgers.MOST.data.Solution;
import gurobi.GRB;
import gurobi.GRBCallback;
import gurobi.GRBException;

public class MILGurobiSolver extends GurobiSolver
{

	public MILGurobiSolver()
	{
		super();
	}

	@Override
	protected GRBCallback createGRBCallback()
	{
		return new GRBCallback()
		{
			@Override
			protected void callback()
			{
				try
				{
					if( aborted() )
						this.abort();
					else if( this.where == GRB.CB_MIPSOL ) //MIP
					{
						// GDBB intermediate solutions
						GDBB.getintermediateSolution().add( new Solution( this
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
		};
	}

}
