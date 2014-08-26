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
	
	private ArrayList< AbstractSegmentedParameter > params = null;
	private ArrayList< JTextField > textFields = null;
	public JButton okButton = new JButton("   OK   ");
	public JButton cancelButton = new JButton( "Cancel" );
	public JButton resetButton = new JButton( "Reset to Defaults" );
	
	public void setUp( final ArrayList< AbstractSegmentedParameter > params,
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
		this.params = params;
		this.textFields = new ArrayList< JTextField >();
    	
    	Box vBox = Box.createVerticalBox();
		
    	for( final AbstractSegmentedParameter param : params )
		{
			Box hBox = Box.createHorizontalBox();
			final JLabel label = new JLabel();
			final JTextField field = new JTextField();
			this.textFields.add( field );
			boolean isReal = param.defaultVal.getClass().equals( Double.class );
			field.setToolTipText( "Valid Range: " + (isReal ? "Real" : "Integer") +
					" values from " + param.minVal.toString() +" to " + param.maxVal.toString() );

			Dimension labDimension = new Dimension( meta.labelWidth, meta.labelHeight );
			label.setPreferredSize( labDimension );
			label.setMinimumSize( labDimension );
			label.setMaximumSize( labDimension );
			
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
			label.setText( param.name );
			JPanel panelLabel = new JPanel();

			panelLabel.setLayout( new BoxLayout( panelLabel, BoxLayout.X_AXIS ) );
			panelLabel.add( label );
			panelLabel.setBorder( BorderFactory.createEmptyBorder( 0, 0, 10, 0 ) );
			panelLabel.setAlignmentX( CENTER_ALIGNMENT );
			hBox.add( panelLabel );

			JPanel panelField = new JPanel();
			panelField.add( field );
			panelField.setBorder( BorderFactory.createEmptyBorder( 0, 0, 10, 0 ) );
			panelField.setAlignmentX( RIGHT_ALIGNMENT );
			panelField.setLayout( new BoxLayout( panelField, BoxLayout.X_AXIS ) );
			hBox.add( panelField );
			hBox.setAlignmentX( CENTER_ALIGNMENT );
			vBox.add( hBox );
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
    	vBox.add( buttonsBox );
    	
    	JPanel panel = new JPanel();
    	panel.add( vBox );
    	this.getContentPane().add( panel, java.awt.BorderLayout.CENTER );
    	this.resetToDefaults();
	}

	public void resetToDefaults()
	{
		if( params != null && textFields != null )
		{
			for( int i = 0; i < params.size(); ++i )
			{
				params.get( i ).val = params.get( i ).defaultVal;
				textFields.get( i ).setText( params.get( i ).val.toString() );
			}
		}
	}
	
	public static void main( String[] args )
	{
		final ArrayList< Image > icons = new ArrayList< Image >();
		icons.add( new ImageIcon( "etc/most16.jpg" ).getImage() );
		icons.add( new ImageIcon( "etc/most32.jpg" ).getImage() );

		AbstractParametersDialog d = new AbstractParametersDialog();
		d.setUp( GurobiParameters.getSegmentedParameterList(),
				GurobiParameters.getAbstractDialogMetaData() );

		d.setIconImages( icons );
		d.setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		d.setSize( GurobiParameters.DIALOG_WIDTH,
				GurobiParameters.DIALOG_HEIGHT );
		d.setLocationRelativeTo( null );
		d.setVisible( true );
	}
}
