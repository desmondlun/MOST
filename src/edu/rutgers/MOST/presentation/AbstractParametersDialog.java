package edu.rutgers.MOST.presentation;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class AbstractParametersDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	
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
    	
    	Box vBox = Box.createVerticalBox();
		
    	for( final AbstractSegmentedParameter param : params )
		{
			Box hBox = Box.createHorizontalBox();
			final JLabel label = new JLabel();
			final JTextField field = new JTextField();
			field.setToolTipText( "Valid Range: " +
					param.minVal.toString() +" to " + param.maxVal.toString() );

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
				public void focusLost( FocusEvent e )
				{
					String text = field.getText();
					if( text != null )
					{
						if( !text.isEmpty() )
						{
							param.val = param.defaultVal.getClass().equals( Double.class ) ?
								Double.valueOf( text ) : Integer.valueOf( text );
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
			panelLabel.setAlignmentX( LEFT_ALIGNMENT );
			hBox.add( panelLabel );

			JPanel panelField = new JPanel();
			panelField.add( field );
			panelField.setBorder( BorderFactory.createEmptyBorder( 0, 0, 10, 0 ) );
			panelField.setAlignmentX( RIGHT_ALIGNMENT );
			panelField.setLayout( new BoxLayout( panelField, BoxLayout.X_AXIS ) );
			hBox.add( panelField );
			hBox.setAlignmentX( LEFT_ALIGNMENT );
			vBox.add( hBox );
		}
    	
    	JPanel panel = new JPanel();
    	panel.add( vBox );
    	this.getContentPane().add( panel, java.awt.BorderLayout.CENTER );
	}

	public static void main( String[] args )
	{
		final ArrayList< Image > icons = new ArrayList< Image >();
		icons.add( new ImageIcon( "etc/most16.jpg" ).getImage() );
		icons.add( new ImageIcon( "etc/most32.jpg" ).getImage() );

		AbstractParametersDialog d = new AbstractParametersDialog();
		d.setUp( GLPKParameters.getSegmentedParameterList(),
				GLPKParameters.getAbstractDialogMetaData() );

		d.setIconImages( icons );
		d.setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		d.setSize( GurobiParameters.DIALOG_WIDTH,
				GurobiParameters.DIALOG_HEIGHT );
		d.setLocationRelativeTo( null );
		d.setVisible( true );
	}
}
