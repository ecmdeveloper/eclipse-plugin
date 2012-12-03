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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.chemistry.opencmis.commons.data.Ace;

import com.ecmdeveloper.plugin.core.model.constants.AccessControlEntrySource;
import com.ecmdeveloper.plugin.core.model.constants.AccessControlEntryType;
import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntries;
import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntry;
import com.ecmdeveloper.plugin.core.model.security.IAccessLevel;
import com.ecmdeveloper.plugin.core.model.security.IPrincipal;
import com.ecmdeveloper.plugin.core.model.security.ISecurityPrincipal;
import com.ecmdeveloper.plugin.core.util.collections.AbstractArrayListObserver;
import com.ecmdeveloper.plugin.core.util.collections.ObservableArrayList;

/**
 * @author ricardo.belfor
 *
 */
public class AccessControlEntries implements IAccessControlEntries, PropertyChangeListener {

	private ObservableArrayList<ISecurityPrincipal> securityPrincipals;
	transient private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	private List<IAccessLevel> accessLevels = new ArrayList<IAccessLevel>();
	private boolean readOnly;

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
		AccessControlEntry accessControlEntry = createAccessControlEntry(securityPrincipal,
				AccessControlEntrySource.DIRECT, AccessLevel.CMIS_READ_PERMISSION);
		securityPrincipal.getAccessControlEntries().add(accessControlEntry);

		return accessControlEntry;
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

	private AccessControlEntrySource getSource(Ace permission) {
		if ( permission.isDirect()) {
			return AccessControlEntrySource.DIRECT;
		} else {
			return AccessControlEntrySource.INHERITED;
		}
	}

	private ISecurityPrincipal getPrincipal(Ace ace) {
		ISecurityPrincipal securityPrincipal = findSecurityPrincipal( ace.getPrincipal() );
		if ( securityPrincipal == null ) {
			securityPrincipal = new SecurityPrincipal(ace.getPrincipal(), listeners );
			securityPrincipals.add(securityPrincipal);
		}
		return securityPrincipal;
	}

	private ISecurityPrincipal findSecurityPrincipal(org.apache.chemistry.opencmis.commons.data.Principal principal) {
		String granteeName = principal.getId();
		for ( ISecurityPrincipal securityPrincipal : getPrincipals() ) {
			if (granteeName.equalsIgnoreCase(securityPrincipal.getName() ) ) {
				return securityPrincipal;
			}
		}
		return null;
	}

	@Override
	public Collection<IAccessControlEntry> getAccessControlEntries() {
		Set<IAccessControlEntry> accessControlEntries = new HashSet<IAccessControlEntry>();
		
		for ( ISecurityPrincipal principal : securityPrincipals ) {
			accessControlEntries.addAll( principal.getAccessControlEntries() );
		}

		return accessControlEntries;
	}

	public void addAce(Ace ace) {
		SecurityPrincipal principal = (SecurityPrincipal) getPrincipal(ace);
		AccessControlEntrySource accessControlEntrySource = getSource(ace);

		for ( String permission : ace.getPermissions() ) {
			AccessControlEntry accessControlEntry = createAccessControlEntry(principal, accessControlEntrySource, permission);
			principal.getAccessControlEntries().add(accessControlEntry);
		}
	}

	private AccessControlEntry createAccessControlEntry(SecurityPrincipal principal,
			AccessControlEntrySource accessControlEntrySource, String permission ) {

		AccessControlEntry accessControlEntry = new AccessControlEntry(principal, accessControlEntrySource, listeners );
		accessControlEntry.setType( AccessControlEntryType.ALLOW );
		accessControlEntry.setAccessLevel( getAccessLevel(permission) );
		accessControlEntry.getAllowedAccessLevels().addAll(accessLevels);
		accessControlEntry.setReadOnly( readOnly);
		
		return accessControlEntry;
	}

	private IAccessLevel getAccessLevel(String permission) {
		for ( IAccessLevel accessLevel : accessLevels) {
			if ( permission.equalsIgnoreCase( ((AccessLevel)accessLevel).getId() ) ) {
				return accessLevel;
			}
		}
		AccessLevel accessLevel = new AccessLevel(permission);
		accessLevels.add(accessLevel);
		return accessLevel;
	}

	public void addAccessLevel(AccessLevel accessLevel) {
		accessLevels.add(accessLevel);
	}

	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
}
