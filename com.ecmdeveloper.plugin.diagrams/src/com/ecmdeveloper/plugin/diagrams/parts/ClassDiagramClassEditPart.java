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

package com.ecmdeveloper.plugin.diagrams.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;

import com.ecmdeveloper.plugin.diagrams.figures.ResourceFigure;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramClass;

/**
 * @author Ricardo Belfor
 *
 */
public class ClassDiagramClassEditPart extends AbstractClassesGraphicalEditPart
		implements PropertyChangeListener {

	private final ResourceFigure resourceFigure = new ResourceFigure();
	
	public ClassDiagramClassEditPart(ClassDiagramClass classDiagramClass) {
		setModel(classDiagramClass);
	    resourceFigure.setText( getClassDiagramClass().getName());
	}

	public ClassDiagramClass getClassDiagramClass() {
		return (ClassDiagramClass) getModel();
	}
	
	@Override
	public void activate() {
		if (!isActive()) {
			super.activate();
			getClassDiagramClass().addPropertyChangeListener(this);
		}
	}

	@Override
	public void deactivate() {
		if (isActive()) {
			super.deactivate();
			getClassDiagramClass().removePropertyChangeListener(this);
		}
	}

	@Override
	protected IFigure createFigure() {
		resourceFigure.setSize( getClassDiagramClass().getSize() );
		// resourceFigure.setToolTip(createToolTipLabel());
		return resourceFigure;
	}

	@Override
	protected void createEditPolicies() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void refreshVisuals() {
		Rectangle bounds = new Rectangle(getClassDiagramClass().getLocation(),
				getClassDiagramClass().getSize());
		((GraphicalEditPart) getParent()).setLayoutConstraint(this,
				getFigure(), bounds);
		
		System.out.println( "refreshVisuals called " + bounds.toString() );
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();
		if (ClassDiagramClass.SIZE_PROP.equals(propertyName)
				|| ClassDiagramClass.LOCATION_PROP.equals(propertyName)) {
			refreshVisuals();
		}		
	}	
}
