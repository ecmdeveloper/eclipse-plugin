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

import org.eclipse.core.runtime.IProgressMonitor;

import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.constants.PrincipalType;
import com.ecmdeveloper.plugin.core.model.security.IPrincipal;
import com.ecmdeveloper.plugin.core.model.security.IRealm;

/**
 * @author ricardo.belfor
 *
 */
public class RealmMock implements IRealm {

	private static ArrayList<IPrincipal> resources = new ArrayList<IPrincipal>();

	static {
		generateRescourcesTestCases('A', 'C', 8, ""); //$NON-NLS-1$
		generateRescourcesTestCases('a', 'c', 4, ""); //$NON-NLS-1$
	}

	private static void generateRescourcesTestCases(char startChar, char endChar, int length, String resource){
		for (char ch = startChar; ch <= endChar; ch++) {
			String res = resource + String.valueOf(ch);
			if (length == res.length()) 
				resources.add( new PrincipalMock(res, res.startsWith("C")? PrincipalType.GROUP : PrincipalType.USER ) );
			else if ((res.trim().length() % 2) == 0)
				generateRescourcesTestCases(Character.toUpperCase((char)(startChar + 1)), Character.toUpperCase((char)(endChar + 1)), length, res);
			else 
				generateRescourcesTestCases(Character.toLowerCase((char)(startChar + 1)), Character.toLowerCase((char)(endChar + 1)), length, res);
		}
	}
	
	private final String name;
	private final SpecialAccountPrincipalMock authenticatedUsers = new SpecialAccountPrincipalMock("#AUTHENTICATED-USERS" );
	private final SpecialAccountPrincipalMock creatorOwner = new SpecialAccountPrincipalMock("#CREATOR-OWNER");
	private final IObjectStore objectStore;

	public RealmMock(String name, IObjectStore objectStore) {
		this.name = name;
		this.objectStore = objectStore;
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
	public Collection<IPrincipal> findPrincipals(String pattern, PrincipalType type, IProgressMonitor progressMonitor) {
		
		Collection<IPrincipal> selection = new ArrayList<IPrincipal>();
		
		if ( PrincipalType.SPECIAL_ACCOUNT.equals(type) ) {
			selection.add( creatorOwner );
			selection.add( authenticatedUsers );
			return selection;
		}

		for ( IPrincipal principal : resources ) {
			if ( (principal.getType().equals( type ) ) ) {
				if ( principal.getName().toLowerCase().startsWith(pattern.toLowerCase()) ) {
					selection.add(principal);
					
//					try {
//					Thread.sleep(1);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				if ( progressMonitor != null && progressMonitor.isCanceled() ) {
					System.err.println("Canceled!");
					return null;
				}
					
				}
			}
		}

		System.err.println( selection.size() + " found");
		return selection;
	}

	@Override
	public Collection<IPrincipal> findPrincipals(String pattern, PrincipalType type) {
		return findPrincipals(pattern, type, null);
	}

	@Override
	public IObjectStore getObjectStore() {
		return objectStore;
	}
}
