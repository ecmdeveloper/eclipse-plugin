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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.commons.PropertyIds;
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
	protected String className;

	protected Map<String,Object> changedProperties;
	private Map<String,Property<?>> properties;

	private transient PropertyChangeSupport pcsDelegate = new PropertyChangeSupport(this);

	public ObjectStoreItem(IObjectStoreItem parent, ObjectStore objectStore, boolean saved ) {
		this.parent = parent;
		this.objectStore = objectStore;
		this.saved = saved;
		changedProperties = new HashMap<String, Object>();
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
	public String getClassName() {
		return className;
	}

	@Override
	public boolean isSupportedFeature(Feature feature) {
		if ( feature.equals(Feature.CLASS_FILTER ) ) {
			return false;
		}
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

	@Override
	public Object getValue(String propertyName) {
		
		if ( !saved ) {
			return null;
		}

		Object objectValue = getObjectValue(propertyName);
		return convertFromInternalValue(objectValue);
	}

	private Object getObjectValue(String propertyName) {
		Object objectValue = null;

		if ( changedProperties.containsKey(propertyName) ) {
			objectValue = changedProperties.get(propertyName);
		} else {
			Property<?> property = properties.get(propertyName);
			if ( property != null ) {
				objectValue = property.getValue();
			}
		}
		return objectValue;
	}

	@SuppressWarnings("unchecked")
	private Object convertFromInternalValue(Object objectValue) {
		
		if ( objectValue instanceof Calendar) {
			return ((Calendar) objectValue).getTime();
		} else if ( objectValue instanceof BigInteger ) {
			return new Integer( ((BigInteger) objectValue).intValue() );
		} else if ( objectValue instanceof BigDecimal ) {
			return new Double( ((BigDecimal) objectValue).doubleValue() );
		} else if ( objectValue instanceof ArrayList ) {
			ArrayList<? super Object> valueList = (ArrayList<? super Object>) objectValue;
			ArrayList<? super Object> newValueList = new ArrayList<Object>(valueList.size());
			for (int i = 0; i < valueList.size(); ++i) {
				newValueList.add(convertFromInternalValue( valueList.get(i) ) );
			}
			return newValueList.toArray();
		}

		return objectValue;
	}
	
	protected void initalizeProperties() {
		properties = new HashMap<String, Property<?>>();
		
		for ( Property<?> propery : getCmisObject().getProperties() ) {
			properties.put(propery.getId(), propery );
		}
	}

	@Override
	public void setValue(String propertyName, Object value) {
		Object oldValue = getValue(propertyName);
		changedProperties.put(propertyName, convertToInternalValue(value) );
		if ( propertyName.equals( PropertyIds.NAME ) ) {
			name = (String) value;
		}

		if ( PropertyIds.OBJECT_TYPE_ID.equals( propertyName) ) {
			className = (String) value;
		}
		firePropertyChange(propertyName, oldValue, value );
	}

	@SuppressWarnings("unchecked")
	private Object convertToInternalValue(Object value) {
		if ( value instanceof Date ) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime((Date) value);
			return calendar;
		} else if ( value instanceof Double ) {
			BigDecimal bigDecimalValue = new BigDecimal( ((Double)value).toString() );
			return bigDecimalValue;
		} else if ( value instanceof List<?> ) {
			List<? super Object> valueList = (List<? super Object>) value;
			ArrayList<? super Object> newValueList = new ArrayList<Object>(valueList.size());
			for (int i = 0; i < valueList.size(); ++i) {
				newValueList.add(convertToInternalValue( valueList.get(i) ) );
			}
			return newValueList;
		}
		return value;
	}

	public void save() {
		if ( !changedProperties.isEmpty() ) {
			getCmisObject().updateProperties(changedProperties);
		}
		changedProperties.clear();
		initalizeProperties();
	}
}
