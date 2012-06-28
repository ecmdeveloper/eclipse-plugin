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
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IEditorInput;

import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntries;
import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntry;
import com.ecmdeveloper.plugin.core.model.security.ISecurityPrincipal;

/**
 * @author ricardo.belfor
 *
 */
public class SecurityContentProvider implements IStructuredContentProvider,  ITreeContentProvider {

	private TreeViewer viewer;
	private IObjectStoreItem objectStoreItem;
	private IAccessControlEntries accessControlEntries2;

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		this.viewer = (TreeViewer) viewer;
	
		if ( newInput != null ) {
//			objectStoreItem  = (IObjectStoreItem)((IEditorInput) newInput).getAdapter( IObjectStoreItem.class);
			accessControlEntries2  = (IAccessControlEntries) newInput;
		}
		
	}

	@Override
	public void dispose() {
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if ( parentElement instanceof IAccessControlEntries ) {
			return ((IAccessControlEntries) parentElement).getPrincipals().toArray();
		} else if ( parentElement instanceof ISecurityPrincipal ) {
			return ((ISecurityPrincipal) parentElement).getAccessControlEntries().toArray();
		}

		return null;
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if ( element instanceof IAccessControlEntries ) {
			Collection<ISecurityPrincipal> securityPrincipals = ((IAccessControlEntries) element).getPrincipals();
			return securityPrincipals != null && securityPrincipals.isEmpty();
		} else if ( element instanceof ISecurityPrincipal ) {
			Collection<IAccessControlEntry> accessControlEntries = ((ISecurityPrincipal) element).getAccessControlEntries();
			return accessControlEntries != null && !accessControlEntries.isEmpty();
		}
		return false;
	}
}
