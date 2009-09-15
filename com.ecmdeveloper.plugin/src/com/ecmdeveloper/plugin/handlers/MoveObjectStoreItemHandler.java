package com.ecmdeveloper.plugin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;

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

		ITreeContentProvider contentProvider = new ObjectStoresViewContentProvider();
		ILabelProvider labelProvider = new ObjectStoreItemLabelProvider();
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(window.getShell(), labelProvider, contentProvider );
		dialog.setInput( ObjectStoresManager.getManager() );
		contentProvider.inputChanged( null, null, ObjectStoresManager.getManager() );
		dialog.setTitle("Move");
		dialog.setMessage( "Choose destination for '" + "TODO" + "'" );
		dialog.open();
		return null;
	}

	class MyContent implements ITreeContentProvider {

		@Override
		public Object[] getChildren(Object parentElement) {
			return new String[] { "Kind 1", "Kind 2", "Kind 3" };
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			return true;
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub
			
		}
	}
}
