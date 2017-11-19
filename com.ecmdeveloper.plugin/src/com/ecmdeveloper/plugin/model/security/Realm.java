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

package com.ecmdeveloper.plugin.model.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IMemento;

import com.ecmdeveloper.plugin.Activator;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.constants.PrincipalType;
import com.ecmdeveloper.plugin.core.model.security.IPrincipal;
import com.ecmdeveloper.plugin.core.model.security.IRealm;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskFactory;
import com.ecmdeveloper.plugin.core.model.tasks.security.IFindPrincipalsTask;
import com.ecmdeveloper.plugin.core.model.tasks.security.IGetMembersTask;
import com.ecmdeveloper.plugin.core.model.tasks.security.IGetMembershipsTask;
import com.filenet.api.collection.GroupSet;
import com.filenet.api.collection.UserSet;
import com.filenet.api.constants.PrincipalSearchAttribute;
import com.filenet.api.constants.PrincipalSearchSortType;
import com.filenet.api.constants.PrincipalSearchType;
import com.filenet.api.constants.SecurityPrincipalType;
import com.filenet.api.core.Factory;
import com.filenet.api.security.AccessPermission;
import com.filenet.api.security.Group;
import com.filenet.api.security.User;

/**
 * @author ricardo.belfor
 *
 */
public class Realm implements IRealm {

	private static final String COMMON_NAME_TYPE = "CN";
	private com.filenet.api.security.Realm realm;
	private final IObjectStore objectStore;

	private final IPrincipal authenticatedUsers = new SpecialPrincipal( com.filenet.api.constants.SpecialPrincipal.AUTHENTICATED_USERS.toString() );
	private final IPrincipal creatorOwner = new SpecialPrincipal( com.filenet.api.constants.SpecialPrincipal.CREATOR_OWNER.toString() );

	public Realm(com.filenet.api.security.Realm realm, IObjectStore objectStore) {
		this.realm = realm;
		this.objectStore = objectStore;
	}
	
	@Override
	public String getName() {
		return realm.get_Name();
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public Collection<IPrincipal> findPrincipals(String pattern, PrincipalType type, IProgressMonitor progressMonitor) throws ExecutionException {
		if ( !PrincipalType.SPECIAL_ACCOUNT.equals(type) ) {
			ITaskFactory taskFactory = objectStore.getTaskFactory();
			IFindPrincipalsTask task = taskFactory.getFindPrincipalsTask(this, pattern, type, progressMonitor);
			Activator.getDefault().getTaskManager().executeTaskSync(task);
			return task.getPrincipals();
		} else {
			ArrayList<IPrincipal> list = new ArrayList<IPrincipal>();
			list.add( creatorOwner );
			list.add( authenticatedUsers );
			return list;
		}
	}

	@Override
	public Collection<IPrincipal> findPrincipals(String pattern, PrincipalType type) throws ExecutionException {
		return findPrincipals(pattern, type, null);
	}

	@Override
	public IObjectStore getObjectStore() {
		return objectStore;
	}
	
	public com.filenet.api.security.Realm getInternalRealm() {
		return realm;
	}

	@Override
	public void store(IPrincipal principal, IMemento memento) {
		// TODO Auto-generated method stub
	}

	@Override
	public IPrincipal restore(IMemento memento) {
		// TODO Auto-generated method stub
		return null;
	}

	public Principal getPrincipal(AccessPermission permission) {
		
		String granteeName = permission.get_GranteeName();
		if ( !isSpecialAccountName(granteeName ) ) {
			if (permission.get_GranteeType().equals(SecurityPrincipalType.GROUP)) {
				return getGroupPrincipal(granteeName );
			} else if (permission.get_GranteeType().equals(SecurityPrincipalType.USER)) {
				return getUserPrincipal(granteeName );
			}
		} else {
			return new Principal(granteeName, PrincipalType.SPECIAL_ACCOUNT, this );
		}
		
		return null;
	}


	private Principal getUserPrincipal(String granteeName) {
		
		try {
			User user = Factory.User.fetchInstance(realm.getConnection(), granteeName, null);
			return new Principal( user, this );
		} catch (Exception e) {
			return null;
		}
	}

	private Principal getGroupPrincipal(String granteeName) {
		
		try {
			Group group = Factory.Group.fetchInstance(realm.getConnection(), granteeName, null);
			return new Principal( group, this );
		} catch (Exception e) {
			return null;
		}
	}

	private boolean isSpecialAccountName(String name) {
		return name.equals( com.filenet.api.constants.SpecialPrincipal.AUTHENTICATED_USERS.getValue() ) ||
				name.equals( com.filenet.api.constants.SpecialPrincipal.CREATOR_OWNER.getValue() );
	}

	@Override
	public Collection<IPrincipal> getMembers(IPrincipal principal) throws Exception {
		
		ITaskFactory taskFactory = objectStore.getTaskFactory();
		IGetMembersTask task = taskFactory.getGetMembersTask(principal);
		Activator.getDefault().getTaskManager().executeTaskSync(task);
		return task.getMembers();
	}

	@Override
	public Collection<IPrincipal> getMemberships(IPrincipal principal) throws Exception {
		ITaskFactory taskFactory = objectStore.getTaskFactory();
		IGetMembershipsTask task = taskFactory.getGetMembershipsTask(principal);
		Activator.getDefault().getTaskManager().executeTaskSync(task);
		return task.getMemberships();
	}
}
