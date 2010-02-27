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
import java.util.Collection;

import com.ecmdeveloper.plugin.model.tasks.LoadChildrenTask;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.core.IndependentlyPersistableObject;

/**
 * This class represents a folder object.
 * 
 * @author Ricardo Belfor
 *
 */
public class Folder extends ObjectStoreItem {

	private Collection<IObjectStoreItem> children;
	protected com.filenet.api.core.Folder folder;
	protected Boolean hasChildren;
	protected boolean contained;
	protected String pathName;
	
	/**
	 * Instantiates a new folder object.
	 * 
	 * @param folder the folder
	 * @param parent the parent
	 * @param objectStore the object store
	 */
	public Folder(Object folder, IObjectStoreItem parent, ObjectStore objectStore ) {
		super(parent, objectStore );
		
		this.folder = (com.filenet.api.core.Folder) folder;
		contained = false;
		refresh();
	}
	
	/**
	 * Gets the underlying object store object, in this case the folder.
	 * 
	 * @return the object store object
	 * 
	 * @see com.ecmdeveloper.plugin.model.ObjectStoreItem#getObjectStoreObject()
	 */
	@Override
	public IndependentlyPersistableObject getObjectStoreObject() {
		return folder;
	}

	/**
	 * Refreshes this object. The values of the properties are fetched from 
	 * the Content Engine.
	 * 
	 * @see com.ecmdeveloper.plugin.model.IObjectStoreItem#refresh()
	 */
	@Override
	public void refresh() {
		
		folder.refresh( new String[] { PropertyNames.FOLDER_NAME,
				PropertyNames.PATH_NAME, PropertyNames.ID,
				PropertyNames.CONTAINEES, PropertyNames.SUB_FOLDERS });

		name = folder.get_FolderName();
		pathName = folder.get_PathName();
		id = folder.get_Id().toString();
		children = null;
		hasChildren = ! folder.get_SubFolders().isEmpty() || ! folder.get_Containees().isEmpty();
	}

	@Override
	public void save() {
		super.save();
		name = folder.get_FolderName();
		pathName = folder.get_PathName();
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
	public Collection<IObjectStoreItem> getChildren() 
	{
		if ( children == null )
		{
			children = new ArrayList<IObjectStoreItem>();
			children.add( new Placeholder() );

			LoadChildrenTask loadChildrenTask = new LoadChildrenTask( this );
			ObjectStoresManager.getManager().executeTaskASync(loadChildrenTask);
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
	public void removeChild(IObjectStoreItem childItem ) {
		if ( children.contains( childItem ) ) {
			children.remove(childItem);
			hasChildren = ! children.isEmpty();
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
		folder.set_FolderName( name );
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
	public boolean isContained() {
		return contained;
	}

	public String getPathName() {
		return pathName;
	}

	@Override
	public String getClassName() {
		if ( folder != null ) {
			return folder.getClassName();
		}
		return null;
	}
}
