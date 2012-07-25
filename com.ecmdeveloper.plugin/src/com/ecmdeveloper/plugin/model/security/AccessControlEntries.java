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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ecmdeveloper.plugin.core.model.constants.AccessControlEntrySource;
import com.ecmdeveloper.plugin.core.model.constants.AccessControlEntryType;
import com.ecmdeveloper.plugin.core.model.constants.PrincipalType;
import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntries;
import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntry;
import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntryPropagation;
import com.ecmdeveloper.plugin.core.model.security.IAccessLevel;
import com.ecmdeveloper.plugin.core.model.security.IPrincipal;
import com.ecmdeveloper.plugin.core.model.security.ISecurityPrincipal;
import com.ecmdeveloper.plugin.core.util.collections.AbstractArrayListObserver;
import com.ecmdeveloper.plugin.core.util.collections.ObservableArrayList;
import com.filenet.api.constants.AccessType;
import com.filenet.api.constants.PermissionSource;
import com.filenet.api.constants.SecurityPrincipalType;
import com.filenet.api.security.AccessPermission;

/**
 * @author ricardo.belfor
 *
 */
public class AccessControlEntries implements IAccessControlEntries, PropertyChangeListener {

	private static final String CUSTOM_ACCESS_CONTROL_ENTRY_PROPAGATION_LABEL = "Custom ({0})";

	private static final List<IAccessControlEntryPropagation> accessControlEntryPropagations = new ArrayList<IAccessControlEntryPropagation>();
	{
		accessControlEntryPropagations.add( new AccessControlEntryPropagation("This object only (no inheritance)", 0 ) );
		accessControlEntryPropagations.add( new AccessControlEntryPropagation("This object and immediate children only", 1 ) );
		accessControlEntryPropagations.add( new AccessControlEntryPropagation("This object and all children (infinite levels deep)", -1 ) );
		accessControlEntryPropagations.add( new AccessControlEntryPropagation("All children (infinite levels deep) but not the object itself", -2 ) );
		accessControlEntryPropagations.add( new AccessControlEntryPropagation("Immediate children only but not this object", -3 ) );
	}
	
	private ObservableArrayList<ISecurityPrincipal> securityPrincipals;
	transient private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	private List<IAccessLevel> accessLevels = new ArrayList<IAccessLevel>();
	private List<AccessRightDescription> accessRightDescriptions = new ArrayList<AccessRightDescription>();
	
	public AccessControlEntries() {
		securityPrincipals = new ObservableArrayList<ISecurityPrincipal>( getArrayListObserver() );
		listeners.addPropertyChangeListener(this);
	}

	private AbstractArrayListObserver<ISecurityPrincipal> getArrayListObserver() {
		return new AbstractArrayListObserver<ISecurityPrincipal>() {

			@Override
			public void onAdd(ISecurityPrincipal element) {
				// TODO Auto-generated method stub
				super.onAdd(element);
			}

			@Override
			public void onRemove(Object obj) {
				// TODO Auto-generated method stub
				super.onRemove(obj);
			}
			
		};
	}

	@Override
	public Collection<ISecurityPrincipal> getPrincipals() {
		return securityPrincipals;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
	}

	@Override
	public IAccessControlEntry addAccessControlEntry(IPrincipal principal) {
		SecurityPrincipal securityPrincipal = getSecurityPrincipal(principal);

		Integer accessMask = getDefaultAccessMask();
		IAccessControlEntryPropagation accessControlEntryPropagation = getAccessControlEntryPropagation(0);
		AccessControlEntry accessControlEntry = createAccessControlEntry(securityPrincipal,
				AccessControlEntrySource.DIRECT, AccessControlEntryType.ALLOW, accessMask,
				accessControlEntryPropagation);
		securityPrincipal.getAccessControlEntries().add(accessControlEntry);
		
		return accessControlEntry;
	}

