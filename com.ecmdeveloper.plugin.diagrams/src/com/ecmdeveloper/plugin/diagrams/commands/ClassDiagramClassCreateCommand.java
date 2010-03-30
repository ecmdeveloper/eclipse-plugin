/**
 * Copyright 2010, Ricardo Belfor
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

package com.ecmdeveloper.plugin.diagrams.commands;

import java.text.MessageFormat;

import org.eclipse.gef.commands.Command;

import com.ecmdeveloper.plugin.diagrams.model.ClassDiagram;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramClass;

/**
 * @author Ricardo.Belfor
 *
 */
public class ClassDiagramClassCreateCommand extends Command {

	private static final String COMMAND_NAME_FORMAT = "Add Class {0}";

	private ClassDiagram classDiagram;
    private ClassDiagramClass classDiagramClass;

	public ClassDiagramClassCreateCommand(ClassDiagram classDiagram, ClassDiagramClass classDiagramClass) {
		this.classDiagram = classDiagram;
		this.classDiagramClass = classDiagramClass;
		setLabel(MessageFormat.format(COMMAND_NAME_FORMAT, classDiagramClass.getDisplayName()));
	}

	public void execute() {
		redo();
	}

	public void redo() {
		classDiagram.addClassDiagramElement( classDiagramClass );
	}

	public boolean canUndo() {
		return classDiagramClass != null;
	}

	public void undo() {
		classDiagram.deleteClassDiagramElement( classDiagramClass);
	}
	
}
