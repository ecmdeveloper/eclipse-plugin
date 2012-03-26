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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.security.auth.Subject;

import com.ecmdeveloper.plugin.core.model.AbstractConnection;
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
public class ContentEngineConnection extends AbstractConnection
{
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
			connection = Factory.Connection.getConnection(getUrl() );
	
			subject = UserContext.createSubject(connection, getUsername(), getPassword(), null );
			UserContext uc = UserContext.get();
			uc.pushSubject(subject);
			
			EntireNetwork entireNetwork = Factory.EntireNetwork.fetchInstance(connection, null);
			domain = entireNetwork.get_LocalDomain();
			
			setName( domain.get_Name() );
			if ( getDisplayName() == null ) {
				setDisplayName( domain.get_Name() );
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
	public Map<String, String> getParameters() {
		return new HashMap<String, String>();
	}
}
