/**
 * 
 */
package com.ecmdeveloper.plugin.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Ricardo Belfor
 *
 */
public class ObjectStores extends ObjectStoreItem {

	private ArrayList<IObjectStoreItem> children;
	
	public ObjectStores() {
		super(null);
		children = new ArrayList<IObjectStoreItem>();
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
}
