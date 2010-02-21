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
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.properties.util.PluginLog;
import com.ecmdeveloper.plugin.properties.util.PluginMessage;

/**
 * @author Ricardo.Belfor
 *
 */
public class ObjectStoreItemEditor extends FormEditor implements PropertyChangeListener {

	private static final String SAVE_PROPERTIES_COMMAND_ID = "com.ecmdeveloper.plugin.saveProperties";
	private PropertiesInputForm propertiesInputForm;
	private ClassDescription classDescription;
	private ObjectStoreItem objectStoreItem;
	private boolean isPageModified;
	
	@Override
	protected void addPages() {
		try {
			classDescription = (ClassDescription) getEditorInput().getAdapter( ClassDescription.class);

			addPropertiesInputForm();
			
			objectStoreItem = (ObjectStoreItem) getEditorInput().getAdapter( ObjectStoreItem.class);
			objectStoreItem.addPropertyChangeListener(this);
			isPageModified = false;
			
			updateTitle();
			
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
		propertiesInputForm.refreshFormContent(objectStoreItem);
	}

	public void refreshProperties() {
		getSite().getShell().getDisplay().syncExec( new Runnable() {
			@Override
			public void run() {
				propertiesInputForm.refreshFormContent(objectStoreItem);
			}} 
		);
	}
	
	private void updateTitle() {
		setPartName( objectStoreItem.getName() );
		setTitleToolTip( "Folder: " + objectStoreItem.getName() );
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
			IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
			handlerService.executeCommand(SAVE_PROPERTIES_COMMAND_ID, null );
		} catch (Exception e) {
			PluginMessage.openError(getSite().getShell(), "Folder Editor" , "Save failed.", e );
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
}
