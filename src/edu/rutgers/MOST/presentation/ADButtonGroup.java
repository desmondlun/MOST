package edu.rutgers.MOST.presentation;

import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;

public class ADButtonGroup extends ButtonGroup implements AbstractSavableObjectInterface
{
	private static final long serialVersionUID = 1L;
	private String name;
	ArrayList< AbstractButton > buttons;
	int defSelectedIdx;
	
	public ADButtonGroup( String name, ArrayList< AbstractButton > buttons )
	{
		this.name = name;
		this.buttons = buttons;
		for( AbstractButton button : buttons )
			add( button );
		for( int i = 0; i < buttons.size(); ++i )
			if( buttons.get( i ).isSelected() )
			{
				defSelectedIdx = i;
				break;
			}
		
	}

	@Override
	public String getValue()
	{
		for( AbstractButton button : buttons )
			if( button.isSelected() )
				return button.getText();
		return null;
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	@Override
	public void resetToDefault()
	{
		for( int i = 0; i < buttons.size(); ++i )
			buttons.get( i ).setSelected( false );
		buttons.get( defSelectedIdx ).setSelected( true );
	}

	@Override
	public void setValue( String string )
	{
		try
		{
			for( AbstractButton button : buttons )
				if( button.getText().equals( string ) )
				{
					button.setSelected( true );
					return;
				}
			resetToDefault();
		}
		catch( Exception e )
		{
			resetToDefault();
		}
	}
}
