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
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ricardo.Belfor
 *
 */
public class UnsavedPropertiesObject implements PropertiesObject {

	private Map<String,Object> propertiesMap;
	
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
		propertiesMap.put(propertyName, value);
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		return "Untitled Object";
	}
}
