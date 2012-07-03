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

import com.ecmdeveloper.plugin.core.model.constants.PrincipalType;
import com.ecmdeveloper.plugin.core.model.security.IPrincipal;

/**
 * @author ricardo.belfor
 *
 */
public class SpecialAccountPrincipalMock implements IPrincipal {

	private final String name;

	public SpecialAccountPrincipalMock(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public PrincipalType getType() {
		return PrincipalType.SPECIAL_ACCOUNT;
	}

	@Override
	public boolean isGroup() {
		return PrincipalType.GROUP.equals(getType() );
	}

	@Override
	public boolean isSpecialAccount() {
		return PrincipalType.SPECIAL_ACCOUNT.equals( getType() );
	}

	@Override
	public boolean isUser() {
		return PrincipalType.USER.equals( getType() );
	}
}
