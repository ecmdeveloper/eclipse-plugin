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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IWorkbenchWindow;

import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.model.tasks.FetchObjectTask;
import com.ecmdeveloper.plugin.tracker.model.TrackedFile;

/**
 * @author ricardo.belfor
 *
 */
public abstract class AbstractTrackedFileJob extends Job {

	private static final String FETCHING_DOCUMENT_TASK = "Fetching document \"{0}\"";

	private TrackedFile trackedFile;
	private IWorkbenchWindow window;
	private IFile file;
	
	public AbstractTrackedFileJob(String name, TrackedFile trackedFile, IFile file, IWorkbenchWindow window) {
		super(name);
		this.trackedFile = trackedFile;
		this.window = window;
		this.file = file;
	}

	public IWorkbenchWindow getWindow() {
		return window;
	}

	public IFile getFile() {
		return file;
	}

	protected String getMimeType() {
		return trackedFile.getMimeType();
	}
	protected Document getDocument(IProgressMonitor monitor) throws ExecutionException {
		
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
}
