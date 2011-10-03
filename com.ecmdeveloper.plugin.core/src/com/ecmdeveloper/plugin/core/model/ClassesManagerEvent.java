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
package com.ecmdeveloper.plugin.core.model;

import java.util.EventObject;

import com.ecmdeveloper.plugin.core.model.IClassDescription;
import com.ecmdeveloper.plugin.core.model.IObjectStore;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class ClassesManagerEvent extends EventObject {

	private static final long serialVersionUID = 4241259959052923785L;

	private IObjectStore[] objectStores;
	private IClassDescription[] itemsAdded;
	private IClassDescription[] itemsUpdated;
	private IClassDescription[] itemsRemoved;

	public ClassesManagerEvent(Object source, IObjectStore[] objectStores) {
		super(source);
		this.objectStores = objectStores;
	}

	public ClassesManagerEvent(Object source,
			IClassDescription[] itemsAdded, IClassDescription[] itemsUpdated, IClassDescription[] itemsRemoved) {
		super(source);
		this.itemsAdded = itemsAdded;
		this.itemsUpdated = itemsUpdated;
		this.itemsRemoved = itemsRemoved;
	}

	public IObjectStore[] getObjectStores() {
		return objectStores;
	}

	public IClassDescription[] getItemsAdded() {
		return itemsAdded;
	}

	public IClassDescription[] getItemsUpdated() {
		return itemsUpdated;
	}

	public IClassDescription[] getItemsRemoved() {
		return itemsRemoved;
	}
}
