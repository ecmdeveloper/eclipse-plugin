/**
 * 
 */
package com.ecmdeveloper.plugin.model;

import java.util.ArrayList;
import java.util.Collection;

import com.filenet.api.core.IndependentlyPersistableObject;

/**
 * @author Ricardo Belfor
 *
 */
public class ObjectStores extends ObjectStoreItem {

	private ArrayList<IObjectStoreItem> children;
	
	public ObjectStores() {
		super(null,null);
		children = new ArrayList<IObjectStoreItem>();
	}

	@Override
	public IndependentlyPersistableObject getObjectStoreObject() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ecmdeveloper.plugin.model.IObjectStoreItem#getChildren()
	 */
	@Override
	public Collection<IObjectStoreItem> getChildren() {
		return children;
	}
	
	public void add( ObjectStore objectStore ) {
		children.add( objectStore );
	}

	public void remove(ObjectStore objectStore) {
		children.remove(objectStore);
	}

	@Override
	public void setName(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		
	}
}
