package edu.rutgers.MOST.Analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import edu.rutgers.MOST.data.*;
import edu.rutgers.MOST.optimization.solvers.*;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;

public class GDBB extends Thread {
	
	public static interface Callback
	{
		public abstract void invoke();
	}

	private ModelCompressor compressor = new ModelCompressor();
	private Callback finalizingCallback = null;
	private GDBBModel model;
	private MILSolver solver;
	private double maxObj;

	private ArrayList<Double> solution;

	public GDBB() {
		this.setSolver( SolverFactory.createGDBBSolver() );
		new Vector<String>();
	}

	public GDBB(GDBBModel m) {
		this.model = m;
		this.setSolver(SolverFactory.createGDBBSolver());
		new Vector<String>();
	}

	private void setVars()
	{
		
	}

	private void setConstraints()
	{
		// A matrix (see matlab code)
		ArrayList< Map< Integer, Double > > A = new ArrayList< Map< Integer, Double > >();
		int nmetab = this.model.getSMatrix().size();
		int nrxn = this.model.getReactions().size();
		int nbin = this.model.getGprMatrix().size();
		
//		r1: -speye(nrxn)               sparse(nrxn, nmetab)        sparse(nrxn, nrxn)             sparse(nrxn, nrxn)               sparse(nrxn, nrxn)         -fbamodel.G' .* repmat(fbamodel.vmin, 1, nbin)   ; 
//      r2:  speye(nrxn)               sparse(nrxn, nmetab)        sparse(nrxn, nrxn)             sparse(nrxn, nrxn)               sparse(nrxn, nrxn)          fbamodel.G' .* repmat(fbamodel.vmax, 1, nbin)   ;
//      r3:  sparse(nrxn, nrxn)        sparse(nrxn, nmetab)        sparse(nrxn, nrxn)             sparse(nrxn, nrxn)              -speye(nrxn)                -fbamodel.G' * MAXDUAL                           ;
//      r4:  sparse(nrxn, nrxn)        sparse(nrxn, nmetab)        sparse(nrxn, nrxn)             sparse(nrxn, nrxn)               speye(nrxn)                -fbamodel.G' * MAXDUAL                           ;
//      r5:  sparse(1, nrxn)           sparse(1, nmetab)           sparse(1, nrxn)                sparse(1, nrxn)                  sparse(1, nrxn)             ((y0(:, istart) == 0) - (y0(:, istart) == 1))'  ;
//      r6:  sparse(size(y, 2), nrxn)  sparse(size(y, 2), nmetab)  sparse(size(y, 2), nrxn)       sparse(size(y, 2), nrxn)         sparse(size(y, 2), nrxn)   -((y == 0) - (y == 1))'                          ;
//      r7:  sparse(1, nrxn)           sparse(1, nmetab)           sparse(1, nrxn)                sparse(1, nrxn)                  sparse(1, nrxn)             fbamodel.ko_cost'                               ; 
		
		
		// r1
		for( int i = 0; i < nrxn; ++i )
		{
			Map< Integer, Double > row = new HashMap< Integer, Double >();
			row.put( i, -1.0 );
			
			A.add( row );
		}
		
				
		
	}        

//  Setting Synthetic Objective Function
	private void setSyntheticObjective() {
		this.getSolver().setObjType(ObjType.Maximize);
		Vector<Double> objective = this.model.getSyntheticObjective();

		Map<Integer, Double> map = new HashMap<Integer, Double>();
		for (int i = 0; i < objective.size(); i++) {
			if (objective.elementAt(i) != 0.0) {
				map.put(i, objective.elementAt(i));
			}
		}
		this.getSolver().setObj(map);

	}

