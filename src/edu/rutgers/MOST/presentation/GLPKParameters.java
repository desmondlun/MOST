package edu.rutgers.MOST.presentation;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;

public class GLPKParameters
{
	public static final String TITLE = "GLPK Parameters";
	public static final String GLPK_PARAMETERS_MENU_ITEM = "Set GLPK Properties";
	
	// source of names of parameters
	// http://www.maximalsoftware.com/solvopt/optglpk.html
	
	public static final String FEASIBILITYTOL_NAME = "Feasibility Tolerance";
	public static final double FEASIBILITYTOL_DEFAULT_VALUE = 1.0E-9;
	public static final double FEASIBILITYTOL_MIN_VALUE = 1.0E-9;
	public static final double FEASIBILITYTOL_MAX_VALUE = Double.MAX_VALUE;
	
	public static final String INTFEASIBILITYTOL_NAME = "Integer Feasibility Tolerance";
	public static final double INTFEASIBILITYTOL_DEFAULT_VALUE = 1.0E-9;
	public static final double INTFEASIBILITYTOL_MIN_VALUE = 1.0E-9;
	public static final double INTFEASIBILITYTOL_MAX_VALUE = Double.MAX_VALUE;
	
	public static final String MIPGAP_NAME = "MIP Gap";
	public static final double MIPGAP_DEFAULT_VALUE = 1.0E-4;
	public static final double MIPGAP_MIN_VALUE = 1.0E-9;
	public static final double MIPGAP_MAX_VALUE = Double.MAX_VALUE;
	

	public static final String SAVE_TO_MPS_NAME = "Save to MPS File";
	
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
		
		segmentedParameterlist.add( new ADRealSegmentJTextFieldParameter( MIPGAP_NAME, MIPGAP_MIN_VALUE,
				MIPGAP_MAX_VALUE, MIPGAP_DEFAULT_VALUE ) );
		
		Box vBox = Box.createVerticalBox();
		
		Utilities u = new Utilities();
		
		// add used mnemonics to list
		ArrayList<String> usedMnemonics = new ArrayList<String>();
		for (int i = 0; i < AbstractParametersDialogConstants.USED_MNEMONICS.length; i++) {
			usedMnemonics.add(AbstractParametersDialogConstants.USED_MNEMONICS[i]);
		}
		
		for( JComponent segmentedParameter : segmentedParameterlist )
		{
			segmentedParameter.setPreferredSize( new Dimension( COMPONENT_WIDTH, COMPONENT_HEIGHT ) );
			JLabel label = new JLabel();
			label.setText( segmentedParameter.getName() );
			label.setPreferredSize( new Dimension( LABEL_WIDTH, LABEL_HEIGHT ) );
			String mnemonic = "";
			
			if( segmentedParameter instanceof ADRealSegmentJTextFieldParameter )
			{
				ADRealSegmentJTextFieldParameter param = (ADRealSegmentJTextFieldParameter)segmentedParameter;
				param.setToolTipText( "real values from " + param.minVal + " to " + param.maxVal );
				mnemonic = u.findMnemonic(usedMnemonics, segmentedParameter.getName());
			}
			
			if (mnemonic.length() > 0) {
				usedMnemonics.add(mnemonic.toUpperCase());
				label.setDisplayedMnemonic(mnemonic.charAt(0));
				label.setLabelFor(segmentedParameter);
			}
			
			JPanel content = new JPanel();
			Box hBox = Box.createHorizontalBox();
			hBox.add( label );
			hBox.add( segmentedParameter );
			content.add( hBox );
			vBox.add( content );
			
			components.add( (AbstractSavableObjectInterface)segmentedParameter );
		}
		
		// RadioButtons
		{
			ArrayList< AbstractButton > buttons = new ArrayList< AbstractButton >();
			buttons.add( new JRadioButton( Boolean.toString( true ), false ) );
			buttons.add( new JRadioButton( Boolean.toString( false ), true ) );
			// add mnemonics to buttons
			buttons.get(0).setMnemonic(KeyStroke.getKeyStroke(AbstractParametersDialogConstants.SAVE_TO_MPS_FILE_TRUE).getKeyCode());
			buttons.get(1).setMnemonic(KeyStroke.getKeyStroke(AbstractParametersDialogConstants.SAVE_TO_MPS_FILE_FALSE).getKeyCode());
			ADButtonGroup bg = new ADButtonGroup( SAVE_TO_MPS_NAME, buttons );
			components.add( bg );
			JLabel label = new JLabel();
			label.setText( bg.getName() );
			label.setPreferredSize( new Dimension( LABEL_WIDTH, LABEL_HEIGHT ) );
			
			Box hBox = Box.createHorizontalBox();
			JPanel panelLabel = new JPanel();
			panelLabel.add( label );
			hBox.add( panelLabel );
			for( JComponent buttonParam : buttons )
			{
				JPanel content = new JPanel();
				buttonParam.setPreferredSize( new Dimension( COMPONENT_WIDTH / buttons.size(), COMPONENT_HEIGHT ) );
				hBox.add( buttonParam );
				content.add( hBox );
			}
			JPanel p = new JPanel();
			p.setLayout( new BoxLayout( p, BoxLayout.X_AXIS ) );
			p.add( hBox );
			vBox.add( p );
		}
		
					
		
		JPanel panel = new JPanel();
		panel.add( vBox );
		result.add( panel );
		
		return result;
	}	
}
