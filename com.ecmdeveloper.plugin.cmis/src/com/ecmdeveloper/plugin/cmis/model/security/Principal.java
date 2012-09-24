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

import com.ecmdeveloper.plugin.core.model.constants.PrincipalType;
import com.ecmdeveloper.plugin.core.model.security.IPrincipal;
import com.ecmdeveloper.plugin.core.model.security.IRealm;

/**
 * @author ricardo.belfor
 *
 */
public class Principal implements IPrincipal {

	private String name;

	public Principal(org.apache.chemistry.opencmis.commons.data.Principal internalPrincipal) {
		name = internalPrincipal.getId();
	}

	@Override
	public String getDisplayName() {
		return name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public IRealm getRealm() {
		return null;
	}

	@Override
	public String getShortName() {
		return name;
	}

	@Override
	public PrincipalType getType() {
		return PrincipalType.UNKNOWN;
	}

	@Override
	public boolean isGroup() {
		return false;
	}

	@Override
	public boolean isSpecialAccount() {
		return false;
	}

	@Override
	public boolean isUser() {
		return false;
	}
}
