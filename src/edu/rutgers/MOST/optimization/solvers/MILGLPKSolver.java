package edu.rutgers.MOST.optimization.solvers;

import org.apache.commons.lang3.ArrayUtils;
import org.gnu.glpk.GLPK;
import org.gnu.glpk.GLPKConstants;
import org.gnu.glpk.GlpkCallbackListener;
import org.gnu.glpk.glp_tree;

import edu.rutgers.MOST.data.GDBBModel;
import edu.rutgers.MOST.data.Solution;
import edu.rutgers.MOST.presentation.GraphicalInterface;
import edu.rutgers.MOST.presentation.GraphicalInterface.GDBBParam;

public class MILGLPKSolver extends GLPKSolver implements MILSolver, GlpkCallbackListener
{
	int idx = 1;
	private boolean firstSolution = true;
	private Double lastSol = Double.NaN;
	public MILGLPKSolver()
	{
		super();
	}
	
	void func()
	{
		soln.clear();
		int columnCount = GLPK.glp_get_num_cols( problem_tmp );
		for( int i = 1; i <= columnCount; ++i)
			soln.add( GLPK.glp_mip_col_val( problem_tmp, i ) );
		
		double[] darray = ArrayUtils.toPrimitive( soln
				.toArray( new Double[] {} ) );
		
		objval = compressor.getMaxSynthObj( darray );
		
		if( lastSol.equals( objval ) )
			return;
		lastSol = objval;
		// get the solution columns
		soln.clear();

		darray = compressor.decompress( darray );
		soln.clear();
		for( double d : darray )
			soln.add( d );
		
		Solution sn = new Solution( objval, darray );
		sn.setIndex( idx++ );
		GDBBParam param = new GDBBParam();
		param.solution = sn;
		param.model = (GDBBModel)this.dataModel;
		param.string = "success!";
		param.addFolder = firstSolution;
		param.maxObj = objval;
		firstSolution = false;
		GraphicalInterface.addGDBBSolution( param );
	}

	public void callback( glp_tree tree )
	{
		int reason = GLPK.glp_ios_reason( tree );
		if( aborted() )
		{
			GLPK.glp_ios_terminate( tree );
		}
		else if( reason == GLPKConstants.GLP_IBINGO )
		{
			func();
		}
	}
	
	public void postCheck()
	{
	}
}
