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

import com.ecmdeveloper.plugin.core.model.constants.PrincipalType;
import com.ecmdeveloper.plugin.core.model.security.IPrincipal;
import com.filenet.api.security.Group;
import com.filenet.api.security.User;

/**
 * @author ricardo.belfor
 *
 */
public class Principal implements IPrincipal {

	private Object internalPrincipal;
	private final String name;
	private final String displayName;
	private final PrincipalType type;
	private final String shortName;
	
	public Principal(String name, String shortName, String displayName, PrincipalType type) {
		this.name = name;
		this.displayName = displayName;
		this.type = isSpecialAccountName(name) ? PrincipalType.SPECIAL_ACCOUNT : type;
		this.shortName = shortName;
	}

	public Principal(String name, PrincipalType type) {
		this.name = name;
		this.displayName = name;
		this.type = isSpecialAccountName(name) ? PrincipalType.SPECIAL_ACCOUNT : type;
		this.shortName = name;
	}
	
	public Principal(Object internalPrincipal) {
		
		this.internalPrincipal = internalPrincipal;
		
		if (internalPrincipal instanceof Group) {
			Group group = (Group) internalPrincipal;
			displayName = group.get_DisplayName();
			name = group.get_Name();
			shortName = group.get_ShortName();
			if ( isSpecialAccountName(name) ) {
				type = PrincipalType.SPECIAL_ACCOUNT;
			} else {
				type = PrincipalType.GROUP;
			}
		} else if (internalPrincipal instanceof User ) {
			User user = (User) internalPrincipal;
			displayName = user.get_DisplayName();
			name = user.get_Name();
			type = PrincipalType.USER;
			shortName = user.get_ShortName();
		} else {
			displayName = "";
			name = "";
			type = PrincipalType.UNKNOWN;
			shortName = "";
		}
	}

	protected boolean isSpecialAccountName(String name) {
		return name.equals( com.filenet.api.constants.SpecialPrincipal.AUTHENTICATED_USERS.getValue() ) ||
				name.equals( com.filenet.api.constants.SpecialPrincipal.CREATOR_OWNER.getValue() );
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public PrincipalType getType() {
		return type;
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

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String getShortName() {
		return shortName;
	}
}
