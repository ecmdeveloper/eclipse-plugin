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

package com.ecmdeveloper.plugin.properties.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import com.ecmdeveloper.plugin.core.model.ICustomObject;
import com.ecmdeveloper.plugin.core.model.IDocument;
import com.ecmdeveloper.plugin.core.model.IFolder;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.properties.editors.CustomObjectEditor;
import com.ecmdeveloper.plugin.properties.editors.DocumentEditor;
import com.ecmdeveloper.plugin.properties.editors.FolderEditor;
import com.ecmdeveloper.plugin.properties.editors.input.ObjectStoreItemEditorInput;
import com.ecmdeveloper.plugin.properties.jobs.OpenObjectStoreItemEditorJob;
import com.ecmdeveloper.plugin.properties.util.PluginLog;

/**
 * @author Ricardo.Belfor
 *
 */
public abstract class AbstractEditHandler extends AbstractHandler {

	protected void openObjectStoreItemEditor(IObjectStoreItem objectStoreItem, IWorkbenchWindow window) {
		if (objectStoreItem instanceof IFolder ) {
			showEditor( objectStoreItem, window, FolderEditor.EDITOR_ID );
		} else if (objectStoreItem instanceof IDocument ) {
			showEditor( objectStoreItem, window, DocumentEditor.EDITOR_ID );
		} else if (objectStoreItem instanceof ICustomObject ) {
			showEditor( objectStoreItem, window, CustomObjectEditor.EDITOR_ID );
		}
	}

	protected void showEditor(IObjectStoreItem objectStoreItem, IWorkbenchWindow window, String editorId ) {
		
		try {
			IWorkbenchPage activePage = window.getActivePage();
			IEditorReference objectStoreItemEditor = getObjectStoreItemEditor(activePage, objectStoreItem );
			if ( objectStoreItemEditor == null ) {
				openEditor(objectStoreItem, window, editorId);
			} else {
				activePage.activate( objectStoreItemEditor.getEditor(true) );
			}
		} catch (PartInitException e) {
			PluginLog.error(e);
		}
	}

	private void openEditor(IObjectStoreItem objectStoreItem, IWorkbenchWindow window, String editorId) {
		OpenObjectStoreItemEditorJob job = new OpenObjectStoreItemEditorJob(objectStoreItem, editorId, window);
		job.setUser(true);
		job.schedule();
	}
	
	private IEditorReference getObjectStoreItemEditor(IWorkbenchPage activePage, IObjectStoreItem objectStoreItem) throws PartInitException {
		
		IEditorReference[] editors = activePage.getEditorReferences();
		
		for ( int i = 0; i < editors.length; i++ ) {
			if ( isObjectStoreItemEditor( editors[i], objectStoreItem ) ) {
				return editors[i];
			}
		}
		return null;
	}
	
	private boolean isObjectStoreItemEditor(IEditorReference editor, IObjectStoreItem objectStoreItem) throws PartInitException {
		
		if ( ! ( editor.getEditorInput() instanceof ObjectStoreItemEditorInput ) ) {
			return false;
		}
		
		ObjectStoreItemEditorInput editorInput = (ObjectStoreItemEditorInput) editor.getEditorInput();
		IObjectStoreItem editorItem = (IObjectStoreItem) editorInput.getAdapter( IObjectStoreItem.class);
		if ( editorItem == null ) {
			return false;
		}
		return objectStoreItem.getId().equalsIgnoreCase( editorItem.getId() );
	}
}
