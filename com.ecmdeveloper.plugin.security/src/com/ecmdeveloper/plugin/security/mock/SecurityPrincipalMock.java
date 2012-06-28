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
import java.util.List;

import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntry;
import com.ecmdeveloper.plugin.core.model.security.ISecurityPrincipal;

/**
 * @author ricardo.belfor
 *
 */
public class SecurityPrincipalMock extends PrincipalMock implements ISecurityPrincipal{

	private final List<IAccessControlEntry> accessControlEntries = new ArrayList<IAccessControlEntry>();
	
	public SecurityPrincipalMock(String name, PropertyChangeSupport listeners) {
		super(name);
		accessControlEntries.add(new AccessControlEntryMock(this, listeners) );
	}

	@Override
	public List<IAccessControlEntry> getAccessControlEntries() {
		return accessControlEntries;
	}
}
