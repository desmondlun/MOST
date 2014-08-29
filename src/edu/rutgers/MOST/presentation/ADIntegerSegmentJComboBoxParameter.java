package edu.rutgers.MOST.presentation;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

public class ADIntegerSegmentJComboBoxParameter extends JComboBox< Integer > implements AbstractSavableObjectInterface
{
	private static final long serialVersionUID = 1L;
	
	private Integer defVal;
	
	public ADIntegerSegmentJComboBoxParameter( String name, Integer from, Integer to, Integer defVal )
	{
		setName( name );
		this.defVal = defVal;
		for( Integer integer = from; integer <= to; ++integer )
			addItem( integer );
	}
	@Override
	public String getValue()
	{
		this.getSelectedItem().toString();
		return null;
	}

	@Override
	public void resetToDefault()
	{
		this.setSelectedItem( defVal );
	}

	@Override
	public void setValue( String str )
	{
		try
		{
			Integer val = Integer.valueOf( str );
			setSelectedItem( val );
		}
		catch( Exception e )
		{
			JOptionPane.showMessageDialog( null,
					"Invalid value: " + str + " JComboBox could not set the value" , "Invalid Input",
					JOptionPane.WARNING_MESSAGE );
		}
	}

}
