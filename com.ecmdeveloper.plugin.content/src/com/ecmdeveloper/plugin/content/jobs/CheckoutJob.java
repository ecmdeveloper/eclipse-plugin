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
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IWorkbenchWindow;

import com.ecmdeveloper.plugin.content.Activator;
import com.ecmdeveloper.plugin.core.model.IDocument;
import com.ecmdeveloper.plugin.core.model.tasks.ICheckoutTask;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskFactory;
import com.ecmdeveloper.plugin.core.util.PluginMessage;

/**
 * @author Ricardo.Belfor
 *
 */
public class CheckoutJob extends Job {

	private static final String HANDLER_NAME = "Checkout";
	private static final String FAILED_MESSAGE = "Checkin out \"{0}\" failed";
	private static final String TASK_MESSAGE = "Checking out document \"{0}\"";

	private IDocument document;
	private boolean download;
	private boolean openEditor;
	private boolean trackFile;
	private IWorkbenchWindow window;
	
	public CheckoutJob(IDocument document, IWorkbenchWindow window, boolean download, boolean openEditor, boolean trackFile ) {
		super(HANDLER_NAME);
		this.document = document;
		this.window = window;
		this.download = download;
		this.openEditor = openEditor;
		this.trackFile = trackFile;
	}

	public IDocument getDocument() {
		return document;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		try {
			String taskName = MessageFormat.format( TASK_MESSAGE, document.getName() );
			monitor.beginTask(taskName, IProgressMonitor.UNKNOWN );
			IDocument checkoutDocument = checkoutDocument();
			if ( download ) {
				scheduleDownloadDocumentJob(checkoutDocument, monitor);
			}
			monitor.done();
			return Status.OK_STATUS;
		} catch (final Exception e) {
			showError(e);
		}
		return Status.CANCEL_STATUS;
	}

	private void showError(final Exception e) {
		String message = MessageFormat.format(FAILED_MESSAGE, document.getName());
		PluginMessage.openErrorFromThread( window.getShell(), HANDLER_NAME, message, e);
	}

	private IDocument checkoutDocument() throws ExecutionException {
		ITaskFactory taskFactory = document.getTaskFactory();
		ICheckoutTask task = taskFactory.getCheckoutTask(document);
		Activator.getDefault().getTaskManager().executeTaskSync(task);
		return task.getCheckoutDocument();
	}

	private void scheduleDownloadDocumentJob(IDocument checkoutDocument, IProgressMonitor monitor) {
		DownloadDocumentJob downloadJob = new DownloadDocumentJob(checkoutDocument, window, openEditor,
				trackFile);
		downloadJob.setRule( new ChainedJobsSchedulingRule(2) );
		downloadJob.setProgressGroup(monitor, IProgressMonitor.UNKNOWN );
		downloadJob.setUser(true);
		downloadJob.schedule();
	}
}
