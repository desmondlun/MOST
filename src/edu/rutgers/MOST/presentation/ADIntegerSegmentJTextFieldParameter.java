package edu.rutgers.MOST.presentation;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JOptionPane;

public class ADIntegerSegmentJTextFieldParameter extends AbstractJTextFieldParameter
{
	private static final long serialVersionUID = 1L;

	protected Integer minVal;
	protected Integer maxVal;
	protected Integer defVal;
	protected Integer value;

	protected void setIfValid( Integer attempt )
	{

		if( minVal <= attempt && attempt <= maxVal )
			value = attempt;
		else
		{
			JOptionPane.showMessageDialog( null,
					"Invalid value: " + attempt + " is not in a valid domain" , "Invalid Input",
					JOptionPane.WARNING_MESSAGE );
			resetToDefault();
			selectAll();
		}
	}
	
	public void setValue( String str )
	{
		try
		{
			setIfValid( Integer.valueOf( str ) );
		}
		catch( Exception e )
		{
			JOptionPane.showMessageDialog( null,
					"Invalid value: " + str + " must be an integer" , "Invalid Input",
					JOptionPane.WARNING_MESSAGE );
			resetToDefault();
			selectAll();
		}
	}
	
	public String getValue()
	{
		return value.toString();
	}
	
	public ADIntegerSegmentJTextFieldParameter( String name, final Integer minVal,
			final Integer maxVal, final Integer defVal )
	{
		super( name );
		this.minVal = minVal;
		this.maxVal = maxVal;
		this.defVal = defVal;
		this.value = defVal;
		this.addFocusListener( new FocusListener()
		{
			@Override
			public void focusGained( FocusEvent event )
			{
				selectAll();
			}

			@Override
			public void focusLost( FocusEvent event )
			{
				if( !event.isTemporary() )
					setValue( getText() );
			}
		});
	}

	@Override
	public void resetToDefault()
	{
		this.value = this.defVal;
		this.setText( this.value.toString() );
	}
}
