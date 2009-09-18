package com.ecmdeveloper.plugin.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.filenet.api.collection.CodeModuleSet;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.core.IndependentObject;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;

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

		super(parent, null);
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
			rootFolder = new Folder( objectStore.get_RootFolder(), this, this );
			children.addAll( rootFolder.getChildren() );
		}

		return children;
	}

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

	// TODO: remove this offline test stuff
	
//	private boolean tempConnected = false;
//	
//	public boolean isConnected() {
//		
//		return tempConnected;
//	}
//
//	public void connect() {
//		tempConnected = true;
//	}
	
	public ContentEngineConnection getConnection() {
		return connection;
	}

	public void setConnection(ContentEngineConnection connection) {
		this.connection = connection;
	}

	@SuppressWarnings("unchecked")
	public Collection<CodeModule> getCodeModules() {
	
		SearchScope scope = new SearchScope(objectStore);
		String query = "Select This From CodeModule WHERE IsCurrentVersion = TRUE";
		CodeModuleSet codeModuleSet = (CodeModuleSet) scope.fetchObjects(new SearchSQL( query  ), null, null, null);
		Iterator iterator = codeModuleSet.iterator();
		
		ArrayList<CodeModule> codeModules = new ArrayList<CodeModule>();

		while (iterator.hasNext()) {
			codeModules.add( new CodeModule( iterator.next(), this, this ) );
		}
		
		return codeModules;
	}
	
	public IObjectStoreItem getObject(String id, String className )
	{
		IndependentObject object = objectStore.getObject(className, id);
		return ObjectStoreItemFactory.getObject( object, this, this ); 
	}
}