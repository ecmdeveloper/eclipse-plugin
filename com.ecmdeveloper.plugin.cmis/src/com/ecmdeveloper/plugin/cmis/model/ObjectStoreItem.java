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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.eclipse.core.runtime.Platform;

import com.ecmdeveloper.plugin.cmis.model.tasks.TaskFactory;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.constants.Feature;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskFactory;

/**
 * @author ricardo.belfor
 *
 */
public abstract class ObjectStoreItem implements IObjectStoreItem {

	protected IObjectStoreItem parent;
	protected String name;
	protected String id;
	protected ObjectStore objectStore;
	protected boolean saved;

	private transient PropertyChangeSupport pcsDelegate = new PropertyChangeSupport(this);

	public ObjectStoreItem(IObjectStoreItem parent, ObjectStore objectStore, boolean saved ) {
		this.parent = parent;
		this.objectStore = objectStore;
		this.saved = saved;
	}
	
	public ObjectStoreItem(IObjectStoreItem parent, ObjectStore objectStore) {
		this(parent, objectStore, true );
	}

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

	@Override
	public boolean isSupportedFeature(Feature feature) {
		return true;
	}
	
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

	public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException();
		}
		pcsDelegate.addPropertyChangeListener(listener);
	}

	public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
		if (listener != null) {
			pcsDelegate.removePropertyChangeListener(listener);
		}
	}

	protected void firePropertyChange(String property, Object oldValue, Object newValue) {
		if (pcsDelegate.hasListeners(property)) {
			pcsDelegate.firePropertyChange(property, oldValue, newValue);
		}
	}

	public abstract CmisObject getCmisObject();
	
	@Override
	public ITaskFactory getTaskFactory() {
		return TaskFactory.getInstance();
	}

	public abstract void save();
	
	@Override
	public boolean isSimilarObject(IObjectStoreItem otherItem) {

		if ( !otherItem.equals(this) ) {
			if ( otherItem.getId() != null && otherItem.getId().equalsIgnoreCase(getId()) ) {
				// TODO: add extra object store and connection test
//				System.out.println(this.toString() + " = " + otherItem.toString() );
//				System.out.println(otherItem.getDisplayName() + " found!");
				return true;
			} 
		}
		
		return false;
	}
}
