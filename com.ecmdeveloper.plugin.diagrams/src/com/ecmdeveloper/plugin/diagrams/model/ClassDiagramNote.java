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

package com.ecmdeveloper.plugin.diagrams.model;

import java.util.ArrayList;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * 
 * @author Ricardo.Belfor
 *
 */
public class ClassDiagramNote extends ClassDiagramElementWithResize {

	public static final String TEXT_PROP = "ClassDiagramNote.Text";
	
	private static final TextPropertyDescriptor TEXT_PROPERTY_DESCRIPTOR = new TextPropertyDescriptor(TEXT_PROP, "Text");
	
	static {
		TEXT_PROPERTY_DESCRIPTOR.setCategory("Note Properties");
	}

	private static IPropertyDescriptor[] noteDescriptors = { WIDTH_PROPERTY_DESCRIPTOR,
		HEIGHT_PROPERTY_DESCRIPTOR, XPOS_PROPERTY_DESCRIPTOR, YPOS_PROPERTY_DESCRIPTOR, 
		TEXT_PROPERTY_DESCRIPTOR };

	private String noteText;
	
	public String getNoteText() {
		return noteText;
	}

	public void setNoteText(String noteText) {
		this.noteText = noteText;
		firePropertyChange(TEXT_PROP, null, this.noteText);
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return noteDescriptors;
	}

	@Override
	public Object getPropertyValue(Object propertyId) {
		if (TEXT_PROP.equals(propertyId)) {
			return noteText;
		}
		return super.getPropertyValue(propertyId);
	}

	@Override
	public void setPropertyValue(Object propertyId, Object value) {
		if (TEXT_PROP.equals(propertyId)) {
			setNoteText((String) value);
			return;
		}
		super.setPropertyValue(propertyId, value);
	}
}
