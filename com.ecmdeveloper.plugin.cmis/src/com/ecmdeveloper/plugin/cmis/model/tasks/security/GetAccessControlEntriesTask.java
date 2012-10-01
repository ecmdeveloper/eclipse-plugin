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
import java.util.List;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.OperationContextImpl;
import org.apache.chemistry.opencmis.commons.data.Ace;
import org.apache.chemistry.opencmis.commons.data.Acl;
import org.apache.chemistry.opencmis.commons.data.AclCapabilities;
import org.apache.chemistry.opencmis.commons.definitions.PermissionDefinition;

import com.ecmdeveloper.plugin.cmis.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.cmis.model.security.AccessControlEntries;
import com.ecmdeveloper.plugin.cmis.model.security.AccessLevel;
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
	    Session session = objectStoreItem.getObjectStore().getSession();
		initializeDescription(session);
		
		CmisObject cmisObject = objectStoreItem.getCmisObject();
		
		OperationContext operationContext = new OperationContextImpl();
	    operationContext.setIncludeAcls(true);	    
	    
	    cmisObject = session.getObject(cmisObject, operationContext);
	    
		Acl acl = cmisObject.getAcl();
		if ( acl != null ) {
			for (Ace ace : acl.getAces() ) {
				accessControlEntries.addAce(ace);
			}
		}

		return null;
	}

	private void initializeDescription(Session session) {
		
	    AclCapabilities aclCapabilities = session.getRepositoryInfo().getAclCapabilities();
	    List<PermissionDefinition> permissionDefinitions = aclCapabilities.getPermissions();
	    for (PermissionDefinition permissionDefinition : permissionDefinitions )
	    {
			accessControlEntries.addAccessLevel( new AccessLevel(permissionDefinition, aclCapabilities.getPermissionMapping() ) );
	    }
	}
}
