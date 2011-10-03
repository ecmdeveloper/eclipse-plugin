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
package com.ecmdeveloper.plugin.core.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ecmdeveloper.plugin.core.model.impl.ObjectStoresManager;
import com.ecmdeveloper.plugin.core.model.tasks.IBaseTask;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskManager;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskManagerListener;
import com.ecmdeveloper.plugin.core.model.tasks.ObjectStoresManagerEvent;
import com.ecmdeveloper.plugin.core.model.tasks.ObjectStoresManagerRefreshEvent;
import com.ecmdeveloper.plugin.core.model.tasks.TaskCompleteEvent;
import com.ecmdeveloper.plugin.core.model.tasks.TaskListener;
import com.ecmdeveloper.plugin.core.model.tasks.classes.IGetChildClassDescriptionsTask;
import com.ecmdeveloper.plugin.core.model.tasks.classes.IGetClassDescriptionTask;
import com.ecmdeveloper.plugin.core.model.tasks.classes.IRefreshClassDescriptionTask;
import com.ecmdeveloper.plugin.core.model.tasks.impl.TaskManager;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class ClassesManager implements ITaskManagerListener, TaskListener {

	private static ClassesManager classesManager;
	private IObjectStoresManager objectStoresManager;
	private ITaskManager taskManager; 
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

	public IObjectStores getObjectStores() {
		return objectStoresManager.getObjectStores();
	}

	private IObjectStoresManager getObjectStoresManager() {

		if ( objectStoresManager == null ) {
			objectStoresManager = ObjectStoresManager.getManager();
			taskManager = TaskManager.getInstance();
			taskManager.addTaskManagerListener(this);
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
		
		if ( event.getItemsUpdated() != null ) {
			updateConnectedObjectStores(event);
		}
		
		if ( event.getItemsAdded() != null ) {
			updateAddedObjectStores(event);
		}
		
		if ( event.getItemsRemoved() != null) {
			updateRemovedObjectStores(event);			
		}
	}

	private void updateAddedObjectStores(ObjectStoresManagerEvent event) {
		ClassesManagerEvent classesEvent = getObjectStoresEvent(event.getItemsAdded());
		if ( classesEvent != null ) {
			for (ClassesManagerListener listener : listeners) {
				listener.objectStoresAdded(classesEvent);
			}
		}
	}

	private void updateConnectedObjectStores(ObjectStoresManagerEvent event) {
		ClassesManagerEvent classesEvent = getObjectStoresEvent(event.getItemsUpdated() );

		if ( classesEvent != null ) {
			for (ClassesManagerListener listener : listeners) {
				listener.objectStoresConnected(classesEvent);
			}
		}
	}

	private void updateRemovedObjectStores(ObjectStoresManagerEvent event) {
		ClassesManagerEvent classesEvent = getObjectStoresEvent(event.getItemsRemoved() );
		if ( classesEvent != null ) {
			for (ClassesManagerListener listener : listeners) {
				listener.objectStoresRemoved(classesEvent);
			}
		}
	}

	private ClassesManagerEvent getObjectStoresEvent(IObjectStoreItem[] itemsAdded) {
		Set<IObjectStore> objectStores = getObjectStoreItems(itemsAdded);
	
		if (objectStores.isEmpty()) {
			return null;
		}
		
		ClassesManagerEvent classesEvent = new ClassesManagerEvent(this, objectStores
				.toArray(new IObjectStore[0]));
		return classesEvent;
	}

	private Set<IObjectStore> getObjectStoreItems(IObjectStoreItem[] objectStoreItems ) {
		Set<IObjectStore> objectStores = new HashSet<IObjectStore>();

		for (IObjectStoreItem objectStoreItem : objectStoreItems ) {
			if (objectStoreItem instanceof IObjectStore) {
				objectStores.add((IObjectStore) objectStoreItem);
			}
		}

		return objectStores;
	}

	public void executeTaskASync(IBaseTask task) {
		task.addTaskListener(this);
		taskManager.executeTaskASync(task);
	}

	public Object executeTaskSync(IBaseTask task) throws Exception {
		task.addTaskListener(this);
		return taskManager.executeTaskSync(task);
	}

	@Override
	public void onTaskComplete(TaskCompleteEvent taskCompleteEvent) {
		if ( isTaskSourceInstanceOf(taskCompleteEvent, IGetChildClassDescriptionsTask.class) ) {
			handleGetChildClassDescriptionsCompleted(taskCompleteEvent);
		} else if ( isTaskSourceInstanceOf(taskCompleteEvent, IGetClassDescriptionTask.class) ) {
			handleGetClassDescriptionCompleted(taskCompleteEvent);
		} else if ( isTaskSourceInstanceOf(taskCompleteEvent, IRefreshClassDescriptionTask.class ) ) {
			handleRefreshClassDescriptionCompleted(taskCompleteEvent);
		}
	}

	private void handleRefreshClassDescriptionCompleted(TaskCompleteEvent taskCompleteEvent) {
		IRefreshClassDescriptionTask task = (IRefreshClassDescriptionTask) taskCompleteEvent.getSource();
		IClassDescription[] classDescriptions = task.getClassDescriptions();
		fireClassDescriptionChanged(null, null, classDescriptions );
	}

	private void handleGetClassDescriptionCompleted(TaskCompleteEvent taskCompleteEvent) {
		IGetClassDescriptionTask task = (IGetClassDescriptionTask) taskCompleteEvent.getSource();
		ArrayList<IClassDescription> children = task.getChildren();
		Collection<Object> oldChildren = task.getOldChildren();
		fireClassDescriptionChanged( children.toArray(new IClassDescription[1]),
				oldChildren != null ? oldChildren.toArray( new IClassDescription[1] ) : null, null);
	}

	private void handleGetChildClassDescriptionsCompleted(TaskCompleteEvent taskCompleteEvent) {
		IGetChildClassDescriptionsTask task = (IGetChildClassDescriptionsTask) taskCompleteEvent.getSource();
		IClassDescription parent = task.getParent();
		Collection<Object> oldChildren = task.getOldChildren();
//		fireClassDescriptionChanged( parent.getChildren().toArray( new ClassDescription[0] ), 
//				oldChildren != null ? oldChildren.toArray( new ClassDescription[0] ) : null, new ClassDescription[] { parent } );
		fireClassDescriptionChanged( parent.getChildren().toArray( new IClassDescription[0] ), 
		null, new IClassDescription[] { parent } );
	}

	private boolean isTaskSourceInstanceOf(TaskCompleteEvent taskCompleteEvent, Class<?> taskClass) {

		Class<? extends Object> eventClass = taskCompleteEvent.getSource().getClass();
		
		if ( taskClass.isAssignableFrom(eventClass) ) {
			return true;
		}
		
		do {
			if ( eventClass.equals( taskClass ) ) {
				return true;
			}
			eventClass = eventClass.getSuperclass();
		} while ( ! eventClass.equals( Object.class ) );
		
		return false;
	}
	

	public void fireClassDescriptionChanged(IClassDescription[] itemsAdded,
			IClassDescription[] itemsRemoved, IClassDescription[] itemsUpdated) {
		
		if ( listeners == null || listeners.isEmpty() ) {
			return;
		}
		
		ClassesManagerEvent event = new ClassesManagerEvent(this,
				itemsAdded, itemsUpdated, itemsRemoved );
		for (ClassesManagerListener listener : listeners) {
			listener.classDescriptionsChanged(event);
		}
	}

	@Override
	public void objectStoreItemsRefreshed(ObjectStoresManagerRefreshEvent event) {
	}
}
