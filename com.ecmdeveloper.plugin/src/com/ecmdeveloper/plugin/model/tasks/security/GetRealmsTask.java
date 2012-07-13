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
import java.util.HashSet;
import java.util.Iterator;

import com.ecmdeveloper.plugin.core.model.security.IRealm;
import com.ecmdeveloper.plugin.core.model.tasks.security.IGetRealmsTask;
import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.security.Realm;
import com.ecmdeveloper.plugin.model.tasks.BaseTask;
import com.filenet.api.collection.RealmSet;
import com.filenet.api.core.Factory;

/**
 * @author ricardo.belfor
 *
 */
public class GetRealmsTask extends BaseTask implements IGetRealmsTask {

	private final ObjectStore objectStore;
	private final Collection<IRealm> realms = new HashSet<IRealm>();
	
	public GetRealmsTask(ObjectStore objectStore) {
		this.objectStore = objectStore;
	}

	@Override
	public Collection<IRealm> getRealms() {
		return realms;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object execute() throws Exception {

		com.filenet.api.core.ObjectStore internalObjectStore = (com.filenet.api.core.ObjectStore) objectStore
		.getObjectStoreObject();
		
		RealmSet realmSet = Factory.Realm.fetchAll(internalObjectStore.getConnection(), null);
		Iterator<com.filenet.api.security.Realm> iterator = realmSet.iterator();
		while ( iterator.hasNext()) {
			realms.add( new Realm( iterator.next(), objectStore ) );
		}
		return null;
	}

	@Override
	protected ContentEngineConnection getContentEngineConnection() {
		return objectStore.getConnection();
	}
}
