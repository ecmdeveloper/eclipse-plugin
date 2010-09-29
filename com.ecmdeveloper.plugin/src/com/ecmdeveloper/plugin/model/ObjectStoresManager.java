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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ecmdeveloper.plugin.model.tasks.BaseTask;

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
	
	protected Map<String,ContentEngineConnection> connections;
	protected ObjectStores objectStores;

	private List<ObjectStoresManagerListener> listeners = new ArrayList<ObjectStoresManagerListener>();
	
	private ExecutorService executorService;
	private ObjectStoresStore objectStoresStore;
	private ObjectStoreItemsModelController modelController;
	
	private ObjectStoresManager() {
		executorService = Executors.newSingleThreadExecutor();
		objectStoresStore = new ObjectStoresStore();
		modelController = new ObjectStoreItemsModelController();
	}
	
	public static ObjectStoresManager getManager()
	{
		if ( objectStoresManager == null )
		{
			objectStoresManager = new ObjectStoresManager();
		}
		return objectStoresManager;
	}

	public Collection<ContentEngineConnection> getConnections() {

		if ( connections == null) {
			loadObjectStores();
		}
		
		return connections.values();
	}
	
	private void loadObjectStores() {
		connections = new HashMap<String, ContentEngineConnection>();
		objectStores = new ObjectStores();
		objectStoresStore.load(objectStores, connections);
	}

	public void saveObjectStores() {
		objectStoresStore.save(objectStores, connections);
	}

	public String createConnection(String url, String username, String password, IProgressMonitor monitor ) throws ExecutionException 
	{
		try {
			
			if ( monitor != null ) {
	    		monitor.beginTask( MessageFormat.format( CONNECT_MESSAGE, url ), IProgressMonitor.UNKNOWN);
			}
			
			if ( connections == null) {
				loadObjectStores();
			}

			CreateConnectionTask createConnection = new CreateConnectionTask(url, username, password );
			
			try {
				return executorService.submit(createConnection).get();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		} finally {
			if ( monitor != null ) {
				monitor.done();
			}
		}
	}
	
	public void connectConnection( ContentEngineConnection connection,IProgressMonitor monitor ) throws ExecutionException {

		try {
			
			String connectionName = connection.getName();
			if ( monitor != null ) {
				monitor.beginTask( MessageFormat.format( CONNECT_MESSAGE, connection.getDisplayName() ), IProgressMonitor.UNKNOWN);
			}
	
			if ( connections.containsKey( connectionName ) ) {
	
				ConnectConnectionTask callable = new ConnectConnectionTask(connectionName);
				try {
					executorService.submit(callable).get();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				
			} else {
				throw new UnsupportedOperationException( "Invalid connection name '" + connectionName + "'" );
			}
		} finally {
			if ( monitor != null ) {
				monitor.done();
			}
		}
	}
	
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
				modelController.fireObjectStoreItemsChanged( new IObjectStoreItem[] { objectStore }, null, null );
				return null;
			}
		};
		
		executorService.submit(callable);
	}

	/**
	 * Removes the Object Store from the list of configured Object Stores. If
	 * there are no more Object Stores using the same connection then the
	 * connection is also removed.
	 * 
	 * @param objectStore the object store
	 */
	public void removeObjectStore(ObjectStore objectStore) 
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

		modelController.fireObjectStoreItemsChanged( null, new IObjectStoreItem[] { objectStore }, null );
	}

	public void connectObjectStore(ObjectStore objectStore, IProgressMonitor monitor ) throws ExecutionException
	{
		if ( objectStore == null) {
			return;
		}
		
		if ( objectStore.isConnected() ) {
			return;
		}
		
		connectConnection(objectStore.getConnection(), monitor );
	}

	public ObjectStores getObjectStores()
	{
		if ( objectStores == null) {
			loadObjectStores();
		}
		return objectStores;
	}

	public ObjectStore[] getNewObjectstores( String connectionName ) {

		ObjectStore[] objectStores2 = getObjectStores(connectionName);
		ArrayList<ObjectStore> newObjectStores = new ArrayList<ObjectStore>();

		for (ObjectStore objectStore2 : objectStores2 ) {
		
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
	
	public ObjectStore[] getObjectStores(String connectionName )
	{
		if ( objectStores == null) {
			loadObjectStores();
		}
		
		if ( ! connections.containsKey( connectionName ) )
		{
			return new ObjectStore[0];
		}
		
		final ContentEngineConnection connection = connections.get( connectionName );

		Callable<ObjectStore[]> a = new Callable<ObjectStore[]>(){

			@Override
			public ObjectStore[] call() throws Exception {
				return connection.getObjectStores(objectStores);			}
		};
		
		try {
			return executorService.submit( a ).get();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	public ObjectStore getObjectStore(String connectionName, String objectStoreName) {

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

	public Object executeTaskSync( Callable<Object> task ) throws ExecutionException
	{
		try {
			if ( task instanceof BaseTask ) {
				((BaseTask)task).addTaskListener(modelController);
			}
			return executorService.submit(task).get();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} 
	}

	public void executeTaskASync( Callable<Object> task )
	{
		if ( task instanceof BaseTask ) {
			((BaseTask)task).addTaskListener(modelController);
		}
		executorService.submit(task);
	}
	
	public void moveObjectStoreItems( IObjectStoreItem[] objectStoreItems, IObjectStoreItem destination )
	{
		modelController.fireObjectStoreItemsChanged(null, objectStoreItems, null );

		Set<IObjectStoreItem> updateSet = new HashSet<IObjectStoreItem>();
		for (IObjectStoreItem objectStoreItem : objectStoreItems) {
			if ( objectStoreItem.getParent() != null ) {
				objectStoreItem.getParent().refresh();
				updateSet.add( objectStoreItem.getParent() );
			}
		}

		destination.refresh();
		updateSet.add( destination );
		
		modelController.fireObjectStoreItemsChanged(null, null, updateSet.toArray( new IObjectStoreItem[0]) );
	}
	
	public void addObjectStoresManagerListener( ObjectStoresManagerListener listener) {
		modelController.addObjectStoresManagerListener(listener);
	}

	public void removeObjectStoresManagerListener( ObjectStoresManagerListener listener) {
		modelController.removeObjectStoresManagerListener(listener);
	}

	class ConnectConnectionTask implements Callable<Object> {
		private Object connectionName;

		public ConnectConnectionTask(Object connectionName) {
			super();
			this.connectionName = connectionName;
		}

		@Override
		public Object call() throws Exception {
			connections.get( connectionName ).connect();
			ArrayList<IObjectStoreItem> connectionObjectStores = new ArrayList<IObjectStoreItem>();
			
			for ( IObjectStoreItem objectStoreItem : objectStores.getChildren() ) {
				
				ObjectStore objectStore = (ObjectStore) objectStoreItem;
				if ( objectStore.getConnection().getName().equals(connectionName)) {
					objectStore.connect();
					connectionObjectStores.add( objectStore );
				}
			}

			modelController.fireObjectStoreItemsChanged(null, null, connectionObjectStores.toArray( new IObjectStoreItem[0] ) );
			return null;
		}
	}
	
	class CreateConnectionTask implements Callable<String> {

		private String url;
		private String username;
		private String password;

		public CreateConnectionTask(String url, String username, String password) {
			super();
			this.url = url;
			this.username = username;
			this.password = password;
		}

		@Override
		public String call() throws Exception {

			ContentEngineConnection objectStoreConnection = new ContentEngineConnection();
			
			objectStoreConnection.setUrl(url);
			objectStoreConnection.setUsername(username);
			objectStoreConnection.setPassword(password);
			objectStoreConnection.connect();
	
			connections.put( objectStoreConnection.getName(), objectStoreConnection);
			
			return objectStoreConnection.getName();
		}
	}
}