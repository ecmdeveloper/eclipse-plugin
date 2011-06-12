/**
 * Copyright 2011, Ricardo Belfor
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

package com.ecmdeveloper.plugin.ui.jobs;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;

import com.ecmdeveloper.plugin.model.Folder;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.model.tasks.GetParentTask;
import com.ecmdeveloper.plugin.util.PluginMessage;

/**
 * @author ricardo.belfor
 *
 */
public class GetParentJob extends Job {

	private static final String JOB_MESSAGE = "Getting Parents for \"{0}\"";
	private static final String JOB_NAME = "Get Parent";
	private final IObjectStoreItem objectStoreItem;
	private final Shell shell;
	private Collection<Folder> parents;

	public GetParentJob(IObjectStoreItem objectStoreItem, Shell shell ) {
		super(JOB_NAME);
		this.objectStoreItem = objectStoreItem;
		this.shell = shell;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		try {
			String message = MessageFormat.format(JOB_MESSAGE, objectStoreItem.getDisplayName());
			monitor.beginTask(message, IProgressMonitor.UNKNOWN );
			GetParentTask task = new GetParentTask( (ObjectStoreItem) objectStoreItem );
			ObjectStoresManager.getManager().executeTaskSync(task);
			parents = task.getParents();
		} catch (ExecutionException e) {
			PluginMessage.openErrorFromThread(shell, JOB_NAME, e.getLocalizedMessage(), e);
			return Status.CANCEL_STATUS;
		}
		
		return Status.OK_STATUS;
	}

	public Collection<Folder> getParents() {
		return parents;
	}
}
