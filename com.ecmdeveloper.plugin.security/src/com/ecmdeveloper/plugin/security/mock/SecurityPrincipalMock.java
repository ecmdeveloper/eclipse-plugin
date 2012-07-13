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
import java.util.Collection;
import java.util.List;

import com.ecmdeveloper.plugin.core.model.constants.PrincipalType;
import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntry;
import com.ecmdeveloper.plugin.core.model.security.ISecurityPrincipal;
import com.ecmdeveloper.plugin.core.util.collections.AbstractArrayListObserver;
import com.ecmdeveloper.plugin.core.util.collections.ObservableArrayList;

/**
 * @author ricardo.belfor
 *
 */
public class SecurityPrincipalMock extends PrincipalMock implements ISecurityPrincipal{

	public static final String ACCESS_CONTROL_ENTRY_ADDED = "ACCESS_CONTROL_ENTRY_ADDED";
	public static final String ACCESS_CONTROL_ENTRY_REMOVED = "ACCESS_CONTROL_ENTRY_REMOVED";
	
	private final ObservableArrayList<IAccessControlEntry> accessControlEntries = new ObservableArrayList<IAccessControlEntry>();
	private final PropertyChangeSupport listeners;
	
	public SecurityPrincipalMock(String name, PrincipalType principalType, PropertyChangeSupport listeners) {
		super(name, principalType);
		this.listeners = listeners;
		accessControlEntries.registerObserver( getAccessControlEntriesListener() );
		accessControlEntries.add(new AccessControlEntryMock(this, listeners) );
	}

	private AbstractArrayListObserver<IAccessControlEntry> getAccessControlEntriesListener() {

		return new AbstractArrayListObserver<IAccessControlEntry>() {

			@Override
			public void onAdd(IAccessControlEntry element) {
				SecurityPrincipalMock.this.listeners.firePropertyChange(ACCESS_CONTROL_ENTRY_ADDED, null, element);
			}

			@Override
			public void onRemove(Object obj) {
				SecurityPrincipalMock.this.listeners.firePropertyChange(ACCESS_CONTROL_ENTRY_REMOVED, null, obj);
			}

			@Override
			public void onRemoveAll(Collection<?> c) {
				for ( Object object : c) {
					onRemove(object);
				}
			}
		};
	}

	@Override
	public List<IAccessControlEntry> getAccessControlEntries() {
		return accessControlEntries;
	}
}
