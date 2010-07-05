/**
 * Copyright 2010, Ricardo Belfor
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

package com.ecmdeveloper.plugin.jobs;

import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IWorkbenchWindow;

import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.model.tasks.GetCurrentVersionTask;
import com.ecmdeveloper.plugin.util.PluginMessage;

/**
 * 
 * @author Ricardo.Belfor
 *
 */
public abstract class AbstractCurrentDocumentJob extends Job {

	private Document currentVersionDocument;
	private Document document;
	private IWorkbenchWindow window;

	public AbstractCurrentDocumentJob(String name, Document document, IWorkbenchWindow window) {
		super(name);
		this.document = document;
		this.window = window;
	}

	public Document getCurrentVersionDocument() {
		return currentVersionDocument;
	}

	public IWorkbenchWindow getWindow() {
		return window;
	}

	protected String getDocumentName() {
		return document.getName();
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		GetCurrentVersionTask task = new GetCurrentVersionTask(document);
		try {
			ObjectStoresManager.getManager().executeTaskSync(task);
			currentVersionDocument = task.getCurrentVersionDocument();
		} catch (ExecutionException e) {
			PluginMessage.openErrorFromThread(window.getShell(), getName(),
					"Getting current version failed", e);
			return Status.CANCEL_STATUS;
		}

		scheduleNextJob(monitor);
		return Status.OK_STATUS;
	}

	protected abstract void scheduleNextJob(IProgressMonitor monitor);
}