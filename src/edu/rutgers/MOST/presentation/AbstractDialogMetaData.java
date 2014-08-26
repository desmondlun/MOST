package edu.rutgers.MOST.presentation;

public class AbstractDialogMetaData
{
	// layout
	public int dialogWidth = 400;
	public int dialogheight = 320;
	
	// layout constants
	public int componentWidth = 150;
	public int componentHeight = 25;
	public int labelWidth = 200;
	public int labelHeight = 25;
	
	public int labelTopBorderSize = 10;
	public int labelBottomBorderSize = 10;
	
	public static AbstractDialogMetaData getGenericMetaData()
	{
		return new AbstractDialogMetaData();
	}
}
