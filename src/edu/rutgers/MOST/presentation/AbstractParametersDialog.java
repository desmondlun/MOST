package edu.rutgers.MOST.presentation;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class AbstractParametersDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	
	private ArrayList< AbstractTextSegmentedParameter > paramSegments = null;
	private ArrayList< JTextField > segmentedTextFields = new ArrayList< JTextField >();
	private Box vContentBox = Box.createVerticalBox();
	public JButton okButton = new JButton("   OK   ");
	public JButton cancelButton = new JButton( "Cancel" );
	public JButton resetButton = new JButton( "Reset to Defaults" );
	
	public void addSegmentedTextParameters( final ArrayList< AbstractTextSegmentedParameter > params,
		AbstractDialogMetaData meta )
	{
		final ArrayList< Image > icons = new ArrayList< Image >();
		icons.add( new ImageIcon( "etc/most16.jpg" ).getImage() );
		icons.add( new ImageIcon( "etc/most32.jpg" ).getImage() );
		setIconImages( icons );
		setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		setSize( meta.dialogWidth, meta.dialogheight );
		setLocationRelativeTo( null );
		setVisible( true );
		this.paramSegments = params;
		
    	for( final AbstractTextSegmentedParameter param : params )
		{
			Box hBox = Box.createHorizontalBox();
			hBox.setAlignmentX( CENTER_ALIGNMENT );
			
			// set the label
			final JLabel label = new JLabel();
			label.setText( param.name );
			JPanel panelLabel = new JPanel();
			panelLabel.setLayout( new BoxLayout( panelLabel, BoxLayout.X_AXIS ) );
			panelLabel.add( label );
			panelLabel.setBorder( BorderFactory.createEmptyBorder( 0, 0, 10, 0 ) );
			panelLabel.setAlignmentX( CENTER_ALIGNMENT );
			Dimension labDimension = new Dimension( meta.labelWidth, meta.labelHeight );
			label.setPreferredSize( labDimension );
			label.setMinimumSize( labDimension );
			label.setMaximumSize( labDimension );
			hBox.add( panelLabel );
			
			
			// set the TextField
			final JTextField field = new JTextField();
			this.segmentedTextFields.add( field );
			boolean isReal = param.defaultVal.getClass().equals( Double.class );
			field.setToolTipText( "Valid Range: " + (isReal ? "Real" : "Integer") +
					" values from " + param.minVal.toString() +" to " + param.maxVal.toString() );
			
			Dimension compDimension =  new Dimension( meta.componentWidth,
					meta.componentHeight );
			field.setPreferredSize( compDimension );
			field.setMaximumSize( compDimension );
			field.setMinimumSize( compDimension );
			field.addFocusListener( new FocusListener()
			{
				@Override
				public void focusGained( FocusEvent e )
				{
					Component c = e.getComponent();
					if( c instanceof JTextField )
						((JTextField)c).selectAll();
				}
				@Override
				public void focusLost( FocusEvent fe )
				{
					String text = field.getText();
					if( text != null )
					{
						if( !text.isEmpty() )
						{
							try
							{
								try
								{
									Class< ? extends Object > defClass = param.defaultVal.getClass();
									Object val = defClass.getMethod( "valueOf", String.class ).invoke( new Integer( 0), text );
									if( !param.checkVal( val ) )
									{
										throw new Exception( "value outside of domain" );
									}
									param.val = val;
								}
								catch( InvocationTargetException ie )
								{
									throw new Exception( param.defaultVal.getClass().toString() + " could not parse \"" + text + "\"" );
								}
							}
							catch( Exception e )
							{
								JOptionPane.showMessageDialog( null,
									"Invalid value: " + e.getMessage() , "Invalid Input",
									JOptionPane.WARNING_MESSAGE );
								Component c = fe.getComponent();
								if( c instanceof JTextField )
									((JTextField)c).setText( param.defaultVal.toString() );
								param.val = param.defaultVal;
							}
						}
					}
				}
			});
			field.getDocument().addDocumentListener( new DocumentListener()
			{
				public void changedUpdate(DocumentEvent e)
				{
					field.setForeground(Color.BLACK);
				}
				public void removeUpdate(DocumentEvent e)
				{
					field.setForeground(Color.BLACK);
				}
				public void insertUpdate(DocumentEvent e)
				{
					field.setForeground(Color.BLACK);
				}
			});

			JPanel panelField = new JPanel();
			panelField.add( field );
			panelField.setBorder( BorderFactory.createEmptyBorder( 0, 0, 10, 0 ) );
			panelField.setAlignmentX( RIGHT_ALIGNMENT );
			panelField.setLayout( new BoxLayout( panelField, BoxLayout.X_AXIS ) );
			hBox.add( panelField );
			
			vContentBox.add( hBox );
		}
    	
    	this.okButton.addActionListener( new ActionListener()
    	{
			@Override
			public void actionPerformed( ActionEvent event )
			{
			}
    	});
    	this.cancelButton.addActionListener( new ActionListener()
    	{
			@Override
			public void actionPerformed( ActionEvent event )
			{
			}
    	});
    	this.resetButton.addActionListener( new ActionListener()
    	{
			@Override
			public void actionPerformed( ActionEvent event )
			{
				resetToDefaults();
			}
    	});

    	Box buttonsBox = Box.createHorizontalBox();
    	buttonsBox.add( okButton );
    	buttonsBox.add( cancelButton );
    	buttonsBox.add( resetButton );
    	vContentBox.add( buttonsBox );
    	
    	JPanel panel = new JPanel();
    	panel.add( vContentBox );
    	this.getContentPane().add( panel, java.awt.BorderLayout.CENTER );
    	this.resetToDefaults();
	}

	public void resetToDefaults()
	{
		if( paramSegments != null && segmentedTextFields != null )
		{
			for( int i = 0; i < paramSegments.size(); ++i )
			{
				paramSegments.get( i ).val = paramSegments.get( i ).defaultVal;
				segmentedTextFields.get( i ).setText( paramSegments.get( i ).val.toString() );
			}
		}
	}
	
	public static void main( String[] args )
	{
		final ArrayList< Image > icons = new ArrayList< Image >();
		icons.add( new ImageIcon( "etc/most16.jpg" ).getImage() );
		icons.add( new ImageIcon( "etc/most32.jpg" ).getImage() );

		AbstractParametersDialog d = new AbstractParametersDialog();
		d.addSegmentedTextParameters( GurobiParameters.getSegmentedParameterList(),
				GurobiParameters.getAbstractDialogMetaData() );

		d.setIconImages( icons );
		d.setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		d.setSize( GurobiParameters.DIALOG_WIDTH,
				GurobiParameters.DIALOG_HEIGHT );
		d.setLocationRelativeTo( null );
		d.setVisible( true );
	}
}
