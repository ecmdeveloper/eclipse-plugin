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

package com.ecmdeveloper.plugin.cmis.model.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.commons.data.PermissionMapping;
import org.apache.chemistry.opencmis.commons.definitions.PermissionDefinition;

import com.ecmdeveloper.plugin.core.model.security.IAccessLevel;
import com.ecmdeveloper.plugin.core.model.security.IAccessRight;

/**
 * @author ricardo.belfor
 *
 */
public class AccessLevel implements IAccessLevel {

	public static final String CMIS_READ_PERMISSION = "cmis:read";
	public static final String CMIS_WRITE_PERMISSION = "cmis:write";
	public static final String CMIS_ALL_PERMISSION = "cmis:all";

	private final String name;
	private final String id;
	private final boolean defaultLevel;
	private final List<IAccessRight> accessRights;
	
	public AccessLevel(PermissionDefinition permissionDefinition, Map<String, PermissionMapping> permissionMappings) {
		this.id = permissionDefinition.getId();
		this.name = permissionDefinition.getDescription();
		this.defaultLevel = false;
		
		accessRights = new ArrayList<IAccessRight>();
		addAccessRights(permissionMappings);
	}

	private void addAccessRights(Map<String, PermissionMapping> permissionMappings) {
	    for ( PermissionMapping permissionMapping : permissionMappings.values() ) {
	    	boolean granted = isGranted(id, permissionMapping);
	    	AccessRight accessRight = new AccessRight(permissionMapping.getKey(), granted );
	    	accessRights.add(accessRight);
	    }
	}

	public AccessLevel(String id) {
		this.id = id;
		this.name = id;
		this.defaultLevel = false;
		
		accessRights = new ArrayList<IAccessRight>();
	}
	
	private boolean isGranted(String permission, PermissionMapping permissionMapping) {
		for (String p : permissionMapping.getPermissions() ) {
			if (p.equalsIgnoreCase(permission) ) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String getName() {
		return name != null ? name : id;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public boolean isDefaultLevel() {
		return defaultLevel;
	}

	public String getId() {
		return id;
	}

	public List<IAccessRight> getAccessRights() {
		return accessRights;
	}
}
