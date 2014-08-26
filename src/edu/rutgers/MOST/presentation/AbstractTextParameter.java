package edu.rutgers.MOST.presentation;

import javax.swing.JTextField;

public abstract class AbstractTextParameter extends JTextField
{
	private static final long serialVersionUID = 1L;
	public String name;
	public AbstractTextParameter( String name )
	{
		this.name = name;
	}
}
