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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;

import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntries;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskFactory;
import com.ecmdeveloper.plugin.core.model.tasks.security.ISaveAccessControlEntriesTask;
import com.ecmdeveloper.plugin.core.util.PluginMessage;
import com.ecmdeveloper.plugin.security.Activator;

/**
 * @author ricardo.belfor
 *
 */
public class SaveAccessControlEntriesJob extends Job {

	private static final String JOB_NAME = "Save Permissions";
	private static final String FAILED_MESSAGE = "Saving permissions for \"{0}\" failed";

	private final IObjectStoreItem objectStoreItem;
	private final IAccessControlEntries accessControlEntries;
	private final Shell shell;
	
	public SaveAccessControlEntriesJob(IObjectStoreItem objectStoreItem, IAccessControlEntries accessControlEntries, Shell shell) {
		super(JOB_NAME);
		this.objectStoreItem = objectStoreItem;
		this.accessControlEntries = accessControlEntries;
		this.shell = shell;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			ITaskFactory taskFactory = objectStoreItem.getTaskFactory();
			ISaveAccessControlEntriesTask task = taskFactory.getSaveAccessControlEntriesTask(objectStoreItem, accessControlEntries);
			Activator.getDefault().getTaskManager().executeTaskSync(task);
		} catch (Exception e) {
			PluginMessage.openErrorFromThread(shell, JOB_NAME, MessageFormat.format(
					FAILED_MESSAGE, objectStoreItem.getName()), e);
		}
		return Status.OK_STATUS;
	}
}
