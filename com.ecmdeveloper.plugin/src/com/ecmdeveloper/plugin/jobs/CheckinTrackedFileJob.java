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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;

import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.model.tasks.FetchObjectTask;
import com.ecmdeveloper.plugin.tracker.model.TrackedFile;
import com.ecmdeveloper.plugin.util.PluginMessage;
import com.ecmdeveloper.plugin.wizard.CheckinWizard;

/**
 * @author Ricardo.Belfor
 *
 */
public class CheckinTrackedFileJob extends Job {

	private static final String FETCHING_DOCUMENT_TASK = "Fetching document \"{0}\"";
	private static final String JOB_NAME = "Checkin Tracked File";
	private TrackedFile trackedFile;
	private IWorkbenchWindow window;
	private IFile file;
	
	public CheckinTrackedFileJob(TrackedFile trackedFile, IFile file, IWorkbenchWindow window) {
		super(JOB_NAME);
		this.trackedFile = trackedFile;
		this.window = window;
		this.file = file;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		try {
			openCheckinWizard( getDocument(monitor) );
		} catch (ExecutionException e) {
			PluginMessage.openErrorFromThread(window.getShell(), JOB_NAME, JOB_NAME + " failed.",
					e);
			return Status.CANCEL_STATUS;
		}
		
		return Status.OK_STATUS;
	}

	private Document getDocument(IProgressMonitor monitor) throws ExecutionException {
		
		ObjectStore objectStore = getObjectStore(monitor);
		String message = MessageFormat.format(FETCHING_DOCUMENT_TASK, trackedFile.getName());
		monitor.beginTask(message, IProgressMonitor.UNKNOWN );
		FetchObjectTask task = new FetchObjectTask(objectStore, trackedFile.getId(), trackedFile
				.getClassName(), "Document");
		Document document = (Document) ObjectStoresManager.getManager().executeTaskSync(task);
		monitor.done();
		return document;
	}

	private ObjectStore getObjectStore(IProgressMonitor monitor) throws ExecutionException {
		String objectStoreName = trackedFile.getObjectStoreName();
		String connectionName = trackedFile.getConnectionName();
		ObjectStore objectStore = ObjectStoresManager.getManager().getObjectStore(connectionName , objectStoreName );
		if ( ! objectStore.isConnected() ) {
			ObjectStoresManager.getManager().connectConnection(objectStore.getConnection(), monitor );
		}
		return objectStore;
	}

	private void openCheckinWizard(Document document) {
		CheckinWizard wizard = new CheckinWizard( document );
		wizard.setInitialContent(file);

		final WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
		window.getWorkbench().getDisplay().syncExec( new Runnable() {
			@Override
			public void run() {
				dialog.create();
				dialog.open();
			}
		} );
	}
}
