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

import java.security.AccessControlContext;
import java.util.Iterator;

import com.ecmdeveloper.plugin.core.model.constants.PrincipalType;
import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntries;
import com.ecmdeveloper.plugin.core.model.security.ISecurityPrincipal;
import com.ecmdeveloper.plugin.core.model.tasks.security.IGetAccessControlEntriesTask;
import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.model.CustomObject;
import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.Folder;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.model.security.AccessControlEntries;
import com.ecmdeveloper.plugin.model.security.Principal;
import com.ecmdeveloper.plugin.model.security.SecurityPrincipal;
import com.ecmdeveloper.plugin.model.tasks.BaseTask;
import com.filenet.api.collection.AccessPermissionList;
import com.filenet.api.constants.SecurityPrincipalType;
import com.filenet.api.core.Containable;
import com.filenet.api.security.AccessPermission;

/**
 * @author ricardo.belfor
 *
 */
public class GetAccessControlEntriesTask extends BaseTask implements IGetAccessControlEntriesTask {

	private AccessControlEntries accessControlEntries;
	private ObjectStoreItem objectStoreItem;
	
	public GetAccessControlEntriesTask(ObjectStoreItem objectStoreItem) {
		this.objectStoreItem = objectStoreItem;
	}

	@Override
	protected Object execute() throws Exception {
		
		accessControlEntries = new AccessControlEntries();
		
		if ( objectStoreItem instanceof Document || objectStoreItem instanceof Folder || objectStoreItem instanceof CustomObject ) {
			Containable containable = (Containable) objectStoreItem.getObjectStoreObject();
			AccessPermissionList permissions = containable.get_Permissions();
			for ( AccessPermission permission : getAccessPermissionListIterator(permissions) ) {
				accessControlEntries.addPermission(permission);
			}

		}
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ContentEngineConnection getContentEngineConnection() {
		return objectStoreItem.getObjectStore().getConnection();
	}

	@Override
	public IAccessControlEntries getAccessControlEntries() {
		return accessControlEntries;
	}
	
	private Iterable<AccessPermission> getAccessPermissionListIterator(final AccessPermissionList accessPermissionList) {
		return new Iterable<AccessPermission>() {

			@SuppressWarnings("unchecked")
			@Override
			public Iterator<AccessPermission> iterator() {
				return accessPermissionList.iterator();
			}
		};
	}
}
