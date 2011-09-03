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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IWorkbenchWindow;

import com.ecmdeveloper.plugin.content.util.PluginMessage;
import com.ecmdeveloper.plugin.core.model.tasks.TaskManager;
import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.tasks.CancelCheckoutTask;
import com.ecmdeveloper.plugin.model.tasks.DocumentTask;
import com.ecmdeveloper.plugin.tracker.model.FilesTracker;
import com.ecmdeveloper.plugin.tracker.model.TrackedFile;

/**
 * @author ricardo.belfor
 *
 */
public class CancelCheckoutTrackedFileJob extends AbstractTrackedFileJob {

	private static final String CANCELING_CHECKOUT_TASK = "Canceling Checkout \"{0}\"";
	private static final String HANDLER_NAME = "Cancel Checkout";
	private static final String FAILED_MESSAGE = "Canceling Checkout \"{0}\" failed";

	public CancelCheckoutTrackedFileJob(TrackedFile trackedFile, IFile file, IWorkbenchWindow window) {
		super(HANDLER_NAME, trackedFile, file, window);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			Document document = getDocument(monitor);
			String message = MessageFormat.format( CANCELING_CHECKOUT_TASK, document.getName() );
			monitor.beginTask( message, IProgressMonitor.UNKNOWN );
			DocumentTask task = new CancelCheckoutTask(document);
			TaskManager.getInstance().executeTaskSync(task);
			removeFromFilesTracker(document.getVersionSeriesId());
			return Status.OK_STATUS;
		} catch (Exception e) {
			showError(e);
		}
		return Status.CANCEL_STATUS;
	}

	private void removeFromFilesTracker(String versionSeriesId) {
		if ( FilesTracker.getInstance().isVersionSeriesTracked(versionSeriesId) ) {
			FilesTracker.getInstance().removeTrackedVersionSeries(versionSeriesId);
		}
	}

	private void showError(final Exception e) {
		String message = MessageFormat.format(FAILED_MESSAGE, getFile().toString() );
		PluginMessage.openErrorFromThread(getWindow().getShell(), HANDLER_NAME, message, e);
	}
}
