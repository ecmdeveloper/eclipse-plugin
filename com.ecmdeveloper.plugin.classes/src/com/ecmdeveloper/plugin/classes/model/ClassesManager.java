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
package com.ecmdeveloper.plugin.classes.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import com.ecmdeveloper.plugin.classes.model.task.GetChildClassDescriptionsTask;
import com.ecmdeveloper.plugin.classes.model.task.GetClassDescriptionTask;
import com.ecmdeveloper.plugin.classes.model.task.RefreshClassDescriptionTask;
import com.ecmdeveloper.plugin.model.tasks.BaseTask;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStores;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.model.ObjectStoresManagerEvent;
import com.ecmdeveloper.plugin.model.ObjectStoresManagerListener;
import com.ecmdeveloper.plugin.model.tasks.TaskCompleteEvent;
import com.ecmdeveloper.plugin.model.tasks.TaskListener;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class ClassesManager implements ObjectStoresManagerListener, TaskListener {

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

	public void executeTaskASync(BaseTask task) {
		task.addTaskListener(this);
		objectStoresManager.executeTaskASync(task);
	}

	public Object executeTaskSync(BaseTask task) throws Exception {
		task.addTaskListener(this);
		return objectStoresManager.executeTaskSync(task);
	}

	@Override
	public void onTaskComplete(TaskCompleteEvent taskCompleteEvent) {
		if ( isTaskSourceInstanceOf(taskCompleteEvent, GetChildClassDescriptionsTask.class) ) {
			handleGetChildClassDescriptionsCompleted(taskCompleteEvent);
		} else if ( isTaskSourceInstanceOf(taskCompleteEvent, GetClassDescriptionTask.class) ) {
			handleGetClassDescriptionCompleted(taskCompleteEvent);
		} else if ( isTaskSourceInstanceOf(taskCompleteEvent, RefreshClassDescriptionTask.class ) ) {
			handleRefreshClassDescriptionCompleted(taskCompleteEvent);
		}
	}

	private void handleRefreshClassDescriptionCompleted(TaskCompleteEvent taskCompleteEvent) {
		RefreshClassDescriptionTask task = (RefreshClassDescriptionTask) taskCompleteEvent.getSource();
		ClassDescription[] classDescriptions = task.getClassDescriptions();
		fireClassDescriptionChanged(null, null, classDescriptions );
	}

	private void handleGetClassDescriptionCompleted(TaskCompleteEvent taskCompleteEvent) {
		GetClassDescriptionTask task = (GetClassDescriptionTask) taskCompleteEvent.getSource();
		ArrayList<ClassDescription> children = task.getChildren();
		Collection<Object> oldChildren = task.getOldChildren();
		fireClassDescriptionChanged( children.toArray(new ClassDescription[1]),
				oldChildren != null ? oldChildren.toArray( new ClassDescription[1] ) : null, null);
	}

	private void handleGetChildClassDescriptionsCompleted(TaskCompleteEvent taskCompleteEvent) {
		GetChildClassDescriptionsTask task = (GetChildClassDescriptionsTask) taskCompleteEvent.getSource();
		ClassDescription parent = task.getParent();
		Collection<Object> oldChildren = task.getOldChildren();
//		fireClassDescriptionChanged( parent.getChildren().toArray( new ClassDescription[0] ), 
//				oldChildren != null ? oldChildren.toArray( new ClassDescription[0] ) : null, new ClassDescription[] { parent } );
		fireClassDescriptionChanged( parent.getChildren().toArray( new ClassDescription[0] ), 
		null, new ClassDescription[] { parent } );
	}

	private boolean isTaskSourceInstanceOf(TaskCompleteEvent taskCompleteEvent, Class<?> taskClass) {
		return taskCompleteEvent.getSource().getClass().equals(taskClass);
	}

	public void fireClassDescriptionChanged(ClassDescription[] itemsAdded,
			ClassDescription[] itemsRemoved, ClassDescription[] itemsUpdated) {
		
		if ( listeners == null || listeners.isEmpty() ) {
			return;
		}
		
		ClassesManagerEvent event = new ClassesManagerEvent(this,
				itemsAdded, itemsUpdated, itemsRemoved );
		for (ClassesManagerListener listener : listeners) {
			listener.classDescriptionsChanged(event);
		}
	}
}
