package edu.rutgers.MOST.presentation;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class AbstractParametersDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	private Box vContentBox = Box.createVerticalBox();
	protected ArrayList< AbstractSavableObjectInterface > parameters = new ArrayList< AbstractSavableObjectInterface >();
	private JButton okButton = new JButton("   OK   ");
	private JButton cancelButton = new JButton( "Cancel" );
	private JButton resetButton = new JButton( "Reset to Defaults" );
	private File saveFile = null;
	
	public String getParameter( String name )
	{
		for( AbstractSavableObjectInterface param : parameters )
			if( param.getName().equals( name ) )
				return param.getValue();
		return null;
	}
	
	public AbstractParametersDialog( final String name, final File saveFile )
	{
		this.saveFile = saveFile;
		final ArrayList< Image > icons = new ArrayList< Image >();
		icons.add( new ImageIcon( "etc/most16.jpg" ).getImage() );
		icons.add( new ImageIcon( "etc/most32.jpg" ).getImage() );
		setIconImages( icons );
		setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		setSize( 100, 100 );
		setLocationRelativeTo( null );
		setTitle( name + " Parameters" );
		setName( name );
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
		
		
		CSVWriter csvWriter = null;
		try
		{
			csvWriter = new CSVWriter( new FileWriter( saveFile ) );
			ArrayList< String[] > all = new ArrayList< String[] >();
			for( AbstractSavableObjectInterface param : parameters )
			{
				String[] keyVal = new String[]{ param.getName(), param.getValue() };
				all.add( keyVal );
			}
			csvWriter.writeAll( all );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				csvWriter.close();
			}
			catch ( Exception e )
			{
			}
		}
	}
	
	public void add( JPanel panel, ArrayList< AbstractSavableObjectInterface > params )
	{
		vContentBox.add( panel );
		parameters.addAll( params );
		loadParameters( saveFile );
		setVisible( true );
	}
	
	public void loadParameters( File loadFile )
	{
		if( saveFile == null )
		{
			resetToDefaults();
			return;
		}
		
		CSVReader csvReader = null;
		ArrayList< AbstractSavableObjectInterface > paramsCopy
			= new ArrayList< AbstractSavableObjectInterface >( parameters );
		try
		{
			csvReader = new CSVReader( new FileReader( loadFile ) );
			List< String[] > all = csvReader.readAll();
			for( AbstractSavableObjectInterface param : parameters )
			{
				for( String[] keyVal : all )
				{
					if( param.getName().equals( keyVal[ 0 ] ) )
					{
						param.setValue( keyVal[ 1 ] );
						paramsCopy.remove( param );
						break;
					}
				}
			}
			csvReader.close();
		}
		catch( FileNotFoundException e )
		{
			JOptionPane.showMessageDialog( null, "MOST could not find the " + getName()
				+ " settings file. A new settings file will be created.",
				getName() + " Settings", JOptionPane.OK_OPTION, null );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				csvReader.close();
			}
			catch ( Exception e )
			{
			}
			for( AbstractSavableObjectInterface param : paramsCopy )
				param.resetToDefault();
		}
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
		AbstractParametersDialog dialog = new AbstractParametersDialog( "Gurobi", new File( "Gurobi.properties" ) );
		GurobiParameters params = new GurobiParameters();
		dialog.add( params.getDialogPanel(), params.getSavableParameters() );
		dialog.finishSetup();
		dialog.setVisible( true );
	}
}
