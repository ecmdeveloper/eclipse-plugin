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

package com.ecmdeveloper.plugin.favorites.handlers;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ecmdeveloper.plugin.favorites.model.FavoriteObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.util.PluginMessage;

/**
 * @author ricardo.belfor
 *
 */
public class ConnectFavoriteObjectStoreHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if (window == null)	return null;

		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if ( selection == null) {
			return null;
		}
		
		if ( !(selection instanceof IStructuredSelection) ) {
			return null;
		}
		
		Iterator<?> iter = ((IStructuredSelection) selection).iterator();			
		while (iter.hasNext()) {
			FavoriteObjectStore favoriteObjectStore = (FavoriteObjectStore) iter.next();
			ConnectObjectStoreJob job = new ConnectObjectStoreJob( favoriteObjectStore.getObjectStore(), window.getShell() );
			job.setUser(true);
			job.schedule();
		}

		return null;
	}

	class ConnectObjectStoreJob extends Job {

		private Shell shell;
		private ObjectStore objectStore;

		public ConnectObjectStoreJob(ObjectStore objectStore, Shell shell) {
			super("Connect Object Store");
			this.objectStore = objectStore;
			this.shell = shell;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			try {
				ObjectStoresManager.getManager().connectObjectStore( objectStore, monitor );
			} catch (Exception e ) {
				PluginMessage.openErrorFromThread(shell, getName(), e.getLocalizedMessage(), e );
			}
			return Status.OK_STATUS;
		}
	};
}
