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

package com.ecmdeveloper.plugin.properties.editors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.handlers.IHandlerService;

import com.ecmdeveloper.plugin.classes.model.ClassDescription;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.model.ObjectStoresManagerEvent;
import com.ecmdeveloper.plugin.model.ObjectStoresManagerListener;
import com.ecmdeveloper.plugin.model.ObjectStoresManagerRefreshEvent;
import com.ecmdeveloper.plugin.properties.model.PropertiesObject;
import com.ecmdeveloper.plugin.properties.model.UnsavedPropertiesObject;
import com.ecmdeveloper.plugin.properties.util.PluginLog;
import com.ecmdeveloper.plugin.properties.util.PluginMessage;

/**
 * @author Ricardo.Belfor
 *
 */
public class ObjectStoreItemEditor extends FormEditor implements PropertyChangeListener, ObjectStoresManagerListener {

	private static final String SAVE_PROPERTIES_COMMAND_ID = "com.ecmdeveloper.plugin.saveProperties";
	private static final String SAVE_NEW_PROPERTIES_COMMAND_ID = "com.ecmdeveloper.plugin.saveNewProperties";
	
	private PropertiesInputForm propertiesInputForm;
	private ClassDescription classDescription;
	private PropertiesObject propertiesObject;
	private boolean isPageModified;
	
	@Override
	protected void addPages() {
		try {
			classDescription = (ClassDescription) getEditorInput().getAdapter( ClassDescription.class);

			addPropertiesInputForm();
			
			propertiesObject = (PropertiesObject) getEditorInput().getAdapter( PropertiesObject.class);
			propertiesObject.addPropertyChangeListener(this);
			isPageModified = propertiesObject instanceof UnsavedPropertiesObject;
		
			updateTitle();
			
			ObjectStoresManager.getManager().addObjectStoresManagerListener(this);
			
		} catch (PartInitException e) {
			PluginLog.error( e );
		}
	}

	private void addPropertiesInputForm() throws PartInitException {
		propertiesInputForm = new PropertiesInputForm(this, classDescription);
		addPage(propertiesInputForm);
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);

		site.setSelectionProvider( new ISelectionProvider() {

			@Override
			public void addSelectionChangedListener( ISelectionChangedListener listener) {
				// No changing selection
			}

			@Override
			public ISelection getSelection() {
				//Object selection = getEditorInput().getAdapter( ObjectStoreItem.class);
				return new StructuredSelection( ObjectStoreItemEditor.this );
			}

			@Override
			public void removeSelectionChangedListener(ISelectionChangedListener listener) {
				// No changing selection
			}

			@Override
			public void setSelection(ISelection selection) {
				// No changing selection
			}
		});

	}

	@Override
	protected void setActivePage(int pageIndex) {
		super.setActivePage(pageIndex);
		propertiesInputForm.refreshFormContent();
	}

	public void refreshProperties() {
		
		
		getSite().getShell().getDisplay().syncExec( new Runnable() {
			@Override
			public void run() {
				propertiesInputForm.refreshFormContent();
			}} 
		);
	}
	
	private void updateTitle() {
		setPartName( propertiesObject.getName() );
		setTitleToolTip( propertiesObject.getName() );
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
	public void doSave(final IProgressMonitor monitor) {
		try {
			String commandId = getSaveCommandId();
			IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
			handlerService.executeCommand(commandId, null );
		} catch (Exception e) {
			PluginMessage.openError(getSite().getShell(), getEditorName() , "Save failed.", e );
		}
	}

	private String getEditorName() {
		// TODO fix this for other object types
		return "Folder Editor";
	}

	private String getSaveCommandId() {
		String commandId;
		
		Object object = getEditorInput().getAdapter( ObjectStoreItem.class);
		if ( object == null ) {
			commandId = SAVE_NEW_PROPERTIES_COMMAND_ID;
		} else {
			commandId = SAVE_PROPERTIES_COMMAND_ID;
		}
		return commandId;
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		boolean wasDirty = isDirty();
		isPageModified = true;
		if (!wasDirty) {
			firePropertyChange(IEditorPart.PROP_DIRTY);
		}
	}

	public void saved() {
		isPageModified = false;
		getSite().getShell().getDisplay().syncExec( new Runnable() {
			@Override
			public void run() {
				updateTitle();
				firePropertyChange(IEditorPart.PROP_DIRTY);
			}} 
		);
	}

	public void saveNew() {
		updatePropertiesObject();
		saved();
	}

	private void updatePropertiesObject() {
		propertiesObject.removePropertyChangeListener(this);
		propertiesObject = (PropertiesObject) getEditorInput().getAdapter( PropertiesObject.class);
		propertiesObject.addPropertyChangeListener(this);
	}

	@Override
	public void objectStoreItemsChanged(ObjectStoresManagerEvent event) {
		if ( event.getItemsRemoved() != null ) {
			closeEditorOnDelete(event);
		}
	}

	private void closeEditorOnDelete(ObjectStoresManagerEvent event) {
		ObjectStoreItem objectStoreItem;
		objectStoreItem = (ObjectStoreItem) getEditorInput().getAdapter(ObjectStoreItem.class);
		if (objectStoreItem != null) {
			for (IObjectStoreItem itemRemoved : event.getItemsRemoved()) {
				if (itemRemoved.equals(objectStoreItem)) {
					close(false);
					return;
				}
			}
		}
	}

	@Override
	public void objectStoreItemsRefreshed(ObjectStoresManagerRefreshEvent event) {
	}

	@Override
	public void dispose() {
		super.dispose();
		ObjectStoresManager.getManager().removeObjectStoresManagerListener(this);
	}
}
