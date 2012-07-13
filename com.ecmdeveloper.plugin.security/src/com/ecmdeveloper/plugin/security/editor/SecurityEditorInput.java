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

import java.util.Collection;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntries;
import com.ecmdeveloper.plugin.core.model.security.IRealm;

/**
 * @author ricardo.belfor
 *
 */
public class SecurityEditorInput implements IEditorInput {

	private final IObjectStoreItem objectStoreItem;
	private final IAccessControlEntries accessControlEntries;
	private final Collection<IRealm> realms;

	public SecurityEditorInput(IObjectStoreItem objectStoreItem, IAccessControlEntries accessControlEntries, Collection<IRealm> realms) {
		this.objectStoreItem = objectStoreItem;
		this.accessControlEntries = accessControlEntries;
		this.realms = realms;
	}	

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return objectStoreItem.getDisplayName();
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return getName();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		if ( adapter.equals( IObjectStoreItem.class) ) {
			return objectStoreItem;
		} else if ( adapter.equals( IAccessControlEntries.class ) ) {
			return accessControlEntries;
		}
		return null;
	}

	public Collection<IRealm> getRealms() {
		return realms;
	}
}
