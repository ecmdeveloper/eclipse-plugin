/**
 * Copyright 2010, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in
 * is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ECM Developer plug-in. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 */

package com.ecmdeveloper.plugin.core.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.ecmdeveloper.plugin.core.model.IDocument;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.ObjectStoreItemsModel;
import com.ecmdeveloper.plugin.core.model.tasks.ICreateTask;
import com.ecmdeveloper.plugin.core.model.tasks.IDeleteTask;
import com.ecmdeveloper.plugin.core.model.tasks.IDisconnectConnectionTask;
import com.ecmdeveloper.plugin.core.model.tasks.IDocumentTask;
import com.ecmdeveloper.plugin.core.model.tasks.IAddObjectStoreTask;
import com.ecmdeveloper.plugin.core.model.tasks.IConnectConnectionTask;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskManagerListener;
import com.ecmdeveloper.plugin.core.model.tasks.ILoadChildrenTask;
import com.ecmdeveloper.plugin.core.model.tasks.IMoveTask;
import com.ecmdeveloper.plugin.core.model.tasks.ObjectStoresManagerEvent;
import com.ecmdeveloper.plugin.core.model.tasks.ObjectStoresManagerRefreshEvent;
import com.ecmdeveloper.plugin.core.model.tasks.IRefreshTask;
import com.ecmdeveloper.plugin.core.model.tasks.TaskCompleteEvent;
import com.ecmdeveloper.plugin.core.model.tasks.TaskListener;
import com.ecmdeveloper.plugin.core.model.tasks.IUpdateTask;

/**
 * @author ricardo.belfor
 *
 */
public class ObjectStoreItemsModelController implements TaskListener {

	private ObjectStoreItemsModel objectStoreItemsModel;
	private List<ITaskManagerListener> listeners = new ArrayList<ITaskManagerListener>();

	public ObjectStoreItemsModelController() {
		objectStoreItemsModel = ObjectStoreItemsModel.getInstance();
	}

	@Override
	public void onTaskComplete(TaskCompleteEvent taskCompleteEvent) {
		handleTaskCompleteEvent(taskCompleteEvent);
	}

	private void handleTaskCompleteEvent(TaskCompleteEvent taskCompleteEvent) {
		if ( isTaskSourceInstanceOf(taskCompleteEvent, IConnectConnectionTask.class) ) {
			handleConnectConnectionTask(taskCompleteEvent);
		} else if ( isTaskSourceInstanceOf(taskCompleteEvent, IDisconnectConnectionTask.class) ) {
			handleDisconnectConnectionTask( taskCompleteEvent );
		} else if ( isTaskSourceInstanceOf(taskCompleteEvent, IDeleteTask.class) ) {
			handleDeleteTaskCompleted( taskCompleteEvent );
		} else if ( isTaskSourceInstanceOf(taskCompleteEvent, ILoadChildrenTask.class) ) {
			handleLoadChildrenTaskCompleted( taskCompleteEvent );
		} else if ( isTaskSourceInstanceOf(taskCompleteEvent, IRefreshTask.class) ) {
			handleRefreshTaskCompleted( taskCompleteEvent );
		} else if ( isTaskSourceInstanceOf(taskCompleteEvent, IMoveTask.class) ) {
			handleMoveTaskCompleted( taskCompleteEvent );
		} else if ( isTaskSourceInstanceOf(taskCompleteEvent, IUpdateTask.class) ) {
			handleUpdateTaskCompleted(taskCompleteEvent);
		} if ( isTaskSourceInstanceOf(taskCompleteEvent, ICreateTask.class) ) {
			handleCreateTaskCompleted(taskCompleteEvent);
		} else if ( isTaskSourceInstanceOf(taskCompleteEvent, IDocumentTask.class) ) {
			handleDocumentTaskCompleted(taskCompleteEvent);
		} else if ( isTaskSourceInstanceOf(taskCompleteEvent, IAddObjectStoreTask.class) ) {
			handleAddObjectStoreTask(taskCompleteEvent);
		}
	}

	private void handleAddObjectStoreTask(TaskCompleteEvent taskCompleteEvent) {
		IAddObjectStoreTask task = (IAddObjectStoreTask) taskCompleteEvent.getSource();
		IObjectStore objectStore = task.getObjectStore();
		fireObjectStoreItemsChanged( new IObjectStoreItem[] { objectStore }, null, null );
	}

	private void handleConnectConnectionTask(TaskCompleteEvent taskCompleteEvent) {
		IConnectConnectionTask task = (IConnectConnectionTask) taskCompleteEvent.getSource();
		Collection<IObjectStoreItem> connectionObjectStores = task.getConnectionObjectStores();
		fireObjectStoreItemsChanged(null, null, connectionObjectStores.toArray( new IObjectStoreItem[0] ) );
	}

