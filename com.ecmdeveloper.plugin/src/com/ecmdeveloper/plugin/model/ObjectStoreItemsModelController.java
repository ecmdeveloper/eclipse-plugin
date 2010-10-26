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

package com.ecmdeveloper.plugin.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.ecmdeveloper.plugin.model.tasks.CreateTask;
import com.ecmdeveloper.plugin.model.tasks.DeleteTask;
import com.ecmdeveloper.plugin.model.tasks.DocumentTask;
import com.ecmdeveloper.plugin.model.tasks.LoadChildrenTask;
import com.ecmdeveloper.plugin.model.tasks.MoveTask;
import com.ecmdeveloper.plugin.model.tasks.RefreshTask;
import com.ecmdeveloper.plugin.model.tasks.TaskCompleteEvent;
import com.ecmdeveloper.plugin.model.tasks.TaskListener;
import com.ecmdeveloper.plugin.model.tasks.UpdateTask;

/**
 * @author ricardo.belfor
 *
 */
public class ObjectStoreItemsModelController implements TaskListener {

	private ObjectStoreItemsModel objectStoreItemsModel;
	private List<ObjectStoresManagerListener> listeners = new ArrayList<ObjectStoresManagerListener>();

	public ObjectStoreItemsModelController() {
		objectStoreItemsModel = ObjectStoreItemsModel.getInstance();
	}

	@Override
	public void onTaskComplete(TaskCompleteEvent taskCompleteEvent) {
		handleTaskCompleteEvent(taskCompleteEvent);
	}

	private void handleTaskCompleteEvent(TaskCompleteEvent taskCompleteEvent) {
		if ( isTaskSourceInstanceOf(taskCompleteEvent, DeleteTask.class) ) {
			handleDeleteTaskCompleted( taskCompleteEvent );
		} else if ( isTaskSourceInstanceOf(taskCompleteEvent, LoadChildrenTask.class) ) {
			handleLoadChildrenTaskCompleted( taskCompleteEvent );
		} else if ( isTaskSourceInstanceOf(taskCompleteEvent, RefreshTask.class) ) {
			handleRefreshTaskCompleted( taskCompleteEvent );
		} else if ( isTaskSourceInstanceOf(taskCompleteEvent, MoveTask.class) ) {
			handleMoveTaskCompleted( taskCompleteEvent );
		} else if ( isTaskSourceInstanceOf(taskCompleteEvent, UpdateTask.class) ) {
			handleUpdateTaskCompleted(taskCompleteEvent);
		} if ( isTaskSourceInstanceOf(taskCompleteEvent, CreateTask.class) ) {
			handleCreateTaskCompleted(taskCompleteEvent);
		} else if ( isTaskSourceInstanceOf(taskCompleteEvent, DocumentTask.class) ) {
			handleDocumentTaskCompleted(taskCompleteEvent);
		}
	}

	private boolean isTaskSourceInstanceOf(TaskCompleteEvent taskCompleteEvent, Class<?> taskClass) {

		Class<? extends Object> eventClass = taskCompleteEvent.getSource().getClass();
		
		do {
			if ( eventClass.equals( taskClass ) ) {
				return true;
			}
			eventClass = eventClass.getSuperclass();
		} while ( ! eventClass.equals( Object.class ) );
		
		return false;
	}

	private void handleUpdateTaskCompleted(TaskCompleteEvent taskCompleteEvent) {
		UpdateTask updateTask = (UpdateTask) taskCompleteEvent.getSource();
		refreshUpdatedItems(updateTask.getObjectStoreItems());
	}

	private void refreshUpdatedItems(IObjectStoreItem[] objectStoreItems) {
		fireObjectStoreItemsChanged(null, null, objectStoreItems );
		
		for (IObjectStoreItem objectStoreItem : objectStoreItems) {
			refreshSimilarObjects(objectStoreItem);
		}
	}

	private void handleDeleteTaskCompleted(TaskCompleteEvent taskCompleteEvent) {
		DeleteTask deleteTask = (DeleteTask) taskCompleteEvent.getSource();
		IObjectStoreItem[] objectStoreItems = deleteTask.getObjectStoreItems();
		Collection<ObjectStoreItem> collection = toCollection(objectStoreItems);
		Collection<ObjectStoreItem> updatedItems = new ArrayList<ObjectStoreItem>();

		if ( ! deleteTask.isDeleteAllVersions() ) {
			
			for ( IObjectStoreItem objectStoreItem : objectStoreItems ) {
				if (objectStoreItem instanceof Document ) {
					updatedItems.add((ObjectStoreItem) objectStoreItem);
					collection.remove(objectStoreItem);
				}
			}
		}

		Collection<ObjectStoreItem> deletedItems = objectStoreItemsModel.delete(collection );
		fireObjectStoreItemsChanged(null, toArray(deletedItems), null );

		if ( !deleteTask.isDeleteAllVersions() ) {
			refreshUpdatedItems(toArray(updatedItems));
		}
	}

