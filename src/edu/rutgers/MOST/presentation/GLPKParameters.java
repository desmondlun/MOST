package edu.rutgers.MOST.presentation;

import java.util.ArrayList;

public class GLPKParameters
{
	public static final String TITLE = "GLPK Parameters";
	public static final String GLPK_PARAMETERS_MENU_ITEM = "Set GLPK Properties";
	
	// source of names of parameters
	// http://www.maximalsoftware.com/solvopt/optglpk.html
	
	public static final String FEASIBILITYTOL_NAME = "Feasibility Tolerance";
	public static final double FEASIBILITYTOL_DEFAULT_VALUE = 1e-8;
	public static final double FEASIBILITYTOL_MIN_VALUE = 0.0;
	public static final double FEASIBILITYTOL_MAX_VALUE = Double.MAX_VALUE;
	
	public static final String INTFEASIBILITYTOL_NAME = "Integer Feasibility Tolerance";
	public static final double INTFEASIBILITYTOL_DEFAULT_VALUE = 1.0E-6;
	public static final double INTFEASIBILITYTOL_MIN_VALUE = 0.0;
	public static final double INTFEASIBILITYTOL_MAX_VALUE = Double.MAX_VALUE;
	
	public static final String DUALFEASIBILITYTOL_NAME = "Dual Feasability Tolerance";
	public static final double DUALFEASIBILITYTOL_DEFAULT_VALUE = 1E-8;
	public static final double DUALFEASIBILITYTOL_MIN_VALUE = 0.0;
	public static final double DUALFEASIBILITYTOL_MAX_VALUE = Double.MAX_VALUE;
	
	public static final String RELAXATION_NAME = "Relaxation";
	public static final double RELAXATION_DEFAULT_VALUE = 0.7;
	public static final double RELAXATION_MIN_VALUE = 0.0;
	public static final double RELAXATION_MAX_VALUE = Double.MAX_VALUE;
	
	public static final String RELOBJGAP_NAME = "Relative Objective Gap";
	public static final double RELOBJGAP_DEFAULT_VALUE = 1E-8;
	public static final double RELOBJGAP_MIN_VALUE = 0.0;
	public static final double RELOBJGAP_MAX_VALUE = Double.MAX_VALUE;
	
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
	
	public static ArrayList< AbstractTextSegmentedParameter > getSegmentedParameterList()
	{
		class RealSegmentParameter extends AbstractTextSegmentedParameter
		{
			private static final long serialVersionUID = 1L;
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
		ArrayList< AbstractTextSegmentedParameter > result = new ArrayList< AbstractTextSegmentedParameter >();
		
		result.add( new RealSegmentParameter( FEASIBILITYTOL_NAME, FEASIBILITYTOL_MIN_VALUE,
				FEASIBILITYTOL_MAX_VALUE, FEASIBILITYTOL_DEFAULT_VALUE) );
		
		result.add( new RealSegmentParameter( INTFEASIBILITYTOL_NAME, INTFEASIBILITYTOL_MIN_VALUE,
				INTFEASIBILITYTOL_MAX_VALUE, INTFEASIBILITYTOL_DEFAULT_VALUE) );
		
		result.add( new RealSegmentParameter( DUALFEASIBILITYTOL_NAME, DUALFEASIBILITYTOL_MIN_VALUE,
				DUALFEASIBILITYTOL_MAX_VALUE, DUALFEASIBILITYTOL_DEFAULT_VALUE) );
		
		result.add( new RealSegmentParameter( RELAXATION_NAME, RELAXATION_MIN_VALUE,
				RELAXATION_MAX_VALUE, RELAXATION_DEFAULT_VALUE ) );
		
		result.add( new RealSegmentParameter( RELOBJGAP_NAME, RELOBJGAP_MIN_VALUE,
				RELOBJGAP_MAX_VALUE, RELOBJGAP_DEFAULT_VALUE) );
		
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
