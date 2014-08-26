package edu.rutgers.MOST.presentation;

public abstract class AbstractParameter
{
	public static enum Type
	{
		ComboBox, TextField, CheckBox, RadioButton
	}
	
	public Type type;
	public String name;
	public AbstractParameter( String name, Type type )
	{
		this.name = name;
		this.type = type;
	}
}
