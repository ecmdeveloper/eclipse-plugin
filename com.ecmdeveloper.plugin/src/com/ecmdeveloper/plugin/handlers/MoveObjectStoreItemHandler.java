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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ecmdeveloper.plugin.model.CustomObject;
import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.model.tasks.MoveTask;
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
	private static final String MONITOR_MESSAGE = "Moving";
	private static final String PROGRESS_MESSAGE = "Moving \"{0}\"";
	private static final String FAILED_MESSAGE = "Moving \"{0}\" failed";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if (window == null)	return null;

		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (!(selection instanceof IStructuredSelection) || selection.isEmpty() )
			return null;

		Iterator<?> iterator = ((IStructuredSelection) selection).iterator();
		Set<ObjectStore> elementObjectStores = new HashSet<ObjectStore>();

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
		dialog.setInput( ObjectStoresManager.getManager() );
		
		dialog.addFilter( new MoveTargetFilter( elementObjectStores.iterator().next() ) );
		contentProvider.inputChanged( null, null, ObjectStoresManager.getManager() );
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
		final private ObjectStore objectStore;
		
		public MoveTargetFilter(ObjectStore objectStoreName ) {
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

	class MoveJob extends Job
	{
		private ArrayList<IObjectStoreItem> itemsMoved;
		private IObjectStoreItem destination;
		private Shell shell;
		
		public MoveJob(ArrayList<IObjectStoreItem> itemsMoved,
				IObjectStoreItem destination, Shell shell) {
			super(HANDLER_NAME);
			this.itemsMoved = itemsMoved;
			this.shell = shell;
			this.destination = destination;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			
			try {
				monitor.beginTask( MONITOR_MESSAGE, itemsMoved.size() );
				for ( IObjectStoreItem objectStoreItem : itemsMoved ) {
					monitor.subTask( MessageFormat.format(PROGRESS_MESSAGE, objectStoreItem.getName() ) );
					moveItem( objectStoreItem );
					monitor.worked(1);

					if ( monitor.isCanceled() ) {
						break;
					}
				}
				
				return Status.OK_STATUS;
			} finally {
				monitor.done();
			}
		}

		private void moveItem(final IObjectStoreItem objectStoreItem ) {

			try {
				MoveTask moveTask = new MoveTask( new IObjectStoreItem[] { objectStoreItem }, destination );
				ObjectStoresManager.getManager().executeTaskSync( moveTask);
			} catch(final Exception e ) {
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						PluginMessage.openError(shell, HANDLER_NAME, 
								MessageFormat.format(FAILED_MESSAGE, objectStoreItem.getName() ), e );
					}
				} );
			}
		}
	}
}
