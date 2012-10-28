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

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ecmdeveloper.plugin.core.model.constants.AccessControlEntrySource;
import com.ecmdeveloper.plugin.core.model.constants.AccessControlEntryType;
import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntry;
import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntryPropagation;
import com.ecmdeveloper.plugin.core.model.security.IAccessLevel;
import com.ecmdeveloper.plugin.core.model.security.IAccessRight;
import com.ecmdeveloper.plugin.core.model.security.ISecurityPrincipal;

/**
 * @author ricardo.belfor
 *
 */
public class AccessControlEntry implements IAccessControlEntry {

	private static final String ACCESS_LEVEL_CHANGED = "ACCESS_LEVEL";
	private static final String ACCESS_CONTROL_ENTRY_PROPAGATION_CHANGED = "ACCESS_CONTROL_ENTRY_PROPAGATION";
	private final ISecurityPrincipal securityPrincipal;
	private final List<IAccessRight> accessRights;
	private final AccessControlEntrySource accessControlEntrySource;
	private AccessControlEntryType accessControlEntryType;
	private IAccessControlEntryPropagation accessControlEntryPropagation;
	private IAccessLevel accessLevel;
	private final Collection<IAccessLevel> allowedAccessLevels = new ArrayList<IAccessLevel>();
	private final Collection<IAccessControlEntryPropagation> accessControlEntryPropagations;
	private final PropertyChangeSupport listeners; 
	private final boolean readOnly;
	
	public AccessControlEntry(ISecurityPrincipal securityPrincipal,
			AccessControlEntrySource accessControlEntrySource, PropertyChangeSupport listeners, boolean readOnly) {
		
		this.securityPrincipal = securityPrincipal;
		this.accessControlEntrySource = accessControlEntrySource;
		this.listeners = listeners;
		this.readOnly = readOnly;
		
		accessRights = new ArrayList<IAccessRight>();
		accessControlEntryPropagations = new ArrayList<IAccessControlEntryPropagation>();
	}

	@Override
	public AccessControlEntrySource getSource() {
		return accessControlEntrySource;
	}

	@Override
	public ISecurityPrincipal getPrincipal() {
		return securityPrincipal;
	}

	@Override
	public List<IAccessRight> getAccessRights() {
		return accessRights;
	}

	@Override
	public boolean isEditable() {
		return !readOnly && ( getSource().equals(AccessControlEntrySource.DIRECT)
				|| getSource().equals(AccessControlEntrySource.DEFAULT) );
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
		AccessControlEntryType oldValue = this.accessControlEntryType;
		this.accessControlEntryType = accessControlEntryType;
		listeners.firePropertyChange("ACCESS_CONTROL_ENTRY_TYPE", oldValue, accessControlEntryType );
	}

	@Override
	public IAccessLevel getAccessLevel() {
		return accessLevel;
	}

	@Override
	public void setAccessLevel(IAccessLevel accessLevel) {
		IAccessLevel oldValue = this.accessLevel;
		this.accessLevel = accessLevel;
		
		for ( IAccessRight accessRight : accessRights ) {
			accessRight.setGranted(accessLevel);
		}
		listeners.firePropertyChange(ACCESS_LEVEL_CHANGED, oldValue, accessLevel );
	}

	@Override
	public Collection<IAccessLevel> getAllowedAccessLevels() {
		return allowedAccessLevels;
	}

	@Override
	public IAccessControlEntryPropagation getAccessControlEntryPropagation() {
		return accessControlEntryPropagation;
	}

	@Override
	public void setAccessControlEntryPropagation(
			IAccessControlEntryPropagation accessControlEntryPropagation) {
		IAccessControlEntryPropagation oldValue = this.accessControlEntryPropagation; 
		this.accessControlEntryPropagation = accessControlEntryPropagation;
		listeners.firePropertyChange(ACCESS_CONTROL_ENTRY_PROPAGATION_CHANGED, oldValue, accessControlEntryPropagation );
	}

	@Override
	public Collection<IAccessControlEntryPropagation> getAllowedAccessControlEntryPropagations() {
		return accessControlEntryPropagations;
	}

	private IAccessLevel getAccessLevel(Integer accessMask) {
		IAccessLevel customAccessLevel = null;
		for ( IAccessLevel accessLevel : allowedAccessLevels ) {
			if ( ((AccessLevel)accessLevel).getAccessMask().equals( accessMask ) ) {
				return accessLevel;
			}
			
			if ( accessLevel.getName().equals(AccessLevel.CUSTOM_ACCESS_LEVEL_NAME) ) {
				customAccessLevel = accessLevel;
			}
		}
		
		((AccessLevel)customAccessLevel).setAccessMask(accessMask);
		return customAccessLevel;
	}

	public Integer getAccessMask() {
		Integer accessMask = 0;
		for ( IAccessRight accessRight : accessRights ) {
			if ( accessRight.isGranted() ) {
				accessMask |= ((AccessRight)accessRight).getMask();
			}
		}
		return accessMask;
	}

	void onAccessRightChanged(AccessRight accessRight) {
		setAccessLevel( getAccessLevel( getAccessMask() ) );
	}

	@Override
	public boolean isContentEngine() {
		return true;
	}
}
