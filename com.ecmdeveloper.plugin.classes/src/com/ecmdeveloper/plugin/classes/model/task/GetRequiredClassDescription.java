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

package com.ecmdeveloper.plugin.classes.model.task;

import java.util.Iterator;

import com.filenet.api.meta.ClassDescription;
import com.filenet.api.meta.PropertyDescription;
import com.filenet.api.meta.PropertyDescriptionObject;
import com.filenet.api.util.Id;

/**
 * @author Ricardo.Belfor
 *
 */
public class GetRequiredClassDescription extends BaseTask {

	private PropertyDescriptionObject propertyDescription;
	private PropertyDescriptionObject reflectivePropertyDescription;
	private ClassDescription requiredClass;

	public GetRequiredClassDescription(PropertyDescriptionObject propertyDescription) {
		this.propertyDescription = propertyDescription;
	}

	@Override
	public Object call() throws Exception {
		
		requiredClass = propertyDescription.get_RequiredClass();
		
		Id reflectivePropertyId = propertyDescription.get_ReflectivePropertyId();
		if ( reflectivePropertyId != null ) {
			Iterator<?> iterator = requiredClass.get_PropertyDescriptions().iterator();
			while (iterator.hasNext()) {
				PropertyDescription propertyDescription = (PropertyDescription) iterator.next();
				if ( propertyDescription.get_Id().equals( reflectivePropertyId ) ) {
					reflectivePropertyDescription = (PropertyDescriptionObject) propertyDescription;
					break;
				}
			}
		}

		return null;
	}

	public PropertyDescriptionObject getReflectivePropertyDescription() {
		return reflectivePropertyDescription;
	}

	public ClassDescription getRequiredClass() {
		return requiredClass;
	}
}
	
