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

import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;

/**
 * @author ricardo.belfor
 *
 */
public abstract class FavoriteObjectStoreItem {

	private String id;
	private String className;
	private String connectionName;
	private String objectStoreName;
	private String name;

	public FavoriteObjectStoreItem(String id, String name, String className, String connectionName, String objectStoreName) {
		this.id = id;
		this.className = className;
		this.connectionName = connectionName;
		this.objectStoreName = objectStoreName;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}
	public String getClassName() {
		return className;
	}
	public String getConnectionName() {
		return connectionName;
	}
	public String getObjectStoreName() {
		return objectStoreName;
	}

	public boolean isFavoriteOf(ObjectStoreItem objectStoreItem) {
		ObjectStore objectStore = objectStoreItem.getObjectStore();
		ContentEngineConnection connection = objectStore.getConnection();
		return id.equalsIgnoreCase(objectStoreItem.getId())
				&& objectStoreName.equals(objectStore.getName())
				&& connectionName.equals(connection.getName());
	}
}
