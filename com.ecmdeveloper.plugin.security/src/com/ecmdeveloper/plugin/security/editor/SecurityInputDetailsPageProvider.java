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

package com.ecmdeveloper.plugin.security.editor;

import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;

import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntry;
import com.ecmdeveloper.plugin.core.model.security.IPrincipal;

/**
 * @author ricardo.belfor
 *
 */
public class SecurityInputDetailsPageProvider implements IDetailsPageProvider {

	private static final String ACCESS_CONTROL_ENTRY_VIEW_PAGE_KEY = "accessControlEntryPage";
	private static final String ACCESS_CONTROL_ENTRY_EDIT_PAGE_KEY = "accessControlEditEntryPage";
	private static final String PRINCIPAL_PAGE_KEY = "principalPage";
	private IDetailsPage unknownDetailsPage;
	private IDetailsPage accessControlEntryDetailsViewPage;
	private IDetailsPage accessControlEntryDetailsEditPage;
	
	@Override
	public IDetailsPage getPage(Object key) {

		if ( ACCESS_CONTROL_ENTRY_VIEW_PAGE_KEY.equals(key) ) {
			return getAccessControlEntryDetailsViewPage();
		} else if ( ACCESS_CONTROL_ENTRY_EDIT_PAGE_KEY.equals(key) ) {
			return getAccessControlEntryDetailsEditPage();
		}
		
		if ( unknownDetailsPage == null ) {
			unknownDetailsPage = new PermissionDetailsPage();
		}
		return unknownDetailsPage;
	}

	private IDetailsPage getAccessControlEntryDetailsEditPage() {
		if ( accessControlEntryDetailsEditPage == null ) {
			accessControlEntryDetailsEditPage = new AccessControlEntryDetailsEditPage();
		}
		return accessControlEntryDetailsEditPage;
	}

	private IDetailsPage getAccessControlEntryDetailsViewPage() {
		if ( accessControlEntryDetailsViewPage == null) {
			accessControlEntryDetailsViewPage = new AccessControlEntryDetailsViewPage();
		}
		return accessControlEntryDetailsViewPage;
	}

	@Override
	public Object getPageKey(Object object) {
		if (object instanceof IPrincipal ) {
			return PRINCIPAL_PAGE_KEY;
		} else if (object instanceof IAccessControlEntry ) {
			if ( ((IAccessControlEntry) object).isEditable() ) {
				return ACCESS_CONTROL_ENTRY_EDIT_PAGE_KEY;
			} else {
				return ACCESS_CONTROL_ENTRY_VIEW_PAGE_KEY;
			}
		}
		return null;
	}
}
