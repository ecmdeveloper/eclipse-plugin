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
package com.ecmdeveloper.plugin.views;

import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import com.ecmdeveloper.plugin.Activator;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.IObjectStores;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskManager;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskManagerListener;
import com.ecmdeveloper.plugin.core.model.tasks.ObjectStoresManagerEvent;
import com.ecmdeveloper.plugin.core.model.tasks.ObjectStoresManagerRefreshEvent;
import com.ecmdeveloper.plugin.model.Folder;
import com.ecmdeveloper.plugin.model.ObjectStore;

public class ObjectStoresViewContentProvider implements
		IStructuredContentProvider, ITreeContentProvider, ITaskManagerListener {

	private ITaskManager taskManager;
	private IObjectStores invisibleRoot;
	private TreeViewer viewer;
	private Object[] rootElements;

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		this.viewer = (TreeViewer) viewer;
		
		if (taskManager != null) {
			taskManager.removeTaskManagerListener(this);
		}
		
		taskManager = (ITaskManager) newInput;
		
		if (taskManager != null) {
			taskManager.addTaskManagerListener(this);
		}
	}
	
	public void dispose() {
	}

	public void setRootElements(Object[] rootElements) {
		this.rootElements = rootElements;
	}
	
	public Object[] getElements(Object parent) {

		if ( ! ( parent instanceof IObjectStoreItem ) ) {
			if ( rootElements == null ) {
				if ( invisibleRoot == null ) {
					initialize();
				}
				return getChildren(invisibleRoot);
			} else {
				return rootElements;
			}
		} else {
			return getChildren(parent);
		}
	}
	public Object getParent(Object child) {
		if (child instanceof IObjectStoreItem ) {
			return ((IObjectStoreItem)child).getParent();
		}
		return null;
	}

	public Object[] getChildren(Object parent) {
		
		if (parent instanceof IObjectStoreItem) {
			
			Collection<IObjectStoreItem> children = ((IObjectStoreItem)parent).getChildren();
			
			if ( children != null )
			{
				Object[] childrenArray = getChildrenAsArray(children);
				return childrenArray;
			}
		} else if ( parent instanceof IObjectStores ) {
			Collection<IObjectStore> children = ((IObjectStores)parent).getChildren();
			if ( children != null ) {
				return children.toArray();
			}
			
		}
		return new Object[0];
	}

	private Object[] getChildrenAsArray(Collection<IObjectStoreItem> children) {
		Object[]  childrenArray = new Object[children.size()];
		int index = 0;
		
		for (IObjectStoreItem object : children) {
			childrenArray[index++] = object;
		}
		return childrenArray;
	}
	
	public boolean hasChildren(Object parent) {

		if (parent instanceof IObjectStoreItem) {
			return ((IObjectStoreItem)parent).hasChildren();
		}
		return false;
	}
	
	private void initialize() {
		invisibleRoot = Activator.getDefault().getObjectStoresManager().getObjectStores();		
	}

	@Override
	public void objectStoreItemsChanged(final ObjectStoresManagerEvent event) {

		viewer.getTree().getDisplay().asyncExec( new Runnable() {
			
			@Override
			public void run() {
				updateViewer(event);
			}
		});
	}

	private void updateViewer(final ObjectStoresManagerEvent event) {

		if ( event.getItemsRemoved() != null ) {
			viewer.remove( event.getItemsRemoved() );
		}

		if ( event.getItemsAdded() != null ) {
			updateItemsAdded(event);
		}
		
		if ( event.getItemsUpdated() != null ) {
			updateItemsUpdated(event);
		}
	}

	private void updateItemsUpdated(final ObjectStoresManagerEvent event) {
		viewer.update( event.getItemsUpdated() , null );

		for ( IObjectStoreItem objectStoreItem : event.getItemsUpdated() ) {
			if ( objectStoreItem instanceof Folder ) {
				viewer.refresh(objectStoreItem);
			} else if ( objectStoreItem instanceof ObjectStore ) {
				viewer.refresh( objectStoreItem, false );
			}
		}
		
//		Object[] expandedElements = viewer.getExpandedElements();
//		for (IObjectStoreItem updatedItem : event.getItemsUpdated()) {
//			Collection<IObjectStoreItem> similarObjects = getSimilarObjects(expandedElements, updatedItem);
//			if ( similarObjects.size() > 0 ) {
//				RefreshTask refreshTask = new RefreshTask( similarObjects.toArray( new IObjectStoreItem[0] ), true );
//				ObjectStoresManager.getManager().executeTaskASync(refreshTask);
//			}
//		}
	}

	private void updateItemsAdded(final ObjectStoresManagerEvent event) {
		for ( IObjectStoreItem objectStoreItem : event.getItemsAdded() ) {
			if ( objectStoreItem instanceof ObjectStore ) {
				viewer.refresh(null, false );
				viewer.add( invisibleRoot, objectStoreItem );
			} else {
			}
		}
	}

	@Override
	public void objectStoreItemsRefreshed(final ObjectStoresManagerRefreshEvent event) {
		viewer.getTree().getDisplay().asyncExec( new Runnable() {
			
			@Override
			public void run() {
				refreshViewer(event);
			}
		});
	}

	protected void refreshViewer(ObjectStoresManagerRefreshEvent event) {
		viewer.update( event.getItemsRefreshed(), null );

		for ( IObjectStoreItem objectStoreItem : event.getItemsRefreshed() ) {
			if ( objectStoreItem instanceof Folder ) {
	//			viewer.refresh(objectStoreItem);
			} else if ( objectStoreItem instanceof ObjectStore ) {
				viewer.refresh( objectStoreItem, false );
			}
		}
	}
}
