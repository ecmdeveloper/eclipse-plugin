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
package com.ecmdeveloper.plugin.handlers;

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

import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.util.Messages;
import com.ecmdeveloper.plugin.util.PluginMessage;

/**
 * @author Ricardo Belfor
 *
 */
public class ConnectObjectStoreHandler extends AbstractHandler implements IHandler {

	private static final String HANDLER_NAME = Messages.ConnectObjectStoreHandler_HandlerName;

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@SuppressWarnings("unchecked")
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
		
		Iterator iter = ((IStructuredSelection) selection).iterator();			
		while (iter.hasNext()) {
			final Object selectedObject = iter.next();
			if ( selectedObject instanceof ObjectStore ) {
				ConnectObjectStoreJob job = new ConnectObjectStoreJob( (ObjectStore)selectedObject, window.getShell() );
				job.setUser(true);
				job.schedule();
			}
		}

		return null;
	}

	class ConnectObjectStoreJob extends Job {

		private ObjectStore objectStore;
		private Shell shell;

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
				PluginMessage.openErrorFromThread(shell, HANDLER_NAME, e.getLocalizedMessage(), e );
			}
			return Status.OK_STATUS;
		}
	};
}
