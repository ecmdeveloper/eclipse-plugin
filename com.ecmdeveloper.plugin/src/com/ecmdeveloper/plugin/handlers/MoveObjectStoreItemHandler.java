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
package com.ecmdeveloper.plugin.handlers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ecmdeveloper.plugin.Activator;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.jobs.MoveJob;
import com.ecmdeveloper.plugin.model.CustomObject;
import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.util.Messages;
import com.ecmdeveloper.plugin.util.PluginMessage;
import com.ecmdeveloper.plugin.views.ObjectStoreItemLabelProvider;
import com.ecmdeveloper.plugin.views.ObjectStoresViewContentProvider;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class MoveObjectStoreItemHandler extends AbstractHandler implements IHandler {

	private static final String MOVING_ACROSS_OBJECT_STORES_ERROR = Messages.MoveObjectStoreItemHandler_MovingAcrossObjectStoresError;
	private static final String CHOOSE_DESTINATION_MESSAGE = Messages.MoveObjectStoreItemHandler_ChooseDestinationMessage;
	private static final String HANDLER_NAME = Messages.MoveObjectStoreItemHandler_HandlerName;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if (window == null)	return null;

		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (!(selection instanceof IStructuredSelection) || selection.isEmpty() )
			return null;

		Iterator<?> iterator = ((IStructuredSelection) selection).iterator();
		Set<IObjectStore> elementObjectStores = new HashSet<IObjectStore>();

		while ( iterator.hasNext() ) {
			IObjectStoreItem elem = (IObjectStoreItem) iterator.next();
			elementObjectStores.add( elem.getObjectStore() );
		}

		ArrayList<IObjectStoreItem> elementsMoved = new ArrayList<IObjectStoreItem>();
		IObjectStoreItem destination = null;
		
		if ( elementObjectStores.size() != 1 ) {
			PluginMessage.openError(window.getShell(), HANDLER_NAME, MOVING_ACROSS_OBJECT_STORES_ERROR, null );
			return null;
		}
	
		ITreeContentProvider contentProvider = new ObjectStoresViewContentProvider();
		ILabelProvider labelProvider = new ObjectStoreItemLabelProvider();
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(window.getShell(), labelProvider, contentProvider );
		dialog.setInput( Activator.getDefault().getObjectStoresManager() );
		
		dialog.addFilter( new MoveTargetFilter( elementObjectStores.iterator().next() ) );
		contentProvider.inputChanged( null, null, Activator.getDefault().getObjectStoresManager() );
		dialog.setTitle(HANDLER_NAME);
		dialog.setMessage( CHOOSE_DESTINATION_MESSAGE );
		
		int answer = dialog.open();
	
		if ( answer != ElementTreeSelectionDialog.OK ) {
			return null;
		}
	
		destination = (IObjectStoreItem) dialog.getFirstResult();
		if ( destination == null ) {
			return null;
		}

		iterator = ((IStructuredSelection) selection).iterator();
		while ( iterator.hasNext() ) {
			IObjectStoreItem objectStoreItem = (IObjectStoreItem) iterator.next();
			elementsMoved.add( objectStoreItem );
		}
		
		if ( destination != null && ! elementsMoved.isEmpty() ) {
			MoveJob job = new MoveJob(elementsMoved, destination, window.getShell() );
			job.setUser(true);
			job.schedule();
		}
		
		return null;
	}
	
	class MoveTargetFilter extends ViewerFilter
	{
		final private IObjectStore objectStore;
		
		public MoveTargetFilter(IObjectStore objectStoreName ) {
			this.objectStore = objectStoreName;
		}
		
		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			if ( ( element instanceof Document || element instanceof CustomObject  ) ) {
				return false;
			} else if ( element instanceof ObjectStore ) {
				return objectStore.getId().equalsIgnoreCase( ((ObjectStore) element).getId() );
			}
			return true;
		}
	};
}
