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

package com.ecmdeveloper.plugin.cmis.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.commons.data.PropertyData;

import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.ISearchResultRow;

/**
 * @author ricardo.belfor
 *
 */
public class SearchResultRow implements ISearchResultRow {

	private static final String THIS_PROPERTY_NAME = "This";
	private HashMap<String, Object> values = new HashMap<String, Object>();
	private boolean hasObjectValue = false;
	private ObjectStore objectStore;
	private IObjectStoreItem objectStoreItem;
	
	public SearchResultRow(QueryResult row, ObjectStore objectStore) {
		
		this.objectStore = objectStore;
		
		for ( PropertyData<?> property : row.getProperties() ) {
			String propertyName = property.getQueryName();
			Object value = property.getFirstValue();
// FIXME search			
//			if ( !THIS_PROPERTY_NAME.equals(propertyName) && value instanceof EngineObject ) {
//				value = getEngineObjectValue(value);
//			}
			values.put(propertyName, value );
			if (propertyName.equals(THIS_PROPERTY_NAME) ) {
				hasObjectValue = true;
			}
		}
	}

	private String getEngineObjectValue(Object value) {

// FIXME search		
//		EngineObject repositoryObject = (EngineObject) value;
//		StringBuffer engineObjectValue = new StringBuffer('[');
//		engineObjectValue.append( repositoryObject.getClassName() );
//		Properties properties2 = repositoryObject.getProperties();
//		if ( properties2.isPropertyPresent( PropertyNames.ID ) ) {
//			engineObjectValue.append( ',');
//			engineObjectValue.append( properties2.getIdValue( PropertyNames.ID ).toString() );
//		}
//		engineObjectValue.append(']');
//		return engineObjectValue.toString();
		return null;
	}
	
	@Override
	public Object getValue(String name) {
		Object object = values.get(name);
		if ( object instanceof Calendar ) {
			return ((Calendar) object).getTime();
		}
		return object;
	}

	@Override
	public boolean isHasObjectValue() {
		return hasObjectValue;
	}

	@Override
	public IObjectStoreItem loadObjectValue() throws ExecutionException {
		if ( hasObjectValue && objectStoreItem == null ) {
//			IndependentlyPersistableObject objectStoreObject = (IndependentlyPersistableObject) values.get(THIS_PROPERTY_NAME);
//			String objectType = getObjectType(objectStoreObject);
//			if ( objectType != null ) {
//				String id = objectStoreObject.getProperties().getIdValue(PropertyNames.ID ).toString();
//				FetchObjectTask task = new FetchObjectTask(objectStore, id, objectStoreObject.getClassName(), objectType );
//				objectStoreItem = (IObjectStoreItem) Activator.getDefault().getTaskManager().executeTaskSync(task);
//			}
		}
		return objectStoreItem; 
	}

	@Override
	public ArrayList<String> getValueNames() {
		ArrayList<String> valueNames = new ArrayList<String>();

		valueNames.addAll( values.keySet() );
		Collections.sort( valueNames, new Comparator<String>() {

			@Override
			public int compare(String arg0, String arg1) {
				if ( arg0.equals(THIS_PROPERTY_NAME) ) {
					return -1;
				}

				if ( arg1.equals(THIS_PROPERTY_NAME) ) {
					return 1;
				}
				return arg0.compareTo(arg1);
			}
		} );
		
		return valueNames;
	}
	
	@Override
	public IObjectStoreItem getObjectValue() {
		return objectStoreItem;
	}
	
//	private String getObjectType(IndependentObject objectStoreObject) {
//		String objectType = null;
//		if ( objectStoreObject instanceof com.filenet.api.core.Folder ) {
//			objectType = FetchObjectTask.FOLDER_OBJECT_TYPE;
//		} else if ( objectStoreObject instanceof com.filenet.api.core.Document ) {
//			objectType = FetchObjectTask.DOCUMENT_OBJECT_TYPE;
//		} else if ( objectStoreObject instanceof com.filenet.api.core.CustomObject ) {
//			objectType = FetchObjectTask.CUSTOM_OBJECT_TYPE;
//		}
//		return objectType;
//	}

	@Override
	public int compareTo(ISearchResultRow searchResultRow, String valueName ) {
		
		Object value1 = getValue(valueName);
		Object value2 = searchResultRow.getValue(valueName);
		
		int returnCode; 
		if ( value1 == null && value2 == null ) {
			returnCode = 0;
		} else if ( value1 == null ) {
			returnCode = -1;
		} else if ( value2 == null ) {
			returnCode = 1;
		} else if ( valueName.equals( THIS_PROPERTY_NAME ) ) {
			returnCode = getObjectValue().getDisplayName().compareTo(
					searchResultRow.getObjectValue().getDisplayName());
		} else {
			
			if ( value1 instanceof Date ) {
				returnCode = ((Date) value1).compareTo((Date) value2);
			} else if ( value1 instanceof Boolean )  {
				returnCode = ((Boolean) value1).compareTo((Boolean) value2);
			} else if ( value1 instanceof Double) {
				returnCode = ((Double) value1).compareTo((Double) value2);
			} else if ( value1 instanceof Long ) {
				returnCode = ((Long) value1).compareTo((Long) value2);
			} else {
				returnCode = value1.toString().compareTo( value2.toString() );
			}
		}
		
		return returnCode;
	}
}
