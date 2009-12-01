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

package com.ecmdeveloper.plugin.classes.model;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;

import com.ecmdeveloper.plugin.classes.model.constants.PropertyType;


/**
 * @author Ricardo.Belfor
 *
 */
public class PropertyDescription implements IAdaptable {
	
	private com.filenet.api.meta.PropertyDescription propertyDescription;
	private PropertyType propertyType;
	private String name;
	
	public PropertyDescription(Object internalPropertyDescription) {
		this.propertyDescription = (com.filenet.api.meta.PropertyDescription) internalPropertyDescription;
		name = this.propertyDescription.get_Name();
		propertyType = PropertyType.fromTypeID(this.propertyDescription.get_DataType() );
	}

	public String getName() {
		return name;
	}
	
	public PropertyType getPropertyType() {
		return propertyType;
	}
	
	public String getType() {
		// TODO look for the real type in case of an object type
		return propertyType.toString();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {

		if ( adapter.isInstance(propertyDescription) ) {
			return propertyDescription;
		}
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}
}
