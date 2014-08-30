package edu.rutgers.MOST.presentation;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JOptionPane;

public class ADRealSegmentJTextFieldParameter extends AbstractJTextFieldParameter
{
	private static final long serialVersionUID = 1L;

	protected Double minVal;
	protected Double maxVal;
	protected Double defVal;
	protected Double value;

	protected void setIfValid( Double attempt )
	{

		if( minVal <= attempt && attempt <= maxVal )
		{
			value = attempt;
			setText( attempt.toString() );
		}
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
			setIfValid( Double.valueOf( str ) );
		}
		catch( Exception e )
		{
			JOptionPane.showMessageDialog( null,
					"Invalid value: " + str + " must be an real number" , "Invalid Input",
					JOptionPane.WARNING_MESSAGE );
			resetToDefault();
			selectAll();
		}
	}
	
	public String getValue()
	{
		return value.toString();
	}
	
	public ADRealSegmentJTextFieldParameter( String name, final Double minVal,
			final Double maxVal, final Double defVal )
	{
		super( name );
		this.minVal = minVal;
		this.maxVal = maxVal;
		this.defVal = defVal;
		this.value = defVal;
		super.addFocusListener( new FocusListener()
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
