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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.model.tasks.DocumentTask;
import com.ecmdeveloper.plugin.model.tasks.SaveTask;
import com.ecmdeveloper.plugin.content.util.PluginMessage;

/**
 * @author Ricardo.Belfor
 *
 */
public class SaveJob extends Job {

	private static final String HANDLER_NAME = "Save";
	private static final String FAILED_MESSAGE = "Saving \"{0}\" failed";
	private static final String TASK_MESSAGE = "Saving document \"{0}\"";

	private Document document;
	private Shell shell;
	private Collection<Object> content;
	private String mimeType;

	public SaveJob(Document document, Shell shell, Collection<Object> content, String mimeType ) {
		super(HANDLER_NAME);
		this.document = document;
		this.shell = shell;
		this.content = content;
		this.mimeType = mimeType;
	}

	public Document getDocument() {
		return document;
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			String taskName = MessageFormat.format( TASK_MESSAGE, document.getName() );
			monitor.beginTask(taskName, IProgressMonitor.UNKNOWN );

			DocumentTask task = new SaveTask(document, content, mimeType);
			ObjectStoresManager.getManager().executeTaskSync(task);
			
			monitor.done();
			return Status.OK_STATUS;
		} catch (final Exception e) {
			monitor.done();
			showError(e);
		}
		return Status.CANCEL_STATUS;
	}

	private void showError(final Exception e) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				String message = MessageFormat.format(FAILED_MESSAGE, document.getName());
				PluginMessage.openError(shell, HANDLER_NAME, message, e);
			}
		});
	}
}
