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

package com.ecmdeveloper.plugin.search.ui;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import com.ecmdeveloper.plugin.core.model.tasks.ITaskManager;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskManagerListener;
import com.ecmdeveloper.plugin.core.model.tasks.ObjectStoresManagerEvent;
import com.ecmdeveloper.plugin.core.model.tasks.ObjectStoresManagerRefreshEvent;
import com.ecmdeveloper.plugin.search.Activator;

/**
 * @author ricardo.belfor
 *
 */
public class SearchResultContentProvider implements IStructuredContentProvider, ITaskManagerListener {

	private QuerySearchResult searchResult; 
	private TableViewer viewer;
	private ITaskManager manager;
	
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		this.viewer = (TableViewer) viewer;
		
		if ( newInput instanceof QuerySearchResult ) {
			searchResult = (QuerySearchResult) newInput;
			
			if ( manager == null ) {
				manager = Activator.getDefault().getTaskManager();
				manager.addTaskManagerListener(this);
			}
		}
		
		if ( newInput == null && manager != null ) {
			manager.removeTaskManagerListener(this);
			manager = null;
		}
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if ( searchResult != null ) {
			return searchResult.getResult().toArray();
		}
		return null;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void objectStoreItemsChanged(final ObjectStoresManagerEvent event) {
		viewer.getControl().getDisplay().asyncExec( new Runnable() {
			
			@Override
			public void run() {
				updateViewer(event);
			}
		});
		
	}

	protected void updateViewer(ObjectStoresManagerEvent event) {

		if ( event.getItemsRemoved() != null ) {
			viewer.remove( searchResult.getSearchResultRows( event.getItemsRemoved() ) );
		}
		
		if ( event.getItemsUpdated() != null ) {
			viewer.update( searchResult.getSearchResultRows( event.getItemsUpdated() ), null );
		}
		
	}

	@Override
	public void objectStoreItemsRefreshed(final ObjectStoresManagerRefreshEvent event) {
		viewer.getControl().getDisplay().asyncExec( new Runnable() {
			
			@Override
			public void run() {
				refreshViewer(event);
			}
		});
	}

	protected void refreshViewer(ObjectStoresManagerRefreshEvent event) {
		viewer.update( searchResult.getSearchResultRows( event.getItemsRefreshed() ), null );
	}
}
