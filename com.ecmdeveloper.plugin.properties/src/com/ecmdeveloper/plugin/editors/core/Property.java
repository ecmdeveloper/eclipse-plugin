/**
 * Copyright 2009, Ricardo Belfor
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

package com.ecmdeveloper.plugin.editors.core;

import com.ecmdeveloper.plugin.classes.model.PropertyDescription;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.properties.input.PropertyValueConversion;

/**
 * @author Ricardo.Belfor
 *
 */
public class Property {

	private ObjectStoreItem objectStoreItem;
	private PropertyDescription propertyDescription;
	
	public Property(ObjectStoreItem objectStoreItem, PropertyDescription propertyDescription) {
		this.objectStoreItem = objectStoreItem;
		this.propertyDescription = propertyDescription;
	}

	public ObjectStoreItem getObjectStoreItem() {
		return objectStoreItem;
	}

	public PropertyDescription getPropertyDescription() {
		return propertyDescription;
	}
	
	public String getName() {
		return propertyDescription.getName();
	}
	
	public Object getValue() {
		return objectStoreItem.getValue( getName() );
	}
	
	public String getValueAsString() {
		return PropertyValueConversion.getValueAsString( getValue() );
	}
}
