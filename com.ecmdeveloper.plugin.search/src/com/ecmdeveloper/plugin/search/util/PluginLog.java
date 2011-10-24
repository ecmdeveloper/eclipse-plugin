/**
 * Copyright 2009, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in is
 * free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ECM Developer plug-in. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 */
package com.ecmdeveloper.plugin.search.util;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.ecmdeveloper.plugin.search.Activator;

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
