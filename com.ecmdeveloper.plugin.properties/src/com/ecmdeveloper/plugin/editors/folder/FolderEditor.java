/**
 * Copyright 2009, Ricardo Belfor
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

package com.ecmdeveloper.plugin.editors.folder;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

import com.ecmdeveloper.plugin.classes.model.ClassDescription;
import com.ecmdeveloper.plugin.editors.core.PropertiesInputForm;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.model.tasks.UpdateTask;
import com.ecmdeveloper.plugin.properties.util.PluginLog;
import com.ecmdeveloper.plugin.properties.util.PluginMessage;

/**
 * @author Ricardo Belfor
 *
 */
public class FolderEditor extends FormEditor implements PropertyChangeListener {

	public static final String EDITOR_ID = "com.ecmdeveloper.plugin.editors.folderEditor";
	
//	private FolderEditorForm folderEditorForm;
	private PropertiesInputForm propertiesInputForm;
	private ClassDescription classDescription;
	private ObjectStoreItem objectStoreItem;
	private boolean isPageModified;
	
	@Override
	protected void addPages() {
		try {
			classDescription = (ClassDescription) getEditorInput().getAdapter( ClassDescription.class);

			addFolderEditorForm();
			
			objectStoreItem = (ObjectStoreItem) getEditorInput().getAdapter( ObjectStoreItem.class);
			objectStoreItem.addPropertyChangeListener(this);
			isPageModified = false;
			
		} catch (PartInitException e) {
			PluginLog.error( e );
		}
	}

	private void addFolderEditorForm() throws PartInitException {
		propertiesInputForm = new PropertiesInputForm(this, classDescription);
		addPage(propertiesInputForm);
//		folderEditorForm = new FolderEditorForm(this, classDescription);
//		addPage( folderEditorForm );
	}

	@Override
	protected void setActivePage(int pageIndex) {
		super.setActivePage(pageIndex);
		propertiesInputForm.refreshFormContent(objectStoreItem);
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public boolean isDirty() {
		return isPageModified;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}


	@Override
	public void doSave(IProgressMonitor monitor) {

		isPageModified = false;
		monitor.beginTask("Saving folder", 1);
		
		try {
			UpdateTask task = new UpdateTask(objectStoreItem);
			ObjectStoresManager.getManager().executeTaskSync(task);
			firePropertyChange(IEditorPart.PROP_DIRTY);
		} catch (Exception e) {
			PluginMessage.openError(getSite().getShell(), "Folder Editor" , "Save failed.", e );
		} finally {
			monitor.done();
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		boolean wasDirty = isDirty();
		isPageModified = true;
		if (!wasDirty) {
			firePropertyChange(IEditorPart.PROP_DIRTY);
		}
	}
}
