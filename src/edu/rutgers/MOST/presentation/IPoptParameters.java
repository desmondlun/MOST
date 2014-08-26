package edu.rutgers.MOST.presentation;

import java.util.ArrayList;

public class IPoptParameters
{
	public static final String TITLE = "IPopt Parameters";
	public static final String GLPK_PARAMETERS_MENU_ITEM = "Set IPopt Properties";
	
	// source of names of parameters
	// http://www.coin-or.org/Ipopt/documentation/node41.html
	
	public static final String FEASIBILITYTOL_NAME = "Feasibility Tolerance";
	public static final double FEASIBILITYTOL_DEFAULT_VALUE = 1E-8;
	public static final double FEASIBILITYTOL_MIN_VALUE = 0.0;
	public static final double FEASIBILITYTOL_MAX_VALUE = Double.POSITIVE_INFINITY;
	
	public static final String MAXITER_NAME = "Maximum number of Iterations";
	public static final int MAXITER_DEFAULT_VALUE = 3000;
	public static final int MAXITER_MIN_VALUE = 0;
	public static final int MAXITER_MAX_VALUE = Integer.MAX_VALUE;
	
	public static final String DUALFEASIBILITYTOL_NAME = "Dual Feasibility Tolerance";
	public static final double DUALFEASABILITYTOL_DEFAULT_VALUE = 1.0;
	public static final double DUALFEASABILITYTOL_MIN_VALUE = 0.0;
	public static final double DUALFEASABILITYTOL_MAX_VALUE = Double.POSITIVE_INFINITY;
	
	public static final String CONSTRAINTOL_NAME = "Constraint Tolerance";
	public static final double CONSTRAINTOL_DEFAULT_VALUE = 1E-4;
	public static final double CONSTRAINTOL_MIN_VALUE = 0.0;
	public static final double CONSTRAINTOL_MAX_VALUE = Double.POSITIVE_INFINITY;
	
	public static final String DIVITER_TOL_NAME = "Diverging Iterates Tolerance";
	public static final int DIVITER_TOL_DEFAULT_VALUE = (int)1E20;
	public static final int DIVITER_TOL_MIN_VALUE = 0;
	public static final int DIVITER_TOL_MAX_VALUE = Integer.MAX_VALUE;
	
	// layout
	public static final int DIALOG_WIDTH = 400;
	public static final int DIALOG_HEIGHT = 320;
	
	// layout constants
	public static final int COMPONENT_WIDTH = 150;
	public static final int COMPONENT_HEIGHT = 25;
	public static final int LABEL_WIDTH = 200;
	public static final int LABEL_HEIGHT = 25;
	
	public static final int LABEL_TOP_BORDER_SIZE = 10;
	public static final int LABEL_BOTTOM_BORDER_SIZE = 10;
	
	public static ArrayList< AbstractSegmentedParameter > getSegmentedParameterList()
	{
		class RealSegmentParameter extends AbstractSegmentedParameter
		{
			public RealSegmentParameter( String name, Double minVal,
					Double maxVal, Double defaultVal )
			{
				super( name, minVal, maxVal, defaultVal );
			}
			@Override
			public boolean checkVal( Object value )
			{
				if( value.getClass() == Double.class )
				{
					Double min = (Double)minVal;
					Double max = (Double)maxVal;
					Double val = (Double)value;
					if( min <= val && val <= max )
						return true;
				}
				return false;
			}
		}
		class IntegerSegmentParameter extends AbstractSegmentedParameter
		{
			public IntegerSegmentParameter( String name, Integer minVal,
					Integer maxVal, Integer defaultVal )
			{
				super( name, minVal, maxVal, defaultVal );
			}
			@Override
			public boolean checkVal( Object value )
			{
				if( value.getClass() == Integer.class )
				{
					Integer min = (Integer)minVal;
					Integer max = (Integer)maxVal;
					Integer val = (Integer)value;
					if( min <= val && val <= max )
						return true;
				}
				return false;
			}
		}
		
		ArrayList< AbstractSegmentedParameter > result = new ArrayList< AbstractSegmentedParameter >();
		
		result.add( new RealSegmentParameter( FEASIBILITYTOL_NAME, FEASIBILITYTOL_MIN_VALUE,
				FEASIBILITYTOL_MAX_VALUE, FEASIBILITYTOL_DEFAULT_VALUE) );
		
		result.add( new IntegerSegmentParameter( MAXITER_NAME, MAXITER_MIN_VALUE,
				 MAXITER_MAX_VALUE,  MAXITER_DEFAULT_VALUE) );
		
		result.add( new RealSegmentParameter( DUALFEASIBILITYTOL_NAME, DUALFEASABILITYTOL_MIN_VALUE,
				DUALFEASABILITYTOL_MAX_VALUE, DUALFEASABILITYTOL_DEFAULT_VALUE) );
		
		result.add( new RealSegmentParameter( CONSTRAINTOL_NAME, CONSTRAINTOL_MIN_VALUE,
				CONSTRAINTOL_MAX_VALUE, CONSTRAINTOL_DEFAULT_VALUE ) );
		
		result.add( new IntegerSegmentParameter( DIVITER_TOL_NAME, DIVITER_TOL_MIN_VALUE,
				DIVITER_TOL_MAX_VALUE, DIVITER_TOL_DEFAULT_VALUE) );
		
		return result;
	}
	public static AbstractDialogMetaData getAbstractDialogMetaData()
	{
		AbstractDialogMetaData metaData = new AbstractDialogMetaData();
		metaData.componentHeight = COMPONENT_HEIGHT;
		metaData.componentWidth = COMPONENT_WIDTH;
		metaData.dialogheight = DIALOG_HEIGHT;
		metaData.dialogWidth = DIALOG_WIDTH;
		metaData.labelHeight = LABEL_HEIGHT;
		metaData.labelWidth = LABEL_WIDTH;
		metaData.labelTopBorderSize = LABEL_TOP_BORDER_SIZE;
		metaData.labelBottomBorderSize = LABEL_BOTTOM_BORDER_SIZE;
		return metaData;
	}
	
}
