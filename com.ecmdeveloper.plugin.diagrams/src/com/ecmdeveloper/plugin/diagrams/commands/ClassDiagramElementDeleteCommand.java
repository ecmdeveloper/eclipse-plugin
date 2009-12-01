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

package com.ecmdeveloper.plugin.diagrams.commands;

import java.text.MessageFormat;

import org.eclipse.gef.commands.Command;

import com.ecmdeveloper.plugin.diagrams.model.ClassDiagram;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramElement;

/**
 * @author Ricardo Belfor
 *
 */
public class ClassDiagramElementDeleteCommand extends Command {

	private final ClassDiagramElement object;
	private final ClassDiagram classDiagram;
	
	public ClassDiagramElementDeleteCommand(ClassDiagramElement classDiagramElement, ClassDiagram classDiagram ) {
		this.object = classDiagramElement;
		this.classDiagram = classDiagram;
		setLabel( MessageFormat.format( "Delete {0}", classDiagramElement.getName() ) );
	}

	@Override
	public void execute() {
		redo();
	}

	@Override
	public void redo() {
		classDiagram.deleteClassDiagramElement( object );
	}

	@Override
	public void undo() {
		classDiagram.addClassDiagramElement( object );
	}
	
	
}
