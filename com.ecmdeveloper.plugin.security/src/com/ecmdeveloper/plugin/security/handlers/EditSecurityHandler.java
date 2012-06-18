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

package com.ecmdeveloper.plugin.security.handlers;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.security.editor.SecurityEditor;
import com.ecmdeveloper.plugin.security.jobs.OpenSecurityEditorJob;

/**
 * @author Ricardo.Belfor
 *
 */
public class EditSecurityHandler extends AbstractHandler {

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
			IObjectStoreItem objectStoreItem = (IObjectStoreItem) iterator.next();
			openEditor( objectStoreItem, window, SecurityEditor.EDITOR_ID );
		}
	}
	
	private void openEditor(IObjectStoreItem objectStoreItem, IWorkbenchWindow window, String editorId) {
		OpenSecurityEditorJob job = new OpenSecurityEditorJob(objectStoreItem, editorId, window);
		job.setUser(true);
		job.schedule();
	}
	
}
