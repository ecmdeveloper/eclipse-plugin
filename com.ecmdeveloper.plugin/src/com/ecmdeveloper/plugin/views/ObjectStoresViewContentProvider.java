package com.ecmdeveloper.plugin.views;

import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import com.ecmdeveloper.plugin.model.Folder;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;
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
	public void objectStoreItemsChanged(ObjectStoresManagerEvent event) {

		if ( event.getItemsRemoved() != null ) {
			viewer.remove( event.getItemsRemoved() );
		}

		if ( event.getItemsAdded() != null ) {
			
			for ( IObjectStoreItem objectStoreItem : event.getItemsAdded() ) {
//				viewer.add( invisibleRoot, objectStoreItem );
			}
			viewer.refresh(null, false );
		}
		
		if ( event.getItemsUpdated() != null ) {
			viewer.update( event.getItemsUpdated() , null );

			for ( IObjectStoreItem objectStoreItem : event.getItemsUpdated() ) {
				if ( objectStoreItem instanceof Folder ) {
					viewer.refresh(objectStoreItem);
				}
			}
		}
	}
}
