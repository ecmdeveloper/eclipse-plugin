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

import java.util.concurrent.ExecutionException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;

import com.ecmdeveloper.plugin.content.util.PluginMessage;
import com.ecmdeveloper.plugin.content.wizard.CheckinWizard;
import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.tracker.model.TrackedFile;

/**
 * @author Ricardo.Belfor
 *
 */
public class CheckinTrackedFileJob extends AbstractTrackedFileJob {

	private static final String JOB_NAME = "Checkin Tracked File";
	
	public CheckinTrackedFileJob(TrackedFile trackedFile, IFile file, IWorkbenchWindow window) {
		super(JOB_NAME, trackedFile, file, window);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		try {
			openCheckinWizard( getDocument(monitor) );
		} catch (ExecutionException e) {
			PluginMessage.openErrorFromThread(getWindow().getShell(), JOB_NAME, JOB_NAME + " failed.",
					e);
			return Status.CANCEL_STATUS;
		}
		
		return Status.OK_STATUS;
	}

	private void openCheckinWizard(Document document) {
		CheckinWizard wizard = new CheckinWizard( document, true );
		wizard.setInitialContent(getFile(), getMimeType() );

		final WizardDialog dialog = new WizardDialog(getWindow().getShell(), wizard);
		getWindow().getWorkbench().getDisplay().syncExec( new Runnable() {
			@Override
			public void run() {
				dialog.create();
				dialog.open();
			}
		} );
	}
}
