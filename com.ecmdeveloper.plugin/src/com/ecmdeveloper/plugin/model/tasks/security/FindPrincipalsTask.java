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

package com.ecmdeveloper.plugin.model.tasks.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ecmdeveloper.plugin.core.model.constants.PrincipalType;
import com.ecmdeveloper.plugin.core.model.security.IPrincipal;
import com.ecmdeveloper.plugin.core.model.tasks.security.IFindPrincipalsTask;
import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.model.security.Principal;
import com.ecmdeveloper.plugin.model.security.Realm;
import com.ecmdeveloper.plugin.model.tasks.BaseTask;
import com.filenet.api.collection.GroupSet;
import com.filenet.api.collection.UserSet;
import com.filenet.api.constants.PrincipalSearchAttribute;
import com.filenet.api.constants.PrincipalSearchSortType;
import com.filenet.api.constants.PrincipalSearchType;

/**
 * @author ricardo.belfor
 *
 */
public class FindPrincipalsTask  extends BaseTask implements IFindPrincipalsTask {

	private static final String STAR_STRING = "*";

	private final Realm realm;
	private final String pattern;
	private final PrincipalType type;
	@SuppressWarnings("unused")
	private final IProgressMonitor progressMonitor;
	private final ArrayList<IPrincipal> principals = new ArrayList<IPrincipal>();
	
	public FindPrincipalsTask(Realm realm, String pattern, PrincipalType type, IProgressMonitor progressMonitor) {
		this.realm = realm;
		this.pattern = pattern;
		this.type = type;
		this.progressMonitor = progressMonitor;
	}

	@Override
	protected Object execute() throws Exception {
		int starCount = getStartCount(pattern);
		PrincipalSearchType searchType = getPrincipalSearchType(pattern, starCount);
		String searchPattern = getSearchPattern(pattern, starCount);
		Iterator iterator = findPrincipals(searchType, searchPattern);

		if ( iterator != null ) {
			while ( iterator.hasNext() ) {
				principals.add( new Principal(iterator.next() ) );
			}
		}
		
		return null;
	}

	@Override
	protected ContentEngineConnection getContentEngineConnection() {
		return (ContentEngineConnection) realm.getObjectStore().getConnection();
	}

	@Override
	public Collection<IPrincipal> getPrincipals() {
		return principals;
	}

	@SuppressWarnings("unchecked")
	private Iterator findPrincipals(PrincipalSearchType searchType, String searchPattern) {
		com.filenet.api.security.Realm internalRealm = realm.getInternalRealm();
		Iterator iterator = null;
		
		if ( PrincipalType.GROUP.equals(type) ) {
			GroupSet groups = internalRealm.findGroups(searchPattern, searchType, PrincipalSearchAttribute.DISPLAY_NAME, PrincipalSearchSortType.ASCENDING, null, null );
			iterator = groups.iterator();
		} else if ( PrincipalType.USER.equals(type) ) {
			UserSet users = internalRealm.findUsers(searchPattern, searchType, PrincipalSearchAttribute.DISPLAY_NAME, PrincipalSearchSortType.ASCENDING, null, null );
			iterator = users.iterator();
		}
		return iterator;
	}

	private String getSearchPattern(String pattern, int starCount) {
		if ( starCount == 0 ) { 
		} else if (starCount == 1 && pattern.endsWith(STAR_STRING) ) {
			pattern = pattern.substring(0, pattern.length() - 2);
		} else if (starCount == 1 && pattern.endsWith(STAR_STRING) ) {
			pattern = pattern.substring(1);
		}
		return pattern;
	}

	private PrincipalSearchType getPrincipalSearchType(String pattern, int starCount) {
		PrincipalSearchType prefixMatch;
		if ( starCount == 0 ) { 
			prefixMatch = PrincipalSearchType.PREFIX_MATCH;
		} else if (starCount == 1 && pattern.endsWith(STAR_STRING) ) {
			prefixMatch = PrincipalSearchType.PREFIX_MATCH;
		} else if (starCount == 1 && pattern.endsWith(STAR_STRING) ) {
			prefixMatch = PrincipalSearchType.SUFFIX_MATCH;
		} else {
			prefixMatch = PrincipalSearchType.CUSTOM;
		}
		return prefixMatch;
	}

	private int getStartCount(String string) {
		int count = 0;
		for (int i = 0; i < string.length(); ++i) {
			if (string.charAt(i) == '*') {
				count += 1;
			}
		}
		return count;
	}
}
