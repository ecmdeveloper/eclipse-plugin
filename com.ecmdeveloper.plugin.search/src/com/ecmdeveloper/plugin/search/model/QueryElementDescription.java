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

package com.ecmdeveloper.plugin.search.model;

import org.eclipse.jface.resource.ImageDescriptor;

import com.ecmdeveloper.plugin.search.Activator;
import com.ecmdeveloper.plugin.search.editor.QueryIcons;

/**
 * @author ricardo.belfor
 *
 */
public class QueryElementDescription {

	private final String label;
	private final String description;
	private ImageDescriptor icon;
	private ImageDescriptor largeIcon;

	public QueryElementDescription(String label, String description, String iconPath, String largeIconPath ) {
		this.label = label;
		this.description = description;
		icon = Activator.getImageDescriptor(iconPath);
		largeIcon = Activator.getImageDescriptor(largeIconPath);
	}

	public String getLabel() {
		return label;
	}
	
	public String getDescription() {
		return description;
	}
	
	public ImageDescriptor getIcon() {
		return icon;
	}

	public ImageDescriptor getLargeIcon() {
		return largeIcon;
	}

	@Override
	public String toString() {
		return getLabel();
	}
}
