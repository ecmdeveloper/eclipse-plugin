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

package com.ecmdeveloper.plugin.ui.views;

import java.text.MessageFormat;

import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.ShowInContext;

import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.Placeholder;
import com.ecmdeveloper.plugin.ui.jobs.GetParentJob;
import com.ecmdeveloper.plugin.core.util.PluginMessage;

/**
 * @author ricardo.belfor
 *
 */
public class ParentFolderView extends ObjectStoresView implements IShowInTarget {

	private static final String NO_PARENTS_MESSAGE = "\"{0}\" has no parents.";

	public static final String ID = "com.ecmdeveloper.plugin.ui.parentFolderView";
	
	public boolean instantiated = false;
	
	public void setSelection(final IObjectStoreItem objectStoreItem ) {
		
		setPartName( objectStoreItem.getDisplayName() + " Parents View" );
		instantiated = true;
		displayLoadingMessage();
		runGetParentJob(objectStoreItem);
	}

	private void displayLoadingMessage() {
		Placeholder placeholder = new Placeholder(Placeholder.Type.LOADING);
		getContentProvider().setRootElements( new Object[] { placeholder } );
		getViewer().refresh();
	}

	private void runGetParentJob(final IObjectStoreItem objectStoreItem) {
		GetParentJob job = new GetParentJob( objectStoreItem , getSite().getShell() );
		job.setUser(true);
		job.addJobChangeListener( new JobChangeAdapter() {

			@Override
			public void done(IJobChangeEvent event) {
				
				if ( event.getResult().equals( Status.CANCEL_STATUS ) ) {
					return;
				}

				GetParentJob job = (GetParentJob) event.getJob();
				if ( job.getParents().size() > 0 ) {
					getContentProvider().setRootElements( job.getParents().toArray() );
				} else {
					displayNoParentsMessage(objectStoreItem);
				}
				
				refreshViewer();
			}

			private void displayNoParentsMessage(final IObjectStoreItem objectStoreItem) {
				Placeholder placeholder = new Placeholder(Placeholder.Type.MESSAGE);
				String message = MessageFormat.format(NO_PARENTS_MESSAGE, objectStoreItem.getDisplayName());
				placeholder.setName(message );
				getContentProvider().setRootElements( new Object[] { placeholder } );
			}

			private void refreshViewer() {
				getViewer().getTree().getDisplay().asyncExec( new Runnable() {
					@Override
					public void run() {
						getViewer().refresh();
					}
				} );
			}
			
		});
		
		job.schedule();
	}

	@Override
	public boolean show(ShowInContext context) {
		
		if (getViewer() == null || context == null) {
			return false;
		}

		ISelection selection = context.getSelection();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection)selection;
			Object first = structuredSelection.getFirstElement();
			if (first instanceof IObjectStoreItem) {
				if ( !instantiated ) {
					setSelection((IObjectStoreItem) first);
				} else {
			        return openNewView(first);
				}
				return true;
			}
		}
		
		return false;
	}

	private boolean openNewView(Object first) {
		String secondaryId = ((IObjectStoreItem)first).getId();
		int mode = IWorkbenchPage.VIEW_CREATE;
		try {
			IViewPart view = getSite().getWorkbenchWindow().getActivePage().showView(ID, secondaryId, mode );
			((ParentFolderView)view).setSelection( (IObjectStoreItem) first  );
			getSite().getWorkbenchWindow().getActivePage().activate(view);
			return true;
		} catch (PartInitException e) {
			PluginMessage.openError(getSite().getShell(), "Parents View", e.getLocalizedMessage(), e);
			return false;
		}
	}
}
