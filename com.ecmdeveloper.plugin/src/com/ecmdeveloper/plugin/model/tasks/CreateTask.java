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

package com.ecmdeveloper.plugin.model.tasks;

import java.util.Map;

import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.model.Folder;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.ReferentialContainmentRelationship;

/**
 * @author ricardo.belfor
 *
 */
public abstract class CreateTask extends BaseTask {

	private Map<String,Object> propertiesMap;
	private ObjectStoreItem parent;
	private String className;
	private boolean addedToParent;
	
	public CreateTask(ObjectStoreItem parent, String className, Map<String, Object> propertiesMap) {
		this.propertiesMap = propertiesMap;
		this.parent = parent;
		this.className = className;
		addedToParent = false;
	}

	public ObjectStoreItem getParent() {
		return parent;
	}
	
	public String getClassName() {
		return className;
	}

	public abstract ObjectStoreItem getNewObjectStoreItem();
	
	protected void setProperties(ObjectStoreItem objectStoreItem) throws Exception {
		for ( String propertyName : propertiesMap.keySet() ) {
			objectStoreItem.setValue(propertyName, propertiesMap.get(propertyName) );
		}
	}

	protected com.filenet.api.core.ObjectStore getInternalObjectStore() {
		ObjectStore objectStore = parent.getObjectStore();
		com.filenet.api.core.ObjectStore internalObjectStore = (com.filenet.api.core.ObjectStore) objectStore
				.getObjectStoreObject();
		return internalObjectStore;
	}

	protected void fileInParent() {

		com.filenet.api.core.Folder internalParent = (com.filenet.api.core.Folder) getParent()
				.getObjectStoreObject();
		
		ObjectStoreItem newDocument = getNewObjectStoreItem();
		
		ReferentialContainmentRelationship relationship = internalParent.file(newDocument
				.getObjectStoreObject(), AutoUniqueName.AUTO_UNIQUE, newDocument.getName(),
				DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);
		relationship.save(RefreshMode.NO_REFRESH);
		setAddedToParent(true);
	}

	public boolean isAddedToParent() {
		return addedToParent;
	}

	protected void setAddedToParent(boolean addedToParent) {
		this.addedToParent = addedToParent;
	}

	@Override
	protected ContentEngineConnection getContentEngineConnection() {
		return parent.getObjectStore().getConnection();
	}
}
