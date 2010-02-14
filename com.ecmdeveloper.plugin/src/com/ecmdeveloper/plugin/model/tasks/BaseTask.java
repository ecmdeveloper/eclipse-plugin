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
package com.ecmdeveloper.plugin.model.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStoresManagerEvent;
import com.ecmdeveloper.plugin.model.ObjectStoresManagerListener;


public abstract class BaseTask implements Callable<Object>{

	protected List<TaskListener> listeners = new ArrayList<TaskListener>();
	
	public void addTaskListener(TaskListener taskListener) {
		if ( ! listeners.contains( taskListener ) ) {
			listeners.add(taskListener);
		}
	} 
	
	protected void fireTaskCompleteEvent(TaskResult taskResult) {
		for (TaskListener taskListener : listeners) {
			TaskCompleteEvent taskCompleteEvent = new TaskCompleteEvent(this,taskResult);
			taskListener.onTaskComplete(taskCompleteEvent );
		}
	}

//	protected List<ObjectStoresManagerListener> listeners;
//
//	public BaseTask() {
//	}
//
//	public void setListeners( List<ObjectStoresManagerListener> listeners ) {
//		this.listeners = listeners;
//	}
//
//	public void fireObjectStoreItemsChanged(IObjectStoreItem[] itemsAdded,
//			IObjectStoreItem[] itemsRemoved, IObjectStoreItem[] itemsUpdated ) {
//		
//		if ( listeners == null || listeners.isEmpty() ) {
//			return;
//		}
//		
//		ObjectStoresManagerEvent event = new ObjectStoresManagerEvent(this,
//				itemsAdded, itemsRemoved, itemsUpdated );
//		for (ObjectStoresManagerListener listener : listeners) {
//			listener.objectStoreItemsChanged(event);
//		}
//	}
}
