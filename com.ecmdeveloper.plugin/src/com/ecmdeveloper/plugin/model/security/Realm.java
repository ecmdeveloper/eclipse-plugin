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

import org.eclipse.core.runtime.IProgressMonitor;

import com.ecmdeveloper.plugin.Activator;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.constants.PrincipalType;
import com.ecmdeveloper.plugin.core.model.security.IPrincipal;
import com.ecmdeveloper.plugin.core.model.security.IRealm;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskFactory;
import com.ecmdeveloper.plugin.core.model.tasks.security.IFindPrincipalsTask;

/**
 * @author ricardo.belfor
 *
 */
public class Realm implements IRealm {

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
}
