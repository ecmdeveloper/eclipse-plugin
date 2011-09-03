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

import org.eclipse.swt.graphics.Image;

import com.ecmdeveloper.plugin.Activator;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.util.IconFiles;
import com.filenet.api.core.IndependentlyPersistableObject;

/**
 * 
 * @author Ricardo.Belfor
 *
 */
public class Placeholder extends ObjectStoreItem implements IObjectStoreItem {

	private final Type type;

	public enum Type {
		LOADING,
		ERROR,
		MESSAGE
	}
	
	public Placeholder(Placeholder.Type type) {
		super(null, null);
		this.type = type;
		if ( type.equals(Type.LOADING) ) {
			this.name = "Loading...";
		}
	}

	@Override
	public IndependentlyPersistableObject getObjectStoreObject() {
		return null;
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
}
