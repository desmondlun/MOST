package edu.rutgers.MOST.presentation;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class AbstractParametersDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	private Box vContentBox = Box.createVerticalBox();
	protected ArrayList< AbstractSavableObjectInterface > parameters = new ArrayList< AbstractSavableObjectInterface >();
	JButton okButton = new JButton("   OK   ");
	JButton cancelButton = new JButton( "Cancel" );
	JButton resetButton = new JButton( "Reset to Defaults" );
	File saveFile = null;
	
	public AbstractParametersDialog( final String title, final File saveFile )
	{
		this.saveFile = saveFile;
		final ArrayList< Image > icons = new ArrayList< Image >();
		icons.add( new ImageIcon( "etc/most16.jpg" ).getImage() );
		icons.add( new ImageIcon( "etc/most32.jpg" ).getImage() );
		setIconImages( icons );
		setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		setSize( 100, 100 );
		setLocationRelativeTo( null );
		setTitle( title );
		add( vContentBox );
		setVisible( true );

		okButton.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent event )
			{
				saveParametersToFile( saveFile );
				dispose();
			}
		} );
		cancelButton.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent event )
			{
				dispose();
			}
		} );
		resetButton.addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent event )
			{
				resetToDefaults();
			}
		} );

	}
	
	public void saveParametersToFile( File saveFile )
	{
		if( saveFile == null )
			return;
		
		// TODO implement the save
	}
	
	public void add( JPanel panel, ArrayList< AbstractSavableObjectInterface > params )
	{
		vContentBox.add( panel );
		parameters.addAll( params );
		loadParameters();
		setVisible( true );
	}
	
	public void loadParameters()
	{
		if( saveFile == null )
		{
			resetToDefaults();
			return;
		}
		
		// TODO implement the load
	}
	
	public void finishSetup()
	{
		int spacingX = 15;
		Box hBox = Box.createHorizontalBox();
		hBox.add( okButton );
		hBox.add( Box.createRigidArea( new Dimension( spacingX, 0 ) ) );
		hBox.add( cancelButton );
		hBox.add( Box.createRigidArea( new Dimension( spacingX, 0 ) ) );
		hBox.add( resetButton );
		vContentBox.add( hBox );
		vContentBox.add( Box.createRigidArea( new Dimension( 0, 25 ) ) );
		pack();
		setLocationRelativeTo( null );
		setVisible( true );
	}
	
	public void resetToDefaults()
	{
		for( AbstractSavableObjectInterface param : parameters )
		{
			param.resetToDefault();
		}
	}
	
	public static void main( String[] args )
	{
		AbstractParametersDialog dialog = new AbstractParametersDialog( "Gurobi Parameters Test Dialog", null );
		IPoptParameters params = new IPoptParameters();
		dialog.add( params.getDialogPanel(), params.getSavableParameters() );
		dialog.finishSetup();
		dialog.setVisible( true );
	}
}
