package edu.rutgers.MOST.presentation;

import javax.swing.JTextField;

public abstract class AbstractJTextFieldParameter extends JTextField implements AbstractSavableObjectInterface
{
	private static final long serialVersionUID = 1L;
	
	public AbstractJTextFieldParameter( String name )
	{
		this.setName( name );
	}
}