	private Integer getDefaultAccessMask() {
		Integer accessMask = 0;
		for ( IAccessLevel accessLevel : accessLevels ) {
			if ( accessLevel.isDefaultLevel() ) {
				accessMask = ((AccessLevel)accessLevel).getAccessMask();
				break;
			}
		}
		return accessMask;
	}

	private SecurityPrincipal getSecurityPrincipal(IPrincipal principal) {
		String name = principal.getName();
		SecurityPrincipal securityPrincipal = getSecurityPrincipalByName(name);

		if ( securityPrincipal == null ) {
			securityPrincipal = new SecurityPrincipal( principal, listeners );
			securityPrincipals.add(securityPrincipal);
		}
		return securityPrincipal;
	}

	private SecurityPrincipal getSecurityPrincipalByName(String name) {
		for ( ISecurityPrincipal securityPrincipal : securityPrincipals ) {
			if ( securityPrincipal.getName().equals(name ) ) {
				return (SecurityPrincipal) securityPrincipal;
			}
		}
		return null;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ( evt.getPropertyName().equals( SecurityPrincipal.ACCESS_CONTROL_ENTRY_REMOVED ) ) {
			removeEmptyPrincipals(evt);
		}
	}

	private void removeEmptyPrincipals(PropertyChangeEvent evt) {
		IAccessControlEntry accessControlEntry = (IAccessControlEntry) evt.getNewValue();
		ISecurityPrincipal principal = accessControlEntry.getPrincipal();
		if ( principal.getAccessControlEntries().isEmpty() ) {
			securityPrincipals.remove(principal);
		}
	}

	public void addPermission(AccessPermission permission, Principal principal2) {
		SecurityPrincipal principal = (SecurityPrincipal) getPrincipal(permission, principal2 );
		AccessControlEntry accessControlEntry = createAccessControlEntry(permission, principal);
		principal.getAccessControlEntries().add(accessControlEntry);
	}

	private AccessControlEntry createAccessControlEntry(AccessPermission permission, SecurityPrincipal principal) {
		
		AccessControlEntrySource accessControlEntrySource = getSource(permission);
		AccessControlEntryType accessControlEntryType = getAccessControlEntryType(permission);
		Integer accessMask = permission.get_AccessMask();
		IAccessControlEntryPropagation accessControlEntryPropagation = getAccessControlEntryPropagation(permission.get_InheritableDepth() );
		
		return createAccessControlEntry(principal, accessControlEntrySource, accessControlEntryType, accessMask,
				accessControlEntryPropagation);
	}

	private AccessControlEntry createAccessControlEntry(SecurityPrincipal principal,
			AccessControlEntrySource accessControlEntrySource,
			AccessControlEntryType accessControlEntryType, Integer accessMask,
			IAccessControlEntryPropagation accessControlEntryPropagation) {
		AccessControlEntry accessControlEntry = new AccessControlEntry(principal, accessControlEntrySource, listeners );
		accessControlEntry.setType( accessControlEntryType );
		accessControlEntry.getAllowedAccessLevels().addAll(accessLevels);
		accessControlEntry.getAllowedAccessLevels().add(
				new AccessLevel(AccessLevel.CUSTOM_ACCESS_LEVEL_NAME, accessMask));
		accessControlEntry.setAccessLevel( getAccesLevel(accessMask, accessControlEntry.getAllowedAccessLevels() ) );
		addAccessRights(accessControlEntry, accessMask);
		accessControlEntry.setAccessControlEntryPropagation( accessControlEntryPropagation );

		accessControlEntry.getAllowedAccessControlEntryPropagations().addAll( accessControlEntryPropagations );
		if ( accessControlEntryPropagations.contains(accessControlEntry.getAccessControlEntryPropagation() ) ) {
			accessControlEntry.getAllowedAccessControlEntryPropagations().add(
					accessControlEntry.getAccessControlEntryPropagation());
		}

		return accessControlEntry;
	}

