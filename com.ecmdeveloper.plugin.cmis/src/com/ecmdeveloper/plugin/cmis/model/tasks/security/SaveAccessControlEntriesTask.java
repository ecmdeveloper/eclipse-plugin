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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.OperationContextImpl;
import org.apache.chemistry.opencmis.commons.data.Ace;
import org.apache.chemistry.opencmis.commons.data.Acl;
import org.apache.chemistry.opencmis.commons.enums.AclPropagation;

import com.ecmdeveloper.plugin.cmis.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.cmis.model.security.AccessControlEntries;
import com.ecmdeveloper.plugin.cmis.model.security.AccessLevel;
import com.ecmdeveloper.plugin.core.model.constants.AccessControlEntrySource;
import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntry;
import com.ecmdeveloper.plugin.core.model.security.IAccessLevel;
import com.ecmdeveloper.plugin.core.model.security.ISecurityPrincipal;
import com.ecmdeveloper.plugin.core.model.tasks.AbstractTask;
import com.ecmdeveloper.plugin.core.model.tasks.security.ISaveAccessControlEntriesTask;

/**
 * @author ricardo.belfor
 *
 */
public class SaveAccessControlEntriesTask extends AbstractTask implements ISaveAccessControlEntriesTask {

	private final ObjectStoreItem objectStoreItem;
	private final AccessControlEntries accessControlEntries;
	
	public SaveAccessControlEntriesTask(ObjectStoreItem objectStoreItem, AccessControlEntries accessControlEntries) {
		this.objectStoreItem = objectStoreItem;
		this.accessControlEntries = accessControlEntries;
	}

	@Override
	public Object call() throws Exception {
		
	    Session session = objectStoreItem.getObjectStore().getSession();
	    CmisObject cmisObject = objectStoreItem.getCmisObject();
		Acl acl = getOldAcl(session, cmisObject);
	
		List<Ace> aceListAdd = new ArrayList<Ace>();
		List<Ace> aceListRemove = new ArrayList<Ace>();

		if ( acl != null ) {
			
			for (Ace ace : acl.getAces() ) {

//				System.out.println( ace.toString() );
				if ( !ace.isDirect() ) {
					continue;
				}

				ISecurityPrincipal principal = getPrincipal( ace );
				if ( principal == null ) {
					aceListRemove.add( ace );
				} else {

					if ( isDifferent(ace, principal) ) {
						aceListAdd.add( createAce(session, principal) );
						aceListRemove.add( ace );
					} 
				}
			}
	
			for ( ISecurityPrincipal principal : accessControlEntries.getPrincipals() ) {
				if ( getAce(principal, acl) == null ) {
					Ace createAce = createAce(session, principal);
					if ( !createAce.getPermissions().isEmpty() ) {
						aceListAdd.add(createAce );
					}
				}
			}
		}
	
//		System.out.println( aceListAdd.toString() );
//		System.out.println( aceListRemove.toString() );
		
		cmisObject.applyAcl(aceListAdd, aceListRemove, AclPropagation.REPOSITORYDETERMINED);
		
	    return null;
	}

	private Acl getOldAcl(Session session, CmisObject cmisObject) {
		OperationContext operationContext = new OperationContextImpl();
	    operationContext.setIncludeAcls(true);	    
	    CmisObject cmisObject2 = session.getObject(cmisObject, operationContext);
		Acl acl = cmisObject2.getAcl();
		return acl;
	}

	private Ace getAce(ISecurityPrincipal principal, Acl acl) {
		for ( Ace ace : acl.getAces() ) {
			if ( ace.isDirect() ) {
				if ( principal.getName().equals( ace.getPrincipalId() ) ) {
					return ace;
				}
			}
		}
		return null;
	}

    private boolean isDifferent(Ace ace, ISecurityPrincipal principal) {
		Set<String> permissions = getPermissions(principal);
		if ( permissions.size() != ace.getPermissions().size() ) {
			return true;
		}
		permissions.removeAll( ace.getPermissions() );
		return !permissions.isEmpty();
	}

	private Set<String> getPermissions(ISecurityPrincipal principal) {
		
		Set<String> permissionSet = new HashSet<String>();
		
		for (IAccessControlEntry accessControlEntry : principal.getAccessControlEntries() ) {
			if ( accessControlEntry.getSource().equals(AccessControlEntrySource.DIRECT ) ) {
				IAccessLevel accessLevel = accessControlEntry.getAccessLevel();
				permissionSet.add( ((AccessLevel)accessLevel).getId() );
			}
		}

		return permissionSet;
	}
		
	private ISecurityPrincipal getPrincipal(Ace ace) {
		for ( ISecurityPrincipal principal : accessControlEntries.getPrincipals() ) {
			if ( principal.getName().equals( ace.getPrincipalId() ) ) {
				return principal;
			}
		}
		return null;
	}

	private List<Ace> createAces(Session session) {
		List<Ace> aceListIn = new ArrayList<Ace>();
	    for ( ISecurityPrincipal principal : accessControlEntries.getPrincipals() ) {
	    	
		    Ace ace = createAce(session, principal);
		    if ( !ace.getPermissions().isEmpty() ) {
		    	aceListIn.add( ace );
		    }
	    }
		return aceListIn;
	}

	private Ace createAce(Session session, ISecurityPrincipal principal) {
		
		List<String> permissions = new ArrayList<String>();
		for (IAccessControlEntry accessControlEntry : principal.getAccessControlEntries() ) {
			if ( accessControlEntry.getSource().equals(AccessControlEntrySource.DIRECT ) ) {
				IAccessLevel accessLevel = accessControlEntry.getAccessLevel();
				permissions.add(  ((AccessLevel)accessLevel).getId() );
			}
		}

		Ace aceIn = session.getObjectFactory().createAce(principal.getName(), permissions);
		return aceIn;
	}
}
