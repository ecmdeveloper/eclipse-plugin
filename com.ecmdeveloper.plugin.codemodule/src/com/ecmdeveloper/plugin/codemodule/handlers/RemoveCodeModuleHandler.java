package com.ecmdeveloper.plugin.codemodule.handlers;

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

import com.ecmdeveloper.plugin.codemodule.model.CodeModuleFile;
import com.ecmdeveloper.plugin.codemodule.model.CodeModulesManager;
import com.ecmdeveloper.plugin.codemodule.util.Messages;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class RemoveCodeModuleHandler extends AbstractHandler implements
		IHandler {

	private static final String REMOVE_MESSAGE = Messages.RemoveCodeModuleHandler_RemoveMessage;
	private static final String HANDLER_NAME = Messages.RemoveCodeModuleHandler_HandlerName;

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
			if ( selectedObject instanceof CodeModuleFile ) {
				
				boolean answerTrue = MessageDialog.openQuestion( window.getShell(), HANDLER_NAME, 
						MessageFormat.format( REMOVE_MESSAGE, ((CodeModuleFile)selectedObject).getName() ) );
				if ( answerTrue )
				{
					CodeModulesManager.getManager().removeCodeModuleFile( (CodeModuleFile)selectedObject );
				}
			}
		}

		return null;
	}
}
