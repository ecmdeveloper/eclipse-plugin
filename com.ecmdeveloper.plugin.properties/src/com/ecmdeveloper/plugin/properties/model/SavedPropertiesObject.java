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

import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;

/**
 * @author Ricardo.Belfor
 *
 */
public class SavedPropertiesObject implements PropertiesObject {

	private IObjectStoreItem objectStoreItem;

	@Override
	public String getName() {
		return objectStoreItem.getDisplayName();
	}

	public SavedPropertiesObject(IObjectStoreItem objectStoreItem) {
		this.objectStoreItem = objectStoreItem;
	}
	
	@Override
	public Object getValue(String propertyName) {
		return objectStoreItem.getValue(propertyName );
	}

	@Override
	public void setValue(String propertyName, Object value) throws Exception {
		objectStoreItem.setValue(propertyName, value);
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		objectStoreItem.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		objectStoreItem.removePropertyChangeListener(listener);
	}
}
