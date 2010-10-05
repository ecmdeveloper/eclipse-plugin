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

package com.ecmdeveloper.plugin.jobs;

import java.text.MessageFormat;
import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.model.tasks.DeleteTask;
import com.ecmdeveloper.plugin.util.Messages;
import com.ecmdeveloper.plugin.util.PluginMessage;

/**
 * @author Ricardo.Belfor
 *
 */
public class DeleteJob extends Job {
	
	private static final String MONITOR_MESSAGE = Messages.DeleteJob_MonitorMessage;
	private static final String PROGRESS_MESSAGE = Messages.DeleteJob_ProgressMessage;
	private static final String FAILED_MESSAGE = Messages.DeleteJob_FailedMessage;
	private static final String HANDLER_NAME = Messages.DeleteJob_HandlerName;

	private ArrayList<IObjectStoreItem> itemsDeleted;
	private Shell shell;
	private boolean deleteAllVersions;
	
	public DeleteJob(ArrayList<IObjectStoreItem> itemsDeleted, Shell shell) {
		this(itemsDeleted, shell, true);
	}

	public DeleteJob(ArrayList<IObjectStoreItem> itemsDeleted, Shell shell, boolean deleteAllVersions) {
		super(HANDLER_NAME);
		this.itemsDeleted = itemsDeleted;
		this.shell = shell;
		this.deleteAllVersions = deleteAllVersions;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		try {

			monitor.beginTask(MONITOR_MESSAGE, itemsDeleted.size());
			for (IObjectStoreItem objectStoreItem : itemsDeleted) {
				monitor.subTask(MessageFormat.format(PROGRESS_MESSAGE, objectStoreItem.getName()));
				deleteItem(objectStoreItem);
				monitor.worked(1);

				if (monitor.isCanceled()) {
					break;
				}
			}

			return Status.OK_STATUS;
		} finally {
			monitor.done();
		}
	}

	private void deleteItem(final IObjectStoreItem objectStoreItem) {

		try {
			DeleteTask deleteTask = new DeleteTask(new IObjectStoreItem[] { objectStoreItem }, deleteAllVersions);
			ObjectStoresManager.getManager().executeTaskSync(deleteTask);
		} catch (final Exception e) {
			String name = objectStoreItem.getName();
			String safeName = name; //name.replaceAll("{", "").replaceAll("}", "");
			PluginMessage.openErrorFromThread(shell, HANDLER_NAME, MessageFormat.format(
					FAILED_MESSAGE, safeName), e);
		}
	}
}
