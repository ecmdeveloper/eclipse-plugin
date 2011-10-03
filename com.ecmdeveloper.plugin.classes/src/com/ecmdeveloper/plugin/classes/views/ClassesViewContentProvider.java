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

import com.ecmdeveloper.plugin.core.model.ClassesManager;
import com.ecmdeveloper.plugin.core.model.ClassesManagerEvent;
import com.ecmdeveloper.plugin.core.model.ClassesManagerListener;
import com.ecmdeveloper.plugin.core.model.IClassDescription;
import com.ecmdeveloper.plugin.core.model.IClassDescriptionFolder;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.IObjectStores;
import com.ecmdeveloper.plugin.core.model.constants.ClassDescriptionFolderType;
import com.ecmdeveloper.plugin.core.model.constants.ClassType;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class ClassesViewContentProvider implements IStructuredContentProvider,
		ITreeContentProvider, ClassesManagerListener {

	private ClassesManager manager;
	private IObjectStores classesRoot;
	private TreeViewer viewer;
	private ClassType classesFilter;
	private IClassDescriptionFolder rootVirtualFolder;
	public ClassesViewContentProvider(ClassType classesFilter) {
		this.classesFilter = classesFilter;
	}

	public void setRootClassDescription(IClassDescription rootClassDescription ) {
		Object[] virtualFolders = getVirtualFolders(rootClassDescription.getObjectStore() );
		if ( virtualFolders.length == 1 ) {
			ArrayList<Object> children = new ArrayList<Object>();
			children.add( rootClassDescription );
			rootVirtualFolder = (IClassDescriptionFolder)virtualFolders[0]; 
			rootVirtualFolder.setChildren(children);
		}
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
			if ( rootVirtualFolder != null) {
				return getChildren(rootVirtualFolder);
			}
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

		if ( parent instanceof IObjectStores ) {
			return ((IObjectStores) parent).getChildren().toArray();
		} else if ( parent instanceof IObjectStore ) {
			return getVirtualFolders((IObjectStore) parent);
		} else if ( parent instanceof IClassDescriptionFolder ) {
			
			IClassDescriptionFolder classDescriptionFolder = (IClassDescriptionFolder) parent;
			return classDescriptionFolder.getChildren().toArray();
			
		} else if (parent instanceof IClassDescription) {
			
			IClassDescription classDescription = (IClassDescription) parent;
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

	private Object[] getAllVirtualFolders(Object parent) {
		if ( parent instanceof IObjectStore ) {
			return ((IObjectStore) parent).getClassDescriptionFolders().toArray();
		}
		return null;
	}

	private Object[] getVirtualFolders(IObjectStore objectStore) {
		
		ClassDescriptionFolderType classDescriptionFolderType = null;
		
		switch ( classesFilter ) {
		case FOLDER_CLASSES:
			classDescriptionFolderType = ClassDescriptionFolderType.FOLDER_CLASSES;
			break;
		case DOCUMENT_CLASSES:
			classDescriptionFolderType = ClassDescriptionFolderType.DOCUMENT_CLASSES;
			break;
		case CUSTOM_OBJECT_CLASSES:
			classDescriptionFolderType = ClassDescriptionFolderType.CUSTOM_OBJECT_CLASSES;
			break;
		case ALL_CLASSES:
			return getAllVirtualFolders(objectStore);
		}
		
		if ( classDescriptionFolderType != null ) {
			IClassDescriptionFolder classDescriptionFolder = objectStore.getClassDescriptionFolder( classDescriptionFolderType );
			return new Object[] { classDescriptionFolder };
		}

		throw new UnsupportedOperationException( "Unsupported classes filter" );
	}
	
	@Override
	public Object getParent(Object child) {
		if (child instanceof IObjectStoreItem ) {
			return ((IObjectStoreItem)child).getParent();
		} else if ( child instanceof IClassDescriptionFolder ) {
			
			IClassDescriptionFolder classDescriptionFolder = (IClassDescriptionFolder) child;
			return classDescriptionFolder.getParent();
			
		} else if (child instanceof IClassDescription) {
			
			IClassDescription classDescription = (IClassDescription) child;
			return classDescription.getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof IObjectStoreItem) {
			return ((IObjectStoreItem)element).hasChildren();
		} if ( element instanceof IClassDescriptionFolder ) {
			return true;
		} if ( element instanceof IClassDescription ) {
			return ((IClassDescription)element).hasChildren();
		}
		return true;
	}

	@Override
	public void objectStoresConnected(final ClassesManagerEvent event) {

		viewer.getTree().getDisplay().asyncExec( new Runnable() {
			@Override
			public void run() {
				for ( IObjectStore objectStore: event.getObjectStores() ) {
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
					for ( IClassDescription classDescription : event.getItemsAdded() ) {
						viewer.add( classDescription.getParent(), classDescription );
					}
				}
				
				if ( event.getItemsUpdated() != null ) {
					for ( IClassDescription classDescription : event.getItemsUpdated() ) {
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
