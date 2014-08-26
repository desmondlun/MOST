package edu.rutgers.MOST.presentation;

public abstract class AbstractSegmentedParameter extends AbstractParameter
{
	public Object minVal;
	public Object maxVal;
	public Object defaultVal;
	public Object val;
	
	public abstract boolean checkVal( Object value );

	public AbstractSegmentedParameter( String name, Object minVal, Object maxVal, Object defaultVal )
	{
		super( name );
		this.minVal = minVal;
		this.maxVal = maxVal;
		this.defaultVal = defaultVal;
		this.val = defaultVal;
	}
}
