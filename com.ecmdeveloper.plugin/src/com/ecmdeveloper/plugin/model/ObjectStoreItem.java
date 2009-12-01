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

import java.util.Collection;

import org.eclipse.core.runtime.Platform;

import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.IndependentlyPersistableObject;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public abstract class ObjectStoreItem implements IObjectStoreItem {

	protected IObjectStoreItem parent;
	protected String name;
	protected String id;
	protected ObjectStore objectStore;

	public ObjectStoreItem(IObjectStoreItem parent, ObjectStore objectStore ) {
		this.parent = parent;
		this.objectStore = objectStore;
	}
	
	public abstract IndependentlyPersistableObject getObjectStoreObject();
	
	/* (non-Javadoc)
	 * @see com.ecmdeveloper.plugin.model.IObjectStoreItem#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	
	/**
	 * Gets the display name of this item. The default implementation just
	 * returns the name.
	 * 
	 * @return the display name
	 * 
	 * @see com.ecmdeveloper.plugin.model.IObjectStoreItem#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return getName();
	}

	@Override
	public String getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see com.ecmdeveloper.plugin.model.IObjectStoreItem#getParent()
	 */
	@Override
	public IObjectStoreItem getParent() {
		return parent;
	}
	
	@Override
	public void setParent(IObjectStoreItem parent) {
		this.parent = parent;
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	@Override
	public boolean hasChildren() 
	{
		return false;
	}

	@Override
	public ObjectStore getObjectStore() {
		return objectStore;
	}
	
	public void save() {
		getObjectStoreObject().save(RefreshMode.REFRESH);
	}

	public void removeChild(IObjectStoreItem childItem ) {
		// Place holder
	}

	@Override
	public void addChild(IObjectStoreItem childItem) {
		// Place holder	
	}
	
	@Override
	public void setChildren(Collection<IObjectStoreItem> children) {
		// Stub for childless objects 
	}	
}
