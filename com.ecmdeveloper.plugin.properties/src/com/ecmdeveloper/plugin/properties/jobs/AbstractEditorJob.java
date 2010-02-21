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

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchWindow;

import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.properties.editors.ObjectStoreItemEditor;
import com.ecmdeveloper.plugin.properties.util.PluginMessage;

/**
 * @author Ricardo.Belfor
 *
 */
public abstract class AbstractEditorJob extends Job {

	protected ObjectStoreItemEditor editor;
	protected ObjectStoreItem objectStoreItem;
	protected IWorkbenchWindow window;

	public AbstractEditorJob(ObjectStoreItemEditor editor,IWorkbenchWindow window, String name ) {
		super(name);
		this.editor = editor;
		this.window = window;
		IEditorInput editorInput = getEditorInput();
		objectStoreItem = (ObjectStoreItem) editorInput.getAdapter( ObjectStoreItem.class);
	}

	protected IEditorInput getEditorInput() {
		return this.editor.getEditorInput();
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			monitor.beginTask(getMonitorMessage(), IProgressMonitor.UNKNOWN );
			runEditorJob();
		} catch (Exception e) {
			PluginMessage.openErrorFromThread(window.getShell(), getName(), MessageFormat.format(
					getFailedMessage(), objectStoreItem.getName()), e);
		}
		return Status.OK_STATUS;
	}

	@Override
	public boolean belongsTo(Object family) {
		return family instanceof AbstractEditorJob;
	}

	protected abstract void runEditorJob() throws Exception;

	protected abstract String getFailedMessage();

	protected abstract String getMonitorMessage();
}
