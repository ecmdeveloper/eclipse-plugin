/**
 * 
 */
package com.ecmdeveloper.plugin.handlers;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.progress.IProgressService;

import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.util.Messages;
import com.ecmdeveloper.plugin.util.PluginMessage;

/**
 * @author Ricardo Belfor
 *
 */
public class ConnectObjectStoreHandler extends AbstractHandler implements IHandler {

	private static final String HANDLER_NAME = Messages.ConnectObjectStoreHandler_HandlerName;

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
			final Object selectedObject = iter.next();
			if ( selectedObject instanceof ObjectStore ) {
				try {
					
					IProgressService progressService = window.getWorkbench().getProgressService();
					progressService.run( true, false, new IRunnableWithProgress() {

						@Override
						public void run(IProgressMonitor monitor) throws InvocationTargetException,	InterruptedException {
							ObjectStoresManager.getManager().connectObjectStore(( ObjectStore)selectedObject, monitor );
						}
					} );
					
				} catch(Exception e ) {
					PluginMessage.openError(window.getShell(), HANDLER_NAME, e.getLocalizedMessage(), e );
				}
			}
		}

		return null;
	}
}
