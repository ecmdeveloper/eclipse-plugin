/**
 * Copyright 2009, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in is
 * free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.ecmdeveloper.plugin.ui.Activator;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.tasks.IMoveTask;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskFactory;
import com.ecmdeveloper.plugin.core.model.tasks.IMoveTask;
import com.ecmdeveloper.plugin.core.util.PluginMessage;

public class MoveJob extends Job {
	
	private static final String MONITOR_MESSAGE = "Moving";
	private static final String PROGRESS_MESSAGE = "Moving \"{0}\"";
	private static final String FAILED_MESSAGE = "Moving \"{0}\" failed";
	private static final String HANDLER_NAME = "Move";
	
	private ArrayList<IObjectStoreItem> itemsMoved;
	private IObjectStoreItem destination;
	private Shell shell;
	
	public MoveJob(ArrayList<IObjectStoreItem> itemsMoved,
			IObjectStoreItem destination, Shell shell) {
		super(HANDLER_NAME);
		this.itemsMoved = itemsMoved;
		this.shell = shell;
		this.destination = destination;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		try {
			monitor.beginTask( MONITOR_MESSAGE, itemsMoved.size() );
			for ( IObjectStoreItem objectStoreItem : itemsMoved ) {
				monitor.subTask( MessageFormat.format(PROGRESS_MESSAGE, objectStoreItem.getName() ) );
				moveItem( objectStoreItem );
				monitor.worked(1);

				if ( monitor.isCanceled() ) {
					break;
				}
			}
			
			return Status.OK_STATUS;
		} finally {
			monitor.done();
		}
	}

	private void moveItem(final IObjectStoreItem objectStoreItem ) {

		try {
			ITaskFactory taskFactory = objectStoreItem.getTaskFactory();
			IMoveTask moveTask = taskFactory.getMoveTask( new IObjectStoreItem[] { objectStoreItem }, destination );
			 
			Activator.getDefault().getTaskManager().executeTaskSync( moveTask);
		} catch(final Exception e ) {
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					PluginMessage.openError(shell, HANDLER_NAME, 
							MessageFormat.format(FAILED_MESSAGE, objectStoreItem.getName() ), e );
				}
			} );
		}
	}
}
