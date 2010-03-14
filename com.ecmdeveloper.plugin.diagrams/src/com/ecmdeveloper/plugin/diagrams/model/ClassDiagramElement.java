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

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.ecmdeveloper.plugin.diagrams.model.validators.PositiveIntegerValidator;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public abstract class ClassDiagramElement extends ClassDiagramBase implements IPropertySource {

	private static final String XPOS_PROP = "ClassDiagramElement.xPos";
	private static final String YPOS_PROP = "ClassDiagramElement.yPos";

	public static final String LOCATION_PROP = "ClassDiagramElement.Location";
	public static final String SIZE_PROP = "ClassDiagramElement.Size";
	public static final String CLASS_DIAGRAM_SETTINGS_CHANGED_PROP = "ClassDiagramElement.ClassDiagramSettingsChanged";

	public static final String SOURCE_CONNECTIONS_PROP = "ClassDiagramElement.SourceConn";
	public static final String TARGET_CONNECTIONS_PROP = "ClassDiagramElement.TargetConn";

	protected static final TextPropertyDescriptor XPOS_PROPERTY_DESCRIPTOR = new TextPropertyDescriptor(XPOS_PROP, "X");
	protected static final TextPropertyDescriptor YPOS_PROPERTY_DESCRIPTOR = new TextPropertyDescriptor(YPOS_PROP, "Y");
	
	static {

		XPOS_PROPERTY_DESCRIPTOR.setCategory("Appearance");
		XPOS_PROPERTY_DESCRIPTOR.setValidator( new PositiveIntegerValidator() );

		YPOS_PROPERTY_DESCRIPTOR.setCategory("Appearance");
		YPOS_PROPERTY_DESCRIPTOR.setValidator( new PositiveIntegerValidator() );
	}
	
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
	
	public Object getPropertyValue(Object propertyId) {
		if (XPOS_PROP.equals(propertyId)) {
			return Integer.toString(location.x);
		}
		if (YPOS_PROP.equals(propertyId)) {
			return Integer.toString(location.y);
		}
		return null;
	}

	public void setPropertyValue(Object propertyId, Object value) {
		if (XPOS_PROP.equals(propertyId)) {
			int x = Integer.parseInt((String) value);
			setLocation(new Point(x, location.y));
		} else if (YPOS_PROP.equals(propertyId)) {
			int y = Integer.parseInt((String) value);
			setLocation(new Point(location.x, y));
		}
	}

	@Override
	public boolean isPropertySet(Object id) {
		return false;
	}

	@Override
	public void resetPropertyValue(Object id) {
		// Do nothing
	}

	public String getName() {
		return null;
	}

	@Override
	public Object getEditableValue() {
		return this;
	}
}
