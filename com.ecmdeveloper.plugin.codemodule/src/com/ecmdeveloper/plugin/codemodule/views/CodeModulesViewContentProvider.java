/**
 * Copyright 2009, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in is
 * free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ECM Developer plug-in. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 */

package com.ecmdeveloper.plugin.codemodule.views;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import com.ecmdeveloper.plugin.codemodule.model.CodeModulesManagerEvent;
import com.ecmdeveloper.plugin.codemodule.model.CodeModulesManager;
import com.ecmdeveloper.plugin.codemodule.model.CodeModulesManagerListener;

public class CodeModulesViewContentProvider implements IStructuredContentProvider, CodeModulesManagerListener {

	private TableViewer viewer;
	private CodeModulesManager manager;

	@Override
	public Object[] getElements(Object inputElement) {
		return manager.getCodeModuleFiles().toArray();
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TableViewer) viewer;
		if (manager != null)
			manager.removeCodeModulesManagerListener(this);
		manager = (CodeModulesManager) newInput;
		if (manager != null)
			manager.addCodeModuleManagerListener(this);
	}

	@Override
	public void codeModuleFilesItemsChanged(final CodeModulesManagerEvent event) {

		viewer.getTable().getDisplay().asyncExec( new Runnable() {

			@Override
			public void run() {
				if ( event.getItemsUpdated() != null ) {
					viewer.update(event.getItemsUpdated(), null );
				}

				if ( event.getItemsRemoved() != null ) {
					viewer.remove( event.getItemsRemoved() );
				}
				
				if ( event.getItemsAdded() != null ) {
					viewer.add( event.getItemsAdded() );
				}
			}
		} );
	}
}
