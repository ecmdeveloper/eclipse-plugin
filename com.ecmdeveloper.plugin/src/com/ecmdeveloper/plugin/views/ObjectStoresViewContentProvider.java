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
import java.util.HashSet;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import com.ecmdeveloper.plugin.jobs.RefreshJob;
import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.Folder;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStores;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.model.ObjectStoresManagerEvent;
import com.ecmdeveloper.plugin.model.ObjectStoresManagerListener;
import com.ecmdeveloper.plugin.model.tasks.RefreshTask;

public class ObjectStoresViewContentProvider implements
		IStructuredContentProvider, ITreeContentProvider, ObjectStoresManagerListener {

	private ObjectStoresManager manager;
	private ObjectStores invisibleRoot;
	private TreeViewer viewer;

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		this.viewer = (TreeViewer) viewer;
		
		if (manager != null) {
			manager.removeObjectStoresManagerListener(this);
		}
		
		manager = (ObjectStoresManager) newInput;
		
		if (manager != null) {
			manager.addObjectStoresManagerListener(this);
		}
	}
	
	public void dispose() {
	}

	public Object[] getElements(Object parent) {

		if ( ! ( parent instanceof IObjectStoreItem ) ) {
			if ( invisibleRoot == null ) initialize();
			return getChildren(invisibleRoot);
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

		invisibleRoot = manager.getObjectStores();		
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
		
		Object[] expandedElements = viewer.getExpandedElements();
		for (IObjectStoreItem updatedItem : event.getItemsUpdated()) {
			Collection<IObjectStoreItem> similarObjects = getSimilarObjects(expandedElements, updatedItem);
//			for ( IObjectStoreItem similarObject : similarObjects ) {
//				if ( similarObject instanceof Document ) {
//					((Document)similarObject).refresh( (com.filenet.api.core.Document) ((ObjectStoreItem)updatedItem).getObjectStoreObject() );
//					viewer.update(similarObject, null);
//				}
//			}
			if ( similarObjects.size() > 0 ) {
				RefreshTask refreshTask = new RefreshTask( similarObjects.toArray( new IObjectStoreItem[0] ) );
				ObjectStoresManager.getManager().executeTaskASync(refreshTask);
				
//				RefreshJob refreshJob = new RefreshJob( similarObjects.toArray( new IObjectStoreItem[0] ) );
//				refreshJob.schedule();
			}
		}
	}

	private Collection<IObjectStoreItem> getSimilarObjects(Object[] expandedElements, IObjectStoreItem updatedItem) {
		
		Collection<IObjectStoreItem> similarObjects = new HashSet<IObjectStoreItem>();
		
		for (Object object : expandedElements) {
			if (object instanceof Folder) {
				Folder folder = (Folder) object;
				for (IObjectStoreItem child : folder.getChildren()) {
					if ( isSimilarObject(updatedItem, child) ) {
						similarObjects.add(child);
					}
				}
			}
		}
		
		return similarObjects;
	}

	private boolean isSimilarObject(IObjectStoreItem updatedItem, IObjectStoreItem otherItem) {
		if ( !otherItem.equals(updatedItem) ) {
			System.out.println( "Comparing " + otherItem.getName() + " with " + updatedItem.getName() );
			if ( otherItem.getId() != null && otherItem.getId().equalsIgnoreCase(updatedItem.getId()) ) {
//				System.out.println(otherItem.getDisplayName() + " found!");
//				return true;
			} 
			else if ( isSameVersionSeries(updatedItem, otherItem) ) {
				System.out.println(otherItem.getDisplayName() + " found!");
				return true;
			}
		}
		return false;
	}

	private boolean isSameVersionSeries(IObjectStoreItem updatedItem, IObjectStoreItem otherItem) {
		return otherItem instanceof Document && updatedItem instanceof Document && 
				((Document)otherItem).getVersionSeriesId().equalsIgnoreCase( ((Document)updatedItem).getVersionSeriesId() );
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
}
