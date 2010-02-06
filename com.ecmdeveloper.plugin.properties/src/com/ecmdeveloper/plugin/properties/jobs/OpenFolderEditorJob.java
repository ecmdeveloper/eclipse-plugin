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
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

import com.ecmdeveloper.plugin.classes.model.ClassDescription;
import com.ecmdeveloper.plugin.classes.model.PropertyDescription;
import com.ecmdeveloper.plugin.classes.model.task.GetClassDescriptionTask;
import com.ecmdeveloper.plugin.editors.folder.FolderEditor;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.model.tasks.FetchPropertiesTask;
import com.ecmdeveloper.plugin.properties.editors.ObjectStoreItemEditorInput;
import com.ecmdeveloper.plugin.properties.util.PluginMessage;

/**
 * @author Ricardo.Belfor
 *
 */
public class OpenFolderEditorJob extends Job {

	private static final String JOB_NAME = "Open Editor";
	private static final String MONITOR_MESSAGE = "Opening Editor";
	private static final String FAILED_MESSAGE = "Opening Editor for \"{0}\" failed";

	private ObjectStoreItem objectStoreItem;
	private IWorkbenchWindow window;
	
	public OpenFolderEditorJob(ObjectStoreItem objectStoreItem,IWorkbenchWindow window) {
		super(JOB_NAME);
		this.objectStoreItem = objectStoreItem;
		this.window = window;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			monitor.beginTask(MONITOR_MESSAGE, 1);
			openNewFolderEditor();
			monitor.worked(1);
		} catch (Exception e) {
			PluginMessage.openErrorFromThread(window.getShell(), JOB_NAME, MessageFormat.format(
					FAILED_MESSAGE, objectStoreItem.getName()), e);
		}
		
		return Status.OK_STATUS;
	}

	private void openNewFolderEditor() throws Exception {
		
		ClassDescription classDescription = getClassDescription();
		fetchProperties(classDescription);

		IEditorInput input = new ObjectStoreItemEditorInput( (ObjectStoreItem) objectStoreItem, classDescription );
		String editorId = FolderEditor.EDITOR_ID;
		
		openEditorWindow(input, editorId);
	}

	private void fetchProperties(ClassDescription classDescription)
			throws Exception {
		String[] propertyNames = getPropertyNames(classDescription);
		FetchPropertiesTask task = new FetchPropertiesTask(objectStoreItem, propertyNames);
		ObjectStoresManager.getManager().executeTaskSync(task);
	}

	private String[] getPropertyNames(ClassDescription classDescription) {

		Set<String> propertyNames = new HashSet<String>();
		for (PropertyDescription propertyDescription : classDescription.getPropertyDescriptions()) {
			propertyNames.add(propertyDescription.getName());
		}

		return propertyNames.toArray(new String[0]);
	}

	private ClassDescription getClassDescription() throws Exception {

		GetClassDescriptionTask task = new GetClassDescriptionTask(objectStoreItem.getClassName(),
				objectStoreItem.getObjectStore());
		ClassDescription classDescription = (ClassDescription) ObjectStoresManager.getManager()
				.executeTaskSync(task);
		return classDescription;
	}

	private void openEditorWindow(final IEditorInput input, final String editorId) {
		
		window.getWorkbench().getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				try {
					IDE.openEditor( window.getActivePage(), input, editorId);
				} catch (PartInitException e) {
					PluginMessage.openError(window.getShell(), JOB_NAME, MessageFormat.format(
					FAILED_MESSAGE, objectStoreItem.getName()), e);
				}
			}
		} );
	}
	
}
