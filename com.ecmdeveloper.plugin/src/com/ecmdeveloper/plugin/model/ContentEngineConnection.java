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

import java.util.ArrayList;
import java.util.Iterator;

import javax.security.auth.Subject;

import com.ecmdeveloper.plugin.core.model.IConnection;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.IObjectStores;
import com.filenet.api.collection.ObjectStoreSet;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.EntireNetwork;
import com.filenet.api.core.Factory;
import com.filenet.api.util.UserContext;

/**
 * This class represents a connection to the Content Engine.
 * 
 * @author Ricardo Belfor
 *
 */
public class ContentEngineConnection implements IConnection
{
	private String url;
	private String username;
	private String password;
	private String name;
	private String displayName;
	
	private Connection connection;
	private Domain domain;
	private Subject subject;
	
	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	private boolean connected;
	
	public ContentEngineConnection() {
		connected = false;
	}
	
	@Override
	public void connect() {
		
		if ( ! connected ) {
			connection = Factory.Connection.getConnection(url);
	
			subject = UserContext.createSubject(connection, username, password, null );
			UserContext uc = UserContext.get();
			uc.pushSubject(subject);
			
			EntireNetwork entireNetwork = Factory.EntireNetwork.fetchInstance(connection, null);
			domain = entireNetwork.get_LocalDomain();
			
			name = domain.get_Name();
			if ( displayName == null ) {
				displayName = domain.get_Name();
			}
			
			connected = true;
		}
	}

	@Override
	public void disconnect() {
		connection = null;
		subject = null;
		domain = null;
		connected = false;
	}

	@Override
	public boolean isConnected() {
		return connected;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public Subject getSubject() {
		return subject;
	}

	@SuppressWarnings("unchecked")
	public IObjectStore[] getObjectStores(IObjectStores parent )
	{
		ObjectStoreSet objectStores = domain.get_ObjectStores();
		Iterator iterator = objectStores.iterator();
		ArrayList<IObjectStore> objectStoreList = new ArrayList<IObjectStore>();
		
		while (iterator.hasNext()) 
		{
			com.filenet.api.core.ObjectStore objectStore = (com.filenet.api.core.ObjectStore) iterator.next();

			objectStore.fetchProperties( new String[] { PropertyNames.SYMBOLIC_NAME, PropertyNames.DISPLAY_NAME } );
			ObjectStore os =  new ObjectStore(objectStore.get_SymbolicName(), objectStore.get_DisplayName() );
			os.setConnection( this );
			objectStoreList.add( os ); 
		}

		return objectStoreList.toArray( new IObjectStore[0] );
	}
	
	public Object getObjectStoreObject(String objectStoreName ) 
	{
		if ( connected == false )
		{
			return null;
		}
		
		return Factory.ObjectStore.getInstance( domain, objectStoreName );
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
}
