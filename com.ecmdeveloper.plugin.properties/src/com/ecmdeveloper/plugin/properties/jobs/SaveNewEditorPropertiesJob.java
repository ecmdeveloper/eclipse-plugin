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
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.eclipse.ui.IWorkbenchWindow;

import com.ecmdeveloper.plugin.core.model.IClassDescription;
import com.ecmdeveloper.plugin.core.model.IDocument;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.tasks.ICreateTask;
import com.ecmdeveloper.plugin.core.model.tasks.ICheckinTask;
import com.ecmdeveloper.plugin.core.model.tasks.IRefreshTask;
import com.ecmdeveloper.plugin.core.model.tasks.ISaveTask;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskFactory;
import com.ecmdeveloper.plugin.properties.Activator;
import com.ecmdeveloper.plugin.properties.editors.ObjectStoreItemEditor;
import com.ecmdeveloper.plugin.properties.editors.input.NewCustomObjectEditorInput;
import com.ecmdeveloper.plugin.properties.editors.input.NewDocumentEditorInput;
import com.ecmdeveloper.plugin.properties.editors.input.NewFolderEditorInput;
import com.ecmdeveloper.plugin.properties.editors.input.NewObjectStoreItemEditorInput;

/**
 * @author ricardo.belfor
 *
 */
public class SaveNewEditorPropertiesJob extends AbstractEditorJob {

	private static final String JOB_NAME = "Creating new object";
	private static final String MONITOR_MESSAGE = "Saving Editor Properties";
	private static final String FAILED_MESSAGE = "Creating new object \"{0}\" failed.";

	public SaveNewEditorPropertiesJob(ObjectStoreItemEditor editor,IWorkbenchWindow window) {
		super(editor, window, JOB_NAME);
	}

	@Override
	protected String getFailedMessage() {
		return MessageFormat.format( FAILED_MESSAGE, getEditorInput().getName() );		
	}

	@Override
	protected String getMonitorMessage() {
		return MONITOR_MESSAGE;
	}

	@Override
	protected void runEditorJob() throws Exception {
		if ( getEditorInput() instanceof NewObjectStoreItemEditorInput ) {
			saveNew();
			notifyEditor();
		} 
		scheduleRefresh();
	}

	private void saveNew() throws ExecutionException {
		
		NewObjectStoreItemEditorInput editorInput = (NewObjectStoreItemEditorInput) getEditorInput();
		
		ICreateTask task = createCreateTask(editorInput);
		Activator.getDefault().getTaskManager().executeTaskSync(task);
		editorInput.setObjectStoreItem( task.getNewObjectStoreItem() );
		
		ITaskFactory taskFactory;
		if ( editorInput instanceof NewDocumentEditorInput ) {
			IDocument document = storeDocumentContent(task.getNewObjectStoreItem(), editorInput);
			taskFactory = document.getTaskFactory();
			editorInput.setObjectStoreItem( document );
		} else {
			IObjectStoreItem newObjectStoreItem = task.getNewObjectStoreItem();
			taskFactory = newObjectStoreItem.getTaskFactory();
			editorInput.setObjectStoreItem( newObjectStoreItem );
		}
		
		IRefreshTask refreshTask = taskFactory.getRefreshTask( task.getNewObjectStoreItem() );
		Activator.getDefault().getTaskManager().executeTaskSync(refreshTask);
	}

	private ICreateTask createCreateTask(NewObjectStoreItemEditorInput editorInput) {
	
		IClassDescription classDescription = (IClassDescription) editorInput.getAdapter( IClassDescription.class);
		String className = classDescription.getName();
		
		ITaskFactory taskFactory = editorInput.getParent().getTaskFactory();
		if ( editorInput instanceof NewFolderEditorInput ) {
			return taskFactory.getCreateFolderTask( editorInput.getParent(), className, editorInput.getPropertiesMap() );
		} else if ( editorInput instanceof NewDocumentEditorInput ) {
			return taskFactory.getCreateDocumentTask( editorInput.getParent(), className, editorInput.getPropertiesMap() );
		} else if ( editorInput instanceof NewCustomObjectEditorInput ) {
			return taskFactory.getCreateCustomObjectTask( editorInput.getParent(), className, editorInput.getPropertiesMap() );
		} else {
			throw new UnsupportedOperationException( "Creation using the class " + editorInput.getName() + " not yet implemented" );
		}
	}

	private IDocument storeDocumentContent(IObjectStoreItem objectStoreItem, NewObjectStoreItemEditorInput editorInput) throws ExecutionException {
		NewDocumentEditorInput newDocumentEditorInput = (NewDocumentEditorInput) editorInput;
		
		ISaveTask saveTask = createSaveTask(objectStoreItem, newDocumentEditorInput);
		Activator.getDefault().getTaskManager().executeTaskSync(saveTask);

		ICheckinTask checkinTask = createCheckinTask(saveTask.getReservationDocument(), newDocumentEditorInput);
		Activator.getDefault().getTaskManager().executeTaskSync(checkinTask);
		
		return checkinTask.getDocument();
	}

	private ISaveTask createSaveTask(IObjectStoreItem objectStoreItem, NewDocumentEditorInput newDocumentEditorInput) {
		IDocument document = (IDocument) objectStoreItem;
		ArrayList<Object> content = newDocumentEditorInput.getContent();
		String mimeType = newDocumentEditorInput.getMimeType();
		ITaskFactory taskFactory = document.getTaskFactory();
		ISaveTask saveTask = taskFactory.getSaveTask( document, content, mimeType );
		return saveTask;
	}

	private ICheckinTask createCheckinTask(IDocument document, NewDocumentEditorInput newDocumentEditorInput) {
		boolean autoClassify = newDocumentEditorInput.isAutoClassify();
		boolean checkinMajor = newDocumentEditorInput.isCheckinMajor();
		ITaskFactory taskFactory = document.getTaskFactory();
		ICheckinTask checkinTask = taskFactory.getCheckinTask(document, checkinMajor, autoClassify);
		return checkinTask;
	}

	private void scheduleRefresh() {
		RefreshEditorPropertiesJob refreshJob = new RefreshEditorPropertiesJob((ObjectStoreItemEditor) editor, window );
		refreshJob.setUser(true);
		refreshJob.setRule( new EditorSchedulingRule(2) );
		refreshJob.schedule();
	}

	private void notifyEditor() throws ExecutionException {

		window.getShell().getDisplay().syncExec( new Runnable() {
			@Override
			public void run() {
				editor.saveNew();
			}} 
		);
	}
}
