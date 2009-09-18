package com.ecmdeveloper.plugin.handlers;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ecmdeveloper.plugin.model.CustomObject;
import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.Folder;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.views.ObjectStoreItemLabelProvider;
import com.ecmdeveloper.plugin.views.ObjectStoresViewContentProvider;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class MoveObjectStoreItemHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if (window == null)	return null;

		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (!(selection instanceof IStructuredSelection))
			return null;

		Iterator<?> iterator = ((IStructuredSelection) selection).iterator();
		while ( iterator.hasNext() ) {

			IObjectStoreItem elem = (IObjectStoreItem) iterator.next();
			
			ITreeContentProvider contentProvider = new ObjectStoresViewContentProvider();
			ILabelProvider labelProvider = new ObjectStoreItemLabelProvider();
			ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(window.getShell(), labelProvider, contentProvider );
			dialog.setInput( ObjectStoresManager.getManager() );
			
			dialog.addFilter( new MoveTargetFilter( elem.getObjectStore().getName() ) );
			contentProvider.inputChanged( null, null, ObjectStoresManager.getManager() );
			dialog.setTitle("Move");
			dialog.setMessage( "Choose destination for '" + elem.getName() + "'" );
			dialog.open();
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
