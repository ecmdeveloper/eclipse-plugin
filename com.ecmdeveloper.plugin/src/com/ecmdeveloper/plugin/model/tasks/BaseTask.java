package com.ecmdeveloper.plugin.model.tasks;

import java.util.List;
import java.util.concurrent.Callable;

import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStoresManagerEvent;
import com.ecmdeveloper.plugin.model.ObjectStoresManagerListener;


public abstract class BaseTask implements Callable<Object>{

	protected List<ObjectStoresManagerListener> listeners;

	public BaseTask() {
	}

	public void setListeners( List<ObjectStoresManagerListener> listeners ) {
		this.listeners = listeners;
	}

	public void fireObjectStoreItemsChanged(IObjectStoreItem[] itemsAdded,
			IObjectStoreItem[] itemsRemoved, IObjectStoreItem[] itemsUpdated ) {
		
		if ( listeners == null || listeners.isEmpty() ) {
			return;
		}
		
		ObjectStoresManagerEvent event = new ObjectStoresManagerEvent(this,
				itemsAdded, itemsRemoved, itemsUpdated );
		for (ObjectStoresManagerListener listener : listeners) {
			listener.objectStoreItemsChanged(event);
		}
	}
}
