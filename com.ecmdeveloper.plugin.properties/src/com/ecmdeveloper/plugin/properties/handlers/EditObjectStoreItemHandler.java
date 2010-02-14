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

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ecmdeveloper.plugin.model.CustomObject;
import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.Folder;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.properties.editors.CustomObjectEditor;
import com.ecmdeveloper.plugin.properties.editors.DocumentEditor;
import com.ecmdeveloper.plugin.properties.editors.FolderEditor;
import com.ecmdeveloper.plugin.properties.jobs.OpenObjectStoreItemEditorJob;

/**
 * @author Ricardo.Belfor
 *
 */
public class EditObjectStoreItemHandler extends AbstractHandler {

	private IWorkbenchWindow window;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if (window == null)	return null;

		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (!(selection instanceof IStructuredSelection))
			return null;

		openEditors(selection);
		return null;
	}

	private void openEditors(ISelection selection) {

		Iterator<?> iterator = ((IStructuredSelection) selection).iterator();
		while ( iterator.hasNext() ) {
			ObjectStoreItem objectStoreItem = (ObjectStoreItem) iterator.next();
			if (objectStoreItem instanceof Folder ) {
				openEditor( objectStoreItem, FolderEditor.EDITOR_ID );
			} else if (objectStoreItem instanceof Document ) {
				openEditor( objectStoreItem, DocumentEditor.EDITOR_ID );
			} else if (objectStoreItem instanceof CustomObject ) {
				openEditor( objectStoreItem, CustomObjectEditor.EDITOR_ID );
			}
		}
	}

	private void openEditor(ObjectStoreItem objectStoreItem, String editorId ) {
		OpenObjectStoreItemEditorJob job = new OpenObjectStoreItemEditorJob(objectStoreItem, editorId, window);
		job.setUser(true);
		job.schedule();
	}
}
