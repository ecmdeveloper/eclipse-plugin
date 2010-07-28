/**
 * Copyright 2009,2010, Ricardo Belfor
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

package com.ecmdeveloper.plugin.properties.model;

import java.util.Collection;
import java.util.List;

import com.ecmdeveloper.plugin.classes.model.Choice;
import com.ecmdeveloper.plugin.classes.model.PropertyDescription;
import com.ecmdeveloper.plugin.classes.model.constants.PropertyType;
import com.ecmdeveloper.plugin.properties.input.PropertyValueConversion;
import com.filenet.api.core.Factory;

/**
 * @author Ricardo.Belfor
 *
 */
public class Property {

	private PropertiesObject propertiesObject;
	private PropertyDescription propertyDescription;
	
	public Property(PropertiesObject objectStoreItem, PropertyDescription propertyDescription) {
		this.propertiesObject = objectStoreItem;
		this.propertyDescription = propertyDescription;
	}

	public PropertyDescription getPropertyDescription() {
		return propertyDescription;
	}
	
	public String getName() {
		return propertyDescription.getName();
	}
	
	public String getDisplayName() {
		return propertyDescription.getDisplayName();
	}
	
	public String getDescriptiveText() {
		return propertyDescription.getDescriptiveText();
	}

	public Object getValue() {
		return propertiesObject.getValue( getName() );
	}

	public void setValue(Object value) throws Exception {
		propertiesObject.setValue(getName(), value);
	}
	
	public String getValueAsString() {
		return PropertyValueConversion.getValueAsString( getValue() );
	}

	public boolean isRequired() {
		return propertyDescription.isRequired();
	}

	public boolean isMultivalue() {
		return propertyDescription.isMultivalue();
	}

	public boolean isSettableOnEdit() {
		return propertyDescription.isSettableOnEdit();
	}
	
	public Collection<Choice> getChoices() {
		return propertyDescription.getChoices();
	}

	@SuppressWarnings("unchecked")
	public List getList() {

		PropertyType propertyType = propertyDescription.getPropertyType();
		
		switch ( propertyType ) {
		case BINARY:
			return Factory.BinaryList.createList();
		case BOOLEAN:
			return Factory.BooleanList.createList();
		case DATE:
			return Factory.DateTimeList.createList();
		case DOUBLE:
			return Factory.Float64List.createList();
		case GUID:
			return Factory.IdList.createList();
		case LONG:
			return Factory.Integer32List.createList();
		case OBJECT:
			// TODO: fix this!!!
			return null;
		case STRING:
			return Factory.StringList.createList();
		}
		return null;
	}
}
