package com.ecmdeveloper.plugin.handlers;

import java.text.MessageFormat;
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

import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.util.Messages;
import com.ecmdeveloper.plugin.util.PluginLog;
import com.ecmdeveloper.plugin.util.PluginMessage;

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

		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if (window == null)	return null;

		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (!(selection instanceof IStructuredSelection))
			return null;

		Iterator<?> iterator = ((IStructuredSelection) selection).iterator();
		while ( iterator.hasNext() ) {

			Object elem = iterator.next();

			if ( !( elem instanceof IObjectStoreItem ) ) {
				continue;
			}

			IObjectStoreItem objectStoreItem = (IObjectStoreItem)elem;
			
			try {
			
				if ( ! objectStoreItem.hasChildren() ) {

					String name = objectStoreItem.getName();
					boolean answerTrue = MessageDialog.openQuestion(window
							.getShell(), HANDLER_NAME, MessageFormat.format(
							DELETE_MESSAGE, name));
					if (answerTrue) {
						objectStoreItem.delete();
						ObjectStoresManager
								.getManager()
								.updateObjectStoreItems(
										new IObjectStoreItem[] { objectStoreItem },
										true);
					}
				} else {
					// TODO: show a new confirmation dialog
					throw new RuntimeException("Deleting objects with children is not yet supported" ); //$NON-NLS-1$
				}

			} catch (Exception e) {
				PluginLog.error(e);
				PluginMessage.openError(window.getShell(), HANDLER_NAME, e.getLocalizedMessage(), e );
			} 
		}
		return null;
	}
}
