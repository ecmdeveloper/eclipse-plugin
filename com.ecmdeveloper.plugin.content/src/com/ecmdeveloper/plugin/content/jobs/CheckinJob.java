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
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;

import com.ecmdeveloper.plugin.content.util.PluginMessage;
import com.ecmdeveloper.plugin.core.model.tasks.TaskManager;
import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.tasks.CheckinTask;
import com.ecmdeveloper.plugin.model.tasks.DocumentTask;
import com.ecmdeveloper.plugin.model.tasks.SaveTask;
import com.ecmdeveloper.plugin.tracker.model.FilesTracker;

/**
 * @author Ricardo.Belfor
 *
 */
public class CheckinJob extends Job {

	private static final String CHECKIN_DOCUMENT_SUBTASK_NAME = "Checkin Document";
	private static final String SAVING_CONTENT_SUBTASK_NAME = "Saving Content";
	private static final String TASK_MESSAGE = "Checking in document \"{0}\"";
	private static final String HANDLER_NAME = "Checkin";
	private static final String FAILED_MESSAGE = "Checkin \"{0}\" failed";

	private Document document;
	private Shell shell;
	private Collection<Object> content;
	private String mimeType;
	private boolean majorVersion;
	
	public CheckinJob(Document document, Shell shell, Collection<Object> content, String mimeType, boolean majorVersion ) {
		super(HANDLER_NAME);
		this.document = document;
		this.shell = shell;
		this.content = content;
		this.mimeType = mimeType;
		this.majorVersion = majorVersion;
	}

	public Document getDocument() {
		return document;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			String taskName = MessageFormat.format( TASK_MESSAGE, document.getName() );
			monitor.beginTask(taskName, IProgressMonitor.UNKNOWN );
			saveContent(monitor);
			checkinDocument(monitor);
			removeFromFilesTracker();
			monitor.done();
			return Status.OK_STATUS;
		} catch (final Exception e) {
			monitor.done();
			e.printStackTrace();
			showError(e);
		}
		return Status.CANCEL_STATUS;
	}

	private void removeFromFilesTracker() {
		String versionSeriesId = document.getVersionSeriesId();
		if ( FilesTracker.getInstance().isVersionSeriesTracked(versionSeriesId) ) {
			FilesTracker.getInstance().removeTrackedVersionSeries(versionSeriesId);
		}
	}

	private void saveContent(IProgressMonitor monitor) throws ExecutionException {
		if ( content != null ) {
			monitor.subTask( SAVING_CONTENT_SUBTASK_NAME );
			DocumentTask task = new SaveTask(document, content, mimeType);
			TaskManager.getInstance().executeTaskSync(task);
		}
	}

	private void checkinDocument(IProgressMonitor monitor) throws ExecutionException {
		monitor.subTask( CHECKIN_DOCUMENT_SUBTASK_NAME );
		CheckinTask task  = new CheckinTask(document, majorVersion, false );
		TaskManager.getInstance().executeTaskSync(task);
	}

	private void showError(final Exception e) {
		String message = MessageFormat.format(FAILED_MESSAGE, document.getName());
		PluginMessage.openErrorFromThread(shell, HANDLER_NAME, message, e);
	}
}
