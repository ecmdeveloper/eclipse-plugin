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

import org.eclipse.swt.graphics.Image;

import com.ecmdeveloper.plugin.core.Activator;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskFactory;
import com.ecmdeveloper.plugin.core.util.IconFiles;

/**
 * 
 * @author Ricardo.Belfor
 *
 */
public class Placeholder implements IObjectStoreItem {

	private final Type type;
	private String name;
	
	public enum Type {
		LOADING,
		ERROR,
		MESSAGE
	}
	
	public Placeholder(Placeholder.Type type) {
		this.type = type;
		if ( type.equals(Type.LOADING) ) {
			this.name = "Loading...";
		}
	}

	@Override
	public Collection<IObjectStoreItem> getChildren() {
		return null;
	}

	@Override
	public void refresh() {
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDisplayName() {
		return name;
	}

	@Override
	public String getClassName() {
		return null;
	}
	
	public Image getImage() {
		switch (type) {
		case ERROR:
			return Activator.getImage( IconFiles.ERROR );
		case LOADING:
			return Activator.getImage( IconFiles.HOURGLASS );
		case MESSAGE:
			return Activator.getImage( IconFiles.MESSAGE );		
		}
		throw new IllegalArgumentException();
	}

	@Override
	public void addChild(IObjectStoreItem objectStoreItem) {
		// TODO Auto-generated method stub
		
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
	public IObjectStoreItem getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getValue(String propertyName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSimilarObject(IObjectStoreItem otherItem) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeChild(IObjectStoreItem objectStoreItem) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setChildren(Collection<IObjectStoreItem> children) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setParent(IObjectStoreItem parent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setValue(String propertyName, Object value) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITaskFactory getTaskFactory() {
		throw new UnsupportedOperationException();
	}
}
