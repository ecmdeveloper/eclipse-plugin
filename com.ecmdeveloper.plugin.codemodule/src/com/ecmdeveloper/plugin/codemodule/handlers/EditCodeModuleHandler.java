/**
 * Copyright 2009, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in is
 * free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ECM Developer plug-in. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 */

package com.ecmdeveloper.plugin.codemodule.handlers;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.IDE;

import com.ecmdeveloper.plugin.codemodule.editors.CodeModuleEditor;
import com.ecmdeveloper.plugin.codemodule.editors.CodeModuleEditorInput;
import com.ecmdeveloper.plugin.codemodule.editors.CodeModuleEditorUtils;
import com.ecmdeveloper.plugin.codemodule.model.CodeModuleFile;
import com.ecmdeveloper.plugin.codemodule.util.Messages;
import com.ecmdeveloper.plugin.codemodule.util.PluginLog;
import com.ecmdeveloper.plugin.codemodule.util.PluginMessage;

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
				if ( ! CodeModuleEditorUtils.isEditorActive( window.getActivePage(), (CodeModuleFile) elem ) ) {
					IEditorInput input = new CodeModuleEditorInput( (CodeModuleFile) elem );
					String editorId = CodeModuleEditor.CODE_MODULE_EDITOR_ID;
					IDE.openEditor( window.getActivePage(), input, editorId);
				}
				
			} catch (PartInitException e) {
				PluginMessage.openError(window.getShell(), HANDLER_NAME, e.getLocalizedMessage(), e );
			}
		}
		
		return null;
	}
}
