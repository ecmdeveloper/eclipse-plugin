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

package com.ecmdeveloper.plugin.security.mock;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ecmdeveloper.plugin.core.model.constants.AccessControlEntrySource;
import com.ecmdeveloper.plugin.core.model.constants.AccessControlEntryType;
import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntry;
import com.ecmdeveloper.plugin.core.model.security.IAccessLevel;
import com.ecmdeveloper.plugin.core.model.security.IAccessRight;
import com.ecmdeveloper.plugin.core.model.security.IPrincipal;

/**
 * @author ricardo.belfor
 *
 */
public class AccessControlEntryMock implements IAccessControlEntry {

	private final IPrincipal principal;
	private final List<IAccessRight> accessRights;
	private AccessControlEntryType accessControlEntryType;
	private final IAccessLevel accessLevel;
	private final Collection<IAccessLevel> allowedAccessLevels;
	
	public AccessControlEntryMock(IPrincipal principal, PropertyChangeSupport listeners) {
		this.principal = principal;
		accessRights = new ArrayList<IAccessRight>();
		boolean granted = principal.getName().endsWith("n");
		accessRights.add( new AccessRightMock("Read", granted, listeners) );
		accessRights.add( new AccessRightMock("Write", true, listeners ) );
		accessRights.add( new AccessRightMock("Delete", granted, listeners ) );
		accessRights.add( new AccessRightMock("View Content", true, listeners ) );
		accessRights.add( new AccessRightMock("Change Owner", granted, listeners ) );

		
		accessLevel = new AccessLevelMock("Full Control");
		allowedAccessLevels = new ArrayList<IAccessLevel>();
		allowedAccessLevels.add(accessLevel);
		allowedAccessLevels.add( new AccessLevelMock("Modify properties" ) );
		allowedAccessLevels.add( new AccessLevelMock("Link" ) );
		allowedAccessLevels.add( new AccessLevelMock("View properties" ) );
		allowedAccessLevels.add( new AccessLevelMock("Custom" ) );
	}

	@Override
	public AccessControlEntrySource getSource() {
		return principal.getName().startsWith("C") ? AccessControlEntrySource.INHERITED
				: AccessControlEntrySource.DIRECT;
	}

	@Override
	public IPrincipal getPrincipal() {
		return principal;
	}

	@Override
	public List<IAccessRight> getAccessRights() {
		return accessRights;
	}

	@Override
	public boolean isEditable() {
		return getSource().equals( AccessControlEntrySource.DIRECT);
	}

	@Override
	public boolean canDelete() {
		return getSource().equals( AccessControlEntrySource.DIRECT);
	}

	@Override
	public AccessControlEntryType getType() {
		return accessControlEntryType;
	}

	@Override
	public void setType(AccessControlEntryType accessControlEntryType) {
		this.accessControlEntryType = accessControlEntryType;
	}

	@Override
	public IAccessLevel getAccessLevel() {
		return accessLevel;
	}

	@Override
	public Collection<IAccessLevel> getAllowedAccessLevels() {
		return allowedAccessLevels;
	}
}
