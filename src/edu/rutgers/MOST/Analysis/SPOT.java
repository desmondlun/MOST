package edu.rutgers.MOST.Analysis;

import java.io.File;
import java.util.ArrayList;
import edu.rutgers.MOST.optimization.solvers.Algorithm;
import edu.rutgers.MOST.presentation.GraphicalInterface;

public class SPOT extends Analysis
{
	public SPOT()
	{
		super( Algorithm.SPOT );
	}

	public ArrayList< Double > run()
	{
		ModelFormatter modelFormatter = new ModelFormatter();
		File file = GraphicalInterface.chooseCSVFile( "Load Gene Expression Data" );
		modelFormatter.formatFluxBoundsfromGeneExpressionData( file, this.model );
		return super.run();
	}
}
