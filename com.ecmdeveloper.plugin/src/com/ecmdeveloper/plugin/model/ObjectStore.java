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

import com.ecmdeveloper.plugin.Activator;
import com.ecmdeveloper.plugin.core.model.IClassDescriptionFolder;
import com.ecmdeveloper.plugin.core.model.IConnection;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.ObjectStoreItemsModel;
import com.ecmdeveloper.plugin.core.model.Placeholder;
import com.ecmdeveloper.plugin.core.model.constants.ClassDescriptionFolderType;
import com.ecmdeveloper.plugin.core.model.constants.ClassType;
import com.ecmdeveloper.plugin.core.model.constants.PlaceholderType;
import com.ecmdeveloper.plugin.model.tasks.LoadChildrenTask;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.core.IndependentlyPersistableObject;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class ObjectStore extends ObjectStoreItem implements IObjectStore {
	
	private static final String DEFAULT_FOLDER_CLASS_NAME = "Folder";
	private static final String DEFAULT_DOCUMENT_CLASS_NAME = "Document";
	private static final String DEFAULT_CUSTOM_OBJECT_CLASS_NAME = "CustomObject";
	
	private static final String NOT_CONNECTED_MESSAGE = "Object Store ''{0}:{1}'' is not connected.\nConnect to the Object Store before trying this action";
	protected ContentEngineConnection connection;
	protected com.filenet.api.core.ObjectStore objectStore;
	
	private Collection<IObjectStoreItem> children;
	private String displayName;

	protected ObjectStore(String objectStoreName, String displayName ) {
		this(objectStoreName, displayName, null);
	}
	
	protected ObjectStore(String objectStoreName, String displayName, IObjectStoreItem parent) {

		super(parent, null);
		this.name = objectStoreName;
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
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
		
		if ( !isConnected() ) {
			return null;
		}
		
		if ( children == null )
		{
			children = new ArrayList<IObjectStoreItem>();
			children.add( new Placeholder( PlaceholderType.LOADING ) );
			
			LoadChildrenTask loadChildrenTask = new LoadChildrenTask( this );
			Activator.getDefault().getTaskManager().executeTaskASync(loadChildrenTask);
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

		if ( children != null ) {
			if ( ! children.contains( childItem ) ) {
				children.add(childItem);
			}
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
		objectStore.fetchProperties( new String[] { PropertyNames.ID, PropertyNames.DISPLAY_NAME } );
		id = objectStore.get_Id().toString();
		displayName = objectStore.get_DisplayName();
		
		ObjectStoreItemsModel.getInstance().add(this);
	}
	
	@Override
	public void disconnect() {
		connection = null;
		children = null;
	}

	public ContentEngineConnection getConnection() {
		return connection;
	}

	public void setConnection(IConnection connection) {
		this.connection = (ContentEngineConnection) connection;
	}
	
	@Override
	public void setName(String name) {
		objectStore.set_DisplayName(name);
	}

	@Override
	public void refresh() {
		children = null;
		
		if ( isConnected() ) {
			objectStore.refresh( new String[] { PropertyNames.DISPLAY_NAME } );
			displayName = objectStore.get_DisplayName();
		}
	}

	@Override
	public void assertConnected() {
		if (!isConnected()) {
			throw new RuntimeException(MessageFormat.format(NOT_CONNECTED_MESSAGE, getConnection()
					.toString(), getName()));
		}
	}

	@Override
	public String getClassName() {
		if ( objectStore != null ) {
			return objectStore.getClassName();
		}
		return null;
	}

	@Override
	public Collection<IClassDescriptionFolder> getClassDescriptionFolders() {
		
		ArrayList<IClassDescriptionFolder> virtualFolders = new ArrayList<IClassDescriptionFolder>();
		for (ClassDescriptionFolderType classDescriptionFolderType : ClassDescriptionFolderType.values() ) {
			virtualFolders.add( new ClassDescriptionFolder( classDescriptionFolderType, this )  );
		}
		
		return virtualFolders;
	}

	@Override
	public IClassDescriptionFolder getClassDescriptionFolder( ClassDescriptionFolderType classDescriptionFolderType) {
		return new ClassDescriptionFolder( classDescriptionFolderType, this );
	}

	@Override
	public String getDefaultClassName(ClassType classType) {
		if ( classType.equals( ClassType.DOCUMENT_CLASSES ) ) {
			return DEFAULT_DOCUMENT_CLASS_NAME;
		} else if ( classType.equals( ClassType.FOLDER_CLASSES ) ) {
			return DEFAULT_FOLDER_CLASS_NAME;
		} else if ( classType.equals( ClassType.CUSTOM_OBJECT_CLASSES ) ) {
			return DEFAULT_CUSTOM_OBJECT_CLASS_NAME;
		}
		return null;
	}
}