	private void handleDisconnectConnectionTask(TaskCompleteEvent taskCompleteEvent) {
		IDisconnectConnectionTask task = (IDisconnectConnectionTask) taskCompleteEvent.getSource();
		Collection<IObjectStoreItem> connectionObjectStores = task.getConnectionObjectStores();
		fireObjectStoreItemsChanged(null, null, connectionObjectStores.toArray( new IObjectStoreItem[0] ) );
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

	private void handleUpdateTaskCompleted(TaskCompleteEvent taskCompleteEvent) {
		IUpdateTask updateTask = (IUpdateTask) taskCompleteEvent.getSource();
		refreshUpdatedItems(updateTask.getObjectStoreItems());
	}

	private void refreshUpdatedItems(IObjectStoreItem[] objectStoreItems) {
		fireObjectStoreItemsChanged(null, null, objectStoreItems );
		
		for (IObjectStoreItem objectStoreItem : objectStoreItems) {
			refreshSimilarObjects(objectStoreItem);
		}
	}

	private void handleDeleteTaskCompleted(TaskCompleteEvent taskCompleteEvent) {
		IDeleteTask deleteTask = (IDeleteTask) taskCompleteEvent.getSource();
		IObjectStoreItem[] objectStoreItems = deleteTask.getObjectStoreItems();
		Collection<IObjectStoreItem> collection = toCollection(objectStoreItems);
		Collection<IObjectStoreItem> updatedItems = new ArrayList<IObjectStoreItem>();

		if ( ! deleteTask.isDeleteAllVersions() ) {
			
			for ( IObjectStoreItem objectStoreItem : objectStoreItems ) {
				if (objectStoreItem instanceof IDocument ) {
					updatedItems.add(objectStoreItem);
					collection.remove(objectStoreItem);
				}
			}
		}

		Collection<IObjectStoreItem> deletedItems = objectStoreItemsModel.delete(collection );
		fireObjectStoreItemsChanged(null, toArray(deletedItems), null );

		if ( !deleteTask.isDeleteAllVersions() ) {
			refreshUpdatedItems(toArray(updatedItems));
		}
	}

	private void handleLoadChildrenTaskCompleted(TaskCompleteEvent taskCompleteEvent) {
		
		ILoadChildrenTask loadChildrenTask = (ILoadChildrenTask) taskCompleteEvent.getSource();
		IObjectStoreItem objectStoreItem = loadChildrenTask.getObjectStoreItem();
		ArrayList<IObjectStoreItem> children = loadChildrenTask.getChildren();
		
		objectStoreItemsModel.addChildren(objectStoreItem, children );
		fireObjectStoreItemsChanged(null, null, toArray(objectStoreItem ) );
	}

	private void handleRefreshTaskCompleted(TaskCompleteEvent taskCompleteEvent) {
		IRefreshTask refreshTask = (IRefreshTask)taskCompleteEvent.getSource();
		IObjectStoreItem[] objectStoreItems = refreshTask.getObjectStoreItems();
		fireObjectStoreItemsChanged(null, null, objectStoreItems );
//		fireObjectStoreItemsRefreshed( objectStoreItems );
	}

	private void handleMoveTaskCompleted(TaskCompleteEvent taskCompleteEvent) {
		IMoveTask moveTask = (IMoveTask) taskCompleteEvent.getSource();
		fireObjectStoreItemsChanged(null, moveTask.getObjectStoreItems(), moveTask.getUpdatedObjectStoreItems() );
	}

	private void handleDocumentTaskCompleted(TaskCompleteEvent taskCompleteEvent) {
		IDocumentTask documentTask = (IDocumentTask) taskCompleteEvent.getSource();
		IObjectStoreItem objectStoreItem = documentTask.getDocument();
 		fireObjectStoreItemsChanged(null, null, new IObjectStoreItem[] { objectStoreItem } );
		refreshSimilarObjects(objectStoreItem);
	}

	private void refreshSimilarObjects(IObjectStoreItem objectStoreItem) {
		Collection<IObjectStoreItem> objectStoreItems = objectStoreItemsModel.get(objectStoreItem);
		for (IObjectStoreItem o : objectStoreItems ) {
			if ( ! o.equals(objectStoreItem ) ) {
// FIXME				
//				RefreshTask refreshTask = new RefreshTask( o );
//				TaskManager.getInstance().executeTaskASync(refreshTask);
			}
		}
	}

	private void handleCreateTaskCompleted(TaskCompleteEvent taskCompleteEvent) {
		ICreateTask createTask = (ICreateTask) taskCompleteEvent.getSource();
		if ( createTask.isAddedToParent() ) {
			objectStoreItemsModel.add( createTask.getNewObjectStoreItem() );
			Collection<IObjectStoreItem> parents = objectStoreItemsModel.addChild(
					createTask.getParent(), createTask.getNewObjectStoreItem());
			fireObjectStoreItemsChanged(null, null, toArray(parents) );
		}
	}

	public void fireObjectStoreItemsChanged(IObjectStoreItem[] itemsAdded,
			IObjectStoreItem[] itemsRemoved, IObjectStoreItem[] itemsUpdated ) {
		ObjectStoresManagerEvent event = new ObjectStoresManagerEvent(this,
				itemsAdded, itemsRemoved, itemsUpdated );
		for (ITaskManagerListener listener : listeners) {
			listener.objectStoreItemsChanged(event);
		}
	}

	private void fireObjectStoreItemsRefreshed(IObjectStoreItem[] itemsRefreshed) {
		ObjectStoresManagerRefreshEvent event = new ObjectStoresManagerRefreshEvent(this,itemsRefreshed);
		for (ITaskManagerListener listener : listeners) {
			listener.objectStoreItemsRefreshed(event);
		}
	}

	public void addObjectStoresManagerListener( ITaskManagerListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void removeObjectStoresManagerListener( ITaskManagerListener listener) {
		listeners.remove(listener);
	}

	private Collection<IObjectStoreItem> toCollection(IObjectStoreItem[] objectStoreItems) {
		Collection<IObjectStoreItem> objectStoreItemsCollection = new HashSet<IObjectStoreItem>();
		for ( IObjectStoreItem objectStoreItem : objectStoreItems ) {
			objectStoreItemsCollection.add((IObjectStoreItem) objectStoreItem);
		}
		return objectStoreItemsCollection;
	}

	private IObjectStoreItem[] toArray(Collection<IObjectStoreItem> objectStoreItems) {
		return objectStoreItems.toArray( new IObjectStoreItem[objectStoreItems.size()] );
	}

	private IObjectStoreItem[] toArray(IObjectStoreItem objectStoreItem) {
		return new IObjectStoreItem[] {objectStoreItem };
	}
}
