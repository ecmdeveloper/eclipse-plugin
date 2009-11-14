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

import com.filenet.api.constants.PropertyNames;
import com.filenet.api.core.IndependentlyPersistableObject;

/**
 * @author Ricardo Belfor
 *
 */
public class CustomObject extends ObjectStoreItem {

	protected com.filenet.api.core.CustomObject customObject;
	
	public CustomObject( Object customObject, IObjectStoreItem parent, ObjectStore objectStore ) {

		super(parent, objectStore );
		
		this.customObject = (com.filenet.api.core.CustomObject) customObject;
		refresh();
	}
	
	/**
	 * A custom object has no children.
	 * 
	 * @see com.ecmdeveloper.plugin.model.IObjectStoreItem#getChildren()
	 */
	@Override
	public Collection<IObjectStoreItem> getChildren() {
		return null;
	}

	@Override
	public IndependentlyPersistableObject getObjectStoreObject() {
		return customObject;
	}

	@Override
	public void setName(String name) {
		throw new UnsupportedOperationException( "Renaming of Custom Objects is not supported" );
	}

	@Override
	public void refresh() {

		this.customObject.fetchProperties( new String[] { PropertyNames.NAME, PropertyNames.ID } );
		this.name = this.customObject.get_Name();
		this.id = this.customObject.get_Id().toString();
	}
}
