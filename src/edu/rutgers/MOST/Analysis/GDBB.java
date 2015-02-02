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

	private Double noNull( Double d )
	{
		if( d == null )
			return 0.0;
		return d;
	}
	
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
		// the vars (see matlab code)
		ArrayList< Double > c = new ArrayList< Double >();
		int nmetab = this.model.getSMatrix().size();
		int nrxn = this.model.getReactions().size();
		int nbin = this.model.getGprMatrix().size();
		
		// fbamodel.g;
        // zeros(nmetab, 1);
        // zeros(nrxn, 1);
        // zeros(nrxn, 1);
        // zeros(nrxn, 1);
        // zeros(nbin, 1);
		
		for( Double d : this.model.getSyntheticObjective() )
			c.add( d );
		
		for( int i = 0; i < 3 * nrxn + nmetab + nbin; ++i )
			c.add( 0.0 );
		
		/**************************************************/
		ArrayList< Double > b = new ArrayList< Double >();
		
		// -fbamodel.vmin;
        //  fbamodel.vmax;
        //  zeros(nrxn, 1);
        //  zeros(nrxn, 1);
        //  numknock;
        //  MAXKNOCK;
		
		for( int i = 0; i < nrxn; ++ i )
			b.add( -this.model.getReactions().get( i ).getLowerBound() );
		
		for( int i = 0; i < nrxn; ++ i )
			b.add( this.model.getReactions().get( i ).getUpperBound() );
		
		for( int i = 0; i < 2 * nrxn; ++i )
			b.add( 0.0 );
		
		b.add( this.model.getC() );
		
		b.add( 20.0 );
		
		/**************************************************/
		ArrayList< Double > lb = new ArrayList< Double >();
		
		//   fbamodel.vmin;
        //  -Inf * ones(nmetab, 1);
        //   zeros(nrxn, 1);
        //   zeros(nrxn, 1);
        //  -Inf * ones(nrxn, 1);
        //   zeros(nbin, 1);
		
		for( int i = 0; i < nrxn; ++i )
			lb.add( this.model.getReactions().get( i ).getLowerBound() );
		
		for( int i = 0; i < nmetab; ++i )
			lb.add( Double.NEGATIVE_INFINITY );
		
		for( int i = 0; i < 2 * nrxn; ++i )
			lb.add( 0.0 );
		
		for( int i = 0; i < nrxn; ++i )
			lb.add( Double.NEGATIVE_INFINITY );
		
		for( int i = 0; i < nbin; ++i )
			lb.add( 0.0 );
		
		/**************************************************/
		ArrayList< Double > ub = new ArrayList< Double >();
		
		//   fbamodel.vmax;
        //   Inf * ones(nmetab, 1);
        //   Inf * ones(nrxn, 1);
        //   Inf * ones(nrxn, 1);
        //   Inf * ones(nrxn, 1);
		//   ones(nbin, 1)
		
		for( int i = 0; i < nrxn; ++i )
			ub.add( model.getReactions().get( i ).getUpperBound() );
		
		for( int i = 0; i < 3 * nrxn + nmetab; ++i )
			ub.add( Double.POSITIVE_INFINITY );
		
		for( int i = 0; i < nbin; ++i )
			ub.add( 1.0 );
		
		/**************************************************/
		ArrayList< VarType > varTypes = new ArrayList< VarType >();
		
		//  'C' * ones(nrxn, 1);
		//  'C' * ones(nmetab, 1);
		//  'C' * ones(nrxn, 1);
		//  'C' * ones(nrxn, 1);
		//  'C' * ones(nrxn, 1);
		//  'B' * ones(nbin, 1);
		
		for( int i = 0; i < 4 * nrxn + nmetab; ++i )
			varTypes.add( VarType.CONTINUOUS );
		
		for( int i = 0; i < nrxn; ++i )
			varTypes.add( VarType.BINARY );
		
	}
	
	private void setConstraints()
	{
		// A matrix (see matlab code)
		ArrayList< Map< Integer, Double > > A = new ArrayList< Map< Integer, Double > >();
		int nmetab = this.model.getSMatrix().size();
		int nrxn = this.model.getReactions().size();
		int nbin = this.model.getGprMatrix().size();
		double MAXDUAL = 100.0;
		
		
//		r1: -speye(nrxn)               sparse(nrxn, nmetab)        sparse(nrxn, nrxn)             sparse(nrxn, nrxn)               sparse(nrxn, nrxn)         -fbamodel.G' .* repmat(fbamodel.vmin, 1, nbin)   ; 
//      r2:  speye(nrxn)               sparse(nrxn, nmetab)        sparse(nrxn, nrxn)             sparse(nrxn, nrxn)               sparse(nrxn, nrxn)          fbamodel.G' .* repmat(fbamodel.vmax, 1, nbin)   ;
//      r3:  sparse(nrxn, nrxn)        sparse(nrxn, nmetab)        sparse(nrxn, nrxn)             sparse(nrxn, nrxn)              -speye(nrxn)                -fbamodel.G' * MAXDUAL                           ;
//      r4:  sparse(nrxn, nrxn)        sparse(nrxn, nmetab)        sparse(nrxn, nrxn)             sparse(nrxn, nrxn)               speye(nrxn)                -fbamodel.G' * MAXDUAL                           ;
//      r5:  sparse(1, nrxn)           sparse(1, nmetab)           sparse(1, nrxn)                sparse(1, nrxn)                  sparse(1, nrxn)             ((y0(:, istart) == 0) - (y0(:, istart) == 1))'  ;                     ;
//      r6:  sparse(1, nrxn)           sparse(1, nmetab)           sparse(1, nrxn)                sparse(1, nrxn)                  sparse(1, nrxn)             fbamodel.ko_cost'                               ; 
		
		
		// r1
		for( int i = 0; i < nrxn; ++i )
		{
			Map< Integer, Double > row = new HashMap< Integer, Double >();
			row.put( i, -1.0 );
			for( int j = 0; j < nbin; ++j )
			{
				Double gTVal = -noNull( this.model.getGprMatrix().get( j ).get( i ) ) * this.model.getReactions().get( i ).getLowerBound();
				if( !gTVal.equals( 0.0 ) )
					row.put( i + 4 * nrxn + nmetab, gTVal );
			}
			A.add( row );
		}
		
		// r2
		for( int i = 0; i < nrxn; ++i )
		{
			Map< Integer, Double > row = new HashMap< Integer, Double >();
			row.put( i, 1.0 );
			for( int j = 0; j < nbin; ++j )
			{
				Double gTVal = noNull( this.model.getGprMatrix().get( j ).get( i ) ) * this.model.getReactions().get( i ).getUpperBound();
				if( !gTVal.equals( 0.0 ) )
					row.put( i + 4 * nrxn + nmetab, gTVal );
			}
			A.add( row );
		}
		
		// r3
		for( int i = 0; i < nrxn; ++i )
		{
			Map< Integer, Double > row = new HashMap< Integer, Double >();
			row.put( 3 * nrxn + nmetab + i, -1.0 );
			for( int j = 0; j < nbin; ++j )
			{
				Double gTVal = -noNull( this.model.getGprMatrix().get( j ).get( i ) ) * MAXDUAL;
				if( !gTVal.equals( 0.0 ) )
					row.put( i + 3 * nrxn + nmetab, gTVal );
			}
			A.add( row );
		}
		
		// r4
		for( int i = 0; i < nrxn; ++i )
		{
			Map< Integer, Double > row = new HashMap< Integer, Double >();
			row.put( 3 * nrxn + nmetab + i, 1.0 );
			for( int j = 0; j < nbin; ++j )
			{
				Double gTVal = -noNull( this.model.getGprMatrix().get( j ).get( i ) ) * MAXDUAL;
				if( !gTVal.equals( 0.0 ) )
					row.put( i + 3 * nrxn + nmetab, gTVal );
			}
			A.add( row );
		}
		
		// r5
		{
			Map< Integer, Double > constraint = new HashMap< Integer, Double >();
			for( int i = 4 * nrxn + nmetab; i < 5 * nrxn + nmetab; ++i )
				constraint.put( i, 1.0 );
			A.add( constraint );
		}
		
		// r6
		{
			Map< Integer, Double > constraint = new HashMap< Integer, Double >();
			for( int i = 4 * nrxn + nmetab; i < 5 * nrxn + nmetab; ++i )
				constraint.put( i, 1.0 );
			A.add( constraint );
		}
		
		
		ArrayList< Map< Integer, Double > > Aeq = new ArrayList< Map< Integer, Double > >();
		
//      r1:  Aeq = [ fbamodel.S           sparse(nmetab, nmetab)      sparse(nmetab, nrxn)                             sparse(nmetab, nrxn)                             sparse(nmetab, nrxn)        sparse(nmetab, nbin);
//      r2:  sparse(nrxn, nrxn)   fbamodel.S'                -sparse(jmu, 1:nrxn, ones(nrxn, 1), nrxn, nrxn)   sparse(jnu, 1:nrxn, ones(nrxn, 1), nrxn, nrxn)   speye(nrxn)                 sparse(nrxn, nbin);
//      r3:  fbamodel.f'          sparse(1, nmetab)           fbamodel.vmin(jmu)'                             -fbamodel.vmax(jnu)'                              sparse(1, nrxn)             sparse(1, nbin); ];
		
		// r1
		for( Map< Integer, Double > m : this.model.getSMatrix() )
			Aeq.add( m );
		
		// r2
		for( int i = 0; i < nrxn; ++i )
		{
			Map< Integer, Double > constraint = new HashMap< Integer, Double >();
			for( int j = 0; j < nmetab; ++j )
			{
				Double valT = this.model.getSMatrix().get( j ).get( i );
				if( valT != null )
					constraint.put( j, valT );
			}
			Aeq.add( constraint );
		}
		
		// r3
		{
			Map< Integer, Double > constraint = new HashMap< Integer, Double >();
			for( int i = 0; i < this.model.getObjective().size(); ++i )
				if( this.model.getObjective().get( i ).equals( 0.0 ) )
					constraint.put( i, this.model.getObjective().get( i ) );
			
			for( int i = 0; i < nrxn; ++i )
				if( this.model.getReactions().get( i ).getLowerBound() != 0.0 )
					constraint.put( 1 * nrxn + nmetab + i, this.model.getReactions().get( i ).getLowerBound() );
			
			for( int i = 0; i < nrxn; ++i )
				if( this.model.getReactions().get( i ).getUpperBound() != 0.0 )
					constraint.put( 2 * nrxn + nmetab + i, -this.model.getReactions().get( i ).getUpperBound() );
					
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

