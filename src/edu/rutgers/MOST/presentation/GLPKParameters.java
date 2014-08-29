package edu.rutgers.MOST.presentation;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
		
		segmentedParameterlist.add( new ADRealSegmentJTextFieldParameter( INTFEASIBILITYTOL_NAME, INTFEASIBILITYTOL_MIN_VALUE,
				INTFEASIBILITYTOL_MAX_VALUE, INTFEASIBILITYTOL_DEFAULT_VALUE) );
		
		segmentedParameterlist.add( new ADRealSegmentJTextFieldParameter( DUALFEASIBILITYTOL_NAME, DUALFEASIBILITYTOL_MIN_VALUE,
				DUALFEASIBILITYTOL_MAX_VALUE, DUALFEASIBILITYTOL_DEFAULT_VALUE ) );
		
		segmentedParameterlist.add( new ADRealSegmentJTextFieldParameter( RELAXATION_NAME, RELAXATION_MIN_VALUE,
				RELAXATION_MAX_VALUE, RELAXATION_DEFAULT_VALUE ) );
		
		segmentedParameterlist.add( new ADRealSegmentJTextFieldParameter( RELOBJGAP_NAME, RELOBJGAP_MIN_VALUE,
				RELOBJGAP_MAX_VALUE, RELOBJGAP_DEFAULT_VALUE ) );
		
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
