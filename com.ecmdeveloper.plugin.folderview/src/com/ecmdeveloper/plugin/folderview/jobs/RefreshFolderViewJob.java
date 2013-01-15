/**
 * Copyright 2013, Ricardo Belfor
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

package com.ecmdeveloper.plugin.folderview.jobs;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.tasks.IFetchPropertiesTask;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskFactory;
import com.ecmdeveloper.plugin.core.util.PluginMessage;
import com.ecmdeveloper.plugin.folderview.Activator;

/**
 * @author ricardo.belfor
 *
 */
public class RefreshFolderViewJob extends Job {

	private static final String NAME = "Refresh Folder View";
	
	private final IObjectStoreItem folder;
	private final Collection<IObjectStoreItem> objectStoreItems;
	private final List<String> propertyNames;
	
	public RefreshFolderViewJob(IObjectStoreItem folder,
			Collection<IObjectStoreItem> objectStoreItems, List<String> propertyNames) {
		super(NAME);
		
		this.folder = folder;
		this.objectStoreItems = objectStoreItems;
		this.propertyNames = propertyNames;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		try {
			ITaskFactory taskFactory = folder.getTaskFactory();
			IFetchPropertiesTask task = taskFactory.getFetchPropertiesTask(objectStoreItems, propertyNames
					.toArray(new String[propertyNames.size()]));
			Activator.getDefault().getTaskManager().executeTaskSync(task);
			return Status.OK_STATUS;
		} catch (ExecutionException e) {
			PluginMessage.openErrorFromThread(NAME, "Fetching property values failed.", e);
			return Status.CANCEL_STATUS;
		}
	}
}
