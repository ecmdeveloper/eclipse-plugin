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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import com.ecmdeveloper.plugin.core.model.IFolder;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.IPropertyDescription;
import com.ecmdeveloper.plugin.core.model.tasks.ILoadChildrenTask;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskFactory;
import com.ecmdeveloper.plugin.core.model.tasks.classes.IGetPropertyDescriptionsTask;
import com.ecmdeveloper.plugin.core.util.PluginMessage;
import com.ecmdeveloper.plugin.folderview.Activator;
import com.ecmdeveloper.plugin.folderview.model.ColumnDescription;
import com.ecmdeveloper.plugin.folderview.views.FolderView;

/**
 * @author ricardo.belfor
 *
 */
public class OpenFolderViewJob extends Job {

	private static final String LOADING_CHILDREN_FAILED_MESSAGE = "Loading children failed";
	private static final String NAME = "Open Folder View";
	private final IFolder folder;
	private final IWorkbenchWindow window;

	public OpenFolderViewJob(IFolder folder, IWorkbenchWindow window) {
		super(NAME);
		this.folder = folder;
		this.window = window;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		ArrayList<IObjectStoreItem> children = loadChildren();
		Collection<IPropertyDescription> propertyDescriptions = getPropertyDescriptions();
		if ( propertyDescriptions != null) {
			openFolderView(children, getColumnDescriptions(propertyDescriptions) );
		} else {
			return Status.CANCEL_STATUS;
		}
		
		return Status.OK_STATUS;
	}

	private List<ColumnDescription> getColumnDescriptions(Collection<IPropertyDescription> propertyDescriptions) {

		List<ColumnDescription> columnDescriptions = new ArrayList<ColumnDescription>();
		
		for ( IPropertyDescription propertyDescription : propertyDescriptions ) {
			ColumnDescription columnDescription = new ColumnDescription(propertyDescription.getName(),
					propertyDescription.getDisplayName());
			columnDescriptions.add(columnDescription);
		}
		return columnDescriptions;
	}

	private Collection<IPropertyDescription> getPropertyDescriptions() {

		try {
			ITaskFactory taskFactory = folder.getTaskFactory();
			IGetPropertyDescriptionsTask task = taskFactory.getGetPropertyDescriptionsTask( folder.getObjectStore() );
			Activator.getDefault().getTaskManager().executeTaskSync(task);
			return task.getPropertyDescriptions();
		} catch (ExecutionException e) {
			PluginMessage.openErrorFromThread(window.getShell(), getName(), LOADING_CHILDREN_FAILED_MESSAGE, e);
		}
		return null;
	}

	private ArrayList<IObjectStoreItem> loadChildren() {
		try {
			ITaskFactory taskFactory = folder.getTaskFactory();
			ILoadChildrenTask task = taskFactory.getLoadChildrenTask(folder);
			Activator.getDefault().getTaskManager().executeTaskSync(task);
			return task.getChildren();
		} catch (ExecutionException e) {
			PluginMessage.openErrorFromThread(window.getShell(), getName(), LOADING_CHILDREN_FAILED_MESSAGE, e);
		}
		return new ArrayList<IObjectStoreItem>();
	}

	private void openFolderView(final List<IObjectStoreItem> children, final List<ColumnDescription> columnDescriptions) {
		
		window.getShell().getDisplay().asyncExec( new Runnable() {

			@Override
			public void run() {
				FolderView view;
				try {
					view = (FolderView) window.getActivePage().showView(FolderView.ID,
							folder.getId(), IWorkbenchPage.VIEW_ACTIVATE);
					view.show(folder, children, columnDescriptions);
				} catch (PartInitException e) {
					PluginMessage.openErrorFromThread(window.getShell(), getName(), e.getLocalizedMessage(), e);
				}
			}} 
		);
	}
}
