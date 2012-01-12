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

import java.util.Collection;

import com.ecmdeveloper.plugin.core.model.IClassDescription;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.IPropertyDescription;
import com.ecmdeveloper.plugin.core.model.constants.PlaceholderType;


/**
 * 
 * @author Ricardo Belfor
 *
 */
public class ClassesPlaceholder extends AbstractPlaceholder implements IClassDescription {

	private static final String LOADING_MESSAGE = "Loading class descriptions...";
	private Object parent;

	public ClassesPlaceholder(Exception exception) {
		super(PlaceholderType.ERROR);
		String message = exception.getLocalizedMessage();
		setName( message != null ? message : exception.toString() );
	}
	
	public ClassesPlaceholder(PlaceholderType type) {
		super(type);
		setName(LOADING_MESSAGE);
	}

	public ClassesPlaceholder() {
		super();
		setName(LOADING_MESSAGE);
	}

	@Override
	public Collection<Object> getChildren() {
		return null;
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public Collection<IPropertyDescription> getAllPropertyDescriptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean getCBREnabled() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Object> getChildren(boolean synchronous) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IObjectStore getObjectStore() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IPropertyDescription> getPropertyDescriptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setChildren(Collection<IClassDescription> children) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getParent() {
		return parent;
	}

	public void setParent(Object parent) {
		this.parent = parent;
	}

	@Override
	public String getNamePropertyName() {
		return null;
	}

	@Override
	public boolean isAbstract() {
		// TODO Auto-generated method stub
		return false;
	}
}
