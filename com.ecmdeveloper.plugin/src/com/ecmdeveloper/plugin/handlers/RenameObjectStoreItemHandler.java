package com.ecmdeveloper.plugin.handlers;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.IDE;

import com.ecmdeveloper.plugin.editors.CodeModuleEditor;
import com.ecmdeveloper.plugin.editors.CodeModuleEditorInput;
import com.ecmdeveloper.plugin.model.CodeModuleFile;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.util.PluginLog;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class RenameObjectStoreItemHandler extends AbstractHandler implements IHandler {

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

			if (!(elem instanceof IObjectStoreItem)) {
				continue;
			}

			IObjectStoreItem objectStoreItem = (IObjectStoreItem)elem; 
			String oldName = objectStoreItem.getName();
			InputDialog inputDialog = new InputDialog( window.getShell(), "Rename", "New name:", oldName, new NameValidator() );
			int open = inputDialog.open();
			
			if ( open == InputDialog.OK ) {
				objectStoreItem.setName( inputDialog.getValue() );
				ObjectStoresManager.getManager().updateObjectStoreItems(
						new IObjectStoreItem[] { objectStoreItem }, false); 
			}
		}

		return null;
	}

	// TODO: define validator based on object type
	
	class NameValidator implements IInputValidator
	{
		@Override
		public String isValid(String newText) {
			if ( newText.indexOf('/') > 0 ) {
				return "The following characters are not allowed in a name: /";
			}
			return null;
		}
	}
}
