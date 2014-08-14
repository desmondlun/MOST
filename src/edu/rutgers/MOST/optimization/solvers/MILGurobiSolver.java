package edu.rutgers.MOST.optimization.solvers;

import edu.rutgers.MOST.Analysis.GDBB;
import edu.rutgers.MOST.data.Solution;
import edu.rutgers.MOST.presentation.GraphicalInterface;
import gurobi.GRB;
import gurobi.GRBCallback;
import gurobi.GRBException;

public class MILGurobiSolver extends GurobiSolver
{
	private int idx = 1;
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
						GraphicalInterface.GDBBParam  param = new GraphicalInterface.GDBBParam();
						param.string = "success!";
						param.model = dataModel;
						param.solution = new Solution( this
								.getDoubleInfo( GRB.CB_MIPSOL_OBJ ), this
								.getSolution( model.getVars() ) );
						param.solution.setIndex( idx++ );
						GraphicalInterface.addGDBBSolution( param );
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
