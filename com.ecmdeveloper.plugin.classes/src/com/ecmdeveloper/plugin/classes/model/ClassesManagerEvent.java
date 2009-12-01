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
package com.ecmdeveloper.plugin.classes.model;

import java.util.EventObject;

import com.ecmdeveloper.plugin.model.ObjectStore;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class ClassesManagerEvent extends EventObject {

	private static final long serialVersionUID = 4241259959052923785L;

	private ObjectStore[] objectStores;
	private ClassDescription[] itemsAdded;
	private ClassDescription[] itemsUpdated;
	private ClassDescription[] itemsRemoved;

	public ClassesManagerEvent(Object source, ObjectStore[] objectStores) {
		super(source);
		this.objectStores = objectStores;
	}

	public ClassesManagerEvent(Object source,
			ClassDescription[] itemsAdded, ClassDescription[] itemsUpdated, ClassDescription[] itemsRemoved) {
		super(source);
		this.itemsAdded = itemsAdded;
		this.itemsUpdated = itemsUpdated;
		this.itemsRemoved = itemsRemoved;
	}

	public ObjectStore[] getObjectStores() {
		return objectStores;
	}

	public ClassDescription[] getItemsAdded() {
		return itemsAdded;
	}

	public ClassDescription[] getItemsUpdated() {
		return itemsUpdated;
	}

	public ClassDescription[] getItemsRemoved() {
		return itemsRemoved;
	}
}
