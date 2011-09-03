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

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;

import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramElement;
import com.ecmdeveloper.plugin.diagrams.policies.ClassDiagramComponentEditPolicy;

/**
 * @author Ricardo.Belfor
 *
 */
public abstract class ClassDiagramElementEditPart extends AbstractClassesGraphicalEditPart
		implements PropertyChangeListener, NodeEditPart {

	private ConnectionAnchor anchor;

	public ClassDiagramElement getClassDiagramElement() {
		return (ClassDiagramElement) getModel();
	}
	
	@Override
	public void activate() {
		if (!isActive()) {
			super.activate();
			getClassDiagramElement().addPropertyChangeListener(this);
		}
	}

	@Override
	public void deactivate() {
		if (isActive()) {
			super.deactivate();
			getClassDiagramElement().removePropertyChangeListener(this);
		}
	}
	
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new ClassDiagramComponentEditPolicy() );
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();
		if (ClassDiagramElement.SIZE_PROP.equals(propertyName)
				|| ClassDiagramElement.LOCATION_PROP.equals(propertyName) ) {
			refreshVisuals();
		} else if ( ClassDiagramElement.SOURCE_CONNECTIONS_PROP.equals( propertyName) ) {
			refreshSourceConnections();
			refreshVisuals();
		} else if ( ClassDiagramElement.TARGET_CONNECTIONS_PROP.equals( propertyName) ) {
			refreshTargetConnections();
			refreshVisuals();
		} else if ( ClassDiagramElement.CLASS_DIAGRAM_SETTINGS_CHANGED_PROP.equals(propertyName) ) {
			refreshVisuals();
		}
	}	

	protected ConnectionAnchor getConnectionAnchor() {
		if (anchor == null) {
			anchor = new ChopboxAnchor(getFigure());
		}
		return anchor;
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart arg0) {
		return getConnectionAnchor();
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(Request arg0) {
		return getConnectionAnchor();
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart arg0) {
		return getConnectionAnchor();
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(Request arg0) {
		return getConnectionAnchor();
	}
}
