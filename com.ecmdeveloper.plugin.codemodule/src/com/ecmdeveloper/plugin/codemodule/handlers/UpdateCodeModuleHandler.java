/**
 * 
 */
package com.ecmdeveloper.plugin.codemodule.handlers;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ecmdeveloper.plugin.model.Action;
import com.ecmdeveloper.plugin.codemodule.model.CodeModuleFile;
import com.ecmdeveloper.plugin.codemodule.model.CodeModulesManager;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.codemodule.util.Messages;
import com.ecmdeveloper.plugin.codemodule.util.PluginLog;
import com.ecmdeveloper.plugin.codemodule.util.PluginMessage;
import com.ecmdeveloper.plugin.views.ObjectStoreItemLabelProvider;

/**
 * @author Ricardo Belfor
 *
 */
public class UpdateCodeModuleHandler extends AbstractHandler implements IHandler {

	private static final String SELECT_ACTIONS_MESSAGE = Messages.UpdateCodeModuleHandler_SelectActionsMessage;
	private static final String UPDATE_MESSAGE = Messages.UpdateCodeModuleHandler_UpdateMessage;
	private static final String HANDLER_NAME = Messages.UpdateCodeModuleHandler_HandlerName;
	private static final String ACTION_SELECTION_DIALOG_TITLE = Messages.UpdateCodeModuleHandler_ActionSelectionDialogTitle;
	private static final String ACTION_LABEL = Messages.UpdateCodeModuleHandler_ActionLabel;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if (window == null)	return null;

		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (!(selection instanceof IStructuredSelection) || selection.isEmpty() ) {
			return null;
		}

		try {
			CodeModulesManager codeModulesManager = CodeModulesManager.getManager();
			
			Iterator<?> iterator = ((IStructuredSelection) selection).iterator();
			while ( iterator.hasNext() ) {

				CodeModuleFile codeModuleFile = (CodeModuleFile) iterator.next();
				Collection<Action> actions = codeModulesManager.getCodeModuleActions(codeModuleFile);
				String message = MessageFormat.format( SELECT_ACTIONS_MESSAGE, codeModuleFile.getName() );
				LabelProvider labelProvider = new CodeModuleActionLabelProvider();
				ListSelectionDialog dialog = new ListSelectionDialog(window.getShell(), actions, new ArrayContentProvider(), labelProvider, message );
				dialog.setInitialSelections( actions.toArray() );
				dialog.setTitle( ACTION_SELECTION_DIALOG_TITLE );

				if ( dialog.open() == Dialog.OK ) {
					codeModulesManager.updateCodeModule(codeModuleFile, dialog.getResult() );
					MessageDialog.openInformation(window.getShell(),
							HANDLER_NAME, MessageFormat.format(
									UPDATE_MESSAGE,
									codeModuleFile.getName()).toString() );
				}
			}
		} catch (Exception e) {
			PluginLog.error(e);
			PluginMessage.openError(window.getShell(), HANDLER_NAME, e.getLocalizedMessage(), e );
		}
		return null;
	}

	class CodeModuleActionLabelProvider extends ObjectStoreItemLabelProvider {

		public String getText(Object object) {
			if ( object instanceof Action ) {
				String name = ((IObjectStoreItem) object).getName();
				String codeModuleVersion = ((Action) object).getCodeModuleVersion();
				return  MessageFormat.format( ACTION_LABEL, name, codeModuleVersion ); 
			}
			
			return super.getText(object);
		}
	}
}
