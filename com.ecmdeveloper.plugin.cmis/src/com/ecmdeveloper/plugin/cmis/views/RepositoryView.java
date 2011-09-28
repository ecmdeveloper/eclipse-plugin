/**
 * Copyright 2011, Ricardo Belfor
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

package com.ecmdeveloper.plugin.cmis.views;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * @author ricardo.belfor
 *
 */
public class RepositoryView extends ViewPart {

	private TreeViewer viewer;
	private RepositoryViewContentProvider contentProvider;

	public RepositoryView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		contentProvider = new RepositoryViewContentProvider();
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(new CmisObjectLabelProvider());
		viewer.setInput( "Bla" );
//		viewer.setSorter( new ObjectStoresViewSorter() );
//		hookContextMenu();
//		hookMouse();
		
		getSite().setSelectionProvider(viewer);
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
