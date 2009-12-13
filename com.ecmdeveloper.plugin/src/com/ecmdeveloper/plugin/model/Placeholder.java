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

import com.filenet.api.core.IndependentlyPersistableObject;

/**
 * 
 * @author Ricardo.Belfor
 *
 */
public class Placeholder extends ObjectStoreItem implements IObjectStoreItem{

	public Placeholder() {
		super(null, null);
		this.name = "Loading...";
		// TODO Auto-generated constructor stub
	}

	@Override
	public IndependentlyPersistableObject getObjectStoreObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IObjectStoreItem> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getClassName() {
		return null;
	}
}
