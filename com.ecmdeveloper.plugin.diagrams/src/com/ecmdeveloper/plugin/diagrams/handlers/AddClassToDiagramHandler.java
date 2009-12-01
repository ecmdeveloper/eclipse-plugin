/**
 * Copyright 2009, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in
 * is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ECM Developer plug-in. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 */

package com.ecmdeveloper.plugin.diagrams.handlers;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ecmdeveloper.plugin.classes.model.ClassDescription;
import com.ecmdeveloper.plugin.diagrams.editors.ClassDiagramEditor;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagram;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramClass;

/**
 * @author Ricardo.Belfor
 *
 */
public class AddClassToDiagramHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if (window == null)	return null;

		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (!(selection instanceof IStructuredSelection) || selection.isEmpty() )
			return null;

		IEditorPart activeEditor = window.getActivePage().getActiveEditor();
		ClassDiagram classDiagram = null;
		
		if ( activeEditor != null && activeEditor instanceof ClassDiagramEditor ) {
			classDiagram = ((ClassDiagramEditor) activeEditor).getClassDiagram();
		}
		
		if ( classDiagram == null ) {
			return null;
		}
		
		Iterator<?> iterator = ((IStructuredSelection) selection).iterator();

		while ( iterator.hasNext() ) {
			ClassDescription elem = (ClassDescription) iterator.next();
			classDiagram.addClassDiagramClass((ClassDiagramClass) elem
					.getAdapter(ClassDiagramClass.class)); 
		}

		return null;
	}
}
