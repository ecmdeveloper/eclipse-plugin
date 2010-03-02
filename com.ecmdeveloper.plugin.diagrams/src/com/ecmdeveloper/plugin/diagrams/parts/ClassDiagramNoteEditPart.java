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

package com.ecmdeveloper.plugin.diagrams.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;

import com.ecmdeveloper.plugin.diagrams.figures.NoteFigure;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramNote;
import com.ecmdeveloper.plugin.diagrams.policies.ClassDiagramComponentEditPolicy;

/**
 * @author Ricardo.Belfor
 *
 */
public class ClassDiagramNoteEditPart extends AbstractClassesGraphicalEditPart implements
		PropertyChangeListener {

	private final NoteFigure noteFigure = new NoteFigure();

	public ClassDiagramNoteEditPart(ClassDiagramNote classDiagramNote) {
		setModel( classDiagramNote );
		noteFigure.setText( classDiagramNote.getNoteText() );
	}

	public ClassDiagramNote getClassDiagramNote() {
		return (ClassDiagramNote) getModel();
	}
	
	@Override
	public void activate() {
		if (!isActive()) {
			super.activate();
			getClassDiagramNote().addPropertyChangeListener(this);
		}
	}

	@Override
	public void deactivate() {
		if (isActive()) {
			super.deactivate();
			getClassDiagramNote().removePropertyChangeListener(this);
		}
	}
	
	@Override
	protected IFigure createFigure() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new ClassDiagramComponentEditPolicy() );
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
