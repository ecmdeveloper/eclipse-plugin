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

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.ecmdeveloper.plugin.core.model.constants.AccessControlEntrySource;
import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntry;
import com.ecmdeveloper.plugin.core.model.security.IPrincipal;
import com.ecmdeveloper.plugin.security.Activator;
import com.ecmdeveloper.plugin.security.util.IconFiles;

/**
 * @author ricardo.belfor
 *
 */
public class SecurityLabelProvider extends LabelProvider implements IBaseLabelProvider {

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
		if ( ace.getSource().equals( AccessControlEntrySource.INHERITED ) ) {
			return Activator.getImage(IconFiles.INHERITED);
		} else if ( ace.getSource().equals( AccessControlEntrySource.DIRECT) ) {
			return Activator.getImage(IconFiles.DIRECT);
		}
		return null;
	}

	private Image getPrincipalImage(Object element) {
		final IPrincipal ace = (IPrincipal) element;
		if ( ace.isGroup() == null || ace.isGroup() ) {
			return Activator.getImage(IconFiles.GROUP);
		} else {
			return Activator.getImage(IconFiles.USER);
		}
	}
	
	@Override
	public String getText(Object element) {
		if ( element instanceof IPrincipal ) {
			return ((IPrincipal) element).getName();
		} else if ( element instanceof IAccessControlEntry ) {
			return ((IAccessControlEntry) element).getPermission();
		}
		return super.getText(element);
	}
}
