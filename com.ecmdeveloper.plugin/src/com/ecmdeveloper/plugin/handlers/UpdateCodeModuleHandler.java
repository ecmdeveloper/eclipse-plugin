/**
 * 
 */
package com.ecmdeveloper.plugin.handlers;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ecmdeveloper.plugin.model.Action;
import com.ecmdeveloper.plugin.model.CodeModuleFile;
import com.ecmdeveloper.plugin.model.CodeModulesManager;
import com.ecmdeveloper.plugin.views.ObjectStoreItemLabelProvider;

/**
 * @author Ricardo Belfor
 *
 */
public class UpdateCodeModuleHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if (window == null)	return null;

		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (!(selection instanceof IStructuredSelection) || selection.isEmpty() ) {
			return null;
		}

		CodeModulesManager codeModulesManager = CodeModulesManager.getManager();
		
		Iterator<?> iterator = ((IStructuredSelection) selection).iterator();
		while ( iterator.hasNext() ) {

			CodeModuleFile codeModuleFile = (CodeModuleFile) iterator.next();
			Collection<Action> actions = codeModulesManager.getCodeModuleActions(codeModuleFile);
			String message = "Select the actions related to the Code Module '" + codeModuleFile.getName() + "'\nto update:";
			LabelProvider labelProvider = new ObjectStoreItemLabelProvider();
			ListSelectionDialog dialog = new ListSelectionDialog(window.getShell(), actions, new ArrayContentProvider(), labelProvider, message );
			dialog.setInitialSelections( actions.toArray() );
			dialog.setTitle( "Action selection" );

			dialog.open();
//			codeModulesManager.getCodeModuleActions(codeModuleFile)
//			
//			MessageDialog.openInformation(window.getShell(), "TODO", "Updating " + ( (CodeModuleFile) elem ).getName() );
		}
		
		return null;
	}

}
