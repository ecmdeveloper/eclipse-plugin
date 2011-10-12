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
import java.util.concurrent.ExecutionException;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.commons.PropertyIds;

import com.ecmdeveloper.plugin.cmis.Activator;
import com.ecmdeveloper.plugin.cmis.model.tasks.LoadChildrenTask;
import com.ecmdeveloper.plugin.core.model.IFolder;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.Placeholder;
import com.ecmdeveloper.plugin.core.model.constants.PlaceholderType;

/**
 * @author ricardo.belfor
 *
 */
public class Folder extends ObjectStoreItem implements IFolder {

	private Collection<IObjectStoreItem> children;
	private org.apache.chemistry.opencmis.client.api.Folder folder;
	private Boolean hasChildren;
	private boolean contained;
	private String pathName;
	private String className;
	
	protected Folder(Object folder, IObjectStoreItem parent, ObjectStore objectStore, boolean saved) {
		super(parent, objectStore, saved);

		this.folder = (org.apache.chemistry.opencmis.client.api.Folder) folder;
		contained = false;
		refresh();
	}

	protected Folder(Object folder, IObjectStoreItem parent, ObjectStore objectStore) {
		this(folder, parent, objectStore, true);
	}
	
	public org.apache.chemistry.opencmis.client.api.Folder getInternalFolder() {
		return folder;
	}
	
	@Override
	public void refresh() {

		if ( !saved ) {
			return;
		}
		
//		folder.refresh( new String[] { PropertyNames.FOLDER_NAME,
//				PropertyNames.PATH_NAME, PropertyNames.ID,
//				PropertyNames.CONTAINEES, PropertyNames.SUB_FOLDERS });

		name = folder.getName();
		pathName = folder.getPath();
		id = folder.getId();
		children = null;
		hasChildren = folder.getChildren().iterator().hasNext();
		className = folder.getType().getId();
		
		initalizeProperties();
	}

	/**
	 * Gets the children of this folder. If the children are not yet loaded a
	 * place holder is returned and asynchronous task is started to fetch the
	 * children.
	 * 
	 * @return the children
	 * 
	 * @see com.ecmdeveloper.plugin.model.IObjectStoreItem#getChildren()
	 */
	@Override
	public Collection<IObjectStoreItem> getChildren() {
		if ( children == null )	{
			children = new ArrayList<IObjectStoreItem>();
			children.add( new Placeholder(PlaceholderType.LOADING) );

			LoadChildrenTask loadChildrenTask = new LoadChildrenTask( this );
			Activator.getDefault().getTaskManager().executeTaskASync(loadChildrenTask);
		}
		
		return children;
	}

	@Override
	public Collection<? extends IObjectStoreItem> getChildrenSync() throws ExecutionException {
		if ( children == null )	{
			children = new ArrayList<IObjectStoreItem>();
			LoadChildrenTask loadChildrenTask = new LoadChildrenTask( this );
			Activator.getDefault().getTaskManager().executeTaskSync(loadChildrenTask);
		}
		
		return children;
	}
	
	/**
	 * Removes the child object. This is only to keep the model consistent with
	 * reality and will not actually remove the object.
	 * 
	 * @param childItem the child item
	 * 
	 * @see com.ecmdeveloper.plugin.model.ObjectStoreItem#removeChild(com.ecmdeveloper.plugin.model.IObjectStoreItem)
	 */
	@Override
	public void removeChild(IObjectStoreItem childItem ) {
		if (children != null) {
			if ( children.contains( childItem ) ) {
				children.remove(childItem);
				hasChildren = ! children.isEmpty();
			}
		}
	}

	/**
	 * Adds the child object. This is only to keep the model consistent with
	 * reality and will not actually add the object. If the children are not
	 * yet loaded only the internal folder object is refreshed.
	 * 
	 * @param childItem the child item
	 * 
	 * @see com.ecmdeveloper.plugin.model.ObjectStoreItem#addChild(com.ecmdeveloper.plugin.model.IObjectStoreItem)
	 */
	@Override
	public void addChild(IObjectStoreItem childItem) {
		
		if ( children == null ) {
			refresh();
			return;
		}
		
		if ( ! children.contains( childItem ) ) {
			children.add(childItem);
			hasChildren = ! children.isEmpty();
		}
	}

	/**
	 * Checks for children.
	 * 
	 * @return true, if checks for children
	 * 
	 * @see com.ecmdeveloper.plugin.model.ObjectStoreItem#hasChildren()
	 */
	@Override
	public boolean hasChildren() 
	{
		if ( hasChildren == null ) {
			return false;
		} else {
			return hasChildren;
		}
	}

	/**
	 * Sets the children.
	 * 
	 * @param children the children
	 * 
	 * @see com.ecmdeveloper.plugin.model.ObjectStoreItem#setChildren(java.util.Collection)
	 */
	@Override
	public void setChildren(Collection<IObjectStoreItem> children) {
		this.children = children;
	}	
	
	/**
	 * Sets the name.
	 * 
	 * @param name the name
	 * 
	 * @see com.ecmdeveloper.plugin.model.IObjectStoreItem#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;
		setValue(PropertyIds.NAME, name );
	}

	@Override
	public void save() {
		super.save();
		this.name = folder.getName();
	}
	
	/**
	 * Sets if this folder is contained rather then a child object.
	 * 
	 * @param contained the new contained
	 */
	public void setContained(boolean contained) {
		this.contained = contained;
	}

	/**
	 * Checks if this folder is contained rather then a child object.
	 * 
	 * @return true, if is contained
	 */
	@Override
	public boolean isContained() {
		return contained;
	}

	@Override
	public String getPathName() {
		return pathName;
	}

	@Override
	public String getClassName() {
		return className;
	}

	@Override
	public CmisObject getCmisObject() {
		return folder;
	}
}
