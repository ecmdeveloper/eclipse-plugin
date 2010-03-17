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
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramNote;

/**
 * @author Ricardo.Belfor
 *
 */
public class ClassDiagramNoteProperties extends ClassDiagramElementWithResizeProperties {

	private ClassDiagramNote classDiagramNote;

	public ClassDiagramNoteProperties(ClassDiagramNote classDiagramNote) {
		super(classDiagramNote);
		this.classDiagramNote = classDiagramNote;
	}

	public static final String TEXT_PROP = "ClassDiagramNote.Text";
	
	private static final TextPropertyDescriptor TEXT_PROPERTY_DESCRIPTOR = new TextPropertyDescriptor(TEXT_PROP, "Text");
	
	static {
		TEXT_PROPERTY_DESCRIPTOR.setCategory( PropertyCategory.APPERANCE );
	}

	private static IPropertyDescriptor[] noteDescriptors = { WIDTH_PROPERTY_DESCRIPTOR,
		HEIGHT_PROPERTY_DESCRIPTOR, XPOS_PROPERTY_DESCRIPTOR, YPOS_PROPERTY_DESCRIPTOR, 
		TEXT_PROPERTY_DESCRIPTOR };

	@Override
	public Object getEditableValue() {
		return this;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return noteDescriptors;
	}

	@Override
	public Object getPropertyValue(Object propertyId) {
		if (TEXT_PROP.equals(propertyId)) {
			return classDiagramNote.getNoteText();
		}
		return super.getPropertyValue(propertyId);
	}

	@Override
	public void setPropertyValue(Object propertyId, Object value) {
		if (TEXT_PROP.equals(propertyId)) {
			classDiagramNote.setNoteText((String) value);
			return;
		}
		super.setPropertyValue(propertyId, value);
	}
}
