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
package com.ecmdeveloper.plugin.codemodule.editors;

import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import com.ecmdeveloper.plugin.codemodule.model.CodeModuleFile;

/**
 * This utility class contains several static methods useful for Code Module
 * editors.
 * 
 * @author Ricardo Belfor
 * 
 */
public class CodeModuleEditorUtils {

	public static IEditorReference getCodeModuleEditor(IWorkbenchPage activePage, CodeModuleFile codeModuleFile ) throws PartInitException {

		IEditorReference[] editors = activePage.getEditorReferences();
		
		for ( int i = 0; i < editors.length; i++ ) {
			
			if ( ! ( editors[i].getEditorInput() instanceof CodeModuleEditorInput ) ) {
				continue;
			}

			CodeModuleFile editorFile = (CodeModuleFile) editors[i].getEditorInput().getAdapter( codeModuleFile.getClass() );
			if ( editorFile.getId().equalsIgnoreCase( codeModuleFile.getId() ) ) {
				return editors[i];
			}
		}

		return null;
	}
	
	public static boolean isEditorActive( IWorkbenchPage activePage, CodeModuleFile codeModuleFile ) throws PartInitException {

		IEditorReference codeModuleEditor = getCodeModuleEditor(activePage, codeModuleFile );
		if ( codeModuleEditor != null ) {
			activePage.activate( codeModuleEditor.getEditor(true) );
			return true;
		}

		return false;
	}
}
