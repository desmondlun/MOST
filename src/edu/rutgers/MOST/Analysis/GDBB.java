package edu.rutgers.MOST.Analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import edu.rutgers.MOST.data.*;
import edu.rutgers.MOST.optimization.solvers.*;

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

	private void setVarsAndObjective()
	{
		// the vars and objective (see matlab code)
		ArrayList< Double > c = new ArrayList< Double >();
		int nmetab = this.compressor.getSMatrix().size();
		int nrxn = this.compressor.getReactions().size();
		int nbin = this.compressor.getGprMatrix().size();
		
		// fbamodel.g;
        // zeros(nmetab, 1);
        // zeros(nrxn, 1);
        // zeros(nrxn, 1);
        // zeros(nrxn, 1);
        // zeros(nbin, 1);

		for( Double d : this.compressor.getSyntheticObjective() )
			c.add( d );
		
		for( int i = 0; i < 3 * nrxn + nmetab + nbin; ++i )
			c.add( 0.0 );
		
		Map< Integer, Double > obj = new HashMap< Integer, Double >();

		for( int i = 0; i < c.size(); ++i )
			if( !c.get( i ).equals( 0.0 ) )
				obj.put( i, c.get( i ) );
		
		/**************************************************/
		ArrayList< Double > lb = new ArrayList< Double >();
		
		//   fbamodel.vmin;
        //  -Inf * ones(nmetab, 1);
        //   zeros(nrxn, 1);
        //   zeros(nrxn, 1);
        //  -Inf * ones(nrxn, 1);
        //   zeros(nbin, 1);
		
		for( int i = 0; i < nrxn; ++i )
			lb.add( this.compressor.getReactions().get( i ).getLowerBound() );
		
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
			ub.add( compressor.getReactions().get( i ).getUpperBound() );
		
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
		
		for( int i = 0; i < nbin; ++i )
			varTypes.add( VarType.BINARY );
		
		/**************************************************/
		
		for( int i = 0; i < 4 * nrxn + nmetab + nbin; ++i )
			this.getSolver().setVar( null, varTypes.get( i ), lb.get( i ), ub.get( i ) );
		
		this.getSolver().setObjType( ObjType.Maximize );
		
		this.getSolver().setObj( obj );
		
	}
	
	private void setConstraints()
	{
		// A matrix (see matlab code)
		ArrayList< Map< Integer, Double > > A = new ArrayList< Map< Integer, Double > >();
		int nmetab = this.compressor.getSMatrix().size();
		int nrxn = this.compressor.getReactions().size();
		int nbin = this.compressor.getGprMatrix().size();
		double MAXDUAL = 100.0;
		ArrayList< Map< Integer, Double > > gTransposed = new ArrayList< Map< Integer, Double > >();
		for( int i = 0; i < nrxn; ++i )
		{
			Map< Integer, Double > con = new HashMap< Integer, Double >();
			for( int j = 0; j < nbin; ++j )
			{
				Double d = this.compressor.getGprMatrix().get( j ).get( i );
				if( d != null )
					con.put( j, d );
			}
			gTransposed.add( con );
		}
		ArrayList< Map< Integer, Double > > sTransposed = new ArrayList< Map< Integer, Double > >();
		for( int i = 0; i < nrxn; ++i )
		{
			Map< Integer, Double > con = new HashMap< Integer, Double >();
			for( int j = 0; j < nmetab; ++j )
			{
				Double d = this.compressor.getSMatrix().get( j ).get( i );
				if( d != null )
					con.put( j, d );
			}
			sTransposed.add( con );
		}
		
		
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
				Double gTVal = -noNull( gTransposed.get( i ).get( j ) )* this.compressor.getReactions().get( i ).getLowerBound() + 0.0;
				if( !gTVal.equals( 0.0 ) )
					row.put( j + 4 * nrxn + nmetab, gTVal );
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
				Double gTVal = noNull( gTransposed.get( i ).get( j ) )* this.compressor.getReactions().get( i ).getUpperBound() + 0.0;
				if( !gTVal.equals( 0.0 ) )
					row.put( j + 4 * nrxn + nmetab, gTVal );
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
				Double gTVal = -noNull( gTransposed.get( i ).get( j ) ) * MAXDUAL + 0.0;
				if( !gTVal.equals( 0.0 ) )
					row.put( j + 4 * nrxn + nmetab, gTVal );
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
				Double gTVal = -noNull( gTransposed.get( i ).get( j ) ) * MAXDUAL + 0.0;
				if( !gTVal.equals( 0.0 ) )
					row.put( j + 4 * nrxn + nmetab, gTVal );
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
				constraint.put( i, 6.0 );
			A.add( constraint );
		}
		
		/**************************************************/
		ArrayList< Map< Integer, Double > > Aeq = new ArrayList< Map< Integer, Double > >();
		
