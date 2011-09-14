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

package com.ecmdeveloper.plugin.favorites.handlers;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.common.CommandException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;

import com.ecmdeveloper.plugin.favorites.model.FavoriteObjectStore;
import com.ecmdeveloper.plugin.core.model.IDocument;
import com.ecmdeveloper.plugin.favorites.util.PluginLog;

/**
 * @author ricardo.belfor
 *
 */
public class FavoritesViewDoubleClickHandler extends AbstractHandler  {

	public static final String VIEW_DOCUMENT_COMMAND_ID = "com.ecmdeveloper.plugin.viewDocument";
	public static final String CONNECT_OBJECT_STORE_COMMAND_ID = "com.ecmdeveloper.plugin.connectFavoriteObjectStore";
	
	private IWorkbenchWindow window;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if (window == null)	return null;

		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if ( selection == null || selection.isEmpty() ) {
			return null;
		}
		
		if ( !(selection instanceof IStructuredSelection) ) {
			return null;
		}

		Iterator<?> iterator = ((IStructuredSelection) selection).iterator();
		
		if (iterator.hasNext()) {
			try 
			{
				executeCommand( iterator.next() );
			} catch (CommandException e) {
				PluginLog.error( e );
			} 
		}

		return null;
	}

	private void executeCommand(Object selectedObject) throws CommandException  {
		IHandlerService handlerService = (IHandlerService) window.getService(IHandlerService.class);
		if ( selectedObject instanceof IDocument) {
			handlerService.executeCommand(VIEW_DOCUMENT_COMMAND_ID, null );
		} else if ( selectedObject instanceof FavoriteObjectStore) {
			if ( ! ((FavoriteObjectStore)selectedObject).getObjectStore().isConnected() ) {
				handlerService.executeCommand(CONNECT_OBJECT_STORE_COMMAND_ID, null );
			}
		}
	}
}
