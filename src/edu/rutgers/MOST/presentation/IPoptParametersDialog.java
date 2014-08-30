package edu.rutgers.MOST.presentation;

import java.io.File;

public class IPoptParametersDialog extends AbstractParametersDialog
{
	private static final long serialVersionUID = 1L;

	public IPoptParametersDialog( File saveFile )
	{
		super( "IPopt", saveFile );
		IPoptParameters params = new IPoptParameters();
		add( params.getDialogPanel(), params.getSavableParameters() );
		finishSetup();
		setModal( true );
	}
}
