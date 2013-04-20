/**
 * Copyright 2012, Ricardo Belfor
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

package com.ecmdeveloper.plugin.model.tasks.classes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.ecmdeveloper.plugin.core.model.IPropertyDescription;
import com.ecmdeveloper.plugin.core.model.tasks.classes.IGetPropertyDescriptionsTask;
import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.PropertyDescription;
import com.ecmdeveloper.plugin.model.tasks.BaseTask;
import com.ecmdeveloper.plugin.util.CEIterable;
import com.filenet.api.collection.ClassDescriptionSet;
import com.filenet.api.collection.PropertyDescriptionList;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.meta.ClassDescription;

/**
 * @author ricardo.belfor
 *
 */
public class GetPropertyDescriptionsTask extends BaseTask implements IGetPropertyDescriptionsTask {

	private final ObjectStore objectStore;
//	private ArrayList<IPropertyDescription> propertyDescriptions;
	private Map<String, IPropertyDescription> propertyDescriptionsMap = new HashMap<String, IPropertyDescription>();
	
	public GetPropertyDescriptionsTask(ObjectStore objectStore) {
		this.objectStore = objectStore;
	}

	@Override
	public Collection<IPropertyDescription> getPropertyDescriptions() {
		return propertyDescriptionsMap.values();
	}

	@Override
	protected Object execute() throws Exception {
		
		com.filenet.api.core.ObjectStore objectStoreObject = 
			(com.filenet.api.core.ObjectStore) objectStore.getObjectStoreObject();

		objectStoreObject.refresh(new String[] { PropertyNames.CLASS_DESCRIPTIONS} );
		ClassDescriptionSet classDescriptions = objectStoreObject.get_ClassDescriptions();
		for ( ClassDescription classDescription : new CEIterable<ClassDescription>(classDescriptions) ) {
			if (classDescription.describedIsOfClass("Document")
					|| classDescription.describedIsOfClass("Folder")
					|| classDescription.describedIsOfClass("CustomObject")) {
				getPropertyDescriptions(classDescription);
			}
		}

		return null;
	}

	private void getPropertyDescriptions(ClassDescription classDescription) {
		PropertyDescriptionList propertyDescriptions = classDescription.get_PropertyDescriptions();
		for ( com.filenet.api.meta.PropertyDescription p : getIterable(propertyDescriptions) ) {
			if ( !propertyDescriptionsMap.containsKey(p.get_SymbolicName() ) ) {
				propertyDescriptionsMap.put(p.get_SymbolicName(), new PropertyDescription(p, objectStore) );
			}
		}
	}

	private CEIterable<com.filenet.api.meta.PropertyDescription> getIterable(PropertyDescriptionList propertyDescriptions2) {
		return new CEIterable<com.filenet.api.meta.PropertyDescription>(propertyDescriptions2);
	}

	@Override
	protected ContentEngineConnection getContentEngineConnection() {
		return objectStore.getConnection();
	}
}
