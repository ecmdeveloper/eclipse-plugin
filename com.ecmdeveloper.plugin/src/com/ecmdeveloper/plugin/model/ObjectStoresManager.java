package com.ecmdeveloper.plugin.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

import com.ecmdeveloper.plugin.Activator;
import com.ecmdeveloper.plugin.util.PluginLog;
import com.ecmdeveloper.plugin.util.PluginTagNames;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class ObjectStoresManager implements IObjectStoresManager
{
	private static ObjectStoresManager objectStoresManager;
	
	protected Map<String,ContentEngineConnection> connections;
	protected ObjectStores objectStores;

	private List<ObjectStoresManagerListener> listeners = new ArrayList<ObjectStoresManagerListener>();
	
	private ObjectStoresManager() {}
	
	public static ObjectStoresManager getManager()
	{
		if ( objectStoresManager == null )
		{
			objectStoresManager = new ObjectStoresManager();
		}
		return objectStoresManager;
	}

	public String createConnection(String url, String username, String password) 
	{
		if ( connections == null) {
			loadObjectStores();
		}
		
		ContentEngineConnection objectStoreConnection = new ContentEngineConnection();
		
		objectStoreConnection.setUrl(url);
		objectStoreConnection.setUsername(username);
		objectStoreConnection.setPassword(password);
		objectStoreConnection.connect();
				
		PluginLog.info("Connecting to " + url + " with " + username);

		connections.put( objectStoreConnection.getName(), objectStoreConnection);
		
		return objectStoreConnection.getName();
	}
	
	public void addObjectStore(String name, String connectionName)
	{
		if ( objectStores == null) {
			loadObjectStores();
		}
		
		ObjectStore objectStore = new ObjectStore( name, objectStores );
		objectStore.setConnection( connections.get( connectionName ) );
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

	public void connectObjectStore(ObjectStore objectStore)
	{
		if ( objectStore == null) {
			return;
		}
		
		objectStore.connect();
		
		fireObjectStoreItemsChanged(null, null, new IObjectStoreItem[] { objectStore } );
	}

	public ObjectStores getObjectStores()
	{
		if ( objectStores == null) {
			loadObjectStores();
		}
		return objectStores;
	}

	public String[] getObjectStores(String connectionName )
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