	private IAccessControlEntryPropagation getAccessControlEntryPropagation(Integer inheritableDepth) {
		for ( IAccessControlEntryPropagation accessControlEntryPropagation : accessControlEntryPropagations ) {
			if ( accessControlEntryPropagation.getValue().equals( inheritableDepth ) ) {
				return accessControlEntryPropagation;
			}
		}
		
		String name = MessageFormat.format(CUSTOM_ACCESS_CONTROL_ENTRY_PROPAGATION_LABEL, inheritableDepth);
		return new AccessControlEntryPropagation(name, inheritableDepth);
	}

	@SuppressWarnings("deprecation")
	private AccessControlEntrySource getSource(AccessPermission permission) {
		switch (permission.get_PermissionSource().getValue() ) {
		case PermissionSource.SOURCE_DEFAULT_AS_INT:
			return AccessControlEntrySource.DEFAULT;
		case PermissionSource.SOURCE_DIRECT_AS_INT:
			return AccessControlEntrySource.DIRECT;
		case PermissionSource.PROXY_AS_INT:
		case PermissionSource.SOURCE_PARENT_AS_INT:
		case PermissionSource.SOURCE_TEMPLATE_AS_INT:	
		case PermissionSource.MARKING_AS_INT:
		default:
			return AccessControlEntrySource.INHERITED;
		}
	}

	private AccessControlEntryType getAccessControlEntryType(AccessPermission permission) {
		return permission.get_AccessType().equals(AccessType.ALLOW) ? AccessControlEntryType.ALLOW
				: AccessControlEntryType.DENY;
	}

	private void addAccessRights(AccessControlEntry accessControlEntry, Integer accessMask) {
		for ( AccessRightDescription accessRightDescription : accessRightDescriptions ) {
			boolean granted = (accessMask & accessRightDescription.getAccessMask()) != 0;
			AccessRight accessRight = new AccessRight(accessControlEntry, accessRightDescription
					.getName(), accessRightDescription.getAccessMask(), granted);
			accessControlEntry.getAccessRights().add(accessRight);
		}
	}

	private IAccessLevel getAccesLevel(Integer accessMask, Collection<IAccessLevel> accessLevels) {
		for ( IAccessLevel accessLevel : accessLevels ) {
			if ( ((AccessLevel) accessLevel).getAccessMask().equals(accessMask ) ) {
				return accessLevel;
			}
		}
		throw new IllegalStateException("Access Level should always exists");
	}

	private ISecurityPrincipal getPrincipal(AccessPermission permission, Principal principal) {
		ISecurityPrincipal securityPrincipal = findSecurityPrincipal(principal);
		if ( securityPrincipal == null ) {
			securityPrincipal = new SecurityPrincipal(principal, listeners );
			securityPrincipals.add(securityPrincipal);
		}
		return securityPrincipal;
	}

	private ISecurityPrincipal findSecurityPrincipal(Principal principal) {
		String granteeName = principal.getName();
		PrincipalType type = principal.getType(); 
		for ( ISecurityPrincipal securityPrincipal : getPrincipals() ) {
			if (granteeName.equalsIgnoreCase(securityPrincipal.getName())
					&& type.equals(securityPrincipal.getType())) {
				return securityPrincipal;
			}
		}
		return null;
	}

	private PrincipalType getPrincipalType(AccessPermission permission) {
		return permission.get_GranteeType().equals(SecurityPrincipalType.GROUP) ? PrincipalType.GROUP
				: (permission.get_GranteeType().equals(SecurityPrincipalType.USER) ? PrincipalType.USER
						: PrincipalType.UNKNOWN);
	}

	public void addAccessLevel(AccessLevel accessLevel) {
		accessLevels.add(accessLevel);
	}

	public void addAccessRightDescription(AccessRightDescription accessRightDescription) {
		accessRightDescriptions.add(accessRightDescription);
	}
}
