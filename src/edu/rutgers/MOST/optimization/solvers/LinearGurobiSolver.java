package edu.rutgers.MOST.optimization.solvers;

import gurobi.GRB;
import gurobi.GRBCallback;
import gurobi.GRBException;

public class LinearGurobiSolver extends GurobiSolver implements LinearSolver
{

	public LinearGurobiSolver()
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
					else if( this.where == GRB.CB_SIMPLEX ) //FBA
						objval = getDoubleInfo( GRB.CB_SPX_OBJVAL );
				}
				catch ( GRBException e )
				{
					processStackTrace( e );
				}
			}
		};
	}
	
}
