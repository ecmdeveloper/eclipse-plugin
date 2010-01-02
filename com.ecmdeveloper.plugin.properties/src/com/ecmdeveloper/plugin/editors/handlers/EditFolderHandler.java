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

package com.ecmdeveloper.plugin.editors.handlers;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.IDE;

import com.ecmdeveloper.plugin.classes.model.ClassDescription;
import com.ecmdeveloper.plugin.classes.model.PropertyDescription;
import com.ecmdeveloper.plugin.classes.model.task.GetClassDescriptionTask;
import com.ecmdeveloper.plugin.editors.folder.FolderEditor;
import com.ecmdeveloper.plugin.editors.core.ObjectStoreItemEditorInput;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.model.tasks.FetchPropertiesTask;
import com.ecmdeveloper.plugin.properties.util.PluginMessage;

/**
 * @author Ricardo.Belfor
 *
 */
public class EditFolderHandler extends AbstractHandler {

	private IWorkbenchWindow window;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if (window == null)	return null;

		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (!(selection instanceof IStructuredSelection))
			return null;

		openFolderEditors(selection);
		return null;
	}

	private void openFolderEditors(ISelection selection) {

		Iterator<?> iterator = ((IStructuredSelection) selection).iterator();
		while ( iterator.hasNext() ) {
			openFolderEditor(iterator.next());
		}
	}

	private void openFolderEditor(Object elem) {
		
		try {
			// TODO check if there is already an active editor
			openNewFolderEditor((ObjectStoreItem) elem);
			
		} catch (Exception e) {
			PluginMessage.openError(window.getShell(), "Edit Folder", e.getLocalizedMessage(), e );
		}
	}

	private void openNewFolderEditor(ObjectStoreItem objectStoreItem) throws Exception {
		
		ClassDescription classDescription = getClassDescription(objectStoreItem);
		fetchProperties(objectStoreItem, classDescription);

		IEditorInput input = new ObjectStoreItemEditorInput( (ObjectStoreItem) objectStoreItem, classDescription );
		String editorId = FolderEditor.EDITOR_ID;
		IDE.openEditor( window.getActivePage(), input, editorId);
		System.out.println( " Editor opened");
	}

	private void fetchProperties(ObjectStoreItem objectStoreItem, ClassDescription classDescription)
			throws Exception {
		String[] propertyNames = getPropertyNames(classDescription);
		FetchPropertiesTask task = new FetchPropertiesTask(objectStoreItem, propertyNames );
		ObjectStoresManager.getManager().executeTaskSync(task);
	}

	private String[] getPropertyNames(ClassDescription classDescription) {

		Set<String> propertyNames = new HashSet<String>(); 
		for (PropertyDescription propertyDescription : classDescription.getPropertyDescriptions() ) {
			propertyNames.add( propertyDescription.getName() );
		}
		
		String[] propertyNames2 = propertyNames.toArray( new String[0] );
		return propertyNames2;
	}

	private ClassDescription getClassDescription(ObjectStoreItem objectStoreItem) throws Exception {
		
		GetClassDescriptionTask task = new GetClassDescriptionTask(objectStoreItem.getClassName(),
				objectStoreItem.getObjectStore());
		ClassDescription classDescription = (ClassDescription) ObjectStoresManager.getManager().executeTaskSync(task);
		return classDescription;
	}
}
