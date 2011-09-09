/**
 * Copyright 2011, Ricardo Belfor
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

package com.ecmdeveloper.plugin.core.model.tasks.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.impl.ObjectStoreItemsModelController;
import com.ecmdeveloper.plugin.core.model.tasks.IBaseTask;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskManager;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskManagerListener;

/**
 * @author ricardo.belfor
 *
 */
public class TaskManager implements ITaskManager {

	private static ITaskManager taskManager;
	
	private ExecutorService executorService;
	private ObjectStoreItemsModelController modelController;
	
	public static ITaskManager getInstance()
	{
		if ( taskManager == null )
		{
			taskManager = new TaskManager();
		}
		return taskManager;
	}
	
	private TaskManager() {
		executorService = Executors.newSingleThreadExecutor();
		modelController = new ObjectStoreItemsModelController();
	}

	@Override
	public <T> T executeTaskSync( Callable<T> task ) throws ExecutionException
	{
		try {
			if ( task instanceof IBaseTask ) {
				((IBaseTask)task).addTaskListener(modelController);
			}
			return executorService.submit(task).get();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} 
	}

	@Override
	public void executeTaskASync( Callable<?> task )
	{
		if ( task instanceof IBaseTask ) {
			((IBaseTask)task).addTaskListener(modelController);
		}
		executorService.submit(task);
	}

	@Override
	public void addTaskManagerListener( ITaskManagerListener listener) {
		modelController.addObjectStoresManagerListener(listener);
	}

	@Override
	public void removeTaskManagerListener( ITaskManagerListener listener) {
		modelController.removeObjectStoresManagerListener(listener);
	}

	@Override
	public void fireObjectStoreItemsChanged(IObjectStoreItem[] itemsAdded,
			IObjectStoreItem[] itemsRemoved, IObjectStoreItem[] itemsUpdated) {
		modelController.fireObjectStoreItemsChanged(itemsAdded, itemsRemoved, itemsUpdated);
	}
}
