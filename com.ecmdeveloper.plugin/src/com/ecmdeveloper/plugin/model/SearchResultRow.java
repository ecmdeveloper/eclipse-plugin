/**
 * Copyright 2011, Ricardo Belfor
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

package com.ecmdeveloper.plugin.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import com.ecmdeveloper.plugin.model.tasks.FetchObjectTask;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.core.EngineObject;
import com.filenet.api.core.IndependentObject;
import com.filenet.api.core.IndependentlyPersistableObject;
import com.filenet.api.property.Properties;
import com.filenet.api.property.Property;
import com.filenet.api.query.RepositoryRow;

/**
 * @author ricardo.belfor
 *
 */
public class SearchResultRow {

	private static final String THIS_PROPERTY_NAME = "This";
	private HashMap<String, Object> values = new HashMap<String, Object>();
	private boolean hasObjectValue = false;
	private ObjectStore objectStore;
	private IObjectStoreItem objectStoreItem;
	
	public SearchResultRow(RepositoryRow row, ObjectStore objectStore) {
		
		this.objectStore = objectStore;
		
		Properties properties = row.getProperties();
		Iterator<?> iterator = properties.iterator();
		while ( iterator.hasNext() ) {
			Property property = (Property) iterator.next();
			String propertyName = property.getPropertyName();
			Object value = property.getObjectValue();
			if ( !THIS_PROPERTY_NAME.equals(propertyName) && value instanceof EngineObject ) {
				value = getEngineObjectValue(value);
			}
			values.put(propertyName, value );
			if (propertyName.equals(THIS_PROPERTY_NAME) ) {
				hasObjectValue = true;
			}
		}
	}

	private String getEngineObjectValue(Object value) {
		
		EngineObject repositoryObject = (EngineObject) value;
		StringBuffer engineObjectValue = new StringBuffer('[');
		engineObjectValue.append( repositoryObject.getClassName() );
		Properties properties2 = repositoryObject.getProperties();
		if ( properties2.isPropertyPresent( PropertyNames.ID ) ) {
			engineObjectValue.append( ',');
			engineObjectValue.append( properties2.getIdValue( PropertyNames.ID ).toString() );
		}
		engineObjectValue.append(']');
		return engineObjectValue.toString();
	}
	
	public Object getValue(String name) {
		return values.get(name);
	}

	public boolean isHasObjectValue() {
		return hasObjectValue;
	}

	public IObjectStoreItem loadObjectValue() throws ExecutionException {
		if ( hasObjectValue && objectStoreItem == null ) {
			IndependentlyPersistableObject objectStoreObject = (IndependentlyPersistableObject) values.get(THIS_PROPERTY_NAME);
			String objectType = getObjectType(objectStoreObject);
			if ( objectType != null ) {
				String id = objectStoreObject.getProperties().getIdValue(PropertyNames.ID ).toString();
				FetchObjectTask task = new FetchObjectTask(objectStore, id, objectStoreObject.getClassName(), objectType );
				objectStoreItem = (IObjectStoreItem) ObjectStoresManager.getManager().executeTaskSync(task);
			}
		}
		return objectStoreItem; 
	}

	public IObjectStoreItem getObjectValue() {
		return objectStoreItem;
	}
	
	private String getObjectType(IndependentObject objectStoreObject) {
		String objectType = null;
		if ( objectStoreObject instanceof com.filenet.api.core.Folder ) {
			objectType = FetchObjectTask.FOLDER_OBJECT_TYPE;
		} else if ( objectStoreObject instanceof com.filenet.api.core.Document ) {
			objectType = FetchObjectTask.DOCUMENT_OBJECT_TYPE;
		} else if ( objectStoreObject instanceof com.filenet.api.core.CustomObject ) {
			objectType = FetchObjectTask.CUSTOM_OBJECT_TYPE;
		}
		return objectType;
	}
}
