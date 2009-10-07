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

import com.ecmdeveloper.plugin.model.CustomObject;
import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
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
		Set<String> elementObjectStores = new HashSet<String>();

		while ( iterator.hasNext() ) {
			IObjectStoreItem elem = (IObjectStoreItem) iterator.next();
			elementObjectStores.add( elem.getObjectStore().getName() );
		}

		ArrayList<IObjectStoreItem> elementsMoved = new ArrayList<IObjectStoreItem>();
		IObjectStoreItem destination = null;
		
		try {
			if ( elementObjectStores.size() != 1 ) {
				throw new UnsupportedOperationException(MOVING_ACROSS_OBJECT_STORES_ERROR );
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
				objectStoreItem.move(destination);
				elementsMoved.add( objectStoreItem );
			}
		} catch (Exception e ) {
			PluginMessage.openError(window.getShell(), HANDLER_NAME, e.getLocalizedMessage(), e );
		}
		
		if ( destination != null && ! elementsMoved.isEmpty() ) {
			ObjectStoresManager.getManager().moveObjectStoreItems(
					elementsMoved.toArray(new IObjectStoreItem[0]), destination );
		}
		return null;
	}
	
	class MoveTargetFilter extends ViewerFilter
	{
		final private String objectStoreName;
		
		public MoveTargetFilter(String objectStoreName ) {
			this.objectStoreName = objectStoreName;
		}
		
		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			if ( ( element instanceof Document || element instanceof CustomObject  ) ) {
				return false;
			} else if ( element instanceof ObjectStore ) {
				return objectStoreName.equals( ((ObjectStore) element).getName() );
			}
			return true;
		}
	};
}
