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

import java.util.Collection;

import com.ecmdeveloper.plugin.core.model.IObjectStore;

/**
 * @author ricardo.belfor
 *
 */
public interface IClassDescription {

	void refresh();

	IObjectStore getObjectStore();

	Collection<Object> getChildren();

	Collection<Object> getChildren(boolean synchronous) throws Exception;

	String getDisplayName();

	String getName();

	String getId();

	Boolean getCBREnabled();

	boolean hasChildren();

	void setChildren(Collection<IClassDescription> children);

	Collection<IPropertyDescription> getAllPropertyDescriptions();

	Collection<IPropertyDescription> getPropertyDescriptions();

	Object getParent();

}
