/**
 * Copyright 2012, Ricardo Belfor
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

package com.ecmdeveloper.plugin.security.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.security.editor.SecurityEditor;
import com.ecmdeveloper.plugin.security.editor.SecurityEditorInput;
import com.ecmdeveloper.plugin.security.jobs.OpenSecurityEditorJob;
import com.ecmdeveloper.plugin.security.util.PluginLog;

/**
 * @author ricardo.belfor
 *
 */
public abstract class AbstractEditSecurityHandler extends AbstractHandler {

	protected void showEditor(IObjectStoreItem objectStoreItem, IWorkbenchWindow window) {
		
		try {
			final IWorkbenchPage activePage = window.getActivePage();
			final IEditorReference objectStoreItemEditor = getObjectStoreItemEditor(activePage, objectStoreItem );
			if ( objectStoreItemEditor == null ) {
				openEditor(objectStoreItem, window);
			} else {
				activePage.getActivePart().getSite().getShell().getDisplay().asyncExec( new Runnable() {

					@Override
					public void run() {
						activePage.activate( objectStoreItemEditor.getEditor(true) );
					}
					
				});
			}
		} catch (PartInitException e) {
			PluginLog.error(e);
		}
	}

	private void openEditor(IObjectStoreItem objectStoreItem, IWorkbenchWindow window) {
		OpenSecurityEditorJob job = new OpenSecurityEditorJob(objectStoreItem, SecurityEditor.EDITOR_ID, window);
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
		
		if ( ! ( editor.getEditorInput() instanceof SecurityEditorInput ) ) {
			return false;
		}
		
		SecurityEditorInput editorInput = (SecurityEditorInput) editor.getEditorInput();
		IObjectStoreItem editorItem = (IObjectStoreItem) editorInput.getAdapter( IObjectStoreItem.class);
		return objectStoreItem.getId().equalsIgnoreCase( editorItem.getId() );
	}
}
