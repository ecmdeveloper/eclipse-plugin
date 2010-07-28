/**
 * Copyright 2009,2010, Ricardo Belfor
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
package com.ecmdeveloper.plugin.classes.views;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import com.ecmdeveloper.plugin.classes.model.ClassDescription;
import com.ecmdeveloper.plugin.classes.model.ClassesManager;
import com.ecmdeveloper.plugin.classes.model.ClassesManagerEvent;
import com.ecmdeveloper.plugin.classes.model.ClassesManagerListener;
import com.ecmdeveloper.plugin.classes.model.VirtualFolder;
import com.ecmdeveloper.plugin.classes.model.constants.ClassType;
import com.ecmdeveloper.plugin.classes.model.constants.VirtualFolderType;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStores;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class ClassesViewContentProvider implements IStructuredContentProvider,
		ITreeContentProvider, ClassesManagerListener {

	private ClassesManager manager;
	private ObjectStores classesRoot;
	private TreeViewer viewer;
	private ClassType classesFilter;
	
	public ClassesViewContentProvider(ClassType classesFilter) {
		this.classesFilter = classesFilter;
	}
	
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TreeViewer) viewer;

		if (manager != null) {
			manager.removeClassesManagerListener(this);
		}
		
		manager = (ClassesManager) newInput;
		
		if (manager != null) {
			manager.addClassesManagerListener(this);
		}
	}

	@Override
	public Object[] getElements(Object parent) {
		if ( ! ( parent instanceof IObjectStoreItem ) ) {
			if ( classesRoot == null ) initialize();
			return getChildren(classesRoot);
		} else {
			return getChildren(parent);
		}
	}

	private void initialize() {
		classesRoot = manager.getObjectStores();		
	}

	@Override
	public void dispose() {
	}

	/**
	 * Gets the children. In the hierarchy virual folders are child objects
	 * of object stores.
	 * 
	 * @param parent the parent
	 * 
	 * @return the children
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	@Override
	public Object[] getChildren(Object parent) {

		if ( parent instanceof ObjectStore ) {
			if ( ClassType.ALL_CLASSES.equals( classesFilter ) ) {
				return getVirtualFolders(parent);
			} else {
				throw new UnsupportedOperationException( "Not yet implemented" );
			}
		} else if ( parent instanceof VirtualFolder ) {
			
			VirtualFolder virtualFolder = (VirtualFolder) parent;
			return virtualFolder.getChildren().toArray();
			
		} else if (parent instanceof ClassDescription) {
			
			ClassDescription classDescription = (ClassDescription) parent;
			return classDescription.getChildren().toArray();
		} else if ( parent instanceof IObjectStoreItem ) {
			Collection<IObjectStoreItem> children = ((IObjectStoreItem)parent).getChildren();
			
			if ( children != null )
			{
				ArrayList<Object> childObjects = new ArrayList<Object>();
				childObjects.addAll( children );
				return childObjects.toArray();
			}
		}
		return new Object[0];
	}

	private Object[] getVirtualFolders(Object parent) {
		ArrayList<Object> virtualFolders = new ArrayList<Object>();
		for (VirtualFolderType virtualFolderType : VirtualFolderType.values() ) {
			virtualFolders.add( new VirtualFolder( virtualFolderType, (ObjectStore) parent )  );
		}
		return virtualFolders.toArray();
	}

	@Override
	public Object getParent(Object child) {
		if (child instanceof IObjectStoreItem ) {
			return ((IObjectStoreItem)child).getParent();
		} else if ( child instanceof VirtualFolder ) {
			
			VirtualFolder virtualFolder = (VirtualFolder) child;
			return virtualFolder.getParent();
			
		} else if (child instanceof ClassDescription) {
			
			ClassDescription classDescription = (ClassDescription) child;
			return classDescription.getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof IObjectStoreItem) {
			return ((IObjectStoreItem)element).hasChildren();
		} if ( element instanceof VirtualFolder ) {
			return true;
		} if ( element instanceof ClassDescription ) {
			return ((ClassDescription)element).hasChildren();
		}
		return true;
	}

	@Override
	public void objectStoresConnected(final ClassesManagerEvent event) {

		viewer.getTree().getDisplay().asyncExec( new Runnable() {
			@Override
			public void run() {
				for ( ObjectStore objectStore: event.getObjectStores() ) {
						viewer.refresh(objectStore);
				}
			}
		});
	}

	@Override
	public void classDescriptionsChanged(final ClassesManagerEvent event) {
		viewer.getTree().getDisplay().asyncExec( new Runnable() {
			
			@Override
			public void run() {
	
				if ( event.getItemsAdded() != null ) {
					for ( ClassDescription classDescription : event.getItemsAdded() ) {
						viewer.add( classDescription.getParent(), classDescription );
					}
				}
				
				if ( event.getItemsUpdated() != null ) {
					for ( ClassDescription classDescription : event.getItemsUpdated() ) {
						viewer.refresh( classDescription );
					}
				}

				if ( event.getItemsRemoved() != null ) {
					viewer.remove( event.getItemsRemoved() );
				}
			}
		});
	}

	@Override
	public void objectStoresAdded(final ClassesManagerEvent event) {
		refreshTreeRootASync();
	}

	@Override
	public void objectStoresRemoved(ClassesManagerEvent event) {
		refreshTreeRootASync();
	}

	private void refreshTreeRootASync() {
		viewer.getTree().getDisplay().asyncExec( new Runnable() {
			@Override
			public void run() {
				viewer.refresh(null, false );
			}
		});
	}
}
