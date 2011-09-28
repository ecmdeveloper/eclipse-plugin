/**
 * Copyright 2011, Ricardo Belfor
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.commons.SessionParameter;

import com.ecmdeveloper.plugin.cmis.Activator;
import com.ecmdeveloper.plugin.cmis.model.tasks.LoadChildrenTask;
import com.ecmdeveloper.plugin.core.model.IClassDescriptionFolder;
import com.ecmdeveloper.plugin.core.model.IConnection;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.ObjectStoreItemsModel;
import com.ecmdeveloper.plugin.core.model.Placeholder;
import com.ecmdeveloper.plugin.core.model.constants.ClassDescriptionFolderType;
import com.ecmdeveloper.plugin.core.model.constants.ClassType;

/**
 * @author ricardo.belfor
 *
 */
public class ObjectStore extends ObjectStoreItem implements IObjectStore {

	private Session session;
	private final Connection connection;
	private final String id;
	private String name;
	private String displayName;
	private Collection<IObjectStoreItem> children;

	protected ObjectStore(String id, Connection connection ) {
		super(null, null);
		
		this.id = id;
		this.name = id;
		this.displayName = id;
		this.connection = connection;
	}
	
	@Override
	public void connect() {
		if ( session == null ) {
			Map<String, String> parameters = connection.getParameters();
			parameters.put(SessionParameter.REPOSITORY_ID, id );
			SessionFactory sessionFactory = Activator.getDefault().getSessionFactory();
			session = sessionFactory.createSession(parameters);
			name = session.getRepositoryInfo().getName();

			ObjectStoreItemsModel.getInstance().add(this);			
		}
	}

	public Session getSession() {
		return session;
	}

	@Override
	public IConnection getConnection() {
		return connection;
	}

	@Override
	public boolean isConnected() {
		return session != null;
	}

	@Override
	public String getClassName() {
		return null;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}

	@Override
	public ObjectStore getObjectStore() {
		return this;
	}

	@Override
	public Collection<IObjectStoreItem> getChildren() {
		
		if ( children == null )
		{
			children = new ArrayList<IObjectStoreItem>();
			children.add( new Placeholder( Placeholder.Type.LOADING ) );
			
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
	
	@Override
	public IObjectStoreItem getParent() {
		return null;
	}

	@Override
	public Object getValue(String propertyName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren() {
		return isConnected();
	}

	@Override
	public boolean isSimilarObject(IObjectStoreItem otherItem) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void refresh() {
		children = null;
		
		if ( isConnected() ) {
//			objectStore.refresh( new String[] { PropertyNames.DISPLAY_NAME } );
//			displayName = objectStore.get_DisplayName();
		}
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setParent(IObjectStoreItem parent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setValue(String propertyName, Object value) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CmisObject getCmisObject() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<IClassDescriptionFolder> getClassDescriptionFolders() {

		ArrayList<IClassDescriptionFolder> virtualFolders = new ArrayList<IClassDescriptionFolder>();
		for (ClassDescriptionFolderType classDescriptionFolderType : ClassDescriptionFolderType.values() ) {
			if ( !classDescriptionFolderType.equals( ClassDescriptionFolderType.CUSTOM_OBJECT_CLASSES ) ) {
				virtualFolders.add( new ClassDescriptionFolder( classDescriptionFolderType, this )  );
			}
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
			return "cmis:document";
		} else if ( classType.equals( ClassType.FOLDER_CLASSES ) ) {
			return "cmis:folder";
		}
		return null;
	}
}
