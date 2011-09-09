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
package com.ecmdeveloper.plugin.core.model.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ecmdeveloper.plugin.core.Activator;
import com.ecmdeveloper.plugin.core.model.IConnection;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.IObjectStores;
import com.ecmdeveloper.plugin.core.model.IObjectStoresManager;
import com.ecmdeveloper.plugin.core.model.IObjectStoresStore;
import com.ecmdeveloper.plugin.core.model.tasks.IAddObjectStoreTask;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskManager;
import com.ecmdeveloper.plugin.core.model.tasks.impl.AddObjectStoreTask;
import com.ecmdeveloper.plugin.core.model.tasks.impl.ConnectConnectionTask;
import com.ecmdeveloper.plugin.core.model.tasks.impl.ConnectNewConnectionTask;
import com.ecmdeveloper.plugin.core.model.tasks.impl.GetObjectStoresTask;

/**
 * This class manages the connections to the Object Stores. It is also
 * responsible for executing the different tasks on the object store items.
 * 
 * @author Ricardo Belfor
 * 
 */
public class ObjectStoresManager implements IObjectStoresManager
{
	private static final String CONNECT_MESSAGE = "Connecting to \"{0}\"";

	private static ObjectStoresManager objectStoresManager;
	
	private Map<String,IConnection> connections;
	private IObjectStores objectStores;
	private Collection<IObjectStoresStore> objectStoresStoreList;
	private ITaskManager taskManager;
	
	private ObjectStoresManager() {
		objectStoresStoreList = new ArrayList<IObjectStoresStore>();
		objectStores = new ObjectStores();
		taskManager = (ITaskManager) Activator.getDefault().getWorkbench().getService(ITaskManager.class);
		connections = new HashMap<String, IConnection>();
	}
	
	public static ObjectStoresManager getManager()
	{
		if ( objectStoresManager == null )
		{
			objectStoresManager = new ObjectStoresManager();
		}
		return objectStoresManager;
	}

	@Override
	public Collection<IConnection> getConnections() {
		return connections.values();
	}
	
	public void saveObjectStores() {
		for ( IObjectStoresStore objectStoresStore : objectStoresStoreList) {
			objectStoresStore.save(objectStores, connections);
		}
	}

	@Override
	public String connectNewConnection(IConnection connection, IProgressMonitor monitor ) throws ExecutionException 
	{
		try {
			
			if ( monitor != null ) {
	    		monitor.beginTask( MessageFormat.format( CONNECT_MESSAGE, connection.getUrl() ), IProgressMonitor.UNKNOWN);
			}
			
			ConnectNewConnectionTask task = new ConnectNewConnectionTask(connection);

			taskManager.executeTaskSync(task);
			connections.put( connection.getName(), connection);
			return connection.getName();

		} finally {
			if ( monitor != null ) {
				monitor.done();
			}
		}
	}
	
	@Override
	public void connectConnection( IConnection connection,IProgressMonitor monitor ) throws ExecutionException {

		try {
			
			String connectionName = connection.getName();
			if ( monitor != null ) {
				monitor.beginTask( MessageFormat.format( CONNECT_MESSAGE, connection.getDisplayName() ), IProgressMonitor.UNKNOWN);
			}
	
			if ( connections.containsKey( connectionName ) ) {
				ConnectConnectionTask task = new ConnectConnectionTask(connectionName, connections, objectStores );
				taskManager.executeTaskSync(task);
			} else {
				throw new UnsupportedOperationException( "Invalid connection name '" + connectionName + "'" );
			}
		} finally {
			if ( monitor != null ) {
				monitor.done();
			}
		}
	}
	
	@Override
	public void addObjectStore( IObjectStore objectStore )
	{
		objectStores.add(objectStore);
		saveObjectStores();
		IAddObjectStoreTask task = new AddObjectStoreTask(objectStore);
		taskManager.executeTaskASync(task);
	}

	@Override
	public void removeObjectStore(IObjectStore objectStore) 
	{
		objectStores.remove( objectStore );
		
		String connectionName = objectStore.getConnection().getName();
		boolean found = false;
		for (IObjectStore childObjectStore : objectStores.getChildren() ) {
			String objectStoreConnectionName = childObjectStore.getConnection().getName();
			if ( objectStoreConnectionName.equals(connectionName ) ) {
				found = true;
				break;
			}
		}
			
		if ( ! found ) {
			connections.remove( connectionName );
		}

		saveObjectStores();
		
		taskManager.fireObjectStoreItemsChanged( null, new IObjectStoreItem[] { objectStore }, null );
	}

	@Override
	public void connectObjectStore(IObjectStore objectStore, IProgressMonitor monitor ) throws ExecutionException
	{
		if ( objectStore == null) {
			return;
		}
		
		if ( objectStore.isConnected() ) {
			return;
		}
		
		connectConnection(objectStore.getConnection(), monitor );
	}

	@Override
	public IObjectStores getObjectStores() {
		return objectStores;
	}

	@Override
	public IObjectStore[] getNewObjectstores( String connectionName ) {

		IObjectStore[] objectStores2 = getObjectStores(connectionName);
		ArrayList<IObjectStore> newObjectStores = new ArrayList<IObjectStore>();

		for (IObjectStore objectStore2 : objectStores2 ) {
		
			boolean found = false;

			for ( IObjectStoreItem objectStore : objectStores.getChildren() ) {
			
				if (objectStore.getName().equals( objectStore2.getName() )
						&& ((IObjectStore) objectStore).getConnection()
								.getName().equals(connectionName)) {
					found = true;
					break;
				}
			}
			
			if ( ! found ) {
				newObjectStores.add( objectStore2 );
			}
		}
		
		return newObjectStores.toArray( new IObjectStore[0] );
	}
	
	private IObjectStore[] getObjectStores(String connectionName )
	{
		if ( ! connections.containsKey( connectionName ) )
		{
			return new IObjectStore[0];
		}
		
		final IConnection connection = connections.get( connectionName );
		GetObjectStoresTask task = new GetObjectStoresTask(connection, objectStores);
		
		try {
			taskManager.executeTaskSync( task );
			return task.getObjectStores();
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public IObjectStore getObjectStore(String connectionName, String objectStoreName) {

		for (IObjectStore objectStore : objectStores.getChildren() )
		{
			if ( objectStore.getName().equals(objectStoreName) &&
					objectStore.getConnection().getName().equals(connectionName ) )
			{
				return objectStore;
			}
		}
		return null;
	}

	@Override
	public void deregisterObjectStoresStore(IObjectStoresStore objectStoresStore) {
		objectStoresStoreList.remove(objectStoresStore);
	}

	@Override
	public void registerObjectStoresStore(IObjectStoresStore objectStoresStore) {
		objectStoresStoreList.add(objectStoresStore);
		objectStoresStore.load(objectStores, connections);
		IObjectStoreItem[] itemsAdded = objectStores.getChildren().toArray( new IObjectStoreItem[0] );
		taskManager.fireObjectStoreItemsChanged(itemsAdded , null, null);
	}
}