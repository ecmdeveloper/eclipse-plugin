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

package com.ecmdeveloper.plugin.security.jobs;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntries;
import com.ecmdeveloper.plugin.core.model.security.IRealm;
import com.ecmdeveloper.plugin.core.model.tasks.security.IGetAccessControlEntriesTask;
import com.ecmdeveloper.plugin.core.model.tasks.security.IGetRealmsTask;
import com.ecmdeveloper.plugin.core.util.PluginMessage;
import com.ecmdeveloper.plugin.security.Activator;
import com.ecmdeveloper.plugin.security.editor.SecurityEditorInput;
import com.ecmdeveloper.plugin.security.mock.AccessControlEntriesMock;
import com.ecmdeveloper.plugin.security.mock.RealmMock;

/**
 * @author Ricardo.Belfor
 *
 */
public class OpenSecurityEditorJob extends Job {

	private static final String JOB_NAME = "Open Security Editor";
	private static final String MONITOR_MESSAGE = "Opening Security Editor";
	private static final String FAILED_MESSAGE = "Opening Security Editor for \"{0}\" failed";
	
	private IObjectStoreItem objectStoreItem;
	private IWorkbenchWindow window;
	private String editorId;
	
	public OpenSecurityEditorJob(IObjectStoreItem objectStoreItem, String editorId, IWorkbenchWindow window) {
		super(JOB_NAME);
		this.objectStoreItem = objectStoreItem;
		this.window = window;
		this.editorId = editorId;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			monitor.beginTask(MONITOR_MESSAGE, IProgressMonitor.UNKNOWN);
			openNewEditor(monitor);
		} catch (Exception e) {
			PluginMessage.openErrorFromThread(window.getShell(), JOB_NAME, MessageFormat.format(
					FAILED_MESSAGE, objectStoreItem.getName()), e);
		}
		
		return Status.OK_STATUS;
	}

	private void openNewEditor(IProgressMonitor monitor) throws Exception {
		
		Collection<IRealm> realms = getRealms();
		IAccessControlEntries accessControlEntries = getAccessControlEntries(realms);
		IEditorInput input = new SecurityEditorInput( objectStoreItem, accessControlEntries, realms );
		openEditorWindow(input, editorId);
	}

	private Collection<IRealm> getRealms() throws ExecutionException {
		
		IGetRealmsTask getRealmsTask = objectStoreItem.getTaskFactory().getGetRealmsTask( objectStoreItem.getObjectStore() );
		if ( getRealmsTask != null ) {
			Activator.getDefault().getTaskManager().executeTaskSync(getRealmsTask);
			return getRealmsTask.getRealms();
		} else {
			Collection<IRealm> realms = new HashSet<IRealm>();
			realms.add( new RealmMock("MyRealm", objectStoreItem.getObjectStore() ) );
			return realms;
		}
	}

	private IAccessControlEntries getAccessControlEntries(Collection<IRealm> realms) throws ExecutionException {
		IGetAccessControlEntriesTask task = objectStoreItem.getTaskFactory()
				.getGetAccessControlEntriesTask(objectStoreItem, realms);
		if ( task != null ) {
			Activator.getDefault().getTaskManager().executeTaskSync(task);
			return task.getAccessControlEntries();
		} else {
			return new AccessControlEntriesMock(); 
		}
	}

	private void openEditorWindow(final IEditorInput input, final String editorId) {
		
		window.getWorkbench().getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				try {
					IDE.openEditor( window.getActivePage(), input, editorId);
				} catch (PartInitException e) {
					PluginMessage.openErrorFromThread(window.getShell(), JOB_NAME, MessageFormat.format(
					FAILED_MESSAGE, objectStoreItem.getName()), e);
				}
			}
		} );
	}
}
