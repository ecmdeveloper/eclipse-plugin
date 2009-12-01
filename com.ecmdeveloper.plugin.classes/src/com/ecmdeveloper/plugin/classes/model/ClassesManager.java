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
package com.ecmdeveloper.plugin.classes.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import com.ecmdeveloper.plugin.classes.model.task.BaseTask;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStores;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.model.ObjectStoresManagerEvent;
import com.ecmdeveloper.plugin.model.ObjectStoresManagerListener;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class ClassesManager implements ObjectStoresManagerListener {

	private static ClassesManager classesManager;
	protected ObjectStoresManager objectStoresManager;
	private List<ClassesManagerListener> listeners = new ArrayList<ClassesManagerListener>();

	public static ClassesManager getManager()
	{
		if ( classesManager == null ) {
			classesManager = new ClassesManager();
		}
		return classesManager;
	}

	public ClassesManager() {
		super();
		getObjectStoresManager();	
	}

	public ObjectStores getObjectStores() {
		return objectStoresManager.getObjectStores();
	}

	private ObjectStoresManager getObjectStoresManager() {

		if ( objectStoresManager == null ) {
			objectStoresManager = ObjectStoresManager.getManager();
			objectStoresManager.addObjectStoresManagerListener(this);
		}
		return objectStoresManager;
	}

	public void removeClassesManagerListener( ClassesManagerListener listener) {
		listeners.remove(listener);
	}

	public void addClassesManagerListener( ClassesManagerListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	@Override
	public void objectStoreItemsChanged(ObjectStoresManagerEvent event) {
		
		Set<ObjectStore> objectStores = new HashSet<ObjectStore>();
		
		if ( event.getItemsUpdated() != null ) {
			for ( IObjectStoreItem objectStoreItem : event.getItemsUpdated() ) {
				if ( objectStoreItem instanceof ObjectStore ) {
					objectStores.add( (ObjectStore) objectStoreItem );
				}
			}
		}

		if ( objectStores.isEmpty() ) {
			return;
		}
		
		ClassesManagerEvent classesEvent = new ClassesManagerEvent(this, objectStores.toArray( new ObjectStore[0]) );
		
		for (ClassesManagerListener listener : listeners) {
			listener.objectStoresConnected( classesEvent );
		}
	}

	public void executeTaskASync(Callable<Object> task) {
		if ( task instanceof BaseTask) {
			((BaseTask)task).setListeners(listeners);
		}
		objectStoresManager.executeTaskASync(task);
	}
}
