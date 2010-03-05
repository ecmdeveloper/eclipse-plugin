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

import com.ecmdeveloper.plugin.diagrams.model.validators.PositiveIntegerValidator;

/**
 * 
 * @author Ricardo.Belfor
 *
 */
public class ClassDiagramNote extends ClassDiagramElement {

	private static IPropertyDescriptor[] noteDescriptors;
	public static final String TEXT_PROP = "ClassDiagramNote.Text";
	
	static {
		noteDescriptors = new IPropertyDescriptor[] { 
				new TextPropertyDescriptor(TEXT_PROP, "Text")
		};
		
		for (int i = 0; i < noteDescriptors.length; i++) {
			((PropertyDescriptor) noteDescriptors[i]).setCategory( "Note Properties" );
		}
		
	}
	private String noteText = "My first note";
	
	public String getNoteText() {
		return noteText;
	}

	public void setNoteText(String noteText) {
		this.noteText = noteText;
		firePropertyChange(TEXT_PROP, null, this.noteText);
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		ArrayList<IPropertyDescriptor> propertyDescriptors = new ArrayList<IPropertyDescriptor>();
		for ( IPropertyDescriptor propertyDescriptor : descriptors ) {
			propertyDescriptors.add( propertyDescriptor);
		}
		for ( IPropertyDescriptor propertyDescriptor : noteDescriptors ) {
			propertyDescriptors.add( propertyDescriptor);
		}
		return propertyDescriptors.toArray( new IPropertyDescriptor[0] );
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
		super.getPropertyValue(propertyId);
	}
}
