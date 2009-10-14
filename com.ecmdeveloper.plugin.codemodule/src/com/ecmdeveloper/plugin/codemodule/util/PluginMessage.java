package com.ecmdeveloper.plugin.codemodule.util;

import org.eclipse.swt.widgets.Shell;

import com.ecmdeveloper.plugin.codemodule.Activator;
import com.ecmdeveloper.plugin.dialogs.ExceptionDetailsDialog;

public class PluginMessage {

	public static int openError(  Shell parentShell, String title, String message, Object details ) {
	
		ExceptionDetailsDialog dialog = new ExceptionDetailsDialog(parentShell,
				title, null, message, details, Activator.getDefault()
						.getBundle());

		return dialog.open();
	}
}
