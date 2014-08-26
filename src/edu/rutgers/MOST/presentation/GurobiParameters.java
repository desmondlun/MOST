package edu.rutgers.MOST.presentation;

import java.util.ArrayList;

import edu.rutgers.MOST.presentation.AbstractParameter.Type;

public class GurobiParameters {

	public static final String TITLE = "Gurobi Parameters";
	// names of parameters from http://www.gurobi.com/documentation/5.6/reference-manual/parameters
	
	public static final String GUROBI_PARAMETERS_MENU_ITEM = "Set Gurobi Properties";
	
	public static final String FEASIBILITYTOL_NAME = "Feasibility Tolerance";
	public static final double FEASIBILITYTOL_DEFAULT_VALUE = 1.0E-6;
	public static final double FEASIBILITYTOL_MINIMUM_VALUE = 1.0E-9;
	public static final double FEASIBILITYTOL_MAXIMUM_VALUE = 1.0E-2;
	
	public static final String INTFEASIBILITYTOL_NAME = "Integer Feasibility Tolerance";
	public static final double INTFEASIBILITYTOL_DEFAULT_VALUE = 1.0E-5;
	public static final double INTFEASIBILITYTOL_MINIMUM_VALUE = 1.0E-9;
	public static final double INTFEASIBILITYTOL_MAXIMUM_VALUE = 1.0E-1;
	
	public static final String OPTIMALITYTOL_NAME = "Dual Feasibility Tolerance";
	public static final double OPTIMALITYTOL_DEFAULT_VALUE = 1.0E-6;
	public static final double OPTIMALITYTOL_MINIMUM_VALUE = 1.0E-9;
	public static final double OPTIMALITYTOL_MAXIMUM_VALUE = 1.0E-2;
	
	public static final String HEURISTICS_NAME = "Heuristics";
	public static final double HEURISTICS_DEFAULT_VALUE = 0.05;
	public static final double HEURISTICS_MINIMUM_VALUE = 0;
	public static final double HEURISTICS_MAXIMUM_VALUE = 1;
	
	public static final String MIPFOCUS_NAME = "MIP Focus";
	public static final int MIPFOCUS_DEFAULT_VALUE = 0;
	public static final int MIPFOCUS_MINIMUM_VALUE = 0;
	public static final int MIPFOCUS_MAXIMUM_VALUE = 3;
	
	public static final String NUM_THREADS_NAME = "Number of Threads";
	public static final int MAX_NUM_THREADS = Runtime.getRuntime().availableProcessors();
	
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
			public RealSegmentParameter( String name, Type type, Double minVal,
					Double maxVal, Double defaultVal )
			{
				super( name, type, minVal, maxVal, defaultVal );
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
			public IntegerSegmentParameter( String name, Type type, Integer minVal,
					Integer maxVal, Integer defaultVal )
			{
				super( name, type, minVal, maxVal, defaultVal );
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
		class HalfIntegerSegmentParameter extends AbstractSegmentedParameter
		{
			public HalfIntegerSegmentParameter( String name, Type type, Double minVal,
					Double maxVal, Double defaultVal )
			{
				super( name, type, minVal, maxVal, defaultVal );
			}
			@Override
			public boolean checkVal( Object value )
			{
				if( value.getClass() == Double.class )
					if( (Double)value % 0.5 == 0.0 )
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
		ArrayList< AbstractSegmentedParameter > result = new ArrayList< AbstractSegmentedParameter >();
		
		result.add( new RealSegmentParameter( FEASIBILITYTOL_NAME, Type.TextField, FEASIBILITYTOL_MINIMUM_VALUE,
				FEASIBILITYTOL_MAXIMUM_VALUE, FEASIBILITYTOL_DEFAULT_VALUE) );
		
		result.add( new RealSegmentParameter( INTFEASIBILITYTOL_NAME, Type.TextField, INTFEASIBILITYTOL_MINIMUM_VALUE,
				INTFEASIBILITYTOL_MAXIMUM_VALUE, INTFEASIBILITYTOL_DEFAULT_VALUE) );
		
		result.add( new RealSegmentParameter( OPTIMALITYTOL_NAME, Type.TextField, OPTIMALITYTOL_MINIMUM_VALUE,
				OPTIMALITYTOL_MAXIMUM_VALUE, OPTIMALITYTOL_DEFAULT_VALUE) );
		
		result.add( new RealSegmentParameter( HEURISTICS_NAME, Type.TextField, HEURISTICS_MINIMUM_VALUE,
				HEURISTICS_MAXIMUM_VALUE, HEURISTICS_DEFAULT_VALUE ) );
		
		result.add( new IntegerSegmentParameter( MIPFOCUS_NAME, Type.TextField, MIPFOCUS_MINIMUM_VALUE,
				MIPFOCUS_MAXIMUM_VALUE, MIPFOCUS_DEFAULT_VALUE) );
		
		result.add( new IntegerSegmentParameter( NUM_THREADS_NAME, Type.TextField, 1,
				MAX_NUM_THREADS, MAX_NUM_THREADS ) );
		
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
