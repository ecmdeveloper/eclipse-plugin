package com.ecmdeveloper.plugin.model;

import java.util.EventObject;

public class ObjectStoresManagerEvent  extends EventObject {

	private static final long serialVersionUID = 4305309975129741874L;

	private final IObjectStoreItem[] itemsAdded;
	private final IObjectStoreItem[] itemsRemoved;
	private final IObjectStoreItem[] itemsUpdated;
	
	public ObjectStoresManagerEvent(ObjectStoresManager source,
			IObjectStoreItem[] itemsAdded, IObjectStoreItem[] itemsRemoved, IObjectStoreItem[] itemsUpdated) {
		super(source);
		this.itemsAdded = itemsAdded;
		this.itemsRemoved = itemsRemoved;
		this.itemsUpdated = itemsUpdated;
	}
    
	public IObjectStoreItem[] getItemsAdded() {
		return itemsAdded;
	}

	public IObjectStoreItem[] getItemsRemoved() {
		return itemsRemoved;
	}

	public IObjectStoreItem[] getItemsUpdated() {
		return itemsUpdated;
	}
}
