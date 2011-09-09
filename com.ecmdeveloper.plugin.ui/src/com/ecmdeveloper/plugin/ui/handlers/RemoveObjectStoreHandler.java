/**
 * Copyright 2009, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in is
 * free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ECM Developer plug-in. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 */
package com.ecmdeveloper.plugin.ui.handlers;

import java.text.MessageFormat;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ecmdeveloper.plugin.ui.Activator;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.ui.util.Messages;

/**
 * @author Ricardo Belfor
 *
 */
public class RemoveObjectStoreHandler extends AbstractHandler implements
		IHandler {

	private static final String REMOVE_OBJECT_STORE_MESSAGE = Messages.RemoveObjectStoreHandler_RemoveObjectStoreMessage;
	private static final String HANDLER_NAME = Messages.RemoveObjectStoreHandler_HandlerName;

	@SuppressWarnings("unchecked")
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if (window == null)	return null;

		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if ( selection == null) {
			return null;
		}
		
		if ( !(selection instanceof IStructuredSelection) ) {
			return null;
		}

		Iterator iter = ((IStructuredSelection) selection).iterator();			
		while (iter.hasNext()) {
			Object selectedObject = iter.next();
			if ( selectedObject instanceof IObjectStore ) {
				
				IObjectStore objectStore = (IObjectStore)selectedObject;
				String objecStoreName = objectStore.getConnection().getDisplayName() + ":" + objectStore.getDisplayName(); //$NON-NLS-1$
				boolean answerTrue = MessageDialog.openQuestion(window
						.getShell(), HANDLER_NAME, MessageFormat.format(
						REMOVE_OBJECT_STORE_MESSAGE, objecStoreName));
				if ( answerTrue )
				{
					Activator.getDefault().getObjectStoresManager().removeObjectStore( (IObjectStore)selectedObject );
				}
			}
		}

		return null;
	}
}
