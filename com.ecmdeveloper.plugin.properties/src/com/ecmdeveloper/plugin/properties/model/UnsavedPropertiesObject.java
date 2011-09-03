/**
 * Copyright 2010, Ricardo Belfor
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

package com.ecmdeveloper.plugin.properties.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ricardo.Belfor
 *
 */
public class UnsavedPropertiesObject implements PropertiesObject {

	private static final String DEFAULT_NAME = "Untitled Object";
	private Map<String,Object> propertiesMap;
	private transient PropertyChangeSupport pcsDelegate = new PropertyChangeSupport(this);
	private String name = DEFAULT_NAME;
	
	public UnsavedPropertiesObject() {
		propertiesMap = new HashMap<String, Object>();
	}
	
	@Override
	public Object getValue(String propertyName) {
		if ( propertiesMap.containsKey(propertyName) ) {
			return propertiesMap.get(propertyName);
		}
		return null;
	}

	@Override
	public void setValue(String propertyName, Object value) throws Exception {
		
		Object oldValue = getOldPropertyValue(propertyName);
		
		propertiesMap.put(propertyName, value);

		if (pcsDelegate.hasListeners(propertyName)) {
			pcsDelegate.firePropertyChange(propertyName, oldValue, value);
		}
	}

	private Object getOldPropertyValue(String propertyName) {
		Object oldValue = null;
		if ( propertiesMap.containsKey(propertyName) ) {
			oldValue = propertiesMap.get(propertyName);
		}
		return oldValue;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException();
		}
		pcsDelegate.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		if (listener != null) {
			pcsDelegate.removePropertyChangeListener(listener);
		}
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<String> getPropertyNames() {
		return propertiesMap.keySet();
	}
}
