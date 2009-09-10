package com.ecmdeveloper.plugin.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.filenet.api.collection.ReferentialContainmentRelationshipSet;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.core.IndependentObject;
import com.filenet.api.core.ReferentialContainmentRelationship;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class ObjectStore extends ObjectStoreItem 
{
	protected boolean connected = false;
	protected ContentEngineConnection connection;
	protected com.filenet.api.core.ObjectStore objectStore;
	protected Folder rootFolder;
	
	private ArrayList<IObjectStoreItem> children;

	public ObjectStore(String objectStoreName, IObjectStoreItem parent) {

		super(parent);
		this.name = objectStoreName;
	}

	@Override
	public boolean hasChildren() {
		return true;
	}

	@Override
	public Collection<IObjectStoreItem> getChildren() {
		
		if ( children == null )
		{
			children = new ArrayList<IObjectStoreItem>();
		}

		if ( objectStore != null && rootFolder == null )
		{
			objectStore.fetchProperties( new String[] { PropertyNames.ROOT_FOLDER } );
			rootFolder = new Folder( objectStore.get_RootFolder(), this );
			children.addAll( rootFolder.getChildren() );
		}

		return children;
	}
/*
	public boolean isConnected() {
		
		if ( connection == null ) {
			return false;
		}
		return connection.isConnected();
	}

	public void connect() {
		
		if ( connection == null ) {
			return;
		}
		connection.connect();
		
		objectStore = (com.filenet.api.core.ObjectStore) connection.getObjectStoreObject( name );
		objectStore.fetchProperties( new String[] { PropertyNames.ID } );
		id = objectStore.get_Id().toString();
	}
*/

	// TODO: remove this offline test stuff
	
	private boolean tempConnected = false;
	
	public boolean isConnected() {
		
		return tempConnected;
	}

	public void connect() {
		tempConnected = true;
	}
	
	public ContentEngineConnection getConnection() {
		return connection;
	}

	public void setConnection(ContentEngineConnection connection) {
		this.connection = connection;
	}

	public Collection<CodeModule> getCodeModules() {
		
		ArrayList<CodeModule> codeModules = new ArrayList<CodeModule>();
		codeModules.add( new CodeModule( "CodeModule 1 (" + name + ")", "{12345}" ) );
		codeModules.add( new CodeModule( "CodeModule 2 (" + name + ")", "{23456}" ) );
		codeModules.add( new CodeModule( "CodeModule 3 (" + name + ")", "{34567}" ) );
		
		return codeModules;
	}
}