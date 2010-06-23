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
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.model.tasks.CheckoutTask;
import com.ecmdeveloper.plugin.util.PluginMessage;

/**
 * @author Ricardo.Belfor
 *
 */
public class CheckoutJob extends Job {

	private static final String HANDLER_NAME = "Checkout";
	private static final String FAILED_MESSAGE = "Checkin out \"{0}\" failed";
	private static final String TASK_MESSAGE = "Checking out document \"{0}\"";

	private Document document;
	private boolean download;
	private boolean openEditor;
	private IWorkbenchWindow window;
	
	public CheckoutJob(Document document, IWorkbenchWindow window, boolean download, boolean openEditor) {
		super(HANDLER_NAME);
		this.document = document;
		this.window = window;
		this.download = download;
		this.openEditor = openEditor;
	}

	public Document getDocument() {
		return document;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		try {
			String taskName = MessageFormat.format( TASK_MESSAGE, document.getName() );
			monitor.beginTask(taskName, IProgressMonitor.UNKNOWN );
			checkoutDocument();
			if ( download ) {
				scheduleDownloadDocumentJob();
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

	private void checkoutDocument() throws ExecutionException {
		CheckoutTask task = new CheckoutTask(document);
		ObjectStoresManager.getManager().executeTaskSync(task);
	}

	private void scheduleDownloadDocumentJob() {
		DownloadDocumentJob downloadJob = new DownloadDocumentJob( document, window, openEditor );
		downloadJob.setRule( new ChainedJobsSchedulingRule(2) );
		downloadJob.setUser(true);
		downloadJob.schedule();
	}
}
