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

package com.ecmdeveloper.plugin.model.tasks.security;

import com.ecmdeveloper.plugin.core.model.constants.AccessControlEntryType;
import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntry;
import com.ecmdeveloper.plugin.core.model.tasks.security.ISaveAccessControlEntriesTask;
import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.model.security.AccessControlEntries;
import com.ecmdeveloper.plugin.model.security.AccessControlEntry;
import com.ecmdeveloper.plugin.model.tasks.BaseTask;
import com.filenet.api.collection.AccessPermissionList;
import com.filenet.api.constants.AccessType;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Containable;
import com.filenet.api.core.Factory;
import com.filenet.api.core.IndependentlyPersistableObject;
import com.filenet.api.security.AccessPermission;

/**
 * @author ricardo.belfor
 *
 */
public class SaveAccessControlEntriesTask extends BaseTask implements ISaveAccessControlEntriesTask {

	private final ObjectStoreItem objectStoreItem;
	private final AccessControlEntries accessControlEntries;
	
	public SaveAccessControlEntriesTask(ObjectStoreItem objectStoreItem, AccessControlEntries accessControlEntries) {
		this.objectStoreItem = objectStoreItem;
		this.accessControlEntries = accessControlEntries;
	}

	@Override
	protected Object execute() throws Exception {
		
		IndependentlyPersistableObject objectStoreObject = objectStoreItem.getObjectStoreObject();
		((Containable)objectStoreObject).set_Permissions( createPermissionList() );
		objectStoreObject.save(RefreshMode.REFRESH);
		
		return null;
	}

	@SuppressWarnings("unchecked")
	private AccessPermissionList createPermissionList() {
		AccessPermissionList permissionList = Factory.AccessPermission.createList();
		
		for ( IAccessControlEntry accessControlEntry : accessControlEntries.getAccessControlEntries() ) {
			if ( accessControlEntry.isEditable() ) {
				permissionList.add( createPermission( (AccessControlEntry) accessControlEntry) );
			}
		}
		return permissionList;
	}

	private AccessPermission createPermission(AccessControlEntry accessControlEntry) {
		
		AccessPermission permission = Factory.AccessPermission.createInstance();
		
		permission.set_AccessMask( accessControlEntry.getAccessMask() );
		permission.set_GranteeName( accessControlEntry.getPrincipal().getName() );
		permission.set_AccessType( getAccessType(accessControlEntry) );
		permission.set_InheritableDepth( (Integer) accessControlEntry.getAccessControlEntryPropagation().getValue() );
		
		return permission;
	}

	private AccessType getAccessType(AccessControlEntry accessControlEntry) {
		return accessControlEntry.getType() == AccessControlEntryType.ALLOW ? AccessType.ALLOW : AccessType.DENY;
	}

	@Override
	protected ContentEngineConnection getContentEngineConnection() {
		return objectStoreItem.getObjectStore().getConnection();
	}
}
