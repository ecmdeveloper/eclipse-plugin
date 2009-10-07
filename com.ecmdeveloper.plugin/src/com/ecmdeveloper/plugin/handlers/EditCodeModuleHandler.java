package com.ecmdeveloper.plugin.handlers;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.IDE;

import com.ecmdeveloper.plugin.editors.CodeModuleEditor;
import com.ecmdeveloper.plugin.editors.CodeModuleEditorInput;
import com.ecmdeveloper.plugin.model.CodeModuleFile;
import com.ecmdeveloper.plugin.util.Messages;
import com.ecmdeveloper.plugin.util.PluginLog;
import com.ecmdeveloper.plugin.util.PluginMessage;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class EditCodeModuleHandler extends AbstractHandler implements IHandler {

	private static final String HANDLER_NAME = Messages.EditCodeModuleHandler_HandlerName;
	private static final String OPEN_CODE_MODULE_EDITOR_ERROR = Messages.EditCodeModuleHandler_OpenCodeModuleEditorError;

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
	
			try {
				if ( ! isEditorActive( window.getActivePage(), (CodeModuleFile) elem ) ) {
					IEditorInput input = new CodeModuleEditorInput( (CodeModuleFile) elem );
					String editorId = CodeModuleEditor.CODE_MODULE_EDITOR_ID;
					IDE.openEditor( window.getActivePage(), input, editorId);
				}
				
			} catch (PartInitException e) {
				PluginLog.error(OPEN_CODE_MODULE_EDITOR_ERROR , e);
				PluginMessage.openError(window.getShell(), HANDLER_NAME, e.getLocalizedMessage(), e );
			}
		}
		
		return null;
	}
	
	public static boolean isEditorActive( IWorkbenchPage activePage, CodeModuleFile codeModuleFile ) throws PartInitException {

		IEditorReference[] editors = activePage.getEditorReferences();
		
		for ( int i = 0; i < editors.length; i++ ) {
			
			if ( ! ( editors[i].getEditorInput() instanceof CodeModuleEditorInput ) ) {
				continue;
			}

			CodeModuleFile editorFile = (CodeModuleFile) editors[i].getEditorInput().getAdapter( codeModuleFile.getClass() );
			if ( editorFile.getId().equalsIgnoreCase( codeModuleFile.getId() ) ) {
				activePage.activate( editors[i].getEditor(true) );
				return true;
			}
		}

		return false;
	}

}
