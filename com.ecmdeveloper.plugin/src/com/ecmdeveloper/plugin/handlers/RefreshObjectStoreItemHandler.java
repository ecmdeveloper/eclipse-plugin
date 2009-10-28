/**
 * 
 */
package com.ecmdeveloper.plugin.handlers;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.model.tasks.RefreshTask;
import com.ecmdeveloper.plugin.util.Messages;

/**
 * @author Ricardo.Belfor
 *
 */
public class RefreshObjectStoreItemHandler extends AbstractHandler  {

	private static final String HANDLER_NAME = Messages.RefreshObjectStoreItemHandler_HandlerName;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if (window == null)	return null;

		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if ( selection == null || selection.isEmpty() ) {
			return null;
		}
		
		if ( !(selection instanceof IStructuredSelection) ) {
			return null;
		}

		Iterator<?> iter = ((IStructuredSelection) selection).iterator();
		
		ArrayList<IObjectStoreItem> elementsRefreshed = new ArrayList<IObjectStoreItem>();
		
		while (iter.hasNext()) {
			IObjectStoreItem selectedObject = (IObjectStoreItem) iter.next();
			elementsRefreshed.add( selectedObject );
		}

		RefreshTask refreshTask = new RefreshTask( elementsRefreshed.toArray(new IObjectStoreItem[0] ) );
		ObjectStoresManager.getManager().executeTaskASync(refreshTask);
		
		return null;
	}
}
