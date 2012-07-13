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
import java.util.Collection;

import com.ecmdeveloper.plugin.core.model.constants.PrincipalType;
import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntries;
import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntry;
import com.ecmdeveloper.plugin.core.model.security.IPrincipal;
import com.ecmdeveloper.plugin.core.model.security.ISecurityPrincipal;
import com.ecmdeveloper.plugin.core.util.collections.AbstractArrayListObserver;
import com.ecmdeveloper.plugin.core.util.collections.ObservableArrayList;
import com.filenet.api.constants.SecurityPrincipalType;
import com.filenet.api.security.AccessPermission;

/**
 * @author ricardo.belfor
 *
 */
public class AccessControlEntries implements IAccessControlEntries, PropertyChangeListener {

	private ObservableArrayList<ISecurityPrincipal> securityPrincipals;
	transient private PropertyChangeSupport listeners = new PropertyChangeSupport(this);

	public AccessControlEntries() {
		securityPrincipals = new ObservableArrayList<ISecurityPrincipal>( getArrayListObserver() );
//		for ( String name : names ) {
//			securityPrincipals.add( new SecurityPrincipalMock(name, name.startsWith("C")? PrincipalType.GROUP : PrincipalType.USER, listeners) );
//		}
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
//		String name = principal.getName();
//		ISecurityPrincipal securityPrincipal = getSecurityPrincipalByName(name);
//		IAccessControlEntry accessControlEntryMock;
//
//		if ( securityPrincipal == null ) {
//			securityPrincipal = new SecurityPrincipalMock( principal.getName(), principal.getType(), listeners );
//			securityPrincipals.add(securityPrincipal);
//			accessControlEntryMock = securityPrincipal.getAccessControlEntries().iterator().next();
//		} else {
//			accessControlEntryMock = new AccessControlEntryMock(securityPrincipal, listeners);
//			securityPrincipal.getAccessControlEntries().add( accessControlEntryMock );
//		}
//
//		return accessControlEntryMock;
		return null;
	}

	private ISecurityPrincipal getSecurityPrincipalByName(String name) {
		for ( ISecurityPrincipal securityPrincipal : securityPrincipals ) {
			if ( securityPrincipal.getName().equals(name ) ) {
				return securityPrincipal;
			}
		}
		return null;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
//		if ( evt.getPropertyName().equals( SecurityPrincipalMock.ACCESS_CONTROL_ENTRY_REMOVED ) ) {
//			removeEmptyPrincipals(evt);
//		}
	}

	private void removeEmptyPrincipals(PropertyChangeEvent evt) {
		IAccessControlEntry accessControlEntry = (IAccessControlEntry) evt.getNewValue();
		ISecurityPrincipal principal = accessControlEntry.getPrincipal();
		if ( principal.getAccessControlEntries().isEmpty() ) {
			securityPrincipals.remove(principal);
		}
	}

	public void addPermission(AccessPermission permission) {
		
		ISecurityPrincipal principal = getPrincipal(permission); 

		System.out.println( principal.toString() );
		
	}

	private ISecurityPrincipal getPrincipal(AccessPermission permission) {
		ISecurityPrincipal principal = findPrincipal(permission);
		if ( principal == null ) {
			principal = new SecurityPrincipal(permission, listeners );
		}
		return principal;
	}

	private ISecurityPrincipal findPrincipal(AccessPermission permission) {
		String granteeName = permission.get_GranteeName();
		PrincipalType type = getPrincipalType(permission); 
		for ( ISecurityPrincipal principal : getPrincipals() ) {
			if ( granteeName.equalsIgnoreCase(principal.getName() ) && type.equals(principal.getType() ) ) {
				return principal;
			}
		}
		return null;
	}

	private PrincipalType getPrincipalType(AccessPermission permission) {
		return permission.get_GranteeType().equals(SecurityPrincipalType.GROUP) ? PrincipalType.GROUP
				: (permission.get_GranteeType().equals(SecurityPrincipalType.USER) ? PrincipalType.USER
						: PrincipalType.UNKNOWN);
	}

}
