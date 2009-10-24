package com.ecmdeveloper.plugin.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

import com.ecmdeveloper.plugin.Activator;
import com.ecmdeveloper.plugin.util.PluginLog;
import com.ecmdeveloper.plugin.util.PluginTagNames;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.core.IndependentObject;
import com.filenet.api.core.ReferentialContainmentRelationship;

/**
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

	private ObjectStoresManager() {
		executorService = Executors.newSingleThreadExecutor();
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
//		return new String[0];
	}
	
	public String createConnection(String url, String username, String password, IProgressMonitor monitor ) 
	{
		try {
			
			if ( monitor != null ) {
	    		monitor.beginTask( MessageFormat.format( CONNECT_MESSAGE, url ), IProgressMonitor.UNKNOWN);
			}
			
			if ( connections == null) {
				loadObjectStores();
			}
			
			ContentEngineConnection objectStoreConnection = new ContentEngineConnection();
			
			objectStoreConnection.setUrl(url);
			objectStoreConnection.setUsername(username);
			objectStoreConnection.setPassword(password);
			objectStoreConnection.connect();
	
			connections.put( objectStoreConnection.getName(), objectStoreConnection);
			
			return objectStoreConnection.getName();
		} finally {
			if ( monitor != null ) {
				monitor.done();
			}
		}
	}
	
	public void loadChildren( final ObjectStoreItem objectStoreItem )
	{
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				ArrayList<IObjectStoreItem> children = new ArrayList<IObjectStoreItem>();
		
				com.filenet.api.core.Folder folder;
		
				if ( objectStoreItem instanceof ObjectStore ) {
					com.filenet.api.core.ObjectStore objectStore = (com.filenet.api.core.ObjectStore) objectStoreItem.getObjectStoreObject();
					objectStore.fetchProperties( new String[] { PropertyNames.ROOT_FOLDER } );
					folder = objectStore.get_RootFolder();
				} else if ( objectStoreItem instanceof Folder ) {
					folder = (com.filenet.api.core.Folder) objectStoreItem.getObjectStoreObject();
				} else {
					return;
				}
				
				folder.fetchProperties( new String[] { PropertyNames.CONTAINEES, PropertyNames.SUB_FOLDERS } );
				
				Iterator<?> iterator = folder.get_SubFolders().iterator();
				ObjectStore objectStore = objectStoreItem.getObjectStore();
				while (iterator.hasNext()) {
					children.add( new Folder( iterator.next(), objectStoreItem, objectStore ) );
				}
				
				iterator = folder.get_Containees().iterator();
				
				while (iterator.hasNext() ) {
					
					ReferentialContainmentRelationship relation = (ReferentialContainmentRelationship) iterator.next();
					relation.fetchProperties( new String[]{ PropertyNames.HEAD } );
		
					IndependentObject object = relation.get_Head();
		
					if ( object instanceof com.filenet.api.core.Document )
					{
						children.add( new Document( object, objectStoreItem, objectStore ) );
					}
					else if ( object instanceof com.filenet.api.core.Folder )
					{
						// TODO: mark this folder as a link instead of a regular child?
						children.add( new Folder( object, objectStoreItem, objectStore ) );
					}
					else if ( object instanceof com.filenet.api.core.CustomObject )
					{
						children.add( new CustomObject( object, objectStoreItem, objectStore ) );
					}
				}
				
//				ObjectStoreItem[] oldChildren = objectStoreItem.getChildren().toArray(new ObjectStoreItem[1] );
				objectStoreItem.setChildren(children);
				
//				fireObjectStoreItemsChanged(children.toArray(new ObjectStoreItem[0]), oldChildren, null );
				fireObjectStoreItemsChanged(null, null, new ObjectStoreItem[] { objectStoreItem} );
			}
		};
		
		executorService.execute(runnable);
	}
	
	public void connectConnection( final String connectionName, IProgressMonitor monitor ) {

		try {
			if ( monitor != null ) {
				monitor.beginTask( MessageFormat.format( CONNECT_MESSAGE, connectionName ), IProgressMonitor.UNKNOWN);
			}
	
			if ( connections.containsKey( connectionName ) ) {
	
				Runnable runnable = new Runnable() {
					@Override
					public void run() {
						connections.get( connectionName ).connect();
						ArrayList<IObjectStoreItem> connectionObjectStores = new ArrayList<IObjectStoreItem>();
						
						for ( IObjectStoreItem objectStoreItem : objectStores.getChildren() ) {
							
							ObjectStore objectStore = (ObjectStore) objectStoreItem;
							if ( objectStore.getConnection().getName().equals(connectionName)) {
								objectStore.connect();
								connectionObjectStores.add( objectStore );
							}
						}
			
						fireObjectStoreItemsChanged(null, null, connectionObjectStores.toArray( new IObjectStoreItem[0] ) );
					}
				};
	
				try {
					executorService.submit(runnable).get();
				} catch (ExecutionException e) {
					throw new RuntimeException(e);
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
	
	public void addObjectStore(String name, String connectionName)
	{
		if ( objectStores == null) {
			loadObjectStores();
		}
		
		ObjectStore objectStore = new ObjectStore( name, objectStores );
		objectStore.setConnection( connections.get( connectionName ) );
		objectStore.connect();
		objectStores.add(objectStore);
		
		fireObjectStoreItemsChanged( new IObjectStoreItem[] { objectStore }, null, null );
	}
	
	public void removeObjectStore(ObjectStore objectStore) 
	{
		if ( objectStores == null) {
			loadObjectStores();
		}
		
		objectStores.remove( objectStore );

		fireObjectStoreItemsChanged( null, new IObjectStoreItem[] { objectStore }, null );
	}

	public void connectObjectStore(ObjectStore objectStore, IProgressMonitor monitor )
	{
		if ( objectStore == null) {
			return;
		}
		
		if ( objectStore.isConnected() ) {
			return;
		}
		
		connectConnection(objectStore.getConnection().getName(), monitor );
	}

	public ObjectStores getObjectStores()
	{
		if ( objectStores == null) {
			loadObjectStores();
		}
		return objectStores;
	}

	public String[] getNewObjectstoreNames( String connectionName ) {

		String[] objectStoreNames = getObjectStoreNames(connectionName);
		ArrayList<String> newObjectStoreNames = new ArrayList<String>();

		for (String objectStoreName : objectStoreNames ) {
		
			boolean found = false;

			for ( IObjectStoreItem objectStore : objectStores.getChildren() ) {
			
				if (objectStore.getName().equals(objectStoreName)
						&& ((ObjectStore) objectStore).getConnection()
								.getName().equals(connectionName)) {
					found = true;
					break;
				}
			}
			
			if ( ! found ) {
				newObjectStoreNames.add( objectStoreName );
			}
		}
		
		return newObjectStoreNames.toArray( new String[0] );
	}
	
	public String[] getObjectStoreNames(String connectionName )
	{
		if ( objectStores == null) {
			loadObjectStores();
		}
		
		if ( ! connections.containsKey( connectionName ) )
		{
			return new String[0];
		}
		
		ContentEngineConnection connection = connections.get( connectionName );
		return connection.getObjectStoreNames();
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

	public void updateObjectStoreItems(IObjectStoreItem[] objectStoreItems, boolean delete ) {
		
		// TODO add updating batch support
		for (IObjectStoreItem objectStoreItem2 : objectStoreItems) {
			((ObjectStoreItem)objectStoreItem2).save();
		}

		if ( delete ) {
			fireObjectStoreItemsChanged(null, objectStoreItems, null );
		} else {
			fireObjectStoreItemsChanged(null, null, objectStoreItems );
		}
	}
	
	public void refreshObjectStoreItems(IObjectStoreItem[] objectStoreItems ) {
		fireObjectStoreItemsChanged(null, null, objectStoreItems );
	}

	public void moveObjectStoreItems( IObjectStoreItem[] objectStoreItems, IObjectStoreItem destination )
	{
		fireObjectStoreItemsChanged(null, objectStoreItems, null );

		Set<IObjectStoreItem> updateSet = new HashSet<IObjectStoreItem>();
		for (IObjectStoreItem objectStoreItem : objectStoreItems) {
			if ( objectStoreItem.getParent() != null ) {
				objectStoreItem.getParent().refresh();
				updateSet.add( objectStoreItem.getParent() );
			}
		}

		destination.refresh();
		updateSet.add( destination );
		
		fireObjectStoreItemsChanged(null, null, updateSet.toArray( new IObjectStoreItem[0]) );
	}
	
	private void saveObjectStores(XMLMemento memento) 
	{
		// First save the connection to the object store
		
		IMemento connectionsChild = memento.createChild(PluginTagNames.CONNECTIONS_TAG); 
		
		for ( ContentEngineConnection contentEngineConnection : connections.values() ) {
			
			IMemento connectionChild = connectionsChild.createChild(PluginTagNames.CONNECTION_TAG);
			
			connectionChild.putString( PluginTagNames.NAME_TAG, contentEngineConnection.getName() );
			connectionChild.putString( PluginTagNames.URL_TAG, contentEngineConnection.getUrl() );
			connectionChild.putString( PluginTagNames.USERNAME_TAG, contentEngineConnection.getUsername() );
			connectionChild.putString( PluginTagNames.PASSWORD_TAG, contentEngineConnection.getPassword() );
		}
		
		// Next save the object stores

		IMemento objectStoresChild = memento.createChild(PluginTagNames.OBJECT_STORES_TAG); 
		
		for ( IObjectStoreItem objectStore : objectStores.getChildren()) {
			
			IMemento objectStoreChild = objectStoresChild.createChild(PluginTagNames.OBJECT_STORE_TAG);
			
			objectStoreChild.putString( PluginTagNames.NAME_TAG, objectStore.getName() );
			objectStoreChild.putString( PluginTagNames.CONNECTION_NAME_TAG, ((ObjectStore)objectStore).getConnection().getName() );
		}
	}
	
	private void loadObjectStores(XMLMemento memento)
	{
		IMemento connectionsChild = memento.getChild(PluginTagNames.CONNECTIONS_TAG);
		if ( connectionsChild != null ) {
			for ( IMemento connectionChild : connectionsChild.getChildren(PluginTagNames.CONNECTION_TAG) ) {
				
				ContentEngineConnection contentEngineConnection = new ContentEngineConnection();
				
				contentEngineConnection.setName( connectionChild.getString( PluginTagNames.NAME_TAG ) );
				contentEngineConnection.setUrl( connectionChild.getString( PluginTagNames.URL_TAG ) );
				contentEngineConnection.setUsername( connectionChild.getString( PluginTagNames.USERNAME_TAG ) );
				contentEngineConnection.setPassword( connectionChild.getString( PluginTagNames.PASSWORD_TAG ) );
				
				connections.put(contentEngineConnection.getName(), contentEngineConnection );
			}
		}

		IMemento objectStoresChild = memento.getChild(PluginTagNames.OBJECT_STORES_TAG);
		if ( objectStoresChild != null )
		{
			for ( IMemento objectStoreChild : objectStoresChild.getChildren( PluginTagNames.OBJECT_STORE_TAG ) )
			{
				String name = objectStoreChild.getString( PluginTagNames.NAME_TAG );
				ObjectStore objectStore = new ObjectStore( name, objectStores );
				
				String connectionName = objectStoreChild.getString( PluginTagNames.CONNECTION_NAME_TAG );
				objectStore.setConnection( connections.get( connectionName ) );
				
				objectStores.add( objectStore );
			}
		}
	}

	private void loadObjectStores() {

		connections = new HashMap<String, ContentEngineConnection>();
		objectStores = new ObjectStores();
		
		FileReader reader = null;
		try {
			reader = new FileReader(getObjectStoresFile());
			loadObjectStores(XMLMemento.createReadRoot(reader));
		} catch (FileNotFoundException e) {
			// Ignored... no object store items exist yet.
		} catch (Exception e) {
			// Log the exception and move on.
			PluginLog.error(e);
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				PluginLog.error(e);
			}
		}
	}
	
	public void saveObjectStores()
	{
		if (connections == null) {
			return;
		}

		XMLMemento memento = XMLMemento.createWriteRoot(PluginTagNames.OBJECT_STORES_TAG);

		saveObjectStores(memento);
		FileWriter writer = null;
		try {
			writer = new FileWriter(getObjectStoresFile());
			memento.save(writer);
		} catch (IOException e) {
			PluginLog.error(e);
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				PluginLog.error(e);
			}
		}
	 		
	}
	
	private File getObjectStoresFile()
	{
		return Activator.getDefault().getStateLocation().append("objectstores.xml").toFile();
	}

	public void addObjectStoresManagerListener( ObjectStoresManagerListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void removeObjectStoresManagerListener( ObjectStoresManagerListener listener) {
		listeners.remove(listener);
	}

	private void fireObjectStoreItemsChanged(IObjectStoreItem[] itemsAdded,
			IObjectStoreItem[] itemsRemoved, IObjectStoreItem[] itemsUpdated ) {
		ObjectStoresManagerEvent event = new ObjectStoresManagerEvent(this,
				itemsAdded, itemsRemoved, itemsUpdated );
		for (ObjectStoresManagerListener listener : listeners) {
			listener.objectStoreItemsChanged(event);
		}
	}
}