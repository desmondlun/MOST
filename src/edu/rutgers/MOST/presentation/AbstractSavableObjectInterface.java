package edu.rutgers.MOST.presentation;

public interface AbstractSavableObjectInterface
{
	public abstract String getValue();
	public abstract String getName();
	public abstract void resetToDefault();
	public abstract void setValue( String string );
}
