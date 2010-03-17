/**
 * Copyright 2009,2010, Ricardo Belfor
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
package com.ecmdeveloper.plugin.diagrams.model;

import org.eclipse.draw2d.geometry.Point;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public abstract class ClassDiagramElement extends ClassDiagramBase {

	public static final String LOCATION_PROP = "ClassDiagramElement.Location";
	public static final String SIZE_PROP = "ClassDiagramElement.Size";
	public static final String CLASS_DIAGRAM_SETTINGS_CHANGED_PROP = "ClassDiagramElement.ClassDiagramSettingsChanged";

	public static final String SOURCE_CONNECTIONS_PROP = "ClassDiagramElement.SourceConn";
	public static final String TARGET_CONNECTIONS_PROP = "ClassDiagramElement.TargetConn";

	private Point location = new Point(0, 0);
	protected ClassDiagram parent;
	
	public void setParent( ClassDiagram parent ) {
		this.parent = parent;
	}
	
	public ClassDiagram getParent() {
		return parent;
	}
	
	public Point getLocation() {
		return location.getCopy();
	}

	public void setLocation(Point location) {
		if (location == null) {
			throw new IllegalArgumentException();
		}
		this.location.setLocation(location);
		firePropertyChange(LOCATION_PROP, null, this.location);
	}

	public void notifyClassDiagramSettingsChanged(String propertyId) {
		firePropertyChange(CLASS_DIAGRAM_SETTINGS_CHANGED_PROP, null, propertyId);
	}
	
	public String getName() {
		return null;
	}
}
