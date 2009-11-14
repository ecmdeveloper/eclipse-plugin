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

import com.ecmdeveloper.plugin.model.tasks.LoadChildrenTask;
import com.ecmdeveloper.plugin.util.Messages;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.core.IndependentlyPersistableObject;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class ObjectStore extends ObjectStoreItem 
{
	private static final String NOT_CONNECTED_MESSAGE = Messages.ObjectStore_NotConnectedMessage;
	protected ContentEngineConnection connection;
	protected com.filenet.api.core.ObjectStore objectStore;
	
	private Collection<IObjectStoreItem> children;

	public ObjectStore(String objectStoreName, IObjectStoreItem parent) {

		super(parent, null);
		this.name = objectStoreName;
	}

	@Override
	public ObjectStore getObjectStore() {
		return this;
	}
	
	@Override
	public IndependentlyPersistableObject getObjectStoreObject() {
		return objectStore;
	}

	@Override
	public boolean hasChildren() {
		return isConnected();
	}

	@Override
	public Collection<IObjectStoreItem> getChildren() {
		
		if ( children == null )
		{
			children = new ArrayList<IObjectStoreItem>();
			children.add( new Placeholder() );
			
			LoadChildrenTask loadChildrenTask = new LoadChildrenTask( this );
			ObjectStoresManager.getManager().executeTaskASync(loadChildrenTask);
		}
		return children;
	}

	@Override
	public void setChildren(Collection<IObjectStoreItem> children) {
		this.children = children;
	}	

	public void removeChild(IObjectStoreItem childItem ) {
		if ( children.contains( childItem ) ) {
			children.remove(childItem);
		}
	}

	@Override
	public void addChild(IObjectStoreItem childItem) {

		if ( children == null ) {
			children = new ArrayList<IObjectStoreItem>();
		}
		
		if ( ! children.contains( childItem ) ) {
			children.add(childItem);
		}
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
	
	public ContentEngineConnection getConnection() {
		return connection;
	}

	public void setConnection(ContentEngineConnection connection) {
		this.connection = connection;
	}
	
//	public IObjectStoreItem getObject(String id, String className )
//	{
//		if ( objectStore == null ) {
//			throw new RuntimeException(MessageFormat.format(
//					NOT_CONNECTED_MESSAGE, connection.getName(), getName()));
//		}
//		
//		if ( VERSION_SERIES_CLASS.equals( className) ) {
//			return new CodeModule(Factory.VersionSeries.getInstance(objectStore, new Id( id ) ), this, this);
//		} else {
//			IndependentObject object = objectStore.getObject(className, id);
//			return ObjectStoreItemFactory.getObject( object, this, this );
//		}
//	}

	@Override
	public void setName(String name) {
		objectStore.set_DisplayName(name);
	}

	@Override
	public void refresh() {
		children = null;
	}

	public static void assertConnected(ObjectStore objectStore) {

		if ( ! objectStore.isConnected() ) {
			throw new RuntimeException(MessageFormat.format(
					NOT_CONNECTED_MESSAGE, objectStore.getConnection()
							.toString(), objectStore.getName()));
		}
	}
}