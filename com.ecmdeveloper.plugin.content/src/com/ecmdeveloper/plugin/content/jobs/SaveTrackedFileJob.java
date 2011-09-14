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
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;

import com.ecmdeveloper.plugin.content.Activator;
import com.ecmdeveloper.plugin.core.model.IDocument;
import com.ecmdeveloper.plugin.core.model.tasks.IDocumentTask;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskFactory;
import com.ecmdeveloper.plugin.core.util.PluginMessage;
import com.ecmdeveloper.plugin.tracker.model.TrackedFile;

/**
 * @author ricardo.belfor
 *
 */
public class SaveTrackedFileJob extends AbstractTrackedFileJob {

	private static final String JOB_NAME = "Save Tracked File";
	private static final String TASK_MESSAGE = "Saving document \"{0}\"";
	private static final String FAILED_MESSAGE = "Saving \"{0}\" failed";

	private String mimeType;

	public SaveTrackedFileJob(TrackedFile trackedFile, IFile file,
			IWorkbenchWindow window, String mimeType) {
		super(JOB_NAME, trackedFile, file, window);
		this.mimeType = mimeType;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			IDocument document = getDocument(monitor);
			saveDocument(document, monitor);
			return Status.OK_STATUS;
		} catch (final Exception e) {
			monitor.done();
			showError(e);
		}
		return Status.CANCEL_STATUS;
	}

	private void saveDocument(IDocument document, IProgressMonitor monitor) throws ExecutionException {
		
		String taskName = MessageFormat.format( TASK_MESSAGE, document.getName() );
		monitor.beginTask(taskName, IProgressMonitor.UNKNOWN );
		
		Collection<Object> files = new ArrayList<Object>();
		files.add( getFile() );
		ITaskFactory taskFactory = document.getTaskFactory();
		IDocumentTask task = taskFactory.getSaveTask( document, files, mimeType);
		Activator.getDefault().getTaskManager().executeTaskSync(task);
		
		monitor.done();
	}

	private void showError(final Exception e) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				String message = MessageFormat.format(FAILED_MESSAGE, getFile().getName());
				PluginMessage.openError(getWindow().getShell(), getName(), message, e);
			}
		});
	}
}
