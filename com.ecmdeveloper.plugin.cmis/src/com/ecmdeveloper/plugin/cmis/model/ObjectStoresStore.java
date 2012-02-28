/**
 * Copyright 2010, Ricardo Belfor
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

package com.ecmdeveloper.plugin.cmis.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

import com.ecmdeveloper.plugin.cmis.Activator;
import com.ecmdeveloper.plugin.cmis.util.PluginLog;
import com.ecmdeveloper.plugin.cmis.util.PluginTagNames;
import com.ecmdeveloper.plugin.core.model.IConnection;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.IObjectStores;
import com.ecmdeveloper.plugin.core.model.IObjectStoresStore;

/**
 * @author ricardo.belfor
 *
 */
public class ObjectStoresStore implements IObjectStoresStore {

	private static final int CURRENT_FILE_VERSION = 1;

	@Override
	public boolean isSupportedObjectStore(IObjectStore objectStore) {
		return objectStore instanceof ObjectStore;
	}
	
	@Override
	public boolean isSupportedConnection(IConnection connection) {
		return connection instanceof Connection;
	}
	
	@Override
	public Collection<IObjectStore> load( Map<String, IConnection> connections) {

		FileReader reader = null;
		try {
			reader = new FileReader(getObjectStoresFile());
			XMLMemento memento = XMLMemento.createReadRoot(reader);
			loadConnections(memento, connections);
			return loadObjectStores(memento, connections);
		} catch (FileNotFoundException e) {
			return new ArrayList<IObjectStore>();
		} catch (Exception e) {
			PluginLog.error(e);
			return new ArrayList<IObjectStore>();
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				PluginLog.error(e);
			}
		}
	}

	private void loadConnections(XMLMemento memento, Map<String,IConnection> connections) {
		IMemento connectionsChild = memento.getChild(PluginTagNames.CONNECTIONS_TAG);
		if ( connectionsChild != null ) {
			for ( IMemento connectionChild : connectionsChild.getChildren(PluginTagNames.CONNECTION_TAG) ) {
				IConnection connection = loadConnection(connectionChild);
				connections.put(connection.getName(), connection );
			}
		}
	}

	private Collection<IObjectStore> loadObjectStores(XMLMemento memento, Map<String, IConnection> connections) {
		IMemento objectStoresChild = memento.getChild(PluginTagNames.OBJECT_STORES_TAG);
		Collection<IObjectStore> objectStoresList = new ArrayList<IObjectStore>();
		if ( objectStoresChild != null )
		{
			for ( IMemento objectStoreChild : objectStoresChild.getChildren( PluginTagNames.OBJECT_STORE_TAG ) )
			{
				objectStoresList.add( loadObjectStore(objectStoreChild, connections) );
			}
		}
		return objectStoresList;
	}

	private ObjectStore loadObjectStore(IMemento objectStoreChild,
			Map<String, IConnection> connections) {
		
		String connectionName = objectStoreChild.getString( PluginTagNames.CONNECTION_NAME_TAG );
		IConnection connection = connections.get( connectionName );
		
		String name = objectStoreChild.getString( PluginTagNames.NAME_TAG );
		ObjectStore objectStore = new ObjectStore( name, (Connection) connection );

		String displayName = objectStoreChild.getString( PluginTagNames.DISPLAY_NAME_TAG );
		objectStore.setDisplayName(displayName);
		
		return objectStore;
	}

	private IConnection loadConnection(IMemento connectionChild) {
		
		Connection connection = new Connection();
		
		connection.setName( connectionChild.getString( PluginTagNames.NAME_TAG ) );
		connection.setDisplayName( connectionChild.getString( PluginTagNames.DISPLAY_NAME_TAG ) );
		connection.setUrl( connectionChild.getString( PluginTagNames.URL_TAG ) );
		connection.setUsername( connectionChild.getString( PluginTagNames.USERNAME_TAG ) );
		String bindingTypeValue = connectionChild.getString( PluginTagNames.BINDING_TYPE_TAG  );
		connection.setBindingType( BindingType.valueOf( bindingTypeValue ) );
		String authenticationValue = connectionChild.getString( PluginTagNames.AUTHENTICATION_TAG );
		connection.setAuthentication( Authentication.valueOf( authenticationValue ) );
		connection.setUseCompression( connectionChild.getBoolean( PluginTagNames.USE_COMPRESSION_TAG ) );
		connection.setUseClientCompression( connectionChild.getBoolean( PluginTagNames.USE_CLIENT_COMPRESSION_TAG ) );
		connection.setUseCookies( connectionChild.getBoolean( PluginTagNames.USE_COOKIES_TAG ) );

		Boolean storePassword = connectionChild.getBoolean( PluginTagNames.STORE_PASSWORD_TAG );
		connection.setStorePassword( storePassword == null || storePassword );
		if ( connection.isStorePassword() ) {
			connection.setPassword( connectionChild.getString( PluginTagNames.PASSWORD_TAG ) );
		}
				
		return connection;
	}

	@Override
	public void save(IObjectStores objectStores, Map<String, IConnection> connections)
	{
		if (connections == null) {
			return;
		}

		XMLMemento memento = XMLMemento.createWriteRoot(PluginTagNames.OBJECT_STORES_TAG);
		memento.putInteger(PluginTagNames.VERSION_TAG, CURRENT_FILE_VERSION );

		saveConnections(memento, connections);
		saveObjectStores(memento, objectStores);

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

	private void saveObjectStores(XMLMemento memento, IObjectStores objectStores) {
		IMemento objectStoresChild = memento.createChild(PluginTagNames.OBJECT_STORES_TAG); 
		
		for ( IObjectStore objectStore : objectStores.getChildren()) {
			if ( isSupportedObjectStore(objectStore)) {
				saveObjectStore(objectStoresChild, objectStore);
			}
		}
	}

	private void saveConnections(XMLMemento memento, Map<String, IConnection> connections) {

		IMemento connectionsChild = memento.createChild(PluginTagNames.CONNECTIONS_TAG); 
		
		for ( IConnection connection : connections.values() ) {
			if ( isSupportedConnection(connection)) {
				saveConnection(connectionsChild, (Connection) connection);
			}
		}
	}

	private void saveConnection(IMemento connectionsChild, Connection connection) {
		
		IMemento connectionChild = connectionsChild.createChild(PluginTagNames.CONNECTION_TAG);
		
		connectionChild.putString( PluginTagNames.NAME_TAG, connection.getName() );
		connectionChild.putString( PluginTagNames.DISPLAY_NAME_TAG, connection.getDisplayName() );
		connectionChild.putString( PluginTagNames.URL_TAG, connection.getUrl() );
		connectionChild.putString( PluginTagNames.USERNAME_TAG, connection.getUsername() );
		connectionChild.putString( PluginTagNames.BINDING_TYPE_TAG, connection.getBindingType().name() );
		connectionChild.putString( PluginTagNames.AUTHENTICATION_TAG, connection.getAuthentication().name() );
		connectionChild.putBoolean( PluginTagNames.USE_COMPRESSION_TAG, connection.isUseCompression() );
		connectionChild.putBoolean( PluginTagNames.USE_CLIENT_COMPRESSION_TAG, connection.isUseClientCompression() );
		connectionChild.putBoolean( PluginTagNames.USE_COOKIES_TAG, connection.isUseCookies() );
		
		connectionChild.putBoolean( PluginTagNames.STORE_PASSWORD_TAG, connection.isStorePassword() );
		if ( connection.isStorePassword() ) {
			String password = connection.getPassword();
			connectionChild.putString( PluginTagNames.PASSWORD_TAG, password );
		}
	}

	private void saveObjectStore(IMemento objectStoresChild, IObjectStoreItem objectStore) {
		IMemento objectStoreChild = objectStoresChild.createChild(PluginTagNames.OBJECT_STORE_TAG);
		
		objectStoreChild.putString( PluginTagNames.NAME_TAG, objectStore.getName() );
		objectStoreChild.putString( PluginTagNames.DISPLAY_NAME_TAG, objectStore.getDisplayName() );
		objectStoreChild.putString( PluginTagNames.CONNECTION_NAME_TAG, ((ObjectStore)objectStore).getConnection().getName() );
	}
}
