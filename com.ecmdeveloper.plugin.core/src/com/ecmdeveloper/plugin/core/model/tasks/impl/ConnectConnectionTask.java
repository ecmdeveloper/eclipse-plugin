/**
 * Copyright 2011, Ricardo Belfor
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

package com.ecmdeveloper.plugin.core.model.tasks.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.ecmdeveloper.plugin.core.model.IConnection;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.IObjectStores;
import com.ecmdeveloper.plugin.core.model.tasks.AbstractTask;
import com.ecmdeveloper.plugin.core.model.tasks.IConnectConnectionTask;
import com.ecmdeveloper.plugin.core.model.tasks.TaskResult;

/**
 * @author ricardo.belfor
 *
 */
public class ConnectConnectionTask extends AbstractTask implements IConnectConnectionTask {

	private Object connectionName;
	private Map<String,IConnection> connections;
	private IObjectStores objectStores;
	private ArrayList<IObjectStoreItem> connectionObjectStores;

	@Override
	public Collection<IObjectStoreItem> getConnectionObjectStores() {
		return connectionObjectStores;
	}

	public ConnectConnectionTask(Object connectionName, Map<String,IConnection> connections, IObjectStores objectStores ) {
		this.connectionName = connectionName;
		this.connections = connections;
		this.objectStores = objectStores;
	}

	@Override
	public Object call() throws Exception {
		connections.get( connectionName ).connect();
		connectionObjectStores = new ArrayList<IObjectStoreItem>();
		
		for ( IObjectStore objectStoreItem : objectStores.getChildren() ) {
			
			IObjectStore objectStore = (IObjectStore) objectStoreItem;
			if ( objectStore.getConnection().getName().equals(connectionName)) {
				objectStore.connect();
				connectionObjectStores.add( objectStore );
			}
		}

		fireTaskCompleteEvent( TaskResult.COMPLETED );
		return null;
	}
}
