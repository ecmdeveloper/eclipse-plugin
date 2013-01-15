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

package com.ecmdeveloper.plugin.folderview.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ecmdeveloper.plugin.folderview.jobs.RefreshFolderViewJob;
import com.ecmdeveloper.plugin.folderview.model.ColumnDescription;
import com.ecmdeveloper.plugin.folderview.views.FolderView;

/**
 * @author ricardo.belfor
 *
 */
public abstract class AbstractFolderViewHandler extends AbstractHandler {

	private FolderView folderView;
	
	protected Job getRefreshFolderViewJob() {
		
		List<String> propertyNames = getPropertyNames();
		Job job = new RefreshFolderViewJob(folderView.getFolder(), folderView.getChildren(), propertyNames);
		job.setUser(true);
		job.addJobChangeListener( new JobChangeAdapter() {

			@Override
			public void done(IJobChangeEvent event) {
				if ( event.getResult().equals( Status.OK_STATUS ) ) {
					folderView.getSite().getShell().getDisplay().asyncExec( new Runnable() {

						@Override
						public void run() {
							folderView.updateColumns();
						}
					});
				}
			}
		} );
		return job;
	}

	protected List<String> getPropertyNames() {
		List<String> propertyNames = new ArrayList<String>();

		for ( ColumnDescription columnDescription : folderView.getVisibleColumns() ) {
			propertyNames.add( columnDescription.getName() );
		}

		for ( ColumnDescription columnDescription : folderView.getGroupByColumns() ) {
			propertyNames.add( columnDescription.getName() );
		}

		return propertyNames;
	}

	private FolderView getActiveFolderView(ExecutionEvent event) {
		FolderView folderView = null;
		final IWorkbenchPart part = HandlerUtil.getActivePart(event);
		if (part != null && part instanceof FolderView ) {
			folderView = (FolderView) part;
		}
		return folderView;
	}

	@Override
	public final Object execute(ExecutionEvent event) throws ExecutionException {
		
		folderView = getActiveFolderView(event);
		execute(event, folderView);
		
		return null;
	}

	protected abstract void execute(ExecutionEvent event, FolderView folderView) throws ExecutionException;
}
