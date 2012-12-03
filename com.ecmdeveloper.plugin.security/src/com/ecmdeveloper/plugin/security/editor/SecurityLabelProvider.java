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

import java.text.MessageFormat;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.ecmdeveloper.plugin.core.model.constants.AccessControlEntrySource;
import com.ecmdeveloper.plugin.core.model.constants.AccessControlEntryType;
import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntry;
import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntryPropagation;
import com.ecmdeveloper.plugin.core.model.security.IPrincipal;
import com.ecmdeveloper.plugin.core.model.security.ISecurityPrincipal;
import com.ecmdeveloper.plugin.security.Activator;
import com.ecmdeveloper.plugin.security.util.IconFiles;

/**
 * @author ricardo.belfor
 *
 */
public class SecurityLabelProvider extends LabelProvider implements ITableLabelProvider {

	private static final String EMPTY_STRING = "";

	@Override
	public Image getImage(Object element) {
		if ( element instanceof IPrincipal ) {
			return getPrincipalImage(element);
		} else if ( element instanceof IAccessControlEntry ) { 
			return getAccessControlEntryImage(element);
		}
		
		return super.getImage(element);
	}

	private Image getAccessControlEntryImage(Object element) {
		final IAccessControlEntry ace = (IAccessControlEntry) element;
//		if ( ace.getSource().equals( AccessControlEntrySource.INHERITED ) ) {
//			return Activator.getImage(IconFiles.INHERITED);
//		} else if ( ace.getSource().equals( AccessControlEntrySource.DEFAULT) ) {
//			if ( ace.getType().equals(AccessControlEntryType.ALLOW) ) {
//				return Activator.getImage(IconFiles.DEFAULT);
//			} else {
//				return Activator.getImage(IconFiles.DIRECT_DENY);
//			}
//		} else if ( ace.getSource().equals( AccessControlEntrySource.DIRECT) ) {
//			if ( ace.getType().equals(AccessControlEntryType.ALLOW) ) {
//				return Activator.getImage(IconFiles.DIRECT);
//			} else {
//				return Activator.getImage(IconFiles.DIRECT_DENY);
//			}
//		}
		if ( ace.canDelete() ) {
			if ( ace.getType().equals(AccessControlEntryType.ALLOW) ) {
				return Activator.getImage(IconFiles.ALLOW);
			} else {
				return Activator.getImage(IconFiles.DENY);
			}
		} else {
			if ( ace.getType().equals(AccessControlEntryType.ALLOW) ) {
				return Activator.getImage(IconFiles.ALLOW_READ_ONY);
			} else {
				return Activator.getImage(IconFiles.DENY_READ_ONLY);
			}
		}
	}

	private Image getPrincipalImage(Object element) {
		final IPrincipal ace = (IPrincipal) element;
		if ( ace.isGroup() ) {
			return Activator.getImage(IconFiles.GROUP);
		} else if ( ace.isSpecialAccount() ) {
			return Activator.getImage(IconFiles.SPECIAL_ACCOUNT);
		} else {
			return Activator.getImage(IconFiles.USER);
		}
	}
	
	@Override
	public String getText(Object element) {
		if ( element instanceof IPrincipal ) {
			return ((IPrincipal) element).getDisplayName();
		} else if ( element instanceof IAccessControlEntry ) {
			IAccessControlEntry accessControlEntry = (IAccessControlEntry) element;
			IAccessControlEntryPropagation accessControlEntryPropagation = accessControlEntry.getAccessControlEntryPropagation();
			if ( accessControlEntryPropagation != null) {
				String propagation = accessControlEntryPropagation.getName();
				String name = accessControlEntry.getAccessLevel().getName();
				return MessageFormat.format("{0} - {1}", name, propagation) ;
			}
			else 
			{
				return accessControlEntry.getAccessLevel().getName();
			}
		}
		return super.getText(element);
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if ( columnIndex == 0 ) {
			return getImage(element);
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if ( columnIndex == 0 ) {
			return getText(element);
		} else if ( columnIndex == 1 ) {
			if ( element instanceof IAccessControlEntry ) {
				return ((IAccessControlEntry) element).getAccessControlEntryPropagation().getName();
			}
		}
		return EMPTY_STRING;
	}
}
