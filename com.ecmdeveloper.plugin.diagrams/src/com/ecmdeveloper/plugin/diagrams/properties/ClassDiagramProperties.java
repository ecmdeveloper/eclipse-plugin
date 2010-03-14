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
import org.eclipse.ui.views.properties.IPropertySource;

import com.ecmdeveloper.plugin.diagrams.model.ClassDiagram;

/**
 * @author Ricardo.Belfor
 *
 */
public class ClassDiagramProperties implements IPropertySource {

	private static IPropertyDescriptor[] descriptors;

	static {
		descriptors = new IPropertyDescriptor[] { 
//			new ColorPropertyDescriptor( DEFAULT_FILL_COLOR_PROP, "Default Fill Color"), 
//			new ColorPropertyDescriptor( DEFAULT_LINE_COLOR_PROP, "Default Line Color")
//			new ComboBoxPropertyDescriptor( ClassDiagram.SHOW_ICONS_PROP, "Show Icons", new String[] { "Yes", "No" } )
			new CheckBoxPropertyDescriptor(ClassDiagram.SHOW_ICONS_PROP, "Show Icons")
		};
	}

	private ClassDiagram classDiagram;

	public ClassDiagramProperties(ClassDiagram classDiagram) {
		this.classDiagram = classDiagram;
	}

	@Override
	public Object getEditableValue() {
		return this;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	@Override
	public Object getPropertyValue(Object propertyId) {
//		if ( ClassDiagram.DEFAULT_FILL_COLOR_PROP.equals(propertyId)) {
//			return classDiagram.getdefaultFillColor;
//		} else if ( DEFAULT_LINE_COLOR_PROP.equals(propertyId)) {
//			return defaultLineColor;
		if ( ClassDiagram.SHOW_ICONS_PROP.equals(propertyId) ) {
//			return new Integer( classDiagram.isShowIcons() ? TRUE_INDEX : FALSE_INDEX );
			return new Boolean( classDiagram.isShowIcons() );
		}
		
		return null;
	}

	@Override
	public boolean isPropertySet(Object id) {
		return false;
	}

	@Override
	public void resetPropertyValue(Object id) {
	}

	@Override
	public void setPropertyValue(Object propertyId, Object value) {
//		if (DEFAULT_FILL_COLOR_PROP.equals(propertyId)) {
//			defaultFillColor = (RGB) value;
//		} else if (DEFAULT_LINE_COLOR_PROP.equals(propertyId)) {
//			defaultLineColor = (RGB) value;
//		} 
		if ( ClassDiagram.SHOW_ICONS_PROP.equals(propertyId) ) {
			classDiagram.setShowIcons( (Boolean) value );
//			classDiagram.setShowIcons( value == null || ((Integer)value).intValue() == TRUE_INDEX );
		}
	}
}
