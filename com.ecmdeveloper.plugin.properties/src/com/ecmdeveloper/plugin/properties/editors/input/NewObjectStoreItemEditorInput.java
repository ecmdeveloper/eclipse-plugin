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

package com.ecmdeveloper.plugin.properties.editors.input;

import java.util.HashMap;
import java.util.Map;

import com.ecmdeveloper.plugin.classes.model.ClassDescription;
import com.ecmdeveloper.plugin.model.Folder;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.properties.model.SavedPropertiesObject;
import com.ecmdeveloper.plugin.properties.model.UnsavedPropertiesObject;

/**
 * @author ricardo.belfor
 *
 */
public class NewObjectStoreItemEditorInput extends ObjectStoreItemEditorInput {

	private ObjectStoreItem parent;

	public NewObjectStoreItemEditorInput(ClassDescription classDescription, ObjectStoreItem parent) {
		super(null, classDescription);
		this.parent = parent;
	}

	public ObjectStoreItem getParent() {
		return parent;
	}
	
	public Map<String, Object> getPropertiesMap() {
		UnsavedPropertiesObject unsavedPropertiesObject = (UnsavedPropertiesObject) getPropertiesObject();
		Map<String,Object> propertiesMap = new HashMap<String, Object>();
		for ( String propertyName : unsavedPropertiesObject.getPropertyNames() ) {
			propertiesMap.put(propertyName, unsavedPropertiesObject.getValue(propertyName));
		}
		return propertiesMap;
	}
	
	public void setObjectStoreItem(ObjectStoreItem objectStoreItem ) {
		this.objectStoreItem = objectStoreItem;
		this.propertiesObject = new SavedPropertiesObject(objectStoreItem);
	}
}
