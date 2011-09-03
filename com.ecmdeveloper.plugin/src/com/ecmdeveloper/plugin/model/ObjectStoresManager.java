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
package com.ecmdeveloper.plugin.model;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ecmdeveloper.plugin.Activator;
import com.ecmdeveloper.plugin.core.model.IConnection;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.IObjectStores;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskManager;
import com.ecmdeveloper.plugin.model.tasks.ConnectConnectionTask;
import com.ecmdeveloper.plugin.model.tasks.CreateConnectionTask;
import com.ecmdeveloper.plugin.model.tasks.GetObjectStoresTask;

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
	private ObjectStoresStore objectStoresStore;
	private ITaskManager taskManager;
	
	private ObjectStoresManager() {
		objectStoresStore = new ObjectStoresStore();
		taskManager = (ITaskManager) Activator.getDefault().getWorkbench().getService(ITaskManager.class);
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

		if ( connections == null) {
			loadObjectStores();
		}
		
		return connections.values();
	}
	
	private void loadObjectStores() {
		connections = new HashMap<String, IConnection>();
		objectStores = new ObjectStores();
		objectStoresStore.load(objectStores, connections);
	}

	public void saveObjectStores() {
		objectStoresStore.save(objectStores, connections);
	}

	@Override
	public String createConnection(String url, String username, String password, IProgressMonitor monitor ) throws ExecutionException 
	{
		try {
			
			if ( monitor != null ) {
	    		monitor.beginTask( MessageFormat.format( CONNECT_MESSAGE, url ), IProgressMonitor.UNKNOWN);
			}
			
			if ( connections == null) {
				loadObjectStores();
			}

			CreateConnectionTask task = new CreateConnectionTask(url, username, password );

			taskManager.executeTaskSync(task);
			ContentEngineConnection objectStoreConnection = task.getObjectStoreConnection();
			connections.put( objectStoreConnection.getName(), objectStoreConnection);
			return objectStoreConnection.getName();

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
	public void addObjectStore( final ObjectStore objectStore )
	{
		if ( objectStores == null) {
			loadObjectStores();
		}
		
		Callable<Object> callable = new Callable<Object>(){

			@Override
			public Object call() throws Exception {
				objectStore.connect();
				objectStores.add(objectStore);
				taskManager.fireObjectStoreItemsChanged( new IObjectStoreItem[] { objectStore }, null, null );
				return null;
			}
		};
		
		taskManager.executeTaskASync(callable);
	}

	@Override
	public void removeObjectStore(IObjectStore objectStore) 
	{
		if ( objectStores == null) {
			loadObjectStores();
		}
		
		objectStores.remove( objectStore );
		
		String connectionName = objectStore.getConnection().getName();
		boolean found = false;
		for (IObjectStoreItem objectStoreItem : objectStores.getChildren() ) {
			String objectStoreConnectionName = ((ObjectStore)objectStoreItem).getConnection().getName();
			if ( objectStoreConnectionName.equals(connectionName ) ) {
				found = true;
				break;
			}
		}
			
		if ( ! found ) {
			connections.remove( connectionName );
		}

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
	public IObjectStores getObjectStores()
	{
		if ( objectStores == null) {
			loadObjectStores();
		}
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
						&& ((ObjectStore) objectStore).getConnection()
								.getName().equals(connectionName)) {
					found = true;
					break;
				}
			}
			
			if ( ! found ) {
				newObjectStores.add( objectStore2 );
			}
		}
		
		return newObjectStores.toArray( new ObjectStore[0] );
	}
	
	private IObjectStore[] getObjectStores(String connectionName )
	{
		if ( objectStores == null) {
			loadObjectStores();
		}
		
		if ( ! connections.containsKey( connectionName ) )
		{
			return new ObjectStore[0];
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

		if ( objectStores == null) {
			loadObjectStores();
		}
		
		for (IObjectStoreItem objectStoreItem : objectStores.getChildren() )
		{
			ObjectStore objectStore = (ObjectStore) objectStoreItem;
			if ( objectStore.getName().equals(objectStoreName) &&
					objectStore.getConnection().getName().equals(connectionName ) )
			{
				return objectStore;
			}
		}
		return null;
	}
}