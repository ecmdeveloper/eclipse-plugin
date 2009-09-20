package com.ecmdeveloper.plugin.handlers;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ecmdeveloper.plugin.handlers.RenameObjectStoreItemHandler.NameValidator;
import com.ecmdeveloper.plugin.model.CodeModuleFile;
import com.ecmdeveloper.plugin.model.CodeModulesManager;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;

/**
 * 
 * @author Ricardo.Belfor
 *
 */
public class DeleteObjectStoreItemHandler extends AbstractHandler implements IHandler {

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

			if ( elem instanceof IObjectStoreItem )
			{
				String name = ((IObjectStoreItem)elem).getName();
				boolean answerTrue = MessageDialog.openQuestion( window.getShell(), "Delete", "Do you want to delete '" +  name + "'?" );
				if ( answerTrue )
				{
				}
			}
		}
		return null;
	}

}
