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

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.ecmdeveloper.plugin.search.Activator;
import com.ecmdeveloper.plugin.dialogs.ExceptionDetailsDialog;

/**
 * Facade class for the error message dialog.
 * 
 * @author Ricardo Belfor
 *
 */
public class PluginMessage {

	public static int openError(  Shell parentShell, String title, String message, Object details ) {
	
		ExceptionDetailsDialog dialog = new ExceptionDetailsDialog(parentShell,
				title, null, message, details, Activator.getDefault()
						.getBundle());

		return dialog.open();
	}

	public static void openErrorFromThread( String title, String message, Object details ) {
		openErrorFromThread(null, Display.getDefault(), title, message, details);
	}

	public static void openErrorFromThread( Shell parentShell, String title, String message, Object details ) {
		Display display;
		try {
			display = parentShell.getDisplay();
		} catch (org.eclipse.swt.SWTException  e) {
			// Assuming a " Widget is disposed" exception
			display = Display.getDefault();
		}
		openErrorFromThread(parentShell, display, title, message, details);
	}

	private static void openErrorFromThread( final Shell parentShell, Display display, final String title, final String message, final Object details ) {
		
		display.syncExec( new Runnable() {

			@Override
			public void run() {

				Shell shell;
				shell = parentShell == null ? Display.getDefault().getActiveShell() : parentShell; 
				ExceptionDetailsDialog dialog = new ExceptionDetailsDialog(shell,
						title, null, message, details, Activator.getDefault()
								.getBundle());

				dialog.open();
			}
		});
	}
}
