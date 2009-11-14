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

import com.ecmdeveloper.plugin.model.Folder;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStores;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.model.ObjectStoresManagerEvent;
import com.ecmdeveloper.plugin.model.ObjectStoresManagerListener;

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
	public Object [] getChildren(Object parent) {
		
		if (parent instanceof IObjectStoreItem) {
			
			// Transform the collection to an array
			
			Collection<IObjectStoreItem> children = ((IObjectStoreItem)parent).getChildren();
			
			if ( children != null )
			{
				Object[]  childrenArray = new Object[children.size()];
				int index = 0;
				
				for (IObjectStoreItem object : children) {
					childrenArray[index++] = object;
				}
				
				return childrenArray;
			}
		}
		return new Object[0];
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
				if ( event.getItemsRemoved() != null ) {
					viewer.remove( event.getItemsRemoved() );
				}
	
				if ( event.getItemsAdded() != null ) {
					
					for ( IObjectStoreItem objectStoreItem : event.getItemsAdded() ) {
						if ( objectStoreItem instanceof ObjectStore ) {
							viewer.refresh(null, false );
							viewer.add( invisibleRoot, objectStoreItem );
						} else {
						}
					}
				}
				
				if ( event.getItemsUpdated() != null ) {
					viewer.update( event.getItemsUpdated() , null );
	
					for ( IObjectStoreItem objectStoreItem : event.getItemsUpdated() ) {
						if ( objectStoreItem instanceof Folder ) {
							viewer.refresh(objectStoreItem);
						} else if ( objectStoreItem instanceof ObjectStore ) {
							viewer.refresh( objectStoreItem, false );
						}
					}
				}
			}
		});
	}
}
