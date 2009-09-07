/**
 * 
 */
package com.ecmdeveloper.plugin.handlers;

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

import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;

/**
 * @author Ricardo Belfor
 *
 */
public class ConnectObjectStoreHandler extends AbstractHandler implements IHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
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
			if ( selectedObject instanceof ObjectStore ) {
				ObjectStoresManager.getManager().connectObjectStore(( ObjectStore)selectedObject );
//				MessageDialog.openInformation( window.getShell(), "Object Stores", ((ObjectStore)selectedObject).getName() );
			}
		}

		return null;
	}
}
