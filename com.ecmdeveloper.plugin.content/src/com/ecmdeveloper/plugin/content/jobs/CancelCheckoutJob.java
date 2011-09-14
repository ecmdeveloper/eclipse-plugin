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

package com.ecmdeveloper.plugin.content.jobs;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.ecmdeveloper.plugin.content.Activator;
import com.ecmdeveloper.plugin.core.model.IDocument;
import com.ecmdeveloper.plugin.core.model.tasks.ICancelCheckoutTask;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskFactory;
import com.ecmdeveloper.plugin.core.util.PluginMessage;
import com.ecmdeveloper.plugin.tracker.model.FilesTracker;

/**
 * @author Ricardo.Belfor
 *
 */
public class CancelCheckoutJob extends Job {

	private static final String CANCELING_CHECKOUT_TASK = "Canceling Checkout \"{0}\"";
	private static final String HANDLER_NAME = "Cancel Checkout";
	private static final String FAILED_MESSAGE = "Canceling Checkout \"{0}\" failed";

	private IDocument document;
	private Shell shell;
	
	public CancelCheckoutJob(IDocument document, Shell shell) {
		super(HANDLER_NAME);
		this.document = document;
		this.shell = shell;
	}

	public IDocument getDocument() {
		return document;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		try {
			String message = MessageFormat.format( CANCELING_CHECKOUT_TASK, document.getName() );
			monitor.beginTask( message, IProgressMonitor.UNKNOWN );
			ITaskFactory taskFactory = document.getTaskFactory();
			ICancelCheckoutTask task = taskFactory.getCancelCheckoutTask(document);
			Activator.getDefault().getTaskManager().executeTaskSync(task);
			removeFromFilesTracker();
			return Status.OK_STATUS;
		} catch (Exception e) {
			showError(e);
		}
		return Status.CANCEL_STATUS;
	}

	private void removeFromFilesTracker() {
		String versionSeriesId = document.getVersionSeriesId();
		FilesTracker tracker = FilesTracker.getInstance();
		if ( tracker.isVersionSeriesTracked(versionSeriesId) ) {
			tracker.removeTrackedVersionSeries(versionSeriesId);
		}
	}

	private void showError(final Exception e) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				String message = MessageFormat.format(FAILED_MESSAGE, document.getName());
				PluginMessage.openError(shell, HANDLER_NAME, message, e);
			}
		});
	}
}
