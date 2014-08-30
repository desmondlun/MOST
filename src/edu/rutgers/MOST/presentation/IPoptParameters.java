package edu.rutgers.MOST.presentation;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class IPoptParameters
{
	public static final String TITLE = "IPopt Parameters";
	public static final String IPOPT_PARAMETERS_MENU_ITEM = "Set IPopt Properties";
	
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
	public static final double DUALFEASIBILITYTOL_DEFAULT_VALUE = 1.0;
	public static final double DUALFEASIBILITYTOL_MIN_VALUE = 0.0;
	public static final double DUALFEASIBILITYTOL_MAX_VALUE = Double.POSITIVE_INFINITY;
	
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
	
	protected ArrayList< AbstractSavableObjectInterface > components = new ArrayList< AbstractSavableObjectInterface >();
	
	public ArrayList< AbstractSavableObjectInterface > getSavableParameters()
	{
		return components;
	}
	
	public JPanel getDialogPanel()
	{		
		JPanel result = new JPanel();
		ArrayList< JComponent > segmentedParameterlist = new ArrayList< JComponent >();

		segmentedParameterlist.add( new ADRealSegmentJTextFieldParameter( FEASIBILITYTOL_NAME, FEASIBILITYTOL_MIN_VALUE,
				FEASIBILITYTOL_MAX_VALUE, FEASIBILITYTOL_DEFAULT_VALUE) );
		
		segmentedParameterlist.add( new ADIntegerSegmentJTextFieldParameter( MAXITER_NAME, MAXITER_MIN_VALUE,
				MAXITER_MAX_VALUE, MAXITER_DEFAULT_VALUE ) );
		
		segmentedParameterlist.add( new ADRealSegmentJTextFieldParameter( DUALFEASIBILITYTOL_NAME, DUALFEASIBILITYTOL_MIN_VALUE,
				DUALFEASIBILITYTOL_MAX_VALUE, DUALFEASIBILITYTOL_DEFAULT_VALUE ) );
		
		segmentedParameterlist.add( new ADRealSegmentJTextFieldParameter( CONSTRAINTOL_NAME, DUALFEASIBILITYTOL_MIN_VALUE,
				CONSTRAINTOL_MAX_VALUE, CONSTRAINTOL_DEFAULT_VALUE ) );
		
		segmentedParameterlist.add( new ADIntegerSegmentJTextFieldParameter( DIVITER_TOL_NAME, DIVITER_TOL_MIN_VALUE,
				DIVITER_TOL_MAX_VALUE, DIVITER_TOL_DEFAULT_VALUE) );
		
		Box vBox = Box.createVerticalBox();
		for( JComponent segmentedParameter : segmentedParameterlist )
		{
			segmentedParameter.setPreferredSize( new Dimension( COMPONENT_WIDTH, COMPONENT_HEIGHT ) );
			JLabel label = new JLabel();
			label.setText( segmentedParameter.getName() );
			label.setPreferredSize( new Dimension( LABEL_WIDTH, LABEL_HEIGHT ) );
			
			if( segmentedParameter instanceof ADRealSegmentJTextFieldParameter )
			{
				ADRealSegmentJTextFieldParameter param = (ADRealSegmentJTextFieldParameter)segmentedParameter;
				param.setToolTipText( "real values from " + param.minVal + " to " + param.maxVal );
			}
			else if ( segmentedParameter instanceof ADIntegerSegmentJTextFieldParameter )
			{
				ADIntegerSegmentJTextFieldParameter param = (ADIntegerSegmentJTextFieldParameter)segmentedParameter;
				param.setToolTipText( "real values from " + param.minVal + " to " + param.maxVal );
			
			}
			
			JPanel content = new JPanel();
			Box hBox = Box.createHorizontalBox();
			hBox.add( label );
			hBox.add( segmentedParameter );
			content.add( hBox );
			vBox.add( content );
			
			components.add( (AbstractSavableObjectInterface)segmentedParameter );
		}
		
		JPanel panel = new JPanel();
		panel.add( vBox );
		result.add( panel );
		
		return result;
	}
}
