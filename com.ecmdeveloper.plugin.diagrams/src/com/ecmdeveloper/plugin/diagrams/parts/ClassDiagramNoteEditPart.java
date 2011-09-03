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

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;

import com.ecmdeveloper.plugin.diagrams.figures.NoteFigure;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramNote;

/**
 * @author Ricardo.Belfor
 *
 */
public class ClassDiagramNoteEditPart extends ClassDiagramElementEditPart {

	private final NoteFigure noteFigure = new NoteFigure();

	public ClassDiagramNoteEditPart(ClassDiagramNote classDiagramNote) {
		setModel( classDiagramNote );
		noteFigure.setText( classDiagramNote.getNoteText() );
	}

	public ClassDiagramNote getClassDiagramNote() {
		return (ClassDiagramNote) getModel();
	}
		
	@Override
	protected IFigure createFigure() {
		return noteFigure;
	}

	@Override
	protected void refreshVisuals() {
		Rectangle bounds = new Rectangle(getClassDiagramNote().getLocation(),
				getClassDiagramNote().getSize());

		((GraphicalEditPart) getParent()).setLayoutConstraint(this,
				getFigure(), bounds);
		noteFigure.setText( getClassDiagramNote().getNoteText() );
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		String propertyName = evt.getPropertyName();
		if (ClassDiagramNote.TEXT_PROP.equals(propertyName) ) {
			refreshVisuals();
		}
	}
}
