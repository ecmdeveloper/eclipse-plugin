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

package com.ecmdeveloper.plugin.cmis.model.tasks.security;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.OperationContextImpl;
import org.apache.chemistry.opencmis.commons.data.Ace;
import org.apache.chemistry.opencmis.commons.data.Acl;
import org.apache.chemistry.opencmis.commons.data.Principal;

import com.ecmdeveloper.plugin.cmis.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.cmis.model.security.AccessControlEntries;
import com.ecmdeveloper.plugin.core.model.constants.PrincipalType;
import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntries;
import com.ecmdeveloper.plugin.core.model.security.IRealm;
import com.ecmdeveloper.plugin.core.model.tasks.AbstractTask;
import com.ecmdeveloper.plugin.core.model.tasks.security.IGetAccessControlEntriesTask;

/**
 * @author ricardo.belfor
 *
 */
public class GetAccessControlEntriesTask extends AbstractTask implements IGetAccessControlEntriesTask {

	private AccessControlEntries accessControlEntries;
	private final ObjectStoreItem objectStoreItem;
	private final Collection<IRealm> realms;
	
	public GetAccessControlEntriesTask(ObjectStoreItem objectStoreItem, Collection<IRealm> realms) {
		this.objectStoreItem = objectStoreItem;
		this.realms = realms;
	}

	@Override
	public IAccessControlEntries getAccessControlEntries() {
		return accessControlEntries;
	}

	@Override
	public Object call() throws Exception {
		
		accessControlEntries = new AccessControlEntries();

		CmisObject cmisObject = objectStoreItem.getCmisObject();
		
		OperationContext operationContext = new OperationContextImpl();
	    operationContext.setIncludeAcls(true);
	    Session session = objectStoreItem.getObjectStore().getSession();
	    
	    cmisObject = session.getObject(cmisObject, operationContext);
	    
		Acl acl = cmisObject.getAcl();
		if ( acl != null ) {
			for (Ace ace : acl.getAces() ) {
				accessControlEntries.addAce(ace);
			}
		}
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	protected Object execute() throws Exception {
//		
//		accessControlEntries = new AccessControlEntries();
//		
//		PropertyFilter pf = new PropertyFilter();
//		pf.addIncludeProperty(0, null, null, PropertyNames.CLASS_DESCRIPTION, null);
//		pf.addIncludeProperty(0, null, null, PropertyNames.PERMISSIONS, null);
//		objectStoreItem.getObjectStoreObject().refresh(pf);
//		
//		intializeDescriptions();
//		
//		if ( objectStoreItem instanceof Document || objectStoreItem instanceof Folder || objectStoreItem instanceof CustomObject ) {
//			Containable containable = (Containable) objectStoreItem.getObjectStoreObject();
//			AccessPermissionList permissions = containable.get_Permissions();
//			
//			for ( AccessPermission permission : getAccessPermissionListIterator(permissions) ) {
//				Principal principal = getPrincipal(permission);
//				
//				accessControlEntries.addPermission(permission, principal );
//			}
//		}
//
//		return null;
//	}

//	private Principal getPrincipal(AccessPermission permission) {
//		for ( IRealm realm : realms) {
//			Principal principal = ((Realm)realm).getPrincipal( permission );
//			if ( principal != null ) {
//				return principal;
//			}
//		}
//		return new Principal(permission.get_GranteeName(), PrincipalType.UNKNOWN, null );
//	}
//
//	private void intializeDescriptions() {
//		ClassDescription classDescription = objectStoreItem.getObjectStoreObject().get_ClassDescription();
//		AccessPermissionDescriptionList descriptions = classDescription.get_PermissionDescriptions();
//		for ( AccessPermissionDescription description : getAccessPermissionDescriptionListIterator(descriptions) ) {
//			initializeDescription(description);
//		}
//	}
//
//	private void initializeDescription(AccessPermissionDescription description) {
//		PermissionType permissionType = description.get_PermissionType();
//		if (permissionType.equals(PermissionType.LEVEL)
//				|| permissionType.equals(PermissionType.LEVEL_DEFAULT)) {
//			AccessLevel accessLevel = new AccessLevel(description);
//			accessControlEntries.addAccessLevel(accessLevel);
//		} else {
//			AccessRightDescription accessRightDescription = new AccessRightDescription(description);
//			accessControlEntries.addAccessRightDescription(accessRightDescription);
//		}
//	}
//
//	@Override
//	protected ContentEngineConnection getContentEngineConnection() {
//		return objectStoreItem.getObjectStore().getConnection();
//	}
//
//
//	private Iterable<AccessPermission> getAccessPermissionListIterator(final AccessPermissionList accessPermissionList) {
//		return new Iterable<AccessPermission>() {
//
//			@SuppressWarnings("unchecked")
//			@Override
//			public Iterator<AccessPermission> iterator() {
//				return accessPermissionList.iterator();
//			}
//		};
//	}
//
//	private Iterable<AccessPermissionDescription> getAccessPermissionDescriptionListIterator(final AccessPermissionDescriptionList accessPermissionDescriptionList) {
//		return new Iterable<AccessPermissionDescription>() {
//
//			@SuppressWarnings("unchecked")
//			@Override
//			public Iterator<AccessPermissionDescription> iterator() {
//				return accessPermissionDescriptionList.iterator();
//			}
//		};
//	}
}
