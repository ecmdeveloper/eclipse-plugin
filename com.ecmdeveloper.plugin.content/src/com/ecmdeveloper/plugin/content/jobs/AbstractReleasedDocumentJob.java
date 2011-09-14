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

package com.ecmdeveloper.plugin.content.jobs;

import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IWorkbenchWindow;

import com.ecmdeveloper.plugin.content.Activator;
import com.ecmdeveloper.plugin.core.model.IDocument;
import com.ecmdeveloper.plugin.core.model.tasks.IGetReleasedVersionTask;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskFactory;
import com.ecmdeveloper.plugin.core.util.PluginMessage;

/**
 * 
 * @author Ricardo.Belfor
 *
 */
public abstract class AbstractReleasedDocumentJob extends Job {

	private IDocument releasedVersionDocument;
	private IDocument document;
	private IWorkbenchWindow window;

	public AbstractReleasedDocumentJob(String name, IDocument document, IWorkbenchWindow window) {
		super(name);
		this.document = document;
		this.window = window;
	}

	public IDocument getReleasedVersionDocument() {
		return releasedVersionDocument;
	}

	public IWorkbenchWindow getWindow() {
		return window;
	}

	protected String getDocumentName() {
		return document.getName();
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		ITaskFactory taskFactory = document.getTaskFactory();
		IGetReleasedVersionTask task = taskFactory.getGetReleasedVersionTask(document);
		try {
			Activator.getDefault().getTaskManager().executeTaskSync(task);
			releasedVersionDocument = task.getReleasedVersionDocument();
		} catch (ExecutionException e) {
			PluginMessage.openErrorFromThread(window.getShell(), getName(),
					"Getting released version failed", e);
			return Status.CANCEL_STATUS;
		}

		scheduleNextJob(monitor);
		return Status.OK_STATUS;
	}

	protected abstract void scheduleNextJob(IProgressMonitor monitor);
}
