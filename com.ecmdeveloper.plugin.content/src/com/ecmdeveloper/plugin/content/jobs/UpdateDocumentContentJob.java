/**
 * Copyright 2012, Ricardo Belfor
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
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;

import com.ecmdeveloper.plugin.content.Activator;
import com.ecmdeveloper.plugin.core.model.IDocument;
import com.ecmdeveloper.plugin.core.model.tasks.IDocumentTask;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskFactory;
import com.ecmdeveloper.plugin.core.util.PluginMessage;

/**
 * @author ricardo.belfor
 *
 */
public class UpdateDocumentContentJob extends Job {

	private static final String HANDLER_NAME = "Update Document Content";
	private static final String TASK_MESSAGE = "Updating Document \"{0}\" Content";
	private static final String FAILED_MESSAGE = "Updating Content \"{0}\" failed";

	private IDocument document;
	private Shell shell;
	private Collection<Object> content;
	private String mimeType;
	
	public UpdateDocumentContentJob(IDocument document, Shell shell, Collection<Object> content, String mimeType ) {
		super(HANDLER_NAME);
		this.document = document;
		this.shell = shell;
		this.content = content;
		this.mimeType = mimeType;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			String taskName = MessageFormat.format( TASK_MESSAGE, document.getName() );
			monitor.beginTask(taskName, IProgressMonitor.UNKNOWN );
			updateContent(monitor);
			monitor.done();
			return Status.OK_STATUS;
		} catch (final Exception e) {
			monitor.done();
			showError(e);
		}
		return Status.CANCEL_STATUS;
	}

	private void updateContent(IProgressMonitor monitor) throws ExecutionException {
		ITaskFactory taskFactory = document.getTaskFactory();
		IDocumentTask task = taskFactory.getUpdateDocumentContentTask( document, content, mimeType);
		Activator.getDefault().getTaskManager().executeTaskSync(task);
	}

	private void showError(final Exception e) {
		String message = MessageFormat.format(FAILED_MESSAGE, document.getName());
		PluginMessage.openErrorFromThread(shell, HANDLER_NAME, message, e);
	}
}
