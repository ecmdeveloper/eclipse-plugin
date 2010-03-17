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
package com.ecmdeveloper.plugin.diagrams.properties;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramElement;
import com.ecmdeveloper.plugin.diagrams.model.validators.PositiveIntegerValidator;

/**
 * 
 * @author Ricardo.Belfor
 *
 */
public abstract class ClassDiagramElementProperties implements IPropertySource {

	private static final String XPOS_PROP = "ClassDiagramElement.xPos";
	private static final String YPOS_PROP = "ClassDiagramElement.yPos";

	protected static final TextPropertyDescriptor XPOS_PROPERTY_DESCRIPTOR = new TextPropertyDescriptor(XPOS_PROP, "X");
	protected static final TextPropertyDescriptor YPOS_PROPERTY_DESCRIPTOR = new TextPropertyDescriptor(YPOS_PROP, "Y");
	
	static {

		XPOS_PROPERTY_DESCRIPTOR.setCategory( PropertyCategory.POSITION );
		XPOS_PROPERTY_DESCRIPTOR.setValidator( new PositiveIntegerValidator() );

		YPOS_PROPERTY_DESCRIPTOR.setCategory( PropertyCategory.POSITION );
		YPOS_PROPERTY_DESCRIPTOR.setValidator( new PositiveIntegerValidator() );
	}

	private ClassDiagramElement classDiagramElement;
	
	public ClassDiagramElementProperties(ClassDiagramElement classDiagramElement) {
		this.classDiagramElement = classDiagramElement;
	}

	@Override
	public Object getPropertyValue(Object propertyId) {
		if (XPOS_PROP.equals(propertyId)) {
			return Integer.toString(classDiagramElement.getLocation().x);
		}
		if (YPOS_PROP.equals(propertyId)) {
			return Integer.toString(classDiagramElement.getLocation().y);
		}
		return null;
	}

	@Override
	public boolean isPropertySet(Object id) {
		return false;
	}

	@Override
	public void resetPropertyValue(Object id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPropertyValue(Object propertyId, Object value) {
		if (XPOS_PROP.equals(propertyId)) {
			int x = Integer.parseInt((String) value);
			classDiagramElement.setLocation(new Point(x, classDiagramElement.getLocation().y));
		} else if (YPOS_PROP.equals(propertyId)) {
			int y = Integer.parseInt((String) value);
			classDiagramElement.setLocation(new Point(classDiagramElement.getLocation().x, y));
		}
	}
}
