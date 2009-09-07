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
public class RemoveObjectStoreHandler extends AbstractHandler implements
		IHandler {

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
				
				boolean answerTrue = MessageDialog.openQuestion( window.getShell(), "Remove Object Stores from view", "Do you want to remove the Object Store '" + ((ObjectStore)selectedObject).getName() + " from the view?" );
				if ( answerTrue )
				{
					ObjectStoresManager.getManager().removeObjectStore( (ObjectStore)selectedObject );
				}
			}
		}

		return null;
	}
}
