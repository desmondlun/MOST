package edu.rutgers.MOST.Analysis;

import java.util.ArrayList;

import edu.rutgers.MOST.optimization.solvers.*;
import edu.rutgers.MOST.presentation.GraphicalInterface;

public class Eflux2 extends Analysis
{	
 	public Eflux2()
	{
		super( Algorithm.Eflux2 );
	}
 	
 	public ArrayList< Double > run()
 	{
 		ModelFormatter modelFormatter = new ModelFormatter();
 		modelFormatter.formatFluxBoundsfromGeneExpressionData( GraphicalInterface.chooseCSVFile( "Load Gene Expressions" ), this.model );
 		return super.run();
 	}
}
