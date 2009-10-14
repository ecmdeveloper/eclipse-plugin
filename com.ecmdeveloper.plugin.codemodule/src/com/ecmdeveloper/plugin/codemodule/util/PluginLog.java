package com.ecmdeveloper.plugin.codemodule.util;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.ecmdeveloper.plugin.codemodule.Activator;

public class PluginLog {

	public static void info(String message )
	{
		log( Status.INFO, IStatus.OK, message, null );
	}
	
	public static void error(Throwable exception) {
		error("Unexpected Exception", exception);
	}
	
	public static void error(String message, Throwable exception )
	{
		log( Status.ERROR, IStatus.OK, message, exception );
	}
	
	public static void log( int severity, int code, String message, Throwable exception)
	{
		log( createStatus(severity,code,message, exception ) );
	}
	
	public static IStatus createStatus( int severity, int code, String message, Throwable exception )
	{
		return new Status(severity,Activator.PLUGIN_ID, code, message, exception );
	}
	
	public static void log( IStatus status )
	{
		Activator.getDefault().getLog().log(status);
	}
}
