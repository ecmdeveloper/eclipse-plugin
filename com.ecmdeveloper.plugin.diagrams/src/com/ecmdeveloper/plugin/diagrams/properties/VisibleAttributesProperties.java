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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramAttribute;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramClass;

/**
 * @author Ricardo.Belfor
 *
 */
public class VisibleAttributesProperties implements IPropertySource {

	private ClassDiagramClass classDiagramClass;
	
	public VisibleAttributesProperties(ClassDiagramClass classDiagramClass) {
		this.classDiagramClass = classDiagramClass;
	}

	@Override
	public Object getEditableValue() {
		return this;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		
		List<ClassDiagramAttribute> attributes = classDiagramClass.getAttributes();
		ArrayList<IPropertyDescriptor> descriptors = new ArrayList<IPropertyDescriptor>();

		for ( ClassDiagramAttribute attribute : attributes ) {
			if ( attribute.isActive() ) {
				CheckBoxPropertyDescriptor propertyDescriptor = new CheckBoxPropertyDescriptor(
						attribute.getName(), attribute.getDisplayName());
				descriptors.add( propertyDescriptor );
			}
		}

		return descriptors.toArray( new IPropertyDescriptor[ descriptors.size() ] );
	}

	@Override
	public Object getPropertyValue(Object id) {
		ClassDiagramAttribute classDiagramAttribute = classDiagramClass
				.getClassDiagramAttribute((String) id);
		if ( classDiagramAttribute != null ) {
			return new Boolean( classDiagramAttribute.isVisible() );
		}
		return null;
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		classDiagramClass.setAttributeVisible( (String) id, (Boolean)value );
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
	public String toString() {

		StringBuffer visibleAttributes = new StringBuffer();
		List<ClassDiagramAttribute> attributes = classDiagramClass.getAttributes();
		String concat = "";
		for ( ClassDiagramAttribute attribute : attributes ) {
			if ( attribute.isVisible() && attribute.isActive() ) {
				visibleAttributes.append( concat );
				visibleAttributes.append( attribute.getDisplayName() );
				concat = ", ";
			}
		}
		return visibleAttributes.toString();
	}
}
