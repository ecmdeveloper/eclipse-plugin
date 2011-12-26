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

import com.ecmdeveloper.plugin.core.model.IConnection;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.Placeholder;
import com.ecmdeveloper.plugin.core.model.constants.PlaceholderType;

/**
 * @author ricardo.belfor
 *
 */
public class FavoriteObjectStore {

	private IObjectStore objectStore;
	private Collection<IObjectStoreItem> children;

	public FavoriteObjectStore(IObjectStore objectStore) {
		this.objectStore = objectStore;
	}

	public IObjectStore getObjectStore() {
		return objectStore;
	}

	public Collection<IObjectStoreItem> getChildren() 
	{
		if ( !objectStore.isConnected() ) {
			return null;
		}
		
		if ( children == null )
		{
			children = new ArrayList<IObjectStoreItem>();
			children.add( new Placeholder( PlaceholderType.LOADING ) );

			FavoritesManager.getInstance().fetchFavorites(this);
		}		
		return children;
	}

	public void refreshChildren(Collection<IObjectStoreItem> children) {
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

	public void addChild(IObjectStoreItem objectStoreItem) {
		if (children == null ) {
			children = new ArrayList<IObjectStoreItem>();
		}
		children.add(objectStoreItem);
	}

	public void removeChild(IObjectStoreItem objectStoreItem) {
		if ( children == null ) {
			return;
		}
		
		for ( IObjectStoreItem child : children ) {
			if ( child.equals(objectStoreItem) || child.isSimilarObject(objectStoreItem) ) {
				children.remove(child);
				return;
			}
		}
	}
	
	@Override
	public String toString() {
		return getObjectStore().getDisplayName();
	}

	public boolean isObjectStoreOf(IObjectStore objectStore) {
		IConnection connection = objectStore.getConnection();
		String objectStoreName = this.objectStore.getName();
		String connectionName = this.objectStore.getConnection().getName();
		return objectStoreName.equals(objectStore.getName())
				&& connectionName.equals(connection.getName());
	}
}
