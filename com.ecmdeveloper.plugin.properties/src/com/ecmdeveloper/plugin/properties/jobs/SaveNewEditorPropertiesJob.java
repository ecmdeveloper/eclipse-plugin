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

import com.ecmdeveloper.plugin.classes.model.ClassDescription;
import com.ecmdeveloper.plugin.core.model.tasks.TaskManager;
import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.model.tasks.CheckinTask;
import com.ecmdeveloper.plugin.model.tasks.CreateCustomObjectTask;
import com.ecmdeveloper.plugin.model.tasks.CreateDocumentTask;
import com.ecmdeveloper.plugin.model.tasks.CreateFolderTask;
import com.ecmdeveloper.plugin.model.tasks.CreateTask;
import com.ecmdeveloper.plugin.model.tasks.RefreshTask;
import com.ecmdeveloper.plugin.model.tasks.SaveTask;
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
		
		CreateTask task = createCreateTask(editorInput);
		TaskManager.getInstance().executeTaskSync(task);
		editorInput.setObjectStoreItem( task.getNewObjectStoreItem() );
		
		if ( editorInput instanceof NewDocumentEditorInput ) {
			Document document = storeDocumentContent(task.getNewObjectStoreItem(), editorInput);
			editorInput.setObjectStoreItem( document );
		} else {
			editorInput.setObjectStoreItem( task.getNewObjectStoreItem() );
		}
		
		RefreshTask refreshTask = new RefreshTask( task.getNewObjectStoreItem() );
		TaskManager.getInstance().executeTaskSync(refreshTask);
	}

	private CreateTask createCreateTask(NewObjectStoreItemEditorInput editorInput) {
	
		ClassDescription classDescription = (ClassDescription) editorInput.getAdapter( ClassDescription.class);
		String className = classDescription.getName();
		
		if ( editorInput instanceof NewFolderEditorInput ) {
			return new CreateFolderTask( editorInput.getParent(), className, editorInput.getPropertiesMap() );
		} else if ( editorInput instanceof NewDocumentEditorInput ) {
			return new CreateDocumentTask( editorInput.getParent(), className, editorInput.getPropertiesMap() );
		} else if ( editorInput instanceof NewCustomObjectEditorInput ) {
			return new CreateCustomObjectTask( editorInput.getParent(), className, editorInput.getPropertiesMap() );
		} else {
			throw new UnsupportedOperationException( "Creation using the class " + editorInput.getName() + " not yet implemented" );
		}
	}

	private Document storeDocumentContent(ObjectStoreItem objectStoreItem, NewObjectStoreItemEditorInput editorInput) throws ExecutionException {
		NewDocumentEditorInput newDocumentEditorInput = (NewDocumentEditorInput) editorInput;
		
		SaveTask saveTask = createSaveTask(objectStoreItem, newDocumentEditorInput);
		TaskManager.getInstance().executeTaskSync(saveTask);

		CheckinTask checkinTask = createCheckinTask(saveTask.getReservationDocument(), newDocumentEditorInput);
		TaskManager.getInstance().executeTaskSync(checkinTask);
		
		return checkinTask.getDocument();
	}

	private SaveTask createSaveTask(ObjectStoreItem objectStoreItem, NewDocumentEditorInput newDocumentEditorInput) {
		Document document = (Document) objectStoreItem;
		ArrayList<Object> content = newDocumentEditorInput.getContent();
		String mimeType = newDocumentEditorInput.getMimeType();
		SaveTask saveTask = new SaveTask( document, content, mimeType );
		return saveTask;
	}

	private CheckinTask createCheckinTask(Document document, NewDocumentEditorInput newDocumentEditorInput) {
		boolean autoClassify = newDocumentEditorInput.isAutoClassify();
		boolean checkinMajor = newDocumentEditorInput.isCheckinMajor();
		CheckinTask checkinTask = new CheckinTask(document, checkinMajor, autoClassify);
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
