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

package com.ecmdeveloper.plugin.diagrams.properties;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramClass;

/**
 * @author Ricardo.Belfor
 *
 */
public class ClassDiagramClassProperties extends ClassDiagramElementProperties {

	private static final String VISIBLE_ATTRIBUTES_PROPERTY = "ClassDiagramClass.visibleAttributes";

	protected static final PropertyDescriptor VISIBLE_ATTRIBUTES_PROPERTY_DESCRIPTOR = new PropertyDescriptor(
			VISIBLE_ATTRIBUTES_PROPERTY, "Visible Attributes");
	
	private static IPropertyDescriptor[] classDescriptors = { 
		XPOS_PROPERTY_DESCRIPTOR,
		YPOS_PROPERTY_DESCRIPTOR,
		VISIBLE_ATTRIBUTES_PROPERTY_DESCRIPTOR };

	static {
		VISIBLE_ATTRIBUTES_PROPERTY_DESCRIPTOR.setCategory( PropertyCategory.APPERANCE );
	}
	
	private ClassDiagramClass classDiagramClass;
	
	public ClassDiagramClassProperties(ClassDiagramClass classDiagramClass) {
		super(classDiagramClass);
		this.classDiagramClass = classDiagramClass;
	}

	@Override
	public Object getEditableValue() {
		return this;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return classDescriptors;
	}

	@Override
	public Object getPropertyValue(Object propertyId) {

		if ( VISIBLE_ATTRIBUTES_PROPERTY.equals( propertyId ) ) {
			return new VisibleAttributesProperties(classDiagramClass);
		}
		
		return super.getPropertyValue(propertyId);
	}
	
	
}
