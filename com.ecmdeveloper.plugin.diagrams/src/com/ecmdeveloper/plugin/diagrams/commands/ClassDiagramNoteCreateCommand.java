/**
 * Copyright 2010, Ricardo Belfor
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

package com.ecmdeveloper.plugin.diagrams.commands;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.ecmdeveloper.plugin.diagrams.model.ClassDiagram;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramNote;

/**
 * 
 * @author Ricardo.Belfor
 *
 */
public class ClassDiagramNoteCreateCommand extends Command {

	private static final String TEXT_PROMPT = "Text";
	private static final String COMMAND_NAME = "Create New Note";

	private ClassDiagram classDiagram;
    private ClassDiagramNote classDiagramNote;

	public ClassDiagramNoteCreateCommand(ClassDiagram classDiagram, ClassDiagramNote classDiagramNote) {
		this.classDiagram = classDiagram;
		this.classDiagramNote = classDiagramNote;
		setLabel(COMMAND_NAME);
	}

	public void execute() {
		
		Shell shell = Display.getCurrent().getActiveShell();
		InputDialog inputDialog = new InputDialog( shell, COMMAND_NAME, TEXT_PROMPT, null, null );
		int open = inputDialog.open();
		
		if ( open == InputDialog.OK ) {
			String noteText = inputDialog.getValue();
			classDiagramNote.setNoteText(noteText );
			redo();
		}
	}

	public void redo() {
		classDiagram.addClassDiagramElement( classDiagramNote );
	}

	public boolean canUndo() {
		return classDiagramNote != null;
	}

	public void undo() {
		classDiagram.deleteClassDiagramElement( classDiagramNote);
	}
}
