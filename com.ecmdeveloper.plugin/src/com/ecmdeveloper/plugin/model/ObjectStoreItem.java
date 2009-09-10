package com.ecmdeveloper.plugin.model;

import org.eclipse.core.runtime.Platform;


public abstract class ObjectStoreItem implements IObjectStoreItem {

	protected IObjectStoreItem parent;
	protected String name;
	protected String id;

	public ObjectStoreItem(IObjectStoreItem parent) {
		this.parent = parent;
	}
	
	/* (non-Javadoc)
	 * @see com.ecmdeveloper.plugin.model.IObjectStoreItem#getName()
	 */
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see com.ecmdeveloper.plugin.model.IObjectStoreItem#getParent()
	 */
	@Override
	public IObjectStoreItem getParent() {
		return parent;
	}
	
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	@Override
	public boolean hasChildren() 
	{
		return false;
	}
}
