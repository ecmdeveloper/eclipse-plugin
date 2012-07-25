/**
 * Copyright 2012, Ricardo Belfor
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

package com.ecmdeveloper.plugin.model.security;

import com.ecmdeveloper.plugin.core.model.security.IAccessLevel;
import com.filenet.api.constants.PermissionType;
import com.filenet.api.security.AccessPermissionDescription;

/**
 * @author ricardo.belfor
 *
 */
public class AccessLevel implements IAccessLevel {

	public static final String CUSTOM_ACCESS_LEVEL_NAME = "Custom";

	private final String name;
	private final boolean defaultLevel;
	private Integer accessMask;
	
	public AccessLevel(String name, Integer accessMask) {
		this.name = name;
		this.accessMask = accessMask;
		this.defaultLevel = false;
	}

	public AccessLevel(AccessPermissionDescription description) {
		name = description.get_DisplayName();
		accessMask = description.get_AccessMask();
		defaultLevel = description.get_PermissionType().equals( PermissionType.LEVEL_DEFAULT );
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getName();
	}

	public Integer getAccessMask() {
		return accessMask;
	}

	public void setAccessMask(Integer accessMask) {
		this.accessMask = accessMask;
	}
	
	@Override
	public boolean isDefaultLevel() {
		return defaultLevel;
	}
}
