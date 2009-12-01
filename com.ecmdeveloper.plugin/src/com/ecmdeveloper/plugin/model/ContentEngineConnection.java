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
public class ContentEngineConnection 
{
	private String url;
	private String username;
	private String password;
	private String name;
	private String displayName;
	
	private Connection connection;
	private Domain domain;
	
	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	private boolean connected;
	
	public ContentEngineConnection() {
		connected = false;
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
	
	public boolean isConnected() {
		return connected;
	}

	@SuppressWarnings("unchecked")
	public ObjectStore[] getObjectStores(IObjectStoreItem parent )
	{
		ObjectStoreSet objectStores = domain.get_ObjectStores();
		Iterator iterator = objectStores.iterator();
		ArrayList<ObjectStore> objectStoreList = new ArrayList<ObjectStore>();
		
		while (iterator.hasNext()) 
		{
			com.filenet.api.core.ObjectStore objectStore = (com.filenet.api.core.ObjectStore) iterator.next();

			objectStore.fetchProperties( new String[] { PropertyNames.NAME, PropertyNames.DISPLAY_NAME } );
			ObjectStore os = new ObjectStore(objectStore.get_Name(), objectStore.get_DisplayName(), parent );
			os.setConnection( this );
			objectStoreList.add( os ); 
		}

		return objectStoreList.toArray( new ObjectStore[0] );
	}
	
	public void connect() {
		
		if ( ! connected ) {
			connection = Factory.Connection.getConnection(url);
	
			Subject subject = UserContext.createSubject(connection, username, password, null );
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
