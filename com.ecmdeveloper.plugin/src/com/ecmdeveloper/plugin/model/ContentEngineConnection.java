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
import com.filenet.api.core.ObjectStore;
import com.filenet.api.util.UserContext;

/**
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
	public String[] getObjectStoreNames()
	{
		ObjectStoreSet objectStores = domain.get_ObjectStores();
		Iterator iterator = objectStores.iterator();
		ArrayList<String> objectStoreNames = new ArrayList<String>();
		
		while (iterator.hasNext()) 
		{
			com.filenet.api.core.ObjectStore objectStore = (com.filenet.api.core.ObjectStore) iterator.next();
			objectStore.fetchProperties( new String[] { PropertyNames.NAME } );
			objectStoreNames.add( objectStore.get_Name() );
		}

		return objectStoreNames.toArray( new String[0] );
	}
	
	public void connect() {
		
		if ( ! connected ) {
			connection = Factory.Connection.getConnection(url);
	
			Subject subject = UserContext.createSubject(connection, username, password, null );
			UserContext uc = UserContext.get();
			uc.pushSubject(subject);
			
			EntireNetwork entireNetwork = Factory.EntireNetwork.fetchInstance(connection, null);
			domain = entireNetwork.get_LocalDomain();
			
			this.name = domain.get_Name();
			connected = true;
		}
	}

	public Object getObjectStoreObject(String objectStoreName ) 
	{
		if ( connected == false )
		{
			// TODO throw an exception
			return null;
		}
		
		return Factory.ObjectStore.getInstance( domain, objectStoreName );
	}

	@Override
	public String toString() {
		return getName();
	}
}
