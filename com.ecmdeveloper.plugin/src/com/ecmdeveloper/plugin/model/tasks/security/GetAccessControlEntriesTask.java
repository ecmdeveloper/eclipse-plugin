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

import java.util.Collection;
import java.util.Iterator;

import com.ecmdeveloper.plugin.core.model.constants.PrincipalType;
import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntries;
import com.ecmdeveloper.plugin.core.model.security.IRealm;
import com.ecmdeveloper.plugin.core.model.tasks.security.IGetAccessControlEntriesTask;
import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.model.CustomObject;
import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.Folder;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.model.security.AccessControlEntries;
import com.ecmdeveloper.plugin.model.security.AccessLevel;
import com.ecmdeveloper.plugin.model.security.AccessRightDescription;
import com.ecmdeveloper.plugin.model.security.Principal;
import com.ecmdeveloper.plugin.model.security.Realm;
import com.ecmdeveloper.plugin.model.tasks.BaseTask;
import com.filenet.api.collection.AccessPermissionDescriptionList;
import com.filenet.api.collection.AccessPermissionList;
import com.filenet.api.constants.PermissionType;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.core.Containable;
import com.filenet.api.meta.ClassDescription;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.security.AccessPermission;
import com.filenet.api.security.AccessPermissionDescription;

/**
 * @author ricardo.belfor
 *
 */
public class GetAccessControlEntriesTask extends BaseTask implements IGetAccessControlEntriesTask {

	private AccessControlEntries accessControlEntries;
	private final ObjectStoreItem objectStoreItem;
	private final Collection<IRealm> realms;
	
	public GetAccessControlEntriesTask(ObjectStoreItem objectStoreItem, Collection<IRealm> realms) {
		this.objectStoreItem = objectStoreItem;
		this.realms = realms;
	}

	@Override
	protected Object execute() throws Exception {
		
		accessControlEntries = new AccessControlEntries();
		
		PropertyFilter pf = new PropertyFilter();
		pf.addIncludeProperty(0, null, null, PropertyNames.CLASS_DESCRIPTION, null);
		pf.addIncludeProperty(0, null, null, PropertyNames.PERMISSIONS, null);
		objectStoreItem.getObjectStoreObject().refresh(pf);
		
		intializeDescriptions();
		
		if ( objectStoreItem instanceof Document || objectStoreItem instanceof Folder || objectStoreItem instanceof CustomObject ) {
			Containable containable = (Containable) objectStoreItem.getObjectStoreObject();
			AccessPermissionList permissions = containable.get_Permissions();
			
			for ( AccessPermission permission : getAccessPermissionListIterator(permissions) ) {
				Principal principal = getPrincipal(permission);
				
				accessControlEntries.addPermission(permission, principal );
			}
		}

		return null;
	}

	private Principal getPrincipal(AccessPermission permission) {
		for ( IRealm realm : realms) {
			Principal principal = ((Realm)realm).getPrincipal( permission );
			if ( principal != null ) {
				return principal;
			}
		}
		return new Principal(permission.get_GranteeName(), PrincipalType.UNKNOWN, null );
	}

	private void intializeDescriptions() {
		ClassDescription classDescription = objectStoreItem.getObjectStoreObject().get_ClassDescription();
		AccessPermissionDescriptionList descriptions = classDescription.get_PermissionDescriptions();
		for ( AccessPermissionDescription description : getAccessPermissionDescriptionListIterator(descriptions) ) {
			initializeDescription(description);
		}
	}

	private void initializeDescription(AccessPermissionDescription description) {
		PermissionType permissionType = description.get_PermissionType();
		if (permissionType.equals(PermissionType.LEVEL)
				|| permissionType.equals(PermissionType.LEVEL_DEFAULT)) {
			AccessLevel accessLevel = new AccessLevel(description);
			accessControlEntries.addAccessLevel(accessLevel);
		} else {
			AccessRightDescription accessRightDescription = new AccessRightDescription(description);
			accessControlEntries.addAccessRightDescription(accessRightDescription);
		}
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

	private Iterable<AccessPermissionDescription> getAccessPermissionDescriptionListIterator(final AccessPermissionDescriptionList accessPermissionDescriptionList) {
		return new Iterable<AccessPermissionDescription>() {

			@SuppressWarnings("unchecked")
			@Override
			public Iterator<AccessPermissionDescription> iterator() {
				return accessPermissionDescriptionList.iterator();
			}
		};
	}
}
