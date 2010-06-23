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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.model.tasks.CancelCheckoutTask;
import com.ecmdeveloper.plugin.model.tasks.DocumentTask;
import com.ecmdeveloper.plugin.util.PluginMessage;

/**
 * @author Ricardo.Belfor
 *
 */
public class CancelCheckoutJob extends Job {

	private static final String HANDLER_NAME = "Cancel Checkout";
	private static final String FAILED_MESSAGE = "Canceling Checkout \"{0}\" failed";

	private Document document;
	private Shell shell;
	
	public CancelCheckoutJob(Document document, Shell shell) {
		super(HANDLER_NAME);
		this.document = document;
		this.shell = shell;
	}

	public Document getDocument() {
		return document;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		try {
			DocumentTask task = new CancelCheckoutTask(document);
			ObjectStoresManager.getManager().executeTaskSync(task);
			return Status.OK_STATUS;
		} catch (Exception e) {
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
