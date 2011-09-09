/**
 * Copyright 2009, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in is
 * free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ECM Developer plug-in. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 */
package com.ecmdeveloper.plugin.core.model;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ecmdeveloper.plugin.core.model.IConnection;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.IObjectStores;

public interface IObjectStoresManager {

	Collection<IConnection> getConnections();

	void connectConnection(IConnection connection, IProgressMonitor monitor)
			throws ExecutionException;

	String connectNewConnection(IConnection connection, IProgressMonitor monitor)
	throws ExecutionException;

	void addObjectStore(IObjectStore objectStore);

	/**
	 * Removes the Object Store from the list of configured Object Stores. If
	 * there are no more Object Stores using the same connection then the
	 * connection is also removed.
	 * 
	 * @param objectStore the object store
	 */
	void removeObjectStore(IObjectStore objectStore);

	void connectObjectStore(IObjectStore objectStore, IProgressMonitor monitor)
			throws ExecutionException;

	IObjectStores getObjectStores();

	IObjectStore[] getNewObjectstores(String connectionName);

	IObjectStore getObjectStore(String connectionName, String objectStoreName);
	
	void registerObjectStoresStore(IObjectStoresStore objectStoresStore);

	void deregisterObjectStoresStore(IObjectStoresStore objectStoresStore);
}
