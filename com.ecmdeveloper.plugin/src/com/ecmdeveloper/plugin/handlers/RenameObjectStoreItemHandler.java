package com.ecmdeveloper.plugin.handlers;

import java.text.MessageFormat;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.Folder;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.util.Messages;
import com.ecmdeveloper.plugin.util.PluginLog;
import com.ecmdeveloper.plugin.util.PluginMessage;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class RenameObjectStoreItemHandler extends AbstractHandler implements IHandler {

	private static final String NEW_NAME_MESSAGE = Messages.NewNameMessage;
	private static final String HANDLER_NAME = Messages.HandlerName;
	private static final String INVALID_CHARS_MESSAGE = Messages.RenameObjectStoreItemHandler_InvalidCharsMessage;
	private static final String INVALID_LENGTH_MESSAGE = Messages.RenameObjectStoreItemHandler_InvalidLengthMessage;

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
			
			// Create the name validator
			
			IInputValidator inputValidator = null;
			if ( objectStoreItem instanceof Folder ) {
				inputValidator = new FolderNameValidator();	
			} else if ( objectStoreItem instanceof Document ) {
				inputValidator = new DocumentNameValidator();
			}
			
			InputDialog inputDialog = new InputDialog( window.getShell(), HANDLER_NAME, NEW_NAME_MESSAGE, oldName, inputValidator );
			int open = inputDialog.open();
			
			if ( open == InputDialog.OK ) {
				try {
					objectStoreItem.setName( inputDialog.getValue() );
					ObjectStoresManager.getManager().updateObjectStoreItems(
							new IObjectStoreItem[] { objectStoreItem }, false); 
				} catch (Exception e) {
					PluginLog.error(e);
					PluginMessage.openError(window.getShell(), HANDLER_NAME, e.getLocalizedMessage(), e );
				} 
			}
		}

		return null;
	}

	class FolderNameValidator implements IInputValidator
	{
		private static final String NAME_INVALID_CHARS = "\\/*:?\"<>|"; //$NON-NLS-1$
		private static final int MAX_NAME_LENGTH = 64;
		
		@Override
		public String isValid(String newText) {

			if ( newText.length() > MAX_NAME_LENGTH ) {
				return MessageFormat.format(INVALID_LENGTH_MESSAGE, MAX_NAME_LENGTH );
			}
			for ( byte invalidChar : NAME_INVALID_CHARS.getBytes() ) {
				if ( newText.indexOf(invalidChar) > 0 ) {
					return MessageFormat.format( INVALID_CHARS_MESSAGE,  NAME_INVALID_CHARS );
				}
			}
			return null;
		}
	}

	class DocumentNameValidator implements IInputValidator
	{
		private static final int MAX_NAME_LENGTH = 64;
		
		@Override
		public String isValid(String newText) {

			if ( newText.length() > MAX_NAME_LENGTH ) {
				return MessageFormat.format(INVALID_LENGTH_MESSAGE, MAX_NAME_LENGTH );
			}
			return null;
		}
	}
}
