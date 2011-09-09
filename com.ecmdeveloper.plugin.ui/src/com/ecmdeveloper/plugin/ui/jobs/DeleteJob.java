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

package com.ecmdeveloper.plugin.ui.jobs;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;

import com.ecmdeveloper.plugin.core.model.ICustomObject;
import com.ecmdeveloper.plugin.core.model.IDocument;
import com.ecmdeveloper.plugin.core.model.IFolder;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.tasks.IDeleteTask;
import com.ecmdeveloper.plugin.core.util.PluginMessage;
import com.ecmdeveloper.plugin.ui.Activator;
import com.ecmdeveloper.plugin.ui.util.Messages;

/**
 * @author Ricardo.Belfor
 *
 */
public class DeleteJob extends Job {
	
	private static final String FETCHING_CHILDREN_SUBTASK_MESSAGE = "Fetching children of \"{0}\"";
	private static final String DELETE_SUBTASK_MESSAGE = "Deleting \"{0}\"";
	private static final String MONITOR_MESSAGE = Messages.DeleteJob_MonitorMessage;
	private static final String PROGRESS_MESSAGE = Messages.DeleteJob_ProgressMessage;
	private static final String FAILED_MESSAGE = Messages.DeleteJob_FailedMessage;
	private static final String HANDLER_NAME = Messages.DeleteJob_HandlerName;

	private Collection<IObjectStoreItem> itemsDeleted;
	private Shell shell;
	private boolean deleteAllVersions;
	private boolean deleteContainedDocuments;
	private boolean deleteContainedCustomObjects;
	private boolean deleteContainedFolders;
	
	public DeleteJob(Collection<IObjectStoreItem> itemsDeleted ) {
		this(itemsDeleted, true);
	}

	public DeleteJob(Collection<IObjectStoreItem> itemsDeleted, boolean deleteAllVersions) {
		super(HANDLER_NAME);
		this.itemsDeleted = itemsDeleted;
		this.deleteAllVersions = deleteAllVersions;
	}

	public boolean isDeleteContainedDocuments() {
		return deleteContainedDocuments;
	}

	public void setDeleteContainedDocuments(boolean deleteContainedDocuments) {
		this.deleteContainedDocuments = deleteContainedDocuments;
	}

	public boolean isDeleteContainedCustomObjects() {
		return deleteContainedCustomObjects;
	}

	public void setDeleteContainedCustomObjects(boolean deleteContainedCustomObjects) {
		this.deleteContainedCustomObjects = deleteContainedCustomObjects;
	}

	public boolean isDeleteContainedFolders() {
		return deleteContainedFolders;
	}

	public void setDeleteContainedFolders(boolean deleteContainedFolders) {
		this.deleteContainedFolders = deleteContainedFolders;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		try {
			monitor.beginTask(MONITOR_MESSAGE, itemsDeleted.size());
			for (IObjectStoreItem objectStoreItem : itemsDeleted) {
				monitor.subTask(MessageFormat.format(PROGRESS_MESSAGE, objectStoreItem.getName()));
				try {
					deleteItem(objectStoreItem, monitor);
					monitor.worked(1);
				} catch (final Exception e) {
					showError(objectStoreItem, e);
				}

				if (monitor.isCanceled()) {
					break;
				}
			}

			return Status.OK_STATUS;
		} finally {
			monitor.done();
		}
	}

	private void showError(IObjectStoreItem objectStoreItem, final Exception e) {
		String name = objectStoreItem.getName();
		String safeName = name; //name.replaceAll("{", "").replaceAll("}", "");
		PluginMessage.openErrorFromThread(HANDLER_NAME, MessageFormat.format(
				FAILED_MESSAGE, safeName), e);
	}

	private void deleteItem(final IObjectStoreItem objectStoreItem, IProgressMonitor monitor) throws ExecutionException {
		if ( !(objectStoreItem instanceof IFolder) || ((IFolder)objectStoreItem).isContained() ) {
			deleteItemWithoutChildren(objectStoreItem, monitor);
		} else {
			deleteItemWithChildren(objectStoreItem, monitor);
		}
	}

	private void deleteItemWithChildren(final IObjectStoreItem objectStoreItem,
			IProgressMonitor monitor) throws ExecutionException {
		
		if (monitor.isCanceled()) {
			return;
		}
		
		IFolder folder = (IFolder) objectStoreItem;
		if ( folder.hasChildren() ) {
			String message = MessageFormat.format( FETCHING_CHILDREN_SUBTASK_MESSAGE, objectStoreItem.getDisplayName() );
			monitor.subTask( message );
			Collection<IObjectStoreItem> children = new ArrayList<IObjectStoreItem>();
			children.addAll( folder.getChildrenSync() ); 
				
			for ( IObjectStoreItem child : children ) {
				if ( !isExcludedChild(child) ) {
					deleteItem(child, monitor);
				}
			}
		}
		deleteItemWithoutChildren(objectStoreItem, monitor);
	}

	private boolean isExcludedChild(IObjectStoreItem child) {
		if ( child instanceof IDocument && !deleteContainedDocuments ) {
			return true;
		} else if ( child instanceof ICustomObject && !deleteContainedCustomObjects) {
			return true;
		} else if ( child instanceof IFolder && ((IFolder)child).isContained() && !deleteContainedFolders) {
			return true;
		}
		return false;
	}

	private void deleteItemWithoutChildren(final IObjectStoreItem objectStoreItem,
			IProgressMonitor monitor) throws ExecutionException {
		
		if (monitor.isCanceled()) {
			return;
		}
		
		String message = MessageFormat.format( DELETE_SUBTASK_MESSAGE, objectStoreItem.getDisplayName() );
		monitor.subTask( message );
		IDeleteTask deleteTask = objectStoreItem.getTaskFactory().getDeleteTask( new IObjectStoreItem[] { objectStoreItem }, deleteAllVersions);
		Activator.getDefault().getTaskManager().executeTaskSync(deleteTask);
	}
}
