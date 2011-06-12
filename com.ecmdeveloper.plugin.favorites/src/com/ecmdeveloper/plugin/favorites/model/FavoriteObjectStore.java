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

package com.ecmdeveloper.plugin.favorites.model;

import java.util.ArrayList;
import java.util.Collection;

import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.model.Placeholder;

/**
 * @author ricardo.belfor
 *
 */
public class FavoriteObjectStore {

	private ObjectStore objectStore;
	private Collection<ObjectStoreItem> children;

	public FavoriteObjectStore(ObjectStore objectStore) {
		this.objectStore = objectStore;
	}

	public ObjectStore getObjectStore() {
		return objectStore;
	}

	public Collection<ObjectStoreItem> getChildren() 
	{
		if ( children == null )
		{
			children = new ArrayList<ObjectStoreItem>();
			children.add( new Placeholder( Placeholder.Type.LOADING ) );

			FavoritesManager.getInstance().fetchFavorites(this);
		}		
		return children;
	}

	public void refreshChildren(Collection<ObjectStoreItem> children) {
		this.children.clear();
		this.children.addAll( children );
	}

	public boolean hasChildren() {
		if ( !objectStore.isConnected() ) {
			return false;
		}
		if (children == null ) {
			return true;
		}
		return children.size() > 0;
	}

	public void addChild(ObjectStoreItem objectStoreItem) {
		if (children == null ) {
			children = new ArrayList<ObjectStoreItem>();
		}
		children.add(objectStoreItem);
	}

	public void removeChild(ObjectStoreItem objectStoreItem) {
		if ( children == null ) {
			return;
		}
		
		for ( ObjectStoreItem child : children ) {
			if ( child.equals(objectStoreItem) || child.isSimilarObject(objectStoreItem) ) {
				children.remove(child);
				return;
			}
		}
	}
	
	public boolean isObjectStoreOf(ObjectStore objectStore) {
		ContentEngineConnection connection = objectStore.getConnection();
		String objectStoreName = this.objectStore.getName();
		String connectionName = this.objectStore.getConnection().getName();
		return objectStoreName.equals(objectStore.getName())
				&& connectionName.equals(connection.getName());
	}
}
