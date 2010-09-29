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

package com.ecmdeveloper.plugin.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author ricardo.belfor
 *
 */
public class ObjectStoreItemsModel {

	Map<String,Map<String,Collection<ObjectStoreItem>>> objectStoresMap;
	private static ObjectStoreItemsModel objectStoreItemsModel;
	private ObjectStoreItemsModel() {
		objectStoresMap = new HashMap<String, Map<String,Collection<ObjectStoreItem>>>();
	}
	
	public static ObjectStoreItemsModel getInstance() {
		synchronized (ObjectStoreItemsModel.class) {
			if ( objectStoreItemsModel == null )
			{
				objectStoreItemsModel = new ObjectStoreItemsModel();
			}
			return objectStoreItemsModel;
		}
	}
	
	public synchronized void add(ObjectStoreItem objectStoreItem) {

		Collection<ObjectStoreItem> objectStoreItemList = getObjectStoreItemList(objectStoreItem);
		objectStoreItemList.add(objectStoreItem);
	}

	public Collection<ObjectStoreItem> get(ObjectStoreItem objectStoreItem ) {
		if (objectStoreItem == null ) {
			return new ArrayList<ObjectStoreItem>();
		}
		return getObjectStoreItemList(objectStoreItem);
	}
	
	private Collection<ObjectStoreItem> getObjectStoreItemList(ObjectStoreItem objectStoreItem) {
		String objectStoreItemKey = getObjectStoreItemKey(objectStoreItem);
		return getObjectStoreItemList(objectStoreItemKey, objectStoreItem.getObjectStore());
	}

	private String getObjectStoreItemKey(ObjectStoreItem objectStoreItem) {
		if (objectStoreItem instanceof ObjectStore ) {
			return objectStoreItem.getName();
		} else if ( objectStoreItem instanceof Document ) {
			return ((Document)objectStoreItem).getVersionSeriesId();
		}
		String objectStoreItemKey = objectStoreItem.getId();
		return objectStoreItemKey;
	}

	private Collection<ObjectStoreItem> remove(ObjectStoreItem objectStoreItem) {
		Map<String, Collection<ObjectStoreItem>> objectStoreItemsMap = getObjectStoreItemsMap(objectStoreItem.getObjectStore() );
		String objectStoreItemKey = getObjectStoreItemKey(objectStoreItem);
		Collection<ObjectStoreItem> objectStoreItemList = objectStoreItemsMap.remove( objectStoreItemKey);
		if ( objectStoreItemList == null ) {
			return new ArrayList<ObjectStoreItem>();
		}
		return objectStoreItemList;
	}
	
	private Collection<ObjectStoreItem> getObjectStoreItemList(String objectStoreItemKey, ObjectStore objectStore) {
		Map<String, Collection<ObjectStoreItem>> objectStoreItemsMap = getObjectStoreItemsMap(objectStore);
		Collection<ObjectStoreItem> objectStoreItemList;

		if  (! objectStoreItemsMap.containsKey(objectStoreItemKey) ) {
			objectStoreItemList = new HashSet<ObjectStoreItem>();
			objectStoreItemsMap.put(objectStoreItemKey, objectStoreItemList);
		} else {
			objectStoreItemList = objectStoreItemsMap.get(objectStoreItemKey);
		}
		return objectStoreItemList;
	}


	private Map<String, Collection<ObjectStoreItem>> getObjectStoreItemsMap(ObjectStore objectStore) {
		String objectStoreKey = getObjectStoreKey(objectStore);
		Map<String, Collection<ObjectStoreItem>> objectStoreItemsMap;

		if ( ! objectStoresMap.containsKey(objectStoreKey) ) { 
			objectStoreItemsMap = new HashMap<String, Collection<ObjectStoreItem>>();
			objectStoresMap.put(objectStoreKey, objectStoreItemsMap );
		} else {
			objectStoreItemsMap = objectStoresMap.get(objectStoreKey);
		}
		return objectStoreItemsMap;
	}


	private String getObjectStoreKey(ObjectStore objectStore) {
		String objectStoreKey = objectStore.getName() + objectStore.getConnection().getName();
		return objectStoreKey;
	}

	public Collection<ObjectStoreItem> addChildren(ObjectStoreItem objectStoreItem, ArrayList<IObjectStoreItem> children) {
		Collection<ObjectStoreItem> parents = get(objectStoreItem);
		for (ObjectStoreItem parent : parents ) {
			parent.setChildren(children);
		}
		return parents;
	}

	public Collection<ObjectStoreItem> addChild(ObjectStoreItem objectStoreItem, ObjectStoreItem child) {
		Collection<ObjectStoreItem> parents = get(objectStoreItem);
		for (ObjectStoreItem parent : parents ) {
			parent.addChild( child );
		}
		return parents;
	}
	
	public Collection<ObjectStoreItem> delete(Collection<ObjectStoreItem> deletedItems) {
		Collection<ObjectStoreItem> deletedModelItems = new HashSet<ObjectStoreItem>();

		for (ObjectStoreItem deletedItem : deletedItems ) {
			Collection<ObjectStoreItem> objectStoreItemList = remove(deletedItem);
			for ( ObjectStoreItem objectStoreItem : objectStoreItemList ) {
				removeChildFromParent(objectStoreItem);
				deletedModelItems.addAll(objectStoreItemList);
			}
		}
		
		return deletedModelItems;
	}

	private void removeChildFromParent(ObjectStoreItem objectStoreItem) {
		Collection<ObjectStoreItem> parents = get((ObjectStoreItem) objectStoreItem.getParent());
		for ( ObjectStoreItem parent : parents ) {
			if ( parent instanceof Folder ) {
				((Folder)parent).removeChild(objectStoreItem);
			} else if ( parent instanceof ObjectStore ) {
				((ObjectStore)parent).removeChild(objectStoreItem);
			}
		}
	}
}
