package edu.rutgers.MOST.presentation;

import java.io.File;

public class GLPKParametersDialog extends AbstractParametersDialog
{
	private static final long serialVersionUID = 1L;

	public GLPKParametersDialog( File saveFile )
	{
		super( "GLPK", saveFile );
		GLPKParameters params = new GLPKParameters();
		add( params.getDialogPanel(), params.getSavableParameters() );
		finishSetup();
		setModal( true );
	}
}
