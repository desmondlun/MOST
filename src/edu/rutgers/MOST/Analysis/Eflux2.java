package edu.rutgers.MOST.Analysis;

import edu.rutgers.MOST.optimization.solvers.*;
import edu.rutgers.MOST.presentation.GraphicalInterface;

public class Eflux2 extends Analysis
{
	private ModelFormatter modelFormatter = new ModelFormatter();
 	public Eflux2()
	{
		super( Algorithm.Eflux2 );
		modelFormatter.formatFluxBoundsfromGeneExpressionData( GraphicalInterface.chooseCSVFile( "Load Gene Expressions" ), this.model );
	}
}
