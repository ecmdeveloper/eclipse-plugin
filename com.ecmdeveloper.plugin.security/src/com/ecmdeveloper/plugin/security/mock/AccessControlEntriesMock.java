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

import java.util.ArrayList;
import java.util.Collection;

import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntries;
import com.ecmdeveloper.plugin.core.model.security.IPrincipal;

/**
 * @author ricardo.belfor
 *
 */
public class AccessControlEntriesMock implements IAccessControlEntries {

	private ArrayList<IPrincipal> principals;

	String names[] = {"Peter Griffin","Lois Griffin","Meg Griffin","Chris Griffin","Stewie Griffin","Brian Griffin","Glenn Quagmire","Cleveland Brown","Joe Swanson" };
	
	public AccessControlEntriesMock() {
		principals = new ArrayList<IPrincipal>();
		for ( String name : names ) {
			principals.add( new PrincipalMock(name) );
		}
	}

	@Override
	public Collection<IPrincipal> getPrincipals() {
		return principals;
	}
}
