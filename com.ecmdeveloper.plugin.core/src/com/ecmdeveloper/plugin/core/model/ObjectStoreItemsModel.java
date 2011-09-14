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

package com.ecmdeveloper.plugin.core.model;

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

	Map<String,Map<String,Collection<IObjectStoreItem>>> objectStoresMap;
	private static ObjectStoreItemsModel objectStoreItemsModel;
	private ObjectStoreItemsModel() {
		objectStoresMap = new HashMap<String, Map<String,Collection<IObjectStoreItem>>>();
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
	
	public synchronized void add(IObjectStoreItem objectStoreItem) {

		Collection<IObjectStoreItem> objectStoreItemList = getObjectStoreItemList(objectStoreItem);
		objectStoreItemList.add(objectStoreItem);
	}

	public Collection<IObjectStoreItem> get(IObjectStoreItem objectStoreItem ) {
		if (objectStoreItem == null ) {
			return new ArrayList<IObjectStoreItem>();
		}
		return getObjectStoreItemList(objectStoreItem);
	}
	
	private Collection<IObjectStoreItem> getObjectStoreItemList(IObjectStoreItem objectStoreItem) {
		String objectStoreItemKey = getObjectStoreItemKey(objectStoreItem);
		return getObjectStoreItemList(objectStoreItemKey, objectStoreItem.getObjectStore());
	}

	private String getObjectStoreItemKey(IObjectStoreItem objectStoreItem) {
		if (objectStoreItem instanceof IObjectStore ) {
			return objectStoreItem.getName();
		} else if ( objectStoreItem instanceof IDocument ) {
			String versionSeriesId = ((IDocument)objectStoreItem).getVersionSeriesId();
			if ( versionSeriesId != null ) {
				return versionSeriesId;
			}
		}
		String objectStoreItemKey = objectStoreItem.getId();
		return objectStoreItemKey;
	}

	private Collection<IObjectStoreItem> remove(IObjectStoreItem objectStoreItem) {
		Map<String, Collection<IObjectStoreItem>> objectStoreItemsMap = getObjectStoreItemsMap(objectStoreItem.getObjectStore() );
		String objectStoreItemKey = getObjectStoreItemKey(objectStoreItem);
		Collection<IObjectStoreItem> objectStoreItemList = objectStoreItemsMap.remove( objectStoreItemKey);
		if ( objectStoreItemList == null ) {
			return new ArrayList<IObjectStoreItem>();
		}
		return objectStoreItemList;
	}
	
	private Collection<IObjectStoreItem> getObjectStoreItemList(String objectStoreItemKey, IObjectStore objectStore) {
		Map<String, Collection<IObjectStoreItem>> objectStoreItemsMap = getObjectStoreItemsMap(objectStore);
		Collection<IObjectStoreItem> objectStoreItemList;

		if  (! objectStoreItemsMap.containsKey(objectStoreItemKey) ) {
			objectStoreItemList = new HashSet<IObjectStoreItem>();
			objectStoreItemsMap.put(objectStoreItemKey, objectStoreItemList);
		} else {
			objectStoreItemList = objectStoreItemsMap.get(objectStoreItemKey);
		}
		return objectStoreItemList;
	}


	private Map<String, Collection<IObjectStoreItem>> getObjectStoreItemsMap(IObjectStore objectStore) {
		String objectStoreKey = getObjectStoreKey(objectStore);
		Map<String, Collection<IObjectStoreItem>> objectStoreItemsMap;

		if ( ! objectStoresMap.containsKey(objectStoreKey) ) { 
			objectStoreItemsMap = new HashMap<String, Collection<IObjectStoreItem>>();
			objectStoresMap.put(objectStoreKey, objectStoreItemsMap );
		} else {
			objectStoreItemsMap = objectStoresMap.get(objectStoreKey);
		}
		return objectStoreItemsMap;
	}


	private String getObjectStoreKey(IObjectStore objectStore) {
		String objectStoreKey = objectStore.getName() + objectStore.getConnection().getName();
		return objectStoreKey;
	}

	public Collection<IObjectStoreItem> addChildren(IObjectStoreItem objectStoreItem, ArrayList<IObjectStoreItem> children) {
		Collection<IObjectStoreItem> parents = get(objectStoreItem);
		for (IObjectStoreItem parent : parents ) {
			parent.setChildren(children);
		}
		return parents;
	}

	public Collection<IObjectStoreItem> addChild(IObjectStoreItem objectStoreItem, IObjectStoreItem child) {
		Collection<IObjectStoreItem> parents = get(objectStoreItem);
		for (IObjectStoreItem parent : parents ) {
			parent.addChild( child );
		}
		return parents;
	}
	
	public Collection<IObjectStoreItem> delete(Collection<IObjectStoreItem> deletedItems) {
		Collection<IObjectStoreItem> deletedModelItems = new HashSet<IObjectStoreItem>();

		for (IObjectStoreItem deletedItem : deletedItems ) {
			Collection<IObjectStoreItem> objectStoreItemList = remove(deletedItem);
			for ( IObjectStoreItem objectStoreItem : objectStoreItemList ) {
				removeChildFromParent(objectStoreItem);
				deletedModelItems.addAll(objectStoreItemList);
			}
		}
		
		return deletedModelItems;
	}

	private void removeChildFromParent(IObjectStoreItem objectStoreItem) {
		Collection<IObjectStoreItem> parents = get((IObjectStoreItem) objectStoreItem.getParent());
		for ( IObjectStoreItem parent : parents ) {
			if ( parent instanceof IFolder ) {
				((IFolder)parent).removeChild(objectStoreItem);
			} else if ( parent instanceof IObjectStore ) {
				((IObjectStore)parent).removeChild(objectStoreItem);
			}
		}
	}
}