	public void setGDBBModel(GDBBModel m) {
		
		this.model = m;
		
		ArrayList< Double > lowerBounds = new ArrayList< Double >();
		ArrayList< Double > upperBounds = new ArrayList< Double >();
		for( SBMLReaction reac : m.getReactions() )
		{
			double lb = reac.getLowerBound();
			double ub = reac.getUpperBound();
			if( reac.getKnockout().equals( GraphicalInterfaceConstants.BOOLEAN_VALUES[1] ) )
			{
				lb = 0.0;
				ub = 0.0;
			}
			lowerBounds.add( lb );
			upperBounds.add( ub );
		}
		
		Vector< Double > objective = m.getObjective();
		Map< Integer, Double > mapObjective = new HashMap< Integer, Double >();
		for( int i = 0; i < objective.size(); i++)
		{
			if( objective.elementAt( i ) != 0.0 )
			{
				mapObjective.put( i, objective.elementAt( i ) );
			}
		}
		
		Vector< Double > syntheticObjective = m.getSyntheticObjective();
		Map< Integer, Double > mapSyntheticObjective = new HashMap< Integer, Double >();
		for( int i = 0; i < syntheticObjective.size(); i++)
		{
			if( syntheticObjective.elementAt( i ) != 0.0 )
			{
				mapSyntheticObjective.put( i, syntheticObjective.elementAt( i ) );
			}
		}
		
		compressor.setReactions( m.getReactions() );
		compressor.setMetabolites( m.getMetabolites() );
		compressor.setGeneAssociations( m.getGeneAssociations() );
		compressor.setgMatrix( m.getGprMatrix() );
		compressor.setsMatrix( m.getSMatrix() );
		compressor.setObjVec( mapObjective );
		compressor.setSynthObjVec( mapSyntheticObjective );
		compressor.setLowerBounds( lowerBounds );
		compressor.setUpperBounds( upperBounds );
		compressor.compressNet();
		
		for( int i = 0; i < m.getReactions().size(); ++i )
		{
			m.getReactions().get( i ).setLowerBound( lowerBounds.get( i ) );
			m.getReactions().get( i ).setUpperBound( upperBounds.get( i ) );
		}
		
		
		m.getSyntheticObjective().clear();
		m.getObjective().clear();
		mapObjective = compressor.getObjVec();
		mapSyntheticObjective = compressor.getSynthObjVec();
		
		for( int j = 0; j < m.getReactions().size(); ++j )
		{
			if( mapObjective.containsKey( j ) )
				m.getObjective().add( mapObjective.get( j ) );
			else
				m.getObjective().add( 0.0 );
			
			if( mapSyntheticObjective.containsKey( j ) )
				m.getSyntheticObjective().add( mapSyntheticObjective.get( j ) );
			else
				m.getSyntheticObjective().add( 0.0 );
		}
	
	}

//  public ArrayList<Double> run() {
	public void run() {
		this.setEnv(model.getTimeLimit(), model.getThreadNum());
		this.setVars();
		this.setConstraints();
		this.setSyntheticObjective();
		this.getSolver().setDataModel( this.model );
		this.model.setReactions( compressor.getReactions() );
		this.model.setMetabolites( compressor.getMetabolites() );
		this.solver.setModelCompressor( this.compressor );
		try
		{
			this.maxObj = this.getSolver().optimize();
			solution = this.getSolver().getSoln();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		finally
		{
			finalizingCallback.invoke();
		}
	}

	public void setTimeLimit(double timeLimit) {
	}

	private void setEnv(double timeLimit, int threadNum) {
		this.getSolver().setEnv(timeLimit, threadNum);
	}

	public double getMaxObj() {
		return this.maxObj;
	}

	public void stopGDBB() {
		this.getSolver().abort();
	}

	public ArrayList<Double> getSolution() {
		return solution;
	}

	public Solver getSolver() {
		return this.solver;
	}

	public void setSolver(MILSolver solver) {
		this.solver = solver;
	}

	public void enableGDBB() {
		this.getSolver().enable();
	}

	public void setFinalizingCallback( Callback callback )
	{
		this.finalizingCallback = callback;
	}
}

