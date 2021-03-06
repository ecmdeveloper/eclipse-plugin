/**
 * Copyright 2009,2010, Ricardo Belfor
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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.Platform;

import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.constants.Feature;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskFactory;
import com.ecmdeveloper.plugin.model.tasks.TaskFactory;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.IndependentlyPersistableObject;
import com.filenet.api.property.Properties;

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
	
	public void save() {
		getObjectStoreObject().save(RefreshMode.REFRESH);
		saved = true;
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
	
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getValue(String propertyName) {
		
		if ( !saved ) {
			return null;
		}
		
		Properties properties = getProperties();
		
		Object objectValue;
		try {
			objectValue = properties.getObjectValue(propertyName);
		} catch (Exception e) {
			objectValue = "Error fetching value";
		}

		if ( objectValue instanceof Collection ) {
			ArrayList<?> values = new ArrayList( (Collection) objectValue );
			return values.toArray();
		} else if ( objectValue instanceof IndependentlyPersistableObject ) {
//			return ((IndependentlyPersistableObject)objectValue).getProperties().getIdValue(PropertyNames.ID).toString();
			return "TODO";
		}
		return objectValue;
	}

	@Override
	public Object getSafeValue(String propertyName) {
		
		if ( !saved ) {
			return null;
		}
		
		Properties properties = getProperties();
		if ( properties.isPropertyPresent(propertyName) ) {
			return getValue(propertyName);
		}
		return null;
	}
	
	private Properties getProperties() {
		IndependentlyPersistableObject objectStoreObject = getObjectStoreObject();
		Properties properties = objectStoreObject.getProperties();
		return properties;
	}

	@Override
	public void setValue(String propertyName, Object value) throws Exception {
		
		Object oldValue = getValue(propertyName);

		Properties properties = getProperties();
		
		if ( saved && ! properties.get(propertyName).isSettable() ) {
			// TODO throw exception
			return;
		}
		
		if ( value != null ) {
			properties.putObjectValue(propertyName, value);
		} else {
			setNullValue( properties.get( propertyName ) );
		}

		firePropertyChange(propertyName, oldValue, value );
	}

	private void setNullValue(com.filenet.api.property.Property property)
	{
		Method method = getSetValueMethod(property);
		if ( method != null ) {
			try {
				method.invoke( property, new Object[] { null } );
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private Method getSetValueMethod( com.filenet.api.property.Property property ) 
	{
		Method methods[] = property.getClass().getMethods();
		
		for (int i = 0; i < methods.length; i++) {
			if ( methods[i].getName().equals( "setValue" ) ) {
				return methods[i];
			}
		}
		return null;
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

	@Override
	public boolean isSimilarObject(IObjectStoreItem otherItem) {

		if ( !otherItem.equals(this) ) {
			if ( otherItem.getId() != null && otherItem.getId().equalsIgnoreCase(getId()) ) {
				// TODO: add extra object store and connection test
				System.out.println(this.toString() + " = " + otherItem.toString() );
				System.out.println(otherItem.getDisplayName() + " found!");
				return true;
			} 
		}
		
		return false;
	}

	@Override
	public ITaskFactory getTaskFactory() {
		return TaskFactory.getInstance();
	}
}
