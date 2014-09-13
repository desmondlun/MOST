package edu.rutgers.MOST.optimization.solvers;

import edu.rutgers.MOST.data.GDBBModel;
import edu.rutgers.MOST.data.ModelCompressor;
import edu.rutgers.MOST.data.Solution;
import edu.rutgers.MOST.presentation.GraphicalInterface;
import gurobi.GRB;
import gurobi.GRBCallback;
import gurobi.GRBException;

public class MILGurobiSolver extends GurobiSolver
{
	private int idx = 1;
	private boolean firstSolution = true;
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
						double[] vals = this.getSolution( model.getVars() );
						
						GraphicalInterface.GDBBParam param = new GraphicalInterface.GDBBParam();
						objval = compressor.getMaxSynthObj( vals );
						param.maxObj = objval;
						param.string = "success!";
						param.model = (GDBBModel)dataModel;
						param.solution = new Solution( param.maxObj, compressor.decompress( vals ) );
						param.solution.setIndex( idx++ );
						param.addFolder = firstSolution;
						firstSolution = false;
						
						GraphicalInterface.addGDBBSolution( param );
						// GDBB intermediate solutions
					/*	GDBB.getintermediateSolution().add( new Solution( this
								.getDoubleInfo( GRB.CB_MIPSOL_OBJ ), vals ) ); */
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
