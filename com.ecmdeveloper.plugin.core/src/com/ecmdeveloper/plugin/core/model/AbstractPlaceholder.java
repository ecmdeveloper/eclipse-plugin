/**
 * Copyright 2011, Ricardo Belfor
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

package com.ecmdeveloper.plugin.core.model;

import org.eclipse.swt.graphics.Image;

import com.ecmdeveloper.plugin.core.Activator;
import com.ecmdeveloper.plugin.core.model.constants.PlaceholderType;
import com.ecmdeveloper.plugin.core.util.IconFiles;


/**
 * @author ricardo.belfor
 *
 */
public abstract class AbstractPlaceholder {

	private final PlaceholderType type;
	private String name;

	public AbstractPlaceholder() {
		this(PlaceholderType.LOADING);
	}
	
	public AbstractPlaceholder(PlaceholderType type) {
		this.type = type;
		if ( type.equals(PlaceholderType.LOADING) ) {
			this.name = "Loading...";
		}
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

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
