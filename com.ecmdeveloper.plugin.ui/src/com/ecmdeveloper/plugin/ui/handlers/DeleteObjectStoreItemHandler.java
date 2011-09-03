/**
 * Copyright 2009,2010, Ricardo Belfor
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.ui.wizard.DeleteWizard;
import com.ecmdeveloper.plugin.util.Messages;
import com.ecmdeveloper.plugin.views.ObjectStoresViewContentProvider;

/**
 * 
 * @author Ricardo.Belfor
 *
 */
public class DeleteObjectStoreItemHandler extends AbstractHandler implements IHandler {

	private static final String HANDLER_NAME = Messages.DeleteObjectStoreItemHandler_HandlerName;
	private static final String DELETE_MESSAGE = Messages.DeleteObjectStoreItemHandler_DeleteMessage;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if (window == null)	return null;

		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (!(selection instanceof IStructuredSelection))
			return null;

		ArrayList<IObjectStoreItem> itemsDeleted = getItemsDeleted(selection);
		DeleteWizard wizard = new DeleteWizard( itemsDeleted );
		WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
		dialog.create();
		dialog.open();

//		ILabelProvider labelProvider = new ObjectStoreItemLabelProvider();
		
//		ITreeContentProvider contentProvider = new DeletedItemsContentProvider(itemsDeleted);
//		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(window.getShell(), labelProvider, contentProvider );
		
			
//			if ( ! objectStoreItem.hasChildren() ) {
//
//				String name = objectStoreItem.getName();
//				boolean answerTrue = MessageDialog.openQuestion(window
//						.getShell(), HANDLER_NAME, MessageFormat.format(
//						DELETE_MESSAGE, name) );
//				if (answerTrue) {
//					itemsDeleted.add(objectStoreItem);
//				}
//			} else {
//				// TODO: show a new confirmation dialog
//				MessageDialog.openInformation( window.getShell(), HANDLER_NAME, "Deleting objects with children is not yet supported" ); //$NON-NLS-1$
//			}
//		}

//		Job deleteJob = new DeleteJob(itemsDeleted, window.getShell() );
//		deleteJob.setUser(true);
//		deleteJob.schedule();
		
		return null;
	}

	private ArrayList<IObjectStoreItem> getItemsDeleted(ISelection selection) {
		ArrayList<IObjectStoreItem> itemsDeleted = new ArrayList<IObjectStoreItem>();
		
		Iterator<?> iterator = ((IStructuredSelection) selection).iterator();
		while ( iterator.hasNext() ) {

			Object elem = iterator.next();
			IObjectStoreItem objectStoreItem = (IObjectStoreItem)elem;

			if ( !( elem instanceof IObjectStoreItem ) ) {
				continue;
			}
			itemsDeleted.add(objectStoreItem);
		}
		return itemsDeleted;
	}
	
	class DeletedItemsContentProvider extends ObjectStoresViewContentProvider {
		
		Collection<IObjectStoreItem> deletedItems;
		
		public DeletedItemsContentProvider(Collection<IObjectStoreItem> deletedItems) {
			this.deletedItems = deletedItems;
		}

		public Object[] getElements(Object parent) {
			if ( ! ( parent instanceof IObjectStoreItem ) ) {
				return deletedItems.toArray();
			} else {
				return super.getElements(parent);
			}
		}		
	}
}
