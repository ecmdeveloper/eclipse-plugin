/**
 * Copyright 2010, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in
 * is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ECM Developer plug-in. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 */

package com.ecmdeveloper.plugin.scripting.handlers;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ecmdeveloper.plugin.core.model.IConnection;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.scripting.jobs.ExecuteMethodJob;
import com.ecmdeveloper.plugin.scripting.wizard.LaunchScriptWizard;

/**
 * @author ricardo.belfor
 *
 */
public abstract class AbstractExecuteMethodHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if (window == null)	return null;

		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (!(selection instanceof IStructuredSelection) || selection.isEmpty() )
			return null;

		Iterator<?> iterator = ((IStructuredSelection) selection).iterator();
		ArrayList<IObjectStoreItem> objectStoreItems = new ArrayList<IObjectStoreItem>();

		LaunchScriptWizard wizard = new LaunchScriptWizard(getPreferenceStore(), getProjectNatureId() );
		WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
		dialog.create();
		if ( dialog.open() != Dialog.OK ) {
			return null;
		}
		
		while ( iterator.hasNext() ) {
			IObjectStoreItem objectStoreItem = (IObjectStoreItem) iterator.next();
			objectStoreItems.add(objectStoreItem);
		}

		String username;
		String password;

		if ( wizard.isUseExistingCredentials() ) {
			IObjectStore objectStore = objectStoreItems.get(0).getObjectStore();
			IConnection connection = objectStore.getConnection();
			username = connection.getUsername();
			password = connection.getPassword();
		} else {
			username = wizard.getUsername();
			password = wizard.getPassword();
		}
		 
		ExecuteMethodJob job = new ExecuteMethodJob(wizard.getMethod(), objectStoreItems, username, password, getRunnerClassName(), wizard.isDebug() );
		job.setUser(true);
		job.schedule();

		return null;
	}

	protected abstract String getRunnerClassName();

	protected abstract String getProjectNatureId();

	protected abstract IPreferenceStore getPreferenceStore();
}
