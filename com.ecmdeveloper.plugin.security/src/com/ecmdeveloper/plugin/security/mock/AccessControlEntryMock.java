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

import com.ecmdeveloper.plugin.core.model.constants.AccessControlEntrySource;
import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntry;
import com.ecmdeveloper.plugin.core.model.security.IPrincipal;

/**
 * @author ricardo.belfor
 *
 */
public class AccessControlEntryMock implements IAccessControlEntry {

	private IPrincipal principal;

	public AccessControlEntryMock(IPrincipal principal) {
		this.principal = principal;
	}

	@Override
	public AccessControlEntrySource getSource() {
		return principal.getName().startsWith("C") ? AccessControlEntrySource.INHERITED
				: AccessControlEntrySource.DIRECT;
	}

	@Override
	public String getPermission() {
		return "Full Control";
	}

	@Override
	public IPrincipal getPrincipal() {
		return principal;
	}
}
