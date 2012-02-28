/**
 * Copyright 2012, Ricardo Belfor
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

package com.ecmdeveloper.plugin.codemodule.handlers;

import java.text.MessageFormat;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ecmdeveloper.plugin.codemodule.jobs.CreateEventActionJob;
import com.ecmdeveloper.plugin.codemodule.model.CodeModuleFile;
import com.ecmdeveloper.plugin.codemodule.wizard.NewEventActionWizard;
import com.ecmdeveloper.plugin.core.util.PluginMessage;

/**
 * @author Ricardo Belfor
 *
 */
public class CreateEventActionHandler extends AbstractHandler implements IHandler {

	private static final String NOT_SAVED_MESSAGE = "The Code Module ''{0}'' is not saved yet. Please save before trying to add an event action.";
	private static final String HANDLER_NAME = "Create Event Action";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if (window == null)	return null;

		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (!(selection instanceof IStructuredSelection) || selection.isEmpty() ) {
			return null;
		}

		Iterator<?> iterator = ((IStructuredSelection) selection).iterator();
		
		while ( iterator.hasNext() ) {
			CodeModuleFile codeModuleFile = (CodeModuleFile) iterator.next();
			if ( codeModuleFile.getId() == null ) {
				PluginMessage.openError(window.getShell(), HANDLER_NAME, MessageFormat.format(
						NOT_SAVED_MESSAGE, codeModuleFile.getName()), null);
				return null;
			}
			
			NewEventActionWizard wizard = new NewEventActionWizard(codeModuleFile);
			WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
			dialog.create();
			if ( dialog.open() == Dialog.OK ) {
				scheduleCreateEventActionJob(window, codeModuleFile, wizard);
			}
		}
		
		return null;
	}

	private void scheduleCreateEventActionJob(final IWorkbenchWindow window, CodeModuleFile codeModuleFile,
			NewEventActionWizard wizard) {
		String name = wizard.getName();
		String className = wizard.getClassName();
		boolean enabled = wizard.isEnabled();
		CreateEventActionJob job = new CreateEventActionJob(codeModuleFile, name, className, enabled, window.getShell() );
		job.setUser(true);
		job.schedule();
	}
}