	private void handleLoadChildrenTaskCompleted(TaskCompleteEvent taskCompleteEvent) {
		
		LoadChildrenTask loadChildrenTask = (LoadChildrenTask) taskCompleteEvent.getSource();
		ObjectStoreItem objectStoreItem = loadChildrenTask.getObjectStoreItem();
		ArrayList<IObjectStoreItem> children = loadChildrenTask.getChildren();
		
		objectStoreItemsModel.addChildren(objectStoreItem, children );
		fireObjectStoreItemsChanged(null, null, toArray(objectStoreItem ) );
	}

	private void handleRefreshTaskCompleted(TaskCompleteEvent taskCompleteEvent) {
		RefreshTask refreshTask = (RefreshTask)taskCompleteEvent.getSource();
		IObjectStoreItem[] objectStoreItems = refreshTask.getObjectStoreItems();
		fireObjectStoreItemsChanged(null, null, objectStoreItems );
//		fireObjectStoreItemsRefreshed( objectStoreItems );
	}

	private void handleMoveTaskCompleted(TaskCompleteEvent taskCompleteEvent) {
		MoveTask moveTask = (MoveTask) taskCompleteEvent.getSource();
		fireObjectStoreItemsChanged(null, moveTask.getObjectStoreItems(), moveTask.getUpdatedObjectStoreItems() );
	}

	private void handleDocumentTaskCompleted(TaskCompleteEvent taskCompleteEvent) {
		DocumentTask documentTask = (DocumentTask) taskCompleteEvent.getSource();
		ObjectStoreItem objectStoreItem = documentTask.getDocument();
 		fireObjectStoreItemsChanged(null, null, new ObjectStoreItem[] { objectStoreItem } );
		refreshSimilarObjects(objectStoreItem);
	}

	private void refreshSimilarObjects(IObjectStoreItem objectStoreItem) {
		Collection<ObjectStoreItem> objectStoreItems = objectStoreItemsModel.get((ObjectStoreItem) objectStoreItem);
		for (ObjectStoreItem o : objectStoreItems ) {
			if ( ! o.equals(objectStoreItem ) ) {
				RefreshTask refreshTask = new RefreshTask( o );
				ObjectStoresManager.getManager().executeTaskASync(refreshTask);
			}
		}
	}

	private void handleCreateTaskCompleted(TaskCompleteEvent taskCompleteEvent) {
		CreateTask createTask = (CreateTask) taskCompleteEvent.getSource();
		if ( createTask.isAddedToParent() ) {
			objectStoreItemsModel.add( createTask.getNewObjectStoreItem() );
			Collection<ObjectStoreItem> parents = objectStoreItemsModel.addChild(
					createTask.getParent(), createTask.getNewObjectStoreItem());
			fireObjectStoreItemsChanged(null, null, toArray(parents) );
		}
	}

	public void fireObjectStoreItemsChanged(IObjectStoreItem[] itemsAdded,
			IObjectStoreItem[] itemsRemoved, IObjectStoreItem[] itemsUpdated ) {
		ObjectStoresManagerEvent event = new ObjectStoresManagerEvent(this,
				itemsAdded, itemsRemoved, itemsUpdated );
		for (ObjectStoresManagerListener listener : listeners) {
			listener.objectStoreItemsChanged(event);
		}
	}

	private void fireObjectStoreItemsRefreshed(IObjectStoreItem[] itemsRefreshed) {
		ObjectStoresManagerRefreshEvent event = new ObjectStoresManagerRefreshEvent(this,itemsRefreshed);
		for (ObjectStoresManagerListener listener : listeners) {
			listener.objectStoreItemsRefreshed(event);
		}
	}

	public void addObjectStoresManagerListener( ObjectStoresManagerListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void removeObjectStoresManagerListener( ObjectStoresManagerListener listener) {
		listeners.remove(listener);
	}

	private Collection<ObjectStoreItem> toCollection(IObjectStoreItem[] objectStoreItems) {
		Collection<ObjectStoreItem> objectStoreItemsCollection = new HashSet<ObjectStoreItem>();
		for ( IObjectStoreItem objectStoreItem : objectStoreItems ) {
			objectStoreItemsCollection.add((ObjectStoreItem) objectStoreItem);
		}
		return objectStoreItemsCollection;
	}

	private ObjectStoreItem[] toArray(Collection<ObjectStoreItem> objectStoreItems) {
		return objectStoreItems.toArray( new ObjectStoreItem[objectStoreItems.size()] );
	}

	private ObjectStoreItem[] toArray(ObjectStoreItem objectStoreItem) {
		return new ObjectStoreItem[] {objectStoreItem };
	}
}
