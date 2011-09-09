/**
 * Copyright 2009, Ricardo Belfor
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
package com.ecmdeveloper.plugin.model.tasks;

import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.tasks.IFetchPropertiesTask;
import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.filenet.api.core.IndependentlyPersistableObject;

/**
 * @author Ricardo Belfor
 *
 */
public class FetchPropertiesTask extends BaseTask implements IFetchPropertiesTask {

	private ObjectStoreItem objectStoreItem; 
	private String propertyNames[];
	
	public FetchPropertiesTask(IObjectStoreItem objectStoreItem, String[] propertyNames) {
		this.objectStoreItem = (ObjectStoreItem) objectStoreItem;
		this.propertyNames = propertyNames;
	}

	@Override
	protected Object execute() throws Exception {

		IndependentlyPersistableObject objectStoreObject = objectStoreItem.getObjectStoreObject();
		objectStoreObject.refresh(propertyNames);
		//objectStoreObject.fetchProperties(propertyNames);

		return null;
	}

	@Override
	protected ContentEngineConnection getContentEngineConnection() {
		return objectStoreItem.getObjectStore().getConnection();
	}
}
