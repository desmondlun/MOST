package edu.rutgers.MOST.presentation;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

public class SimpleProgressBar extends JFrame
{
	private static final long serialVersionUID = 1L;
	public JProgressBar progressBar = new JProgressBar();
	public SimpleProgressBar( String title, String borderTitle )
	{
		super( title );
		progressBar.setBorder( BorderFactory.createTitledBorder( borderTitle ) );
		progressBar.setStringPainted( true );
		this.getContentPane().add( progressBar, BorderLayout.NORTH );
		this.setSize( 300, 100 );
		this.setVisible( true );
		this.setDefaultCloseOperation( DISPOSE_ON_CLOSE );
	}
	
	public static void main( String[] args )
	{
		SimpleProgressBar progress = new SimpleProgressBar( "Title example", "Border title example" );
		progress.progressBar.setIndeterminate( false );
		progress.progressBar.setMaximum( 150 );
		progress.progressBar.setValue( 0 );
		progress.progressBar.setStringPainted( true );
		
		try
		{
			while( progress.progressBar.getValue() < 150 )
			{
				progress.progressBar.setValue(  progress.progressBar.getValue() + 10 );
				Thread.sleep( 100 );
			}
		}
		catch ( InterruptedException e )
		{
			e.printStackTrace();
		}
		
		progress.dispose();
	}
}