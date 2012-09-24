/**
 * Copyright 2012, Ricardo Belfor
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
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;

import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntries;
import com.ecmdeveloper.plugin.core.model.security.IRealm;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskFactory;
import com.ecmdeveloper.plugin.core.model.tasks.security.IGetAccessControlEntriesTask;
import com.ecmdeveloper.plugin.core.util.PluginMessage;
import com.ecmdeveloper.plugin.security.Activator;
import com.ecmdeveloper.plugin.security.editor.SecurityEditorInput;
import com.ecmdeveloper.plugin.security.mock.AccessControlEntriesMock;

/**
 * @author ricardo.belfor
 *
 */
public class RefreshSecurityEditorJob extends Job {

	private static final String NAME = "Refresh Security";
	private static final String FAILED_MESSAGE = "Refreshing Security Editor for \"{0}\" failed";
	private static final String MONITOR_MESSAGE = "Refreshing Security Editor";
	
	private final SecurityEditorInput securityEditorInput;
	private final Shell shell;

	public RefreshSecurityEditorJob(SecurityEditorInput securityEditorInput, Shell shell) {
		super(NAME);
		this.securityEditorInput = securityEditorInput;
		this.shell = shell;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		try {
			monitor.beginTask(MONITOR_MESSAGE, IProgressMonitor.UNKNOWN);
			securityEditorInput.setAccessControlEntries( getAccessControlEntries() );
		} catch (Exception e) {
			String message = MessageFormat.format( FAILED_MESSAGE, securityEditorInput.getObjectStoreItem().getDisplayName() );
			PluginMessage.openErrorFromThread(shell, getName(), message, e);
			return Status.CANCEL_STATUS;
		}
		
		return Status.OK_STATUS;
	}

	private IAccessControlEntries getAccessControlEntries() throws Exception {
		IObjectStoreItem objectStoreItem = securityEditorInput.getObjectStoreItem();
		Collection<IRealm> realms = securityEditorInput.getRealms();
		ITaskFactory taskFactory = objectStoreItem.getTaskFactory();
		IGetAccessControlEntriesTask task = taskFactory.getGetAccessControlEntriesTask(objectStoreItem, realms);
		if ( task != null ) {
			Activator.getDefault().getTaskManager().executeTaskSync(task);
			return task.getAccessControlEntries();
		} else {
			return new AccessControlEntriesMock(); 
		}
	}
}