//      r1:  Aeq = [ fbamodel.S   sparse(nmetab, nmetab)      sparse(nmetab, nrxn)                             sparse(nmetab, nrxn)                             sparse(nmetab, nrxn)        sparse(nmetab, nbin);
//      r2:  sparse(nrxn, nrxn)   fbamodel.S'                -sparse(jmu, 1:nrxn, ones(nrxn, 1), nrxn, nrxn)   sparse(jnu, 1:nrxn, ones(nrxn, 1), nrxn, nrxn)   speye(nrxn)                 sparse(nrxn, nbin);
//      r3:  fbamodel.f'          sparse(1, nmetab)           fbamodel.vmin(jmu)'                             -fbamodel.vmax(jnu)'                              sparse(1, nrxn)             sparse(1, nbin); ];
		
		// r1
		for( Map< Integer, Double > m : this.compressor.getSMatrix() )
			Aeq.add( m );
		
		// r2
		for( int i = 0; i < nrxn; ++i )
		{
			Map< Integer, Double > constraint = new HashMap< Integer, Double >();
			for( int j = 0; j < nmetab; ++j )
			{
				Double valT = sTransposed.get( i ).get( j );
				if( valT != null )
					constraint.put( j + nrxn, valT );
			}
			
			constraint.put( i + 1 * nrxn + nmetab , -1.0 );
			constraint.put( i + 2 * nrxn + nmetab, 1.0 );
			constraint.put( i + 3 * nrxn + nmetab, 1.0 );
			Aeq.add( constraint );
		}
		
		// r3
		{
			Map< Integer, Double > constraint = new HashMap< Integer, Double >();
			for( int i = 0; i < this.compressor.getObjective().size(); ++i )
				if( !this.compressor.getObjective().get( i ).equals( 0.0 ) )
					constraint.put( i, this.compressor.getObjective().get( i ) );
			
			for( int i = 0; i < nrxn; ++i )
				if( this.compressor.getReactions().get( i ).getLowerBound() != 0.0 )
					constraint.put( 1 * nrxn + nmetab + i, this.compressor.getReactions().get( i ).getLowerBound() );
			
			for( int i = 0; i < nrxn; ++i )
				if( this.compressor.getReactions().get( i ).getUpperBound() != 0.0 )
					constraint.put( 2 * nrxn + nmetab + i, -this.compressor.getReactions().get( i ).getUpperBound() );
			
			Aeq.add( constraint );
		}
		
		/**************************************************/
		ArrayList< Double > b = new ArrayList< Double >();
		
		// -fbamodel.vmin;
        //  fbamodel.vmax;
        //  zeros(nrxn, 1);
        //  zeros(nrxn, 1);
        //  numknock;
        //  MAXKNOCK;
		
		for( int i = 0; i < nrxn; ++ i )
			b.add( -this.compressor.getReactions().get( i ).getLowerBound() );
		
		for( int i = 0; i < nrxn; ++ i )
			b.add( this.compressor.getReactions().get( i ).getUpperBound() );
		
		for( int i = 0; i < 2 * nrxn; ++i )
			b.add( 0.0 );
		
		b.add( this.model.getC() );
		
		b.add( 20.0 );
		
		/**************************************************/
		ArrayList< Double > beq = new ArrayList< Double >();
		
		//  zeros(nmetab, 1);
        //  fbamodel.f;
        //  0;
		
		for( int i = 0; i < nmetab; ++i )
			beq.add( 0.0 );
		
		for( int i = 0; i < nrxn; ++i )
			beq.add( this.compressor.getObjective().get( i ) );
		
		beq.add( 0.0 );
		
		/**We have everything necessary to put the constraints into the solver
		 *
		 */
		
	//	ModelCompressor.dump( "MOST-A.txt", A, 4*nrxn+nmetab+nbin );
	//	ModelCompressor.dump( "MOST-Aeq.txt", Aeq, 4*nrxn+nmetab+nbin );
		
	//	ModelCompressor.compareCSV( "MOST-A.txt", "Matlab-A.txt", "\t" );
	//	ModelCompressor.compareCSV( "MOST-Aeq.txt", "Matlab-Aeq.txt", "\t" );
		
		for( int i = 0; i < A.size(); ++i )
			this.getSolver().addConstraint( A.get( i ), ConType.LESS_EQUAL, b.get( i ) );
		
		for( int i = 0; i < Aeq.size(); ++i )
			this.getSolver().addConstraint( Aeq.get( i ), ConType.EQUAL, beq.get( i ) );
		
	}        
//  Setting Synthetic Objective Function
	

	public void setGDBBModel(GDBBModel m) {
		
		this.model = m;
		
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
	//	compressor.compressNet();	
	}

//  public ArrayList<Double> run() {
	
	public void run()
	{
		this.setEnv(model.getTimeLimit(), model.getThreadNum());
		this.setVarsAndObjective();
		this.setConstraints();
		this.getSolver().setDataModel( this.model );
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

	private void setEnv( double timeLimit, int threadNum ) {
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

