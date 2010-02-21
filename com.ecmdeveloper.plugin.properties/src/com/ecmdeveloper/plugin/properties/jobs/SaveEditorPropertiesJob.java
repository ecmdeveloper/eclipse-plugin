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

package com.ecmdeveloper.plugin.properties.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchWindow;

import com.ecmdeveloper.plugin.classes.model.ClassDescription;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.model.tasks.UpdateTask;
import com.ecmdeveloper.plugin.properties.editors.ObjectStoreItemEditor;

/**
 * @author Ricardo.Belfor
 *
 */
public class SaveEditorPropertiesJob extends AbstractEditorJob {

	private static final String JOB_NAME = "Save Editor Properties";
	private static final String MONITOR_MESSAGE = "Saving Editor Properties";
	private static final String FAILED_MESSAGE = "Saving Editor Properties for \"{0}\" failed";

	public SaveEditorPropertiesJob(ObjectStoreItemEditor editor,IWorkbenchWindow window) {
		super(editor, window, JOB_NAME);
	}

	@Override
	protected String getFailedMessage() {
		return FAILED_MESSAGE;
	}

	@Override
	protected String getMonitorMessage() {
		return MONITOR_MESSAGE;
	}

	@Override
	protected void runEditorJob() throws Exception {
		UpdateTask task = new UpdateTask(objectStoreItem);
		ObjectStoresManager.getManager().executeTaskSync(task);

		window.getShell().getDisplay().syncExec( new Runnable() {
			@Override
			public void run() {
				editor.saved();
			}} 
		);
	}
}
